package com.嘤嘤嘤.qwq.MailBox;

import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import static com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI.getCustomMail;
import com.嘤嘤嘤.qwq.MailBox.Events.JoinAndQuit;
import com.嘤嘤嘤.qwq.MailBox.Events.Mail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import com.嘤嘤嘤.qwq.MailBox.Utils.SQLManager;
import com.嘤嘤嘤.qwq.MailBox.Utils.UpdateCheck;
import com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxGui;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailContentGui.openMailContentGui;
import static com.嘤嘤嘤.qwq.MailBox.VexView.VexViewConfig.VexViewConfigSet;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import lk.vexview.api.VexViewAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

public class MailBox extends JavaPlugin {
    
    private int temp;
    private boolean enCmdOpen;
    private boolean enVexView;
    public static MailBox instance;
    // 首次启动
    private static boolean FirstEnable = true;
    // config 配置文件
    private static final String DATA_FOLDER = "plugins/VexMailBox";
    private static FileConfiguration config;
    // system 类型邮件
    public static HashMap<Integer, TextMail> MailListSystem = new HashMap();
    public static HashMap<String, HashMap<String, ArrayList<Integer>>> MailListSystemId = new HashMap();
    // player 类型邮件
    public static HashMap<Integer, TextMail> MailListPlayer = new HashMap();
    public static HashMap<String, HashMap<String, ArrayList<Integer>>> MailListPlayerId = new HashMap();
    
