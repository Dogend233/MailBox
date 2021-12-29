package com.tripleying.dogend.mailbox.api.money;

import org.bukkit.entity.Player;

/**
 * 整数钱父类
 * @author Dogend
 */
public abstract class IntegerMoney extends BaseMoney {

    public IntegerMoney(String name, String display) {
        super(name, display);
    }

    @Override
    public final boolean givePlayerBalance(Player p, Object i) {
        if(i instanceof Integer && (int)i>=0){
            return givePlayerBalance(p, (int)i);
        }else{
            return false;
        }
    }
    
    protected abstract boolean givePlayerBalance(Player p, int i);

    @Override
    public final boolean removePlayerBalance(Player p, Object i) {
        if(i instanceof Integer && (int)i>=0 && hasPlayerBalance(p,(int)i)){
            return removePlayerBalance(p, (int)i);
        }else{
            return false;
        }
    }
    
    protected abstract boolean removePlayerBalance(Player p, int i);

    @Override
    public final boolean hasPlayerBalance(Player p, Object i) {
        if(i instanceof Integer && (int)i>=0){
            return hasPlayerBalance(p, (int)i);
        }else{
            return false;
        }
    }
    
    protected abstract boolean hasPlayerBalance(Player p, int i);
    
}
