package com.QuestMaster.utils;

import com.QuestMaster.config.Config;
import com.QuestMaster.gui.QuestInfoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class RenderUtils {
    static Tessellator tessellator = Tessellator.getInstance();
    static WorldRenderer worldRenderer = tessellator.getWorldRenderer();
    public static float lineHeight = 11f;
    private static void addQuadVertices(double x, double y, double w, double h) {
        worldRenderer.pos(x, y + h, 0.0).endVertex();
        worldRenderer.pos(x + w, y + h, 0.0).endVertex();
        worldRenderer.pos(x + w, y, 0.0).endVertex();
        worldRenderer.pos(x, y, 0.0).endVertex();
    }

    private static void enableStuff() {
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }

    private static void disableStuff() {
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
    }


    public static void renderRect(double x, double y, double w, double h, Color color) {
        if (color.getAlpha() == 0) return;
        enableStuff();
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        addQuadVertices(x, y, w, h);
        tessellator.draw();
        disableStuff();
    }

    public static void renderRectBorder(double x, double y, double w, double h, double thickness, Color color) {
        if (color.getAlpha() == 0) return;
        enableStuff();
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        addQuadVertices(x - thickness, y, thickness, h);
        addQuadVertices(x - thickness, y - thickness, w + thickness * 2, thickness);
        addQuadVertices(x + w, y, thickness, h);
        addQuadVertices(x - thickness, y + h, w + thickness * 2, thickness);
        tessellator.draw();
        disableStuff();
    }

    public static void renderTextList(Minecraft mc, List<String> list, float x, float y, double scale, int outline) {
        for (int i = 0; i < list.size(); i++) {
            drawText(mc, list.get(i), x, y + (float) (i * lineHeight * Config.infoTextScale), scale, outline);
        }
    }

    //from DungeonRoooms under the GNU 3.0 license
    public static void drawText(Minecraft mc, String text, float x, float y, double scale, int outline) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        for (String line : text.split("\n")) {
            if (outline == 2) {
                String noColourLine = StringUtils.stripControlCodes(line);
                mc.fontRendererObj.drawString(noColourLine, (int) Math.round(x / scale) - 1, (int) Math.round(y / scale), 0x000000, false);
                mc.fontRendererObj.drawString(noColourLine, (int) Math.round(x / scale) + 1, (int) Math.round(y / scale), 0x000000, false);
                mc.fontRendererObj.drawString(noColourLine, (int) Math.round(x / scale), (int) Math.round(y / scale) - 1, 0x000000, false);
                mc.fontRendererObj.drawString(noColourLine, (int) Math.round(x / scale), (int) Math.round(y / scale) + 1, 0x000000, false);
                mc.fontRendererObj.drawString(line, (int) Math.round(x / scale), (int) Math.round(y / scale), 0xFFFFFF, false);
            } else {
                mc.fontRendererObj.drawString(line, (int) Math.round(x / scale), (int) Math.round(y / scale), 0xFFFFFF, outline == 1);
            }
            y += mc.fontRendererObj.FONT_HEIGHT * scale;
        }
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
    }
}
