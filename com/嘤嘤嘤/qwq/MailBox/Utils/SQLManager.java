package com.嘤嘤嘤.qwq.MailBox.Utils;

import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
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
import java.util.Properties;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getLogger;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class SQLManager {
    private String ip;
    private String databaseName;
    private String userName;
    private String userPassword;
    private Connection connection;
    private String SQLPrefix;
    private int port;
    public static SQLManager instance = null;

    public static SQLManager get() {
        return instance == null ? instance = new SQLManager() : instance;
    }
    
    //设置SQLite参数
    private void setConfig(String databaseName, String SQLPrefix){
        this.databaseName = databaseName;
        this.SQLPrefix = SQLPrefix;
    }
    
    //设置MySQL参数
    private void setConfig(String ip, String databaseName, String userName, String userPassword, int port, String SQLPrefix){
        this.ip = ip;
        this.databaseName = databaseName;
        this.userName = userName;
        this.userPassword = userPassword;
        this.port = port;
        this.SQLPrefix = SQLPrefix;
    }
    
    //启用SQLite
    public void enableSQLite(String databaseName, String prefix)
    {
        setConfig(databaseName, prefix);
        connectSQLite();
        try {
            String cmd = SQLCommand.CREATE_SYSTEM_SQLITE.commandToString(SQLPrefix);
            PreparedStatement ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_PERMISSION_SQLITE.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_PLAYER_SQLITE.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_SYSTEM_COLLECT.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_PERMISSION_COLLECT.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().info(e.getLocalizedMessage());
        }
    }
    
    //连接SQLite
    private void connectSQLite()
    {
        try {
            // 覆盖时间格式
            Properties pro = new Properties();
            pro.put("date_string_format", "yyyy-MM-dd HH:mm:ss"); 
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/VexMailBox/" + databaseName + ".db", pro);
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"SQLite数据库连接成功！");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"SQLite数据库连接失败！");
            getLogger().info(e.getLocalizedMessage());
        }
    }

    //启用MySQL
    public void enableMySQL(String ipurl, String dbName, String user, String password, int Port, String prefix)
    {
        setConfig(ipurl, dbName, user, password, Port, prefix);
        connectMySQL();
        try {
            String cmd = SQLCommand.CREATE_SYSTEM_MYSQL.commandToString(SQLPrefix);
            PreparedStatement ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_PERMISSION_MYSQL.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_PLAYER_MYSQL.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_SYSTEM_COLLECT.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_PERMISSION_COLLECT.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().info(e.getLocalizedMessage());
        }
    }
    
    //连接MySQL
    private void connectMySQL()
    {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + databaseName + "?autoReconnect=true", userName, userPassword);
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"MySQL数据库连接成功！");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"MySQL数据库连接失败！");
            getLogger().info(e.getLocalizedMessage());
        }
    }
    
    //断开数据库连接
    public void shutdown() {
        try {
            connection.close();
        } catch (SQLException e) {
            getLogger().info(e.getLocalizedMessage());
        }
    }
    
    //设置一封邮件已被某玩家领取
    public boolean setMailCollect(String type, int id, String playername) {
        String sql1;
        String sql2 = null;
        switch (type) {
            case "system" :
                sql1 = SQLCommand.COLLECT_SYSTEM_MAIL.commandToString(SQLPrefix);
                break;
            case "permission" :
                sql1 = SQLCommand.COLLECT_PERMISSION_MAIL.commandToString(SQLPrefix);
                break;
            case "player" :
                sql1 = SQLCommand.SELECT_PLAYER_MAIL.commandToString(SQLPrefix);
                sql2 = SQLCommand.COLLECT_PLAYER_MAIL.commandToString(SQLPrefix);
                break;
            default:
                return false;
        }
        try {
            PreparedStatement ps;
            switch (type) {
                case "system" :
                    ps = connection.prepareStatement(sql1);
                    ps.setInt(1, id);
                    ps.setString(2, playername);
                    ps.executeUpdate();
                    break;
                case "permission" :
                    ps = connection.prepareStatement(sql1);
                    ps.setInt(1, id);
                    ps.setString(2, playername);
                    ps.executeUpdate();
                    break;
                case "player" :
                    ps = connection.prepareStatement(sql1);
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    List<String> pl = new ArrayList();
                    while (rs.next()){
                        pl = new ArrayList<>(Arrays.asList(rs.getString("recipient").split(" ")));
                    }
                    if(pl.contains(playername)) pl.remove(playername);
                    if(pl.isEmpty()){
                        deleteMail(type, id);
                    }else{
                        String str = "";
                        for(String n:pl) str += " "+n;
                        str = str.substring(1);
                        ps = connection.prepareStatement(sql2);
                        ps.setString(1, str);
                        ps.setInt(2, id);
                        ps.executeUpdate();
                    }
                    break;
            }
            return true;
        } catch (SQLException e) {
            getLogger().info(e.getLocalizedMessage());
            return false;
        }
    }
    
    //发送一封邮件
    public boolean sendMail(String type, String mailsender, String mailrecipient, String permission, String topic, String text, String date, String filename) {
        String sql;
        switch (type) {
            case "system" :
                sql = SQLCommand.SEND_SYSTEM_MAIL.commandToString(SQLPrefix);
                break;
            case "permission" :
                sql = SQLCommand.SEND_PERMISSION_MAIL.commandToString(SQLPrefix);
                break;
            case "player" :
                sql = SQLCommand.SEND_PLAYER_MAIL.commandToString(SQLPrefix);
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
            switch (type) {
                case "player" :
                    ps.setString(6, mailrecipient);
                    break;
                case "permission" :
                    ps.setString(6, permission);
                    break;
            }
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            getLogger().info(e.getLocalizedMessage());
            return false;
        }
    }
    
    //删除一封邮件
    public boolean deleteMail(String type, int id) {
        String sql1;
        String sql2 = null;
        switch (type) {
            case "system" :
                sql1 = SQLCommand.DELETE_SYSTEM_MAIL.commandToString(SQLPrefix);
                sql2 = SQLCommand.DELETE_COLLECTED_SYSTEM_MAIL.commandToString(SQLPrefix);
                break;
             case "permission" :
                sql1 = SQLCommand.DELETE_PERMISSION_MAIL.commandToString(SQLPrefix);
                sql2 = SQLCommand.DELETE_COLLECTED_PERMISSION_MAIL.commandToString(SQLPrefix);
                break;
            case "player" :
                sql1 = SQLCommand.DELETE_PLAYER_MAIL.commandToString(SQLPrefix);
                break;
            default:
                return false;
        }
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(sql1);
            ps.setInt(1, id);
            ps.executeUpdate();
            if(sql2!=null){
                ps = connection.prepareStatement(sql2);
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            getLogger().info(e.getLocalizedMessage());
            return false;
        }
    }
    
    //获取邮件列表
    public HashMap<Integer, TextMail> getMailList(String type){
        String typeName = GlobalConfig.getTypeName(type);
        String sql = SQLCommand.FIND_LIST_MAIL.commandToString(SQLPrefix, type);
        if(typeName==null) {
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
                List<String> recipient = null;
                String permission = null;
                switch (type) {
                    case "player" :
                        recipient = Arrays.asList(rs.getString("recipient").split(" "));
                        break;
                    case "permission":
                        permission = rs.getString("permission");
                        break;
                }
                if(rs.getString("filename").equals("0")){
                    TextMail tm = new TextMail(type, rs.getInt("mail"), rs.getString("sender"), recipient, permission, rs.getString("topic"), rs.getString("text"), dateFormat.format(new Date(rs.getTimestamp("sendtime").getTime())));
                    hm.put(rs.getInt("mail"), tm);
                }else{
                    FileMail fm = new FileMail(type, rs.getInt("mail"), rs.getString("sender"), recipient, permission, rs.getString("topic"), rs.getString("text"), dateFormat.format(new Date(rs.getTimestamp("sendtime").getTime())), rs.getString("filename"));
                    hm.put(rs.getInt("mail"), fm);
                }
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"获取"+typeName+"邮件列表失败.");
            getLogger().info(e.getLocalizedMessage());
        }
        return hm;
    }
    
    //获取玩家已领取的邮件ID列表
    public ArrayList<Integer> getCollectedMailList(Player p, String type){
        String typeName = GlobalConfig.getTypeName(type);
        String sql;
        switch (type) {
            case "system" :
                sql = SQLCommand.FIND_COLLECTED_SYSTEM_MAIL.commandToString(SQLPrefix);
                break;
            case "permission" :
                sql = SQLCommand.FIND_COLLECTED_PERMISSION_MAIL.commandToString(SQLPrefix);
                break;
            case "player" :
                Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"查询可领取邮件失败：此邮件类型不可通过此方法查询");
                return null;
            default:
                Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"查询可领取邮件失败：未定义的邮件类型 "+type);
                return null;
        }
        ArrayList l = new ArrayList<Integer>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, p.getName());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) l.add(rs.getInt("mail"));
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"查询"+p.getName()+"已领取"+typeName+"邮件失败");
            getLogger().info(e.getLocalizedMessage());
        }
        return l;
    }

}
