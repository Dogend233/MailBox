package com.嘤嘤嘤.qwq.MailBox.VexView;

import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.MailBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexHoverText;
import lk.vexview.gui.components.VexImage;
import lk.vexview.gui.components.VexScrollingList;
import lk.vexview.gui.components.VexSlot;
import lk.vexview.gui.components.VexText;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MailContentGui extends VexGui{
    
    private static String gui_img;
    private static int gui_x;
    private static int gui_y;
    private static int gui_w;
    private static int gui_h;
    private static int gui_ww;
    private static int gui_hh;
    private static HashMap<String, String[]> buttonString = new HashMap();
    private static HashMap<String, int[]> buttonInt = new HashMap();
    private static HashMap<String, List<String>> buttonHover = new HashMap();
    private static int text_topic_x;
    private static int text_topic_y;
    private static double text_topic_size;
    private static int text_topic_w;
    private static int text_date_x;
    private static int text_date_y;
    private static double text_date_size;
    private static String text_date_prefix;
    private static List<String> text_date_display;
    private static int text_sender_x;
    private static int text_sender_y;
    private static double text_sender_size;
    private static String text_sender_prefix;
    private static int text_coin_x;
    private static int text_coin_y;
    private static double text_coin_size;
    private static String text_coin_prefix;
    private static String text_coin_suffix;
    private static int text_point_x;
    private static int text_point_y;
    private static double text_point_size;
    private static String text_point_prefix;
    private static String text_point_suffix;
    private static VexText text_file_yes;
    private static VexText text_file_no;
    private static VexText text_cmd;
    private static int text_content_x;
    private static int text_content_y;
    private static int text_content_w;
    private static int text_content_h;
    private static int text_content_mh;
    private static double text_content_size;
    private static int text_content_count;
    private static int text_content_line;
    private static int text_content_sh;
    private static String image_cmd_url;
    private static int image_cmd_x;
    private static int image_cmd_y;
    private static int image_cmd_w;
    private static int image_cmd_h;
    private static VexImage image_coin;
    private static VexImage image_point;
    private static String slot_img;
    private static int slot_w;
    private static int slot_h;
    private static List<Integer> slot_x;
    private static List<Integer> slot_y;

    private boolean collecte;
    private VexButton vbr;
    private VexButton vbd;
    private VexButton vbs;
    private VexButton vbc;
    private VexButton vbcd;
    
    public MailContentGui(Player p, TextMail tm, VexGui vg, boolean asSender){
        super(gui_img,gui_x,gui_y,gui_w,gui_h,gui_ww,gui_hh);
        // 返回按钮
        vbr = new VexButton(
                buttonString.get("return")[0],buttonString.get("return")[1],buttonString.get("return")[2],buttonString.get("return")[3],
                buttonInt.get("return")[0],buttonInt.get("return")[1],buttonInt.get("return")[2],buttonInt.get("return")[3]);
        if(!buttonHover.get("return").isEmpty()) vbr.setHover(new VexHoverText(buttonHover.get("return")));
        if(asSender){
            vbr.setFunction(player -> MailBoxGui.openMailBoxGui(player, "Recipient"));
        }else{
            vbr.setFunction(player -> MailBoxGui.openMailBoxGui(player, "Sender"));
        }
        if(vg!=null){
            vbr.setFunction(player -> MailSendGui.openMailSendGui(player, tm.getType(), vg));
        }
        this.addComponent(vbr);
        // 删除按钮
        vbd = new VexButton(
                buttonString.get("delete")[0],buttonString.get("delete")[1],buttonString.get("delete")[2],buttonString.get("delete")[3],
                buttonInt.get("delete")[0],buttonInt.get("delete")[1],buttonInt.get("delete")[2],buttonInt.get("delete")[3],player -> {
                    // 关闭GUI
                    player.closeInventory();
                    // 删除邮件
                    tm.Delete(player);
                });
        if(!buttonHover.get("delete").isEmpty()) vbd.setHover(new VexHoverText(buttonHover.get("delete")));
        // 发送按钮
        vbs = new VexButton(
                buttonString.get("send")[0],buttonString.get("send")[1],buttonString.get("send")[2],buttonString.get("send")[3],
                buttonInt.get("send")[0],buttonInt.get("send")[1],buttonInt.get("send")[2],buttonInt.get("send")[3],player -> {
                    // 关闭GUI
                    player.closeInventory();
                    // 发送邮件
                    if(tm.Send(player, null)){
                        player.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"邮件发送成功");
                    }else{
                        player.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件发送失败");
                    }
                });
        // 领取按钮
        vbc = new VexButton(
                buttonString.get("collect")[0],buttonString.get("collect")[1],buttonString.get("collect")[2],buttonString.get("collect")[3],
                buttonInt.get("collect")[0],buttonInt.get("collect")[1],buttonInt.get("collect")[2],buttonInt.get("collect")[3],player -> {
                    if(player.hasPermission("mailbox.collect."+tm.getType())){
                        if(tm.getType().equals("permission") && !p.hasPermission(tm.getPermission())){
                            player.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限领取这封邮件");
                        }else{
                            // 关闭GUI
                            player.closeInventory();
                            // 领取邮件
                            tm.Collect(player);
                        }
                    }else{
                        player.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限领取此类型邮件");
                    }
                });
        if(!buttonHover.get("collect").isEmpty()) vbc.setHover(new VexHoverText(buttonHover.get("collect")));
        // 已领取按钮
        vbcd = new VexButton(
                buttonString.get("collected")[0],buttonString.get("collected")[1],buttonString.get("collected")[2],buttonString.get("collected")[3],
                buttonInt.get("collected")[0],buttonInt.get("collected")[1],buttonInt.get("collected")[2],buttonInt.get("collected")[3]);
        if(!buttonHover.get("collected").isEmpty()) vbcd.setHover(new VexHoverText(buttonHover.get("collected")));
        // 获取玩家是否可以领取这封邮件
        ArrayList<Integer> l = MailBox.getRelevantMailList(p, tm.getType()).get("asRecipient");
        collecte = l.contains(tm.getId());
        textMail(tm, p);
    }
    
    public static void setContentConfig(
        String gui_img,
        int gui_x,
        int gui_y,
        int gui_w,
        int gui_h,
        int gui_ww,
        int gui_hh,
        String button_return_id,
        String button_return_text,
        List<String> button_return_hover,
        String button_return_img_1,
        String button_return_img_2,
        int button_return_x,
        int button_return_y,
        int button_return_w,
        int button_return_h,
        String button_collect_id,
        String button_collect_text,
        List<String> button_collect_hover,
        String button_collect_img_1,
        String button_collect_img_2,
        String button_collected_text,
        List<String> button_collected_hover,
        String button_collected_img_1,
        String button_collected_img_2,
        int button_collect_x,
        int button_collect_y,
        int button_collect_w,
        int button_collect_h,
        String button_delete_id,
        String button_delete_text,
        List<String> button_delete_hover,
        String button_delete_img_1,
        String button_delete_img_2,
        int button_delete_x,
        int button_delete_y,
        int button_delete_w,
        int button_delete_h,
        String button_send_id,
        String button_send_text,
        List<String> button_send_hover,
        String button_send_img_1,
        String button_send_img_2,
        int button_send_x,
        int button_send_y,
        int button_send_w,
        int button_send_h,
        int text_topic_x,
        int text_topic_y,
        double text_topic_size,
        int text_topic_w,
        int text_date_x,
        int text_date_y,
        double text_date_size,
        String text_date_prefix,
        List<String> text_date_display,
        int text_sender_x,
        int text_sender_y,
        double text_sender_size,
        String text_sender_prefix,
        int text_coin_x,
        int text_coin_y,
        double text_coin_size,
        String text_coin_prefix,
        String text_coin_suffix,
        int text_point_x,
        int text_point_y,
        double text_point_size,
        String text_point_prefix,
        String text_point_suffix,
        int text_file_x,
        int text_file_y,
        String text_file_text_yes,
        String text_file_text_no,
        double text_file_size,
        int text_cmd_x,
        int text_cmd_y,
        String text_cmd_text,
        double text_cmd_size,
        int text_content_x,
        int text_content_y,
        int text_content_w,
        int text_content_h,
        int text_content_mh,
        double text_content_size,
        int text_content_count,
        int text_content_line,
        int text_content_sh, 
        String image_cmd_url,
        int image_cmd_x,
        int image_cmd_y,
        int image_cmd_w,
        int image_cmd_h,
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
        MailContentGui.gui_img = gui_img;
        MailContentGui.gui_x = gui_x;
        MailContentGui.gui_y = gui_y;
        MailContentGui.gui_w = gui_w;
        MailContentGui.gui_h = gui_h;
        MailContentGui.gui_ww = gui_ww;
        MailContentGui.gui_hh = gui_hh;
        // 按钮String
        buttonString.clear();
        buttonString.put("return", new String[]{button_return_id,button_return_text,button_return_img_1,button_return_img_2});
        buttonString.put("collect", new String[]{button_collect_id,button_collect_text,button_collect_img_1,button_collect_img_2});
        buttonString.put("collected", new String[]{button_collect_id+"ed",button_collected_text,button_collected_img_1,button_collected_img_2});
        buttonString.put("delete", new String[]{button_delete_id,button_delete_text,button_delete_img_1,button_delete_img_2});
        buttonString.put("send", new String[]{button_send_id,button_send_text,button_send_img_1,button_send_img_2});
        // 按钮int
        buttonInt.clear();
        buttonInt.put("return", new int[]{button_return_x,button_return_y,button_return_w,button_return_h});
        buttonInt.put("collect", new int[]{button_collect_x,button_collect_y,button_collect_w,button_collect_h});
        buttonInt.put("collected", new int[]{button_collect_x,button_collect_y,button_collect_w,button_collect_h});
        buttonInt.put("delete", new int[]{button_delete_x,button_delete_y,button_delete_w,button_delete_h});
        buttonInt.put("send", new int[]{button_send_x,button_send_y,button_send_w,button_send_h});
        // 按钮Hover
        buttonHover.clear();
        buttonHover.put("return", button_return_hover);
        buttonHover.put("collect", button_collect_hover);
        buttonHover.put("collected", button_collected_hover);
        buttonHover.put("delete", button_delete_hover);
        buttonHover.put("send", button_send_hover);
        // 邮件主题
        MailContentGui.text_topic_x = text_topic_x;
        MailContentGui.text_topic_y = text_topic_y;
        MailContentGui.text_topic_size = text_topic_size;
        MailContentGui.text_topic_w = text_topic_w;
        // 邮件发送时间
        MailContentGui.text_date_x = text_date_x;
        MailContentGui.text_date_y = text_date_y;
        MailContentGui.text_date_size = text_date_size;
        MailContentGui.text_date_prefix = text_date_prefix;
        MailContentGui.text_date_display = text_date_display;
        // 发件人
        MailContentGui.text_sender_x = text_sender_x;
        MailContentGui.text_sender_y = text_sender_y;
        MailContentGui.text_sender_size = text_sender_size;
        MailContentGui.text_sender_prefix = text_sender_prefix;
        // Vault的金币
        MailContentGui.text_coin_x = text_coin_x;
        MailContentGui.text_coin_y = text_coin_y;
        MailContentGui.text_coin_size = text_coin_size;
        MailContentGui.text_coin_prefix = text_coin_prefix;
        MailContentGui.text_coin_suffix = text_coin_suffix;
        // PlayerPoints的点券
        MailContentGui.text_point_x = text_point_x;
        MailContentGui.text_point_y = text_point_y;
        MailContentGui.text_point_size = text_point_size;
        MailContentGui.text_point_prefix = text_point_prefix;
        MailContentGui.text_point_suffix = text_point_suffix;
        // 附件提示字
        text_file_yes = new VexText(text_file_x, text_file_y, Arrays.asList(text_file_text_yes), text_file_size);
        text_file_no = new VexText(text_file_x, text_file_y, Arrays.asList(text_file_text_no), text_file_size);
        // 指令提示字
        text_cmd = new VexText(text_cmd_x, text_cmd_y, Arrays.asList(text_cmd_text), text_cmd_size);
        // 邮件内容
        MailContentGui.text_content_x = text_content_x;
        MailContentGui.text_content_y = text_content_y;
        MailContentGui.text_content_w = text_content_w;
        MailContentGui.text_content_h = text_content_h;
        MailContentGui.text_content_mh = text_content_mh;
        MailContentGui.text_content_size = text_content_size;
        MailContentGui.text_content_count = text_content_count;
        MailContentGui.text_content_line = text_content_line;
        MailContentGui.text_content_sh = text_content_sh;
        // 指令指示图
        MailContentGui.image_cmd_url = image_cmd_url;
        MailContentGui.image_cmd_x = image_cmd_x;
        MailContentGui.image_cmd_y = image_cmd_y;
        MailContentGui.image_cmd_w = image_cmd_w;
        MailContentGui.image_cmd_h = image_cmd_h;
        // Vault的金币指示图
        image_coin = new VexImage(image_coin_url,image_coin_x,image_coin_y,image_coin_w,image_coin_h);
        // PlayerPoints的点券指示图
        image_point = new VexImage(image_point_url,image_point_x,image_point_y,image_point_w,image_point_h);
        // 物品槽
        MailContentGui.slot_img = slot_img;
        MailContentGui.slot_w = slot_w;
        MailContentGui.slot_h = slot_h;
        MailContentGui.slot_x = slot_x;
        MailContentGui.slot_y = slot_y;
    }
    
    // 对文本邮件的操作
    private void textMail(TextMail tm, Player p){
        // 邮件id
        int mail = tm.getId();
        // 邮件类型
        String type = tm.getType();
        // 邮件主题
        String t = tm.getTopic();
        List<String> tl = new ArrayList();
        if(t.length()>text_topic_w) {
            String str = t;
            int temp = text_topic_w;
            int c = t.length()/temp;
            if(t.length()%temp!=0) c++;
            for(int i=0;i<c;i++){
                if(str.length()>text_topic_w){
                    if(str.substring(text_topic_w-1, text_topic_w).equals("§")){
                        temp -= 1;
                    }
                    tl.add(str.substring(0, temp));
                    str = str.substring(temp);
                }else{
                    tl.add(str);
                }
            }
        }else{
            tl.add(t);
        }
        VexText vtp = new VexText(text_topic_x,text_topic_y,tl,text_topic_size);
        // 邮件类型+ID+信息
        if(p.hasPermission("mailbox.content.id")){
            switch (type){
                case "player":
                    List<String> playerReci = new ArrayList();
                    playerReci.add(tm.getTypeName()+" - "+tm.getId());
                    for(String s:tm.getRecipient()){
                        playerReci.add(s);
                    }
                    vtp.setHover(new VexHoverText(playerReci));
                    break;
                case "permission":
                    vtp.setHover(new VexHoverText(Arrays.asList(tm.getTypeName()+" - "+tm.getId(),tm.getPermission())));
                    break;
                default: 
                    vtp.setHover(new VexHoverText(Arrays.asList(tm.getTypeName()+" - "+tm.getId())));
            }
        }
        this.addComponent(vtp);
        // 发送时间
        if(text_date_display.contains(type) && tm.getDate()!=null) this.addComponent(new VexText(text_date_x,text_date_y,Arrays.asList(text_date_prefix+tm.getDate()),text_date_size));
        // 发送人
        this.addComponent(new VexText(text_sender_x,text_sender_y,Arrays.asList(text_sender_prefix+tm.getSender()),text_sender_size));
        // 邮件内容
        this.addComponent(divContent(tm.getContent()));
        // 附件邮件
        if(tm instanceof FileMail) {
            fileMail(tm, p, mail);
        }else{
            this.addComponent(text_file_no);
            if(mail==0){
                List<String> hover = new ArrayList();
                if(!buttonHover.get("send").isEmpty()) buttonHover.get("send").forEach(v -> hover.add(v));
                if(tm.getExpandCoin()!=0) hover.add("§6消耗: §r"+tm.getExpandCoin()+" "+GlobalConfig.vaultDisplay);
                if(tm.getExpandPoint()!=0) hover.add("§6消耗: §r"+tm.getExpandPoint()+" "+GlobalConfig.playerPointsDisplay);
                if(!hover.isEmpty()) vbs.setHover(new VexHoverText(hover));
                this.addComponent(vbs);
            }else{
                // 如果邮件不是附件邮件且为未读状态，则设置为已读
                if(collecte) tm.Collect(p);
            }
        }
        if(mail==0){
            this.addComponent(vbr);
        }else{
            this.addComponent(vbr);
            if(p.hasPermission("mailbox.admin.delete."+type) || ((type.equals("player")) && tm.getSender().equals(p.getName()) && p.hasPermission("mailbox.delete.player"))) {
                this.addComponent(vbd);
            }
        }
    }
    
    // 对附件邮件的操作
    private void fileMail(TextMail tm, Player p, int mail){
        FileMail fm = (FileMail) tm;
        fm.getFile();
        if((fm.isHasItem() && !fm.getItemList().isEmpty()) || (fm.isHasCommand() && !fm.getCommandList().isEmpty()) || fm.getCoin()!=0 || fm.getPoint()!=0){
            // 附件文字
            VexText vtF = text_file_yes;
            // 附件类型+名称
            if(p.hasPermission("mailbox.content.filename")) vtF.setHover(new VexHoverText(Arrays.asList(fm.getType()+" - "+fm.getFileName())));
            this.addComponent(vtF);
            // 附件物品
            if(fm.isHasItem()){
                int x_offset = ((slot_w-18)/2)-1; 
                int y_offset = ((slot_h-18)/2)-1;
                ArrayList<ItemStack> isl = fm.getItemList();
                for(int i = 0 ;i<isl.size();i++){
                    int x = slot_x.get(i);
                    int y = slot_y.get(i);
                    ItemStack is = isl.get(i);
                    this.addComponent(new VexSlot(i,x,y,is));
                    this.addComponent(new VexImage(slot_img,x+x_offset,y+y_offset,slot_w,slot_h));
                }
            }
            // 附件指令
            if(fm.isHasCommand()){
                this.addComponent(text_cmd);
                VexImage vi = new VexImage(image_cmd_url,image_cmd_x,image_cmd_y,image_cmd_w,image_cmd_h);
                List<String> cD = fm.getCommandDescription();
                if(!cD.isEmpty()) vi.setHover(new VexHoverText(cD));
                this.addComponent(vi);
            }
            // 附件Vault金币
            if(GlobalConfig.enVault){
                double coin = fm.getCoin();
                if(coin!=0){
                    //String c = MailBoxAPI.getEconomyFormat(coin);
                    this.addComponent(image_coin);
                    this.addComponent(new VexText(text_coin_x,text_coin_y,Arrays.asList(text_coin_prefix+coin+text_coin_suffix),text_coin_size));
                }
            }
            // 附件PlayerPoints点券
            if(GlobalConfig.enPlayerPoints){
                int point = fm.getPoint();
                if(point!=0){
                    this.addComponent(image_point);
                    this.addComponent(new VexText(text_point_x,text_point_y,Arrays.asList(text_point_prefix+point+text_point_suffix),text_point_size));
                }
            }
            // 发送邮件/领取附件按钮
            if(mail==0){
                List<String> hover = new ArrayList();
                if(!buttonHover.get("send").isEmpty()) buttonHover.get("send").forEach(v -> hover.add(v));
                if(fm.getExpandCoin()!=0) hover.add("§6消耗: §r"+fm.getExpandCoin()+" "+GlobalConfig.vaultDisplay);
                if(fm.getExpandPoint()!=0) hover.add("§6消耗: §r"+fm.getExpandPoint()+" "+GlobalConfig.playerPointsDisplay);
                if(!hover.isEmpty()) vbs.setHover(new VexHoverText(hover));
                this.addComponent(vbs);
            }else{
                if(collecte) {
                    this.addComponent(vbc);
                }else{
                    this.addComponent(vbcd);
                }
            }
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"此邮件附件配置文件错误！");
        }
    }
    
    // 分割邮件内容
    private VexScrollingList divContent(String content){
        String text = content;
        int length = text_content_count;
        int size = 0;
        String[] t = text.split(" ");
        for(String t1 : t) {
            size += t1.length() / length;
            if(t1.length()%length != 0) size++;
        }
        int mh = text_content_mh;
        if(size>text_content_line){
            mh += (size-text_content_line)*text_content_sh;
        }
        VexScrollingList vsl = new VexScrollingList(text_content_x,text_content_y,text_content_w,text_content_h,mh);
        vsl.addComponent(new VexText(0,0,Arrays.asList(t),text_content_size));
        return vsl;
    }
    
    // 打开邮件GUI
    public static void openMailContentGui(Player p, TextMail tm, VexGui a, boolean asSender) throws IOException{
        if(p.hasPermission("mailbox.gui.mailcontent")){
            VexViewAPI.openGui(p, new MailContentGui(p, tm, a, asSender));
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限打开此GUI");
        }
    }
    
}
