package com.tripleying.qwq.MailBox.Mail;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;

public interface MailTemplate {
    public boolean Send(CommandSender send, ConversationContext cc);
    public void setTemplate(String templateName);
    public String getTemplate();
}
