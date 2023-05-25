package com.tripleying.dogend.mailbox.data;

import com.tripleying.dogend.mailbox.api.data.BaseData;
import com.tripleying.dogend.mailbox.api.mail.PersonMail;
import com.tripleying.dogend.mailbox.api.mail.PlayerData;
import com.tripleying.dogend.mailbox.api.mail.SystemMail;
import com.tripleying.dogend.mailbox.manager.MailManager;
import com.tripleying.dogend.mailbox.api.data.sql.SQLCommand;
import com.tripleying.dogend.mailbox.util.FileUtil;
import com.tripleying.dogend.mailbox.util.ReflectUtil;
import com.tripleying.dogend.mailbox.util.TimeUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import com.tripleying.dogend.mailbox.api.data.Data;
import com.tripleying.dogend.mailbox.api.data.sql.DeleteData;
import com.tripleying.dogend.mailbox.api.data.sql.SelectData;
import com.tripleying.dogend.mailbox.api.mail.CustomData;

/**
 * SQL数据接口
 * @author Dogend
 */
public abstract class SQLData implements BaseData {
    
    /**
     * 数据类型
     */
    private final String type;
    
    public SQLData(String type){
        this.type = type;
    }
    
    /**
     * 获取一个SQL连接
     * @since 3.1.0
     * @return Connection
     */
    public abstract Connection getConnection();
    
    /**
     * 释放一个SQL连接
     * @since 3.1.0
     * @param con Connection
     */
    public abstract void releaseConnection(Connection con);
    
