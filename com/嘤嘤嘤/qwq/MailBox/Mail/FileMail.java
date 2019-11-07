package com.嘤嘤嘤.qwq.MailBox.Mail;

import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailCollectEvent;
import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailSendEvent;
import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.Utils.DateTime;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FileMail extends TextMail implements ItemMail, CommandMail{
    
    // 附件名
    public String fileName;
    // 附件是否启用指令
    public boolean hasCommand;
    // 指令列表
    public List<String> commandList;
    // 指令描述
    public List<String> commandDescription;
    // 附件是否含有物品
    public boolean hasItem;
    // 物品列表
    public ArrayList<ItemStack> itemList;
    
    public FileMail(String type, int id, String sender, String topic, String content, String date, String filename){
        super(type, id, sender, topic, content, date);
        this.fileName = filename;
        getFile();
    }
    
    public FileMail(String type, int id, String sender, String topic, String content, String date, String filename, ArrayList<ItemStack> isl, List<String> cl, List<String> cd){
        super(type, id, sender, topic, content, date);
        this.fileName = filename;
        this.itemList = isl;
        this.commandList = cl;
        this.commandDescription = cd;
        hasItem = !isl.isEmpty();
        hasCommand = cl != null;
    }
    
    // 获取附件信息
    private void getFile(){
        fileHasCommand();
        if(hasCommand){
            getCommandList();
            getCommandDescription();
        }
        getItemList();
    }
    
    // 设置玩家领取邮件
    @Override
    public boolean Collect(Player p){
        // 发送邮件附件
        if(hasItem){
            if(giveItem(p)){
                p.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"附件发送完毕");
            }else{
                Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"玩家："+p.getName()+" 领取 "+typeName+" - "+id+" 邮件附件失败.");
                return false;
            }
        }
        // 执行邮件指令
        if(hasCommand){
            if(doCommand(p)){
                p.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"指令执行完毕");
            }else{
                Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"玩家："+p.getName()+" 执行 "+typeName+" - "+id+" 邮件指令失败.");
            }
        }
        // 设置玩家领取邮件
        if(MailBoxAPI.setCollect(type, id, p.getName())){
            MailCollectEvent mce = new MailCollectEvent(this, p);
            Bukkit.getServer().getPluginManager().callEvent(mce);
            p.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"邮件领取成功！");
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"玩家："+p.getName()+" 领取了 "+typeName+" - "+id+" 邮件.");
            return true;
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件领取失败！");
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"玩家："+p.getName()+" 领取 "+typeName+" - "+id+" 邮件失败.");
            return false;
        }
    }
    
    // 发送这封邮件
    @Override
    public boolean Send(Player p){
        if(id==0){
            // 新建邮件
            // 获取时间
            date = DateTime.get("ymdhms");
            try {
                // 生成一个文件名
                fileName = MailBoxAPI.getMD5(type);
            } catch (IOException ex) {
                p.sendMessage(GlobalConfig.normal+"[邮件预览]：生成文件名失败");
                return false;
            }
            if(MailBoxAPI.saveMailFiles(this)){
                if(MailBoxAPI.setSend(type, id, sender, topic, content, date, fileName)){
                    MailSendEvent mse = new MailSendEvent(this, p);
                    Bukkit.getServer().getPluginManager().callEvent(mse);
                    return true;
                }else{
                    p.sendMessage(GlobalConfig.normal+"[邮件预览]：邮件发送至数据库失败");
                    return false;
                }
            }else{
                p.sendMessage(GlobalConfig.normal+"[邮件预览]：保存为附件失败");
                if(p.isOp())p.sendMessage(GlobalConfig.normal+"[邮件预览]：附件名:"+fileName);
                return false;
            }
            
        }else{
            // 修改已有邮件
            return false;
        }
    }
    
    // 删除这封邮件
    @Override
    public boolean Delete(Player p){
        if(DeleteFile()){
            return DeleteData(p);
        }else{
            return false;
        }
    }
    
    // 删除这封邮件的附件
    public boolean DeleteFile(){
        return MailBoxAPI.setDeleteFile(type,fileName);
    }

    // 判断附件是否启用指令
    @Override
    public void fileHasCommand() {
        hasCommand = MailBoxAPI.hasFileCommands(type, fileName);
        if(hasCommand) ;
    }

    // 获取指令列表
    @Override
    public void getCommandList() {
        commandList = MailBoxAPI.getFileCommands(type, fileName);
    }
    
    // 获取指令描述
    @Override
    public void getCommandDescription() {
        commandDescription = MailBoxAPI.getFileCommandsDescription(type, fileName);
    }

    // 执行指令
    @Override
    public boolean doCommand(Player p) {
        if(commandList!=null){
            for(int i=0;i<commandList.size();i++){
                String cs = commandList.get(i);
                try{
                    cs = cs.replace(GlobalConfig.fileCmdPlayer, p.getName());
                    if(Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cs)){
                        p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+" 第"+(i+1)+"条指令执行成功");
                    }else{
                        p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+" 第"+(i+1)+"条指令执行失败");
                    }
                } catch (CommandException e) {
                    p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+" 第"+(i+1)+"条指令执行失败");
                }
            }
            return true;
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+" 获取指令信息失败");
            return false;
        }
    }

    @Override
    public void getItemList() {
        itemList = MailBoxAPI.getFileItems(type, fileName);
        hasItem = !(itemList==null || itemList.isEmpty());
    }

    @Override
    public boolean giveItem(Player p) {
        // 检查背包空位够不够
        int x = 0;
        boolean hasBlank = false;
        for(int i = 0;i<36;i++){
            if(p.getInventory().getItem(i)==null)x++;
            if(x>=itemList.size()){
                hasBlank = true;
                break;
            }
        }
        if(hasBlank){
            ItemStack[] isa = {new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR)};
            for(int i = 0 ;i<itemList.size();i++){
                ItemStack is = itemList.get(i);
                isa[i] = is;
            }
            p.getInventory().addItem(isa);
            return true;
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+" 领取失败，请在背包中留出"+itemList.size()+"个空位！");
            return false;
        }
    }
    
}
