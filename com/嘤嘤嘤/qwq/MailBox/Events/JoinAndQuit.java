package com.嘤嘤嘤.qwq.MailBox.Events;

import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.MailBox;
import com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxHud;
import lk.vexview.api.VexViewAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinAndQuit implements Listener {
    
    private final boolean enVexView;
    private final boolean enHud;
    
    public JoinAndQuit(boolean enVexView, boolean enHud){
        this.enVexView = enVexView;
        this.enHud = enHud;
        if(enHud) Bukkit.getConsoleSender().sendMessage(GlobalConfig.success+"-----[MailBox]:已启用邮箱HUD");
    }
    
    // 玩家进入事件
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent evt){
        Player player = evt.getPlayer();
        // 获取可领取邮件列表
        MailBox.updateRelevantMailList(player, "all");
        // 移除HUD
        if(enVexView) VexViewAPI.removeHUD(player, MailBoxHud.id);
        // 设置HUD
        if(enVexView && enHud) MailBoxHud.setMailBoxHud(player);
        for(String type:MailBoxAPI.getAllType()){
            MailBox.updateRelevantMailList(player, type);
            if(!MailBox.getRelevantMailList(player, type).get("asRecipient").isEmpty()){
                MailBoxAPI.sendTips(player);
                System.out.println(1);
                return;
            }
        }
    }
    
    // 玩家退出事件
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent evt){
        // 将玩家移出邮件列表
        MailBox.removeRelevantMailList(evt.getPlayer());
    }
    
}
