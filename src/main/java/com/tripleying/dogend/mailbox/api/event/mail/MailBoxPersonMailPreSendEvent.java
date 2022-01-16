package com.tripleying.dogend.mailbox.api.event.mail;

import com.tripleying.dogend.mailbox.api.mail.PersonMail;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * 邮箱个人邮件预发送事件
 * 当邮件满足发送条件即将准备发送时触发
 * @author Dogend
 */
public class MailBoxPersonMailPreSendEvent extends MailBoxPersonMailEvent implements Cancellable {
    
    private static final HandlerList HANDLERS = new HandlerList();
    private final PersonMail pm;
    private boolean cancel;
    
    public MailBoxPersonMailPreSendEvent(PersonMail pm){
        this.pm = pm;
        this.cancel = false;
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

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean bln) {
        this.cancel = bln;
    }
    
}