package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.OuterMessage;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

/**
 * online邮件
 */
public interface MailOnline {
    
    /**
     * 发送一封player邮件
     * @param send 指令发送者
     * @param cc 会话
     * @param recipients 收件人列表
     * @return boolean
     */
    public boolean sendPlayerMail(CommandSender send, ConversationContext cc, List<String> recipients);
    
    default boolean Send(CommandSender send, ConversationContext cc) {
        if(Bukkit.getOnlinePlayers().isEmpty()){
            if(cc==null){
                send.sendMessage(OuterMessage.onlineNoPlayer);
            }else{
                cc.getForWhom().sendRawMessage(OuterMessage.onlineNoPlayer);
            }
            return false;
        }
        StringBuilder sb = new StringBuilder();
        List<String> l = new ArrayList();
        for(Player p:Bukkit.getOnlinePlayers()){
            String name = p.getName();
            if(sb.append(" ").append(name).length()<=255){
                l.add(name);
            }else{
                if(!sendPlayerMail(send,cc,l)) return false;
                sb.delete(0, sb.length());
                l.clear();
                sb.append(" ").append(name);
                l.add(name);
            }
        }
        if(!l.isEmpty()) return sendPlayerMail(send,cc,l);
        else return true;
    }
    
}
