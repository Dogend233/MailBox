package com.tripleying.qwq.MailBox.Mail;

import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class TemplateMail extends BaseMail implements MailTemplate {
    
    /**
     * 模板文件名
     */
    private String template;
    
    public TemplateMail(String sender, String topic, String content, String template) {
        super("template", 0, sender, topic, content, "0");
        this.template = template;
    }
    
    @Override
    public boolean Send(CommandSender send, ConversationContext cc) {
        return MailTemplate.super.Send(send, cc);
    }
    
    @Override
    public BaseFileMail addFile() {
        return new TemplateFileMail(getSender(),getTopic(),getContent(),template,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
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
