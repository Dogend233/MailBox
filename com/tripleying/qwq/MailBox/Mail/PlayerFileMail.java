package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.ItemUtil;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import com.tripleying.qwq.MailBox.Utils.PlayerPointsUtil;
import com.tripleying.qwq.MailBox.Utils.VaultUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerFileMail extends BaseFileMail implements MailPlayer,MailExpirable {

    private List<String> recipient;
    
    public PlayerFileMail(int id, String sender, String topic, String content, String date, List<String> recipient, String filename) {
        super("player", id, sender, topic, content, date, filename);
        this.recipient = recipient;
    }

    public PlayerFileMail(int id, String sender, String topic, String content, String date, List<String> recipient, String filename, List<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super("player", id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
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
        MailUtil.createBaseFileMail("player", 0, getSender(), recipients, "", getTopic(), getContent(), getDate(), "", 0, "", false, "", "0",getItemList(),getCommandList(),getCommandDescription(),getCoin(),getPoint()).Send(sender, cc);
    }
    
    @Override
    public boolean Send(CommandSender send, ConversationContext cc) {
        if(recipientValidate(send,cc)) return super.Send(send, cc);
        return false;
    }
    
    @Override
    public boolean hasItem(ArrayList<ItemStack> isl, Player p, ConversationContext cc){
        return ItemUtil.hasSendItem(isl, p, cc, recipient.size());
    }
    
    @Override
    public boolean removeItem(ArrayList<ItemStack> isl, Player p, ConversationContext cc){
        return ItemUtil.removeSendItem(isl, p, cc, recipient.size());
    }
    
    @Override
    public double getExpandCoin(){
        return VaultUtil.getFileMailExpandCoin(this, recipient.size());
    }
    
    @Override
    public int getExpandPoint(){
        return PlayerPointsUtil.getFileMailExpandPoints(this, recipient.size());
    }

    @Override
    public boolean sendData() {
        return MailUtil.setSend("player", getId(), getSender(), getRecipientString(), "", getTopic(), getContent(), getDate(), "", 0, "", false, getFileName());
    }

    @Override
    public BaseMail removeFile() {
        return new PlayerMail(getId(),getSender(),getTopic(),getContent(),getDate(),recipient);
    }

    @Override
    public final void setRecipient(List<String> recipient) {
        this.recipient = recipient;
    }
    
    @Override
    public final List<String> getRecipient(){
        return this.recipient;
    }
    
}
