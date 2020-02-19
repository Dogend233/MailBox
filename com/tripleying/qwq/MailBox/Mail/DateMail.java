package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.Message;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import com.tripleying.qwq.MailBox.Utils.TimeUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DateMail extends BaseMail implements MailDate {
    
    // 邮件截止日期
    private String deadline;

    public DateMail(int id, String sender, String topic, String content, String date, String deadline) {
        super("date", id, sender, topic, content, date);
        if(date==null) setDate("0");
        if(deadline==null){
            this.deadline = "0";
        }else{
            this.deadline = deadline;
        }
    }

    @Override
    public boolean sendData() {
        return MailUtil.setSend("date", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), deadline, 0, "", false, "0");
    }

    @Override
    public boolean collectValidate(Player p) {
        if(ExpireValidate()){
            p.sendMessage(Message.mailExpire.replace("%para%",""));
            Delete(p);
            return false;
        }
        return true;
    }

    @Override
    public boolean ExpireValidate() {
        if(getDeadline().equals("0")) return false;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long deadlineTime = df.parse(getDeadline()).getTime();
            long now = df.parse(TimeUtil.get("ymdhms")).getTime();
            return deadlineTime<=now;
        } catch (ParseException ex) {
            Bukkit.getLogger().info(ex.getLocalizedMessage());
            return false; 
        }
    }
    
    // 生成时间
    @Override
    public void generateDate(){
        if(getDate().equals("0")) setDate(TimeUtil.get("ymdhms"));
    }

    @Override
    public final void setDeadline(String deadline){
        this.deadline = deadline;
    }
    
    @Override
    public final String getDeadline(){
        return this.deadline;
    }

    @Override
    public BaseFileMail addFile() {
        return new DateFileMail(getId(),getSender(),getTopic(),getContent(),getDate(),deadline,"0",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder(super.toString());
        if(!deadline.equals("0")){
            str.append("§r-截止时间：");
            str.append(deadline);
        }
        return str.toString();
    }

    @Override
    public boolean isStart() {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long start = df.parse(getDate()).getTime();
            long now = df.parse(TimeUtil.get("ymdhms")).getTime();
            return start<now;
        } catch (ParseException ex) {
            Bukkit.getLogger().info(ex.getLocalizedMessage());
            return true;
        }
    }
    
}
