package com.QuestMaster.classes.triggers;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Trigger;
import com.QuestMaster.utils.SkyblockItemHandler;
import net.minecraft.item.ItemStack;

public class PlayerCollect extends Trigger {
    public String itemName;
    public int amount;
    public boolean exact;

    public PlayerCollect(String itemName, int amount, boolean exact) {
        this.itemName = itemName;
        this.amount = amount;
        this.exact = exact;
    }

    @Override
    public boolean checkTrigger(Object object) {
        if (object instanceof ItemStack) {
            if (QuestMaster.mc.thePlayer == null) return false;
            boolean found = compare(((ItemStack) object).getDisplayName());
            if (!found) {
                found = compare(SkyblockItemHandler.actualItemID((ItemStack) object));
            }
            if (found) {
                if (amount > 1) {
                    int inInv = 0;
                    for (ItemStack i : QuestMaster.mc.thePlayer.getInventory()) {
                        boolean f = compare(i.getDisplayName());
                        if (!f) {
                            f = compare(SkyblockItemHandler.actualItemID(i));
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
        }
        return false;
    }

    private boolean compare(String item) {
        return this.exact ? item.equals(this.itemName) : item.contains(this.itemName);
    }
}
