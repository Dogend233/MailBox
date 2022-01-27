package com.tripleying.dogend.mailbox.util;

import com.tripleying.dogend.mailbox.api.data.DataType;
import com.tripleying.dogend.mailbox.api.mail.SystemMail;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;
import com.tripleying.dogend.mailbox.api.data.Data;
import com.tripleying.dogend.mailbox.api.mail.CustomData;

/**
 * 反射工具
 * @author Dogend
 */
public class ReflectUtil {
    
    /**
     * 获取类的方法
     * @param clazz 类
     * @param methodName 方法名
     * @param params 参数列表
     * @return 方法
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        Method method = null;
        try {
            method = clazz.getMethod(methodName, params);
        } catch (NoSuchMethodException | SecurityException e) {}
        return method;
    }
    
    /**
     * 获取类的字段
     * @param clazz 类
     * @param fieldNames 字段名
     * @return 字段
     * @throws Exception 异常
     */
    public static Field findField(Class<?> clazz, String ... fieldNames) throws Exception {
        Exception failed = null;
        for (String fieldName : fieldNames) {
            try {
                Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            }
            catch (Exception e) {
                failed = e;
            }
        }
        return null;
    }
    
    /**
     * 获取私有字段值
     * @param <T> T
     * @param <E> E
     * @param classToAccess 类
     * @param instance 实例
     * @param fieldNames 字段名
     * @return T
     * @throws Exception 异常
     */
    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String ... fieldNames) throws Exception {
        return (T)findField(classToAccess, fieldNames).get(instance);
    }
    
    /**
     * 获取系统邮件需要在数据源中创建的字段及类型
     * @param clazz 继承系统邮件的类
     * @return Map
     */
    public static Map<String, Data> getSystemMailColumns(Class<? extends SystemMail> clazz){
        Map<String, Data> map = new LinkedHashMap();
        List<Field> fields = new ArrayList();
        Class sc = clazz;
        do{
            fields.addAll(Arrays.asList(sc.getDeclaredFields()));
            sc = sc.getSuperclass();
        }while(sc!=SystemMail.class);
        for(Field field:fields){
            if(field.isAnnotationPresent(Data.class)){
                map.put(field.getName(), field.getDeclaredAnnotation(Data.class));
            }
        }
        return map;
    }
    
    /**
     * 获取系统邮件需要在数据源中创建的字段及值
     * @param sm 系统邮件实例
     * @param cols 系统邮件在数据源中创建的字段及类型
     * @return Map
     * @throws java.lang.Exception 异常
     */
    public static Map<String, Object> getSystemMailValues(SystemMail sm, Map<String, Data> cols) throws Exception {
        Map<String, Object> map = new LinkedHashMap();
        Map<String, Object> temp = new LinkedHashMap();
        Map<Class, Field[]> fields = new LinkedHashMap();
        Class sc = sm.getClass();
        do{
            fields.put(sc, sc.getDeclaredFields());
            sc = sc.getSuperclass();
        }while(sc!=SystemMail.class);
        Iterator<Map.Entry<Class, Field[]>> it = fields.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Class, Field[]> me = it.next();
            for(Field f:me.getValue()){
                if(f.isAnnotationPresent(Data.class)){
                    f.setAccessible(true);
                    temp.put(f.getName(), f.get(sm));
                }
            }
        }
        cols.forEach((c,dc) -> {
            if(dc.type()==DataType.YamlString){
                YamlConfiguration yml = new YamlConfiguration();
                yml.set(c, temp.get(c));
                map.put(c, yml.saveToString());
            }else{
                map.put(c, temp.get(c));
            }
        });
        return map;
    }
    
    /**
     * 获取自定义数据需要在数据源中创建的字段及类型
     * @param cd 继承自定义数据的类
     * @since 3.1.0
     * @return Map
     */
    public static Map<String, Data> getCustomDataColumns(Class<? extends CustomData> cd){
        Map<String, Data> map = new LinkedHashMap();
        List<Field> fields = new ArrayList();
        Class sc = cd;
        do{
            fields.addAll(Arrays.asList(sc.getDeclaredFields()));
            sc = sc.getSuperclass();
        }while(sc!=CustomData.class);
        for(Field field:fields){
            if(field.isAnnotationPresent(Data.class)){
                map.put(field.getName(), field.getDeclaredAnnotation(Data.class));
            }
        }
        return map;
    }
    
    /**
     * 获取自定义数据需要在数据源中创建的字段及值
     * @param cd 自定义数据实例
     * @param cols 自定义数据在数据源中创建的字段及类型
     * @since 3.1.0
     * @return Map
     * @throws java.lang.Exception 异常
     */
    public static Map<String, Object> getCustomDataValues(CustomData cd, Map<String, Data> cols) throws Exception {
        Map<String, Object> map = new LinkedHashMap();
        Map<String, Object> temp = new LinkedHashMap();
        Map<Class, Field[]> fields = new LinkedHashMap();
        Class sc = cd.getClass();
        do{
            fields.put(sc, sc.getDeclaredFields());
            sc = sc.getSuperclass();
        }while(sc!=CustomData.class);
        Iterator<Map.Entry<Class, Field[]>> it = fields.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Class, Field[]> me = it.next();
            for(Field f:me.getValue()){
                if(f.isAnnotationPresent(Data.class)){
                    f.setAccessible(true);
                    temp.put(f.getName(), f.get(cd));
                }
            }
        }
        cols.forEach((c,dc) -> {
            if(dc.type()==DataType.YamlString){
                YamlConfiguration yml = new YamlConfiguration();
                yml.set(c, temp.get(c));
                map.put(c, yml.saveToString());
            }else{
                map.put(c, temp.get(c));
            }
        });
        return map;
    }
    
}
