package com.tripleying.dogend.mailbox.api.mail;

import com.tripleying.dogend.mailbox.api.event.mail.MailBoxPersonMailDeleteEvent;
import com.tripleying.dogend.mailbox.api.event.mail.MailBoxPersonMailPreSendEvent;
import com.tripleying.dogend.mailbox.api.event.mail.MailBoxPersonMailReceiveEvent;
import com.tripleying.dogend.mailbox.api.event.mail.MailBoxPersonMailSendEvent;
import com.tripleying.dogend.mailbox.manager.MailManager;
import com.tripleying.dogend.mailbox.util.TimeUtil;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * 个人邮件
 * 直接玩家收件箱的邮件
 * @author Dogend
 */
public final class PersonMail extends BaseMail {
    
    /**
     * 是否已接收附件
     */
    protected boolean received;
    /**
     * 收件人
     */
    protected String uuid;
    
    /**
     * 将一封SystemMail转换为PersonMail 
     * @param sm 系统邮件
     */
    public PersonMail(SystemMail sm){
        super(sm.toYamlConfiguration());
        this.received = false;
    }
    
    /**
     * 从yml恢复一封邮件
     * @param yml YamlConfiguration
     */
    public PersonMail(YamlConfiguration yml){
        super(yml);
        this.received = yml.getBoolean("received");
        this.uuid = yml.getString("uuid");
    }
    
    /**
     * 设置接收者
     * @param p 玩家
     * @return this
     */
    public PersonMail setReceiver(Player p){
        this.uuid = p.getUniqueId().toString();
        return this;
    }
    
    /**
     * 是否已读/接收附件
     * @return boolean
     */
    public boolean isReceived(){
        return this.received;
    }
    
    /**
     * 获取UUID
     * @return String
     */
    public String getUUID() {
        return uuid;
    }
    
    /**
     * 获取接收人
     * @return Player
     */
    public Player getReceiver(){
        return Bukkit.getPlayer(UUID.fromString(this.uuid));
    }
    
    /**
     * 判断邮件是否过期
     * @return boolean
     */
    @Override
    public boolean isExpire(){
        return TimeUtil.isExpire(this.sendtime);
    }
    
    /**
     * 发送邮件
     * @return boolean 
     */
    public boolean sendMail(){
        Player p = Bukkit.getPlayer(UUID.fromString(this.uuid));
        if(p!=null){
            MailBoxPersonMailPreSendEvent evt = new MailBoxPersonMailPreSendEvent(this);
            Bukkit.getPluginManager().callEvent(evt);
            if(evt.isCancelled()) return false;
            if(MailManager.getMailManager().sendPersonMail(this, p)){
                Bukkit.getPluginManager().callEvent(new MailBoxPersonMailSendEvent(this));
                return true;
            }
        }
        return false;
    }
    
    /**
     * 玩家查看邮件
     * 如果没有附件则设置为已领取
     * 如果领取状态改变则返回true
     * @return boolean
     */
    public boolean seeMail(){
        return (!this.isReceived() && !this.getAttachFile().hasAttach() && this.receivedMail());
    }
    
    /**
     * 已读/领取邮件
     * @return boolean
     */
    public boolean receivedMail(){
        Player p = Bukkit.getPlayer(UUID.fromString(this.uuid));
        if(p!=null && this.getAttachFile().checkInventory(p) && MailManager.getMailManager().receivePersonMail(this, p)){
            this.getAttachFile().receivedAttach(p);
            this.received = true;
            Bukkit.getPluginManager().callEvent(new MailBoxPersonMailReceiveEvent(this));
            return true;
        }
        return false;
    }
    
    /**
     * 删除邮件
     */
    public void deleteMail(){
        if(MailManager.getMailManager().deletePersonMail(this)){
            Bukkit.getPluginManager().callEvent(new MailBoxPersonMailDeleteEvent(this));
        }
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o!=null && o instanceof PersonMail && super.equals(o)){
            PersonMail pm =(PersonMail)o;
            return (this.received==pm.received && this.uuid.equals(pm.uuid));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 83 * hash + (this.received ? 1 : 0);
        hash = 83 * hash + Objects.hashCode(this.uuid);
        return hash;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("uuid: ").append(this.uuid).append('\n');
        sb.append("received: ").append(this.received).append('\n');
        sb.append(super.toString());
        return sb.toString();
    }
    
    @Override
    public YamlConfiguration toYamlConfiguration(){
        YamlConfiguration yml = super.toYamlConfiguration();
        yml.set("uuid", this.uuid);
        yml.set("received", this.received);
        return yml;
    }
    
}
