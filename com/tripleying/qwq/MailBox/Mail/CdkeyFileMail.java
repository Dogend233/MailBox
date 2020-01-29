package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.Listener.MailCollectEvent;
import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CdkeyFileMail extends BaseFileMail implements MailCdkey {
    
    private boolean only;
    
    public CdkeyFileMail(int id, String sender, String topic, String content, String date, boolean only, String filename) {
        super("cdkey", id, sender, topic, content, date, filename);
        this.only = only;
    }
    public CdkeyFileMail(int id, String sender, String topic, String content, String date, boolean only, String filename, ArrayList<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super("cdkey", id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
        this.only = only;
    }
    
    @Override
    public boolean sendData() {
        return MailBoxAPI.setSend("cdkey", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), "", 0, "", only, getFileName());
    }
    
    @Override
    public BaseMail removeFile() {
        return new CdkeyMail(getId(),getSender(),getTopic(),getContent(),getDate(),only);
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
        if(MailBoxAPI.createBaseFileMail("player", 0, getSender(), Arrays.asList(p.getName()), "", getTopic(), getContent(), getDate(), "", 0, "", false, "", "0",getItemList(),getCommandList(),getCommandDescription(),getCoin(),getPoint()).Send(Bukkit.getConsoleSender(), null)){
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
        if(DeleteFile()){
            return DeleteData(p);
        }else{
            return false;
        }
    }
    
}
