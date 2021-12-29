package com.tripleying.dogend.mailbox;

import com.tripleying.dogend.mailbox.api.event.MailBoxLoadFinishEvent;
import com.tripleying.dogend.mailbox.api.mail.attach.AttachCommand;
import com.tripleying.dogend.mailbox.data.SQLiteData;
import com.tripleying.dogend.mailbox.api.mail.attach.AttachFile;
import com.tripleying.dogend.mailbox.api.util.CommonConfig;
import com.tripleying.dogend.mailbox.listener.onPlayerJoin;
import com.tripleying.dogend.mailbox.manager.*;
import com.tripleying.dogend.mailbox.data.MySQLData;
import com.tripleying.dogend.mailbox.util.ConfigUtil;
import com.tripleying.dogend.mailbox.util.FileUtil;
import com.tripleying.dogend.mailbox.util.MessageUtil;
import com.tripleying.dogend.mailbox.util.UpdateUtil;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 邮箱主类
 * @author Dogend
 */
public class MailBox extends JavaPlugin {
    
    // 邮箱实例
    private static MailBox mailbox;
    // MC版本
    private static double mc_version;
    // 数据管理器
    private DataManager datamgr;
    // 邮件管理器
    private MailManager mailmgr;
    // 金钱管理器
    private MoneyManager moneymgr;
    // 指令管理器
    private CommandManager cmdmgr;
    // 监听器管理器
    private ListenerManager listenermgr;
    // 模块管理器
    private ModuleManager modulemgr;
    
    @Override
    public void onEnable(){
        mailbox = this;
        // 为邮件附件注册序列化
        ConfigurationSerialization.registerClass(AttachCommand.class);
        ConfigurationSerialization.registerClass(AttachFile.class);
        // 获取游戏版本
        String v1 = Bukkit.getServer().getVersion();
        v1 = v1.substring(v1.indexOf("MC")+3, v1.length()-1).trim();
        String v2 = v1.substring(0, v1.lastIndexOf('.'));
        v2 = v2.substring(v1.indexOf('.')+1);
        if(v2.length()==1) v2 = "0"+v2;
        mc_version = Double.parseDouble(v1.substring(0, v1.indexOf('.'))+"."+v2);
        // 设置默认编码
        if(mc_version<1.09) FileUtil.setCharset(Bukkit.getServer().getName());
        // 加载插件
        load();
        // 更新检查
        if(this.getConfig().getBoolean("update-check", false)) UpdateUtil.updatePlugin(Bukkit.getConsoleSender(), CommonConfig.auto_update);
    }
    
    public void load(){
        // 加载各管理器
        this.datamgr = new DataManager();
        this.mailmgr = new MailManager();
        this.moneymgr = new MoneyManager();
        this.cmdmgr = new CommandManager();
        this.listenermgr = new ListenerManager();
        this.modulemgr = new ModuleManager();
        // 初始化配置文件
        YamlConfiguration msg = FileUtil.getConfig("message.yml");
        YamlConfiguration db = FileUtil.getConfig("database.yml");
        YamlConfiguration config = FileUtil.getConfig("config.yml");
        // 检查配置文件更新
        if(config.getBoolean("auto-config", false)){
            ConfigUtil.checkConfigVersion("message", msg);
            ConfigUtil.checkConfigVersion("db", db);
            ConfigUtil.checkConfigVersion("config", config);
        }
        // 初始化消息工具
        MessageUtil.init(msg);
        // 配置默认数据源
        this.loadDataBase(db);
        // 重载配置文件
        this.reloadConfig();
        // 初始化公共配置
        CommonConfig.init(config);
        // 当服务器完全启动后加载
        Bukkit.getScheduler().runTask(this, () -> {
            // 注册基础指令
            this.cmdmgr.registerBaseCommand();
            // 加载本地模块
            this.modulemgr.loadLocalModule();
            // 选择使用的数据源
            String database = config.getString("database", "sqlite");
            if(this.datamgr.selectData(database)){
                // 注册指令
                Bukkit.getPluginCommand("mailbox").setExecutor(this.cmdmgr);
                // 注册监听器
                Bukkit.getPluginManager().registerEvents(new onPlayerJoin(), this);
                // 发起加载完成事件
                Bukkit.getPluginManager().callEvent(new MailBoxLoadFinishEvent());
            }else{
                // 启动数据源失败, 卸载插件
                MessageUtil.error(MessageUtil.data_enable_error.replaceAll("%data%", database));
                Bukkit.getPluginManager().disablePlugin(this);
            }
        });
        
    }
    
    public void loadDataBase(YamlConfiguration database){
        // 加载SQLite配置
        if(database.getBoolean("sqlite.enable", false)){
            this.datamgr.addData(null, new SQLiteData(database));
        }
        // 加载MySQL配置
        if(database.getBoolean("mysql.enable", false)){
            this.datamgr.addData(null, new MySQLData(database));
        }
    }
    
    public void unload(){
        // 注销监听器
        HandlerList.unregisterAll(this);
        // 关闭数据源
        this.datamgr.closeData();
        // 卸载全部模块
        this.modulemgr.unloadAllModule();
    }
    
    public void reload(CommandSender cs){
        MessageUtil.error(cs, MessageUtil.reload_unload);
        unload();
        MessageUtil.log(cs, MessageUtil.reload_load);
        load();
        MessageUtil.log(cs, MessageUtil.reload_finish);
    }
    
    @Override
    public void onDisable(){
        // 卸载插件
        unload();
        // 为邮件附件注销序列化
        ConfigurationSerialization.unregisterClass(AttachFile.class);
        ConfigurationSerialization.unregisterClass(AttachCommand.class);
    }
    
    public DataManager getDataManager(){
        return this.datamgr;
    }
    
    public MailManager getMailManager(){
        return this.mailmgr;
    }
    
    public MoneyManager getMoneyManager(){
        return this.moneymgr;
    }
    
    public CommandManager getCommandManager(){
        return this.cmdmgr;
    }
    
    public ListenerManager getListenerManager(){
        return this.listenermgr;
    }
    
    public ModuleManager getModuleManager(){
        return this.modulemgr;
    }
    
    public static MailBox getMailBox(){
        return mailbox;
    }
    
    public static double getMCVersion(){
        return mc_version;
    }
    
    @Override
    public File getFile(){
        return super.getFile();
    }
    
}