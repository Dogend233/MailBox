package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Message;
import com.tripleying.qwq.MailBox.Utils.DateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class PlayerMail extends BaseMail implements MailPlayer{
    
    // 邮件接收者
    private List<String> recipient;

    public PlayerMail(int id, String sender, String topic, String content, String date, List<String> recipient) {
        super("player", id, sender, topic, content, date);
        this.recipient = recipient;
    }
    
    @Override
    public boolean ExpireValidate() {
        try {
            long deadline = new SimpleDateFormat("dd").parse(GlobalConfig.playerExpired).getTime();
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
            p.sendMessage(Message.mailExpire.replace("%para%",""));
            Delete(p);
            return false;
        }
        if(!getRecipient().contains(p.getName())){
            p.sendMessage(Message.playerNoRecipient);
            return false;
        }
        return true;
    }
    
    @Override
    public boolean sendValidate(Player p, ConversationContext cc){
        int out = MailBoxAPI.playerAsSenderAllow(p);
        int outed = MailBoxAPI.playerAsSender(p);
        if((out-outed)<=0){
            p.sendMessage(Message.playerMailOutMax.replace("%type%",Message.getTypeName("player")));
            return false;
        }
        if(recipient.size()>GlobalConfig.playerMultiplayer && !p.hasPermission("mailbox.admin.send.multiplayer")){
            p.sendMessage(Message.playerRecipientMax.replace("%max%", Integer.toString(GlobalConfig.playerMultiplayer)));
            return false;
        }
        String Recipient = getRecipientString();
        if(Recipient.length()>255){
            if(p.hasPermission("mailbox.admin.send.extraplayer")){
                String thisRecipient = Recipient.substring(0, 255);
                thisRecipient = thisRecipient.substring(0, thisRecipient.lastIndexOf(' '));
                String otherRecipient = Recipient.substring(thisRecipient.length()+1);
                setRecipient(Arrays.asList(thisRecipient.split(" ")));
                StringBuilder sb = new StringBuilder();
                List<String> l = new ArrayList();
                for(String name:otherRecipient.split(" ")){
                    if(sb.append(" ").append(name).length()<=255){
                        l.add(name);
                    }else{
                        MailBoxAPI.createBaseMail("player", 0, getSender(), l, "", getTopic(), getContent(), getDate(), "", 0, "", false, "").Send(p, cc);
                        sb.delete(0, sb.length());
                        l.clear();
                        sb.append(" ").append(name);
                        l.add(name);
                    }
                }
                if(!l.isEmpty()) MailBoxAPI.createBaseMail("player", 0, getSender(), l, "", getTopic(), getContent(), getDate(), "", 0, "", false, "").Send(p, cc);
                return true;
            }else{
                p.sendMessage(Message.playerRecipientExceedMax);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean sendData() {
        return MailBoxAPI.setSend("player", getId(), getSender(), getRecipientString(), "", getTopic(), getContent(), getDate(), "", 0, "", false, "0");
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
    public BaseFileMail addFile() {
        return new PlayerFileMail(getId(),getSender(),getTopic(),getContent(),getDate(),recipient,"0",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
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
