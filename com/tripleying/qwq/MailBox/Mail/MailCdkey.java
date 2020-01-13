package com.tripleying.qwq.MailBox.Mail;

import org.bukkit.entity.Player;

public interface MailCdkey {
    public boolean sendData();
    public int generateCdkey(int i);
    public boolean Collect(Player p);
    public boolean isOnly();
    public void setOnly(boolean only);
    public boolean Delete(Player p);
}
