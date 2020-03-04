package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.util.ArrayList;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class PermissionMail extends BaseMail implements MailPermission {

    /**
     * 领取邮件所需权限
     */
    private String permission;
    
    public PermissionMail(int id, String sender, String topic, String content, String date, String permission) {
        super("permission", id, sender, topic, content, date);
        this.permission = permission;
    }

    @Override
    public boolean sendData() {
        return MailUtil.setSend("date", getId(), getSender(), "", permission, getTopic(), getContent(), getDate(), "", 0, "", false, "0");
    }
    
    @Override
    public final void setPermission(String permission){
        this.permission = permission;
    }
    
    @Override
    public final String getPermission(){
        return this.permission;
    }

    @Override
    public boolean collectValidate(Player p) {
        return MailPermission.super.collectValidate(p);
    }

    @Override
    public BaseFileMail addFile() {
        return new PermissionFileMail(getId(),getSender(),getTopic(),getContent(),getDate(),permission,"0",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }

    @Override
    public boolean sendValidate(Player p, ConversationContext cc) {
        return true;
    }
    
}
