package com.tripleying.qwq.MailBox.VexView;

import lk.vexview.api.VexViewAPI;
import lk.vexview.hud.VexImageShow;
import org.bukkit.entity.Player;

public class MailTipsHud extends VexImageShow {
    
    public static String id;
    private static String img;
    private static int x;
    private static int y;
    private static int w;
    private static int h;
    private static int ww;
    private static int hh;
    private static int time;
    
    public MailTipsHud() {
        super(id,img,x,y,w,h,ww,hh,time);
    }
    
    public static void setHudConfig(String id, String img, int x, int y, int w, int h, int ww, int hh, int time){
        MailTipsHud.id = id;
        MailTipsHud.img = img;
        MailTipsHud.x = x;
        MailTipsHud.y = y;
        MailTipsHud.w = w;
        MailTipsHud.h = h;
        MailTipsHud.ww = ww;
        MailTipsHud.hh = hh;
        MailTipsHud.time = time;
    }
    
    public static void setMailTipsHud(Player p){
        VexViewAPI.sendHUD(p, new MailTipsHud());
    }
    
}
