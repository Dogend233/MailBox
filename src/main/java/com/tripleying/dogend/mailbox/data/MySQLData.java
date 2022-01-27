package com.tripleying.dogend.mailbox.data;

import com.tripleying.dogend.mailbox.api.data.sql.SQLCommand;
import com.tripleying.dogend.mailbox.util.TimeUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * MySQL数据
 * @author Dogend
 */
public class MySQLData extends SQLData {
    
    private Connection connection;
    private final boolean encp;
    private final String url;
    /**
     * 连接池
     */
    protected final SimpleCP cp;
    
    public MySQLData(YamlConfiguration yml){
        super(yml.getString("mysql.type", "mysql"));
        this.url = "jdbc:mysql://".concat(yml.getString("mysql.host")).concat(":").concat(Integer.toString(yml.getInt("mysql.port"))).concat("/").concat(yml.getString("mysql.database"))
                    .concat("?user=").concat(yml.getString("mysql.username")).concat("&password=").concat(yml.getString("mysql.password"))
                    .concat("&autoReconnect=true&autoReconnectForPools=true&useSSL=false");
        this.encp = yml.getBoolean("mysql.encp", false);
        if(this.encp) this.cp = new SimpleCP(yml.getConfigurationSection("simplecp"), url);
        else this.cp = null;
    }

    @Override
    public boolean enable(){
        try{
            if(this.encp){
                return this.cp.enable();
            }else{
                this.createConnection();
                return !this.connection.isClosed();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    
    private void createConnection(){
        try{
            this.connection = DriverManager.getConnection(url);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return this.encp?this.getCpConnection():this.getNoCpConnection();
    }
    
    public Connection getCpConnection() {
        Connection conn = this.cp.getConnection();
        if(this.cp.isAvailable(conn)){
            return conn;
        }else{
            this.cp.releaseConnection(conn);
            return null;
        }
    }
    
    public Connection getNoCpConnection() {
        try {
            if(this.connection==null || this.connection.isClosed()){
                this.createConnection();;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            if(null!=this.connection && !this.connection.isClosed()){
                return this.connection;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void releaseConnection(Connection con) {
        if(this.encp) this.cp.releaseConnection(con);
    }

    @Override
    public void close() {
        if(this.encp) this.cp.close();
        else {
            if(this.connection!=null){
                try {
                    this.connection.close();
                } catch (SQLException ex) {}
            };
        }
    }

    @Override
    protected String command2String(SQLCommand cmd) {
        return cmd.toMySQLCommand();
    }
    
    @Override
    protected String getDateTime(String col, ResultSet rs) throws SQLException{
        return TimeUtil.long2String(rs.getTimestamp(col).getTime());
    }
    
}
