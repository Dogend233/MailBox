package com.tripleying.dogend.mailbox.util;

import com.google.gson.JsonObject;
import com.tripleying.dogend.mailbox.MailBox;
import java.io.File;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 配置工具
 * @author Dogend
 */
public class ConfigUtil {
    
    /**
     * 检查配置版本
     * @param config 配置名
     * @param yml 配置文件
     */
    public static void checkConfigVersion(String config, YamlConfiguration yml){
        if(yml.contains("version")){
            String version = yml.getString("version");
            Map<String, ConfigUpdatePackage> map = new LinkedHashMap();
            getUpdateMap(config, version, map);
            map.forEach((v,cup) -> {
                if(v.equals(yml.getString("version"))) cup.update(yml);
            });
            if(!version.equals(yml.getString("version"))){
                if(FileUtil.saveYaml(new StringReader(yml.saveToString()), new File(FileUtil.getMailBoxFolder(), config.concat(".yml")))){
                    MessageUtil.log(MessageUtil.config_update.replaceAll("%config%", config).replaceAll("%version%", yml.getString("version")));
                }else{
                    MessageUtil.error(MessageUtil.config_save_error.replaceAll("%config%", config));
                }
            }
        }
    }
    
    /**
     * 获取更新列表
     * @param config 配置名
     * @param version 版本
     * @param map LinkedHashMap
     */
    public static void getUpdateMap(String config, String version, Map<String, ConfigUpdatePackage> map){
        JsonObject json = HTTPUtil.getJson(MailBox.getMailBox().getDescription().getWebsite().concat("/config?config=").concat(config).concat("&version=").concat(version));
        if(json.get("update").getAsBoolean()){
            ConfigUpdatePackage cup = new ConfigUpdatePackage(json);
            if(cup.isAvaliable()){
                map.put(version, cup);
                getUpdateMap(config, cup.getVersion(), map);
            }
        }
    }
    
}
