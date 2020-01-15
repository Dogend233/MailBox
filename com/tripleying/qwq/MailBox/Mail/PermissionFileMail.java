package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PermissionFileMail extends BaseFileMail implements MailPermission {
    
    // 领取邮件需要权限
    private String permission;
    
    public PermissionFileMail(int id, String sender, String topic, String content, String date, String permission, String filename) {
        super("permission", id, sender, topic, content, date, filename);
        this.permission = permission;
    }
    public PermissionFileMail(int id, String sender, String topic, String content, String date, String permission, String filename, ArrayList<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super("permission", id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
        this.permission = permission;
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
    public boolean sendData() {
        return MailBoxAPI.setSend("permission", getId(), getSender(), "", permission, getTopic(), getContent(), getDate(), "", 0, "", false, getFileName());
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
    public BaseMail removeFile() {
        return new PermissionMail(getId(),getSender(),getTopic(),getContent(),getDate(),permission);
    }
    
}
