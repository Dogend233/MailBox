package com.tripleying.dogend.mailbox.manager;

import com.tripleying.dogend.mailbox.api.mail.PersonMail;
import com.tripleying.dogend.mailbox.api.mail.PlayerData;
import com.tripleying.dogend.mailbox.api.mail.SystemMail;
import com.tripleying.dogend.mailbox.api.module.MailBoxModule;
import com.tripleying.dogend.mailbox.util.MessageUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * 邮件管理器
 * @author Dogend
 */
public class MailManager {
    
    private static MailManager manager;
    private final Map<String, SystemMail> map;
    private final Map<SystemMail, MailBoxModule> mod_map;
    
    public MailManager(){
        manager = this;
        this.map = new LinkedHashMap();
        this.mod_map = new HashMap();
    }
    
    /**
     * 创建一个新的系统邮件实例
     * @param type 邮件类型
     * @return SystemMail
     */
    public SystemMail createSystemMail(String type){
        if(this.map.containsKey(type)){
            return this.map.get(type).createSystemMail();
        }
        return null;
    }
    
    /**
     * 从Yml配置还原一封系统邮件
     * @param yml YamlConfiguration
     * @return SystemMail
     */
    public SystemMail loadSystemMail(YamlConfiguration yml){
        String type = yml.getString("type", null);
        if(type!=null && this.map.containsKey(type)){
            return this.map.get(type).loadSystemMail(yml);
        }
        return null;
    }
    
    /**
     * 检查玩家数据
     * @param pd 玩家数据
     */
    public void checkPlayerData(PlayerData pd){
        this.map.forEach((t,sm) -> {
            if(sm.needCheckPlayerData()) sm.checkPlayerData(pd);
        });
        pd.saveData();
    }
    
    /**
     * 注册系统邮件类型
     * @param module 模块
     * @param sm 系统邮件实例
     */
    public void registerSystemMail(MailBoxModule module, SystemMail sm){
        String type = sm.getType();
        if(this.map.containsKey(type)){
            MessageUtil.error(MessageUtil.mail_reg_error.replaceAll("%mail%", type));
        }else{
            this.map.put(type, sm);
            this.mod_map.put(sm, module);
            MessageUtil.log(MessageUtil.mail_reg.replaceAll("%mail%", type));
            if(sm.autoCreateDatabaseTable()) DataManager.getDataManager().createSystemMailStorage(sm);
        }
    }
    
    /**
     * 注销邮件
     * @param type 邮件类型
     */
    public void unregisterSystemMail(String type){
        if(this.map.containsKey(type)){
            this.mod_map.remove(this.map.get(type));
            this.map.remove(type);
            MessageUtil.error(MessageUtil.mail_unreg.replaceAll("%mail%", type));
        }
    }
    
    /**
     * 注销邮件
     * @param sm 邮件实例
     */
    public void unregisterSystemMail(SystemMail sm){
        if(this.map.containsValue(sm)){
            this.mod_map.remove(sm);
            this.map.remove(sm.getType());
            MessageUtil.error(MessageUtil.mail_unreg.replaceAll("%mail%", sm.getType()));
        }
    }
    
    /**
     * 注销全部邮件
     * @param module 模块
     */
    public void unregisterAllSystemMail(MailBoxModule module){
        if(this.mod_map.containsValue(module)){
            Set<SystemMail> mails = new HashSet();
            this.mod_map.entrySet().stream().filter(me -> (me.getValue()==module)).forEachOrdered(me -> {
                mails.add(me.getKey());
            });
            mails.forEach(mail -> {
                this.unregisterSystemMail(mail);
            });
        }
    }
    
    /**
     * 获取系统邮件类型列表
     * @return List
     */
    public List<SystemMail> getSystemMailTypeList(){
        List<SystemMail> list = new ArrayList();
        list.addAll(this.map.values());
        return list;
    }
    
    /**
     * 获取系统邮件展示名称
     * @param type 类型
     * @return String
     */
    public String getSystemMailDisplay(String type){
        return this.map.containsKey(type)?this.map.get(type).getDisplay():type;
    }
    
    /**
     * 获取系统邮件最大ID
     * @param sm 系统邮件
     * @return long
     */
    public long getSystemMailMax(SystemMail sm){
        return DataManager.getDataManager().getSystemMailMax(sm);
    }
    
    /**
     * 获取系统邮件页数
     * @param sm 系统邮件
     * @param view 单页最大数量
     * @return int
     */
    public int getSystemMailPages(SystemMail sm, int view){
        if(view>0){
            long count = DataManager.getDataManager().getSystemMailCount(sm);
            if(count>0){
                long pagel = count/view;
                if(count%view!=0) pagel++;
                return pagel>Integer.MAX_VALUE?Integer.MAX_VALUE:(int)pagel;
            }
        }
        return 0;
    }
    
