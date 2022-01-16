package com.tripleying.dogend.mailbox.command;

import com.tripleying.dogend.mailbox.api.command.BaseCommand;
import com.tripleying.dogend.mailbox.api.util.CommonConfig;
import com.tripleying.dogend.mailbox.util.UpdateUtil;
import org.bukkit.command.CommandSender;

/**
 * 检查更新指令
 * @author Dogend
 */
public class CheckCommand implements BaseCommand {

    @Override
    public String getLabel() {
        return "check";
    }

    @Override
    public String getDescription(CommandSender sender) {
        return sender.isOp()?"检查更新":null;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(sender.isOp()){
            UpdateUtil.updatePlugin(sender, CommonConfig.auto_update);
            return true;
        }else{
            return false;
        }
    }
    
}
