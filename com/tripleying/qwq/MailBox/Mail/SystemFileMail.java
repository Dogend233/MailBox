package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.MailUtil;
import java.util.List;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SystemFileMail extends BaseFileMail implements MailSystem {

    public SystemFileMail(int id, String sender, String topic, String content, String date, String filename) {
        super("system", id, sender, topic, content, date, filename);
    }
    public SystemFileMail(int id, String sender, String topic, String content, String date, String filename, List<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point){
        super("system", id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
    }

    @Override
    public boolean sendData() {
        return MailUtil.setSend("system", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), "", 0, "", false, getFileName());
    }

    @Override
    public BaseMail removeFile() {
        return new SystemMail(getId(),getSender(),getTopic(),getContent(),getDate());
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
