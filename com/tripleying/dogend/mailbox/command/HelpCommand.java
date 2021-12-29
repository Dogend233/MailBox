package com.tripleying.dogend.mailbox.command;

import com.tripleying.dogend.mailbox.api.command.BaseCommand;
import org.bukkit.command.CommandSender;

/**
 * 帮助指令
 * @author Dogend
 */
public class HelpCommand implements BaseCommand {

    @Override
    public String getLabel() {
        return "help";
    }

    @Override
    public String getDescription(CommandSender sender) {
        return "插件帮助";
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        return false;
    }
    
}
