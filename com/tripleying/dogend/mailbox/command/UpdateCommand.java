package com.tripleying.dogend.mailbox.command;

import com.tripleying.dogend.mailbox.api.command.BaseCommand;
import com.tripleying.dogend.mailbox.util.UpdateUtil;
import org.bukkit.command.CommandSender;

/**
 * 更新指令
 * @author Dogend
 */
public class UpdateCommand implements BaseCommand {

    @Override
    public String getLabel() {
        return "update";
    }

    @Override
    public String getDescription(CommandSender sender) {
        return sender.isOp()?"自动更新插件":null;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(sender.isOp()){
            UpdateUtil.updatePlugin(sender, true);
            return true;
        }else{
            return false;
        }
    }
    
}