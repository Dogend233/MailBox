package com.tripleying.qwq.MailBox.VexView;

import java.util.ArrayList;
import java.util.List;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexComponents;
import lk.vexview.gui.components.expand.VexColorfulTextArea;
import lk.vexview.hud.VexButtonShow;
import lk.vexview.hud.VexShow;

public class VexView2_6 {
    
    public static VexShow createVexButtonShow(String id, VexButton vb){
        return new VexButtonShow(id,vb,0);
    }
    
    public static VexComponents createTextArea(int[] f){
        List<String> temp = new ArrayList();
        temp.add("");
        return new VexColorfulTextArea(f[0],f[1],f[2],f[3],f[4],f[5],f[6],f[7],temp);
    }
    
}
