package com.tripleying.qwq.MailBox.Original;

import com.tripleying.qwq.MailBox.Mail.*;
import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.Message;
import static com.tripleying.qwq.MailBox.Original.MailNew.color;
import static com.tripleying.qwq.MailBox.Original.MailNew.sendable;
import com.tripleying.qwq.MailBox.Utils.DateTime;
import com.tripleying.qwq.MailBox.VexView.MailContentGui;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MailNew {
    public static void New(CommandSender sender){
        if(sender.hasPermission("mailbox.admin.send.player")
            || sender.hasPermission("mailbox.admin.send.system")
            || sender.hasPermission("mailbox.admin.send.permission")
            || sender.hasPermission("mailbox.admin.send.date")
            || sender.hasPermission("mailbox.admin.send.times")
            || sender.hasPermission("mailbox.admin.send.keytimes")
            || sender.hasPermission("mailbox.admin.send.cdkey")
            || sender.hasPermission("mailbox.admin.send.online")
            || sender.hasPermission("mailbox.admin.send.template")){
            create(new TypeSelect(sender), sender);
        }else if(MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.player") || MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.times") || MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.keytimes")){
            int c = 0;
            boolean pl = sendable(sender,"player",null);
            boolean ti = sendable(sender,"times",null);
            boolean kti = sendable(sender,"keytimes",null);
            if(pl) c++;
            if(ti) c++;
            if(kti) c++;
            if(c>1){
                create(new TypeSelect(sender), sender);
            }else if(pl){
                sender.sendMessage(Message.newCreate.replace("%type%", Message.getTypeName("player")));
                create(new Topic(new PlayerMail(0,sender.getName(),null,null,null,null), sender, false), sender);
            }else if(ti){
                sender.sendMessage(Message.newCreate.replace("%type%", Message.getTypeName("times")));
                create(new Topic(new TimesMail(0,sender.getName(),null,null,null,0), sender, false), sender);
            }else if(kti){
                sender.sendMessage(Message.newCreate.replace("%type%", Message.getTypeName("keytimes")));
                create(new Topic(new KeyTimesMail(0,sender.getName(),null,null,null,0,null), sender, false), sender);
            }else{
                sender.sendMessage(Message.globalNoPermission);
            }
        }else{
            sender.sendMessage(Message.globalNoPermission);
        }
    }
    public static void New(CommandSender sender, BaseMail bm){
        if(bm==null) New(sender);
        else if(bm instanceof MailTemplate){
            create(new TypeSelect(sender, bm), sender);
        }else{
            sender.sendMessage(Message.newCreate.replace("%type%", bm.getTypeName()));
            if(bm.getSender()==null){
                create(new Sender(bm, sender, true), sender);
            }else{
                if(bm instanceof MailPermission && ((MailPermission)bm).getPermission()==null) create(new PermissionPermission(bm, sender, true),sender);
                if(bm instanceof MailPlayer && (((MailPlayer)bm).getRecipient()==null || ((MailPlayer)bm).getRecipient().isEmpty())) create(new PlayerRecipient(bm, sender, true),sender);
                if(bm instanceof MailDate && (bm.getDate().equals("0") && ((MailDate)bm).getDeadline().equals("0"))) create(new DateStart(bm, sender, true),sender);
                if(bm instanceof MailTimes && ((MailTimes)bm).getTimes()==0) create(new TimesTimes(bm, sender, true),sender);
                else if(bm instanceof MailKeyTimes && ((MailKeyTimes)bm).getKey()==null) create(new KeytimesKey(bm, sender, true),sender);
                if(bm instanceof MailCdkey) create(new CdkeyOnly(bm, sender, true),sender);
                create(new Preview(bm, sender), sender);
            }
        }
    }
    public static void Preview(CommandSender sender, BaseMail bm){
        sender.sendMessage(Message.newCreate.replace("%type%", bm.getTypeName()));
        if(bm.getSender()==null){
            if(sender instanceof Player){
                bm.setSender(((Player)sender).getName());
            }else{
                create(new Sender(bm, sender, true), sender);
                return;
            }
        }
        create(new Preview(bm, sender), sender);
    }
    public static void create(ValidatingPrompt p, CommandSender s){
        ((Conversable)s).acceptConversationInput(Message.newStop);
        Conversation conversation = new ConversationFactory(MailBox.getInstance())
        .withFirstPrompt(p)
        .addConversationAbandonedListener((ConversationAbandonedEvent abandonedEvent) -> {
            if (abandonedEvent.gracefulExit()) {
                abandonedEvent.getContext().getForWhom().sendRawMessage(Message.newStopMsg);
            }
        }).buildConversation((Conversable)s);
        conversation.begin();
    }
    public static String color(String target){
        return target.replace('&', '§');
    }
    public static boolean sendable(CommandSender sender, String type, ConversationContext cc){
        if(sender instanceof Player){
            switch (type) {
                case "player":
                    if(sender.hasPermission("mailbox.admin.send."+type) || MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.player")){
                        Player p = (Player)sender;
                        int out = MailBoxAPI.playerAsSenderAllow(p);
                        int outed = MailBoxAPI.playerAsSender(p);
                        if(outed>=out){
                            if(cc==null){
                                sender.sendMessage(Message.playerMailOutMax.replace("%type%",Message.getTypeName("player")));
                            }else{
                                cc.getForWhom().sendRawMessage(Message.playerMailOutMax.replace("%type%",Message.getTypeName("player")));
                            }
                            return false;
                        }
                        return true;
                    }
                    return false;
                case "times":
                    return MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.times");
                case "keytimes":
                    return MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.keytimes");
                default:
                    return sender.hasPermission("mailbox.admin.send."+type);
            }
        }else return sender instanceof ConsoleCommandSender;
    }
    public static boolean filable(CommandSender sender){
        if(sender instanceof Player){
            return (MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.money.coin") || 
                    MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.money.point") || 
                    sender.hasPermission("mailbox.admin.send.command") || 
                    itemable(sender)>0);
        }else return sender instanceof ConsoleCommandSender;
    }
    public static int itemable(CommandSender sender){
        if(sender instanceof Player){
            return MailBoxAPI.playerSendItemAllow((Player)sender);
        }else if(sender instanceof ConsoleCommandSender){
            return GlobalConfig.maxItem;
        }else{
            return 0;
        }
    }
}

