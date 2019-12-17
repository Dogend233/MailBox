package com.嘤嘤嘤.qwq.MailBox.Mail;

import java.util.ArrayList;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

interface Item {
    public boolean hasBlank(Player p);
    public boolean giveItem(Player p);
    public boolean hasItem(ArrayList<ItemStack> isl, Player p, ConversationContext cc);
    public boolean removeItem(ArrayList<ItemStack> isl, Player p, ConversationContext cc);
    public void setItemList(ArrayList<ItemStack> itemList);
    public boolean isHasItem();
    public ArrayList<ItemStack> getItemList();
}
