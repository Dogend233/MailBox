package com.tripleying.dogend.mailbox.api.data;

import com.tripleying.dogend.mailbox.api.mail.CustomData;
import com.tripleying.dogend.mailbox.api.mail.PersonMail;
import com.tripleying.dogend.mailbox.api.mail.PlayerData;
import com.tripleying.dogend.mailbox.api.mail.SystemMail;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

/**
 * 基础数据父类
 * @author Dogend
 */
public interface BaseData {
    
    /**
     * 获取数据源类型
     * @return String
     */
    public String getType();
    
    /**
     * 启用数据源
     * @return boolean
     */
    public boolean enable();
    
    /**
     * 关闭数据源
     */
    public void close();
    
    /**
     * 创建玩家数据存储库
     * @return boolean
     */
    public boolean createPlayerDataStorage();
    
    /**
     * 通过UUID获取玩家数据
     * 若玩家数据不存在则创建新数据
     * 若玩家名与数据库已存玩家名不同则更新数据
     * @param p 玩家
     * @return PlayerData
     */
    public PlayerData getPlayerData(Player p);
    
    /**
     * 获取全部玩家数据
     * @return List
     */
    public List<PlayerData> getAllPlayerData();
    
    /**
     * 更新玩家数据
     * 若玩家数据不存在则插入数据
     * @param pd PlayerData
     * @return PlayerData
     */
    public boolean updatePlayerData(PlayerData pd);
    
    /**
     * 创建个人邮件存储库
     * @return boolean
     */
    public boolean createPersonMailStorage();
    
    /**
     * 获取个人邮件数量
     * @param p 玩家
     * @return Long
     */
    public long getPersonMailCount(Player p);
    
    /**
     * 获取未领取附件的个人邮件数量
     * @param p 玩家
     * @return Long
     */
    public long getNotReceivedPersonMailCount(Player p);
    
    /**
     * 获取个人邮件列表
     * 拉取时邮件过期会进行删除
     * @param p 玩家
     * @return List
     */
    public List<PersonMail> getPersonMail(Player p);
    
    /**
     * 以特定id和type获取个人邮件
     * 不存在返回null
     * @param p 玩家
     * @param id id
     * @param type 邮件类型
     * @return PersonMail
     */
    public PersonMail getPersonMail(Player p, long id, String type);
    
    /**
     * 获取固定数量的个人邮件列表
     * 拉取时邮件过期会进行删除
     * @param p 玩家
     * @param count 每页个数
     * @param page 页数
     * @return List
     */
    public List<PersonMail> getPersonMail(Player p, int count, int page);
    
    /**
     * 发送一封个人邮件
     * @param pm 个人邮件
     * @param p 玩家
     * @return boolean
     */
    public boolean sendPersonMail(PersonMail pm, Player p);
    
    /**
     * 领取一封个人邮件
     * @param pm 个人邮件
     * @param p 玩家
     * @return boolean
     */
    public boolean receivePersonMail(PersonMail pm, Player p);
    
    /**
     * 删除个人邮件
     * @param pm 个人邮件
     * @return boolean
     */
    public boolean deletePersonMail(PersonMail pm);
    
    /**
     * 清空玩家已领取的邮件
     * @param p 玩家
     * @return long
     */
    public long clearPersonReceivedMail(Player p);
    
    /**
     * 创建系统邮件存储库
     * @param sm 系统邮件实例
     * @return boolean
     */
    public boolean createSystemMailStorage(SystemMail sm);
    
    /**
     * 获取系统邮件最大ID
     * @param sm 系统邮件实例
     * @return long
     */
    public long getSystemMailMax(SystemMail sm);
    
    /**
     * 获取系统邮件数量
     * @param sm 系统邮件
     * @return Long
     */
    public long getSystemMailCount(SystemMail sm);
    
    /**
     * 获取系统邮件列表
     * @param sm 系统邮件实例
     * @return List
     */
    public List<SystemMail> getSystemMail(SystemMail sm);
    
    /**
     * 获取特定id的系统邮件列表
     * 没有返回null
     * @param sm 系统邮件
     * @param id id
     * @return SystemMail
     */
    public SystemMail getSystemMail(SystemMail sm, long id);
    
    /**
     * 获取固定数量的系统邮件列表
     * @param sm 系统邮件实例
     * @param count 每页个数
     * @param page 页数
     * @return List
     */
    public List<SystemMail> getSystemMail(SystemMail sm, int count, int page);
    
    /**
     * 获取id从min到max的邮件集合
     * @param sm 系统邮件实例
     * @param min 最小值
     * @param max 最大值
     * @return Map
     */
    public Map<Long, SystemMail> getSystemMail(SystemMail sm, long min, long max);
    
    /**
     * 发送一封系统邮件
     * 若返回的邮件ID为0则发送失败
     * @param sm 系统邮件
     * @return SystemMail
     */
    public SystemMail sendSystemMail(SystemMail sm);
    
    /**
     * 删除系统邮件
     * @param sm 系统邮件实例
     * @return boolean
     */
    public boolean deleteSystemMail(SystemMail sm);
    
    /**
     * 创建自定义存储库
     * @param cd CustomData
     * @since 3.1.0
     * @return boolean
     */
    public boolean createCustomStorage(CustomData cd);
    
    /**
     * 将自定义数据插入存储库
     * @param cd 自定义数据
     * @since 3.1.0
     * @return boolean
     */
    public boolean insertCustomData(CustomData cd);
    
    /**
     * 按主键将存储库的其他数据更新
     * @param cd 自定义数据
     * @since 3.3.0
     * @return boolean
     */
    public boolean updateCustomDataByPrimaryKey(CustomData cd);
    
    /**
     * 以特定条件获取自定义数据
     * @param cd 自定义数据实例
     * @param args 条件
     * @since 3.1.0
     * @return List
     */
    public List<CustomData> selectCustomData(CustomData cd, LinkedHashMap<String, Object> args);
    
    /**
     * 以特定条件删除自定义数据
     * @param cd 自定义数据实例
     * @param args 条件
     * @since 3.1.0
     * @return List
     */
    public long deleteCustomData(CustomData cd, LinkedHashMap<String, Object> args);
    
}
