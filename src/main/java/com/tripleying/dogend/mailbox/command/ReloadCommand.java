package com.tripleying.dogend.mailbox.command;

import com.tripleying.dogend.mailbox.MailBox;
import com.tripleying.dogend.mailbox.api.command.BaseCommand;
import com.tripleying.dogend.mailbox.api.command.BaseTabCompleter;
import java.util.List;
import org.bukkit.command.CommandSender;

/**
 * 重载指令
 * @author Dogend
 */
public class ReloadCommand implements BaseCommand, BaseTabCompleter {

    @Override
    public String getLabel() {
        return "reload";
    }

    @Override
    public String getDescription(CommandSender sender) {
        return sender.isOp()?"重载插件":null;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(sender.isOp()){
            MailBox.getMailBox().reload(sender);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean allowTab(CommandSender sender) {
        return sender.isOp();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
    
}
