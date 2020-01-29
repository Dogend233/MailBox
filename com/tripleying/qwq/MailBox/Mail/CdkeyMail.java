package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.Listener.MailCollectEvent;
import com.tripleying.qwq.MailBox.API.Listener.MailSendEvent;
import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CdkeyMail extends BaseMail implements MailCdkey {
    
    private boolean only;
    
    public CdkeyMail(int id, String sender, String topic, String content, String date, boolean only) {
        super("cdkey", id, sender, topic, content, date);
        this.only = only;
    }
    
    @Override
    public boolean sendData() {
        return MailBoxAPI.setSend("cdkey", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), "", 0, "", only, "0");
    }

    @Override
    public BaseFileMail addFile() {
        return new CdkeyFileMail(getId(),getSender(),getTopic(),getContent(),getDate(),only,"0",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }

    @Override
    public boolean isOnly() {
        return only;
    }

    @Override
    public void setOnly(boolean only) {
        this.only = only;
    }
    
    @Override
    public int generateCdkey(int i) {
        if(only){
            try {
                if(MailBoxAPI.sendCdkey(MailBoxAPI.generateCdkey(),getId())) return 1;
                else return 0;
            } catch (Exception ex) {
                Logger.getLogger(CdkeyMail.class.getName()).log(Level.SEVERE, null, ex);
                return 0;
            }
        }else{
            int count = 0;
            int ID = getId();
            for(int j=0;j<i;j++){
                try {
                    if(MailBoxAPI.sendCdkey(MailBoxAPI.generateCdkey(),ID)) count++;
                } catch (Exception ex) {
                    Logger.getLogger(CdkeyMail.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return count;
        }
    }
    
    @Override
    public boolean Collect(Player p){
        if(MailBoxAPI.createBaseMail("player", 0, getSender(), Arrays.asList(p.getName()), "", getTopic(), getContent(), getDate(), "", 0, "", false, "").Send(Bukkit.getConsoleSender(), null)){
            MailBoxAPI.setCollect(getType(), getId(), p.getName());
            MailCollectEvent mse = new MailCollectEvent(this, p);
            Bukkit.getServer().getPluginManager().callEvent(mse);
            return true;
        }
        return false;
    }
    
    @Override
    public void DeleteLocalCdkey(){
        MailBoxAPI.deleteLocalCdkey(getId());
    }
    
    @Override
    public boolean Delete(Player p){
        DeleteLocalCdkey();
        return DeleteData(p);
    }
    
}
