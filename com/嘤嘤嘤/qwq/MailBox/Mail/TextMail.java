package com.嘤嘤嘤.qwq.MailBox.Mail;

import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailCollectEvent;
import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailDeleteEvent;
import static com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI.setCollect;
import static com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI.setDelete;
import static com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI.setSend;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.Utils.DateTime;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TextMail {
    
    // 邮件类型
    protected String type;
    // 邮件id
    protected int id;
    // 邮件发送者
    protected String sender;
    // 邮件接收者
    protected List<String> recipient;
    // 邮件主题
    protected String topic;
    // 邮件内容
    protected String content;
    // 邮件发送日期
    protected String date;
    // 邮件类型显示名称
    protected String typeName;
    
    // 普通文本邮件（无附件）
    public TextMail(String type, int id, String sender, List<String> recipient, String topic, String content, String date){
        this.type = type;
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.topic = topic;
        this.content = content;
        this.date = date;
        this.typeName = GlobalConfig.getTypeName(type);
    }

    
    // 让玩家领取这封邮件
    public boolean Collect(Player p){
        return Read(p);
    }
    
    // 让玩家阅读这封邮件
    public boolean Read(Player p){
        if(setCollect(type, id, p.getName())){
            MailCollectEvent mce = new MailCollectEvent(this, p);
            Bukkit.getServer().getPluginManager().callEvent(mce);
            return true;
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件阅读失败！");
            return false;
        }
    }
    
    // 发送这封邮件
    public boolean Send(Player p){
        if(id==0){
            // 获取时间
            date = DateTime.get("ymdhms");
            // 新建邮件
            return setSend(type, id, sender, getRecipientString(), topic, content, date, "0");
        }else{
            // 修改已有邮件
            return false;
        }
    }
    
    // 删除这封邮件
    public boolean Delete(Player p){
        return DeleteData(p);
    }
    
    // 删除这封邮件的MySQL数据
    public boolean DeleteData(Player p){
        if(setDelete(type, id)){
            if(p!=null) p.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"邮件删除成功！");
            MailDeleteEvent mde = new MailDeleteEvent(this, p);
            Bukkit.getServer().getPluginManager().callEvent(mde);
            return true;
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件删除失败！");
            return false;
        }
    }
    
    public void setSender(String sender){
        this.sender = sender;
    }
    
    public void setRecipient(List<String> recipient) {
        this.recipient = recipient;
    }
    
    public String getType(){
        return this.type;
    }
    
    public int getId(){
        return this.id;
    }
    
    public String getSender(){
        return this.sender;
    }
    
    public List<String> getRecipient(){
        return this.recipient;
    }
    
    public String getRecipientString(){
        if(!type.equals("player")) return null;
        String str = "";
        for(String n: recipient) str += " "+n;
        str = str.substring(1);
        return str;
    }
    
    public String getTopic(){
        return this.topic;
    }
    
    public String getContent(){
        return this.content;
    }
    
    public String getDate(){
        return this.date;
    }
    
    public String getTypeName(){
        return this.typeName;
    }
    
    @Override
    public String toString(){
        String str = typeName+"-"+id+"-"+topic+"-"+content+"-"+sender+"-"+date;
        if(!recipient.isEmpty()) str += "-收件人："+getRecipientString();
        return str;
    }
    
}
