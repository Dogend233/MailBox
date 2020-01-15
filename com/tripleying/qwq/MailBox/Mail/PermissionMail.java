package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
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
        return MailBoxAPI.setSend("date", getId(), getSender(), "", permission, getTopic(), getContent(), getDate(), "", 0, "", false, "0");
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
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有领取这个邮件的权限！");
            return false;
        }
        return true;
    }

    @Override
    public BaseFileMail addFile() {
        return new PermissionFileMail(getId(),getSender(),getTopic(),getContent(),getDate(),permission,"0",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }
    
}
