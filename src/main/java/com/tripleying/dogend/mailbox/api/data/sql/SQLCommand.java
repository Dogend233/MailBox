package com.tripleying.dogend.mailbox.api.data.sql;

/**
 * SQL指令父类
 * @author Dogend
 */
public interface SQLCommand {
    
    public String toMySQLCommand();
    public String toSQLiteCommand();
    
}
