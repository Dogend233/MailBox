package com.嘤嘤嘤.qwq.MailBox;

public class GlobalConfig {
    
    private static String mailDisplay_SYSTEM;
    private static String mailDisplay_PLAYER;
    public static String pluginPrefix;
    public static String normal;
    public static String success;
    public static String warning;
    public static String fileDiv;
    public static String fileCmdPlayer;
    public static String expiredDay;
    
    public static void setGlobalConfig(
        String pluginPrefix,
        String normal,
        String success,
        String warning,
        String mailDisplay_SYSTEM,
        String mailDisplay_PLAYER,
        String fileDiv,
        String fileCmdPlayer,
        String expiredDay
    ){
        // 全局
        GlobalConfig.pluginPrefix = pluginPrefix;// 插件提示信息前缀
        GlobalConfig.normal = normal;// 普通 插件信息颜色
        GlobalConfig.success = success;// 成功 插件信息颜色
        GlobalConfig.warning = warning;// 失败 插件信息颜色
        GlobalConfig.mailDisplay_SYSTEM = mailDisplay_SYSTEM;// system 邮件显示名称
        GlobalConfig.mailDisplay_PLAYER = mailDisplay_PLAYER;// player 邮件显示名称
        // 附件
        GlobalConfig.fileDiv = fileDiv;// 分割符
        GlobalConfig.fileCmdPlayer = fileCmdPlayer;// 领取邮件的玩家变量
        // player邮件过期时间
        GlobalConfig.expiredDay = expiredDay;
    }
    
    public static String getTypeName(String type) {
        switch (type) {
            case "system":
                return mailDisplay_SYSTEM;
            case "player": 
                return mailDisplay_PLAYER;
            default:
                return null;
        }
    }
}
