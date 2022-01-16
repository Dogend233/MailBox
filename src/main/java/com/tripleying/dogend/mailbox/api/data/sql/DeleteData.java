package com.tripleying.dogend.mailbox.api.data.sql;

import java.util.LinkedList;
import java.util.List;

/**
 * 删除数据
 * @author Dogend
 */
public class DeleteData implements SQLCommand {
    
    private final String table;
    private final List<String> where;
    
    public DeleteData(String table){
        this.table = table;
        this.where = new LinkedList();
    }
    
    public DeleteData addWhere(String column){
        this.where.add(column);
        return this;
    }
    
    private String toCommand(){
        StringBuilder sb = new StringBuilder().append("DELETE FROM `").append(this.table).append('`');
        if(!this.where.isEmpty()){
            sb.append(" WHERE");
            for(String column:this.where){
                sb.append(" `").append(column).append("` = ? AND");
            }
            sb.delete(sb.length()-3, sb.length());
        }
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
