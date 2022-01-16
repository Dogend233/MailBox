package com.tripleying.dogend.mailbox.api.data.sql;

import java.util.LinkedList;
import java.util.List;

/**
 * 表中最后一条数据(按ID排列)
 * @author Dogend
 */
public class LastData implements SQLCommand {
    
    private final String table;
    private final List<String> select;
    
    public LastData(String table){
        this.table = table;
        this.select = new LinkedList();
    }
    
    public LastData addSelect(String column){
        this.select.add(column);
        return this;
    }
    
    private String toCommand(){
        StringBuilder sb = new StringBuilder().append("SELECT ");
        if(this.select.isEmpty()){
            sb.append("*");
        }else{
            for(String column:this.select){
                sb.append('`').append(column).append("`,");
            }
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(" FROM `").append(this.table).append("` order by `id` desc limit 1");
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
