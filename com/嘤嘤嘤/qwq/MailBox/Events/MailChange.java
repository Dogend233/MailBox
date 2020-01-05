package com.嘤嘤嘤.qwq.MailBox.Events;

import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailCollectEvent;
import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailDeleteEvent;
import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailSendEvent;
import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import com.嘤嘤嘤.qwq.MailBox.MailBox;
import java.io.IOException;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MailChange implements Listener {
    
    @EventHandler
    public void onMailSend(MailSendEvent e) throws IOException{
        TextMail tm = e.getMail();
        String type = tm.getType();
        String typeName = tm.getTypeName();
        String file;
        if(e.getMailType()==1){
            file = "附件: "+((FileMail)tm).getFileName();
        }else{
            file = "无附件";
        }
        // 更新邮件列表
        MailBox.updateMailList(e.getPlayer(), type);
        // 输出到控制台
        Bukkit.getConsoleSender().sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+e.getName()+"发送了一封邮件: <"+typeName+"-"+file+">");
        if(GlobalConfig.tips.isEmpty()) return;
        switch (type){
            case "system":
                for(Player p:Bukkit.getOnlinePlayers()){
                    if(MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.system")){
                        MailBoxAPI.sendTips(p);
                    }
                }   break;
            case "permission":
                for(Player p:Bukkit.getOnlinePlayers()){
                    String perm = tm.getPermission();
                    if(MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.permission") && p.hasPermission(perm)){
                        MailBoxAPI.sendTips(p);
                    }
                }   break;
            case "date":
                if(MailBoxAPI.isStart(tm)){
                    for(Player p:Bukkit.getOnlinePlayers()){
                        if(MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.date")){
                            MailBoxAPI.sendTips(p);
                        }
                    }
                }   break;
            case "player":
                List<String> pl = tm.getRecipient();
                for(Player p:Bukkit.getOnlinePlayers()){
                    if(MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.player") && pl.contains(p.getName())){
                        MailBoxAPI.sendTips(p);
                    }
                }   break;
            default:
        }
    }
    
    @EventHandler
    public void onMailCollect(MailCollectEvent e){
        TextMail tm = e.getMail();
        String type = tm.getType();
        // 更新邮件列表
        if(type.equals("player")) MailBox.updateMailList(null, type);
        // 更新玩家可领取邮件列表
        MailBox.updateRelevantMailList(e.getPlayer(), type);
        // 输出到控制台
        Bukkit.getConsoleSender().sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+e.getPlayer().getName()+"领取了邮件: <"+tm.getTypeName()+" - "+tm.getId()+">");
    }
    
    @EventHandler
    public void onMailDelete(MailDeleteEvent e){
        TextMail tm = e.getMail();
        String type = tm.getType();
        // 更新邮件列表
        MailBox.updateMailList(e.getPlayer(), type);
        // 输出到控制台
        Bukkit.getConsoleSender().sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+e.getName()+"删除了邮件: <"+tm.getTypeName()+" - "+tm.getId()+">");
    }
    
}