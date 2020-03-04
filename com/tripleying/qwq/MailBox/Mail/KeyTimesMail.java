package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.util.ArrayList;

public class KeyTimesMail extends TimesMail implements MailKeyTimes {
    
    /**
     * 邮件口令
     * 当玩家领取邮件时发送口令
     * 当玩家发送口令时领取邮件
     */
    private String key;
    
    public KeyTimesMail(int id, String sender, String topic, String content, String date, int times, String key) {
        super("keytimes", id, sender, topic, content, date, times);
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
        return MailUtil.setSend("keytimes", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), "", getTimes(), key, false, "0");
    }
    
    @Override
    public BaseFileMail addFile() {
        return new KeyTimesFileMail(getId(),getSender(),getTopic(),getContent(),getDate(),getTimes(),key,"0",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }
    
}
