package com.tripleying.qwq.MailBox.API.Listener;

import com.tripleying.qwq.MailBox.Mail.BaseFileMail;
import com.tripleying.qwq.MailBox.Mail.BaseMail;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 邮件删除事件
 * @author Dogend
 */
public class MailDeleteEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    private final boolean file;
    private final BaseMail bmail;
    private final Player player;
    private final CommandSender sender;
    
    /**
     * 构造器
     * @param bm 基础邮件
     * @param p 玩家
     */
    public MailDeleteEvent(BaseMail bm, Player p){
        this.bmail = bm;
        file = bm instanceof BaseFileMail;
        this.player = p;
        this.sender = null;
    }
    
    /**
     * 构造器
     * @param bm 基础邮件
     * @param sender 指令执行者
     */
    public MailDeleteEvent(BaseMail bm, CommandSender sender){
        this.bmail = bm;
        file = bm instanceof BaseFileMail;
        this.sender = sender;
        this.player = null;
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
     * 获取删除邮件的玩家
     * @return 玩家
     */
    public Player getPlayer(){
        return player;
    }
    
    /**
     * 获取发件人
     * @return String
     */
    public String getName(){
        if(player==null){
            return sender.getName();
        }else{
            return player.getName();
        }
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
}
