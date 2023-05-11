package com.QuestMaster.utils;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Island;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;

import javax.vecmath.Vector3f;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

    public static Vector3f vec3ToSerializable(Vec3 vec3) {
        return new Vector3f((float) vec3.xCoord, (float) vec3.yCoord, (float) vec3.zCoord);
    }

    public static Vec3 serializableToVec3(Vector3f vector3f) {
        return new Vec3(vector3f.x, vector3f.y, vector3f.z);
    }

    public static List<Integer> colorToList(Color color) {
        return new ArrayList<>(Arrays.asList(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
    }

    public static Color listTocolor(List<Integer> ints) {
        if (ints.size() == 4) {
            return new Color(ints.get(0), ints.get(1), ints.get(2), ints.get(3));
        }
        return new Color(0, 0, 0, 0);
    }

    public static JsonObject readJsonObject(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean writeJsonObject(File file, JsonObject obj) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(obj, writer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getArgsAfter(String[] strings, int in) {
        List<String> args = new ArrayList<>();
        for (int i = in; i < strings.length; i++) {
            args.add(strings[i]);
        }
        return args;
    }

    public static void sendModMessage(String msg) {
        sendModMessage(new ChatComponentText(QuestMaster.chatTitle + msg));
    }
    public static void sendModMessage(IChatComponent msg) {
        QuestMaster.mc.thePlayer.addChatMessage(msg);
    }

    public static void showClientTitle(String title, String subtitle) {
        QuestMaster.mc.ingameGUI.displayTitle(null, null, 2, 40, 2); //set timings
        QuestMaster.mc.ingameGUI.displayTitle(null, subtitle, -1, -1, -1); //do subtitle
        QuestMaster.mc.ingameGUI.displayTitle(title, null, -1, -1, -1); //do title
    }

    public static void ping() {
        Utils.playSound("note.pling", 1, 0.6f);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Utils.playSound("note.pling", 1, 0.7f);
            }
        }, 180);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Utils.playSound("note.pling", 1, 0.8f);
            }
        }, 360);
    }

    public static void playSound(String sound, float volume, float pitch) {
        QuestMaster.mc.thePlayer.playSound(sound, volume, pitch);
    }

    //from Danker's Skyblock Mod under GNU 3.0 license
    public static void checkForSkyblock() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.theWorld != null && !mc.isSingleplayer()) {
            ScoreObjective scoreboardObj = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
            if (scoreboardObj != null) {
                String scObjName = cleanSB(scoreboardObj.getDisplayName());
                if (scObjName.contains("SKYBLOCK")) {
                    QuestMaster.inSkyblock = true;
                    return;
                }
            }
        }
        QuestMaster.inSkyblock = false;
    }

    //from Danker's Skyblock Mod under GNU 3.0 license
    public static String cleanSB(String scoreboard) {
        char[] nvString = StringUtils.stripControlCodes(scoreboard).toCharArray();
        StringBuilder cleaned = new StringBuilder();

        for (char c : nvString) {
            if ((int) c > 20 && (int) c < 127) {
                cleaned.append(c);
            }
        }

        return cleaned.toString();
    }

    //from Danker's Skyblock Mod under GNU 3.0 license
    public static void checkTabLocation() {
        if (QuestMaster.inSkyblock) {
            Collection<NetworkPlayerInfo> players = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
            for (NetworkPlayerInfo player : players) {
                if (player == null || player.getDisplayName() == null) continue;
                String text = player.getDisplayName().getUnformattedText();
                if (text.startsWith("Area: ") || text.startsWith("Dungeon: ")) {
                    QuestMaster.island = Island.fromTab(text.substring(text.indexOf(":") + 2));
                    return;
                }
            }
        }
        QuestMaster.island = Island.NONE;
    }

    public static void checkArea() {
        String area = "Unknown";
        List<String> scoreboard = getSidebarLines();
        for (String s : scoreboard) {
            String sCleaned = StringUtils.stripControlCodes(s).replace("⚽", "");
            if (sCleaned.contains("⏣")) {
                Matcher loc = Pattern.compile("⏣ (?<area>\\S+((\\s\\S+)?))").matcher(sCleaned);
                if (loc.find()) {
                    area = loc.group("area").replaceFirst("(\\w+)'(s*)\\s", "");
                }
            };
        }
        QuestMaster.area = area;
    }

    //The following function was taken from DungeonRooms under the GNU 3.0 license
    public static List<String> getSidebarLines() {
        List<String> lines = new ArrayList<>();
        if (Minecraft.getMinecraft().theWorld == null) return lines;
        Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
        if (scoreboard == null) return lines;

        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) return lines;

        Collection<Score> scores = scoreboard.getSortedScores(objective);
        List<Score> list = scores.stream()
                .filter(input -> input != null && input.getPlayerName() != null &! input.getPlayerName()
                        .startsWith("#"))
                .collect(Collectors.toList());

        if (list.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
        } else {
            scores = list;
        }

        for (Score score : scores) {
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            lines.add(ScorePlayerTeam.formatPlayerName(team, score.getPlayerName()));
        }

        return lines;
    }

    public static double maxDistance(Vector3f pos, Vector3f target) {
        double x = Math.abs(Math.abs(target.x) - Math.abs(pos.x));
        double y = Math.abs(Math.abs(target.y) - Math.abs(pos.y));
        double z = Math.abs(Math.abs(target.z) - Math.abs(pos.z));
        return Math.max(x, Math.max(y, z));
    }
}