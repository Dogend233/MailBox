package com.tripleying.qwq.MailBox.Events;

import com.tripleying.qwq.MailBox.VexView.MailSendGui;
import java.util.ArrayList;
import lk.vexview.event.gui.VexGuiCloseEvent;
import lk.vexview.gui.components.VexSlot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class VexInvGuiClose implements Listener {
    
    @EventHandler
    public void onVexGuiClose(VexGuiCloseEvent evt){
        if(evt.getGui() instanceof MailSendGui){
            ArrayList<ItemStack> lis = new ArrayList();
            evt.getGui().getComponents().stream().filter(c -> c instanceof VexSlot).filter(s -> ((VexSlot)s).getItem()!=null).forEach(i -> lis.add(((VexSlot)i).getItem()));
            ItemStack[] is = new ItemStack[lis.size()];
            for(int i=0;i<is.length;i++){
                is[i] = lis.get(i);
            }
            evt.getPlayer().getInventory().addItem(is);
        }
    }
    
}
