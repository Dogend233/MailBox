package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.Event.MailSendEvent;
import com.tripleying.qwq.MailBox.API.Event.MailCollectEvent;
import com.tripleying.qwq.MailBox.API.Event.MailDeleteEvent;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.OuterMessage;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import com.tripleying.qwq.MailBox.Utils.PlayerPointsUtil;
import com.tripleying.qwq.MailBox.Utils.TimeUtil;
import com.tripleying.qwq.MailBox.Utils.VaultUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

/**
 * 基础邮件(文本邮件)
 */
public abstract class BaseMail {
    
    /**
     * 邮件类型
     */
    private final String type;
    
    /**
     * 邮件类型显示名称
     */
    private final String typeName;
    
    /**
     * 邮件id
     */
    private final int id;
    
    /**
     * 邮件发送者
     */
    private String sender;
    
    /**
     * 邮件主题
     */
    private String topic;
    
    /**
     * 邮件内容
     */
    private String content;
    
    /**
     * 邮件发送日期
     */
    private String date;
    
    /**
     * 邮件内容是否被修改过
     * （未实现）
     */
    private boolean modify;
    
    public BaseMail(String type, int id, String sender, String topic, String content, String date){
        this.type = type;
        this.id = id;
        this.sender = sender;
        this.topic = topic;
        this.content = content;
        this.date = date;
        this.typeName = OuterMessage.getTypeName(type);
    }

    /**
     * 将这封邮件的数据发送到数据库
     * @return boolean
     */
    public abstract boolean sendData();

    /**
     * 邮件领取额外验证
     * @param p 玩家
     * @return boolean
     */
    public abstract boolean collectValidate(Player p);

    /**
     * 邮件发送额外验证
     * @param p 玩家
     * @param cc 会话
     * @return boolean
     */
    public abstract boolean sendValidate(Player p, ConversationContext cc);
    
    /**
     * 将邮件转化为附件邮件
     * @return 附件邮件
     */
    public abstract BaseFileMail addFile();
    
    /**
     * 生成发送时间
     */
    public void generateDate(){
        date = TimeUtil.get("ymdhms");
    }
    
    /**
     * 让玩家领取这封邮件
     * @param p 玩家
     * @return boolean
     */
    public boolean Collect(Player p) {
        if(!collectValidate(p)) return false;
        return Read(p);
    }

    /**
     * 让玩家阅读这封邮件
     * @param p 玩家
     * @return boolean
     */
    public boolean Read(Player p){
        if(MailUtil.setCollect(type, id, p.getName())){
            MailCollectEvent mce = new MailCollectEvent(this, p);
            Bukkit.getServer().getPluginManager().callEvent(mce);
            return true;
        }else{
            p.sendMessage(OuterMessage.mailReadError);
            return false;
        }
    }
    
