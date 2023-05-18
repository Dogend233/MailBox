package com.tripleying.dogend.mailbox.api.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据字段注解
 * @author Dogend
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Data {
    
    // 字段类型
    public DataType type();
    
    // 字段长度(String类型用)
    public int size() default 50;
    
}
