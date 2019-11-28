package com.嘤嘤嘤.qwq.MailBox.Utils;

public enum SQLCommand {
    
    //获取邮件列表
    FIND_LIST_MAIL(
        "SELECT * FROM `", "_", "`"
    ),
    
    //获取玩家已领取的"system"邮件ID列表
    FIND_COLLECTED_SYSTEM_MAIL(
        "SELECT mail FROM `", "_system_collect` WHERE `user` = ? "
    ),
    
    //发送一封"system"邮件
    SEND_SYSTEM_MAIL(
        "INSERT INTO `", "_system` " +
        "(`sender`, `topic`, `text`, `sendtime`, `filename`)" +
        "VALUES (?, ?, ?, ?, ?)"
    ),
    
    //发送一封"player"邮件
    SEND_PLAYER_MAIL(
        "INSERT INTO `", "_player` " +
        "(`sender`, `topic`, `text`, `sendtime`, `filename`, `recipient`)" +
        "VALUES (?, ?, ?, ?, ?, ?)"
    ),
    
    //删除一封"system"邮件
    DELETE_SYSTEM_MAIL(
        "DELETE FROM `", "_system` WHERE `mail` = ?"
    ),
    
    //删除一封"system"邮件的已领取列表
    DELETE_COLLECTED_SYSTEM_MAIL(
        "DELETE FROM `", "_system_collect` WHERE `mail` = ?"
    ),
    
    //删除一封"player"邮件
    DELETE_PLAYER_MAIL(
        "DELETE FROM `", "_player` WHERE `mail` = ?"
    ),
    
    //设置一封"system"邮件已被某玩家领取
    COLLECT_SYSTEM_MAIL(
        "INSERT INTO `", "_system_collect` " +
        "(`mail`, `user`)" +
        "VALUES (?, ?)"
    ),
    
    //获取一封"player"邮件的收件人
    SELECT_PLAYER_MAIL(
        "SELECT `recipient` FROM `", "_player` WHERE `mail` = ?"
    ),
    
    //设置一封"player"邮件已被某玩家领取
    COLLECT_PLAYER_MAIL(
        "UPDATE `", "_player` " +
        "SET `recipient` = ? " +
        "WHERE `mail` = ?"
    ),
    
    //创建MySQL的"system"邮箱system数据表
    CREATE_SYSTEM_MYSQL(
        "CREATE TABLE IF NOT EXISTS `", "_system` (" +
        "`mail` int(11) AUTO_INCREMENT," +
        "`sender` varchar(32)," +
        "`topic` varchar(32)," +
        "`text` varchar(255)," +
        "`sendtime` datetime," +
        "`filename` varchar(32)," +
        "PRIMARY KEY (`mail`))"
    ),
    
    //创建SQLite的"system"邮箱system数据表
    CREATE_SYSTEM_SQLITE(
        "CREATE TABLE IF NOT EXISTS `", "_system` (" +
        "`mail` INTEGER PRIMARY KEY," +
        "`sender` varchar(32)," +
        "`topic` varchar(32)," +
        "`text` varchar(255)," +
        "`sendtime` datetime," +
        "`filename` varchar(32))"
    ),

    //创建"system"邮箱system_collect数据表
    CREATE_SYSTEM_COLLECT(
        "CREATE TABLE IF NOT EXISTS `", "_system_collect` (" +
        "`mail` int(11)," +
        "`user` varchar(32))"
    ),
    
    //创建MySQL的"player"邮箱player数据表
    CREATE_PLAYER_MYSQL(
        "CREATE TABLE IF NOT EXISTS `", "_player` (" +
        "`mail` int(11) AUTO_INCREMENT," +
        "`sender` varchar(32)," +
        "`recipient` varchar(255)," +
        "`topic` varchar(32)," +
        "`text` varchar(255)," +
        "`sendtime` datetime," +
        "`filename` varchar(32)," +
        "PRIMARY KEY (`mail`))"
    ),
    
    //创建SQLite的"player"邮箱player数据表
    CREATE_PLAYER_SQLITE(
        "CREATE TABLE IF NOT EXISTS `", "_player` (" +
        "`mail` INTEGER PRIMARY KEY," +
        "`sender` varchar(32)," +
        "`recipient` varchar(255)," +
        "`topic` varchar(32)," +
        "`text` varchar(255)," +
        "`sendtime` datetime," +
        "`filename` varchar(32))"
    );

    private String command_1;
    private String command_2;
    private String command_3;

    SQLCommand(String command_1, String command_2)
    {
        this.command_1 = command_1;
        this.command_2 = command_2;
    }
    
    SQLCommand(String command_1, String command_2, String command_3)
    {
        this.command_1 = command_1;
        this.command_2 = command_2;
        this.command_3 = command_3;
    }
    
    public String commandToString(String prefix)
    {
        return command_1+prefix+command_2;
    }
    
    public String commandToString(String prefix, String type)
    {
        return command_1+prefix+command_2+type+command_3;
    }
}
