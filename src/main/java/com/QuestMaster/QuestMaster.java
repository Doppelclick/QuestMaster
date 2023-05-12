package com.QuestMaster;

import com.QuestMaster.classes.Island;
import com.QuestMaster.classes.Quest;
import com.QuestMaster.classes.QuestElement;
import com.QuestMaster.classes.Trigger;
import com.QuestMaster.command.MainCommand;
import com.QuestMaster.config.Config;
import com.QuestMaster.handlers.PacketHandler;
import com.QuestMaster.utils.FileUtils;
import com.QuestMaster.utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.vecmath.Vector3f;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Mod(modid = QuestMaster.Name, version = QuestMaster.V)
public class QuestMaster {
    public static final String Name = "QuestMaster";
    public static final String V = "0.1.0";
    public static String chatTitle = "§b[QuestMaster]§r ";
    public static final Logger logger = LogManager.getLogger(Name);
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static int tickAmount = 0;

    public static boolean inSkyblock = false;
    public static Island island = Island.NONE;
    public static String area = "unknown";

    public static HashMap<String, List<Quest>> quests = new HashMap<String, List<Quest>>(){{
       put("testCategory", Collections.singletonList(new Quest("testQuest", true, true) {{
           add(new QuestElement("test this thing 1", new Trigger(), new Vector3f(0,0,0)));
           add(new QuestElement("try out this 2", new Trigger(), new Vector3f(0,10,0)));
       }}));
    }};

    @Mod.EventHandler
    void preInit(final FMLPreInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new MainCommand());
    }

    @Mod.EventHandler
    void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        Config.cfgReload();
        int fileDelStat = FileUtils.deleteBinned();

        int questLoadStat = 0; // FileUtils.loadQuests(); todo: re-enable

        logger.info("Finished init, deleted " + fileDelStat + " binned files from yesterday or older, loaded " + questLoadStat + " quests.");
    }

    @SubscribeEvent
    void serverConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (mc.getCurrentServerData() == null) return;
        if (mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel.")) {
            event.manager.channel().pipeline().addBefore("packet_handler", "diana_packet_handler", new PacketHandler());
            logger.info("Added Hypixel packet handler, searching for updates");
            updateThread();
        }
    }


    void updateThread() {
        if (mc.thePlayer == null) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    updateThread();
                }
            }, 100);
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("https://api.github.com/repos/Doppelclick/Diana/releases/latest");
                        URLConnection request = url.openConnection();
                        request.connect();
                        JsonParser json = new JsonParser();
                        JsonObject latestRelease = json.parse(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();

                        String latestTag = latestRelease.get("tag_name").getAsString();
                        DefaultArtifactVersion currentVersion = new DefaultArtifactVersion(V);
                        DefaultArtifactVersion latestVersion = new DefaultArtifactVersion(latestTag.substring(1));

                        if (currentVersion.compareTo(latestVersion) < 0) {
                            logger.info("Update available");
                            String releaseURL = "https://github.com/Doppelclick/Diana/releases/latest";
                            ChatComponentText update = new ChatComponentText("§l§2  [UPDATE]  ");
                            update.setChatStyle(update.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, releaseURL)).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("github - " + latestVersion))));
                            Utils.sendModMessage(new ChatComponentText(chatTitle + "§cSolver is outdated. Please update to " + latestTag + ".\n").appendSibling(update));
                        } else logger.info("No update found");
                    } catch (Exception e) {
                        logger.warn("An error has occurred connecting to github");
                        Utils.sendModMessage("§cAn error has occurred connecting to github");
                        e.printStackTrace();
                    }
                }
            }, 1000);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getMinecraft();

        tickAmount++;
        if (tickAmount % 20 == 0) {
            if (mc.thePlayer != null) {
                if (!inSkyblock) Utils.checkForSkyblock();
                if (island.equals(Island.NONE)) Utils.checkTabLocation();
                else Utils.checkArea();
            }

            tickAmount = 0;
        }
    }

    @SubscribeEvent
    void worldUnload(WorldEvent.Unload event) {
        tickAmount = 0;

        inSkyblock = false;
        island = Island.NONE;
        area = "unknown";
    }
}