class TypeSelect extends ValidatingPrompt{
    CommandSender sender;
    List<String> select = new ArrayList();
    String type = null;
    BaseMail bm = null;
    TypeSelect(CommandSender sender){
        this.sender = sender;
    }
    TypeSelect(CommandSender sender, BaseMail bm){
        this.sender = sender;
        this.bm = bm;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        int i = 1;
        for(String t:MailBoxAPI.getAllType()){
            if(sender.hasPermission("mailbox.admin.send."+t)){
                cc.getForWhom().sendRawMessage(Message.newSelect.replace("%num%", Integer.toString(i++)).replace("%type%", Message.getTypeName(t)));
                select.add(t);
            }
        }
        if(i==1){
            if(sendable(sender,"player",null)){
                cc.getForWhom().sendRawMessage(Message.newSelect.replace("%num%", Integer.toString(i++)).replace("%type%", Message.getTypeName("player")));
                select.add("player");
            }
            if(sendable(sender,"times",null)){
                cc.getForWhom().sendRawMessage(Message.newSelect.replace("%num%", Integer.toString(i++)).replace("%type%", Message.getTypeName("times")));
                select.add("times");
            }
            if(sendable(sender,"keytimes",null)){
                cc.getForWhom().sendRawMessage(Message.newSelect.replace("%num%", Integer.toString(i++)).replace("%type%", Message.getTypeName("keytimes")));
                select.add("keytimes");
            }
        }
        return Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)){
            return true;
        }
        try{
            switch(Integer.parseInt(str)){
                case 9:
                    if(select.size()>=9){
                        type = select.get(7);
                        return true;
                    }else break;
                case 8:
                    if(select.size()>=8){
                        type = select.get(7);
                        return true;
                    }else break;
                case 7:
                    if(select.size()>=7){
                        type = select.get(6);
                        return true;
                    }else break;
                case 6:
                    if(select.size()>=6){
                        type = select.get(5);
                        return true;
                    }else break;
                case 5:
                    if(select.size()>=5){
                        type = select.get(4);
                        return true;
                    }else break;
                case 4:
                    if(select.size()>=4){
                        type = select.get(3);
                        return true;
                    }else break;
                case 3:
                    if(select.size()>=3){
                        type = select.get(2);
                        return true;
                    }else break;
                case 2:
                    if(select.size()>=2){
                        type = select.get(1);
                        return true;
                    }else break;
                case 1:
                    if(select.size()>=1){
                        type = select.get(0);
                        return true;
                    } break;
                default:
                    break;
            }
            cc.getForWhom().sendRawMessage(Message.newOptionNotExist);
            return false;
        }catch(NumberFormatException e){
            cc.getForWhom().sendRawMessage(Message.globalNumberError);
            return false;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop) || type==null) return Prompt.END_OF_CONVERSATION;
        cc.getForWhom().sendRawMessage(Message.newCreate.replace("%type%", Message.getTypeName(type)));
        if(bm!=null){
            bm = bm.setType(type);
            if(bm.getSender()==null) return new Sender(bm, sender, true);
            if(bm instanceof MailPermission && ((MailPermission)bm).getPermission()==null) return new PermissionPermission(bm, sender, true);
            if(bm instanceof MailPlayer && (((MailPlayer)bm).getRecipient()==null || ((MailPlayer)bm).getRecipient().isEmpty())) return new PlayerRecipient(bm, sender, true);
            if(bm instanceof MailDate && (bm.getDate().equals("0") && ((MailDate)bm).getDeadline().equals("0"))) return new DateStart(bm, sender, true);
            if(bm instanceof MailTimes && ((MailTimes)bm).getTimes()==0) return new TimesTimes(bm, sender, true);
            if(bm instanceof MailKeyTimes && ((MailKeyTimes)bm).getKey()==null) return new KeytimesKey(bm, sender, true);
            if(bm instanceof MailCdkey) return new CdkeyOnly(bm, sender, true);
            if(bm instanceof MailTemplate && ((MailTemplate)bm).getTemplate()==null) return new Template(bm, sender, true);
            return new Preview(bm, sender);
        }
        if(sender instanceof Player){
            return new Topic(MailBoxAPI.createBaseMail(type,0,sender.getName(),null,null,null,null,null,null,0,null,false,null), sender, false);
        }else{
            return new Topic(MailBoxAPI.createBaseMail(type,0,null,null,null,null,null,null,null,0,null,false,null), sender, false);
        }
    }
}

