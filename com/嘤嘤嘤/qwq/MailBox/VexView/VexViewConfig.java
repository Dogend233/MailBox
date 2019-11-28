package com.嘤嘤嘤.qwq.MailBox.VexView;

import com.嘤嘤嘤.qwq.MailBox.Events.DoubleKeyPress;
import com.嘤嘤嘤.qwq.MailBox.Events.JoinAndQuit;
import com.嘤嘤嘤.qwq.MailBox.Events.SingleKeyPress;
import com.嘤嘤嘤.qwq.MailBox.MailBox;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.getInstance;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxGui.setBoxConfig;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxHud.setHudConfig;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailContentGui.setContentConfig;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailSelectGui.setSelectConfig;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailSendGui.setSendConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getLogger;
import org.bukkit.configuration.file.YamlConfiguration;

public class VexViewConfig {
        
    public static final String DATA_FOLDER = "plugins/VexMailBox/VexView";
    
    // 配置VexView
    public static void VexViewConfigSet(){
        Bukkit.getConsoleSender().sendMessage("§6-----正在配置VexView");
        VexViewConfig config = new VexViewConfig();
        config.ConfigExist();
        config.ConfigLoad();
    }
    
    public void ConfigSet(YamlConfiguration hud, YamlConfiguration box, YamlConfiguration content, YamlConfiguration select, YamlConfiguration send){
        MailBox mb = getInstance();
        // 配置Hud
        if(hud.getBoolean("hud.enable")){
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:正在注册 加入/退出 事件");
            Bukkit.getPluginManager().registerEvents(new JoinAndQuit(true, true), mb);
            setHudConfig(
                hud.getString("hud.id"),
                hud.getString("hud.img"),
                hud.getInt("hud.x"),
                hud.getInt("hud.y"),
                hud.getInt("hud.w"),
                hud.getInt("hud.h"),
                hud.getInt("hud.ww"),
                hud.getInt("hud.hh")
            );
        }else{
            Bukkit.getConsoleSender().sendMessage("§6-----[MailBox]:正在注册 加入/退出 事件");
            Bukkit.getPluginManager().registerEvents(new JoinAndQuit(true, false), mb);
        }
        // 配置BoxGui
        getInstance().setOpenCmd(box.getBoolean("gui.openCmd"));
        String key = box.getString("gui.openKey");
        if(key.equals("0")){
            Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:已关闭按键打开邮箱GUI");
        }else{
            if(key.contains("+")){
                int l = key.indexOf("+");
                String key1 = key.substring(0, l);
                String key2 = key.substring(l+1);
                Bukkit.getPluginManager().registerEvents(new DoubleKeyPress(Integer.parseInt(key1), Integer.parseInt(key2)), mb);
                Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:已启用组合键打开邮箱GUI");
            }else{
                Bukkit.getPluginManager().registerEvents(new SingleKeyPress(Integer.parseInt(key)), mb);
                Bukkit.getConsoleSender().sendMessage("§a-----[MailBox]:已启用单按键打开邮箱GUI");
            }
        }
        setBoxConfig(
            box.getString("gui.img"),
            box.getInt("gui.x"),
            box.getInt("gui.y"),
            box.getInt("gui.w"),
            box.getInt("gui.h"),
            box.getInt("gui.ww"),
            box.getInt("gui.hh"),
            box.getString("button.new.id"),
            box.getString("button.new.text"),
            box.getStringList("button.new.hover"),
            box.getString("button.new.img_1"),
            box.getString("button.new.img_2"),
            box.getInt("button.new.x"),
            box.getInt("button.new.y"),
            box.getInt("button.new.w"),
            box.getInt("button.new.h"),
            box.getBoolean("title.enable"),
            box.getString("title.type"),
            box.getInt("title.x"),
            box.getInt("title.y"),
            box.getString("title.text"),
            box.getDouble("title.size"),
            box.getString("title.image"),
            box.getInt("title.w"),
            box.getInt("title.h"),
            box.getInt("list.x"),
            box.getInt("list.y"),
            box.getInt("list.w"),
            box.getInt("list.h"),
            box.getInt("list.mh"),
            box.getInt("list.sh"),
            box.getInt("list.oh"),
            box.getString("list.nullBox"),
            box.getInt("mail.oy"),
            box.getString("mail.button.id"),
            box.getString("mail.button.image_1"),
            box.getString("mail.button.image_2"),
            box.getInt("mail.button.x"),
            box.getInt("mail.button.fy"),
            box.getInt("mail.button.w"),
            box.getInt("mail.button.h"),
            box.getInt("mail.topic.x"),
            box.getInt("mail.topic.fy"),
            box.getDouble("mail.topic.size"),
            box.getString("mail.topic.noRead"),
            box.getString("mail.topic.noFile"),
            box.getInt("mail.topic.div"),
            box.getInt("mail.date.x"),
            box.getInt("mail.date.fy"),
            box.getDouble("mail.date.size"),
            box.getString("mail.date.prefix"),
            box.getStringList("mail.date.display"),
            box.getInt("mail.sender.x"),
            box.getInt("mail.sender.fy"),
            box.getDouble("mail.sender.size"),
            box.getString("mail.sender.prefix"),
            box.getStringList("mail.sender.display"),
            box.getInt("mail.type.x"),
            box.getInt("mail.type.fy"),
            box.getDouble("mail.type.size"),
            box.getString("mail.type.prefix"),
            box.getStringList("mail.type.display"),
            box.getString("mail.icon.image_1"),
            box.getString("mail.icon.image_2"),
            box.getInt("mail.icon.x"),
            box.getInt("mail.icon.fy"),
            box.getInt("mail.icon.w"),
            box.getInt("mail.icon.h"),
            box.getStringList("mail.icon.display")
        );
        // 配置ContentGui
        setContentConfig(
            content.getString("gui.img"),
            content.getInt("gui.x"),
            content.getInt("gui.y"),
            content.getInt("gui.w"),
            content.getInt("gui.h"),
            content.getInt("gui.ww"),
            content.getInt("gui.hh"),
            content.getString("button.return.id"),
            content.getString("button.return.text"),
            content.getStringList("button.return.hover"),
            content.getString("button.return.img_1"),
            content.getString("button.return.img_2"),
            content.getInt("button.return.x"),
            content.getInt("button.return.y"),
            content.getInt("button.return.w"),
            content.getInt("button.return.h"),
            content.getString("button.collect.id"),
            content.getString("button.collect.collect.text"),
            content.getStringList("button.collect.collect.hover"),
            content.getString("button.collect.collect.img_1"),
            content.getString("button.collect.collect.img_2"),
            content.getString("button.collected.collected.text"),
            content.getStringList("button.collected.collected.hover"),
            content.getString("button.collected.collected.img_1"),
            content.getString("button.collected.collected.img_2"),
            content.getInt("button.collect.x"),
            content.getInt("button.collect.y"),
            content.getInt("button.collect.w"),
            content.getInt("button.collect.h"),
            content.getString("button.delete.id"),
            content.getString("button.delete.text"),
            content.getStringList("button.delete.hover"),
            content.getString("button.delete.img_1"),
            content.getString("button.delete.img_2"),
            content.getInt("button.delete.x"),
            content.getInt("button.delete.y"),
            content.getInt("button.delete.w"),
            content.getInt("button.delete.h"),
            content.getString("button.send.id"),
            content.getString("button.send.text"),
            content.getStringList("button.send.hover"),
            content.getString("button.send.img_1"),
            content.getString("button.send.img_2"),
            content.getInt("button.send.x"),
            content.getInt("button.send.y"),
            content.getInt("button.send.w"),
            content.getInt("button.send.h"),
            content.getInt("text.topic.x"),
            content.getInt("text.topic.y"),
            content.getDouble("text.topic.size"),
            content.getInt("text.topic.w"),
            content.getInt("text.date.x"),
            content.getInt("text.date.y"),
            content.getDouble("text.date.size"),
            content.getString("text.date.prefix"),
            content.getStringList("text.date.display"),
            content.getInt("text.sender.x"),
            content.getInt("text.sender.y"),
            content.getDouble("text.sender.size"),
            content.getString("text.sender.prefix"),
            content.getInt("text.coin.x"),
            content.getInt("text.coin.y"),
            content.getDouble("text.coin.size"),
            content.getString("text.coin.prefix"),
            content.getString("text.coin.suffix"),
            content.getInt("text.file.x"),
            content.getInt("text.file.y"),
            content.getString("text.file.text_yes"),
            content.getString("text.file.text_no"),
            content.getDouble("text.file.size"),
            content.getInt("text.cmd.x"),
            content.getInt("text.cmd.y"),
            content.getString("text.cmd.text"),
            content.getDouble("text.cmd.size"),
            content.getInt("text.content.x"),
            content.getInt("text.content.y"),
            content.getInt("text.content.w"),
            content.getInt("text.content.h"),
            content.getInt("text.content.mh"),
            content.getDouble("text.content.size"),
            content.getInt("text.content.count"),
            content.getInt("text.content.line"),
            content.getInt("text.content.sh"),
            content.getString("image.cmd.url"),
            content.getInt("image.cmd.x"),
            content.getInt("image.cmd.y"),
            content.getInt("image.cmd.w"),
            content.getInt("image.cmd.h"),
            content.getString("image.coin.url"),
            content.getInt("image.coin.x"),
            content.getInt("image.coin.y"),
            content.getInt("image.coin.w"),
            content.getInt("image.coin.h"),
            content.getString("slot.img"),
            content.getInt("slot.w"),
            content.getInt("slot.h"),
            content.getIntegerList("slot.x"),
            content.getIntegerList("slot.y")
        );
        // 配置SelectGui
        setSelectConfig(
            select.getString("gui.img"),
            select.getInt("gui.x"),
            select.getInt("gui.y"),
            select.getInt("gui.w"),
            select.getInt("gui.h"),
            select.getInt("gui.ww"),
            select.getInt("gui.hh"),
            select.getString("button.id.system"),
            select.getString("button.id.player"),
            select.getString("button.img_1"),
            select.getString("button.img_2"),
            select.getIntegerList("button.x"),
            select.getIntegerList("button.y"),
            select.getInt("button.w"),
            select.getInt("button.h"),
            select.getStringList("button.list")
        );
        // 配置SendGui
        setSendConfig(
            send.getString("gui.img"),
            send.getInt("gui.x"),
            send.getInt("gui.y"),
            send.getInt("gui.w"),
            send.getInt("gui.h"),
            send.getInt("gui.ww"),
            send.getInt("gui.hh"),
            send.getInt("gui.ix"),
            send.getInt("gui.iy"),
            send.getString("button.return.id"),
            send.getString("button.return.text"),
            send.getStringList("button.return.hover"),
            send.getString("button.return.img_1"),
            send.getString("button.return.img_2"),
            send.getInt("button.return.x"),
            send.getInt("button.return.y"),
            send.getInt("button.return.w"),
            send.getInt("button.return.h"),
            send.getString("button.preview.id"),
            send.getString("button.preview.text"),
            send.getStringList("button.preview.hover"),
            send.getString("button.preview.img_1"),
            send.getString("button.preview.img_2"),
            send.getInt("button.preview.x"),
            send.getInt("button.preview.y"),
            send.getInt("button.preview.w"),
            send.getInt("button.preview.h"),
            send.getInt("text.topic.x"),
            send.getInt("text.topic.y"),
            send.getDouble("text.topic.size"),
            send.getString("text.topic.text"),
            send.getInt("text.recipient.x"),
            send.getInt("text.recipient.y"),
            send.getDouble("text.recipient.size"),
            send.getString("text.recipient.text"),
            send.getInt("text.text.x"),
            send.getInt("text.text.y"),
            send.getDouble("text.text.size"),
            send.getString("text.text.text"),
            send.getInt("text.command.x"),
            send.getInt("text.command.y"),
            send.getDouble("text.command.size"),
            send.getString("text.command.text"),
            send.getInt("text.description.x"),
            send.getInt("text.description.y"),
            send.getDouble("text.description.size"),
            send.getString("text.description.text"),
            send.getInt("text.item.x"),
            send.getInt("text.item.y"),
            send.getDouble("text.item.size"),
            send.getString("text.item.text"),
            send.getInt("field.topic.x"),
            send.getInt("field.topic.y"),
            send.getInt("field.topic.w"),
            send.getInt("field.topic.h"),
            send.getInt("field.topic.max"),
            send.getInt("field.recipient.x"),
            send.getInt("field.recipient.y"),
            send.getInt("field.recipient.w"),
            send.getInt("field.recipient.h"),
            send.getInt("field.recipient.max"),
            send.getInt("field.text.x"),
            send.getInt("field.text.y"),
            send.getInt("field.text.w"),
            send.getInt("field.text.h"),
            send.getInt("field.text.max"),
            send.getInt("field.command.x"),
            send.getInt("field.command.y"),
            send.getInt("field.command.w"),
            send.getInt("field.command.h"),
            send.getInt("field.command.max"),
            send.getInt("field.description.x"),
            send.getInt("field.description.y"),
            send.getInt("field.description.w"),
            send.getInt("field.description.h"),
            send.getInt("field.description.max"),
            send.getInt("field.coin.x"),
            send.getInt("field.coin.y"),
            send.getInt("field.coin.w"),
            send.getInt("field.coin.h"),
            send.getInt("field.coin.max"),
            send.getString("image.coin.url"),
            send.getInt("image.coin.x"),
            send.getInt("image.coin.y"),
            send.getInt("image.coin.w"),
            send.getInt("image.coin.h"),
            send.getString("slot.img"),
            send.getInt("slot.w"),
            send.getInt("slot.h"),
            send.getIntegerList("slot.x"),
            send.getIntegerList("slot.y")
        );
    }
    
