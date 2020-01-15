package com.tripleying.qwq.MailBox;

import com.tripleying.qwq.MailBox.Mail.*;
import com.tripleying.qwq.MailBox.Utils.*;
import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.Utils.Placeholder;
import com.tripleying.qwq.MailBox.Events.JoinAndQuit;
import com.tripleying.qwq.MailBox.Events.MailChange;
import com.tripleying.qwq.MailBox.Original.MailList;
import com.tripleying.qwq.MailBox.Original.MailNew;
import com.tripleying.qwq.MailBox.Original.MailView;
import com.tripleying.qwq.MailBox.VexView.MailBoxGui;
import com.tripleying.qwq.MailBox.VexView.MailItemListGui;
import com.tripleying.qwq.MailBox.VexView.MailItemModifyGui;
import com.tripleying.qwq.MailBox.VexView.VexViewConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import lk.vexview.api.VexViewAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import static org.bukkit.Material.AIR;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

public class MailBox extends JavaPlugin {
    
    private boolean enCmdOpen;
    private static MailBox instance;
    // config 配置文件
    private static final String DATA_FOLDER = "plugins/MailBox";
    private static FileConfiguration config;
    // system 类型邮件
    public static final HashMap<Integer, BaseMail> SYSTEM_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> SYSTEM_RELEVANT = new HashMap();
    // player 类型邮件
    public static final HashMap<Integer, BaseMail> PLAYER_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> PLAYER_RELEVANT = new HashMap();
    // permission 类型邮件
    public static final HashMap<Integer, BaseMail> PERMISSION_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> PERMISSION_RELEVANT = new HashMap();
    // date 类型邮件
    public static final HashMap<Integer, BaseMail> DATE_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> DATE_RELEVANT = new HashMap();
    // times 类型邮件
    public static final HashMap<Integer, BaseMail> TIMES_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> TIMES_RELEVANT = new HashMap();
    // keytimes 类型邮件
    public static final HashMap<String, List<Integer>> KEYTIMES_KEY = new HashMap();
    public static final HashMap<Integer, BaseMail> KEYTIMES_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> KEYTIMES_RELEVANT = new HashMap();
    // cdkey 类型邮件
    public static final HashMap<Integer, BaseMail> CDKEY_LIST = new HashMap();
    private static final HashMap<String, HashMap<String, ArrayList<Integer>>> CDKEY_RELEVANT = new HashMap();
      
