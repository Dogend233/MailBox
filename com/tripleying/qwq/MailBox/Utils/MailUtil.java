package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Mail.*;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.OuterMessage;
import com.tripleying.qwq.MailBox.SQL.SQLManager;
import com.tripleying.qwq.MailBox.VexView.MailTipsHud;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 邮件工具
 */
public class MailUtil {
    
    /**
     * 真实邮件列表(有数据表)
     */
    private static final String[] TRUE_MAIL_TYPE = {"keytimes","times","date","system","permission","player","cdkey"};
    
    /**
     * 特殊邮件列表(领取时转化为其他邮件)
     */
    private static final String[] SPECIAL_MAIL_TYPE = {"cdkey"};
    
    /**
     * 虚拟邮件列表(发送时转化为其他邮件)
     */
    private static final String[] VIRTUAL_MAIL_TYPE = {"template","online"};

    /**
     * 获取真实邮件类型
     * @return 邮件类型列表
     */
    public static List<String> getTrueType(){
        return Arrays.asList(TRUE_MAIL_TYPE);
    }
    
    /**
     * 获取虚拟邮件类型
     * @return 邮件类型列表
     */
    public static List<String> getVirtualType(){
        return Arrays.asList(VIRTUAL_MAIL_TYPE);
    }
    
    /**
     * 获取除特殊邮件类型外的真实邮件类型
     * @return 邮件类型列表
     */
    public static List<String> getTrueTypeWhithoutSpecial(){
        List<String> all = new ArrayList();
        all.addAll(Arrays.asList(TRUE_MAIL_TYPE));
        Arrays.asList(SPECIAL_MAIL_TYPE).forEach(type -> {
            if(all.contains(type)) all.remove(type);
        });
        return all;
    }

    /**
     * 获取除特殊邮件类型外的虚拟邮件类型
     * @return 邮件类型列表
     */
    public static List<String> getVirtualTypeWhithoutSpecial(){
        List<String> all = new ArrayList();
        all.addAll(Arrays.asList(VIRTUAL_MAIL_TYPE));
        Arrays.asList(SPECIAL_MAIL_TYPE).forEach(type -> {
            if(all.contains(type)) all.remove(type);
        });
        return all;
    }
    
