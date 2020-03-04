package com.tripleying.qwq.MailBox.API.Event;

import com.tripleying.qwq.MailBox.Mail.BaseMail;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 邮件发送事件
 */
public class MailSendEvent extends MailEvent{
    
    /**
     * 玩家
     */
    private final Player player;
    
    /**
     * 指令发送者
     */
    private final CommandSender sender;
    
    public MailSendEvent(BaseMail bm, Player p){
        super(bm);
        this.player = p;
        this.sender = null;
    }

    public MailSendEvent(BaseMail bm, CommandSender sender){
        super(bm);
        this.sender = sender;
        this.player = null;
    }

    /**
     * 获取发送邮件的玩家
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
