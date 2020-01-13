package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Utils.DateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerFileMail extends BaseFileMail implements MailPlayer {

    // 邮件接收者
    private List<String> recipient;
    
    public PlayerFileMail(int id, String sender, String topic, String content, String date, List<String> recipient, String filename) {
        super("player", id, sender, topic, content, date, filename);
        this.recipient = recipient;
    }

    public PlayerFileMail(int id, String sender, String topic, String content, String date, List<String> recipient, String filename, ArrayList<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super("player", id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
        this.recipient = recipient;
    }
    
    @Override
    public boolean ExpireValidate() {
        try {
            long deadline = new SimpleDateFormat("dd").parse(GlobalConfig.expiredDay).getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long sendTime = df.parse(getDate()).getTime();
            long now = df.parse(DateTime.get("ymdhms")).getTime();
            return (sendTime+deadline)<=now;
        } catch (ParseException ex) {
            Bukkit.getLogger().info(ex.getLocalizedMessage());
            return false; 
        }
    }
    
    @Override
    public boolean collectValidate(Player p) {
        if(ExpireValidate()){
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件已过期，自动删除");
            Delete(p);
            return false;
        }
        if(!getRecipient().contains(p.getName())){
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你不是这个邮件的收件人！");
            return false;
        }
        return true;
    }
    
    @Override
    public boolean sendValidate(Player p){
        int out = MailBoxAPI.playerAsSenderAllow(p);
        int outed = MailBoxAPI.playerAsSender(p);
        if((out-outed)<=0){
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你的player邮件发送数量达到上限");
            return false;
        }
        return true;
    }

    @Override
    public boolean sendData() {
        return MailBoxAPI.setSend("player", getId(), getSender(), getRecipientString(), "", getTopic(), getContent(), getDate(), "", 0, false, getFileName());
    }

    @Override
    public BaseMail removeFile() {
        return new PlayerMail(getId(),getSender(),getTopic(),getContent(),getDate(),recipient);
    }

    @Override
    public final void setRecipient(List<String> recipient) {
        this.recipient = recipient;
    }
    
    @Override
    public final List<String> getRecipient(){
        return this.recipient;
    }
    
    @Override
    public final String getRecipientString(){
        String str = "";
        str = recipient.stream().map((n) -> " "+n).reduce(str, String::concat);
        str = str.substring(1);
        return str;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder(super.toString());
        if(!recipient.isEmpty()){
            str.append("§r-收件人：");
            str.append(getRecipientString());
        }
        return str.toString();
    }
    
}
