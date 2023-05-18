package com.QuestMaster.classes;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.config.Config;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;


public class Quest extends ArrayList<QuestElement> implements Serializable {
    public String name;
    public boolean enabled = false;
    public Color color = new Color(0, 0, 0);
    public ArrayList<Island> locations = new ArrayList<>();

    public Quest(String name) {
        this.name = name;
    }

    public Quest(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    public Quest(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Quest(String name, boolean enabled, Color color) {
        this.name = name;
        this.enabled = enabled;
        this.color = color;
    }

    public Quest(String name, boolean enabled, Color color, ArrayList<Island> locations) {
        this.name = name;
        this.enabled = enabled;
        this.color = color;
        this.locations = locations;
    }

    public void checkTrigger(Object object) {
        if ((this.enabled || Config.autoEnableQuests) && ((locations.contains(QuestMaster.island) &! QuestMaster.island.equals(Island.NONE)) || locations.isEmpty())) {
            for (int i = 0; i < super.size(); i++) {
                if (super.get(i).progressTrigger.checkTrigger(object)) {
                    if (super.get(i).name.equals("END_OF_QUEST"))  {
                        this.enabled = false;
                        setState(false);
                    } else {
                        super.get(i).enabled = true;
                        this.enabled = true;
                        if (Config.disableLast && i - 1 >= 0) {
                            if (!super.get(i - 1).progressTrigger.equals(super.get(i).progressTrigger))
                                super.get(i - 1).enabled = false;
                        }
                    }
                }
            }
        }
    }

    public void setState(boolean enabled) {
        if (!enabled) {
            super.forEach(element -> element.enabled = false);
        } else if (super.size() >= 1) {
            super.get(0).enabled = true;
        }
        this.enabled = enabled;
    }
}