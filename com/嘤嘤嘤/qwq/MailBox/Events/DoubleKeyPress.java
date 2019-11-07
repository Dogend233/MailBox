package com.嘤嘤嘤.qwq.MailBox.Events;

import static com.嘤嘤嘤.qwq.MailBox.VexView.MailBoxGui.openMailBoxGui;
import lk.vexview.api.VexViewAPI;
import lk.vexview.event.KeyBoardPressEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DoubleKeyPress implements Listener {
    
    private boolean isCtrl = false;
    private int First = 0;
    
    private int key1;
    private int key2;
    
    public DoubleKeyPress(int key1, int key2){
        this.key1 = key1;
        this.key2 = key2;
    }
    
    @EventHandler
    public void openMailBox(KeyBoardPressEvent e){
        // 按Ctrl+M打开邮箱GUI
        // 方案一
        if(isCtrl&&e.getKey()==key2&&VexViewAPI.getPlayerCurrentGui(e.getPlayer())==null){
            openMailBoxGui(e.getPlayer());
            isCtrl = false;
        }
        if(e.getKey()==key1){
            isCtrl = e.getEventKeyState();
        }
        // 方案二
        /*if(!e.getEventKeyState()){
            First = 0;
        }else{
            if(First==0){
                First = e.getKey();
            }else{
                if(First==key1&&e.getKey()==key2&&VexViewAPI.getPlayerCurrentGui(e.getPlayer())==null){
                    openMailBoxGui(e.getPlayer());
                }
            }
        }*/
    }
    
}
