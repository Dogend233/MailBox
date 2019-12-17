package com.嘤嘤嘤.qwq.MailBox.API;

import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import com.嘤嘤嘤.qwq.MailBox.MailBox;
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
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MailBoxAPI {
    
    private static Economy economy = null;
    private static PlayerPoints points = null;
    private static String VERSION;
    private static final String DATA_FOLDER = "plugins/MailBox";
    
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
    
    // 获取玩家[Vault]余额
    public static double getEconomyBalance(Player p){
        return economy.getBalance(p);
    }

    // 格式化[Vault]字符串
    /*public static String getEconomyFormat(double coin){
        return economy.format(coin);
    }*/
    
    // 给玩家[Vault]的钱
    public static boolean addEconomy(Player p, double coin){
        EconomyResponse r = economy.depositPlayer(p, coin);
        return r.transactionSuccess();
    }
    
    // 拿玩家[Vault]的钱
    public static boolean reduceEconomy(Player p, double coin){
        EconomyResponse r = economy.withdrawPlayer(p, coin);
        return r.transactionSuccess();
    }
    
    // 设置[PlayerPoint]
    public static boolean setPoints(PlayerPoints p){
        points = p;
        return points != null;
    }
    
    // 获取玩家[PlayerPoint]余额
    public static int getPoints(Player p){
        return points.getAPI().look(p.getUniqueId());
    }
    
    // 给玩家[PlayerPoint]的钱
    public static boolean addPoints(Player p, int point){
        return points.getAPI().give(p.getUniqueId(), point);
    }
    
    // 拿玩家[PlayerPoint]的钱
    public static boolean reducePoints(Player p, int point){
        return points.getAPI().take(p.getUniqueId(), point);
    }
    
    // 获取与该玩家有关的邮件
    public static HashMap<String, ArrayList<Integer>> getRelevantMail(Player p, String type){
        HashMap<String, ArrayList<Integer>> hm = new HashMap();
        String name = p.getName();
        ArrayList<Integer> senderList = new ArrayList();
        ArrayList<Integer> recipientList = new ArrayList();
        switch (type) {
            case "player":
                MailBox.MailListPlayer.forEach((k, v) -> {
                    if(v.getSender().equals(name)) senderList.add(k);
                    if(v.getRecipient().contains(name)) recipientList.add(k);
                });
                break;
            case "system":
                ArrayList<Integer> collectedSystem = SQLManager.get().getCollectedMailList(p, type);
                MailBox.MailListSystem.forEach((k, v) -> {
                    if(v.getSender().equals(name)) senderList.add(k);
                    if(!collectedSystem.contains(k)) recipientList.add(k);
                });
                break;
            case "permission":
                ArrayList<Integer> collectedPermission = SQLManager.get().getCollectedMailList(p, type);
                MailBox.MailListPermission.forEach((k, v) -> {
                    if(v.getSender().equals(name)) senderList.add(k);
                    if(p.hasPermission(v.getPermission()) && !collectedPermission.contains(k)) recipientList.add(k);
                });
                break;
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
    public static boolean setSend(String type, int id, String playername, String recipient, String permission, String topic, String text, String date, String filename){
        if(id==0){
            return SQLManager.get().sendMail(type, playername, recipient, permission, topic, text, date, filename);
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
    public static boolean existFilesSQL(String fileName, String type){
        return SQLManager.get().existMailFiles(fileName, type);
    }
    
    // 保存附件文件到数据库
    public static boolean saveMailFilesSQL(FileMail fm){ 
        YamlConfiguration mailFiles = new YamlConfiguration();
        mailFiles.set("type", fm.getType());
        mailFiles.set("cmd.enable", fm.isHasCommand());
        mailFiles.set("cmd.commands", fm.getCommandList());
        mailFiles.set("cmd.descriptions", fm.getCommandDescription());
        int i = 0;
        if(fm.isHasItem()){
            ArrayList<ItemStack> isl = fm.getItemList();
            for(;i<isl.size();i++){
                mailFiles.set("is.is_"+(i+1), isl.get(i));
            }
        }
        mailFiles.set("is.count", i);
        mailFiles.set("money.coin", fm.getCoin());
        mailFiles.set("money.point", fm.getPoint());
        return SQLManager.get().sendMailFiles(fm.getFileName(), mailFiles);
    }
    
    // 从数据库获取附件数据
    public static void getMailFilesSQL(FileMail fm){
        YamlConfiguration mf = SQLManager.get().getMailFiles(fm.getFileName(), fm.getType());
        if(mf!=null){
            if(mf.getBoolean("cmd.enable")){
                fm.setCommandList(mf.getStringList("cmd.commands"));
                fm.setCommandDescription(mf.getStringList("cmd.descriptions"));
            }else{
                List<String> nullList = new ArrayList();
                fm.setCommandList(nullList);
                fm.setCommandDescription(nullList);
            }
            int count = mf.getInt("is.count");
            ArrayList<ItemStack> isl = new ArrayList();
            if(count>0){
                for(int i=0;i<count;i++){
                    ItemStack is = mf.getItemStack("is.is_"+(i+1));
                    isl.add(is);
                }
            }
            fm.setItemList(isl);
            fm.setCoin(mf.getDouble("money.coin"));
            fm.setPoint(mf.getInt("money.point"));
        }
    }
    
    // 保存附件文件到本地
    public static boolean saveMailFilesLocal(FileMail fm){
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
        if(fm.isHasItem()){
            ArrayList<ItemStack> isl = fm.getItemList();
            for(int i=0;i<isl.size();i++){
                mailFiles.set("is."+(i+1), isl.get(i));
            }
        }
        mailFiles.set("cmd.enable", fm.isHasCommand());
        mailFiles.set("cmd.commands", fm.getCommandList());
        mailFiles.set("cmd.descriptions", fm.getCommandDescription());
        mailFiles.set("money.coin", fm.getCoin());
        mailFiles.set("money.point", fm.getPoint());
        try {
            mailFiles.save(f);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    // 从本地获取附件数据
    public static void getMailFilesLocal(FileMail fm){
        YamlConfiguration mf;
        File f = new File(DATA_FOLDER+"/MailFiles/"+fm.getType(), fm.getFileName()+".yml");
        if(f.exists()){
            mf = YamlConfiguration.loadConfiguration(f);
            if(mf.getBoolean("cmd.enable")){
                fm.setCommandList(mf.getStringList("cmd.commands"));
                fm.setCommandDescription(mf.getStringList("cmd.descriptions"));
            }else{
                List<String> nullList = new ArrayList();
                fm.setCommandList(nullList);
                fm.setCommandDescription(nullList);
            }
            ArrayList<ItemStack> isl = new ArrayList();
            for(int i=0;i<GlobalConfig.maxItem;i++){
                if(mf.contains("is."+(i+1))){
                    ItemStack is = mf.getItemStack("is."+(i+1));
                    isl.add(is);
                }
            }
            fm.setItemList(isl);
            fm.setCoin(mf.getDouble("money.coin"));
            fm.setPoint(mf.getInt("money.point"));
        }
    }
    
    //将一封本地附件上传到数据库
    public static boolean uploadFile(String type, String filename){
        FileMail fm = new FileMail(type, 0, "", null, "", "", "", "", "");
        fm.setFileName(filename);
        getMailFilesLocal(fm);
        return saveMailFilesSQL(fm);
    }
    
    //将一个类型的所有本地附件上传到数据库
    public static void uploadFile(CommandSender cs, String type){
        List<String> nl = SQLManager.get().getAllFileName(type);
        int all = nl.size();
        cs.sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+"共有"+all+"封待上传邮件");
        if(all!=0){
            int succ = 0;
            for(String fn : nl){
                if(uploadFile(type,fn)){
                    succ++;
                }else{
                    cs.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"附件名"+fn+"上传失败");
                }
            }
            cs.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"已成功上传"+succ+"封邮件");
            cs.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+(all-succ)+"封邮件上传失败");
        }
    }
    
    //将一封数据库附件下载到本地
    public static boolean downloadFile(String type, String filename){
        FileMail fm = new FileMail(type, 0, "", null, "", "", "", "", "");
        fm.setFileName(filename);
        getMailFilesSQL(fm);
        return saveMailFilesLocal(fm);
    }
    
    //将一个类型的所有数据库附件下载到本地
    public static void downloadFile(CommandSender cs, String type){
        List<String> nl = SQLManager.get().getAllFileName(type);
        int all = nl.size();
        cs.sendMessage(GlobalConfig.normal+GlobalConfig.pluginPrefix+"共有"+all+"封待下载邮件");
        if(all!=0){
            int succ = 0;
            for(String fn : nl){
                if(downloadFile(type,fn)){
                    succ++;
                }else{
                    cs.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"附件名"+fn+"下载失败");
                }
            }
            cs.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"已成功下载"+succ+"封邮件");
            cs.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+(all-succ)+"封邮件下载失败");
        }
    }
    
    // 删除某一封邮件
    public static boolean setDelete(String type, int id){
        return SQLManager.get().deleteMail(type, id);
    }
    
    // 删除某一封邮件的附件文件
    public static boolean setDeleteFile( String fileName, String type){
        File f = new File(DATA_FOLDER+"/MailFiles/"+type, fileName+".yml");
        if(f.exists()){
            return f.delete();
        }else{
            return true;
        }
    }
    public static boolean setDeleteFileSQL(String filename, String type){
        return SQLManager.get().deleteMailFiles(filename, type);
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
    // 将手上物品写入ItemExport文件夹的自定义文件名.yml
    public static boolean saveItem(ItemStack is, String filename){
        File f = new File(DATA_FOLDER+"/ItemExport");
        if(!f.exists())f.mkdir();
        YamlConfiguration mailFiles = new YamlConfiguration();
        f = new File(DATA_FOLDER+"/ItemExport", filename+".yml");
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
    
    // 将itemstack.yml的物品拿到手上
    public static ItemStack readItem(){
        File f = new File(DATA_FOLDER);
        if(!f.exists()) return null;
        f = new File(DATA_FOLDER, "itemstack.yml");
        if(!f.exists()) return null;
        YamlConfiguration mailFiles = YamlConfiguration.loadConfiguration(f);
        return mailFiles.getItemStack("itemstack");
    }
    // 将ItemExport文件夹的自定义文件名.yml的物品拿到手上
    public static ItemStack readItem(String filename){
        File f = new File(DATA_FOLDER);
        if(!f.exists()) return null;
        f = new File(DATA_FOLDER+"/ItemExport", filename+".yml");
        if(!f.exists()) return null;
        YamlConfiguration mailFiles = YamlConfiguration.loadConfiguration(f);
        return mailFiles.getItemStack("itemstack");
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
                double co = 0;
                int po = 0;
                if(mailFiles.getBoolean("cmd.enable")){
                    cl = mailFiles.getStringList("cmd.commands");
                    cd = mailFiles.getStringList("cmd.descriptions");
                }
                for(int i=0;i<GlobalConfig.maxItem;i++){
                    if(mailFiles.contains("is."+(i+1))){
                        ItemStack s = mailFiles.getItemStack("is."+(i+1));
                        is.add(s);
                    }
                }
                if(GlobalConfig.enVault && mailFiles.contains("money.coin")) co = mailFiles.getDouble("money.coin");
                if(GlobalConfig.enPlayerPoints && mailFiles.contains("money.point")) po = mailFiles.getInt("money.point");
                return(new FileMail(
                    type,
                    0,
                    mailFiles.getString("sender"),
                    null,
                    null,
                    mailFiles.getString("topic"),
                    mailFiles.getString("content"),
                    null,
                    "0",
                    is,
                    cl,
                    cd,
                    co,
                    po
                ));
            }else{
                return(new TextMail(
                    type,
                    0,
                    mailFiles.getString("sender"),
                    null,
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
        MailBox.updateRelevantMailList(p, "player");
        return MailBox.getRelevantMailList(p, "player").get("asSender").size();
    }
    
    // 获取该玩家player可以发件的数量
    public static int playerAsSenderAllow(Player p){
        for(int count : GlobalConfig.player_out){
            if(p.hasPermission("mailbox.send.player.out."+count)){
                return count;
            }
        }
        return 0;
    }
    
    // 获取该玩家可发送的物品数量
    public static int playerSendItemAllow(Player p){
        int item = 0;
        for(int i=GlobalConfig.maxItem;i>0;i--){
            if(p.hasPermission("mailbox.send.item."+i)){
                item = i;
                break;
            }
        }
        return item;
    }
    
    // 判断这封邮件是否过期
    public static boolean isExpired(TextMail tm){
        try {
            SimpleDateFormat dd = new SimpleDateFormat("dd");
            String ds = GlobalConfig.expiredDay; 
            long dt = dd.parse(ds).getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sendTime = tm.getDate();
            String nowTime = DateTime.get("ymdhms");
            long st = df.parse(sendTime).getTime();
            long nt = df.parse(nowTime).getTime();
            return st+dt<=nt;
        } catch (ParseException ex) {
            Bukkit.getLogger().info(ex.getLocalizedMessage());
        }
        return false;
    }
    
    // 判断物品是否允许发送(不含有禁止发送的Lore)
    public static boolean isAllowSend(ItemStack is){
        if(is.hasItemMeta()){
            ItemMeta im = is.getItemMeta();
            if(im.hasLore()){
                List<String> lore = im.getLore();
                for(String l:lore){
                    if(l.contains(GlobalConfig.fileBanLore))
                        return false;
                }
            }
        }
        String id = is.getType().name();
        for(String i:GlobalConfig.fileBanId){
            if(i.equals(id)) return false;
        }
        return true;
    }
    
    // 生成一个32位MD5码
    public static String getMD5(String type) throws IOException{
        String md5 = MD5.Hex(DateTime.get("ms"));
        while(existFiles(md5, type) || existFilesSQL(md5, type)){
            md5 = MD5.Hex(DateTime.get("ms"));
        }
        return md5;
    }
    
}
