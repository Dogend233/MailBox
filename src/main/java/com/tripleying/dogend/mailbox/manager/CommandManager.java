package com.tripleying.dogend.mailbox.manager;

import com.tripleying.dogend.mailbox.api.command.BaseCommand;
import com.tripleying.dogend.mailbox.api.command.BaseTabCompleter;
import com.tripleying.dogend.mailbox.api.module.MailBoxModule;
import com.tripleying.dogend.mailbox.command.CheckCommand;
import com.tripleying.dogend.mailbox.command.HelpCommand;
import com.tripleying.dogend.mailbox.command.ReloadCommand;
import com.tripleying.dogend.mailbox.command.UpdateCommand;
import com.tripleying.dogend.mailbox.util.MessageUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * 指令管理器
 * @author Dogend
 */
public class CommandManager implements CommandExecutor {
    
    private static CommandManager manager;
    private final TabManager tab;
    private final Map<String, BaseCommand> map;
    private final Map<BaseCommand, MailBoxModule> mod_map;
    
    public CommandManager(){
        manager = this;
        this.tab = new TabManager();
        this.map = new LinkedHashMap();
        this.mod_map = new HashMap();
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if(args.length>0){
            String label = args[0];
            if(this.map.containsKey(label)){
                if(this.map.get(label).onCommand(cs, args)){
                    return true;
                }
            }
        }
        this.help(cs);
        return true;
    }
    
    /**
     * 注册基础指令
     */
    public void registerBaseCommand(){
        putCommand(new HelpCommand());
        putCommand(new CheckCommand());
        putCommand(new UpdateCommand());
        putCommand(new ReloadCommand());
    }
    
    public void help(CommandSender cs){
        MessageUtil.log(cs, MessageUtil.command_help);
        this.map.forEach((l,cmd) -> {
            String desc = cmd.getDescription(cs);
            if(desc!=null){
                MessageUtil.log(cs, MessageUtil.color("&6/mailbox ".concat(l).concat(" &b").concat(desc)));
            }
        });
    }
    
    private void putCommand(BaseCommand cmd){
        String label = cmd.getLabel();
        this.map.put(label, cmd);
        if(cmd instanceof BaseTabCompleter){
            this.tab.registerTab(label, (BaseTabCompleter)cmd);
        }
    }
    
    private void removeCommand(String label){
        this.map.remove(label);
        this.tab.unregisterTab(label);
    }
    
    /**
     * 注册指令
     * @param module 模块
     * @param cmd 指令类
     * @return boolean
     */
    public boolean registerCommand(MailBoxModule module, BaseCommand cmd){
        String label = cmd.getLabel();
        if(this.map.containsKey(label)){
            MessageUtil.log(MessageUtil.command_reg_error.replaceAll("%command%", label));
            return false;
        }else{
            putCommand(cmd);
            this.mod_map.put(cmd, module);
            MessageUtil.log(MessageUtil.command_reg.replaceAll("%command%", label));
            return true;
        }
    }
    
    /**
     * 注销指令
     * @param label 指令名
     */
    public void unregisterCommand(String label){
        if(this.map.containsKey(label)){
            this.mod_map.remove(this.map.get(label));
            removeCommand(label);
            MessageUtil.log(MessageUtil.command_unreg.replaceAll("%command%", label));
        }
    }
    
    /**
     * 注销指令
     * @param cmd 指令实例
     */
    public void unregisterCommand(BaseCommand cmd){
        if(this.map.containsValue(cmd)){
            this.mod_map.remove(cmd);
            removeCommand(cmd.getLabel());
            MessageUtil.log(MessageUtil.command_unreg.replaceAll("%command%", cmd.getLabel()));
        }
    }
    
    /**
     * 注销全部指令
     * @param module 模块
     */
    public void unregisterAllCommand(MailBoxModule module){
        if(this.mod_map.containsValue(module)){
            Set<BaseCommand> cmds = new HashSet();
            this.mod_map.entrySet().stream().filter(me -> (me.getValue()==module)).forEachOrdered(me -> {
                cmds.add(me.getKey());
            });
            cmds.forEach(cmd -> {
                this.unregisterCommand(cmd);
            });
        }
    }
    
    public TabManager getTabManager() {
        return this.tab;
    }
    
    public static CommandManager getCommandManager(){
        return manager;
    }
    
}
