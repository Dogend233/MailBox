package com.嘤嘤嘤.qwq.MailBox.VexView;

import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
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
    private static HashMap<String, String> button_id = new HashMap();
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
                this.addComponent(new VexButton(button_id.get(type),GlobalConfig.getTypeName(type),button_img_1,button_img_2,button_x.get(i),button_y.get(i),button_w,button_h,player -> MailSendGui.openMailSendGui(player, type, null)));
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
        button_id.clear();
        button_id.put("system", button_id_system);
        button_id.put("player", button_id_player);
        button_id.put("permission", button_id_permission);
        MailSelectGui.button_img_1 = button_img_1;
        MailSelectGui.button_img_2 = button_img_2;
        MailSelectGui.button_x = button_x;
        MailSelectGui.button_y = button_y;
        MailSelectGui.button_w = button_w;
        MailSelectGui.button_h = button_h;
        MailSelectGui.button_list = button_list;
    }
    
    public static void openMailSelectGui(Player p){
        if(p.hasPermission("mailbox.admin.send.player") || p.hasPermission("mailbox.admin.send.system") || p.hasPermission("mailbox.admin.send.permission")){
            VexViewAPI.openGui(p, new MailSelectGui(p));
        }else if(p.hasPermission("mailbox.send.player.only")){
            MailSendGui.openMailSendGui(p, "player", null);
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+" 你没有权限发送邮件");
        }
    }
    
}
