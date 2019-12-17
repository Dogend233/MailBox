package com.嘤嘤嘤.qwq.MailBox.Original;

import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;

public class OriginalConfig {
    public static String stopStr = ".";
    public static String msgCancel = "§b[邮件预览]: 输入"+stopStr+"取消发送";
    public static String msgStop = "§b[邮件预览]: 对话已结束";
    public static String msgTopic = "§b[邮件预览]: 请输入邮件标题";
    public static int maxTopic = 30;
    public static String msgContent = "§b[邮件预览]: 请输入邮件内容";
    public static int maxContent = 255;
    public static String msgSender = "§b[邮件预览]: 请输入发件人";
    public static String msgRecipient = "§b[邮件预览]: 请输入收件人";
    public static String msgPermission = "§b[邮件预览]: 请输入领取邮件所需的权限";
    public static String msgFile = "§b[邮件预览]: 是否发送附件？（Y：是，N：否）";
    public static String msgCoin = "§b[邮件预览]: 请输入想发送的"+GlobalConfig.vaultDisplay+"§b数量";
    public static String msgPoint = "§b[邮件预览]: 请输入想发送的"+GlobalConfig.playerPointsDisplay+"§b数量";
    public static String msgItemPlayer = "§b[邮件预览]: 请输入想发送的物品快捷栏位置（1~9），多物品用空格隔开，最多可发送物品格子数量： ";
    public static String msgItemConsole = "§b[邮件预览]: 请输入想发送的物品文件名（不加.yml），多物品用空格隔开，最多可发送物品格子数量： ";
    public static String msgItemCancel = "§b[邮件预览]: 输入 0 不发送物品";
    public static String msgCommand = "§b[邮件预览]: 请输入想发送邮件后执行指令，以/分割（第一条不加/），玩家用 "+GlobalConfig.fileCmdPlayer+" 表示";
    public static String msgCommandCancel = "§b[邮件预览]: 输入 0 不发送指令";
    public static String msgCommandDescription = "§b[邮件预览]: 请输入执行指令的提示，多行以空格分割";
    public static String msgCommandDescriptionCancel = "§b[邮件预览]: 输入 0 不显示提示";
    public static String msgPreview = "§b[邮件预览]: 输入 0 确认发送";   
}