    @Override
    public void onEnable(){
        // 插件启动
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:插件正在启动......");
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:版本："+this.getDescription().getVersion());
        String version = Bukkit.getServer().getVersion();
        version = version.substring(version.indexOf("MC")+3, version.length()-1).trim();
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:服务器版本："+version);
        if(GlobalConfig.lowServer1_12 = !UpdateCheck.check(version.substring(0, version.lastIndexOf('.')), "1.12")){
            Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:服务器版本低于1.12, 进行邮件查看方法调整");
            if(GlobalConfig.lowServer1_11 = !UpdateCheck.check(version.substring(0, version.lastIndexOf('.')), "1.11")){
                Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:服务器版本低于1.11, 进行Title提醒方法调整");
                if(GlobalConfig.lowServer1_9 = !UpdateCheck.check(version.substring(0, version.lastIndexOf('.')), "1.9")){
                    Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:服务器版本低于1.9, 进行获取手上物品方法调整");
                    if(GlobalConfig.lowServer1_8 = !UpdateCheck.check(version.substring(0, version.lastIndexOf('.')), "1.8")){
                        Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:服务器版本低于1.8, 已删除标题提醒方法");
                    }
                }
            }
        }
        // 加载插件
        instance = this;
        loadPlugin();
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

    @Override
    public void onDisable(){
        unloadPlugin();
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:插件已卸载");
    }
    
    // 检查前置插件
    private void checkSoftDepend(){
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查前置插件");
        // [Vault]
        if(config.getBoolean("softDepend.Vault")){
            boolean enable;
            if(getServer().getPluginManager().getPlugin("Vault") == null){
                enable = false;
            }else{
                RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp == null){
                    enable = false;
                }else{
                    enable = MailBoxAPI.setEconomy(rsp.getProvider());
                }
            }
            if(enable){
                Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:前置插件[Vault]已安装，版本：".concat(Bukkit.getPluginManager().getPlugin("Vault").getDescription().getVersion()));
            }else{
                Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[Vault]未安装，已关闭相关功能");
            }
            GlobalConfig.setVault(enable);
        }else{
            Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:未开启[Vault]，已关闭相关功能");
            GlobalConfig.setVault(false);
        }
        // [PlayerPoints]
        if(config.getBoolean("softDepend.PlayerPoints")){
            Plugin plugin = getServer().getPluginManager().getPlugin("PlayerPoints");
            boolean enable;
            if(plugin == null){
                enable = false;
            }else{
                enable = MailBoxAPI.setPoints(PlayerPoints.class.cast(plugin));
            }
            if(enable){
                Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:前置插件[PlayerPoints]已安装，版本：".concat(Bukkit.getPluginManager().getPlugin("PlayerPoints").getDescription().getVersion()));
            }else{
                Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[PlayerPoints]未安装，已关闭相关功能");
            }
            GlobalConfig.setPlayerPoints(enable);
        }else{
            Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:未开启[PlayerPoints]，已关闭相关功能");
            GlobalConfig.setPlayerPoints(false);
        }
        // [PlaceholderAPI]
        if(config.getBoolean("softDepend.PlaceholderAPI")){
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:前置插件[PlaceholderAPI]已安装，版本：".concat(Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion()));
                if(PlaceholderAPI.isRegistered("mailbox")) PlaceholderAPI.unregisterPlaceholderHook("mailbox");
                new Placeholder().register();
            }else{
                Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[PlaceholderAPI]未安装，已关闭相关功能");
            }
        }else{
            Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:未开启[PlaceholderAPI]，已关闭相关功能");
        }
        // [VexView]
        if(config.getBoolean("softDepend.VexView")){
            if(Bukkit.getPluginManager().isPluginEnabled("VexView")){
                String version = VexViewAPI.getVexView().getVersion();
                Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:前置插件[VexView]已安装，版本：".concat(version));
                GlobalConfig.setVexView(true);
                // 检查[VexView]版本号
                if(UpdateCheck.check(version, "2.5.0")){
                    GlobalConfig.setLowVexView_2_5(false);
                    GlobalConfig.setLowVexView_2_4(false);
                }else{
                    Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[VexView]版本小于2.5, 已关闭发送邮件GUI和导出物品列表, 使用指令代替");
                    GlobalConfig.setLowVexView_2_5(true);
                    if(UpdateCheck.check(version, "2.4.0")){
                        GlobalConfig.setLowVexView_2_4(false);
                    }else{
                        Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[VexView]版本小于2.4, 已关闭鼠标悬停文字");
                        GlobalConfig.setLowVexView_2_4(true);
                    }
                }
            }else{
                Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:前置插件[VexView]未安装，已关闭相关功能");
                GlobalConfig.setVexView(false);
                GlobalConfig.setLowVexView_2_5(true);
                GlobalConfig.setLowVexView_2_4(true);
            }
        }else{
            Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:未开启[VexView]，已关闭相关功能");
            GlobalConfig.setVexView(false);
            GlobalConfig.setLowVexView_2_5(true);
            GlobalConfig.setLowVexView_2_4(true);
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
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:正在注销监听器");
        // 注销PlaceholderAPI占位符
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && PlaceholderAPI.isRegistered("mailbox")){
            PlaceholderAPI.unregisterPlaceholderHook("mailbox");
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:正在注销PlaceholderAPI变量");
        }
        // 断开MySQL连接
        try{
            SQLManager.get().shutdown();
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:关闭数据库连接");
        }catch(Exception e){
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:断开数据库连接失败");
            this.getLogger().info(e.getLocalizedMessage());
        }
    }
    
    // 加载插件
    private void loadPlugin(){
        // 加载Data2cn数据文件
        Data2cn.reloadConfig();
        // 获取NMS版本
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:获取NMS版本: "+Reflection.getVersion());
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
        if(!f.exists()){
            Bukkit.getConsoleSender().sendMessage("§c-----[MailBox]:配置文件不存在");
            saveDefaultConfig();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建配置文件");
        }
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:加载配置文件");
        reloadConfig();
        config = getConfig();
        // 检查前置
        checkSoftDepend();
        // 设置
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
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查date邮件文件夹是否存在");
        f = new File(DATA_FOLDER+"/MailFiles/"+"date");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建date邮件文件夹");
        }
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查times邮件文件夹是否存在");
        f = new File(DATA_FOLDER+"/MailFiles/"+"times");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建times邮件文件夹");
        }
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查cdkey邮件文件夹是否存在");
        f = new File(DATA_FOLDER+"/MailFiles/"+"cdkey");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建cdkey邮件文件夹");
        }
        // 模板文件夹
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查Template邮件文件夹是否存在");
        f = new File(DATA_FOLDER+"/Template");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建Template邮件文件夹");
        }
        // 兑换码文件夹
        Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:检查Cdkey文件夹是否存在");
        f = new File(DATA_FOLDER+"/Cdkey");
        if(!f.exists()){
            f.mkdir();
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:创建Cdkey邮件文件夹");
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
        MailBoxAPI.getTrueType().forEach((type) -> {
            updateMailList(null, type);
        });
        Bukkit.getOnlinePlayers().forEach((p) -> {
            MailBox.updateRelevantMailList(p, "all");
        });
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
            config.getStringList("mailbox.newMailTips"),
            config.getString("mailbox.newMailTipsMsg"),
            config.getString("mailbox.name.system"),
            config.getString("mailbox.name.player"),
            config.getString("mailbox.name.permission"),
            config.getString("mailbox.name.date"),
            config.getString("mailbox.name.times"),
            config.getString("mailbox.name.keytimes"),
            config.getString("mailbox.name.cdkey"),
            config.getString("mailbox.name.online"),
            config.getString("mailbox.name.template"),
            config.getString("mailbox.file.command.player"),
            config.getInt("mailbox.file.maxItem"),
            config.getString("mailbox.file.ban.lore"),
            config.getStringList("mailbox.file.ban.id"),
            config.getString("mailbox.player_maxtime"),
            config.getIntegerList("mailbox.player_max.out"),
            config.getInt("mailbox.times_count"),
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
    
    @Override    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (label.equals("mailbox") || label.equals("mb")){
            if(args.length==0){
                if(GlobalConfig.enVexView){
                    if(sender instanceof Player && MailBoxAPI.hasPlayerPermission(sender, "mailbox.gui.mailbox")){
                        if(enCmdOpen) MailBoxGui.openMailBoxGui((Player)sender, "Recipient");
                        else MailList.list(sender, "Recipient");
                    }else{
                        MailList.list(sender, "Recipient");
                    }
                }else{
                    MailList.list(sender, "Recipient");
                }
            }else if(args.length==1){
                if(args[0].equals("help")){
                    sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"指令帮助");
                    sender.sendMessage("§b/mb §e打开邮箱");
                    sender.sendMessage("§b/mb rb §e查看收件箱");
                    sender.sendMessage("§b/mb sb §e查看发件箱");
                    sender.sendMessage("§b/mb new §e写邮件");
                    sender.sendMessage("§b/mb cdk [兑换码] §e使用兑换码");
                    sender.sendMessage("§b/mb [邮件类型] see [邮件ID] §e查看一封邮件");
                    sender.sendMessage("§b/mb [邮件类型] collect [邮件ID] §e领取一封邮件");
                    sender.sendMessage("§b/mb [邮件类型] delete [邮件ID] §e删除一封邮件");
                    if(sender.hasPermission("mailbox.admin.item")){
                        if(GlobalConfig.enVexView) sender.sendMessage("§b/mb item §e打开物品编辑GUI（仅VV）");
                        sender.sendMessage("§b/mb item id §e查看手上物品的Material_ID");
                        sender.sendMessage("§b/mb item name [名称] §e为手上物品重命名");
                        sender.sendMessage("§b/mb item lore add [描述] §e为手上物品添加一行Lore");
                        sender.sendMessage("§b/mb item lore remove [行数] §e为手上物品移除指定行Lore");
                        sender.sendMessage("§b/mb item list §e查看已导出的物品文件名列表");
                        sender.sendMessage("§b/mb item give [在线玩家] §e将itemstack.yml中的物品给予指定玩家");
                        sender.sendMessage("§b/mb item give [在线玩家] [文件名] §e将ItemExport文件夹下的[文件名].yml中的物品给予指定玩家");
                        sender.sendMessage("§b/mb item gived [在线玩家] §e将itemstack.yml中的物品强制给予指定玩家，不会判断背包是否有空位");
                        sender.sendMessage("§b/mb item gived [在线玩家] [文件名] §e将ItemExport文件夹下的[文件名].yml中的物品强制给予指定玩家，不会判断背包是否有空位");
                        sender.sendMessage("§b/mb item export §e导出手上的物品至itemstack.yml");
                        sender.sendMessage("§b/mb item export [文件名] §e将手上物品导出至ItemExport文件夹下的[文件名].yml");
                        sender.sendMessage("§b/mb item import §e将itemstack.yml中的物品取出到手上");
                        sender.sendMessage("§b/mb item import [文件名] §e将ItemExport文件夹下的[文件名].yml中的物品取出到手上");
                    }
                    for(String t:MailBoxAPI.getTrueType()){
                        if(sender.hasPermission("mailbox.admin.update."+t)){
                            sender.sendMessage("§b/mb [邮件类型] update §e更新目标类型邮件列表");
                            break;
                        }
                    }
                    if(sender.hasPermission("mailbox.admin.upload")){
                        sender.sendMessage("§b/mb [邮件类型] upload [邮件ID] §e将目标邮件的本地附件上传到数据库");
                        sender.sendMessage("§b/mb [邮件类型] upload all §e将目标邮件类型的全部本地附件上传到数据库");
                    }
                    if(sender.hasPermission("mailbox.admin.download")){
                        sender.sendMessage("§b/mb [邮件类型] download [邮件ID] §e将目标邮件的数据库附件下载到本地");
                        sender.sendMessage("§b/mb [邮件类型] download all §e将目标邮件类型的全部数据库附件下载到本地");
                    }
                    if(sender.hasPermission("mailbox.admin.clean.player")) sender.sendMessage("§b/mb player clean §e手动清理player类型过期邮件");
                    if(sender.hasPermission("mailbox.admin.create.cdkey")){
                        sender.sendMessage("§b/mb cdkey create [邮件ID] §e为一封cdkey邮件生成兑换码");
                        sender.sendMessage("§b/mb cdkey create [邮件ID] [兑换码数量] §e为一封cdkey邮件生成一定数量的兑换码");
                    }
                    if(sender.hasPermission("mailbox.admin.export.cdkey")){
                        sender.sendMessage("§b/mb cdkey export [邮件ID] §e导出一封cdkey邮件的兑换码");
                        sender.sendMessage("§b/mb cdkey export all §e导出所以cdkey邮件的兑换码");
                    }
                    if(sender.hasPermission("mailbox.admin.template")){
                        sender.sendMessage("§b/mb template [模板名] §e读取一个邮件模板进入类型选择");
                        sender.sendMessage("§b/mb template [模板名] [邮件类型] §e读取一个邮件模板以[邮件类型]类型进入预览/参数设置");
                        sender.sendMessage("§b/mb template [模板名] template [目标文件名] §e复制一个邮件模板进入预览，并写入目标文件名");
                        sender.sendMessage("§b/mb template [模板名] times [邮件数量] §e读取一个邮件模板以times类型进入预览，并写入邮件数量");
                        sender.sendMessage("§b/mb template [模板名] keytimes [邮件数量] [邮件口令] §e读取一个邮件模板以keytimes类型进入预览，并写入邮件数量和口令");
                        sender.sendMessage("§b/mb template [模板名] cdkey [兑换码唯一性] §e读取一个邮件模板以cdkey类型进入预览，并设置兑换码唯一性");
                        sender.sendMessage("§b/mb template [模板名] permission [所需权限] §e读取一个邮件模板以permission类型进入预览，并写入所需权限");
                        sender.sendMessage("§b/mb template [模板名] date [开始时间] [截止时间] §e读取一个邮件模板以date类型进入预览，并写入开始时间和截止时间");
                        sender.sendMessage("§b/mb template [模板名] player [收件人1] <收件人2> <收件人3> ... §e读取一个邮件模板以player类型进入预览，并写入收件人");
                    }
                    if(sender.hasPermission("mailbox.admin.template.send")){
                        sender.sendMessage("§b/mb send [模板名] system §e读取一个邮件模板，为system类型，不进入预览直接发送");
                        sender.sendMessage("§b/mb send [模板名] online §e读取一个邮件模板，为online类型，不进入预览直接发送");
                        sender.sendMessage("§b/mb send [模板名] template [目标文件名] §e复制一个邮件模板，不进入预览直接保存为目标文件名");
                        sender.sendMessage("§b/mb send [模板名] times [邮件数量] §e读取一个邮件模板，写入times类型，并写入邮件数量，不进入预览直接发送");
                        sender.sendMessage("§b/mb send [模板名] keytimes [邮件数量] [邮件口令] §e读取一个邮件模板，写入keytimes类型，并写入邮件数量和口令，不进入预览直接发送");
                        sender.sendMessage("§b/mb send [模板名] cdkey [兑换码唯一性] §e读取一个邮件模板，写入cdkey类型，并设置兑换码唯一性，不进入预览直接发送");
                        sender.sendMessage("§b/mb send [模板名] permission [所需权限] §e读取一个邮件模板，写入permission类型，并写入所需权限，不进入预览直接发送");
                        sender.sendMessage("§b/mb send [模板名] date [开始时间] [截止时间] §e读取一个邮件模板，写入player类型，并写入收件人，不进入预览直接发送");
                        sender.sendMessage("§b/mb send [模板名] player [收件人1] <收件人2> <收件人3> ... §e读取一个邮件模板，为date类型并写入开始时间和截止时间，不进入预览直接发送");
                    }
                    if(sender.hasPermission("mailbox.admin.check")){
                        sender.sendMessage("§b/mb check §e检查更新");
                    }
                    if(sender.hasPermission("mailbox.admin.reload")){
                        sender.sendMessage("§b/mb reload §e重载插件");
                    }
                }else if(args[0].equals("item") && GlobalConfig.enVexView){
                    if(sender instanceof Player){
                        MailItemModifyGui.openItemModifyGui((Player)sender);
                    }else{
                        Bukkit.getServer().dispatchCommand(sender, "mb item list");
                    }
                }else{
                    onCommandNormal(sender, args[0]);
                }
            }else if(args.length>=2){
                switch (args[0]) {
                    case "cdk":
                        if(sender instanceof Player && MailBoxAPI.hasPlayerPermission(sender, "mailbox.cdkey.use")) MailBoxAPI.exchangeCdkey(((Player)sender), args[1]);
                        else sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有执行此指令的权限");
                        break;
                    case "item":
                        if(sender.hasPermission("mailbox.admin.item")) onCommandItem(sender,args);
                        else sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有执行此指令的权限");
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
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"未知的指令");
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
                    if(args[0].startsWith("cd") && MailBoxAPI.hasPlayerPermission(sender, "mailbox.cdkey.use")) return Arrays.asList("cdk");
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
                                return Arrays.asList("export","give","gived","id","import","list","lore","name");
                            }
                            if(args[1].length()>1){
                                if(args[1].equals("gived")) return Arrays.asList("give");
                                if(args[1].equals("give")) return Arrays.asList("gived");
                                if(args[1].startsWith("id")) return Arrays.asList("id");
                                if(args[1].startsWith("im")) return Arrays.asList("import");
                                if(args[1].startsWith("li")) return Arrays.asList("list");
                                if(args[1].startsWith("lo")) return Arrays.asList("lore");
                            }
                            switch (args[1].substring(0,1)){
                                case "e":
                                    return Arrays.asList("export");
                                case "g":
                                    return Arrays.asList("give","gived");
                                case "i":
                                    return Arrays.asList("id","import");
                                case "l":
                                    return Arrays.asList("list","lore");
                                case "n":
                                    return Arrays.asList("name");
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
                    case "item":
                        if(args[1].equals("lore")){
                            if(args[2].length()==0) return Arrays.asList("add","remove");
                            switch (args[2].substring(0,1)){
                                case "a":
                                    return Arrays.asList("add");
                                case "r":
                                    return Arrays.asList("remove");
                            }   break;
                        }   break;
                }
        }
        return null;
    }
    
    private void onCommandNormal(CommandSender sender, String arg){
        if(sender==null) return;
        switch (arg) {
            case "sendbox":
            case "sb":
                if((sender instanceof Player)){
                    MailList.list(sender, "Sender");
                }else{
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"只有玩家可以查看发件箱");
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
                if(sender instanceof Player && MailBoxAPI.hasPlayerPermission(sender, "mailbox.onekeycollect")){
                    Player p = (Player)sender;
                    for(String type:MailBoxAPI.getTrueTypeWhithoutSpecial()){
                        if(!MailBoxAPI.hasPlayerPermission(p, "mailbox.see."+type)) continue;
                        MailBox.updateRelevantMailList(p, type);
                        HashMap<Integer, BaseMail> mhm = MailBox.getMailHashMap(type);
                        MailBox.getRelevantMailList(p, type).get("asRecipient").forEach(id -> {
                            if(mhm.get(id) instanceof BaseFileMail) mhm.get(id).Collect(p);
                        });
                    }
                }else{
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                }   break;
            case "reload":
                if(sender.hasPermission("mailbox.admin.reload")){
                    reloadPlugin();
                    sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"插件已重载");
                }else{
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                }
                break;
            case "check":
                if(sender.hasPermission("mailbox.admin.check")){
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            UpdateCheck.check(sender);
                        }
                    }.runTaskAsynchronously(this);
                }else{
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                }
                break;
            default:
                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"未知的指令");
        }
    }
    
    private void onCommandItem(CommandSender sender, String[] args){
        if(sender==null) return;
        ItemStack is;
        switch (args[1]) {
            case "list":
                List<String> list = MailBoxAPI.getItemExport();
                if(list.isEmpty()){
                    sender.sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+"没有已导出的物品");
                }else{
                    if(GlobalConfig.lowVexView_2_5 || !(sender instanceof Player)){
                        int i = 0;
                        for(String name:MailBoxAPI.getItemExport()){
                            sender.sendMessage("§b"+(++i)+". §e"+name);
                        }
                    }else{
                        MailItemListGui.openItemListGui((Player)sender, list);
                    }
                }   break;
            case "export":
                if(sender instanceof Player){
                    if(GlobalConfig.lowServer1_9) is = ((Player)sender).getInventory().getItemInHand();
                    else is = ((Player)sender).getInventory().getItemInMainHand();
                    if(args.length==3){
                        if(!is.getType().equals(AIR) && MailBoxAPI.saveItem(is, args[2])){
                            sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"物品导出至"+args[2]+".yml成功");
                        }else{
                            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"导出物品失败");
                        }
                    }else{
                        if(!is.getType().equals(AIR) && MailBoxAPI.saveItem(is)){
                            sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"物品导出成功");
                        }else{
                            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"导出物品失败");
                        }
                    }
                }else{
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"只有玩家可以执行此指令");
                }   break;
            case "import":
                if(args.length==3){
                    is = MailBoxAPI.readItem(args[2]);
                }else{
                    is = MailBoxAPI.readItem();
                }
                if(is==null){
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"读取物品失败");
                }else{
                    if(sender instanceof Player){
                        if(GlobalConfig.lowServer1_9) ((Player)sender).getInventory().setItemInHand(is);
                        else ((Player)sender).getInventory().setItemInMainHand(is);
                    }else{
                        sender.sendMessage("物品："+MailBoxAPI.getItemName(is)+'\n'+"§a"+Reflection.Item2Json(is).replace(',', '\n'));
                    }
                    sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"已取出物品");
                }   break;
            case "give":
            case "gived":
                if(args.length<3){
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"未知的指令");
                    break;
                }
                Player p = Bukkit.getPlayer(args[2]);
                if(p==null){
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"玩家不在线");
                    break;
                }
                if(args.length==4){
                    is = MailBoxAPI.readItem(args[3]);
                }else{
                    is = MailBoxAPI.readItem();
                }
                if(is==null){
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"读取物品失败");
                }else{
                    if(args[1].equals("give") && p.getInventory().firstEmpty()==-1){
                        HashMap<Integer, ? extends ItemStack> im = p.getInventory().all(is.getType());
                        if(!im.isEmpty()){
                            Set<Integer> ks = im.keySet();
                            for(Integer k:ks){
                                ItemStack iss = im.get(k);
                                if(iss.isSimilar(is) && iss.getAmount()+is.getAmount()<=iss.getMaxStackSize()){
                                    p.getInventory().addItem(is);
                                    sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"已给予物品");
                                    return;
                                }
                            }
                        }
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标玩家背包空间不足");
                        break;
                    }else{
                        p.getInventory().addItem(is);
                        sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"已给予物品");
                    }
                }   break;
            case "lore":
                if(args.length!=4 || (!args[2].equals("add") && !args[2].equals("remove"))){
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"未知的指令");
                    return;
                }
                if (sender instanceof Player) {
                    if(GlobalConfig.lowServer1_9) is = ((Player)sender).getInventory().getItemInHand();
                    else is = ((Player)sender).getInventory().getItemInMainHand();
                    if(is.getType().equals(AIR)){
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"获取物品失败");
                        return;
                    }
                    ItemMeta im = is.getItemMeta();
                    List<String> lores = new ArrayList();
                    String lore = args[3].replace('&','§');
                    int line;
                    switch (args[2]) {
                        case "remove":
                            try {
                                line = Integer.parseInt(lore);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"Lore行数输入错误，请输入数字");
                                return;
                            }
                            if(im.hasLore()){
                                lores = im.getLore();
                                if(line>lores.size()){
                                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"要删除的行数超出该物品Lore的总行数");
                                    return;
                                }else{
                                    lores.remove(line-1);
                                    break;
                                }
                            }else{
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"该物品没有Lore");
                                return;
                            }
                        case "add":
                            if(im.hasLore()){
                                lores = im.getLore();
                            }
                            lores.add(lore);
                            break;
                    }
                    im.setLore(lores);
                    is.setItemMeta(im);
                    sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"Lore已修改");
                } else {
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"只有玩家可以执行此指令");
                }
                break;
            case "name":
                if(args.length!=3){
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"未知的指令");
                    return;
                }
                if(sender instanceof Player){
                    if(GlobalConfig.lowServer1_9) is = ((Player)sender).getInventory().getItemInHand();
                    else is = ((Player)sender).getInventory().getItemInMainHand();
                    if(is.getType().equals(AIR)){
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"获取物品失败");
                        return;
                    }
                    ItemMeta im = is.getItemMeta();
                    im.setDisplayName(args[2].replace('&','§'));
                    is.setItemMeta(im);
                    sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"物品已重命名");
                }else{
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"只有玩家可以执行此指令");
                }   break;
            case "id":
                if(sender instanceof Player){
                    if(GlobalConfig.lowServer1_9) is = ((Player)sender).getInventory().getItemInHand();
                    else is = ((Player)sender).getInventory().getItemInMainHand();
                    if(is.getType().equals(AIR)){
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"获取物品失败");
                    }else{
                        sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"物品的Material_ID为: "+GlobalConfig.normal+is.getType().name());
                    }
                }else{
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"只有玩家可以执行此指令");
                }   break;
            default:
                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"未知的指令");
        }
    }
    
    private void onCommandTemplate(CommandSender sender, String[] args){
        if(sender.hasPermission("mailbox.admin.template")){
            BaseMail bm = MailBoxAPI.loadTemplateMail(args[1]);
            if(bm==null){
                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标文件不存在");
                return;
            }
            if(args.length==2 && args[0].equals("template")){
                MailNew.New(sender, bm);
            }else if(args.length>=3){
                if(!MailBoxAPI.getAllType().contains(args[2])){
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件类型不存在");
                    return;
                }
                if(!sender.hasPermission("mailbox.admin.send."+args[2])){
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有发送此类型邮件的权限");
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
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"兑换码唯一性格式错误，请输入true或false");
                                return;
                            }
                            ((MailCdkey)bm).setOnly(only);
                            break;
                        case "keytimes":
                            if(args.length==5){
                                ((MailKeyTimes)bm).setKey(args[4].replace('&', '§'));
                            }else{
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"请输入足够的参数: 邮件数量 和 邮件口令");
                                return;
                            }
                        case "times":
                            int times;
                            try{
                                times = Integer.parseInt(args[3]);
                            }catch(NumberFormatException e){
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件数量格式错误，请输入数字");
                                return;
                            }
                            if(times<1) {
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件数量不能小于1");
                                return;
                            }
                            if(times>GlobalConfig.times_count && !sender.hasPermission("mailbox.admin.send.check.times")){
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件数量不能大于"+GlobalConfig.times_count);
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
                                    List<Integer> t = DateTime.toDate(args[3], sender, null);
                                    switch (t.size()) {
                                        case 3:
                                        case 6:
                                            String date = DateTime.toDate(t, sender, null);
                                            if(date==null){
                                                return;
                                            }else{
                                                bm.setDate(date);
                                            }
                                            break;
                                        default:
                                            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"请输入足够的时间参数，年-月-日 或 年-月-日-时-分-秒");
                                        return;
                                    }
                                }
                                if(args[4].equals("0")){
                                    ((MailDate)bm).setDeadline("0");
                                }else{
                                    List<Integer> t = DateTime.toDate(args[3], sender, null);
                                    switch (t.size()) {
                                        case 3:
                                        case 6:
                                            String date = DateTime.toDate(t, sender, null);
                                            if(date==null){
                                                return;
                                            }else{
                                                ((MailDate)bm).setDeadline(date);
                                            }
                                            break;
                                        default:
                                            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"请输入足够的时间参数，年-月-日 或 年-月-日-时-分-秒");
                                        return;
                                    }
                                }
                            }else{
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"请输入足够的参数: 开始时间 和 截止时间");
                                return;
                            }
                            break;
                        case "online":
                        case "system":
                            break;
                        default:
                            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"此邮件类型不支持参数发送");
                            return;
                    }
                    if(args[0].equals("template")){
                        MailNew.New(sender, bm);
                    }else if(sender.hasPermission("mailbox.admin.template.send")){
                        if(bm.getSender()==null){
                            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"此邮件未设置发件人，无法直接发送");
                        }else{
                            bm.Send(sender, null);
                        }
                    }else{
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                    }
                }
            }else{
                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"未知的指令");
            }
        }else{
            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
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
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                }
            }else if(args[1].equals("clean") && (type.equals("player") || type.equals("date"))){
                if(sender.hasPermission("mailbox.admin.clean."+type)){
                    StringBuilder t = new StringBuilder("");
                    if((type.equals("player"))) PLAYER_LIST.forEach((Integer k, BaseMail v) -> { if(v.ExpireValidate()) if(v.Delete(null)) t.append("1"); });
                    if((type.equals("date"))) DATE_LIST.forEach((Integer k, BaseMail v) -> { if(v.ExpireValidate()) if(v.Delete(null)) t.append("1"); });
                    sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"已清理"+GlobalConfig.getTypeName(type)+"邮件"+t.length()+"封");
                }else{
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                }
            }else{
            }
        }else{
            switch (args[1]) {
                case "see":
                    try{
                        MailView.view(type, Integer.parseInt(args[2]), sender);
                    }catch(NumberFormatException e){
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件格式输入错误，请输入数字ID");
                    }
                    break;
                case "collect":
                    try{
                        MailView.collect(type, Integer.parseInt(args[2]), sender);
                    }catch(NumberFormatException e){
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件格式输入错误，请输入数字ID");
                    } 
                    break;
                case "delete":
                    try{
                        MailView.delete(type, Integer.parseInt(args[2]), sender);
                    }catch(NumberFormatException e){
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件格式输入错误，请输入数字ID");
                    }
                    break;
                case "create":
                    if(!type.equals("cdkey") || !sender.hasPermission("mailbox.admin.createt.cdkey")){
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"未知的指令");
                        break;
                    }
                    updateMailList(null, "cdkey");
                    MailCdkey mc;
                    try{
                        int mail = Integer.parseInt(args[2]);
                        mc = (MailCdkey)MailBox.getMailHashMap(type).get(mail);
                    }
                    catch(NumberFormatException e){
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件格式输入错误，请输入数字ID");
                        break;
                    }
                    if(mc==null){
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标邮件不存在");
                        break;
                    }
                    if(args.length==3 || mc.isOnly()){
                        sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"生成"+mc.generateCdkey(1)+"个兑换码成功");
                    }else{
                        try{
                            int count = Integer.parseInt(args[3]);
                            sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"生成"+mc.generateCdkey(count)+"个兑换码成功");
                        }
                        catch(NumberFormatException e){
                            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"兑换码数量输入错误，请输入数字");
                        }
                    }   break;
                case "export":
                    if(!type.equals("cdkey") || !sender.hasPermission("mailbox.admin.export.cdkey")){
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"未知的指令");
                        break;
                    }
                    if(args[2].equals("all")){
                        updateMailList(null, "cdkey");
                        getMailHashMap("cdkey").forEach((k,v) -> {
                            if(MailBoxAPI.exportCdkey(k)){
                                sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"兑换码: §6"+k+" 导出成功");
                            }else{
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"兑换码: §6"+k+" 导出失败");
                            }
                        });
                    }else{
                        try{
                            int mail = Integer.parseInt(args[2]);
                            if(MailBox.getMailHashMap(type).containsKey(mail)){
                                if(MailBoxAPI.exportCdkey(mail)){
                                    sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"兑换码导出成功");
                                }else{
                                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"兑换码导出失败");
                                }
                            }else{
                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标邮件不存在");
                            }
                        }
                        catch(NumberFormatException e){
                            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件格式输入错误，请输入数字ID");
                        }
                    }   break;
                case "upload":
                case "download":
                    if(args.length==3) {
                        String load = args[1];
                        if(sender.hasPermission("mailbox.admin."+load)){
                            if(args[2].equals("all")){
                                if(load.equals("upload")) MailBoxAPI.uploadFile(sender, type);
                                else MailBoxAPI.downloadFile(sender, type);
                            }else{
                                int mail;
                                try{
                                    mail = Integer.parseInt(args[2]);
                                    BaseMail bm = MailBox.getMailHashMap(type).get(mail);
                                    if(bm!=null && (bm instanceof BaseFileMail)){
                                        String filename = ((BaseFileMail)bm).getFileName();
                                        if(args[1].equals("upload")){
                                            if(MailBoxAPI.uploadFile(type, filename)){
                                                sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"附件: "+filename+"上传成功");
                                            }else{
                                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"附件: "+filename+"上传失败");
                                            }
                                        }else{
                                            if(MailBoxAPI.downloadFile(type, filename)){
                                                sender.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"附件: "+filename+"下载成功");
                                            }else{
                                                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"附件: "+filename+"下载失败");
                                            }
                                        }
                                    }else{
                                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标邮件不存在或无附件");
                                    }
                                }
                                catch(NumberFormatException e){
                                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件格式输入错误，请输入数字ID");
                                }
                            }
                        }else{
                            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限执行此指令");
                        }
                    }else{
                        sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"未知的指令");
                    }   break;
                default:
                    sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"未知的指令");
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
        Bukkit.getConsoleSender().sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+GlobalConfig.getTypeName(type)+"邮件列表["+count+"封]已更新");
        if(p!=null){
            p.sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+GlobalConfig.getTypeName(type)+"邮件列表["+count+"封]已更新");
        }
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
            return MailBoxAPI.getTrueType().stream().map((type) -> getRelevantMailList(p, type).get("asRecipient").size()).reduce(Integer::sum).get();
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
                SYSTEM_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                break;
            case "player":
                PLAYER_RELEVANT.remove(p.getName());
                PLAYER_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                break;
            case "permission":
                PERMISSION_RELEVANT.remove(p.getName());
                PERMISSION_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                break;
            case "date":
                DATE_RELEVANT.remove(p.getName());
                DATE_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                break;
            case "times":
                TIMES_RELEVANT.remove(p.getName());
                TIMES_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                break;
            case "keytimes":
                KEYTIMES_RELEVANT.remove(p.getName());
                KEYTIMES_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                break;
            case "cdkey":
                CDKEY_RELEVANT.remove(p.getName());
                CDKEY_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, type));
                break;
            default:
                removeRelevantMailList(p);
                SYSTEM_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, "system"));
                PLAYER_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, "player"));
                PERMISSION_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, "permission"));
                DATE_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, "date"));
                TIMES_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, "times"));
                KEYTIMES_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, "keytimes"));
                CDKEY_RELEVANT.put(p.getName(), MailBoxAPI.getRelevantMail(p, "cdkey"));
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
        if(enCmdOpen)Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:已启用指令打开邮箱GUI");
    }
    
}
