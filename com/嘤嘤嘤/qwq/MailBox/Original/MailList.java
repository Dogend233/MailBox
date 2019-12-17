package com.嘤嘤嘤.qwq.MailBox.Original;

import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.MailBox;
import java.util.ArrayList;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MailList {
    
    public static void listPlayer(Player p, String type){
        int count = 0;
        ArrayList<Integer> systemid = new ArrayList();
        ArrayList<Integer> playerid = new ArrayList();
        ArrayList<Integer> permissionid = new ArrayList();
        if(p.hasPermission("mailbox.see.system")){
            MailBox.updateRelevantMailList(p, "system");
            systemid = MailBox.getRelevantMailList(p, "system").get("as"+type);
            count += systemid.size();
        }
        if(p.hasPermission("mailbox.see.permission")){
            MailBox.updateRelevantMailList(p, "permission");
            permissionid = MailBox.getRelevantMailList(p, "permission").get("as"+type);
            count += permissionid.size();
        }
        if(p.hasPermission("mailbox.see.player")){
            MailBox.updateRelevantMailList(p, "player");
            playerid = MailBox.getRelevantMailList(p, "player").get("as"+type);
            count += playerid.size();
        }
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
        for(int mid: systemid){
            StringBuilder str = new StringBuilder("§d"+GlobalConfig.getTypeName("system")+" §r"+MailBox.MailListSystem.get(mid).getTopic());
            if(MailBox.MailListSystem.get(mid).hasFile()) str.append("§r - §c有附件");
            TextComponent msg = new TextComponent(str.toString());
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb system see "+mid));
            p.spigot().sendMessage(msg);
        }
        for(int mid: permissionid){
            StringBuilder str = new StringBuilder("§d"+GlobalConfig.getTypeName("permission")+" §r"+MailBox.MailListPermission.get(mid).getTopic());
            if(MailBox.MailListPermission.get(mid).hasFile()) str.append("§r - §c有附件");
            TextComponent msg = new TextComponent(str.toString());
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb permission see "+mid));
            p.spigot().sendMessage(msg);
        }
        for(int mid: playerid){
            StringBuilder str = new StringBuilder("§d"+GlobalConfig.getTypeName("player")+" §r"+MailBox.MailListPlayer.get(mid).getTopic());
            if(MailBox.MailListPlayer.get(mid).hasFile()) str.append("§r - §c有附件");
            TextComponent msg = new TextComponent(str.toString());
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb player see "+mid));
            p.spigot().sendMessage(msg);
        }
    }
    
    public static void listConsole(CommandSender sender){
        int count = 0;
        MailBox.updateMailList(null, "system");
        MailBox.updateMailList(null, "permission");
        MailBox.updateMailList(null, "player");
        count += MailBox.MailListSystem.size();
        count += MailBox.MailListPermission.size();
        count += MailBox.MailListPlayer.size();
        if(count==0){
            sender.sendMessage("§c当前没有邮件");
            return;
        }else{
            sender.sendMessage("§6共有"+count+"封邮件");
        }
        MailBox.MailListSystem.forEach((k,v) -> {
            StringBuilder str = new StringBuilder("§d"+v.getTypeName()+" §r- "+k+" - "+v.getTopic());
            if(v.hasFile()) str.append("§r - §c有附件");
            sender.sendMessage(str.toString());
        });
        MailBox.MailListPermission.forEach((k,v) -> {
            StringBuilder str = new StringBuilder("§d"+v.getTypeName()+" §r- "+k+" - "+v.getTopic()+" - §r所需权限: "+v.getPermission());
            if(v.hasFile()) str.append("§r - §c有附件");
            sender.sendMessage(str.toString());
        });
        MailBox.MailListPlayer.forEach((k,v) -> {
            StringBuilder str = new StringBuilder("§d"+v.getTypeName()+" §r- "+k+" - "+v.getTopic()+" - §r收件人: "+v.getRecipientString());
            if(v.hasFile()) str.append("§r - §c有附件");
            sender.sendMessage(str.toString());
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
