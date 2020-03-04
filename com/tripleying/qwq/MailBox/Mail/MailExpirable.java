package com.tripleying.qwq.MailBox.Mail;

/**
 * 可过期邮件
 */
public interface MailExpirable {
    
    /**
     * 邮件过期验证
     * @return boolean
     */
    public boolean ExpireValidate();
    
    /**
     * 邮件过期时间
     * @return boolean
     */
    public String getExpireDate();
    
}