    // 判断VexView文件夹是否存在
    private void ConfigExist(){
        File f = new File(DATA_FOLDER);
        Bukkit.getConsoleSender().sendMessage("§6-----正在检查VexView配置文件夹是否存在");
        if(!f.exists()){
            Bukkit.getConsoleSender().sendMessage("§a-----正在创建VexView配置文件夹");
            f.mkdir();
        }
    }
    
    // 加载配置
    private void ConfigLoad(){
        YamlConfiguration hud = ConfigGet("hud");
        YamlConfiguration box = ConfigGet("box");
        YamlConfiguration content = ConfigGet("content");
        YamlConfiguration select = ConfigGet("select");
        YamlConfiguration send = ConfigGet("send");
        ConfigSet(hud, box, content, select, send);
    }
    
    // 获取/创建 配置
    private YamlConfiguration ConfigGet(String filename){
        File f = new File(DATA_FOLDER, filename+".yml");
        Bukkit.getConsoleSender().sendMessage("§6-----正在检查"+filename+"配置文件是否存在");
        if(!f.exists()){
            try {
                Bukkit.getConsoleSender().sendMessage("§a-----正在创建"+filename+"配置文件");
                InputStream is = VexViewConfig.class.getResourceAsStream("Default/"+filename+".yml");
                OutputStream os = new FileOutputStream(f);
                byte[] flush = new byte[1024];
                int len = -1;
                while((len=is.read(flush))!=-1) os.write(flush, 0, len);
                os.flush();
                os.close();
                is.close();
                Bukkit.getConsoleSender().sendMessage("§a-----"+filename+"配置文件创建成功");
            } catch (IOException ex) {
                Bukkit.getConsoleSender().sendMessage("§c-----"+filename+"配置文件创建失败");
                getLogger().info(ex.getLocalizedMessage());
                return null;
            }
        }
        Bukkit.getConsoleSender().sendMessage("§6-----正在加载"+filename+"配置文件");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
        return config;
    }

}
