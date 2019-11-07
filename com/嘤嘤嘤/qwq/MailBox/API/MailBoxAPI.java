package com.嘤嘤嘤.qwq.MailBox.API;

import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Utils.DateTime;
import com.嘤嘤嘤.qwq.MailBox.Utils.MD5;
import com.嘤嘤嘤.qwq.MailBox.Utils.MySQLManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class MailBoxAPI {
    
    private static String VERSION = "1.0";
    private static final String DATA_FOLDER = "plugins/VexMailBox";
    
    // 获取插件版本
    public static String getVersion(){
        return VERSION;
    }
    
    // 设置某玩家领取一封邮件
    public static boolean setCollect(String type, int id, String playername){
        return MySQLManager.get().setMailCollect(type, id, playername);
    }
    
    // 发送一封邮件
    public static boolean setSend(String type, int id, String playername, String topic, String text, String date, String filename){
        if(id==0){
            return MySQLManager.get().sendMail(type, playername, topic, text, date, filename);
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
        f = new File(DATA_FOLDER+"/MailFiles/"+fm.type);
        if(!f.exists())f.mkdir();
        YamlConfiguration mailFiles = new YamlConfiguration();
        f = new File(DATA_FOLDER+"/MailFiles/"+fm.type, fm.fileName+".yml");
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException ex) {
                return false;
            }
        }
        if(fm.hasItem){
            ArrayList<ItemStack> isl = fm.itemList;
            for(int i=0;i<isl.size();i++){
                mailFiles.set("is."+(i+1), isl.get(i));
            }
        }
        mailFiles.set("cmd.enable", fm.hasCommand);
        mailFiles.set("cmd.commands", fm.commandList);
        mailFiles.set("cmd.descriptions", fm.commandDescription);
        try {
            mailFiles.save(f);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    // 删除某一封邮件
    public static boolean setDelete(String type, int id){
        return MySQLManager.get().deleteMail(type, id);
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
    
    // 生成一个32位MD5码
    public static String getMD5(String type) throws IOException{
        String md5 = MD5.Hex(DateTime.get("ms"));
        while(existFiles(md5, type)){
            System.out.println("1");
            md5 = MD5.Hex(DateTime.get("ms"));
        }
        return md5;
    }
    
}
