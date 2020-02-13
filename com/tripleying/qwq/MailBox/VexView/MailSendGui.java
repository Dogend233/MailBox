package com.tripleying.qwq.MailBox.VexView;

import com.tripleying.qwq.MailBox.Mail.*;
import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Message;
import com.tripleying.qwq.MailBox.Utils.DateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.VexInventoryGui;
import lk.vexview.gui.components.*;
import lk.vexview.gui.components.expand.VexColorfulTextField;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MailSendGui extends VexInventoryGui{

    private static String gui_img;
    private static int gui_x;
    private static int gui_y;
    private static int gui_w;
    private static int gui_h;
    private static int gui_ww;
    private static int gui_hh;
    private static int gui_ix;
    private static int gui_iy;
    private static VexButton button_return;
    private static String[] button_preview;
    private static List<String> button_preview_hover;
    private static String PreviewError;
    private static String ItemBan;
    private static VexText text_topic;
    private static VexText text_text;
    private static VexText text_recipient;
    private static VexText text_permission;
    private static VexText text_startdate;
    private static VexText text_deadline;
    private static VexText text_times;
    private static VexText text_key;
    private static VexText text_onlyCDK;
    private static VexText text_template;
    private static VexText text_sender;
    private static VexText text_command;
    private static VexText text_description;
    private static VexText text_item;
    public static final HashMap<String, int[]> FIELD = new  HashMap();
    public static int[] checkBox_onlyCDK;
    private static String[] image_checkBox_onlyCDK;
    private static List<Integer> slot_x;
    private static List<Integer> slot_y;
    private static final List<VexImage> SLOT_IMAGE = new ArrayList();
    private static VexImage image_coin;
    private static VexImage image_point;
    
    private BaseMail basem = null;
    private boolean enVault;
    private boolean enPlayerPoints;
    private Player p = null;
    private double bal_coin = 0;
    private int bal_point = 0;
    private String type;
    private String topic = null;
    private String text = null;
    private String sender = null;
    private List<String> rl = new ArrayList();
    private String perm = null;
    private String startdate = null;
    private String deadline = null;
    private int times = 1;
    private String key = "";
    private boolean only = false;
    private String template = null;
    private List<String> cl = new ArrayList();
    private List<String> cd = new ArrayList();
    private ArrayList<ItemStack> al = new ArrayList();
    private double co = 0;
    private int po = 0;
    private boolean perm_sender = false;
    private boolean perm_cmd = false;
    private boolean perm_coin = false;
    private boolean perm_point = false;
    private int perm_item = 0;
    
    public MailSendGui(Player p, BaseMail bm){
        this(p, bm.getType());
        basem = bm;
    }
    
    public MailSendGui(Player p, String type) {
        super(gui_img,gui_x,gui_y,gui_w,gui_h,gui_ww,gui_hh,gui_ix,gui_iy);
        this.setClosable(false);
        this.p = p;
        this.type = type;
        sender = p.getName();
        enVault = GlobalConfig.enVault;
        enPlayerPoints = GlobalConfig.enPlayerPoints;
        perm_sender = p.hasPermission("mailbox.admin.send.sender");
        perm_cmd = p.hasPermission("mailbox.admin.send.command");
        perm_coin = MailBoxAPI.hasPlayerPermission(p, "mailbox.send.money.coin");
        perm_point = MailBoxAPI.hasPlayerPermission(p, "mailbox.send.money.point");
        perm_item = MailBoxAPI.playerSendItemAllow(p);
        this.addComponent(button_return);
        VexButton vbp = new VexButton(button_preview[0],button_preview[1],button_preview[2],button_preview[3],Integer.parseInt(button_preview[4]),Integer.parseInt(button_preview[5]),Integer.parseInt(button_preview[6]),Integer.parseInt(button_preview[7]),player -> previewMail(player));
        if(!button_preview_hover.isEmpty()) VexViewConfig.setHover(vbp, button_preview_hover);
        this.addComponent(vbp);
        this.addComponent(text_topic);
        this.addComponent(createTextField(FIELD.get("topic")));
        this.addComponent(text_text);
        if(GlobalConfig.vexview_under_2_6_3){
            this.addComponent(createTextField(FIELD.get("text")));
        }else{
            this.addComponent(createTextArea(FIELD.get("text")));
        }
        if(perm_sender){
            this.addComponent(text_sender);
            this.addComponent(createTextField(FIELD.get("sender"), sender));
        }
        if(perm_cmd){
            this.addComponent(text_command);
            this.addComponent(text_description);
            if(GlobalConfig.vexview_under_2_6_3){
                this.addComponent(createTextField(FIELD.get("command")));
                this.addComponent(createTextField(FIELD.get("description")));
            }else{
                this.addComponent(createTextArea(FIELD.get("command")));
                this.addComponent(createTextArea(FIELD.get("description")));
            }
        }
        if(perm_item!=0) this.addComponent(text_item);
        for(int i=0;i<perm_item;i++){
            this.addComponent(new VexSlot(i,slot_x.get(i),slot_y.get(i),null));
            this.addComponent(SLOT_IMAGE.get(i));
        }
        switch (type) {
            case "player" :
                this.addComponent(text_recipient);
                if(GlobalConfig.vexview_under_2_6_3){
                    this.addComponent(createTextField(FIELD.get("recipient")));
                }else{
                    this.addComponent(createTextArea(FIELD.get("recipient")));
                }
                break;
            case "permission":
                this.addComponent(text_permission);
                this.addComponent(createTextField(FIELD.get("permission")));
                break;
            case "date":
                this.addComponent(text_startdate);
                this.addComponent(createTextField(FIELD.get("startdate"),"0"));
                this.addComponent(text_deadline);
                this.addComponent(createTextField(FIELD.get("deadline"),"0"));
                break;
            case "keytimes":
                this.addComponent(text_key);
                this.addComponent(createTextField(FIELD.get("key")));
            case "times":
                this.addComponent(text_times);
                this.addComponent(createTextField(FIELD.get("times"),"1"));
                break;
            case "cdkey":
                this.addComponent(text_onlyCDK);
                this.addComponent(createCheckBox(checkBox_onlyCDK,image_checkBox_onlyCDK));
                break;
            case "template":
                this.addComponent(text_template);
                this.addComponent(createTextField(FIELD.get("template")));
                break;
        }
        if(enVault && perm_coin){
            bal_coin = MailBoxAPI.getEconomyBalance(p);
            this.addComponent(image_coin);
            VexTextField vtf = createTextField(FIELD.get("coin"),"0");
            VexViewConfig.setHover(vtf, Arrays.asList(Message.moneyBalance+"："+bal_coin));
            this.addComponent(vtf);
        }
        if(enPlayerPoints && perm_point){
            bal_point = MailBoxAPI.getPoints(p);
            this.addComponent(image_point);
            VexTextField vtf = createTextField(FIELD.get("point"),"0");
            VexViewConfig.setHover(vtf, Arrays.asList(Message.moneyBalance+"："+bal_point));
            this.addComponent(vtf);
        }
    }
    
    public static void setSendConfig(
        String gui_img,
        int gui_x,
        int gui_y,
        int gui_w,
        int gui_h,
        int gui_ww,
        int gui_hh,
        int gui_ix,
        int gui_iy,
        String button_return_id,
        String button_return_text,
        List<String> button_return_hover,
        String button_return_img_1,
        String button_return_img_2,
        int button_return_x,
        int button_return_y,
        int button_return_w,
        int button_return_h,
        String button_preview_id,
        String button_preview_text,
        List<String> button_preview_hover,
        String button_preview_img_1,
        String button_preview_img_2,
        int button_preview_x,
        int button_preview_y,
        int button_preview_w,
        int button_preview_h,
        String button_preview_error,
        String button_preview_itemBan,
        int text_topic_x,
        int text_topic_y,
        double text_topic_size,
        String text_topic_text,
        int text_recipient_x,
        int text_recipient_y,
        double text_recipient_size,
        String text_recipient_text,
        int text_permission_x,
        int text_permission_y,
        double text_permission_size,
        String text_permission_text,
        int text_startdate_x,
        int text_startdate_y,
        double text_startdate_size,
        String text_startdate_text,
        int text_deadline_x,
        int text_deadline_y,
        double text_deadline_size,
        String text_deadline_text,
        int text_times_x,
        int text_times_y,
        double text_times_size,
        String text_times_text,
        int text_key_x,
        int text_key_y,
        double text_key_size,
        String text_key_text,
        int text_onlyCDK_x,
        int text_onlyCDK_y,
        double text_onlyCDK_size,
        String text_onlyCDK_text,
        int text_template_x,
        int text_template_y,
        double text_template_size,
        String text_template_text,
        int text_text_x,
        int text_text_y,
        double text_text_size,
        String text_text_text,
        int text_sender_x,
        int text_sender_y,
        double text_sender_size,
        String text_sender_text,
        int text_command_x,
        int text_command_y,
        double text_command_size,
        String text_command_text,
        int text_description_x,
        int text_description_y,
        double text_description_size,
        String text_description_text,
        int text_item_x,
        int text_item_y,
        double text_item_size,
        String text_item_text,
        int checkBox_onlyCDK_x,
        int checkBox_onlyCDK_y,
        int checkBox_onlyCDK_w,
        int checkBox_onlyCDK_h,
        String checkBox_onlyCDK_image1,
        String checkBox_onlyCDK_image2,
        String image_coin_url,
        int image_coin_x,
        int image_coin_y,
        int image_coin_w,
        int image_coin_h,
        String image_point_url,
        int image_point_x,
        int image_point_y,
        int image_point_w,
        int image_point_h,
        String slot_img,
        int slot_w,
        int slot_h,
        List<Integer> slot_x,
        List<Integer> slot_y,
        YamlConfiguration send
    ){
        // GUI
        MailSendGui.gui_img = gui_img;
        MailSendGui.gui_x = gui_x;
        MailSendGui.gui_y = gui_y;
        MailSendGui.gui_w = gui_w;
        MailSendGui.gui_h = gui_h;
        MailSendGui.gui_ww = gui_ww;
        MailSendGui.gui_hh = gui_hh;
        MailSendGui.gui_ix = gui_ix;
        MailSendGui.gui_iy = gui_iy;
        // 返回按钮
        button_return = new VexButton(button_return_id,button_return_text,button_return_img_1,button_return_img_2,button_return_x,button_return_y,button_return_w,button_return_h, player -> MailBoxGui.openMailBoxGui(player, "Recipient"));
        if(!button_return_hover.isEmpty()) VexViewConfig.setHover(button_return, button_return_hover);
        // 预览按钮
        button_preview = new String[]{button_preview_id,button_preview_text,button_preview_img_1,button_preview_img_2,Integer.toString(button_preview_x),Integer.toString(button_preview_y),Integer.toString(button_preview_w),Integer.toString(button_preview_h)};
        MailSendGui.button_preview_hover = button_preview_hover;
        PreviewError = button_preview_error;
        ItemBan = button_preview_itemBan;
        // 提示文字
        text_topic = new VexText(text_topic_x,text_topic_y,Arrays.asList(text_topic_text),text_topic_size);
        text_recipient = new VexText(text_recipient_x,text_recipient_y,Arrays.asList(text_recipient_text),text_recipient_size);
        text_permission = new VexText(text_permission_x,text_permission_y,Arrays.asList(text_permission_text),text_permission_size);
        text_startdate = new VexText(text_startdate_x,text_startdate_y,Arrays.asList(text_startdate_text),text_startdate_size);
        text_deadline = new VexText(text_deadline_x,text_deadline_y,Arrays.asList(text_deadline_text),text_deadline_size);
        text_times = new VexText(text_times_x,text_times_y,Arrays.asList(text_times_text),text_times_size);
        text_key = new VexText(text_key_x,text_key_y,Arrays.asList(text_key_text),text_key_size);
        text_onlyCDK = new VexText(text_onlyCDK_x,text_onlyCDK_y,Arrays.asList(text_onlyCDK_text),text_onlyCDK_size);
        text_template = new VexText(text_template_x,text_template_y,Arrays.asList(text_template_text),text_template_size);
        text_text = new VexText(text_text_x,text_text_y,Arrays.asList(text_text_text),text_text_size);
        text_sender = new VexText(text_sender_x,text_sender_y,Arrays.asList(text_sender_text),text_sender_size);
        text_command = new VexText(text_command_x,text_command_y,Arrays.asList(text_command_text),text_command_size);
        text_description = new VexText(text_description_x,text_description_y,Arrays.asList(text_description_text),text_description_size);
        text_item = new VexText(text_item_x,text_item_y,Arrays.asList(text_item_text),text_item_size);
        // 文本框
        FIELD.clear();
        FIELD.put("topic", new int[]{send.getInt("field.topic.x"),send.getInt("field.topic.y"),send.getInt("field.topic.w"),send.getInt("field.topic.h"),send.getInt("field.topic.max"),1,send.getInt("field.topic.main",0xFF000000),send.getInt("field.topic.side",0xFFFFFFFF)});
        FIELD.put("recipient", new int[]{send.getInt("field.recipient.x"),send.getInt("field.recipient.y"),send.getInt("field.recipient.w"),send.getInt("field.recipient.h"),send.getInt("field.recipient.max"),2,send.getInt("field.recipient.main",0xFF000000),send.getInt("field.recipient.side",0xFFFFFFFF)});
        FIELD.put("permission",new int[]{send.getInt("field.permission.x"),send.getInt("field.permission.y"),send.getInt("field.permission.w"),send.getInt("field.permission.h"),send.getInt("field.permission.max"),3,send.getInt("field.permission.main",0xFF000000),send.getInt("field.permission.side",0xFFFFFFFF)});
        FIELD.put("text", new int[]{send.getInt("field.text.x"),send.getInt("field.text.y"),send.getInt("field.text.w"),send.getInt("field.text.h"),send.getInt("field.text.max"),4,send.getInt("field.text.main",0xFF000000),send.getInt("field.text.side",0xFFFFFFFF)});
        FIELD.put("command", new int[]{send.getInt("field.command.x"),send.getInt("field.command.y"),send.getInt("field.command.w"),send.getInt("field.command.h"),send.getInt("field.command.max"),5,send.getInt("field.command.main",0xFF000000),send.getInt("field.command.side",0xFFFFFFFF)});
        FIELD.put("description",new int[]{send.getInt("field.description.x"),send.getInt("field.description.y"),send.getInt("field.description.w"),send.getInt("field.description.h"),send.getInt("field.description.max"),6,send.getInt("field.description.main",0xFF000000),send.getInt("field.description.side",0xFFFFFFFF)});
        FIELD.put("coin",new int[]{send.getInt("field.coin.x"),send.getInt("field.coin.y"),send.getInt("field.coin.w"),send.getInt("field.coin.h"),send.getInt("field.coin.max"),7,send.getInt("field.coin.main",0xFF000000),send.getInt("field.coin.side",0xFFFFFFFF)});
        FIELD.put("point",new int[]{send.getInt("field.point.x"),send.getInt("field.point.y"),send.getInt("field.point.w"),send.getInt("field.point.h"),send.getInt("field.point.max"),8,send.getInt("field.point.main",0xFF000000),send.getInt("field.point.side",0xFFFFFFFF)});
        FIELD.put("startdate",new int[]{send.getInt("field.startdate.x"),send.getInt("field.startdate.y"),send.getInt("field.startdate.w"),send.getInt("field.startdate.h"),send.getInt("field.startdate.max"),9,send.getInt("field.startdate.main",0xFF000000),send.getInt("field.startdate.side",0xFFFFFFFF)});
        FIELD.put("deadline", new int[]{send.getInt("field.deadline.x"),send.getInt("field.deadline.y"),send.getInt("field.deadline.w"),send.getInt("field.deadline.h"),send.getInt("field.deadline.max"),10,send.getInt("field.deadline.main",0xFF000000),send.getInt("field.deadline.side",0xFFFFFFFF)});
        FIELD.put("template",new int[]{send.getInt("field.template.x"),send.getInt("field.template.y"),send.getInt("field.template.w"),send.getInt("field.template.h"),send.getInt("field.template.max"),11,send.getInt("field.template.main",0xFF000000),send.getInt("field.template.side",0xFFFFFFFF)});
        FIELD.put("times", new int[]{send.getInt("field.times.x"),send.getInt("field.times.y"),send.getInt("field.times.w"),send.getInt("field.times.h"),send.getInt("field.times.max"),12,send.getInt("field.times.main",0xFF000000),send.getInt("field.times.side",0xFFFFFFFF)});
        FIELD.put("sender",new int[]{send.getInt("field.sender.x"),send.getInt("field.sender.y"),send.getInt("field.sender.w"),send.getInt("field.sender.h"),send.getInt("field.sender.max"),13,send.getInt("field.sender.main",0xFF000000),send.getInt("field.sender.side",0xFFFFFFFF)});
        FIELD.put("key", new int[]{send.getInt("field.key.x"),send.getInt("field.key.y"),send.getInt("field.key.w"),send.getInt("field.key.h"),send.getInt("field.key.max"),14,send.getInt("field.key.main",0xFF000000),send.getInt("field.key.side",0xFFFFFFFF)});
        // 勾选框
        checkBox_onlyCDK = new int[]{0,checkBox_onlyCDK_x,checkBox_onlyCDK_y,checkBox_onlyCDK_w,checkBox_onlyCDK_h};
        // 勾选框图片
        image_checkBox_onlyCDK = new String[]{checkBox_onlyCDK_image1,checkBox_onlyCDK_image2};
        // [Vault]提示图
        image_coin = new VexImage(image_coin_url,image_coin_x,image_coin_y,image_coin_w,image_coin_h);
        // [PlayerPoints]提示图
        image_point = new VexImage(image_point_url,image_point_x,image_point_y,image_point_w,image_point_h);
        // 物品槽
        MailSendGui.slot_x = slot_x;
        MailSendGui.slot_y = slot_y;
        int x_offset = ((slot_w-18)/2)-1; 
        int y_offset = ((slot_h-18)/2)-1;
        SLOT_IMAGE.clear();
        while(slot_x.size()<GlobalConfig.maxItem) slot_x.add(-1000);
        while(slot_y.size()<GlobalConfig.maxItem) slot_y.add(0);
        for(int i=0;i<GlobalConfig.maxItem;i++){
            int x = slot_x.get(i);
            int y = slot_y.get(i);
            SLOT_IMAGE.add(new VexImage(slot_img,x+x_offset,y+y_offset,slot_w,slot_h));
        }
    }
    
    // 获取文本框
    private static VexTextField createTextField(int[] f){
        return new VexColorfulTextField(f[0],f[1],f[2],f[3],f[4],f[5],f[7],f[6]);
    }
    private static VexTextField createTextField(int[] f, String v){
        return new VexColorfulTextField(f[0],f[1],f[2],f[3],f[4],f[5],f[7],f[6],v);
    }
    
    public static VexComponents createTextArea(int[] f){
        return VexView2_6.createTextArea(f);
    }
    
    // 获取勾选框
    private static VexCheckBox createCheckBox(int[] i, String[] s){
        return new VexCheckBox(i[0],s[0],s[1],i[1],i[2],i[3],i[4],false);
        
    }
    
    // 预览邮件
    private void previewMail(Player p){
        topic = getTextField(FIELD.get("topic")[5]).getTypedText();
        if(GlobalConfig.vexview_under_2_6_3){
            text = getTextField(FIELD.get("text")[5]).getTypedText();
            if(!p.hasPermission("mailbox.admin.send.percent")) text = text.replace("%", "");
        }else{
            text = "";
            List<String> textlist = getTextArea(FIELD.get("text")[5]).getTypedText();
            boolean percent = p.hasPermission("mailbox.admin.send.percent");
            if(!textlist.isEmpty()){
                textlist.stream().filter((r) -> (r.trim().length()>0)).forEachOrdered((r) -> {
                    r = r.trim();
                    if(!percent) r = r.replace("%", "");
                    if(r.length()>0){
                        text += " "+r;
                    }
                });
            }
            if(text.length()>0) text = text.substring(1);
            if(text.length()>255) text = text.substring(0, 255);
        }
        if(perm_sender) sender = getTextField(FIELD.get("sender")[5]).getTypedText();
        if(enVault && perm_coin){
            String t = getTextField(FIELD.get("coin")[5]).getTypedText();
            if(t!=null && !t.equals("")){
                try{
                    co = Double.parseDouble(t);
                    if(co<0) co=0;
                }catch(NumberFormatException e){
                    p.sendMessage(Message.globalNumberError);
                    return;
                }
                if(co>bal_coin && !p.hasPermission("mailbox.admin.send.check.coin")){
                    p.sendMessage(Message.moneyBalanceNotEnough.replace("%money%", Message.moneyVault).replace("%max%", Double.toString(MailBoxAPI.getEconomyBalance(p))));
                    return;
                }else if(co>GlobalConfig.vaultMax && !p.hasPermission("mailbox.admin.send.check.coin")){
                    p.sendMessage(Message.globalExceedMax.replace("%para%", Message.moneyVault).replace("%max%", Double.toString(GlobalConfig.vaultMax)));
                    return;
                }
            }
        }
        if(enPlayerPoints && perm_point){
            String t = getTextField(FIELD.get("point")[5]).getTypedText().trim();
            if(t!=null && !t.equals("")){
                try{
                    po = Integer.parseInt(t);
                    if(po<0) po=0;
                }catch(NumberFormatException e){
                    p.sendMessage(Message.globalNumberError);
                    return;
                }
                if(po>bal_point && !p.hasPermission("mailbox.admin.send.check.point")){
                    p.sendMessage(Message.moneyBalanceNotEnough.replace("%money%", Message.moneyPlayerpoints).replace("%max%", Double.toString(MailBoxAPI.getPoints(p))));
                    return;
                }else if(po>GlobalConfig.playerPointsMax && !p.hasPermission("mailbox.admin.send.check.coin")){
                    p.sendMessage(Message.globalExceedMax.replace("%para%", Message.moneyPlayerpoints).replace("%max%", Integer.toString(GlobalConfig.playerPointsMax)));
                    return;
                }
            }
        }
        switch (type) {
            case "player":
                if(GlobalConfig.vexview_under_2_6_3){
                    String[] recipient = divide(getTextField(FIELD.get("recipient")[5]).getTypedText(), "recipient");
                    rl.clear();
                    if(recipient!=null) rl.addAll(Arrays.asList(recipient));
                }else{
                    List<String> recipient = getTextArea(FIELD.get("recipient")[5]).getTypedText();
                    rl.clear();
                    if(!recipient.isEmpty()){
                        recipient.stream().filter((r) -> (r.trim().length()>0)).forEachOrdered((r) -> {
                            rl.add(r.trim());
                        });
                    }
                }
                break;
            case "permission":
                perm = getTextField(FIELD.get("permission")[5]).getTypedText();
                break;
            case "date":
                startdate = getTextField(FIELD.get("startdate")[5]).getTypedText();
                deadline = getTextField(FIELD.get("deadline")[5]).getTypedText();
                break;
            case "template":
                template = getTextField(FIELD.get("template")[5]).getTypedText();
                break;
            case "keytimes":
                key = getTextField(FIELD.get("key")[5]).getTypedText();
            case "times":
                try{
                    times = Integer.parseInt(getTextField(FIELD.get("times")[5]).getTypedText().trim());
                }catch(NumberFormatException e){
                    p.sendMessage(Message.globalNumberError);
                    return;
                }
                break;
            case "cdkey":
                only = getCheckBoxById(checkBox_onlyCDK[0]).isChecked();
                break;
        }
        if(valid()){
            if(perm_cmd){
                if(GlobalConfig.vexview_under_2_6_3){
                    String[] command = divide(getTextField(FIELD.get("command")[5]).getTypedText(), "command");
                    String[] description = divide(getTextField(FIELD.get("description")[5]).getTypedText(), "description");
                    cl.clear();
                    if(command!=null) cl.addAll(Arrays.asList(command));
                    cd.clear();
                    if(description!=null) cd.addAll(Arrays.asList(description));
                }else{
                    List<String> command = getTextArea(FIELD.get("command")[5]).getTypedText();
                    List<String> description = getTextArea(FIELD.get("description")[5]).getTypedText();
                    cl.clear();
                    cd.clear();
                    if(!command.isEmpty()){
                        command.stream().filter((r) -> (r.trim().length()>0)).forEachOrdered((r) -> {
                            r = r.trim();
                            if(r.startsWith("/")) r = r.substring(1);
                            cl.add(r);
                        });
                    }
                    if(!description.isEmpty()){
                        description.stream().filter((r) -> (r.trim().length()>0)).forEachOrdered((r) -> {
                            r = r.trim();
                            if(r.length()>0){
                                cd.add(r);
                            }
                        });
                    }
                }
            }
            if(perm_item!=0){
                al = getItem(p);
            }
            if(al.isEmpty() && cl.isEmpty() && co==0 && po==0){
                BaseMail bm = MailBoxAPI.createBaseMail(type, 0, sender, rl, perm, topic.replace("&", "§"), text.replace("&", "§"), startdate, deadline, times, key.replace("&", "§"), only, template);
                try{
                    MailContentGui.openMailContentGui(p, bm, false);
                }catch(Exception e){
                    p.sendMessage(PreviewError);
                }
            }else{
                BaseFileMail fm = MailBoxAPI.createBaseFileMail(type, 0, sender, rl, perm, topic.replace("&", "§"), text.replace("&", "§"), startdate, deadline, times, key.replace("&", "§"), only, template, "0", al, cl, cd, co, po);
                try{
                    MailContentGui.openMailContentGui(p, fm, false);
                }catch(Exception e){
                    p.sendMessage(PreviewError);
                }
            }
        }
    }
    
    // 验证邮件主题、内容、收件人和权限
    private boolean valid(){
        if(topic.equals("")){
            p.sendMessage(Message.globalEmptyField.replace("%para%", Message.globalTopic));
            return false;
        }else{
            if(text.equals("")){
                p.sendMessage(Message.globalEmptyField.replace("%para%", Message.globalContent));
                return false;
            }else{
                switch (type) {
                    case "cdkey":
                    case "online":
                    case "system":
                        return true;
                    case "keytimes":
                        if(key.equals("")){
                            p.sendMessage(Message.globalEmptyField.replace("%para%", Message.keytimesKey));
                            return false;
                        }
                    case "times":
                        if(times<1){
                            p.sendMessage(Message.timesSendZero);
                            return false;
                        }
                        if(times>GlobalConfig.timesCount && !p.hasPermission("mailbox.admin.send.check.times")){
                            p.sendMessage(Message.timesSendExceed.replace("%max%", Integer.toString(GlobalConfig.timesCount)));
                            return false;
                        }
                        return true;
                    case "template":
                        if(template==null || template.equals("")){
                            p.sendMessage(Message.globalEmptyField.replace("%para%", Message.templateTemplate));
                            return false;
                        }
                        return true;
                    case "permission":
                        if(perm==null || perm.equals("")){
                            p.sendMessage(Message.globalEmptyField.replace("%para%", Message.permissionPermission));
                            return false;
                        }
                        return true;
                    case "date":
                        if(startdate==null || startdate.equals("")){
                            p.sendMessage(Message.globalEmptyField.replace("%para%", Message.dateStart));
                            return false;
                        }
                        if(!startdate.equals("0")){
                            List<Integer> l = DateTime.toDate(startdate, p, null);
                            switch (l.size()) {
                                case 3:
                                case 6:
                                    String date = DateTime.toDate(l, p, null);
                                    if(date==null){
                                        return false;
                                    }else{
                                        startdate = date;
                                        break;
                                    }
                                default:
                                    p.sendMessage(Message.commandMailNewDateTime);
                                    return false;
                            }
                        }
                        if(deadline==null || deadline.equals("")){
                            p.sendMessage(Message.globalEmptyField.replace("%para%", Message.dateDeadline));
                            return false;
                        }
                        if(deadline.equals("0")){
                            return true;
                        }
                        List<Integer> l = DateTime.toDate(deadline, p, null);
                        switch (l.size()) {
                            case 3:
                            case 6:
                                String date = DateTime.toDate(l, p, null);
                                if(date==null){
                                    return false;
                                }else{
                                    deadline = date;
                                    return true;
                                }
                            default:
                                p.sendMessage(Message.commandMailNewDateTime);
                                return false;
                        }
                    case "player":
                        if(rl.isEmpty()){
                            p.sendMessage(Message.globalEmptyField.replace("%para%", Message.playerRecipient));
                            return false;
                        }
                        if(rl.size()>GlobalConfig.playerMultiplayer && !p.hasPermission("mailbox.admin.send.multiplayer")){
                            p.sendMessage(Message.playerRecipientMax.replace("%max%", Integer.toString(GlobalConfig.playerMultiplayer)));
                            return false;
                        }
                        if(!p.hasPermission("mailbox.admin.send.me") && rl.contains(p.getName())){
                            p.sendMessage(Message.playerSelfRecipient);
                            return false;
                        }
                        // 获取目标收件箱上限 有卡顿
                        /*if(Bukkit.getOfflinePlayer(name).hasPlayedBefore()){
                            /*目前无法实现此段
                            int in = MailBoxAPI.playerAsRecipientAllow(Bukkit.getOfflinePlayer(name).getPlayer(), player_in);
                            System.out.println(in);
                            int ined = MailBoxAPI.playerAsSender(Bukkit.getOfflinePlayer(name).getPlayer());
                            if((in-ined)<=0){
                                p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标玩家："+name+"的"+GlobalConfig.getTypeName(type)+"邮件收取数量达到上限");
                                return false;
                            }*/
                        /*}else{
                            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标玩家："+name+"不存在");
                            return false;
                        }*/
                        return true;
                    default:
                        return false;
                }
            }
        }
    }
    
    // 分割文字
    private String[] divide(String text, String type){
        if(text.equals("")){
            return null;
        }else{
            switch (type) {
                case "recipient":
                {
                    String[] result = text.split(" ");
                    return result;
                }
                case "command":
                {
                    if(text.indexOf("/")==0){
                        return text.substring(1).split("/");
                    }else{
                        return text.split("/");
                    }
                }
                case "description":
                {
                    text = text.replace("&", "§");
                    String[] result = text.split(" ");
                    return result;
                }
                default:
                    return null;
            }
        }
    }
    
    // 获取附件物品
    private ArrayList<ItemStack> getItem(Player p){
        ArrayList<ItemStack> isl = new ArrayList();
        boolean skip = p.hasPermission("mailbox.admin.send.check.ban");
        for(int i=0;i<perm_item;i++){
            ItemStack t = getSlotById(i).getItem();
            if(t!=null && t.getType()!=Material.AIR){
                if(skip || MailBoxAPI.isAllowSend(t)){
                    isl.add(t);
                }else{
                    p.sendMessage(ItemBan.replace("%num%", Integer.toString(i+1)));
                }
            }
        }
        return isl;
    }
    
    public BaseMail getMail(){
        return this.basem;
    }
    
    // 打开发送邮件GUI
    public static void openMailSendGui(Player p, String type) {
        if(type.equals("player")){
            int out = MailBoxAPI.playerAsSenderAllow(p);
            int outed = MailBoxAPI.playerAsSender(p);
            if((out-outed)<=0){
                p.sendMessage(Message.playerMailOutMax.replace("%type%",Message.getTypeName("player")));
                return;
            }
        }
        VexViewAPI.openGui(p, new MailSendGui(p, type));
    }
    
    public static void openMailSendGui(Player p, BaseMail bm){
        if(bm.getType().equals("player")){
            int out = MailBoxAPI.playerAsSenderAllow(p);
            int outed = MailBoxAPI.playerAsSender(p);
            if((out-outed)<=0){
                p.sendMessage(Message.playerMailOutMax.replace("%type%",Message.getTypeName("player")));
                return;
            }
        }
        VexViewAPI.openGui(p, new MailSendGui(p, bm));
    }
    
}
