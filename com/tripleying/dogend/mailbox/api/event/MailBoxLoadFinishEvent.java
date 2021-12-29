package com.tripleying.dogend.mailbox.api.event;

import org.bukkit.event.HandlerList;

/**
 * 插件加载完成事件
 * 此事件只会在服务器完全启动并运行后发生
 * 当插件首次加载或重载时, 除更新检查外所有操作执行完成时触发
 * @author Dogend
 */
public class MailBoxLoadFinishEvent extends MailBoxEvent {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
}
