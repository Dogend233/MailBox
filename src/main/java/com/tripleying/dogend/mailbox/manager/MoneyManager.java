package com.tripleying.dogend.mailbox.manager;

import com.tripleying.dogend.mailbox.api.mail.attach.AttachDoubleMoney;
import com.tripleying.dogend.mailbox.api.mail.attach.AttachIntegerMoney;
import com.tripleying.dogend.mailbox.api.mail.attach.AttachMoney;
import com.tripleying.dogend.mailbox.api.module.MailBoxModule;
import com.tripleying.dogend.mailbox.api.money.DoubleMoney;
import com.tripleying.dogend.mailbox.api.money.IntegerMoney;
import com.tripleying.dogend.mailbox.api.money.BaseMoney;
import com.tripleying.dogend.mailbox.util.MessageUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.entity.Player;

/**
 * 金钱管理器
 * @author Dogend
 */
public class MoneyManager {
    
    private static MoneyManager manager;
    private final Map<String, BaseMoney> map;
    private final Map<BaseMoney, MailBoxModule> mod_map;
    
    public MoneyManager(){
        manager = this;
        this.map = new HashMap();
        this.mod_map = new HashMap();
    }
    
    /**
     * 给予玩家金钱
     * @param p 玩家
     * @param name 金钱名
     * @param count 金钱数量
     * @return boolean
     */
    public boolean addBalance(Player p, String name, Object count){
        if(this.map.containsKey(name)){
            return this.map.get(name).givePlayerBalance(p, count);
        }
        return false;
    }
    
    /**
     * 移除玩家金钱
     * @param p 玩家
     * @param name 金钱名
     * @param count 金钱数量
     * @return boolean
     */
    public boolean removeBalance(Player p, String name, Object count){
        if(this.map.containsKey(name)){
            return this.map.get(name).removePlayerBalance(p, count);
        }
        return false;
    }
    
    /**
     * 获取玩家余额
     * @param p 玩家
     * @param name 金钱名
     * @return boolean
     */
    public Object getBalance(Player p, String name){
        if(this.map.containsKey(name)){
            return this.map.get(name).getPlayerBalance(p);
        }
        return 0;
    }
    
    /**
     * 判断玩家是否有足够的余额
     * @param p 玩家
     * @param name 金钱名
     * @param count 金钱数量
     * @return boolean
     */
    public boolean hasBalance(Player p, String name, Object count){
        if(this.map.containsKey(name)){
            return this.map.get(name).hasPlayerBalance(p, count);
        }
        return false;
    }
    
    /**
     * 获取金钱展示名称
     * @param name 金钱名
     * @return String
     */
    public String getMoneyDisplay(String name){
        return this.map.containsKey(name)?this.map.get(name).getDisplay():"无";
    }
    
    /**
     * 获取金钱列表
     * @return List
     */
    public List<BaseMoney> getMoneyList(){
        List<BaseMoney> list = new ArrayList();
        list.addAll(this.map.values());
        return list;
    }
    
    /**
     * 获取一个金钱附件
     * @param name 金钱名
     * @return AttachMoney
     */
    public AttachMoney getAttachMoney(String name){
        if(this.map.containsKey(name)){
            BaseMoney money = this.map.get(name);
            if(money instanceof IntegerMoney) return new AttachIntegerMoney();
            if(money instanceof DoubleMoney) return new AttachDoubleMoney();
        }
        return null;
    }
    
    /**
     * 获取一个金钱附件
     * @param name 金钱名
     * @param o 数量
     * @return AttachMoney
     */
    public AttachMoney getAttachMoney(String name, Object o){
        if(this.map.containsKey(name)){
            BaseMoney money = this.map.get(name);
            if(money instanceof IntegerMoney && o instanceof Integer) return new AttachIntegerMoney((int)o);
            if(money instanceof DoubleMoney && o instanceof Double) return new AttachDoubleMoney((double)o);
        }
        return null;
    }
    
    /**
     * 注册金钱
     * @param module 模块
     * @param money 金钱类
     */
    public void registerMoney(MailBoxModule module, BaseMoney money){
        String name = money.getName();
        if(money instanceof IntegerMoney || money instanceof DoubleMoney){
            if(this.map.containsKey(name)){
                MessageUtil.log(MessageUtil.money_reg_duplicate.replaceAll("%money%", name));
            }else{
                this.map.put(name, money);
                this.mod_map.put(money, module);
                MessageUtil.log(MessageUtil.money_reg.replaceAll("%money%", name));
            }
        }else{
            MessageUtil.log(MessageUtil.money_reg_invalid.replaceAll("%money%", name));
        }
    }
    
    /**
     * 注销金钱
     * @param name 金钱名
     */
    public void unregisterMoney(String name){
        if(this.map.containsKey(name)){
            this.mod_map.remove(this.map.get(name));
            this.map.remove(name);
            MessageUtil.log(MessageUtil.money_unreg.replaceAll("%money%", name));
        }
    }
    
    /**
     * 注销金钱
     * @param money 金钱实例
     */
    public void unregisterMoney(BaseMoney money){
        if(this.map.containsValue(money)){
            this.mod_map.remove(money);
            this.map.remove(money.getName());
            MessageUtil.log(MessageUtil.money_unreg.replaceAll("%money%", money.getName()));
        }
    }
    
    /**
     * 注销全部金钱
     * @param module 模块
     */
    public void unregisterAllMoney(MailBoxModule module){
        if(this.mod_map.containsValue(module)){
            Set<BaseMoney> moneys = new HashSet();
            this.mod_map.entrySet().stream().filter(me -> (me.getValue()==module)).forEachOrdered(me -> {
                moneys.add(me.getKey());
            });
            moneys.forEach(money -> {
                this.unregisterMoney(money);
            });
        }
    }
    
    public static MoneyManager getMoneyManager(){
        return manager;
    }
    
}