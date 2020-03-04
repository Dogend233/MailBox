package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.util.List;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PermissionFileMail extends BaseFileMail implements MailPermission {
    
    private String permission;
    
    public PermissionFileMail(int id, String sender, String topic, String content, String date, String permission, String filename) {
        super("permission", id, sender, topic, content, date, filename);
        this.permission = permission;
    }
    public PermissionFileMail(int id, String sender, String topic, String content, String date, String permission, String filename, List<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super("permission", id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
        this.permission = permission;
    }

    @Override
    public boolean collectValidate(Player p) {
        return MailPermission.super.collectValidate(p);
    }

    @Override
    public boolean sendData() {
        return MailUtil.setSend("permission", getId(), getSender(), "", permission, getTopic(), getContent(), getDate(), "", 0, "", false, getFileName());
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

    @Override
    public boolean sendValidate(Player p, ConversationContext cc) {
        return true;
    }
    
}
