package com.tripleying.dogend.mailbox.manager;

import com.tripleying.dogend.mailbox.MailBox;
import com.tripleying.dogend.mailbox.api.data.BaseData;
import com.tripleying.dogend.mailbox.api.mail.CustomData;
import com.tripleying.dogend.mailbox.api.mail.PersonMail;
import com.tripleying.dogend.mailbox.api.mail.PlayerData;
import com.tripleying.dogend.mailbox.api.mail.SystemMail;
import com.tripleying.dogend.mailbox.api.module.MailBoxModule;
import com.tripleying.dogend.mailbox.util.MessageUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

/**
 * 数据源管理器
 * @author Dogend
 */
public class DataManager {
    
    private static DataManager manager;
    private final Map<String, BaseData> map;
    private final Map<BaseData, MailBoxModule> mod_map;
    /**
     * 使用的数据源
     */
    private BaseData data;
    
    public DataManager(){
        manager = this;
        map = new HashMap();
        mod_map = new HashMap();
    }
    
    /**
     * 获取当前使用的数据源
     * @return BaseData
     */
    public BaseData getData(){
        return this.data;
    }
    
    /**
     * 添加一个数据源
     * @param module 模块
     * @param data 数据源
     * @return boolean
     */
    public boolean addData(MailBoxModule module, BaseData data){
        if(this.map.containsKey(data.getType())){
            MessageUtil.error(MessageUtil.data_reg_error.replaceAll("%data%", data.getType()));
            return false;
        }else{
            this.map.put(data.getType(), data);
            this.mod_map.put(data, module);
            MessageUtil.log(MessageUtil.data_reg.replaceAll("%data%", data.getType()));
            return true;
        }
    }
    
    /**
     * 选择已存在的数据源并启用
     * 启用成功后会删除其他的数据源
     * 启用失败将会卸载插件
     * @param name 数据源名
     * @return boolean
     */
    public boolean selectData(String name){
        if(this.map.containsKey(name)){
            BaseData db = this.map.get(name);
            if(db.enable()){
                this.data = db;
                MessageUtil.log(MessageUtil.data_enable.replaceAll("%data%", name));
                this.map.clear();
                MailBoxModule module = this.mod_map.get(db);
                if(module!=null) this.mod_map.put(db, module);
                MessageUtil.log(MessageUtil.data_unreg);
                this.createStorage();
                return true;
            }
        }
        return false;
    }
    
    /**
     * 创建各基础存储库
     */
    public void createStorage(){
        if(this.data==null) return;
        this.data.createPlayerDataStorage();
        this.data.createPersonMailStorage();
        MailBox.getMailBox().getMailManager().getSystemMailTypeList().stream().filter(sm -> (sm.autoCreateDatabaseTable())).forEachOrdered(sm -> {
            this.data.createSystemMailStorage(sm);
        });
    }
    
    /**
     * 通过UUID获取玩家数据
     * 若玩家数据不存在则创建新数据
     * 若玩家名与数据库已存玩家名不同则更新数据
     * @param p 玩家
     * @return PlayerData
     */
    public PlayerData getPlayerData(Player p){
        return this.data==null?null:this.data.getPlayerData(p);
    }
    
    /**
     * 获取全部玩家数据
     * @return List
     */
    public List<PlayerData> getAllPlayerData(){
        return this.data==null?new ArrayList():this.data.getAllPlayerData();
    }
    
    /**
     * 更新玩家数据
     * 若玩家数据不存在则插入数据
     * @param pd PlayerData
     * @return PlayerData
     */
    public boolean updatePlayerData(PlayerData pd){
        return this.data==null?false:this.data.updatePlayerData(pd);
    }
    
    /**
     * 获取个人邮件数量
     * @param p 玩家
     * @return Long
     */
    public long getPersonMailCount(Player p){
        return (this.data==null || p==null)?0:this.data.getPersonMailCount(p);
    }
    
    /**
     * 获取未领取附件的个人邮件数量
     * @param p 玩家
     * @return Long
     */
    public long getNotReceivedPersonMailCount(Player p){
        return (this.data==null || p==null)?0:this.data.getNotReceivedPersonMailCount(p);
    }
    
    /**
     * 获取个人邮件列表
     * @param p 玩家
     * @return List
     */
    public List<PersonMail> getPersonMailList(Player p){
        return this.data==null?new ArrayList():this.data.getPersonMail(p);
    }
    
    /**
     * 以特定id和type获取个人邮件
     * 不存在返回null
     * @param p 玩家
     * @param id id
     * @param type 邮件类型
     * @return PersonMail
     */
    public PersonMail getPersonMail(Player p, long id, String type){
        return this.data==null?null:this.data.getPersonMail(p, id, type);
    }
    
    /**
     * 获取固定数量的个人邮件列表
     * @param p 玩家
     * @param count 每页个数
     * @param page 页数
     * @return List
     */
    public List<PersonMail> getPersonMailList(Player p, int count, int page){
        return (this.data==null || page==0)?new ArrayList():this.data.getPersonMail(p, count, page);
    }
    
    /**
     * 发送一封个人邮件
     * @param pm 个人邮件
     * @param p 玩家
     * @return boolean
     */
    public boolean sendPersonMail(PersonMail pm, Player p){
        return this.data==null?false:this.data.sendPersonMail(pm, p);
    }
    
