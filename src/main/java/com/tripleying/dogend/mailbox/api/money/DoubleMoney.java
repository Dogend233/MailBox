package com.tripleying.dogend.mailbox.api.money;

import org.bukkit.entity.Player;

/**
 * 小数型钱父类
 * @author Dogend
 */
public abstract class DoubleMoney extends BaseMoney {
    
    public DoubleMoney(String name, String display) {
        super(name, display);
    }
    
    @Override
    public final boolean givePlayerBalance(Player p, Object i) {
        if(i instanceof Double && (double)i>=0.0){
            return givePlayerBalance(p, (double)i);
        }else{
            return false;
        }
    }
    
    protected abstract boolean givePlayerBalance(Player p, double i);

    @Override
    public final boolean removePlayerBalance(Player p, Object i) {
        if(i instanceof Double && (double)i>=0.0 && hasPlayerBalance(p,(double)i)){
            return removePlayerBalance(p, (double)i);
        }else{
            return false;
        }
    }
    
    protected abstract boolean removePlayerBalance(Player p, double i);

    @Override
    public final boolean hasPlayerBalance(Player p, Object i) {
        if(i instanceof Double && (double)i>=0.0){
            return hasPlayerBalance(p, (double)i);
        }else{
            return false;
        }
    }
    
    protected abstract boolean hasPlayerBalance(Player p, double i);
    
}
