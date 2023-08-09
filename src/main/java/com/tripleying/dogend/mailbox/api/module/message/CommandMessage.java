package com.tripleying.dogend.mailbox.api.module.message;

import com.tripleying.dogend.mailbox.manager.CommandManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

/**
 * 利用指令向模块内发送消息
 * @since 3.3.2
 * @author Dogend
 */
public class CommandMessage implements Message {
    
    private final String[] cmds;
    private final CommandSender cs;
    
    public CommandMessage(String[] cmds, CommandSender cs){
        this.cmds = cmds;
        this.cs = cs;
    }
    
    public CommandMessage(String[] cmds, String senderName, boolean isOp){
        this.cmds = cmds;
        this.cs = new MessageCommandSender(senderName, isOp);
    }
    
    public String[] getCommands(){
        return this.cmds;
    }
    
    public CommandSender getCommandSender(){
        return this.cs;
    }

    @Override
    public MessageReply sendMessage() {
        CommandManager.getCommandManager().onCommand(cs, null, null, cmds);
        if(cs instanceof MessageCommandSender){
            List<String> result = ((MessageCommandSender)cs).getResult();
            return new MessageReply(this, result, result.getClass());
        }else{
            return new MessageReply(this, "已执行指令", String.class);
        }
    }
    
}

/**
 * 虚拟指令发送者
 * @since 3.3.2
 * @author Dogend
 */
class MessageCommandSender implements CommandSender {
    
    private String name;
    private boolean op;
    private List<String> result;
    
    public MessageCommandSender(String name, boolean op){
        this.name = name;
        this.op = op;
        this.result = new ArrayList();
    }
    
    public List<String> getResult(){
        return this.result;
    }

    @Override
    public void sendMessage(String message) {
        result.add(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        result.addAll(Arrays.asList(messages));
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isPermissionSet(String name) {
        return true;
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return true;
    }

    @Override
    public boolean hasPermission(String name) {
        return true;
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return true;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        
    }

    @Override
    public void recalculatePermissions() {
        
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return new HashSet();
    }

    @Override
    public boolean isOp() {
        return this.op;
    }

    @Override
    public void setOp(boolean value) {
        this.op = value;
    }
    
}