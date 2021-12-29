package com.tripleying.dogend.mailbox.util;

import com.tripleying.dogend.mailbox.api.util.CommonConfig;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具
 * @author Dogend
 */
public class TimeUtil {
    
    private static final SimpleDateFormat sdf;
    
    static{
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * 将日期格式化为字符串
     * @param date Date
     * @return String
     */
    public static String date2String(Date date){
        return sdf.format(date);
    }
    
    /**
     * 将时间戳格式化为字符串
     * @param time long
     * @return String
     */
    public static String long2String(long time){
        return sdf.format(new Date(time));
    }
    
    /**
     * 获取当前时间并格式化为字符串
     * @return String
     */
    public static String currentTimeString(){
        return long2String(System.currentTimeMillis());
    }
    
    /**
     * 判断个人邮件是否过期
     * @param sendtime 发送时间
     * @return boolean
     */
    public static boolean isExpire(String sendtime){
        try {
            long deadline = sdf.parse(sendtime).getTime() + 1000L*60*60*24*CommonConfig.expire_day;
            return System.currentTimeMillis()>deadline;
        } catch (ParseException ex) {
            ex.printStackTrace();
            return false; 
        }
    }
    
}
