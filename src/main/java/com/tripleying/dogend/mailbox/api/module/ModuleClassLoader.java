package com.tripleying.dogend.mailbox.api.module;

import com.tripleying.dogend.mailbox.manager.ModuleManager;
import com.tripleying.dogend.mailbox.util.MessageUtil;
import com.tripleying.dogend.mailbox.util.ModuleUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import org.bukkit.Bukkit;

/**
 * 模块类加载器
 * @since 3.1.0
 * @author Administrator
 */
public class ModuleClassLoader extends URLClassLoader {
    
    private final MailBoxModule module;
    private final Map<String, Class<?>> classes = new ConcurrentHashMap();
    private final ModuleInfo info;
    private final File file;
    private final JarFile jar;
    private final ModuleManager mm;
    private final Set<String> seenIllegalAccess = Collections.newSetFromMap(new ConcurrentHashMap());
    
    static {
        ClassLoader.registerAsParallelCapable();
    }
    
    public ModuleClassLoader(File file, ClassLoader parent, ModuleManager mm) throws IOException, InvalidModuleException, MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);
        this.file = file;
        this.mm = mm;
        // 读取jar文件
        jar = new JarFile(this.file);
        // 读取模块信息
        try {
            info = ModuleUtil.loadModuleInfo(jar);
        } catch (Exception ex) {
            closeJar();
            throw new InvalidModuleException(MessageUtil.modlue_load_error_not_info.replaceAll("%module%", file.getName()), ex);
        }
        if(!info.isAvaliable()){
            closeJar();
            throw new InvalidModuleException(MessageUtil.modlue_load_error_info_err.replaceAll("%module%", file.getName()));
        }
        String name = info.getName();
        if(mm.hasModule(name)){
            closeJar();
            throw new InvalidModuleException(MessageUtil.modlue_load_error_has_duplicate.replaceAll("%module%", file.getName()).replaceAll("%another%", mm.getMailBoxModule(name).getFileName()));
        }
        // 检查前置插件/模块
        if(!info.getDependPlugin().isEmpty()){
            List<String> lost = new ArrayList();
            info.getDependPlugin().stream().filter(plugin -> (!Bukkit.getPluginManager().isPluginEnabled(plugin))).forEachOrdered(plugin -> {
                lost.add(plugin);
            });
            if(!lost.isEmpty()){
                closeJar();
                throw new InvalidModuleException(MessageUtil.modlue_load_error_depend_plugin.replaceAll("%module%", file.getName()).replaceAll("%depends%", lost.stream().reduce("",(a,b) -> a.concat(" ").concat(b))));
            }
        }
        if(!info.getDependModule().isEmpty()){
            List<String> lost = new ArrayList();
            info.getDependModule().stream().filter(mod -> (!mm.hasModule(mod))).forEachOrdered(mod -> {
                lost.add(mod);
            });
            if(!lost.isEmpty()){
                closeJar();
                throw new InvalidModuleException(MessageUtil.modlue_load_error_depend_module.replaceAll("%module%", file.getName()).replaceAll("%depends%", lost.stream().reduce("",(a,b) -> a.concat(" ").concat(b))));
            }
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(info.getMain(), true, this);
        }catch (ClassNotFoundException ex) {
            closeJar();
            throw new InvalidModuleException(MessageUtil.modlue_load_error_main_find.replaceAll("%module%", name).replaceAll("%main%", info.getMain()), ex);
        }
        try {
            Constructor<?> constructor = clazz.getConstructor();
            Object instance = constructor.newInstance();
            Method init = MailBoxModule.class.getDeclaredMethod("init", ModuleInfo.class, JarFile.class, String.class);
            init.setAccessible(true);
            module = MailBoxModule.class.cast(instance);
            init.invoke(module, info, jar, file.getName());
        } catch (Exception ex) {
            closeJar();
            throw new InvalidModuleException(MessageUtil.modlue_load_error_main_init.replaceAll("%module%", name), ex);
        }
    }
    
    private void closeJar(){
        try {jar.close();} catch (Exception exj) {}
    }
    
    @Override
    public void close() throws IOException {
        try{
            super.close();
        }finally {
            this.jar.close();
        }
    }

    public MailBoxModule getModule() {
        return module;
    }
    
    @Override
    public URL getResource(String name) {
        return this.findResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return this.findResources(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return this.loadModuleClass(name, resolve, true);
    }
    
    public Class<?> loadModuleClass(String name, boolean resolve, boolean checkGlobal) throws ClassNotFoundException {
        Class<?> result;
        try {
            result = super.loadClass(name, resolve);
            if (checkGlobal || result.getClassLoader() == this) {
                return result;
            }
        }catch (ClassNotFoundException ex) {}
        if (checkGlobal && (result = this.mm.getClassFromModuleClassLoaders(name, resolve)) != null) {
            ModuleInfo provider;
            if (result.getClassLoader() instanceof ModuleClassLoader
            && (provider = ((ModuleClassLoader)result.getClassLoader()).info) != this.info
            && !this.seenIllegalAccess.contains(provider.getName())
            && !this.info.isDependModule(provider.getName())) {
                this.seenIllegalAccess.add(provider.getName());
                MessageUtil.error(MessageUtil.modlue_load_error_no_depend.replaceAll("%module%", this.info.getName()).replaceAll("%provider%", provider.getName()).replaceAll("%class%", name));
            }
            return result;
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> result = this.classes.get(name);
        if (result == null) {
            result = super.findClass(name);
        }
        this.classes.put(name, result);
        return result;
    }
    
    Collection<Class<?>> getClasses() {
        return this.classes.values();
    }
    
}
