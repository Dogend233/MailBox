package com.tripleying.dogend.mailbox.api.mail;

import com.tripleying.dogend.mailbox.MailBox;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * 玩家数据
 * @author Dogend
 */
public class PlayerData {
    
    /**
     * 玩家名
     */
    private final String name;
    /**
     * 玩家UUID
     */
    private final UUID uuid;
    /**
     * 数据
     */
    private final Map<String, Object> data;
    
    public PlayerData(Player player){
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.data = new LinkedHashMap();
    }
    
    public PlayerData(Player player, Map<String, Object> data){
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.data = data;
    }
    
    public PlayerData(YamlConfiguration yml){
        this.name = yml.getString("name");
        this.uuid = UUID.fromString(yml.getString("uuid"));
        this.data = new LinkedHashMap();
        ConfigurationSection data = yml.getConfigurationSection("data");
        for(String key:data.getKeys(false)){
            this.data.put(key, data.get(key));
        }
    }
    
    /**
     * 获取玩家名
     * @return String
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * 获取玩家UUID
     * @return UUID
     */
    public UUID getUUID(){
        return this.uuid;
    }
    
    /**
     * 获取玩家
     * @return Player
     */
    public Player getPlayer(){
        return Bukkit.getPlayer(this.uuid);
    }
    
    /**
     * 获取全部数据
     * @return Map
     */
    public Map<String, Object> getData(){
        return this.data;
    }
    
    /**
     * 设置数据
     * @param type 系统邮件类型
     * @param value 数据值
     */
    public void setData(String type, Object value){
        this.data.put(type, value);
    }
    
    /**
     * 获取数据
     * 若不存在返回null
     * @param type 系统邮件类型
     * @return null
     */
    public Object getData(String type){
        if(this.data.containsKey(type)){
            return this.data.get(type);
        }else{
            return null;
        }
    }
    
    /**
     * 保存数据
     * @return boolean 
     */
    public boolean saveData(){
        return MailBox.getMailBox().getDataManager().getData().updatePlayerData(this);
    }
    
    public YamlConfiguration toYamlConfiguration(){
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("name", this.name);
        yml.set("uuid", this.uuid.toString());
        YamlConfiguration dyml = new YamlConfiguration();
        for(Entry<String, Object> entry:this.data.entrySet()){
            dyml.set(entry.getKey(), entry.getValue());
        }
        yml.set("data", dyml);
        return yml;
    }
    
}
