package com.嘤嘤嘤.qwq.MailBox.VexView;

import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListAllUn;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxGui.openMailBoxGui;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailSendGui.openMailSendGui;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    private static String colorSenderTitle;
    private static String colorSender;
    private static String colorDate;
    private static String colorFile;
    private static String colorCommand;
    private static String colorCollect;
    private static String colorDelete;
    private static String colorConfirm;
    
    public MailContentGui(Player p, TextMail tm){
        super("[local]MailBox/gui_content.png",-1,-1,240,165,240,165);
        textMail(tm, p);
    }
    
    public static void setContentConfig(
        String colorSenderTitle, 
        String colorSender,
        String colorDate,
        String colorFile,
        String colorCommand, 
        String colorCollect,
        String colorDelete,
        String colorConfirm
    ){
        MailContentGui.colorSenderTitle = colorSenderTitle;
        MailContentGui.colorSender = colorSender;
        MailContentGui.colorDate = colorDate;
        MailContentGui.colorFile = colorFile;
        MailContentGui.colorCommand = colorCommand;
        MailContentGui.colorCollect = colorCollect;
        MailContentGui.colorDelete = colorDelete;
        MailContentGui.colorConfirm = colorConfirm;
    }
    
    // 对文本邮件的操作
    private void textMail(TextMail tm, Player p){
        // 邮件id
        int mail = tm.getId();
        // 邮件主题
        double s = 2;
        String t = tm.getTopic();
        if(t.length()>10) {
            s = 1;
            if(t.length()>20) {
                t = t.substring(0, 20)+"\n"+t.substring(21);
                s = 1;
            }
        }
        VexText vtp = new VexText(8,7,Arrays.asList(t),s);
        // 附件类型+ID
        if(p.hasPermission("mailbox.content.id"))vtp.setHover(new VexHoverText(Arrays.asList(tm.getType()+"-"+tm.getId())));
        this.addComponent(vtp);
        // 发送时间
        if(tm.getDate()!=null)this.addComponent(new VexText(7,117,Arrays.asList(colorDate+tm.getDate()),1));
        // 发送人
        this.addComponent(new VexText(142,149,Arrays.asList(colorSenderTitle+"来自： "+colorSender+tm.getSender()),1));
        // 邮件内容
        this.addComponent(divContent(tm.getContent()));
        // 附件邮件
        if(tm instanceof FileMail) {
            fileMail(tm, p, mail);
        }else{
            // 附件文字
            this.addComponent(new VexText(7,129,Arrays.asList(colorFile+"无附件"),1));
            if(mail==0){
                p.sendMessage(GlobalConfig.success+"[邮件预览]：你阅读了这封邮件");
                // 发送邮件
                VexButton vb = new VexButton("send", colorConfirm+"发送", "[local]MailBox/button_small.png", "[local]MailBox/button_small_.png", 203,120,30,18, player -> {
                    // 发送邮件
                    if(tm.Send(player)){
                        p.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"邮件发送成功");
                        // 关闭GUI
                        player.closeInventory();
                    }else{
                        p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件发送失败");
                    }
                });
                this.addComponent(vb);
            }else{
                ArrayList<Integer> l = MailListAllUn.get(p.getName());
                if(l.contains(mail)){
                    // 如果邮件不是附件邮件且为未读状态，则设置为已读
                    tm.Collect(p);
                }
            }
        }
        if(mail==0){
            // 返回按钮
            this.addComponent(new VexButton("return","","[local]MailBox/button_return.png","[local]MailBox/button_return.png",207,1,30,25,player -> {
                openMailSendGui(player, tm);
            }));
        }else{
            // 返回按钮
            this.addComponent(new VexButton("return","","[local]MailBox/button_return.png","[local]MailBox/button_return.png",207,1,30,25,player -> {
                openMailBoxGui(player);
            }));
            // 如果是OP，增加删除按钮
            if(p.isOp()){
                this.addComponent(new VexButton("delete", colorDelete+"删除", "[local]MailBox/button_small.png", "[local]MailBox/button_small_.png", 170,120,30,18, player -> {
                    // 删除邮件
                    tm.Delete(p);
                }));
            }
        }
    }
    
    // 对附件邮件的操作
    private void fileMail(TextMail tm, Player p, int mail){
        FileMail fm = (FileMail) tm;
        if((fm.getHasItem() && !fm.getItemList().isEmpty()) || (fm.getHasCommand() && !fm.getCommandList().isEmpty())){
            // 附件文字
            VexText vtF = new VexText(7,129,Arrays.asList(colorFile+"附件: "),1);
            // 附件类型+名称
            if(p.hasPermission("mailbox.content.filename"))vtF.setHover(new VexHoverText(Arrays.asList(fm.getType()+"-"+fm.getFileName())));
            this.addComponent(vtF);
            // 附件物品
            if(fm.getHasItem()){
                ArrayList<ItemStack> isl = fm.getItemList();
                for(int i = 0 ;i<isl.size();i++){
                    ItemStack is = isl.get(i);
                    this.addComponent(new VexSlot(i,i*18+7,142,is));
                }
            }
            // 附件指令
            VexHoverText vht = null;
            if(fm.getHasCommand()){
                List<String> cD = fm.getCommandDescription();
                if(cD!=null){
                    vht = new VexHoverText(cD);
                }
                this.addComponent(new VexText(96,132,Arrays.asList(colorCommand+"指令"),1));
                this.addComponent(new VexImage("[local]MailBox/img_cmd.png",97,142,16,16,vht));
            }
            // 领取附件按钮
            if(mail==0){
                VexButton vb = new VexButton("确认", "确认", "[local]MailBox/button_small.png", "[local]MailBox/button_small_.png", 203,120,30,18, player -> {
                    // 发送邮件
                    if(tm.Send(player)){
                        p.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"邮件发送成功");
                    }else{
                        p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件发送失败");
                    }
                    // 关闭GUI
                    player.closeInventory();
                });
                this.addComponent(vb);
            }else{
                String f_c = "领取";
                ArrayList<Integer> l = MailListAllUn.get(p.getName());
                if(!l.contains(mail)) { f_c = "已领取"; }
                VexButton vb = new VexButton("id_"+mail, colorCollect+f_c, "[local]MailBox/button_small.png", "[local]MailBox/button_small_.png", 203,120,30,18, player -> {
                    if(player.hasPermission("mailbox.collect."+fm.getType())){
                        if(l.contains(mail)){
                            // 领取邮件
                            fm.Collect(p);
                            // 关闭GUI
                            player.closeInventory();
                        }
                    }else{
                        player.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限领取此类型邮件");
                    }
                    
                });
                this.addComponent(vb);
            }
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"此邮件附件配置文件错误！");
        }
    }
    
    // 分割邮件内容
    private VexScrollingList divContent(String content){
        String text = content;
        int length = 23;
        int size = 0;
        String[] t = text.split(" ");
        for(String t1 : t) {
            size += t1.length() / length;
            if(t1.length()%length != 0)size++;
        }
        int mh = 82;
        if(size>8){
            mh += (size-8)*10;
        }
        VexScrollingList vsl = new VexScrollingList(10,28,220,88,mh);
        vsl.addComponent(new VexText(0,0,Arrays.asList(t),1));
        return vsl;
    }
    
    // 打开邮件GUI
    public static void openMailContentGui(Player p, TextMail tm) throws IOException{
        if(p.hasPermission("mailbox.gui.mailcontent")){
            VexViewAPI.openGui(p, new MailContentGui(p, tm));
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限打开此GUI");
        }
    }
    
}
