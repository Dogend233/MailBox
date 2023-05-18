package com.tripleying.dogend.mailbox.api.data;

/**
 * 邮件字段类型
 * 用于在数据库中创建字段, 进行查询等操作
 * SystemMail默认创建字段id, title, body, sender, sendtime, attach
 * @author Dogend
 */
public enum DataType {
    
    Integer,// 整数
    Long,// 长整数
    Boolean,// 布尔值
    DateTime,// 日期
    String,// 字符串
    YamlString,// YML字符串
    /**
     * 自增主键 (CustomData用)
     * @since 3.1.0
     */
    Primary;
    
}