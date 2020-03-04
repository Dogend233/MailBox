package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.OuterMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 版本工具
 */
public class VersionUtil {

    /**
     * 从远程连接获取最新版本号
     * @param httpurl 地址
     * @return 字符串列表
     */
    private static ArrayList<String> getVersion(String httpurl){
        try{
            URL url = new URL(httpurl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setReadTimeout(2000);
            HttpURLConnection connection;
            if(urlConnection instanceof HttpURLConnection)
            {
               connection = (HttpURLConnection) urlConnection;
            }
            else
            {
               return null;
               
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            ArrayList<String> urlString = new ArrayList();
            String current;
            while((current = in.readLine()) != null)
            {
                urlString.add(current);
            }
            return urlString;
        }catch(IOException e){
            Bukkit.getConsoleSender().sendMessage(OuterMessage.updateError);
        }
        return null;
    }
    
    /**
     * 比较本插件版本号
     * @param sender 指令发送者
     */
    private static void check(CommandSender sender){
        ArrayList<String> info = getVersion("http://qwq.xn--o5raa.com/plugins/mailbox/version.php");
        if(info!=null && !info.isEmpty()){
            if(check(MailBoxAPI.getVersion(), info.get(0))){
                sender.sendMessage(OuterMessage.updateNewest);
            }else{
                String msg = "";
                int l = OuterMessage.updateNew.size();
                for(int j=0;j<l;j++){
                    msg += OuterMessage.updateNew.get(j)+'\n';
                }
                msg = msg.replace("%version%", info.get(0)).replace("%date%", info.get(1)).replace("%download%", MailBox.getInstance().getDescription().getWebsite()+"/download.php");
                String[] in = info.get(2).split("#");
                for(int j=0;j<in.length;j++){
                    msg += "§b"+(j+1)+": "+in[j]+'\n';
                }
                sender.sendMessage(msg);
            }
        }
    }

    /**
     * 比较两个字符串版本号
     * @param now 目前
     * @param need 需要
     * @return boolean
     */
    public static boolean check(String now, String need){
        int[] nowarr = String2Integer(now.split("\\."));
        int[] needarr = String2Integer(need.split("\\."));
        int nowl = nowarr.length;
        int needl = needarr.length;
        for(int i=0;i<(nowl>needl?nowl:needl);i++){
            int nowi = i<nowl?nowarr[i]:0;
            int needi = i<needl?needarr[i]:0;
            if(nowi<needi) return false;
        }
        return true;
    }

    /**
     * 将字符串数组转为int数组
     * @param str 字符串数组
     * @return int数组
     */
    public static int[] String2Integer(String[] str){
        int l = str.length;
        int[] in = new int[l];
        for(int i=0;i<l;i++){
            in[i] = Integer.parseInt(str[i]);
        }
        return in;
    }

    /**
     * 更新检查
     * @param sender 指令发送者
     * @param tick 延迟执行tick
     */
    public static void check(CommandSender sender, long tick){
        new BukkitRunnable(){
            @Override
            public void run(){
                check(sender);
            }
        }.runTaskLater(MailBox.getInstance(), tick);
    }
    
}
