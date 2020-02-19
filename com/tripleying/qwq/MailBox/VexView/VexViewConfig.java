package com.tripleying.qwq.MailBox.VexView;

import com.tripleying.qwq.MailBox.ConfigMessage;
import com.tripleying.qwq.MailBox.Events.DoubleKeyPress;
import com.tripleying.qwq.MailBox.Events.JoinAndQuit;
import com.tripleying.qwq.MailBox.Events.SingleKeyPress;
import com.tripleying.qwq.MailBox.Events.SendGuiOpen;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.Utils.FileUtil;
import com.tripleying.qwq.MailBox.Utils.ReflectionUtil;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import lk.vexview.gui.components.HoverTextComponent;
import lk.vexview.gui.components.VexHoverText;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class VexViewConfig {
        
    private static final String DIR = "VexView";
    private static final String JAR = "vexview";
    private static final HashMap<String, String> KEY = new HashMap();
    
    // 配置VexView
    public static void VexViewConfigSet(){
        Bukkit.getConsoleSender().sendMessage(ConfigMessage.vexview);
        // 加载按键列表
        if(KEY.isEmpty()) ReflectionUtil.getVexViewKeys(KEY);
        // 注册SendGUI打开事件监听器
        Bukkit.getPluginManager().registerEvents(new SendGuiOpen(), MailBox.getInstance());
        // VexView文件夹
        File f = FileUtil.getFile(DIR);
        if(!f.exists()){
            Bukkit.getConsoleSender().sendMessage(ConfigMessage.folder_create.replace("%folder%", DIR));
            f.mkdir();
        }
        // 加载配置
        ConfigLoad();
    }
    
    public static void ConfigSet(YamlConfiguration hud, 
            YamlConfiguration box, 
            YamlConfiguration content, 
            YamlConfiguration select, 
            YamlConfiguration send, 
            YamlConfiguration item_list, 
            YamlConfiguration cdkey){
        // 配置常驻Hud
        if(hud.getBoolean("hud.enable")){
            MailBoxHud.setHudConfig(hud);
            Bukkit.getPluginManager().registerEvents(new JoinAndQuit(true, true), MailBox.getInstance());
        }else{
            Bukkit.getPluginManager().registerEvents(new JoinAndQuit(true, false), MailBox.getInstance());
        }
        // 配置邮件提醒Hud
        MailTipsHud.setHudConfig(hud);
        // 配置BoxGui
        MailBox.getInstance().setOpenCmd(box.getBoolean("gui.openCmd"));
        String key = box.getString("gui.openKey");
        if(!key.equals("0")){
            if(key.contains("+")){
                int l = key.indexOf("+");
                String key1 = key.substring(0, l);
                String key2 = key.substring(l+1);
                Bukkit.getPluginManager().registerEvents(new DoubleKeyPress(Integer.parseInt(key1), Integer.parseInt(key2)), MailBox.getInstance());
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.double_box.replace("%key1%", getKey(key1)).replace("%key2%", getKey(key2)));
            }else{
                Bukkit.getPluginManager().registerEvents(new SingleKeyPress(Integer.parseInt(key)), MailBox.getInstance());
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.single_box.replace("%key%", getKey(key)));
            }
        }
        MailBoxGui.setBoxConfig(
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
            box.getString("button.box.id"),
            box.getString("button.box.text_r"),
            box.getString("button.box.text_s"),
            box.getString("button.box.img_1"),
            box.getString("button.box.img_2"),
            box.getInt("button.box.x"),
            box.getInt("button.box.y"),
            box.getInt("button.box.w"),
            box.getInt("button.box.h"),
            box.getString("button.cdkey.id"),
            box.getString("button.cdkey.text"),
            box.getString("button.cdkey.img_1"),
            box.getString("button.cdkey.img_2"),
            box.getInt("button.cdkey.x"),
            box.getInt("button.cdkey.y"),
            box.getInt("button.cdkey.w"),
            box.getInt("button.cdkey.h"),
            box.getString("button.onekey.id"),
            box.getString("button.onekey.text"),
            box.getStringList("button.onekey.hover"),
            box.getString("button.onekey.img_1"),
            box.getString("button.onekey.img_2"),
            box.getInt("button.onekey.x"),
            box.getInt("button.onekey.y"),
            box.getInt("button.onekey.w"),
            box.getInt("button.onekey.h"),
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
            box.getString("mail.icon.image"),
            box.getInt("mail.icon.x"),
            box.getInt("mail.icon.fy"),
            box.getInt("mail.icon.w"),
            box.getInt("mail.icon.h"),
            box.getStringList("mail.icon.display")
        );
        // 配置ContentGui
        MailContentGui.setContentConfig(
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
            content.getString("button.collect.collected.text"),
            content.getStringList("button.collect.collected.hover"),
            content.getString("button.collect.collected.img_1"),
            content.getString("button.collect.collected.img_2"),
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
            content.getString("button.send.cdk"),
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
            content.getInt("text.deadline.x"),
            content.getInt("text.deadline.y"),
            content.getDouble("text.deadline.size"),
            content.getString("text.deadline.prefix"),
            content.getInt("text.times.x"),
            content.getInt("text.times.y"),
            content.getDouble("text.times.size"),
            content.getString("text.times.prefix"),
            content.getInt("text.key.x"),
            content.getInt("text.key.y"),
            content.getDouble("text.key.size"),
            content.getString("text.key.prefix"),
            content.getInt("text.sender.x"),
            content.getInt("text.sender.y"),
            content.getDouble("text.sender.size"),
            content.getString("text.sender.prefix"),
            content.getInt("text.coin.x"),
            content.getInt("text.coin.y"),
            content.getDouble("text.coin.size"),
            content.getString("text.coin.prefix"),
            content.getString("text.coin.suffix"),
            content.getInt("text.point.x"),
            content.getInt("text.point.y"),
            content.getDouble("text.point.size"),
            content.getString("text.point.prefix"),
            content.getString("text.point.suffix"),
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
            content.getString("image.point.url"),
            content.getInt("image.point.x"),
            content.getInt("image.point.y"),
            content.getInt("image.point.w"),
            content.getInt("image.point.h"),
            content.getString("slot.img"),
            content.getInt("slot.w"),
            content.getInt("slot.h"),
            content.getIntegerList("slot.x"),
            content.getIntegerList("slot.y")
        );
        // 配置SelectGui
        MailSelectGui.setSelectConfig(
            select.getString("gui.img"),
            select.getInt("gui.x"),
            select.getInt("gui.y"),
            select.getInt("gui.w"),
            select.getInt("gui.h"),
            select.getInt("gui.ww"),
            select.getInt("gui.hh"),
            select.getString("button.id.system"),
            select.getString("button.id.player"),
            select.getString("button.id.permission"),
            select.getString("button.id.date"),
            select.getString("button.id.times"),
            select.getString("button.id.keytimes"),
            select.getString("button.id.cdkey"),
            select.getString("button.id.online"),
            select.getString("button.id.template"),
            select.getString("button.img_1"),
            select.getString("button.img_2"),
            select.getIntegerList("button.x"),
            select.getIntegerList("button.y"),
            select.getInt("button.w"),
            select.getInt("button.h"),
            select.getStringList("button.list")
        );
        // 配置SendGui
        MailSendGui.setSendConfig(
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
            send.getString("button.preview.error"),
            send.getString("button.preview.item-ban"),
            send.getInt("text.topic.x"),
            send.getInt("text.topic.y"),
            send.getDouble("text.topic.size"),
            send.getString("text.topic.text"),
            send.getInt("text.recipient.x"),
            send.getInt("text.recipient.y"),
            send.getDouble("text.recipient.size"),
            send.getString("text.recipient.text"),
            send.getInt("text.permission.x"),
            send.getInt("text.permission.y"),
            send.getDouble("text.permission.size"),
            send.getString("text.permission.text"),
            send.getInt("text.startdate.x"),
            send.getInt("text.startdate.y"),
            send.getDouble("text.startdate.size"),
            send.getString("text.startdate.text"),
            send.getInt("text.deadline.x"),
            send.getInt("text.deadline.y"),
            send.getDouble("text.deadline.size"),
            send.getString("text.deadline.text"),
            send.getInt("text.times.x"),
            send.getInt("text.times.y"),
            send.getDouble("text.times.size"),
            send.getString("text.times.text"),
            send.getInt("text.key.x"),
            send.getInt("text.key.y"),
            send.getDouble("text.key.size"),
            send.getString("text.key.text"),
            send.getInt("text.onlyCDK.x"),
            send.getInt("text.onlyCDK.y"),
            send.getDouble("text.onlyCDK.size"),
            send.getString("text.onlyCDK.text"),
            send.getInt("text.template.x"),
            send.getInt("text.template.y"),
            send.getDouble("text.template.size"),
            send.getString("text.template.text"),
            send.getInt("text.text.x"),
            send.getInt("text.text.y"),
            send.getDouble("text.text.size"),
            send.getString("text.text.text"),
            send.getInt("text.sender.x"),
            send.getInt("text.sender.y"),
            send.getDouble("text.sender.size"),
            send.getString("text.sender.text"),
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
            send.getInt("checkBox.onlyCDK.x"),
            send.getInt("checkBox.onlyCDK.y"),
            send.getInt("checkBox.onlyCDK.w"),
            send.getInt("checkBox.onlyCDK.h"),
            send.getString("checkBox.onlyCDK.image1"),
            send.getString("checkBox.onlyCDK.image2"),
            send.getString("image.coin.url"),
            send.getInt("image.coin.x"),
            send.getInt("image.coin.y"),
            send.getInt("image.coin.w"),
            send.getInt("image.coin.h"),
            send.getString("image.point.url"),
            send.getInt("image.point.x"),
            send.getInt("image.point.y"),
            send.getInt("image.point.w"),
            send.getInt("image.point.h"),
            send.getString("slot.img"),
            send.getInt("slot.w"),
            send.getInt("slot.h"),
            send.getIntegerList("slot.x"),
            send.getIntegerList("slot.y"),
            send
        );
        // 配置物品列表GUI
        MailItemListGui.setItemListConfig(item_list);
        // 配置CdkeyGUI
        MailCdkeyGUI.setCdkeyConfig(cdkey);
    }
    
    // 加载配置
    private static void ConfigLoad(){
        ConfigSet(FileUtil.getConfig(DIR, "hud.yml", JAR), 
            FileUtil.getConfig(DIR, "box.yml", JAR), 
            FileUtil.getConfig(DIR, "content.yml", JAR), 
            FileUtil.getConfig(DIR, "select.yml", JAR), 
            FileUtil.getConfig(DIR, "send.yml", JAR), 
            FileUtil.getConfig(DIR, "item_list.yml", JAR), 
            FileUtil.getConfig(DIR, "cdkey.yml", JAR)
        );
    }
    
    public static void setHover(HoverTextComponent v, List<String> t){
        v.setHover(new VexHoverText(t));
    }
    
    private static String getKey(String key){
        if(KEY.containsKey(key)){
            return KEY.get(key);
        }else{
            return "";
        }
    }

}
