package com.tripleying.dogend.mailbox.api.module;

import com.tripleying.dogend.mailbox.api.util.Version;
import com.tripleying.dogend.mailbox.util.FileUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 模块信息
 * @author Dogend
 */
public class ModuleInfo {
    
    /**
     * 插件名
     */
    private final String name;
    /**
     * 插件主类
     */
    private final String main;
    /**
     * 插件描述
     */
    private final String description;
    /**
     * 插件作者
     */
    private final List<String> author;
    /**
     * 插件版本
     */
    private final Version version;
    /**
     * 前置插件
     */
    private final List<String> depend_plugin;
    /**
     * 前置模块
     */
    private final List<String> depend_module;
    /**
     * 软前置模块
     */
    private final List<String> softdepend_module;
    /**
     * 后置模块
     */
    private final List<String> before_module;
    /**
     * 是否可用
     */
    private final boolean avaliable;
    
    public ModuleInfo(YamlConfiguration yml){
        this.name = yml.getString("name", null);
        this.main = yml.getString("main", null);
        this.description = yml.getString("description", "none").replaceAll("&", "§");
        this.author = yml.contains("author")
                ?FileUtil.getYamlStringList(yml, "author")
                :Arrays.asList("none");
        this.version = new Version(yml.getString("version", "1.0.0"));
        this.depend_plugin = yml.contains("depend-plugin")
                ?FileUtil.getYamlStringList(yml, "depend-plugin")
                :new ArrayList();
        this.depend_module = yml.contains("depend-module")
                ?FileUtil.getYamlStringList(yml, "depend-module")
                :new ArrayList();
        this.softdepend_module = yml.contains("softdepend-module")
                ?FileUtil.getYamlStringList(yml, "softdepend-module")
                :new ArrayList();
        this.before_module = new ArrayList();
        if(this.name!=null && this.main!=null && this.version.isAvaliable()){
            this.avaliable = true;
        }else{
            this.avaliable = false;
        }
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getMain(){
        return this.main;
    }
    
    public String getDescription(){
        return this.description;
    }
    
    public List<String> getAuthors(){
        return this.author;
    }
    
    public Version getVersion(){
        return this.version;
    }
    
    public List<String> getDependPlugin(){
        return this.depend_plugin;
    }
    
    public List<String> getDependModule(){
        return this.depend_module;
    }
    
    public List<String> getSoftdependModule(){
        return this.softdepend_module;
    }
    
    public List<String> getBeforeModule(){
        return this.before_module;
    }
    
    /**
     * 判断A模块是否是此模块的前置
     * @since 3.1.0
     * @param name A模块名
     * @return boolean
     */
    public boolean isDependModule(String name){
        return this.depend_module.contains(name) || this.softdepend_module.contains(name);
    }
    
    public void addBeforeModule(String module){
        if(!this.before_module.contains(module)){
            this.before_module.add(module);
        }
    }
    
    public void removeBeforeModule(String module){
        if(this.before_module.contains(module)){
            this.before_module.remove(module);
        }
    }
    
    public boolean isAvaliable(){
        return this.avaliable;
    }
    
}
