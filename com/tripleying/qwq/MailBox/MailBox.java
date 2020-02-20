package com.tripleying.qwq.MailBox;

import com.tripleying.qwq.MailBox.SQL.SQLManager;
import com.tripleying.qwq.MailBox.Mail.*;
import com.tripleying.qwq.MailBox.Utils.*;
import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.Events.JoinAndQuit;
import com.tripleying.qwq.MailBox.Events.MailChange;
import com.tripleying.qwq.MailBox.Events.VexInvGuiClose;
import com.tripleying.qwq.MailBox.Original.MailList;
import com.tripleying.qwq.MailBox.Original.MailNew;
import com.tripleying.qwq.MailBox.Original.MailView;
import com.tripleying.qwq.MailBox.VexView.MailBoxGui;
import com.tripleying.qwq.MailBox.VexView.MailBoxHud;
import com.tripleying.qwq.MailBox.VexView.MailItemListGui;
import com.tripleying.qwq.MailBox.VexView.VexViewConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import static org.bukkit.Material.AIR;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class MailBox extends JavaPlugin {
    
    private boolean enCmdOpen;
    private static MailBox instance;
    // config 配置文件
    private static YamlConfiguration config;
    // system 类型邮件
    private static final HashMap<Integer, BaseMail> SYSTEM_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> SYSTEM_RELEVANT = new HashMap();
    // player 类型邮件
    private static final HashMap<Integer, BaseMail> PLAYER_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> PLAYER_RELEVANT = new HashMap();
    // permission 类型邮件
    private static final HashMap<Integer, BaseMail> PERMISSION_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> PERMISSION_RELEVANT = new HashMap();
    // date 类型邮件
    private static final HashMap<Integer, BaseMail> DATE_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> DATE_RELEVANT = new HashMap();
    // times 类型邮件
    private static final HashMap<Integer, BaseMail> TIMES_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> TIMES_RELEVANT = new HashMap();
    // keytimes 类型邮件
    public static final HashMap<String, List<Integer>> KEYTIMES_KEY = new HashMap();
    private static final HashMap<Integer, BaseMail> KEYTIMES_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> KEYTIMES_RELEVANT = new HashMap();
    // cdkey 类型邮件
    public static final HashMap<String, Integer> CDKEY_DAY = new HashMap();
    private static final HashMap<Integer, BaseMail> CDKEY_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> CDKEY_RELEVANT = new HashMap();
      
    @Override
    public void onEnable(){
        // 获取实例
        instance = this;
        // 设置插件文件夹
        FileUtil.setDataFolder(getDataFolder());
        // 加载插件内部语言
        if(!ConfigMessage.set(this)) {
            Bukkit.getConsoleSender().sendMessage("-----§cMailBox: Super Super Super Error ! ! !-----");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        // 插件启动
        Bukkit.getConsoleSender().sendMessage(ConfigMessage.enable);
        // 打印作者
        Bukkit.getConsoleSender().sendMessage(ConfigMessage.author.replace("%author%", getDescription().getAuthors().toString()));
        // 打印网页
        Bukkit.getConsoleSender().sendMessage(ConfigMessage.website.replace("%website%", getDescription().getWebsite()));
        // 打印版本
        Bukkit.getConsoleSender().sendMessage(ConfigMessage.version.replace("%version%", getDescription().getVersion()));
        // 获取反射版本
        ReflectionUtil.getVersion();
        // 获取服务端核心
        String serverJar = Bukkit.getServer().getName();
        Bukkit.getConsoleSender().sendMessage(ConfigMessage.server_jar.replace("%server%", serverJar));
        // 获取MC版本
        String version = Bukkit.getServer().getVersion();
        version = version.substring(version.indexOf("MC")+3, version.length()-1).trim();
        String subversion = version.substring(0, version.lastIndexOf('.'));
        Bukkit.getConsoleSender().sendMessage(ConfigMessage.server_version.replace("%version%", version));
        // 判断MC版本
        if(GlobalConfig.server_under_1_11 = !VersionUtil.check(subversion, "1.11")){
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.under1_11);
            if(GlobalConfig.server_under_1_10 = !VersionUtil.check(subversion, "1.10")){
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.under1_10);
                if(GlobalConfig.server_under_1_9 = !VersionUtil.check(subversion, "1.9")){
                    Bukkit.getConsoleSender().sendMessage(ConfigMessage.under1_9);
                    // 更改配置文件默认编码
                    FileUtil.setEncoding(serverJar);
                    if(GlobalConfig.server_under_1_8 = !VersionUtil.check(subversion, "1.8")){
                        Bukkit.getConsoleSender().sendMessage(ConfigMessage.under1_8);
                    }
                }
            }
        }else{
            GlobalConfig.server_over_1_12 = VersionUtil.check(subversion, "1.13");
        }
        // 加载插件
        loadPlugin();
        // 插件启动完成
        Bukkit.getConsoleSender().sendMessage(ConfigMessage.enabled);
        // 更新修改时间
        TimeUtil.setLastTime(System.currentTimeMillis()/(1000*3600*24)*(1000*3600*24)+24*60*60*1000);
        // 检查更新
        if(config.getBoolean("mailbox.updateCheck")) VersionUtil.check(Bukkit.getConsoleSender(), 20L);
    }

    @Override
    public void onDisable(){
        // 插件关闭
        Bukkit.getConsoleSender().sendMessage(ConfigMessage.disable);
        // 卸载插件
        unloadPlugin();
        // 插件关闭完毕
        Bukkit.getConsoleSender().sendMessage(ConfigMessage.diasbled);
    }
    
    // 检查前置插件
    private void checkSoftDepend(){
        // [Vault]
        if(config.getBoolean("softDepend.Vault",true)){
            boolean enable;
            if(getServer().getPluginManager().getPlugin("Vault") == null){
                enable = false;
            }else{
                RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp == null){
                    enable = false;
                }else{
                    enable = VaultUtil.setEconomy(rsp.getProvider());
                }
            }
            if(GlobalConfig.enVault = enable){
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_enable.replace("%plugin%", "Vault").replace("%version%", Bukkit.getPluginManager().getPlugin("Vault").getDescription().getVersion()));
            }else{
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_close.replace("%plugin%", "Vault"));
            }
        }else{
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_disable.replace("%plugin%", "Vault"));
            GlobalConfig.enVault = false;
        }
        // [PlayerPoints]
        if(config.getBoolean("softDepend.PlayerPoints",true)){
            Plugin plugin = getServer().getPluginManager().getPlugin("PlayerPoints");
            boolean enable;
            if(plugin == null){
                enable = false;
            }else{
                enable = PlayerPointsUtil.setPoints(PlayerPoints.class.cast(plugin));
            }
            if(GlobalConfig.enPlayerPoints = enable){
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_enable.replace("%plugin%", "PlayerPoints").replace("%version%", Bukkit.getPluginManager().getPlugin("PlayerPoints").getDescription().getVersion()));
            }else{
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_close.replace("%plugin%", "PlayerPoints"));
            }
        }else{
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_disable.replace("%plugin%", "PlayerPoints"));
            GlobalConfig.enPlayerPoints = false;
        }
        // [PlaceholderAPI]
        if(config.getBoolean("softDepend.PlaceholderAPI",true)){
            if (GlobalConfig.enPlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_enable.replace("%plugin%", "PlaceholderAPI").replace("%version%", Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion()));
                if(PlaceholderAPI.isRegistered("mailbox")) PlaceholderAPI.unregisterPlaceholderHook("mailbox");
                new com.tripleying.qwq.MailBox.Placeholder.MailBoxExpansion().register();
            }else{
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_close.replace("%plugin%", "PlaceholderAPI"));
            }
        }else{
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_disable.replace("%plugin%", "PlaceholderAPI"));
            GlobalConfig.enPlaceholderAPI = false;
        }
        // [VexView]
        GlobalConfig.enVexView = false;
        GlobalConfig.vexview_under_2_6_8 = false;
        GlobalConfig.vexview_under_2_6 = false;
        GlobalConfig.vexview_over_2_5 = false;
        GlobalConfig.vexview_under_2_5 = false;
        if(config.getBoolean("softDepend.VexView",true)){
            if(Bukkit.getPluginManager().isPluginEnabled("VexView")){
                String version = VexViewAPI.getVexView().getVersion();
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_enable.replace("%plugin%", "VexView").replace("%version%", version));
                GlobalConfig.enVexView = true;
                // 检查[VexView]版本号
                if(VersionUtil.check(version, "2.6.8")){
                    Bukkit.getPluginManager().registerEvents(new VexInvGuiClose(), this);
                }else{
                    if(GlobalConfig.server_over_1_12){
                        Bukkit.getConsoleSender().sendMessage(ConfigMessage.vexview_under2_6_8_server_over1_12);
                        GlobalConfig.enVexView = false;
                    }else{
                        GlobalConfig.vexview_under_2_6_8 = true;
                        Bukkit.getConsoleSender().sendMessage(ConfigMessage.vexview_under2_6_8);
                        if(!VersionUtil.check(version, "2.6.0")){
                            GlobalConfig.vexview_under_2_6 = true;
                            Bukkit.getConsoleSender().sendMessage(ConfigMessage.vexview_under2_6);
                            if(VersionUtil.check(version, "2.5.6")){
                                Bukkit.getPluginManager().registerEvents(new VexInvGuiClose(), this);
                            }else{
                                Bukkit.getConsoleSender().sendMessage(ConfigMessage.vexview_under2_5_6);
                                if(!VersionUtil.check(version, "2.5.0")){
                                    Bukkit.getConsoleSender().sendMessage(ConfigMessage.vexview_under2_5);
                                    GlobalConfig.vexview_under_2_5 = true;
                                    if(!VersionUtil.check(version, "2.4.0")){
                                        Bukkit.getConsoleSender().sendMessage(ConfigMessage.vexview_under2_4);
                                        GlobalConfig.enVexView = false;
                                    }
                                }else if(version.equals("2.5")){
                                }else{
                                    Bukkit.getConsoleSender().sendMessage(ConfigMessage.vexview_under2_5_6_over2_5);
                                    GlobalConfig.vexview_over_2_5 = true;
                                }
                            }
                        }else if(version.equals("2.6")){
                        }else{
                            Bukkit.getConsoleSender().sendMessage(ConfigMessage.vexview_under2_6_8_over2_6);
                            GlobalConfig.enVexView = false;
                        }
                    }
                }
            }else{
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_close.replace("%plugin%", "VexView"));
            }
        }else{
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_disable.replace("%plugin%", "VexView"));
        }
        // [LocaleLanguageAPI]
        if(config.getBoolean("softDepend.LocaleLanguageAPI",true)){
            if (GlobalConfig.enLocaleLanguageAPI = Bukkit.getPluginManager().isPluginEnabled("LocaleLanguageAPI")) {
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_enable.replace("%plugin%", "LocaleLanguageAPI").replace("%version%", Bukkit.getPluginManager().getPlugin("LocaleLanguageAPI").getDescription().getVersion()));
            }else{
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_close.replace("%plugin%", "PlaceholderAPI"));
            }
        }else{
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.soft_depend_disable.replace("%plugin%", "PlaceholderAPI"));
            GlobalConfig.enLocaleLanguageAPI = false;
        }
    }
    
    // 重载插件
    private void reloadPlugin(){
        unloadPlugin();
        loadPlugin();
    }
    
    // 卸载插件
    private void unloadPlugin(){
        // 注销监听器
        HandlerList.unregisterAll(this);
        // 移除HUD
        if(GlobalConfig.enVexView){
            Bukkit.getOnlinePlayers().stream().forEach(player -> VexViewAPI.removeHUD(player, MailBoxHud.id));
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.vexview_hud_close);
        }
        // 注销PlaceholderAPI占位符
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && PlaceholderAPI.isRegistered("mailbox")){
            PlaceholderAPI.unregisterPlaceholderHook("mailbox");
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.placeholder_unhook);
        }
        // 断开MySQL连接
        try{
            SQLManager.get().shutdown();
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.sql_shutdown);
        }catch(Exception e){
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.sql_shutdown_error);
        }
    }
    
    // 加载插件
    private void loadPlugin(){
        // 加载插件内部语言
        if(!ConfigMessage.set(this)) {
            Bukkit.getConsoleSender().sendMessage("-----§cMailBox: Super Super Super Error ! ! !-----");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        // 插件文件夹
        File f = getDataFolder();
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.folder_create.replace("%folder%", "MailBox"));
        }
        // 语言文件夹
        f = FileUtil.getFile("Message");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.folder_create.replace("%folder%", "Message"));
        }
        // 读取config配置文件
        config = FileUtil.getConfig("", "config.yml", "");
        // 检查前置
        checkSoftDepend();
        // 设置GlobalConfig
        GlobalConfig.setGlobalConfig(config);
        if(GlobalConfig.enVexView){
            // 设置VexViewConfig
            VexViewConfig.VexViewConfigSet();
        }else{
            Bukkit.getPluginManager().registerEvents(new JoinAndQuit(false, false), this);
        }
        Bukkit.getPluginManager().registerEvents(new MailChange(), this);
        // 邮件文件夹（总）
        f = FileUtil.getFile("MailFiles");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.folder_create.replace("%folder%", "MailFiles"));
        }
        // 邮件文件夹
        for(String type:MailUtil.getTrueType()){
            f = FileUtil.getFile("MailFiles/"+type);
            if(!f.exists()){
                f.mkdir();
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.folder_create.replace("%folder%", type));
            }
        }
        // 模板文件夹
        f = FileUtil.getFile("Template");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.folder_create.replace("%folder%", "Template"));
        }
        // 兑换码文件夹
        f = FileUtil.getFile("Cdkey");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.folder_create.replace("%folder%", "Cdkey"));
        }
        // 连接数据库
        Bukkit.getConsoleSender().sendMessage(ConfigMessage.sql_connect);
        if(config.getBoolean("database.enableMySQL")){
            SQLManager.get().enableMySQL(
                config.getString("database.mySQLhost"), 
                config.getString("database.dataBaseName"), 
                config.getString("database.mySQLusername"), 
                config.getString("database.mySQLpassword"), 
                config.getInt("database.mySQLport"), 
                config.getString("database.dataTablePrefix")
            );
        }else{
            SQLManager.get().enableSQLite(
                config.getString("database.dataBaseName"), 
                config.getString("database.dataTablePrefix")
            );
        }
        // 更新邮件列表
        MailUtil.getTrueType().forEach((type) -> {
            updateMailList(null, type);
        });
        Bukkit.getOnlinePlayers().forEach((p) -> {
            MailBox.updateRelevantMailList(p, "all");
        });
    }
    
    @Override    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (label.equals("mailbox") || label.equals("mb")){
            if(args.length==0){
                if(GlobalConfig.enVexView && enCmdOpen && sender instanceof Player && MailBoxAPI.hasPlayerPermission(sender, "mailbox.gui.mailbox")){
                    MailBoxGui.openMailBoxGui((Player)sender, "Recipient");
                }else{
                    MailList.list(sender, "Recipient");
                }
            }else if(args.length==1){
                onCommandNormal(sender, args[0]);
            }else if(args.length>=2){
                switch (args[0]) {
                    case "cdk":
                        if(sender instanceof Player && MailBoxAPI.hasPlayerPermission(sender, "mailbox.cdkey.use")) CdkeyUtil.exchangeCdkey(((Player)sender), args[1]);
                        else sender.sendMessage(Message.globalNoPermission);
                        break;
                    case "item":
                        if(sender.hasPermission("mailbox.admin.item")) onCommandItem(sender,args);
                        else sender.sendMessage(Message.globalNoPermission);
                        break;
                    case "template":
                    case "send":
                        onCommandTemplate(sender,args);
                        break;
                    case "system":
                    case "player":
                    case "permission":
                    case "date":
                    case "times":
                    case "keytimes":
                    case "cdkey":
                        onCommandMail(sender, args);
                        break;
                    default:
                        sender.sendMessage(Message.commandInvalid);
                }
            }else{
                return true;
            }
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                if(args[0].length()==0){
                    ArrayList<String> l = new ArrayList();
                    if(MailBoxAPI.hasPlayerPermission(sender, "mailbox.cdkey.use")) l.add("cdk");
                    if(sender.hasPermission("mailbox.admin.check")) l.add("check");
                    l.add("date");
                    if(sender.hasPermission("mailbox.admin.item")) l.add("item");
                    l.add("keytimes");
                    l.add("new");
                    if(MailBoxAPI.hasPlayerPermission(sender, "mailbox.onekey")) l.add("ok");
                    l.add("permission");
                    l.add("player");
                    l.add("receivebox");
                    if(sender.hasPermission("mailbox.admin.reload")) l.add("reload");
                    if(sender.hasPermission("mailbox.admin.template.send")) l.add("send");
                    l.add("sendbox");
                    l.add("system");
                    if(sender.hasPermission("mailbox.admin.template")) l.add("template");
                    l.add("times");
                    return l;
                }
                if(args[0].length()>1){
                    if(args[0].startsWith("cdk") && sender.hasPermission("mailbox.admin.see.cdkey")) return Arrays.asList("cdkey");
                    if(args[0].startsWith("cd")){
                        List<String> temp = new ArrayList();
                        if(MailBoxAPI.hasPlayerPermission(sender, "mailbox.cdkey.use")) temp.add("cdk");
                        if(sender.hasPermission("mailbox.admin.see.cdkey") 
                            || sender.hasPermission("mailbox.admin.reset.cdkey")
                            || sender.hasPermission("mailbox.admin.create.cdkey")
                            || sender.hasPermission("mailbox.admin.export.cdkey")) temp.add("cdkey");
                        if(!temp.isEmpty()) return temp;
                    }
                    if(args[0].startsWith("ch") && sender.hasPermission("mailbox.admin.check")) return Arrays.asList("check");
                    if(args[0].startsWith("pe")) return Arrays.asList("permission");
                    if(args[0].startsWith("pl")) return Arrays.asList("player");
                    if(args[0].startsWith("rec")) return Arrays.asList("receivebox");
                    if(args[0].startsWith("rel") && sender.hasPermission("mailbox.admin.reload")) return Arrays.asList("reload");
                    if(args[0].startsWith("se")){
                        if(sender.hasPermission("mailbox.admin.template.send")) return Arrays.asList("send","sendbox");
                        return Arrays.asList("sendbox");
                    }
                    if(args[0].startsWith("sy")) return Arrays.asList("system");
                    if(args[0].startsWith("te")) return Arrays.asList("template");
                    if(args[0].startsWith("ti")) return Arrays.asList("times");
                }
                switch (args[0].substring(0,1)){
                    case "c":
                        ArrayList<String> l = new ArrayList();
                        if(MailBoxAPI.hasPlayerPermission(sender, "mailbox.cdkey.use")) l.add("cdk");
                        if(sender.hasPermission("mailbox.admin.see.cdkey") 
                            || sender.hasPermission("mailbox.admin.reset.cdkey")
                            || sender.hasPermission("mailbox.admin.create.cdkey")
                            || sender.hasPermission("mailbox.admin.export.cdkey")) l.add("cdkey");
                        if(sender.hasPermission("mailbox.admin.check")) l.add("check");
                        if(l.isEmpty()) break;
                        else return l;
                    case "d":
                        return Arrays.asList("date");
                    case "i":
                        if(sender.hasPermission("mailbox.admin.item")) return Arrays.asList("item");
                        else break;
                    case "k":
                        return Arrays.asList("keytimes");
                    case "n":
                        return Arrays.asList("new");
                    case "o":
                        return Arrays.asList("ok");
                    case "p":
                        return Arrays.asList("permission","player");
                    case "r":
                        if(sender.hasPermission("mailbox.admin.reload")) return Arrays.asList("receivebox","reload");
                        return Arrays.asList("receivebox");
                    case "s":
                        if(sender.hasPermission("mailbox.admin.template.send")) return Arrays.asList("sendbox","system","send");
                        return Arrays.asList("sendbox","system");
                    case "t":
                        if(sender.hasPermission("mailbox.admin.template")) return Arrays.asList("template","times");
                        return Arrays.asList("times");
                }   break;
            case 2:
                switch (args[0]){
                    case "date":
                    case "system":
                    case "permission":
                    case "player":
                    case "times":
                    case "keytimes":
                    case "cdkey":
                        if(args[1].length()==0){
                            ArrayList<String> l = new ArrayList();
                            if(args[0].equals("player") && sender.hasPermission("mailbox.admin.clean.player")) l.add("clean");
                            l.add("collect");
                            if(args[0].equals("cdkey") && sender.hasPermission("mailbox.admin.create.cdkey")) l.add("create");
                            l.add("delete");
                            if(sender.hasPermission("mailbox.admin.download")) l.add("download");
                            if(args[0].equals("cdkey") && sender.hasPermission("mailbox.admin.export.cdkey")) l.add("export");
                            if(args[0].equals("cdkey") && sender.hasPermission("mailbox.admin.reset.cdkey")) l.add("reset");
                            l.add("see");
                            if(sender.hasPermission("mailbox.admin.delete."+args[0])) l.add("update");
                            if(sender.hasPermission("mailbox.admin.upload")) l.add("upload");
                            return l;
                        }
                        if(args[1].length()>1){
                            if(args[1].startsWith("cr") && args[0].equals("cdkey") && sender.hasPermission("mailbox.admin.create.cdkey")) return Arrays.asList("create");
                            if(args[1].startsWith("cl") && args[0].equals("player") && sender.hasPermission("mailbox.admin.clean.player")) return Arrays.asList("clean");
                            if(args[1].startsWith("co")) return Arrays.asList("collect");
                            if(args[1].startsWith("de")) return Arrays.asList("delete");
                            if(args[1].startsWith("cr") && args[0].equals("cdkey") && sender.hasPermission("mailbox.admin.create.cdkey")) return Arrays.asList("create");
                            if(args[1].startsWith("do") && (sender.hasPermission("mailbox.admin.download"))) return Arrays.asList("download");
                            if(args[1].startsWith("upd") && sender.hasPermission("mailbox.admin.delete."+args[0])) return Arrays.asList("update");
                            if(args[1].startsWith("upl") && (sender.hasPermission("mailbox.admin.upload"))) return Arrays.asList("upload");
                        }
                        switch (args[1].substring(0,1)){
                            case "c":
                                ArrayList<String> lc = new ArrayList();
                                if(args[0].equals("player") && sender.hasPermission("mailbox.admin.clean.player")) lc.add("clean");
                                lc.add("collect");
                                if(args[0].equals("cdkey") && sender.hasPermission("mailbox.admin.create.cdkey")) return Arrays.asList("create");
                                return lc;
                            case "d":
                                if(sender.hasPermission("mailbox.admin.download")) return Arrays.asList("date","download");
                                else return Arrays.asList("date");
                            case "e":
                                if(args[0].equals("cdkey") && sender.hasPermission("mailbox.admin.export.cdkey")) return Arrays.asList("export");
                                else break;
                            case "r":
                                if(args[0].equals("cdkey") && sender.hasPermission("mailbox.admin.reset.cdkey")) return Arrays.asList("reset");
                                else break;
                            case "s":
                                return Arrays.asList("see");
                            case "u":
                                ArrayList<String> lu = new ArrayList();
                                if(sender.hasPermission("mailbox.admin.delete."+args[0])) lu.add("update");
                                if(sender.hasPermission("mailbox.admin.upload")) lu.add("upload");
                                if(!lu.isEmpty()) return lu;
                        }   break;
                    case "item":
                        if(sender.hasPermission("mailbox.admin.item")){
                            if(args[1].length()==0){
                                return Arrays.asList("export","id","import","list");
                            }
                            if(args[1].length()>1){
                                if(args[1].startsWith("id")) return Arrays.asList("id");
                                if(args[1].startsWith("im")) return Arrays.asList("import");
                            }
                            switch (args[1].substring(0,1)){
                                case "e":
                                    return Arrays.asList("export");
                                case "i":
                                    return Arrays.asList("id","import");
                                case "l":
                                    return Arrays.asList("list");
                            }   break;
                        }
                }
                break;
            case 3:
                switch (args[0]){
                    case "date":
                    case "system":
                    case "permission":
                    case "player":
                    case "times":
                    case "keytimes":
                    case "cdkey":
                        if((args[1].equals("download") && sender.hasPermission("mailbox.admin.download"))
                            || (args[1].equals("upload") && sender.hasPermission("mailbox.admin.upload"))) return Arrays.asList("all");
                        else break;
                    case "template":
                    case "send":
                        if(sender.hasPermission("mailbox.admin.template") || sender.hasPermission("mailbox.admin.template.send")){
                            if(args[2].length()==0) return Arrays.asList("cdkey","date","online","permission","player","system","template","times");
                            if(args[2].length()>1){
                                if(args[2].startsWith("pe")) return Arrays.asList("permission");
                                if(args[2].startsWith("pl")) return Arrays.asList("player");
                                if(args[2].startsWith("te")) return Arrays.asList("template");
                                if(args[2].startsWith("ti")) return Arrays.asList("times");
                            }
                            switch (args[2].substring(0,1)){
                                case "c":
                                    return Arrays.asList("cdkey");
                                case "d":
                                    return Arrays.asList("date");
                                case "k":
                                    return Arrays.asList("keytimes");
                                case "o":
                                    return Arrays.asList("online");
                                case "p":
                                    return Arrays.asList("permission","player");
                                case "s":
                                    return Arrays.asList("system");
                                case "t":
                                    return Arrays.asList("template","times");
                            }   break;
                        }   break;
                }
        }
        return null;
    }
    
    private void onCommandNormal(CommandSender sender, String arg){
        if(sender==null) return;
        switch (arg) {
            case "help":
                (sender.hasPermission("mailbox.admin.help") ? Message.helpAdmin : Message.helpPlayer).forEach(s -> sender.sendMessage(s));
                break;
            case "sendbox":
            case "sb":
                if((sender instanceof Player)){
                    MailList.list(sender, "Sender");
                }else{
                    sender.sendMessage(Message.commandOnlyPlayer);
                }
                break;
            case "receivebox":
            case "rb":
                MailList.list(sender, "Recipient");
                break;
            case "new":
                MailNew.New(sender);
                break;
            case "onekey":
            case "ok":
                if(sender instanceof Player && MailBoxAPI.hasPlayerPermission(sender, "mailbox.onekey")){
                    Player p = (Player)sender;
                    MailUtil.getTrueTypeWhithoutSpecial().stream().filter((type) -> MailBoxAPI.hasPlayerPermission(p, "mailbox.see."+type)).map((type) -> {
                        MailBox.updateRelevantMailList(p, type);
                        return type;
                    }).forEachOrdered(type -> {
                        HashMap<Integer, BaseMail> mhm = MailBox.getMailHashMap(type);
                        MailBox.getRelevantMailList(p, type).get("asRecipient").forEach(id -> {
                            if(mhm.get(id) instanceof BaseFileMail) mhm.get(id).Collect(p);
                        });
                    });
                }else{
                    sender.sendMessage(Message.globalNoPermission);
                }   break;
            case "reload":
                if(sender.hasPermission("mailbox.admin.reload")){
                    reloadPlugin();
                    sender.sendMessage(Message.commandReload);
                }else{
                    sender.sendMessage(Message.globalNoPermission);
                }
                break;
            case "check":
                if(sender.hasPermission("mailbox.admin.check")){
                    VersionUtil.check(sender, 0L);
                }else{
                    sender.sendMessage(Message.globalNoPermission);
                }
                break;
            default:
                sender.sendMessage(Message.commandInvalid);
        }
    }
    
    private void onCommandItem(CommandSender sender, String[] args){
        if(sender==null) return;
        ItemStack is;
        switch (args[1]) {
            case "list":
                List<String> list = ItemUtil.getExportList();
                if(list.isEmpty()){
                    sender.sendMessage(Message.commandEmptyItemList);
                }else{
                    if(GlobalConfig.vexview_under_2_5 || !(sender instanceof Player)){
                        int i = 0;
                        for(String name:ItemUtil.getExportList()){
                            sender.sendMessage("§b"+(++i)+". §e"+name);
                        }
                    }else{
                        MailItemListGui.openItemListGui((Player)sender, list);
                    }
                }   break;
            case "export":
                if(args.length!=3){
                    sender.sendMessage(Message.commandInvalid);
                    break;
                }
                if(sender instanceof Player){
                    if(GlobalConfig.server_under_1_9) is = ((Player)sender).getInventory().getItemInHand();
                    else is = ((Player)sender).getInventory().getItemInMainHand();
                    if(!is.getType().equals(AIR) && ItemUtil.exportItem(is, args[2])){
                        sender.sendMessage(Message.commandExportItemSuccess);
                    }else{
                        sender.sendMessage(Message.commandExportItemError);
                    }
                }else{
                    sender.sendMessage(Message.commandOnlyPlayer);
                }   break;
            case "import":
                if(args.length==3){
                    is = ItemUtil.importItem(args[2]);
                }else{
                    sender.sendMessage(Message.commandInvalid);
                    break;
                }
                if(is==null){
                    sender.sendMessage(Message.commandReadItemError);
                }else{
                    if(sender instanceof Player){
                        ((Player)sender).getInventory().addItem(is);
                    }else{
                        sender.sendMessage(ItemUtil.getName(is)+'\n'+"§a"+ReflectionUtil.Item2Json(is).replace(',', '\n'));
                    }
                    sender.sendMessage(Message.commandImportItemSuccess);
                }   break;
            case "id":
                if(sender instanceof Player){
                    if(GlobalConfig.server_under_1_9) is = ((Player)sender).getInventory().getItemInHand();
                    else is = ((Player)sender).getInventory().getItemInMainHand();
                    if(is==null){
                        sender.sendMessage(Message.commandReadItemError);
                    }else{
                        sender.sendMessage("Material: "+is.getType().name());
                    }
                }else{
                    sender.sendMessage(Message.commandOnlyPlayer);
                }   break;
            default:
                sender.sendMessage(Message.commandInvalid);
        }
    }
    
    private void onCommandTemplate(CommandSender sender, String[] args){
        if(sender.hasPermission("mailbox.admin.template")){
            BaseMail bm = TemplateUtil.loadTemplateMail(args[1]);
            if(bm==null){
                sender.sendMessage(Message.commandFileNotExist);
                return;
            }
            if(args.length==2 && args[0].equals("template")){
                MailNew.New(sender, bm);
            }else if(args.length>=3){
                if(!MailBoxAPI.getAllType().contains(args[2])){
                    sender.sendMessage(Message.commandMailTypeNotExist);
                    return;
                }
                if(!sender.hasPermission("mailbox.admin.send."+args[2])){
                    sender.sendMessage(Message.globalNoPermission);
                    return;
                }
                bm = bm.setType(args[2]);
                if(args.length==3 && args[0].equals("template")){
                    MailNew.New(sender, bm);
                }else{
                    switch (args[2]) {
                        case "cdkey":
                            boolean only;
                            try{
                                only = Boolean.parseBoolean(args[3]);
                            }catch(NumberFormatException e){
                                sender.sendMessage(Message.commandMailNewCdkeyOnly);
                                return;
                            }
                            ((MailCdkey)bm).setOnly(only);
                            break;
                        case "keytimes":
                            if(args.length==5){
                                ((MailKeyTimes)bm).setKey(args[4].replace('&', '§'));
                            }else{
                                sender.sendMessage(Message.commandMailNewKeytimesLength);
                                return;
                            }
                        case "times":
                            int times;
                            try{
                                times = Integer.parseInt(args[3]);
                            }catch(NumberFormatException e){
                                sender.sendMessage(Message.commandMailNewTimesCount);
                                return;
                            }
                            if(times<1) {
                                sender.sendMessage(Message.commandMailNewTimesZero);
                                return;
                            }
                            if(times>GlobalConfig.timesCount && !sender.hasPermission("mailbox.admin.send.check.times")){
                                sender.sendMessage(Message.commandMailNewTimesMax.replace("%max%", Integer.toString(GlobalConfig.timesCount)));
                                return;
                            }
                            ((MailTimes)bm).setTimes(times);
                            break;
                        case "template":
                            ((MailTemplate)bm).setTemplate(args[3]);
                            break;
                        case "permission":
                            ((MailPermission)bm).setPermission(args[3]);
                            break;
                        case "player":
                            ArrayList<String> rl = new ArrayList();
                            for(int i=3;i<args.length;i++){
                                rl.add(args[i]);
                            }
                            ((MailPlayer)bm).setRecipient(rl);
                            break;
                        case "date":
                            if(args.length==5){
                                if(args[3].equals("0")){
                                    bm.setDate("0");
                                }else{
                                    List<Integer> t = TimeUtil.toDate(args[3], sender, null);
                                    switch (t.size()) {
                                        case 3:
                                        case 6:
                                            String date = TimeUtil.toDate(t, sender, null);
                                            if(date==null){
                                                return;
                                            }else{
                                                bm.setDate(date);
                                            }
                                            break;
                                        default:
                                            sender.sendMessage(Message.commandMailNewDateTime);
                                        return;
                                    }
                                }
                                if(args[4].equals("0")){
                                    ((MailDate)bm).setDeadline("0");
                                }else{
                                    List<Integer> t = TimeUtil.toDate(args[3], sender, null);
                                    switch (t.size()) {
                                        case 3:
                                        case 6:
                                            String date = TimeUtil.toDate(t, sender, null);
                                            if(date==null){
                                                return;
                                            }else{
                                                ((MailDate)bm).setDeadline(date);
                                            }
                                            break;
                                        default:
                                            sender.sendMessage(Message.commandMailNewDateTime);
                                        return;
                                    }
                                }
                            }else{
                                sender.sendMessage(Message.commandMailNewDateLength);
                                return;
                            }
                            break;
                        case "online":
                        case "system":
                            break;
                        default:
                            sender.sendMessage(Message.commandMailTypeNotExist);
                            return;
                    }
                    if(args[0].equals("template")){
                        MailNew.New(sender, bm);
                    }else if(sender.hasPermission("mailbox.admin.template.send")){
                        if(bm.getSender()==null){
                            sender.sendMessage(Message.commandMailSendSender);
                        }else{
                            bm.Send(sender, null);
                        }
                    }else{
                        sender.sendMessage(Message.globalNoPermission);
                    }
                }
            }else{
                sender.sendMessage(Message.commandInvalid);
            }
        }else{
            sender.sendMessage(Message.globalNoPermission);
        }
    }
    
    private void onCommandMail(CommandSender sender, String[] args){
        String type = args[0];
        if(args.length==2){
            if(args[1].equals("update")){
                if(sender.hasPermission("mailbox.admin.update."+type)){
                    if(sender instanceof Player) updateMailList((Player) sender, type);
                    else updateMailList(null, type);
                }else{
                    sender.sendMessage(Message.globalNoPermission);
                }
            }else if(args[1].equals("clean") && (type.equals("player") || type.equals("date") || type.equals("times") || type.equals("keytimes"))){
                if(sender.hasPermission("mailbox.admin.clean."+type)){
                    StringBuilder t = new StringBuilder("");
                    if((type.equals("player"))) PLAYER_LIST.forEach((Integer k, BaseMail v) -> { if(v.ExpireValidate()) if(v.Delete(null)) t.append("1"); });
                    if((type.equals("date"))) DATE_LIST.forEach((Integer k, BaseMail v) -> { if(v.ExpireValidate()) if(v.Delete(null)) t.append("1"); });
                    if((type.equals("times"))) TIMES_LIST.forEach((Integer k, BaseMail v) -> { if(v.ExpireValidate()) if(v.Delete(null)) t.append("1"); });
                    if((type.equals("keytimes"))) KEYTIMES_LIST.forEach((Integer k, BaseMail v) -> { if(v.ExpireValidate()) if(v.Delete(null)) t.append("1"); });
                    sender.sendMessage(Message.commandMailClean.replace("%type%", Message.getTypeName(type)).replace("%count%", Integer.toString(t.length())));
                }else{
                    sender.sendMessage(Message.globalNoPermission);
                }
            }else{
            }
        }else{
            switch (args[1]) {
                case "see":
                    try{
                        MailView.view(type, Integer.parseInt(args[2]), sender);
                    }catch(NumberFormatException e){
                        sender.sendMessage(Message.commandMailIdError);
                    }
                    break;
                case "collect":
                    try{
                        MailView.collect(type, Integer.parseInt(args[2]), sender);
                    }catch(NumberFormatException e){
                        sender.sendMessage(Message.commandMailIdError);
                    } 
                    break;
                case "delete":
                    try{
                        MailView.delete(type, Integer.parseInt(args[2]), sender);
                    }catch(NumberFormatException e){
                        sender.sendMessage(Message.commandMailIdError);
                    }
                    break;
                case "create":
                    if(!type.equals("cdkey") || !sender.hasPermission("mailbox.admin.createt.cdkey")){
                        sender.sendMessage(Message.commandInvalid);
                        break;
                    }
                    updateMailList(null, "cdkey");
                    MailCdkey mc;
                    try{
                        int mail = Integer.parseInt(args[2]);
                        mc = (MailCdkey)MailBox.getMailHashMap(type).get(mail);
                    }
                    catch(NumberFormatException e){
                        sender.sendMessage(Message.commandMailIdError);
                        break;
                    }
                    if(mc==null){
                        sender.sendMessage(Message.commandMailNull);
                        break;
                    }
                    int count;
                    if(args.length==3 || mc.isOnly()){
                        count = 1;
                    }else{
                        try{
                            count = Integer.parseInt(args[3]);
                        }
                        catch(NumberFormatException e){
                            sender.sendMessage(Message.commandMailCdkeyCreateError);
                            break;
                        }
                    }
                    sender.sendMessage(Message.commandMailCdkeyCreate.replace("%count%", Integer.toString(mc.generateCdkey(count))));
                    break;
                case "export":
                    if(!type.equals("cdkey") || !sender.hasPermission("mailbox.admin.export.cdkey")){
                        sender.sendMessage(Message.commandInvalid);
                        break;
                    }
                    if(args[2].equals("all")){
                        updateMailList(null, "cdkey");
                        getMailHashMap("cdkey").forEach((k,v) -> {
                            if(CdkeyUtil.exportCdkey(k)){
                                sender.sendMessage(Message.commandMailCdkeyExportSuccess+" - "+k);
                            }else{
                                sender.sendMessage(Message.commandMailCdkeyExportError+" - "+k);
                            }
                        });
                    }else{
                        try{
                            int mail = Integer.parseInt(args[2]);
                            if(MailBox.getMailHashMap(type).containsKey(mail)){
                                if(CdkeyUtil.exportCdkey(mail)){
                                    sender.sendMessage(Message.commandMailCdkeyExportSuccess);
                                }else{
                                    sender.sendMessage(Message.commandMailCdkeyExportError);
                                }
                            }else{
                                sender.sendMessage(Message.commandMailNull);
                            }
                        }
                        catch(NumberFormatException e){
                            sender.sendMessage(Message.commandMailIdError);
                        }
                    }   break;
                case "reset":
                    if(!type.equals("cdkey") || !sender.hasPermission("mailbox.admin.reset.cdkey")){
                        sender.sendMessage(Message.commandInvalid);
                        break;
                    }
                    if(args.length==3){
                        CdkeyUtil.cdkeyDayRemove(args[2]);
                        sender.sendMessage(Message.cdkeyReset.replace("%player%", args[2]));
                    }else{
                        sender.sendMessage(Message.commandInvalid);
                    }   break;
                case "upload":
                case "download":
                    if(args.length==3){
                        String load = args[1];
                        if(sender.hasPermission("mailbox.admin."+load)){
                            if(args[2].equals("all")){
                                if(load.equals("upload")) MailFileUtil.uploadFile(sender, type);
                                else MailFileUtil.downloadFile(sender, type);
                            }else{
                                int mail;
                                try{
                                    mail = Integer.parseInt(args[2]);
                                    BaseMail bm = MailBox.getMailHashMap(type).get(mail);
                                    if(bm!=null && (bm instanceof BaseFileMail)){
                                        String filename = ((BaseFileMail)bm).getFileName();
                                        if(args[1].equals("upload")){
                                            if(MailFileUtil.uploadFile(type, filename)){
                                                sender.sendMessage(Message.fileSuccess.replace("%file%", filename).replace("%state%", Message.fileUpload));
                                            }else{
                                                sender.sendMessage(Message.fileError.replace("%file%", filename).replace("%state%", Message.fileUpload));
                                            }
                                        }else{
                                            if(MailFileUtil.downloadFile(type, filename)){
                                                sender.sendMessage(Message.fileSuccess.replace("%file%", filename).replace("%state%", Message.fileDownload));
                                            }else{
                                                sender.sendMessage(Message.fileError.replace("%file%", filename).replace("%state%", Message.fileDownload));
                                            }
                                        }
                                    }else{
                                        sender.sendMessage(Message.fileNotFile);
                                    }
                                }
                                catch(NumberFormatException e){
                                    sender.sendMessage(Message.commandMailIdError);
                                }
                            }
                        }else{
                            sender.sendMessage(Message.globalNoPermission);
                        }
                    }else{
                        sender.sendMessage(Message.commandInvalid);
                    }   break;
                default:
                    sender.sendMessage(Message.commandInvalid);
                    break;
            }
        }
    }
    
    //更新邮件列表
    public static void updateMailList(Player p, String type){
        int count;
        switch (type) {
            case "system": 
                SYSTEM_LIST.clear();
                SYSTEM_LIST.putAll(SQLManager.get().getMailList(type));
                count = SYSTEM_LIST.size();
                break;
            case "player": 
                PLAYER_LIST.clear();
                PLAYER_LIST.putAll(SQLManager.get().getMailList(type));
                count = PLAYER_LIST.size();
                break;
            case "permission": 
                PERMISSION_LIST.clear();
                PERMISSION_LIST.putAll(SQLManager.get().getMailList(type));
                count = PERMISSION_LIST.size();
                break;
            case "date": 
                DATE_LIST.clear();
                DATE_LIST.putAll(SQLManager.get().getMailList(type));
                count = DATE_LIST.size();
                break;
            case "times": 
                TIMES_LIST.clear();
                TIMES_LIST.putAll(SQLManager.get().getMailList(type));
                count = TIMES_LIST.size();
                break;
            case "keytimes": 
                KEYTIMES_LIST.clear();
                KEYTIMES_KEY.clear();
                KEYTIMES_LIST.putAll(SQLManager.get().getMailList(type));
                KEYTIMES_LIST.forEach((k,v) -> {
                    String key = ((MailKeyTimes)v).getKey();
                    if(KEYTIMES_KEY.containsKey(key)){
                        KEYTIMES_KEY.get(key).add(k);
                    }else{
                        List<Integer> l = new ArrayList();
                        l.add(k);
                        KEYTIMES_KEY.put(key, l);
                    }
                });
                count = KEYTIMES_LIST.size();
                break;
            case "cdkey": 
                CDKEY_LIST.clear();
                CDKEY_LIST.putAll(SQLManager.get().getMailList(type));
                count = CDKEY_LIST.size();
                break;
            default:
                return;
        }
        String str = Message.commandMailUpdate.replace("%type%", Message.getTypeName(type)).replace("%count%", Integer.toString(count));
        Bukkit.getConsoleSender().sendMessage(str);
        if(p!=null) p.sendMessage(str);
    }
    
    // 根据类型获取Map集合
    public static HashMap<Integer, BaseMail> getMailHashMap(String type){
        switch (type){
            case "system":
                return SYSTEM_LIST;
            case "permission":
                return PERMISSION_LIST;
            case "player":
                return PLAYER_LIST;
            case "date":
                return DATE_LIST;
            case "times":
                return TIMES_LIST;
            case "keytimes":
                return KEYTIMES_LIST;
            case "cdkey":
                return CDKEY_LIST;
            default:
                return null;
        }
    }
    
    // 获取邮件总数
    public static int getMailAllCount(Player p){
        if(p==null){
            return (SYSTEM_LIST.size()+PERMISSION_LIST.size()+PLAYER_LIST.size()+DATE_LIST.size()+TIMES_LIST.size()+CDKEY_LIST.size());
        }else{
            return MailUtil.getTrueType().stream().map((type) -> getRelevantMailList(p, type).get("asRecipient").size()).reduce(Integer::sum).get();
        }
    }
    
    // 获取玩家相关邮件列表
    public static HashMap<String, ArrayList<Integer>> getRelevantMailList(Player p, String type){
        switch (type) {
            case "system" :
                if(!SYSTEM_RELEVANT.containsKey(p.getName())) updateRelevantMailList(p,type);
                return SYSTEM_RELEVANT.get(p.getName());
            case "player" :
                if(!PLAYER_RELEVANT.containsKey(p.getName())) updateRelevantMailList(p,type);
                return PLAYER_RELEVANT.get(p.getName());
            case "permission" :
                if(!PERMISSION_RELEVANT.containsKey(p.getName())) updateRelevantMailList(p,type);
                return PERMISSION_RELEVANT.get(p.getName());
            case "date" :
                if(!DATE_RELEVANT.containsKey(p.getName())) updateRelevantMailList(p,type);
                return DATE_RELEVANT.get(p.getName());
            case "times":
                if(!TIMES_RELEVANT.containsKey(p.getName())) updateRelevantMailList(p,type);
                return TIMES_RELEVANT.get(p.getName());
            case "keytimes":
                if(!KEYTIMES_RELEVANT.containsKey(p.getName())) updateRelevantMailList(p,type);
                return KEYTIMES_RELEVANT.get(p.getName());
            case "cdkey":
                if(!CDKEY_RELEVANT.containsKey(p.getName())) updateRelevantMailList(p,type);
                return CDKEY_RELEVANT.get(p.getName());
            default:
                return null;
        }
    }
    
    // 更新玩家相关邮件列表
    public static void updateRelevantMailList(Player p, String type){
        switch (type) {
            case "system":
                SYSTEM_RELEVANT.remove(p.getName());
                SYSTEM_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, type));
                break;
            case "player":
                PLAYER_RELEVANT.remove(p.getName());
                PLAYER_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, type));
                break;
            case "permission":
                PERMISSION_RELEVANT.remove(p.getName());
                PERMISSION_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, type));
                break;
            case "date":
                DATE_RELEVANT.remove(p.getName());
                DATE_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, type));
                break;
            case "times":
                TIMES_RELEVANT.remove(p.getName());
                TIMES_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, type));
                break;
            case "keytimes":
                KEYTIMES_RELEVANT.remove(p.getName());
                KEYTIMES_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, type));
                break;
            case "cdkey":
                CDKEY_RELEVANT.remove(p.getName());
                CDKEY_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, type));
                break;
            default:
                removeRelevantMailList(p);
                SYSTEM_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, "system"));
                PLAYER_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, "player"));
                PERMISSION_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, "permission"));
                DATE_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, "date"));
                TIMES_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, "times"));
                KEYTIMES_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, "keytimes"));
                CDKEY_RELEVANT.put(p.getName(), MailUtil.getRelevantMail(p, "cdkey"));
        }
    }
    
    // 将玩家移出相关邮件列表
    public static void removeRelevantMailList(Player p){
        SYSTEM_RELEVANT.remove(p.getName());
        PLAYER_RELEVANT.remove(p.getName());
        PERMISSION_RELEVANT.remove(p.getName());
        DATE_RELEVANT.remove(p.getName());
        TIMES_RELEVANT.remove(p.getName());
        KEYTIMES_RELEVANT.remove(p.getName());
        CDKEY_RELEVANT.remove(p.getName());
    }
    
    // 获取此类
    public static MailBox getInstance(){
        return instance;
    }
    
    // 设置OpenCmd
    public void setOpenCmd(boolean enable){
        this.enCmdOpen = enable;
        if(enCmdOpen)Bukkit.getConsoleSender().sendMessage(ConfigMessage.command_box);
    }
    
}
