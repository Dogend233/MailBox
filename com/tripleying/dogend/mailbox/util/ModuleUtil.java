package com.tripleying.dogend.mailbox.util;

import java.io.File;
import com.tripleying.dogend.mailbox.api.module.MailBoxModule;
import com.tripleying.dogend.mailbox.api.module.ModuleInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 模块工具
 * @author Dogend
 */
public class ModuleUtil {
    
    /**
     * 获取模块文件夹
     * @return File
     */
    public static File getModuleFolder(){
        File file = new File("plugins/MailBox/Module");
        if(!file.exists()) file.mkdirs();
        return file;
    }
    
    /**
     * 获取模块文件夹
     * @param module 模块
     * @return File
     */
    public static File getModuleDataFolder(MailBoxModule module){
        File file = new File(getModuleFolder(), module.getInfo().getName());
        if(!file.exists()) file.mkdirs();
        return file;
    }
    
    /**
     * 保存配置文件
     * @param module 模块
     * @param file 文件
     */
    public static void saveConfig(MailBoxModule module, String file){
        JarFile jar = module.getJar();
        File f = new File(getModuleDataFolder(module), file);
        File parent = f.getParentFile();
        if(!parent.exists()) parent.mkdirs();
        if(!f.exists()){
            try{
                OutputStreamWriter osw;
                BufferedWriter bw;
                PrintWriter pw;
                try (InputStreamReader isr = getInputStreamReader(jar.getInputStream(jar.getEntry(file))); 
                    BufferedReader br = new BufferedReader(isr)) {
                    osw = new OutputStreamWriter(new FileOutputStream(f), FileUtil.getCharset());
                    bw = new BufferedWriter(osw);
                    pw = new PrintWriter(bw);
                    String temp;
                    while((temp=br.readLine())!=null){
                        pw.println(temp);
                    }
                    br.close();
                    isr.close();
                }
                pw.close();
                bw.close();
                osw.close();
                MessageUtil.log(MessageUtil.file_create.replaceAll("%file%", file));
            }catch(IOException ex){
                ex.printStackTrace();
                MessageUtil.error(MessageUtil.file_create_error.replaceAll("%file%", file));
            }
        }
    }
    
    /**
     * 获取配置文件
     * @param module 模块
     * @param file 文件
     * @return YamlConfiguration
     */
    public static YamlConfiguration getConfig(MailBoxModule module, String file){
        File f = new File(getModuleDataFolder(module), file);
        if(!f.exists()) saveConfig(module, file);
        try {
            return FileUtil.getYaml(f);
        } catch (Exception ex) {
            return new YamlConfiguration();
        }
    }
    
    /**
     * 获取使用默认编码创建一个InputStream的Reader
     * @param is 输入流
     * @return InputStreamReader
     * @throws UnsupportedEncodingException 编码格式不支持
     */
    public static InputStreamReader getInputStreamReader(InputStream is) throws UnsupportedEncodingException{
        return new InputStreamReader(is, FileUtil.getCharset());
    }
    
    /**
     * 从jar加载模块信息
     * @param jar Jar
     * @return ModuleInfo
     * @throws Exception 异常
     */
    public static ModuleInfo loadModuleInfo(JarFile jar) throws Exception{
        JarEntry entry = jar.getJarEntry("module.yml");
        try(
            InputStream input = jar.getInputStream(entry);
            InputStreamReader reader = new InputStreamReader(input)
        ){
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(reader);
            return new ModuleInfo(yml);
        }
    }
    
}
