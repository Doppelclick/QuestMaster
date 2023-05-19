package com.QuestMaster.command;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Island;
import com.QuestMaster.classes.Quest;
import com.QuestMaster.classes.QuestElement;
import com.QuestMaster.config.Config;
import com.QuestMaster.gui.*;
import com.QuestMaster.utils.FileUtils;
import com.QuestMaster.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.*;

public class MainCommand extends CommandBase {
    static String help() {
        return "§bQuestMaster§r (/questmaster, /qm)\n"
                + " - help §7| This message§r\n"
                + " - toggle §7| Toggle the mod§r (" + Config.understandMe(Config.modToggle) + ")\n"
                + " - next [first quest / quest name] §7| Skip to the next element of a quest§r\n"
                + " - main §7| Main gui and quests§r\n"
                + " - config §7| General config gui§r\n"
                + " - info §7| Edit the info display position and config§r\n"
                + " - reload §7| Reload config and quests from file§r";
    }

    @Override
    public String getCommandName() {
        return "questmaster";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("qm");
    }

    @Override
    public String getCommandUsage(ICommandSender arg0) {
        return "/" + getCommandName();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] strings, BlockPos pos) {
        if (strings.length == 1) {
            return getListOfStringsMatchingLastWord(strings, "help", "toggle", "next", "main", "config", "info", "reload");
        } else if (strings.length == 2) {
            List<String> quests = new ArrayList<>();
            for (Map.Entry<String, List<Quest>> category : QuestMaster.quests.entrySet()) {
                for (Quest quest : category.getValue()) {
                    if (quest.enabled &! quest.isEmpty() && ((quest.locations.contains(QuestMaster.island) &! QuestMaster.island.equals(Island.NONE)) || quest.locations.isEmpty())) {
                        quests.add(quest.name);
                    }
                }
            }
            if (!quests.isEmpty()) return getListOfStringsMatchingLastWord(strings, quests);
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) throws CommandException {
        if (strings.length < 1) {
            GuiManager.openLastGui();
        } else {
            switch (strings[0].toLowerCase()) {
                case "toggle":
                    Config.modToggle =! Config.modToggle;
                    Utils.sendModMessage("Toggled mod " + Config.understandMe(Config.modToggle));
                    Config.writeBooleanConfig("general", "modToggle", Config.modToggle);
                    break;

                case "next":
                    StringBuilder questName = new StringBuilder();
                    if (strings.length > 1) {
                        for (int i = 1; i < strings.length; i++) {
                            questName.append(strings[i]);
                        }
                    }
                    for (Map.Entry<String, List<Quest>> category : QuestMaster.quests.entrySet()) {
                        for (int q = 0; q < category.getValue().size(); q++) {
                            Quest quest = QuestMaster.quests.get(category.getKey()).get(q);
                            if (quest.enabled &! quest.isEmpty() && ((quest.locations.contains(QuestMaster.island) &! QuestMaster.island.equals(Island.NONE)) || quest.locations.isEmpty()) &&
                                    (questName.toString().isEmpty() || quest.name.contentEquals(questName))) {
                                for (QuestElement element : quest) {
                                    if (element.enabled) {
                                        int index = quest.indexOf(element);
                                        if (Config.disableLast) QuestMaster.quests.get(category.getKey()).get(q).enabled = false;
                                        if (quest.size() >= index + 2) QuestMaster.quests.get(category.getKey()).get(index + 1).enabled = true;
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    break;

                case "main":
                case "gui":
                case "maingui":
                    new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new MainGui()))).start();
                    break;

                case "config":
                case "configgui":
                    new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new GeneralConfigGui()))).start();
                    break;

                case "info":
                case "infogui":
                    new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new InfoEditorGui()))).start();
                    break;

                case "reload":
                    Config.cfgReload();
                    FileUtils.loadQuests();
                    break;

                case "configcommand":
                    if (strings.length < 2) return;
                    switch (strings[1].toLowerCase()) {
                        case "deletecategory":
                            CategoryGui.deleteCategory();
                            GuiManager.lastgui = "main";
                            break;
                        case "canceldeletecategory":
                            CategoryGui.deleting = false;
                            Utils.sendModMessage("§aCancelled category deletion");
                            break;
                        case "deletequest":
                            QuestCreatorGui.deleteQuest();
                            GuiManager.lastgui = "category";
                            break;
                        case "canceldeletequest":
                            QuestCreatorGui.deleting = false;
                            Utils.sendModMessage("§aCancelled quest deletion");
                            break;
                        case "deletequestelement":
                            QuestElementCreatorGui.deleteElement();
                            GuiManager.lastgui = "quest";
                            break;
                        case "canceldeletequestelement":
                            QuestElementCreatorGui.deleting = false;
                            Utils.sendModMessage("§aCancelled quest element deletion");
                            break;
                    }
                    break;

                case "dev":
                    if (strings.length > 1) {
                        switch (strings[1].toLowerCase()) {
                            case "printtab":
                                Collection<NetworkPlayerInfo> players = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
                                for (NetworkPlayerInfo player : players) {
                                    if (player == null || player.getDisplayName() == null) continue;
                                    String text = player.getDisplayName().getUnformattedText();
                                    Utils.sendModMessage(text);
                                }
                                break;
                        }
                    } else {
                        QuestMaster.dev =! QuestMaster.dev;
                        Utils.sendModMessage("Dev mode " + Config.understandMe(QuestMaster.dev));
                    }
                    break;

                default:
                    Utils.sendModMessage(new ChatComponentText(help()));
            }
        }
    }
}