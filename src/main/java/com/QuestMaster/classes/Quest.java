package com.QuestMaster.classes;

import com.QuestMaster.config.Config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Quest implements Serializable {
    public List<QuestElement> elements = new ArrayList<>();
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
            for (int i = 0; i < this.elements.size(); i++) {
                if (this.elements.get(i).progressTrigger.checkTrigger(object)) {
                    this.elements.get(i).enabled = true;
                    this.enabled = true;
                    if (this.disableLast && i - 1 >= 0) {
                        if (!this.elements.get(i - 1).progressTrigger.equals(this.elements.get(i).progressTrigger)) this.elements.get(i - 1).enabled = false;
                    }
                }
            }
        }
    }

    public void setState(boolean enabled) {
        if (!enabled) {
            this.elements.forEach(element -> element.enabled = false);
        } else if (this.elements.size() >= 1) {
            this.elements.get(0).enabled = true;
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
