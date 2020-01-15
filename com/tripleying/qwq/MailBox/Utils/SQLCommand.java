package com.tripleying.qwq.MailBox.Utils;

public enum SQLCommand {
    
    //获取邮件列表
    SELECT_LIST_MAIL(
        "SELECT * FROM `", "_", "`"
    ),
    
    //获取玩家已领取的邮件ID列表
    SELECT_COLLECTED_MAIL(
        "SELECT mail FROM `", "_", "_collect` WHERE `user` = ? "
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
    
    //发送一封"permission"邮件
    SEND_PERMISSION_MAIL(
        "INSERT INTO `", "_permission` " +
        "(`sender`, `topic`, `text`, `sendtime`, `filename`, `permission`)" +
        "VALUES (?, ?, ?, ?, ?, ?)"
    ),
    
    //发送一封"date"邮件
    SEND_DATE_MAIL(
        "INSERT INTO `", "_date` " +
        "(`sender`, `topic`, `text`, `sendtime`, `filename`, `deadline`)" +
        "VALUES (?, ?, ?, ?, ?, ?)"
    ),
    
    //发送一封"times"邮件
    SEND_TIMES_MAIL(
        "INSERT INTO `", "_times` " +
        "(`sender`, `topic`, `text`, `sendtime`, `filename`, `times`)" +
        "VALUES (?, ?, ?, ?, ?, ?)"
    ),
    
    //发送一封"keytimes"邮件
    SEND_KEYTIMES_MAIL(
        "INSERT INTO `", "_keytimes` " +
        "(`sender`, `topic`, `text`, `sendtime`, `filename`, `times`, `key`)" +
        "VALUES (?, ?, ?, ?, ?, ?, ?)"
    ),
    
    //发送一封"cdkey"邮件
    SEND_CDKEY_MAIL(
        "INSERT INTO `", "_cdkey` " +
        "(`sender`, `topic`, `text`, `sendtime`, `filename`, `only`)" +
        "VALUES (?, ?, ?, ?, ?, ?)"
    ),
    
    //删除一封邮件
    DELETE_MAIL(
        "DELETE FROM `", "_", "` WHERE `mail` = ?"
    ),
    
    //删除一封邮件的已领取列表
    DELETE_COLLECTED_MAIL(
        "DELETE FROM `", "_", "_collect` WHERE `mail` = ?"
    ),
    
    //删除一封cdkey邮件的cdkey
    DELETE_CDKEY_MAIL(
        "DELETE FROM `", "_cdkey_list` WHERE `mail` = ?"
    ),
    
    //删除一个cdkey
    DELETE_CDKEY(
        "DELETE FROM `", "_cdkey_list` WHERE `cdkey` = ?"
    ),
    
    //获取一封"times"或"keytimes"邮件的次数
    SELECT_TIMES_MAIL(
        "SELECT `times` FROM `", "_", "` WHERE `mail` = ?"
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
    
    //设置一封"times"邮件已被某玩家领取
    COLLECT_TIMES_MAIL(
        "UPDATE `", "_", "` " +
        "SET `times` = ? " +
        "WHERE `mail` = ?"
    ),
    
    //发送一个Cdkey
    SEND_CDKEY(
        "INSERT INTO `", "_cdkey_list` " +
        "(`cdkey`, `mail`)" +
        "VALUES (?, ?)"
    ),
    
    //获取一封邮件的CDKEY
    SELECT_MAIL_CDKEY(
        "SELECT `cdkey` FROM `", "_cdkey_list` WHERE `mail` = ?"
    ),
    
    //获取所有CDKEY
    SELECT_ALL_CDKEY(
        "SELECT * FROM `", "_cdkey_list`"
    ),
    
    //设置一封"cdkey"邮件已被某玩家领取
    COLLECT_CDKEY_MAIL(
        "DELETE FROM `", "_cdkey_list` WHERE `cdkey` = ?"
    ),
    
    //获取cdkey对应的邮件
    SELECT_CDKEY2MAIL_MAIL(
        "SELECT `mail` FROM `", "_cdkey_list` WHERE `cdkey` = ?"
    ),
    
    //获取一封邮件的已领取人
    SELECT_MAIL(
        "SELECT * FROM `", "_", "_collect` WHERE `mail` = ? AND `user` = ?"
    ),
    
    //设置一封邮件已被某玩家领取
    COLLECT_MAIL(
        "INSERT INTO `", "_", "_collect` " +
        "(`mail`, `user`)" +
        "VALUES (?, ?)"
    ),
    
    //发送一个附件
    SEND_FILE(
        "INSERT INTO `", "_file` " +
        "(`commands`, `descriptions`, `coin`, `point`, `items`, `filename`, `type`)" +
        "VALUES (?, ?, ?, ?, ?, ?, ?)"
    ),
    
    //更新一个附件
    UPDATE_FILE(
        "UPDATE `", "_file` " +
        "SET (`commands` = ? , `descriptions` = ? , `coin` = ? , `point` = ? , `items` = ? ) " +
        "WHERE `filename` = ? AND `type` = ?"
    ),
    
    //获取一个附件
    SELECT_FILE(
        "SELECT * FROM `", "_file` WHERE `filename` = ? AND `type` = ?"
    ),
    
    //获取一个类型所有附件名字
    SELECT_FILE_NAME(
        "SELECT `filename` FROM `", "_", "`"
    ),
    
    //删除一个附件
    DELETE_FILE(
        "DELETE FROM `", "_file` WHERE `filename` = ? AND `type` = ?"
    ),
    
    //创建"system"邮箱system数据表
    CREATE_SYSTEM(
        "CREATE TABLE IF NOT EXISTS `", "_system` (" +
        "`mail` int(11) AUTO_INCREMENT," +
        "`sender` varchar(32)," +
        "`topic` varchar(32)," +
        "`text` varchar(255)," +
        "`sendtime` datetime," +
        "`filename` varchar(32)," +
        "PRIMARY KEY (`mail`))"
    ),

    //创建"system"邮箱system_collect数据表
    CREATE_SYSTEM_COLLECT(
        "CREATE TABLE IF NOT EXISTS `", "_system_collect` (" +
        "`mail` int(11)," +
        "`user` varchar(32))"
    ),
    
    //创建"player"邮箱player数据表
    CREATE_PLAYER(
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
    
    //创建"permission"邮箱permission数据表
    CREATE_PERMISSION(
        "CREATE TABLE IF NOT EXISTS `", "_permission` (" +
        "`mail` int(11) AUTO_INCREMENT," +
        "`sender` varchar(32)," +
        "`permission` varchar(32)," +
        "`topic` varchar(32)," +
        "`text` varchar(255)," +
        "`sendtime` datetime," +
        "`filename` varchar(32)," +
        "PRIMARY KEY (`mail`))"
    ),

    //创建"permission"邮箱permission_collect数据表
    CREATE_PERMISSION_COLLECT(
        "CREATE TABLE IF NOT EXISTS `", "_permission_collect` (" +
        "`mail` int(11)," +
        "`user` varchar(32))"
    ),
    
    //创建"date"邮箱date数据表
    CREATE_DATE(
        "CREATE TABLE IF NOT EXISTS `", "_date` (" +
        "`mail` int(11) AUTO_INCREMENT," +
        "`sender` varchar(32)," +
        "`deadline` datetime," +
        "`topic` varchar(32)," +
        "`text` varchar(255)," +
        "`sendtime` datetime," +
        "`filename` varchar(32)," +
        "PRIMARY KEY (`mail`))"
    ),

    //创建"date"邮箱date_collect数据表
    CREATE_DATE_COLLECT(
        "CREATE TABLE IF NOT EXISTS `", "_date_collect` (" +
        "`mail` int(11)," +
        "`user` varchar(32))"
    ),
    
    //创建"times"邮箱times数据表
    CREATE_TIMES(
        "CREATE TABLE IF NOT EXISTS `", "_times` (" +
        "`mail` int(11) AUTO_INCREMENT," +
        "`sender` varchar(32)," +
        "`times` int(11)," +
        "`topic` varchar(32)," +
        "`text` varchar(255)," +
        "`sendtime` datetime," +
        "`filename` varchar(32)," +
        "PRIMARY KEY (`mail`))"
    ),

    //创建"times"邮箱times_collect数据表
    CREATE_TIMES_COLLECT(
        "CREATE TABLE IF NOT EXISTS `", "_times_collect` (" +
        "`mail` int(11)," +
        "`user` varchar(32))"
    ),
    
    //创建"keytimes"邮箱keytimes数据表
    CREATE_KEYTIMES(
        "CREATE TABLE IF NOT EXISTS `", "_keytimes` (" +
        "`mail` int(11) AUTO_INCREMENT," +
        "`sender` varchar(32)," +
        "`times` int(11)," +
        "`key` varchar(255)," +
        "`topic` varchar(32)," +
        "`text` varchar(255)," +
        "`sendtime` datetime," +
        "`filename` varchar(32)," +
        "PRIMARY KEY (`mail`))"
    ),

    //创建"keytimes"邮箱keytimes_collect数据表
    CREATE_KEYTIMES_COLLECT(
        "CREATE TABLE IF NOT EXISTS `", "_keytimes_collect` (" +
        "`mail` int(11)," +
        "`user` varchar(32))"
    ),
    
    //创建"cdkey"邮箱cdkey数据表
    CREATE_CDKEY(
        "CREATE TABLE IF NOT EXISTS `", "_cdkey` (" +
        "`mail` int(11) AUTO_INCREMENT," +
        "`sender` varchar(32)," +
        "`only` varchar(5)," +
        "`topic` varchar(32)," +
        "`text` varchar(255)," +
        "`sendtime` datetime," +
        "`filename` varchar(32)," +
        "PRIMARY KEY (`mail`))"
    ),

    //创建"cdkey"邮箱cdkey_collect数据表
    CREATE_CDKEY_COLLECT(
        "CREATE TABLE IF NOT EXISTS `", "_cdkey_collect` (" +
        "`mail` int(11)," +
        "`user` varchar(32))"
    ),
    
    //创建"cdkey"邮箱cdkey_list数据表
    CREATE_CDKEY_LIST(
        "CREATE TABLE IF NOT EXISTS `", "_cdkey_list` (" +
        "`cdkey` varchar(32)," +
        "`mail` int(11))"
    ),


    //创建统一的file数据表
    CREATE_FILE(
        "CREATE TABLE IF NOT EXISTS `", "_file` (" +
        "`type` varchar(16)," +
        "`filename` varchar(32)," +
        "`commands` varchar(255)," +
        "`descriptions` varchar(255)," +
        "`coin` varchar(22)," +
        "`point` varchar(20)," +
        "`items` text)"
    );

    private final String A;
    private final String B;
    private final String C;

    SQLCommand(String A, String B)
    {
        this.A = A;
        this.B = B;
        this.C = "";
    }
    
    SQLCommand(String A, String B, String C)
    {
        this.A = A;
        this.B = B;
        this.C = C;
    }
    
    public String commandToString(String prefix)
    {
        return A+prefix+B;
    }
    
    public String commandToString(String prefix, String type)
    {
        return A+prefix+B+type+C;
    }
    
    public String commandToStringToSQLite(String prefix){
        String sql = commandToString(prefix).replace("int(11) AUTO_INCREMENT", "INTEGER PRIMARY KEY");
        return sql.substring(0, sql.lastIndexOf(',')).concat(")");
    }
}
