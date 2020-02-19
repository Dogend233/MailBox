package com.tripleying.qwq.MailBox.Utils;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

/**
 * Vault工具
 * @author Dogend
 */
public class VaultUtil {
    
    private static Economy economy = null;
    
    // 设置实例
    public static boolean setEconomy(Economy eco){
        economy = eco;
        return economy != null;
    }
    
    // 获取玩家余额
    public static double getEconomyBalance(Player p){
        return economy.getBalance(p);
    }
    
    // 给玩家钱
    public static boolean addEconomy(Player p, double coin){
        EconomyResponse r = economy.depositPlayer(p, coin);
        return r.transactionSuccess();
    }
    
    // 扣玩家钱
    public static boolean reduceEconomy(Player p, double coin){
        EconomyResponse r = economy.withdrawPlayer(p, coin);
        return r.transactionSuccess();
    }
    
}
