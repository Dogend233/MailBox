package com.tripleying.qwq.MailBox.Events;

import com.tripleying.qwq.MailBox.VexView.MailBoxGui;
import lk.vexview.api.VexViewAPI;
import lk.vexview.event.KeyBoardPressEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SingleKeyPress implements Listener {
    
    private final int key;
    
    public SingleKeyPress(int key){
        this.key = key;
    }
    
    @EventHandler
    public void openMailBox(KeyBoardPressEvent e){
        // 按key键打开邮箱GUI
        if(e.getKey()==key&&VexViewAPI.getPlayerCurrentGui(e.getPlayer())==null){
            MailBoxGui.openMailBoxGui(e.getPlayer(), "Recipient");
        }
    }
    
}
