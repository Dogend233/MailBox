package com.嘤嘤嘤.qwq.MailBox;

import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import static com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI.getCustomMail;
import com.嘤嘤嘤.qwq.MailBox.Events.JoinAndQuit;
import com.嘤嘤嘤.qwq.MailBox.Events.Mail;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
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
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    private static final String DATA_FOLDER = "plugins/VexMailBox";
    private static FileConfiguration config;
    // system 类型邮件
    public static HashMap<Integer, TextMail> MailListSystem = new HashMap();
    public static HashMap<String, HashMap<String, ArrayList<Integer>>> MailListSystemId = new HashMap();
    // player 类型邮件
    public static HashMap<Integer, TextMail> MailListPlayer = new HashMap();
    public static HashMap<String, HashMap<String, ArrayList<Integer>>> MailListPlayerId = new HashMap();
    // permission 类型邮件
    public static HashMap<Integer, TextMail> MailListPermission = new HashMap();
    public static HashMap<String, HashMap<String, ArrayList<Integer>>> MailListPermissionId = new HashMap();
    
    @Override    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (label.equalsIgnoreCase("mailbox")||label.equalsIgnoreCase("mb")){
            if(args.length==0 && enCmdOpen){
                if((sender instanceof Player)){
                    /*Player p = (Player)sender;
                    ItemStack itemStack = p.getInventory().getItemInMainHand();
                    net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
                    net.minecraft.server.v1_12_R1.NBTTagCompound compound = new NBTTagCompound();
                    compound = nmsItemStack.save(compound);
                    String json = compound.toString();
                    BaseComponent[] hoverEventComponents = new BaseComponent[]{
                            new TextComponent(json)
                    };
                    HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);
                    TextComponent component = new TextComponent("附件");
                    component.setHoverEvent(event);
                    p.spigot().sendMessage(component);*/
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
                if(args[0].equalsIgnoreCase("item")){
                    if(args[1].equalsIgnoreCase("export")){
                        if(sender instanceof Player && sender.hasPermission("mailbox.admin.item.export")){
                            ItemStack is = ((Player)sender).getInventory().getItemInMainHand();
                            if(is!=null && MailBoxAPI.saveItem(is)){
                                sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"物品导出成功");
                            }else{
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"导出物品失败");
                            }
                            return true;
                        }
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有导出物品的权限");
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
                                    TextMail tm = getCustomMail(args[2], type);
                                    if(tm==null){
                                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标文件不存在");
                                        return true;
                                    }else{
                                        if(tm.getSender()==null) tm.setSender(p.getName());
                                        if(type.equals("player")) tm.setRecipient(rl);
                                        if(type.equals("permission")) tm.setPermission(rl.get(0));
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
        // 检查前置[Vault]
        GlobalConfig.setVault(setupEconomy());
        if(GlobalConfig.enVault){
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:前置插件[Vault]已安装");
        }else{
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:前置插件[Vault]未安装，已关闭相关功能");
        }
        // 检查前置[PlayerPoints]
        GlobalConfig.setPlayerPoints(setupPoints());
        if(GlobalConfig.enPlayerPoints){
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:前置插件[PlayerPoints]已安装");
        }else{
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:前置插件[PlayerPoints]未安装，已关闭相关功能");
        }
        // 检查前置[VexView]
        if(Bukkit.getPluginManager().isPluginEnabled("VexView")){
            String version = VexViewAPI.getVexView().getVersion();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:前置插件[VexView]已安装，版本："+version);
            // 检查[VexView]版本号
            if(UpdateCheck.check(version, "2.5.0")){
                // 加载插件
                instance = this;
                GlobalConfig.setVexView(true);
                reloadPlugin();
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
            }else{
                Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[VexView]版本小于2.5");
                GlobalConfig.setVexView(false);
                this.enCmdOpen = false;
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }else{
            Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[VexView]未安装，卸载插件");
            GlobalConfig.setVexView(false);
            Bukkit.getPluginManager().disablePlugin(this);
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
            config.getString("mailbox.player_points.display"),
            config.getInt("mailbox.player_points.max")
        );
        // 设置VexViewConfig
        if(GlobalConfig.enVexView) VexViewConfigSet();
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
            case "permission" :
                MailListPermissionId.remove(p.getName());
                MailListPermissionId.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
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
    
}
