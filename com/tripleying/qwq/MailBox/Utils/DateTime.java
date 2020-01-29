package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.Message;
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
            if(cc==null) sender.sendMessage(Message.globalNumberError);
            else cc.getForWhom().sendRawMessage(Message.globalNumberError);
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
                                    if(cc==null) sender.sendMessage(Message.datess);
                                    else cc.getForWhom().sendRawMessage(Message.datess);
                                }
                            }else{
                                if(cc==null) sender.sendMessage(Message.datemm);
                                else cc.getForWhom().sendRawMessage(Message.datemm);
                            }
                        }else{
                            if(cc==null) sender.sendMessage(Message.dateHH);
                            else cc.getForWhom().sendRawMessage(Message.dateHH);
                        }
                    }
                }else{
                    if(cc==null) sender.sendMessage(Message.datedd.replace("%max%", Integer.toString(dayMax)));
                    else cc.getForWhom().sendRawMessage(Message.datedd.replace("%max%", Integer.toString(dayMax)));
                }
            }else{
                if(cc==null) sender.sendMessage(Message.dateMM);
                else cc.getForWhom().sendRawMessage(Message.dateMM);
            }
        }else{
            if(cc==null) sender.sendMessage(Message.dateyyyy);
            else cc.getForWhom().sendRawMessage(Message.dateyyyy);
        }
        return null;
    }
}
