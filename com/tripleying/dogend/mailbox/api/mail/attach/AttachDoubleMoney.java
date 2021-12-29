package com.tripleying.dogend.mailbox.api.mail.attach;

/**
 * 小数附件金钱
 * @author Dogend
 */
public class AttachDoubleMoney implements AttachMoney {
    
    private double count;
    
    public AttachDoubleMoney(){
        this.count = 0.0;
    }
    
    public AttachDoubleMoney(double count){
        this.count = count;
    }

    @Override
    public Object getCount() {
        return this.count;
    }

    @Override
    public boolean addCount(Object o) {
        if(o instanceof Double && (double)o>=0.0){
            count += (double)o;
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean removeCount(Object o) {
        if(o instanceof Double && (double)o>=0.0){
            if(count-(double)o<0.0){
                return false;
            }else{
                count -= (double)o;
                return true;
            }
        }else{
            return false;
        }
    }

    @Override
    public boolean isZero() {
        return count==0.0;
    }
    
}