    /**
     * 获取与玩家有关的邮件
     * @param p 玩家
     * @param type 邮件类型
     * @return 发送/收到邮件的ID列表
     */
    public static HashMap<String, ArrayList<Integer>> getRelevantMail(Player p, String type){
        HashMap<String, ArrayList<Integer>> hm = new HashMap();
        String name = p.getName();
        ArrayList<Integer> senderList = new ArrayList();
        ArrayList<Integer> recipientList = new ArrayList();
        ArrayList<Integer> deleteList = new ArrayList();
        switch (type) {
            case "player":
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(isExpired(v)){
                        deleteList.add(k);
                    }else{
                        if(v.getSender().equals(name)) senderList.add(k);
                        if(((MailPlayer)v).getRecipient().contains(name)) recipientList.add(k);
                    }
                });
                deleteList.forEach((i) -> MailBox.getMailHashMap(type).get(i).Delete(null));
                break;
            case "system":
                ArrayList<Integer> collectedSystem = SQLManager.get().getCollectedMailList(p, type);
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(v.getSender().equals(name)) senderList.add(k);
                    if(!collectedSystem.contains(k)) recipientList.add(k);
                });
                break;
            case "permission":
                ArrayList<Integer> collectedPermission = SQLManager.get().getCollectedMailList(p, type);
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(v.getSender().equals(name)) senderList.add(k);
                    if(p.hasPermission(((MailPermission)v).getPermission()) && !collectedPermission.contains(k)) recipientList.add(k);
                });
                break;
            case "date":
                ArrayList<Integer> collectedDate = SQLManager.get().getCollectedMailList(p, type);
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(((MailDate)v).isStart() || p.hasPermission("mailbox.admin.see.date")){
                        if(isExpired(v)){
                            deleteList.add(k);
                        }else{
                            if(v.getSender().equals(name)) senderList.add(k);
                            if(!collectedDate.contains(k)) recipientList.add(k);
                        }
                    }else{
                        if(isExpired(v)){
                            deleteList.add(k);
                        }else{
                            if(v.getSender().equals(name)) senderList.add(k);
                        }
                    }
                });
                deleteList.forEach((i) -> MailBox.getMailHashMap(type).get(i).Delete(null));
                break;

            case "times":
                ArrayList<Integer> collectedTimes = SQLManager.get().getCollectedMailList(p, type);
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(((MailTimes)v).TimesValidate()){
                        if(v.getSender().equals(name)) senderList.add(k);
                        if(!collectedTimes.contains(k)) recipientList.add(k);
                    }else{
                        deleteList.add(k);
                    }
                });
                deleteList.forEach((i) -> MailBox.getMailHashMap(type).get(i).Delete(null));
                break;
            case "keytimes":
                ArrayList<Integer> collectedKeyTimes = SQLManager.get().getCollectedMailList(p, type);
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(((MailTimes)v).TimesValidate()){
                        if(v.getSender().equals(name)) senderList.add(k);
                        if(!collectedKeyTimes.contains(k)) recipientList.add(k);
                    }else{
                        deleteList.add(k);
                    }
                });
                deleteList.forEach((i) -> MailBox.getMailHashMap(type).get(i).Delete(null));
                break;
            case "cdkey":
                ArrayList<Integer> collectedCdkey = SQLManager.get().getCollectedMailList(p, type);
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(v.getSender().equals(name)) senderList.add(k);
                    if(!collectedCdkey.contains(k)) recipientList.add(k);
                });
                break;
        }
        hm.put("asSender", senderList);
        hm.put("asRecipient", recipientList);
        return hm;
    }

    /**
     * 设置玩家领取一封邮件
     * @param type 邮件类型
     * @param id 邮件ID
     * @param playername 玩家名
     * @return boolean
     */
    public static boolean setCollect(String type, int id, String playername){
        return SQLManager.get().setMailCollect(type, id, playername);
    }
    
    /**
     * 发送一封邮件
     * @param type 邮件类型
     * @param id 邮件id
     * @param playername 玩家名
     * @param recipient 收件人列表
     * @param permission 所需权限
     * @param topic 主题
     * @param text 内容
     * @param date 发送日期
     * @param deadline 截止日期
     * @param times 邮件数量
     * @param key 邮件口令
     * @param only 兑换码唯一性
     * @param filename 附件名
     * @return boolean
     */
    public static boolean setSend(String type, int id, String playername,
            String recipient, String permission, String topic, String text, 
            String date, String deadline, int times, String key, boolean only,
            String filename){
        if(id==0){
            return SQLManager.get().sendMail(type, playername, recipient, permission, topic, text, date, deadline, times, key, only, filename);
        }else{
            // 修改现有邮件
            return false;
        }
    }

    /**
     * 删除一封邮件
     * @param type 邮件类型
     * @param id 邮件ID
     * @return boolean
     */
    public static boolean setDelete(String type, int id){
        return SQLManager.get().deleteMail(type, id);
    }
    
    /**
     * 判断一封邮件是否过期
     * @param bm 邮件
     * @return boolean
     */
    public static boolean isExpired(BaseMail bm){
        if(bm instanceof MailExpirable){
            return ((MailExpirable)bm).ExpireValidate();
        }else{
            return false;
        }
    }

    /**
     * 获取玩家某类型邮件已发件数量
     * @param p 玩家
     * @param type 邮件类型
     * @return 数量
     */
    public static int asSenderNumber(Player p, String type){
        MailBox.updateRelevantMailList(p, type);
        return MailBox.getRelevantMailList(p, type).get("asSender").size();
    }

    /**
     * 获取玩家player类型邮件最大发件的数量
     * @param p 玩家
     * @return 数量
     */
    public static int playerAsSenderAllow(Player p){
        for(int i=GlobalConfig.playerOut;i>0;i--){
            if(p.hasPermission("mailbox.send.player.out."+i)){
                return i;
            }
        }
        return 0;
    }
    
    /**
     * 创建一封基础邮件
     * @param type 邮件类型
     * @param id 邮件id
     * @param sender 发件人
     * @param recipient 收件人列表
     * @param permission 所需权限
     * @param topic 主题
     * @param content 内容
     * @param date 发送日期
     * @param deadline 截止日期
     * @param times 邮件数量
     * @param key 邮件口令
     * @param only 兑换码唯一性
     * @param template 模板名
     * @return 基础邮件
     */
    public static BaseMail createBaseMail(String type, int id, String sender, List<String> recipient, String permission, String topic, String content, String date, String deadline, int times, String key, boolean only, String template){
        switch(type){
            case "system":
                return new SystemMail(id, sender, topic, content, date);
            case "permission":
                return new PermissionMail(id, sender, topic, content, date, permission);
            case "date":
                return new DateMail(id, sender, topic, content, date, deadline);
            case "player":
                return new PlayerMail(id, sender, topic, content, date, recipient);
            case "times":
                return new TimesMail(id, sender, topic, content, date, times);
            case "keytimes":
                return new KeyTimesMail(id, sender, topic, content, date, times, key);
            case "cdkey":
                return new CdkeyMail(id, sender, topic, content, date, only);
            case "online":
                return new OnlineMail(sender, topic, content, date);
            case "template":
                return new TemplateMail(sender, topic, content, template);
            default:
                return null;
        }
    }
    
    /**
     * 创建一封基础附件邮件
     * @param type 邮件类型
     * @param id 邮件id
     * @param sender 发件人
     * @param recipient 收件人列表
     * @param permission 所需权限
     * @param topic 主题
     * @param content 内容
     * @param date 发送日期
     * @param deadline 截止日期
     * @param times 邮件数量
     * @param key 邮件口令
     * @param only 兑换码唯一性
     * @param filename 附件名
     * @return 基础附件邮件
     */
    public static BaseFileMail createBaseFileMail(String type, int id, String sender, List<String> recipient, String permission, String topic, String content, String date, String deadline, int times, String key, boolean only, String filename){
        switch(type){
            case "system":
                return new SystemFileMail(id, sender, topic, content, date, filename);
            case "permission":
                return new PermissionFileMail(id, sender, topic, content, date, permission, filename);
            case "date":
                return new DateFileMail(id, sender, topic, content, date, deadline, filename);
            case "player":
                return new PlayerFileMail(id, sender, topic, content, date, recipient, filename);
            case "times":
                return new TimesFileMail(id, sender, topic, content, date, times, filename);
            case "keytimes":
                return new KeyTimesFileMail(id, sender, topic, content, date, times, key, filename);
            case "cdkey":
                return new CdkeyFileMail(id, sender, topic, content, date, only, filename);
            case "online":
            case "template":
            default:
                return null;
        }
    }
    
    /**
     * 创建一封基础附件邮件
     * @param type 邮件类型
     * @param id 邮件id
     * @param sender 发件人
     * @param recipient 收件人列表
     * @param permission 所需权限
     * @param topic 主题
     * @param content 内容
     * @param date 发送日期
     * @param deadline 截止日期
     * @param times 邮件数量
     * @param key 邮件口令
     * @param only 兑换码唯一性
     * @param template 模板名
     * @param filename 附件名
     * @param isl 物品列表
     * @param cl 指令列表
     * @param cd 指令描述列表
     * @param coin 金币
     * @param point 点券
     * @return 基础附件邮件
     */
    public static BaseFileMail createBaseFileMail(String type, int id, String sender, List<String> recipient, String permission, String topic, String content, String date, String deadline, int times, String key, boolean only, String template, String filename, List<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point){
        switch(type){
            case "system":
                return new SystemFileMail(id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
            case "permission":
                return new PermissionFileMail(id, sender, topic, content, date, permission, filename, isl, cl, cd, coin, point);
            case "date":
                return new DateFileMail(id, sender, topic, content, date, deadline, filename, isl, cl, cd, coin, point);
            case "player":
                return new PlayerFileMail(id, sender, topic, content, date, recipient, filename, isl, cl, cd, coin, point);
            case "times":
                return new TimesFileMail(id, sender, topic, content, date, times, filename, isl, cl, cd, coin, point);
            case "keytimes":
                return new KeyTimesFileMail(id, sender, topic, content, date, times, key, filename, isl, cl, cd, coin, point);
            case "cdkey":
                return new CdkeyFileMail(id, sender, topic, content, date, only, filename, isl, cl, cd, coin, point);
            case "online":
                return new OnlineFileMail(sender, topic, content, date, isl, cl, cd, coin, point);
            case "template":
                return new TemplateFileMail(sender, topic, content, template, isl, cl, cd, coin, point);
            default:
                return null;
        }
    }

    /**
     * 向玩家发送邮件提醒
     * @param p 玩家
     * @param msg 提醒信息
     * @param key 邮件口令
     */
    public static void sendTips(Player p, String msg, String key){
        if(GlobalConfig.tips.contains("msg")){
            p.sendMessage(msg);
            if(!key.equals("")){
                TextComponent tc = new TextComponent(OuterMessage.tipsKey.replace("%key%", key));
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, key));
                p.spigot().sendMessage(tc);
            }
        }
        if(!GlobalConfig.server_under_1_8 && GlobalConfig.tips.contains("title")) {
            if(GlobalConfig.server_under_1_11){
                p.sendTitle(msg, key);
            }else{
                p.sendTitle(msg, key, 10, 70, 20);
            }
        }
        if(GlobalConfig.tips.contains("sound")) p.playSound(p.getLocation(), GlobalConfig.tipsSound, 1, 1);
        if(GlobalConfig.enVexView && GlobalConfig.tips.contains("flow")) VexViewAPI.sendFlowView(p, msg+key, 10, true);
        if(GlobalConfig.enVexView && GlobalConfig.tips.contains("hud")) MailTipsHud.setMailTipsHud(p);
    }
    
}
