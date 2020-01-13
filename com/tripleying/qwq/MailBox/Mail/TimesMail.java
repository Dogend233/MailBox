package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import java.util.ArrayList;
import org.bukkit.entity.Player;

public class TimesMail extends BaseMail implements MailTimes {
    
    private int times;
    
    public TimesMail(int id, String sender, String topic, String content, String date, int times) {
        super("times",id, sender, topic, content, date);
        this.times = times;
    }
    
    @Override
    public boolean TimesValidate() {
        return times>0;
    }
    
    @Override
    public boolean collectValidate(Player p) {
        if(!TimesValidate()){
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件已被领完，自动删除");
            Delete(p);
            return false;
        }
        return true;
    }

    @Override
    public boolean sendData() {
        return MailBoxAPI.setSend("times", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), "", times, false, "0");
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
    public boolean sendValidate(Player p){
        if(times>GlobalConfig.times_count && !p.hasPermission("mailbox.admin.send.check.times")){
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件数量不能大于"+GlobalConfig.times_count);
            return false;
        }
        return true;
    }
    
    @Override
    public BaseFileMail addFile() {
        return new TimesFileMail(getId(),getSender(),getTopic(),getContent(),getDate(),times,"0",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }
    
}