    /**
     * 领取一封个人邮件
     * @param pm 个人邮件
     * @param p 玩家
     * @return boolean
     */
    public boolean receivePersonMail(PersonMail pm, Player p){
        return this.data==null?false:this.data.receivePersonMail(pm, p);
    }
    
    /**
     * 删除一封个人邮件
     * @param pm 个人邮件
     * @return boolean
     */
    public boolean deletePersonMail(PersonMail pm){
        return this.data==null?false:this.data.deletePersonMail(pm);
    }
    
    /**
     * 清空玩家已读/已领取邮件
     * @param p 玩家
     * @return long
     */
    public long clearPersonReceivedMail(Player p){
        return this.data==null?0:this.data.clearPersonReceivedMail(p);
    }
    
    /**
     * 创建系统邮件存储库
     * @param sm 系统邮件
     */
    public void createSystemMailStorage(SystemMail sm){
        if(this.data==null) return;
        this.data.createSystemMailStorage(sm);
    }
    
    /**
     * 获取系统邮件最大ID
     * @param sm 系统邮件
     * @return long
     */
    public long getSystemMailMax(SystemMail sm){
        return this.data==null?0:this.data.getSystemMailMax(sm);
    }
    
    /**
     * 获取特定id的系统邮件列表
     * 没有返回null
     * @param sm 系统邮件
     * @param id id
     * @return SystemMail
     */
    public SystemMail getSystemMail(SystemMail sm, long id){
        return (this.data==null || sm==null)?null:this.data.getSystemMail(sm, id);
    }
    
    /**
     * 获取系统邮件数量
     * @param sm 系统邮件
     * @return Long
     */
    public long getSystemMailCount(SystemMail sm){
        return (this.data==null || sm==null)?0:this.data.getSystemMailCount(sm);
    }
    
    /**
     * 获取id从min到max的邮件集合
     * @param sm 系统邮件实例
     * @param min 最小值
     * @param max 最大值
     * @return Map
     */
    public Map<Long, SystemMail> getSystemMail(SystemMail sm, long min, long max){
        return (this.data==null || sm==null)?null:this.data.getSystemMail(sm, min, max);
    }
    
    /**
     * 获取系统邮件列表
     * @param sm 系统邮件实例
     * @return List
     */
    public List<SystemMail> getSystemMailList(SystemMail sm){
        return (this.data==null || sm==null)?new ArrayList():this.data.getSystemMail(sm);
    }
    
    /**
     * 获取固定数量的系统邮件列表
     * @param sm 系统邮件实例
     * @param count 每页个数
     * @param page 页数
     * @return List
     */
    public List<SystemMail> getSystemMailList(SystemMail sm, int count, int page){
        return (this.data==null || sm==null || page==0)?new ArrayList():this.data.getSystemMail(sm, count, page);
    }
    
    /**
     * 发送一封系统邮件
     * 若返回的邮件ID为0则发送失败
     * @param sm 系统邮件
     * @return SystemMail
     */
    public SystemMail sendSystemMail(SystemMail sm){
        return this.data==null?sm:this.data.sendSystemMail(sm);
    }
    
    /**
     * 删除系统邮件
     * @param sm 系统邮件实例
     * @return boolean
     */
    public boolean deleteSystemMail(SystemMail sm){
        return this.data==null?false:this.data.deleteSystemMail(sm);
    }
    
    /**
     * 关闭当前数据源
     */
    public void closeData(){
        if(this.data!=null){
            this.data.close();
            this.data = null;
            this.mod_map.clear();
        }
    }
    
    /**
     * 创建自定义存储库
     * @param cd CustomData
     * @since 3.1.0
     * @return boolean
     */
    public boolean createCustomStorage(CustomData cd){
        return this.data==null?false:this.data.createCustomStorage(cd);
    }
    
    /**
     * 将自定义数据插入存储库
     * @param cd 自定义数据
     * @since 3.1.0
     * @return boolean
     */
    public boolean insertCustomData(CustomData cd){
        return this.data==null?false:this.data.insertCustomData(cd);
    }
    
    /**
     * 按主键将存储库的其他数据更新
     * @param cd 自定义数据
     * @since 3.3.0
     * @return boolean
     */
    public boolean updateCustomDataByPrimaryKey(CustomData cd){
        return this.data==null?false:this.data.updateCustomDataByPrimaryKey(cd);
    }
    
    /**
     * 以特定条件获取自定义数据
     * @param cd 自定义数据实例
     * @param args 条件
     * @since 3.1.0
     * @return List
     */
    public List<CustomData> selectCustomData(CustomData cd, LinkedHashMap<String, Object> args){
        return this.data==null?new ArrayList():this.data.selectCustomData(cd, args);
    }
    
     /**
     * 以特定条件删除自定义数据
     * @param cd 自定义数据实例
     * @param args 条件
     * @since 3.1.0
     * @return List
     */
    public long deleteCustomData(CustomData cd, LinkedHashMap<String, Object> args){
        return this.data==null?0:this.data.deleteCustomData(cd, args);
    }
    
    public static DataManager getDataManager(){
        return manager;
    } 
    
}
