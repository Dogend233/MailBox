package com.tripleying.qwq.MailBox.VexView;

import com.tripleying.qwq.MailBox.Utils.ItemUtil;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexScrollingList;
import lk.vexview.gui.components.VexSlot;
import org.bukkit.configuration.file.YamlConfiguration;
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
    
    public static void setItemListConfig(YamlConfiguration itemList){
        // GUI
        gui_img = itemList.getString("gui.img");
        gui_x = itemList.getInt("gui.x");
        gui_y = itemList.getInt("gui.y");
        gui_w = itemList.getInt("gui.w");
        gui_h = itemList.getInt("gui.h");
        gui_ww = itemList.getInt("gui.ww");
        gui_hh = itemList.getInt("gui.hh");
        // 滚动列表
        list_x = itemList.getInt("list.x");
        list_y = itemList.getInt("list.y");
        list_w = itemList.getInt("list.w");
        list_h = itemList.getInt("list.h");
        list_mh = itemList.getInt("list.mh");
        list_sh = itemList.getInt("list.sh");
        list_oh = itemList.getInt("list.oh");
        // 物品槽
        x_offset = ((slot_w-18)/2)-1; 
        y_offset = ((slot_h-18)/2)-1;
        slot_img = itemList.getString("slot.img");
        slot_w = itemList.getInt("slot.w");
        slot_h = itemList.getInt("slot.h");
        slot_c = itemList.getInt("slot.c");
        slot_fx = itemList.getInt("slot.fx");
        slot_fy = itemList.getInt("slot.fy");
        slot_x_offset = itemList.getInt("slot.ox");
        slot_y_offset = itemList.getInt("slot.oy");
    }
    
    // 获取物品列表
    private VexScrollingList getItemList(List<String> ItemList){
        int h = ItemList.size()/slot_c;
        if(ItemList.size()%slot_c!=0) h++;
        int mh = (h)*list_sh+list_oh;
        if(mh<list_mh) mh = list_mh;
        VexScrollingList vsl = new VexScrollingList(list_x,list_y,list_w,list_h,mh);
        int count = 0;
        for(String name:ItemList){
            ItemStack is = ItemUtil.importItem(name.substring(0, name.length()-4));
            ItemStack isc = is.clone();
            isc.setAmount(1);
            int xc = count%slot_c;
            int yc = count/slot_c;
            vsl.addComponent(new VexSlot(count++,xc*slot_x_offset+slot_fx,yc*slot_y_offset+list_y+slot_fy,isc));
            vsl.addComponent(new VexButton("MailBoxItemButton_"+count,"",slot_img,slot_img,xc*slot_x_offset+slot_fx+x_offset,yc*slot_y_offset+slot_fy-y_offset,slot_w,slot_h,player -> {
                if(player.hasPermission("mailbox.admin.item")) player.getInventory().addItem(is);
            }));
        }
        return vsl;
    }
    
    // 打开导出物品列表GUI
    public static void openItemListGui(Player p, List<String> il) {
        VexViewAPI.openGui(p, new MailItemListGui(il));
    }
    
}
