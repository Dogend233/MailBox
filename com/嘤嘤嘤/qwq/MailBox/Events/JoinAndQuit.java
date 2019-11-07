package com.嘤嘤嘤.qwq.MailBox.Events;

import static com.嘤嘤嘤.qwq.MailBox.GlobalConfig.success;
import com.嘤嘤嘤.qwq.MailBox.MailBox;
import static com.嘤嘤嘤.qwq.MailBox.MailBox.MailListAllUn;
import com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxHud;
import lk.vexview.api.VexViewAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinAndQuit implements Listener {
    
    private boolean enHud;
    
    public JoinAndQuit(boolean enHud){
        this.enHud = enHud;
        if(enHud)Bukkit.getConsoleSender().sendMessage(success+"-----[MailBox]:已启用邮箱HUD");
    }
    
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent evt){
        Player player = evt.getPlayer();
        // 获取可领取邮件列表
        MailBox.getUnMailList(player, "all");
        // 设置HUD
        if(enHud){
            VexViewAPI.sendHUD(player, new MailBoxHud());
        }
    }
    
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent evt){
        Player player = evt.getPlayer();
        String Username = player.getName();
        // 将玩家移出MailListAllUn列表
        MailListAllUn.remove(Username);
        // 移除HUD
        VexViewAPI.removeHUD(player, "MailBoxHud");
    }
    
}
