package com.tripleying.dogend.mailbox.api.data.sql;

import java.util.LinkedList;
import java.util.List;

/**
 * 选择数据
 * @author Dogend
 */
public class SelectData implements SQLCommand {
    
    private String order;
    private boolean desc;
    private boolean limit;
    private final String table;
    private final List<String> select;
    private final List<String> where;
    private final List<String> between;
    
    public SelectData(String table){
        this.order = null;
        this.desc = false;
        this.limit = false;
        this.table = table;
        this.select = new LinkedList();
        this.where = new LinkedList();
        this.between = new LinkedList();
    }
    
    public SelectData addSelectWithoutBackquote(String column){
        this.select.add(column);
        return this;
    }
    
    public SelectData addSelect(String column){
        this.select.add("`"+column+"`");
        return this;
    }
    
    public SelectData addWhere(String column){
        this.where.add(column);
        return this;
    }
    
    public SelectData addBetween(String column){
        this.between.add(column);
        return this;
    }
    
    public SelectData orderBy(String column){
        this.order = column;
        return this;
    }
    
    public SelectData desc(boolean desc){
        this.desc = desc;
        return this;
    }
    
    public SelectData setLimit(boolean limit){
        this.limit = limit;
        return this;
    }
    
    private String toCommand(){
        StringBuilder sb = new StringBuilder().append("SELECT ");
        if(this.select.isEmpty()){
            sb.append("*");
        }else{
            for(String column:this.select){
                sb.append(column).append(",");
            }
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(" FROM `").append(this.table).append('`');
        if(!this.where.isEmpty() || !this.between.isEmpty()){
            sb.append(" WHERE");
            for(String column:this.where){
                sb.append(" `").append(column).append("` = ? AND");
            }
            for(String column:this.between){
                sb.append(" (`").append(column).append("` BETWEEN ? AND ? ) AND");
            }
            sb.delete(sb.length()-3, sb.length());
        }
        if(this.order!=null){
            sb.append(" ORDER BY `").append(this.order).append("`");
            if(this.desc){
                sb.append(" DESC");
            }
        }
        if(this.limit){
            sb.append(" LIMIT ?, ?");
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
