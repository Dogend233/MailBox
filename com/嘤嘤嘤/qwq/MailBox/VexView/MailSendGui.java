package com.嘤嘤嘤.qwq.MailBox.VexView;

import static com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI.getMD5;
import com.嘤嘤嘤.qwq.MailBox.Mail.FileMail;
import com.嘤嘤嘤.qwq.MailBox.Mail.TextMail;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import static com.嘤嘤嘤.qwq.MailBox.VexView.MailContentGui.openMailContentGui;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.OpenedVexGui;
import lk.vexview.gui.VexInventoryGui;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexSlot;
import lk.vexview.gui.components.VexText;
import lk.vexview.gui.components.VexTextField;
import net.md_5.bungee.api.ChatColor;
import static org.bukkit.Material.AIR;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MailSendGui extends VexInventoryGui{
    
    public MailSendGui() {
        super("[local]MailBox/gui_send.png",-1,-1,240,300,240,300,40,222);
        this.addComponent(vt1);
        this.addComponent(vt2);
        this.addComponent(vt3);
        this.addComponent(vt4);
        this.addComponent(vt5);
        this.addComponent(vt6);
        this.addComponent(new VexTextField(11,60,218,11,30,1));
        this.addComponent(new VexTextField(11,85,218,11,0,2));
        this.addComponent(new VexTextField(11,110,218,11,255,3));
        this.addComponent(new VexTextField(11,135,218,11,255,4));
        this.addComponent(new VexTextField(11,160,218,11,255,5));
        this.addComponent(vbr);
        this.addComponent(new VexSlot(0,148,193,null));
        this.addComponent(new VexSlot(1,166,193,null));
        this.addComponent(new VexSlot(2,184,193,null));
        this.addComponent(new VexSlot(3,202,193,null));
        this.addComponent(new VexSlot(4,220,193,null));
    }
    
    public MailSendGui(TextMail tm) {
        super("[local]MailBox/gui_send.png",-1,-1,240,300,240,300,40,222);
        this.addComponent(vt1);
        this.addComponent(vt2);
        this.addComponent(vt3);
        this.addComponent(vt4);
        this.addComponent(vt5);
        this.addComponent(vt6);
        this.addComponent(new VexTextField(11,60,218,11,30,1,tm.topic));
        this.addComponent(new VexTextField(11,85,218,11,0,2));
        this.addComponent(new VexTextField(11,110,218,11,255,3,tm.content));
        this.addComponent(vbr);
        if(tm instanceof FileMail){
            FileMail fm = (FileMail) tm;
            String f4 = "";
            String f5 = "";
            List<String> cl = fm.commandList;
            List<String> cd = fm.commandDescription;
            if(cl!=null){
                for(int i=0;i<cl.size();i++){
                    if(i==0){
                        f4 = cl.get(i);
                    }else{
                        f4 += GlobalConfig.fileDiv+cl.get(i);
                    }
                }
            }
            if(cd!=null){
                for(int i=0;i<cd.size();i++){
                    if(i==0){
                        if(cd.get(i).contains("§")){
                            f5 = cd.get(i).replace("§", "&");
                        }else{
                            f5 = cd.get(i);
                        }
                    }else{
                        if(cd.get(i).contains("§")){
                            f5 += GlobalConfig.fileDiv+cd.get(i).replace("§", "&");
                        }else{
                            f5 += GlobalConfig.fileDiv+cd.get(i);
                        }
                    }
                }
            }
            this.addComponent(new VexTextField(11,135,218,11,255,4,f4));
            this.addComponent(new VexTextField(11,160,218,11,255,5,f5));
            ArrayList<ItemStack> sl = fm.itemList;
            if(sl.isEmpty()){
                this.addComponent(new VexSlot(0,148,193,null));
                this.addComponent(new VexSlot(1,166,193,null));
                this.addComponent(new VexSlot(2,184,193,null));
                this.addComponent(new VexSlot(3,202,193,null));
                this.addComponent(new VexSlot(4,220,193,null));
            }else{
                for(int i=0;i<sl.size();i++){
                    this.addComponent(new VexSlot(i,18*i+148,193,fm.itemList.get(i)));
                }
                for(int i=5;i>sl.size();i--){
                    this.addComponent(new VexSlot(i-1,18*(i-1)+148,193,null));
                }
            }
        }else{
            this.addComponent(new VexTextField(11,135,218,11,255,4));
            this.addComponent(new VexTextField(11,160,218,11,255,5));
            this.addComponent(new VexSlot(0,148,193,null));
            this.addComponent(new VexSlot(1,166,193,null));
            this.addComponent(new VexSlot(2,184,193,null));
            this.addComponent(new VexSlot(3,202,193,null));
            this.addComponent(new VexSlot(4,220,193,null));
        }
    }
    VexText vt1 = new VexText(11,49,Arrays.asList(ChatColor.YELLOW+"邮件主题 （最长30字）"),1);
    VexText vt2 = new VexText(11,74,Arrays.asList(ChatColor.YELLOW+"收件人 （暂时仅支持全员）："),1);
    VexText vt3 = new VexText(11,99,Arrays.asList(ChatColor.YELLOW+"邮件内容 （最长255字）"),1);
    VexText vt4 = new VexText(11,124,Arrays.asList(ChatColor.YELLOW+"执行指令 （同上， 不加“ / ”）"),1);
    VexText vt5 = new VexText(11,149,Arrays.asList(ChatColor.YELLOW+"指令描述 （同上）"),1);
    VexText vt6 = new VexText(128,177,Arrays.asList(ChatColor.YELLOW+"附件："),1);
    
    VexButton vbr = new VexButton("预览","预览","[local]MailBox/button_small.png","[local]MailBox/button_small_.png",5,190,30,18,player -> {
        OpenedVexGui ovg = VexViewAPI.getPlayerCurrentGui(player);
        String topic = ovg.getVexGui().getTextField(1).getTypedText();
        String text = ovg.getVexGui().getTextField(3).getTypedText();
        if(valid(player, topic, text)){
            ArrayList<ItemStack> al = getItem(ovg);
            List<String> cl = divCommand(ovg.getVexGui().getTextField(4).getTypedText());
            List<String> cd = divCommand(ovg.getVexGui().getTextField(5).getTypedText());
            if(al.isEmpty() && cl==null && cd==null){
                TextMail tm = new TextMail("all", 0, player.getName(), topic, text, null);
                try{
                    openMailContentGui(player, tm);
                }catch(IOException e){
                    player.sendMessage(GlobalConfig.warning+"[邮件预览]：打开预览界面失败");
                }
            }else{
                try{
                    String filename = getMD5("all");
                    FileMail fm = new FileMail("all", 0, player.getName(), topic, text, null, filename, al, cl, cd);
                    try{
                        openMailContentGui(player, fm);
                    }catch(IOException e){
                        player.sendMessage(GlobalConfig.warning+"[邮件预览]：打开预览界面失败");
                    }
                }catch(IOException e){
                    player.sendMessage(GlobalConfig.warning+"[邮件预览]：文件名生成失败");
                }
            }
        }
    });
    
    private boolean valid(Player p, String t, String c){
        if(t.equals("")){
            p.sendMessage(GlobalConfig.warning+"[邮件预览]：主题不能为空");
            return false;
        }else{
            if(c.equals("")){
                p.sendMessage(GlobalConfig.warning+"[邮件预览]：内容不能为空");
                return false;
            }else{
                return true;
            }
        }
    }
    
    private List<String> divCommand(String text){
        if(text.equals("")){
            return null;
        }else{
            if(text.contains("&"))text = text.replace("&", "§");
            if(text.contains("\\"+GlobalConfig.fileDiv))text = text.replace("\\"+GlobalConfig.fileDiv, GlobalConfig.fileDiv);
            String[] cmd = text.split(GlobalConfig.fileDiv);
            List<String> l = new ArrayList();
            l.addAll(Arrays.asList(cmd));
            return l;
        }
    }
    
    private ArrayList<ItemStack> getItem(OpenedVexGui ovg){
        ArrayList<ItemStack> al = new ArrayList();
        for(int i=0;i<5;i++){
            if(ovg.getVexGui().getSlotById(i).getItem().getType()!=AIR){
                al.add(ovg.getVexGui().getSlotById(i).getItem());
            }
        }
        return al;
    }
    
    // 打开邮件GUI
    public static void openMailSendGui(Player p, TextMail tm) {
        // 如果不是OP不打开GUI
        if(p.isOp()){
            if(tm==null){
                VexViewAPI.openGui(p, new MailSendGui());
            }else{
                VexViewAPI.openGui(p, new MailSendGui(tm));
            }
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+" 你没有权限打开GUI");
        }
    }
}
