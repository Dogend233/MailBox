package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.OuterMessage;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import com.tripleying.qwq.MailBox.Utils.TimeUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

/**
 * player邮件
 */
public interface MailPlayer extends MailExpirable {
    
    /**
     * 发送一封新的player邮件
     * @param sender 指令发送者
     * @param cc 会话
     * @param recipients 收件人列表
     */
    public void sendNewPlayerMail(CommandSender sender, ConversationContext cc, List<String> recipients);

    /**
     * 设置收件人列表
     * @param recipient 收件人列表
     */
    public void setRecipient(List<String> recipient);

    /**
     * 获取收件人列表
     * @return 收件人列表
     */
    public List<String> getRecipient();

    /**
     * 获取字符串形式收件人列表(以空格分割)
     * @return 收件人
     */
    default String getRecipientString(){
        String str = "";
        str = getRecipient().stream().map((n) -> " "+n).reduce(str, String::concat);
        str = str.substring(1);
        return str;
    }
    
    /**
     * 收件人长度验证
     * @param sender 指令发送者
     * @param cc 会话
     * @return boolean
     */
    default boolean recipientValidate(CommandSender sender, ConversationContext cc){
        String Recipient = getRecipientString();
        if(Recipient.length()>255){
            if(sender.hasPermission("mailbox.admin.send.extraplayer")){
                String thisRecipient = Recipient.substring(0, 255);
                thisRecipient = thisRecipient.substring(0, thisRecipient.lastIndexOf(' '));
                String otherRecipient = Recipient.substring(thisRecipient.length()+1);
                setRecipient(Arrays.asList(thisRecipient.split(" ")));
                StringBuilder sb = new StringBuilder();
                List<String> l = new ArrayList();
                for(String name:otherRecipient.split(" ")){
                    if(sb.append(" ").append(name).length()<=255){
                        l.add(name);
                    }else{
                        sendNewPlayerMail(sender, cc, l);
                        sb.delete(0, sb.length());
                        l.clear();
                        sb.append(" ").append(name);
                        l.add(name);
                    }
                }
                if(!l.isEmpty()) sendNewPlayerMail(sender, cc, l);
                return true;
            }else{
                sender.sendMessage(OuterMessage.playerRecipientExceedMax);
                return false;
            }
        }
        return true;
    }
    
    default boolean collectValidate(Player p) {
        if(ExpireValidate()){
            p.sendMessage(OuterMessage.mailExpire.replace("%para%",""));
            Delete(p);
            return false;
        }
        if(!getRecipient().contains(p.getName())){
            p.sendMessage(OuterMessage.playerNoRecipient);
            return false;
        }
        return true;
    }
    
    default boolean sendValidate(Player p, ConversationContext cc){
        int out = MailUtil.playerAsSenderAllow(p);
        int outed = MailUtil.asSenderNumber(p, "player");
        if((out-outed)<=0){
            p.sendMessage(OuterMessage.playerMailOutMax.replace("%type%",OuterMessage.getTypeName("player")));
            return false;
        }
        if(getRecipient().size()>GlobalConfig.playerMultiplayer && !p.hasPermission("mailbox.admin.send.multiplayer")){
            p.sendMessage(OuterMessage.playerRecipientMax.replace("%max%", Integer.toString(GlobalConfig.playerMultiplayer)));
            return false;
        }
        return true;
    }
    
    @Override
    default boolean ExpireValidate() {
        try {
            long deadline = new SimpleDateFormat("dd").parse(GlobalConfig.playerExpired).getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long sendTime = df.parse(getDate()).getTime();
            long now = df.parse(TimeUtil.get("ymdhms")).getTime();
            return (sendTime+deadline)<=now;
        } catch (ParseException ex) {
            Bukkit.getLogger().info(ex.getLocalizedMessage());
            return false; 
        }
    }
    
    @Override
    default String getExpireDate() {
        String date = getDate();
        if(date==null) date = TimeUtil.get("ymdhms"); 
        try {
            long deadline = new SimpleDateFormat("dd").parse(GlobalConfig.playerExpired).getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long sendTime = df.parse(date).getTime();
            return df.format(sendTime+deadline);
        } catch (ParseException ex) {
            Bukkit.getLogger().info(ex.getLocalizedMessage());
            return getDate(); 
        }
    }
    
    public String getDate();
    public boolean Delete(Player p);
    
}
