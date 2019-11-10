package com.嘤嘤嘤.qwq.MailBox.Utils;

import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class UpdateCheck {
    
    private static boolean count;
    
    // 从远程连接获取最新版本号
    private static String getVersion(String httpurl){
        try{
            URL url = new URL(httpurl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(3000);
            HttpURLConnection connection = null;
            if(urlConnection instanceof HttpURLConnection)
            {
               connection = (HttpURLConnection) urlConnection;
            }
            else
            {
               return null;
               
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String urlString = "";
            String current;
            while((current = in.readLine()) != null)
            {
                urlString += current;
            }
            return urlString;
        }catch(IOException e){
            if(count){
                count = false;
                // 尝试从备用链接获取最新版本号
                return getVersion("https://dogend233.github.io/version.txt");
            }else{
                Bukkit.getConsoleSender().sendMessage("§c-----[MailBox更新检测]:获取最新版本信息失败");
            }
            
        }
        return null;
    }
    
    // 比较本插件版本号
    public static void check(CommandSender sender){
        count = true;
        String ns = getVersion("http://qwq.xn--o5raa.com/plugins/mailbox/download.php?version");
        if(ns!=null){
            String[] nsl = ns.split("\\.");
            String[] osl = MailBoxAPI.getVersion().split("\\.");
            for(int i=0;i<3;i++){
                int n = Integer.parseInt(nsl[i]);
                int o = Integer.parseInt(osl[i]);
                if(o<n){
                    sender.sendMessage("§c-----[MailBox更新检测]:检测到新版本："+ns);
                    break;
                }
            }
        }
    }
    
    // 比较两个字符串版本号
    public static boolean check(String now, String need){
        String[] nowl = now.split("\\.");
        String[] needl = need.split("\\.");
        int c = nowl.length;
        if(needl.length<c) c = needl.length;
        for(int i=0;i<c;i++){
            int w = Integer.parseInt(nowl[i]);
            int d = Integer.parseInt(needl[i]);
            if(w<d) return false;
        }
        return true;
    }
    
}
