package com.嘤嘤嘤.qwq.MailBox.VexView;

import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.OpenedVexGui;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexImage;
import lk.vexview.gui.components.VexScrollingList;
import lk.vexview.gui.components.VexSlot;
import lk.vexview.gui.components.VexText;
import lk.vexview.gui.components.VexTextField;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MailItemModifyGui extends VexGui{
    
    private static String gui_img;
    private static int gui_x;
    private static int gui_y;
    private static int gui_w;
    private static int gui_h;
    private static int gui_ww;
    private static int gui_hh;
    private static VexButton button_close;
    private static HashMap<String, String[]> button_modify = new  HashMap();
    private static HashMap<String, List<String>> button_hover_modify = new  HashMap();
    private static VexImage slot_image;
    private static int slot_x;
    private static int slot_y;
    private static String field_text_name;
    private static String field_text_lore;
    private static String field_text_export;
    private static String field_text_import;
    private static int[] field;
    private static int list_x;
    private static int list_y;
    private static int list_w;
    private static int list_h;
    private static int list_mh;
    private static int list_sh;
    private static int list_oh;
    private static int lore_y_offset;
    private static int lore_text_x;
    private static int lore_text_y;
    private static double lore_text_size;
    
    private ItemStack is;
    private ItemMeta meta;
    private String name;
    private List<String> lores;
    private String type = "name";
    private int loreLine = 0;
    
    public MailItemModifyGui(Player p, ItemStack is) {
        super(gui_img,gui_x,gui_y,gui_w,gui_h,gui_ww,gui_hh);
        this.is = is;
        meta = is.getItemMeta();
        if(meta.hasDisplayName()){
            name = meta.getDisplayName();
        }else{
            name = field_text_name;
        }
        this.addComponent(getTextField(field,name));
        if(meta.hasLore()){
            lores = meta.getLore();
        }else{
            lores = new ArrayList();
        }
        this.setClosable(false);
        this.addComponent(getConfirmButton(button_modify.get("confirm")));
        this.addComponent(new VexButton(button_modify.get("name")[0],button_modify.get("name")[1],button_modify.get("name")[2],button_modify.get("name")[3],Integer.parseInt(button_modify.get("name")[4]),Integer.parseInt(button_modify.get("name")[5]),Integer.parseInt(button_modify.get("name")[6]),Integer.parseInt(button_modify.get("name")[7]),player -> {
            type = "name";
            OpenedVexGui ovg = VexViewAPI.getPlayerCurrentGui(player);
            ovg.setTextFieldContent(0, name);
        }));
        this.addComponent(new VexButton(button_modify.get("export")[0],button_modify.get("export")[1],button_modify.get("export")[2],button_modify.get("export")[3],Integer.parseInt(button_modify.get("export")[4]),Integer.parseInt(button_modify.get("export")[5]),Integer.parseInt(button_modify.get("export")[6]),Integer.parseInt(button_modify.get("export")[7]),player -> {
            type = "export";
            OpenedVexGui ovg = VexViewAPI.getPlayerCurrentGui(player);
            ovg.setTextFieldContent(0, field_text_export);
        }));
        this.addComponent(new VexButton(button_modify.get("import")[0],button_modify.get("import")[1],button_modify.get("import")[2],button_modify.get("import")[3],Integer.parseInt(button_modify.get("import")[4]),Integer.parseInt(button_modify.get("import")[5]),Integer.parseInt(button_modify.get("import")[6]),Integer.parseInt(button_modify.get("import")[7]),player -> {
            type = "import";
            OpenedVexGui ovg = VexViewAPI.getPlayerCurrentGui(player);
            ovg.setTextFieldContent(0, field_text_import);
        }));
        this.addComponent(new VexButton(button_modify.get("list")[0],button_modify.get("list")[1],button_modify.get("list")[2],button_modify.get("list")[3],Integer.parseInt(button_modify.get("list")[4]),Integer.parseInt(button_modify.get("list")[5]),Integer.parseInt(button_modify.get("list")[6]),Integer.parseInt(button_modify.get("list")[7]),player -> {
            player.performCommand("mb item list");
        }));
        this.addComponent(button_close);
        this.addComponent(new VexSlot(0,slot_x,slot_y,is));
        this.addComponent(slot_image);
        this.addComponent(getLoreList(p));
    }
    
    public static void setItemModifyConfig(
        String gui_img,
        int gui_x,
        int gui_y,
        int gui_w,
        int gui_h,
        int gui_ww,
        int gui_hh,
        String button_confirm_id,
        String button_confirm_text,
        String button_confirm_img_1,
        String button_confirm_img_2,
        int button_confirm_x,
        int button_confirm_y,
        int button_confirm_w,
        int button_confirm_h,
        String button_name_id,
        String button_name_text,
        String button_name_img_1,
        String button_name_img_2,
        int button_name_x,
        int button_name_y,
        int button_name_w,
        int button_name_h,
        String button_export_id,
        String button_export_text,
        String button_export_img_1,
        String button_export_img_2,
        int button_export_x,
        int button_export_y,
        int button_export_w,
        int button_export_h,
        String button_import_id,
        String button_import_text,
        String button_import_img_1,
        String button_import_img_2,
        int button_import_x,
        int button_import_y,
        int button_import_w,
        int button_import_h,
        String button_list_id,
        String button_list_text,
        String button_list_img_1,
        String button_list_img_2,
        int button_list_x,
        int button_list_y,
        int button_list_w,
        int button_list_h,
        String button_close_id,
        String button_close_text,
        String button_close_img_1,
        String button_close_img_2,
        int button_close_x,
        int button_close_y,
        int button_close_w,
        int button_close_h,
        String slot_img,
        int slot_w,
        int slot_h,
        int slot_x,
        int slot_y,
        String field_text_name,
        String field_text_lore,
        String field_text_export,
        String field_text_import,
        int field_x,
        int field_y,
        int field_w,
        int field_h,
        int list_x,
        int list_y,
        int list_w,
        int list_h,
        int list_mh,
        int list_sh,
        int list_oh,
        int lore_y_offset,
        int lore_text_x,
        int lore_text_y,
        double lore_text_size,
        String button_add_id,
        String button_add_text,
        List<String> button_add_hover,
        String button_add_img_1,
        String button_add_img_2,
        int button_add_x,
        int button_add_y,
        int button_add_w,
        int button_add_h,
        String button_change_id,
        String button_change_text,
        List<String> button_change_hover,
        String button_change_img_1,
        String button_change_img_2,
        int button_change_x,
        int button_change_y,
        int button_change_w,
        int button_change_h,
        String button_delete_id,
        String button_delete_text,
        List<String> button_delete_hover,
        String button_delete_img_1,
        String button_delete_img_2,
        int button_delete_x,
        int button_delete_y,
        int button_delete_w,
        int button_delete_h
    ){
        // GUI
        MailItemModifyGui.gui_img = gui_img;
        MailItemModifyGui.gui_x = gui_x;
        MailItemModifyGui.gui_y = gui_y;
        MailItemModifyGui.gui_w = gui_w;
        MailItemModifyGui.gui_h = gui_h;
        MailItemModifyGui.gui_ww = gui_ww;
        MailItemModifyGui.gui_hh = gui_hh;
        // 关闭按钮
        button_close = new VexButton(button_close_id,button_close_text,button_close_img_1,button_close_img_2,button_close_x,button_close_y,button_close_w,button_close_h, player -> player.closeInventory());
        // 物品槽
        int x_offset = ((slot_w-18)/2)-1; 
        int y_offset = ((slot_h-18)/2)-1;
        slot_image = new VexImage(slot_img,slot_x+x_offset,slot_y+y_offset,slot_w,slot_h);
        MailItemModifyGui.slot_x = slot_x;
        MailItemModifyGui.slot_y = slot_y;
        // 文本框
        field = new int[]{field_x,field_y,field_w,field_h,255,0, };
        // 物品名称提示文字
        MailItemModifyGui.field_text_name = field_text_name;
        MailItemModifyGui.field_text_lore = field_text_lore;
        MailItemModifyGui.field_text_export = field_text_export;
        MailItemModifyGui.field_text_import = field_text_import;
        // 滚动列表
        MailItemModifyGui.list_x = list_x;
        MailItemModifyGui.list_y = list_y;
        MailItemModifyGui.list_w = list_w;
        MailItemModifyGui.list_h = list_h;
        MailItemModifyGui.list_mh = list_mh;
        MailItemModifyGui.list_sh = list_sh;
        MailItemModifyGui.list_oh = list_oh;
        // Lore文本
        MailItemModifyGui.lore_y_offset = lore_y_offset;
        MailItemModifyGui.lore_text_x = lore_text_x;
        MailItemModifyGui.lore_text_y = lore_text_y;
        MailItemModifyGui.lore_text_size = lore_text_size;
        // 按钮
        button_modify.clear();
        button_hover_modify.clear();
        // 确认按钮
        button_modify.put("confirm", new String[]{button_confirm_id,button_confirm_text,button_confirm_img_1,button_confirm_img_2,Integer.toString(button_confirm_x),Integer.toString(button_confirm_y),Integer.toString(button_confirm_w),Integer.toString(button_confirm_h)});
        // 改名按钮
        button_modify.put("name", new String[]{button_name_id,button_name_text,button_name_img_1,button_name_img_2,Integer.toString(button_name_x),Integer.toString(button_name_y),Integer.toString(button_name_w),Integer.toString(button_name_h)});
        // 导出按钮
        button_modify.put("export", new String[]{button_export_id,button_export_text,button_export_img_1,button_export_img_2,Integer.toString(button_export_x),Integer.toString(button_export_y),Integer.toString(button_export_w),Integer.toString(button_export_h)});
        // 导入按钮
        button_modify.put("import", new String[]{button_import_id,button_import_text,button_import_img_1,button_import_img_2,Integer.toString(button_import_x),Integer.toString(button_import_y),Integer.toString(button_import_w),Integer.toString(button_import_h)});
        // 导出物品列表按钮
        button_modify.put("list", new String[]{button_list_id,button_list_text,button_list_img_1,button_list_img_2,Integer.toString(button_list_x),Integer.toString(button_list_y),Integer.toString(button_list_w),Integer.toString(button_list_h)});
        // 添加按钮
        button_modify.put("add", new String[]{button_add_id,button_add_text,button_add_img_1,button_add_img_2,Integer.toString(button_add_x),Integer.toString(button_add_y),Integer.toString(button_add_w),Integer.toString(button_add_h)});
        button_hover_modify.put("add", button_add_hover);
        // 修改按钮
        button_modify.put("change", new String[]{button_change_id,button_change_text,button_change_img_1,button_change_img_2,Integer.toString(button_change_x),Integer.toString(button_change_y),Integer.toString(button_change_w),Integer.toString(button_change_h)});
        button_hover_modify.put("change", button_change_hover);
        // 删除按钮
        button_modify.put("delete", new String[]{button_delete_id,button_delete_text,button_delete_img_1,button_delete_img_2,Integer.toString(button_delete_x),Integer.toString(button_delete_y),Integer.toString(button_delete_w),Integer.toString(button_delete_h)});
        button_hover_modify.put("delete", button_delete_hover);
    }
    
    // 获取Lore列表
    private VexScrollingList getLoreList(Player p){
        int mh = (lores.size()+1)*list_sh+list_oh;
        if(mh<list_mh) mh = list_mh;
        VexScrollingList vsl = new VexScrollingList(list_x,list_y,list_w,list_h,mh);
        int count = 0;
        for(String lore:lores){
            final int line = count;
            VexButton add = new VexButton(button_modify.get("add")[0]+"_"+(line+1),button_modify.get("add")[1],button_modify.get("add")[2],button_modify.get("add")[3],Integer.parseInt(button_modify.get("add")[4]),lore_y_offset*count+Integer.parseInt(button_modify.get("add")[5]),Integer.parseInt(button_modify.get("add")[6]),Integer.parseInt(button_modify.get("add")[7]),player -> {
                type = "add";
                loreLine = line;
                OpenedVexGui ovg = VexViewAPI.getPlayerCurrentGui(player);
                ovg.setTextFieldContent(0, field_text_lore);
            });
            if(!GlobalConfig.lowVexView_2_4 && !button_hover_modify.get("add").isEmpty()) VexViewConfig.setHover(add, button_hover_modify.get("add"));
            VexButton cha = new VexButton(button_modify.get("change")[0]+"_"+(line+1),button_modify.get("change")[1],button_modify.get("change")[2],button_modify.get("change")[3],Integer.parseInt(button_modify.get("change")[4]),lore_y_offset*count+Integer.parseInt(button_modify.get("change")[5]),Integer.parseInt(button_modify.get("change")[6]),Integer.parseInt(button_modify.get("change")[7]),player -> {
                type = "change";
                loreLine = line;
                OpenedVexGui ovg = VexViewAPI.getPlayerCurrentGui(player);
                ovg.setTextFieldContent(0, lore);
            });
            if(!GlobalConfig.lowVexView_2_4 && !button_hover_modify.get("change").isEmpty()) VexViewConfig.setHover(cha, button_hover_modify.get("change"));
            VexButton del = new VexButton(button_modify.get("delete")[0]+"_"+(line+1),button_modify.get("delete")[1],button_modify.get("delete")[2],button_modify.get("delete")[3],Integer.parseInt(button_modify.get("delete")[4]),lore_y_offset*count+Integer.parseInt(button_modify.get("delete")[5]),Integer.parseInt(button_modify.get("delete")[6]),Integer.parseInt(button_modify.get("delete")[7]),player -> {
                lores.remove(line);
                meta.setLore(lores);
                is.setItemMeta(meta);
                VexViewAPI.openGui(p, new MailItemModifyGui(p, is));
            });
            if(!GlobalConfig.lowVexView_2_4 && !button_hover_modify.get("delete").isEmpty()) VexViewConfig.setHover(del, button_hover_modify.get("delete"));
            vsl.addComponent(add);
            vsl.addComponent(cha);
            vsl.addComponent(del);
            vsl.addComponent(new VexText(lore_text_x,lore_y_offset*(count++)+lore_text_y,Arrays.asList(lore),lore_text_size));
        }
        final int line = count;
        VexButton add = new VexButton(button_modify.get("add")[0]+"_"+(count+1),button_modify.get("add")[1],button_modify.get("add")[2],button_modify.get("add")[3],Integer.parseInt(button_modify.get("add")[4]),lore_y_offset*count+Integer.parseInt(button_modify.get("add")[5]),Integer.parseInt(button_modify.get("add")[6]),Integer.parseInt(button_modify.get("add")[7]),player -> {
            type = "add";
            loreLine = line;
            OpenedVexGui ovg = VexViewAPI.getPlayerCurrentGui(player);
            ovg.setTextFieldContent(0, field_text_lore);
        });
        if(!GlobalConfig.lowVexView_2_4 && !button_hover_modify.get("add").isEmpty()) VexViewConfig.setHover(add, button_hover_modify.get("add"));
        vsl.addComponent(add);
        return vsl;
    }
    
    // 获取文本框
    private VexTextField getTextField(int[] f, String text){
        return new VexTextField(f[0],f[1],f[2],f[3],f[4],f[5],text);
    }
    
    // 确认按钮
    private VexButton getConfirmButton(String[] b){
        VexButton confirm = new VexButton(b[0],b[1],b[2],b[3],Integer.parseInt(b[4]),Integer.parseInt(b[5]),Integer.parseInt(b[6]),Integer.parseInt(b[7]),player -> {
            String l;
            switch (type){
                case "name":
                    l = getTextField(0).getTypedText();
                    if(l.trim().equals("")){
                        player.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"名字不能为空!");
                    }else{
                        meta.setDisplayName(l.replace('&', '§'));
                        is.setItemMeta(meta);
                        VexViewAPI.openGui(player, new MailItemModifyGui(player, is));
                    }   break;
                case "add":
                    l = getTextField(0).getTypedText();
                    if(l.trim().equals("")){
                        player.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"Lore不能为空!");
                    }else{
                        lores.add(loreLine, l.replace('&', '§'));
                        meta.setLore(lores);
                        is.setItemMeta(meta);
                        VexViewAPI.openGui(player, new MailItemModifyGui(player, is));
                    }   break;
                case "change":
                    l = getTextField(0).getTypedText();
                    if(l.trim().equals("")){
                        player.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"Lore不能为空!");
                    }else{
                        lores.set(loreLine, l.replace('&', '§'));
                        meta.setLore(lores);
                        is.setItemMeta(meta);
                        VexViewAPI.openGui(player, new MailItemModifyGui(player, is));
                    }   break;
                case "export":
                    l = getTextField(0).getTypedText();
                    if(l.endsWith(".yml")) l = l.substring(0, l.length()-4);
                    if(l.trim().equals("")){
                        player.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"文件名不能为空!");
                    }else{
                        if(MailBoxAPI.saveItem(is, l)){
                            player.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"物品导出至"+l+".yml成功");
                            player.closeInventory();
                        }else{
                            player.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"物品导出失败");
                        }
                    }   break;
                case "import":
                    l = getTextField(0).getTypedText();
                    if(l.endsWith(".yml")) l = l.substring(0, l.length()-4);
                    if(l.trim().equals("")){
                        player.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"文件名不能为空!");
                    }else{
                        ItemStack item = MailBoxAPI.readItem(l);
                        if(item==null){
                            player.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"物品导入失败");
                        }else{
                            player.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"物品"+l+".yml导入成功");
                            VexViewAPI.openGui(player, new MailItemModifyGui(player, item));
                        }
                    }   break;
            }
        });
        return confirm;
    }
    
    // 打开物品修改GUI
    public static void openItemModifyGui(Player p) {
        if(p.hasPermission("mailbox.admin.item")){
            ItemStack is;
            if(GlobalConfig.lowServer1_9) is = p.getInventory().getItemInHand();
            else is = p.getInventory().getItemInMainHand();
            if(is.getType().equals(Material.AIR)){
                p.performCommand("mb item list");
            }else{
                VexViewAPI.openGui(p, new MailItemModifyGui(p, is));
            }
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你没有执行此指令的权限");
        }
    }
    public static void openItemModifyGui(Player p, ItemStack is) {
        if(is.getType().equals(Material.AIR)){
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"该物品为空");
        }else{
            VexViewAPI.openGui(p, new MailItemModifyGui(p, is));
        }
    }
    
}
