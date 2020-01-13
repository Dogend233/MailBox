package com.tripleying.qwq.MailBox.Original;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.Mail.BaseFileMail;
import com.tripleying.qwq.MailBox.Mail.BaseMail;
import com.tripleying.qwq.MailBox.MailBox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MailList {
    
    public static void listPlayer(Player p, String type){
        HashMap<String, ArrayList<Integer>> idMap = new HashMap();
        int count = MailBoxAPI.getTrueTypeWhithoutSpecial().stream().filter((t) -> (MailBoxAPI.hasPlayerPermission(p, "mailbox.see."+t))).map((t) -> {
            MailBox.updateRelevantMailList(p, t);
            return t;
        }).map((t) -> {
            ArrayList<Integer> id = MailBox.getRelevantMailList(p, t).get("as"+type);
            idMap.put(t, id);
            return id;
        }).map((id) -> id.size()).reduce(Integer::sum).get();
        if(count==0){
            if(type.equals("Sender")){
                p.sendMessage("§c您的发件箱没有邮件");
            }else{
                p.sendMessage("§c您的收件箱没有邮件");
            }
            return;
        }else{
            if(type.equals("Sender")){
                p.sendMessage("§6发件箱共有"+count+"封邮件 (点击对应邮件查看)：");
            }else{
                p.sendMessage("§6收件箱共有"+count+"封邮件 (点击对应邮件查看)：");
            }
        }
        MailBoxAPI.getTrueTypeWhithoutSpecial().stream().filter((t) -> (idMap.containsKey(t))).forEachOrdered((t) -> {
            idMap.get(t).stream().map((mid) -> {
                BaseMail bm = MailBox.getMailHashMap(t).get(mid);
                StringBuilder str = new StringBuilder("§d"+bm.getTypeName()+" §r"+bm.getTopic());
                if(bm instanceof BaseFileMail) str.append("§r - §c有附件");
                TextComponent msg = new TextComponent(str.toString());
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+t+" see "+mid));
                return msg;
            }).forEachOrdered((msg) -> {
                p.spigot().sendMessage(msg);
            });
        });
        if(p.hasPermission("mailbox.admin.see.cdkey")) for(BaseMail bm:MailBox.getMailHashMap("cdkey").values()){
            StringBuilder str = new StringBuilder("§d"+bm.getTypeName()+" §r"+bm.getTopic());
            if(bm instanceof BaseFileMail) str.append("§r - §c有附件");
            TextComponent msg = new TextComponent(str.toString());
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb cdkey see "+bm.getId()));
            p.spigot().sendMessage(msg);
        }
    }
    
    public static void listConsole(CommandSender sender){
        int count = MailBoxAPI.getTrueType().stream().map((type) -> {
            MailBox.updateMailList(null, type);
            return type;
        }).map((type) -> MailBox.getMailHashMap(type).size()).reduce(Integer::sum).get();
        if(count==0){
            sender.sendMessage("§c当前没有邮件");
            return;
        }else{
            sender.sendMessage("§6共有"+count+"封邮件");
        }
        MailBoxAPI.getTrueType().forEach((type) -> {
            MailBox.getMailHashMap(type).forEach((k,v) -> {
                StringBuilder str = new StringBuilder("§d"+v.getTypeName()+" §r- "+k+" - "+v.getTopic());
                if(v instanceof BaseFileMail) str.append("§r - §c有附件");
                sender.sendMessage(str.toString());
            });
        });
    }
    
    public static void list(CommandSender sender, String type){
        if(sender instanceof Player){
            listPlayer((Player)sender,type);
        }else{
            listConsole(sender);
        }
    }
}
