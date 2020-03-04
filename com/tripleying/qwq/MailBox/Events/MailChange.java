package com.tripleying.qwq.MailBox.Events;

import com.tripleying.qwq.MailBox.API.Event.MailSendEvent;
import com.tripleying.qwq.MailBox.API.Event.MailCollectEvent;
import com.tripleying.qwq.MailBox.API.Event.MailDeleteEvent;
import com.tripleying.qwq.MailBox.Mail.*;
import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.OuterMessage;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.io.IOException;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MailChange implements Listener {
    
    @EventHandler
    public void onInputTimesKey(AsyncPlayerChatEvent e){
        String m = e.getMessage();
        if(MailBox.KEYTIMES_KEY.containsKey(m)){
            Player p = e.getPlayer();
            MailBox.KEYTIMES_KEY.get(m).forEach(k -> {if(MailBox.getRelevantMailList(p, "keytimes").get("asRecipient").contains(k)) MailBox.getMailHashMap("keytimes").get(k).Collect(p);});
            
        }
    }
    
    @EventHandler
    public void onMailSend(MailSendEvent e) throws IOException{
        BaseMail bm = e.getMail();
        String type = bm.getType();
        String typeName = bm.getTypeName();
        String file;
        if(bm instanceof BaseFileMail){
            file = OuterMessage.globalHasFile+": "+((BaseFileMail)bm).getFileName();
        }else{
            file = OuterMessage.globalNoFile;
        }
        // 更新邮件列表
        MailBox.updateMailList(e.getPlayer(), type);
        // 输出到控制台
        Bukkit.getConsoleSender().sendMessage(OuterMessage.mailSend.replace("%sender%", bm.getSender()).replace("%type%", type).replace("%file%", file));
        if(GlobalConfig.tips.isEmpty()) return;
        String msg = OuterMessage.tipsNew.replace("%sender%", bm.getSender()).replace("%type%", typeName);
        switch (type){
            case "system":
                Bukkit.getOnlinePlayers().stream().filter((p) -> (MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.system"))).forEachOrdered((p) -> {
                    MailUtil.sendTips(p, msg.replace("%player", p.getName()),"");
                }); break;
            case "permission":
                Bukkit.getOnlinePlayers().forEach((p) -> {
                    String perm = ((MailPermission)bm).getPermission();
                    if (MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.permission") && p.hasPermission(perm)) {
                        MailUtil.sendTips(p, msg.replace("%player", p.getName()),"");
                    }
                }); break;
            case "date":
                if(((MailDate)bm).isStart()){
                    Bukkit.getOnlinePlayers().stream().filter((p) -> (MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.date"))).forEachOrdered((p) -> {
                        MailUtil.sendTips(p, msg.replace("%player", p.getName()),"");
                    });
                }   break;
            case "keytimes":
            case "times":
                if(((MailTimes)bm).getTimes()>0){
                    Bukkit.getOnlinePlayers().stream().filter((p) -> (MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.times"))).forEachOrdered((p) -> {
                        if(bm instanceof MailKeyTimes) MailUtil.sendTips(p, msg.replace("%player", p.getName()), ((MailKeyTimes)bm).getKey());
                        else MailUtil.sendTips(p, msg.replace("%player", p.getName()),"");
                    });
                }   break;
            case "player":
                List<String> pl = ((MailPlayer)bm).getRecipient();
                Bukkit.getOnlinePlayers().stream().filter((p) -> (MailBoxAPI.hasPlayerPermission(p, "mailbox.collect.player") && pl.contains(p.getName()))).forEachOrdered((p) -> {
                    MailUtil.sendTips(p, msg.replace("%player", p.getName()),"");
                }); break;
        }
    }
    
    @EventHandler
    public void onMailCollect(MailCollectEvent e){
        BaseMail bm = e.getMail();
        String type = bm.getType();
        Player p = e.getPlayer();
        // 更新邮件列表
        MailBox.updateMailList(null, type);
        // 更新玩家可领取邮件列表
        MailBox.updateRelevantMailList(p, type);
        // 输出到控制台
        Bukkit.getConsoleSender().sendMessage(OuterMessage.mailCollect.replace("%player%", p.getName()).replace("%type%", bm.getTypeName()).replace("%id%", Integer.toString(bm.getId())));
    }
    
    @EventHandler
    public void onMailDelete(MailDeleteEvent e){
        BaseMail bm = e.getMail();
        String deleter = e.getName();
        String type = bm.getType();
        // 更新邮件列表
        MailBox.updateMailList(e.getPlayer(), type);
        // 输出到控制台
        Bukkit.getConsoleSender().sendMessage(OuterMessage.mailDelete.replace("%deleter%", deleter).replace("%type%", bm.getTypeName()).replace("%id%", Integer.toString(bm.getId())));
    }
    
}