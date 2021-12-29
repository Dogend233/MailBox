package com.tripleying.dogend.mailbox.listener;

import com.tripleying.dogend.mailbox.api.mail.PlayerData;
import com.tripleying.dogend.mailbox.manager.DataManager;
import com.tripleying.dogend.mailbox.manager.MailManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * 玩家加入监听器
 * @author Dogend
 */
public class onPlayerJoin implements Listener {
    
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent evt){
        PlayerData pd = DataManager.getDataManager().getPlayerData(evt.getPlayer());
        if(pd!=null) MailManager.getMailManager().checkPlayerData(pd);
    }  
    
}
