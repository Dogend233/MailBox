package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Mail.BaseFileMail;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.entity.Player;

/**
 * PlayerPoints工具
 */
public class PlayerPointsUtil {
    
    /**
     * PlayerPoints实例
     */
    private static PlayerPoints points = null;

    /**
     * 设置PlayerPoints实例
     * @param p PlayerPoints实例
     * @return boolean
     */
    public static boolean setPoints(PlayerPoints p){
        points = p;
        return points != null;
    }

    /**
     * 获取玩家余额
     * @param p 玩家
     * @return 余额(int)
     */
    public static int getPoints(Player p){
        return points.getAPI().look(p.getUniqueId());
    }
    
    /**
     * 增加玩家余额
     * @param p 玩家
     * @param point 数量
     * @return boolean
     */
    public static boolean addPoints(Player p, int point){
        return points.getAPI().give(p.getUniqueId(), point);
    }
    
    /**
     * 减少玩家余额
     * @param p 玩家
     * @param point 数量
     * @return boolean
     */
    public static boolean reducePoints(Player p, int point){
        return points.getAPI().take(p.getUniqueId(), point);
    }
    
    /**
     * 获取发送附件邮件消耗的指定倍数的金钱
     * @param fm 附件邮件
     * @param multiple 倍数
     * @return 金钱(int)
     */
    public static int getFileMailExpandPoints(BaseFileMail fm, int multiple){
        if(GlobalConfig.enPlayerPoints && (fm.getPoint()!=0 || GlobalConfig.playerPointsExpand!=0 || (fm.isHasItem() && GlobalConfig.playerPointsItem!=0))){
            return fm.getPoint()*multiple+GlobalConfig.playerPointsExpand+fm.getItemList().size()*GlobalConfig.playerPointsItem;
        }else{
            return 0;
        }
    }
    
}
