package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.LocaleLanguageAPI.LocaleLanguageAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.OuterMessage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * 物品工具
 */
public class ItemUtil {

    /**
     * 获取物品名称
     * @param is 物品
     * @return 物品名称
     */
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

    /**
     * 获取玩家可发送的物品堆数量
     * @param p 玩家
     * @return 数量
     */
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
    
    /**
     * 判断物品是否允许发送
     * @param is 物品
     * @return boolean
     */
    public static boolean isAllowSend(ItemStack is){
        if(is.hasItemMeta()){
            ItemMeta im = is.getItemMeta();
            if(im.hasLore() && !im.getLore().isEmpty() && im.getLore().stream().anyMatch(l -> l.contains(GlobalConfig.fileBanLore))) return false;
        }
        String id = is.getType().name();
        return GlobalConfig.fileBanId.stream().noneMatch(i -> i.contains(id));
    }
    
    /**
     * 导出一个物品到ItemExport
     * @param is 物品
     * @param filename 文件名
     * @return boolean
     */
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
    /**
     * 从ItemExport取出一个物品
     * @param filename 文件名
     * @return 物品
     */
    public static ItemStack importItem(String filename){
        File f = FileUtil.getFile("ItemExport/"+filename+".yml");
        if(!f.exists()) return null;
        YamlConfiguration yml = FileUtil.getYaml(f);
        return yml.getItemStack("itemstack");
    }

    /**
     * 返回ItemExport目录中的文件名
     * @return 文件名列表
     */
    public static List<String> getExportList(){
        File f = FileUtil.getFile("ItemExport");
        if(!f.exists()) return new ArrayList();
        return Arrays.asList(f.list((File dir, String name) -> name.endsWith(".yml")));
    }

    /**
     * 判断玩家背包里是否有指定倍数要发送的物品
     * @param isl 物品列表
     * @param p 玩家
     * @param cc 会话
     * @param multiple 倍数
     * @return boolean
     */
    public static boolean hasSendItem(ArrayList<ItemStack> isl, Player p, ConversationContext cc, int multiple){
        for(int i=0;i<isl.size();i++){
            if(!p.getInventory().containsAtLeast(isl.get(i), isl.get(i).getAmount()*multiple)) {
                if(cc==null){
                    p.sendMessage(OuterMessage.itemItemNotEnough.replace("%item%", ItemUtil.getName(isl.get(i))));
                }else{
                    cc.getForWhom().sendRawMessage(OuterMessage.itemItemNotEnough.replace("%item%", ItemUtil.getName(isl.get(i))));
                }
                return false;
            }
        }
        return true;
    }
    
    /**
     * 从玩家背包中移除指定倍数要发送的物品
     * @param isl 物品列表
     * @param p 玩家
     * @param cc 会话
     * @param multiple 倍数
     * @return boolean
     */
    public static boolean removeSendItem(ArrayList<ItemStack> isl, Player p, ConversationContext cc, int multiple){
        if(p.hasPermission("mailbox.admin.send.noconsume.item"))return true;
        boolean success = true;
        ArrayList<Integer> clearList = new ArrayList();
        HashMap<Integer, ItemStack> reduceList = new HashMap();
        String error = "";
        for(int i=0;i<isl.size();i++){
            ItemStack is1 = isl.get(i);
            int count = is1.getAmount()*multiple;
            for(int j=0;j<36;j++){
                if(p.getInventory().getItem(j)!=null){
                    ItemStack is2 = p.getInventory().getItem(j).clone();
                    if(is1.isSimilar(is2)){
                        int amount = is2.getAmount();
                        if(count<=amount){
                            int temp = amount-count;
                            if(temp==0){
                                clearList.add(j);
                            }else{
                                is2.setAmount(temp);
                                reduceList.put(j, is2);
                            }
                            count = 0;
                            break;
                        }else{
                            clearList.add(j);
                            count -= amount;
                        }
                    }
                }
            }
            if(count!=0){
                success = false;
                error += " "+ItemUtil.getName(is1)+"x"+count;
            }
        }
        if(success){
            if(!clearList.isEmpty()){
                clearList.forEach(k -> {
                    p.getInventory().clear(k);
                });
            }
            if(!reduceList.isEmpty()){
                reduceList.forEach((k, v) -> {
                    p.getInventory().setItem(k, v);
                });
            }
        }else{
            if(cc==null){
                p.sendMessage(OuterMessage.itemItemNotEnough.replace("%item%", error));
            }else{
                cc.getForWhom().sendRawMessage(OuterMessage.itemItemNotEnough.replace("%item%", error));
            }
        }
        return success;
    }
    
}
