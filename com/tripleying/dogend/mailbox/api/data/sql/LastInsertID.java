package com.tripleying.dogend.mailbox.api.data.sql;

/**
 * 上次插入的主键ID
 * @author Dogend
 */
public class LastInsertID implements SQLCommand {
    
    private final static String mysql = "SELECT LAST_INSERT_ID() AS `id`";
    private final static String sqlite = "SELECT LAST_INSERT_ROWID() AS `id`";

    @Override
    public String toMySQLCommand() {
        return mysql;
    }

    @Override
    public String toSQLiteCommand() {
        return sqlite;
    }
    
}
