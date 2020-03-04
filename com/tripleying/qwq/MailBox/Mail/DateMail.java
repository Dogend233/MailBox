package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.util.ArrayList;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class DateMail extends BaseMail implements MailDate {
    
    /**
     * 邮件截止日期
     * 当邮件超过截止日期时
     * 自动删除此邮件
     */
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
        return MailDate.super.collectValidate(p);
    }
    
    @Override
    public void generateDate(){
        MailDate.super.generateDate();
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
    public boolean sendValidate(Player p, ConversationContext cc) {
        return true;
    }
    
}