class Topic extends ValidatingPrompt{
    BaseMail bm;
    CommandSender sender;
    boolean change;
    Topic(BaseMail bm, CommandSender sender, boolean change){
        this.bm = bm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.newInputPrompt.replace("%para%", Message.globalTopic)+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.trim().equals("")){
            cc.getForWhom().sendRawMessage(Message.globalEmptyField.replace("%para%", Message.globalTopic));
            return false;
        }else{
            if(str.length()>GlobalConfig.topicMax){
                cc.getForWhom().sendRawMessage(Message.globalExceedMax.replace("%para%", Message.globalTopic).replace("%max%", Integer.toString(GlobalConfig.topicMax)));
                return false;
            }else{
                return true;
            }
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        str = MailNew.color(str);
        cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.globalTopic).replace("%value%", str));
        bm.setTopic(str);
        if(change) return new Preview(bm, sender);
        return new Content(bm, sender, false);
    }
}

class Content extends ValidatingPrompt{
    BaseMail bm;
    CommandSender sender;
    boolean change;
    Content(BaseMail bm, CommandSender sender, boolean change){
        this.bm = bm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.newInputPrompt.replace("%para%", Message.globalContent)+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.trim().equals("")){
            cc.getForWhom().sendRawMessage(Message.globalEmptyField.replace("%para%", Message.globalContent));
            return false;
        }else{
            if(str.length()>GlobalConfig.contentMax){
                cc.getForWhom().sendRawMessage(Message.globalExceedMax.replace("%para%", Message.globalContent).replace("%max%", Integer.toString(GlobalConfig.contentMax)));
                return false;
            }else{
                return true;
            }
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        str = MailNew.color(str);
        cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.globalContent).replace("%value%", str));
        bm.setContent(str);
        if(change) return new Preview(bm, sender);
        if(bm.getSender()==null){
            return new Sender(bm, sender, false);
        }else{
            switch (bm.getType()){
                case "permission":
                    return new PermissionPermission(bm, sender, false);
                case "player":
                    return new PlayerRecipient(bm, sender, false);
                case "date":
                    return new DateStart(bm, sender, false);
                case "keytimes":
                case "times":
                    return new TimesTimes(bm, sender, false);
                case "template":
                    return new Template(bm, sender, false);
                default:
                    if(MailNew.filable(sender)){
                        return new File(bm, sender);
                    }else{
                        return new Preview(bm, sender);
                    }
            }
        }
    }
}

class Sender extends ValidatingPrompt{
    BaseMail bm;
    CommandSender sender;
    boolean change;
    Sender(BaseMail bm, CommandSender sender, boolean change){
        this.bm = bm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.newInputPrompt.replace("%para%", Message.globalSender)+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.trim().equals("")){
            cc.getForWhom().sendRawMessage(Message.globalEmptyField.replace("%para%", Message.globalSender));
            return false;
        }else{
            return true;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        str = MailNew.color(str);
        cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.globalSender).replace("%value%", str));
        bm.setSender(str);
        if(change){
            if(bm instanceof MailPermission && ((MailPermission)bm).getPermission()==null) return new PermissionPermission(bm, sender, true);
            if(bm instanceof MailPlayer && (((MailPlayer)bm).getRecipient()==null || ((MailPlayer)bm).getRecipient().isEmpty())) return new PlayerRecipient(bm, sender, true);
            if(bm instanceof MailDate && (bm.getDate().equals("0") && ((MailDate)bm).getDeadline().equals("0"))) return new DateStart(bm, sender, true);
            if(bm instanceof MailTimes && (((MailTimes)bm).getTimes()==0)) return new TimesTimes(bm, sender, true);
            if(bm instanceof MailKeyTimes && (((MailKeyTimes)bm).getKey()==null)) return new KeytimesKey(bm, sender, true);
            if(bm instanceof MailTemplate && ((MailTemplate)bm).getTemplate()==null) return new Template(bm, sender, true);
            return new Preview(bm, sender);
        }
        switch (bm.getType()){
            case "permission":
                return new PermissionPermission(bm, sender, false);
            case "player":
                return new PlayerRecipient(bm, sender, false);
            case "date":
                return new DateStart(bm, sender, false);
            case "keytimes":
            case "times":
                return new TimesTimes(bm, sender, false);
            case "template":
                return new Template(bm, sender, false);
            default:
                if(MailNew.filable(sender)){
                    return new File(bm, sender);
                }else{
                    return new Preview(bm, sender);
                }
        }
    }
}

