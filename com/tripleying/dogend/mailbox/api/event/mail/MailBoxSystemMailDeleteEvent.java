package com.tripleying.dogend.mailbox.api.event.mail;

import com.tripleying.dogend.mailbox.api.mail.SystemMail;
import org.bukkit.event.HandlerList;

/**
 * 邮箱系统邮件删除事件
 * @author Dogend
 */
public class MailBoxSystemMailDeleteEvent extends MailBoxSystemMailEvent {
    
    private static final HandlerList HANDLERS = new HandlerList();
    private final SystemMail sm;
    
    public MailBoxSystemMailDeleteEvent(SystemMail sm){
        this.sm = sm;
    }
    
    public SystemMail getSystemMail(){
        return this.sm;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
