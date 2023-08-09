package com.tripleying.dogend.mailbox.api.module.message;

import com.tripleying.dogend.mailbox.util.ReflectUtil;
import java.lang.reflect.Method;

/**
 * 利用反射向模块内发送消息
 * @since 3.3.2
 * @author Dogend
 */
public class ReflectMessage implements Message {
    
    private String clazzName;
    private String methodName;
    private Object instance;
    private Object[] params;
    private Class<?>[] paramClazzs;
    private String[] paramClazzNames;

    public ReflectMessage(String clazzName, String methodName, Object instance, Object[] params, Class<?>[] paramClazzs, String[] paramClazzNames) {
        this.clazzName = clazzName;
        this.methodName = methodName;
        this.instance = instance;
        this.params = params;
        this.paramClazzs = paramClazzs;
        this.paramClazzNames = paramClazzNames;
    }
    
    public ReflectMessage(String clazzName, String methodName, Object instance, Object[] params, Class<?>[] paramClazzs) {
        this(clazzName, methodName, instance, params, paramClazzs, null);
    }
    
    public ReflectMessage(String clazzName, String methodName, Object instance, Object[] params, String[] paramClazzNames) {
        this(clazzName, methodName, instance, params, null, paramClazzNames);
    }
    
    public ReflectMessage(String clazzName, String methodName, Object instance, Object[] params) {
        this(clazzName, methodName, instance, params, null, null);
    }

    public String getClazzName() {
        return clazzName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object getInstance() {
        return instance;
    }

    public Object[] getParams() {
        return params;
    }

    public Class<?>[] getParamClazzs() {
        return paramClazzs;
    }

    public String[] getParamClazzNames() {
        return paramClazzNames;
    }
    
    @Override
    public MessageReply sendMessage(){
        Class<?> clazz = ReflectUtil.getClass(clazzName);
        if(clazz==null){
            return new MessageReply(this, "找不到目标类: ".concat(clazzName));
        }else{
            if(paramClazzs==null){
                if(paramClazzNames==null){
                    paramClazzs = new Class<?>[params.length];
                    for(int i=0;i<params.length;i++){
                        paramClazzs[i] = params[i].getClass();
                    }
                }else{
                    paramClazzs = new Class<?>[paramClazzNames.length];
                    for(int i=0;i<paramClazzNames.length;i++){
                        paramClazzs[i] = ReflectUtil.getClass(paramClazzNames[i]);
                        if(paramClazzs[i]==null){
                            return new MessageReply(this, "找不到目标参数类: ".concat(paramClazzNames[i]));
                        }
                    }
                }
            }
            Method method = ReflectUtil.getMethod(clazz, methodName, paramClazzs);
            if(method==null){
                return new MessageReply(this, "找不到目标方法: ".concat(methodName));
            }else{
                try{
                    Object result = method.invoke(instance, params);
                    return new MessageReply(this, result, method.getReturnType());
                }catch(Exception ex){
                    return new MessageReply(this, "调用目标方法错误: ".concat(methodName).concat(ex.getMessage()));
                }
            }
        }
    }
    
}
