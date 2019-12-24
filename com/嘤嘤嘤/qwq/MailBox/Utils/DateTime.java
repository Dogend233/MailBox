package com.嘤嘤嘤.qwq.MailBox.Utils;

import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import java.util.Date; 
import java.text.SimpleDateFormat; 
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;

public class DateTime {
    public static String get(String type){
        long l = System.currentTimeMillis();
        switch (type) {
            case "ms":
                return l+"";
            case "s":
                int length = (l+"").length();
                if (length > 3) {
                    return (l+"").substring(0,length-3);
                } else {
                    return l+"";
                }
            case "ymdhms":
                Date date = new Date(l);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return dateFormat.format(date);
            default:
                break;
        }
        return null;
    }
    
    public static String getDefault(){
        return "2000-01-01 00:00:00";
    }
    
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
            if(cc==null) sender.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"输入格式错误，请输入数字");
            else cc.getForWhom().sendRawMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"输入格式错误，请输入数字");
            return new ArrayList();
        }
    }
    
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
                                    if(cc==null) sender.sendMessage(GlobalConfig.warning+"秒数不合法，请输入0-59整数");
                                    else cc.getForWhom().sendRawMessage(GlobalConfig.warning+"秒数不合法，请输入0-59整数");
                                }
                            }else{
                                if(cc==null) sender.sendMessage(GlobalConfig.warning+"分钟不合法，请输入0-59整数");
                                else cc.getForWhom().sendRawMessage(GlobalConfig.warning+"分钟不合法，请输入0-59整数");
                            }
                        }else{
                            if(cc==null) sender.sendMessage(GlobalConfig.warning+"小时不合法，请输入0-23整数");
                            else cc.getForWhom().sendRawMessage(GlobalConfig.warning+"小时不合法，请输入0-23整数");
                        }
                    }
                }else{
                    if(cc==null) sender.sendMessage(GlobalConfig.warning+"天数不合法，请输入1-"+dayMax+"整数");
                    else cc.getForWhom().sendRawMessage(GlobalConfig.warning+"天数不合法，请输入1-"+dayMax+"整数");
                }
            }else{
                if(cc==null) sender.sendMessage(GlobalConfig.warning+"月份不合法，请输入1-12整数");
                else cc.getForWhom().sendRawMessage(GlobalConfig.warning+"月份不合法，请输入1-12整数");
            }
        }else{
            if(cc==null) sender.sendMessage(GlobalConfig.warning+"年份不合法，请输入4位整数");
            else cc.getForWhom().sendRawMessage(GlobalConfig.warning+"年份不合法，请输入4位整数");
        }
        return null;
    }
}
