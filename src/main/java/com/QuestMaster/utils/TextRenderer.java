package com.QuestMaster.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;

public class TextRenderer extends Gui {
    //from DungeonRoooms under the GNU 3.0 license
    public static void drawText(Minecraft mc, String text, int x, int y, double scale, boolean outline) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        y -= mc.fontRendererObj.FONT_HEIGHT;
        for (String line : text.split("\n")) {
            y += mc.fontRendererObj.FONT_HEIGHT * scale;
            if (outline) {
                String noColourLine = StringUtils.stripControlCodes(line);
                mc.fontRendererObj.drawString(noColourLine, (int) Math.round(x / scale) - 1, (int) Math.round(y / scale), 0x000000, false);
                mc.fontRendererObj.drawString(noColourLine, (int) Math.round(x / scale) + 1, (int) Math.round(y / scale), 0x000000, false);
                mc.fontRendererObj.drawString(noColourLine, (int) Math.round(x / scale), (int) Math.round(y / scale) - 1, 0x000000, false);
                mc.fontRendererObj.drawString(noColourLine, (int) Math.round(x / scale), (int) Math.round(y / scale) + 1, 0x000000, false);
                mc.fontRendererObj.drawString(line, (int) Math.round(x / scale), (int) Math.round(y / scale), 0xFFFFFF, false);
            } else {
                mc.fontRendererObj.drawString(line, (int) Math.round(x / scale), (int) Math.round(y / scale), 0xFFFFFF, true);
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
    }
}

