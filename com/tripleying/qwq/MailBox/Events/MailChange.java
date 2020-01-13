package com.tripleying.qwq.MailBox.Events;

import com.tripleying.qwq.MailBox.Mail.*;
import com.tripleying.qwq.MailBox.API.Listener.*;
import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.MailBox;
import java.io.IOException;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MailChange implements Listener {
    
    @EventHandler
    public void onMailSend(MailSendEvent e) throws IOException{
        BaseMail bm = e.getMail();
        String type = bm.getType();
        String typeName = bm.getTypeName();
        String file;
        if(e.hasFile()){
            file = "附件: "+((BaseFileMail)bm).getFileName();
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
                Bukkit.getOnlinePlayers().stream().filter((p) -> (MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.system"))).forEachOrdered((p) -> {
                    MailBoxAPI.sendTips(p);
                }); break;
            case "permission":
                Bukkit.getOnlinePlayers().forEach((p) -> {
                    String perm = ((MailPermission)bm).getPermission();
                    if (MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.permission") && p.hasPermission(perm)) {
                        MailBoxAPI.sendTips(p);
                    }
                }); break;
            case "date":
                if(bm.isStart()){
                    Bukkit.getOnlinePlayers().stream().filter((p) -> (MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.date"))).forEachOrdered((p) -> {
                        MailBoxAPI.sendTips(p);
                    });
                }   break;
            case "times":
                if(((MailTimes)bm).getTimes()>0){
                    Bukkit.getOnlinePlayers().stream().filter((p) -> (MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.times"))).forEachOrdered((p) -> {
                        MailBoxAPI.sendTips(p);
                    });
                }   break;
            case "player":
                List<String> pl = ((MailPlayer)bm).getRecipient();
                Bukkit.getOnlinePlayers().stream().filter((p) -> (MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.player") && pl.contains(p.getName()))).forEachOrdered((p) -> {
                    MailBoxAPI.sendTips(p);
                }); break;
        }
    }
    
    @EventHandler
    public void onMailCollect(MailCollectEvent e){
        BaseMail bm = e.getMail();
        String type = bm.getType();
        // 更新邮件列表
        MailBox.updateMailList(null, type);
        // 更新玩家可领取邮件列表
        MailBox.updateRelevantMailList(e.getPlayer(), type);
        // 输出到控制台
        Bukkit.getConsoleSender().sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+e.getPlayer().getName()+"领取了邮件: <"+bm.getTypeName()+" - "+bm.getId()+">");
    }
    
    @EventHandler
    public void onMailDelete(MailDeleteEvent e){
        BaseMail bm = e.getMail();
        String type = bm.getType();
        // 更新邮件列表
        MailBox.updateMailList(e.getPlayer(), type);
        // 输出到控制台
        Bukkit.getConsoleSender().sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+e.getName()+"删除了邮件: <"+bm.getTypeName()+" - "+bm.getId()+">");
    }
    
}