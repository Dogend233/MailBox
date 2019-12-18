package com.嘤嘤嘤.qwq.MailBox.Original;

import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.MailBox;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import com.嘤嘤嘤.qwq.MailBox.VexView.MailContentGui;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MailNew {
    public MailNew(CommandSender sender, MailBox mb){
        if(sender.hasPermission("mailbox.admin.send.player") || sender.hasPermission("mailbox.admin.send.system") || sender.hasPermission("mailbox.admin.send.permission")){
            create(sender,mb);
        }else if(sender.hasPermission("mailbox.send.player.only")){
            if(sender instanceof Player){
                if(sendable(sender,"player",null)){
                    sender.sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+"正在创建"+GlobalConfig.getTypeName("player")+GlobalConfig.normal+"邮件");
                    create(sender,mb,new TextMail("player",0,sender.getName(),null,null,null,null,null));
                }
            }else if(sender instanceof ConsoleCommandSender){
                sender.sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+"正在创建"+GlobalConfig.getTypeName("player")+GlobalConfig.normal+"邮件");
                create(sender,mb,new TextMail("player",0,null,null,null,null,null,null));
            }else{
                sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"噢!你到底是用什么跟我对话的?");
            }
        }else{
            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+" 你没有权限发送邮件");
        }
    }
    public MailNew(CommandSender sender, TextMail tm){
        
    }
    public void create(CommandSender sender, MailBox mb){
        Conversation conversation = new ConversationFactory(mb)
            .withFirstPrompt(new TypeSelect(sender))
            .addConversationAbandonedListener((ConversationAbandonedEvent abandonedEvent) -> {
                if (abandonedEvent.gracefulExit()) {
                    abandonedEvent.getContext().getForWhom().sendRawMessage(OriginalConfig.msgStop);
                }
        })
            .buildConversation((Conversable) sender);
        conversation.begin();
    }
    public void create(CommandSender sender, MailBox mb, TextMail tm){
        Conversation conversation = new ConversationFactory(mb)
            .withFirstPrompt(new Topic(tm, sender, false))
            .addConversationAbandonedListener(new ConversationAbandonedListener() {
                @Override
                public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
                    if (abandonedEvent.gracefulExit()) {
                        abandonedEvent.getContext().getForWhom().sendRawMessage(OriginalConfig.msgStop);
                    }
                }
            })
            .buildConversation((Conversable) sender);
        conversation.begin();
    }
    public static String color(String target){
        return target.replace('&', '§');
    }
    public static boolean sendable(CommandSender sender, String type, ConversationContext cc){
        if(sender instanceof Player){
            if(type.equals("player")){
                if(sender.hasPermission("mailbox.admin.send."+type) || sender.hasPermission("mailbox.send.player.only")){
                    Player p = (Player)sender;
                    int out = MailBoxAPI.playerAsSenderAllow(p);
                    int outed = MailBoxAPI.playerAsSender(p);
                    if(outed>=out){
                        if(cc==null){
                            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+" 你的"+GlobalConfig.getTypeName(type)+"邮件发送数量达到上限");
                        }else{
                            cc.getForWhom().sendRawMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+" 你的"+GlobalConfig.getTypeName(type)+"邮件发送数量达到上限");
                        }
                        return false;
                    }
                    return true;
                }
                return false;
            }else{
                return sender.hasPermission("mailbox.admin.send."+type);
            }
        }else if(sender instanceof ConsoleCommandSender){
            return true;
        }else{
            return false;
        }
    }
    public static boolean filable(CommandSender sender){
        if(sender instanceof Player){
            return (sender.hasPermission("mailbox.send.money.coin") || 
                    sender.hasPermission("mailbox.send.money.point") || 
                    sender.hasPermission("mailbox.admin.send.command") || 
                    itemable(sender)>0);
        }else if(sender instanceof ConsoleCommandSender){
            return true;
        }else{
            return false;
        }
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
    TypeSelect(CommandSender sender){
        this.sender = sender;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        int i = 1;
        if(sender.hasPermission("mailbox.admin.send.player")){
            cc.getForWhom().sendRawMessage("§b[邮件预览]: 输入"+(i++)+"发送"+GlobalConfig.getTypeName("player")+"§b邮件");
            select.add("player");
        }
        if(sender.hasPermission("mailbox.admin.send.system")){
            cc.getForWhom().sendRawMessage("§b[邮件预览]: 输入"+(i++)+"发送"+GlobalConfig.getTypeName("system")+"§b邮件");
            select.add("system");
        }
        if(sender.hasPermission("mailbox.admin.send.permission")){
            cc.getForWhom().sendRawMessage("§b[邮件预览]: 输入"+(i++)+"发送"+GlobalConfig.getTypeName("permission")+"§b邮件");
            select.add("permission");
        }
        return OriginalConfig.msgCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)){
            return true;
        }
        try{
            switch(Integer.parseInt(str)){
                case 3:
                    if(select.size()==3){
                        type = select.get(2);
                        return true;
                    }else{
                        cc.getForWhom().sendRawMessage("§a[邮件预览]：目标选项不存在");
                        return false;
                    }
                case 2:
                    if(select.size()>=2){
                        type = select.get(1);
                        return true;
                    }else{
                        cc.getForWhom().sendRawMessage("§a[邮件预览]：目标选项不存在");
                        return false;
                    }
                    
                case 1:
                    if(select.size()>=1){
                        type = select.get(0);
                        return true;
                    }else{
                        cc.getForWhom().sendRawMessage("§a[邮件预览]：目标选项不存在");
                        return false;
                    }
                default:
                    cc.getForWhom().sendRawMessage("§a[邮件预览]：目标选项不存在");
                    return false;
            }
        }catch(NumberFormatException e){
            cc.getForWhom().sendRawMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"输入格式错误，请输入数字");
            return false;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr) || type==null) return Prompt.END_OF_CONVERSATION;
        cc.getForWhom().sendRawMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+"正在创建"+GlobalConfig.getTypeName(type)+GlobalConfig.normal+"邮件");
        if(sender instanceof Player){
            return new Topic(new TextMail(type,0,sender.getName(),null,null,null,null,null), sender, false);
        }else{
            return new Topic(new TextMail(type,0,null,null,null,null,null,null), sender, false);
        }
    }
}

