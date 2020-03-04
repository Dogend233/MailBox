package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.OuterMessage;
import java.text.SimpleDateFormat; 
import java.util.ArrayList;
import java.util.Date; 
import java.util.Calendar;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;

/**
 * 时间工具
 */
public class TimeUtil {
    
    /**
     * 上次操作时间
     */
    private static long lastTime;
    
    /**
     * 设置上次操作时间
     * @param l 时间
     */
    public static void setLastTime(long l){
        lastTime = l;
    }

    /**
     *更新上次操作时间
     */
    public static void updateLastTime(){
        long newTime = System.currentTimeMillis();
        if(newTime>lastTime) MailBox.CDKEY_DAY.clear();
        lastTime = System.currentTimeMillis()/(1000*3600*24)*(1000*3600*24)+24*60*60*1000;
    }

    /**
     * 获取当前时间
     * @param type 类型
     * @return 时间字符串
     */
    public static String get(String type){
        long l = System.currentTimeMillis();
        switch (type) {
            // 毫秒
            case "ms":
                return l+"";
            // 秒
            case "s":
                int length = (l+"").length();
                if (length > 3) {
                    return (l+"").substring(0,length-3);
                } else {
                    return l+"";
                }
            // 年月日时分秒
            case "ymdhms":
                Date date = new Date(l);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return dateFormat.format(date);
            default:
                break;
        }
        return null;
    }

    /**
     * 获取一个默认的时间字符串
     * @return 时间字符串
     */
    public static String getDefault(){
        return "2000-01-01 00:00:00";
    }

    /**
     * 将收到的字符串转化为时间列表
     * @param str 字符串
     * @param sender 指令发送者
     * @param cc 会话
     * @return 时间列表
     */
    public static List<Integer> toDate(String str, CommandSender sender, ConversationContext cc){
        String[] s;
        if(str.contains("-")) s = str.split("-");
        else s = str.split(" ");
        try{
            List<Integer> t = new ArrayList();
            for(String ss:s){
                while(ss.length()>1 && ss.indexOf('0')==0){
                    ss = ss.substring(1);
                }
                t.add(Integer.parseInt(ss));
            }
            return t;
        }catch(NumberFormatException e){
            if(cc==null) sender.sendMessage(OuterMessage.globalNumberError);
            else cc.getForWhom().sendRawMessage(OuterMessage.globalNumberError);
            return new ArrayList();
        }
    }

    /**
     * 将收到的时间列表转化为字符串
     * @param t 时间列表
     * @param sender 指令发送者
     * @param cc 会话
     * @return 时间字符串
     */
    public static String toDate(List<Integer> t, CommandSender sender, ConversationContext cc){
        int yyyy = t.get(0);
        int MM = t.get(1);
        int dd = t.get(2);
        if(yyyy>999 && yyyy<10000){
            if(MM>0 && MM<13){
                Calendar c = Calendar.getInstance();
                c.set(yyyy, MM, 0);
                int dayMax = c.get(Calendar.DAY_OF_MONTH);
                if(dd>0 && dd<=dayMax){
                    StringBuilder str = new StringBuilder();
                    str.append(yyyy);
                    str.append('-');
                    if(MM<10) str.append('0');
                    str.append(MM);
                    str.append('-');
                    if(dd<10) str.append('0');
                    str.append(dd);
                    str.append(' ');
                    if(t.size()==3){
                        str.append("00:00:00");
                        return str.toString();
                    }else{
                        int HH = t.get(3);
                        int mm = t.get(4);
                        int ss = t.get(5);
                        if(HH>=0 && HH<24){
                            if(mm>=0 && mm<60){
                                if(ss>=0 && ss<60){
                                    if(HH<10) str.append('0');
                                    str.append(HH);
                                    str.append(':');
                                    if(mm<10) str.append('0');
                                    str.append(mm);
                                    str.append(':');
                                    if(ss<10) str.append('0');
                                    str.append(ss);
                                    return str.toString();
                                }else{
                                    if(cc==null) sender.sendMessage(OuterMessage.datess);
                                    else cc.getForWhom().sendRawMessage(OuterMessage.datess);
                                }
                            }else{
                                if(cc==null) sender.sendMessage(OuterMessage.datemm);
                                else cc.getForWhom().sendRawMessage(OuterMessage.datemm);
                            }
                        }else{
                            if(cc==null) sender.sendMessage(OuterMessage.dateHH);
                            else cc.getForWhom().sendRawMessage(OuterMessage.dateHH);
                        }
                    }
                }else{
                    if(cc==null) sender.sendMessage(OuterMessage.datedd.replace("%max%", Integer.toString(dayMax)));
                    else cc.getForWhom().sendRawMessage(OuterMessage.datedd.replace("%max%", Integer.toString(dayMax)));
                }
            }else{
                if(cc==null) sender.sendMessage(OuterMessage.dateMM);
                else cc.getForWhom().sendRawMessage(OuterMessage.dateMM);
            }
        }else{
            if(cc==null) sender.sendMessage(OuterMessage.dateyyyy);
            else cc.getForWhom().sendRawMessage(OuterMessage.dateyyyy);
        }
        return null;
    }
}