    /**
     * 获取id从min到max的邮件集合
     * @param sm 系统邮件实例
     * @param min 最小值
     * @param max 最大值
     * @return Map
     */
    public Map<Long, SystemMail> getSystemMail(SystemMail sm, long min, long max){
        return DataManager.getDataManager().getSystemMail(sm, min, max);
    }
    
    /**
     * 获取系统邮件列表
     * @param type 邮件类型
     * @return List
     */
    public List<SystemMail> getSystemMailList(String type){
        return DataManager.getDataManager().getSystemMailList(this.map.getOrDefault(type, null));
    }
    
    /**
     * 获取固定数量的系统邮件列表
     * @param type 邮件类型
     * @param count 每页个数
     * @param page 页数
     * @return List
     */
    public List<SystemMail> getSystemMailList(String type, int count, int page){
        return DataManager.getDataManager().getSystemMailList(this.map.getOrDefault(type, null), count, page);
    }
    
    /**
     * 获取全部系统邮件列表
     * @return Map  
     */
    public Map<String, List<SystemMail>> getSystemMailList(){
        Map<String, List<SystemMail>> sml = new LinkedHashMap();
        this.map.forEach((t,sm) -> sml.put(t, DataManager.getDataManager().getSystemMailList(sm)));
        return sml;
    }
    
    /**
     * 按id及类型获取一封系统邮件
     * @param type 邮件类型
     * @param id 邮件ID
     * @since 3.3.0
     * @return SystemMail
     */
    public SystemMail getSystemMailById(String type, long id){
        return DataManager.getDataManager().getSystemMail(this.map.getOrDefault(type, null), id);
    }
    
    
    
    /**
     * 发送一封系统邮件
     * 若返回的邮件ID为0则发送失败
     * @param sm 系统邮件
     * @return boolean
     */
    public SystemMail sendSystemMail(SystemMail sm){
        return DataManager.getDataManager().sendSystemMail(sm);
    }
    
    /**
     * 删除系统邮件
     * @param sm 系统邮件实例
     * @return boolean
     */
    public boolean deleteSystemMail(SystemMail sm){
        return DataManager.getDataManager().deleteSystemMail(sm);
    }
    
    /**
     * 获取个人邮件页数
     * @param p 玩家
     * @param view 单页最大数量
     * @return int
     */
    public int getPersonMailPages(Player p, int view){
        if(view>0){
            long count = DataManager.getDataManager().getPersonMailCount(p);
            if(count>0){
                long pagel = count/view;
                if(count%view!=0) pagel++;
                return pagel>Integer.MAX_VALUE?Integer.MAX_VALUE:(int)pagel;
            }
        }
        return 0;
    }
    
    /**
     * 获取个人邮件列表
     * @param p 玩家
     * @return List
     */
    public List<PersonMail> getPersonMailList(Player p){
        return DataManager.getDataManager().getPersonMailList(p);
    }
    
    /**
     * 获取固定数量的个人邮件列表
     * @param p 玩家
     * @param count 每页个数
     * @param page 页数
     * @return List
     */
    public List<PersonMail> getPersonMailList(Player p, int count, int page){
        return DataManager.getDataManager().getPersonMailList(p, count, page);
    }
    
    /**
     * 发送一封个人邮件
     * @param pm 个人邮件
     * @param p 玩家
     * @return boolean
     */
    public boolean sendPersonMail(PersonMail pm, Player p){
        return DataManager.getDataManager().sendPersonMail(pm, p);
    }
    
    /**
     * 领取一封个人邮件
     * @param pm 个人邮件
     * @param p 玩家
     * @return boolean
     */
    public boolean receivePersonMail(PersonMail pm, Player p){
        return DataManager.getDataManager().receivePersonMail(pm, p);
    }
    
    /**
     * 领取全部个人邮件
     * @param p 玩家
     * @return long
     */
    public long receiveAllPersonMail(Player p){
        long i = 0l;
        for(PersonMail pm:getPersonMailList(p)){
            if(!pm.isReceived()){
                if(pm.receivedMail()){
                    i++;
                }else{
                    break;
                }
            }
        }
        return i;
    }
    
    /**
     * 删除一封个人邮件
     * @param pm 个人邮件
     * @return boolean
     */
    public boolean deletePersonMail(PersonMail pm){
        return DataManager.getDataManager().deletePersonMail(pm);
    }
    
    /**
     * 清空玩家已读/已领取邮件
     * @param p 玩家
     * @return long
     */
    public long clearPersonReceivedMail(Player p){
        return DataManager.getDataManager().clearPersonReceivedMail(p);
    }
    
    public static MailManager getMailManager(){
        return manager;
    }
    
}
