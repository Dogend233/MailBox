package com.tripleying.qwq.MailBox.Utils;

import java.util.List;
import java.util.Random;

/**
 * 随机工具
 */
public class RandomUtil {

    /**
     * 输入成功率返回是否成功
     * @param chance 成功率
     * @return boolean
     */
    public static boolean RandomBoolean(double chance){
        if(chance<=0) return false;
        if(chance>=100) return true;
        return Math.random()<=(chance/100);
    }
    
    /**
     * 随机取一个start-end的整数
     * @param start 开始值
     * @param end 结束值
     * @return 整数
     */
    public static int RandomInt(int start, int end){
        return new Random().nextInt(end-start+1)+start;
    }

    /**
     * 从列表中随机取一个对象返回
     * @param list 列表
     * @return 对象
     */
    public static Object RandomObject(List list){
        return list.get(RandomInt(1,list.size())-1);
    }
    
}
