package com.tripleying.qwq.MailBox.Mail;

import org.bukkit.entity.Player;

public interface MailPermission {
    public boolean sendData();
    public boolean collectValidate(Player p);
    public void setPermission(String permission);
    public String getPermission();
}
