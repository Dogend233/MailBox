package com.tripleying.qwq.MailBox.API;

import com.tripleying.qwq.LocaleLanguageAPI.LocaleLanguageAPI;
import com.tripleying.qwq.MailBox.ConfigMessage;
import com.tripleying.qwq.MailBox.Mail.*;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.Message;
import com.tripleying.qwq.MailBox.Original.MailNew;
import com.tripleying.qwq.MailBox.Utils.*;
import com.tripleying.qwq.MailBox.VexView.MailTipsHud;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lk.vexview.api.VexViewAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * API
 * @author Dogend
 */
public class MailBoxAPI {
    
    public static long secondDay;
    private static Economy economy = null;
    private static PlayerPoints points = null;
    private static String VERSION;
    private static final String DATA_FOLDER = "plugins/MailBox";
    private static final String[] DEFAULT_LANGUAGE = {"zh_cn"};
    private static final String[] TRUE_MAIL_TYPE = {"keytimes","times","date","system","permission","player","cdkey"};
    private static final String[] SPECIAL_MAIL_TYPE = {"cdkey"};
    private static final String[] VIRTUAL_MAIL_TYPE = {"template","online"};
    
    /**
     * 更新上次操作时间
     */
    public static void updateLastTime(){
        long newTime = System.currentTimeMillis();
        if(newTime>secondDay) MailBox.CDKEY_DAY.clear();
        secondDay = System.currentTimeMillis()/(1000*3600*24)*(1000*3600*24)+24*60*60*1000;
    }
    
    /**
     * 获取插件版本
     * @return 插件版本
     */
    public static String getVersion(){
        if(VERSION==null) VERSION = MailBox.getInstance().getDescription().getVersion();
        return VERSION;
    }
    
    /**
     * 获取插件默认支持的语言
     * @return 语言文件
     */
    public static List<String> getDefaultLanguage(){
        return Arrays.asList(DEFAULT_LANGUAGE);
    }
    
    /**
     * 获取插件包含的真实邮件类型
     * @return 真实邮件类型列表
     */
    public static List<String> getTrueType(){
        return Arrays.asList(TRUE_MAIL_TYPE);
    }
    
    /**
     * 获取插件包含的除特殊邮件类型外的真实邮件类型
     * @return 无特殊邮件类型的真实邮件类型列表
     */
    public static List<String> getTrueTypeWhithoutSpecial(){
        List<String> all = new ArrayList();
        all.addAll(Arrays.asList(TRUE_MAIL_TYPE));
        for(String s:SPECIAL_MAIL_TYPE) all.remove(s);
        return all;
    }
    
    /**
     * 获取插件包含的虚拟邮件类型
     * @return 伪邮件类型列表
     */
    public static List<String> getVirtualType(){
        return Arrays.asList(VIRTUAL_MAIL_TYPE);
    }
    
    /**
     * 获取插件包含的全部邮件类型
     * @return 邮件类型列表
     */
    public static List<String> getAllType(){
        List<String> all = new ArrayList();
        all.addAll(Arrays.asList(TRUE_MAIL_TYPE));
        all.addAll(Arrays.asList(VIRTUAL_MAIL_TYPE));
        return all;
    }
    
    /**
     * 设置[Vault]
     * @param eco Economy实例
     * @return boolean
     */
    public static boolean setEconomy(Economy eco){
        economy = eco;
        return economy != null;
    }
    
    /**
     * 获取玩家[Vault]余额
     * @param p 玩家
     * @return double
     */
    public static double getEconomyBalance(Player p){
        return economy.getBalance(p);
    }
    
    /**
     * 给玩家[Vault]的钱
     * @param p 玩家
     * @param coin 数量
     * @return boolean
     */
    public static boolean addEconomy(Player p, double coin){
        EconomyResponse r = economy.depositPlayer(p, coin);
        return r.transactionSuccess();
    }
    
    /**
     * 拿玩家[Vault]的钱
     * @param p 玩家
     * @param coin 数量
     * @return boolean
     */
    public static boolean reduceEconomy(Player p, double coin){
        EconomyResponse r = economy.withdrawPlayer(p, coin);
        return r.transactionSuccess();
    }

    /**
     * 设置[PlayerPoint]
     * @param p PlayerPoints实例
     * @return boolean
     */
    public static boolean setPoints(PlayerPoints p){
        points = p;
        return points != null;
    }

    /**
     * 获取玩家[PlayerPoint]余额
     * @param p 玩家
     * @return int
     */
    public static int getPoints(Player p){
        return points.getAPI().look(p.getUniqueId());
    }
    
