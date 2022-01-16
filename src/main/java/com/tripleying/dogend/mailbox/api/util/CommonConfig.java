package com.tripleying.dogend.mailbox.api.util;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 公共配置
 * @author Dogend
 */
public class CommonConfig {
    
    public static int expire_day;
    public static boolean auto_update;
    
    public static void init(YamlConfiguration yml){
        expire_day = yml.getInt("expire-day", 30);
        auto_update = yml.getBoolean("auto-update", false);
    }
    
}