class PermissionPermission extends ValidatingPrompt{
    BaseMail bm;
    CommandSender sender;
    boolean change;
    PermissionPermission(BaseMail bm, CommandSender sender, boolean change){
        this.bm = bm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.permissionPermissionInputPrompt+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.trim().equals("")){
            cc.getForWhom().sendRawMessage(Message.globalEmptyField.replace("%para%", Message.permissionPermission));
            return false;
        }
        return true;
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.permissionPermission).replace("%value%", str));
        ((MailPermission)bm).setPermission(str);
        if(change) return new Preview(bm, sender);
        if(MailNew.filable(sender)){
            return new File(bm, sender);
        }else{
            return new Preview(bm, sender);
        }
    }
}

class PlayerRecipient extends ValidatingPrompt{
    BaseMail bm;
    CommandSender sender;
    boolean change;
    PlayerRecipient(BaseMail bm, CommandSender sender, boolean change){
        this.bm = bm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.playerRecipientInputPrompt+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        String[] r = str.split(" ");
        if(r.length<1){
            cc.getForWhom().sendRawMessage(Message.globalEmptyField.replace("%para%", Message.playerRecipient));
            return false;
        }else{
            if(r.length>GlobalConfig.playerMultiplayer && !sender.hasPermission("mailbox.admin.send.multiplayer")){
                cc.getForWhom().sendRawMessage(Message.playerRecipientMax.replace("%max%", Integer.toString(GlobalConfig.playerMultiplayer)));
                return false;
            }
            if(sender instanceof Player && !sender.hasPermission("mailbox.admin.send.me")){
                for(String name:r){
                    if(name.equals(sender.getName())){
                        cc.getForWhom().sendRawMessage(Message.playerSelfRecipient);
                        return false;
                    }
                }
            }
            return true;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        ((MailPlayer)bm).setRecipient(Arrays.asList(str.split(" ")));
        if(((MailPlayer)bm).getRecipient().size()==1){
            cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.playerRecipient).replace("%value%", str));
        }else{
            cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.playerRecipient).replace("%value%", ""));
            ((MailPlayer)bm).getRecipient().forEach((s) -> {
                cc.getForWhom().sendRawMessage(s);
            });
        }
        if(change) return new Preview(bm, sender);
        if(MailNew.filable(sender)){
            return new File(bm, sender);
        }else{
            return new Preview(bm, sender);
        }
    }
}

class DateStart extends ValidatingPrompt{
    BaseMail bm;
    CommandSender sender;
    String date;
    boolean change;
    DateStart(BaseMail bm, CommandSender sender, boolean change){
        this.bm = bm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.dateStartInputPrompt+'\n'+Message.newNullInputPrompt.replace("%para%", Message.dateStart)+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(Message.newStop) || str.equals("0")) return true;
        List<Integer> t = DateTime.toDate(str, sender, cc);
        switch (t.size()) {
            case 3:
            case 6:
                date = DateTime.toDate(t, sender, cc);
                return date != null;
            default:
                cc.getForWhom().sendRawMessage(Message.dateFormat);
                return false;
        }
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        if(str.equals("0")){
            cc.getForWhom().sendRawMessage(Message.globalSetNull.replace("%para%", Message.dateStart));
            bm.setDate(str);
        }else{
            cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.dateStart).replace("%value%", date));
            bm.setDate(date);
        }
        if(change) return new Preview(bm, sender);
        return new DateDeadline(bm, sender, false);
    }
}

class DateDeadline extends ValidatingPrompt{
    BaseMail bm;
    CommandSender sender;
    String date;
    boolean change;
    DateDeadline(BaseMail bm, CommandSender sender, boolean change){
        this.bm = bm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.dateDeadlineInputPrompt+'\n'+Message.newNullInputPrompt.replace("%para%", Message.dateDeadline)+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(Message.newStop) || str.equals("0")) return true;
        List<Integer> t = DateTime.toDate(str, sender, cc);
        switch (t.size()) {
            case 3:
            case 6:
                date = DateTime.toDate(t, sender, cc);
                return date != null;
            default:
                cc.getForWhom().sendRawMessage(Message.dateFormat);
                return false;
        }
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        if(str.equals("0")){
            cc.getForWhom().sendRawMessage(Message.globalSetNull.replace("%para%", Message.dateDeadline));
            ((MailDate)bm).setDeadline(str);
        }else{
            cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.dateDeadline).replace("%value%", date));
            ((MailDate)bm).setDeadline(date);
        }
        if(change) return new Preview(bm, sender);
        if(MailNew.filable(sender)){
            return new File(bm, sender);
        }else{
            return new Preview(bm, sender);
        }
    }
}

class TimesTimes extends ValidatingPrompt{
    BaseMail bm;
    CommandSender sender;
    boolean change;
    TimesTimes(BaseMail bm, CommandSender sender, boolean change){
        this.bm = bm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.timesTimesInputPrompt+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)){
            return true;
        }
        try{
            if(Integer.parseInt(str)<1){
                cc.getForWhom().sendRawMessage(Message.timesSendZero);
                return false;
            }
            if(Integer.parseInt(str)>GlobalConfig.timesCount && !sender.hasPermission("mailbox.admin.send.check.times")){
                cc.getForWhom().sendRawMessage(Message.timesSendExceed.replace("%max%", Integer.toString(GlobalConfig.timesCount)));
                return false;
            }
            return true;
        }catch(NumberFormatException e){
            cc.getForWhom().sendRawMessage(Message.globalNumberError);
            return false;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        int times = Integer.parseInt(str);
        cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.timesTimes).replace("%value%", Integer.toString(times)));
        ((MailTimes)bm).setTimes(times);
        if(change) return new Preview(bm, sender);
        if(bm instanceof MailKeyTimes) return new KeytimesKey(bm, sender, false);
        if(MailNew.filable(sender)){
            return new File(bm, sender);
        }else{
            return new Preview(bm, sender);
        }
    }
}

