package com.tripleying.dogend.mailbox.api.mail.attach;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.bukkit.entity.Player;

/**
 * 代理玩家
 * @since 3.1.0
 * @author Dogend
 */
public class ProxyPlayer implements InvocationHandler {
    
    /**
     * 被代理的玩家
     */
    public Player cs;
    
    public ProxyPlayer(Player p){
        this.cs = p;
    }
    
    /**
     * 将代理玩家执行isOp和hasPermission方法的返回值更改为true
     * @param proxy 代理对象
     * @param method 原方法
     * @param args 方法参数
     * @return 方法返回值
     * @throws Throwable 异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if("isOp".equals(method.getName())){
            return true;
        }
        if("hasPermission".equals(method.getName())){
            return true;
        }
        return method.invoke(cs, args);
    }
    
    /**
     * 获取代理玩家
     * @param p Player
     * @return Player
     */
    public static Player getProxyPlayer(Player p){
        ProxyPlayer pp = new ProxyPlayer(p);
        return (Player) Proxy.newProxyInstance(p.getClass().getClassLoader(), p.getClass().getInterfaces(), pp);
    }
    
}
