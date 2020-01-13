package com.tripleying.qwq.MailBox.Mail;

public interface MailDate {
    public void generateDate();
    public boolean isStart();
    public boolean ExpireValidate();
    public void setDeadline(String deadline);
    public String getDeadline();
}