class Topic extends ValidatingPrompt{
    TextMail tm;
    CommandSender sender;
    boolean change;
    Topic(TextMail tm, CommandSender sender, boolean change){
        this.tm = tm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return OriginalConfig.msgTopic+'\n'+OriginalConfig.msgCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.trim().equals("")){
            cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: 标题不能为空");
            return false;
        }else{
            if(str.length()>OriginalConfig.maxTopic){
                cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: 标题长度超出限制 "+OriginalConfig.maxTopic);
                return false;
            }else{
                return true;
            }
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return Prompt.END_OF_CONVERSATION;
        str = MailNew.color(str);
        cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置标题: "+str);
        tm.setTopic(str);
        if(change) return new Preview(tm, sender);
        return new Content(tm, sender, false);
    }
}

class Content extends ValidatingPrompt{
    TextMail tm;
    CommandSender sender;
    boolean change;
    Content(TextMail tm, CommandSender sender, boolean change){
        this.tm = tm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return OriginalConfig.msgContent+'\n'+OriginalConfig.msgCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.trim().equals("")){
            cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: 内容不能为空");
            return false;
        }else{
            if(str.length()>OriginalConfig.maxContent){
                cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: 内容长度超出限制 "+OriginalConfig.maxContent);
                return false;
            }else{
                return true;
            }
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return Prompt.END_OF_CONVERSATION;
        str = MailNew.color(str);
        cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置内容: "+str);
        tm.setContent(str);
        if(change) return new Preview(tm, sender);
        if(tm.getSender()==null){
            return new Sender(tm, sender, false);
        }else{
            switch (tm.getType()){
                case "permission":
                    return new Permission(tm, sender, false);
                case "player":
                    return new Recipient(tm, sender, false);
                default:
                    if(MailNew.filable(sender)){
                        return new File(tm, sender);
                    }else{
                        return new Preview(tm, sender);
                    }
            }
        }
    }
}

