package com.嘤嘤嘤.qwq.MailBox.VexView;

import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexScrollingList;
import lk.vexview.gui.components.VexSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MailItemListGui extends VexGui{
    
    private static String gui_img;
    private static int gui_x;
    private static int gui_y;
    private static int gui_w;
    private static int gui_h;
    private static int gui_ww;
    private static int gui_hh;
    private static int list_x;
    private static int list_y;
    private static int list_w;
    private static int list_h;
    private static int list_mh;
    private static int list_sh;
    private static int list_oh;
    private static String slot_img;
    private static int x_offset;
    private static int y_offset;
    private static int slot_w;
    private static int slot_h;
    private static int slot_c;
    private static int slot_fx;
    private static int slot_fy;
    private static int slot_x_offset;
    private static int slot_y_offset;
    
    public MailItemListGui(List<String> il) {
        super(gui_img,gui_x,gui_y,gui_w,gui_h,gui_ww,gui_hh);
        this.addComponent(getItemList(il));
    }
    
    public static void setItemListConfig(
        String gui_img,
        int gui_x,
        int gui_y,
        int gui_w,
        int gui_h,
        int gui_ww,
        int gui_hh,
        int list_x,
        int list_y,
        int list_w,
        int list_h,
        int list_mh,
        int list_sh,
        int list_oh,
        String slot_img,
        int slot_w,
        int slot_h,
        int slot_c,
        int slot_fx,
        int slot_fy,
        int slot_x_offset,
        int slot_y_offset
    ){
        // GUI
        MailItemListGui.gui_img = gui_img;
        MailItemListGui.gui_x = gui_x;
        MailItemListGui.gui_y = gui_y;
        MailItemListGui.gui_w = gui_w;
        MailItemListGui.gui_h = gui_h;
        MailItemListGui.gui_ww = gui_ww;
        MailItemListGui.gui_hh = gui_hh;
        // 滚动列表
        MailItemListGui.list_x = list_x;
        MailItemListGui.list_y = list_y;
        MailItemListGui.list_w = list_w;
        MailItemListGui.list_h = list_h;
        MailItemListGui.list_mh = list_mh;
        MailItemListGui.list_sh = list_sh;
        MailItemListGui.list_oh = list_oh;
        // 物品槽
        x_offset = ((slot_w-18)/2)-1; 
        y_offset = ((slot_h-18)/2)-1;
        MailItemListGui.slot_img = slot_img;
        MailItemListGui.slot_w = slot_w;
        MailItemListGui.slot_h = slot_h;
        MailItemListGui.slot_c = slot_c;
        MailItemListGui.slot_fx = slot_fx;
        MailItemListGui.slot_fy = slot_fy;
        MailItemListGui.slot_x_offset = slot_x_offset;
        MailItemListGui.slot_y_offset = slot_y_offset;
    }
    
    // 获取Lore列表
    private VexScrollingList getItemList(List<String> ItemList){
        int mh = (ItemList.size()/slot_c)*list_sh+list_oh;
        if(mh<list_mh) mh = list_mh;
        VexScrollingList vsl = new VexScrollingList(list_x,list_y,list_w,list_h,mh);
        int count = 0;
        for(String name:ItemList){
            ItemStack is = MailBoxAPI.readItem(name.substring(0, name.length()-4));
            int xc = count%slot_c;
            int yc = count/slot_c;
            vsl.addComponent(new VexSlot(count,xc*slot_x_offset+slot_fx,yc*slot_y_offset+list_y+slot_fy,is));
            vsl.addComponent(new VexButton("ItemButton_"+count,"",slot_img,slot_img,xc*slot_x_offset+slot_fx+x_offset,yc*slot_y_offset+slot_fy-y_offset,slot_w,slot_h,player -> {
                MailItemModifyGui.openItemModifyGui(player, is);
            }));
            count++;
        }
        return vsl;
    }
    
    // 打开导出物品列表GUI
    public static void openItemListGui(Player p, List<String> il) {
        VexViewAPI.openGui(p, new MailItemListGui(il));
    }
    
}
