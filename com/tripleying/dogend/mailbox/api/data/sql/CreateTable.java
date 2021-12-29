package com.tripleying.dogend.mailbox.api.data.sql;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 创建表
 * @author Dogend
 */
public class CreateTable implements SQLCommand {
    
    private String primary;
    private final String table;
    private final Map<String, String> columns;
    
    public CreateTable(String table){
        this.table = table;
        this.columns = new LinkedHashMap();
    }
    
    public CreateTable setPrimaryKey(String column){
        this.primary = column;
        return this;
    }
    
    public CreateTable addInt(String column){
        this.columns.put(column, "int(1)");
        return this;
    }
    
    public CreateTable addLong(String column){
        this.columns.put(column, "bigint(1)");
        return this;
    }
    
    public CreateTable addBoolean(String column){
        this.columns.put(column, "tinyint(1)");
        return this;
    }
    
    public CreateTable addDateTime(String column){
        this.columns.put(column, "datetime");
        return this;
    }
    
    public CreateTable addString(String column, int length){
        this.columns.put(column, "varchar(N)".replace("N", Integer.toString(length)));
        return this;
    }
    
    public CreateTable addYamlString(String column){
        this.columns.put(column, "mediumtext");
        return this;
    }
    
    @Override
    public String toMySQLCommand(){
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append('`').append(this.table).append("` (");
        if(this.primary!=null){
            sb.append('`').append(this.primary).append("` bigint(1) AUTO_INCREMENT,");
        }
        for(Entry<String, String> entry:this.columns.entrySet()){
            sb.append('`').append(entry.getKey()).append("` ").append(entry.getValue()).append(',');
        }
        if(this.primary!=null){
            sb.append("PRIMARY KEY (`").append(this.primary).append("`)");
        }else if(!this.columns.isEmpty()){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(')');
        return sb.toString();
    }
    
    @Override
    public String toSQLiteCommand(){
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append('`').append(this.table).append("` (");
        if(this.primary!=null){
            sb.append('`').append(this.primary).append("` INTEGER PRIMARY KEY AUTOINCREMENT,");
        }
        for(Entry<String, String> entry:this.columns.entrySet()){
            sb.append('`').append(entry.getKey()).append("` ").append(entry.getValue()).append(',');
        }
        if(!this.columns.isEmpty()){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(')');
        return sb.toString();
    }
    
}
