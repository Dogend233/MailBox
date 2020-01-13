package com.tripleying.qwq.MailBox.Mail;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;

public interface MailOnline {
    public boolean Send(CommandSender send, ConversationContext cc);
}
