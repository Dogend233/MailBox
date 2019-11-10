package com.嘤嘤嘤.qwq.MailBox.VexView;

import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListAll;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListAllId;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListAllUn;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.getUnMailList;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.updateMailList;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailContentGui.openMailContentGui;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailSendGui.openMailSendGui;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexScrollingList;
import lk.vexview.gui.components.VexText;
import org.bukkit.entity.Player;

public class MailBoxGui extends VexGui{
    
    private static String colorBox;
    private static String colorWrite;
    private static String colorRead;
    private static String colorFile;
    private static String colorTopic;
    private static String colorQAQ;
    private static String colorSender;
    private static String nullBox;
    
    public MailBoxGui(Player p){
        super("[local]MailBox/gui_mailbox.png",-1,-1,260,250,260,250);
        this.addComponent(vt);
        // 如果是OP则添加"写邮件"按钮
        if(p.isOp()){
            this.addComponent(vbr);
        }
        VexScrollingList aml = getAllMailList(p);
        if(aml==null){
            this.addComponent(new VexText(-1,-1,Arrays.asList(nullBox),1));
        }else{
            this.addComponent(aml);
        }
    }
    
    public static void setBoxConfig(
        String colorBox, 
        String colorWrite,
        String colorRead,
        String colorFile,
        String colorTopic, 
        String colorQAQ,
        String colorSender,
        String nullBox
    ){
        MailBoxGui.colorBox = colorBox;
        MailBoxGui.colorWrite = colorWrite;
        MailBoxGui.colorRead = colorRead;
        MailBoxGui.colorFile = colorFile;
        MailBoxGui.colorTopic = colorTopic;
        MailBoxGui.colorQAQ = colorQAQ;
        MailBoxGui.colorSender = colorSender;
        MailBoxGui.nullBox = nullBox;
    }
    
    private VexScrollingList getAllMailList(Player p){
        updateMailList(null, "all");
        ArrayList<Integer> aml = MailListAllId;
        if(MailListAllId.isEmpty()){
            return null;
        }else{
            getUnMailList(p, "all");
            HashMap<Integer, TextMail> am = MailListAll;
            ArrayList<Integer> uam = MailListAllUn.get(p.getName());
            int mh = aml.size()*30+5;
            if(mh<198)mh=198;
            VexScrollingList vsl = new VexScrollingList(10,33,240,203,mh);
            for(int i=0;i<aml.size();i++){
                String s = "";
                int mid = aml.get(i);
                TextMail tm = am.get(mid);
                String t = tm.getTopic();
                for(int x=0;x<uam.size();x++){
                    if(uam.get(x)==mid){
                        if(tm instanceof FileMail) {
                            s = colorFile+"[附件] ";
                        }else{
                            s = colorRead+"[未读] ";
                        }
                        break;
                    }
                }
                if(t.length()>15){
                    t = t.substring(0, 14)+"...";
                }
                vsl.addComponent(new VexButton("id_"+mid,s+colorTopic+t+colorQAQ+" - "+colorSender+tm.getSender(),"[local]MailBox/button_mail.png","[local]MailBox/button_mail_.png",5,i*30+1,225,26,player -> {
                    try {
                        openMailContentGui(player, tm);
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                }));
            }
            return vsl;
        }
    }

    VexText vt = new VexText(8,14,Arrays.asList(colorBox+"邮箱"),1.8);
    VexButton vbr = new VexButton("id_0",colorWrite+"写邮件","[local]MailBox/button_small.png","[local]MailBox/button_small_.png",222,11,30,18,player -> {
        openMailSendGui(player, null);
    });
    
    public static void openMailBoxGui(Player p){
        if(p.hasPermission("mailbox.gui.mailbox")){
            VexViewAPI.openGui(p, new MailBoxGui(p));
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有权限打开此GUI");
        }
    }
    
}
