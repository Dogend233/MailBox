package com.tripleying.dogend.mailbox.api.mail;

import com.tripleying.dogend.mailbox.api.mail.attach.AttachFile;
import com.tripleying.dogend.mailbox.manager.MailManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 基础邮件父类
 * 不要继承此类进行附属开发
 * @author Dogend
 */
public abstract class BaseMail {
    
    /**
     * 邮件id
     */
    protected long id;
    
    /**
     * 邮件类型
     */
    protected String type;
    
    /**
     * 邮件类型显示名称
     */
    protected String display;
    
    /**
     * 邮件标题
     */
    protected String title;
    
    /**
     * 邮件内容
     */
    protected List<String> body;
    
    /**
     * 发送人
     */
    protected String sender;
    
    /**
     * 发送时间
     */
    protected String sendtime;
    
    /**
     * 邮件附件
     */
    protected AttachFile attach;
    
    /**
     * 创建一封初始的新邮件
     * @param type 邮件类型
     * @param display 显示名称
     */
    public BaseMail(String type, String display){
        this.id = 0;
        this.type = type;
        this.display = display;
        this.title = "无";
        this.body = new ArrayList();
        this.sender = "无";
        this.sendtime = "2021-01-01 08:00:00";
        this.attach = new AttachFile();
    }
    
    /**
     * 创建一封完整参数的邮件
     * @param id 邮件id
     * @param title 邮件标题
     * @param type 邮件类型
     * @param display 展示名称
     * @param body 邮件内容
     * @param sender 发送人
     * @param sendtime 发送时间
     * @param attach 邮件附件
     */
    public BaseMail(long id, String title, String type, String display, List<String> body, String sender, String sendtime, AttachFile attach){
        this.id = id;
        this.title = title;
        this.type = type;
        this.display = display;
        this.body = body==null?new ArrayList():body;
        this.sender = sender;
        this.sendtime = sendtime;
        this.attach = attach==null?new AttachFile():attach;
    }
    
    /**
     * 从yml恢复一封邮件
     * @param yml YamlConfiguration
     */
    public BaseMail(YamlConfiguration yml){
        this.id = yml.getLong("id");
        this.title = yml.getString("title");
        this.type = yml.getString("type");
        this.display = MailManager.getMailManager().getSystemMailDisplay(type);
        this.body = yml.getStringList("body");
        this.sender = yml.getString("sender");
        this.sendtime = yml.getString("sendtime");
        this.attach = (AttachFile)yml.get("attach");
    }

    public long getId() {
        return id;
    }

    public BaseMail setId(long id) {
        this.id = id;
        return this;
    }
    
    public String getType(){
        return this.type;
    }
    
    public BaseMail setType(String type) {
        this.type = type;
        this.display = MailManager.getMailManager().getSystemMailDisplay(type);
        if(this instanceof SystemMail){
            return MailManager.getMailManager().loadSystemMail(this.toYamlConfiguration());
        }else{
            return this;
        }
        
    }
    
    public String getDisplay(){
        return this.display;
    }

    public String getTitle() {
        return title;
    }

    public BaseMail setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<String> getBody() {
        return body;
    }

    public BaseMail setBody(List<String> body) {
        this.body = body;
        return this;
    }
    
    public BaseMail addBody(String... strs){
        this.body.addAll(Arrays.asList(strs));
        return this;
    }

    public String getSender() {
        return sender;
    }
    
    public BaseMail setSender(String sender){
        this.sender = sender;
        return this;
    }

    public String getSendtime() {
        return sendtime;
    }

    public BaseMail setSendtime(String sendtime) {
        this.sendtime = sendtime;
        return this;
    }
    
    public AttachFile getAttachFile(){
        return this.attach;
    }

    public BaseMail setAttachFile(AttachFile attach) {
        this.attach = attach;
        return this;
    }
    
    /**
     * 邮件是否过期
     * 过期自动删除
     * @return boolean
     */
    public abstract boolean isExpire();
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o!=null && o instanceof BaseMail){
            final BaseMail bm =(BaseMail)o;
            return (this.id==bm.id && this.type.equals(bm.type) && this.title.equals(bm.title) && this.sender.equals(bm.sender) &&  this.sendtime.equals(bm.sendtime) && this.body.equals(bm.body) && this.attach.equals(bm.attach));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 43 * hash + Objects.hashCode(this.type);
        hash = 43 * hash + Objects.hashCode(this.title);
        hash = 43 * hash + Objects.hashCode(this.body);
        hash = 43 * hash + Objects.hashCode(this.sender);
        hash = 43 * hash + Objects.hashCode(this.sendtime);
        hash = 43 * hash + Objects.hashCode(this.attach);
        return hash;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("id: ").append(this.id).append('\n');
        sb.append("type: ").append(this.type).append('\n');
        sb.append("title: ").append(this.title).append('\n');
        sb.append("body: ").append('\n');
        this.body.forEach(s -> {
            sb.append("- ").append(s).append("\n");
        });
        sb.append("sender: ").append(this.type).append('\n');
        sb.append("sendtime: ").append(this.type).append('\n');
        sb.append(this.attach.toString());
        return sb.toString();
    }
    
    public YamlConfiguration toYamlConfiguration(){
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("id", this.id);
        yml.set("type", this.type);
        yml.set("title", this.title);
        yml.set("body", this.body);
        yml.set("sender", this.sender);
        yml.set("sendtime", this.sendtime);
        yml.set("attach", this.attach);
        return yml;
    }
    
}
