package com.tripleying.qwq.MailBox.Mail;

public interface MailKeyTimes extends MailTimes {
    @Override
    public boolean sendData();
    public String getKey();
    public void setKey(String key);
}
