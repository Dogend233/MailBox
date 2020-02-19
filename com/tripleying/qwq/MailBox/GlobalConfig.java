package com.tripleying.qwq.MailBox;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.Utils.FileUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class GlobalConfig {
    
    private static final List<String> DEFAULT_LANGUAGE = Arrays.asList("zh_cn");
    
    public static boolean server_over_1_12 = false;
    public static boolean server_under_1_11 = false;
    public static boolean server_under_1_10 = false;
    public static boolean server_under_1_9 = false;
    public static boolean server_under_1_8 = false;
    public static boolean vexview_under_2_6_8;
    public static boolean vexview_under_2_6;
    public static boolean vexview_over_2_5;
    public static boolean vexview_under_2_5;
    public static boolean enVault;
    public static boolean enPlayerPoints;
    public static boolean enVexView;
    public static boolean enPlaceholderAPI;
    public static boolean enLocaleLanguageAPI;
    
    public static String language;
    public static boolean fileSQL;
    public static String pluginPrefix;
    public static List<String> tips;
    public static Sound tipsSound;
    public static int maxItem;
    public static String fileBanLore;
    public static List<String> fileBanId;
    public static int topicMax;
    public static int contentMax;
    public static String playerExpired;
    public static int playerMultiplayer;
    public static int playerOut;
    public static String timesExpired;
    public static int timesCount;
    public static int cdkeyDay;
    public static double vaultMax;
    public static double vaultExpand;
    public static double vaultItem;
    public static int playerPointsMax;
    public static int playerPointsExpand;
    public static int playerPointsItem;
    
    public static void setGlobalConfig(FileConfiguration config){
        ConsoleCommandSender ccs = Bukkit.getConsoleSender();
        // 语言
        ccs.sendMessage(ConfigMessage.lang_check);
        String lang = config.getString("language","zh_cn");
        if(FileUtil.existFile("Message/"+lang+".yml") || DEFAULT_LANGUAGE.contains(lang)){
            language = lang;
        }else{
            ccs.sendMessage(ConfigMessage.lang_not_exist.replace("%lang%", lang));
            language = "zh_cn";
        }
        Message.setLanguage(language);
        fileSQL = config.getBoolean("database.fileSQL",false);
        tips = config.getStringList("mailbox.newMailTips");
        // 提示声音
        try{
            tipsSound = Sound.valueOf(config.getString("mailbox.newMailTipsSound","ENTITY_PLAYER_LEVELUP"));
        }catch(Exception e){
            if(server_under_1_9) tipsSound = Sound.valueOf("LEVEL_UP");
            else tipsSound = Sound.valueOf("ENTITY_PLAYER_LEVELUP");
        }
        // 附件
        maxItem = config.getInt("mailbox.file.maxItem",9);
        fileBanLore = config.getString("mailbox.file.ban.lore","§e- 无法作为邮件");
        fileBanId = formatMaterial(config.getStringList("mailbox.file.ban.id"));
        // 邮件
        topicMax = config.getInt("mailbox.topic_max",30);
        contentMax = config.getInt("mailbox.content_max",255);
        // player邮件
        playerExpired = config.getString("mailbox.player.expire","30");
        playerMultiplayer = config.getInt("mailbox.player.multiplayer",1);
        playerOut = config.getInt("mailbox.player.out",10);
        // times邮件
        timesExpired  = config.getString("mailbox.times.expire","24");
        timesCount = config.getInt("mailbox.times.count",20);//单封times邮件最大数量
        // cdkey邮件
        cdkeyDay = config.getInt("mailbox.cdkey_day",5);// 玩家每日可输入兑换码的次数
        // [Vault]设置
        vaultMax = config.getDouble("mailbox.vault.max",5000);// 单次邮件发送最大值
        vaultExpand = config.getDouble("mailbox.vault.expand",10);// 发送邮件时所消耗的金钱
        vaultItem = config.getDouble("mailbox.vault.item",50);// 每多一个附件物品增加的金钱消耗
        // [PlayerPoints]设置
        playerPointsMax = config.getInt("mailbox.player_points.max",500);// 单次邮件发送最大值
        playerPointsExpand = config.getInt("mailbox.player_points.expand",0);// 发送邮件时所消耗的点券
        playerPointsItem = config.getInt("mailbox.player_points.item",0);// 每多一个附件物品增加的点券消耗
    }
    
    private static List<String> formatMaterial(List<String> idList){
        List<String> material = new ArrayList();
        idList.stream().map((id) -> {
            if(id.length()>9 && id.substring(0, 10).equalsIgnoreCase("minecraft:")) id = id.substring(10);
            return id;
        }).map((id) -> {
            if(id.contains(":")) id = id.replace(":", "_");
            return id;
        }).map((id) -> id.toUpperCase()).forEach((id) -> {
            material.add(id);
        });
        return material;
    }
    
}
