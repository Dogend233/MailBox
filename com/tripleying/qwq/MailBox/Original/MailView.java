package com.tripleying.qwq.MailBox.Original;

import com.tripleying.qwq.MailBox.Mail.BaseMail;
import com.tripleying.qwq.MailBox.Mail.MailDate;
import com.tripleying.qwq.MailBox.Mail.MailPlayer;
import com.tripleying.qwq.MailBox.Mail.BaseFileMail;
import com.tripleying.qwq.MailBox.Mail.MailPermission;
import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Mail.MailCdkey;
import com.tripleying.qwq.MailBox.Mail.MailKeyTimes;
import com.tripleying.qwq.MailBox.Mail.MailTemplate;
import com.tripleying.qwq.MailBox.Mail.MailTimes;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.Message;
import com.tripleying.qwq.MailBox.Utils.DateTime;
import com.tripleying.qwq.MailBox.Utils.Reflection;
import java.util.ArrayList;
import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
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
        if(sender==null) return;
        if(type.equals("cdkey") && !sender.hasPermission("mailbox.admin.see.cdkey")){
            sender.sendMessage(Message.globalNoPermission);
            return;
        }
        BaseMail bm = getMail(type,mid);
        if(bm==null){
            sender.sendMessage(Message.mailNotMail);
            return;
        }
        if(bm.ExpireValidate()){
            sender.sendMessage(Message.mailExpire.replace("%para%",""));
            bm.Delete(sender instanceof Player ? (Player)sender : null);
            return;
        }
        if(bm instanceof MailDate && !bm.isStart() && !sender.hasPermission("mailbox.admin.see.date")){
            sender.sendMessage(Message.mailNoStart);
            return;
        }
        if(sender instanceof Player){
            if((sender.hasPermission("mailbox.admin.see."+type) || collectable(bm,sender) || deletable(bm, sender))){
                view(bm, (Player)sender);
            }else{
                sender.sendMessage(Message.globalNoPermission);
            }
        }else if(sender instanceof ConsoleCommandSender){
            view(bm, sender);
        }else{
            sender.sendMessage(Message.globalNoPermission);
        }
    }
    public static void view(BaseMail bm, Player p){
        List<BaseComponent> lbc = new ArrayList();
        p.sendMessage("====================");
        viewTopic(bm, p);
        // 邮件内容
        p.sendMessage("  \""+(GlobalConfig.enPlaceholderAPI ? PlaceholderAPI.setPlaceholders(p, bm.getContent()) : bm.getContent()).replace(" ", '\n'+"  ")+"\"");
        if(bm instanceof BaseFileMail){
            BaseFileMail fm = (BaseFileMail)bm;
            if(fm.readFile()){
                p.sendMessage("--------------------");
                viewFile(fm,p);
                if(GlobalConfig.enVault && fm.getCoin()!=0){
                    p.sendMessage("  "+Message.moneyVault+" "+fm.getCoin());
                }
                if(GlobalConfig.enPlayerPoints && fm.getPoint()!=0){
                    p.sendMessage("  "+Message.moneyPlayerpoints+" "+fm.getPoint());
                }
                if(fm.isHasCommand()){
                    viewCommand(fm.getCommandDescription(),p);
                    if(p.hasPermission("mailbox.content.command")) viewCommandTruth(fm.getCommandList(), p, null);
                }
                if(fm.isHasItem()){
                    viewItem(fm.getItemList(), p);
                }
                if(bm.getType().equals("cdkey")){
                    if(p.hasPermission("mailbox.admin.create.cdkey")){
                        if(!lbc.isEmpty()) lbc.add(new TextComponent("  "));
                        TextComponent tc = new TextComponent(Message.cdkeyCreate);
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+bm.getType()+" create "+bm.getId()));
                        lbc.add(tc);
                    }
                    if(p.hasPermission("mailbox.admin.export.cdkey")){
                        if(!lbc.isEmpty()) lbc.add(new TextComponent("  "));
                        TextComponent tc = new TextComponent("  "+Message.cdkeyExport);
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+bm.getType()+" export "+bm.getId()));
                        lbc.add(tc);
                    }
                }
                if(collectable(bm,p)){
                    if(!lbc.isEmpty()) lbc.add(new TextComponent("  "));
                    TextComponent tc = new TextComponent("  "+Message.commandCollect);
                    tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+bm.getType()+" collect "+bm.getId()));
                    lbc.add(tc);
                }
            }else{
                p.sendMessage(Message.fileFailed);
            }
        }else{
            bm.Collect(p);
        }
        // 删除
        if(deletable(bm, p)) {
            if(!lbc.isEmpty()) lbc.add(new TextComponent("  "));
            TextComponent tc = new TextComponent("  "+Message.commandDelete);
            tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+bm.getType()+" delete "+bm.getId()));
            lbc.add(tc);
        }
        if(!lbc.isEmpty()){
            p.sendMessage("--------------------");
            BaseComponent[] bc = new BaseComponent[lbc.size()];
            lbc.toArray(bc);
            p.spigot().sendMessage(bc);
        }
        // 发送时间和发件人
        viewSenderAndTime(bm, p, null);
        p.sendMessage("====================");
    }
    public static void view(BaseMail bm, CommandSender s){
        s.sendMessage("====================");
        viewTopic(bm, s, null);
        // 邮件内容
        s.sendMessage(" §b"+Message.globalContent+":");
        for(String c:("\""+(GlobalConfig.enPlaceholderAPI ? PlaceholderAPI.setPlaceholders(null, bm.getContent()) : bm.getContent())+"\"").split(" ")){
            s.sendMessage("  "+c);
        }
        if(bm instanceof BaseFileMail){
            BaseFileMail fm = (BaseFileMail)bm;
            if(fm.readFile()){
                s.sendMessage("--------------------");
                viewFile(fm,s,null);
                if(GlobalConfig.enVault && fm.getCoin()!=0){
                    s.sendMessage("  "+Message.moneyVault+" "+fm.getCoin());
                }
                if(GlobalConfig.enPlayerPoints && fm.getPoint()!=0){
                    s.sendMessage("  "+Message.moneyPlayerpoints+" "+fm.getPoint());
                }
                if(fm.isHasCommand()){
                    viewCommand(fm.getCommandList(), fm.getCommandDescription(), s, null);
                }
                if(fm.isHasItem()){
                    viewItem(fm.getItemList(), s, null);
                }
            }else{
                s.sendMessage(Message.fileFailed);
            }
        }
        // 发送时间和发件人
        viewSenderAndTime(bm, s, null);
        s.sendMessage("====================");
    }
    
    // 预览
    public static void preview(BaseMail bm, CommandSender sender, ConversationContext cc){
        Conversable who = cc.getForWhom();
        who.sendRawMessage("=========="+Message.globalPreview+"==========");
        // 邮件类型+ID+信息
        if(sender instanceof Player){
            viewTopic(bm, (Player)sender);
        }else{
            viewTopic(bm, sender, who);
        }
        // 邮件内容
        if(sender instanceof Player){
            who.sendRawMessage("  \""+bm.getContent().replace(" ", '\n'+"  ")+"\"");
        }else{
            who.sendRawMessage('\n'+"  \""+bm.getContent().replace(" ", '\n'+"  ")+"\"");
        }
        if(bm instanceof BaseFileMail){
            BaseFileMail fm = (BaseFileMail)bm;
            who.sendRawMessage("--------------------");
            if(sender instanceof Player){
                viewFile(fm,(Player)sender);
            }else{
                viewFile(fm,sender,who);
            }
            if(GlobalConfig.enVault && fm.getCoin()!=0){
                who.sendRawMessage("  "+Message.moneyVault+" "+fm.getCoin());
            }
            if(GlobalConfig.enPlayerPoints && fm.getPoint()!=0){
                who.sendRawMessage("  "+Message.moneyPlayerpoints+" "+fm.getPoint());
            }
            if(fm.isHasCommand()){
                if(sender instanceof Player){
                    viewCommand(fm.getCommandDescription(), (Player)sender);
                    viewCommandTruth(fm.getCommandList(), sender, who);
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
        viewSenderAndTime(bm, sender, who);
        if(bm.getExpandCoin()!=0 || bm.getExpandPoint()!=0){
            boolean f = true;
            StringBuilder sb = new StringBuilder();
            if(bm.getExpandCoin()!=0){
                sb.append(Message.moneyExpand).append(": ").append(bm.getExpandCoin()).append(' ').append(Message.moneyVault);
                f = false;
            }
            if(bm.getExpandPoint()!=0){
                if(f){
                    sb.append(Message.moneyExpand).append(": ");
                }else{
                    sb.append(", ");
                }
                sb.append(bm.getExpandPoint()).append(' ').append(Message.moneyPlayerpoints);
            }
            who.sendRawMessage(sb.toString());
        }
        who.sendRawMessage("====================");
    }
    
    public static void viewTopic(BaseMail bm, Player p){
        TextComponent firstTC = new TextComponent("<"+bm.getTopic()+"§r>");
        if(bm instanceof MailTimes) firstTC.addExtra(" "+Message.timesTimes+": "+((MailTimes)bm).getTimes());
        if(bm instanceof MailKeyTimes) firstTC.addExtra('\n'+" "+Message.keytimesKey+": "+((MailKeyTimes)bm).getKey());
        // 邮件类型+ID+信息
        if(p.hasPermission("mailbox.content.id")){
            TextComponent secondTC = new TextComponent(bm.getType()+" - "+bm.getId());
            switch (bm.getType()){
                case "player":
                    secondTC.addExtra('\n'+" "+Message.playerRecipient+":");
                    ((MailPlayer)bm).getRecipient().forEach((re) -> {
                        secondTC.addExtra('\n'+"  "+re);
                    });
                    break;
                case "permission":
                    secondTC.addExtra('\n'+" "+Message.permissionPermission+": "+((MailPermission)bm).getPermission());
                    break;
                case "cdkey":
                    secondTC.addExtra('\n'+" "+Message.cdkeyOnly+": "+((MailCdkey)bm).isOnly());
                    break;
                case "template":
                    secondTC.addExtra('\n'+" "+Message.templateTemplate+": "+((MailTemplate)bm).getTemplate());
                    break;
            }
            firstTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{secondTC}));
        }
        p.spigot().sendMessage(firstTC);
    }
    public static void viewTopic(BaseMail bm, CommandSender s, Conversable who){
        StringBuilder sb = new StringBuilder("<"+bm.getTopic()+"§r>");
        if(bm instanceof MailTimes) sb.append(" ").append(Message.timesTimes).append(": ").append(((MailTimes)bm).getTimes());
        if(bm instanceof MailKeyTimes) sb.append('\n'+" ").append(Message.keytimesKey).append(": ").append(((MailKeyTimes)bm).getKey());
        if(s.hasPermission("mailbox.content.id")){
            sb.append(" - ");
            sb.append(bm.getType());
            sb.append(" - ");
            sb.append(bm.getId());
            if(who==null)  s.sendMessage(sb.toString());
            else who.sendRawMessage(sb.toString());
            switch (bm.getType()){
                case "player":
                    sb = new StringBuilder("  §6"+Message.playerRecipient+":§e");
                        for(String re:((MailPlayer)bm).getRecipient()){
                            sb.append("  ");
                            sb.append(re);
                        }
                        if(who==null)  s.sendMessage(sb.toString());
                        else who.sendRawMessage(sb.toString());
                        break;
                case "permission":
                    if(who==null)  s.sendMessage(" §6"+Message.permissionPermission+"  §e"+((MailPermission)bm).getPermission());
                    else who.sendRawMessage(" §6"+Message.permissionPermission+"  §e"+((MailPermission)bm).getPermission());
                    break;
                case "cdkey":
                    if(who==null)  s.sendMessage(" §6"+Message.cdkeyOnly+"  §e"+((MailCdkey)bm).isOnly());
                    else who.sendRawMessage(" §6"+Message.cdkeyOnly+"  §e"+((MailCdkey)bm).isOnly());
                    break;
                case "template":
                    if(who==null)  s.sendMessage(" §6"+Message.templateTemplate+"  §e"+((MailTemplate)bm).getTemplate());
                    else who.sendRawMessage(" §6"+Message.templateTemplate+"  §e"+((MailTemplate)bm).getTemplate());
                    break;
            }
        }else{
            if(who==null)  s.sendMessage(sb.toString());
            else who.sendRawMessage(sb.toString());
        }
    }
    
    public static void viewFile(BaseFileMail fm, Player p){
        TextComponent firstTC = new TextComponent("§d"+Message.globalHasFile+"：");
        if(p.hasPermission("mailbox.content.filename")){
            firstTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fm.getType()+" - "+fm.getFileName())}));
        }
        p.spigot().sendMessage(firstTC);
    }
    public static void viewFile(BaseFileMail fm, CommandSender s, Conversable who){
        StringBuilder sb = new StringBuilder("§d"+Message.globalHasFile+"：");
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
        List<BaseComponent> lbc = new ArrayList();
        lbc.add(new TextComponent("§e"+Message.itemItem+":§a"));
        for(ItemStack is:isl){
            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM,  new BaseComponent[]{new TextComponent(Reflection.Item2Json(is))});
            TextComponent component = new TextComponent(" §r"+MailBoxAPI.getItemName(is)+"§8x§r"+is.getAmount());
            component.setHoverEvent(event);
            lbc.add(new TextComponent("  "));
            lbc.add(component);
        }
        BaseComponent[] bc = new BaseComponent[lbc.size()];
        lbc.toArray(bc);
        p.spigot().sendMessage(bc);
    }
    public static void viewItem(ArrayList<ItemStack> isl, CommandSender s, ConversationContext cc){
        StringBuilder sb = new StringBuilder("  §e"+Message.itemItem+":  §a");
        isl.forEach((is) -> {
            sb.append(MailBoxAPI.getItemName(is)).append("§8x§r").append(is.getAmount()).append(" ");
        });
        if(cc==null){
            s.sendMessage(sb.toString());
        }else{
            cc.getForWhom().sendRawMessage(sb.toString());
        }
    }
    
    public static void viewCommand(List<String> desc, Player p){
        TextComponent tc = new TextComponent("  §e"+Message.extracommandDescription);
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
        StringBuilder sb = new StringBuilder("  §e"+Message.extracommandDescription);
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
                desc.forEach((c) -> {
                    s.sendMessage("  "+c);
                });
            }else{
                who.sendRawMessage(sb.toString());
                desc.forEach((c) -> {
                    who.sendRawMessage("  "+c);
                });
            }
        }
        viewCommandTruth(cmd, s, who);
    }
    
    public static void viewCommandTruth(List<String> cmd, CommandSender s, Conversable who){
        if(who==null){
            s.sendMessage(" §b"+Message.extracommandCommand);
            cmd.forEach((c) -> {
                s.sendMessage("  /"+c);
            });
        }else{
            who.sendRawMessage(" §b"+Message.extracommandCommand);
            cmd.forEach((c) -> {
                who.sendRawMessage("  /"+c);
            });
        }
    }
    
    public static void viewSenderAndTime(BaseMail bm, CommandSender s, Conversable who){
        StringBuilder str = new StringBuilder("§6"+Message.globalFrom+": §a"+bm.getSender()+" - §b");
        if(bm.getDate()==null || bm.getDate().equals("0")){
            str.append(DateTime.get("ymdhms"));
        }else{
            str.append(bm.getDate());
        }
        if(bm.getType().equals("date") && !((MailDate)bm).getDeadline().equals("0")){
            str.append(" - ");
            str.append(((MailDate)bm).getDeadline());
        }
        if(who==null){
            s.sendMessage(str.toString());
        }else{
            who.sendRawMessage(str.toString());
        }
    }

    // 领取邮件
    public static void collect(String type, int mid, CommandSender sender){
        BaseMail bm = getMail(type,mid);
        if(bm==null){
            sender.sendMessage(Message.commandMailNull);
            return;
        }
        if(collectable(bm,sender) && bm.isStart()){
            if(bm instanceof MailKeyTimes){
                // 发送口令
                ((Player)sender).chat(((MailKeyTimes) bm).getKey());
            }else{
                // 领取邮件
                bm.Collect((Player)sender);
            }
        }else{
            sender.sendMessage(Message.globalNoPermission);
        }
    }
    
    // 删除邮件
    public static void delete(String type, int mid, CommandSender sender){
        BaseMail bm = getMail(type,mid);
        if(bm==null){
            sender.sendMessage(Message.commandMailNull);
            return;
        }
        if(deletable(bm,sender)){
            if(sender instanceof Player){
                bm.Delete((Player)sender);
            }else{
                bm.Delete(null);
            }
        }else{
            sender.sendMessage(Message.globalNoPermission);
        }
    }
    
     // 获取此封邮件
    private static BaseMail getMail(String type, int mid){
        return MailBox.getMailHashMap(type).get(mid);
    }
    
    // 获取玩家是否可以领取这封邮件
    private static boolean collectable(BaseMail bm, CommandSender sender){
        String type = bm.getType();
        if(type.equals("cdkey")) return false;
        if(sender instanceof Player){
            if(!MailBoxAPI.hasPlayerPermission(sender, "mailbox.collect."+type)) return false;
            Player p = (Player)sender;
            MailBox.updateRelevantMailList(p, type);
            return MailBox.getRelevantMailList(p, type).get("asRecipient").contains(bm.getId());
        }else{
            return false;
        }
    }
    
    // 获取玩家是否可以删除这封邮件
    private static boolean deletable(BaseMail bm, CommandSender sender){
        if(sender instanceof Player) return (sender.hasPermission("mailbox.admin.delete."+bm.getType()) || ((bm.getType().equals("player")) && bm.getSender().equals(sender.getName()) && MailBoxAPI.hasPlayerPermission(sender, "mailbox.delete.player")));
        else return (sender instanceof ConsoleCommandSender);
    }
}
