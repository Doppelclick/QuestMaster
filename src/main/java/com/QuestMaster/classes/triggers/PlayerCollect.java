package com.QuestMaster.classes.triggers;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Trigger;
import com.QuestMaster.utils.SkyblockItemHandler;
import net.minecraft.item.ItemStack;

public class PlayerCollect extends Trigger {
    public String itemName;
    public int amount;

    public PlayerCollect(String itemName, int amount) {
        this.itemName = itemName;
        this.amount = amount;
    }

    @Override
    public boolean checkTrigger(ItemStack item) {
        if (QuestMaster.mc.thePlayer == null) return false;
        boolean found = item.getDisplayName().equals(itemName);
        if (!found) {
            found = SkyblockItemHandler.actualItemID(item).equals(itemName);
        }
        if (found) {
            if (amount > 1) {
                int inInv = 0;
                for (ItemStack i : QuestMaster.mc.thePlayer.getInventory()) {
                    boolean f = i.getDisplayName().equals(itemName);
                    if (!f) {
                        f = SkyblockItemHandler.actualItemID(i).equals(itemName);
                    }
                    if (f) {
                        inInv += i.stackSize;
                    }
                }
                return inInv >= amount;
            } else {
                return true;
            }
        }
        return false;
    }
}