    /**
     * 给玩家[PlayerPoint]的钱
     * @param p 玩家
     * @param point 数量
     * @return boolean
     */
    public static boolean addPoints(Player p, int point){
        return points.getAPI().give(p.getUniqueId(), point);
    }
    
    /**
     * 拿玩家[PlayerPoint]的钱
     * @param p 玩家
     * @param point 数量
     * @return boolean
     */
    public static boolean reducePoints(Player p, int point){
        return points.getAPI().take(p.getUniqueId(), point);
    }
    
    /**
     * 获取与玩家有关的邮件
     * @param p 玩家
     * @param type 邮件类型
     * @return HashMap 作为发送者/接收者 - 邮件id列表
     */
    public static HashMap<String, ArrayList<Integer>> getRelevantMail(Player p, String type){
        HashMap<String, ArrayList<Integer>> hm = new HashMap();
        String name = p.getName();
        ArrayList<Integer> senderList = new ArrayList();
        ArrayList<Integer> recipientList = new ArrayList();
        ArrayList<Integer> deleteList = new ArrayList();
        switch (type) {
            case "player":
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(v.ExpireValidate()){
                        deleteList.add(k);
                    }else{
                        if(v.getSender().equals(name)) senderList.add(k);
                        if(((MailPlayer)v).getRecipient().contains(name)) recipientList.add(k);
                    }
                });
                deleteList.forEach((i) -> MailBox.getMailHashMap(type).get(i).Delete(null));
                break;
            case "system":
                ArrayList<Integer> collectedSystem = SQLManager.get().getCollectedMailList(p, type);
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(v.getSender().equals(name)) senderList.add(k);
                    if(!collectedSystem.contains(k)) recipientList.add(k);
                });
                break;
            case "permission":
                ArrayList<Integer> collectedPermission = SQLManager.get().getCollectedMailList(p, type);
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(v.getSender().equals(name)) senderList.add(k);
                    if(p.hasPermission(((MailPermission)v).getPermission()) && !collectedPermission.contains(k)) recipientList.add(k);
                });
                break;
            case "date":
                ArrayList<Integer> collectedDate = SQLManager.get().getCollectedMailList(p, type);
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(v.isStart() || p.hasPermission("mailbox.admin.see.date")){
                        if(v.ExpireValidate()){
                            deleteList.add(k);
                        }else{
                            if(v.getSender().equals(name)) senderList.add(k);
                            if(!collectedDate.contains(k)) recipientList.add(k);
                        }
                    }else{
                        if(v.ExpireValidate()){
                            deleteList.add(k);
                        }else{
                            if(v.getSender().equals(name)) senderList.add(k);
                        }
                    }
                });
                deleteList.forEach((i) -> MailBox.getMailHashMap(type).get(i).Delete(null));
                break;

            case "times":
                ArrayList<Integer> collectedTimes = SQLManager.get().getCollectedMailList(p, type);
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(v.TimesValidate()){
                        if(v.getSender().equals(name)) senderList.add(k);
                        if(!collectedTimes.contains(k)) recipientList.add(k);
                    }else{
                        deleteList.add(k);
                    }
                });
                deleteList.forEach((i) -> MailBox.getMailHashMap(type).get(i).Delete(null));
                break;
            case "keytimes":
                ArrayList<Integer> collectedKeyTimes = SQLManager.get().getCollectedMailList(p, type);
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(v.TimesValidate()){
                        if(v.getSender().equals(name)) senderList.add(k);
                        if(!collectedKeyTimes.contains(k)) recipientList.add(k);
                    }else{
                        deleteList.add(k);
                    }
                });
                deleteList.forEach((i) -> MailBox.getMailHashMap(type).get(i).Delete(null));
                break;
            case "cdkey":
                ArrayList<Integer> collectedCdkey = SQLManager.get().getCollectedMailList(p, type);
                MailBox.getMailHashMap(type).forEach((k, v) -> {
                    if(v.getSender().equals(name)) senderList.add(k);
                    if(!collectedCdkey.contains(k)) recipientList.add(k);
                });
                break;
        }
        hm.put("asSender", senderList);
        hm.put("asRecipient", recipientList);
        return hm;
    }

    /**
     * 设置某玩家领取一封邮件
     * @param type 邮件类型
     * @param id 邮件id
     * @param playername 玩家名
     * @return boolean
     */
    public static boolean setCollect(String type, int id, String playername){
        return SQLManager.get().setMailCollect(type, id, playername);
    }
    
    /**
     * 发送一封邮件
     * @param type 邮件类型
     * @param id 邮件id
     * @param playername 玩家名
     * @param recipient 收件人列表
     * @param permission 所需权限
     * @param topic 主题
     * @param text 内容
     * @param date 发送日期
     * @param deadline 截止日期
     * @param times 邮件数量
     * @param key 邮件口令
     * @param only 兑换码唯一性
     * @param filename 附件名
     * @return boolean
     */
    public static boolean setSend(String type, int id, String playername,
            String recipient, String permission, String topic, String text, 
            String date, String deadline, int times, String key, boolean only,
            String filename){
        if(id==0){
            return SQLManager.get().sendMail(type, playername, recipient, permission, topic, text, date, deadline, times, key, only, filename);
        }else{
            // 修改现有邮件
            return false;
        }
    }

    /**
     * 返回ItemExport目录中的文件
     * @return 文件名数组
     */
    public static List<String> getItemExport(){
        File f = new File(DATA_FOLDER);
        if(!f.exists()) return new ArrayList();
        f = new File(DATA_FOLDER, "/ItemExport");
        if(!f.exists()) return new ArrayList();
        return Arrays.asList(f.list((File dir, String name) -> name.endsWith(".yml")));
    }

    /**
     * 判断文件是否存在
     * @param fileName 文件名
     * @return boolean
     * @throws IOException 创建文件夹失败
     */
    public static boolean existFiles(String fileName) throws IOException{
        File f = new File(DATA_FOLDER);
        if(!f.exists())f.mkdir();
        f = new File(DATA_FOLDER, fileName+".yml");
        return f.exists();
    }

    /**
     * 判断附件是否存在本地
     * @param fileName 附件名
     * @param type 邮件类型
     * @return boolean
     * @throws IOException 创建文件夹失败
     */
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

    /**
     * 判断附件是否存在数据库
     * @param fileName 附件名
     * @param type 邮件类型
     * @return boolean
     */
    public static boolean existFilesSQL(String fileName, String type){
        return SQLManager.get().existMailFiles(fileName, type);
    }

    /**
     * 保存附件文件到数据库
     * @param fm 附件类型邮件
     * @return boolean
     */
    public static boolean saveMailFilesSQL(BaseFileMail fm){
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("type", fm.getType());
        yaml.set("cmd.enable", fm.isHasCommand());
        yaml.set("cmd.commands", fm.getCommandList());
        yaml.set("cmd.descriptions", fm.getCommandDescription());
        yaml.set("money.coin", fm.getCoin());
        yaml.set("money.point", fm.getPoint());
        YamlConfiguration item = new YamlConfiguration();
        int i = 0;
        if(fm.isHasItem()){
            ArrayList<ItemStack> isl = fm.getItemList();
            for(;i<isl.size();i++){
                item.set("is.is_"+(i+1), isl.get(i));
            }
        }
        item.set("is.count", i);
        String itemString = item.saveToString();
        return SQLManager.get().sendMailFiles(fm.getFileName(), yaml, itemString);
    }

    /**
     * 从数据库获取附件数据
     * @param fm 附件类型邮件
     * @return boolean
     */
    public static boolean getMailFilesSQL(BaseFileMail fm){
        YamlConfiguration mf = SQLManager.get().getMailFiles(fm.getFileName(), fm.getType());
        if(mf==null){
            ArrayList nullList = new ArrayList();
            fm.setCommandList(nullList);
            fm.setCommandDescription(nullList);
            fm.setItemList(nullList);
            fm.setCoin(0);
            fm.setPoint(0);
            return false;
        }else{
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
            return true;
        }
    }

    /**
     * 保存附件文件到本地
     * @param fm 附件类型邮件
     * @return boolean
     */
    public static boolean saveMailFilesLocal(BaseFileMail fm){
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
    
    /**
     * 从本地获取附件数据
     * @param fm 基础附件邮件
     * @return boolean
     */
    public static boolean getMailFilesLocal(BaseFileMail fm){
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
            return true;
        }else{
            ArrayList nullList = new ArrayList();
            fm.setCommandList(nullList);
            fm.setCommandDescription(nullList);
            fm.setItemList(nullList);
            fm.setCoin(0);
            fm.setPoint(0);
            return false;
        }
    }

    /**
     * 将一封本地附件上传到数据库
     * @param type 邮件类型
     * @param filename 附件名
     * @return boolean
     */
    public static boolean uploadFile(String type, String filename){
        BaseFileMail fm = new BaseFileMail(type, 0,"", "", "", "", "");
        fm.setFileName(filename);
        getMailFilesLocal(fm);
        return saveMailFilesSQL(fm);
    }

    /**
     * 将一个类型的所有本地附件上传到数据库
     * @param cs 指令执行者
     * @param type 邮件类型
     */
    public static void uploadFile(CommandSender cs, String type){
        List<String> nl = SQLManager.get().getAllFileName(type);
        int all = nl.size();
        if(all!=0){
            int succ = 0;
            for(String fn : nl){
                if(uploadFile(type,fn)){
                    succ++;
                }else{
                    cs.sendMessage(Message.fileSuccess.replace("%file%", fn).replace("%state%", Message.fileUpload));
                }
            }
            cs.sendMessage(Message.fileMulti.replace("%state%", Message.fileUpload).replace("%ok%", Integer.toString(succ)).replace("all", Integer.toString(all)));
        }
    }
    
    /**
     * 将一封数据库附件下载到本地
     * @param type 邮件类型
     * @param filename 附件名
     * @return boolean
     */
    public static boolean downloadFile(String type, String filename){
        BaseFileMail fm = new BaseFileMail(type, 0,"", "", "", "", "");
        fm.setFileName(filename);
        getMailFilesSQL(fm);
        return saveMailFilesLocal(fm);
    }

    /**
     * 将一个类型的所有数据库附件下载到本地
     * @param cs 指令执行者
     * @param type 邮件类型
     */
    public static void downloadFile(CommandSender cs, String type){
        List<String> nl = SQLManager.get().getAllFileName(type);
        int all = nl.size();
        if(all!=0){
            int succ = 0;
            for(String fn : nl){
                if(downloadFile(type,fn)){
                    succ++;
                }else{
                    cs.sendMessage(Message.fileSuccess.replace("%file%", fn).replace("%state%", Message.fileDownload));
                }
            }
            cs.sendMessage(Message.fileMulti.replace("%state%", Message.fileDownload).replace("%ok%", Integer.toString(succ)).replace("all", Integer.toString(all)));

        }
    }

    /**
     * 删除一封邮件
     * @param type 邮件类型
     * @param id 邮件id
     * @return boolean
     */
    public static boolean setDelete(String type, int id){
        return SQLManager.get().deleteMail(type, id);
    }
    
    /**
     * 从本地删除一个附件文件
     * @param fileName 附件名
     * @param type 邮件类型
     * @return boolean
     */
    public static boolean setDeleteFile(String fileName, String type){
        File f = new File(DATA_FOLDER+"/MailFiles/"+type, fileName+".yml");
        if(f.exists()){
            return f.delete();
        }else{
            return true;
        }
    }

    /**
     * 从数据库删除一个附件文件
     * @param filename 附件名
     * @param type 邮件类型
     * @return boolean
     */
    public static boolean setDeleteFileSQL(String filename, String type){
        return SQLManager.get().deleteMailFiles(filename, type);
    }
    
    /**
     * 将物品写入itemstack.yml
     * @param is 物品
     * @return boolean
     */
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

    /**
     * 将写入ItemExport文件夹的自定义文件名.yml
     * @param is 物品
     * @param filename 文件名
     * @return boolean
     */
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

    /**
     * 读取itemstack.yml的物品
     * @return 物品
     */
    public static ItemStack readItem(){
        File f = new File(DATA_FOLDER);
        if(!f.exists()) return null;
        f = new File(DATA_FOLDER, "itemstack.yml");
        if(!f.exists()) return null;
        YamlConfiguration mailFiles = YamlConfiguration.loadConfiguration(f);
        return mailFiles.getItemStack("itemstack");
    }

    /**
     * 读取ItemExport文件夹的自定义文件名.yml的物品
     * @param filename 文件名
     * @return 物品
     */
    public static ItemStack readItem(String filename){
        File f = new File(DATA_FOLDER);
        if(!f.exists()) return null;
        f = new File(DATA_FOLDER+"/ItemExport", filename+".yml");
        if(!f.exists()) return null;
        YamlConfiguration mailFiles = YamlConfiguration.loadConfiguration(f);
        return mailFiles.getItemStack("itemstack");
    }

    /**
     * 模板邮件是否存在
     * @param filename 文件名
     * @return boolean
     */
    public static boolean existTemplate(String filename){
        File f = new File(DATA_FOLDER+"/Template", filename+".yml");
        return f.exists();
    }
    
    /**
     * 将模板转化为邮件
     * @param sender 发送者
     * @param bm 邮件
     */
    public static void template2Mail(CommandSender sender, BaseMail bm){
        MailNew.New(sender,bm);
    }

    /**
     * 取出一封模板邮件
     * @param filename 文件名
     * @return 基础邮件
     */
    public static BaseMail loadTemplateMail(String filename){
        YamlConfiguration mailFiles;
        File f = new File(DATA_FOLDER+"/Template", filename+".yml");
        if(f.exists()){
            mailFiles = YamlConfiguration.loadConfiguration(f);
            String sender = null;
            if(mailFiles.contains("sender") && !mailFiles.getString("sender").trim().equals("")) sender = mailFiles.getString("sender");
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
                return createBaseFileMail(
                    "template",
                    0,
                    sender,
                    null,
                    null,
                    mailFiles.getString("topic"),
                    mailFiles.getString("content"),
                    null,
                    null,
                    0,
                    null,
                    false,
                    filename,
                    "0",
                    is,
                    cl,
                    cd,
                    co,
                    po
                );
            }else{
                return createBaseMail(
                    "template",
                    0,
                    sender,
                    null,
                    null,
                    mailFiles.getString("topic"),
                    mailFiles.getString("content"),
                    null,
                    null,
                    0,
                    null,
                    false,
                    filename
                );
            }
        }else{
            return null;
        }
    }

    /**
     * 保存一封模板邮件
     * @param mt 模板邮件
     * @return boolean
     */
    public static boolean saveTemplateMail(MailTemplate mt){
        File f = new File(DATA_FOLDER+"/Template", mt.getTemplate()+".yml");
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException ex) {
                return false;
            }
        }
        BaseMail bm = (BaseMail)mt;
        YamlConfiguration mailFiles = new YamlConfiguration();
        mailFiles.set("sender", bm.getSender());
        mailFiles.set("topic",bm.getTopic());
        mailFiles.set("content",bm.getContent());
        if(bm instanceof BaseFileMail){
            BaseFileMail fm = (BaseFileMail)bm;
            mailFiles.set("file",true);
            if(fm.isHasCommand()){
                mailFiles.set("cmd.enable",true);
                mailFiles.set("cmd.commands",fm.getCommandList());
                mailFiles.set("cmd.descriptions",fm.getCommandDescription());
            }else{
                mailFiles.set("cmd.enable",false);
            }
            if(fm.isHasItem()){
                ArrayList<ItemStack> is = fm.getItemList();
                for(int i=0;i<is.size();i++){
                    mailFiles.set("is."+(i+1),is.get(i));
                }
            }
            if(GlobalConfig.enVault && fm.getCoin()!=0) mailFiles.set("money.coin",fm.getCoin());
            if(GlobalConfig.enPlayerPoints && fm.getPoint()!=0) mailFiles.set("money.point", fm.getPoint());
        }else{
            mailFiles.set("file",false);
        }
        try {
            mailFiles.save(f);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * 获取玩家player类型邮件已发件数量
     * @param p 玩家
     * @return int
     */
    public static int playerAsSender(Player p){
        MailBox.updateRelevantMailList(p, "player");
        return MailBox.getRelevantMailList(p, "player").get("asSender").size();
    }
    
    /**
     * 获取该玩家player类型邮件可以发件的数量
     * @param p 玩家
     * @return int
     */
    public static int playerAsSenderAllow(Player p){
        for(int i=GlobalConfig.playerOut;i>0;i--){
            if(p.hasPermission("mailbox.send.player.out."+i)){
                return i;
            }
        }
        return 0;
    }

    /**
     * 获取该玩家可发送的物品数量
     * @param p 玩家
     * @return int
     */
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

    /**
     * 检查指令执行者是否有相关的玩家权限
     * @param sender 指令执行者
     * @param perm 权限节点
     * @return boolean
     */
    public static boolean hasPlayerPermission(CommandSender sender, String perm){
        if(sender.hasPermission("mailbox.player.*")){
            return !(!sender.isOp() && sender.hasPermission("."+perm));
        }
        else return sender.hasPermission(perm);
    }

    /**
     * 判断物品是否允许发送
     * @param is 物品
     * @return boolean
     */
    public static boolean isAllowSend(ItemStack is){
        if(is.hasItemMeta()){
            ItemMeta im = is.getItemMeta();
            if(im.hasLore() && !im.getLore().isEmpty() && im.getLore().stream().noneMatch((l) -> (l.contains(GlobalConfig.fileBanLore)))){
                return false;
            }
        }
        String id = is.getType().name();
        return GlobalConfig.fileBanId.stream().noneMatch((i) -> (i.contains(id)));
    }
    
    /**
     * 为物品添加随机的Lore
     * @param is 待修改物品
     * @return 修改后的物品
     */
    public static ItemStack randomLore(ItemStack is){
        if(is.hasItemMeta()){
            ItemMeta im = is.getItemMeta();
            if(im.hasLore()){
                List<String> lores = im.getLore();
                GlobalConfig.fileRandomLoreSelect.forEach((k,v) -> {
                    lores.replaceAll((String l) -> {
                        if(l.equals(k)) return (String)Randoms.RandomObject(v);
                        else return l;
                    });
                });
                GlobalConfig.fileRandomLoreChange.forEach((k,v) -> {
                    lores.replaceAll((String l) -> {
                        if(l.contains(k)) return l.replace(k, Integer.toString(Randoms.RandomInt(v[0], v[1])));
                        else return l;
                    });
                });
                im.setLore(lores);
                is.setItemMeta(im);
            }
        }
        return is;
    }

    /**
     * 生成一个附件名
     * @param type 邮件类型
     * @return String
     * @throws IOException 创建文件失败
     * @throws Exception 尝试失败次数超过10
     */
    public static String generateFilename(String type) throws IOException, Exception{
        String md5 = MD5.Hex(DateTime.get("ms"));
        if(GlobalConfig.fileSQL){
            for(int i=0;existFilesSQL(md5, type);i++){
                md5 = MD5.Hex(DateTime.get("ms"));
                if(i>10)throw new Exception();
            }
        }else{
            while(existFiles(md5, type)){
                md5 = MD5.Hex(DateTime.get("ms"));
            }
        }
        return md5;
    }
    
    /**
     * 向玩家发送邮件提醒
     * @param p 玩家
     * @param msg 邮件提醒内容
     * @param key 提示口令
     */
    public static void sendTips(Player p, String msg, String key){
        if(GlobalConfig.tips.contains("msg")){
            p.sendMessage(msg);
            if(!key.equals("")) p.sendMessage(key);
        }
        if(!GlobalConfig.server_under_1_8 && GlobalConfig.tips.contains("title")) {
            if(GlobalConfig.server_under_1_11){
                p.sendTitle(msg, key);
            }else{
                p.sendTitle(msg, key, 10, 70, 20);
            }
        }
        if(GlobalConfig.tips.contains("sound")) p.playSound(p.getLocation(), GlobalConfig.tipsSound, 1, 1);
        if(GlobalConfig.enVexView && GlobalConfig.tips.contains("flow")) VexViewAPI.sendFlowView(p, msg+key, 10, true);
        if(GlobalConfig.enVexView && GlobalConfig.tips.contains("hud")) MailTipsHud.setMailTipsHud(p);
    }

    /**
     * 获取物品名称
     * @param is 物品
     * @return String
     */
    public static String getItemName(ItemStack is){
        return LocaleLanguageAPI.getItemName(is);
    }
    
    /**
     * 创建一封基础邮件
     * @param type 邮件类型
     * @param id 邮件id
     * @param sender 发件人
     * @param recipient 收件人列表
     * @param permission 所需权限
     * @param topic 主题
     * @param content 内容
     * @param date 发送日期
     * @param deadline 截止日期
     * @param times 邮件数量
     * @param key 邮件口令
     * @param only 兑换码唯一性
     * @param template 模板名
     * @return 基础邮件
     */
    public static BaseMail createBaseMail(String type, int id, String sender, List<String> recipient, String permission, String topic, String content, String date, String deadline, int times, String key, boolean only, String template){
        switch(type){
            case "system":
                return new SystemMail(id, sender, topic, content, date);
            case "permission":
                return new PermissionMail(id, sender, topic, content, date, permission);
            case "date":
                return new DateMail(id, sender, topic, content, date, deadline);
            case "player":
                return new PlayerMail(id, sender, topic, content, date, recipient);
            case "times":
                return new TimesMail(id, sender, topic, content, date, times);
            case "keytimes":
                return new KeyTimesMail(id, sender, topic, content, date, times, key);
            case "cdkey":
                return new CdkeyMail(id, sender, topic, content, date, only);
            case "online":
                return new OnlineMail(sender, topic, content, date);
            case "template":
                return new TemplateMail(sender, topic, content, template);
            default:
                return null;
        }
    }

    /**
     * 创建一封基础附件邮件
     * @param type 邮件类型
     * @param id 邮件id
     * @param sender 发件人
     * @param recipient 收件人列表
     * @param permission 所需权限
     * @param topic 主题
     * @param content 内容
     * @param date 发送日期
     * @param deadline 截止日期
     * @param times 邮件数量
     * @param key 邮件口令
     * @param only 兑换码唯一性
     * @param filename 附件名
     * @return 基础附件邮件
     */
    public static BaseFileMail createBaseFileMail(String type, int id, String sender, List<String> recipient, String permission, String topic, String content, String date, String deadline, int times, String key, boolean only, String filename){
        switch(type){
            case "system":
                return new SystemFileMail(id, sender, topic, content, date, filename);
            case "permission":
                return new PermissionFileMail(id, sender, topic, content, date, permission, filename);
            case "date":
                return new DateFileMail(id, sender, topic, content, date, deadline, filename);
            case "player":
                return new PlayerFileMail(id, sender, topic, content, date, recipient, filename);
            case "times":
                return new TimesFileMail(id, sender, topic, content, date, times, filename);
            case "keytimes":
                return new KeyTimesFileMail(id, sender, topic, content, date, times, key, filename);
            case "cdkey":
                return new CdkeyFileMail(id, sender, topic, content, date, only, filename);
            case "online":
            case "template":
            default:
                return null;
        }
    }

    /**
     * 创建一封基础附件邮件
     * @param type 邮件类型
     * @param id 邮件id
     * @param sender 发件人
     * @param recipient 收件人列表
     * @param permission 所需权限
     * @param topic 主题
     * @param content 内容
     * @param date 发送日期
     * @param deadline 截止日期
     * @param times 邮件数量
     * @param key 邮件口令
     * @param only 兑换码唯一性
     * @param template 模板名
     * @param filename 附件名
     * @param isl 物品列表
     * @param cl 指令列表
     * @param cd 指令描述列表
     * @param coin 金币
     * @param point 点券
     * @return 基础附件邮件
     */
    public static BaseFileMail createBaseFileMail(String type, int id, String sender, List<String> recipient, String permission, String topic, String content, String date, String deadline, int times, String key, boolean only, String template, String filename, ArrayList<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point){
        switch(type){
            case "system":
                return new SystemFileMail(id, sender, topic, content, date, filename, isl, cl, cd, coin, point);
            case "permission":
                return new PermissionFileMail(id, sender, topic, content, date, permission, filename, isl, cl, cd, coin, point);
            case "date":
                return new DateFileMail(id, sender, topic, content, date, deadline, filename, isl, cl, cd, coin, point);
            case "player":
                return new PlayerFileMail(id, sender, topic, content, date, recipient, filename, isl, cl, cd, coin, point);
            case "times":
                return new TimesFileMail(id, sender, topic, content, date, times, filename, isl, cl, cd, coin, point);
            case "keytimes":
                return new KeyTimesFileMail(id, sender, topic, content, date, times, key, filename, isl, cl, cd, coin, point);
            case "cdkey":
                return new CdkeyFileMail(id, sender, topic, content, date, only, filename, isl, cl, cd, coin, point);
            case "online":
                return new OnlineFileMail(sender, topic, content, date, isl, cl, cd, coin, point);
            case "template":
                return new TemplateFileMail(sender, topic, content, template, isl, cl, cd, coin, point);
            default:
                return null;
        }
    }
    
    /**
     * 发送一个兑换码
     * @param cdkey 兑换码
     * @param mail 邮件id
     * @return boolean
     */
    public static boolean sendCdkey(String cdkey, int mail){
        return SQLManager.get().sendCdkey(cdkey, mail);
    }
    
    /**
     * 删除一个兑换码
     * @param cdkey 兑换码
     * @return boolean
     */
    public static boolean deleteCdkey(String cdkey){
        return SQLManager.get().deleteCdkey(cdkey);
    }
    
    /**
     * 生成一个兑换码
     * @return String
     * @throws Exception 尝试失败次数超过10
     */
    public static String generateCdkey() throws Exception{
        String cdkey = MD5.Hex(DateTime.get("ms")).toUpperCase();
        for(int i=0;SQLManager.get().existCdkey(cdkey)>0;i++){
            cdkey = MD5.Hex(DateTime.get("ms"));
            if(i>10)throw new Exception();
        }
        return cdkey;
    }
    
    /**
     * 获取一封邮件的所有兑换码
     * @param mail 邮件id
     * @return 兑换码列表
     */
    public static List<String> getCdkey(int mail){
        return SQLManager.get().getCdkey(mail);
    }
    
    /**
     * 导出一封邮件的兑换码到Cdkey路径下对应id的文件
     * @param mail 邮件id
     * @return boolean
     */
    public static boolean exportCdkey(int mail){
        try {
            List<String> cdk = getCdkey(mail);
            if(cdk.isEmpty()) return false;
            File f = new File(DATA_FOLDER+"/Cdkey", mail+".txt");
            if(!f.exists()) f.createNewFile();
            FileOutputStream fs = new FileOutputStream(f);
            try (PrintStream p = new PrintStream(fs)) {
                cdk.forEach(c -> p.println(c));
            }
            return true;
        }   catch (IOException ex) {
            Logger.getLogger(MailBoxAPI.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * 让玩家兑换一个兑换码
     * @param p 玩家
     * @param cdkey 兑换码
     */
    public static void exchangeCdkey(Player p, String cdkey){
        if(!p.hasPermission("mailbox.admin.cdkey.day") && cdkeyDay(p)>=GlobalConfig.cdkeyDay){
            p.sendMessage(Message.exchangeExceedDay);
            return;
        }else{
            cdkeyDayAdd(p);
        }
        int mail = SQLManager.get().existCdkey(cdkey);
        if(mail>0){
            MailBox.updateRelevantMailList(p, "cdkey");
            if(!MailBox.getRelevantMailList(p, "cdkey").get("asRecipient").contains(mail)){
                p.sendMessage(Message.exchangeRepeat);
            }else{
                MailCdkey mc = (MailCdkey)MailBox.getMailHashMap("cdkey").get(mail);
                if(mc==null){
                    p.sendMessage(Message.exchangeNotMail);
                }else{
                    if(SQLManager.get().existCdkey(cdkey)>0){
                        if(mc.Collect(p)){
                            if(mc.isOnly()) mc.Delete(p);
                            else MailBoxAPI.deleteCdkey(cdkey);
                            p.sendMessage(Message.exchangeSuccess);
                        }else{
                            p.sendMessage(Message.exchangeError);
                        }
                    }else{
                        p.sendMessage(Message.exchangeError);
                    }
                }
            }
        }else{
            p.sendMessage(Message.exchangeNotCdkey);
        }
    }
    
    /**
     * 获取玩家今日输入兑换码的次数
     * @param p 玩家
     * @return 玩家今日输入兑换码的次数
     */
    public static int cdkeyDay(Player p){
        updateLastTime();
        String pn = p.getName();
        if(MailBox.CDKEY_DAY.containsKey(pn)){
            return MailBox.CDKEY_DAY.get(pn);
        }else{
            return 0;
        }
    }
    
    /**
     * 使玩家今日输入兑换码的次数加一
     * @param p 玩家
     */
    public static void cdkeyDayAdd(Player p){
        updateLastTime();
        String pn = p.getName();
        if(MailBox.CDKEY_DAY.containsKey(pn)){
            MailBox.CDKEY_DAY.replace(pn, MailBox.CDKEY_DAY.get(pn)+1);
        }else{
            MailBox.CDKEY_DAY.put(pn, 1);
        }
    }
    
    /**
     * 重置玩家今日输入兑换码的次数
     * @param name 玩家名
     */
    public static void cdkeyDayRemove(String name){
        if(MailBox.CDKEY_DAY.containsKey(name)) MailBox.CDKEY_DAY.remove(name);
    }
    
    /**
     * 删除本地已导出的兑换码
     * @param mail 邮件ID
     * @return boolean
     */
    public static boolean deleteLocalCdkey(int mail){
        File f = new File(DATA_FOLDER+"/Cdkey", mail+".txt");
        if(f.exists()) return f.delete();
        else return true;
    }
    
    /**
     * 获取配置文件，如果不存在则创建
     * @param dir 服务端目录内相对路径
     * @param filename 文件名
     * @param jar 插件jar包内相对路径
     * @return YamlConfiguration
     */
    public static YamlConfiguration configGet(String dir, String filename, String jar){
        File f = new File(dir, filename);
        if(!f.exists()){
            try {
                OutputStreamWriter osw;
                BufferedWriter bw;
                PrintWriter pw;
                try (InputStreamReader isr = new InputStreamReader(MailBox.getInstance().getResource(jar+filename), "UTF-8"); 
                    BufferedReader br = new BufferedReader(isr)) {
                    osw = new OutputStreamWriter(new FileOutputStream(f), (MailBox.getCharset()));
                    bw = new BufferedWriter(osw);
                    pw = new PrintWriter(bw);
                    String temp;
                    while((temp=br.readLine())!=null){
                        pw.println(temp);
                    }
                }
                bw.close();
                osw.close();
                pw.close();
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.file_create.replace("%file%", filename));
            } catch (IOException ex) {
                Bukkit.getConsoleSender().sendMessage(ConfigMessage.file_error.replace("%file%", filename));
                Bukkit.getLogger().info(ex.getLocalizedMessage());
                return null;
            }
        }
        Bukkit.getConsoleSender().sendMessage(ConfigMessage.file_read.replace("%file%", filename));
        return YamlConfiguration.loadConfiguration(f);
    }
    
}
