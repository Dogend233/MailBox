package com.tripleying.dogend.mailbox.util;

import com.tripleying.dogend.mailbox.MailBox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 物品堆工具
 * @author Dogend
 */
public class ItemStackUtil {
    
    /**
     * 判断玩家背包是否有足够位置放下物品堆
     * @param p 玩家
     * @param il 物品堆列表
     * @return 还需要的空位
     */
    public static int hasBlank(Player p, List<ItemStack> il){
        int ils = il.size();
        int allAir = 0;
        for(ItemStack it:MailBox.getMCVersion()<1.10 ? p.getInventory().getContents() : p.getInventory().getStorageContents()){
            if(it==null){
                if((allAir++)>=ils){
                    return 0;
                }
            }
        }
        if(allAir<ils){
            int needAir = 0;
            o:for(int i=0;i<ils;i++){
                ItemStack is1 = il.get(i);
                HashMap<Integer, ? extends ItemStack> im = p.getInventory().all(is1.getType());
                if(!im.isEmpty()){
                    Set<Integer> ks = im.keySet();
                    for(Integer k:ks){
                        ItemStack is2 = im.get(k);
                        if(is2.isSimilar(is1) && is2.getAmount()+is1.getAmount()<=is2.getMaxStackSize()){
                            continue o;
                        }
                    }
                }
                needAir++;
            }
            return allAir>=needAir?0:needAir-allAir;
        }else{
            return 0;
        }
    }
    
    /**
     * 判断玩家背包里是否有指定数量的物品
     * @param isl 物品列表
     * @param p 玩家
     * @return 缺失的物品列表
     */
    public static List<ItemStack> hasSendItem(List<ItemStack> isl, Player p){
        List<ItemStack> lackList = new ArrayList();
        for(int i=0;i<isl.size();i++){
            ItemStack is = isl.get(i);
            if(p.getInventory().containsAtLeast(is, is.getAmount())) {
                continue;
            }
            int count = is.getAmount();
            for(int j=0;j<36;j++){
                if(p.getInventory().getItem(j)!=null){
                    ItemStack isc = p.getInventory().getItem(j).clone();
                    if(is.isSimilar(isc)){
                        int amount = isc.getAmount();
                        if(count<=amount){
                            count = 0;
                            break;
                        }else{
                            count -= amount;
                        }
                    }
                }
            }
            if(count!=0){
                ItemStack ic = is.clone();
                ic.setAmount(count);
                lackList.add(ic);
            }
        }
        return lackList;
    }
    
    /**
     * 从玩家背包中移除指定物品
     * @param isl 物品列表
     * @param p 玩家
     * @return 缺失的物品列表
     */
    public static List<ItemStack> removeSendItem(List<ItemStack> isl, Player p){
        boolean success = true;
        ArrayList<Integer> clearList = new ArrayList();
        HashMap<Integer, ItemStack> reduceList = new HashMap();
        List<ItemStack> lackList = new ArrayList();
        for(int i=0;i<isl.size();i++){
            ItemStack is1 = isl.get(i);
            int count = is1.getAmount();
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
                ItemStack ic = is1.clone();
                ic.setAmount(count);
                lackList.add(ic);
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
        }
        return lackList;
    }
    
}
