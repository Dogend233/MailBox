package com.嘤嘤嘤.qwq.MailBox.Mail;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

interface ItemMail {
    public void getFileItemList();
    public boolean giveItem(Player p);
    public boolean hasItem(ArrayList<ItemStack> isl, Player p);
    public boolean removeItem(ArrayList<ItemStack> isl, Player p);
    public void setItemList(ArrayList<ItemStack> itemList);
    public boolean isHasItem();
    public ArrayList<ItemStack> getItemList();
}
