package com.tripleying.qwq.MailBox.Utils;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.entity.Player;

/**
 * PlayerPoints工具
 * @author Dogend
 */
public class PlayerPointsUtil {
    
    private static PlayerPoints points = null;
    
    // 设置实例
    public static boolean setPoints(PlayerPoints p){
        points = p;
        return points != null;
    }

    // 获取玩家余额
    public static int getPoints(Player p){
        return points.getAPI().look(p.getUniqueId());
    }
    
    // 给玩家钱
    public static boolean addPoints(Player p, int point){
        return points.getAPI().give(p.getUniqueId(), point);
    }
    
    // 扣玩家钱
    public static boolean reducePoints(Player p, int point){
        return points.getAPI().take(p.getUniqueId(), point);
    }
    
}
