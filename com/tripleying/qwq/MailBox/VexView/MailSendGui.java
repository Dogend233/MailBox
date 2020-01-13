package com.tripleying.qwq.MailBox.VexView;

import com.tripleying.qwq.MailBox.Mail.BaseMail;
import com.tripleying.qwq.MailBox.Mail.BaseFileMail;
import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Utils.DateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.VexInventoryGui;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexCheckBox;
import lk.vexview.gui.components.VexImage;
import lk.vexview.gui.components.VexSlot;
import lk.vexview.gui.components.VexText;
import lk.vexview.gui.components.VexTextField;
import org.bukkit.Material;
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
    private static VexText text_topic;
    private static VexText text_text;
    private static VexText text_recipient;
    private static VexText text_permission;
    private static VexText text_startdate;
    private static VexText text_deadline;
    private static VexText text_times;
    private static VexText text_onlyCDK;
    private static VexText text_template;
    private static VexText text_sender;
    private static VexText text_command;
    private static VexText text_description;
    private static VexText text_item;
    private static final HashMap<String, int[]> FIELD = new  HashMap();
    private static int[] checkBox_onlyCDK;
    private static String[] image_checkBox_onlyCDK;
    private static List<Integer> slot_x;
    private static List<Integer> slot_y;
    private static final List<VexImage> SLOT_IMAGE = new ArrayList();
    private static VexImage image_coin;
    private static VexImage image_point;
    
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
    
    public MailSendGui(Player p, String type) {
        super(gui_img,gui_x,gui_y,gui_w,gui_h,gui_ww,gui_hh,gui_ix,gui_iy);
        this.setClosable(false);
        this.p = p;
        sender = p.getName();
        enVault = GlobalConfig.enVault;
        enPlayerPoints = GlobalConfig.enPlayerPoints;
        this.type = type;
        perm_sender = p.hasPermission("mailbox.admin.send.sender");
        perm_cmd = p.hasPermission("mailbox.admin.send.command");
        perm_coin = MailBoxAPI.hasPlayerPermission(p, "mailbox.send.money.coin");
        perm_point = MailBoxAPI.hasPlayerPermission(p, "mailbox.send.money.point");
        perm_item = MailBoxAPI.playerSendItemAllow(p);
        this.addComponent(button_return);
        VexButton vbp = new VexButton(button_preview[0],button_preview[1],button_preview[2],button_preview[3],Integer.parseInt(button_preview[4]),Integer.parseInt(button_preview[5]),Integer.parseInt(button_preview[6]),Integer.parseInt(button_preview[7]),player -> previewMail(player));
        if(!GlobalConfig.lowVexView_2_4 && !button_preview_hover.isEmpty()) VexViewConfig.setHover(vbp, button_preview_hover);
        this.addComponent(vbp);
        this.addComponent(text_topic);
        this.addComponent(getTextField(FIELD.get("topic")));
        this.addComponent(text_text);
        this.addComponent(getTextField(FIELD.get("text")));
        if(perm_sender){
            this.addComponent(text_sender);
            this.addComponent(getTextField(FIELD.get("sender"), sender));
        }
        if(perm_cmd){
            this.addComponent(text_command);
            this.addComponent(getTextField(FIELD.get("command")));
            this.addComponent(text_description);
            this.addComponent(getTextField(FIELD.get("description")));
        }
        if(perm_item!=0) this.addComponent(text_item);
        for(int i=0;i<perm_item;i++){
            this.addComponent(new VexSlot(i,slot_x.get(i),slot_y.get(i),null));
            this.addComponent(SLOT_IMAGE.get(i));
        }
        switch (type) {
            case "player" :
                this.addComponent(text_recipient);
                this.addComponent(getTextField(FIELD.get("recipient")));
                break;
            case "permission":
                this.addComponent(text_permission);
                this.addComponent(getTextField(FIELD.get("permission")));
                break;
            case "date":
                this.addComponent(text_startdate);
                this.addComponent(getTextField(FIELD.get("startdate"),"0"));
                this.addComponent(text_deadline);
                this.addComponent(getTextField(FIELD.get("deadline"),"0"));
                break;
            case "times":
                this.addComponent(text_times);
                this.addComponent(getTextField(FIELD.get("times"),"1"));
                break;
            case "cdkey":
                this.addComponent(text_onlyCDK);
                this.addComponent(getCheckBox(checkBox_onlyCDK,image_checkBox_onlyCDK));
                break;
            case "template":
                this.addComponent(text_template);
                this.addComponent(getTextField(FIELD.get("template")));
                break;
        }
        if(enVault && perm_coin){
            bal_coin = MailBoxAPI.getEconomyBalance(p);
            this.addComponent(image_coin);
            VexTextField vtf = getTextField(FIELD.get("coin"),"0");
            if(!GlobalConfig.lowVexView_2_4) VexViewConfig.setHover(vtf, Arrays.asList("余额："+bal_coin));
            this.addComponent(vtf);
        }
        if(enPlayerPoints && perm_point){
            bal_point = MailBoxAPI.getPoints(p);
            this.addComponent(image_point);
            VexTextField vtf = getTextField(FIELD.get("point"),"0");
            if(!GlobalConfig.lowVexView_2_4) VexViewConfig.setHover(vtf, Arrays.asList("余额："+bal_point));
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
        int field_topic_x,
        int field_topic_y,
        int field_topic_w,
        int field_topic_h,
        int field_topic_max,
        int field_recipient_x,
        int field_recipient_y,
        int field_recipient_w,
        int field_recipient_h,
        int field_recipient_max,
        int field_permission_x,
        int field_permission_y,
        int field_permission_w,
        int field_permission_h,
        int field_permission_max,
        int field_startdate_x,
        int field_startdate_y,
        int field_startdate_w,
        int field_startdate_h,
        int field_startdate_max,
        int field_deadline_x,
        int field_deadline_y,
        int field_deadline_w,
        int field_deadline_h,
        int field_deadline_max,
        int field_times_x,
        int field_times_y,
        int field_times_w,
        int field_times_h,
        int field_times_max,
        int field_template_x,
        int field_template_y,
        int field_template_w,
        int field_template_h,
        int field_template_max,
        int field_text_x,
        int field_text_y,
        int field_text_w,
        int field_text_h,
        int field_text_max,
        int field_sender_x,
        int field_sender_y,
        int field_sender_w,
        int field_sender_h,
        int field_sender_max,
        int field_command_x,
        int field_command_y,
        int field_command_w,
        int field_command_h,
        int field_command_max,
        int field_description_x,
        int field_description_y,
        int field_description_w,
        int field_description_h,
        int field_description_max,
        int field_coin_x,
        int field_coin_y,
        int field_coin_w,
        int field_coin_h,
        int field_coin_max,
        int field_point_x,
        int field_point_y,
        int field_point_w,
        int field_point_h,
        int field_point_max,
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
        List<Integer> slot_y
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
        if(!GlobalConfig.lowVexView_2_4 && !button_return_hover.isEmpty()) VexViewConfig.setHover(button_return, button_return_hover);
        // 预览按钮
        button_preview = new String[]{button_preview_id,button_preview_text,button_preview_img_1,button_preview_img_2,Integer.toString(button_preview_x),Integer.toString(button_preview_y),Integer.toString(button_preview_w),Integer.toString(button_preview_h)};
        MailSendGui.button_preview_hover = button_preview_hover;
        // 提示文字
        text_topic = new VexText(text_topic_x,text_topic_y,Arrays.asList(text_topic_text),text_topic_size);
        text_recipient = new VexText(text_recipient_x,text_recipient_y,Arrays.asList(text_recipient_text),text_recipient_size);
        text_permission = new VexText(text_permission_x,text_permission_y,Arrays.asList(text_permission_text),text_permission_size);
        text_startdate = new VexText(text_startdate_x,text_startdate_y,Arrays.asList(text_startdate_text),text_startdate_size);
        text_deadline = new VexText(text_deadline_x,text_deadline_y,Arrays.asList(text_deadline_text),text_deadline_size);
        text_times = new VexText(text_times_x,text_times_y,Arrays.asList(text_times_text),text_times_size);
        text_onlyCDK = new VexText(text_onlyCDK_x,text_onlyCDK_y,Arrays.asList(text_onlyCDK_text),text_onlyCDK_size);
        text_template = new VexText(text_template_x,text_template_y,Arrays.asList(text_template_text),text_template_size);
        text_text = new VexText(text_text_x,text_text_y,Arrays.asList(text_text_text),text_text_size);
        text_sender = new VexText(text_sender_x,text_sender_y,Arrays.asList(text_sender_text),text_sender_size);
        text_command = new VexText(text_command_x,text_command_y,Arrays.asList(text_command_text),text_command_size);
        text_description = new VexText(text_description_x,text_description_y,Arrays.asList(text_description_text),text_description_size);
        text_item = new VexText(text_item_x,text_item_y,Arrays.asList(text_item_text),text_item_size);
        // 文本框
        FIELD.clear();
        FIELD.put("topic", new int[]{field_topic_x,field_topic_y,field_topic_w,field_topic_h,field_topic_max,1});
        FIELD.put("recipient", new int[]{field_recipient_x,field_recipient_y,field_recipient_w,field_recipient_h,field_recipient_max,2});
        FIELD.put("permission", new int[]{field_permission_x,field_permission_y,field_permission_w,field_permission_h,field_permission_max,3});
        FIELD.put("text", new int[]{field_text_x,field_text_y,field_text_w,field_text_h,field_text_max,4});
        FIELD.put("command", new int[]{field_command_x,field_command_y,field_command_w,field_command_h,field_command_max,5});
        FIELD.put("description", new int[]{field_description_x,field_description_y,field_description_w,field_description_h,field_description_max,6});
        FIELD.put("coin", new int[]{field_coin_x,field_coin_y,field_coin_w,field_coin_h,field_coin_max,7});
        FIELD.put("point", new int[]{field_point_x,field_point_y,field_point_w,field_point_h,field_point_max,8});
        FIELD.put("startdate", new int[]{field_startdate_x,field_startdate_y,field_startdate_w,field_startdate_h,field_startdate_max,9});
        FIELD.put("deadline", new int[]{field_deadline_x,field_deadline_y,field_deadline_w,field_deadline_h,field_deadline_max,10});
        FIELD.put("template", new int[]{field_template_x,field_template_y,field_template_w,field_template_h,field_template_max,11});
        FIELD.put("times", new int[]{field_times_x,field_times_y,field_times_w,field_times_h,field_times_max,12});
        FIELD.put("sender", new int[]{field_sender_x,field_sender_y,field_sender_w,field_sender_h,field_sender_max,13});
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
        for(int i=0;i<GlobalConfig.maxItem;i++){
            int x = slot_x.get(i);
            int y = slot_y.get(i);
            SLOT_IMAGE.add(new VexImage(slot_img,x+x_offset,y+y_offset,slot_w,slot_h));
        }
    }
    
    // 获取文本框
    private VexTextField getTextField(int[] f){
        return new VexTextField(f[0],f[1],f[2],f[3],f[4],f[5]);
    }
    private VexTextField getTextField(int[] f, String v){
        return new VexTextField(f[0],f[1],f[2],f[3],f[4],f[5],v);
    }
    
    // 获取勾选框
    private VexCheckBox getCheckBox(int[] i, String[] s){
        return new VexCheckBox(i[0],s[0],s[1],i[1],i[2],i[3],i[4],false);
        
    }
    
    // 预览邮件
    private void previewMail(Player p){
        topic = getTextField(FIELD.get("topic")[5]).getTypedText();
        text = getTextField(FIELD.get("text")[5]).getTypedText();
        if(perm_sender) sender = getTextField(FIELD.get("sender")[5]).getTypedText();
        if(enVault && perm_coin){
            String t = getTextField(FIELD.get("coin")[5]).getTypedText();
            if(t!=null && !t.equals("")){
                try{
                    co = Double.parseDouble(t);
                }catch(NumberFormatException e){
                    p.sendMessage(GlobalConfig.warning+"[邮件预览]："+GlobalConfig.vaultDisplay+GlobalConfig.warning+"输入格式错误");
                    return;
                }
                if(co>bal_coin && !p.hasPermission("mailbox.admin.send.check.coin")){
                    p.sendMessage(GlobalConfig.warning+"[邮件预览]："+GlobalConfig.vaultDisplay+GlobalConfig.warning+"余额不足");
                    return;
                }else if(co>GlobalConfig.vaultMax && !p.hasPermission("mailbox.admin.send.check.coin")){
                    p.sendMessage(GlobalConfig.warning+"[邮件预览]："+GlobalConfig.vaultDisplay+GlobalConfig.warning+"超出最大发送限制");
                    return;
                }
            }
        }
        if(enPlayerPoints && perm_point){
            String t = getTextField(FIELD.get("point")[5]).getTypedText().trim();
            if(t!=null && !t.equals("")){
                try{
                    po = Integer.parseInt(t);
                }catch(NumberFormatException e){
                    p.sendMessage(GlobalConfig.warning+"[邮件预览]："+GlobalConfig.playerPointsDisplay+GlobalConfig.warning+"输入格式错误");
                    return;
                }
                if(po>bal_point && !p.hasPermission("mailbox.admin.send.check.point")){
                    p.sendMessage(GlobalConfig.warning+"[邮件预览]："+GlobalConfig.playerPointsDisplay+GlobalConfig.warning+"余额不足");
                    return;
                }else if(po>GlobalConfig.playerPointsMax && !p.hasPermission("mailbox.admin.send.check.coin")){
                    p.sendMessage(GlobalConfig.warning+"[邮件预览]："+GlobalConfig.playerPointsDisplay+GlobalConfig.warning+"超出最大发送限制");
                    return;
                }
            }
        }
        switch (type) {
            case "player":
                String[] recipient = divide(getTextField(FIELD.get("recipient")[5]).getTypedText(), "recipient");
                if(recipient!=null) rl.addAll(Arrays.asList(recipient));
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
            case "times":
                try{
                    times = Integer.parseInt(getTextField(FIELD.get("times")[5]).getTypedText().trim());
                }catch(NumberFormatException e){
                    p.sendMessage(GlobalConfig.warning+"[邮件预览]：邮件数量格式不正确，请输入数字");
                    return;
                }
                break;
            case "cdkey":
                only = getCheckBoxById(checkBox_onlyCDK[0]).isChecked();
                break;
        }
        if(valid()){
            if(perm_cmd){
                String[] command = divide(getTextField(FIELD.get("command")[5]).getTypedText(), "command");
                String[] description = divide(getTextField(FIELD.get("description")[5]).getTypedText(), "description");
                if(command!=null) cl.addAll(Arrays.asList(command));
                if(description!=null) cd.addAll(Arrays.asList(description));
            }
            if(perm_item!=0){
                al = getItem(p);
            }
            if(al.isEmpty() && cl.isEmpty() && co==0 && po==0){
                BaseMail bm = MailBoxAPI.createBaseMail(type, 0, sender, rl, perm, topic.replaceAll("&", "§"), text.replaceAll("&", "§"), startdate, deadline, times, only, template);
                try{
                    MailContentGui.openMailContentGui(p, bm, this, false);
                }catch(Exception e){
                    p.sendMessage(GlobalConfig.warning+"[邮件预览]：打开预览界面失败");
                }
            }else{
                BaseFileMail fm = MailBoxAPI.createBaseFileMail(type, 0, sender, rl, perm, topic.replaceAll("&", "§"), text.replaceAll("&", "§"), startdate, deadline, times, only, template, "0", al, cl, cd, co, po);
                try{
                    MailContentGui.openMailContentGui(p, fm, this, false);
                }catch(Exception e){
                    p.sendMessage(GlobalConfig.warning+"[邮件预览]：打开预览界面失败");
                }
            }
        }
    }
    
    // 验证邮件主题、内容、收件人和权限
    private boolean valid(){
        if(topic.equals("")){
            p.sendMessage(GlobalConfig.warning+"[邮件预览]：主题不能为空");
            return false;
        }else{
            if(text.equals("")){
                p.sendMessage(GlobalConfig.warning+"[邮件预览]：内容不能为空");
                return false;
            }else{
                switch (type) {
                    case "cdkey":
                    case "online":
                    case "system":
                        return true;
                    case "times":
                        if(times<1){
                            p.sendMessage(GlobalConfig.warning+"[邮件预览]：邮件数量不能小于1");
                            return false;
                        }
                        if(times>GlobalConfig.times_count && !p.hasPermission("mailbox.admin.send.check.times")){
                            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件数量不能大于"+GlobalConfig.times_count);
                            return false;
                        }
                        return true;
                    case "template":
                        if(template==null || template.equals("")){
                            p.sendMessage(GlobalConfig.warning+"[邮件预览]：模板名不能为空");
                            return false;
                        }
                        return true;
                    case "permission":
                        if(perm==null || perm.equals("")){
                            p.sendMessage(GlobalConfig.warning+"[邮件预览]：权限不能为空");
                            return false;
                        }
                        return true;
                    case "date":
                        if(startdate==null || startdate.equals("")){
                            p.sendMessage(GlobalConfig.warning+"[邮件预览]：发送时间不能为空");
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
                                    p.sendMessage(GlobalConfig.warning+"[邮件预览]：请输入足够的时间参数，年 月 日 或 年 月 日 时 分 秒");
                                    return false;
                            }
                        }
                        if(deadline==null || deadline.equals("")){
                            p.sendMessage(GlobalConfig.warning+"[邮件预览]：截止时间不能为空");
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
                                p.sendMessage(GlobalConfig.warning+"[邮件预览]：请输入足够的时间参数，年 月 日 或 年 月 日 时 分 秒");
                                return false;
                        }
                    case "player":
                        if(rl.isEmpty()){
                            p.sendMessage(GlobalConfig.warning+"[邮件预览]：收件人不能为空");
                            return false;
                        }
                        if(rl.size()>1){
                            if(!p.hasPermission("mailbox.admin.send.multiplayer")){
                                p.sendMessage(GlobalConfig.warning+"[邮件预览]：您只能填写一位收件人");
                                return false;
                            }
                        }
                        if(p.hasPermission("mailbox.admin.send.me")) ;
                        else{
                            if(rl.contains(p.getName())){
                                p.sendMessage(GlobalConfig.warning+"[邮件预览]：收件人不能是自己");
                                return false;
                            }else{
                                // 有卡顿
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
                            }
                        }
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
            if(t.getType()!=null && t.getType()!=Material.AIR){
                if(skip || MailBoxAPI.isAllowSend(t)){
                    isl.add(t);
                }else{
                    p.sendMessage(GlobalConfig.warning+"第"+(i+1)+"个物品无法作为邮件发送，已忽略");
                }
            }
        }
        return isl;
    }
    
    // 打开发送邮件GUI
    public static void openMailSendGui(Player p, String type, VexGui gui) {
        if(type.equals("player")){
            int out = MailBoxAPI.playerAsSenderAllow(p);
            int outed = MailBoxAPI.playerAsSender(p);
            if((out-outed)<=0){
                p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你的"+GlobalConfig.getTypeName(type)+"邮件发送数量达到上限");
                return;
            }
        }
        if(gui==null) gui = new MailSendGui(p, type);
        VexViewAPI.openGui(p, gui);
    }
}
