package com.tripleying.qwq.MailBox.VexView;

import lk.vexview.api.VexViewAPI;
import lk.vexview.hud.VexImageShow;
import lk.vexview.hud.VexShow;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MailTipsHud{
    
    private static VexShow vs;
    
    public static void setHudConfig(YamlConfiguration hud){
        vs = new VexImageShow(hud.getString("new.id"),hud.getString("new.img"),hud.getInt("new.x"),hud.getInt("new.y"),1,hud.getInt("new.ww"),hud.getInt("new.hh"),hud.getInt("new.w"),hud.getInt("new.h"),hud.getInt("new.time"));
    }
    
    public static void setMailTipsHud(Player p){
        VexViewAPI.sendHUD(p, vs);
    }
    
}
