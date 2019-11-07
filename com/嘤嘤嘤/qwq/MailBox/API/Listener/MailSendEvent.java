package com.嘤嘤嘤.qwq.MailBox.API.Listener;

import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MailSendEvent extends Event{
    
    private static final HandlerList HANDLERS = new HandlerList();
    private final int type;
    private final TextMail mail;
    private final Player player;
    
    public MailSendEvent(TextMail tm, Player p){
        this.mail = tm;
        if(tm instanceof FileMail){
            this.type = 1;
        }else{
            this.type = 0;
        }
        this.player = p;
    }
    
    // 获取邮件种类 ( 0 : TextMail , 1 : FileMail)
    public int getMailType(){
        return type;
    }
    
    // 获取邮件
    public TextMail getMail(){
        return mail;
    }
    
    // 获取发送邮件的玩家
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
