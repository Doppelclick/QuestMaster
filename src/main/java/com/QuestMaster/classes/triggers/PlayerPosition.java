package com.QuestMaster.classes.triggers;

import com.QuestMaster.classes.Trigger;
import com.QuestMaster.utils.Utils;
import net.minecraft.util.Vec3;

public class PlayerPosition extends Trigger {
    public Vec3 position;
    public double distance;
    public int durationMillis;
    public long entered = 0;

    public PlayerPosition(Vec3 position, double distance, int durationMillis) {
        this.position = position;
        this.distance = distance;
        this.durationMillis = durationMillis;
    }

    @Override
    public boolean checkTrigger(Vec3 playerPos) {
        if (Utils.maxDistance(playerPos, this.position) < distance) {
            if (entered == 0) entered = System.currentTimeMillis();
            else if (System.currentTimeMillis() - entered >= durationMillis) {
                entered = 0;
                return true;
            }
        } else if (entered != 0) entered = 0;
        return false;
    }
}
