package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.ItemUtil;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import com.tripleying.qwq.MailBox.Utils.PlayerPointsUtil;
import com.tripleying.qwq.MailBox.Utils.VaultUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TimesFileMail extends BaseFileMail implements MailTimes {
    
    private int times;

    public TimesFileMail(int id, String sender, String topic, String content, String date, int times, String filename) {
        super("times", id, sender, topic, content, date, filename);
        this.times = times;
    }
    public TimesFileMail(int id, String sender, String topic, String content, String date, int times, String filename, List<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super("times", id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
        this.times = times;
    }
    public TimesFileMail(String type, int id, String sender, String topic, String content, String date, int times, String filename) {
        super(type, id, sender, topic, content, date, filename);
        this.times = times;
    }
    public TimesFileMail(String type, int id, String sender, String topic, String content, String date, int times, String filename, List<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super(type, id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
        this.times = times;
    }
    
    @Override
    public boolean collectValidate(Player p) {
        return MailTimes.super.collectValidate(p);
    }

    @Override
    public boolean sendData() {
        return MailUtil.setSend("times", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), "", times, "", false, getFileName());
    }

    @Override
    public int getTimes() {
        return times;
    }
    
    @Override
    public boolean hasItem(ArrayList<ItemStack> isl, Player p, ConversationContext cc){
        return ItemUtil.hasSendItem(isl, p, cc, times);
    }
    
    @Override
    public boolean removeItem(ArrayList<ItemStack> isl, Player p, ConversationContext cc){
        return ItemUtil.removeSendItem(isl, p, cc, times);
    }
    
    @Override
    public double getExpandCoin(){
        return VaultUtil.getFileMailExpandCoin(this, times);
    }
    
    @Override
    public boolean sendValidate(Player p, ConversationContext cc){
        return MailTimes.super.sendValidate(p, cc);
    }
    
    @Override
    public int getExpandPoint(){
        return PlayerPointsUtil.getFileMailExpandPoints(this, times);
    }

    @Override
    public void setTimes(int times) {
        this.times = times;
    }
    
    @Override
    public BaseMail removeFile() {
        return new TimesMail(getId(),getSender(),getTopic(),getContent(),getDate(),times);
    }
    
}
