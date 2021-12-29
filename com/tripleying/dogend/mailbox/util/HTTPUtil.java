package com.tripleying.dogend.mailbox.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * HTTP工具
 * @author Dogend
 */
public class HTTPUtil {
    
    /**
     * 按行获取文字
     * @param requestUrl 请求地址
     * @return List
     */
    public static List<String> getLineList(String requestUrl){
        try{
            URL url = new URL(requestUrl);
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
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            List<String> urlString = new ArrayList();
            String current;
            while((current = in.readLine()) != null)
            {
                urlString.add(current);
            }
            return urlString;
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取json对象
     * @param requestUrl 请求地址
     * @return json对象
     */
    public static JsonObject getJson(String requestUrl){
        String res = "";
        JsonObject object = null;
        StringBuilder buffer = new StringBuilder();
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            if (200 == urlCon.getResponseCode()) {
                InputStream is = urlCon.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String str = null;
                while ((str = br.readLine()) != null) {
                    buffer.append(str);
                }
                br.close();
                isr.close();
                is.close();
                res = buffer.toString();
                JsonParser parse = new JsonParser();
                object = (JsonObject) parse.parse(res);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
        }
        return object;
    }
    
    /**
     * 下载文件
     * @param requestUrl 请求地址
     * @param file 目标文件
     * @throws Exception 异常
     */
    public static void downloadFile(String requestUrl, File file) throws Exception {
        int byteread = 0;
        URL url = new URL(requestUrl);
        URLConnection conn = url.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
        httpURLConnection.setRequestProperty("Charset", "UTF-8");
        httpURLConnection.connect();
        InputStream inStream = httpURLConnection.getInputStream();
        FileOutputStream fs = new FileOutputStream(file);
        byte[] buffer = new byte[1204];
        while ((byteread = inStream.read(buffer)) != -1) {
            fs.write(buffer, 0, byteread);
        }
    }
    
}
