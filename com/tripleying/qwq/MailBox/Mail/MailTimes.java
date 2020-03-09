package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.OuterMessage;
import com.tripleying.qwq.MailBox.Utils.TimeUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

/**
 * times邮件
 */
public interface MailTimes extends MailExpirable {
    
    /**
     * 获取邮件剩余可领取数量
     * @return 数量
     */
    public int getTimes();

    /**
     * 设置获取邮件剩余可领取数量
     * @param times 数量
     */
    public void setTimes(int times);
    
    /**
     * 邮件剩余可领取数量验证
     * @return boolean
     */
    default boolean TimesValidate() {
        return getTimes()>0;
    }
    
    default boolean collectValidate(Player p) {
        if(ExpireValidate()){
            p.sendMessage(OuterMessage.mailExpire.replace("%para%",""));
            Delete(p);
            return false;
        }
        if(!TimesValidate()){
            p.sendMessage(OuterMessage.timesZero.replace("%para%", ""));
            Delete(p);
            return false;
        }
        return true;
    }
    
    default boolean sendValidate(Player p, ConversationContext cc){
        if(getTimes()>GlobalConfig.timesCount && !p.hasPermission("mailbox.admin.send.check.times")){
            p.sendMessage(OuterMessage.timesSendExceed.replace("%max%", Integer.toString(GlobalConfig.timesCount)));
            return false;
        }
        return true;
    }
    
    @Override
    default boolean ExpireValidate() {
        try {
            long deadline = new SimpleDateFormat("HH").parse(GlobalConfig.timesExpired).getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long sendTime = df.parse(getDate()).getTime();
            long now = df.parse(TimeUtil.get("ymdhms")).getTime();
            return (sendTime+deadline)<=now;
        } catch (ParseException ex) {
            return false; 
        }
    }
    
    @Override
    default String getExpireDate() {
        String date = getDate();
        if(date==null) date = TimeUtil.get("ymdhms"); 
        try {
            long deadline = new SimpleDateFormat("HH").parse(GlobalConfig.timesExpired).getTime();
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
