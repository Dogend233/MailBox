package com.嘤嘤嘤.qwq.MailBox.Mail;

import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailCollectEvent;
import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailDeleteEvent;
import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailSendEvent;
import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.Utils.DateTime;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TextMail{
    
    // 邮件类型
    private final String type;
    // 邮件类型显示名称
    private final String typeName;
    // 邮件id
    private final int id;
    // 邮件发送者
    private String sender;
    // 邮件接收者
    private List<String> recipient;
    // 领取邮件需要权限
    private String permission;
    // 邮件主题
    private String topic;
    // 邮件内容
    private String content;
    // 邮件发送日期
    private String date;
    // 邮件是否被修改过（未实现）
    private boolean modify;
    
    // 普通文本邮件（无附件）
    public TextMail(String type, int id, String sender, List<String> recipient, String permission, String topic, String content, String date){
        this.type = type;
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.permission = permission;
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
        if(MailBoxAPI.setCollect(type, id, p.getName())){
            MailCollectEvent mce = new MailCollectEvent(this, p);
            Bukkit.getServer().getPluginManager().callEvent(mce);
            return true;
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件阅读失败！");
            return false;
        }
    }
    
    // 判断发件量是否达到上限
    public boolean isMax(Player p){
        if(type.equals("player")){
            int out = MailBoxAPI.playerAsSenderAllow(p);
            int outed = MailBoxAPI.playerAsSender(p);
            if((out-outed)<=0){
                p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你的"+GlobalConfig.getTypeName(type)+"邮件发送数量达到上限");
                return true;
            }
        }
        return false;
    }
    
    // 判断玩家余额够不够
    public boolean enoughMoney(Player p,double needCoin,int needPoint, ConversationContext cc){
        // 判断玩家coin够不够
        if(GlobalConfig.enVault && !p.hasPermission("mailbox.admin.send.check.coin") && GlobalConfig.vaultExpand!=0){
            if(MailBoxAPI.getEconomyBalance(p)<needCoin){
                if(cc==null){
                    p.sendMessage(GlobalConfig.normal+"[邮件预览]："+GlobalConfig.vaultDisplay+GlobalConfig.normal+"不足，共需要"+needCoin);
                }else{
                    cc.getForWhom().sendRawMessage(GlobalConfig.normal+"[邮件预览]："+GlobalConfig.vaultDisplay+GlobalConfig.normal+"不足，共需要"+needCoin);
                }
                return false;
            }
        }
        // 判断玩家point够不够
        if(GlobalConfig.enPlayerPoints && !p.hasPermission("mailbox.admin.send.check.point") && GlobalConfig.playerPointsExpand!=0){
            if(MailBoxAPI.getPoints(p)<needPoint){
                if(cc==null){
                    p.sendMessage(GlobalConfig.normal+"[邮件预览]："+GlobalConfig.playerPointsDisplay+GlobalConfig.normal+"不足，共需要"+needPoint);
                }else{
                    cc.getForWhom().sendRawMessage(GlobalConfig.normal+"[邮件预览]："+GlobalConfig.playerPointsDisplay+GlobalConfig.normal+"不足，共需要"+needPoint);
                }
                return false;
            }
        }
        return true;
    }
    
    // 发送这封邮件
    public boolean Send(CommandSender send, ConversationContext cc){
        if(id==0){
            if(send instanceof Player){
                Player p = (Player)send;
                if(isMax(p)) return false;
                double needCoin = getExpandCoin();
                int needPoint = getExpandPoint();
                if(!enoughMoney(p,needCoin,needPoint,cc)) return false;
                // 获取时间
                date = DateTime.get("ymdhms");
                // 新建邮件
                if(MailBoxAPI.setSend(type, id, sender, getRecipientString(), permission, topic, content, date, "0")){
                    // 扣钱
                    if(needCoin!=0 && !p.hasPermission("mailbox.admin.send.noconsume.coin") && removeCoin(p, needCoin)){
                        if(cc==null){
                            p.sendMessage(GlobalConfig.normal+"[邮件预览]：花费了"+needCoin+GlobalConfig.vaultDisplay);
                        }else{
                            cc.getForWhom().sendRawMessage(GlobalConfig.normal+"[邮件预览]：花费了"+needCoin+GlobalConfig.vaultDisplay);
                        }
                    }
                    if(needPoint!=0 && !p.hasPermission("mailbox.admin.send.noconsume.point") && removePoint(p, needPoint)){
                        if(cc==null){
                            p.sendMessage(GlobalConfig.normal+"[邮件预览]：花费了"+needPoint+GlobalConfig.playerPointsDisplay);
                        }else{
                            cc.getForWhom().sendRawMessage(GlobalConfig.normal+"[邮件预览]：花费了"+needPoint+GlobalConfig.playerPointsDisplay);
                        }
                    }
                    MailSendEvent mse = new MailSendEvent(this, p);
                    Bukkit.getServer().getPluginManager().callEvent(mse);
                    return true;
                }else{
                    if(cc==null){
                        p.sendMessage(GlobalConfig.normal+"[邮件预览]：邮件发送至数据库失败");
                    }else{
                        cc.getForWhom().sendRawMessage(GlobalConfig.normal+"[邮件预览]：邮件发送至数据库失败");
                    }
                    return false;
                }
            }else{
                date = DateTime.get("ymdhms");
                if(MailBoxAPI.setSend(type, id, sender, getRecipientString(), permission, topic, content, date, "0")){
                    MailSendEvent mse = new MailSendEvent(this, send);
                    Bukkit.getServer().getPluginManager().callEvent(mse);
                    return true;
                }else{
                    if(cc==null){
                        send.sendMessage(GlobalConfig.normal+"[邮件预览]：邮件发送至数据库失败");
                    }else{
                        cc.getForWhom().sendRawMessage(GlobalConfig.normal+"[邮件预览]：邮件发送至数据库失败");
                    }
                    return false;
                }
            }
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
        if(MailBoxAPI.setDelete(type, id)){
            if(p!=null) p.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"邮件删除成功！");
            MailDeleteEvent mde = new MailDeleteEvent(this, p);
            Bukkit.getServer().getPluginManager().callEvent(mde);
            return true;
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件删除失败！");
            return false;
        }
    }
    
    public String getType(){
        return this.type;
    }
        
    public String getTypeName(){
        return this.typeName;
    }
    
    public int getId(){
        return this.id;
    }
    
    public void setSender(String sender){
        this.sender = sender;
    }
    
    public String getSender(){
        return this.sender;
    }
    
    public void setRecipient(List<String> recipient) {
        this.recipient = recipient;
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
    
    public void setPermission(String permission){
        this.permission = permission;
    }
    
    public String getPermission(){
        if(!type.equals("permission")) return null;
        return this.permission;
    }
        
    public void setTopic(String topic){
        this.topic = topic;
    }
    
    public String getTopic(){
        return this.topic;
    }
    
    public void setContent(String content){
        this.content = content;
    }
    
    public String getContent(){
        return this.content;
    }
    
    public void setDate(String date){
        this.date = date;
    }
    
    public String getDate(){
        return this.date;
    }
    
    public boolean removeCoin(Player p, double coin){
        return MailBoxAPI.reduceEconomy(p, coin);
    }
    
    public double getExpandCoin(){
        if(GlobalConfig.enVault && GlobalConfig.vaultExpand!=0){
            return GlobalConfig.vaultExpand;
        }else{
            return 0;
        }
    }
    
    public boolean removePoint(Player p, int point){
        return MailBoxAPI.reducePoints(p, point);
    }
    
    public int getExpandPoint(){
        if(GlobalConfig.enPlayerPoints && GlobalConfig.playerPointsExpand!=0){
            return GlobalConfig.playerPointsExpand;
        }else{
            return 0;
        }
    }
    
    public boolean hasFile(){
        return false;
    }
    
    public FileMail toFileMail(){
        return new FileMail(type,id,sender,recipient,permission,topic,content,date,"0",new ArrayList<ItemStack>(),new ArrayList<String>(),new ArrayList<String>(),0,0);
    }
    
    @Override
    public String toString(){
        String str = typeName+"§r-"+id+"§r-"+topic+"§r-"+content+"§r-"+sender+"§r-"+date;
        if(type.equals("player") && !recipient.isEmpty()) str += "§r-收件人："+getRecipientString();
        return str;
    }
    
    @Override
    public TextMail clone(){
        return new TextMail(type,id,sender,recipient,permission,topic,content,date);
    }
    
    public boolean equals(TextMail tm){
        return false;
    }
}
