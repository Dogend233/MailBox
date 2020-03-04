package com.tripleying.qwq.MailBox.Mail;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TemplateFileMail extends BaseFileMail implements MailTemplate {
    
    private String template;
    
    public TemplateFileMail(String sender, String topic, String content, String template, List<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super("template", 0, sender, topic, content, "0", "0", isl, cl, cd, coin, point);
        this.template = template;
    }
    
    @Override
    public boolean Send(CommandSender send, ConversationContext cc) {
        return MailTemplate.super.Send(send, cc);
    }

    @Override
    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public String getTemplate() {
        return template;
    }
    
    @Override
    public BaseMail removeFile() {
        return new TemplateMail(getSender(),getTopic(),getContent(),template);
    }

    @Override
    public boolean sendData() {
        return true;
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
