package com.QuestMaster.command;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Quest;
import com.QuestMaster.classes.QuestElement;
import com.QuestMaster.classes.triggers.ChatMessage;
import com.QuestMaster.config.Config;
import com.QuestMaster.gui.positionEditorGui;
import com.QuestMaster.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class MainCommand extends CommandBase {
    static String help() {
        return "§bQuestMaster§r (/questmaster, /qm)\n"
                + " /qm help §7| This message§r\n"
                + " /qm toggle §7| Toggle the mod§r (" + Config.understandMe(Config.modToggle) + ")\n"
                + " /qm reload §7| Reload Config from file§r\n"
                + " /qm infogui §7| Edit the info position§r";

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
            return getListOfStringsMatchingLastWord(strings, "help", "toggle", "reload", "infogui");
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) throws CommandException {
        if (strings.length < 1) {
            Utils.sendModMessage(new ChatComponentText(help()));
        } else {
            switch (strings[0].toLowerCase()) {
                case "toggle":
                    Config.modToggle =! Config.modToggle;
                    Utils.sendModMessage("Toggled mod " + Config.understandMe(Config.modToggle));
                    Config.writeBooleanConfig("general", "modToggle", Config.modToggle);
                    break;
                case "reload":
                    Config.cfgReload();
                    break;
                case "infogui":
                    new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new positionEditorGui()))).start();
                    break;
                case "test":
                    Quest q = new Quest() {{
                        }};
                    String f = Config.configDir + "quest.bin";
                    Quest.save(q, f);
                    Quest quest = Quest.load(f);
                    Utils.sendModMessage(quest.enabled + " " + quest.disableLast);
                    break;

                default:
                    Utils.sendModMessage(new ChatComponentText(help()));
            }
        }
    }
}
