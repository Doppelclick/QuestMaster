package com.QuestMaster.classes.triggers;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Trigger;
import com.QuestMaster.utils.SkyblockItemHandler;
import net.minecraft.item.ItemStack;

public class PlayerCollect extends Trigger {
    public String itemName;
    public int amount;
    public boolean exact;
    private int inInv = 0;

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
                inInv += ((ItemStack) object).stackSize;
                if (inInv >= amount) {
                    this.inInv = 0;
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