class Sender extends ValidatingPrompt{
    TextMail tm;
    CommandSender sender;
    boolean change;
    Sender(TextMail tm, CommandSender sender, boolean change){
        this.tm = tm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return OriginalConfig.msgSender+'\n'+OriginalConfig.msgCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.trim().equals("")){
            cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: 发件人不能为空");
            return false;
        }else{
            return true;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return Prompt.END_OF_CONVERSATION;
        str = MailNew.color(str);
        cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置发件人: "+str);
        tm.setSender(str);
        if(change) return new Preview(tm, sender);
        switch (tm.getType()){
            case "permission":
                return new Permission(tm, sender, false);
            case "player":
                return new Recipient(tm, sender, false);
            default:
                if(MailNew.filable(sender)){
                    return new File(tm, sender);
                }else{
                    return new Preview(tm, sender);
                }
        }
    }
}

class Permission extends ValidatingPrompt{
    TextMail tm;
    CommandSender sender;
    boolean change;
    Permission(TextMail tm, CommandSender sender, boolean change){
        this.tm = tm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return OriginalConfig.msgPermission+'\n'+OriginalConfig.msgCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.trim().equals("")){
            cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: 权限不能为空");
            return false;
        }
        return true;
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return Prompt.END_OF_CONVERSATION;
        cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置领取所需权限: "+str);
        tm.setPermission(str);
        if(change) return new Preview(tm, sender);
        if(MailNew.filable(sender)){
            return new File(tm, sender);
        }else{
            return new Preview(tm, sender);
        }
    }
}

class Recipient extends ValidatingPrompt{
    TextMail tm;
    CommandSender sender;
    boolean change;
    Recipient(TextMail tm, CommandSender sender, boolean change){
        this.tm = tm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return OriginalConfig.msgRecipient+'\n'+OriginalConfig.msgCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        String[] r = str.split(" ");
        if(r.length<1){
            cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: 收件人不能为空");
            return false;
        }else{
            if(r.length>1 && !sender.hasPermission("mailbox.admin.send.multiplayer")){
                cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: 您只能填写一位收件人");
                return false;
            }
            if(sender instanceof Player){
                for(String name:r){
                    if(name.equals(sender.getName())){
                        cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: 收件人不能是自己");
                        return false;
                    }
                }
            }
            return true;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return Prompt.END_OF_CONVERSATION;
        tm.setRecipient(Arrays.asList(str.split(" ")));
        if(tm.getRecipient().size()==1){
            cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置收件人: "+tm.getRecipient().get(0));
        }else{
            cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置以下收件人: ");
            for(String s:tm.getRecipient()){
                cc.getForWhom().sendRawMessage(s);
            }
        }
        if(change) return new Preview(tm, sender);
        if(MailNew.filable(sender)){
            return new File(tm, sender);
        }else{
            return new Preview(tm, sender);
        }
    }
}

class File extends ValidatingPrompt{
    TextMail tm;
    CommandSender sender;
    boolean file;
    File(TextMail tm, CommandSender sender){
        this.tm = tm;
        this.sender = sender;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return OriginalConfig.msgFile+'\n'+OriginalConfig.msgCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equalsIgnoreCase("y")){
            file = true;
        }else if(str.equalsIgnoreCase("n")){
            file = false;
        }else if(str.equals(OriginalConfig.stopStr)){
        }else{
            cc.getForWhom().sendRawMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标选项不存在");
            return false;
        }
        return true;
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return Prompt.END_OF_CONVERSATION;
        if(file){
            if(GlobalConfig.enVault && sender.hasPermission("mailbox.send.money.coin")){
                return new Coin(tm.toFileMail(), sender, false);
            }else if(GlobalConfig.enPlayerPoints && sender.hasPermission("mailbox.send.money.point")){
                return new Point(tm.toFileMail(), sender, false);
            }else if(MailNew.itemable(sender)>0){
                return new Item(tm.toFileMail(), sender, false);
            }else if(sender.hasPermission("mailbox.admin.send.command")){
                return new Command(tm.toFileMail(), sender, false);
            }else{
                return new Preview(tm, sender);
            }
        }else{
            return new Preview(tm, sender);
        }
    }
}

