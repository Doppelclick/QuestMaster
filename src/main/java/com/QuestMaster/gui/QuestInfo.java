package com.QuestMaster.gui;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Quest;
import com.QuestMaster.classes.QuestElement;
import com.QuestMaster.config.Config;
import com.QuestMaster.utils.RenderUtils;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestInfo {
    @SubscribeEvent
    void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL |! Config.modToggle |! Config.infoToggle || QuestMaster.mc.thePlayer == null) return;
        renderInfo();
    }

    public static void renderInfo() {
        List<String> strings = calculateStrings();
        if (strings.isEmpty() &! InfoEditorGui.guiOpened) return;
        RenderUtils.renderRect(Config.infoPos.x, Config.infoPos.y, Config.infoWidth.x, Math.max(Config.infoWidth.y, strings.size() * 11f * Config.infoTextScale + 2f * Config.infoTextScale), Config.infoColor);
        if (Config.infoBorder) RenderUtils.renderRectBorder(Config.infoPos.x, Config.infoPos.y, Config.infoWidth.x, Math.max(Config.infoWidth.y, strings.size() * 11f * Config.infoTextScale + 2f * Config.infoTextScale), Config.infoBorderThickness, Config.infoBorderColor);
        RenderUtils.renderTextList(QuestMaster.mc, strings, Config.infoPos.x + (float)(2f * Config.infoTextScale), Config.infoPos.y + (float)(2f * Config.infoTextScale), Config.infoTextScale, Config.infoTextOutline);
    }

    public static List<String> calculateStrings() {
        String questSpacing = Config.spaceQuests ? " " : "";
        List<String> re = new ArrayList<>();
        for (Map.Entry<String, List<Quest>> category : QuestMaster.quests.entrySet()) {
            re.add("§1" + category.getKey());
            for (Quest quest : category.getValue()) {
                if (quest.enabled &! quest.isEmpty()) {
                    re.add(questSpacing + "§l§b" + quest.name + "§r");
                    for (int i = 0; i < quest.size(); i++) {
                        QuestElement questElement = quest.get(i);
                        String[] total = ("§d" + (i + 1) + "> " + (questElement.enabled ? "§f" : "§8") + questElement.name + "§r").split(" ");
                        StringBuilder string = new StringBuilder();
                        for (String s : total) {
                            if (QuestMaster.mc.fontRendererObj.getStringWidth(StringUtils.stripControlCodes(string + s)) * Config.infoTextScale > Config.infoWidth.x - 8f * Config.infoTextScale) {
                                re.add(questSpacing + " " + string);
                                string = new StringBuilder((questElement.enabled ? "§f" : "§8") + s);
                            } else string.append(" ").append(s);
                        }
                        if (string.length() > 0) re.add(questSpacing + " " + string);
                    }
                }
            }
        }
        return re;
    }
}
