package com.tripleying.dogend.mailbox.command;

import com.tripleying.dogend.mailbox.api.command.BaseCommand;
import com.tripleying.dogend.mailbox.api.command.BaseTabCompleter;
import java.util.List;
import org.bukkit.command.CommandSender;

/**
 * 帮助指令
 * @author Dogend
 */
public class HelpCommand implements BaseCommand, BaseTabCompleter {

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

    @Override
    public boolean allowTab(CommandSender sender) {
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
    
}
