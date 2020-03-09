package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.API.Event.MailSendEvent;
import com.tripleying.qwq.MailBox.API.Event.MailCollectEvent;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.OuterMessage;
import com.tripleying.qwq.MailBox.Utils.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 基础附件邮件
 */
public abstract class BaseFileMail extends BaseMail {
    
    /**
     * 附件名
     */
    private String fileName;
    
    /**
     * 附件是否启用指令
     */
    private boolean hasCommand;
    
    /**
     * 指令列表
     */
    private List<String> commandList;
    
    /**
     * 指令描述
     */
    private List<String> commandDescription;
    
    /**
     * 附件是否含有物品
     */
    private boolean hasItem;
    
    /**
     * 物品列表
     */
    private List<ItemStack> itemList;
    
    /**
     * 附件经验
     * （未实现）
     */
    private float exp;
    
    /**
     * 附件金币
     */
    private double coin;
    
    /**
     * 附件点券
     */
    private int point;
    
    public BaseFileMail(String type, int id, String sender, String topic, String content, String date, String filename){
        super(type, id, sender, topic, content, date);
        this.fileName = filename;
        readFile();
    }

    public BaseFileMail(String type, int id, String sender, String topic, String content, String date, String filename, List<ItemStack> isl, List<String> cl, List<String> cd, double coin, int point){
        super(type, id, sender, topic, content, date);
        this.fileName = filename;
        this.itemList = isl;
        this.commandList = cl;
        this.commandDescription = cd;
        this.hasItem = !isl.isEmpty();
        this.hasCommand = !cl.isEmpty();
        this.coin = coin;
        this.point = point;
    }
    
    /**
     * 将邮件转化为文本邮件
     * @return 文本邮件
     */
    public abstract BaseMail removeFile();
    
    /**
     * 删除这封邮件的附件
     * @return boolean
     */
    public boolean DeleteFile(){
        return (MailFileUtil.setDeleteFileSQL(fileName, getType()) | MailFileUtil.setDeleteFile(fileName, getType()));
    }
    
