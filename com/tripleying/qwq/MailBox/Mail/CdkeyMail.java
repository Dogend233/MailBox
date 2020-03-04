package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.Event.MailCollectEvent;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class CdkeyMail extends BaseMail implements MailCdkey {
    
    /**
     * 邮件是否只能有一个兑换码
     * 当邮件只有一个兑换码时
     * 领取邮件后自动删除此邮件
     */
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
    public boolean Delete(Player p){
        DeleteLocalCdkey();
        return DeleteData(p);
    }

    @Override
    public boolean collectValidate(Player p) {
        return true;
    }

    @Override
    public boolean sendValidate(Player p, ConversationContext cc) {
        return true;
    }
    
}
