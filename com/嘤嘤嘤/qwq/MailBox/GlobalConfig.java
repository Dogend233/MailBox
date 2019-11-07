package com.嘤嘤嘤.qwq.MailBox;

public class GlobalConfig {
    
    public static String pluginPrefix;
    public static String normal;
    public static String success;
    public static String warning;
    public static String mailPrefix_ALL;
    public static String fileDiv;
    public static String fileCmdPlayer;
    
    public static void setGlobalConfig(
        String pluginPrefix,
        String normal,
        String success,
        String warning,
        String mailPrefix_ALL,
        String fileDiv,
        String fileCmdPlayer
    ){
        // 全局
        GlobalConfig.pluginPrefix = pluginPrefix;// 插件提示信息前缀
        GlobalConfig.normal = normal;// 普通 插件信息颜色
        GlobalConfig.success = success;// 成功 插件信息颜色
        GlobalConfig.warning = warning;// 失败 插件信息颜色
        GlobalConfig.mailPrefix_ALL = mailPrefix_ALL;// [ALL]邮件名称
        // 附件
        GlobalConfig.fileDiv = fileDiv;// 分割符
        GlobalConfig.fileCmdPlayer = fileCmdPlayer;// 领取邮件的玩家变量
    }
    
}
