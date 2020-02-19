package com.tripleying.qwq.MailBox.API.Event;

import com.tripleying.qwq.MailBox.Mail.BaseMail;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * 邮件删除事件
 * @author Dogend
 */
public class MailDeleteEvent extends MailEvent {
    
    private final Player player;
    private final CommandSender sender;
    
    /**
     * 构造器
     * @param bm 基础邮件
     * @param p 玩家
     */
    public MailDeleteEvent(BaseMail bm, Player p){
        super(bm);
        this.player = p;
        this.sender = null;
    }
    
    /**
     * 构造器
     * @param bm 基础邮件
     * @param sender 指令执行者
     */
    public MailDeleteEvent(BaseMail bm, CommandSender sender){
        super(bm);
        this.sender = sender;
        this.player = null;
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
    
}
