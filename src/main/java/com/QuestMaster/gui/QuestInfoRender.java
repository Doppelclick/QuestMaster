package com.QuestMaster.gui;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Island;
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

public class QuestInfoRender {
    @SubscribeEvent
    void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL |! Config.modToggle || (!QuestMaster.inSkyblock &! InfoEditorGui.guiOpened) |! Config.infoToggle || QuestMaster.mc.thePlayer == null) return;
        renderInfo();
    }

    public static void renderInfo() {
        List<String> strings = calculateStrings();
        if (strings.isEmpty() &! InfoEditorGui.guiOpened) return;
        float textOffset = (float) (2f * Config.infoTextScale);
        float x = Config.infoPos.x;
        float y = Config.infoPos.y;
        float height = (float) Math.max(Config.infoWidth.y, strings.size() * RenderUtils.lineHeight * Config.infoTextScale + textOffset);
        RenderUtils.renderRect(x, y, Config.infoWidth.x, height, Config.infoColor);
        if (Config.infoBorder) RenderUtils.renderRectBorder(x, y, Config.infoWidth.x, height, Config.infoBorderThickness, Config.infoBorderColor);
        RenderUtils.renderTextList(QuestMaster.mc, strings, x + textOffset, y + textOffset, Config.infoTextScale, Config.infoTextOutline);
    }

    public static List<String> calculateStrings() {
        String questSpacing = Config.spaceQuests ? " " : "";
        List<String> re = new ArrayList<>();
        for (Map.Entry<String, List<Quest>> category : QuestMaster.quests.entrySet()) {
            boolean addedCat = false;
            for (Quest quest : category.getValue()) {
                if (quest.enabled &! quest.isEmpty() && ((quest.locations.contains(QuestMaster.island) &! QuestMaster.island.equals(Island.NONE)) || quest.locations.isEmpty())) {
                    boolean addedQuest = false;
                    for (int i = 0; i < quest.size(); i++) {
                        QuestElement questElement = quest.get(i);
                        if (questElement.name.equals("END_OF_QUEST")) break;
                        if (!addedCat) {
                            re.add("§1" + category.getKey());
                            addedCat = true;
                        }
                        if (!addedQuest) {
                            re.add(questSpacing + "§l§b" + quest.name + "§r");
                            addedQuest = true;
                        }
                        String[] total = ("§d" + (i + 1) + "> " + (questElement.enabled ? "§f" : "§8") + questElement.name + "§r").split(" ");
                        StringBuilder string = new StringBuilder();
                        for (String s : total) {
                            if ((QuestMaster.mc.fontRendererObj.getStringWidth(StringUtils.stripControlCodes(questSpacing + "  " + string + s)) + 4f) * Config.infoTextScale >= Config.infoWidth.x) {
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
