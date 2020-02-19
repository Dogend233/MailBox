package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.Event.MailSendEvent;
import com.tripleying.qwq.MailBox.API.Event.MailCollectEvent;
import com.tripleying.qwq.MailBox.API.Event.MailDeleteEvent;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Message;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import com.tripleying.qwq.MailBox.Utils.PlayerPointsUtil;
import com.tripleying.qwq.MailBox.Utils.TimeUtil;
import com.tripleying.qwq.MailBox.Utils.VaultUtil;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class BaseMail {
    
    // 邮件类型
    private final String type;
    // 邮件类型显示名称
    private final String typeName;
    // 邮件id
    private final int id;
    // 邮件发送者
    private String sender;
    // 邮件主题
    private String topic;
    // 邮件内容
    private String content;
    // 邮件发送日期
    private String date;
    // 邮件是否被修改过（未实现）
    private boolean modify;
    
    // 基础邮件（无附件）
    public BaseMail(String type, int id, String sender, String topic, String content, String date){
        this.type = type;
        this.id = id;
        this.sender = sender;
        this.topic = topic;
        this.content = content;
        this.date = date;
        this.typeName = Message.getTypeName(type);
    }
    
    // 将这封邮件的发送到数据库
    public boolean sendData(){
        return false;
    }
    
    // 邮件领取额外验证
    public boolean collectValidate(Player p){
        return true;
    }
    
    // 邮件发送额外验证
    public boolean sendValidate(Player p, ConversationContext cc){
        return true;
    }
    
    // 邮件过期验证
    public boolean ExpireValidate() {
        return false;
    }
    
    // 邮件次数验证
    public boolean TimesValidate() {
        return true;
    }
    
    // 邮件开始验证
    public boolean isStart() {
        return true;
    }
    
    // 生成时间
    public void generateDate(){
        date = TimeUtil.get("ymdhms");
    }
    
    // 让玩家领取这封邮件
    public boolean Collect(Player p) {
        if(!collectValidate(p)) return false;
        return Read(p);
    }
    
    // 让玩家阅读这封邮件
    public boolean Read(Player p){
        if(MailUtil.setCollect(type, id, p.getName())){
            MailCollectEvent mce = new MailCollectEvent(this, p);
            Bukkit.getServer().getPluginManager().callEvent(mce);
            return true;
        }else{
            p.sendMessage(Message.mailReadError);
            return false;
        }
    }
    
    // 发送这封邮件
    public boolean Send(CommandSender send, ConversationContext cc){
        if(send==null) return false;
        if(id==0){
            if(send instanceof Player){
                Player p = (Player)send;
                if(!sendValidate(p, cc)) return false;
                double needCoin = getExpandCoin();
                int needPoint = getExpandPoint();
                if(!enoughMoney(p,needCoin,needPoint,cc)) return false;
                // 获取时间
                generateDate();
                // 新建邮件
                if(sendData()){
                    // 扣钱
                    if(needCoin!=0 && !p.hasPermission("mailbox.admin.send.noconsume.coin") && removeCoin(p, needCoin)){
                        if(cc==null){
                            p.sendMessage(Message.mailExpand.replace("%type%", Message.moneyVault).replace("%count%", Double.toString(needCoin)));
                        }else{
                            cc.getForWhom().sendRawMessage(Message.mailExpand.replace("%type%", Message.moneyVault).replace("%count%", Double.toString(needCoin)));
                        }
                    }
                    if(needPoint!=0 && !p.hasPermission("mailbox.admin.send.noconsume.point") && removePoint(p, needPoint)){
                        if(cc==null){
                            p.sendMessage(Message.mailExpand.replace("%type%", Message.moneyPlayerpoints).replace("%count%", Integer.toString(needPoint)));
                        }else{
                            cc.getForWhom().sendRawMessage(Message.mailExpand.replace("%type%", Message.moneyPlayerpoints).replace("%count%", Integer.toString(needPoint)));
                        }
                    }
                    MailSendEvent mse = new MailSendEvent(this, p);
                    Bukkit.getServer().getPluginManager().callEvent(mse);
                    return true;
                }else{
                    if(cc==null){
                        p.sendMessage(Message.mailSendSqlError);
                    }else{
                        cc.getForWhom().sendRawMessage(Message.mailSendSqlError);
                    }
                    return false;
                }
            }else{
                generateDate();
                if(sendData()){
                    MailSendEvent mse = new MailSendEvent(this, send);
                    Bukkit.getServer().getPluginManager().callEvent(mse);
                    return true;
                }else{
                    if(cc==null){
                        send.sendMessage(Message.mailSendSqlError);
                    }else{
                        cc.getForWhom().sendRawMessage(Message.mailSendSqlError);
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
    
    // 删除这封邮件的数据库数据
    public boolean DeleteData(Player p){
        if(MailUtil.setDelete(type, id)){
            if(p!=null) p.sendMessage(Message.mailDeleteSuccess);
            MailDeleteEvent mde = new MailDeleteEvent(this, p);
            Bukkit.getServer().getPluginManager().callEvent(mde);
            return true;
        }else{
            p.sendMessage(Message.mailDeleteError);
            return false;
        }
    }
    
    // 判断玩家余额够不够
    public boolean enoughMoney(Player p,double needCoin,int needPoint, ConversationContext cc){
        // 判断玩家coin够不够
        if(GlobalConfig.enVault && !p.hasPermission("mailbox.admin.send.check.coin") && GlobalConfig.vaultExpand!=0){
            if(VaultUtil.getEconomyBalance(p)<needCoin){
                if(cc==null){
                    p.sendMessage(Message.mailExpandError.replace("%type%", Message.moneyVault).replace("%count%", Double.toString(needCoin)));
                }else{
                    cc.getForWhom().sendRawMessage(Message.mailExpandError.replace("%type%", Message.moneyVault).replace("%count%", Double.toString(needCoin)));
                }
                return false;
            }
        }
        // 判断玩家point够不够
        if(GlobalConfig.enPlayerPoints && !p.hasPermission("mailbox.admin.send.check.point") && GlobalConfig.playerPointsExpand!=0){
            if(PlayerPointsUtil.getPoints(p)<needPoint){
                if(cc==null){
                    p.sendMessage(Message.mailExpandError.replace("%type%", Message.moneyPlayerpoints).replace("%count%", Integer.toString(needPoint)));
                }else{
                    cc.getForWhom().sendRawMessage(Message.mailExpandError.replace("%type%", Message.moneyPlayerpoints).replace("%count%", Integer.toString(needPoint)));
                }
                return false;
            }
        }
        return true;
    }
    
    public final int getId(){
        return this.id;
    }
    
    public final String getTypeName(){
        return this.typeName;
    }
    
    public BaseMail setType(String type){
        return MailUtil.createBaseMail(type, id, sender, null, null, topic, content, date, null, 0, null, false, null);
    }
    
    public final String getType(){
        return this.type;
    }
    
    public final void setSender(String sender){
        this.sender = sender;
    }
    
    public final String getSender(){
        return this.sender;
    }
    
    public final void setTopic(String topic){
        this.topic = topic;
    }
    
    public final String getTopic(){
        return this.topic;
    }
    
    public final void setContent(String content){
        this.content = content;
    }
    
    public final String getContent(){
        return this.content;
    }
    
    public final void setDate(String date){
        this.date = date;
    }
    
    public final String getDate(){
        return this.date;
    }
    
    public final boolean removeCoin(Player p, double coin){
        return VaultUtil.reduceEconomy(p, coin);
    }
    
    public final boolean removePoint(Player p, int point){
        return PlayerPointsUtil.reducePoints(p, point);
    }
    
    public double getExpandCoin(){
        if(GlobalConfig.enVault && GlobalConfig.vaultExpand!=0){
            return GlobalConfig.vaultExpand;
        }else{
            return 0;
        }
    }
    
    public int getExpandPoint(){
        if(GlobalConfig.enPlayerPoints && GlobalConfig.playerPointsExpand!=0){
            return GlobalConfig.playerPointsExpand;
        }else{
            return 0;
        }
    }

    @Override
    public String toString(){
        return typeName+"§r-"+id+"§r-"+topic+"§r-"+content+"§r-"+sender+"§r-"+date;
    }
    
    public BaseFileMail addFile(){
        return new BaseFileMail(type,id,sender,topic,content,date,"0",new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),0,0);
    };
    
}
