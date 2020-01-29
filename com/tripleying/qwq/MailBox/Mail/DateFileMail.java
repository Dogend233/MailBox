package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.Message;
import com.tripleying.qwq.MailBox.Utils.DateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DateFileMail extends BaseFileMail implements MailDate {
    
    // 邮件截止日期
    private String deadline;
    
    public DateFileMail(int id, String sender, String topic, String content, String date, String deadline, String filename) {
        super("date", id, sender, topic, content, date, filename);
        if(date==null) setDate("0");
        if(deadline==null){
            this.deadline = "0";
        }else{
            this.deadline = deadline;
        }
    }

    public DateFileMail(int id, String sender, String topic, String content, String date, String deadline, String filename, ArrayList<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super("date", id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
        if(date==null) setDate("0");
        if(deadline==null){
            this.deadline = "0";
        }else{
            this.deadline = deadline;
        }
    }

    @Override
    public boolean sendData() {
        return MailBoxAPI.setSend("date", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), deadline, 0, "", false, getFileName());
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
            long now = df.parse(DateTime.get("ymdhms")).getTime();
            return deadlineTime<=now;
        } catch (ParseException ex) {
            Bukkit.getLogger().info(ex.getLocalizedMessage());
            return false; 
        }
    }
    
    // 生成时间
    @Override
    public void generateDate(){
        if(getDate().equals("0")) setDate(DateTime.get("ymdhms"));
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
    public BaseMail removeFile() {
        return new DateMail(getId(),getSender(),getTopic(),getContent(),getDate(),deadline);
    }
    
    @Override
    public boolean isStart() {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long start = df.parse(getDate()).getTime();
            long now = df.parse(DateTime.get("ymdhms")).getTime();
            return start<now;
        } catch (ParseException ex) {
            Bukkit.getLogger().info(ex.getLocalizedMessage());
            return true;
        }
    }
    
}
