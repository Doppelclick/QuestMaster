package com.QuestMaster.classes;

import com.QuestMaster.config.Config;

import java.io.*;
import java.util.ArrayList;


public class Quest extends ArrayList<QuestElement> implements Serializable {
    public String name;
    public boolean enabled = false;

    public Quest(String name) {
        this.name = name;
    }

    public Quest(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    public void checkTrigger(Object object) {
        if (this.enabled || Config.autoEnableQuests) {
            for (int i = 0; i < super.size(); i++) {
                if (super.get(i).progressTrigger.checkTrigger(object)) {
                    super.get(i).enabled = true;
                    this.enabled = true;
                    if (Config.disableLast && i - 1 >= 0) {
                        if (!super.get(i - 1).progressTrigger.equals(super.get(i).progressTrigger)) super.get(i - 1).enabled = false;
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