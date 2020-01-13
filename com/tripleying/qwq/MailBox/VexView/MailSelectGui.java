package com.tripleying.qwq.MailBox.VexView;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import java.util.HashMap;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.VexButton;
import org.bukkit.entity.Player;

public class MailSelectGui extends VexGui{
    
    private static String gui_img;
    private static int gui_x;
    private static int gui_y;
    private static int gui_w;
    private static int gui_h;
    private static int gui_ww;
    private static int gui_hh;
    private static final HashMap<String, String> BUTTON_ID = new HashMap();
    private static String button_img_1 = "[local]MailBox/button_small.png";
    private static String button_img_2 = "[local]MailBox/button_small_.png";
    private static List<Integer> button_x;
    private static List<Integer> button_y;
    private static int button_w = 50;
    private static int button_h = 50;
    private static List<String> button_list;
    
    public MailSelectGui(Player p) {
        super(gui_img,gui_x,gui_y,gui_w,gui_h,gui_ww,gui_hh);
        int i=0;
        for(String type: button_list){
            if(p.hasPermission("mailbox.admin.send."+type)){
                this.addComponent(new VexButton(BUTTON_ID.get(type),GlobalConfig.getTypeName(type),button_img_1,button_img_2,button_x.get(i),button_y.get(i),button_w,button_h,player -> MailSendGui.openMailSendGui(player, type, null)));
                i++;
            }
        }
        if(i==0){
            if(button_list.contains("player") && MailBoxAPI.hasPlayerPermission(p, "mailbox.send.player")){
                this.addComponent(new VexButton(BUTTON_ID.get("player"),GlobalConfig.getTypeName("player"),button_img_1,button_img_2,button_x.get(i),button_y.get(i),button_w,button_h,player -> MailSendGui.openMailSendGui(player, "player", null)));
                i++;
            }
            if(button_list.contains("times") && MailBoxAPI.hasPlayerPermission(p, "mailbox.send.times")){
                this.addComponent(new VexButton(BUTTON_ID.get("times"),GlobalConfig.getTypeName("times"),button_img_1,button_img_2,button_x.get(i),button_y.get(i),button_w,button_h,player -> MailSendGui.openMailSendGui(player, "times", null)));
                i++;
            }
        }
    }
    
    public static void setSelectConfig(
        String gui_img,
        int gui_x,
        int gui_y,
        int gui_w,
        int gui_h,
        int gui_ww,
        int gui_hh,
        String button_id_system,
        String button_id_player,
        String button_id_permission,
        String button_id_date,
        String button_id_times,
        String button_id_cdkey,
        String button_id_online,
        String button_id_template,
        String button_img_1,
        String button_img_2,
        List<Integer> button_x,
        List<Integer> button_y,
        int button_w,
        int button_h,
        List<String> button_list
    ){
        // GUI
        MailSelectGui.gui_img = gui_img;
        MailSelectGui.gui_x = gui_x;
        MailSelectGui.gui_y = gui_y;
        MailSelectGui.gui_w = gui_w;
        MailSelectGui.gui_h = gui_h;
        MailSelectGui.gui_ww = gui_ww;
        MailSelectGui.gui_hh = gui_hh;
        // 按钮
        BUTTON_ID.clear();
        BUTTON_ID.put("system", button_id_system);
        BUTTON_ID.put("player", button_id_player);
        BUTTON_ID.put("permission", button_id_permission);
        BUTTON_ID.put("date", button_id_date);
        BUTTON_ID.put("times", button_id_times);
        BUTTON_ID.put("cdkey", button_id_cdkey);
        BUTTON_ID.put("online", button_id_online);
        BUTTON_ID.put("template", button_id_template);
        MailSelectGui.button_img_1 = button_img_1;
        MailSelectGui.button_img_2 = button_img_2;
        MailSelectGui.button_x = button_x;
        MailSelectGui.button_y = button_y;
        MailSelectGui.button_w = button_w;
        MailSelectGui.button_h = button_h;
        MailSelectGui.button_list = button_list;
    }
    
    public static void openMailSelectGui(Player p){
        for(String type:button_list){
            if(p.hasPermission("mailbox.admin.send."+type)){
                VexViewAPI.openGui(p, new MailSelectGui(p));
                return;
            }
        }
        int count = 0;
        if(MailBoxAPI.hasPlayerPermission(p, "mailbox.send.player")) count++;
        if(MailBoxAPI.hasPlayerPermission(p, "mailbox.send.times")) count++;
        switch (count) {
            case 2:
                VexViewAPI.openGui(p, new MailSelectGui(p));
                break;
            case 1:
                if(MailBoxAPI.hasPlayerPermission(p, "mailbox.send.player")) MailSendGui.openMailSendGui(p, "player", null);
                else MailSendGui.openMailSendGui(p, "times", null);
                break;
            default:
                p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+" 你没有权限发送邮件");
        }
    }
    
}
