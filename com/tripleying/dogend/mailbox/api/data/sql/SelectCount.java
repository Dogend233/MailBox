package com.tripleying.dogend.mailbox.api.data.sql;

/**
 * 查询表中数据数量
 * @author Dogend
 */
public class SelectCount implements SQLCommand {
    
    private final String table;
    
    public SelectCount(String table){
        this.table = table;
    }
    
    private String toCommand(){
        return new StringBuilder().append("SELECT COUNT(*) FROM `").append(table).append('`').toString();
    }

    @Override
    public String toMySQLCommand() {
        return this.toCommand();
    }

    @Override
    public String toSQLiteCommand() {
        return this.toCommand();
    }
    
}