class Coin extends ValidatingPrompt{
    FileMail fm;
    CommandSender sender;
    double coin;
    boolean change;
    Coin(FileMail fm, CommandSender sender, boolean change){
        this.fm = fm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return OriginalConfig.msgCoin+'\n'+OriginalConfig.msgCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return true;
        try{
            coin = Double.parseDouble(str);
            if(sender instanceof Player){
                Player p = (Player)sender;
                fm.setCoin(coin);
                double expand = fm.getExpandCoin();
                if(expand>MailBoxAPI.getEconomyBalance(p) && !p.hasPermission("mailbox.admin.send.check.coin")){
                    cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]："+GlobalConfig.vaultDisplay+GlobalConfig.warning+"余额不足, 您有"+MailBoxAPI.getEconomyBalance(p));
                    return false;
                }else if(expand>GlobalConfig.vaultMax && !p.hasPermission("mailbox.admin.send.check.coin")){
                    cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]："+GlobalConfig.vaultDisplay+GlobalConfig.warning+"超出最大发送限制: "+GlobalConfig.vaultMax);
                    return false;
                }else{
                    return true;
                }
            }else if(sender instanceof ConsoleCommandSender){
                return true;
            }else{
                return false;
            }
        }catch(NumberFormatException e){
            cc.getForWhom().sendRawMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"输入格式错误，请输入数字");
            return false;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return Prompt.END_OF_CONVERSATION;
        cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置发送"+GlobalConfig.vaultDisplay+": "+coin);
        fm.setCoin(coin);
        if(change) return new Preview(fm, sender);
        if(sender.hasPermission("mailbox.send.money.point")){
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
    FileMail fm;
    CommandSender sender;
    int point;
    boolean change;
    Point(FileMail fm, CommandSender sender, boolean change){
        this.fm = fm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return OriginalConfig.msgPoint+'\n'+OriginalConfig.msgCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return true;
        try{
            point = Integer.parseInt(str);
            if(sender instanceof Player){
                Player p = (Player)sender;
                fm.setPoint(point);
                int expand = fm.getExpandPoint();
                if(expand>MailBoxAPI.getPoints(p) && !p.hasPermission("mailbox.admin.send.check.point")){
                    cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]："+GlobalConfig.vaultDisplay+GlobalConfig.warning+"余额不足, 您有"+MailBoxAPI.getPoints(p));
                    return false;
                }else if(expand>GlobalConfig.playerPointsMax && !p.hasPermission("mailbox.admin.send.check.point")){
                    cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]："+GlobalConfig.playerPointsDisplay+GlobalConfig.warning+"超出最大发送限制: "+GlobalConfig.playerPointsMax);
                    return false;
                }else{
                    return true;
                }
            }else if(sender instanceof ConsoleCommandSender){
                return true;
            }else{
                return false;
            }
        }catch(NumberFormatException e){
            sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"输入格式错误，请输入数字");
            return false;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return Prompt.END_OF_CONVERSATION;
        cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置发送"+GlobalConfig.playerPointsDisplay+": "+point);
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
    FileMail fm;
    CommandSender sender;
    int itemable;
    ArrayList<ItemStack> item = new ArrayList();
    boolean change;
    Item(FileMail fm, CommandSender sender, boolean change){
        this.fm = fm;
        this.sender = sender;
        this.itemable = MailNew.itemable(sender);
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        if(sender instanceof Player){
            return OriginalConfig.msgItemPlayer+itemable+'\n'+OriginalConfig.msgItemCancel+'\n'+OriginalConfig.msgCancel;
        }else if(sender instanceof ConsoleCommandSender){
            return OriginalConfig.msgItemConsole+itemable+'\n'+OriginalConfig.msgItemCancel+'\n'+OriginalConfig.msgCancel;
        }else{
            return GlobalConfig.warning+GlobalConfig.pluginPrefix+"对话出错"+OriginalConfig.msgItemCancel+'\n'+OriginalConfig.msgCancel;
        }
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return true;
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
                    cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: "+"输入格式错误，请输入数字");
                    return false;
                }
            }
            if(il.size()>itemable){
                cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: "+"超出最大发送限制: "+itemable);
                return false;
            }
            ArrayList<ItemStack> ial = new ArrayList();
            Player p = (Player)sender;
            boolean skip = p.hasPermission("mailbox.admin.send.check.ban");
            for(int i:il){
                ItemStack is = p.getInventory().getItem((i-1));
                if(is==null){
                    cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: "+i+"号格子物品不存在");
                    return false;
                }else{
                    if(skip || MailBoxAPI.isAllowSend(is)){
                        ial.add(is);
                    }else{
                        cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: "+i+"号格子物品无法作为邮件发送");
                        return false;
                    }
                }
            }
            item = ial;
            return true;
        }else if(sender instanceof ConsoleCommandSender){
            List<String> il = Arrays.asList(str.split(" "));
            if(il.size()>itemable){
                cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: "+"超出最大发送限制: "+itemable);
                return false;
            }
            ArrayList<ItemStack> ial = new ArrayList();
            for(String s:il){
                ItemStack is = MailBoxAPI.readItem(s);
                if(is==null){
                    cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]： "+s+" 物品不存在");
                    return false;
                }else{
                    ial.add(is);
                }
            }
            item = ial;
            return true;
        }else{
            item = new ArrayList();
            return true;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return Prompt.END_OF_CONVERSATION;
        if(item.isEmpty()) {
            cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置不发送物品");
        }else{
            fm.setItemList(item);
            cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置发送物品格子数量: "+item.size());
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
    FileMail fm;
    CommandSender sender;
    boolean change;
    Command(FileMail fm, CommandSender sender, boolean change){
        this.fm = fm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return OriginalConfig.msgCommand+'\n'+OriginalConfig.msgCommandCancel+'\n'+OriginalConfig.msgCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return true;
        if(str.trim().equals("0")) return true;
        return true;
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return Prompt.END_OF_CONVERSATION;
        if(str.trim().equals("0")) {
            return new Preview(fm, sender);
        }else{
            if(str.indexOf("/")==0) str = str.substring(1);
            fm.setCommandList(Arrays.asList(str.split("/")));
            if(fm.getCommandList().size()<=1){
                cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置执行指令: /"+fm.getCommandList().get(0));
            }else{
                cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置执行以下指令: ");
                for(String s:fm.getCommandList()){
                    cc.getForWhom().sendRawMessage("/"+s);
                }
            }
            if(change) return new Preview(fm, sender);
            return new CommandDescription(fm, sender, false);
        }
    }
}

class CommandDescription extends ValidatingPrompt{
    FileMail fm;
    CommandSender sender;
    boolean change;
    CommandDescription(FileMail fm, CommandSender sender, boolean change){
        this.fm = fm;
        this.sender = sender;
        this.change = change;
    }
    @Override
    public String getPromptText(ConversationContext cc) {
        return OriginalConfig.msgCommandDescription+'\n'+OriginalConfig.msgCommandDescriptionCancel+'\n'+OriginalConfig.msgCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return true;
        if(str.equals("0")) return true;
        return true;
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return Prompt.END_OF_CONVERSATION;
        if(str.equals("0")) {
            return new Preview(fm, sender);
        }else{
            ArrayList<String> desc = new ArrayList();
            for(String s:Arrays.asList(str.split(" "))){
                desc.add(MailNew.color(s));
            }
            fm.setCommandDescription(desc);
            if(fm.getCommandList().size()<=1){
                cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置指令描述: "+fm.getCommandDescription().get(0));
            }else{
                cc.getForWhom().sendRawMessage("§a[邮件预览]: 设置以下指令描述: ");
                for(String s:fm.getCommandDescription()){
                    cc.getForWhom().sendRawMessage(s);
                }
            }
            if(change) return new Preview(fm, sender);
            return new Preview(fm, sender);
        }
    }
}

class Preview extends ValidatingPrompt{
    private final static HashMap<Integer,String> OPTION = new HashMap();
    static {
        OPTION.put(1, "§b[邮件预览]: 输入 0 修改标题");
        OPTION.put(2, "§b[邮件预览]: 输入 0 修改内容");
        OPTION.put(3, "§b[邮件预览]: 输入 0 修改发件人");
        OPTION.put(4, "§b[邮件预览]: 输入 0 修改收件人");
        OPTION.put(5, "§b[邮件预览]: 输入 0 修改权限");
        OPTION.put(6, "§b[邮件预览]: 输入 0 修改"+GlobalConfig.vaultDisplay);
        OPTION.put(7, "§b[邮件预览]: 输入 0 修改"+GlobalConfig.playerPointsDisplay);
        OPTION.put(8, "§b[邮件预览]: 输入 0 修改指令");
        OPTION.put(9, "§b[邮件预览]: 输入 0 修改指令描述");
        OPTION.put(10, "§b[邮件预览]: 输入 0 修改物品");
        OPTION.put(11, "§b[邮件预览]: 输入 0 添加附件");
        OPTION.put(12, "§b[邮件预览]: 输入 0 移除所有附件");
    }
    TextMail tm;
    CommandSender sender;
    HashMap<Integer,Integer> optional;
    int change = 0;
    Preview(TextMail tm, CommandSender sender){
        this.tm = tm;
        this.sender = sender;
    }
    public static HashMap<Integer,Integer> optional(TextMail tm, CommandSender sender){
        HashMap<Integer,Integer> o = new HashMap();
        int i = 1;
        o.put((i++), 1);
        o.put((i++), 2);
        if(sender.hasPermission("改发件人")){
            o.put((i++), 3);
        }
        switch (tm.getType()){
            case "player":
                o.put((i++), 4);
                break;
            case "permission":
                o.put((i++), 5);
                break;
        }
        if(tm instanceof FileMail){
            if(GlobalConfig.enVault && sender.hasPermission("mailbox.send.money.coin")) o.put((i++), 6);
            if(GlobalConfig.enPlayerPoints && sender.hasPermission("mailbox.send.money.point")) o.put((i++), 7);
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
    @Override
    public String getPromptText(ConversationContext cc) {
        optional = optional(tm, sender);
        if((sender instanceof Player) && GlobalConfig.enVexView && GlobalConfig.lowVexView) MailContentGui.openMailContentGui((Player)sender, tm);
        MailView.preview(tm, sender, cc);
        optional.forEach((k,v) -> {
            cc.getForWhom().sendRawMessage(OPTION.get(v).replace("0", Integer.toString(k)));
        });
        return OriginalConfig.msgPreview+'\n'+OriginalConfig.msgCancel;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return true;
        try{
            change = Integer.parseInt(str);
            if(change==0){
                return true;
            }else{
                if(optional.containsKey(change)){
                    return true;
                }else{
                    cc.getForWhom().sendRawMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标选项不存在");
                    return false;
                }
            }
        }catch(NumberFormatException e){
            cc.getForWhom().sendRawMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"输入格式错误，请输入数字");
            return false;
        }
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String str) {
        if(str.equals(OriginalConfig.stopStr)) return Prompt.END_OF_CONVERSATION;
        if(change==0){
            if(!MailNew.sendable(sender, tm.getType(), cc)){
                cc.getForWhom().sendRawMessage(GlobalConfig.warning+"你无权发送邮件");
                return new Preview(tm, sender);
            }
            if(tm.Send(sender, cc)){
                cc.getForWhom().sendRawMessage(GlobalConfig.success+"[邮件预览]: 发送成功！");
                return Prompt.END_OF_CONVERSATION;
            }else{
                cc.getForWhom().sendRawMessage(GlobalConfig.warning+"[邮件预览]: 发送失败！");
                return new Preview(tm, sender);
            }
        }else{
            switch (optional.get(change)){
                case 1:
                    return new Topic(tm, sender, true);
                case 2:
                    return new Content(tm, sender, true);
                case 3:
                    return new Sender(tm, sender, true);
                case 4:
                    return new Recipient(tm, sender, true);
                case 5:
                    return new Permission(tm, sender, true);
                case 6:
                    return new Coin((FileMail)tm, sender, true);
                case 7:
                    return new Point((FileMail)tm, sender, true);
                case 8:
                    return new Command((FileMail)tm, sender, true);
                case 9:
                    return new CommandDescription((FileMail)tm, sender, true);
                case 10:
                    return new Item((FileMail)tm, sender, true);
                case 11:
                    if(GlobalConfig.enVault && sender.hasPermission("mailbox.send.money.coin")){
                        return new Coin(tm.toFileMail(), sender, false);
                    }else if(GlobalConfig.enPlayerPoints && sender.hasPermission("mailbox.send.money.point")){
                        return new Point(tm.toFileMail(), sender, false);
                    }else if(MailNew.itemable(sender)>0){
                        return new Item(tm.toFileMail(), sender, false);
                    }else if(sender.hasPermission("mailbox.admin.send.command")){
                        return new Command(tm.toFileMail(), sender, false);
                    }else{
                        return new Preview(tm, sender);
                    }
                case 12:
                    return new Preview(((FileMail)tm).toTextMail(), sender);
                default:
                    cc.getForWhom().sendRawMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"目标选项不存在");
                    return new Preview(tm, sender);
            }
        }
    }
}