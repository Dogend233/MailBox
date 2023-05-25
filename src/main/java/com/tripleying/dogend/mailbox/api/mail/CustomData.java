package com.tripleying.dogend.mailbox.api.mail;

import com.tripleying.dogend.mailbox.manager.DataManager;
import java.util.LinkedHashMap;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 自定义数据存储库
 * @since 3.1.0
 * @author Dogend
 */
public abstract class CustomData {
    
    /**
     * 数据存储库名
     */
    private final String name;
    
    public CustomData(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    /**
     * 创建存储库
     * (这一步建议监听模块加载完成事件)
     * @return boolean
     */
    public boolean createStorage(){
        return DataManager.getDataManager().createCustomStorage(this);
    }
    
    /**
     * 从yml加载一个CustomData
     * @param yml YamlConfiguratin
     * @return CustomData
     */
    public abstract CustomData loadFromYamlConfiguration(YamlConfiguration yml);
    
    /**
     * 将自定义数据插入存储库
     * @return boolean
     */
    public boolean insertCustomData(){
        return DataManager.getDataManager().insertCustomData(this);
    }
    
    /**
     * 按主键将存储库的其他数据更新
     * @since 3.3.0
     * @return boolean
     */
    public boolean updateCustomDataByPrimaryKey(){
        return DataManager.getDataManager().updateCustomDataByPrimaryKey(this);
    }
    
    /**
    * 以特定条件获取自定义数据
     * @param args 条件
     * @return List
     */ 
    public List<CustomData> selectCustomData(LinkedHashMap<String, Object> args){
        return DataManager.getDataManager().selectCustomData(this, args);
    }
    
    /**
    * 以特定条件删除自定义数据
     * @param args 条件
     * @return long
     */
    public long deleteCustomData(LinkedHashMap<String, Object> args){
        return DataManager.getDataManager().deleteCustomData(this, args);
    }
    
}