class KeytimesKey extends ValidatingPrompt{
    BaseMail bm;
    CommandSender sender;
    boolean change;
    KeytimesKey(BaseMail bm, CommandSender sender, boolean change){
        this.bm = bm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.keytimesKeyInputPrompt+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.trim().equals("")){
            cc.getForWhom().sendRawMessage(Message.globalEmptyField.replace("%para%", Message.keytimesKey));
            return false;
        }
        return true;
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.keytimesKey).replace("%value%", str));
        ((MailKeyTimes)bm).setKey(color(str));
        if(change) return new Preview(bm, sender);
        if(MailNew.filable(sender)){
            return new File(bm, sender);
        }else{
            return new Preview(bm, sender);
        }
    }
}

class CdkeyOnly extends ValidatingPrompt{
    BaseMail bm;
    CommandSender sender;
    boolean change;
    boolean only;
    CdkeyOnly(BaseMail bm, CommandSender sender, boolean change){
        this.bm = bm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.cdkeyOnlyInputPrompt+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equalsIgnoreCase("y")){
            only = true;
        }else if(str.equalsIgnoreCase("n")){
            only = false;
        }else if(str.equals(Message.newStop)){
        }else{
            cc.getForWhom().sendRawMessage(Message.newOptionNotExist);
            return false;
        }
        return true;
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.cdkeyOnly).replace("%value%", Boolean.toString(only)));
        ((MailCdkey)bm).setOnly(only);
        if(change) return new Preview(bm, sender);
        if(MailNew.filable(sender)){
            return new File(bm, sender);
        }else{
            return new Preview(bm, sender);
        }
    }
}

class Template extends ValidatingPrompt{
    BaseMail bm;
    CommandSender sender;
    boolean change;
    Template(BaseMail bm, CommandSender sender, boolean change){
        this.bm = bm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.templateTemplateInputPrompt+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.trim().equals("")){
            cc.getForWhom().sendRawMessage(Message.globalEmptyField.replace("%para%", Message.templateTemplate));
            return false;
        }
        return true;
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.templateTemplate).replace("%value%", str));
        ((MailTemplate)bm).setTemplate(str);
        if(change) return new Preview(bm, sender);
        if(MailNew.filable(sender)){
            return new File(bm, sender);
        }else{
            return new Preview(bm, sender);
        }
    }
}

class File extends ValidatingPrompt{
    BaseMail bm;
    CommandSender sender;
    boolean file;
    File(BaseMail bm, CommandSender sender){
        this.bm = bm;
        this.sender = sender;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.fileFileInputPrompt+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equalsIgnoreCase("y")){
            file = true;
        }else if(str.equalsIgnoreCase("n")){
            file = false;
        }else if(str.equals(Message.newStop)){
        }else{
            cc.getForWhom().sendRawMessage(Message.newOptionNotExist);
            return false;
        }
        return true;
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        if(file){
            if(GlobalConfig.enVault && MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.money.coin")){
                return new Coin(bm.addFile(), sender, false);
            }else if(GlobalConfig.enPlayerPoints && MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.money.point")){
                return new Point(bm.addFile(), sender, false);
            }else if(MailNew.itemable(sender)>0){
                return new Item(bm.addFile(), sender, false);
            }else if(sender.hasPermission("mailbox.admin.send.command")){
                return new Command(bm.addFile(), sender, false);
            }else{
                return new Preview(bm, sender);
            }
        }else{
            return new Preview(bm, sender);
        }
    }
}

class Coin extends ValidatingPrompt{
    BaseFileMail fm;
    CommandSender sender;
    double coin;
    boolean change;
    double bal;
    Coin(BaseFileMail fm, CommandSender sender, boolean change){
        this.fm = fm;
        this.sender = sender;
        this.change = change;
        if(sender instanceof Player) this.bal = MailBoxAPI.getEconomyBalance((Player)sender);
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.moneyMoneyInputPrompt.replace("%money%", Message.moneyVault).replace("%bal%", Double.toString(bal))+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return true;
        try{
            coin = Double.parseDouble(str);
            if(coin<0) coin=0;
            if(sender instanceof Player){
                Player p = (Player)sender;
                fm.setCoin(coin);
                double expand = fm.getExpandCoin();
                if(expand>bal && !p.hasPermission("mailbox.admin.send.check.coin")){
                    cc.getForWhom().sendRawMessage(Message.moneyBalanceNotEnough.replace("%money%", Message.moneyVault).replace("%max%", Double.toString(bal)));
                    return false;
                }else if(expand>GlobalConfig.vaultMax && !p.hasPermission("mailbox.admin.send.check.coin")){
                    cc.getForWhom().sendRawMessage(Message.globalExceedMax.replace("%para%", Message.moneyVault).replace("%max%", Double.toString(GlobalConfig.vaultMax)));
                    return false;
                }else{
                    return true;
                }
            }else return sender instanceof ConsoleCommandSender;
        }catch(NumberFormatException e){
            cc.getForWhom().sendRawMessage(Message.globalNumberError);
            return false;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.moneyVault).replace("%value%", Double.toString(coin)));
        fm.setCoin(coin);
        if(change) return new Preview(fm, sender);
        if(GlobalConfig.enPlayerPoints && MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.money.point")){
            return new Point(fm, sender, false);
        }else if(MailNew.itemable(sender)>0){
            return new Item(fm, sender, false);
        }else if(sender.hasPermission("mailbox.admin.send.command")){
            return new Command(fm, sender, false);
        }else{
            return new Preview(fm, sender);
        }
    }
}

