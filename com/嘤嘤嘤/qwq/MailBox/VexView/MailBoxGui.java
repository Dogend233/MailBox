package com.嘤嘤嘤.qwq.MailBox.VexView;

import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListPermission;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListPermissionId;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListPlayer;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListPlayerId;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.getUnMailList;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailContentGui.openMailContentGui;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexComponents;
import lk.vexview.gui.components.VexHoverText;
import lk.vexview.gui.components.VexImage;
import lk.vexview.gui.components.VexScrollingList;
import lk.vexview.gui.components.VexText;
import org.bukkit.entity.Player;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListSystem;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListSystemId;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailSelectGui.openMailSelectGui;

public class MailBoxGui extends VexGui{
    
    private static String gui_img;
    private static int gui_x;
    private static int gui_y;
    private static int gui_w;
    private static int gui_h;
    private static int gui_ww;
    private static int gui_hh;
    private static VexButton button_new;
    private static VexButton button_inbox;
    private static VexButton button_outbox;
    private static VexComponents title;
    private static boolean title_enable;
    private static int list_x;
    private static int list_y;
    private static int list_w;
    private static int list_h;
    private static int list_mh;
    private static int list_sh;
    private static int list_oh;
    private static String list_nullBox;
    private static int mail_y_offset;
    private static String mail_button_id;
    private static String mail_button_image_1;
    private static String mail_button_image_2;
    private static int mail_button_x;
    private static int mail_button_y;
    private static int mail_button_w;
    private static int mail_button_h;
    private static int mail_topic_x;
    private static int mail_topic_y;
    private static double mail_topic_size;
    private static String mail_topic_noRead;
    private static String mail_topic_noFile;
    private static int mail_topic_div;
    private static int mail_date_x;
    private static int mail_date_y;
    private static double mail_date_size;
    private static String mail_date_prefix;
    private static List<String> mail_date_display;
    private static int mail_sender_x;
    private static int mail_sender_y;
    private static double mail_sender_size;
    private static String mail_sender_prefix;
    private static List<String> mail_sender_display;
    private static int mail_type_x;
    private static int mail_type_y;
    private static double mail_type_size;
    private static String mail_type_prefix;
    private static List<String> mail_type_display;
    private static String mail_icon_image_1;
    private static String mail_icon_image_2;
    private static int mail_icon_x;
    private static int mail_icon_y;
    private static int mail_icon_w;
    private static int mail_icon_h;
    private static List<String> mail_icon_display;
    
    private boolean asSender;
    
    public MailBoxGui(Player p, String playertype){
        super(gui_img,gui_x,gui_y,gui_w,gui_h,gui_ww,gui_hh);
        if(title_enable) this.addComponent(title);
        this.addComponent(button_new);
        switch (playertype) {
            case "Sender":
                asSender = false;
                this.addComponent(button_inbox);
                break;
            case "Recipient":
                asSender = true;
                this.addComponent(button_outbox);
                break;
        }
        VexScrollingList vsl = getBoxList(p, playertype);
        if(vsl==null){
            this.addComponent(new VexText(-1,-1,Arrays.asList(list_nullBox),1));
        }else{
            this.addComponent(vsl);
        }
    }
    
