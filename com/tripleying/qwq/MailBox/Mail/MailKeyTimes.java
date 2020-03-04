package com.tripleying.qwq.MailBox.Mail;

/**
 * keytimes邮件
 */
public interface MailKeyTimes extends MailTimes {

    /**
     * 获取邮件口令
     * @return 口令
     */
    public String getKey();

    /**
     * 设置邮件口令
     * @param key 口令
     */
    public void setKey(String key);
    
}
