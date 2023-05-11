package com.QuestMaster.config;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.utils.Utils;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;

import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {
    public static Configuration config;
    public final static String configDir = "config/QuestMaster/";
    public final static String configFile = configDir + "QuestMaster.cfg";

    public static boolean modToggle = true;


    public static boolean autoEnableQuests = true;



    public static Point infoPos = new Point(0, 0);
    public static Point infoWidth = new Point(0, 0);
    public static boolean infoBorder = true;
    public static double borderThickness = 2.5;
    public static Color infoColor = new Color(0,0,0, 80);
    public static Color borderColor = new Color(0,0,0, 120);


    public static void init() {
        config = new Configuration(new File(configFile));
        try {
            config.load();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
    }

    public static boolean getBoolean(String category, String key) {
        config = new Configuration(new File(configFile));
        try {
            config.load();
            if (config.getCategory(category).containsKey(key)) {
                return config.get(category, key, false).getBoolean();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
        return true;
    }

    public static int getInt(String category, String key) {
        config = new Configuration(new File(configFile));
        try {
            config.load();
            if (config.getCategory(category).containsKey(key)) {
                return config.get(category, key, 0).getInt();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
        return 0;
    }

    public static double getDouble(String category, String key) {
        config = new Configuration(new File(configFile));
        try {
            config.load();
            if (config.getCategory(category).containsKey(key)) {
                return config.get(category, key, 0D).getDouble();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
        return 0D;
    }

    public static String getString(String category, String key) {
        config = new Configuration(new File(configFile));
        try {
            config.load();
            if (config.getCategory(category).containsKey(key)) {
                return config.get(category, key, "").getString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
        return "";
    }

    public static ArrayList<String> getStringList(String category, String key) {
        config = new Configuration(new File(configFile));
        try {
            config.load();
            if (config.getCategory(category).containsKey(key)) {
                return new ArrayList<>(Arrays.asList(config.get(category, key, new String[0]).getStringList()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
        return new ArrayList<>();
    }

    public static List<Integer> getIntList(String category, String key) {
        config = new Configuration(new File(configFile));
        try {
            config.load();
            if (config.getCategory(category).containsKey(key)) {
                List<Integer> returnable = new ArrayList<>();
                for (int i : config.get(category, key, new int[0]).getIntList()) returnable.add(i);
                return returnable;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
        return null;
    }

    public static void writeBooleanConfig(String category, String key, boolean value) {
        config = new Configuration(new File(configFile));
        try {
            config.load();
            config.get(category, key, value).getBoolean();
            config.getCategory(category).get(key).set(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
    }

    public static void writeIntConfig(String category, String key, int value) {
        config = new Configuration(new File(configFile));
        try {
            config.load();
            int set = config.get(category, key, value).getInt();
            config.getCategory(category).get(key).set(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
    }

    public static void writeDoubleConfig(String category, String key, double value) {
        config = new Configuration(new File(configFile));
        try {
            config.load();
            double set = config.get(category, key, value).getDouble();
            config.getCategory(category).get(key).set(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
    }

    public static void writeStringConfig(String category, String key, String value) {
        config = new Configuration(new File(configFile));
        try {
            config.load();
            String set = config.get(category, key, value).getString();
            config.getCategory(category).get(key).set(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
    }

    public static void writeStringListConfig(String category, String key, List<String> value) {
        config = new Configuration(new File(configFile));
        try {
            String[] write = value.stream().map(String::toString).toArray(String[]::new);
            config.load();
            config.get(category, key, write).getStringList();
            config.getCategory(category).get(key).set(write);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
    }

    public static void writeIntListConfig(String category, String key, List<Integer> value) {
        config = new Configuration(new File(configFile));
        try {
            config.load();
            int[] write = value.stream().mapToInt(i -> i).toArray();
            config.get(category, key, write).getIntList();
            config.getCategory(category).get(key).set(write);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
    }

    public static boolean hasKey(String category, String key) {
        config = new Configuration(new File(configFile));
        try {
            config.load();
            if (!config.hasCategory(category)) return false;
            return config.getCategory(category).containsKey(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
        return false;
    }

    public static boolean deleteFile(String fullDir) {
        File delete = new File(fullDir);
        String[] split = fullDir.split("/");
        String fileName = split[split.length - 1];
        if (!delete.exists() |! fullDir.contains(configDir)) {
            QuestMaster.mc.thePlayer.addChatMessage(new ChatComponentText("Could not locate File " + fullDir));
            return false;
        }
        File newdir = new File(fullDir.replace("QuestMaster", "QuestMaster/bin")).getParentFile();
        if (!newdir.exists()) newdir.mkdirs();
        LocalDateTime now = LocalDateTime.now();
        String time = now.getDayOfMonth() + "-" + now.getMonthValue() + "-" + now.getYear() + "--" + now.getHour() + "-" + now.getMinute() + "-" + now.getSecond();
        return delete.renameTo(new File(newdir + "/" + fileName + "_d_" + time + ".cfg"));
    }

    public static boolean renameFile(String dir, String newdir) {
        File fdir = new File(dir);
        if (!fdir.exists() |! dir.contains(configDir)) {
            QuestMaster.mc.thePlayer.addChatMessage(new ChatComponentText("Could not locate File " + dir));
            return false;
        }
        return fdir.renameTo(new File(newdir));
    }

    public static int deleteBinned() {
        int deleted = 0;
        File main = new File(configDir + "bin/");
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

    public static String understandMe(boolean c) {
        return (c ? "§2On" : "§4Off") + "§r";
    }

    public static void cfgReload() {
        init();

        if (!hasKey("general", "modToggle")) writeBooleanConfig("general", "modToggle", modToggle);

        if (!hasKey("info", "posX")) writeIntConfig("info", "posX", infoPos.x);
        if (!hasKey("info", "posY")) writeIntConfig("info", "posY", infoPos.y);
        if (!hasKey("info", "width")) writeIntConfig("info", "width", infoWidth.x);
        if (!hasKey("info", "height")) writeIntConfig("info", "height", infoWidth.y);
        if (!hasKey("info", "border")) writeBooleanConfig("info", "border", infoBorder);
        if (!hasKey("info", "borderThickness")) writeDoubleConfig("info", "borderThickness", borderThickness);
        if (!hasKey("info", "backgroundColor")) writeIntListConfig("info", "backgroundColor", Utils.colorToList(infoColor));
        if (!hasKey("info", "borderColor")) writeIntListConfig("info", "borderColor", Utils.colorToList(borderColor));

        modToggle = getBoolean("general", "modToggle");

        infoPos = new Point(getInt("info", "posX"), getInt("info", "posY"));
        infoWidth = new Point(getInt("info", "width"), getInt("info", "height"));
        infoBorder = getBoolean("info", "border");
        borderThickness = getDouble("info", "borderThickness");
        infoColor = Utils.listTocolor(Objects.requireNonNull(getIntList("info", "backgroundColor")));
        borderColor = Utils.listTocolor(Objects.requireNonNull(getIntList("info", "borderColor")));

        QuestMaster.logger.info("Reloaded config");
    }
}
