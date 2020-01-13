package com.tripleying.qwq.MailBox.Mail;

import java.util.List;
import org.bukkit.entity.Player;

public interface MailPlayer {
    public boolean ExpireValidate();
    public boolean collectValidate(Player p);
    public boolean sendValidate(Player p);
    public boolean sendData();
    public void setRecipient(List<String> recipient);
    public List<String> getRecipient();
    public String getRecipientString();
}
