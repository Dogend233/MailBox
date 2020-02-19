package com.tripleying.qwq.MailBox.API.Event;

import com.tripleying.qwq.MailBox.Mail.BaseMail;
import org.bukkit.entity.Player;

/**
 *  邮件领取事件
 * @author Dogend
 */
public class MailCollectEvent extends MailEvent {

    private final Player player;
    
    /**
     * 构造器
     * @param bm 基础邮件
     * @param p 玩家
     */
    public MailCollectEvent(BaseMail bm, Player p){
        super(bm);
        this.player = p;
    }

    /**
     * 获取领取邮件的玩家
     * @return 玩家
     */
    public Player getPlayer(){
        return player;
    }
    
}
