package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class OnlineMail extends BaseMail implements MailOnline {
    
    public OnlineMail(String sender, String topic, String content, String date) {
        super("online",0, sender, topic, content, date);
    }

    @Override
    public boolean Send(CommandSender send, ConversationContext cc) {
        return MailOnline.super.Send(send, cc);
    }

    @Override
    public boolean sendPlayerMail(CommandSender send, ConversationContext cc, List<String> recipients) {
        return MailUtil.createBaseMail("player", 0, getSender(), recipients, "", getTopic(), getContent(), getDate(), "", 0, "", false, "").Send(send, cc);
    }
    
    @Override
    public BaseFileMail addFile() {
        return new OnlineFileMail(getSender(),getTopic(),getContent(),getDate(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }

    @Override
    public boolean sendData() {
        return true;
    }

    @Override
    public boolean collectValidate(Player p) {
        return true;
    }

    @Override
    public boolean sendValidate(Player p, ConversationContext cc) {
        return true;
    }
    
}
