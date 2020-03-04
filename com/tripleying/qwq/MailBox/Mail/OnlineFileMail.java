package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OnlineFileMail extends BaseFileMail implements MailOnline {
    
    public OnlineFileMail(String sender, String topic, String content, String date, List<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super("online", 0, sender, topic, content, date, "0", isl, cl, cd, coin, point);
    }
    
    @Override
    public boolean Send(CommandSender send, ConversationContext cc) {
        return MailOnline.super.Send(send, cc);
    }
    
    @Override
    public boolean sendPlayerMail(CommandSender send, ConversationContext cc, List<String> recipients) {
        return MailUtil.createBaseFileMail("player", 0, getSender(), recipients, "", getTopic(), getContent(), getDate(), "", 0, "", false, "", "0",getItemList(),getCommandList(),getCommandDescription(),getCoin(),getPoint()).Send(send, cc);
    }
    
    @Override
    public BaseMail removeFile() {
        return new OnlineMail(getSender(),getTopic(),getContent(),getDate());
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
