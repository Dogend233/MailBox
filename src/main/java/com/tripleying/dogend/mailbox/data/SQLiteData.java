package com.tripleying.dogend.mailbox.data;

import com.tripleying.dogend.mailbox.MailBox;
import com.tripleying.dogend.mailbox.api.data.sql.SQLCommand;
import com.tripleying.dogend.mailbox.util.FileUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * SQLite数据
 * @author Dogend
 */
public class SQLiteData extends SQLData {
    
    private final String url;
    private Connection connection;
    
    public SQLiteData(YamlConfiguration yml){
        super(yml.getString("sqlite.type", "sqlite"));
        url = "jdbc:sqlite:plugins/MailBox/".concat(yml.getString("sqlite.database", "MailBox")).concat(".db");
    }

    @Override
    public boolean enable(){
        try{
            if(MailBox.getMCVersion()<1.11) Class.forName("org.sqlite.JDBC");
            this.createConnection();
            return !this.connection.isClosed();
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    
    private void createConnection(){
        try{
            FileUtil.getMailBoxFolder();
            this.connection = DriverManager.getConnection(url);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
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
    public void releaseConnection(Connection con) {}

    @Override
    public void close() {
        if(this.connection!=null){
            try {
                this.connection.close();
            } catch (SQLException ex) {}
        };
    }

    @Override
    protected String command2String(SQLCommand cmd) {
        return cmd.toSQLiteCommand();
    }
    
    @Override
    protected String getDateTime(String col, ResultSet rs) throws SQLException{
        return rs.getString(col);
    }
    
}