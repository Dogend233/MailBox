package com.嘤嘤嘤.qwq.MailBox.Mail;

import org.bukkit.entity.Player;

interface CommandMail {
    public void fileHasCommand();
    public void getCommandList();
    public void getCommandDescription();
    public boolean doCommand(Player p);
}
