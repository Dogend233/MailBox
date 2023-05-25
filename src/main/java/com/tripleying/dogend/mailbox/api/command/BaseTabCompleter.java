package com.tripleying.dogend.mailbox.api.command;

import java.util.List;
import org.bukkit.command.CommandSender;

/**
 * 基础指令补全器
 * @author Administrator
 */
public interface BaseTabCompleter {
    
    /**
     * 指令发送者是否被允许补全该指令
     * @param sender 指令发送者
     * @return boolean
     */
    public boolean allowTab(CommandSender sender);
    
    /**
     * 指令补全器主体
     * @param sender 指令发送者
     * @param args 指令参数(去掉了mailbox)
     * @return List
     */
    public List<String> onTabComplete(CommandSender sender, String[] args);
    
}
