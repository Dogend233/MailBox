package com.tripleying.dogend.mailbox.manager;

import com.tripleying.dogend.mailbox.api.command.BaseTabCompleter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 * 指令补全管理器
 * @author Administrator
 */
public class TabManager implements TabCompleter {
    
    private final Map<String, BaseTabCompleter> map;
    
    public TabManager(){
        this.map = new LinkedHashMap();
    }
    
    /**
     * 注册指令补全器
     * @param label 指令标签
     * @param tab 指令补全器
     * @return boolean
     */
    public boolean registerTab(String label, BaseTabCompleter tab){
        if(this.map.containsKey(label)){
            return false;
        }else{
            this.map.put(label, tab);
            return true;
        }
    }
    
    /**
     * 注销指令补全器
     * @param label 指令标签
     */
    public void unregisterTab(String label){
        if(this.map.containsKey(label)){
            this.map.remove(label);
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length>0){
            String label = args[0];
            if(this.map.containsKey(label)){
                return this.map.get(label).onTabComplete(sender, args);
            }
            List<String> list = new ArrayList();
            this.map.forEach((k,v) -> {
                if(k.startsWith(label) && v.allowTab(sender)){
                    list.add(k);
                }
            });
            return list;
        }
        return null;
    }
    
}
