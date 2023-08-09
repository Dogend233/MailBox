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
    
    /**
     * 邮箱实例
     */
    private static MailBox mailbox;
    /**
     * MC版本
     */
    private static double mc_version;
    // 管理器
    private DataManager data_man;
    private MailManager mail_man;
    private MoneyManager money_man;
    private CommandManager cmd_man;
    private ListenerManager listener_man;
    private ModuleManager module_man;
    /**
     * 主指令
     */
    private String[] main_cmd = new String[0];
    
    @Override
    public void onEnable(){
        mailbox = this;
        System.out.println("  _____   ____   _____ ______ _   _ _____");
        System.out.println(" |  __ \\ / __ \\ / ____|  ____| \\ | |  __ \\");
        System.out.println(" | |  | | |  | | |  __| |__  |  \\| | |  | |");
        System.out.println(" | |  | | |  | | | |_ |  __| | . ` | |  | |");
        System.out.println(" | |__| | |__| | |__| | |____| |\\  | |__| |");
        System.out.println(" |_____/ \\____/ \\_____|______|_| \\_|_____/ ");
        // 获取插件注册指令
        this.main_cmd = this.getDescription().getCommands().keySet().toArray(this.main_cmd);
        // 为邮件附件注册序列化
        ConfigurationSerialization.registerClass(AttachCommand.class);
        ConfigurationSerialization.registerClass(AttachFile.class);
        // 获取游戏版本
        String v1 = Bukkit.getServer().getVersion();
        v1 = v1.substring(v1.indexOf("MC")+3, v1.length()-1).trim();
        String v2;
        if(v1.indexOf('.')==v1.lastIndexOf('.')){
            v2 = v1;
        }else{
            v2 = v1.substring(0, v1.lastIndexOf('.'));
        }
        v2 = v2.substring(v1.indexOf('.')+1);
        if(v2.length()==1) v2 = "0".concat(v2);
        mc_version = Double.parseDouble(v1.substring(0, v1.indexOf('.')).concat(".").concat(v2));
        // 设置默认编码
        if(mc_version<1.09) FileUtil.setCharset(Bukkit.getServer().getName());
        // 加载插件
        load();
    }
    
    public void load(){
        // 加载各管理器
        this.data_man = new DataManager();
        this.mail_man = new MailManager();
        this.money_man = new MoneyManager();
        this.cmd_man = new CommandManager();
        this.listener_man = new ListenerManager();
        this.module_man = new ModuleManager();
        // 初始化配置文件
        YamlConfiguration msg = FileUtil.getConfig("message.yml");
        YamlConfiguration db = FileUtil.getConfig("database.yml");
        YamlConfiguration config = FileUtil.getConfig("config.yml");
        // 检查配置文件更新
        if(config.getBoolean("auto-config", false)){
            ConfigUtil.checkConfigVersion("message", msg);
            ConfigUtil.checkConfigVersion("database", db);
            ConfigUtil.checkConfigVersion("config", config);
        }
        // 初始化消息工具
        MessageUtil.init(msg);
        // 配置默认数据源
        this.loadDataBase(db);
        // 初始化公共配置
        CommonConfig.init(config);
        // 当服务器完全启动后加载
        Bukkit.getScheduler().runTask(this, () -> {
            // 注册基础指令
            this.cmd_man.registerBaseCommand();
            try{
                // 加载本地模块
                this.module_man.loadLocalModule();
            }catch(Exception ex){
            }finally{
                // 选择使用的数据源
                String database = config.getString("database", "sqlite");
                if(this.data_man.selectData(database)){
                    // 注册指令
                    for(String cmd:this.main_cmd){
                        Bukkit.getPluginCommand(cmd).setExecutor(this.cmd_man);
                        Bukkit.getPluginCommand(cmd).setTabCompleter(this.cmd_man.getTabManager());
                        MessageUtil.log("为 ".concat(cmd).concat(" 注册指令执行器与指令补全器"));
                    }
                    // 注册监听器
                    Bukkit.getPluginManager().registerEvents(new onPlayerJoin(), this);
                    // 发起加载完成事件
                    Bukkit.getPluginManager().callEvent(new MailBoxLoadFinishEvent());
                }else{
                    // 启动数据源失败, 卸载插件
                    MessageUtil.error(MessageUtil.data_enable_error.replaceAll("%data%", database));
                    Bukkit.getPluginManager().disablePlugin(this);
                }
            }
        });
        // 更新检查
        if(config.getBoolean("update-check", false)) UpdateUtil.updatePlugin(Bukkit.getConsoleSender(), CommonConfig.auto_update);
    }
    
    public void loadDataBase(YamlConfiguration database){
        // 加载SQLite配置
        if(database.getBoolean("sqlite.enable", false)){
            this.data_man.addData(null, new SQLiteData(database));
        }
        // 加载MySQL配置
        if(database.getBoolean("mysql.enable", false)){
            this.data_man.addData(null, new MySQLData(database));
        }
    }
    
    public void unload(){
        // 关闭在线玩家的GUI
        Bukkit.getOnlinePlayers().forEach(p -> p.closeInventory());
        // 注销监听器
        HandlerList.unregisterAll(this);
        // 关闭数据源
        if(this.data_man !=null) this.data_man.closeData();
        // 卸载全部模块
        if(this.module_man!=null) this.module_man.unloadAllModule();
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
        return this.data_man;
    }
    
    public MailManager getMailManager(){
        return this.mail_man;
    }
    
    public MoneyManager getMoneyManager(){
        return this.money_man;
    }
    
    public CommandManager getCommandManager(){
        return this.cmd_man;
    }
    
    public ListenerManager getListenerManager(){
        return this.listener_man;
    }
    
    public ModuleManager getModuleManager(){
        return this.module_man;
    }
    
    public String[] getMainCommands(){
        return this.main_cmd;
    }
    
    public String getMainCommand(){
        return this.main_cmd.length>0 ? this.main_cmd[0] : null;
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