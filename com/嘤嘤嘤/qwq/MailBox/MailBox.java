package com.嘤嘤嘤.qwq.MailBox;

import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import com.嘤嘤嘤.qwq.MailBox.API.Placeholder;
import com.嘤嘤嘤.qwq.MailBox.Events.JoinAndQuit;
import com.嘤嘤嘤.qwq.MailBox.Events.MailChange;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import com.嘤嘤嘤.qwq.MailBox.Original.MailList;
import com.嘤嘤嘤.qwq.MailBox.Original.MailNew;
import com.嘤嘤嘤.qwq.MailBox.Original.MailView;
import com.嘤嘤嘤.qwq.MailBox.Utils.NMS;
import com.嘤嘤嘤.qwq.MailBox.Utils.SQLManager;
import com.嘤嘤嘤.qwq.MailBox.Utils.UpdateCheck;
import com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxGui;
import com.嘤嘤嘤.qwq.MailBox.VexView.MailContentGui;
import com.嘤嘤嘤.qwq.MailBox.VexView.VexViewConfig;
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
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

public class MailBox extends JavaPlugin {
    
    private int temp;
    private boolean enCmdOpen;
    private static MailBox instance;
    // 首次启动
    private static boolean FirstEnable = true;
    // config 配置文件
    private static final String DATA_FOLDER = "plugins/MailBox";
    private static FileConfiguration config;
    // system 类型邮件
    public static HashMap<Integer, TextMail> MailListSystem = new HashMap();
    private static HashMap<String, HashMap<String, ArrayList<Integer>>> MailListSystemRelevant = new HashMap();
    // player 类型邮件
    public static HashMap<Integer, TextMail> MailListPlayer = new HashMap();
    private static HashMap<String, HashMap<String, ArrayList<Integer>>> MailListPlayerRelevant = new HashMap();
    // permission 类型邮件
    public static HashMap<Integer, TextMail> MailListPermission = new HashMap();
    private static HashMap<String, HashMap<String, ArrayList<Integer>>> MailListPermissionRelevant = new HashMap();
    
