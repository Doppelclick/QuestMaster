package com.QuestMaster.classes;

import net.minecraft.util.Vec3;

import java.io.Serializable;

public class QuestElement implements Serializable {
    public String name;
    public Trigger progressTrigger;
    public Vec3 waypoint;
    public boolean enabled = false;

    public QuestElement(String name, Trigger progressTrigger, Vec3 waypoint) {
        this.name = name;
        this.progressTrigger = progressTrigger;
        this.waypoint = waypoint;
    }
}
