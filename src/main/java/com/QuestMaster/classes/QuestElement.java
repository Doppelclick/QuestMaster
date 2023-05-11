package com.QuestMaster.classes;

import javax.vecmath.Vector3f;
import java.io.Serializable;

public class QuestElement implements Serializable {
    public String name;
    public Trigger progressTrigger;
    public Vector3f waypoint;
    public boolean enabled = false;

    public QuestElement(String name, Trigger progressTrigger, Vector3f waypoint) {
        this.name = name;
        this.progressTrigger = progressTrigger;
        this.waypoint = waypoint;
    }
}