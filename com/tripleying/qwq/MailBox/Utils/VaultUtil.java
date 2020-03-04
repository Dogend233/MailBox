package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Mail.BaseFileMail;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

/**
 * Vault工具
 */
public class VaultUtil {
    
    /**
     * Economy实例
     */
    private static Economy economy = null;

    /**
     * 设置Economy实例
     * @param eco Economy
     * @return boolean
     */
    public static boolean setEconomy(Economy eco){
        economy = eco;
        return economy != null;
    }

    /**
     * 获取玩家余额
     * @param p 玩家
     * @return 余额(double)
     */
    public static double getEconomyBalance(Player p){
        return economy.getBalance(p);
    }

    /**
     * 增加玩家余额
     * @param p 玩家
     * @param coin 数量
     * @return boolean
     */
    public static boolean addEconomy(Player p, double coin){
        EconomyResponse r = economy.depositPlayer(p, coin);
        return r.transactionSuccess();
    }

    /**
     * 减少玩家余额
     * @param p 玩家
     * @param coin 数量
     * @return boolean
     */
    public static boolean reduceEconomy(Player p, double coin){
        EconomyResponse r = economy.withdrawPlayer(p, coin);
        return r.transactionSuccess();
    }
    
    /**
     * 获取发送附件邮件消耗的指定倍数的金钱
     * @param fm 附件邮件
     * @param multiple 倍数
     * @return 金钱(double)
     */
    public static double getFileMailExpandCoin(BaseFileMail fm, int multiple){
        if(GlobalConfig.enVault && (fm.getCoin()!=0 || GlobalConfig.vaultExpand!=0 || (fm.isHasItem() && GlobalConfig.vaultItem!=0))){
            return fm.getCoin()*multiple+GlobalConfig.vaultExpand+fm.getItemList().size()*GlobalConfig.vaultItem;
        }else{
            return 0;
        }
    }
    
}
