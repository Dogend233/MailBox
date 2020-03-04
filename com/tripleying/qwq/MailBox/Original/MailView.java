package com.tripleying.qwq.MailBox.Original;

import com.tripleying.qwq.MailBox.Mail.BaseMail;
import com.tripleying.qwq.MailBox.Mail.MailDate;
import com.tripleying.qwq.MailBox.Mail.MailPlayer;
import com.tripleying.qwq.MailBox.Mail.BaseFileMail;
import com.tripleying.qwq.MailBox.Mail.MailPermission;
import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Mail.MailCdkey;
import com.tripleying.qwq.MailBox.Mail.MailExpirable;
import com.tripleying.qwq.MailBox.Mail.MailKeyTimes;
import com.tripleying.qwq.MailBox.Mail.MailTemplate;
import com.tripleying.qwq.MailBox.Mail.MailTimes;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.OuterMessage;
import com.tripleying.qwq.MailBox.Utils.ItemUtil;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import com.tripleying.qwq.MailBox.Utils.TimeUtil;
import com.tripleying.qwq.MailBox.Utils.ReflectionUtil;
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
            sender.sendMessage(OuterMessage.globalNoPermission);
            return;
        }
        BaseMail bm = getMail(type,mid);
        if(bm==null){
            sender.sendMessage(OuterMessage.mailNotMail);
            return;
        }
        if(MailUtil.isExpired(bm)){
            sender.sendMessage(OuterMessage.mailExpire.replace("%para%",""));
            bm.Delete(sender instanceof Player ? (Player)sender : null);
            return;
        }
        if(bm instanceof MailDate && !((MailDate)bm).isStart() && !sender.hasPermission("mailbox.admin.see.date")){
            sender.sendMessage(OuterMessage.mailNoStart);
            return;
        }
        if(sender instanceof Player){
            if((sender.hasPermission("mailbox.admin.see."+type) || collectable(bm,sender) || deletable(bm, sender))){
                view(bm, (Player)sender);
            }else{
                sender.sendMessage(OuterMessage.globalNoPermission);
            }
        }else if(sender instanceof ConsoleCommandSender){
            view(bm, sender);
        }else{
            sender.sendMessage(OuterMessage.globalNoPermission);
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
                    p.sendMessage("  "+OuterMessage.moneyVault+" "+fm.getCoin());
                }
                if(GlobalConfig.enPlayerPoints && fm.getPoint()!=0){
                    p.sendMessage("  "+OuterMessage.moneyPlayerpoints+" "+fm.getPoint());
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
                        TextComponent tc = new TextComponent(OuterMessage.cdkeyCreate);
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mailbox "+bm.getType()+" create "+bm.getId()));
                        lbc.add(tc);
                    }
                    if(p.hasPermission("mailbox.admin.export.cdkey")){
                        if(!lbc.isEmpty()) lbc.add(new TextComponent("  "));
                        TextComponent tc = new TextComponent("  "+OuterMessage.cdkeyExport);
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mailbox "+bm.getType()+" export "+bm.getId()));
                        lbc.add(tc);
                    }
                }
                if(collectable(bm,p)){
                    if(!lbc.isEmpty()) lbc.add(new TextComponent("  "));
                    TextComponent tc = new TextComponent("  "+OuterMessage.commandCollect);
                    tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mailbox "+bm.getType()+" collect "+bm.getId()));
                    lbc.add(tc);
                }
            }else{
                p.sendMessage(OuterMessage.fileFailed);
            }
        }else{
            bm.Collect(p);
        }
        // 删除
        if(deletable(bm, p)) {
            if(!lbc.isEmpty()) lbc.add(new TextComponent("  "));
            TextComponent tc = new TextComponent("  "+OuterMessage.commandDelete);
            tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mailbox "+bm.getType()+" delete "+bm.getId()));
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
        s.sendMessage(" §b"+OuterMessage.globalContent+":");
        for(String c:("\""+(GlobalConfig.enPlaceholderAPI ? PlaceholderAPI.setPlaceholders(null, bm.getContent()) : bm.getContent())+"\"").split(" ")){
            s.sendMessage("  "+c);
        }
        if(bm instanceof BaseFileMail){
            BaseFileMail fm = (BaseFileMail)bm;
            if(fm.readFile()){
                s.sendMessage("--------------------");
                viewFile(fm,s,null);
                if(GlobalConfig.enVault && fm.getCoin()!=0){
                    s.sendMessage("  "+OuterMessage.moneyVault+" "+fm.getCoin());
                }
                if(GlobalConfig.enPlayerPoints && fm.getPoint()!=0){
                    s.sendMessage("  "+OuterMessage.moneyPlayerpoints+" "+fm.getPoint());
                }
                if(fm.isHasCommand()){
                    viewCommand(fm.getCommandList(), fm.getCommandDescription(), s, null);
                }
                if(fm.isHasItem()){
                    viewItem(fm.getItemList(), s, null);
                }
            }else{
                s.sendMessage(OuterMessage.fileFailed);
            }
        }
        // 发送时间和发件人
        viewSenderAndTime(bm, s, null);
        s.sendMessage("====================");
    }
    
    // 预览
    public static void preview(BaseMail bm, CommandSender sender, ConversationContext cc){
        Conversable who = cc.getForWhom();
        who.sendRawMessage("=========="+OuterMessage.globalPreview+"==========");
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
                who.sendRawMessage("  "+OuterMessage.moneyVault+" "+fm.getCoin());
            }
            if(GlobalConfig.enPlayerPoints && fm.getPoint()!=0){
                who.sendRawMessage("  "+OuterMessage.moneyPlayerpoints+" "+fm.getPoint());
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
                sb.append(OuterMessage.moneyExpand).append(": ").append(bm.getExpandCoin()).append(' ').append(OuterMessage.moneyVault);
                f = false;
            }
            if(bm.getExpandPoint()!=0){
                if(f){
                    sb.append(OuterMessage.moneyExpand).append(": ");
                }else{
                    sb.append(", ");
                }
                sb.append(bm.getExpandPoint()).append(' ').append(OuterMessage.moneyPlayerpoints);
            }
            who.sendRawMessage(sb.toString());
        }
        who.sendRawMessage("====================");
    }
    
    public static void viewTopic(BaseMail bm, Player p){
        TextComponent firstTC = new TextComponent("<"+bm.getTopic()+"§r>");
        if(bm instanceof MailTimes) firstTC.addExtra(" "+OuterMessage.timesTimes+": "+((MailTimes)bm).getTimes());
        if(bm instanceof MailKeyTimes) firstTC.addExtra('\n'+" "+OuterMessage.keytimesKey+": "+((MailKeyTimes)bm).getKey());
        // 邮件类型+ID+信息
        if(p.hasPermission("mailbox.content.id")){
            TextComponent secondTC = new TextComponent(bm.getType()+" - "+bm.getId());
            switch (bm.getType()){
                case "player":
                    secondTC.addExtra('\n'+" "+OuterMessage.playerRecipient+":");
                    ((MailPlayer)bm).getRecipient().forEach((re) -> {
                        secondTC.addExtra('\n'+"  "+re);
                    });
                    break;
                case "permission":
                    secondTC.addExtra('\n'+" "+OuterMessage.permissionPermission+": "+((MailPermission)bm).getPermission());
                    break;
                case "cdkey":
                    secondTC.addExtra('\n'+" "+OuterMessage.cdkeyOnly+": "+((MailCdkey)bm).isOnly());
                    break;
                case "template":
                    secondTC.addExtra('\n'+" "+OuterMessage.templateTemplate+": "+((MailTemplate)bm).getTemplate());
                    break;
            }
            firstTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{secondTC}));
        }
        p.spigot().sendMessage(firstTC);
    }
    public static void viewTopic(BaseMail bm, CommandSender s, Conversable who){
        StringBuilder sb = new StringBuilder("<"+bm.getTopic()+"§r>");
        if(bm instanceof MailTimes) sb.append(" ").append(OuterMessage.timesTimes).append(": ").append(((MailTimes)bm).getTimes());
        if(bm instanceof MailKeyTimes) sb.append('\n'+" ").append(OuterMessage.keytimesKey).append(": ").append(((MailKeyTimes)bm).getKey());
        if(s.hasPermission("mailbox.content.id")){
            sb.append(" - ");
            sb.append(bm.getType());
            sb.append(" - ");
            sb.append(bm.getId());
            if(who==null)  s.sendMessage(sb.toString());
            else who.sendRawMessage(sb.toString());
            switch (bm.getType()){
                case "player":
                    sb = new StringBuilder("  §6"+OuterMessage.playerRecipient+":§e");
                        for(String re:((MailPlayer)bm).getRecipient()){
                            sb.append("  ");
                            sb.append(re);
                        }
                        if(who==null)  s.sendMessage(sb.toString());
                        else who.sendRawMessage(sb.toString());
                        break;
                case "permission":
                    if(who==null)  s.sendMessage(" §6"+OuterMessage.permissionPermission+"  §e"+((MailPermission)bm).getPermission());
                    else who.sendRawMessage(" §6"+OuterMessage.permissionPermission+"  §e"+((MailPermission)bm).getPermission());
                    break;
                case "cdkey":
                    if(who==null)  s.sendMessage(" §6"+OuterMessage.cdkeyOnly+"  §e"+((MailCdkey)bm).isOnly());
                    else who.sendRawMessage(" §6"+OuterMessage.cdkeyOnly+"  §e"+((MailCdkey)bm).isOnly());
                    break;
                case "template":
                    if(who==null)  s.sendMessage(" §6"+OuterMessage.templateTemplate+"  §e"+((MailTemplate)bm).getTemplate());
                    else who.sendRawMessage(" §6"+OuterMessage.templateTemplate+"  §e"+((MailTemplate)bm).getTemplate());
                    break;
            }
        }else{
            if(who==null)  s.sendMessage(sb.toString());
            else who.sendRawMessage(sb.toString());
        }
    }
    
    public static void viewFile(BaseFileMail fm, Player p){
        TextComponent firstTC = new TextComponent("§d"+OuterMessage.globalHasFile+"：");
        if(p.hasPermission("mailbox.content.filename")){
            firstTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fm.getType()+" - "+fm.getFileName())}));
        }
        p.spigot().sendMessage(firstTC);
    }
    public static void viewFile(BaseFileMail fm, CommandSender s, Conversable who){
        StringBuilder sb = new StringBuilder("§d"+OuterMessage.globalHasFile+"：");
        if(s.hasPermission("mailbox.content.filename")){
            sb.append(" - ");
            sb.append(fm.getType());
            sb.append(" - ");
            sb.append(fm.getFileName());
        }
        if(who==null) s.sendMessage(sb.toString());
        else who.sendRawMessage(sb.toString());
    }
    
    public static void viewItem(List<ItemStack> isl, Player p){
        List<BaseComponent> lbc = new ArrayList();
        lbc.add(new TextComponent("§e"+OuterMessage.itemItem+":§a"));
        isl.stream().map((is) -> {
            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM,  new BaseComponent[]{new TextComponent(ReflectionUtil.Item2Json(is))});
            TextComponent component = new TextComponent(" §r"+ItemUtil.getName(is)+"§8x§r"+is.getAmount());
            component.setHoverEvent(event);
            return component;
        }).forEachOrdered((component) -> {
            lbc.add(new TextComponent("  "));
            lbc.add(component);
        });
        BaseComponent[] bc = new BaseComponent[lbc.size()];
        lbc.toArray(bc);
        p.spigot().sendMessage(bc);
    }
    public static void viewItem(List<ItemStack> isl, CommandSender s, ConversationContext cc){
        StringBuilder sb = new StringBuilder("  §e"+OuterMessage.itemItem+":  §a");
        isl.forEach((is) -> {
            sb.append(ItemUtil.getName(is)).append("§8x§r").append(is.getAmount()).append(" ");
        });
        if(cc==null){
            s.sendMessage(sb.toString());
        }else{
            cc.getForWhom().sendRawMessage(sb.toString());
        }
    }
    
    public static void viewCommand(List<String> desc, Player p){
        TextComponent tc = new TextComponent("  §e"+OuterMessage.extracommandDescription);
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
        StringBuilder sb = new StringBuilder("  §e"+OuterMessage.extracommandDescription);
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
            s.sendMessage(" §b"+OuterMessage.extracommandCommand);
            cmd.forEach((c) -> {
                s.sendMessage("  /"+c);
            });
        }else{
            who.sendRawMessage(" §b"+OuterMessage.extracommandCommand);
            cmd.forEach((c) -> {
                who.sendRawMessage("  /"+c);
            });
        }
    }
    
    public static void viewSenderAndTime(BaseMail bm, CommandSender s, Conversable who){
        StringBuilder str = new StringBuilder("§6"+OuterMessage.globalFrom+": §a"+bm.getSender()+" - §b");
        if(bm.getDate()==null || bm.getDate().equals("0")){
            str.append(TimeUtil.get("ymdhms"));
            if(bm instanceof MailDate){
                String expirableDate = ((MailExpirable)bm).getExpireDate();
                if(!expirableDate.equals("0")){
                    str.append(" - ");
                    str.append(expirableDate);
                }
            }
        }else{
            str.append(bm.getDate());
            if(bm instanceof MailExpirable){
                String expirableDate = ((MailExpirable)bm).getExpireDate();
                if(!expirableDate.equals("0")){
                    str.append(" - ");
                    str.append(expirableDate);
                }
            }
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
            sender.sendMessage(OuterMessage.commandMailNull);
            return;
        }
        if(collectable(bm,sender) && (!(bm instanceof MailDate) || ((MailDate)bm).isStart())){
            if(bm instanceof MailKeyTimes){
                // 发送口令
                ((Player)sender).chat(((MailKeyTimes) bm).getKey());
            }else{
                // 领取邮件
                bm.Collect((Player)sender);
            }
        }else{
            sender.sendMessage(OuterMessage.globalNoPermission);
        }
    }
    
    // 删除邮件
    public static void delete(String type, int mid, CommandSender sender){
        BaseMail bm = getMail(type,mid);
        if(bm==null){
            sender.sendMessage(OuterMessage.commandMailNull);
            return;
        }
        if(deletable(bm,sender)){
            if(sender instanceof Player){
                bm.Delete((Player)sender);
            }else{
                bm.Delete(null);
            }
        }else{
            sender.sendMessage(OuterMessage.globalNoPermission);
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
