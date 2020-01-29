package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Message;
import com.tripleying.qwq.MailBox.Utils.DateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerFileMail extends BaseFileMail implements MailPlayer {

    // 邮件接收者
    private List<String> recipient;
    
    public PlayerFileMail(int id, String sender, String topic, String content, String date, List<String> recipient, String filename) {
        super("player", id, sender, topic, content, date, filename);
        this.recipient = recipient;
    }

    public PlayerFileMail(int id, String sender, String topic, String content, String date, List<String> recipient, String filename, ArrayList<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point) {
        super("player", id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
        this.recipient = recipient;
    }
    
    @Override
    public boolean ExpireValidate() {
        try {
            long deadline = new SimpleDateFormat("dd").parse(GlobalConfig.playerExpiredDay).getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long sendTime = df.parse(getDate()).getTime();
            long now = df.parse(DateTime.get("ymdhms")).getTime();
            return (sendTime+deadline)<=now;
        } catch (ParseException ex) {
            Bukkit.getLogger().info(ex.getLocalizedMessage());
            return false; 
        }
    }
    
    @Override
    public boolean collectValidate(Player p) {
        if(ExpireValidate()){
            p.sendMessage(Message.mailExpire.replace("%para%",""));
            Delete(p);
            return false;
        }
        if(!getRecipient().contains(p.getName())){
            p.sendMessage(Message.playerNoRecipient);
            return false;
        }
        return true;
    }
    
    @Override
    public boolean sendValidate(Player p, ConversationContext cc){
        int out = MailBoxAPI.playerAsSenderAllow(p);
        int outed = MailBoxAPI.playerAsSender(p);
        if((out-outed)<=0){
            p.sendMessage(Message.playerMailOutMax.replace("%type%",Message.getTypeName("player")));
            return false;
        }
        if(recipient.size()>GlobalConfig.playerMultiplayer && !p.hasPermission("mailbox.admin.send.multiplayer")){
            p.sendMessage(Message.playerRecipientMax.replace("%max%", Integer.toString(GlobalConfig.playerMultiplayer)));
            return false;
        }
        String Recipient = getRecipientString();
        if(Recipient.length()>255){
            if(p.hasPermission("mailbox.admin.send.extraplayer")){
                String thisRecipient = Recipient.substring(0, 255);
                thisRecipient = thisRecipient.substring(0, thisRecipient.lastIndexOf(' '));
                String otherRecipient = Recipient.substring(thisRecipient.length()+1);
                setRecipient(Arrays.asList(thisRecipient.split(" ")));
                StringBuilder sb = new StringBuilder();
                List<String> l = new ArrayList();
                for(String name:otherRecipient.split(" ")){
                    if(sb.append(" ").append(name).length()<=255){
                        l.add(name);
                    }else{
                        MailBoxAPI.createBaseFileMail("player", 0, getSender(), l, "", getTopic(), getContent(), getDate(), "", 0, "", false, "", "0",getItemList(),getCommandList(),getCommandDescription(),getCoin(),getPoint()).Send(p, cc);
                        sb.delete(0, sb.length());
                        l.clear();
                        sb.append(" ").append(name);
                        l.add(name);
                    }
                }
                if(!l.isEmpty()) MailBoxAPI.createBaseFileMail("player", 0, getSender(), l, "", getTopic(), getContent(), getDate(), "", 0, "", false, "", "0",getItemList(),getCommandList(),getCommandDescription(),getCoin(),getPoint()).Send(p, cc);
                return true;
            }else{
                p.sendMessage(Message.playerRecipientExceedMax);
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean hasItem(ArrayList<ItemStack> isl, Player p, ConversationContext cc){
        int s = recipient.size();
        for(int i=0;i<isl.size();i++){
            if(!p.getInventory().containsAtLeast(isl.get(i), isl.get(i).getAmount()*s)) {
                if(cc==null){
                    p.sendMessage(Message.itemItemNotEnough.replace("%item%", MailBoxAPI.getItemName(isl.get(i))));
                }else{
                    cc.getForWhom().sendRawMessage(Message.itemItemNotEnough.replace("%item%", MailBoxAPI.getItemName(isl.get(i))));
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
        int s = recipient.size();
        for(int i=0;i<isl.size();i++){
            ItemStack is1 = isl.get(i);
            int count = is1.getAmount()*s;
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
                error += " "+MailBoxAPI.getItemName(is1)+"x"+count;
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
            return getCoin()*recipient.size()+GlobalConfig.vaultExpand+getItemList().size()*GlobalConfig.vaultItem;
        }else{
            return 0;
        }
    }
    
    @Override
    public int getExpandPoint(){
        if(GlobalConfig.enPlayerPoints && (getPoint()!=0 || GlobalConfig.playerPointsExpand!=0 || (isHasItem() && GlobalConfig.playerPointsItem!=0))){
            return getPoint()*recipient.size()+GlobalConfig.playerPointsExpand+getItemList().size()*GlobalConfig.playerPointsItem;
        }else{
            return 0;
        }
    }

    @Override
    public boolean sendData() {
        return MailBoxAPI.setSend("player", getId(), getSender(), getRecipientString(), "", getTopic(), getContent(), getDate(), "", 0, "", false, getFileName());
    }

    @Override
    public BaseMail removeFile() {
        return new PlayerMail(getId(),getSender(),getTopic(),getContent(),getDate(),recipient);
    }

    @Override
    public final void setRecipient(List<String> recipient) {
        this.recipient = recipient;
    }
    
    @Override
    public final List<String> getRecipient(){
        return this.recipient;
    }
    
    @Override
    public final String getRecipientString(){
        String str = "";
        str = recipient.stream().map((n) -> " "+n).reduce(str, String::concat);
        str = str.substring(1);
        return str;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder(super.toString());
        if(!recipient.isEmpty()){
            str.append("§r-收件人：");
            str.append(getRecipientString());
        }
        return str.toString();
    }
    
}
