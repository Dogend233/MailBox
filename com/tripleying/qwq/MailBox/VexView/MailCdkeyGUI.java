    package com.tripleying.qwq.MailBox.VexView;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.Message;
import java.util.Arrays;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexComponents;
import lk.vexview.gui.components.VexImage;
import lk.vexview.gui.components.VexText;
import lk.vexview.gui.components.VexTextField;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MailCdkeyGUI extends VexGui {
    
    private static String gui_img;
    private static int gui_x;
    private static int gui_y;
    private static int gui_w;
    private static int gui_h;
    private static int gui_ww;
    private static int gui_hh;
    private static VexComponents title;
    private static boolean title_enable;
    private static int[] field_exchange;
    private static String[] button_exchange;
    private static String[] button_export;
    
    public MailCdkeyGUI(Player p) {
        super(gui_img,gui_x,gui_y,gui_w,gui_h,gui_ww,gui_hh);
        if(title_enable) this.addComponent(title);
        this.addComponent(new VexTextField(field_exchange[0],field_exchange[1],field_exchange[2],field_exchange[3],field_exchange[4],field_exchange[5]));
        this.addComponent(new VexButton(button_exchange[0],button_exchange[1],button_exchange[2],button_exchange[3],Integer.parseInt(button_exchange[4]),Integer.parseInt(button_exchange[5]),Integer.parseInt(button_exchange[6]),Integer.parseInt(button_exchange[7]),player -> {
            player.closeInventory();
            MailBoxAPI.exchangeCdkey(player, getTextField(field_exchange[5]).getTypedText());
        }));
        if(p.hasPermission("mailbox.admin.export.cdkey")){
            this.addComponent(new VexButton(button_export[0],button_export[1],button_export[2],button_export[3],Integer.parseInt(button_export[4]),Integer.parseInt(button_export[5]),Integer.parseInt(button_export[6]),Integer.parseInt(button_export[7]),player -> {
                player.closeInventory();
                player.performCommand("mb cdkey export all");
            }));
        }
    }
    
    public static void setCdkeyConfig(YamlConfiguration cdkey){
        // GUI
        gui_img = cdkey.getString("gui.img");
        gui_x = cdkey.getInt("gui.x");
        gui_y = cdkey.getInt("gui.y");
        gui_w = cdkey.getInt("gui.w");
        gui_h = cdkey.getInt("gui.h");
        gui_ww = cdkey.getInt("gui.ww");
        gui_hh = cdkey.getInt("gui.hh");
        // 标题
        title_enable = cdkey.getBoolean("title.enable");
        if(title_enable){
            switch (cdkey.getString("title.type")) {
                case "text":
                    title = new VexText(cdkey.getInt("title.x"),cdkey.getInt("title.y"),Arrays.asList(cdkey.getString("title.text")),cdkey.getDouble("title.size"));
                    break;
                case "image":
                    title = new VexImage(cdkey.getString("title.image"),cdkey.getInt("title.x"),cdkey.getInt("title.y"),cdkey.getInt("title.w"),cdkey.getInt("title.h"));
                    break;
                default:
                    title_enable = false;
                    break;
            }
        }
        // 文本框
        field_exchange = new int[]{cdkey.getInt("field.x"),cdkey.getInt("field.y"),cdkey.getInt("field.w"),cdkey.getInt("field.h"),cdkey.getInt("field.max"),1};
        // 按钮
        button_exchange = new String[]{cdkey.getString("button.exchange.id"),cdkey.getString("button.exchange.text"),cdkey.getString("button.exchange.img_1"),cdkey.getString("button.exchange.img_2"),Integer.toString(cdkey.getInt("button.exchange.x")),Integer.toString(cdkey.getInt("button.exchange.y")),Integer.toString(cdkey.getInt("button.exchange.w")),Integer.toString(cdkey.getInt("button.exchange.h"))};
        button_export = new String[]{cdkey.getString("button.export.id"),cdkey.getString("button.export.text"),cdkey.getString("button.export.img_1"),cdkey.getString("button.export.img_2"),Integer.toString(cdkey.getInt("button.export.x")),Integer.toString(cdkey.getInt("button.export.y")),Integer.toString(cdkey.getInt("button.export.w")),Integer.toString(cdkey.getInt("button.export.h"))};
    }
    
    public static void openMailCdkeyGui(Player p){
        if(MailBoxAPI.hasPlayerPermission(p, "mailbox.cdkey.use")){
            VexViewAPI.openGui(p, new MailCdkeyGUI(p));
        }else{
            p.sendMessage(Message.globalNoPermission);
        }
    }
    
}
