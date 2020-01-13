package com.tripleying.qwq.MailBox.API.Listener;

import com.tripleying.qwq.MailBox.Mail.BaseFileMail;
import com.tripleying.qwq.MailBox.Mail.BaseMail;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *  邮件领取事件
 * @author Dogend
 */
public class MailCollectEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final boolean file;
    private final BaseMail bmail;
    private final Player player;
    
    /**
     * 构造器
     * @param bm 基础邮件
     * @param p 玩家
     */
    public MailCollectEvent(BaseMail bm, Player p){
        this.bmail = bm;
        file = bm instanceof BaseFileMail;
        this.player = p;
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

    /**
     * 获取领取邮件的玩家
     * @return 玩家
     */
    public Player getPlayer(){
        return player;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
}