class Point extends ValidatingPrompt{
    BaseFileMail fm;
    CommandSender sender;
    int point;
    boolean change;
    int bal;
    Point(BaseFileMail fm, CommandSender sender, boolean change){
        this.fm = fm;
        this.sender = sender;
        this.change = change;
        if(sender instanceof Player) this.bal = MailBoxAPI.getPoints((Player)sender);
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.moneyMoneyInputPrompt.replace("%money%", Message.moneyPlayerpoints).replace("%bal%", Integer.toString(bal))+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return true;
        try{
            point = Integer.parseInt(str);
            if(point<0) point=0;
            if(sender instanceof Player){
                Player p = (Player)sender;
                fm.setPoint(point);
                int expand = fm.getExpandPoint();
                if(expand>bal && !p.hasPermission("mailbox.admin.send.check.point")){
                    cc.getForWhom().sendRawMessage(Message.moneyBalanceNotEnough.replace("%money%", Message.moneyPlayerpoints).replace("%max%", Double.toString(bal)));
                    return false;
                }else if(expand>GlobalConfig.playerPointsMax && !p.hasPermission("mailbox.admin.send.check.point")){
                    cc.getForWhom().sendRawMessage(Message.globalExceedMax.replace("%para%", Message.moneyPlayerpoints).replace("%max%", Integer.toString(GlobalConfig.playerPointsMax)));
                    return false;
                }else{
                    return true;
                }
            }else return sender instanceof ConsoleCommandSender;
        }catch(NumberFormatException e){
            sender.sendMessage(Message.globalNumberError);
            return false;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.moneyPlayerpoints).replace("%value%", Integer.toString(point)));
        fm.setPoint(point);
        if(change) return new Preview(fm, sender);
        if(MailNew.itemable(sender)>0){
            return new Item(fm, sender, false);
        }else if(sender.hasPermission("mailbox.admin.send.command")){
            return new Command(fm, sender, false);
        }else{
            return new Preview(fm, sender);
        }
    }
}

class Item extends ValidatingPrompt{
    BaseFileMail fm;
    CommandSender sender;
    int itemable;
    ArrayList<ItemStack> item = new ArrayList();
    boolean change;
    Item(BaseFileMail fm, CommandSender sender, boolean change){
        this.fm = fm;
        this.sender = sender;
        this.itemable = MailNew.itemable(sender);
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        if(sender instanceof Player){
            return Message.itemItemInputPromptInv.replace("%slot%", Integer.toString(itemable))+'\n'+Message.newNullInputPrompt.replace("%para%", Message.itemItem)+'\n'+Message.newCancel;
        }else{
            return Message.itemItemInputPromptLocal.replace("%slot%", Integer.toString(itemable))+'\n'+Message.newNullInputPrompt.replace("%para%", Message.itemItem)+'\n'+Message.newCancel;
        }
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return true;
        if(str.equals("0")){
            item = new ArrayList();
            return true;
        }
        if(sender instanceof Player){
            ArrayList<Integer> il = new ArrayList();
            for(String s:str.split(" ")){
                try{
                    int i = Integer.parseInt(s);
                    il.add(i);
                }catch(NumberFormatException e){
                    cc.getForWhom().sendRawMessage(Message.globalNumberError);
                    return false;
                }
            }
            if(il.size()>itemable){
                cc.getForWhom().sendRawMessage(Message.globalExceedMax.replace("%max%", Integer.toString(itemable)));
                return false;
            }
            ArrayList<ItemStack> ial = new ArrayList();
            Player p = (Player)sender;
            boolean skip = p.hasPermission("mailbox.admin.send.check.ban");
            for(int i:il){
                ItemStack is = p.getInventory().getItem((i-1));
                if(is==null){
                    cc.getForWhom().sendRawMessage(Message.itemSlotNullInv.replace("%slot%", Integer.toString(i)));
                    return false;
                }else{
                    if(skip || MailBoxAPI.isAllowSend(is)){
                        ial.add(is);
                    }else{
                        cc.getForWhom().sendRawMessage(Message.itemSlotBan.replace("%slot%", Integer.toString(i)));
                        return false;
                    }
                }
            }
            item = ial;
            return true;
        }else{
            List<String> il = Arrays.asList(str.split(" "));
            if(il.size()>itemable){
                cc.getForWhom().sendRawMessage(Message.globalExceedMax.replace("%max%", Integer.toString(itemable)));
                return false;
            }
            ArrayList<ItemStack> ial = new ArrayList();
            for(String s:il){
                ItemStack is = MailBoxAPI.readItem(s);
                if(is==null){
                    cc.getForWhom().sendRawMessage((Message.itemSlotNullLocal.replace("%item%", s)));
                    return false;
                }else{
                    ial.add(is);
                }
            }
            item = ial;
            return true;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        if(item.isEmpty()) {
            cc.getForWhom().sendRawMessage(Message.globalSetNull.replace("%para%", Message.itemItem));
        }else{
            fm.setItemList(item);
            cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.itemItem).replace("%value%", fm.getItemNameString()));
        }
        if(change) return new Preview(fm, sender);
        if(sender.hasPermission("mailbox.admin.send.command")){
            return new Command(fm, sender, false);
        }else{
            return new Preview(fm, sender);
        }
    }
}

