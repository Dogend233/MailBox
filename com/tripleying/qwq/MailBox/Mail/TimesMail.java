package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.util.ArrayList;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class TimesMail extends BaseMail implements MailTimes {
    
    /**
     * 邮件可领取数量
     * 当数量小于等于0时
     * 自动删除此邮件
     */
    private int times;
    
    public TimesMail(int id, String sender, String topic, String content, String date, int times) {
        super("times",id, sender, topic, content, date);
        this.times = times;
    }
    public TimesMail(String type, int id, String sender, String topic, String content, String date, int times) {
        super(type, id, sender, topic, content, date);
        this.times = times;
    }
    
    @Override
    public boolean collectValidate(Player p) {
        return MailTimes.super.collectValidate(p);
    }

    @Override
    public boolean sendData() {
        return MailUtil.setSend("times", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), "", times, "", false, "0");
    }

    @Override
    public int getTimes() {
        return times;
    }

    @Override
    public void setTimes(int times) {
        this.times = times;
    }
    
    @Override
    public boolean sendValidate(Player p, ConversationContext cc){
        return MailTimes.super.sendValidate(p, cc);
    }
    
    @Override
    public BaseFileMail addFile() {
        return new TimesFileMail(getId(),getSender(),getTopic(),getContent(),getDate(),times,"0",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }
    
}
