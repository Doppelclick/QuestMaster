package com.QuestMaster.classes.triggers;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Trigger;
import com.QuestMaster.utils.SkyblockItemHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public class ClickPos extends Trigger {
    public int mouseButton;
    public BlockPos position;
    public int amount;
    public int timesClicked;
    public String heldItemName;

    public ClickPos(int mouseButton, BlockPos position, int amount, String heldItemName) {
        this.mouseButton = mouseButton;
        this.position = position;
        this.amount = amount;
        this.heldItemName = heldItemName;
    }

    @Override
    public boolean checkTrigger(Packet clickPos) {
        BlockPos pos = null;
        int mb = -1;
        if (clickPos instanceof C07PacketPlayerDigging) {
            pos = ((C07PacketPlayerDigging) clickPos).getPosition();
            mb = 1;
        } else if (clickPos instanceof C08PacketPlayerBlockPlacement) {
            pos = ((C08PacketPlayerBlockPlacement) clickPos).getPosition();
            mb = 2;
        }
        if (pos != null && (mouseButton == 0 || mouseButton == mb)) {
            if (pos.equals(position)) {
                if (heldItemName.equals("any")) ;
                else if (QuestMaster.mc.thePlayer.getHeldItem() == null) {
                    return false;
                } else {
                    String name = SkyblockItemHandler.getSkyBlockItemID(QuestMaster.mc.thePlayer.getHeldItem());
                    if (!heldItemName.equals(name)) return false;
                }

                timesClicked++;
                if (amount == timesClicked) {
                    timesClicked = 0;
                    return true;
                }
            }
        }
        return false;
    }
}
