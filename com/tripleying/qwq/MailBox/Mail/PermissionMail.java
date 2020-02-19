package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.Message;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.util.ArrayList;
import org.bukkit.entity.Player;

public class PermissionMail extends BaseMail implements MailPermission {

    // 领取邮件需要权限
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
        if(!p.hasPermission(getPermission())){
            p.sendMessage(Message.permissionNoPermission);
            return false;
        }
        return true;
    }

    @Override
    public BaseFileMail addFile() {
        return new PermissionFileMail(getId(),getSender(),getTopic(),getContent(),getDate(),permission,"0",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }
    
}