    public static void setBoxConfig(
        String gui_img,
        int gui_x,
        int gui_y,
        int gui_w,
        int gui_h,
        int gui_ww,
        int gui_hh,
        String button_new_id,
        String button_new_text,
        List<String> button_new_hover,
        String button_new_img_1,
        String button_new_img_2,
        int button_new_x,
        int button_new_y,
        int button_new_w,
        int button_new_h,
        boolean title_enable,
        String title_type,
        int title_x,
        int title_y,
        String title_text_content,
        double title_text_size,
        String title_image_url,
        int title_image_w,
        int title_image_h,
        int list_x,
        int list_y,
        int list_w,
        int list_h,
        int list_mh,
        int list_sh,
        int list_oh,
        String list_nullBox,
        int mail_y_offset,
        String mail_button_id,
        String mail_button_image_1,
        String mail_button_image_2,
        int mail_button_x,
        int mail_button_y,
        int mail_button_w,
        int mail_button_h,
        int mail_topic_x,
        int mail_topic_y,
        double mail_topic_size,
        String mail_topic_noRead,
        String mail_topic_noFile,
        int mail_topic_div,
        int mail_date_x,
        int mail_date_y,
        double mail_date_size,
        String mail_date_prefix,
        List<String> mail_date_display,
        int mail_sender_x,
        int mail_sender_y,
        double mail_sender_size,
        String mail_sender_prefix,
        List<String> mail_sender_display,
        int mail_type_x,
        int mail_type_y,
        double mail_type_size,
        String mail_type_prefix,
        List<String> mail_type_display,
        String mail_icon_image_1,
        String mail_icon_image_2,
        int mail_icon_x,
        int mail_icon_y,
        int mail_icon_w,
        int mail_icon_h,
        List<String> mail_icon_display
    ){
        // GUI
        MailBoxGui.gui_img = gui_img;
        MailBoxGui.gui_x = gui_x;
        MailBoxGui.gui_y = gui_y;
        MailBoxGui.gui_w = gui_w;
        MailBoxGui.gui_h = gui_h;
        MailBoxGui.gui_ww = gui_ww;
        MailBoxGui.gui_hh = gui_hh;
        // 新邮件按钮
        button_new = new VexButton(button_new_id,button_new_text,button_new_img_1,button_new_img_2,button_new_x,button_new_y,button_new_w,button_new_h,player -> {
            if(player.hasPermission("mailbox.gui.send")){
                openMailSelectGui(player);
            }else{
                player.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限打开此GUI");
            }
        });
        // 收件箱按钮
        button_inbox = new VexButton(button_new_id+"R","收件箱",button_new_img_1,button_new_img_2,button_new_x,button_new_y+button_new_h+1,button_new_w,button_new_h,player -> {
            openMailBoxGui(player, "Recipient");
        });
        // 发件箱按钮
        button_outbox = new VexButton(button_new_id+"S","发件箱",button_new_img_1,button_new_img_2,button_new_x,button_new_y+button_new_h+1,button_new_w,button_new_h,player -> {
            openMailBoxGui(player, "Sender");
        });
        if(!button_new_hover.isEmpty()) button_new.setHover(new VexHoverText(button_new_hover));
        // 标题
        MailBoxGui.title_enable = title_enable;
        if(title_enable){
            if(title_type.equals("text")){
                title = new VexText(title_x,title_y,Arrays.asList(title_text_content),title_text_size);
            }else if(title_type.equals("image")){
                title = new VexImage(title_image_url,title_x,title_y,title_image_w,title_image_h);
            }else{
                MailBoxGui.title_enable = false;
            }
        }
        // 邮件列表
        MailBoxGui.list_x = list_x;
        MailBoxGui.list_y = list_y;
        MailBoxGui.list_w = list_w;
        MailBoxGui.list_h = list_h;
        MailBoxGui.list_mh = list_mh;
        MailBoxGui.list_sh = list_sh;
        MailBoxGui.list_oh = list_oh;
        MailBoxGui.list_nullBox = list_nullBox;
        // 邮件y坐标偏移量
        MailBoxGui.mail_y_offset = mail_y_offset;
        // 邮件背景图
        MailBoxGui.mail_button_image_1 = mail_button_image_1;
        MailBoxGui.mail_button_image_2 = mail_button_image_2;
        MailBoxGui.mail_button_x = mail_button_x;
        MailBoxGui.mail_button_y = mail_button_y;
        MailBoxGui.mail_button_w = mail_button_w;
        MailBoxGui.mail_button_h = mail_button_h;
        // 邮件标题
        MailBoxGui.mail_topic_x = mail_topic_x;
        MailBoxGui.mail_topic_y = mail_topic_y;
        MailBoxGui.mail_topic_size = mail_topic_size;
        MailBoxGui.mail_topic_noRead = mail_topic_noRead;
        MailBoxGui.mail_topic_noFile = mail_topic_noFile;
        MailBoxGui.mail_topic_div = mail_topic_div;
        // 邮件发送时间
        MailBoxGui.mail_date_x = mail_date_x;
        MailBoxGui.mail_date_y = mail_date_y;
        MailBoxGui.mail_date_size = mail_date_size;
        MailBoxGui.mail_date_prefix = mail_date_prefix;
        MailBoxGui.mail_date_display = mail_date_display;
        // 发件人
        MailBoxGui.mail_sender_x = mail_sender_x;
        MailBoxGui.mail_sender_y = mail_sender_y;
        MailBoxGui.mail_sender_size = mail_sender_size;
        MailBoxGui.mail_sender_prefix = mail_sender_prefix;
        MailBoxGui.mail_sender_display = mail_sender_display;
        // 邮件类型
        MailBoxGui.mail_type_x = mail_type_x;
        MailBoxGui.mail_type_y = mail_type_y;
        MailBoxGui.mail_type_size = mail_type_size;
        MailBoxGui.mail_type_prefix = mail_type_prefix;
        MailBoxGui.mail_type_display = mail_type_display;
        // 附件图标
        MailBoxGui.mail_icon_image_1 = mail_icon_image_1;
        MailBoxGui.mail_icon_image_2 = mail_icon_image_2;
        MailBoxGui.mail_icon_x = mail_icon_x;
        MailBoxGui.mail_icon_y = mail_icon_y;
        MailBoxGui.mail_icon_w = mail_icon_w;
        MailBoxGui.mail_icon_h = mail_icon_h;
        MailBoxGui.mail_icon_display = mail_sender_display;
    }
    