    /**
     * 发送这封邮件
     * @param send 指令发送者
     * @param cc 会话
     * @return boolean
     */
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
                            p.sendMessage(OuterMessage.mailExpand.replace("%type%", OuterMessage.moneyVault).replace("%count%", Double.toString(needCoin)));
                        }else{
                            cc.getForWhom().sendRawMessage(OuterMessage.mailExpand.replace("%type%", OuterMessage.moneyVault).replace("%count%", Double.toString(needCoin)));
                        }
                    }
                    if(needPoint!=0 && !p.hasPermission("mailbox.admin.send.noconsume.point") && removePoint(p, needPoint)){
                        if(cc==null){
                            p.sendMessage(OuterMessage.mailExpand.replace("%type%", OuterMessage.moneyPlayerpoints).replace("%count%", Integer.toString(needPoint)));
                        }else{
                            cc.getForWhom().sendRawMessage(OuterMessage.mailExpand.replace("%type%", OuterMessage.moneyPlayerpoints).replace("%count%", Integer.toString(needPoint)));
                        }
                    }
                    MailSendEvent mse = new MailSendEvent(this, p);
                    Bukkit.getServer().getPluginManager().callEvent(mse);
                    return true;
                }else{
                    if(cc==null){
                        p.sendMessage(OuterMessage.mailSendSqlError);
                    }else{
                        cc.getForWhom().sendRawMessage(OuterMessage.mailSendSqlError);
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
                        send.sendMessage(OuterMessage.mailSendSqlError);
                    }else{
                        cc.getForWhom().sendRawMessage(OuterMessage.mailSendSqlError);
                    }
                    return false;
                }
            }
        }else{
            // 修改已有邮件
            return false;
        }
    }

    /**
     * 删除这封邮件
     * @param p 玩家
     * @return boolean
     */
    public boolean Delete(Player p){
        return DeleteData(p);
    }

    /**
     * 删除这封邮件的数据库数据
     * @param p 玩家
     * @return boolean
     */
    public boolean DeleteData(Player p){
        if(MailUtil.setDelete(type, id)){
            if(p!=null) p.sendMessage(OuterMessage.mailDeleteSuccess);
            MailDeleteEvent mde = new MailDeleteEvent(this, p);
            Bukkit.getServer().getPluginManager().callEvent(mde);
            return true;
        }else{
            p.sendMessage(OuterMessage.mailDeleteError);
            return false;
        }
    }

    /**
     * 判断玩家余额是否充足
     * @param p 玩家
     * @param needCoin 需要的金币
     * @param needPoint 需要的点券
     * @param cc 会话
     * @return boolean
     */
    public boolean enoughMoney(Player p,double needCoin,int needPoint, ConversationContext cc){
        if(GlobalConfig.enVault && !p.hasPermission("mailbox.admin.send.check.coin") && GlobalConfig.vaultExpand!=0){
            if(VaultUtil.getEconomyBalance(p)<needCoin){
                if(cc==null){
                    p.sendMessage(OuterMessage.mailExpandError.replace("%type%", OuterMessage.moneyVault).replace("%count%", Double.toString(needCoin)));
                }else{
                    cc.getForWhom().sendRawMessage(OuterMessage.mailExpandError.replace("%type%", OuterMessage.moneyVault).replace("%count%", Double.toString(needCoin)));
                }
                return false;
            }
        }
        if(GlobalConfig.enPlayerPoints && !p.hasPermission("mailbox.admin.send.check.point") && GlobalConfig.playerPointsExpand!=0){
            if(PlayerPointsUtil.getPoints(p)<needPoint){
                if(cc==null){
                    p.sendMessage(OuterMessage.mailExpandError.replace("%type%", OuterMessage.moneyPlayerpoints).replace("%count%", Integer.toString(needPoint)));
                }else{
                    cc.getForWhom().sendRawMessage(OuterMessage.mailExpandError.replace("%type%", OuterMessage.moneyPlayerpoints).replace("%count%", Integer.toString(needPoint)));
                }
                return false;
            }
        }
        return true;
    }
    
    /**
     * 获取邮件id
     * @return 邮件id
     */
    public final int getId(){
        return this.id;
    }
    
    /**
     * 设置邮件类型
     * @param type 邮件类型
     * @return 邮件
     */
    public BaseMail setType(String type){
        return MailUtil.createBaseMail(type, id, sender, null, null, topic, content, date, null, 0, null, false, null);
    }
    
    /**
     * 获取邮件类型
     * @return 邮件类型
     */
    public final String getType(){
        return this.type;
    }
    
    /**
     * 获取邮件类型展示名字
     * @return 邮件类型展示名字
     */
    public final String getTypeName(){
        return this.typeName;
    }

    /** 
     * 设置发件人
     * @param sender 发件人
     */
    public final void setSender(String sender){
        this.sender = sender;
    }
    
    /**
     * 获取发件人
     * @return 发件人
     */
    public final String getSender(){
        return this.sender;
    }
    
    /**
     * 设置主题
     * @param topic 主题
     */
    public final void setTopic(String topic){
        this.topic = topic;
    }
    
    /**
     * 获取主题
     * @return 主题
     */
    public final String getTopic(){
        return this.topic;
    }
    
    /**
     * 设置内容
     * @param content 内容
     */
    public final void setContent(String content){
        this.content = content;
    }
    
    /**
     * 获取内容
     * @return 内容
     */
    public final String getContent(){
        return this.content;
    }
    
    /**
     * 设置日期
     * @param date 日期
     */
    public final void setDate(String date){
        this.date = date;
    }
    
    /**
     * 获取日期
     * @return 日期
     */
    public final String getDate(){
        return this.date;
    }
    
    /**
     * 扣金币
     * @param p 玩家
     * @param coin 数量
     * @return boolean
     */
    public final boolean removeCoin(Player p, double coin){
        return VaultUtil.reduceEconomy(p, coin);
    }
    
    /**
     * 扣点券
     * @param p 玩家
     * @param point 数量
     * @return boolean
     */
    public final boolean removePoint(Player p, int point){
        return PlayerPointsUtil.reducePoints(p, point);
    }
    
    /**
     * 获取发件消耗金币
     * @return 数量(double)
     */
    public double getExpandCoin(){
        if(GlobalConfig.enVault && GlobalConfig.vaultExpand!=0){
            return GlobalConfig.vaultExpand;
        }else{
            return 0;
        }
    }
    
    /**
     * 获取发件消耗点券
     * @return 数量(int)
     */
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
    
}
