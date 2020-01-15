package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TimesFileMail extends BaseFileMail implements MailTimes {
    
    private int times;

    public TimesFileMail(int id, String sender, String topic, String content, String date, int times, String filename) {
        super("times", id, sender, topic, content, date, filename);
        this.times = times;
    }
    public TimesFileMail(int id, String sender, String topic, String content, String date, int times, String filename, ArrayList<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super("times", id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
        this.times = times;
    }
    public TimesFileMail(String type, int id, String sender, String topic, String content, String date, int times, String filename) {
        super(type, id, sender, topic, content, date, filename);
        this.times = times;
    }
    public TimesFileMail(String type, int id, String sender, String topic, String content, String date, int times, String filename, ArrayList<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super(type, id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
        this.times = times;
    }
    
    @Override
    public boolean TimesValidate() {
        return times>0;
    }
    
    @Override
    public boolean collectValidate(Player p) {
        if(!TimesValidate()){
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件已被领完，自动删除");
            Delete(p);
            return false;
        }
        return true;
    }

    @Override
    public boolean sendData() {
        return MailBoxAPI.setSend("times", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), "", times, "", false, getFileName());
    }

    @Override
    public int getTimes() {
        return times;
    }
    
    @Override
    public boolean hasItem(ArrayList<ItemStack> isl, Player p, ConversationContext cc){
        for(int i=0;i<isl.size();i++){
            if(!p.getInventory().containsAtLeast(isl.get(i), isl.get(i).getAmount()*times)) {
                if(cc==null){
                    p.sendMessage(GlobalConfig.normal+"[邮件预览]：要发送的第"+(i+1)+"个物品不足");
                }else{
                    cc.getForWhom().sendRawMessage(GlobalConfig.normal+"[邮件预览]：要发送的第"+(i+1)+"个物品不足");
                }
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean removeItem(ArrayList<ItemStack> isl, Player p, ConversationContext cc){
        if(p.hasPermission("mailbox.admin.send.noconsume.item"))return true;
        boolean success = true;
        ArrayList<Integer> clearList = new ArrayList();
        HashMap<Integer, ItemStack> reduceList = new HashMap();
        String error = GlobalConfig.normal+"[邮件预览]：从背包中移除以下物品失败";
        for(int i=0;i<isl.size();i++){
            ItemStack is1 = isl.get(i);
            int count = is1.getAmount()*times;
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
                error += "\n"+(i+1)+"号物品"+"缺少"+count+"个";
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
                p.sendMessage(error);
            }else{
                cc.getForWhom().sendRawMessage(error);
            }
        }
        return success;
    }
    
    @Override
    public double getExpandCoin(){
        if(GlobalConfig.enVault && (getCoin()!=0 || GlobalConfig.vaultExpand!=0 || (isHasItem() && GlobalConfig.vaultItem!=0))){
            return getCoin()*times+GlobalConfig.vaultExpand+getItemList().size()*GlobalConfig.vaultItem;
        }else{
            return 0;
        }
    }
    
    @Override
    public boolean sendValidate(Player p){
        if(times>GlobalConfig.times_count && !p.hasPermission("mailbox.admin.send.check.times")){
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件数量不能大于"+GlobalConfig.times_count);
            return false;
        }
        return true;
    }
    
    @Override
    public int getExpandPoint(){
        if(GlobalConfig.enPlayerPoints && (getPoint()!=0 || GlobalConfig.playerPointsExpand!=0 || (isHasItem() && GlobalConfig.playerPointsItem!=0))){
            return getPoint()*times+GlobalConfig.playerPointsExpand+getItemList().size()*GlobalConfig.playerPointsItem;
        }else{
            return 0;
        }
    }

    @Override
    public void setTimes(int times) {
        this.times = times;
    }
    
    @Override
    public BaseMail removeFile() {
        return new TimesMail(getId(),getSender(),getTopic(),getContent(),getDate(),times);
    }
    
}
