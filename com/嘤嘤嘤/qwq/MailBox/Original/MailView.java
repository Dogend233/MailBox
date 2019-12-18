package com.嘤嘤嘤.qwq.MailBox.Original;

import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import com.嘤嘤嘤.qwq.MailBox.MailBox;
import com.嘤嘤嘤.qwq.MailBox.Utils.DateTime;
import com.嘤嘤嘤.qwq.MailBox.Utils.NMS;
import java.util.ArrayList;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
        if(sender instanceof Player){
            if((collectable(type,mid,sender) || deletable(tm, sender))){
                view(tm, (Player)sender);
            }else{
                sender.sendMessage("你不能查看此邮件");
            }
        }else if(sender instanceof ConsoleCommandSender){
            view(tm, (ConsoleCommandSender)sender);
        }else{
            sender.sendMessage("你不能查看此邮件");
        }
    }
    public static void view(TextMail tm, Player p){
        TextComponent firstTC;
        TextComponent secondTC;
        ComponentBuilder CB = null;
        p.sendMessage("====================");
        firstTC = new TextComponent("<"+tm.getTopic()+"§r>");
        // 邮件类型+ID+信息
        if(p.hasPermission("mailbox.content.id")){
            secondTC = new TextComponent(tm.getType()+" - "+tm.getId());
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
        // 邮件内容
        p.sendMessage("  \""+tm.getContent().replace(" ", '\n'+"  ")+"\"");
        if(tm instanceof FileMail){
            FileMail fm = (FileMail)tm;
            fm.getFile();
            p.sendMessage("--------------------");
            firstTC = new TextComponent("§d附件：");
            if(p.hasPermission("mailbox.content.filename")) firstTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fm.getType()+" - "+fm.getFileName())}));
            p.spigot().sendMessage(firstTC);
            if(GlobalConfig.enVault && fm.getCoin()!=0){
                p.sendMessage("  "+GlobalConfig.vaultDisplay+" "+fm.getCoin());
            }
            if(GlobalConfig.enPlayerPoints && fm.getPoint()!=0){
                p.sendMessage("  "+GlobalConfig.playerPointsDisplay+" "+fm.getPoint());
            }
            if(fm.isHasCommand()){
                firstTC = new TextComponent("  §e[执行指令]");
                if(!fm.getCommandDescription().isEmpty()){
                    secondTC = new TextComponent();
                    boolean f = true;
                    for(String s:fm.getCommandDescription()){
                        if(f){
                            secondTC.addExtra(s);
                            f = false;
                        }else{
                            secondTC.addExtra('\n'+s);
                        }
                    }
                    firstTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{secondTC}));
                }
                p.spigot().sendMessage(firstTC);
            }
            if(fm.isHasItem()){
                CB = new ComponentBuilder("  §e附件物品:  §a");
                int count = 0;
                for(ItemStack is:fm.getItemList()){
                    HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM,  new BaseComponent[]{new TextComponent(NMS.Item2Json(is))});
                    TextComponent component;
                    if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()){
                        component = new TextComponent(is.getItemMeta().getDisplayName());
                    }else if(is.hasItemMeta() && is.getItemMeta().hasLocalizedName()){
                        component = new TextComponent(is.getItemMeta().getLocalizedName());
                    }else{
                        component = new TextComponent("[物品-"+(++count)+"]");
                    }
                    component.setHoverEvent(event);
                    CB.append(component);
                    CB.append(" ");
                }
                p.spigot().sendMessage(CB.create());
                CB = null;
            }
            if(collectable(tm.getType(),tm.getId(),p)){
                firstTC = new TextComponent("  §a[领取邮件]");
                firstTC.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+tm.getType()+" collect "+tm.getId()));
                if(CB==null){
                    CB = new ComponentBuilder(firstTC);
                }else{
                    CB.append(firstTC);
                }
            }
        }else{
            tm.Collect(p);
        }
        // 删除
        if(deletable(tm, p)) {
            firstTC = new TextComponent("  §c[删除邮件]");
            firstTC.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+tm.getType()+" delete "+tm.getId()));
            if(CB==null){
                CB = new ComponentBuilder(firstTC);
            }else{
                CB.append(firstTC);
            }
        }
        if(CB!=null){
            p.sendMessage("--------------------");
            p.spigot().sendMessage(CB.create());
        }
        // 发送时间和发件人
        p.sendMessage("§6来自: §a"+tm.getSender()+" - §b"+tm.getDate());
        p.sendMessage("====================");
    }
    public static void view(TextMail tm, ConsoleCommandSender s){
        StringBuilder sb;
        s.sendMessage("====================");
        sb = new StringBuilder("<"+tm.getTopic()+"§r>");
        // 邮件类型+ID+信息
        if(s.hasPermission("mailbox.content.id")){
            sb.append(" - ");
            sb.append(tm.getType());
            sb.append(" - ");
            sb.append(tm.getId());
            s.sendMessage(sb.toString());
            switch (tm.getType()){
                case "player":
                    s.sendMessage(" §6收件人:");
                    for(String re:tm.getRecipient()){
                        s.sendMessage("  §e"+re);
                    }
                    break;
                case "permission":
                    s.sendMessage(" §6所需权限:");
                    s.sendMessage("  §e"+tm.getPermission());
                    break;
            }
        }else{
            s.sendMessage(sb.toString());
        }
        // 邮件内容
        s.sendMessage(" §b邮件内容:");
        for(String c:("\""+tm.getContent()+"\"").split(" ")){
            s.sendMessage("  "+c);
        }
        if(tm instanceof FileMail){
            FileMail fm = (FileMail)tm;
            fm.getFile();
            s.sendMessage("--------------------");
            sb = new StringBuilder("§d附件：");
            if(s.hasPermission("mailbox.content.filename")){
                sb.append(" - ");
                sb.append(fm.getType());
                sb.append(" - ");
                sb.append(fm.getFileName());
            }
            s.sendMessage(sb.toString());
            if(GlobalConfig.enVault && fm.getCoin()!=0){
                s.sendMessage("  "+GlobalConfig.vaultDisplay+" "+fm.getCoin());
            }
            if(GlobalConfig.enPlayerPoints && fm.getPoint()!=0){
                s.sendMessage("  "+GlobalConfig.playerPointsDisplay+" "+fm.getPoint());
            }
            if(fm.isHasCommand()){
                sb = new StringBuilder("  §e[执行指令]");
                if(fm.getCommandDescription().isEmpty()){
                    s.sendMessage(sb.toString());
                }else{
                    sb.delete(0, 1);
                    s.sendMessage(sb.toString());
                    for(String c:fm.getCommandDescription()){
                        s.sendMessage("  "+c);
                    }
                }
                s.sendMessage(" §b此邮件执行以下指令");
                for(String c:fm.getCommandList()){
                    s.sendMessage("  /"+c);
                }
            }
            if(fm.isHasItem()){
                sb = new StringBuilder("  §e附件物品:  §a");
                int count = 0;
                for(ItemStack is:fm.getItemList()){
                    if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()){
                        sb.append(is.getItemMeta().getDisplayName());
                    }else if(is.hasItemMeta() && is.getItemMeta().hasLocalizedName()){
                        sb.append(is.getItemMeta().getLocalizedName());
                    }else{
                        sb.append("[物品-"+(++count)+"]");
                    }
                    sb.append(" ");
                }
                s.sendMessage(sb.toString());
            }
        }
        // 发送时间和发件人
        s.sendMessage("§6来自: §a"+tm.getSender()+" - §b"+tm.getDate());
        s.sendMessage("====================");
    }
    
    // 预览
    public static void preview(TextMail tm, CommandSender sender, ConversationContext cc){
        TextComponent firstTC;
        TextComponent secondTC;
        ComponentBuilder CB = null;
        cc.getForWhom().sendRawMessage("==========邮件预览==========");
        firstTC = new TextComponent("<"+tm.getTopic()+"§r>");
        // 邮件类型+ID+信息
        if(sender.hasPermission("mailbox.content.id")){
            secondTC = new TextComponent(tm.getTypeName()+" - "+tm.getId());
            switch (tm.getType()){
                case "player":
                    for(String re:tm.getRecipient()){
                        secondTC.addExtra('\n'+re);
                    }
                    break;
                case "permission":
                    secondTC.addExtra('\n'+tm.getPermission());
                    break;
            }
            firstTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{secondTC}));
        }
        sender.spigot().sendMessage(firstTC);
        // 邮件内容
        if(sender instanceof Player){
            cc.getForWhom().sendRawMessage("  \""+tm.getContent().replace(" ", '\n'+"  ")+"\"");
        }else if(sender instanceof ConsoleCommandSender){
            cc.getForWhom().sendRawMessage('\n'+"  \""+tm.getContent().replace(" ", '\n'+"  ")+"\"");
        }
        if(tm instanceof FileMail){
            FileMail fm = (FileMail)tm;
            fm.getFile();
            cc.getForWhom().sendRawMessage("--------------------");
            firstTC = new TextComponent("§d附件：");
            if(sender.hasPermission("mailbox.content.filename")){
                firstTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fm.getType()+" - "+fm.getFileName())}));
            }
            sender.spigot().sendMessage(firstTC);
            if(GlobalConfig.enVault && fm.getCoin()!=0){
                cc.getForWhom().sendRawMessage("  "+GlobalConfig.vaultDisplay+" "+fm.getCoin());
            }
            if(GlobalConfig.enPlayerPoints && fm.getPoint()!=0){
                cc.getForWhom().sendRawMessage("  "+GlobalConfig.playerPointsDisplay+" "+fm.getPoint());
            }
            if(fm.isHasCommand()){
                firstTC = new TextComponent("  §e[执行指令]");
                if(!fm.getCommandDescription().isEmpty()){
                    secondTC = new TextComponent();
                    boolean f = true;
                    for(String s:fm.getCommandDescription()){
                        if(f){
                            secondTC.addExtra(s);
                            f = false;
                        }else{
                            secondTC.addExtra('\n'+s);
                        }
                    }
                    if(sender instanceof Player){
                        firstTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{secondTC}));
                    }else if(sender instanceof ConsoleCommandSender){
                        cc.getForWhom().sendRawMessage("----------以下内容只在控制台显示----------");
                        cc.getForWhom().sendRawMessage("  §b指令描述：");
                        sender.spigot().sendMessage(secondTC);
                        cc.getForWhom().sendRawMessage("----------以上内容只在控制台显示----------");
                    }
                }
                sender.spigot().sendMessage(firstTC);
                cc.getForWhom().sendRawMessage("----------以下内容只在预览显示----------");
                firstTC = new TextComponent("  §b此邮件执行以下指令");
                for(String s:fm.getCommandList()){
                    firstTC.addExtra('\n'+"  /"+s);
                }
                sender.spigot().sendMessage(firstTC);
                cc.getForWhom().sendRawMessage("----------以上内容只在预览显示----------");
            }
            if(fm.isHasItem()){
                CB = new ComponentBuilder("  §e附件物品:  §a");
                int count = 0;
                for(ItemStack is:fm.getItemList()){
                    HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM,  new BaseComponent[]{new TextComponent(NMS.Item2Json(is))});
                    TextComponent component;
                    if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()){
                        component = new TextComponent(is.getItemMeta().getDisplayName());
                    }else if(is.hasItemMeta() && is.getItemMeta().hasLocalizedName()){
                        component = new TextComponent(is.getItemMeta().getLocalizedName());
                    }else{
                        component = new TextComponent("[物品-"+(++count)+"]");
                    }
                    component.setHoverEvent(event);
                    CB.append(component);
                    CB.append(" ");
                }
                sender.spigot().sendMessage(CB.create());
                CB = null;
            }
        }
        // 发送时间和发件人
        cc.getForWhom().sendRawMessage("§6来自: §a"+tm.getSender()+" - §b"+DateTime.get("ymdhms"));
        if(tm.getExpandCoin()!=0 || tm.getExpandPoint()!=0){
            boolean f = true;
            cc.getForWhom().sendRawMessage("----------以下内容只在预览显示----------");
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
            cc.getForWhom().sendRawMessage(sb.toString());
            cc.getForWhom().sendRawMessage("----------以上内容只在预览显示----------");
        }
        cc.getForWhom().sendRawMessage("====================");
    }

    // 领取邮件
    public static void collect(String type, int mid, CommandSender sender){
        TextMail tm = getMail(type,mid);
        if(tm==null){
            sender.sendMessage("目标邮件不存在");
            return;
        }
        if(collectable(type,mid,sender)){
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
        TextMail tm = null;
        switch(type){
            case "system":
                tm = MailBox.MailListSystem.get(mid);
                break;
            case "permission":
                tm = MailBox.MailListPermission.get(mid);
                break;
            case "player":
                tm = MailBox.MailListPlayer.get(mid);
                break;
        }
        return tm;
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
        if(sender instanceof Player) return (sender.hasPermission("mailbox.admin.delete."+tm.getType()) || ((tm.getType().equals("player")) && tm.getSender().equals(sender.getName()) && sender.hasPermission("mailbox.delete.player")));
        else return (sender instanceof ConsoleCommandSender);
    }
}
