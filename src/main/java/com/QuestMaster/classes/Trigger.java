package com.QuestMaster.classes;

import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.Vec3;

import javax.vecmath.Vector3f;
import java.io.Serializable;

public class Trigger implements Serializable {

    public boolean checkTrigger(Object object) {
        if (object instanceof String) {
            return checkTrigger((String) object);
        } else if (object instanceof Vector3f) {
            return checkTrigger((Vector3f) object);
        } else if (object instanceof Packet) {
            return checkTrigger((Packet) object);
        } else if (object instanceof ItemStack) {
            return checkTrigger((ItemStack) object);
        }
        return false;
    }

    public boolean checkTrigger(String message) {
        return false;
    }

    public boolean checkTrigger(Vector3f playerPos) {
        return false;
    }

    public boolean checkTrigger(Packet clickedPos) {
        return false;
    }

    public boolean checkTrigger(ItemStack item) {
        return false;
    }
}