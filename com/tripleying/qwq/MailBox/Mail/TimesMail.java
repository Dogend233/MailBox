package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Message;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import com.tripleying.qwq.MailBox.Utils.TimeUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class TimesMail extends BaseMail implements MailTimes {
    
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
    public boolean ExpireValidate() {
        try {
            long deadline = new SimpleDateFormat("HH").parse(GlobalConfig.timesExpired).getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long sendTime = df.parse(getDate()).getTime();
            long now = df.parse(TimeUtil.get("ymdhms")).getTime();
            return (sendTime+deadline)<=now;
        } catch (ParseException ex) {
            return false; 
        }
    }
    
    @Override
    public boolean TimesValidate() {
        return times>0;
    }
    
    @Override
    public boolean collectValidate(Player p) {
        if(ExpireValidate()){
            p.sendMessage(Message.mailExpire.replace("%para%",""));
            Delete(p);
            return false;
        }
        if(!TimesValidate()){
            p.sendMessage(Message.timesZero.replace("%para%", ""));
            Delete(p);
            return false;
        }
        return true;
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
        if(times>GlobalConfig.timesCount && !p.hasPermission("mailbox.admin.send.check.times")){
            p.sendMessage(Message.timesSendExceed.replace("%max%", Integer.toString(GlobalConfig.timesCount)));
            return false;
        }
        return true;
    }
    
    @Override
    public BaseFileMail addFile() {
        return new TimesFileMail(getId(),getSender(),getTopic(),getContent(),getDate(),times,"0",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }
    
}
