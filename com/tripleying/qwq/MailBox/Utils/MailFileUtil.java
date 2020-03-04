package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Mail.BaseFileMail;
import com.tripleying.qwq.MailBox.OuterMessage;
import com.tripleying.qwq.MailBox.SQL.SQLManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * 附件工具
 */
public class MailFileUtil {

    /**
     * 生成一个附件名
     * @param type 邮件类型
     * @return 附件名
     * @throws Exception 尝试失败次数过多
     */
    public static String generateFilename(String type) throws Exception{
        String md5 = EncryptUtil.MD5(TimeUtil.get("ms"));
        if(GlobalConfig.fileSQL){
            for(int i=0;MailFileUtil.existFilesSQL(md5, type);i++){
                md5 = EncryptUtil.MD5(TimeUtil.get("ms"));
                if(i>10)throw new Exception();
            }
        }else{
            while(MailFileUtil.existFiles(md5, type)){
                md5 = EncryptUtil.MD5(TimeUtil.get("ms"));
            }
        }
        return md5;
    }
    
    /**
     * 判断附件是否存在本地
     * @param fileName 附件名
     * @param type 邮件类型
     * @return boolean
     */
    public static boolean existFiles(String fileName, String type){
        File f = FileUtil.getFile("MailFiles");
        if(!f.exists())f.mkdir();
        f = new File(f, type);
        if(!f.exists())f.mkdir();
        f = new File(f, fileName+".yml");
        return f.exists();
    }

    /**
     * 保存附件文件到本地
     * @param fm 附件邮件
     * @return boolean
     */
    public static boolean saveMailFilesLocal(BaseFileMail fm){
        File f = FileUtil.getFile("MailFiles");
        if(!f.exists())f.mkdir();
        f = new File(f, fm.getType());
        if(!f.exists())f.mkdir();
        YamlConfiguration mailFiles = new YamlConfiguration();
        f = new File(f, fm.getFileName()+".yml");
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException ex) {
                return false;
            }
        }
        if(fm.isHasItem()){
            List<ItemStack> isl = fm.getItemList();
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
     * @param fm 附件邮件
     * @return boolean
     */
    public static boolean getMailFilesLocal(BaseFileMail fm){
        YamlConfiguration mf;
        File f = FileUtil.getFile("MailFiles/"+fm.getType()+"/"+fm.getFileName()+".yml");
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
     * 从本地删除一个附件文件
     * @param fileName 附件邮件
     * @param type 邮件类型
     * @return boolean
     */
    public static boolean setDeleteFile(String fileName, String type){
        File f = FileUtil.getFile("MailFiles/"+type+"/"+fileName+".yml");
        if(f.exists()){
            return f.delete();
        }else{
            return true;
        }
    }

    /**
     * 将一封本地附件上传到数据库
     * @param type 邮件类型
     * @param filename 附件名
     * @return boolean
     */
    public static boolean uploadFile(String type, String filename){
        BaseFileMail fm = MailBoxAPI.createBaseFileMail(type, "", "", "", "");
        fm.setFileName(filename);
        getMailFilesLocal(fm);
        return saveMailFilesSQL(fm);
    }

    /**
     * 将一个类型的所有本地附件上传到数据库
     * @param cs 指令发送者
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
                    cs.sendMessage(OuterMessage.fileSuccess.replace("%file%", fn).replace("%state%", OuterMessage.fileUpload));
                }
            }
            cs.sendMessage(OuterMessage.fileMulti.replace("%state%", OuterMessage.fileUpload).replace("%ok%", Integer.toString(succ)).replace("all", Integer.toString(all)));
        }
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
     * 保存附件到数据库
     * @param fm 附件邮件
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
            List<ItemStack> isl = fm.getItemList();
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
     * @param fm 附件邮件
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
     * 将一封数据库附件下载到本地
     * @param type 邮件类型
     * @param filename 附件名
     * @return boolean
     */
    public static boolean downloadFile(String type, String filename){
        BaseFileMail fm = MailBoxAPI.createBaseFileMail(type, "", "", "", "");
        fm.setFileName(filename);
        getMailFilesSQL(fm);
        return saveMailFilesLocal(fm);
    }
    
    /**
     * 将一个类型的所有数据库附件下载到本地
     * @param cs 指令发送者
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
                    cs.sendMessage(OuterMessage.fileSuccess.replace("%file%", fn).replace("%state%", OuterMessage.fileDownload));
                }
            }
            cs.sendMessage(OuterMessage.fileMulti.replace("%state%", OuterMessage.fileDownload).replace("%ok%", Integer.toString(succ)).replace("all", Integer.toString(all)));

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
    
}
