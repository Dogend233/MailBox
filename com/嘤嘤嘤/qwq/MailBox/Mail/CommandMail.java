package com.嘤嘤嘤.qwq.MailBox.Mail;

import java.util.List;
import org.bukkit.entity.Player;

interface CommandMail {
    public void getFileHasCommand();
    public void getFileCommandList();
    public void getFileCommandDescription();
    public boolean doCommand(Player p);
    public void setCommandList(List<String> commandList);
    public boolean isHasCommand();
    public List<String> getCommandList();
    public void setCommandDescription(List<String> commandDescription);
    public List<String> getCommandDescription();
}