class Command extends ValidatingPrompt{
    BaseFileMail fm;
    CommandSender sender;
    boolean change;
    Command(BaseFileMail fm, CommandSender sender, boolean change){
        this.fm = fm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.extracommandCommandInputPrompt+'\n'+Message.newNullInputPrompt.replace("%para%", Message.extracommandCommand)+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return true;
        if(str.trim().equals("0")) return true;
        return true;
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        if(str.trim().equals("0")) {
            return new Preview(fm, sender);
        }else{
            if(str.indexOf("/")==0) str = str.substring(1);
            fm.setCommandList(Arrays.asList(str.split("/")));
            if(fm.getCommandList().size()<=1){
                cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.extracommandCommand).replace("%value%", "/"+str));
            }else{
                cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.extracommandCommand).replace("%value%", ""));
                fm.getCommandList().forEach((s) -> {
                    cc.getForWhom().sendRawMessage("/"+s);
                });
            }
            if(change) return new Preview(fm, sender);
            return new CommandDescription(fm, sender, false);
        }
    }
}

class CommandDescription extends ValidatingPrompt{
    BaseFileMail fm;
    CommandSender sender;
    boolean change;
    CommandDescription(BaseFileMail fm, CommandSender sender, boolean change){
        this.fm = fm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return Message.extracommandDescriptionInputPrompt+'\n'+Message.newNullInputPrompt.replace("%para%", Message.extracommandDescription)+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return true;
        if(str.equals("0")) return true;
        return true;
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        if(str.equals("0")) {
            return new Preview(fm, sender);
        }else{
            ArrayList<String> desc = new ArrayList();
            Arrays.asList(str.split(" ")).forEach((s) -> {
                desc.add(MailNew.color(s));
            });
            fm.setCommandDescription(desc);
            if(fm.getCommandList().size()<=1){
                cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.extracommandDescription).replace("%value%", str));
            }else{
                cc.getForWhom().sendRawMessage(Message.globalSetField.replace("%para%", Message.extracommandDescription).replace("%value%", ""));
                fm.getCommandDescription().forEach((s) -> {
                    cc.getForWhom().sendRawMessage(s);
                });
            }
            if(change) return new Preview(fm, sender);
            return new Preview(fm, sender);
        }
    }
}

