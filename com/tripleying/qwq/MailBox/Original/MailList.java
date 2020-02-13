package com.tripleying.qwq.MailBox.Original;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.Mail.BaseFileMail;
import com.tripleying.qwq.MailBox.Mail.BaseMail;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.Message;
import java.util.ArrayList;
import java.util.HashMap;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MailList {
    
    public static void listPlayer(Player p, String playertype){
        HashMap<String, ArrayList<Integer>> idMap = new HashMap();
        int count;
        if(MailBoxAPI.getTrueTypeWhithoutSpecial().stream().noneMatch(t -> MailBoxAPI.hasPlayerPermission(p, "mailbox.see."+t) || p.hasPermission("mailbox.admin.see."+t))){
            count = 0;
        }else{
            count = MailBoxAPI.getTrueTypeWhithoutSpecial().stream().filter((type) -> (MailBoxAPI.hasPlayerPermission(p, "mailbox.see."+type) || p.hasPermission("mailbox.admin.see."+type))).map((type) -> {
                MailBox.updateRelevantMailList(p, type);
                return type;
            }).map((type) -> {
                ArrayList<Integer> id;
                if(playertype.equals("Recipient") && p.hasPermission("mailbox.admin.see."+type)){
                    id = new ArrayList();
                    id.addAll(MailBox.getMailHashMap(type).keySet());
                }else{
                    id = MailBox.getRelevantMailList(p, type).get("as"+playertype);
                }
                idMap.put(type, id);
                return id;
            }).map((id) -> id.size()).reduce(Integer::sum).get();
        }
        if(count==0){
            p.sendMessage(Message.listNullBox.replace("%box%", playertype.equals("Sender") ? Message.listOutBox : Message.listInBox));
            return;
        }else{
            if(playertype.equals("Sender")){
                p.sendMessage(Message.listCountBox.replace("%box%", Message.listOutBox).replace("%count%", Integer.toString(count)));
            }else{
                p.sendMessage(Message.listCountBox.replace("%box%", Message.listInBox).replace("%count%", Integer.toString(count)));
            }
        }
        MailBoxAPI.getTrueTypeWhithoutSpecial().stream().filter((t) -> (idMap.containsKey(t))).forEachOrdered((t) -> {
            idMap.get(t).stream().map((mid) -> {
                BaseMail bm = MailBox.getMailHashMap(t).get(mid);
                StringBuilder str = new StringBuilder("§d"+bm.getTypeName()+" §r"+bm.getTopic());
                if(bm instanceof BaseFileMail) str.append("§r - §c").append(Message.globalHasFile);
                TextComponent msg = new TextComponent(str.toString());
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+t+" see "+mid));
                return msg;
            }).forEachOrdered((msg) -> {
                p.spigot().sendMessage(msg);
            });
        });
        if(p.hasPermission("mailbox.admin.see.cdkey")) MailBox.getMailHashMap("cdkey").values().stream().map((bm) -> {
            StringBuilder str = new StringBuilder("§d"+bm.getTypeName()+" §r"+bm.getTopic());
            if(bm instanceof BaseFileMail) str.append("§r - §c").append(Message.globalHasFile);
            TextComponent msg = new TextComponent(str.toString());
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb cdkey see "+bm.getId()));
            return msg;
        }).forEachOrdered((msg) -> {
            p.spigot().sendMessage(msg);
        });
    }
    
    public static void listConsole(CommandSender sender){
        int count = MailBoxAPI.getTrueType().stream().map((type) -> {
            MailBox.updateMailList(null, type);
            return type;
        }).map((type) -> MailBox.getMailHashMap(type).size()).reduce(Integer::sum).get();
        if(count==0){
            sender.sendMessage(Message.listNullConsole);
            return;
        }else{
            sender.sendMessage(Message.listCountConsole.replace("%count%", Integer.toString(count)));
        }
        MailBoxAPI.getTrueType().forEach((type) -> {
            MailBox.getMailHashMap(type).forEach((k,v) -> {
                StringBuilder str = new StringBuilder("§d"+v.getTypeName()+" §r- "+k+" - "+v.getTopic());
                if(v instanceof BaseFileMail) str.append("§r - §c").append(Message.globalHasFile);
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
