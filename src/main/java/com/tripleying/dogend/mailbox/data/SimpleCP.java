package com.tripleying.dogend.mailbox.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.configuration.ConfigurationSection;

/**
 * 简单连接池
 * @author Dogend
 */
public class SimpleCP {
    
    /**
     * 空闲连接
     */
    private final List<Connection> free = new CopyOnWriteArrayList<>();
    /**
     * 活动连接
     */
    private final List<Connection> active = new CopyOnWriteArrayList<>();
    /**
     * 大小
     */
    private final AtomicInteger size;
    /**
     * 连接URL
     */
    private final String url;
    /**
     * 做小空闲连接数量
     */
    private final int min;
    /**
     * 最大连接数量
     */
    private final int max;
    /**
     * 连接超时时间
     */
    private final int timeout;

    public SimpleCP(ConfigurationSection config, String url){
        this.min = config.getInt("min", 10);
        this.max = config.getInt("max", 20);
        this.timeout = config.getInt("timeout", 3000);
        this.size = new AtomicInteger(0);
        this.url= url;
    }
    
    /**
     * 启用连接池
     * @return boolean
     */
    public boolean enable(){
        for(int i=0; i<this.min; i++){
            Connection con = createConnection();
            if(con!=null){
                this.free.add(con);
            }
        }
        return !this.free.isEmpty();
    }
    
    /**
     * 创建连接
     * @return Connection
     */
    public Connection createConnection(){
        try{
            Connection connection = DriverManager.getConnection(this.url);
            this.size.addAndGet(1);
            return connection;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 获取连接
     * @return Connection
     */
    public synchronized Connection getConnection() {
        Connection connection = null;
        if(size.get()<max){
            if(free.size()>0){
                connection  = free.remove(0);
            }else{
                connection = createConnection();
            }
            if(isAvailable(connection)){
                active.add(connection);
            }else{
                size.decrementAndGet();
                connection = getConnection();
            }
        }else{
            try{
                wait(timeout);
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * 释放连接
     * @param connection 连接
     */
    public synchronized void releaseConnection(Connection connection) {
        if(isAvailable(connection)){
            if(free.size()<max){
                free.add(connection);
            }else{
                try{
                    connection.close();
                }catch (SQLException ex){
                    ex.printStackTrace();
                }
            }
            active.remove(connection);
            size.decrementAndGet();
            notifyAll();
        }else{
            throw new RuntimeException("连接回收异常");
        }
    }

    /**
     * 关闭连接池
     */
    public void close(){
        for(Connection connection : free){
            if(connection != null){
                try{
                    connection.close();
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
                connection = null;
            }
        }
        for(Connection connection : active){
            if(connection != null){
                try{
                    connection.close();
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
                connection = null;
            }
        }
    }
    
    /**
     * 连接是否可用
     * @param connection 连接
     * @return boolean
     */
    public boolean isAvailable(Connection connection){
        try {
            if(null!=connection && !connection.isClosed()){
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
}