class Preview extends ValidatingPrompt{
    private final static HashMap<Integer,String> OPTION = new HashMap();
    static {
        OPTION.put(1, Message.newPreview.replace("%para%", Message.globalTopic));
        OPTION.put(2, Message.newPreview.replace("%para%", Message.globalContent));
        OPTION.put(3, Message.newPreview.replace("%para%", Message.globalSender));
        OPTION.put(4, Message.newPreview.replace("%para%", Message.playerRecipient));
        OPTION.put(5, Message.newPreview.replace("%para%", Message.permissionPermission));
        OPTION.put(6, Message.newPreview.replace("%para%", Message.moneyVault));
        OPTION.put(7, Message.newPreview.replace("%para%", Message.moneyPlayerpoints));
        OPTION.put(8, Message.newPreview.replace("%para%", Message.extracommandCommand));
        OPTION.put(9, Message.newPreview.replace("%para%", Message.extracommandDescription));
        OPTION.put(10, Message.newPreview.replace("%para%", Message.itemItem));
        OPTION.put(11, Message.newPreview.replace("%para%", Message.newAddFile));
        OPTION.put(12, Message.newPreview.replace("%para%", Message.newRemoveFiles));
        OPTION.put(13, Message.newPreview.replace("%para%", Message.dateStart));
        OPTION.put(14, Message.newPreview.replace("%para%", Message.dateDeadline));
        OPTION.put(15, Message.newPreview.replace("%para%", Message.templateTemplate));
        OPTION.put(16, Message.newPreview.replace("%para%", Message.timesTimes));
        OPTION.put(17, Message.newPreview.replace("%para%", Message.cdkeyOnly));
        OPTION.put(18, Message.newPreview.replace("%para%", Message.keytimesKey));
    }
    public static HashMap<Integer,Integer> optional(BaseMail bm, CommandSender sender){
        HashMap<Integer,Integer> o = new HashMap();
        int i = 1;
        o.put((i++), 1);
        o.put((i++), 2);
        if(sender.hasPermission("mailbox.admin.send.sender")){
            o.put((i++), 3);
        }
        switch (bm.getType()){
            case "player":
                o.put((i++), 4);
                break;
            case "permission":
                o.put((i++), 5);
                break;
            case "date":
                o.put((i++), 13);
                o.put((i++), 14);
                break;
            case "template":
                o.put((i++), 15);
                break;
            case "keytimes":
                o.put((i++), 18);
            case "times":
                o.put((i++), 16);
                break;
            case "cdkey":
                o.put((i++), 17);
                break;
        }
        if(bm instanceof BaseFileMail){
            if(GlobalConfig.enVault && MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.money.coin")) o.put((i++), 6);
            if(GlobalConfig.enPlayerPoints && MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.money.point")) o.put((i++), 7);
            if(sender.hasPermission("mailbox.admin.send.command")){
                o.put((i++), 8);
                o.put((i++), 9);
            }
            if(MailNew.itemable(sender)>0) o.put((i++), 10);
            o.put((i++), 12);
        }else{
            if(MailNew.filable(sender)) o.put((i++), 11);
        }
        return o;
    }
    BaseMail bm;
    CommandSender sender;
    HashMap<Integer,Integer> optional;
    int change = 0;
    Preview(BaseMail bm, CommandSender sender){
        if((bm instanceof BaseFileMail) && !((BaseFileMail)bm).hasFileContent()) bm = ((BaseFileMail)bm).removeFile();
        this.bm = bm;
        this.sender = sender;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        optional = optional(bm, sender);
        if((sender instanceof Player) && GlobalConfig.enVexView && (GlobalConfig.vexview_over_2_5 || GlobalConfig.vexview_under_2_5)) MailContentGui.openMailContentGui((Player)sender, bm, false);
        MailView.preview(bm, sender, cc);
        optional.forEach((k,v) -> {
            cc.getForWhom().sendRawMessage(OPTION.get(v).replace("%num%", Integer.toString(k)));
        });
        if(bm instanceof MailTemplate) return Message.templateSave+'\n'+Message.newCancel;
        else return Message.newSend+'\n'+Message.newCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return true;
        try{
            change = Integer.parseInt(str);
            if(change==0){
                return true;
            }else{
                if(optional.containsKey(change)){
                    return true;
                }else{
                    cc.getForWhom().sendRawMessage(Message.newOptionNotExist);
                    return false;
                }
            }
        }catch(NumberFormatException e){
            cc.getForWhom().sendRawMessage(Message.globalNumberError);
            return false;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(Message.newStop)) return Prompt.END_OF_CONVERSATION;
        if(change==0){
            if(!MailNew.sendable(sender, bm.getType(), cc)){
                cc.getForWhom().sendRawMessage(Message.globalNoPermission);
                return new Preview(bm, sender);
            }
            if(bm.Send(sender, cc)){
                if(bm instanceof MailTemplate) cc.getForWhom().sendRawMessage(Message.templateSaveSuccess);
                else cc.getForWhom().sendRawMessage(Message.mailSendSuccess);
                return Prompt.END_OF_CONVERSATION;
            }else{
                if(bm instanceof MailTemplate) cc.getForWhom().sendRawMessage(Message.templateSaveError);
                else cc.getForWhom().sendRawMessage(Message.mailSendError);
                return new Preview(bm, sender);
            }
        }else{
            switch (optional.get(change)){
                case 1:
                    return new Topic(bm, sender, true);
                case 2:
                    return new Content(bm, sender, true);
                case 3:
                    return new Sender(bm, sender, true);
                case 4:
                    return new PlayerRecipient(bm, sender, true);
                case 5:
                    return new PermissionPermission(bm, sender, true);
                case 6:
                    return new Coin((BaseFileMail)bm, sender, true);
                case 7:
                    return new Point((BaseFileMail)bm, sender, true);
                case 8:
                    return new Command((BaseFileMail)bm, sender, true);
                case 9:
                    return new CommandDescription((BaseFileMail)bm, sender, true);
                case 10:
                    return new Item((BaseFileMail)bm, sender, true);
                case 11:
                    if(GlobalConfig.enVault && MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.money.coin")){
                        return new Coin(bm.addFile(), sender, false);
                    }else if(GlobalConfig.enPlayerPoints && MailBoxAPI.hasPlayerPermission(sender, "mailbox.send.money.point")){
                        return new Point(bm.addFile(), sender, false);
                    }else if(MailNew.itemable(sender)>0){
                        return new Item(bm.addFile(), sender, false);
                    }else if(sender.hasPermission("mailbox.admin.send.command")){
                        return new Command(bm.addFile(), sender, false);
                    }else{
                        return new Preview(bm, sender);
                    }
                case 12:
                    return new Preview(((BaseFileMail)bm).removeFile(), sender);
                case 13:
                    return new DateStart(bm, sender, true);
                case 14:
                    return new DateDeadline(bm, sender, true);
                case 15:
                    return new Template(bm, sender, true);
                case 16:
                    return new TimesTimes(bm, sender, true);
                case 17:
                    return new CdkeyOnly(bm, sender, true);
                case 18:
                    return new KeytimesKey(bm, sender, true);
                default:
                    cc.getForWhom().sendRawMessage(Message.newOptionNotExist);
                    return new Preview(bm, sender);
            }
        }
    }
}