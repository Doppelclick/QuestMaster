package com.QuestMaster.utils;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderUtils {
    static Tessellator tessellator = Tessellator.getInstance();
    static WorldRenderer worldRenderer = tessellator.getWorldRenderer();
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
}
