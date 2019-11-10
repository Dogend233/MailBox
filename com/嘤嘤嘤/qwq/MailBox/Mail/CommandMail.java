package com.嘤嘤嘤.qwq.MailBox.Mail;

import org.bukkit.entity.Player;

interface CommandMail {
    public void fileHasCommand();
    public void getFileCommandList();
    public void getFileCommandDescription();
    public boolean doCommand(Player p);
}
