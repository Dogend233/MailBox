package com.tripleying.qwq.MailBox.API;

import com.tripleying.qwq.MailBox.Mail.BaseFileMail;
import com.tripleying.qwq.MailBox.Mail.BaseMail;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.Utils.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;

/**
 * MailBoxAPI
 * @author Dogend
 */
public class MailBoxAPI {
    
    private static String VERSION;
    
    /**
     * 获取插件版本
     * @return 插件版本
     */
    public static String getVersion(){
        if(VERSION==null) VERSION = MailBox.getInstance().getDescription().getVersion();
        return VERSION;
    }
    
    /**
     * 获取插件支持的邮件类型
     * @return 插件支持的邮件类型
     */
    public static List<String> getAllType(){
        List<String> all = new ArrayList();
        all.addAll(MailUtil.getTrueType());
        all.addAll(MailUtil.getVirtualType());
        return all;
    }
    
    /**
     * 检查指令执行者是否有相关的玩家权限
     * 'mailbox.player.*' 下的子权限用此方法判断
     * @param sender 指令执行者
     * @param perm 权限节点
     * @return boolean
     */
    public static boolean hasPlayerPermission(CommandSender sender, String perm){
        if(sender.hasPermission("mailbox.player.*")){
            return !(!sender.isOp() && sender.hasPermission("."+perm));
        }
        else return sender.hasPermission(perm);
    }

    /**
     * 创建一封文本邮件
     * @param type 邮件类型
     * @param sender 发件人
     * @param topic 邮件主题
     * @param content 邮件内容
     * @param date 发件日期
     * @return 文本邮件
     */
    public static BaseMail createBaseMail(String type, String sender, String topic, String content, String date){
        return new BaseMail(type,0,sender,topic,content,date).setType(type);
    }
    
    /**
     * 创建一封附件邮件
     * @param type 邮件类型
     * @param sender 发件人
     * @param topic 邮件主题
     * @param content 邮件内容
     * @param date 发件日期
     * @return 基础邮件
     */
    public static BaseFileMail createBaseFileMail(String type, String sender, String topic, String content, String date){
        return createBaseMail(type,sender,topic,content,date).addFile();
    }
    
}
