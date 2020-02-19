package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class KeyTimesFileMail extends TimesFileMail implements MailKeyTimes {
    
    private String key;
    
    public KeyTimesFileMail(int id, String sender, String topic, String content, String date, int times, String key, String filename) {
        super("keytimes", id, sender, topic, content, date, times, filename);
        this.key = key;
    }
    public KeyTimesFileMail(int id, String sender, String topic, String content, String date, int times, String key, String filename, ArrayList<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super("keytimes", id, sender, topic, content, date, times, filename, isl, cl, cd, coin, point);
        this.key = key;
    }
    
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }
    
    @Override
    public boolean sendData() {
        return MailUtil.setSend("keytimes", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), "", getTimes(), key, false, getFileName());
    }
    
    @Override
    public BaseMail removeFile() {
        return new KeyTimesMail(getId(),getSender(),getTopic(),getContent(),getDate(),getTimes(),key);
    }
    
}
