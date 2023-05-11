package com.QuestMaster.classes;

import com.QuestMaster.config.Config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Quest extends ArrayList<QuestElement> implements Serializable {
    public boolean enabled = false;
    public boolean disableLast = true;

    public Quest() {

    }

    public Quest(boolean disableLast) {
        this.disableLast = disableLast;
    }

    public Quest(boolean enabled, boolean disableLast) {
        this.enabled = enabled;
        this.disableLast = disableLast;
    }

    public void checkTrigger(Object object) {
        if (this.enabled || Config.autoEnableQuests) {
            for (int i = 0; i < super.size(); i++) {
                if (super.get(i).progressTrigger.checkTrigger(object)) {
                    super.get(i).enabled = true;
                    this.enabled = true;
                    if (this.disableLast && i - 1 >= 0) {
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

    public static void save(Quest quest, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(Paths.get(filename)))) {
            out.writeObject(quest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Quest load(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(Paths.get(filename)))) {
            return (Quest) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}