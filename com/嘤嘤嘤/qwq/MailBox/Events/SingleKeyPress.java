package com.嘤嘤嘤.qwq.MailBox.Events;

import static com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxGui.openMailBoxGui;
import lk.vexview.api.VexViewAPI;
import lk.vexview.event.KeyBoardPressEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SingleKeyPress implements Listener {
    
    private int key;
    
    public SingleKeyPress(int key){
        this.key = key;
    }
    
    @EventHandler
    public void openMailBox(KeyBoardPressEvent e){
        // 按key键打开邮箱GUI
        if(e.getKey()==key&&VexViewAPI.getPlayerCurrentGui(e.getPlayer())==null){
            openMailBoxGui(e.getPlayer());
        }
    }
    
}
