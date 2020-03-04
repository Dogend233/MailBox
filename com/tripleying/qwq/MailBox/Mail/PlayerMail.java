package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class PlayerMail extends BaseMail implements MailPlayer {
    
    /**
     * 收件人列表
     * 当收件人列表为空时
     * 自动删除此邮件
     */
    private List<String> recipient;

    public PlayerMail(int id, String sender, String topic, String content, String date, List<String> recipient) {
        super("player", id, sender, topic, content, date);
        this.recipient = recipient;
    }
    
    @Override
    public boolean collectValidate(Player p) {
        return MailPlayer.super.collectValidate(p);
    }
    
    @Override
    public boolean sendValidate(Player p, ConversationContext cc){
        return MailPlayer.super.sendValidate(p, cc);
    }
    
    @Override
    public void sendNewPlayerMail(CommandSender sender, ConversationContext cc, List<String> recipients){
        MailUtil.createBaseMail("player", 0, getSender(), recipients, "", getTopic(), getContent(), getDate(), "", 0, "", false, "").Send(sender, cc);
    }
    
    @Override
    public boolean Send(CommandSender send, ConversationContext cc) {
        if(recipientValidate(send,cc)) return super.Send(send, cc);
        return false;
    }

    @Override
    public boolean sendData() {
        return MailUtil.setSend("player", getId(), getSender(), getRecipientString(), "", getTopic(), getContent(), getDate(), "", 0, "", false, "0");
    }
    
    @Override
    public final void setRecipient(List<String> recipient) {
        this.recipient = recipient;
    }
    
    @Override
    public final List<String> getRecipient(){
        return this.recipient;
    }

    @Override
    public BaseFileMail addFile() {
        return new PlayerFileMail(getId(),getSender(),getTopic(),getContent(),getDate(),recipient,"0",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }
    
}