    // 获取邮件列表
    private VexScrollingList getBoxList(Player p, String playertype){
        HashMap<Integer, TextMail> systemmail = MailListSystem;
        HashMap<Integer, TextMail> playermail = MailListPlayer;
        HashMap<Integer, TextMail> permissionmail = MailListPermission;
        if(MailListSystem.isEmpty() && MailListPlayer.isEmpty() && MailListPermission.isEmpty()){
            return null;
        }else{
            int count = 0;
            ArrayList<Integer> systemid = new ArrayList();
            ArrayList<Integer> playerid = new ArrayList();
            ArrayList<Integer> permissionid = new ArrayList();
            if(p.hasPermission("mailbox.see.system")){
                getUnMailList(p, "system");
                systemid = MailListSystemId.get(p.getName()).get("as"+playertype);
                count += systemid.size();
            }
            if(p.hasPermission("mailbox.see.player")){
                getUnMailList(p, "player");
                playerid = MailListPlayerId.get(p.getName()).get("as"+playertype);
                count += playerid.size();
            }
            if(p.hasPermission("mailbox.see.permission")){
                getUnMailList(p, "permission");
                permissionid = MailListPermissionId.get(p.getName()).get("as"+playertype);
                count += permissionid.size();
            }
            if(count==0) return null;
            int mh = count*list_sh+list_oh;
            if(mh<list_mh) mh=list_mh;
            VexScrollingList vsl = new VexScrollingList(list_x,list_y,list_w,list_h,mh);
            int i=0;
            for(int mid: systemid) vsl = writeMail(systemmail.get(mid),vsl,i++);
            for(int mid: playerid) vsl = writeMail(playermail.get(mid),vsl,i++);
            for(int mid: permissionid) vsl = writeMail(permissionmail.get(mid),vsl,i++);
            return vsl;
        }
    }
    
    // 向列表中写邮件
    private VexScrollingList writeMail(TextMail tm, VexScrollingList vsl, int i){
        String type = tm.getType();
        String icon_image = mail_icon_image_2;
        boolean file = (tm instanceof FileMail);
        // 邮件主题
        String t = tm.getTopic();
        if(file) {
            t = mail_topic_noFile + t;
            icon_image = mail_icon_image_1;
        }else{
            t = mail_topic_noRead + t;
        }
        if(t.length()>mail_topic_div){
            t = t.substring(0, mail_topic_div-2)+"...";
        }
        vsl.addComponent(new VexText(mail_topic_x,mail_y_offset*i+mail_topic_y,Arrays.asList(t),mail_topic_size));
        // 邮件发送时间
        if(mail_date_display.contains(type)) vsl.addComponent(new VexText(mail_date_x,mail_y_offset*i+mail_date_y,Arrays.asList(mail_date_prefix+tm.getDate()),mail_date_size));
        // 发件人
        if(mail_sender_display.contains(type)) vsl.addComponent(new VexText(mail_sender_x,mail_y_offset*i+mail_sender_y,Arrays.asList(mail_sender_prefix+tm.getSender()),mail_sender_size));
        // 邮件类型
        if(mail_type_display.contains(type)) vsl.addComponent(new VexText(mail_type_x,mail_y_offset*i+mail_type_y,Arrays.asList(mail_type_prefix+tm.getTypeName()),mail_type_size));
        // 图标
        if(file && mail_icon_display.contains(type)) vsl.addComponent(new VexImage(icon_image,mail_icon_x,mail_y_offset*i+mail_icon_y,mail_icon_w,mail_icon_h));
        // 查看邮件按钮
        vsl.addComponent(new VexButton(mail_button_id+"_"+i,"",mail_button_image_1,mail_button_image_2,mail_button_x,mail_y_offset*i+mail_button_y,mail_button_w,mail_button_h,player -> {
            if(tm.getType().equals("player") && MailBoxAPI.isExpired(tm)){
                player.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件已过期，自动删除");
                if(tm.Delete(player)) player.closeInventory();
            }else{
                try {
                    openMailContentGui(player, tm, null, asSender);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }));
        return vsl;
    }
    
    public static void openMailBoxGui(Player p, String playertype){
        if(p.hasPermission("mailbox.gui.mailbox")){
            VexViewAPI.openGui(p, new MailBoxGui(p, playertype));
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限打开此GUI");
        }
    }
    
}
