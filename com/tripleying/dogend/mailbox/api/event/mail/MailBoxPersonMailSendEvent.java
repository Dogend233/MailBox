package com.tripleying.dogend.mailbox.api.event.mail;

import com.tripleying.dogend.mailbox.api.mail.PersonMail;
import org.bukkit.event.HandlerList;

/**
 * 邮箱个人邮件发送事件
 * @author Dogend
 */
public class MailBoxPersonMailSendEvent extends MailBoxPersonMailEvent {
    
    private static final HandlerList HANDLERS = new HandlerList();
    private final PersonMail pm;
    
    public MailBoxPersonMailSendEvent(PersonMail pm){
        this.pm = pm;
    }
    
    public PersonMail getPersonMail(){
        return this.pm;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
}
