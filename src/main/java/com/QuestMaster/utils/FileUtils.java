package com.QuestMaster.utils;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Quest;
import com.QuestMaster.config.Config;
import net.minecraft.util.ChatComponentText;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {
    public static String questDir = Config.configDir + "Quests/";

    public static boolean deleteFile(String fullDir) {
        File delete = new File(fullDir);
        String[] split = fullDir.split("/");
        String fileName = split[split.length - 1].replaceAll("\\.\\S+", "");
        if (!delete.exists() |! fullDir.contains(Config.configDir)) {
            QuestMaster.mc.thePlayer.addChatMessage(new ChatComponentText("Could not locate File " + fullDir));
            return false;
        }
        File newdir = new File(fullDir.replace("QuestMaster", "QuestMaster/bin")).getParentFile();
        if (!newdir.exists()) newdir.mkdirs();
        LocalDateTime now = LocalDateTime.now();
        String time = now.getDayOfMonth() + "-" + now.getMonthValue() + "-" + now.getYear() + "--" + now.getHour() + "-" + now.getMinute() + "-" + now.getSecond();
        return delete.renameTo(new File(newdir + "/" + fileName + "_d_" + time + ".bin"));
    }

    public static boolean renameFile(String dir, String newdir) {
        File fdir = new File(dir);
        if (!fdir.exists() |! dir.contains(Config.configDir)) {
            QuestMaster.mc.thePlayer.addChatMessage(new ChatComponentText("Could not locate File " + dir));
            return false;
        }
        return fdir.renameTo(new File(newdir));
    }

    public static int deleteBinned() {
        int deleted = 0;
        File main = new File(Config.configDir + "bin/");
        if (main.exists()) {
            LocalDateTime now = LocalDateTime.now();
            int cd = now.getDayOfMonth();
            int cm = now.getMonthValue();
            int cy = now.getYear();
            for (File dir : Objects.requireNonNull(main.listFiles())) {
                for (File f : Objects.requireNonNull(dir.listFiles())) {
                    Matcher time = Pattern.compile("/(?<day>\\d+)-(?<month>\\d+)-(?<year>\\d+)--(?<hour>\\d+)-(?<minute>\\d+)-(?<second>\\d+)\\.").matcher(f.getPath());
                    if (time.find()) {
                        int d = Integer.parseInt(time.group("day"));
                        int m = Integer.parseInt(time.group("month"));
                        int y = Integer.parseInt(time.group("year"));
                        if (cd > d || cm > m || cy > y) {
                            if (f.delete()) deleted++;
                        }
                    }
                }
            }
        }
        return deleted;
    }

    public static boolean save(Quest quest, String dir, String filename) {
        try {
            new File(dir).mkdirs();
            ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(Paths.get(dir + filename)));
            out.writeObject(quest);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Quest load(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(Paths.get(filename)))) {
            Object object = in.readObject();
            if (object instanceof Quest) return (Quest) object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int loadQuests() {
        File questDir = new File(Config.configDir + "Quests/");
        QuestMaster.quests.clear();
        int questAmount = 0;
        try {
            for (File dir : Objects.requireNonNull(questDir.listFiles())) {
                if (dir.isDirectory()) {
                    String[] parts = dir.getPath().split("\\\\");
                    String directory = parts[parts.length - 1];
                    QuestMaster.quests.put(directory, new ArrayList<>());
                    for (File file : Objects.requireNonNull(dir.listFiles())) {
                        if (file.getPath().endsWith(".bin")) {
                            Quest quest = load(file.getPath());
                            if (quest != null) {
                                QuestMaster.quests.get(directory).add(quest);
                                questAmount++;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questAmount;
    }

    public static void saveQuests() {
        try {
            for (Map.Entry<String, List<Quest>> dir : QuestMaster.quests.entrySet()) {
                for (Quest quest : dir.getValue()) {
                    save(quest, questDir + dir.getKey() + "/", quest.name + ".bin");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
