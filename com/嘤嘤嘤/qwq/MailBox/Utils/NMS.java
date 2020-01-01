package com.嘤嘤嘤.qwq.MailBox.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
/*import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;*/

public class NMS {
    
    private static String VERSION;
    private static final Map<String, Class<?>> loadedNMSClasses = new HashMap<>();
    private static final Map<String, Class<?>> loadedOBCClasses = new HashMap<>();
    private static final Map<Class<?>, Map<String, Method>> loadedMethods = new HashMap<>();
    
    private static Class<?> craftItemStackClazz;
    private static Method asNMSCopyMethod;
    private static Class<?> nmsItemStackClazz;
    private static Method getNameMethod;
    private static Class<?> nbtTagCompoundClazz;
    private static Method saveNmsItemStackMethod;
    private static Class<?> iChatBaseComponentClazz;
    private static Method getTextMethod;
    
    // 获取物品名称
    public static String getItemName(ItemStack is){
        Object nmsItemStackObj;
        Object nmsItemName;
        try {
            nmsItemStackObj = asNMSCopyMethod.invoke(null, is);
            nmsItemName = getNameMethod.invoke(nmsItemStackObj);
            if(nmsItemName instanceof String){
                return (String)nmsItemName;
            }else{
                return getTextMethod.invoke(nmsItemName).toString();
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException  ex) {
            Logger.getLogger(NMS.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
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
            Logger.getLogger(NMS.class.getName()).log(Level.SEVERE, null, ex);
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
        getNameMethod = getMethod(nmsItemStackClazz, "getName");
        nbtTagCompoundClazz = getNMSClass("NBTTagCompound");
        saveNmsItemStackMethod = getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);
        iChatBaseComponentClazz  = getNMSClass("IChatBaseComponent");
        getTextMethod = getMethod(iChatBaseComponentClazz, "getText");
    }
    
    public static Class<?> getNMSClass(String nmsClassName) {
        if (loadedNMSClasses.containsKey(nmsClassName)) {
            return loadedNMSClasses.get(nmsClassName);
        }

        String clazzName = "net.minecraft.server." + VERSION + nmsClassName;
        Class<?> clazz;

        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException t) {
            return loadedNMSClasses.put(nmsClassName, null);
        }

        loadedNMSClasses.put(nmsClassName, clazz);
        return clazz;
    }
    
    public static Class<?> getOBCClass(String obcClassName) {
        if (loadedOBCClasses.containsKey(obcClassName)) {
            return loadedOBCClasses.get(obcClassName);
        }

        String clazzName = "org.bukkit.craftbukkit." + VERSION + obcClassName;
        Class<?> clazz;

        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException t) {
            loadedOBCClasses.put(obcClassName, null);
            return null;
        }

        loadedOBCClasses.put(obcClassName, clazz);
        return clazz;
    }
    
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        if (!loadedMethods.containsKey(clazz)) {
            loadedMethods.put(clazz, new HashMap<>());
        }

        Map<String, Method> methods = loadedMethods.get(clazz);

        if (methods.containsKey(methodName)) {
            return methods.get(methodName);
        }

        try {
            Method method = clazz.getMethod(methodName, params);
            methods.put(methodName, method);
            loadedMethods.put(clazz, methods);
            return method;
        } catch (NoSuchMethodException | SecurityException e) {
            methods.put(methodName, null);
            loadedMethods.put(clazz, methods);
            return null;
        }
    }
    
}
