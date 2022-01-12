package com.tripleying.dogend.mailbox.api.mail;

import com.tripleying.dogend.mailbox.api.event.mail.MailBoxPersonMailPreSendEvent;
import com.tripleying.dogend.mailbox.api.event.mail.MailBoxPersonMailSendEvent;
import com.tripleying.dogend.mailbox.api.event.mail.MailBoxSystemMailDeleteEvent;
import com.tripleying.dogend.mailbox.api.event.mail.MailBoxSystemMailSendEvent;
import com.tripleying.dogend.mailbox.manager.MailManager;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * 系统邮件
 * 当玩家登陆时, 判断条件将此邮件则以个人邮件形式发送给玩家
 * @author Dogend
 */
public abstract class SystemMail extends BaseMail {

    public SystemMail(String type, String display) {
        super(type, display);
    }
    
    public SystemMail(YamlConfiguration yml) {
        super(yml);
    }
    
    /**
     * 创建一个新的系统邮件实例
     * @return SystemMail
     */
    public abstract SystemMail createSystemMail();
    
    /**
     * 从yml加载一个系统邮件实例
     * @param yml YamlConfiguration
     * @return SystemMail
     */
    public abstract SystemMail loadSystemMail(YamlConfiguration yml);
    
    /**
     * 对比玩家数据并给玩家发送邮件
     * @param pd 玩家数据
     */
    public abstract void checkPlayerData(PlayerData pd);
    
    /**
     * 通过邮件ID自增值对比玩家数据并给玩家发送邮件
     * @param pd 玩家数据
     */
    protected void checkPlayerDataByMaxId(PlayerData pd){
        Player p = pd.getPlayer();
        if(p==null) return;
        Object d = pd.getData(this.type);
        long now = d==null?0:Long.parseLong(d.toString());
        long max = MailManager.getMailManager().getSystemMailMax(this);
        if(now<max){
            now++;
            Map<Long, SystemMail> smap = MailManager.getMailManager().getSystemMail(this, now, max);
            Iterator<Map.Entry<Long, SystemMail>> it = smap.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<Long, SystemMail> me = it.next();
                SystemMail sm = me.getValue();
                if(sm.couldSend2Player(p) && sm.send2Player(p)){
                    now = me.getKey();
                }else{
                    break;
                }
            }
            pd.setData(this.type, now);
            pd.saveData();
        }
    }
    
    /**
     * 是否需要在玩家进入时对比玩家数据
     * @return boolean
     */
    public abstract boolean needCheckPlayerData();
    
    /**
     * 这封邮件是否能发送给此玩家
     * @param p 玩家
     * @return boolean
     */
    public abstract boolean couldSend2Player(Player p);
    
    /**
     * 是否自动创建邮件数据表
     * @return boolean
     */
    public abstract boolean autoCreateDatabaseTable();
    
    /**
     * 这个人是否有权限发送此邮件
     * @param sender 控制台/玩家
     * @return boolean
     */
    public abstract boolean couldSendMail(CommandSender sender);
    
    /**
     * 作为个人邮件发送给玩家
     * @param p 玩家
     * @return PersonMail
     */
    public boolean send2Player(Player p){
        PersonMail pm = new PersonMail(this);
        pm.setReceiver(p);
        MailBoxPersonMailPreSendEvent evt = new MailBoxPersonMailPreSendEvent(pm);
        Bukkit.getPluginManager().callEvent(evt);
        if(!evt.isCancelled() && MailManager.getMailManager().sendPersonMail(pm, p)){
            Bukkit.getPluginManager().callEvent(new MailBoxPersonMailSendEvent(pm));
            return true;
        }
        return false;
    }
    
    /**
     * 发送邮件
     * @return SystemMail
     */
    public SystemMail sendMail(){
        SystemMail sm = MailManager.getMailManager().sendSystemMail(this);
        if(sm.getId()!=0){
            Bukkit.getPluginManager().callEvent(new MailBoxSystemMailSendEvent(sm));
        }
        return sm;
    }
    
    /**
     * 删除邮件
     */
    public void deleteMail(){
        if(MailManager.getMailManager().deleteSystemMail(this)){
            Bukkit.getPluginManager().callEvent(new MailBoxSystemMailDeleteEvent(this));
        }
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
         return (o!=null && o instanceof SystemMail && super.equals(o));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public String toString(){
        return super.toString();
    }
    
    @Override
    public YamlConfiguration toYamlConfiguration(){
        return super.toYamlConfiguration();
    }
    
}
