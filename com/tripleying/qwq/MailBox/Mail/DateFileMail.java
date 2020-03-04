package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.util.List;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DateFileMail extends BaseFileMail implements MailDate {
    
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

    public DateFileMail(int id, String sender, String topic, String content, String date, String deadline, String filename, List<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
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
        return MailUtil.setSend("date", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), deadline, 0, "", false, getFileName());
    }
    
    @Override
    public void generateDate(){
        MailDate.super.generateDate();
    }

    @Override
    public boolean collectValidate(Player p) {
        return MailDate.super.collectValidate(p);
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
    public boolean sendValidate(Player p, ConversationContext cc) {
        return true;
    }
    
}
