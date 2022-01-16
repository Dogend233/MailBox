package com.tripleying.dogend.mailbox.api.command;

import org.bukkit.command.CommandSender;

/**
 * 基础指令
 * @author Dogend
 */
public interface BaseCommand {
    
    /**
     * 获取指令标签 (mailbox后的第一个参数)
     * @return label
     */
    public String getLabel();
    
    /**
     * 获取指令介绍
     * @param sender 指令发送者
     * @return String
     */
    public String getDescription(CommandSender sender);
    
    /**
     * 指令主体
     * 返回false则触发help指令
     * @param sender 指令发送者
     * @param args 指令参数(去掉了mailbox)
     * @return boolean
     */
    public boolean onCommand(CommandSender sender, String[] args);
    
}
