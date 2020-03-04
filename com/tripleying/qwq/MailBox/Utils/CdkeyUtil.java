package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Mail.BaseMail;
import com.tripleying.qwq.MailBox.Mail.MailCdkey;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.OuterMessage;
import com.tripleying.qwq.MailBox.SQL.SQLManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import org.bukkit.entity.Player;

/**
 * Cdkey工具
 */
public class CdkeyUtil {
    
    /**
     * 生成一个Cdkey
     * @return Cdkey
     * @throws Exception 尝试失败次数过多
     */
    public static String generateCdkey() throws Exception{
        String cdkey = EncryptUtil.MD5(TimeUtil.get("ms")).toUpperCase();
        for(int i=0;SQLManager.get().existCdkey(cdkey)>0;i++){
            cdkey = EncryptUtil.MD5(TimeUtil.get("ms"));
            if(i>10)throw new Exception();
        }
        return cdkey;
    }
    
    /**
     * 发送一个Cdkey
     * @param cdkey Cdkey
     * @param mail 邮件id
     * @return boolean
     */
    public static boolean sendCdkey(String cdkey, int mail){
        return SQLManager.get().sendCdkey(cdkey, mail);
    }
    
    /**
     * 获取一个邮件的Cdkey
     * @param mail 邮件id
     * @return Cdkey列表
     */
    public static List<String> getMailCdkey(int mail){
        return SQLManager.get().getCdkey(mail);
    }
    
    /**
     * 导出一个邮件的Cdkey至本地
     * @param mail 邮件id
     * @return boolean
     */
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

    /**
     * 删除本地已导出的Cdkey
     * @param mail 邮件id
     * @return boolean
     */
    public static boolean deleteLocalCdkey(int mail){
        File f = FileUtil.getFile("Cdkey/"+mail+".txt");
        if(f.exists()) return f.delete();
        else return true;
    }
    
    /**
     * 删除一个Cdkey
     * @param cdkey Cdkey
     * @return boolean
     */
    public static boolean deleteCdkey(String cdkey){
        return SQLManager.get().deleteCdkey(cdkey);
    }

    /**
     * 使玩家兑换一个Cdkey
     * @param p 玩家
     * @param cdkey Cdkey
     */
    public static void exchangeCdkey(Player p, String cdkey){
        if(!p.hasPermission("mailbox.admin.cdkey.day") && cdkeyDay(p)>=GlobalConfig.cdkeyDay){
            p.sendMessage(OuterMessage.exchangeExceedDay);
            return;
        }else{
            cdkeyDayAdd(p);
        }
        int mail = SQLManager.get().existCdkey(cdkey);
        if(mail>0){
            MailBox.updateRelevantMailList(p, "cdkey");
            if(!MailBox.getRelevantMailList(p, "cdkey").get("asRecipient").contains(mail)){
                p.sendMessage(OuterMessage.exchangeRepeat);
            }else{
                MailCdkey mc = (MailCdkey)MailBox.getMailHashMap("cdkey").get(mail);
                if(mc==null){
                    p.sendMessage(OuterMessage.exchangeNotMail);
                }else{
                    if(SQLManager.get().existCdkey(cdkey)>0){
                        if(((BaseMail)mc).Collect(p)){
                            if(mc.isOnly()) ((BaseMail)mc).Delete(p);
                            else deleteCdkey(cdkey);
                            p.sendMessage(OuterMessage.exchangeSuccess);
                        }else{
                            p.sendMessage(OuterMessage.exchangeError);
                        }
                    }else{
                        p.sendMessage(OuterMessage.exchangeError);
                    }
                }
            }
        }else{
            p.sendMessage(OuterMessage.exchangeNotCdkey);
        }
    }
    
    /**
     * 获取一个玩家今日输入Cdkey次数
     * @param p 玩家
     * @return 次数
     */
    public static int cdkeyDay(Player p){
        TimeUtil.updateLastTime();
        String pn = p.getName();
        if(MailBox.CDKEY_DAY.containsKey(pn)){
            return MailBox.CDKEY_DAY.get(pn);
        }else{
            return 0;
        }
    }

    /**
     * 使玩家今日输入兑换码的次数加一
     * @param p 玩家
     */
    public static void cdkeyDayAdd(Player p){
        TimeUtil.updateLastTime();
        String pn = p.getName();
        if(MailBox.CDKEY_DAY.containsKey(pn)){
            MailBox.CDKEY_DAY.replace(pn, MailBox.CDKEY_DAY.get(pn)+1);
        }else{
            MailBox.CDKEY_DAY.put(pn, 1);
        }
    }
    
    /**
     * 重置玩家今日输入兑换码的次数
     * @param name 玩家名字
     */
    public static void cdkeyDayRemove(String name){
        if(MailBox.CDKEY_DAY.containsKey(name)) MailBox.CDKEY_DAY.remove(name);
    }
    
}
