package com.嘤嘤嘤.qwq.MailBox.Original;

import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import com.嘤嘤嘤.qwq.MailBox.MailBox;
import java.util.ArrayList;
import java.util.HashMap;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MailList {
    
    public static void listPlayer(Player p, String type){
        int count = 0;
        HashMap<String, ArrayList<Integer>> idMap = new HashMap();
        for(String t:MailBoxAPI.getAllType()){
            if(MailBoxAPI.hasPlayerPermission(p, "mailbox.see."+t)){
                MailBox.updateRelevantMailList(p, t);
                ArrayList<Integer> id = MailBox.getRelevantMailList(p, t).get("as"+type);
                idMap.put(t, id);
                count += id.size();
            }
        }
        /*ArrayList<Integer> dateid = new ArrayList();
        ArrayList<Integer> systemid = new ArrayList();
        ArrayList<Integer> playerid = new ArrayList();
        ArrayList<Integer> permissionid = new ArrayList();
        if(MailBoxAPI.hasPlayerPermission(p, "mailbox.see.date")){
            MailBox.updateRelevantMailList(p, "date");
            dateid = MailBox.getRelevantMailList(p, "date").get("as"+type);
            count += dateid.size();
        }
        if(MailBoxAPI.hasPlayerPermission(p, "mailbox.see.system")){
            MailBox.updateRelevantMailList(p, "system");
            systemid = MailBox.getRelevantMailList(p, "system").get("as"+type);
            count += systemid.size();
        }
        if(MailBoxAPI.hasPlayerPermission(p, "mailbox.see.permission")){
            MailBox.updateRelevantMailList(p, "permission");
            permissionid = MailBox.getRelevantMailList(p, "permission").get("as"+type);
            count += permissionid.size();
        }
        if(MailBoxAPI.hasPlayerPermission(p, "mailbox.see.player")){
            MailBox.updateRelevantMailList(p, "player");
            playerid = MailBox.getRelevantMailList(p, "player").get("as"+type);
            count += playerid.size();
        }*/
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
        for(String t:MailBoxAPI.getAllType()){
            if(idMap.containsKey(t)){
                for(int mid:idMap.get(t)){
                    TextMail tm = MailBox.getMailHashMap(t).get(mid);
                    StringBuilder str = new StringBuilder("§d"+tm.getTypeName()+" §r"+tm.getTopic());
                    if(tm instanceof FileMail) str.append("§r - §c有附件");
                    TextComponent msg = new TextComponent(str.toString());
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+t+" see "+mid));
                    p.spigot().sendMessage(msg);
                }
            }
        }
        /*for(int mid: dateid){
            StringBuilder str = new StringBuilder("§d"+GlobalConfig.getTypeName("date")+" §r"+MailBox.DATE_LIST.get(mid).getTopic());
            if(MailBox.DATE_LIST.get(mid) instanceof FileMail) str.append("§r - §c有附件");
            TextComponent msg = new TextComponent(str.toString());
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb date see "+mid));
            p.spigot().sendMessage(msg);
        }
        for(int mid: systemid){
            StringBuilder str = new StringBuilder("§d"+GlobalConfig.getTypeName("system")+" §r"+MailBox.SYSTEM_LIST.get(mid).getTopic());
            if(MailBox.SYSTEM_LIST.get(mid) instanceof FileMail) str.append("§r - §c有附件");
            TextComponent msg = new TextComponent(str.toString());
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb system see "+mid));
            p.spigot().sendMessage(msg);
        }
        for(int mid: permissionid){
            StringBuilder str = new StringBuilder("§d"+GlobalConfig.getTypeName("permission")+" §r"+MailBox.PERMISSION_LIST.get(mid).getTopic());
            if(MailBox.PERMISSION_LIST.get(mid) instanceof FileMail) str.append("§r - §c有附件");
            TextComponent msg = new TextComponent(str.toString());
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb permission see "+mid));
            p.spigot().sendMessage(msg);
        }
        for(int mid: playerid){
            StringBuilder str = new StringBuilder("§d"+GlobalConfig.getTypeName("player")+" §r"+MailBox.PLAYER_LIST.get(mid).getTopic());
            if(MailBox.PLAYER_LIST.get(mid) instanceof FileMail) str.append("§r - §c有附件");
            TextComponent msg = new TextComponent(str.toString());
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb player see "+mid));
            p.spigot().sendMessage(msg);
        }*/
    }
    
    public static void listConsole(CommandSender sender){
        int count = 0;
        for(String type:MailBoxAPI.getAllType()){
            MailBox.updateMailList(null, type);
            count += MailBox.getMailHashMap(type).size();
        }
        if(count==0){
            sender.sendMessage("§c当前没有邮件");
            return;
        }else{
            sender.sendMessage("§6共有"+count+"封邮件");
        }
        for(String type:MailBoxAPI.getAllType()){
            MailBox.getMailHashMap(type).forEach((k,v) -> {
                StringBuilder str = new StringBuilder("§d"+v.getTypeName()+" §r- "+k+" - "+v.getTopic());
                if(v instanceof FileMail) str.append("§r - §c有附件");
                sender.sendMessage(str.toString());
            });
        }
        /*MailBox.DATE_LIST.forEach((k,v) -> {
            StringBuilder str = new StringBuilder("§d"+v.getTypeName()+" §r- "+k+" - "+v.getTopic());
            if(v instanceof FileMail) str.append("§r - §c有附件");
            sender.sendMessage(str.toString());
        });
        MailBox.SYSTEM_LIST.forEach((k,v) -> {
            StringBuilder str = new StringBuilder("§d"+v.getTypeName()+" §r- "+k+" - "+v.getTopic());
            if(v instanceof FileMail) str.append("§r - §c有附件");
            sender.sendMessage(str.toString());
        });
        MailBox.PERMISSION_LIST.forEach((k,v) -> {
            StringBuilder str = new StringBuilder("§d"+v.getTypeName()+" §r- "+k+" - "+v.getTopic()+" - §r所需权限: "+v.getPermission());
            if(v instanceof FileMail) str.append("§r - §c有附件");
            sender.sendMessage(str.toString());
        });
        MailBox.PLAYER_LIST.forEach((k,v) -> {
            StringBuilder str = new StringBuilder("§d"+v.getTypeName()+" §r- "+k+" - "+v.getTopic()+" - §r收件人: "+v.getRecipientString());
            if(v instanceof FileMail) str.append("§r - §c有附件");
            sender.sendMessage(str.toString());
        });*/
    }
    
    public static void list(CommandSender sender, String type){
        if(sender instanceof Player){
            listPlayer((Player)sender,type);
        }else{
            listConsole(sender);
        }
    }
}
