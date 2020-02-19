package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.LocaleLanguageAPI.LocaleLanguageAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * 物品工具
 * @author Dogend
 */
public class ItemUtil {
    
    // 获取物品名称
    public static String getName(ItemStack is){
        if(GlobalConfig.enLocaleLanguageAPI){
            return LocaleLanguageAPI.getItemName(is);
        }else{
            if(is.getItemMeta().hasDisplayName()){
                return is.getItemMeta().getDisplayName();
            }else{
                return ReflectionUtil.getItemStackName(is);
            }
        }
    }
    
    // 获取玩家可发送的物品数量
    public static int allowPlayerSend(Player p){
        int item = 0;
        for(int i=GlobalConfig.maxItem;i>0;i--){
            if(p.hasPermission("mailbox.send.item."+i)){
                item = i;
                break;
            }
        }
        return item;
    }
    
    // 判断物品是否允许发送
    public static boolean isAllowSend(ItemStack is){
        if(is.hasItemMeta()){
            ItemMeta im = is.getItemMeta();
            if(im.hasLore() && !im.getLore().isEmpty() && im.getLore().stream().anyMatch(l -> l.contains(GlobalConfig.fileBanLore))) return false;
        }
        String id = is.getType().name();
        return GlobalConfig.fileBanId.stream().noneMatch(i -> i.contains(id));
    }
    
    // 导出一个物品到ItemExport
    public static boolean exportItem(ItemStack is, String filename){
        File f = FileUtil.getFile("ItemExport");
        if(!f.exists())f.mkdir();
        YamlConfiguration yml = new YamlConfiguration();
        f = new File(f, filename+".yml");
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException ex) {
                return false;
            }
        }
        yml.set("itemstack", is);
        try {
            yml.save(f);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    // 从ItemExport取出一个物品
    public static ItemStack importItem(String filename){
        File f = FileUtil.getFile("ItemExport/"+filename+".yml");
        if(!f.exists()) return null;
        YamlConfiguration yml = FileUtil.getYaml(f);
        return yml.getItemStack("itemstack");
    }
    
    // 返回ItemExport目录中的文件
    public static List<String> getExportList(){
        File f = FileUtil.getFile("ItemExport");
        if(!f.exists()) return new ArrayList();
        return Arrays.asList(f.list((File dir, String name) -> name.endsWith(".yml")));
    }
    
}
