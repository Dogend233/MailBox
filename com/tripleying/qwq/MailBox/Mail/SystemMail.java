package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import java.util.ArrayList;
import org.bukkit.entity.Player;

public class SystemMail extends BaseMail implements MailSystem{

    public SystemMail(int id, String sender, String topic, String content, String date) {
        super("system", id, sender, topic, content, date);
    }
    
    @Override
    public boolean collectValidate(Player p){
        return true;
    }

    @Override
    public boolean sendData() {
        return MailBoxAPI.setSend("system", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), "", 0, false, "0");
    }

    @Override
    public BaseFileMail addFile() {
        return new SystemFileMail(getId(),getSender(),getTopic(),getContent(),getDate(),"0",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }
    
}
