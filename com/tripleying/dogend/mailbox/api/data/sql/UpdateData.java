package com.tripleying.dogend.mailbox.api.data.sql;

import java.util.LinkedList;
import java.util.List;

/**
 * 更新数据
 * @author Dogend
 */
public class UpdateData implements SQLCommand {
    
    private final String table;
    private final List<String> set;
    private final List<String> where;
    
    public UpdateData(String table){
        this.table = table;
        this.set = new LinkedList();
        this.where = new LinkedList();
    }
    
    public UpdateData addSet(String column){
        this.set.add(column);
        return this;
    }
    
    public UpdateData addWhere(String column){
        this.where.add(column);
        return this;
    }
    
    private String toCommand(){
        if(this.set.isEmpty() || this.where.isEmpty()) return "";
        StringBuilder sb = new StringBuilder().append("UPDATE `").append(this.table).append("` SET ");
        if(this.set.size()==1){
            sb.append('`').append(this.set.get(0)).append("` = ?");
        }else{
            for(String column:this.set){
                sb.append('`').append(column).append("` = ? ,");
            }
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(" WHERE");
        for(String column:this.where){
            sb.append(" `").append(column).append("` = ? AND");
        }
        sb.delete(sb.length()-3, sb.length());
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
