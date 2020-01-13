package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class OnlineMail extends BaseMail implements MailOnline {
    
    public OnlineMail(String sender, String topic, String content, String date) {
        super("online",0, sender, topic, content, date);
    }

    @Override
    public boolean Send(CommandSender send, ConversationContext cc) {
        if(Bukkit.getOnlinePlayers().isEmpty()){
            if(cc==null){
                send.sendMessage(GlobalConfig.normal+"[邮件预览]：没有在线玩家");
            }else{
                cc.getForWhom().sendRawMessage(GlobalConfig.normal+"[邮件预览]：没有在线玩家");
            }
            return false;
        }
        StringBuilder sb = new StringBuilder();
        List<String> l = new ArrayList();
        for(Player p:Bukkit.getOnlinePlayers()){
            String name = p.getName();
            if(sb.append(" ").append(name).length()<=200){
                l.add(name);
            }else{
                if(!MailBoxAPI.createBaseMail("player", 0, getSender(), l, "", getTopic(), getContent(), getDate(), "", 0, false, "").Send(send, cc)) return false;
                sb.delete(0, sb.length());
                l.clear();
                sb.append(" ").append(name);
                l.add(name);
            }
        }
        if(!l.isEmpty()) return MailBoxAPI.createBaseMail("player", 0, getSender(), l, "", getTopic(), getContent(), getDate(), "", 0, false, "").Send(send, cc);
        else return true;
    }
    
    @Override
    public BaseFileMail addFile() {
        return new OnlineFileMail(getSender(),getTopic(),getContent(),getDate(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    }
    
}
