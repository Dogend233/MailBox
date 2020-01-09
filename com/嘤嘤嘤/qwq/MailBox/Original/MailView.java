package com.嘤嘤嘤.qwq.MailBox.Original;

import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import com.嘤嘤嘤.qwq.MailBox.MailBox;
import com.嘤嘤嘤.qwq.MailBox.Utils.DateTime;
import com.嘤嘤嘤.qwq.MailBox.Utils.NMS;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MailView {
    
    // 查看邮件
    public static void view(String type, int mid, CommandSender sender){
        TextMail tm = getMail(type,mid);
        if(tm==null){
            sender.sendMessage("目标邮件不存在");
            return;
        }
        if(MailBoxAPI.isExpired(tm)){
            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件已过期，自动删除");
            tm.Delete((Player)sender);
            return;
        }
        if(!MailBoxAPI.isStart(tm) && !sender.hasPermission("mailbox.admin.see.date")){
            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"此邮件现在还不能领取");
            return;
        }
        if(sender instanceof Player){
            if((collectable(type,mid,sender) || deletable(tm, sender))){
                view(tm, (Player)sender);
            }else{
                sender.sendMessage("你不能查看此邮件");
            }
        }else if(sender instanceof ConsoleCommandSender){
            view(tm, sender);
        }else{
            sender.sendMessage("你不能查看此邮件");
        }
    }
    public static void view(TextMail tm, Player p){
        TextComponent firstTC;
        ComponentBuilder CB = null;
        boolean low1_12 = true;
        p.sendMessage("====================");
        viewTopic(tm, p);
        // 邮件内容
        p.sendMessage("  \""+tm.getContent().replace(" ", '\n'+"  ")+"\"");
        if(tm instanceof FileMail){
            FileMail fm = (FileMail)tm;
            if(fm.readFile()){
                p.sendMessage("--------------------");
                viewFile(fm,p);
                if(GlobalConfig.enVault && fm.getCoin()!=0){
                    p.sendMessage("  "+GlobalConfig.vaultDisplay+" "+fm.getCoin());
                }
                if(GlobalConfig.enPlayerPoints && fm.getPoint()!=0){
                    p.sendMessage("  "+GlobalConfig.playerPointsDisplay+" "+fm.getPoint());
                }
                if(fm.isHasCommand()){
                    viewCommand(fm.getCommandDescription(),p);
                    if(p.hasPermission("mailbox.content.command")) viewCommandDescription(fm.getCommandList(), p, null);;
                }
                if(fm.isHasItem()){
                    viewItem(fm.getItemList(), p);
                }
                if(collectable(tm.getType(),tm.getId(),p)){
                    firstTC = new TextComponent("  §a[领取邮件]");
                    firstTC.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+tm.getType()+" collect "+tm.getId()));
                    if(GlobalConfig.lowServer1_12){
                        p.sendMessage("--------------------");
                        low1_12 = false;
                        p.spigot().sendMessage(firstTC);
                    }else{
                        if(CB==null){
                            CB = new ComponentBuilder(firstTC);
                        }else{
                            CB.append(firstTC);
                        }
                    }
                }
            }else{
                p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"获取附件信息失败！");
            }
        }else{
            tm.Collect(p);
        }
        // 删除
        if(deletable(tm, p)) {
            firstTC = new TextComponent("  §c[删除邮件]");
            firstTC.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+tm.getType()+" delete "+tm.getId()));
            if(GlobalConfig.lowServer1_12){
                if(low1_12) p.sendMessage("--------------------");
                p.spigot().sendMessage(firstTC);
            }else{
                if(CB==null){
                    CB = new ComponentBuilder(firstTC);
                }else{
                    CB.append(firstTC);
                }
            }
        }
        if(CB!=null){
            p.sendMessage("--------------------");
            p.spigot().sendMessage(CB.create());
        }
        // 发送时间和发件人
        viewSenderAndTime(tm, p, null);
        p.sendMessage("====================");
    }
    public static void view(TextMail tm, CommandSender s){
        s.sendMessage("====================");
        viewTopic(tm, s, null);
        // 邮件内容
        s.sendMessage(" §b邮件内容:");
        for(String c:("\""+tm.getContent()+"\"").split(" ")){
            s.sendMessage("  "+c);
        }
        if(tm instanceof FileMail){
            FileMail fm = (FileMail)tm;
            if(fm.readFile()){
                s.sendMessage("--------------------");
                viewFile(fm,s,null);
                if(GlobalConfig.enVault && fm.getCoin()!=0){
                    s.sendMessage("  "+GlobalConfig.vaultDisplay+" "+fm.getCoin());
                }
                if(GlobalConfig.enPlayerPoints && fm.getPoint()!=0){
                    s.sendMessage("  "+GlobalConfig.playerPointsDisplay+" "+fm.getPoint());
                }
                if(fm.isHasCommand()){
                    viewCommand(fm.getCommandList(), fm.getCommandDescription(), s, null);
                }
                if(fm.isHasItem()){
                    viewItem(fm.getItemList(), s, null);
                }
            }else{
                s.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"获取附件信息失败！");
            }
        }
        // 发送时间和发件人
        viewSenderAndTime(tm, s, null);
        s.sendMessage("====================");
    }
    
    // 预览
    public static void preview(TextMail tm, CommandSender sender, ConversationContext cc){
        Conversable who = cc.getForWhom();
        who.sendRawMessage("==========邮件预览==========");
        // 邮件类型+ID+信息
        if(sender instanceof Player){
            viewTopic(tm, (Player)sender);
        }else{
            viewTopic(tm, sender, who);
        }
        // 邮件内容
        if(sender instanceof Player){
            who.sendRawMessage("  \""+tm.getContent().replace(" ", '\n'+"  ")+"\"");
        }else{
            who.sendRawMessage('\n'+"  \""+tm.getContent().replace(" ", '\n'+"  ")+"\"");
        }
        if(tm instanceof FileMail){
            FileMail fm = (FileMail)tm;
            who.sendRawMessage("--------------------");
            if(sender instanceof Player){
                viewFile(fm,(Player)sender);
            }else{
                viewFile(fm,sender,who);
            }
            if(GlobalConfig.enVault && fm.getCoin()!=0){
                who.sendRawMessage("  "+GlobalConfig.vaultDisplay+" "+fm.getCoin());
            }
            if(GlobalConfig.enPlayerPoints && fm.getPoint()!=0){
                who.sendRawMessage("  "+GlobalConfig.playerPointsDisplay+" "+fm.getPoint());
            }
            if(fm.isHasCommand()){
                if(sender instanceof Player){
                    viewCommand(fm.getCommandDescription(), (Player)sender);
                    viewCommandDescription(fm.getCommandList(), sender, who);
                }else{
                    viewCommand(fm.getCommandList(), fm.getCommandDescription(), sender, cc.getForWhom());
                }
            }
            if(fm.isHasItem()){
                if(sender instanceof Player){
                    viewItem(fm.getItemList(),(Player)sender);
                }else{
                    viewItem(fm.getItemList(), null, cc);
                }
            }
        }
        // 发送时间和发件人
        viewSenderAndTime(tm, sender, who);
        if(tm.getExpandCoin()!=0 || tm.getExpandPoint()!=0){
            boolean f = true;
            who.sendRawMessage("----------以下内容只在预览显示----------");
            StringBuilder sb = new StringBuilder("§6发送此邮件");
            if(tm.getExpandCoin()!=0){
                sb.append("消耗: ");
                sb.append(tm.getExpandCoin());
                sb.append(' ');
                sb.append(GlobalConfig.vaultDisplay);
                f = false;
            }
            if(tm.getExpandPoint()!=0){
                if(f){
                    sb.append("消耗: ");
                }else{
                    sb.append("和: ");
                }
                sb.append(tm.getExpandPoint());
                sb.append(' ');
                sb.append(GlobalConfig.playerPointsDisplay);
            }
            who.sendRawMessage(sb.toString());
            who.sendRawMessage("----------以上内容只在预览显示----------");
        }
        who.sendRawMessage("====================");
    }
    
    public static void viewTopic(TextMail tm, Player p){
        TextComponent firstTC = new TextComponent("<"+tm.getTopic()+"§r>");
        // 邮件类型+ID+信息
        if(p.hasPermission("mailbox.content.id")){
            TextComponent secondTC = new TextComponent(tm.getType()+" - "+tm.getId());
            switch (tm.getType()){
                case "player":
                    secondTC.addExtra('\n'+"  收件人:");
                    for(String re:tm.getRecipient()){
                        secondTC.addExtra('\n'+"  "+re);
                    }
                    break;
                case "permission":
                    secondTC.addExtra('\n'+"  所需权限:");
                    secondTC.addExtra('\n'+"  "+tm.getPermission());
                    break;
            }
            firstTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{secondTC}));
        }
        p.spigot().sendMessage(firstTC);
    }
    public static void viewTopic(TextMail tm, CommandSender s, Conversable who){
        StringBuilder sb = new StringBuilder("<"+tm.getTopic()+"§r>");
        if(s.hasPermission("mailbox.content.id")){
            sb.append(" - ");
            sb.append(tm.getType());
            sb.append(" - ");
            sb.append(tm.getId());
            if(who==null)  s.sendMessage(sb.toString());
            else who.sendRawMessage(sb.toString());
            switch (tm.getType()){
                case "player":
                    sb = new StringBuilder("  §6收件人:§e");
                        for(String re:tm.getRecipient()){
                            sb.append("  ");
                            sb.append(re);
                        }
                        if(who==null)  s.sendMessage(sb.toString());
                        else who.sendRawMessage(sb.toString());
                        break;
                case "permission":
                    if(who==null)  s.sendMessage(" §6所需权限:" + "  §e"+tm.getPermission());
                    else who.sendRawMessage(" §6所需权限:" + "  §e"+tm.getPermission());
                    break;
            }
        }else{
            if(who==null)  s.sendMessage(sb.toString());
            else who.sendRawMessage(sb.toString());
        }
    }
    
    public static void viewFile(FileMail fm, Player p){
        TextComponent firstTC = new TextComponent("§d附件：");
        if(p.hasPermission("mailbox.content.filename")){
            firstTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fm.getType()+" - "+fm.getFileName())}));
        }
        p.spigot().sendMessage(firstTC);
    }
    public static void viewFile(FileMail fm, CommandSender s, Conversable who){
        StringBuilder sb = new StringBuilder("§d附件：");
        if(s.hasPermission("mailbox.content.filename")){
            sb.append(" - ");
            sb.append(fm.getType());
            sb.append(" - ");
            sb.append(fm.getFileName());
        }
        if(who==null) s.sendMessage(sb.toString());
        else who.sendRawMessage(sb.toString());
    }
    
    public static void viewItem(ArrayList<ItemStack> isl, Player p){
        ComponentBuilder CB = new ComponentBuilder("  §e附件物品:  §a");
        if(GlobalConfig.lowServer1_12) p.spigot().sendMessage(CB.create());
        int count = 0;
        for(ItemStack is:isl){
            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM,  new BaseComponent[]{new TextComponent(NMS.Item2Json(is))});
            TextComponent component;
            String name = MailBoxAPI.getItemName(is);
            if(name.equals("")){
                component = new TextComponent("[物品-"+(++count)+"]");
            }else{
                component = new TextComponent(name);
            }
            component.setHoverEvent(event);
            if(GlobalConfig.lowServer1_12){
                p.spigot().sendMessage(component);
            }else{
                CB.append(component);
                CB.append(" ");
            }
        }
        if(!GlobalConfig.lowServer1_12) p.spigot().sendMessage(CB.create());
    }
    public static void viewItem(ArrayList<ItemStack> isl, CommandSender s, ConversationContext cc){
        StringBuilder sb = new StringBuilder("  §e附件物品:  §a");
        int count = 0;
        for(ItemStack is:isl){
            String name = MailBoxAPI.getItemName(is);
            if(name.equals("")){
                sb.append("[物品-");
                sb.append((++count));
                sb.append("]");
            }else{
                sb.append(name);
            }
            sb.append(" ");
        }
        if(cc==null){
            s.sendMessage(sb.toString());
        }else{
            cc.getForWhom().sendRawMessage(sb.toString());
        }
    }
    
    public static void viewCommand(List<String> desc, Player p){
        TextComponent tc = new TextComponent("  §e[执行指令]");
        if(!desc.isEmpty()){
            TextComponent hover = new TextComponent();
            boolean f = true;
            for(String s:desc){
                if(f){
                    hover.addExtra(s);
                    f = false;
                }else{
                    hover.addExtra('\n'+s);
                }
            }
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{hover}));
        }
        p.spigot().sendMessage(tc);
    }
    public static void viewCommand(List<String> cmd, List<String> desc, CommandSender s, Conversable who){
        StringBuilder sb = new StringBuilder("  §e[执行指令]");
        if(desc.isEmpty()){
            if(who==null){
                s.sendMessage(sb.toString());
            }else{
                who.sendRawMessage(sb.toString());
            }
        }else{
            sb.delete(0, 1);
            if(who==null){
                s.sendMessage(sb.toString());
                for(String c:desc){
                    s.sendMessage("  "+c);
                }
            }else{
                who.sendRawMessage(sb.toString());
                for(String c:desc){
                    who.sendRawMessage("  "+c);
                }
            }
        }
        viewCommandDescription(cmd, s, who);
    }
    
    public static void viewCommandDescription(List<String> cmd, CommandSender s, Conversable who){
        if(who==null){
            s.sendMessage(" §b此邮件执行以下指令");
            for(String c:cmd){
                s.sendMessage("  /"+c);
            }
        }else{
            who.sendRawMessage(" §b此邮件执行以下指令");
            for(String c:cmd){
                who.sendRawMessage("  /"+c);
            }
        }
    }
    
    public static void viewSenderAndTime(TextMail tm, CommandSender s, Conversable who){
        StringBuilder str = new StringBuilder("§6来自: §a"+tm.getSender()+" - §b");
        if(tm.getDate().equals("0")){
            str.append(DateTime.get("ymdhms"));
        }else{
            str.append(tm.getDate());
        }
        if(tm.getType().equals("date") && !tm.getDeadline().equals("0")){
            str.append(" - ");
            str.append(tm.getDeadline());
        }
        if(who==null){
            s.sendMessage(str.toString());
        }else{
            who.sendRawMessage(str.toString());
        }
    }

    // 领取邮件
    public static void collect(String type, int mid, CommandSender sender){
        TextMail tm = getMail(type,mid);
        if(tm==null){
            sender.sendMessage("目标邮件不存在");
            return;
        }
        if(collectable(type,mid,sender) && MailBoxAPI.isStart(tm)){
            tm.Collect((Player)sender);
        }else{
            sender.sendMessage("你不能领取此邮件");
        }
    }
    
    // 删除邮件
    public static void delete(String type, int mid, CommandSender sender){
        TextMail tm = getMail(type,mid);
        if(tm==null){
            sender.sendMessage("目标邮件不存在");
            return;
        }
        if(deletable(tm,sender)){
            if(sender instanceof Player){
                tm.Delete((Player)sender);
            }else{
                tm.Delete(null);
            }
        }else{
            sender.sendMessage("你不能删除此邮件");
        }
    }
    
     // 获取此封邮件
    private static TextMail getMail(String type, int mid){
        return MailBox.getMailHashMap(type).get(mid);
    }
    
    // 获取玩家是否可以领取这封邮件
    private static boolean collectable(String type, int mid, CommandSender sender){
        if(sender instanceof Player){
            Player p = (Player)sender;
            ArrayList<Integer> l = MailBox.getRelevantMailList(p, type).get("asRecipient");
            return l.contains(mid);
        }else{
            return false;
        }
    }
    
    // 获取玩家是否可以删除这封邮件
    private static boolean deletable(TextMail tm, CommandSender sender){
        if(sender instanceof Player) return (sender.hasPermission("mailbox.admin.delete."+tm.getType()) || ((tm.getType().equals("player")) && tm.getSender().equals(sender.getName()) && MailBoxAPI.hasPlayerPermission(sender, "mailbox.delete.player")));
        else return (sender instanceof ConsoleCommandSender);
    }
}
