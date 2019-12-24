package com.嘤嘤嘤.qwq.MailBox.VexView;

import lk.vexview.api.VexViewAPI;
import lk.vexview.hud.VexImageShow;
import org.bukkit.entity.Player;

public class MailBoxHud extends VexImageShow{
    
    public static String id;
    private static String img;
    private static int x;
    private static int y;
    private static int w;
    private static int h;
    private static int ww;
    private static int hh;
    
    public static String nid;
    private static String nimg;
    private static int nx;
    private static int ny;
    private static int nw;
    private static int nh;
    private static int nww;
    private static int nhh;
    private static int nt;
    
    public MailBoxHud() {
        super(id,img,x,y,w,h,ww,hh,0);
    }
    
    public MailBoxHud(int t) {
        super(nid,nimg,nx,ny,nw,nh,nww,nhh,nt);
    }
    
    public static void setHudConfig(String id, String img, int x, int y, int w, int h, int ww, int hh, String nid, String nimg, int nx, int ny, int nw, int nh, int nww, int nhh, int nt){
        MailBoxHud.id = id;
        MailBoxHud.img = img;
        MailBoxHud.x = x;
        MailBoxHud.y = y;
        MailBoxHud.w = w;
        MailBoxHud.h = h;
        MailBoxHud.ww = ww;
        MailBoxHud.hh = hh;
        MailBoxHud.nid = nid;
        MailBoxHud.nimg = nimg;
        MailBoxHud.nx = nx;
        MailBoxHud.ny = ny;
        MailBoxHud.nw = nw;
        MailBoxHud.nh = nh;
        MailBoxHud.nww = nww;
        MailBoxHud.nhh = nhh;
        MailBoxHud.nt = nt;
    }
    
    public static void setMailBoxHud(Player p){
        VexViewAPI.sendHUD(p, new MailBoxHud());
    }
    
    public static void setNewMailHud(Player p){
        VexViewAPI.sendHUD(p, new MailBoxHud(nt));
    }
    
}
