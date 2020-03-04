package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.InnerMessage;
import com.tripleying.qwq.MailBox.MailBox;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 文件工具
 */
public class FileUtil {
    
    /**
     * 插件文件夹
     */
    private static File dataFolder;
    
    /**
     * 默认编码
     */
    private static String encoding = "UTF-8";
    
    /**
     * 1.9以下默认编码为UTF-8的服务端核心
     */
    private static final List<String> UTF8_SERVER = Arrays.asList("Uranium");
    
    /**
     * 文件是否存在
     * @param fileName 文件名
     * @return boolean
     */
    public static boolean existFile(String fileName){
        if(!dataFolder.exists()) dataFolder.mkdir();
        File file = new File(dataFolder, fileName);
        return file.exists();
    }

    /**
     * 获取文件
     * @param fileName 文件名
     * @return 文件
     */
    public static File getFile(String fileName){
        if(!dataFolder.exists()) dataFolder.mkdir();
        return new File(dataFolder, fileName);
    }

    /**
     * 获取jar内文件
     * @param fileName 文件名
     * @return InputStream
     */
    public static InputStream getInputStream(String fileName){
        return MailBox.getInstance().getResource(fileName);
    }

    /**
     * 获取jar内文件的Reader, 使用默认编码
     * @param fileName 文件名
     * @return InputStreamReader
     * @throws UnsupportedEncodingException 编码格式不支持
     */
    public static InputStreamReader getInputStreamReader(String fileName) throws UnsupportedEncodingException{
        return new InputStreamReader(MailBox.getInstance().getResource(fileName), encoding);
    }

    /**
     * 获取jar内文件Reader并指定编码
     * @param fileName 文件名
     * @param charset 编码格式
     * @return InputStreamReader
     * @throws UnsupportedEncodingException 编码格式不支持
     */
    public static InputStreamReader getInputStreamReader(String fileName, String charset) throws UnsupportedEncodingException{
        return new InputStreamReader(MailBox.getInstance().getResource(fileName), charset);
    }

    /**
     * 读取一个yml
     * @param f 文件
     * @return YamlConfiguration
     */
    public static YamlConfiguration getYaml(File f){
        return YamlConfiguration.loadConfiguration(f);
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
     * @param filename 文件名
     * @param dir 插件文件夹下的目录
     * @param jar jar包内的目录
     * @return YamlConfiguration
     */
    public static YamlConfiguration getConfig(String filename, String dir, String jar){
        File f = getFile(dir.equals("")?filename:(dir+"/"+filename));
        if(!f.exists()){
            try {
                OutputStreamWriter osw;
                BufferedWriter bw;
                PrintWriter pw;
                try (InputStreamReader isr = getInputStreamReader(jar.equals("")?filename:(jar+"/"+filename)); 
                    BufferedReader br = new BufferedReader(isr)) {
                    osw = new OutputStreamWriter(new FileOutputStream(f), encoding);
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
                Bukkit.getConsoleSender().sendMessage(InnerMessage.file_create.replace("%file%", filename));
            } catch (IOException ex) {
                Bukkit.getConsoleSender().sendMessage(InnerMessage.file_error.replace("%file%", filename));
                return null;
            }
        }
        Bukkit.getConsoleSender().sendMessage(InnerMessage.file_read.replace("%file%", filename));
        return getYaml(f);
    }

    /**
     * 设置插件文件夹
     * @param file 文件
     */
    public static void setDataFolder(File file){
        dataFolder = file;
    }

    /**
     * 设置默认编码
     * @param server 服务端核心
     */
    public static void setEncoding(String server){
        if(!UTF8_SERVER.contains(server)) encoding = System.getProperty("file.encoding");
    }
    
}
