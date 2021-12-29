package com.tripleying.dogend.mailbox.api.data.sql;

import java.util.LinkedList;
import java.util.List;

/**
 * 插入数据
 * @author Dogend
 */
public class InsertData implements SQLCommand {
    
    private final String table;
    private final List<String> columns;
    
    public InsertData(String table){
        this.table = table;
        this.columns = new LinkedList();
    }
    
    public InsertData addColumns(String column){
        this.columns.add(column);
        return this;
    }
    
    private String toCommand(){
        StringBuilder sb = new StringBuilder().append("INSERT INTO ").append('`').append(this.table).append('`');
        StringBuilder sbk = new StringBuilder().append('(');
        StringBuilder sbv = new StringBuilder().append('(');
        for(String column:this.columns){
            sbk.append('`').append(column).append("`,");
            sbv.append("?,");
        }
        if(!this.columns.isEmpty()){
            sbk.deleteCharAt(sbk.length()-1);
            sbv.deleteCharAt(sbv.length()-1);
        }
        sbk.append(')');
        sbv.append(')');
        sb.append(' ').append(sbk.toString()).append(" VALUES ").append(sbv.toString());
        return sb.toString();
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
