package com.tripleying.dogend.mailbox.api.mail.attach;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

/**
 * 附件指令
 * @author Dogend
 */
public class AttachCommand implements ConfigurationSerializable {
    
    private final List<String> console;
    private final List<String> player;
    private final List<String> op;
    
    public AttachCommand(){
        this.console = new LinkedList();
        this.player = new LinkedList();
        this.op = new LinkedList();
    }
    
    /**
     * 是否有任何指令
     * @return boolean
     */
    public boolean hasCommand(){
        return !this.console.isEmpty() || !this.player.isEmpty() || !this.op.isEmpty();
    }
    
    /**
     * 添加控制台指令
     * @param cmd 指令
     */
    public void addConsoleCommand(String cmd){
        this.console.add(cmd);
    }
    
    /**
     * 清空控制台指令
     */
    public void clearConsoleCommand(){
        this.console.clear();
    }
    
    /**
     * 添加玩家指令
     * @param cmd 指令
     */
    public void addPlayerCommand(String cmd){
        this.player.add(cmd);
    }
    
    /**
     * 清空玩家指令
     */
    public void clearPlayerCommand(){
        this.player.clear();
    }
    
    /**
     * 添加OP指令
     * @param cmd 指令
     */
    public void addOPCommand(String cmd){
        this.op.add(cmd);
    }
    
    /**
     * 清空OP指令
     */
    public void clearOPCommand(){
        this.op.clear();
    }
    
    /**
     * 获取控制台指令
     * @return List
     */
    public List<String> getConsole() {
        return this.console;
    }
    
    /**
     * 获取玩家指令
     * @return List
     */
    public List<String> getPlayer() {
        return this.player;
    }
    
    /**
     * 获取OP指令
     * @return List
     */
    public List<String> getOp() {
        return this.op;
    }
    
    /**
     * 执行指令
     * @param p 玩家
     */
    public void doCommand(Player p){
        if(!this.hasCommand()) return;
        this.doConsoleCommand(p);
        this.doPlayerCommand(p);
        this.doOPCommand(p);
    }
    
    /**
     * 执行控制台指令
     * @param p Player
     */
    public void doConsoleCommand(Player p){
        if(this.console.isEmpty()) return;
        String name = p.getName();
        Server server = Bukkit.getServer();
        CommandSender cs = Bukkit.getConsoleSender();
        this.console.forEach(cc -> server.dispatchCommand(cs, cc.replace("%player%", name)));
        
    }
    
    /**
     * 执行普通玩家指令
     * @param p 玩家
     */
    public void doPlayerCommand(Player p){
        if(this.player.isEmpty()) return;
        String name = p.getName();
        Server server = Bukkit.getServer();
        this.player.forEach(pc -> server.dispatchCommand(p, pc.replace("%player%", name)));
    }
    
    /**
     * 以OP身份执行玩家指令
     * @param p 玩家
     */
    public void doOPCommand(Player p){
        if(this.op.isEmpty()) return;
//        boolean isOp = p.isOp();
//        try{
//            p.setOp(true);
//            this.op.forEach(oc -> p.performCommand(oc.replace("%player%", p.getName())));
//        }finally {
//            p.setOp(isOp);
//        }
        String name = p.getName();
        Server server = Bukkit.getServer();
        CommandSender cs = ProxyPlayer.getProxyPlayer(p);
        System.out.println(cs.isOp());
        this.op.forEach(oc -> server.dispatchCommand(cs, oc.replace("%player%", name)));
    }
    
    /**
     * 获取全部指令
     * @return Map
     */
    public Map<String, List<String>> getCommand(){
        Map<String, List<String>> map = new LinkedHashMap();
        map.put("console", this.console);
        map.put("player", this.player);
        map.put("op", this.op);
        return map;
    }
    
    /**
     * 配置文件序列化
     * @return Map
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> cmd = new LinkedHashMap();
        if(!this.console.isEmpty()){
            cmd.put("console", this.console);
        }
        if(!this.player.isEmpty()){
            cmd.put("player", this.player);
        }
        if(!this.op.isEmpty()){
            cmd.put("op", this.op);
        }
        return cmd;
    }
    
    /**
     * 配置文件反序列化
     * @param map Map
     * @return AttachCommand
     */
    public static AttachCommand deserialize(Map<String, Object> map){
        AttachCommand cmd = new AttachCommand();
        if(map.containsKey("console")){
            List<String> list = (List<String>)map.get("console");
            list.forEach(cc -> cmd.addConsoleCommand(cc));
        }
        if(map.containsKey("player")){
            List<String> list = (List<String>)map.get("player");
            list.forEach(pc -> cmd.addPlayerCommand(pc));
        }
        if(map.containsKey("op")){
            List<String> list = (List<String>)map.get("op");
            list.forEach(oc -> cmd.addOPCommand(oc));
        }
        return cmd;
    }
    
}
