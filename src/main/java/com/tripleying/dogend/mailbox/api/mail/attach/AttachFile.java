package com.tripleying.dogend.mailbox.api.mail.attach;

import com.tripleying.dogend.mailbox.MailBox;
import com.tripleying.dogend.mailbox.manager.MoneyManager;
import com.tripleying.dogend.mailbox.util.ItemStackUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 邮件附件
 * @author Dogend
 */
public class AttachFile implements ConfigurationSerializable {
    
    /**
     * 金钱附件
     */
    private final Map<String, AttachMoney> moneys;
    /**
     * 物品附件
     */
    private final List<ItemStack> items;
    /**
     * 指令附件
     */
    private AttachCommand cmds; 
    
    /**
     * 创建一个新的附件
     */
    public AttachFile(){
        this.moneys = new HashMap();
        this.items = new ArrayList();
        this.cmds = new AttachCommand();
    }
    
    /**
     * 添加金钱附件
     * @param name 金钱
     * @param count 数量
     * @return boolean
     */
    public boolean addMoney(String name, Object count){
        AttachMoney am;
        if(this.moneys.containsKey(name)){
            am = this.moneys.get(name);
        }else{
            am = MailBox.getMailBox().getMoneyManager().getAttachMoney(name);
            this.moneys.put(name, am);
        }
        return am.addCount(count);
    }
    
    /**
     * 减少金钱附件
     * @param name 金钱
     * @param count 数量
     * @return boolean
     */
    public boolean removeMoney(String name, Object count){
        if(this.moneys.containsKey(name)){
            AttachMoney am = this.moneys.get(name);
            boolean bl = am.removeCount(count);
            if(am.isZero()){
                this.moneys.remove(name);
            }
            return bl;
        }
        return false;
    }
    
    /**
     * 移除金钱附件
     * @param name 金钱
     */
    public void removeMoney(String name){
        if(this.moneys.containsKey(name)) this.moneys.remove(name);
    }
    
    /**
     * 移除全部金钱附件
     */
    public void removeAllMoney(){
        this.moneys.clear();
    }
    
    /**
     * 添加物品
     * @param iss ItemStack
     */
    public void addItemStack(ItemStack... iss){
        for(ItemStack is:iss){
            if(is!=null) this.items.add(is);
        }
    }
    
    /**
     * 移除所有物品
     */
    public void removeAllItemStack(){
        this.items.clear();
    }
    
    /**
     * 是否存在任何附件
     * @return boolean
     */
    public boolean hasAttach(){
        return !this.moneys.isEmpty() || !this.items.isEmpty() || this.cmds.hasCommand();
    }

    /**
     * 获取指令
     * @return AttachCommand
     */
    public AttachCommand getCommands() {
        return this.cmds;
    }
    
    /**
     * 获取金钱
     * @return Map
     */
    public Map<String, AttachMoney> getMoneys() {
        return this.moneys;
    }
    
    /**
     * 获取物品
     * @return List
     */
    public List<ItemStack> getItemStacks() {
        return this.items;
    }
    
    /**
     * 判断玩家背包是否有足够位置放下物品堆
     * @param p 玩家
     * @return boolean
     */
    public boolean checkInventory(Player p){
        return this.items.isEmpty()?true:ItemStackUtil.hasBlank(p, this.items)==0;
    }
    
    /**
     * 使玩家领取附件
     * @param p 玩家
     */
    public void receivedAttach(Player p){
        if(!this.hasAttach()) return;
        this.receivedMoney(p);
        this.receivedItem(p);
        this.receivedCommand(p);
    }
    
    /**
     * 使玩家领取金钱
     * @param p 玩家
     */
    public void receivedMoney(Player p){
        this.moneys.forEach((n,m) -> {
            if(!m.isZero()){
                MoneyManager.getMoneyManager().addBalance(p, n, m.getCount());
            }
        });
    }
    
    /**
     * 使玩家领取物品
     * @param p 玩家
     */
    public void receivedItem(Player p){
        if(!this.items.isEmpty()){
            ItemStack[] iss = new ItemStack[this.items.size()];
            int i=0;
            for(ItemStack is:this.items){
                iss[i++] = is;
            }
            p.getInventory().addItem(iss);
        }
    }
    
    /**
     * 使玩家领取指令
     * @param p 玩家
     */
    public void receivedCommand(Player p){
        this.cmds.doCommand(p);
    }
    
    /**
     * 配置文件序列化
     * @return Map
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> attach = new LinkedHashMap();
        if(!this.moneys.isEmpty()){
            Map<String, Object> mmap = new HashMap();
            this.moneys.forEach((k,v) -> {
                if(!v.isZero()) mmap.put(k, v.getCount());
            });
            attach.put("money", mmap);
        }
        if(!this.items.isEmpty()){
            Map<String, Object> item = new HashMap();
            int i = 1;
            for(ItemStack is:this.items){
                item.put(Integer.toString(i++), is);
            }
            attach.put("item", item);
        }
        if(this.cmds.hasCommand()){
            attach.put("command", this.cmds);
        }
        return attach;
    }
    
    /**
     * 配置文件反序列化
     * @param map Map
     * @return AttachFile
     */
    public static AttachFile deserialize(Map<String, Object> map){
        AttachFile attach = new AttachFile();
        if(map.containsKey("money")){
            Map<String, Object> money = (Map<String, Object>)map.get("money");
            money.forEach((k,v) -> attach.addMoney(k, v));
        }
        if(map.containsKey("item")){
            Map<String, Object> item = (Map<String, Object>)map.get("item");
            item.forEach((k,v) -> attach.addItemStack((ItemStack)v));
        }
        if(map.containsKey("command")){
            attach.cmds = (AttachCommand)map.get("command");
        }
        return attach;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("attach: ");
        sb.append('\n').append("- money: ");
        this.moneys.forEach((k,v) -> sb.append('\n').append("-- ").append(k).append(": ").append(v.getCount()));
        sb.append('\n').append("- item: ");
        this.items.forEach(i -> sb.append('\n').append("-- ").append(i.getType().name()).append(": ").append(i.getAmount()));
        sb.append('\n').append("- command: ");
        this.cmds.getCommand().forEach((t,l) -> {
            sb.append('\n').append("-- ").append(t).append(": ");
            l.forEach(c -> sb.append('\n').append("--- ").append(c));
        });
        return sb.toString();
    }
    
}
