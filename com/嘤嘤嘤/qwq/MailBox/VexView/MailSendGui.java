package com.嘤嘤嘤.qwq.MailBox.VexView;

import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxGui.openMailBoxGui;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailContentGui.openMailContentGui;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.OpenedVexGui;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.VexInventoryGui;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexHoverText;
import lk.vexview.gui.components.VexImage;
import lk.vexview.gui.components.VexSlot;
import lk.vexview.gui.components.VexText;
import lk.vexview.gui.components.VexTextField;
import static org.bukkit.Material.AIR;
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
    private static VexText text_recipient;
    private static VexText text_permission;
    private static VexText text_text;
    private static VexText text_command;
    private static VexText text_description;
    private static VexText text_item;
    private static HashMap<String, int[]> field = new  HashMap();
    private static List<Integer> slot_x;
    private static List<Integer> slot_y;
    private static List<VexImage> solt_image = new ArrayList();
    private static VexImage image_coin;
    private static VexImage image_point;
    
    private boolean enVault;
    private boolean enPlayerPoints;
    private double bal_coin = 0;
    private int bal_point = 0;
    private String type;
    private boolean perm_cmd = false;
    private boolean perm_coin = false;
    private boolean perm_point = false;
    private int perm_item = 0;
    
    public MailSendGui(Player p, String type) {
        super(gui_img,gui_x,gui_y,gui_w,gui_h,gui_ww,gui_hh,gui_ix,gui_iy);
        enVault = GlobalConfig.enVault;
        enPlayerPoints = GlobalConfig.enPlayerPoints;
        this.type = type;
        perm_cmd = p.hasPermission("mailbox.admin.send.command");
        perm_coin = p.hasPermission("mailbox.send.money.coin");
        perm_point = p.hasPermission("mailbox.send.money.point");
        for(int i=5;i>0;i--){
            if(p.hasPermission("mailbox.send.item."+i)){
                perm_item = i;
                break;
            }
        }
        this.addComponent(button_return);
        VexButton vbp = new VexButton(button_preview[0],button_preview[1],button_preview[2],button_preview[3],Integer.parseInt(button_preview[4]),Integer.parseInt(button_preview[5]),Integer.parseInt(button_preview[6]),Integer.parseInt(button_preview[7]),player -> previewMail(player));
        if(!button_preview_hover.isEmpty()) vbp.setHover(new VexHoverText(button_preview_hover));
        this.addComponent(vbp);
        this.addComponent(text_topic);
        this.addComponent(getTextField(field.get("topic")));
        this.addComponent(text_text);
        this.addComponent(getTextField(field.get("text")));
        if(perm_cmd){
            this.addComponent(text_command);
            this.addComponent(getTextField(field.get("command")));
            this.addComponent(text_description);
            this.addComponent(getTextField(field.get("description")));
        }
        if(perm_item!=0) this.addComponent(text_item);
        for(int i=0;i<perm_item;i++){
            this.addComponent(new VexSlot(i,slot_x.get(i),slot_y.get(i),null));
            this.addComponent(solt_image.get(i));
        }
        switch (type) {
            case "player" :
                this.addComponent(text_recipient);
                this.addComponent(getTextField(field.get("recipient")));
                break;
            case "permission":
                this.addComponent(text_permission);
                this.addComponent(getTextField(field.get("permission")));
                break;
        }
        if(enVault && perm_coin){
            bal_coin = MailBoxAPI.getEconomyBalance(p);
            //String bal = MailBoxAPI.getEconomyFormat(bal_coin);
            this.addComponent(image_coin);
            VexTextField vtf = getTextField(field.get("coin"));
            vtf.setHover(new VexHoverText(Arrays.asList("余额："+bal_coin)));
            this.addComponent(vtf);
        }
        if(enPlayerPoints && perm_point){
            bal_point = MailBoxAPI.getPoints(p);
            this.addComponent(image_point);
            VexTextField vtf = getTextField(field.get("point"));
            vtf.setHover(new VexHoverText(Arrays.asList("余额："+bal_point)));
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
        int text_text_x,
        int text_text_y,
        double text_text_size,
        String text_text_text,
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
        int field_text_x,
        int field_text_y,
        int field_text_w,
        int field_text_h,
        int field_text_max,
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
        button_return = new VexButton(button_return_id,button_return_text,button_return_img_1,button_return_img_2,button_return_x,button_return_y,button_return_w,button_return_h, player -> openMailBoxGui(player, "Recipient"));
        if(!button_return_hover.isEmpty()) button_return.setHover(new VexHoverText(button_return_hover));
        // 预览按钮
        button_preview = new String[]{button_preview_id,button_preview_text,button_preview_img_1,button_preview_img_2,Integer.toString(button_preview_x),Integer.toString(button_preview_y),Integer.toString(button_preview_w),Integer.toString(button_preview_h)};
        MailSendGui.button_preview_hover = button_preview_hover;
        // 文本框提示文字
        text_topic = new VexText(text_topic_x,text_topic_y,Arrays.asList(text_topic_text),text_topic_size);
        text_recipient = new VexText(text_recipient_x,text_recipient_y,Arrays.asList(text_recipient_text),text_recipient_size);
        text_permission = new VexText(text_permission_x,text_permission_y,Arrays.asList(text_permission_text),text_permission_size);
        text_text = new VexText(text_text_x,text_text_y,Arrays.asList(text_text_text),text_text_size);
        text_command = new VexText(text_command_x,text_command_y,Arrays.asList(text_command_text),text_command_size);
        text_description = new VexText(text_description_x,text_description_y,Arrays.asList(text_description_text),text_description_size);
        text_item = new VexText(text_item_x,text_item_y,Arrays.asList(text_item_text),text_item_size);
        // 文本框
        field.clear();
        field.put("topic", new int[]{field_topic_x,field_topic_y,field_topic_w,field_topic_h,field_topic_max,1});
        field.put("recipient", new int[]{field_recipient_x,field_recipient_y,field_recipient_w,field_recipient_h,field_recipient_max,2});
        field.put("permission", new int[]{field_permission_x,field_permission_y,field_permission_w,field_permission_h,field_permission_max,2});
        field.put("text", new int[]{field_text_x,field_text_y,field_text_w,field_text_h,field_text_max,4});
        field.put("command", new int[]{field_command_x,field_command_y,field_command_w,field_command_h,field_command_max,5});
        field.put("description", new int[]{field_description_x,field_description_y,field_description_w,field_description_h,field_description_max,6});
        field.put("coin", new int[]{field_coin_x,field_coin_y,field_coin_w,field_coin_h,field_coin_max,7,0});
        field.put("point", new int[]{field_point_x,field_point_y,field_point_w,field_point_h,field_point_max,8,0});
        // [Vault]提示图
        image_coin = new VexImage(image_coin_url,image_coin_x,image_coin_y,image_coin_w,image_coin_h);
        // [PlayerPoints]提示图
        image_point = new VexImage(image_point_url,image_point_x,image_point_y,image_point_w,image_point_h);
        // 物品槽
        MailSendGui.slot_x = slot_x;
        MailSendGui.slot_y = slot_y;
        int x_offset = ((slot_w-18)/2)-1; 
        int y_offset = ((slot_h-18)/2)-1;
        solt_image.clear();
        for(int i=0;i<5;i++){
            int x = slot_x.get(i);
            int y = slot_y.get(i);
            solt_image.add(new VexImage(slot_img,x+x_offset,y+y_offset,slot_w,slot_h));
        }
    }
    
    // 获取文本框
    private VexTextField getTextField(int[] f){
        if(f.length==7) return new VexTextField(f[0],f[1],f[2],f[3],f[4],f[5],Integer.toString(f[6]));
        else return new VexTextField(f[0],f[1],f[2],f[3],f[4],f[5]);
        
    }
    
    // 预览邮件
    private void previewMail(Player p){
        OpenedVexGui ovg = VexViewAPI.getPlayerCurrentGui(p);
        String topic = ovg.getVexGui().getTextField(field.get("topic")[5]).getTypedText();
        String text = ovg.getVexGui().getTextField(field.get("text")[5]).getTypedText();
        List<String> rl = new ArrayList();
        String perm = null;
        List<String> cl = new ArrayList();
        List<String> cd = new ArrayList();
        ArrayList<ItemStack> al = new ArrayList();
        double co = 0;
        int po = 0;
        if(enVault && perm_coin){
            String t = ovg.getVexGui().getTextField(field.get("coin")[5]).getTypedText();
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
            String t = ovg.getVexGui().getTextField(field.get("point")[5]).getTypedText();
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
        boolean valid = false;
        switch (type) {
            case "player":
                String[] recipient = divide(ovg.getVexGui().getTextField(field.get("recipient")[5]).getTypedText(), "recipient");
                rl.addAll(Arrays.asList(recipient));
                valid = valid(p, topic, text, recipient, null);
                break;
            case "permission":
                perm = ovg.getVexGui().getTextField(field.get("permission")[5]).getTypedText();
                valid = valid(p, topic, text, null, perm);
                break;
            case "system":
                valid = valid(p, topic, text, null,null);
                break;
        }
        if(valid){
            if(perm_cmd){
                String[] command = divide(ovg.getVexGui().getTextField(field.get("command")[5]).getTypedText(), "command");
                String[] description = divide(ovg.getVexGui().getTextField(field.get("description")[5]).getTypedText(), "description");
                if(command!=null) cl.addAll(Arrays.asList(command));
                if(description!=null) cd.addAll(Arrays.asList(description));
            }
            if(perm_item!=0){
                al = getItem(ovg);
            }
            if(al.isEmpty() && cl.isEmpty() && cd.isEmpty() && co==0 && po==0){
                TextMail tm = new TextMail(type, 0, p.getName(), rl, perm, topic.replaceAll("&", "§"), text.replaceAll("&", "§"), null);
                try{
                    openMailContentGui(p, tm, this, false);
                }catch(IOException e){
                    p.sendMessage(GlobalConfig.warning+"[邮件预览]：打开预览界面失败");
                }
            }else{
                FileMail fm = new FileMail(type, 0, p.getName(), rl, perm, topic.replaceAll("&", "§"), text.replaceAll("&", "§"), null, "0", al, cl, cd, co, po);
                try{
                    openMailContentGui(p, fm, this, false);
                }catch(IOException e){
                    p.sendMessage(GlobalConfig.warning+"[邮件预览]：打开预览界面失败");
                }
            }
        }
    }
    
    // 验证邮件主题、内容、收件人和权限
    private boolean valid(Player p, String t, String c, String[] r, String pe){
        if(t.equals("")){
            p.sendMessage(GlobalConfig.warning+"[邮件预览]：主题不能为空");
            return false;
        }else{
            if(c.equals("")){
                p.sendMessage(GlobalConfig.warning+"[邮件预览]：内容不能为空");
                return false;
            }else{
                switch (type) {
                    case "system":
                        return true;
                    case "permission":
                        if(pe==null || pe.equals("")){
                            p.sendMessage(GlobalConfig.warning+"[邮件预览]：权限不能为空");
                            return false;
                        }else{
                            return true;
                        }
                    case "player":
                        if(r.length<1){
                            p.sendMessage(GlobalConfig.warning+"[邮件预览]：收件人不能为空");
                            return false;
                        }else{
                            if(r.length>1){
                                if(!p.hasPermission("mailbox.admin.send.multiplayer")){
                                    p.sendMessage(GlobalConfig.warning+"[邮件预览]：您只能填写一位收件人");
                                    return false;
                                }
                            }else{
                                for(String name:r){
                                    if(name.equals(p.getName())){
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
                            }
                        }
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
                    String[] result = text.split(GlobalConfig.fileDiv);
                    return result;
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
    private ArrayList<ItemStack> getItem(OpenedVexGui ovg){
        ArrayList<ItemStack> oldi = new ArrayList();
        for(int i=0;i<perm_item;i++){
            ItemStack t = ovg.getVexGui().getSlotById(i).getItem();
            if(t.getType()!=null && t.getType()!=AIR) oldi.add(t);
        }
        /*for(int i=0;i<perm_item;i++) oldi.add(ovg.getVexGui().getSlotById(i).getItem());
        ArrayList<ItemStack> newi = new ArrayList();
        ArrayList<Integer> no = new ArrayList();
        for(int i=0;i<perm_item;i++){
            if(!no.contains(i)){
                if(oldi.get(i).getType()!=null && oldi.get(i).getType()!=AIR){
                    for(int j=i+1;j<perm_item;j++){
                        if(oldi.get(i).isSimilar(oldi.get(j))){
                            ItemStack t = oldi.get(i);
                            int x1 = t.getAmount();
                            int x2 = oldi.get(j).getAmount();
                            t.setAmount(x1+x2);
                            oldi.set(i, t);
                            no.add(j);
                        }
                    }
                    newi.add(ovg.getVexGui().getSlotById(i).getItem());
                }
            }
        }
        return newi;*/
        return oldi;
    }
    
    // 打开发送邮件GUI
    public static void openMailSendGui(Player p, String type, VexGui gui) {
        if(type.equals("player")){
            int out = MailBoxAPI.playerAsSenderAllow(p, GlobalConfig.player_out);
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
