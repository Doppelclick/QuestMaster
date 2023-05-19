package com.QuestMaster.classes.triggers;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Trigger;
import com.QuestMaster.utils.SkyblockItemHandler;
import com.QuestMaster.utils.Utils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import javax.vecmath.Vector3f;

public class ClickPos extends Trigger {
    public int mouseButton;
    public Vector3f position;
    public int amount;
    public int timesClicked;
    public String heldItemName;
    public boolean exact;
    public int tolerance = 0;

    public ClickPos(int mouseButton, Vector3f position, int amount, String heldItemName, boolean exact) {
        this.mouseButton = mouseButton;
        this.position = position;
        this.amount = amount;
        this.heldItemName = heldItemName;
        this.exact = exact;
    }

    @Override
    public boolean checkTrigger(Object object) {
        if (object instanceof Packet<?>) {
            Packet<?> clickPos = (Packet<?>) object;
            BlockPos pos = null;
            int mb = -1;
            if (clickPos instanceof C07PacketPlayerDigging) {
                pos = ((C07PacketPlayerDigging) clickPos).getPosition();
                mb = 0;
            } else if (clickPos instanceof C08PacketPlayerBlockPlacement) {
                pos = ((C08PacketPlayerBlockPlacement) clickPos).getPosition();
                mb = 1;
            } else if (clickPos instanceof C02PacketUseEntity) {
                pos = ((C02PacketUseEntity) clickPos).getEntityFromWorld(QuestMaster.mc.theWorld).getPosition();
                mb = ((C02PacketUseEntity) clickPos).getAction().equals(C02PacketUseEntity.Action.ATTACK) ? 0 : 1;
            }
            if (pos != null && (mouseButton == -1 || mouseButton == mb)) {
                if (comparePos(pos)) {
                    if (heldItemName.equals("any"));
                    else if (QuestMaster.mc.thePlayer.getHeldItem() == null) {
                        return false;
                    } else {
                        String name = SkyblockItemHandler.getSkyBlockItemID(QuestMaster.mc.thePlayer.getHeldItem());
                        if (!compare(name) &! compare(QuestMaster.mc.thePlayer.getHeldItem().getDisplayName())) return false;
                    }

                    timesClicked++;
                    if (amount == timesClicked) {
                        timesClicked = 0;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean compare(String item) {
        return this.exact ? item.equals(this.heldItemName) : item.contains(this.heldItemName);
    }

    private boolean comparePos(BlockPos pos) {
        Vector3f position = Utils.vec3ToSerializable(new Vec3(pos));
        if (this.tolerance == 0) return position.equals(this.position);
        else if (this.tolerance == 1) return position.x == this.position.x && position.z == this.position.z && Math.abs(position.y - this.position.y) < 1.1f;
        return Utils.maxDistance(this.position, position) < 1.1f;
    }
}
