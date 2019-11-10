package com.嘤嘤嘤.qwq.MailBox.Events;

import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailCollectEvent;
import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailDeleteEvent;
import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailSendEvent;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.getUnMailList;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.updateMailList;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Mail implements Listener {
    
    @EventHandler
    public void onMailSend(MailSendEvent e) throws IOException{
        TextMail tm = e.getMail();
        String type = tm.getType();
        String file;
        if(e.getMailType()==1){
            FileMail fm = (FileMail) tm;
            file = "附件: "+fm.getFileName();
        }else{
            file = "无附件";
        }
        // 更新邮件列表
        updateMailList(e.getPlayer(), type);
        // 关闭GUI
        e.getPlayer().closeInventory();
        // 输出到控制台
        Bukkit.getConsoleSender().sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+e.getPlayer().getName()+"发送了一封邮件: <"+tm.getTypeName()+"-"+file+">");
    }
    
    @EventHandler
    public void onMailCollect(MailCollectEvent e){
        TextMail tm = e.getMail();
        String type = tm.getType();
        // 更新玩家可领取邮件列表
        getUnMailList(e.getPlayer(), type);
        // 关闭GUI
        e.getPlayer().closeInventory();
        // 输出到控制台
        Bukkit.getConsoleSender().sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+e.getPlayer().getName()+"领取了邮件: <"+tm.getTypeName()+" - "+tm.getId()+">");
    }
    
    @EventHandler
    public void onMailDelete(MailDeleteEvent e){
        TextMail tm = e.getMail();
        String type = tm.getType();
        // 更新邮件列表
        updateMailList(e.getPlayer(), type);
        // 关闭GUI
        e.getPlayer().closeInventory();
        // 输出到控制台
        Bukkit.getConsoleSender().sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+e.getPlayer().getName()+"删除了邮件: <"+tm.getTypeName()+" - "+tm.getId()+">");
    }
    
}