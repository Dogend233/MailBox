package com.tripleying.dogend.mailbox.api.mail.attach;

/**
 * 整数附件金钱
 * @author Dogend
 */
public class AttachIntegerMoney implements AttachMoney {
    
    private int count;

    public AttachIntegerMoney(){
        this.count = 0;
    }
    
    public AttachIntegerMoney(int count){
        this.count = count;
    }

    @Override
    public Object getCount() {
        return this.count;
    }

    @Override
    public boolean addCount(Object o) {
        if(o instanceof Integer && (int)o>=0){
            count += (int)o;
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean removeCount(Object o) {
        if(o instanceof Integer && (int)o>=0){
            if(count-(int)o<0){
                return false;
            }else{
                count -= (int)o;
                return true;
            }
        }else{
            return false;
        }
    }

    @Override
    public boolean isZero() {
        return count==0;
    }
    
}
