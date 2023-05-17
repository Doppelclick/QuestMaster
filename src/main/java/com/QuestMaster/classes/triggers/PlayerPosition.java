package com.QuestMaster.classes.triggers;

import com.QuestMaster.classes.Trigger;
import com.QuestMaster.utils.Utils;

import javax.vecmath.Vector3f;

public class PlayerPosition extends Trigger {
    public Vector3f position;
    public double distance;
    public int durationMillis;
    public long entered = 0;

    public PlayerPosition(Vector3f position, double distance, int durationMillis) {
        this.position = position;
        this.distance = distance;
        this.durationMillis = durationMillis;
    }

    @Override
    public boolean checkTrigger(Object object) {
        if (object instanceof Vector3f) {
            if (Utils.maxDistance((Vector3f) object, this.position) < distance) {
                if (entered == 0) entered = System.currentTimeMillis();
                else if (System.currentTimeMillis() - entered >= durationMillis) {
                    entered = 0;
                    return true;
                }
            } else if (entered != 0) entered = 0;
        }
        return false;
    }
}
