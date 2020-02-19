package com.tripleying.qwq.MailBox.Events;

import com.tripleying.qwq.MailBox.ConfigMessage;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.Message;
import com.tripleying.qwq.MailBox.Utils.MailUtil;
import com.tripleying.qwq.MailBox.VexView.MailBoxHud;
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
        if(this.enHud = enHud) Bukkit.getConsoleSender().sendMessage(ConfigMessage.hud_box);
    }
    
    // 玩家进入事件
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent evt){
        Player player = evt.getPlayer();
        // 获取可领取邮件列表
        MailBox.updateRelevantMailList(player, "all");
        for(String type:MailUtil.getTrueTypeWhithoutSpecial()){
            MailBox.updateRelevantMailList(player, type);
            if(!MailBox.getRelevantMailList(player, type).get("asRecipient").isEmpty()){
                MailUtil.sendTips(player, Message.tipsJoin.replace("%player", player.getName()),"");
                return;
            }
        }
        // 移除HUD
        if(enVexView) VexViewAPI.removeHUD(player, MailBoxHud.id);
        // 设置HUD
        if(enVexView && enHud) MailBoxHud.setMailBoxHud(player);
    }
    
    // 玩家退出事件
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent evt){
        Player p = evt.getPlayer();
        // 将玩家移出邮件列表
        MailBox.removeRelevantMailList(p);
    }
    
}