    @Override    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (label.equalsIgnoreCase("mailbox")||label.equalsIgnoreCase("mb")){
            if(args.length==0){
                if(GlobalConfig.enVexView){
                    if((sender instanceof Player)){
                        if(enCmdOpen) MailBoxGui.openMailBoxGui((Player) sender, "Recipient");
                        else MailList.list(sender, "Recipient");
                    }else{
                        MailList.list(sender, "Recipient");
                    }
                }else{
                    MailList.list(sender, "Recipient");
                }
                return true;
            }else if(args.length==1){
                if(args[0].equalsIgnoreCase("sendbox") || args[0].equalsIgnoreCase("sb")){
                    if((sender instanceof Player)){
                        MailList.list(sender, "Sender");
                    }else{
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"只有玩家可以查看发件箱");
                    }
                    return true;
                }else if(args[0].equalsIgnoreCase("receivebox") || args[0].equalsIgnoreCase("rb")){
                    MailList.list(sender, "Recipient");
                    return true;
                }else if(args[0].equalsIgnoreCase("new")){
                    new MailNew(sender,this);
                    return true;
                }else if(args[0].equalsIgnoreCase("reload")){
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
                if(args[0].equalsIgnoreCase("item")){
                    if(args[1].equalsIgnoreCase("export")){
                        if(sender instanceof Player && sender.hasPermission("mailbox.admin.item.export")){
                            ItemStack is = ((Player)sender).getInventory().getItemInMainHand();
                            if(args.length==3){
                                if(is!=null && MailBoxAPI.saveItem(is, args[2])){
                                    sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"物品导出至"+args[2]+".yml成功");
                                }else{
                                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"导出物品失败");
                                }
                            }else{
                                if(is!=null && MailBoxAPI.saveItem(is)){
                                    sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"物品导出成功");
                                }else{
                                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"导出物品失败");
                                }
                            }
                            return true;
                        }
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有导出物品的权限");
                        return true;
                    }else if(args[1].equalsIgnoreCase("import")){
                        if(sender instanceof Player && sender.hasPermission("mailbox.admin.item.import")){
                            if(args.length==3){
                                ItemStack is = MailBoxAPI.readItem(args[2]);
                                if(is!=null){
                                    ((Player)sender).getInventory().setItemInMainHand(is);
                                    sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"取出"+args[2]+".yml物品成功");
                                }else{
                                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"取出物品失败");
                                }
                            }else{
                                ItemStack is = MailBoxAPI.readItem();
                                if(is!=null){
                                    ((Player)sender).getInventory().setItemInMainHand(is);
                                    sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"取出物品成功");
                                }else{
                                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"取出物品失败");
                                }
                            }
                            return true;
                        }
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有取出物品的权限");
                        return true;
                    }else if(args[1].equalsIgnoreCase("id")){
                        if(sender instanceof Player && sender.hasPermission("mailbox.admin.item.id")){
                            ItemStack is = ((Player)sender).getInventory().getItemInMainHand();
                            if(is!=null){
                                sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"物品的Material_ID为: "+GlobalConfig.normal+is.getType().name());
                            }else{
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"获取物品Material_ID失败");
                            }
                            return true;
                        }
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有获取物品ID的权限");
                        return true;
                    }
                }else if(args[0].equalsIgnoreCase("system") || args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("permission")){
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
                        if(args[1].equalsIgnoreCase("see")){
                            int mail = 0;
                            try{
                                mail = Integer.parseInt(args[2]);
                            }catch(NumberFormatException e){
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件格式输入错误，请输入数字ID");
                            }
                            MailView.view(type, mail, sender);
                            return true;
                        }else if(args[1].equalsIgnoreCase("collect")){
                            int mail = 0;
                            try{
                                mail = Integer.parseInt(args[2]);
                            }catch(NumberFormatException e){
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件格式输入错误，请输入数字ID");
                            }
                            MailView.collect(type, mail, sender);
                            return true;
                        }else if(args[1].equalsIgnoreCase("delete")){
                            int mail = 0;
                            try{
                                mail = Integer.parseInt(args[2]);
                            }catch(NumberFormatException e){
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件格式输入错误，请输入数字ID");
                            }
                            MailView.delete(type, mail, sender);
                            return true;
                        }else if(args[1].equalsIgnoreCase("send")){
                            ArrayList<String> rl = new ArrayList();
                            if(type.equals("player") && args.length<4) {
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"请至少填写一个以上的收件人");
                                return true;
                            }else if(type.equals("permission") && args.length!=4) {
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"请填写一个领取邮件所需要的权限");
                                return true;
                            }else{
                                for(int i=3;i<args.length;i++){
                                    rl.add(args[i]);
                                }
                            }
                            if(sender.hasPermission("mailbox.admin.send.custom."+type)){
                                if((sender instanceof Player)){
                                    Player p = (Player) sender;
                                    TextMail tm = MailBoxAPI.getCustomMail(args[2], type);
                                    if(tm==null){
                                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标文件不存在");
                                        return true;
                                    }else{
                                        if(tm.getSender()==null) tm.setSender(p.getName());
                                        if(type.equals("player")) tm.setRecipient(rl);
                                        if(type.equals("permission")) tm.setPermission(rl.get(0));
                                        MailContentGui.openMailContentGui(p, tm, null, false);
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
                        }else if(args[1].equalsIgnoreCase("upload")){
                            if(args[2].equalsIgnoreCase("all") && args.length==3) {
                                if(sender.hasPermission("mailbox.admin.upload."+type+".all")){
                                    MailBoxAPI.uploadFile(sender, type);
                                }else{
                                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                                }
                                return true;
                            }else if(args.length==3) {
                                if(sender.hasPermission("mailbox.admin.download."+type)){
                                    int mail;
                                    try{
                                        mail = Integer.parseInt(args[2]);
                                        TextMail tm = null;
                                        switch (type) {
                                            case "system":
                                                tm=MailListSystem.get(mail);
                                                break;
                                            case "permission":
                                                tm=MailListPermission.get(mail);
                                                break;
                                            case "player":
                                                tm=MailListPlayer.get(mail);
                                                break;
                                        }
                                        if(tm!=null && (tm instanceof FileMail)){
                                            String filename = ((FileMail)tm).getFileName();
                                            if(MailBoxAPI.uploadFile(type, filename)){
                                                sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"附件: "+filename+"上传成功");
                                            }else{
                                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"附件: "+filename+"上传失败");
                                            }
                                        }else{
                                            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标邮件不存在或无附件");
                                        }
                                    }
                                    catch(NumberFormatException e){
                                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件格式输入错误，请输入数字ID");
                                    }
                                }else{
                                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                                }
                                return true;
                            }else{
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"Error");
                                return true;
                            }
                        }else if(args[1].equalsIgnoreCase("download")){
                            if(args[2].equalsIgnoreCase("all") && args.length==3) {
                                if(sender.hasPermission("mailbox.admin.download."+type+".all")){
                                    MailBoxAPI.downloadFile(sender, type);
                                }else{
                                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                                }
                                return true;
                            }else if(args.length==3) {
                                if(sender.hasPermission("mailbox.admin.download."+type)){
                                    int mail;
                                    try{
                                        mail = Integer.parseInt(args[2]);
                                        TextMail tm = null;
                                        switch (type) {
                                            case "system":
                                                tm=MailListSystem.get(mail);
                                                break;
                                            case "permission":
                                                tm=MailListPermission.get(mail);
                                                break;
                                            case "player":
                                                tm=MailListPlayer.get(mail);
                                                break;
                                        }
                                        if(tm!=null && (tm instanceof FileMail)){
                                            String filename = ((FileMail)tm).getFileName();
                                            if(MailBoxAPI.downloadFile(type, filename)){
                                                sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"附件: "+filename+"下载成功");
                                            }else{
                                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"附件: "+filename+"下载失败");
                                            }
                                        }else{
                                            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标邮件不存在或无附件");
                                        }
                                    }
                                    catch(NumberFormatException e){
                                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件格式输入错误，请输入数字ID");
                                    }
                                }else{
                                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                                }
                                return true;
                            }else{
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"Error");
                                return true;
                            }
                        }else{
                            return false;
                        }
                    }else{
                        return false;
                    }
                }else{
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"此邮件类型不存在");
                    return true;
                }
            }else{
                return false;
            }
        }
        return true;
    }
      
    @Override
    public void onEnable(){
        // 插件启动
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:插件正在启动......");
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:版本："+this.getDescription().getVersion());
        // 检查前置[PlaceholderAPI]
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:前置插件[PlaceholderAPI]已安装，版本：".concat(Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion()));
            PlaceholderAPI.unregisterPlaceholderHook("mailbox");
            new Placeholder().register();
        } else {
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:前置插件[PlaceholderAPI]未安装，已关闭相关功能");
        }
        // 检查前置[Vault]
        GlobalConfig.setVault(setupEconomy());
        if(GlobalConfig.enVault){
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:前置插件[Vault]已安装，版本：".concat(Bukkit.getPluginManager().getPlugin("Vault").getDescription().getVersion()));
        }else{
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:前置插件[Vault]未安装，已关闭相关功能");
        }
        // 检查前置[PlayerPoints]
        GlobalConfig.setPlayerPoints(setupPoints());
        if(GlobalConfig.enPlayerPoints){
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:前置插件[PlayerPoints]已安装，版本：".concat(Bukkit.getPluginManager().getPlugin("PlayerPoints").getDescription().getVersion()));
        }else{
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:前置插件[PlayerPoints]未安装，已关闭相关功能");
        }
        // 检查前置[VexView]
        if(Bukkit.getPluginManager().isPluginEnabled("VexView")){
            String version = VexViewAPI.getVexView().getVersion();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:前置插件[VexView]已安装，版本：".concat(version));
            // 检查[VexView]版本号
            if(UpdateCheck.check(version, "2.5.0")){
                GlobalConfig.setVexView(true);
                GlobalConfig.setLowVexView(false);
            }else{
                Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[VexView]版本小于2.5, 已关闭发送邮件GUI, 使用指令代替");
                GlobalConfig.setVexView(true);
                GlobalConfig.setLowVexView(true);
            }
        }else{
            Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[VexView]未安装，已关闭相关功能");
            GlobalConfig.setVexView(false);
            GlobalConfig.setLowVexView(true);
        }
        // 加载插件
        instance = this;
        reloadPlugin();
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:获取NMS版本: "+NMS.getNMSVersion());
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:插件启动完成");
        // 检查更新
        if(config.getBoolean("mailbox.updateCheck")){
            new BukkitRunnable(){
                @Override
                public void run(){
                    UpdateCheck.check(Bukkit.getConsoleSender());
                }
            }.runTaskAsynchronously(this);
        }
    }
    
    // 设置[Vault]
    private boolean setupEconomy() {
        if(getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        return MailBoxAPI.setEconomy(rsp.getProvider());
    }
    
    // 设置[PlayerPoints]
    private boolean setupPoints() {
        Plugin plugin = getServer().getPluginManager().getPlugin("PlayerPoints");
        if(plugin == null) return false;
        return MailBoxAPI.setPoints(PlayerPoints.class.cast(plugin)); 
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
        if(!GlobalConfig.enVexView) {
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:正在注册 加入/退出 事件");
            Bukkit.getPluginManager().registerEvents(new JoinAndQuit(false, false), this);
        }
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:正在注册 邮件 事件");
        Bukkit.getPluginManager().registerEvents(new MailChange(), this);
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
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查permission邮件文件夹是否存在");
        f = new File(DATA_FOLDER+"/MailFiles/"+"permission");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建permission邮件文件夹");
        }
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查custom邮件文件夹是否存在");
        f = new File(DATA_FOLDER+"/MailFiles/"+"custom");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建cutsom邮件文件夹");
        }
        // 连接数据库
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:正在连接数据库");
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
        updateMailList(null, "system");
        updateMailList(null, "player");
        updateMailList(null, "permission");
    }
    
    // 设置Config
    private void setConfig(){
        // 设置GlobalConfig
        GlobalConfig.setGlobalConfig(
            config.getBoolean("database.fileSQL"),
            config.getString("mailbox.prefix"),
            config.getString("mailbox.normalMessage"),
            config.getString("mailbox.successMessage"),
            config.getString("mailbox.warningMessage"),
            config.getString("mailbox.name.system"),
            config.getString("mailbox.name.player"),
            config.getString("mailbox.name.permission"),
            config.getString("mailbox.file.divide"),
            config.getString("mailbox.file.command.player"),
            config.getInt("mailbox.file.maxItem"),
            config.getString("mailbox.file.ban.lore"),
            config.getStringList("mailbox.file.ban.id"),
            config.getString("mailbox.player_maxtime"),
            config.getIntegerList("mailbox.player_max.out"),
            config.getString("mailbox.vault.display"),
            config.getDouble("mailbox.vault.max"),
            config.getDouble("mailbox.vault.expand"),
            config.getDouble("mailbox.vault.item"),
            config.getString("mailbox.player_points.display"),
            config.getInt("mailbox.player_points.max"),
            config.getInt("mailbox.player_points.expand"),
            config.getInt("mailbox.player_points.item")
        );
        // 设置VexViewConfig
        if(GlobalConfig.enVexView) VexViewConfig.VexViewConfigSet();
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
            case "permission": 
                MailListPermission = SQLManager.get().getMailList(type);
                count = MailListPermission.size();
                break;
            default:
                return;
        }
        Bukkit.getConsoleSender().sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+typename+"邮件列表["+count+"封]已更新");
        if(p!=null){
            p.sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+typename+"邮件列表["+count+"封]已更新");
        }
    }
    
    // 获取玩家相关邮件列表
    public static HashMap<String, ArrayList<Integer>> getRelevantMailList(Player p, String type){
        switch (type) {
            case "system" :
                if(!MailListSystemRelevant.containsKey(p.getName())) updateRelevantMailList(p,type);
                return MailListSystemRelevant.get(p.getName());
            case "player" :
                if(!MailListPlayerRelevant.containsKey(p.getName())) updateRelevantMailList(p,type);
                return MailListPlayerRelevant.get(p.getName());
            case "permission" :
                if(!MailListPermissionRelevant.containsKey(p.getName())) updateRelevantMailList(p,type);
                return MailListPermissionRelevant.get(p.getName());
            default:
                return null;
        }
    }
    
    // 更新玩家相关邮件列表
    public static void updateRelevantMailList(Player p, String type){
        switch (type) {
            case "system" :
                MailListSystemRelevant.remove(p.getName());
                MailListSystemRelevant.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                break;
            case "player" :
                MailListPlayerRelevant.remove(p.getName());
                MailListPlayerRelevant.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                break;
            case "permission" :
                MailListPermissionRelevant.remove(p.getName());
                MailListPermissionRelevant.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                break;
            default:
                removeRelevantMailList(p);
                MailListSystemRelevant.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                MailListPlayerRelevant.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                MailListPermissionRelevant.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
        }
    }
    
    // 将玩家移出相关邮件列表
    public static void removeRelevantMailList(Player p){
        MailListSystemRelevant.remove(p.getName());
        MailListPlayerRelevant.remove(p.getName());
        MailListPermissionRelevant.remove(p.getName());
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
    
}
