package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.OuterMessage;
import com.tripleying.qwq.MailBox.Utils.TimeUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * date邮件
 */
public interface MailDate extends MailExpirable {

    /**
     * 设置截止日期
     * @param deadline 截止日期
     */
    public void setDeadline(String deadline);
    
    /**
     * 获取截止日期
     * @return 截止日期
     */
    public String getDeadline();
    
    /**
     * 是否达到邮件开始日期
     * @return boolean
     */
    default boolean isStart(){
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long start = df.parse(getDate()).getTime();
            long now = df.parse(TimeUtil.get("ymdhms")).getTime();
            return start<now;
        } catch (ParseException ex) {
            return false;
        }
    }
    
    default boolean collectValidate(Player p) {
        if(ExpireValidate()){
            p.sendMessage(OuterMessage.mailExpire.replace("%para%",""));
            Delete(p);
            return false;
        }
        return true;
    }
    
    default void generateDate(){
        if(getDate().equals("0")) setDate(TimeUtil.get("ymdhms"));
    }
    
    @Override
    default boolean ExpireValidate() {
        if(getDeadline().equals("0")) return false;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long deadlineTime = df.parse(getDeadline()).getTime();
            long now = df.parse(TimeUtil.get("ymdhms")).getTime();
            return deadlineTime<=now;
        } catch (ParseException ex) {
            Bukkit.getLogger().info(ex.getLocalizedMessage());
            return false; 
        }
    }
    
    @Override
    default String getExpireDate() {
        return getDeadline();
    }
    
    public String getDate();
    public void setDate(String data);
    public boolean Delete(Player p);
    
}