    @Override    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (label.equalsIgnoreCase("mailbox")||label.equalsIgnoreCase("mb")){
            if(args.length==0 && enCmdOpen){
                if((sender instanceof Player)){
                    MailBoxGui.openMailBoxGui((Player) sender, "Recipient");
                    return true;
                }else{
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"只有玩家可以打开GUI");
                    return true;
                }
            }else if(args.length==1){
                if(args[0].equalsIgnoreCase("reload")){
                    if(sender.hasPermission("mailbox.admin.reload")){
                        reloadPlugin();
                        sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"插件已重载");
                        return true;
                    }else{
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                        return true;
                    }
                }else if(args[0].equalsIgnoreCase("check")){
                    if(sender.hasPermission("mailbox.admin.check")){
                        new BukkitRunnable(){
                            @Override
                            public void run(){
                                UpdateCheck.check(sender);
                            }
                        }.runTaskAsynchronously(this);
                        return true;
                    }else{
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                        return true;
                    }
                }else{
                    return true;
                }
            }else if(args.length>=2){
                if(args[0].equalsIgnoreCase("system") || args[0].equalsIgnoreCase("player")){
                    String type = args[0];
                    if(args.length==2){
                        if(args[1].equalsIgnoreCase("update")){
                            if(sender.hasPermission("mailbox.admin.update."+type)){
                                if(sender instanceof Player) updateMailList((Player) sender, type);
                                else updateMailList(null, type);
                                return true;
                            }else{
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                                return true;
                            }
                        }else if(args[1].equalsIgnoreCase("clean") && args[0].equalsIgnoreCase("player")){
                            int count = 0;
                            if(sender.hasPermission("mailbox.admin.clean."+type)){
                                temp = 0;
                                MailListPlayer.forEach((Integer k, TextMail v) -> {
                                    if(MailBoxAPI.isExpired(v)){
                                        v.Delete(null);
                                        temp++;
                                    }
                                });
                                sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"已清理player邮件"+temp+"封");
                                return true;
                            }else{
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                                return true;
                            }
                        }else{
                            return true;
                        }
                    }else if(args.length>=3){
                        if(args[1].equalsIgnoreCase("send")){
                            ArrayList<String> rl = new ArrayList();
                            if(type.equals("player") && args.length<4) {
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"请至少填写一个以上的收件人");
                                return true;
                            }else{
                                for(int i=3;i<args.length;i++){
                                    rl.add(args[i]);
                                }
                            }
                            if(sender.hasPermission("mailbox.admin.send.custom."+type)){
                                if((sender instanceof Player)){
                                    Player p = (Player) sender;
                                    TextMail tm = getCustomMail(args[2], type);
                                    if(tm==null){
                                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标文件不存在");
                                        return true;
                                    }else{
                                        if(tm.getSender()==null) tm.setSender(p.getName());
                                        if(type.equals("player") && tm.getRecipient()==null) tm.setRecipient(rl);
                                        try {
                                            openMailContentGui(p, tm, null, false);
                                        } catch (IOException ex) {
                                            getLogger().log(Level.SEVERE, null, ex);
                                        }
                                        return true;
                                    }
                                }else{
                                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"只有玩家可以打开GUI");
                                    return true;
                                }
                            }else{
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                                return true;
                            }
                        }else{
                            return true;
                        }
                    }else{
                        return true;
                    }
                }else{
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"此邮件类型不存在");
                    return true;
                }
            }else{
                return true;
            }
        }
        return true;
    }
    
    @Override
    public void onEnable(){
        // 插件启动
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:插件正在启动......");
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:版本："+this.getDescription().getVersion());
        // 检查前置[VexView]
        if(Bukkit.getPluginManager().isPluginEnabled("VexView")){
            String version = VexViewAPI.getVexView().getVersion();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:前置插件[VexView]已安装，版本："+version);
            // 检查[VexView]版本号
            if(UpdateCheck.check(version, "2.5.0")){
                // 加载插件
                instance = this;
                enVexView = true;
                reloadPlugin();
                Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:插件启动完成");
                // 检查更新
                if(getConfigBoolean("mailbox.updateCheck")){
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            UpdateCheck.check(Bukkit.getConsoleSender());
                        }
                    }.runTaskAsynchronously(this);
                }
            }else{
                Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[VexView]版本小于2.5");
                enVexView = false;
                this.enCmdOpen = false;
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }else{
            Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[VexView]未安装，卸载插件");
            enVexView = false;
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable(){
        // 注销监听器
        HandlerList.unregisterAll(this);
        try{
            // 断开MySQL连接
            SQLManager.get().shutdown();
        }catch(Exception e){
            this.getLogger().info(e.getLocalizedMessage());
        }
        // 插件关闭
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:插件已卸载");
    }
    
    // 重载插件
    private void reloadPlugin(){
        if(FirstEnable){
            FirstEnable = false;
        }else{
            // 注销监听器
            HandlerList.unregisterAll(this);
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:正在注销监听器");
            // 断开MySQL连接
            try{
                SQLManager.get().shutdown();
                Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:关闭数据库连接");
            }catch(Exception e){
                Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:断开数据库连接失败");
                this.getLogger().info(e.getLocalizedMessage());
            }
        }
        // 插件文件夹
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查插件文件夹是否存在");
        File f = new File(DATA_FOLDER);
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建插件文件夹");
        }
        // 读取config配置文件
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查配置文件是否存在");
        f = new File(DATA_FOLDER,"config.yml");
        if(f.exists()){
            
        }else{
            Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:配置文件不存在");
            saveDefaultConfig();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建配置文件");
        }
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:加载配置文件");
        reloadConfig();
        config = getConfig();
        setConfig();
        if(!enVexView) {
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:正在注册 加入/退出 事件");
            Bukkit.getPluginManager().registerEvents(new JoinAndQuit(enVexView, false), this);
        }
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:正在注册 邮件 事件");
        Bukkit.getPluginManager().registerEvents(new Mail(), this);
        // 邮件文件夹（总）
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查邮件文件夹是否存在");
        f = new File(DATA_FOLDER+"/MailFiles/");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:创建邮件文件夹");
        }
        // 邮件文件夹（独立）
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查system邮件文件夹是否存在");
        f = new File(DATA_FOLDER+"/MailFiles/"+"system");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建system邮件文件夹");
        }
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查player邮件文件夹是否存在");
        f = new File(DATA_FOLDER+"/MailFiles/"+"player");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建player邮件文件夹");
        }
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查custom邮件文件夹是否存在");
        f = new File(DATA_FOLDER+"/MailFiles/"+"custom");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建cutsom邮件文件夹");
        }
        // 连接数据库
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:正在连接数据库");
        if(getConfigBoolean("database.enableMySQL")){
            SQLManager.get().enableMySQL(
                getConfigString("database.mySQLhost"), 
                getConfigString("database.dataBaseName"), 
                getConfigString("database.mySQLusername"), 
                getConfigString("database.mySQLpassword"), 
                getConfigInt("database.mySQLport"), 
                getConfigString("database.dataTablePrefix")
            );
        }else{
            SQLManager.get().enableSQLite(
                getConfigString("database.dataBaseName"), 
                getConfigString("database.dataTablePrefix")
            );
        }
        
        // 更新[SYSTEM]邮件列表
        updateMailList(null, "system");
        updateMailList(null, "player");
    }
    
    // 设置Config
    private void setConfig(){
        // 设置GlobalConfig
        String fileDivS = getConfigString("mailbox.file.divide");
        if(fileDivS.equals(".") || fileDivS.equals("|")){
            fileDivS = "\\"+fileDivS;
        }
        GlobalConfig.setGlobalConfig(
            getConfigString("mailbox.prefix")+" : ",
            getConfigString("mailbox.normalMessage"),
            getConfigString("mailbox.successMessage"),
            getConfigString("mailbox.warningMessage"),
            getConfigString("mailbox.name.system"),
            getConfigString("mailbox.name.player"),
            fileDivS,
            getConfigString("mailbox.file.command.player"),
            getConfigString("mailbox.player_maxtime")
        );
        // 设置VexViewConfig
        if(enVexView) VexViewConfigSet();
    }
    
    //更新邮件列表
    public static void updateMailList(Player p, String type){
        String typename = GlobalConfig.getTypeName(type);
        int count;
        switch (type) {
            case "system": 
                MailListSystem = SQLManager.get().getMailList(type);
                count = MailListSystem.size();
                break;
            case "player": 
                MailListPlayer = SQLManager.get().getMailList(type);
                count = MailListPlayer.size();
                break;
            default:
                return;
        }
        Bukkit.getConsoleSender().sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+typename+"邮件列表["+count+"封]已更新");
        if(p!=null){
            p.sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+typename+"邮件列表["+count+"封]已更新");
        }
    }
    
    // 获取玩家可领取的邮件列表
    public static void getUnMailList(Player p, String type){
        switch (type) {
            case "system" :
                MailListSystemId.remove(p.getName());
                MailListSystemId.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                break;
            case "player" :
                MailListPlayerId.remove(p.getName());
                MailListPlayerId.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                break;
        }
        
    }
    
    // 获取此类
    public static MailBox getInstance(){
        return instance;
    }
    
    // 设置OpenCmd
    public void setOpenCmd(boolean enable){
        this.enCmdOpen = enable;
        if(enCmdOpen)Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:已启用指令打开邮箱GUI");
    }
    
    // 获取config配置信息
    private static String getConfigString(String path)
    {
        return config.getString(path);
    }
    private static int getConfigInt(String path)
    {
        return config.getInt(path);
    }
    private static boolean getConfigBoolean(String path)
    {
        return config.getBoolean(path);
    }
}
