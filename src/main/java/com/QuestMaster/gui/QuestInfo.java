package com.QuestMaster.gui;

import com.QuestMaster.config.Config;
import com.QuestMaster.utils.RenderUtils;

public class QuestInfo {
    public static void renderInfo() {
        RenderUtils.renderRect(Config.infoPos.x, Config.infoPos.y, Config.infoWidth.x, Config.infoWidth.y, Config.infoColor);
        if (Config.infoBorder) RenderUtils.renderRectBorder(Config.infoPos.x, Config.infoPos.y, Config.infoWidth.x, Config.infoWidth.y, Config.borderThickness, Config.borderColor);
    }
}
