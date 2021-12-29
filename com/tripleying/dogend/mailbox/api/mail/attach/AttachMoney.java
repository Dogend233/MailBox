package com.tripleying.dogend.mailbox.api.mail.attach;

/**
 * 附件金钱接口
 * @author Dogend
 */
public interface AttachMoney {
    
    // 获取当前数量
    public Object getCount();
    
    // 增加指定数量
    public boolean addCount(Object o);
    
    // 移除指定数量
    public boolean removeCount(Object o);
    
    // 是否为空
    public boolean isZero();
    
}
