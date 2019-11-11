package com.嘤嘤嘤.qwq.MailBox;

import com.嘤嘤嘤.qwq.MailBox.Events.JoinAndQuit;
import com.嘤嘤嘤.qwq.MailBox.Events.DoubleKeyPress;
import com.嘤嘤嘤.qwq.MailBox.Events.Mail;
import com.嘤嘤嘤.qwq.MailBox.Events.SingleKeyPress;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import com.嘤嘤嘤.qwq.MailBox.Utils.MySQLManager;
import com.嘤嘤嘤.qwq.MailBox.Utils.UpdateCheck;
import com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxGui;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxGui.setBoxConfig;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxHud.setHudConfig;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailContentGui.setContentConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import lk.vexview.api.VexViewAPI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class MailBox extends JavaPlugin {
    
    public static MailBox instance;
    // 首次启动
    private static boolean FirstEnable = true;
    // config 配置文件
    private static final String DATA_FOLDER = "plugins/VexMailBox";
    private static FileConfiguration config;
    private boolean enCmdOpen;
    // all 类型邮件
    public static HashMap<Integer, TextMail> MailListAll = new HashMap();
    public static ArrayList<Integer> MailListAllId = new ArrayList();
    public static HashMap<String, ArrayList<Integer>> MailListAllUn = new HashMap();
    
    @Override    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (label.equalsIgnoreCase("mailbox")||label.equalsIgnoreCase("mb")){
            if(args.length==0 && enCmdOpen){
                if((sender instanceof Player)){
                    MailBoxGui.openMailBoxGui((Player) sender);
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
                        UpdateCheck.check(sender);
                        return true;
                    }else{
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                        return true;
                    }
                }else{
                    return false;
                }
            }else if(args.length==2){
                if(args[0].equalsIgnoreCase("all")){
                    if(args[1].equalsIgnoreCase("update")){
                        if(sender.hasPermission("mailbox.admin.update.all")){
                            if(sender instanceof Player) updateMailList((Player) sender, "all");
                            else updateMailList(null, "all");
                            return true;
                        }else{
                            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                            return true;
                        }
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }
        return false;
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
                reloadPlugin();
                Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:插件启动完成");
                // 检查更新
                if(getConfigBoolean("mailbox.updateCheck"))UpdateCheck.check(Bukkit.getConsoleSender());
            }else{
                Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[VexView]版本小于2.5");
                Bukkit.getPluginManager().disablePlugin(this);
            }
            
        }else{
            Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[VexView]未安装，卸载插件");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable(){
        // 注销监听器
        HandlerList.unregisterAll(this);
        try{
            // 断开MySQL连接
            MySQLManager.get().shutdown();
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
                MySQLManager.get().shutdown();
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
        // config配置文件
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查配置文件是否存在");
        f = new File(DATA_FOLDER,"config.yml");
        if(f.exists()){
            // 使上一版本config.yml兼容新版本
            YamlConfiguration configFile  = YamlConfiguration.loadConfiguration(f);
            if(!configFile.contains("mailbox.updateCheck")){
                Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检测到config.yml缺失 mailbox.updateCheck 属性");
                configFile.set("mailbox.updateCheck", true);
                try {
                    configFile.save(f);
                    Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:存储新版本config.yml成功");
                } catch (IOException ex) {
                    Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:存储新版本config.yml失败");
                    this.getLogger().info(ex.getLocalizedMessage());
                }
            }
        }else{
            Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:配置文件不存在");
            saveDefaultConfig();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建配置文件");
        }
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:加载配置文件");
        reloadConfig();
        config = getConfig();
        setConfig();
        // 注册监听器
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:正在注册监听器");
        Bukkit.getPluginManager().registerEvents(new JoinAndQuit(getConfigBoolean("vexview.hud.enable")), this);
        Bukkit.getPluginManager().registerEvents(new Mail(), this);
        String key = getConfigString("vexview.gui.mailbox.openKey");
        if(key.equals("0")){
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:已关闭按键打开邮箱GUI");
        }else{
            if(key.contains("+")){
                int l = key.indexOf("+");
                String key1 = key.substring(0, l);
                String key2 = key.substring(l+1);
                Bukkit.getPluginManager().registerEvents(new DoubleKeyPress(Integer.parseInt(key1), Integer.parseInt(key2)), this);
                Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:已启用组合键打开邮箱GUI");
            }else{
                Bukkit.getPluginManager().registerEvents(new SingleKeyPress(Integer.parseInt(key)), this);
                Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:已启用单按键打开邮箱GUI");
            }
        }
        enCmdOpen = getConfigBoolean("vexview.gui.mailbox.openCmd");
        if(enCmdOpen)Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:已启用指令打开邮箱GUI");
        // 邮件文件夹（总）
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查邮件文件夹是否存在");
        f = new File(DATA_FOLDER+"/MailFiles/");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:创建邮件文件夹");
        }
        // 邮件文件夹（独立）
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查all邮件文件夹是否存在");
        f = new File(DATA_FOLDER+"/MailFiles/"+"all");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建all邮件文件夹");
        }
        // 连接MySQL数据库
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:正在连接数据库");
        MySQLManager.get().enableMySQL(
            getConfigString("mysql.ip"), 
            getConfigString("mysql.databasename"), 
            getConfigString("mysql.username"), 
            getConfigString("mysql.password"), 
            getConfigInt("mysql.port"), 
            getConfigString("mysql.prefix")
        );
        // 更新[ALL]邮件列表
        updateMailList(null, "all");
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
            getConfigString("mailbox.name.all"),
            fileDivS,
            getConfigString("mailbox.file.command.player")
        );
        // 设置HudConfig
        setHudConfig(
            getConfigInt("vexview.hud.x"),
            getConfigInt("vexview.hud.y"),
            getConfigInt("vexview.hud.w"),
            getConfigInt("vexview.hud.h"),
            getConfigInt("vexview.hud.ww"),
            getConfigInt("vexview.hud.hh")
        );
        // 设置BoxConfig
        setBoxConfig(
            getConfigString("vexview.gui.mailbox.colorBox"),
            getConfigString("vexview.gui.mailbox.colorWrite"),
            getConfigString("vexview.gui.mailbox.colorRead"),
            getConfigString("vexview.gui.mailbox.colorFile"),
            getConfigString("vexview.gui.mailbox.colorTopic"),
            getConfigString("vexview.gui.mailbox.colorQAQ"),
            getConfigString("vexview.gui.mailbox.colorSender"),
            getConfigString("vexview.gui.mailbox.nullBox")
        );
        // 设置ContentConfig
        setContentConfig(
            getConfigString("vexview.gui.mailcontent.colorSenderTitle"),
            getConfigString("vexview.gui.mailcontent.colorSender"),
            getConfigString("vexview.gui.mailcontent.colorDate"),
            getConfigString("vexview.gui.mailcontent.colorFile"),
            getConfigString("vexview.gui.mailcontent.colorCommand"),
            getConfigString("vexview.gui.mailcontent.colorCollect"),
            getConfigString("vexview.gui.mailcontent.colorDelete"),
            getConfigString("vexview.gui.mailcontent.colorConfirm")
        );
    }
    
    //更新邮件列表
    public static void updateMailList(Player p, String type){
        MailListAllId.clear();
        MailListAll = MySQLManager.get().getMailList(type);
        MailListAll.forEach((k, v) -> MailListAllId.add(k));
        Bukkit.getConsoleSender().sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+GlobalConfig.mailPrefix_ALL+"邮件列表["+MailListAllId.size()+"封]已更新");
        if(p!=null){
            p.sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+GlobalConfig.mailPrefix_ALL+"邮件列表["+MailListAllId.size()+"封]已更新");
        }
        
    }
    
    // 获取玩家可领取的邮件列表
    public static void getUnMailList(Player p, String type){
        ArrayList<Integer> l = MySQLManager.get().getUnMailList(p, type);
        MailListAllUn.put(p.getName(), l);
    }
    
    public static MailBox getInstance(){
        return instance;
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
