package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.Event.MailCollectEvent;
import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.Utils.CdkeyUtil;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
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
        return MailUtil.setSend("cdkey", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), "", 0, "", only, "0");
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
                if(CdkeyUtil.sendCdkey(CdkeyUtil.generateCdkey(),getId())) return 1;
            } catch (Exception ex) {}
            return 0;
        }else{
            int count = 0;
            int ID = getId();
            for(int j=0;j<i;j++){
                try {
                    if(CdkeyUtil.sendCdkey(CdkeyUtil.generateCdkey(),ID)) count++;
                } catch (Exception ex) {
                    Logger.getLogger(CdkeyMail.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return count;
        }
    }
    
    @Override
    public boolean Collect(Player p){
        if(MailUtil.createBaseMail("player", 0, getSender(), Arrays.asList(p.getName()), "", getTopic(), getContent(), getDate(), "", 0, "", false, "").Send(Bukkit.getConsoleSender(), null)){
            MailUtil.setCollect(getType(), getId(), p.getName());
            MailCollectEvent mse = new MailCollectEvent(this, p);
            Bukkit.getServer().getPluginManager().callEvent(mse);
            return true;
        }
        return false;
    }
    
    @Override
    public void DeleteLocalCdkey(){
        CdkeyUtil.deleteLocalCdkey(getId());
    }
    
    @Override
    public boolean Delete(Player p){
        DeleteLocalCdkey();
        return DeleteData(p);
    }
    
}
