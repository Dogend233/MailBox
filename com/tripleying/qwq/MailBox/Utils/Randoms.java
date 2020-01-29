package com.tripleying.qwq.MailBox.Utils;

import java.util.List;
import java.util.Random;

public class Randoms {
    
    public static boolean RandomBoolean(double chance){
        if(chance<=0) return false;
        if(chance>=100) return true;
        return Math.random()<=(chance/100);
    }
    
    public static int RandomInt(int start, int end){
        return new Random().nextInt(end-start+1)+start;
    }
    
    public static Object RandomObject(List list){
        return list.get(RandomInt(1,list.size())-1);
    }
    
}
