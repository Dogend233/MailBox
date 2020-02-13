package com.tripleying.qwq.MailBox.Events;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Mail.*;
import com.tripleying.qwq.MailBox.VexView.MailSendGui;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import lk.vexview.event.gui.VexGuiOpenEvent;
import lk.vexview.gui.OpenedVexGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SendGuiOpen implements Listener {
    
    @EventHandler
    public void onVexGuiOpen(VexGuiOpenEvent evt){
        if(evt.getGui() instanceof MailSendGui){
            Player p = evt.getPlayer();
            OpenedVexGui ovg = VexViewAPI.getPlayerCurrentGui(p);
            if(ovg.getVexGui() instanceof MailSendGui){
                BaseMail bm = ((MailSendGui)ovg.getVexGui()).getMail();
                if(bm==null) return;
                ovg.setTextFieldContent(MailSendGui.FIELD.get("topic")[5], bm.getTopic());
                if(GlobalConfig.vexview_under_2_6_3){
                    ovg.setTextFieldContent(MailSendGui.FIELD.get("text")[5], bm.getContent());
                }else{
                    String cont = bm.getContent();
                    if(!cont.trim().equals("")){
                        List<String> textlist = new ArrayList();
                        textlist.addAll(Arrays.asList(cont.split(" ")));
                        ovg.setTextAreaContent(MailSendGui.FIELD.get("text")[5], textlist);
                    }
                }
                if(p.hasPermission("mailbox.admin.send.sender")) ovg.setTextFieldContent(MailSendGui.FIELD.get("sender")[5], bm.getSender());
                switch (bm.getType()) {
                    case "player" :
                        if(GlobalConfig.vexview_under_2_6_3){
                            ovg.setTextFieldContent(MailSendGui.FIELD.get("recipient")[5], ((MailPlayer)bm).getRecipientString());
                        }else{
                            List<String> reci = ((MailPlayer)bm).getRecipient();
                            if(!reci.isEmpty()) ovg.setTextAreaContent(MailSendGui.FIELD.get("recipient")[5], reci);
                        }
                        break;
                    case "permission":
                        ovg.setTextFieldContent(MailSendGui.FIELD.get("permission")[5], ((MailPermission)bm).getPermission());
                        break;
                    case "date":
                        ovg.setTextFieldContent(MailSendGui.FIELD.get("startdate")[5], bm.getDate());
                        ovg.setTextFieldContent(MailSendGui.FIELD.get("deadline")[5], ((MailDate)bm).getDeadline());
                        break;
                    case "keytimes":
                        ovg.setTextFieldContent(MailSendGui.FIELD.get("key")[5], ((MailKeyTimes)bm).getKey());
                    case "times":
                        ovg.setTextFieldContent(MailSendGui.FIELD.get("times")[5], Integer.toString(((MailTimes)bm).getTimes()));
                        break;
                    case "cdkey":
                        ovg.setCheckBox(MailSendGui.checkBox_onlyCDK[0], ((MailCdkey)bm).isOnly());
                        break;
                    case "template":
                        ovg.setTextFieldContent(MailSendGui.FIELD.get("template")[5], ((MailTemplate)bm).getTemplate());
                        break;
                }
                if(bm instanceof BaseFileMail){
                    if(p.hasPermission("mailbox.admin.send.command")){
                        if(GlobalConfig.vexview_under_2_6_3){
                            ovg.setTextFieldContent(MailSendGui.FIELD.get("command")[5], ((BaseFileMail)bm).getCommandListString());
                            ovg.setTextFieldContent(MailSendGui.FIELD.get("description")[5], ((BaseFileMail)bm).getCommandDescriptionString());
                        }else{
                            List<String> comm = ((BaseFileMail)bm).getCommandList();
                            if(!comm.isEmpty()){
                                ovg.setTextAreaContent(MailSendGui.FIELD.get("command")[5], comm);
                                comm = ((BaseFileMail)bm).getCommandDescription();
                                if(!comm.isEmpty()){
                                    ovg.setTextAreaContent(MailSendGui.FIELD.get("description")[5], comm);
                                }
                            }
                        }
                    }
                    if(GlobalConfig.enVault && MailBoxAPI.hasPlayerPermission(p, "mailbox.send.money.coin")) ovg.setTextFieldContent(MailSendGui.FIELD.get("coin")[5], Double.toString(((BaseFileMail)bm).getCoin()));
                    if(GlobalConfig.enPlayerPoints && MailBoxAPI.hasPlayerPermission(p, "mailbox.send.money.point")) ovg.setTextFieldContent(MailSendGui.FIELD.get("point")[5], Integer.toString(((BaseFileMail)bm).getPoint()));
                }
            }
        }
    }
    
}
