package com.tripleying.qwq.MailBox.VexView;

import com.tripleying.qwq.MailBox.GlobalConfig;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.components.VexButton;
import lk.vexview.hud.VexImageShow;
import lk.vexview.hud.VexShow;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MailBoxHud{
    
    public static String id;
    private static VexShow vs;
    
    public static void setHudConfig(YamlConfiguration hud){
        id = hud.getString("hud.id");
        if(GlobalConfig.vexview_under_2_6){
            vs = new VexImageShow(id,hud.getString("hud.img"),hud.getInt("hud.x"),hud.getInt("hud.y"),hud.getInt("hud.w"),hud.getInt("hud.h"),hud.getInt("hud.ww"),hud.getInt("hud.hh"),0);
        }else{
            vs = VexView2_6.createVexButtonShow(id, new VexButton(id,"",hud.getString("hud.img"),hud.getString("hud.img"),hud.getInt("hud.x"),hud.getInt("hud.y"),hud.getInt("hud.ww"),hud.getInt("hud.hh"),p -> p.performCommand("mailbox")));
        }
    }
    
    public static void setMailBoxHud(Player p){
        VexViewAPI.sendHUD(p, vs);
    }
    
}
