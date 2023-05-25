package com.tripleying.dogend.mailbox.data;

import com.tripleying.dogend.mailbox.api.mail.SystemMail;
import com.tripleying.dogend.mailbox.api.data.sql.CreateTable;
import com.tripleying.dogend.mailbox.api.data.sql.DeleteData;
import com.tripleying.dogend.mailbox.api.data.sql.InsertData;
import com.tripleying.dogend.mailbox.api.data.sql.LastData;
import com.tripleying.dogend.mailbox.api.data.sql.LastInsertID;
import com.tripleying.dogend.mailbox.api.data.sql.SelectData;
import com.tripleying.dogend.mailbox.api.data.sql.UpdateData;
import com.tripleying.dogend.mailbox.util.ReflectUtil;
import java.util.Map;
import com.tripleying.dogend.mailbox.api.data.Data;
import com.tripleying.dogend.mailbox.api.data.DataType;
import com.tripleying.dogend.mailbox.api.mail.CustomData;

/**
 * SQL指令构造器
 * @author Dogend
 */
public class CommandBuilder {
    
    public static CreateTable sqlPlayerDataCreateCommand(){
        return new CreateTable("mailbox_player_data")
                .addString("name", 36)
                .addString("uuid", 36)
                .addYamlString("data");
    }
    
    public static InsertData sqlPlayerDataInsertCommand(){
        return new InsertData("mailbox_player_data")
                .addColumns("name")
                .addColumns("uuid")
                .addColumns("data");
    }
    
    public static SelectData sqlPlayerDataSelectCommand(){
        return new SelectData("mailbox_player_data")
                .addWhere("uuid");
    }
    
    public static SelectData sqlPlayerDataSelectAllCommand(){
        return new SelectData("mailbox_player_data");
    }
    
    public static UpdateData sqlPlayerDataUpdateCommand(){
        return new UpdateData("mailbox_player_data")
                .addSet("name")
                .addSet("data")
                .addWhere("uuid");
    }
    
    public static CreateTable sqlPersonMailCreateCommand(){
        return new CreateTable("mailbox_person_mail")
                .addString("uuid", 36)
                .addString("type", 10)
                .addLong("id")
                .addString("title", 50)
                .addYamlString("body")
                .addString("sender", 36)
                .addDateTime("sendtime")
                .addBoolean("received")
                .addYamlString("attach");
    }
    
    public static SelectData sqlPersonMailSelectCommand(){
        return new SelectData("mailbox_person_mail")
                .addWhere("uuid");
    }
    
    public static InsertData sqlPersonMailInsertCommand(){
        return new InsertData("mailbox_person_mail")
                .addColumns("uuid")
                .addColumns("type")
                .addColumns("id")
                .addColumns("title")
                .addColumns("body")
                .addColumns("sender")
                .addColumns("sendtime")
                .addColumns("received")
                .addColumns("attach");
    }
    
    public static UpdateData sqlPersonMailReceiveCommand(){
        return new UpdateData("mailbox_person_mail")
                .addSet("received")
                .addWhere("uuid")
                .addWhere("type")
                .addWhere("id");
    }
    
    public static DeleteData sqlPersonMailDeleteCommand(){
        return new DeleteData("mailbox_person_mail")
                .addWhere("uuid")
                .addWhere("type")
                .addWhere("id")
                .addWhere("received");
    }
    
    public static DeleteData sqlPersonMailClearCommand(){
        return new DeleteData("mailbox_person_mail")
                .addWhere("uuid")
                .addWhere("received");
    }
    
    public static CreateTable sqlSystemMailCreateCommand(String type, Class<? extends SystemMail> clazz){
        Map<String, Data> cols = ReflectUtil.getSystemMailColumns(clazz);
        CreateTable cmd = new CreateTable(getSystemMailTable(type))
                .setPrimaryKey("id")
                .addString("title", 50)
                .addYamlString("body")
                .addString("sender", 36)
                .addDateTime("sendtime")
                .addYamlString("attach");
        cols.forEach((f,dc) -> {
            switch(dc.type()){
                case Integer:
                    cmd.addInt(f);
                    break;
                case Long:
                    cmd.addLong(f);
                    break;
                case Boolean:
                    cmd.addBoolean(f);
                    break;
                case DateTime:
                    cmd.addDateTime(f);
                    break;
                default:
                case String:
                    cmd.addString(f, dc.size());
                    break;
                case YamlString:
                    cmd.addYamlString(f);
                    break;
            }
        });
        return cmd;
    }
    
    public static LastData sqlSystemMailLastDataIDCommand(String type){
        return new LastData(getSystemMailTable(type));
    }
    
    public static SelectData sqlSystemMailSelectCommand(String type){
        return new SelectData(getSystemMailTable(type));
    }
    
    public static InsertData sqlSystemMailInsertCommand(String type, Class<? extends SystemMail> clazz){
        Map<String, Data> cols = ReflectUtil.getSystemMailColumns(clazz);
        InsertData cmd = new InsertData(getSystemMailTable(type))
                .addColumns("title")
                .addColumns("body")
                .addColumns("sender")
                .addColumns("sendtime")
                .addColumns("attach");
        cols.forEach((f,dc) -> cmd.addColumns(f));
        return cmd;
    }
    
    public static DeleteData sqlSystemMailDeleteCommand(String type){
        return new DeleteData(getSystemMailTable(type))
                .addWhere("id");
    }
    
    private static String getSystemMailTable(String type){
        return "mailbox_system_".concat(type).concat("_mail");
    }
    
    public static LastInsertID sqlLastInsertIDCommand(){
        return new LastInsertID();
    }
    
    public static CreateTable sqlCustomDataCreateCommand(CustomData cd){
        Map<String, Data> cols = ReflectUtil.getCustomDataColumns(cd.getClass());
        CreateTable cmd = new CreateTable(cd.getName());
        cols.forEach((f,dc) -> {
            switch(dc.type()){
                case Integer:
                    cmd.addInt(f);
                    break;
                case Long:
                    cmd.addLong(f);
                    break;
                case Boolean:
                    cmd.addBoolean(f);
                    break;
                case DateTime:
                    cmd.addDateTime(f);
                    break;
                default:
                case String:
                    cmd.addString(f, dc.size());
                    break;
                case YamlString:
                    cmd.addYamlString(f);
                    break;
                case Primary:
                    cmd.setPrimaryKey(f);
                    break;
            }
        });
        return cmd;
    }
    
    public static InsertData sqlCustomDataInsertCommand(CustomData cd){
        Map<String, Data> cols = ReflectUtil.getCustomDataColumns(cd.getClass());
        InsertData cmd = new InsertData(cd.getName());
        cols.forEach((f,dc) -> {if(!dc.type().equals(DataType.Primary))cmd.addColumns(f);} );
        return cmd;
    }
    
    public static UpdateData sqlCustomDataUpdateByPrimaryKeyCommand(CustomData cd){
        Map<String, Data> cols = ReflectUtil.getCustomDataColumns(cd.getClass());
        UpdateData cmd = new UpdateData(cd.getName());
        cols.forEach((f,dc) -> {
            if(dc.type().equals(DataType.Primary)){
                cmd.addWhere(f);
            }else{
                cmd.addSet(f);
            }
        });
        return cmd;
    }
    
    public static SelectData sqlCustomDataSelectCommand(CustomData cd){
        return new SelectData(cd.getName());
    }
    
    public static DeleteData sqlCustomDataDeleteCommand(CustomData cd){
        return new DeleteData(cd.getName());
    }
    
}
