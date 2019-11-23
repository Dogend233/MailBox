package com.嘤嘤嘤.qwq.MailBox.Events;

import static com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxGui.openMailBoxGui;
import lk.vexview.api.VexViewAPI;
import lk.vexview.event.KeyBoardPressEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DoubleKeyPress implements Listener {
    
    public DoubleKeyPress(int key1, int key2){
        this.key1 = key1;
        this.key2 = key2;
    }
    
    private int key1;
    private int key2;
    
    private boolean canOpen = false;
    
    @EventHandler
    public void openMailBox(KeyBoardPressEvent e){
        // 按Ctrl+M打开邮箱GUI
        if(canOpen&&e.getKey()==key2&&VexViewAPI.getPlayerCurrentGui(e.getPlayer())==null){
            openMailBoxGui(e.getPlayer(), "Recipient");
            canOpen = false;
        }
        if(e.getKey()==key1){
            canOpen = e.getEventKeyState();
        }
    }
    
}
