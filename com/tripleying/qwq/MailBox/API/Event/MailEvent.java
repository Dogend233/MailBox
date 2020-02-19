package com.tripleying.qwq.MailBox.API.Event;

import com.tripleying.qwq.MailBox.Mail.BaseFileMail;
import com.tripleying.qwq.MailBox.Mail.BaseMail;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MailEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    private final boolean file;
    private final BaseMail bmail;
    
    /**
     * 构造器
     * @param bm 基础邮件
     */
    public MailEvent(BaseMail bm){
        this.bmail = bm;
        file = bm instanceof BaseFileMail;
    }
    
    /**
     * 邮件是否含有附件
     * @return boolean
     */
    public boolean hasFile(){
        return file;
    }
    
    /**
     * 获取邮件
     * @return 基础邮件
     */
    public BaseMail getMail(){
        return bmail;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
}
