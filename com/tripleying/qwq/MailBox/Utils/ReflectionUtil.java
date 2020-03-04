package com.tripleying.qwq.MailBox.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 * 反射工具
 */
public class ReflectionUtil {
    
    /**
     * 反射版本
     */
    private static String VERSION;
    
    /**
     * OBC物品类的asNMSCopy方法
     */
    private static Method asNMSCopyMethod;
    
    /**
     * NMS的NBTTagCompound类
     */
    private static Class<?> nbtTagCompoundClazz;
    
    /**
     * NMS物品类的save方法, 参数为NBTTagCompound
     */
    private static Method saveNmsItemStackMethod;
    
    /**
     * NMS物品类的getName方法
     */
    private static Method getNameNmsItemStackMethod;
    
    /**
     * NMSIChatBaseComponent类的getText方法
     */
    private static Method getTextNmsIChatBaseComponentMethod;

    /**
     * 物品转json
     * @param is 物品
     * @return json字符串
     */
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

    /**
     * 获取物品名
     * @param is 物品
     * @return 物品名
     */
    public static String getItemStackName(ItemStack is){
        String name = is.getType().name();
        try {
            Object nmsItemStackObj = asNMSCopyMethod.invoke(null, is);
            Object nmsItemName = getNameNmsItemStackMethod.invoke(nmsItemStackObj);
            if(nmsItemName instanceof String){
                name = (String)nmsItemName;
            }else{
                name = getTextNmsIChatBaseComponentMethod.invoke(nmsItemName).toString();
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException  ex) {}
        return name;
    }

    /**
     * 获取VexView的按键值
     * @param KEY Map(id-按键名)
     */
    public static void getVexViewKeys(HashMap<String, String> KEY){
        try {
            Class<?> keyClazz = Class.forName("lk.vexview.event.MinecraftKeys");
            Method valuesMethod = keyClazz.getMethod("values");
            Field keyField = keyClazz.getDeclaredField("key");
            keyField.setAccessible(true);
            for(Object key:(Object[])valuesMethod.invoke(null)){
                for(int keys : (int[])keyField.get(key)){
                    KEY.put(keys+"", key.toString().substring(4));
                }
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {}
    }

    /**
     * 获取NMS版本
     * @return NMS版本
     */
    public static String getVersion(){
        String name = Bukkit.getServer().getClass().getPackage().getName();
        VERSION = name.substring(name.lastIndexOf('.') + 1) + ".";
        getClassAndMethod();
        return VERSION;
    }

    /**
     * 获取类和方法
     */
    public static void getClassAndMethod(){
        Class<?> obcItemStackClazz = getOBCClass("inventory.CraftItemStack");
        Class<?> nmsItemStackClazz = getNMSClass("ItemStack");
        Class<?> nmsIChatBaseComponent = getNMSClass("IChatBaseComponent");
        asNMSCopyMethod = getMethod(obcItemStackClazz, "asNMSCopy", ItemStack.class);
        nbtTagCompoundClazz = getNMSClass("NBTTagCompound");
        try {
            getNameNmsItemStackMethod = nmsItemStackClazz.getMethod("getName");
            getTextNmsIChatBaseComponentMethod = nmsIChatBaseComponent.getMethod("getText");
            saveNmsItemStackMethod = getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);
        } catch (NoSuchMethodException | SecurityException ex) {}
    }

    /**
     * 获取NMS的类
     * @param nmsClassName 类名
     * @return 类
     */
    public static Class<?> getNMSClass(String nmsClassName) {
        String clazzName = "net.minecraft.server." + VERSION + nmsClassName;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException t) {}
        return clazz;
    }

    /**
     * 获取OBC的类
     * @param obcClassName 类名
     * @return 类
     */
    public static Class<?> getOBCClass(String obcClassName) {
        String clazzName = "org.bukkit.craftbukkit." + VERSION + obcClassName;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException t) {}
        return clazz;
    }

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
    
}