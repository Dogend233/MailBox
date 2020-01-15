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
import com.tripleying.qwq.MailBox.Utils.DateTime;
import com.tripleying.qwq.MailBox.Utils.Reflection;
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
        if(sender==null) return;
        if(type.equals("cdkey") && !sender.hasPermission("mailbox.admin.see.cdkey")){
            sender.sendMessage("你不能查看此类邮件");
            return;
        }
        BaseMail bm = getMail(type,mid);
        if(bm==null){
            sender.sendMessage("目标邮件不存在");
            return;
        }
        if(bm.ExpireValidate()){
            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件已过期，自动删除");
            bm.Delete((Player)sender);
            return;
        }
        if(bm instanceof MailDate && !bm.isStart() && !sender.hasPermission("mailbox.admin.see.date")){
            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"此邮件现在还不能领取");
            return;
        }
        if(sender instanceof Player){
            if((collectable(type,mid,sender) || deletable(bm, sender))){
                view(bm, (Player)sender);
            }else{
                sender.sendMessage("你不能查看此邮件");
            }
        }else if(sender instanceof ConsoleCommandSender){
            view(bm, sender);
        }else{
            sender.sendMessage("你不能查看此邮件");
        }
    }
    public static void view(BaseMail bm, Player p){
        TextComponent firstTC;
        ComponentBuilder CB = null;
        boolean low1_12 = true;
        p.sendMessage("====================");
        viewTopic(bm, p);
        // 邮件内容
        p.sendMessage("  \""+bm.getContent().replace(" ", '\n'+"  ")+"\"");
        if(bm instanceof BaseFileMail){
            BaseFileMail fm = (BaseFileMail)bm;
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
                    if(p.hasPermission("mailbox.content.command")) viewCommandDescription(fm.getCommandList(), p, null);
                }
                if(fm.isHasItem()){
                    viewItem(fm.getItemList(), p);
                }
                if(bm.getType().equals("cdkey")){
                    if(p.hasPermission("mailbox.admin.create.cdkey")){
                        firstTC = new TextComponent("  §a[生成兑换码]");
                        firstTC.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+bm.getType()+" create "+bm.getId()));
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
                    if(p.hasPermission("mailbox.admin.export.cdkey")){
                        firstTC = new TextComponent("  §6[导出兑换码]");
                        firstTC.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+bm.getType()+" export "+bm.getId()));
                        if(GlobalConfig.lowServer1_12){
                            if(low1_12) p.sendMessage("--------------------");
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
                }
                if(collectable(bm.getType(),bm.getId(),p)){
                    firstTC = new TextComponent("  §a[领取邮件]");
                    firstTC.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+bm.getType()+" collect "+bm.getId()));
                    if(GlobalConfig.lowServer1_12){
                        if(low1_12) p.sendMessage("--------------------");
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
            bm.Collect(p);
        }
        // 删除
        if(deletable(bm, p)) {
            firstTC = new TextComponent("  §c[删除邮件]");
            firstTC.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb "+bm.getType()+" delete "+bm.getId()));
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
        viewSenderAndTime(bm, p, null);
        p.sendMessage("====================");
    }
    public static void view(BaseMail bm, CommandSender s){
        s.sendMessage("====================");
        viewTopic(bm, s, null);
        // 邮件内容
        s.sendMessage(" §b邮件内容:");
        for(String c:("\""+bm.getContent()+"\"").split(" ")){
            s.sendMessage("  "+c);
        }
        if(bm instanceof BaseFileMail){
            BaseFileMail fm = (BaseFileMail)bm;
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
        viewSenderAndTime(bm, s, null);
        s.sendMessage("====================");
    }
    
    // 预览
    public static void preview(BaseMail bm, CommandSender sender, ConversationContext cc){
        Conversable who = cc.getForWhom();
        who.sendRawMessage("==========邮件预览==========");
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
        viewSenderAndTime(bm, sender, who);
        if(bm.getExpandCoin()!=0 || bm.getExpandPoint()!=0){
            boolean f = true;
            who.sendRawMessage("----------以下内容只在预览显示----------");
            StringBuilder sb = new StringBuilder("§6发送此邮件");
            if(bm.getExpandCoin()!=0){
                sb.append("消耗: ");
                sb.append(bm.getExpandCoin());
                sb.append(' ');
                sb.append(GlobalConfig.vaultDisplay);
                f = false;
            }
            if(bm.getExpandPoint()!=0){
                if(f){
                    sb.append("消耗: ");
                }else{
                    sb.append("和: ");
                }
                sb.append(bm.getExpandPoint());
                sb.append(' ');
                sb.append(GlobalConfig.playerPointsDisplay);
            }
            who.sendRawMessage(sb.toString());
            who.sendRawMessage("----------以上内容只在预览显示----------");
        }
        who.sendRawMessage("====================");
    }
    
    public static void viewTopic(BaseMail bm, Player p){
        TextComponent firstTC = new TextComponent("<"+bm.getTopic()+"§r>");
        if(bm instanceof MailTimes) firstTC.addExtra(" 剩余数量: "+((MailTimes)bm).getTimes());
        if(bm instanceof MailKeyTimes) firstTC.addExtra('\n'+" 口令: "+((MailKeyTimes)bm).getKey());
        // 邮件类型+ID+信息
        if(p.hasPermission("mailbox.content.id")){
            TextComponent secondTC = new TextComponent(bm.getType()+" - "+bm.getId());
            switch (bm.getType()){
                case "player":
                    secondTC.addExtra('\n'+" 收件人:");
                    ((MailPlayer)bm).getRecipient().forEach((re) -> {
                        secondTC.addExtra('\n'+"  "+re);
                    });
                    break;
                case "permission":
                    secondTC.addExtra('\n'+" 所需权限: "+((MailPermission)bm).getPermission());
                    break;
                case "cdkey":
                    secondTC.addExtra('\n'+" 兑换码唯一性: "+((MailCdkey)bm).isOnly());
                    break;
                case "template":
                    secondTC.addExtra('\n'+" 模板名称: "+((MailTemplate)bm).getTemplate());
                    break;
            }
            firstTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{secondTC}));
        }
        p.spigot().sendMessage(firstTC);
    }
    public static void viewTopic(BaseMail bm, CommandSender s, Conversable who){
        StringBuilder sb = new StringBuilder("<"+bm.getTopic()+"§r>");
        if(bm instanceof MailTimes) sb.append(" 剩余数量: ").append(((MailTimes)bm).getTimes());
        if(bm instanceof MailKeyTimes) sb.append('\n'+" 口令: ").append(((MailKeyTimes)bm).getKey());
        if(s.hasPermission("mailbox.content.id")){
            sb.append(" - ");
            sb.append(bm.getType());
            sb.append(" - ");
            sb.append(bm.getId());
            if(who==null)  s.sendMessage(sb.toString());
            else who.sendRawMessage(sb.toString());
            switch (bm.getType()){
                case "player":
                    sb = new StringBuilder("  §6收件人:§e");
                        for(String re:((MailPlayer)bm).getRecipient()){
                            sb.append("  ");
                            sb.append(re);
                        }
                        if(who==null)  s.sendMessage(sb.toString());
                        else who.sendRawMessage(sb.toString());
                        break;
                case "permission":
                    if(who==null)  s.sendMessage(" §6所需权限:" + "  §e"+((MailPermission)bm).getPermission());
                    else who.sendRawMessage(" §6所需权限:" + "  §e"+((MailPermission)bm).getPermission());
                    break;
                case "cdkey":
                    if(who==null)  s.sendMessage(" §6兑换码唯一性: "+ "  §e"+((MailCdkey)bm).isOnly());
                    else who.sendRawMessage(" §6兑换码唯一性: "+ "  §e"+((MailCdkey)bm).isOnly());
                    break;
                case "template":
                    if(who==null)  s.sendMessage(" §6模板名称:" + "  §e"+((MailTemplate)bm).getTemplate());
                    else who.sendRawMessage(" §6模板名称:" + "  §e"+((MailTemplate)bm).getTemplate());
                    break;
            }
        }else{
            if(who==null)  s.sendMessage(sb.toString());
            else who.sendRawMessage(sb.toString());
        }
    }
    
    public static void viewFile(BaseFileMail fm, Player p){
        TextComponent firstTC = new TextComponent("§d附件：");
        if(p.hasPermission("mailbox.content.filename")){
            firstTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fm.getType()+" - "+fm.getFileName())}));
        }
        p.spigot().sendMessage(firstTC);
    }
    public static void viewFile(BaseFileMail fm, CommandSender s, Conversable who){
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
            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM,  new BaseComponent[]{new TextComponent(Reflection.Item2Json(is))});
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
        viewCommandDescription(cmd, s, who);
    }
    
    public static void viewCommandDescription(List<String> cmd, CommandSender s, Conversable who){
        if(who==null){
            s.sendMessage(" §b此邮件执行以下指令");
            cmd.forEach((c) -> {
                s.sendMessage("  /"+c);
            });
        }else{
            who.sendRawMessage(" §b此邮件执行以下指令");
            cmd.forEach((c) -> {
                who.sendRawMessage("  /"+c);
            });
        }
    }
    
    public static void viewSenderAndTime(BaseMail bm, CommandSender s, Conversable who){
        StringBuilder str = new StringBuilder("§6来自: §a"+bm.getSender()+" - §b");
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
            sender.sendMessage("目标邮件不存在");
            return;
        }
        if(collectable(type,mid,sender) && bm.isStart()){
            if(bm instanceof MailKeyTimes){
                // 发送口令
                ((Player)sender).chat(((MailKeyTimes) bm).getKey());
            }else{
                // 领取邮件
                bm.Collect((Player)sender);
            }
        }else{
            sender.sendMessage("你不能领取此邮件");
        }
    }
    
    // 删除邮件
    public static void delete(String type, int mid, CommandSender sender){
        BaseMail bm = getMail(type,mid);
        if(bm==null){
            sender.sendMessage("目标邮件不存在");
            return;
        }
        if(deletable(bm,sender)){
            if(sender instanceof Player){
                bm.Delete((Player)sender);
            }else{
                bm.Delete(null);
            }
        }else{
            sender.sendMessage("你不能删除此邮件");
        }
    }
    
     // 获取此封邮件
    private static BaseMail getMail(String type, int mid){
        return MailBox.getMailHashMap(type).get(mid);
    }
    
    // 获取玩家是否可以领取这封邮件
    private static boolean collectable(String type, int mid, CommandSender sender){
        if(type.equals("cdkey")) return false;
        if(sender instanceof Player){
            Player p = (Player)sender;
            ArrayList<Integer> l = MailBox.getRelevantMailList(p, type).get("asRecipient");
            return l.contains(mid);
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
