package com.嘤嘤嘤.qwq.MailBox.Utils;

import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
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
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

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
            cmd = SQLCommand.CREATE_DATE_SQLITE.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_PLAYER_SQLITE.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            creatTable();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getLocalizedMessage());
        }
    }
    
    //连接SQLite
    private void connectSQLite()
    {
        try {
            // 覆盖时间格式
            Properties pro = new Properties();
            pro.put("date_string_format", "yyyy-MM-dd HH:mm:ss"); 
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/MailBox/" + databaseName + ".db", pro);
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"SQLite数据库连接成功！");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"SQLite数据库连接失败！");
            Bukkit.getLogger().info(e.getLocalizedMessage());
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
            cmd = SQLCommand.CREATE_DATE_MYSQL.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_PLAYER_MYSQL.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            creatTable();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getLocalizedMessage());
        }
    }
    
    //连接MySQL
    private void connectMySQL()
    {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + databaseName + "?useSSL=false&autoReconnect=true", userName, userPassword);
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"MySQL数据库连接成功！");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"MySQL数据库连接失败！");
            Bukkit.getLogger().info(e.getLocalizedMessage());
        }
    }
    
    //MySQL与SQLite共用的建表语句
    private void creatTable(){
        try {
            String cmd = SQLCommand.CREATE_SYSTEM_COLLECT.commandToString(SQLPrefix);
            PreparedStatement ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_PERMISSION_COLLECT.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_DATE_COLLECT.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_FILE.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getLocalizedMessage());
        }
    }
    
    //断开数据库连接
    public void shutdown() {
        try {
            connection.close();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getLocalizedMessage());
        }
    }
    
    //获取某玩家是否可以领取某封邮件
    public boolean getMailCollectable(String type, int id, String playername){
        String sql1;
        switch (type) {
            case "system":
            case "permission":
            case "date":
                sql1 = SQLCommand.SELECT_MAIL.commandToString(SQLPrefix, type);
                break;
            case "player":
                sql1 = SQLCommand.SELECT_PLAYER_MAIL.commandToString(SQLPrefix);
                break;
            default:
                return false;
        }
        try {
            PreparedStatement ps;
            ResultSet rs;
            switch (type) {
                case "system":
                case "permission":
                case "date":
                    ps = connection.prepareStatement(sql1);
                    ps.setInt(1, id);
                    ps.setString(2, playername);
                    rs = ps.executeQuery();
                    return !rs.next();
                case "player":
                    ps = connection.prepareStatement(sql1);
                    ps.setInt(1, id);
                    rs = ps.executeQuery();
                    if (rs.next()){
                        List<String> pl = new ArrayList<String>(Arrays.asList(rs.getString("recipient").split(" ")));
                        return (!pl.isEmpty() && pl.contains(playername));
                    }else{
                        return false;
                    }
            }
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return false;
        }
    }
    
    //设置一封邮件已被某玩家领取
    public boolean setMailCollect(String type, int id, String playername) {
        if(!getMailCollectable(type,id,playername)) return false;
        String sql1;
        String sql2 = null;
        switch (type) {
            case "system":
            case "permission":
            case "date":
                sql1 = SQLCommand.COLLECT_MAIL.commandToString(SQLPrefix, type);
                break;
            case "player":
                sql1 = SQLCommand.SELECT_PLAYER_MAIL.commandToString(SQLPrefix);
                sql2 = SQLCommand.COLLECT_PLAYER_MAIL.commandToString(SQLPrefix);
                break;
            default:
                return false;
        }
        try {
            PreparedStatement ps;
            switch (type) {
                case "system":
                case "permission":
                case "date":
                    ps = connection.prepareStatement(sql1);
                    ps.setInt(1, id);
                    ps.setString(2, playername);
                    ps.executeUpdate();
                    break;
                case "player":
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
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return false;
        }
    }
    
    //发送一封邮件
    public boolean sendMail(String type, String mailsender, String mailrecipient, String permission, String topic, String text, String date, String deadline, String filename) {
        String sql;
        switch (type) {
            case "system":
                sql = SQLCommand.SEND_SYSTEM_MAIL.commandToString(SQLPrefix);
                break;
            case "permission":
                sql = SQLCommand.SEND_PERMISSION_MAIL.commandToString(SQLPrefix);
                break;
            case "date":
                sql = SQLCommand.SEND_DATE_MAIL.commandToString(SQLPrefix);
                break;
            case "player":
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
                case "player":
                    ps.setString(6, mailrecipient);
                    break;
                case "permission":
                    ps.setString(6, permission);
                    break;
                case "date":
                    if(deadline.equals("0")){
                        deadline = DateTime.getDefault();
                    }
                    ps.setString(6, deadline);
                    break;
            }
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return false;
        }
    }
    
    //删除一封邮件
    public boolean deleteMail(String type, int id) {
        String sql1;
        String sql2 = null;
        switch (type) {
            case "system":
            case "permission":
            case "date":
                sql1 = SQLCommand.DELETE_MAIL.commandToString(SQLPrefix, type);
                sql2 = SQLCommand.DELETE_COLLECTED_MAIL.commandToString(SQLPrefix, type);
                break;
            case "player":
                sql1 = SQLCommand.DELETE_MAIL.commandToString(SQLPrefix, type);
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
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return false;
        }
    }
    
    //获取邮件列表
    public HashMap<Integer, TextMail> getMailList(String type){
        String typeName = GlobalConfig.getTypeName(type);
        String sql = SQLCommand.SELECT_LIST_MAIL.commandToString(SQLPrefix, type);
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
                String deadline = null;
                switch (type) {
                    case "player" :
                        recipient = Arrays.asList(rs.getString("recipient").split(" "));
                        break;
                    case "permission":
                        permission = rs.getString("permission");
                        break;
                    case "date":
                        deadline = dateFormat.format(new Date(rs.getTimestamp("deadline").getTime()));
                        if(deadline.equals(DateTime.getDefault())) deadline = null;
                        break;
                }
                if(rs.getString("filename").equals("0")){
                    TextMail tm = new TextMail(type, rs.getInt("mail"), rs.getString("sender"), recipient, permission, rs.getString("topic"), rs.getString("text"), dateFormat.format(new Date(rs.getTimestamp("sendtime").getTime())), deadline);
                    if(MailBoxAPI.isExpired(tm)) {
                        if(MailBoxAPI.setDelete(type, tm.getId())){
                            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+tm.getTypeName()+"-"+tm.getId()+"邮件已过期，自动删除");
                            continue;
                        }
                    }
                    hm.put(rs.getInt("mail"), tm);
                }else{
                    FileMail fm = new FileMail(type, rs.getInt("mail"), rs.getString("sender"), recipient, permission, rs.getString("topic"), rs.getString("text"), dateFormat.format(new Date(rs.getTimestamp("sendtime").getTime())), deadline, rs.getString("filename"));
                    if(MailBoxAPI.isExpired(fm)) {
                        if(fm.DeleteFile() & MailBoxAPI.setDelete(type, fm.getId())){
                            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+fm.getTypeName()+"-"+fm.getId()+"邮件已过期，自动删除");
                            continue;
                        }
                    }
                    hm.put(rs.getInt("mail"), fm);
                }
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"获取"+typeName+"邮件列表失败.");
            Bukkit.getLogger().info(e.getLocalizedMessage());
        }
        return hm;
    }
    
    //获取玩家已领取的邮件ID列表
    public ArrayList<Integer> getCollectedMailList(Player p, String type){
        String typeName = GlobalConfig.getTypeName(type);
        String sql;
        switch (type) {
            case "system" :
            case "permission" :
            case "date" :
                sql = SQLCommand.SELECT_COLLECTED_MAIL.commandToString(SQLPrefix, type);
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
            Bukkit.getLogger().info(e.getLocalizedMessage());
        }
        return l;
    }
    
    //发送/更新一个附件
    public boolean sendMailFiles(String filename, YamlConfiguration fileyml) { 
        try {
            String sql;
            PreparedStatement ps;
            YamlConfiguration fy;
            String type = fileyml.getString("type");
            if(existMailFiles(filename, type)){
                sql = SQLCommand.UPDATE_FILE.commandToString(SQLPrefix);
                ps = connection.prepareStatement(sql);
            }else{
                sql = SQLCommand.SEND_FILE.commandToString(SQLPrefix);
                ps = connection.prepareStatement(sql);
            }
            if(fileyml.getBoolean("cmd.enable")){
                fy = new YamlConfiguration();
                fy.set("commands", fileyml.getStringList("cmd.commands"));
                ps.setString(1, fy.saveToString());
                fy = new YamlConfiguration();
                fy.set("descriptions", fileyml.getStringList("cmd.descriptions"));
                ps.setString(2, fy.saveToString());
            }else{
                ps.setString(1, null);
                ps.setString(2, null);
            }
            ps.setString(3, Double.toString(fileyml.getDouble("money.coin")));
            ps.setString(4, Integer.toString(fileyml.getInt("money.point")));
            int count = fileyml.getInt("is.count");
            fy = new YamlConfiguration();
            fy.set("is.count", count);
            for(int i=0;i<count;i++){
                fy.set("is.is_"+(i+1), fileyml.getItemStack("is.is_"+(i+1)));
            }
            ps.setString(5, fy.saveToString());
            ps.setString(6, filename);
            ps.setString(7, type);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return false;
        }
    }
    
    //获取一个附件
    public YamlConfiguration getMailFiles(String filename, String type) {
        String sql = SQLCommand.SELECT_FILE.commandToString(SQLPrefix);
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, filename);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                YamlConfiguration fy = new YamlConfiguration();
                if(rs.getString("commands")==null){
                    fy.set("cmd.enable", false);
                    fy.set("cmd.commands", null);
                    fy.set("cmd.descriptions", null);
                }else{
                    fy.set("cmd.enable", true);
                    YamlConfiguration t = new YamlConfiguration();
                    t.loadFromString(rs.getString("commands"));
                    fy.set("cmd.commands", t.getStringList("commands"));
                    t = new YamlConfiguration();
                    t.loadFromString(rs.getString("descriptions"));
                    fy.set("cmd.descriptions", t.getStringList("descriptions"));
                }
                fy.set("money.coin", Double.parseDouble(rs.getString("coin")));
                fy.set("money.point", Integer.parseInt(rs.getString("point")));
                YamlConfiguration t = new YamlConfiguration();
                t.loadFromString(rs.getString("items"));
                int count = t.getInt("is.count");
                fy.set("is.count", count);
                if(count>0){
                    for(int i=0;i<count;i++){
                        fy.set("is.is_"+(i+1), t.getItemStack("is.is_"+(i+1)));
                    }
                }
                return fy;
            }
        } catch (SQLException | InvalidConfigurationException e) {
            Bukkit.getLogger().info(e.getLocalizedMessage());
            
        }
        return null;
    }
    
    // 判断某附件是否已存在
    public boolean existMailFiles(String filename, String type){
        String sql = SQLCommand.SELECT_FILE.commandToString(SQLPrefix);
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(sql);
            ps.setString(1, filename);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }catch(SQLException e){
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return true;
        }
    }
    
    //获取某类型邮件所有附件名字
    public List<String> getAllFileName(String type){
        String sql = SQLCommand.SELECT_FILE_NAME.commandToString(SQLPrefix, type);
        List<String> L = new ArrayList();
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String file = rs.getString("filename");
                if(!file.equals("0")){
                    L.add(file);
                }
            }
            return L;
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return L;
        }
    }
    
    //删除一个附件
    public boolean deleteMailFiles(String filename, String type) {
        String sql = SQLCommand.DELETE_FILE.commandToString(SQLPrefix);
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(sql);
            ps.setString(1, filename);
            ps.setString(2, type);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return false;
        }
    }

}
