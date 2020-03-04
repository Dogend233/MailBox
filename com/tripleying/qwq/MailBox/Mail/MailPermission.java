package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.OuterMessage;
import org.bukkit.entity.Player;

/**
 * permission邮件
 */
public interface MailPermission {
    
    /**
     * 设置领取权限
     * @param permission 领取权限
     */
    public void setPermission(String permission);

    /**
     * 获取领取权限
     * @return 领取权限
     */
    public String getPermission();
    
    default boolean collectValidate(Player p) {
        if(!p.hasPermission(getPermission())){
            p.sendMessage(OuterMessage.permissionNoPermission);
            return false;
        }
        return true;
    }
    
}
