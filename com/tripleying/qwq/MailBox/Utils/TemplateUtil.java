package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Mail.BaseFileMail;
import com.tripleying.qwq.MailBox.Mail.BaseMail;
import com.tripleying.qwq.MailBox.Mail.MailTemplate;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * 模板工具
 * @author Dogend
 */
public class TemplateUtil {
    
    // 判断模板是否存在
    public static boolean existTemplate(String filename){
        File f = FileUtil.getFile("Template/"+filename+".yml");
        return f.exists();
    }
    
    // 取出一个模板
    public static BaseMail loadTemplateMail(String filename){
        YamlConfiguration mailFiles;
        File f = FileUtil.getFile("Template/"+filename+".yml");
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
                return MailUtil.createBaseFileMail(
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
                return MailUtil.createBaseMail(
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
    
    // 保存一个模板
    public static boolean saveTemplateMail(MailTemplate mt){
        File f = FileUtil.getFile("Template/"+mt.getTemplate()+".yml");
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
    
}
