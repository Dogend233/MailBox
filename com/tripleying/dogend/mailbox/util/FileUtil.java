package com.tripleying.dogend.mailbox.util;

import com.tripleying.dogend.mailbox.MailBox;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 文件工具
 * @author Dogend
 */
public class FileUtil {
    
    // 插件数据文件夹
    private final static File data_folder;
    // 默认编码格式
    private static String charset = "UTF-8";
    // 1.9以下默认编码为UTF-8的服务端核心
    private static final List<String> UTF8_Server;
    
    static{
        data_folder = new File("plugins/MailBox");
        UTF8_Server = Arrays.asList("Uranium", "Cauldron");
    }
    
    /**
     * 设置默认编码
     * @param server 服务端核心
     */
    public static void setCharset(String server){
        if(!UTF8_Server.contains(server)) charset = System.getProperty("file.encoding");
    }
    
    /**
     * 获取默认编码
     * @return charset
     */
    public static String getCharset(){
        return charset;
    } 
    
    /**
     * 为所选路径创建文件夹并返回
     * @param path 路径
     * @return File
     */
    public static File createFolder(String path){
        File file = new File(data_folder, path);
        if(!file.exists()) file.mkdirs();
        return file;
    }
    
    /**
     * 获取MailBox插件文件夹
     * @return File
     */
    public static File getMailBoxFolder(){
        File file = new File("plugins/MailBox");
        if(!file.exists()) file.mkdirs();
        return file;
    }
    
    /**
     * 获取jar内的文件
     * @param fileName 文件名
     * @return InputStream
     */
    public static InputStream getInputStream(String fileName){
        return MailBox.getMailBox().getResource(fileName);
    }

    /**
     * 获取jar内文件的Reader, 使用默认编码
     * @param fileName 文件名
     * @return InputStreamReader
     * @throws UnsupportedEncodingException 编码格式不支持
     */
    public static InputStreamReader getInputStreamReader(String fileName) throws UnsupportedEncodingException{
        return new InputStreamReader(getInputStream(fileName), charset);
    }

    /**
     * 获取jar内文件Reader并指定编码
     * @param fileName 文件名
     * @param charset 编码格式
     * @return InputStreamReader
     * @throws UnsupportedEncodingException 编码格式不支持
     */
    public static InputStreamReader getInputStreamReader(String fileName, String charset) throws UnsupportedEncodingException{
        return new InputStreamReader(getInputStream(fileName), charset);
    }
    
    /**
     * 读取一个yml
     * @param f 文件
     * @return YamlConfiguration
     * @throws java.io.FileNotFoundException 文件不存在
     * @throws java.io.UnsupportedEncodingException 不支持的编码
     */
    public static YamlConfiguration getYaml(File f) throws FileNotFoundException, UnsupportedEncodingException{
        return YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(f), charset));
    }
    
    /**
     * 读取一个yml
     * @param r Reader
     * @return YamlConfiguration
     */
    public static YamlConfiguration getYaml(Reader r){
        return YamlConfiguration.loadConfiguration(r);
    }
    
    /**
     * 获取配置文件(不存在则自动创建)
     * @param file 配置文件名
     * @return YamlConfiguration
     */
    public static YamlConfiguration getConfig(String file){
        File f = new File(getMailBoxFolder(), file);
        if(!f.exists()){
            try {
                try (InputStreamReader isr = getInputStreamReader(file)) {
                    if(saveYaml(isr, f)){
                        MessageUtil.log(MessageUtil.file_create.replaceAll("%file%", file));
                    }else{
                        throw new IOException();
                    }
                    isr.close();
                }
            } catch (IOException ex) {
                MessageUtil.error(MessageUtil.file_create_error.replaceAll("%file%", file));
                return null;
            }
        }
        MessageUtil.log(MessageUtil.file_read.replaceAll("%file%", file));
        try {
            return getYaml(f);
        } catch (Exception ex) {
            return new YamlConfiguration();
        }
    }
    
    /**
     * 保存Yaml文件
     * @param reader InputStreamReader / StringReader
     * @param file 目标文件
     * @return boolean
     */
    public static boolean saveYaml(Reader reader, File file){
        try {
            OutputStreamWriter osw;
            BufferedWriter bw;
            PrintWriter pw;
            try(BufferedReader br = new BufferedReader(reader)){
                osw = new OutputStreamWriter(new FileOutputStream(file), charset);
                bw = new BufferedWriter(osw);
                pw = new PrintWriter(bw);
                String temp;
                while((temp=br.readLine())!=null){
                    pw.println(temp);
                }
                br.close();
                reader.close();
            }
            pw.close();
            bw.close();
            osw.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * 从yml获取StringList
     * @param yml yml
     * @param key 键
     * @return 值
     */
    public static List<String> getYamlStringList(YamlConfiguration yml, String key){
        List<String> list = yml.getStringList(key);
        if(list.isEmpty()){
            return Arrays.asList(yml.getString(key));
        }else{
            return list;
        }
    }
    
    /**
     * 将String读取为Yaml并取出指定key的值
     * @param rs 结果集
     * @param key 路径
     * @return ConfigurationSection
     * @throws InvalidConfigurationException 配置无效异常
     * @throws java.sql.SQLException SQL异常
     */
    public static Object string2Section(ResultSet rs, String key) throws InvalidConfigurationException, SQLException{
        YamlConfiguration yml = new YamlConfiguration();
        yml.loadFromString(rs.getString(key));
        return yml.contains(key)?yml.get(key):null;
    }
    
    /**
     * 将yml的一个片段取出并保存为String
     * @param yml yml
     * @param key 路径
     * @return String
     */
    public static String section2String(YamlConfiguration yml, String key){
        Object o = yml.get(key);
        YamlConfiguration ny = new YamlConfiguration();
        ny.set(key, o);
        return ny.saveToString();
    }
    
}
