package com.tripleying.dogend.mailbox.util;

import com.google.gson.JsonObject;
import com.tripleying.dogend.mailbox.MailBox;
import com.tripleying.dogend.mailbox.api.util.Version;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 更新工具
 * @author Dogend
 */
public class UpdateUtil {
    
    /**
     * 更新插件
     * @param sender 指令发送者
     * @param download 是否下载新版本
     */
    public static void updatePlugin(CommandSender sender, boolean download){
        new BukkitRunnable(){
            @Override
            public void run(){
                try{
                    List<String> version = HTTPUtil.getLineList(MailBox.getMailBox().getDescription().getWebsite()+"/version");
                    if(version.isEmpty()){
                        MessageUtil.error(sender, "获取版本信息失败");
                    }else{
                        Version now = new Version(MailBox.getMailBox().getDescription().getVersion());
                        if(now.checkNewest(version.get(0))){
                            MessageUtil.log(sender, "插件已是最新版本");
                        }else{
                            MessageUtil.log(sender, "检测到最新版本, 正在获取更新信息");
                            JsonObject jo = HTTPUtil.getJson(MailBox.getMailBox().getDescription().getWebsite()+"/version?info");
                            MessageUtil.log(sender, "检测到新版本: ".concat(jo.get("version").getAsString()));
                            MessageUtil.log(sender, "更新时间: ".concat(jo.get("time").getAsString()));
                            MessageUtil.log(sender, "更新内容:");
                            jo.getAsJsonArray("info").forEach(i -> MessageUtil.log("-".concat(i.getAsString())));
                            if(download){
                                MessageUtil.log(sender, "准备更新......");
                                File newfile = new File("Plugins/[邮箱]-MailBox-v"+version.get(0)+".jar");
                                MessageUtil.log(sender, "准备下载新文件......");
                                HTTPUtil.downloadFile(MailBox.getMailBox().getDescription().getWebsite()+"/files/download.php", newfile);
                                MessageUtil.log(sender, "下载完成, 准备卸载旧插件并删除, 然后加载新插件");
                                File oldfile = MailBox.getMailBox().getFile();
                                unloadPlugin("MailBox");
                                oldfile.delete();
                                loadPlugin(newfile);
                            }
                        }
                    }
                } catch (Exception ex) {
                    MessageUtil.error(sender, "更新失败");
                    ex.printStackTrace();
                }
            }
        }.runTask(MailBox.getMailBox());
    }

    /**
     * 加载插件
     * (此代码来自CatServer)
     * @param file 插件文件
     */
    public static void loadPlugin(File file){
        PluginManager manager = Bukkit.getServer().getPluginManager();
        try{
            Plugin plugin = manager.loadPlugin(file);
            plugin.onLoad();
            manager.enablePlugin(plugin);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
    /**
     * 卸载插件
     * (此代码来自CatServer)
     * @param pluginName 插件名
     */
    public static void unloadPlugin(String pluginName){
        SimplePluginManager manager = (SimplePluginManager)Bukkit.getServer().getPluginManager();
        try{
            List plugins = (List)ReflectUtil.getPrivateValue(SimplePluginManager.class, manager, "plugins");
            Map lookupNames = (Map)ReflectUtil.getPrivateValue(SimplePluginManager.class, manager, "lookupNames");
            SimpleCommandMap commandMap = (SimpleCommandMap)ReflectUtil.getPrivateValue(SimplePluginManager.class, manager, "commandMap");
            Map knownCommands = (Map)ReflectUtil.getPrivateValue(SimpleCommandMap.class, commandMap, "knownCommands");
            for (Plugin plugin : manager.getPlugins()) {
                if (!plugin.getDescription().getName().equalsIgnoreCase(pluginName)) continue;
                manager.disablePlugin(plugin);
                plugins.remove(plugin);
                lookupNames.remove(pluginName);
                Iterator<Map.Entry> it = knownCommands.entrySet().iterator();
                while (it.hasNext()) {
                    PluginCommand command;
                    Map.Entry entry = it.next();
                    if (!(entry.getValue() instanceof PluginCommand) || (command = (PluginCommand)entry.getValue()).getPlugin() != plugin) continue;
                    command.unregister(commandMap);
                    it.remove();
                }
                return;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        
    }
    
}
