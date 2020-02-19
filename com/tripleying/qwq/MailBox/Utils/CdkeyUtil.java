package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Mail.MailCdkey;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.Message;
import com.tripleying.qwq.MailBox.SQL.SQLManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

/**
 * Cdkey工具
 * @author Dogend
 */
public class CdkeyUtil {
    
    // 生成一个Cdkey
    public static String generateCdkey() throws Exception{
        String cdkey = EncryptUtil.MD5(TimeUtil.get("ms")).toUpperCase();
        for(int i=0;SQLManager.get().existCdkey(cdkey)>0;i++){
            cdkey = EncryptUtil.MD5(TimeUtil.get("ms"));
            if(i>10)throw new Exception();
        }
        return cdkey;
    }
    
    // 发送一个Cdkey
    public static boolean sendCdkey(String cdkey, int mail){
        return SQLManager.get().sendCdkey(cdkey, mail);
    }
    
    // 获取一个邮件的Cdkey
    public static List<String> getMailCdkey(int mail){
        return SQLManager.get().getCdkey(mail);
    }
    
    // 导出一个邮件的Cdkey
    public static boolean exportCdkey(int mail){
        try {
            List<String> cdk = getMailCdkey(mail);
            if(cdk.isEmpty()) return false;
            File f = FileUtil.getFile("Cdkey/"+mail+".txt");
            if(!f.exists()) f.createNewFile();
            FileOutputStream fs = new FileOutputStream(f);
            try (PrintStream p = new PrintStream(fs)) {
                cdk.forEach(c -> p.println(c));
            }
            return true;
        }   catch (IOException ex) {
            return false;
        }
    }
    
    // 删除本地已导出的兑换码
    public static boolean deleteLocalCdkey(int mail){
        File f = FileUtil.getFile("Cdkey/"+mail+".txt");
        if(f.exists()) return f.delete();
        else return true;
    }
    
    // 删除一个Cdkey
    public static boolean deleteCdkey(String cdkey){
        return SQLManager.get().deleteCdkey(cdkey);
    }
    
    // 兑换一个Cdkey
    public static void exchangeCdkey(Player p, String cdkey){
        if(!p.hasPermission("mailbox.admin.cdkey.day") && cdkeyDay(p)>=GlobalConfig.cdkeyDay){
            p.sendMessage(Message.exchangeExceedDay);
            return;
        }else{
            cdkeyDayAdd(p);
        }
        int mail = SQLManager.get().existCdkey(cdkey);
        if(mail>0){
            MailBox.updateRelevantMailList(p, "cdkey");
            if(!MailBox.getRelevantMailList(p, "cdkey").get("asRecipient").contains(mail)){
                p.sendMessage(Message.exchangeRepeat);
            }else{
                MailCdkey mc = (MailCdkey)MailBox.getMailHashMap("cdkey").get(mail);
                if(mc==null){
                    p.sendMessage(Message.exchangeNotMail);
                }else{
                    if(SQLManager.get().existCdkey(cdkey)>0){
                        if(mc.Collect(p)){
                            if(mc.isOnly()) mc.Delete(p);
                            else deleteCdkey(cdkey);
                            p.sendMessage(Message.exchangeSuccess);
                        }else{
                            p.sendMessage(Message.exchangeError);
                        }
                    }else{
                        p.sendMessage(Message.exchangeError);
                    }
                }
            }
        }else{
            p.sendMessage(Message.exchangeNotCdkey);
        }
    }
    
    // 获取一个玩家今日输入Cdkey次数
    public static int cdkeyDay(Player p){
        TimeUtil.updateLastTime();
        String pn = p.getName();
        if(MailBox.CDKEY_DAY.containsKey(pn)){
            return MailBox.CDKEY_DAY.get(pn);
        }else{
            return 0;
        }
    }
    
    // 使玩家今日输入兑换码的次数加一
    public static void cdkeyDayAdd(Player p){
        TimeUtil.updateLastTime();
        String pn = p.getName();
        if(MailBox.CDKEY_DAY.containsKey(pn)){
            MailBox.CDKEY_DAY.replace(pn, MailBox.CDKEY_DAY.get(pn)+1);
        }else{
            MailBox.CDKEY_DAY.put(pn, 1);
        }
    }
    
    // 重置玩家今日输入兑换码的次数
    public static void cdkeyDayRemove(String name){
        if(MailBox.CDKEY_DAY.containsKey(name)) MailBox.CDKEY_DAY.remove(name);
    }
    
}
