package com.tripleying.dogend.mailbox.manager;

import com.tripleying.dogend.mailbox.MailBox;
import com.tripleying.dogend.mailbox.api.module.MailBoxModule;
import com.tripleying.dogend.mailbox.util.MessageUtil;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * 监听器管理器
 * @author Dogend
 */
public class ListenerManager {
    
    private static ListenerManager manager;
    private final Map<Listener, MailBoxModule> mod_map;
    
    public ListenerManager(){
        manager = this;
        this.mod_map = new LinkedHashMap();
    }
    
    /**
     * 注册监听器
     * @param module 模块
     * @param listener 监听器
     */
    public void registerListener(MailBoxModule module, Listener listener){
        this.mod_map.put(listener, module);
        Bukkit.getPluginManager().registerEvents(listener, MailBox.getMailBox());
        MessageUtil.log(MessageUtil.listener_reg.replaceAll("%listener%", listener.getClass().getName()));
    }
    
    /**
     * 注销监听器
     * @param listener 监听器
     */
    public void unregisterListener(Listener listener){
        if(this.mod_map.containsKey(listener)){
            this.mod_map.remove(listener);
            HandlerList.unregisterAll(listener);
            MessageUtil.log(MessageUtil.listener_unreg.replaceAll("%listener%", listener.getClass().getName()));
        }
    }
    
    /**
     * 注销全部监听器
     * @param module 模块
     */
    public void unregisterAllListener(MailBoxModule module){
        if(this.mod_map.containsValue(module)){
            Set<Listener> cmds = new HashSet();
            this.mod_map.entrySet().stream().filter(me -> (me.getValue()==module)).forEachOrdered(me -> {
                cmds.add(me.getKey());
            });
            cmds.forEach(cmd -> {
                this.unregisterListener(cmd);
            });
        }
    }
    
    public static ListenerManager getListenerManager(){
        return manager;
    }
    
}
