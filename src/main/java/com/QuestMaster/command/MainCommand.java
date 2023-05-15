package com.QuestMaster.command;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.config.Config;
import com.QuestMaster.gui.*;
import com.QuestMaster.utils.FileUtils;
import com.QuestMaster.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.Collections;
import java.util.List;

public class MainCommand extends CommandBase {
    static String help() {
        return "§bQuestMaster§r (/questmaster, /qm)\n"
                + " - help §7| This message§r\n"
                + " - toggle §7| Toggle the mod§r (" + Config.understandMe(Config.modToggle) + ")\n"
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
            return getListOfStringsMatchingLastWord(strings, "help", "toggle", "main", "config", "info", "reload");
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) throws CommandException {
        if (strings.length < 1) {
            new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new MainGui()))).start();
        } else {
            switch (strings[0].toLowerCase()) {
                case "toggle":
                    Config.modToggle =! Config.modToggle;
                    Utils.sendModMessage("Toggled mod " + Config.understandMe(Config.modToggle));
                    Config.writeBooleanConfig("general", "modToggle", Config.modToggle);
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

                default:
                    Utils.sendModMessage(new ChatComponentText(help()));
            }
        }
    }
}