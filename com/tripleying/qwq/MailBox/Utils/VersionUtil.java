package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.Message;
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
 * @author Dogend
 */
public class VersionUtil {
    
    // 从远程连接获取最新版本号
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
            Bukkit.getConsoleSender().sendMessage(Message.updateError);
        }
        return null;
    }
    
    // 比较本插件版本号
    private static void check(CommandSender sender){
        ArrayList<String> info = getVersion("http://qwq.xn--o5raa.com/plugins/mailbox/version.php");
        if(info!=null && !info.isEmpty()){
            if(check(MailBoxAPI.getVersion(), info.get(0))){
                sender.sendMessage(Message.updateNewest);
            }else{
                String msg = "";
                for(int j=Message.updateNew.size()-1;j>0;j++){
                    msg += Message.updateNew.get(j)+ '\n';
                }
                if(!Message.updateNew.isEmpty()) msg += Message.updateNew.get(0);
                sender.sendMessage(msg.replace("%version%", info.get(0)).replace("%date%", info.get(1)).replace("%download%", MailBox.getInstance().getDescription().getWebsite()+"/download.php"));
                String[] in = info.get(2).split("#");
                for(int j=0;j<in.length;j++){
                    sender.sendMessage("§b"+(j+1)+": "+in[j]);
                }
            }
        }
    }
    
    // 比较两个字符串版本号
    public static boolean check(String now, String need){
        int db = Integer.parseInt(need.substring(0, need.indexOf(".")));
        int wb = Integer.parseInt(now.substring(0, now.indexOf(".")));
        if(wb>db){
            return true;
        }else if(db>wb){
            return false;
        }else{
            double ds = Double.parseDouble(need.substring(need.indexOf(".")+1));
            double ws = Double.parseDouble(now.substring(now.indexOf(".")+1));
            return ws>=ds;
        }
    }
    
    // 更新检查
    public static void check(CommandSender sender, long tick){
        new BukkitRunnable(){
            @Override
            public void run(){
                check(sender);
            }
        }.runTaskLater(MailBox.getInstance(), tick);
    }
    
}
