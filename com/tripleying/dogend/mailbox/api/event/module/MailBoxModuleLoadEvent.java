package com.tripleying.dogend.mailbox.api.event.module;

import com.tripleying.dogend.mailbox.api.module.MailBoxModule;
import org.bukkit.event.HandlerList;

/**
 * 邮箱模块加载事件
 * @author Dogend
 */
public class MailBoxModuleLoadEvent extends MailBoxModuleEvent {
    
    private static final HandlerList HANDLERS = new HandlerList();
    // 触发事件的模块
    private final MailBoxModule module;
    
    public MailBoxModuleLoadEvent(MailBoxModule module){
        this.module = module;
    }
    
    public MailBoxModule getModule(){
        return this.module;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
}