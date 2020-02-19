package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Message;
import com.tripleying.qwq.MailBox.Utils.ItemUtil;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import com.tripleying.qwq.MailBox.Utils.TimeUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
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
    public boolean ExpireValidate() {
        try {
            long deadline = new SimpleDateFormat("HH").parse(GlobalConfig.timesExpired).getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long sendTime = df.parse(getDate()).getTime();
            long now = df.parse(TimeUtil.get("ymdhms")).getTime();
            return (sendTime+deadline)<=now;
        } catch (ParseException ex) {
            Bukkit.getLogger().info(ex.getLocalizedMessage());
            return false; 
        }
    }
    
    @Override
    public boolean TimesValidate() {
        return times>0;
    }
    
    @Override
    public boolean collectValidate(Player p) {
        if(ExpireValidate()){
            p.sendMessage(Message.mailExpire.replace("%para%",""));
            Delete(p);
            return false;
        }
        if(!TimesValidate()){
            p.sendMessage(Message.timesZero.replace("%para%", ""));
            Delete(p);
            return false;
        }
        return true;
    }

    @Override
    public boolean sendData() {
        return MailUtil.setSend("times", getId(), getSender(), "", "", getTopic(), getContent(), getDate(), "", times, "", false, getFileName());
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
                    p.sendMessage(Message.itemItemNotEnough.replace("%item%", ItemUtil.getName(isl.get(i))));
                }else{
                    cc.getForWhom().sendRawMessage(Message.itemItemNotEnough.replace("%item%", ItemUtil.getName(isl.get(i))));
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
        String error = "";
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
                p.sendMessage(Message.itemItemNotEnough.replace("%item%", error));
            }else{
                cc.getForWhom().sendRawMessage(Message.itemItemNotEnough.replace("%item%", error));
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
    public boolean sendValidate(Player p, ConversationContext cc){
        if(times>GlobalConfig.timesCount && !p.hasPermission("mailbox.admin.send.check.times")){
            p.sendMessage(Message.timesSendExceed.replace("%max%", Integer.toString(GlobalConfig.timesCount)));
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
