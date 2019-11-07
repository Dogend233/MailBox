package com.嘤嘤嘤.qwq.MailBox.Utils;

import java.util.Date; 
import java.text.SimpleDateFormat; 

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
}
