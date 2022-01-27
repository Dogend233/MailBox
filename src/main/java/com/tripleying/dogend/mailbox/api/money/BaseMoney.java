package com.tripleying.dogend.mailbox.api.money;

import org.bukkit.entity.Player;

/**
 * 基础金钱父类
 * 不要继承此类进行附属开发
 * @author Dogend
 */
public abstract class BaseMoney {
    
    /**
     * 金钱名
     */
    protected final String name;
    /**
     * 显示名称
     */
    protected final String display;
    
    public BaseMoney(String name, String display){
        this.name = name;
        this.display = display;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getDisplay(){
        return this.display;
    }
    
    /**
     * 获取玩家余额
     * @param p 玩家
     * @return 余额
     */
    public abstract Object getPlayerBalance(Player p);
    
    /**
     * 给予玩家金钱
     * @param p 玩家
     * @param i 金钱
     * @return boolean
     */
    public abstract boolean givePlayerBalance(Player p, Object i);
    
    /**
     * 移除玩家金钱
     * @param p 玩家
     * @param i 金钱
     * @return boolean
     */
    public abstract boolean removePlayerBalance(Player p, Object i);
    
    /**
     * 玩家是否有足够余额
     * @param p 玩家
     * @param i 金钱
     * @return boolean
     */
    public abstract boolean hasPlayerBalance(Player p, Object i);
    
}
