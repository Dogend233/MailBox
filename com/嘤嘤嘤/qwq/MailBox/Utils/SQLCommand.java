package com.嘤嘤嘤.qwq.MailBox.Utils;

public enum SQLCommand {
    
    //获取[ALL]邮件列表
    FIND_LIST_ALL_MAIL(
        "SELECT * FROM `", "_all`"
    ),
    
    //获取玩家可领取的[ALL]邮件ID列表
    FIND_UNCOLLECTED_ALL_MAIL(
        "SELECT mail FROM `", "_all_collect` WHERE `mail` = ? AND `user` = ? "
    ),
    
    //发送一封[ALL]邮件
    SEND_ALL_MAIL(
        "INSERT INTO `", "_all` " +
        "(`sender`, `topic`, `text`, `sendtime`, `filename`)" +
        "VALUES (?, ?, ?, ?, ?)"
    ),
    
    //删除一封[ALL]邮件
    DELETE_ALL_MAIL(
        "DELETE FROM `", "_all` WHERE `mail` = ?"
    ),
    
    //删除一封[ALL]邮件的已领取列表
    DELETE_COLLECTED_ALL_MAIL(
        "DELETE FROM `", "_all_collect` WHERE `mail` = ?"
    ),
    
    //设置一封[ALL]邮件已被某玩家领取
    COLLECT_ALL_MAIL(
        "INSERT INTO `", "_all_collect` " +
        "(`mail`, `user`)" +
        "VALUES (?, ?)"
    ),
    
    //创建[ALL]邮箱all数据表
    CREATE_ALL(
        "CREATE TABLE IF NOT EXISTS `", "_all` (" +
        "`mail` int(11) NOT NULL AUTO_INCREMENT," +
        "`sender` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '管理员'," +
        "`topic` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '无'," +
        "`text` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '空'," +
        "`sendtime` datetime NOT NULL DEFAULT '1970-01-01 00:00:00'," +
        "`filename` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0'," +
        "PRIMARY KEY (`mail`) USING BTREE)"
    ),
    
    //创建[ALL]邮箱all_collect数据表
    CREATE_ALL_COLLECT(
        "CREATE TABLE IF NOT EXISTS `", "_all_collect` (" +
        "`mail` int(11) NOT NULL," +
        "`user` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL)"
    );

    private String command_1;
    private String command_2;

    SQLCommand(String command_1, String command_2)
    {
        this.command_1 = command_1;
        this.command_2 = command_2;
    }
    public String commandToString(String prefix)
    {
        return command_1+prefix+command_2;
    }
}
