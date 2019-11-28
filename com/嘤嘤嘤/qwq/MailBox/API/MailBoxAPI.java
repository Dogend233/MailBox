package com.嘤嘤嘤.qwq.MailBox.API;

import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import static com.嘤嘤嘤.qwq.MailBox.GlobalConfig.expiredDay;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import com.嘤嘤嘤.qwq.MailBox.MailBox;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListPlayer;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListPlayerId;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListSystem;
import com.嘤嘤嘤.qwq.MailBox.Utils.DateTime;
import com.嘤嘤嘤.qwq.MailBox.Utils.MD5;
import com.嘤嘤嘤.qwq.MailBox.Utils.SQLManager;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import static org.bukkit.Bukkit.getLogger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MailBoxAPI {
    
    private static Economy economy = null;
    private static String VERSION;
    private static final String DATA_FOLDER = "plugins/VexMailBox";
    
    // 设置插件版本
    public static void setVersion(){
        VERSION = MailBox.getInstance().getDescription().getVersion();
    }
    
    // 获取插件版本
    public static String getVersion(){
        if(VERSION==null) setVersion();
        return VERSION;
    }
    
    // 设置[Vault]
    public static boolean setEconomy(Economy eco){
        economy = eco;
        return economy != null;
    }
    
    // 获取玩家余额
    public static double getEconomyBalance(Player p){
        return economy.getBalance(p);
    }

    // 格式化字符串
    public static String getEconomyFormat(double coin){
        return economy.format(coin);
    }
    
    // 给玩家钱
    public static boolean addEconomy(Player p, double coin){
        EconomyResponse r = economy.depositPlayer(p, coin);
        return r.transactionSuccess();
    }
    
    // 拿玩家钱
    public static boolean reduceEconomy(Player p, double coin){
        EconomyResponse r = economy.withdrawPlayer(p, coin);
        return r.transactionSuccess();
    }
    
    // 获取与该玩家有关的邮件
    public static HashMap<String, ArrayList<Integer>> getRelevantMail(Player p, String type){
        HashMap<String, ArrayList<Integer>> hm = new HashMap();
        String name = p.getName();
        ArrayList<Integer> senderList = new ArrayList();
        ArrayList<Integer> recipientList = new ArrayList();
        switch (type) {
            case "player":
                MailListPlayer.forEach((k, v) -> {
                    if(v.getSender().equals(name)) senderList.add(k);
                    if(v.getRecipient().contains(name)) recipientList.add(k);
                });
                break;
            case "system":
                ArrayList<Integer> collected = SQLManager.get().getCollectedMailList(p, type);
                MailListSystem.forEach((k, v) -> {
                    if(v.getSender().equals(name)) senderList.add(k);
                    if(!collected.contains(k)) recipientList.add(k);
                });
        }
        hm.put("asSender", senderList);
        hm.put("asRecipient", recipientList);
        return hm;
    }
    
    // 设置某玩家领取一封邮件
    public static boolean setCollect(String type, int id, String playername){
        return SQLManager.get().setMailCollect(type, id, playername);
    }
    
    // 发送一封邮件
    public static boolean setSend(String type, int id, String playername, String recipient, String topic, String text, String date, String filename){
        if(id==0){
            return SQLManager.get().sendMail(type, playername, recipient, topic, text, date, filename);
        }else{
            // 修改现有邮件
            return false;
        }
    }
    
    //判断文件是否存在
    public static boolean existFiles(String fileName) throws IOException{
        File f = new File(DATA_FOLDER);
        if(!f.exists())f.mkdir();
        f = new File(DATA_FOLDER, fileName+".yml");
        return f.exists();
    }
    public static boolean existFiles(String fileName, String type) throws IOException{
        File f = new File(DATA_FOLDER);
        if(!f.exists())f.mkdir();
        f = new File(DATA_FOLDER+"/MailFiles/");
        if(!f.exists())f.mkdir();
        f = new File(DATA_FOLDER+"/MailFiles/"+type);
        if(!f.exists())f.mkdir();
        f = new File(DATA_FOLDER+"/MailFiles/"+type, fileName+".yml");
        return f.exists();
    }
    
    // 保存附件文件
    public static boolean saveMailFiles(FileMail fm){
        File f = new File(DATA_FOLDER);
        if(!f.exists())f.mkdir();
        f = new File(DATA_FOLDER+"/MailFiles/");
        if(!f.exists())f.mkdir();
        f = new File(DATA_FOLDER+"/MailFiles/"+fm.getType());
        if(!f.exists())f.mkdir();
        YamlConfiguration mailFiles = new YamlConfiguration();
        f = new File(DATA_FOLDER+"/MailFiles/"+fm.getType(), fm.getFileName()+".yml");
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException ex) {
                return false;
            }
        }
        if(fm.getHasItem()){
            ArrayList<ItemStack> isl = fm.getItemList();
            for(int i=0;i<isl.size();i++){
                mailFiles.set("is."+(i+1), isl.get(i));
            }
        }
        mailFiles.set("cmd.enable", fm.getHasCommand());
        mailFiles.set("cmd.commands", fm.getCommandList());
        mailFiles.set("cmd.descriptions", fm.getCommandDescription());
        mailFiles.set("money.coin", fm.getCoin());
        try {
            mailFiles.save(f);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    // 删除某一封邮件
    public static boolean setDelete(String type, int id){
        return SQLManager.get().deleteMail(type, id);
    }
    
    // 删除某一封邮件的附件文件
    public static boolean setDeleteFile(String type, String fileName){
        File f = new File(DATA_FOLDER+"/MailFiles/"+type, fileName+".yml");
        if(f.exists()){
            return f.delete();
        }else{
            return true;
        }
    }
    
    // 判断附件是否执行指令
    public static boolean hasFileCommands(String type, String fileName){
        YamlConfiguration mailFiles;
        File f = new File(DATA_FOLDER+"/MailFiles/"+type, fileName+".yml");
        if(f.exists()){
            mailFiles = YamlConfiguration.loadConfiguration(f);
            return mailFiles.getBoolean("cmd.enable");
        }else{
            return false;
        }
    }
    
    // 取出附件内的指令描述
    public static List<String> getFileCommandsDescription(String type, String fileName){
        YamlConfiguration mailFiles;
        File f = new File(DATA_FOLDER+"/MailFiles/"+type, fileName+".yml");
        if(f.exists()){
            mailFiles = YamlConfiguration.loadConfiguration(f);
            List<String> cmd_desc = mailFiles.getStringList("cmd.descriptions");
            return cmd_desc;
        }else{
            return null;
        }
    }
    
    // 取出附件内的指令
    public static List<String> getFileCommands(String type, String fileName){
        YamlConfiguration mailFiles;
        File f = new File(DATA_FOLDER+"/MailFiles/"+type, fileName+".yml");
        if(f.exists()){
            mailFiles = YamlConfiguration.loadConfiguration(f);
            List<String> cmd = mailFiles.getStringList("cmd.commands");
            return cmd;
        }else{
            return null;
        }
    }
    
    // 取出附件物品
    public static ArrayList<ItemStack> getFileItems(String type, String fileName){
        YamlConfiguration mailFiles;
        File f = new File(DATA_FOLDER+"/MailFiles/"+type, fileName+".yml");
        if(f.exists()){
            ArrayList<ItemStack> is = new ArrayList();
            mailFiles = YamlConfiguration.loadConfiguration(f);
            for(int i=1;i<6;i++){
                if(mailFiles.contains("is."+i)){
                    is.add(mailFiles.getItemStack("is."+i));
                }
            }
            return is;
        }else{
            return null;
        }
    }
    
    // 取出附件内的钱
    public static double[] getFileMoney(String type, String fileName) {
        double[] t = {0};
        if(GlobalConfig.enVault){
            YamlConfiguration mailFiles;
            File f = new File(DATA_FOLDER+"/MailFiles/"+type, fileName+".yml");
            if(f.exists()){
                mailFiles = YamlConfiguration.loadConfiguration(f);
                if(mailFiles.contains("money.coin")){
                    t[0] = mailFiles.getInt("money.coin");
                }
                return t;
            }else{
                return t;
            }
        }else{
            return t;
        }
    }
    
    // 将手上物品写入itemstack.yml
    public static boolean saveItem(ItemStack is){
        File f = new File(DATA_FOLDER);
        if(!f.exists())f.mkdir();
        YamlConfiguration mailFiles = new YamlConfiguration();
        f = new File(DATA_FOLDER, "itemstack.yml");
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException ex) {
                return false;
            }
        }
        mailFiles.set("itemstack", is);
        try {
            mailFiles.save(f);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    // 取出一封自定义邮件
    public static TextMail getCustomMail(String filename, String type){
        YamlConfiguration mailFiles;
        File f = new File(DATA_FOLDER+"/MailFiles/custom", filename+".yml");
        if(f.exists()){
            mailFiles = YamlConfiguration.loadConfiguration(f);
            if(mailFiles.getBoolean("file")){
                List<String> cl = new ArrayList();
                List<String> cd = new ArrayList();
                ArrayList<ItemStack> is = new ArrayList();
                if(mailFiles.getBoolean("cmd.enable")){
                    cl = mailFiles.getStringList("cmd.commands");
                    cd = mailFiles.getStringList("cmd.descriptions");
                }
                return(new FileMail(
                    type,
                    0,
                    mailFiles.getString("sender"),
                    null,
                    mailFiles.getString("topic"),
                    mailFiles.getString("content"),
                    null,
                    "0",
                    getFileItems("custom", filename),
                    cl,
                    cd,
                    mailFiles.getInt("money.coin")
                ));
            }else{
                return(new TextMail(
                    type,
                    0,
                    mailFiles.getString("sender"),
                    null,
                    mailFiles.getString("topic"),
                    mailFiles.getString("content"),
                    null
                ));
            }
        }else{
            return null;
        }
    }
    
    // 获取该玩家player发件数量
    public static int playerAsSender(Player p){
        if(MailListPlayerId.containsKey(p.getName())){
            return MailListPlayerId.get(p.getName()).get("asSender").size();
        }else{
            return getRelevantMail(p, "player").get("asSender").size();
        }
    }
    
    // 获取该玩家player收件数量
    public static int playerAsRecipient(Player p){
        if(MailListPlayerId.containsKey(p.getName())){
            return MailListPlayerId.get(p.getName()).get("asRecipient").size();
        }else{
            return getRelevantMail(p, "player").get("asRecipient").size();
        }
    }
    
    // 获取该玩家player可以发件的数量
    public static int playerAsSenderAllow(Player p, List<Integer> player_out){
        for(int count:player_out){
            if(p.hasPermission("mailbox.send.player.out."+count)){
                return count;
            }
        }
        return 0;
    }
    
    // 获取该玩家player可以收件的数量
    public static int playerAsRecipientAllow(Player p, List<Integer> player_in){
        for(int count:player_in){
            if(p.hasPermission("mailbox.send.player.in."+count)){
                return count;
            }
        }
        return 0;
    }
    
    // 判断这封邮件是否过期
    public static boolean isExpired(TextMail tm){
        try {
            SimpleDateFormat dd = new SimpleDateFormat("dd");
            String ds = expiredDay; 
            long dt = dd.parse(ds).getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sendTime = tm.getDate();
            String nowTime = DateTime.get("ymdhms");
            long st = df.parse(sendTime).getTime();
            long nt = df.parse(nowTime).getTime();
            return st+dt<=nt;
        } catch (ParseException ex) {
            getLogger().info(ex.getLocalizedMessage());
        }
        return false;
    }
    
    // 生成一个32位MD5码
    public static String getMD5(String type) throws IOException{
        String md5 = MD5.Hex(DateTime.get("ms"));
        while(existFiles(md5, type)){
            md5 = MD5.Hex(DateTime.get("ms"));
        }
        return md5;
    }
    
}
