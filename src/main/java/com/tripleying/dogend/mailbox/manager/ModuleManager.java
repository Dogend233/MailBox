package com.tripleying.dogend.mailbox.manager;

import com.tripleying.dogend.mailbox.api.event.module.MailBoxModuleLoadEvent;
import com.tripleying.dogend.mailbox.api.event.module.MailBoxModuleUnloadEvent;
import com.tripleying.dogend.mailbox.api.module.MailBoxModule;
import com.tripleying.dogend.mailbox.api.module.ModuleClassLoader;
import com.tripleying.dogend.mailbox.api.module.ModuleInfo;
import com.tripleying.dogend.mailbox.util.MessageUtil;
import com.tripleying.dogend.mailbox.util.ModuleUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import org.bukkit.Bukkit;

/**
 * 模块管理器
 * @author Dogend
 */
public class ModuleManager {
    
    private static ModuleManager manager;
    private final Map<String, ModuleClassLoader> map;
    
    public ModuleManager(){
        manager = this;
        this.map = new LinkedHashMap();
    }
    
    /**
     * 获取模块实例
     * @since 3.1.0
     * @param name 模块名
     * @return MailBoxModule
     */
    public MailBoxModule getMailBoxModule(String name){
        if(this.map.containsKey(name)){
            return this.map.get(name).getModule();
        }else{
            return null;
        }
    }
    
    /**
     * 加载模块
     * @param file 模块文件
     * @return int
     */
    public boolean loadModule(File file){
        ModuleClassLoader module = null;
        try{
            module = new ModuleClassLoader(file, this.getClass().getClassLoader(), this);
            module.getModule().onEnable();
            ModuleInfo info = module.getModule().getInfo();
            MessageUtil.log(MessageUtil.modlue_load_success.replaceAll("%module%", info.getName()).replaceAll("%version%", info.getVersion().toString()));
            this.map.put(info.getName(), module);
            this.addBefore(info);
            // 调用模块加载事件
            MailBoxModuleLoadEvent evt = new MailBoxModuleLoadEvent(module.getModule());
            Bukkit.getPluginManager().callEvent(evt);
            return true;
        }catch(Exception ex){
            MessageUtil.error(MessageUtil.modlue_load_error_main_err.replaceAll("%module%", file.getName()));
            if(module!=null){
                try{
                    module.close();
                }catch(Exception e){}
            }
            return false;
        }
    }
    
    /**
     * 加载本地模块
     */
    public void loadLocalModule(){
        File data = ModuleUtil.getModuleFolder();
        if(!data.exists()) data.mkdirs();
        File[] mods = data.listFiles((File dir, String name) -> name.endsWith(".jar"));
        if(mods.length==0){
            MessageUtil.error(MessageUtil.modlue_load_empty);
        }else{
            this.loadModuleFiles(mods);
        }
    }
    
