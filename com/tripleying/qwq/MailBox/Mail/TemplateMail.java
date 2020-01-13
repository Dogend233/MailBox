package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;

public class TemplateMail extends BaseMail implements MailTemplate {
    
    private String template;
    
    public TemplateMail(String sender, String topic, String content, String template) {
        super("template", 0, sender, topic, content, "0");
        this.template = template;
    }
    
    @Override
    public boolean Send(CommandSender send, ConversationContext cc) {
        return MailBoxAPI.saveTemplateMail((MailTemplate)this);
    }
    
    @Override
    public BaseFileMail addFile() {
        return new TemplateFileMail(getSender(),getTopic(),getContent(),template,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }

    @Override
    public void setTemplate(String templateName) {
        this.template = templateName;
    }

    @Override
    public String getTemplate() {
        return template;
    }
    
}
