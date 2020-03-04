package com.tripleying.qwq.MailBox.SQL;

import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.Mail.BaseMail;
import com.tripleying.qwq.MailBox.OuterMessage;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import com.tripleying.qwq.MailBox.Utils.TimeUtil;
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
import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class SQLManager {
    private boolean isMySQL;
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
        isMySQL = false;
        setConfig(databaseName, prefix);
        connectSQLite();
        try {
            String cmd = SQLCommand.CREATE_SYSTEM.commandToStringToSQLite(SQLPrefix);
            PreparedStatement ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_PERMISSION.commandToStringToSQLite(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_DATE.commandToStringToSQLite(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_PLAYER.commandToStringToSQLite(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_TIMES.commandToStringToSQLite(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_KEYTIMES.commandToStringToSQLite(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_CDKEY.commandToStringToSQLite(SQLPrefix);
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
            // 1.11以下手动加载数据库连接类
            if(GlobalConfig.server_under_1_11) Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/MailBox/" + databaseName + ".db");
            Bukkit.getConsoleSender().sendMessage(OuterMessage.sqlSuccess.replace("%sql%", "SQLite"));
        } catch (SQLException | ClassNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage(OuterMessage.sqlError.replace("%sql%", "SQLite"));
            Bukkit.getLogger().info(e.getLocalizedMessage());
        }
    }

    //启用MySQL
    public void enableMySQL(String ipurl, String dbName, String user, String password, int Port, String prefix)
    {
        isMySQL = true;
        setConfig(ipurl, dbName, user, password, Port, prefix);
        connectMySQL();
        try {
            String cmd = SQLCommand.CREATE_SYSTEM.commandToString(SQLPrefix);
            PreparedStatement ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_PERMISSION.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_DATE.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_PLAYER.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_TIMES.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_KEYTIMES.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_CDKEY.commandToString(SQLPrefix);
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
            Bukkit.getConsoleSender().sendMessage(OuterMessage.sqlSuccess.replace("%sql%", "MySQL"));
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(OuterMessage.sqlError.replace("%sql%", "MySQL"));
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
            cmd = SQLCommand.CREATE_TIMES_COLLECT.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_KEYTIMES_COLLECT.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_CDKEY_COLLECT.commandToString(SQLPrefix);
            ps = connection.prepareStatement(cmd);
            ps.executeUpdate();
            cmd = SQLCommand.CREATE_CDKEY_LIST.commandToString(SQLPrefix);
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
        String sql;
        try {
            PreparedStatement ps;
            ResultSet rs;
            switch (type) {
                case "system":
                case "permission":
                case "date":
                    sql = SQLCommand.SELECT_MAIL.commandToString(SQLPrefix, type);
                    ps = connection.prepareStatement(sql);
                    ps.setInt(1, id);
                    ps.setString(2, playername);
                    rs = ps.executeQuery();
                    return !rs.next();
                case "player":
                    sql = SQLCommand.SELECT_PLAYER_MAIL.commandToString(SQLPrefix);
                    ps = connection.prepareStatement(sql);
                    ps.setInt(1, id);
                    rs = ps.executeQuery();
                    if (rs.next()){
                        List<String> pl = new ArrayList<>(Arrays.asList(rs.getString("recipient").split(" ")));
                        return (!pl.isEmpty() && pl.contains(playername));
                    }else{
                        return false;
                    }
                case "keytimes":
                case "times":
                    sql = SQLCommand.SELECT_MAIL.commandToString(SQLPrefix, type);
                    ps = connection.prepareStatement(sql);
                    ps.setInt(1, id);
                    ps.setString(2, playername);
                    rs = ps.executeQuery();
                    if(rs.next()){
                        return false;
                    }else{
                        sql = SQLCommand.SELECT_TIMES_MAIL.commandToString(SQLPrefix, type);
                        ps = connection.prepareStatement(sql);
                        ps.setInt(1, id);
                        rs = ps.executeQuery();
                        if(rs.next()){
                            return rs.getInt("times")>0;
                        }else{
                            return false;
                        }
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
        String sql;
        ResultSet rs;
        try {
            PreparedStatement ps;
            switch (type) {
                case "cdkey":
                case "system":
                case "permission":
                case "date":
                    sql = SQLCommand.COLLECT_MAIL.commandToString(SQLPrefix, type);
                    ps = connection.prepareStatement(sql);
                    ps.setInt(1, id);
                    ps.setString(2, playername);
                    ps.executeUpdate();
                    break;
                case "player":
                    sql = SQLCommand.SELECT_PLAYER_MAIL.commandToString(SQLPrefix);
                    ps = connection.prepareStatement(sql);
                    ps.setInt(1, id);
                    rs = ps.executeQuery();
                    List<String> pl = new ArrayList();
                    if(rs.next()){
                        pl = new ArrayList<>(Arrays.asList(rs.getString("recipient").split(" ")));
                    }
                    if(pl.contains(playername)) pl.remove(playername);
                    if(pl.isEmpty()){
                        deleteMail(type, id);
                    }else{
                        String str = "";
                        for(String n:pl) str += " "+n;
                        str = str.substring(1);
                        sql = SQLCommand.COLLECT_PLAYER_MAIL.commandToString(SQLPrefix);
                        ps = connection.prepareStatement(sql);
                        ps.setString(1, str);
                        ps.setInt(2, id);
                        ps.executeUpdate();
                    }
                    break;
                case "keytimes":
                case "times":
                    sql = SQLCommand.SELECT_TIMES_MAIL.commandToString(SQLPrefix, type);
                    ps = connection.prepareStatement(sql);
                    ps.setInt(1, id);
                    rs = ps.executeQuery();
                    int times = 0;
                    if (rs.next()){
                        times = rs.getInt("times");
                    }
                    times--;
                    if(times<1){
                        deleteMail(type, id);
                    }else{
                        sql = SQLCommand.COLLECT_TIMES_MAIL.commandToString(SQLPrefix, type);
                        ps = connection.prepareStatement(sql);
                        ps.setInt(1, times);
                        ps.setInt(2, id);
                        ps.executeUpdate();
                        sql = SQLCommand.COLLECT_MAIL.commandToString(SQLPrefix, type);
                        ps = connection.prepareStatement(sql);
                        ps.setInt(1, id);
                        ps.setString(2, playername);
                        ps.executeUpdate();
                    }
                    break;
                default:
                    return false;
            }
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return false;
        }
    }
    
    //发送一封邮件
    public boolean sendMail(String type, String mailsender, String mailrecipient, String permission, String topic, String text, String date, String deadline, int times, String key, boolean only, String filename) {
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
            case "times":
                sql = SQLCommand.SEND_TIMES_MAIL.commandToString(SQLPrefix);
                break;
            case "keytimes":
                sql = SQLCommand.SEND_KEYTIMES_MAIL.commandToString(SQLPrefix);
                break;
            case "cdkey":
                sql = SQLCommand.SEND_CDKEY_MAIL.commandToString(SQLPrefix);
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
                case "keytimes":
                    ps.setString(7, key);
                case "times":
                    ps.setInt(6, times);
                    break;
                case "player":
                    ps.setString(6, mailrecipient);
                    break;
                case "permission":
                    ps.setString(6, permission);
                    break;
                case "date":
                    if(deadline.equals("0")){
                        deadline = TimeUtil.getDefault();
                    }
                    ps.setString(6, deadline);
                    break;
                case "cdkey":
                    ps.setString(6, Boolean.toString(only));
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
        String sql3 = null;
        switch (type) {
            case "cdkey":
                sql3 = SQLCommand.DELETE_CDKEY_MAIL.commandToString(SQLPrefix);
            case "system":
            case "permission":
            case "date":
            case "keytimes":
            case "times":
                sql2 = SQLCommand.DELETE_COLLECTED_MAIL.commandToString(SQLPrefix, type);
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
                if(sql3!=null){
                    ps = connection.prepareStatement(sql3);
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return false;
        }
    }
    
    //获取邮件列表
    public HashMap<Integer, BaseMail> getMailList(String type){
        String typeName = OuterMessage.getTypeName(type);
        if(typeName==null) return null;
        String sql = SQLCommand.SELECT_LIST_MAIL.commandToString(SQLPrefix, type);
        HashMap<Integer, BaseMail> hm = new HashMap();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                int mail = rs.getInt("mail");
                String sender = rs.getString("sender");
                List<String> recipient = null;
                String permission = null;
                String topic = rs.getString("topic");
                String content = rs.getString("text");
                String time = isMySQL ? dateFormat.format(new Date(rs.getTimestamp("sendtime").getTime()))
                                        :rs.getString("sendtime");
                String deadline = null;
                int times = 0;
                String key = null;
                boolean only = false;
                String filename = rs.getString("filename");
                switch (type) {
                    case "cdkey" :
                        only = Boolean.parseBoolean(rs.getString("only"));
                        break;
                    case "times" :
                        times = rs.getInt("times");
                        break;
                    case "keytimes" :
                        times = rs.getInt("times");
                        key = rs.getString("key");
                        break;
                    case "player" :
                        recipient = Arrays.asList(rs.getString("recipient").split(" "));
                        break;
                    case "permission":
                        permission = rs.getString("permission");
                        break;
                    case "date":
                        deadline = isMySQL ? dateFormat.format(new Date(rs.getTimestamp("deadline").getTime()))
                                            :rs.getString("deadline");
                        if(deadline.equals(TimeUtil.getDefault())) deadline = null;
                        break;
                }
                BaseMail bm = filename.equals("0") ? MailUtil.createBaseMail(type, mail, sender, recipient, permission, topic, content, time, deadline, times, key, only, null)
                                                    :MailUtil.createBaseFileMail(type, mail, sender, recipient, permission, topic, content, time, deadline, times, key, only, filename);
                if(MailUtil.isExpired(bm) && MailUtil.setDelete(type, mail)){
                    Bukkit.getConsoleSender().sendMessage(OuterMessage.mailExpire.replace("%para%", bm.getTypeName()+"-"+mail));
                    continue;
                }
                hm.put(mail, bm);
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(OuterMessage.mailListError.replace("%type%", typeName));
            Bukkit.getLogger().info(e.getLocalizedMessage());
        }
        return hm;
    }
    
    //获取玩家已领取的邮件ID列表
    public ArrayList<Integer> getCollectedMailList(Player p, String type){
        String typeName = OuterMessage.getTypeName(type);
        if(typeName==null) return null;
        String sql;
        switch (type) {
            case "system":
            case "permission":
            case "date":
            case "keytimes":
            case "times":
            case "cdkey":
                sql = SQLCommand.SELECT_COLLECTED_MAIL.commandToString(SQLPrefix, type);
                break;
            default:
                return null;
        }
        try {
            ArrayList l = new ArrayList<Integer>();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, p.getName());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) l.add(rs.getInt("mail"));
            return l;
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(OuterMessage.mailPlayerError.replace("%player%", p.getName()).replace("%type%", typeName));
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return null;
        }
    }
    
    //发送/更新一个附件
    public boolean sendMailFiles(String filename, YamlConfiguration fileyml, String item) { 
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
            ps.setString(5, item);
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
                if(!file.equals("0")) L.add(file);
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
    
    // 判断Cdkey是否存在
    public int existCdkey(String cdkey){
        String sql = SQLCommand.SELECT_CDKEY2MAIL_MAIL.commandToString(SQLPrefix);
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(sql);
            ps.setString(1, cdkey);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("mail") : 0;
        }catch(SQLException e){
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return 0;
        }
    }
    
    // 发送一个Cdkey
    public boolean sendCdkey(String cdkey, int mail){
        String sql = SQLCommand.SEND_CDKEY.commandToString(SQLPrefix);
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(sql);
            ps.setString(1, cdkey);
            ps.setInt(2, mail);
            ps.executeUpdate();
            return true;
        }catch(SQLException e){
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return false;
        }
    }
    
    // 获取一个邮件的Cdkey
    public List<String> getCdkey(int mail){
        List<String> cdk = new ArrayList();
        String sql = SQLCommand.SELECT_MAIL_CDKEY.commandToString(SQLPrefix);
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(sql);
            ps.setInt(1, mail);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) cdk.add(rs.getString("cdkey"));
        }catch(SQLException e){
            Bukkit.getLogger().info(e.getLocalizedMessage());
        }
        return cdk;
    }
    
    // 获取所有的Cdkey
    public HashMap<Integer,List<String>> getAllCdkey(){
        HashMap<Integer,List<String>> h = new HashMap();
        String sql = SQLCommand.SELECT_ALL_CDKEY.commandToString(SQLPrefix);
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int mail = rs.getInt("mail");
                if(h.containsKey(mail)){
                    h.get(mail).add(rs.getString("cdkey"));
                }else{
                    List<String> l = new ArrayList();
                    l.add(rs.getString("cdkey"));
                    h.put(mail, l);
                }
            }
        }catch(SQLException e){
            Bukkit.getLogger().info(e.getLocalizedMessage());
        }
        return h;
    }
    
    // 删除一个cdkey
    public boolean deleteCdkey(String cdkey){
        String sql = SQLCommand.DELETE_CDKEY.commandToString(SQLPrefix);
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement(sql);
            ps.setString(1, cdkey);
            ps.executeUpdate();
            return true;
        }catch(SQLException e){
            Bukkit.getLogger().info(e.getLocalizedMessage());
            return false;
        }
    }
    
}