    /**
     * 执行指令
     * @param p 玩家
     * @return boolean
     */
    public boolean doCommand(Player p) {
        if(commandList!=null){
            List<String> op = new ArrayList();
            for(int i=0;i<commandList.size();i++){
                String cs = commandList.get(i);
                if(cs.endsWith(":op")){
                    op.add(cs.substring(0, cs.length()-3));
                }else{
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cs.replace("%player%", p.getName()));
                }
            }
            if(!op.isEmpty()){
                boolean isOp = p.isOp();
                try{
                    p.setOp(true);
                    op.forEach(opc -> p.performCommand(opc.replace("%player%", p.getName())));
                }finally {
                    p.setOp(isOp);
                }
            }
            return true;
        }else{
            p.sendMessage(OuterMessage.extracommandCommandError);
            return false;
        }
    }
    
    /**
     * 判断玩家背包是否有空位
     * @param p 玩家
     * @return boolean
     */
    public boolean hasBlank(Player p){
        int ils = itemList.size();
        int allAir = 0;
        for(ItemStack it:GlobalConfig.server_under_1_10 ? p.getInventory().getContents() : p.getInventory().getStorageContents()){
            if(it==null){
                if((allAir++)>=ils){
                    return true;
                }
            }
        }
        if(allAir<ils){
            int needAir = 0;
            o:for(int i=0;i<ils;i++){
                ItemStack is1 = itemList.get(i);
                HashMap<Integer, ? extends ItemStack> im = p.getInventory().all(is1.getType());
                if(!im.isEmpty()){
                    Set<Integer> ks = im.keySet();
                    for(Integer k:ks){
                        ItemStack is2 = im.get(k);
                        if(is2.isSimilar(is1) && is2.getAmount()+is1.getAmount()<=is2.getMaxStackSize()){
                            continue o;
                        }
                    }
                }
                needAir++;
            }
            return allAir >= needAir;
        }else{
            return true;
        }
    }

    /**
     * 给予玩家物品
     * @param p 玩家
     * @return boolean
     */
    public boolean giveItem(Player p) {
        BaseComponent[] bc = new BaseComponent[itemList.size()+1];
        bc[0] = new TextComponent(OuterMessage.itemItemClaim);
        ItemStack[] isa = new ItemStack[itemList.size()];
        for(int i = 0 ;i<itemList.size();i++){
            isa[i] = itemList.get(i);
            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM,  new BaseComponent[]{new TextComponent(ReflectionUtil.Item2Json(isa[i]))});
            TextComponent component = new TextComponent(" §r"+ItemUtil.getName(isa[i])+"§8x§r"+isa[i].getAmount());
            component.setHoverEvent(event);
            bc[i+1] = component;
        }
        p.getInventory().addItem(isa);
        p.spigot().sendMessage(bc);
        return true;
    }

    /**
     * 获取真实发送物品列表
     * @return 实际发送物品
     */
    public ArrayList<ItemStack> getTrueItemList(){
        ItemStack is;
        int amount;
        int size = itemList.size();
        ArrayList<ItemStack> isn = new ArrayList();
        List<Integer> ignore = new ArrayList();
        for(int i=0;i<size;i++){
            if(ignore.contains(i)) continue;
            is = itemList.get(i).clone();
            amount = is.getAmount();
            for(int j=i+1;j<size;j++){
                if(is.isSimilar(itemList.get(j))){
                    ignore.add(j);
                    amount += itemList.get(j).getAmount();
                }
            }
            is.setAmount(amount);
            isn.add(is);
        }
        return isn;
    }

    /**
     * 判断玩家背包里是否有想要发送的物品
     * @param isl 物品列表
     * @param p 玩家
     * @param cc 会话
     * @return boolean
     */
    public boolean hasItem(ArrayList<ItemStack> isl, Player p, ConversationContext cc){
        return ItemUtil.hasSendItem(isl, p, cc, 1);
    }

    /**
     * 移除玩家背包里想要发送的物品
     * @param isl 物品列表
     * @param p 玩家
     * @param cc 会话
     * @return boolean
     */
    public boolean removeItem(ArrayList<ItemStack> isl, Player p, ConversationContext cc){
        return ItemUtil.removeSendItem(isl, p, cc, 1);
    }
    
    /**
     * 给予玩家金币
     * @param p 玩家
     * @param coin 数量
     * @return boolean
     */
    public boolean giveCoin(Player p, double coin){
        return VaultUtil.addEconomy(p, coin);
    }
    
    /**
     * 给予玩家点券
     * @param p 玩家
     * @param point 数量
     * @return boolean
     */
    public boolean givePoint(Player p, int point){
        return PlayerPointsUtil.addPoints(p, point);
    }
    
    /**
     * 设置附件名
     * @param fileName 附件名
     */
    public void setFileName(String fileName){
        this.fileName = fileName;
    }
    
    /**
     * 获取附件名
     * @return 附件名
     */
    public String getFileName(){
        return this.fileName;
    }
    
    /**
     * 设置指令列表
     * @param commandList 指令列表
     */
    public void setCommandList(List<String> commandList){
        this.commandList = commandList;
        this.hasCommand = !commandList.isEmpty();
    }
    
    /**
     * 邮件是否包含指令
     * @return boolean
     */
    public boolean isHasCommand(){
        return this.hasCommand;
    }
    
    /**
     * 获取指令列表
     * @return 指令列表
     */
    public List<String> getCommandList(){
        return this.commandList;
    }
    
    /**
     * 获取字符串形式指令列表(以/分割)
     * @return 指令
     */
    public final String getCommandListString(){
        String str = "";
        if(!commandList.isEmpty()){
            str = commandList.stream().map((n) -> "/"+n).reduce(str, String::concat);
            str = str.substring(1);
        }
        return str;
    }
    
    /**
     * 设置指令描述
     * @param commandDescription 指令描述
     */
    public void setCommandDescription(List<String> commandDescription){
        this.commandDescription = commandDescription;
    }
    
    /**
     * 获取指令描述
     * @return 指令描述
     */
    public List<String> getCommandDescription(){
        return this.commandDescription;
    }
    
    /**
     * 获取字符串形式指令列表(以空格分割)
     * @return 指令描述
     */
    public final String getCommandDescriptionString(){
        String str = "";
        if(!commandDescription.isEmpty()){
            str = commandDescription.stream().map((n) -> " "+n).reduce(str, String::concat);
            str = str.substring(1);
        }
        return str;
    }
    
    /**
     * 设置物品列表
     * @param items 物品列表
     */
    public void setItemList(List<ItemStack> items){
        itemList = items;
        hasItem = !itemList.isEmpty();
    }
    
    /**
     * 邮件是否包含物品
     * @return boolean
     */
    public boolean isHasItem(){
        return this.hasItem;
    }
    
    /**
     * 获取物品列表
     * @return 物品列表
     */
    public List<ItemStack> getItemList(){
        return this.itemList;
    }
    
    /**
     * 获取物品名列表
     * @return 物品名列表
     */
    public List<String> getItemNameList(){
        List<String> l = new ArrayList();
        if(hasItem) itemList.forEach(i -> l.add(ItemUtil.getName(i)));
        return l;
    }
    
    /**
     * 获取字符串形式物品名列表(以空格分割)
     * @return 物品名
     */
    public String getItemNameString(){
        String str = "";
        str = itemList.stream().map((n) -> " "+ItemUtil.getName(n)).reduce(str, String::concat);
        if(str.length()>0) str = str.substring(1);
        return str;
    }
    
    /**
     * 设置金币
     * @param coin 数量
     */
    public void setCoin(double coin){
        this.coin = coin;
    }
    
    /**
     * 获取金币
     * @return 数量
     */
    public double getCoin(){
        return this.coin;
    }
    
    /**
     * 设置点券
     * @param point 数量
     */
    public void setPoint(int point){
        this.point = point;
    }
    
    /**
     * 获取点券
     * @return 数量
     */
    public int getPoint(){
        return this.point;
    }

    /**
     * 邮件是否含有附件
     * @return boolean
     */
    public boolean hasFile(){
        if(fileName.equals("0")){
            return hasFileContent();
        }else{
            return (readFile() && hasFileContent());
        }
    }
    
    /**
     * 邮件是否含有任何附件内容
     * @return boolean
     */
    public boolean hasFileContent(){
        return (hasItem || hasCommand || ((GlobalConfig.enVault && coin!=0) || (GlobalConfig.enPlayerPoints && point!=0)));
    }

    /**
     * 读取附件信息
     * @return boolean
     */
    public final boolean readFile(){
        if(GlobalConfig.fileSQL){
            return MailFileUtil.getMailFilesSQL(this);
        }else{
            return MailFileUtil.getMailFilesLocal(this);
        }
    }

    /**
     * 保存附件信息
     * @return boolean
     */
    public boolean saveFile(){
        if(GlobalConfig.fileSQL){
            return MailFileUtil.saveMailFilesSQL(this);
        }else{
            return MailFileUtil.saveMailFilesLocal(this);
        }
    }
    
    @Override
    public boolean Collect(Player p){
        if(!collectValidate(p)) return false;
        // 判断背包空间
        if(hasItem && !hasBlank(p)){
            p.sendMessage(OuterMessage.itemInvNotEnough);
            return false;
        }
        // 设置玩家领取邮件
        if(MailUtil.setCollect(getType(), getId(), p.getName())){
            // 发送邮件附件
            if(hasItem) giveItem(p);
            // 执行邮件指令
            if(hasCommand) doCommand(p);
            // 给钱
            if(coin!=0 && giveCoin(p, coin)) p.sendMessage(OuterMessage.moneyBalanceAdd.replace("%money%", OuterMessage.moneyVault).replace("%count%", Double.toString(coin)));
            if(point!=0 && givePoint(p, point)) p.sendMessage(OuterMessage.moneyBalanceAdd.replace("%money%", OuterMessage.moneyPlayerpoints).replace("%count%", Integer.toString(point)));
            MailCollectEvent mce = new MailCollectEvent(this, p);
            Bukkit.getServer().getPluginManager().callEvent(mce);
            p.sendMessage(OuterMessage.mailCollectSuccess);
            Bukkit.getConsoleSender().sendMessage(OuterMessage.mailCollect.replace("%player%", p.getName()).replace("%type%", getTypeName()).replace("%id%", Integer.toString(getId())));
            return true;
        }else{
            p.sendMessage(OuterMessage.mailCollectError);
            return false;
        }
    }
    
    @Override
    public boolean Send(CommandSender send, ConversationContext cc){
        if(send==null) return false;
        if(getId()==0){
            if(send instanceof Player){
                Player p = (Player)send;
                if(!sendValidate(p, null)) return false;
                // 新建邮件
                // 判断玩家背包里是否有想要发送的物品
                if(hasItem && !p.hasPermission("mailbox.admin.send.check.item")){
                    if(!hasItem(getTrueItemList(), p, cc)){
                        return false;
                    }
                }
                double needCoin = getExpandCoin();
                int needPoint = getExpandPoint();
                if(!enoughMoney(p,needCoin,needPoint,cc)) return false;
                // 获取时间
                generateDate();
                try {
                    // 生成一个文件名
                    fileName = MailFileUtil.generateFilename(getType());
                }catch (Exception ex) {
                    if(cc==null){
                        p.sendMessage(OuterMessage.mailFileNameError);
                    }else{
                        cc.getForWhom().sendRawMessage(OuterMessage.mailFileNameError);
                    }
                    return false;
                }
                if(saveFile()){
                    if(!sendValidate(p, null)){
                        DeleteFile();
                        return false;
                    }
                    if(!enoughMoney(p,needCoin,needPoint,cc)){
                        DeleteFile();
                        return false;
                    }
                    // 删除玩家背包里想要发送的物品
                    if(removeItem(getTrueItemList(), p, cc)){
                        if(sendData()){
                            // 扣钱
                            if(needCoin!=0 && !p.hasPermission("mailbox.admin.send.noconsume.coin") && removeCoin(p, needCoin)){
                                if(cc==null){
                                    p.sendMessage(OuterMessage.mailExpand.replace("%type%", OuterMessage.moneyVault).replace("%count%", Double.toString(needCoin)));
                                }else{
                                    cc.getForWhom().sendRawMessage(OuterMessage.mailExpand.replace("%type%", OuterMessage.moneyVault).replace("%count%", Double.toString(needCoin)));
                                }
                            }
                            if(needPoint!=0 && !p.hasPermission("mailbox.admin.send.noconsume.point") && removePoint(p, needPoint)){
                                if(cc==null){
                                    p.sendMessage(OuterMessage.mailExpand.replace("%type%", OuterMessage.moneyPlayerpoints).replace("%count%", Integer.toString(needPoint)));
                                }else{
                                    cc.getForWhom().sendRawMessage(OuterMessage.mailExpand.replace("%type%", OuterMessage.moneyPlayerpoints).replace("%count%", Integer.toString(needPoint)));
                                }
                            }
                            MailSendEvent mse = new MailSendEvent(this, p);
                            Bukkit.getServer().getPluginManager().callEvent(mse);
                            return true;
                        }else{
                            if(cc==null){
                                p.sendMessage(OuterMessage.mailSendSqlError);
                            }else{
                                cc.getForWhom().sendRawMessage(OuterMessage.mailSendSqlError);
                            }
                            return false;
                        }
                    }else{
                        DeleteFile();
                        return false;
                    }
                }else{
                    StringBuilder str = new StringBuilder(OuterMessage.mailFileSaveError);
                    if(p.isOp()) str.append(", ").append(OuterMessage.fileFilename).append(": ").append(fileName);
                    if(cc==null){
                        p.sendMessage(str.toString());
                    }else{
                        cc.getForWhom().sendRawMessage(str.toString());
                    }
                    return false;
                }
            }else{
                if(!getType().equals("date") || getDate().equals("0")) setDate(TimeUtil.get("ymdhms"));
                try {
                    // 生成一个文件名
                    fileName = MailFileUtil.generateFilename(getType());
                }catch (Exception ex) {
                    if(cc==null){
                        send.sendMessage(OuterMessage.mailFileNameError);
                    }else{
                        cc.getForWhom().sendRawMessage(OuterMessage.mailFileNameError);
                    }
                    return false;
                }
                if(saveFile()){
                    if(sendData()){
                        MailSendEvent mse = new MailSendEvent(this, send);
                        Bukkit.getServer().getPluginManager().callEvent(mse);
                        return true;
                    }else{
                        if(cc==null){
                            send.sendMessage(OuterMessage.mailSendSqlError);
                        }else{
                            cc.getForWhom().sendRawMessage(OuterMessage.mailSendSqlError);
                        }
                        return false;
                    }
                }else{
                    StringBuilder str = new StringBuilder(OuterMessage.mailFileSaveError);
                    if(send.isOp()) str.append(", ").append(OuterMessage.fileFilename).append(": ").append(fileName);
                    if(cc==null){
                        send.sendMessage(str.toString());
                    }else{
                        cc.getForWhom().sendRawMessage(str.toString());
                    }
                    return false;
                }
            }
        }else{
            //TODO 修改已有邮件
            return false;
        }
    }
    
    @Override
    public boolean Delete(Player p){
        if(DeleteFile()){
            return DeleteData(p);
        }else{
            return false;
        }
    }
    
    @Override
    public double getExpandCoin(){
        return VaultUtil.getFileMailExpandCoin(this, 1);
    }
    
    @Override
    public int getExpandPoint(){
        return PlayerPointsUtil.getFileMailExpandPoints(this, 1);
    }
    
    @Override
    public BaseFileMail setType(String type){
        return MailUtil.createBaseFileMail(type, getId(),getSender(), null, null, getTopic(),getContent(),getDate(), null, 0, null, false, null, fileName, itemList, commandList, commandDescription, coin, point);
    }
    
    @Override
    public final BaseFileMail addFile(){
        return this;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder(super.toString());
        if(hasItem && !itemList.isEmpty()){
            str.append("§r-含物品");
            str.append(itemList.size());
            str.append("个");
        }
        if(hasCommand && !commandList.isEmpty()){
            str.append("§r-含指令");
            str.append(commandList.size());
            str.append("条");
        }
        if(coin!=0){
            str.append("§r-含");
            str.append(coin);
            str.append(OuterMessage.moneyVault);
        }
        if(point!=0){
            str.append("§r-含");
            str.append(point);
            str.append(OuterMessage.moneyPlayerpoints);
        }
        return str.toString();
    }
    
}
