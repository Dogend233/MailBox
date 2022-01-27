package com.tripleying.dogend.mailbox.api.module;

import com.tripleying.dogend.mailbox.MailBox;
import com.tripleying.dogend.mailbox.api.command.BaseCommand;
import com.tripleying.dogend.mailbox.api.data.BaseData;
import com.tripleying.dogend.mailbox.api.mail.SystemMail;
import com.tripleying.dogend.mailbox.api.money.BaseMoney;
import com.tripleying.dogend.mailbox.util.ModuleUtil;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

/**
 * 模块父类
 * @author Dogend
 */
public abstract class MailBoxModule {
    
    /**
     * 模块文件名
     */
    private String filename;
    /**
     * 模块jar文件
     */
    private JarFile jar;
    /**
     * 模块信息
     */
    private ModuleInfo info;
    /**
     * 配置文件
     */
    private final Map<String, YamlConfiguration> configs;
    
    public MailBoxModule(){
        this.configs = new LinkedHashMap();
    }
    
    /**
     * 初始化
     */
    private void init(ModuleInfo info, JarFile jar, String filename){
        this.info = info;
        this.jar = jar;
        this.filename = filename;
    }
    
    /**
     * 模块启动方法
     */
    public void onEnable(){}
    
    /**
     * 模块卸载方法
     */
    public void onDisable(){};
    
    /**
     * 获取模块信息
     * @return ModuleInfo
     */
    public ModuleInfo getInfo(){
        return this.info;
    }
    
    /**
     * 注册一个数据源
     * @param data 数据实例
     */
    public void registerData(BaseData data){
        MailBox.getMailBox().getDataManager().addData(this, data);
    }
    
    /**
     * 注册一个金钱
     * @param money 金钱实例 
     */
    public void registerMoney(BaseMoney money){
        MailBox.getMailBox().getMoneyManager().registerMoney(this, money);
    }
    
    /**
     * 注销一个金钱
     * @param money 金钱实例 
     */
    public void unregisterMoney(BaseMoney money){
        MailBox.getMailBox().getMoneyManager().unregisterMoney(money);
    }
    
    /**
     * 注销所有金钱
     */
    public void unregisterAllMoney(){
        MailBox.getMailBox().getMoneyManager().unregisterAllMoney(this);
    }
    
    /**
     * 注册一个系统邮件
     * @param sm 系统邮件实例 
     */
    public void registerSystemMail(SystemMail sm){
        MailBox.getMailBox().getMailManager().registerSystemMail(this, sm);
    }
    
    /**
     * 注销一个系统邮件
     * @param sm 系统邮件实例 
     */
    public void unregisterSystemMail(SystemMail sm){
        MailBox.getMailBox().getMailManager().unregisterSystemMail(sm);
    }
    
    /**
     * 注销所有系统邮件
     */
    public void unregisterAllSystemMail(){
        MailBox.getMailBox().getMailManager().unregisterAllSystemMail(this);
    }
    
    /**
     * 注册一个指令
     * @param cmd 指令实例 
     */
    public void registerCommand(BaseCommand cmd){
        MailBox.getMailBox().getCommandManager().registerCommand(this, cmd);
    }
    
    /**
     * 注销一个指令
     * @param cmd 指令实例 
     */
    public void unregisterCommand(BaseCommand cmd){
        MailBox.getMailBox().getCommandManager().unregisterCommand(cmd);
    }
    
    /**
     * 注销所有指令
     */
    public void unregisterAllCommand(){
        MailBox.getMailBox().getCommandManager().unregisterAllCommand(this);
    }
    
    /**
     * 注册一个监听器
     * @param listener 指令实例 
     */
    public void registerListener(Listener listener){
        MailBox.getMailBox().getListenerManager().registerListener(this, listener);
    }
    
    /**
     * 注销一个监听器
     * @param listener 指令实例 
     */
    public void unregisterListener(Listener listener){
        MailBox.getMailBox().getListenerManager().unregisterListener(listener);
    }
    
    /**
     * 注销所有监听器
     */
    public void unregisterAllListener(){
        MailBox.getMailBox().getListenerManager().unregisterAllListener(this);
    }
    
    /**
     * 获取模块文件夹
     * @return File
     */
    public File getDataFolder(){
        return ModuleUtil.getModuleDataFolder(this);
    }
    
    /**
     * 保存默认配置
     * @param files 路径+文件名
     */
    public void saveDefaultConfig(String... files){
        for(String file:files){
            ModuleUtil.saveConfig(this, file);
        }
    }
    
    /**
     * 保存默认config.yml配置
     */
    public void saveDefaultConfig(){
        this.saveDefaultConfig("config.yml");
    }
    
    /**
     * 重载配置文件
     * @param files 路径+文件名
     */
    public void reloadConfig(String... files){
        this.configs.clear();
        for(String file:files){
            this.configs.put(file, ModuleUtil.getConfig(this, file));
        }
    }
    
    /**
     * 重载配置文件
     */
    public void reloadConfig(){
        if(this.configs.isEmpty()){
            this.configs.put("config.yml", ModuleUtil.getConfig(this, "config.yml"));
        }else{
            Set<String> files = new HashSet();
            files.addAll(this.configs.keySet());
            this.configs.clear();
            for(String file:files){
                this.configs.put(file, ModuleUtil.getConfig(this, file));
            }
        }
    }
    
    /**
     * 获取内存中的配置文件
     * @param file 路径+文件名
     * @return YamlConfiguration
     */
    public YamlConfiguration getConfig(String file){
        if(!this.configs.containsKey(file)) this.configs.put(file, ModuleUtil.getConfig(this, file));
        return this.configs.get(file);
    }
    
    /**
     * 获取内存中的config.yml
     * @return YamlConfiguration
     */
    public YamlConfiguration getConfig(){
        return this.getConfig("config.yml");
    }
    
    /**
     * 获取Jar文件
     * @return JarFile
     */
    public JarFile getJar(){
        return this.jar;
    }
    
    /**
     * 获取插件文件名
     * @return FileName
     */
    public String getFileName(){
        return this.filename;
    }
    
}
