package com.tripleying.dogend.mailbox.util;

import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 配置文件升级工具包
 * @author Dogend
 */
public class ConfigUpdatePackage{
    
    private final String version;
    private final List<String> removes;
    private final Map<String, Object> adds;
    private final boolean avaliable;
    
    public ConfigUpdatePackage(JsonObject jo){
        this.removes = new LinkedList();
        this.adds = new LinkedHashMap();
        this.version = jo.get("version").getAsString();
        if(jo.has("remove")){
            jo.get("remove").getAsJsonArray().forEach(v -> this.removes.add(v.getAsString()));
        }
        if(jo.has("add")){
            jo.get("add").getAsJsonArray().forEach(ja -> {
                Object value;
                JsonObject jao = ja.getAsJsonObject();
                switch(jao.get("type").getAsString().toLowerCase()){
                    case "int":
                    case "integer":
                        value = jao.get("value").getAsInt();
                        break;
                    case "double":
                        value = jao.get("value").getAsDouble();
                        break;
                    case "bool":
                    case "boolean":
                        value = jao.get("value").getAsBoolean();
                        break;
                    case "str":
                    case "string":
                    default:
                        value = jao.get("value").getAsString();
                        break;
                    case "list":
                        List<String> list = new LinkedList();
                        jao.get("value").getAsJsonArray().forEach(v -> list.add(v.getAsString()));
                        value = list;
                        break;
                    case "yml":
                    case "yaml":
                        YamlConfiguration yml = new YamlConfiguration();
                        try {
                            yml.loadFromString(jao.get("value").getAsString());
                            value = yml.getConfigurationSection(jao.get("root").getAsString());
                        } catch (InvalidConfigurationException ex) {
                            this.avaliable = false;
                            return;
                        }
                        break;

                }
                this.adds.put(jao.get("key").getAsString(), value);
            });
        }
        this.avaliable = true;
    }
    
    /**
     * 升级
     * @param yml 配置文件 
     */
    public void update(YamlConfiguration yml){
        yml.set("version", this.version);
        this.removes.forEach(key -> yml.set(key, null));
        this.adds.forEach((key,value) -> yml.set(key, value));
    }

    public String getVersion() {
        return version;
    }
    
    public boolean isAvaliable(){
        return this.avaliable;
    }
}