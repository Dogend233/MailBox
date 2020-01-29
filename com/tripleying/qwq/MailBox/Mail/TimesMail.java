package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Message;
import java.util.ArrayList;
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
    public boolean TimesValidate() {
        return times>0;
    }
    
    @Override
    public boolean collectValidate(Player p) {
        if(!TimesValidate()){
            p.sendMessage(Message.timesZero.replace("%para%", ""));
            Delete(p);
            return false;
        }
        return true;
    }

    @Override
    public boolean sendData() {
        return MailBoxAPI.setSend("times", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), "", times, "", false, "0");
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