     /**
     * 批量加载模块文件
     * (此代码部分来自于Bukkit)
     * @param mods 模块文件数组
     */
    public void loadModuleFiles(File[] mods){
        HashMap<String, File> modules = new HashMap();
        HashSet<String> loadedModules = new HashSet();
        HashMap<String, LinkedList<String>> depModules = new HashMap();
        HashMap<String, LinkedList<String>> softDepModules = new HashMap();
        HashMap<String, LinkedList<String>> depPlugins = new HashMap();
        for(File mod:mods){
            List<String> depModuleSet;
            List<String> softDepModuleSet;
            List<String> depPluginSet;
            try(JarFile jarFile = new JarFile(mod)){
                ModuleInfo info = ModuleUtil.loadModuleInfo(jarFile);
                if(info.isAvaliable() && !this.hasModule(info.getName())){
                    String name = info.getName();
                    modules.put(name, mod);
                    depPluginSet = info.getDependPlugin();
                    if(!depPluginSet.isEmpty()) depPlugins.put(name, new LinkedList(depPluginSet));
                    depModuleSet = info.getDependModule();
                    if(!depModuleSet.isEmpty()) depModules.put(name, new LinkedList(depModuleSet));
                    softDepModuleSet = info.getSoftdependModule();
                    if(!softDepModuleSet.isEmpty()) softDepModules.put(name, new LinkedList(softDepModuleSet));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        while(!modules.isEmpty()){
            File file;
            String name;
            boolean missingDependency = true;
            Iterator<Map.Entry<String, File>> moduleIterator = modules.entrySet().iterator();
            while(moduleIterator.hasNext()) {
                Map.Entry<String, File> module = moduleIterator.next();
                name = module.getKey();
                if(depPlugins.containsKey(name)){
                    List<String> lost = new ArrayList();
                    for(String plugin:depPlugins.get(name)){
                        if(!Bukkit.getPluginManager().isPluginEnabled(plugin)){
                            lost.add(plugin);
                        }
                    }
                    if(!lost.isEmpty()){
                        missingDependency = false;
                        moduleIterator.remove();
                        depPlugins.remove(name);
                        softDepModules.remove(name);
                        depModules.remove(name);
                        MessageUtil.error(MessageUtil.modlue_load_error_depend_plugin.replaceAll("%module%", module.getValue().getName()).replaceAll("%depends%", lost.stream().reduce("",(a,b) -> a.concat(" ").concat(b))));
                        break;
                    }
                }
                if(depModules.containsKey(name)){
                    Iterator depIterator = ((Collection)depModules.get(name)).iterator();
                    while(depIterator.hasNext()){
                        String dep = (String)depIterator.next();
                        if(loadedModules.contains(dep)){
                            depIterator.remove();
                            continue;
                        }
                        if(modules.containsKey(dep)) continue;
                        missingDependency = false;
                        moduleIterator.remove();
                        depPlugins.remove(name);
                        softDepModules.remove(name);
                        depModules.remove(name);
                        MessageUtil.error(MessageUtil.modlue_load_error_depend_module.replaceAll("%module%", module.getValue().getName()).replaceAll("%depends%", dep));
                        break;
                    }
                    if(depModules.containsKey(name) && ((Collection)depModules.get(name)).isEmpty()) {
                        depModules.remove(name);
                    }
                }
                if(softDepModules.containsKey(name)){
                    Iterator softDepIterator = ((Collection)softDepModules.get(name)).iterator();
                    while(softDepIterator.hasNext()){
                        String softDep = (String)softDepIterator.next();
                        if(modules.containsKey(softDep)) continue;
                        softDepIterator.remove();
                    }
                    if(((Collection)softDepModules.get(name)).isEmpty()) {
                        softDepModules.remove(name);
                    }
                }
                if(depModules.containsKey(name) || softDepModules.containsKey(name) || !modules.containsKey(name)) continue;
                file = modules.get(name);
                moduleIterator.remove();
                depPlugins.remove(name);
                missingDependency = false;
                if(this.loadModule(file)) loadedModules.add(name);
            }
            if(!missingDependency) continue;
            moduleIterator = modules.entrySet().iterator();
            while(moduleIterator.hasNext()) {
                Map.Entry<String, File> module = moduleIterator.next();
                name = module.getKey();
                if(depModules.containsKey(name)) continue;
                softDepModules.remove(name);
                missingDependency = false;
                file = module.getValue();
                moduleIterator.remove();
                depPlugins.remove(name);
                if(this.loadModule(file)) loadedModules.add(name);
            }
            if (!missingDependency) continue;
            depPlugins.clear();
            softDepModules.clear();
            depModules.clear();
            Iterator<File> failedPluginIterator = modules.values().iterator();
            while(failedPluginIterator.hasNext()) {
                File file2 = failedPluginIterator.next();
                failedPluginIterator.remove();
                MessageUtil.error(MessageUtil.modlue_load_error_recycle_depend.replaceAll("%module%", file2.getName()));
            }
        }
    }
    
    /**
     * 卸载模块
     * @param name 模块名
     */
    public void unloadModule(String name){
        if(this.map.containsKey(name)){
            ModuleClassLoader loader = this.map.get(name);
            MailBoxModule module = loader.getModule();
            // 卸载模块的后置模块
            for(String mod:module.getInfo().getBeforeModule()){
                if(this.hasModule(mod)){
                    this.unloadModule(mod);
                }
            }
            MessageUtil.log(MessageUtil.modlue_unload.replaceAll("%module%", name));
            // 调用卸载方法
            module.unregisterAllListener();
            module.unregisterAllCommand();
            module.unregisterAllMoney();
            module.unregisterAllSystemMail();
            try{
                module.onDisable();
            }catch(Exception ex){}
            // 调用插件卸载事件
            MailBoxModuleUnloadEvent evt = new MailBoxModuleUnloadEvent(module);
            Bukkit.getPluginManager().callEvent(evt);
            // 关闭类加载器
            try {
                loader.close();
            } catch (Exception ex) {}
            this.map.remove(name);
        }
    }
    
    /**
     * 卸载全部模块
     */
    public void unloadAllModule(){
        while(!this.map.isEmpty()){
            this.unloadModule(new ArrayList<>(this.map.entrySet()).listIterator(this.map.size()).previous().getKey());
        }
    }
    
    /**
     * 将插件写在前置模块的后置模块列表
     * @param info 模块信息
     */
    public void addBefore(ModuleInfo info){
        info.getDependModule().stream().filter(mod -> this.hasModule(mod)).forEachOrdered(mod -> {
            this.getMailBoxModule(mod).getInfo().addBeforeModule(info.getName());
        });
        info.getSoftdependModule().stream().filter(mod -> this.hasModule(mod)).forEachOrdered(mod -> {
            this.getMailBoxModule(mod).getInfo().addBeforeModule(info.getName());
        });
    }
    
    /**
     * 将插件从前置模块的后置模块列表中移除
     * @param info 模块信息
     */
    public void removeBefore(ModuleInfo info){
        info.getDependModule().stream().filter(mod -> (this.hasModule(mod))).forEachOrdered(mod -> {
            this.getMailBoxModule(mod).getInfo().removeBeforeModule(mod);
        });
        info.getSoftdependModule().stream().filter(mod -> this.hasModule(mod)).forEachOrdered(mod -> {
            this.getMailBoxModule(mod).getInfo().addBeforeModule(info.getName());
        });
    }
    
    /**
     * 从已加载的模块类加载器中加载类
     * @since 3.1.0
     * @param name 类名
     * @param resolve resolve
     * @return Class
     */
    public Class<?> getClassFromModuleClassLoaders(String name, boolean resolve) {
        for (ModuleClassLoader loader : this.map.values()) {
            try {
                return loader.loadModuleClass(name, resolve, false);
            }catch (ClassNotFoundException ex) {}
        }
        return null;
    }
    
    /**
     * 是否有目标模块
     * @param name 模块名
     * @return boolean
     */
    public boolean hasModule(String name){
        return this.map.containsKey(name);
    } 

    public static ModuleManager getModuleManager() {
        return manager;
    }
    
}
