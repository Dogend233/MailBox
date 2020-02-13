package com.tripleying.qwq.MailBox.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class Reflection {
    
    private static String VERSION;
    private static final Map<String, Class<?>> NMS_CLASS = new HashMap<>();
    private static final Map<String, Class<?>> OBC_CLASS = new HashMap<>();
    private static final Map<Class<?>, Map<String, Method>> METHOD = new HashMap<>();
    
    private static Class<?> craftItemStackClazz;
    private static Method asNMSCopyMethod;
    private static Class<?> nmsItemStackClazz;
    private static Class<?> nbtTagCompoundClazz;
    private static Method saveNmsItemStackMethod;
    
    // 物品转json
    public static String Item2Json(ItemStack is){
        Object nmsItemStackObj;
        Object nmsNbtTagCompoundObj;
        Object itemAsJsonObject;
        try {
            nmsItemStackObj = asNMSCopyMethod.invoke(null, is);
            nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
            itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
            return itemAsJsonObject.toString();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
            return "";
        }
    }
    
    // 获取NMS版本
    public static String getVersion(){
        String name = Bukkit.getServer().getClass().getPackage().getName();
        VERSION = name.substring(name.lastIndexOf('.') + 1) + ".";
        getClassAndMethod();
        return VERSION;
    }
    
    // 获取类和方法
    public static void getClassAndMethod(){
        craftItemStackClazz = getOBCClass("inventory.CraftItemStack");
        asNMSCopyMethod = getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);
        nmsItemStackClazz = getNMSClass("ItemStack");
        nbtTagCompoundClazz = getNMSClass("NBTTagCompound");
        saveNmsItemStackMethod = getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);
    }
    
    public static Class<?> getNMSClass(String nmsClassName) {
        if (NMS_CLASS.containsKey(nmsClassName)) {
            return NMS_CLASS.get(nmsClassName);
        }

        String clazzName = "net.minecraft.server." + VERSION + nmsClassName;
        Class<?> clazz;

        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException t) {
            return NMS_CLASS.put(nmsClassName, null);
        }

        NMS_CLASS.put(nmsClassName, clazz);
        return clazz;
    }
    
    public static Class<?> getOBCClass(String obcClassName) {
        if (OBC_CLASS.containsKey(obcClassName)) {
            return OBC_CLASS.get(obcClassName);
        }

        String clazzName = "org.bukkit.craftbukkit." + VERSION + obcClassName;
        Class<?> clazz;

        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException t) {
            OBC_CLASS.put(obcClassName, null);
            return null;
        }

        OBC_CLASS.put(obcClassName, clazz);
        return clazz;
    }
    
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        if (!METHOD.containsKey(clazz)) {
            METHOD.put(clazz, new HashMap<>());
        }

        Map<String, Method> methods = METHOD.get(clazz);

        if (methods.containsKey(methodName)) {
            return methods.get(methodName);
        }

        try {
            Method method = clazz.getMethod(methodName, params);
            methods.put(methodName, method);
            METHOD.put(clazz, methods);
            return method;
        } catch (NoSuchMethodException | SecurityException e) {
            methods.put(methodName, null);
            METHOD.put(clazz, methods);
            return null;
        }
    }
    
}