    @Override
    public boolean createPlayerDataStorage(){
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPlayerDataCreateCommand()));
                ps.executeUpdate();
                return true;
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return false;
    }

    @Override
    public PlayerData getPlayerData(Player p) {
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPlayerDataSelectCommand()));
                ps.setString(1, p.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    YamlConfiguration yml = new YamlConfiguration();
                    yml.set("name", rs.getString("name"));
                    yml.set("uuid", rs.getString("uuid"));
                    yml.set("data", FileUtil.string2Section(rs, "data"));
                    PlayerData pd = new PlayerData(yml);
                    if(!pd.getName().equals(p.getName())){
                        PlayerData npd = new PlayerData(p, pd.getData());
                        ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPlayerDataUpdateCommand()));
                        ps.setString(1, p.getName());
                        ps.setString(2, rs.getString("data"));
                        ps.setString(3, rs.getString("uuid"));
                        return ps.executeUpdate()==0?pd:npd;
                    }
                    return pd;
                }else{
                    ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPlayerDataInsertCommand()));
                    PlayerData pd = new PlayerData(p);
                    YamlConfiguration yml = pd.toYamlConfiguration();
                    ps.setString(1, yml.getString("name"));
                    ps.setString(2, yml.getString("uuid"));
                    ps.setString(3, FileUtil.section2String(yml, "data"));
                    if(ps.executeUpdate()==1) return pd;
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return null;
    }

    @Override
    public List<PlayerData> getAllPlayerData() {
        List<PlayerData> list = new ArrayList();
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPlayerDataSelectAllCommand()));
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    YamlConfiguration yml = new YamlConfiguration();
                    yml.set("name", rs.getString("name"));
                    yml.set("uuid", rs.getString("uuid"));
                    yml.set("data", FileUtil.string2Section(rs, "data"));
                    PlayerData pd = new PlayerData(yml);
                    list.add(pd);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return list;
    }

    @Override
    public boolean updatePlayerData(PlayerData pd) {
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPlayerDataSelectCommand()));
                YamlConfiguration yml = pd.toYamlConfiguration();
                ps.setString(1, yml.getString("uuid"));
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPlayerDataUpdateCommand()));
                    ps.setString(1, yml.getString("name"));
                    ps.setString(2, FileUtil.section2String(yml, "data"));
                    ps.setString(3, yml.getString("uuid"));
                }else{
                    ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPlayerDataInsertCommand()));
                    ps.setString(1, yml.getString("name"));
                    ps.setString(2, yml.getString("uuid"));
                    ps.setString(3, FileUtil.section2String(yml, "data"));
                }
                return ps.executeUpdate()!=0;
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return false;
    }
    
    @Override
    public boolean createPersonMailStorage(){
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPersonMailCreateCommand()));
                ps.executeUpdate();
                return true;
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return false;
    }
    
    @Override
    public long getPersonMailCount(Player p){
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPersonMailSelectCommand().addSelectWithoutBackquote("COUNT(*) AS `count`")));
                ps.setString(1, p.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    return rs.getLong("count");
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return 0;
    }
    
    @Override
    public long getNotReceivedPersonMailCount(Player p){
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPersonMailSelectCommand().addSelectWithoutBackquote("COUNT(*) AS `count`").addWhere("received")));
                ps.setString(1, p.getUniqueId().toString());
                ps.setBoolean(2, false);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    return rs.getLong("count");
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return 0;
    }
    
    @Override
    public List<PersonMail> getPersonMail(Player p){
        List<PersonMail> list = new ArrayList();
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPersonMailSelectCommand()));
                ps.setString(1, p.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    YamlConfiguration yml = new YamlConfiguration();
                    yml.set("id", rs.getLong("id"));
                    yml.set("type", rs.getString("type"));
                    yml.set("title", rs.getString("title"));
                    yml.set("body", FileUtil.string2Section(rs, "body"));
                    yml.set("sender", rs.getString("sender"));
                    yml.set("sendtime", this.getDateTime("sendtime", rs));
                    yml.set("attach", FileUtil.string2Section(rs, "attach"));
                    yml.set("uuid", rs.getString("uuid"));
                    yml.set("received", rs.getBoolean("received"));
                    PersonMail pm = new PersonMail(yml);
                    if(pm.isExpire()){
                        pm.deleteMail();
                    }else{
                        list.add(pm);
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return list;
    }
    
    @Override
    public PersonMail getPersonMail(Player p, long id, String type){
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPersonMailSelectCommand().addWhere("id").addWhere("type")));
                ps.setString(1, p.getUniqueId().toString());
                ps.setLong(2, id);
                ps.setString(3, type);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    YamlConfiguration yml = new YamlConfiguration();
                    yml.set("id", rs.getLong("id"));
                    yml.set("type", rs.getString("type"));
                    yml.set("title", rs.getString("title"));
                    yml.set("body", FileUtil.string2Section(rs, "body"));
                    yml.set("sender", rs.getString("sender"));
                    yml.set("sendtime", this.getDateTime("sendtime", rs));
                    yml.set("attach", FileUtil.string2Section(rs, "attach"));
                    yml.set("uuid", rs.getString("uuid"));
                    yml.set("received", rs.getBoolean("received"));
                    PersonMail pm = new PersonMail(yml);
                    if(pm.isExpire()){
                        pm.deleteMail();
                    }else{
                        return pm;
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return null;
    }

    @Override
    public List<PersonMail> getPersonMail(Player p, int count, int page) {
        List<PersonMail> list = new ArrayList();
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPersonMailSelectCommand().orderBy("sendtime").setLimit(true)));
                ps.setString(1, p.getUniqueId().toString());
                ps.setInt(2, count*(page-1));
                ps.setInt(3, count);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    YamlConfiguration yml = new YamlConfiguration();
                    yml.set("id", rs.getLong("id"));
                    yml.set("type", rs.getString("type"));
                    yml.set("title", rs.getString("title"));
                    yml.set("body", FileUtil.string2Section(rs, "body"));
                    yml.set("sender", rs.getString("sender"));
                    yml.set("sendtime", this.getDateTime("sendtime", rs));
                    yml.set("attach", FileUtil.string2Section(rs, "attach"));
                    yml.set("uuid", rs.getString("uuid"));
                    yml.set("received", rs.getBoolean("received"));
                    PersonMail pm = new PersonMail(yml);
                    if(pm.isExpire()){
                        pm.deleteMail();
                    }else{
                        list.add(pm);
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return list;
    }

    @Override
    public boolean sendPersonMail(PersonMail pm, Player p) {
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPersonMailInsertCommand()));
                pm.setReceiver(p);
                pm.setSendtime(TimeUtil.currentTimeString());
                YamlConfiguration yml = pm.toYamlConfiguration();
                ps.setString(1, yml.getString("uuid"));
                ps.setString(2, yml.getString("type"));
                ps.setLong(3, yml.getLong("id"));
                ps.setString(4, yml.getString("title"));
                ps.setString(5, FileUtil.section2String(yml, "body"));
                ps.setString(6, yml.getString("sender"));
                ps.setString(7, yml.getString("sendtime"));
                ps.setBoolean(8, yml.getBoolean("received"));
                ps.setString(9, FileUtil.section2String(yml, "attach"));
                return ps.executeUpdate()!=0;
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return false;
    }

    @Override
    public boolean receivePersonMail(PersonMail pm, Player p) {
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPersonMailReceiveCommand()));
                YamlConfiguration yml = pm.toYamlConfiguration();
                ps.setBoolean(1, true);
                ps.setString(2, p.getUniqueId().toString());
                ps.setString(3, yml.getString("type"));
                ps.setLong(4, yml.getLong("id"));
                return ps.executeUpdate()!=0;
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return false;
    }

    @Override
    public boolean deletePersonMail(PersonMail pm) {
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPersonMailDeleteCommand()));
                YamlConfiguration yml = pm.toYamlConfiguration();
                ps.setString(1, yml.getString("uuid"));
                ps.setString(2, yml.getString("type"));
                ps.setLong(3, yml.getLong("id"));
                ps.setBoolean(4, yml.getBoolean("received"));
                return ps.executeUpdate()!=0;
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return false;
    }

    @Override
    public long clearPersonReceivedMail(Player p) {
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlPersonMailClearCommand()));
                ps.setString(1, p.getUniqueId().toString());
                ps.setBoolean(2, true);
                return ps.executeUpdate();
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return 0;
    }
    
    @Override
    public boolean createSystemMailStorage(SystemMail sm) {
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlSystemMailCreateCommand(sm.getType(), sm.getClass())));
                ps.executeUpdate();
                return true;
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return false;
    }

    @Override
    public long getSystemMailMax(SystemMail sm) {
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlSystemMailLastDataIDCommand(sm.getType())));
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    return rs.getLong("id");
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return 0;
    }
    
    @Override
    public long getSystemMailCount(SystemMail sm){
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlSystemMailSelectCommand(sm.getType()).addSelectWithoutBackquote("COUNT(*) AS `count`")));
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    return rs.getLong("count");
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return 0;
    }

    @Override
    public List<SystemMail> getSystemMail(SystemMail sm) {
        List<SystemMail> list = new ArrayList();
        Connection con = this.getConnection();
        if(null!=con){
            try{
                String type = sm.getType();
                Map<String, Data> cols = ReflectUtil.getSystemMailColumns(sm.getClass());
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlSystemMailSelectCommand(type)));
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    YamlConfiguration yml = new YamlConfiguration();
                    yml.set("id", rs.getLong("id"));
                    yml.set("type", type);
                    yml.set("title", rs.getString("title"));
                    yml.set("body", FileUtil.string2Section(rs, "body"));
                    yml.set("sender", rs.getString("sender"));
                    yml.set("sendtime", this.getDateTime("sendtime", rs));
                    yml.set("attach", FileUtil.string2Section(rs, "attach"));
                    Iterator<Map.Entry<String, Data>> it = cols.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry<String, Data> me = it.next();
                        String key = me.getKey();
                        Object o;
                        switch(me.getValue().type()){
                            case Integer:
                                o = rs.getInt(key);
                                break;
                            case Long:
                                o = rs.getLong(key);
                                break;
                            case Boolean:
                                o = rs.getBoolean(key);
                                break;
                            case DateTime:
                                o = this.getDateTime(key, rs);
                                break;
                            default:
                            case String:
                                o = rs.getString(key);
                                break;
                            case YamlString:
                                o = FileUtil.string2Section(rs, key);
                                break;
                        }
                        yml.set(key, o);
                    }
                    SystemMail lsm = MailManager.getMailManager().loadSystemMail(yml);
                    if(lsm.isExpire()){
                        lsm.deleteMail();
                    }else{
                        list.add(lsm);
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return list;
    }

    @Override
    public SystemMail getSystemMail(SystemMail sm, long id) {
        Connection con = this.getConnection();
        if(null!=con){
            try{
                String type = sm.getType();
                Map<String, Data> cols = ReflectUtil.getSystemMailColumns(sm.getClass());
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlSystemMailSelectCommand(type).addWhere("id")));
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    YamlConfiguration yml = new YamlConfiguration();
                    yml.set("id", rs.getLong("id"));
                    yml.set("type", type);
                    yml.set("title", rs.getString("title"));
                    yml.set("body", FileUtil.string2Section(rs, "body"));
                    yml.set("sender", rs.getString("sender"));
                    yml.set("sendtime", this.getDateTime("sendtime", rs));
                    yml.set("attach", FileUtil.string2Section(rs, "attach"));
                    Iterator<Map.Entry<String, Data>> it = cols.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry<String, Data> me = it.next();
                        String key = me.getKey();
                        Object o;
                        switch(me.getValue().type()){
                            case Integer:
                                o = rs.getInt(key);
                                break;
                            case Long:
                                o = rs.getLong(key);
                                break;
                            case Boolean:
                                o = rs.getBoolean(key);
                                break;
                            case DateTime:
                                o = this.getDateTime(key, rs);
                                break;
                            default:
                            case String:
                                o = rs.getString(key);
                                break;
                            case YamlString:
                                o = FileUtil.string2Section(rs, key);
                                break;
                        }
                        yml.set(key, o);
                    }
                    SystemMail lsm = MailManager.getMailManager().loadSystemMail(yml);
                    if(lsm.isExpire()){
                        lsm.deleteMail();
                    }else{
                        return lsm;
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return null;
    }

    @Override
    public List<SystemMail> getSystemMail(SystemMail sm, int count, int page) {
        List<SystemMail> list = new ArrayList();
        Connection con = this.getConnection();
        if(null!=con){
            try{
                String type = sm.getType();
                Map<String, Data> cols = ReflectUtil.getSystemMailColumns(sm.getClass());
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlSystemMailSelectCommand(type).orderBy("id").setLimit(true)));
                ps.setInt(1, count*(page-1));
                ps.setInt(2, count);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    YamlConfiguration yml = new YamlConfiguration();
                    yml.set("id", rs.getLong("id"));
                    yml.set("type", type);
                    yml.set("title", rs.getString("title"));
                    yml.set("body", FileUtil.string2Section(rs, "body"));
                    yml.set("sender", rs.getString("sender"));
                    yml.set("sendtime", this.getDateTime("sendtime", rs));
                    yml.set("attach", FileUtil.string2Section(rs, "attach"));
                    Iterator<Map.Entry<String, Data>> it = cols.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry<String, Data> me = it.next();
                        String key = me.getKey();
                        Object o;
                        switch(me.getValue().type()){
                            case Integer:
                                o = rs.getInt(key);
                                break;
                            case Long:
                                o = rs.getLong(key);
                                break;
                            case Boolean:
                                o = rs.getBoolean(key);
                                break;
                            case DateTime:
                                o = this.getDateTime(key, rs);
                                break;
                            default:
                            case String:
                                o = rs.getString(key);
                                break;
                            case YamlString:
                                o = FileUtil.string2Section(rs, key);
                                break;
                        }
                        yml.set(key, o);
                    }
                    SystemMail lsm = MailManager.getMailManager().loadSystemMail(yml);
                    if(lsm.isExpire()){
                        lsm.deleteMail();
                    }else{
                        list.add(lsm);
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return list;
    }
    
    @Override
    public Map<Long, SystemMail> getSystemMail(SystemMail sm, long min, long max){
        Map<Long, SystemMail> map = new LinkedHashMap();
        Connection con = this.getConnection();
        if(null!=con){
            try{
                String type = sm.getType();
                Map<String, Data> cols = ReflectUtil.getSystemMailColumns(sm.getClass());
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlSystemMailSelectCommand(type).addBetween("id")));
                ps.setLong(1, min);
                ps.setLong(2, max);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    YamlConfiguration yml = new YamlConfiguration();
                    yml.set("id", rs.getLong("id"));
                    yml.set("type", type);
                    yml.set("title", rs.getString("title"));
                    yml.set("body", FileUtil.string2Section(rs, "body"));
                    yml.set("sender", rs.getString("sender"));
                    yml.set("sendtime", this.getDateTime("sendtime", rs));
                    yml.set("attach", FileUtil.string2Section(rs, "attach"));
                    Iterator<Map.Entry<String, Data>> it = cols.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry<String, Data> me = it.next();
                        String key = me.getKey();
                        Object o;
                        switch(me.getValue().type()){
                            case Integer:
                                o = rs.getInt(key);
                                break;
                            case Long:
                                o = rs.getLong(key);
                                break;
                            case Boolean:
                                o = rs.getBoolean(key);
                                break;
                            case DateTime:
                                o = this.getDateTime(key, rs);
                                break;
                            default:
                            case String:
                                o = rs.getString(key);
                                break;
                            case YamlString:
                                o = FileUtil.string2Section(rs, key);
                                break;
                        }
                        yml.set(key, o);
                    }
                    SystemMail lsm = MailManager.getMailManager().loadSystemMail(yml);
                    if(lsm.isExpire()){
                        lsm.deleteMail();
                    }else{
                        map.put(lsm.getId(), lsm);
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return map;
    }

    @Override
    public SystemMail sendSystemMail(SystemMail sm) {
        Connection con = this.getConnection();
        if(null!=con){
            try{
                Map<String, Data> cols = ReflectUtil.getSystemMailColumns(sm.getClass());
                Map<String, Object> map = ReflectUtil.getSystemMailValues(sm, cols);
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlSystemMailInsertCommand(sm.getType(), sm.getClass())));
                sm.setSendtime(TimeUtil.currentTimeString());
                YamlConfiguration yml = sm.toYamlConfiguration();
                ps.setString(1, yml.getString("title"));
                ps.setString(2, FileUtil.section2String(yml, "body"));
                ps.setString(3, yml.getString("sender"));
                ps.setString(4, yml.getString("sendtime"));
                ps.setString(5, FileUtil.section2String(yml, "attach"));
                int i = 6;
                Iterator<Map.Entry<String, Data>> it = cols.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry<String, Data> me = it.next();
                    switch(me.getValue().type()){
                        case Integer:
                            ps.setInt(i++, (int)map.get(me.getKey()));
                            break;
                        case Long:
                            ps.setLong(i++, (long)map.get(me.getKey()));
                            break;
                        case Boolean:
                            ps.setBoolean(i++, (boolean)map.get(me.getKey()));
                            break;
                        default:
                        case String:
                        case DateTime:
                        case YamlString:
                            ps.setString(i++, (String)map.get(me.getKey()));
                            break;
                    }
                }
                if(ps.executeUpdate()!=0){
                    ps = con.prepareStatement(this.command2String(CommandBuilder.sqlLastInsertIDCommand()));
                    ResultSet rs = ps.executeQuery();
                    if(rs.next()){
                        sm.setId(rs.getLong("id"));
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return sm;
    }

    @Override
    public boolean deleteSystemMail(SystemMail sm) {
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlSystemMailDeleteCommand(sm.getType())));
                ps.setLong(1, sm.getId());
                return ps.executeUpdate()!=0;
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return false;
    }
    
    @Override
    public boolean createCustomStorage(CustomData cd){
        Connection con = this.getConnection();
        if(null!=con){
            try{
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlCustomDataCreateCommand(cd)));
                ps.executeUpdate();
                return true;
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return false;
    }
    
    @Override
    public boolean insertCustomData(CustomData cd){
        Connection con = this.getConnection();
        if(null!=con){
            try{
                Map<String, Data> cols = ReflectUtil.getCustomDataColumns(cd.getClass());
                Map<String, Object> map = ReflectUtil.getCustomDataValues(cd, cols);
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlCustomDataInsertCommand(cd)));
                int i = 1;
                Iterator<Map.Entry<String, Data>> it = cols.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry<String, Data> me = it.next();
                    switch(me.getValue().type()){
                        case Primary:
                            break;
                        case Integer:
                            ps.setInt(i++, (int)map.get(me.getKey()));
                            break;
                        case Long:
                            ps.setLong(i++, (long)map.get(me.getKey()));
                            break;
                        case Boolean:
                            ps.setBoolean(i++, (boolean)map.get(me.getKey()));
                            break;
                        default:
                        case String:
                        case DateTime:
                        case YamlString:
                            ps.setString(i++, (String)map.get(me.getKey()));
                            break;
                    }
                }
                return ps.executeUpdate()!=0;
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return false;
    }
    
    @Override
    public boolean updateCustomDataByPrimaryKey(CustomData cd){
        Connection con = this.getConnection();
        if(null!=con){
            try{
                Map<String, Data> cols = ReflectUtil.getCustomDataColumns(cd.getClass());
                Map<String, Object> map = ReflectUtil.getCustomDataValues(cd, cols);
                PreparedStatement ps = con.prepareStatement(this.command2String(CommandBuilder.sqlCustomDataUpdateByPrimaryKeyCommand(cd)));
                long main = -1;
                int i = 1;
                Iterator<Map.Entry<String, Data>> it = cols.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry<String, Data> me = it.next();
                    switch(me.getValue().type()){
                        case Primary:
                            main = (long)map.get(me.getKey());
                            break;
                        case Integer:
                            ps.setInt(i++, (int)map.get(me.getKey()));
                            break;
                        case Long:
                            ps.setLong(i++, (long)map.get(me.getKey()));
                            break;
                        case Boolean:
                            ps.setBoolean(i++, (boolean)map.get(me.getKey()));
                            break;
                        default:
                        case String:
                        case DateTime:
                        case YamlString:
                            ps.setString(i++, (String)map.get(me.getKey()));
                            break;
                    }
                }
                if(main>=0){
                    ps.setLong(i, main);
                    return ps.executeUpdate()!=0;
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return false;
    }

    @Override
    public List<CustomData> selectCustomData(CustomData cd, LinkedHashMap<String, Object> args) {
        List<CustomData> list = new ArrayList();
        Connection con = this.getConnection();
        if(null!=con){
            try{
                Map<String, Data> cols = ReflectUtil.getCustomDataColumns(cd.getClass());
                SelectData sd = CommandBuilder.sqlCustomDataSelectCommand(cd);
                args.forEach((k,v) -> sd.addWhere(k));
                PreparedStatement ps = con.prepareStatement(this.command2String(sd));
                int i = 1;
                Iterator<Map.Entry<String, Object>> it = args.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry<String, Object> me = it.next();
                    Data data = cols.getOrDefault(me.getKey(), null);
                    if(data!=null){
                        switch(data.type()){
                            case Primary:
                                ps.setLong(i++, (long)me.getValue());
                                break;
                            case Integer:
                                ps.setInt(i++, (int)me.getValue());
                                break;
                            case Long:
                                ps.setLong(i++, (long)me.getValue());
                                break;
                            case Boolean:
                                ps.setBoolean(i++, (boolean)me.getValue());
                                break;
                            default:
                            case String:
                            case DateTime:
                            case YamlString:
                                ps.setString(i++, (String)me.getValue());
                                break;
                        }
                    }
                }
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    YamlConfiguration yml = new YamlConfiguration();
                    Iterator<Map.Entry<String, Data>> nit = cols.entrySet().iterator();
                    while(nit.hasNext()){
                        Map.Entry<String, Data> me = nit.next();
                        String key = me.getKey();
                        Object o;
                        switch(me.getValue().type()){
                            case Integer:
                                o = rs.getInt(key);
                                break;
                            case Long:
                            case Primary:
                                o = rs.getLong(key);
                                break;
                            case Boolean:
                                o = rs.getBoolean(key);
                                break;
                            case DateTime:
                                o = this.getDateTime(key, rs);
                                break;
                            default:
                            case String:
                                o = rs.getString(key);
                                break;
                            case YamlString:
                                o = FileUtil.string2Section(rs, key);
                                break;
                        }
                        yml.set(key, o);
                    }
                    CustomData ncd = cd.loadFromYamlConfiguration(yml);
                    if(ncd!=null) list.add(ncd);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return list;
    }

    @Override
    public long deleteCustomData(CustomData cd, LinkedHashMap<String, Object> args) {
        Connection con = this.getConnection();
        if(null!=con){
            try{
                Map<String, Data> cols = ReflectUtil.getCustomDataColumns(cd.getClass());
                DeleteData dd = CommandBuilder.sqlCustomDataDeleteCommand(cd);
                args.forEach((k,v) -> dd.addWhere(k));
                PreparedStatement ps = con.prepareStatement(this.command2String(dd));
                int i = 1;
                Iterator<Map.Entry<String, Object>> it = args.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry<String, Object> me = it.next();
                    Data data = cols.getOrDefault(me.getKey(), null);
                    if(data!=null){
                        switch(data.type()){
                            case Primary:
                                ps.setLong(i++, (long)me.getValue());
                                break;
                            case Integer:
                                ps.setInt(i++, (int)me.getValue());
                                break;
                            case Long:
                                ps.setLong(i++, (long)me.getValue());
                                break;
                            case Boolean:
                                ps.setBoolean(i++, (boolean)me.getValue());
                                break;
                            default:
                            case String:
                            case DateTime:
                            case YamlString:
                                ps.setString(i++, (String)me.getValue());
                                break;
                        }
                    }
                }
                return ps.executeUpdate();
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                this.releaseConnection(con);
            }
        }
        return 0;
    }
    
    /**
     * 将SQLCommand转换为String
     * @param cmd SQLCommand
     * @return String
     */
    protected abstract String command2String(SQLCommand cmd);
    
    /**
     * 从结果集中获取时间字符串
     * @param col 列名
     * @param rs 结果集
     * @return Stirng
     * @throws java.sql.SQLException SQL异常
     */
    protected abstract String getDateTime(String col, ResultSet rs) throws SQLException;

    @Override
    public String getType() {
        return this.type;
    }
    
}
