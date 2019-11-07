package com.嘤嘤嘤.qwq.MailBox.Utils;

import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListAllId;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MySQLManager {
    private String ip;
    private String databaseName;
    private String userName;
    private String userPassword;
    private Connection connection;
    private String SQLPrefix;
    private int port;
    public static MySQLManager instance = null;

    public static MySQLManager get() {
        return instance == null ? instance = new MySQLManager() : instance;
    }
    
    //设置参数
    private void setConfig(String ip, String databaseName, String userName, String userPassword, int port, String SQLPrefix){
        this.ip = ip;
        this.databaseName = databaseName;
        this.userName = userName;
        this.userPassword = userPassword;
        this.port = port;
        this.SQLPrefix = SQLPrefix;
    }

    //启用MySQL
    public void enableMySQL(String ipurl, String dbName, String user, String password, int Port, String prefix)
    {
        setConfig(ipurl, dbName, user, password, Port, prefix);
        connectMySQL();
        try {
            String cmd = SQLCommand.CREATE_ALL.commandToString(SQLPrefix);
            PreparedStatement ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_ALL_COLLECT.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    //连接MySQL
    private void connectMySQL()
    {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + databaseName + "?autoReconnect=true", userName, userPassword);
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"数据库连接成功！");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"数据库连接失败！");
            System.out.println(e);
        }
    }
    
    //断开MySQL连接
    public void shutdown() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    //设置一封邮件已被某玩家领取
    public boolean setMailCollect(String type, int id, String playername) {
        String sql;
        switch (type) {
            case "all" :
                sql = SQLCommand.COLLECT_ALL_MAIL.commandToString(SQLPrefix);
                break;
            default:
                return false;
        }
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, playername);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }
    
    //发送一封邮件
    public boolean sendMail(String type, String mailsender, String topic, String text, String date, String filename) {
        String sql;
        switch (type) {
            case "all" :
                sql = SQLCommand.SEND_ALL_MAIL.commandToString(SQLPrefix);
                break;
            default:
                return false;
        }
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(sql);
            ps.setString(1, mailsender);
            ps.setString(2, topic);
            ps.setString(3, text);
            ps.setString(4, date);
            ps.setString(5, filename);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }
    
    //删除一封邮件
    public boolean deleteMail(String type, int id) {
        String sql1;
        String sql2;
        switch (type) {
            case "all" :
                sql1 = SQLCommand.DELETE_ALL_MAIL.commandToString(SQLPrefix);
                sql2 = SQLCommand.DELETE_COLLECTED_ALL_MAIL.commandToString(SQLPrefix);
                break;
            default:
                return false;
        }
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(sql1);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps = connection.prepareStatement(sql2);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }
    
    //获取邮件列表
    public HashMap<Integer, TextMail> getMailList(String type){
        String typeName;
        String sql;
        switch (type) {
            case "all" :
                typeName = GlobalConfig.mailPrefix_ALL;
                sql = SQLCommand.FIND_LIST_ALL_MAIL.commandToString(SQLPrefix);
                break;
            default:
                Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"获取邮件失败：未定义的邮件类型 "+type);
                return null;
        }
        HashMap<Integer, TextMail> hm = new HashMap();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if(rs.getString("filename").equals("0")){
                    TextMail tm = new TextMail(type, rs.getInt("mail"), rs.getString("sender"), rs.getString("topic"), rs.getString("text"), dateFormat.format(new Date(rs.getTimestamp("sendtime").getTime())));
                    hm.put(rs.getInt("mail"), tm);
                }else{
                    FileMail fm = new FileMail(type, rs.getInt("mail"), rs.getString("sender"), rs.getString("topic"), rs.getString("text"), dateFormat.format(new Date(rs.getTimestamp("sendtime").getTime())), rs.getString("filename"));
                    hm.put(rs.getInt("mail"), fm);
                }
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"获取"+typeName+"邮件列表失败.");
        }
        return hm;
    }
    
    //获取玩家可领取的邮件ID列表
    public ArrayList<Integer> getUnMailList(Player p, String type){
        String typeName;
        String sql;
        switch (type) {
            case "all" :
                typeName = GlobalConfig.mailPrefix_ALL;
                sql = SQLCommand.FIND_UNCOLLECTED_ALL_MAIL.commandToString(SQLPrefix);
                break;
            default:
                Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"查询可领取邮件失败：未定义的邮件类型 "+type);
                return null;
        }
        ArrayList l = new ArrayList<Integer>();
        try {
            for(int i=0;i<MailListAllId.size();i++){
                int x = MailListAllId.get(i);
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, x);
                ps.setString(2, p.getName());
                ResultSet rs = ps.executeQuery();
                if(!rs.next()){l.add(x);}
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"查询"+p.getName()+"可领取"+typeName+"邮件失败");
        }
        return l;
    }

}
