package com.QuestMaster.gui;

import com.QuestMaster.config.Config;
import com.QuestMaster.utils.TextRenderer;
import com.QuestMaster.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class positionEditorGui extends GuiScreen {
    private static boolean positionEditorOpened = false;
    private GuiButton close;
    private GuiSlider infoWidth;
    private GuiSlider infoHeight;
    private GuiButton border;
    private GuiSlider borderThickness;
    private GuiButton infoColor;
    private GuiButton borderColor;
    private GuiSlider r;
    private GuiSlider g;
    private GuiSlider b;
    private GuiSlider a;
    private List<Gui> colorCat = new ArrayList<>();
    private static List<Integer> currentRGBA = new ArrayList<>(Arrays.asList(0,0,0,0));
    private static int settingColors = 0;
    private static Point oldInfoPos = new Point(0, 0);

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        close = new GuiButton(0, width / 2 - 75, height / 6 * 5, 150, 20, "Close");
        infoWidth = new GuiSlider(1, width / 2 - 75, height / 6, 150, 20, "info Width: ", "", 1, 500, Config.infoWidth.x, false, true);
        infoHeight = new GuiSlider(2, width / 2 - 75, height / 6 + 25, 150, 20, "Minimum info Height: ", "", 1, 500, Config.infoWidth.y, false, true);
        border = new GuiButton(3, width / 2 - 75, height / 6 + 50, 150, 20, "Border: " + Config.understandMe(Config.infoBorder));
        borderThickness = new GuiSlider(2, width / 2 - 75, height / 6 + 75, 150, 20, "Border thickness: ", "", 0, 70, Config.borderThickness * 10, true, true);
        infoColor = new GuiButton(5, width / 2 - 75, height / 6 + 100, 150, 20, "Set info Color");
        borderColor = new GuiButton(6, width / 2 - 75, height / 6 + 125, 150, 20, "Set border Color");

        r = new GuiSlider(10, 0, 0, 150, 20, "Red: ", "", 0, 255, 0, false, true);
        g = new GuiSlider(11, 0, 0, 150, 20, "Green: ", "", 0, 255, 0, false, true);
        b = new GuiSlider(12, 0, 0, 150, 20, "Blue: ", "", 0, 255, 0, false, true);
        a = new GuiSlider(13, 0, 0, 150, 20, "Alpha: ", "", 0, 255, 0, false, true);
        colorCat = new ArrayList<>(Arrays.asList(r, g, b, a));
        settingColors = 0;


        this.buttonList.add(close);
        this.buttonList.add(infoWidth);
        this.buttonList.add(infoHeight);
        this.buttonList.add(border);
        this.buttonList.add(borderThickness);
        this.buttonList.add(infoColor);
        this.buttonList.add(borderColor);

        oldInfoPos = Config.infoPos;
        positionEditorOpened = true;
    }

    private void RGBAState(int buttonState) {
        boolean shouldInit = settingColors == 0;
        boolean shouldDisable = settingColors == buttonState;

        if (shouldInit &! shouldDisable) {
            setColorList(true, buttonState);
            GuiManager.displaycategory(colorCat, width / 2 + 200, height / 6 + 25, 150, true);
            for (int i = 0; i < 4; i++) {
                ((GuiSlider) colorCat.get(i)).setValue(currentRGBA.get(i));
                ((GuiSlider) colorCat.get(i)).updateSlider();
                this.buttonList.add((GuiSlider) colorCat.get(i));
            }
            settingColors = buttonState;
        } else if (!shouldInit &! shouldDisable) {
            setColorList(false, settingColors);
            setColorList(true, buttonState);
            for (int i = 0; i < 4; i++) {
                ((GuiSlider) colorCat.get(i)).setValue(currentRGBA.get(i));
                ((GuiSlider) colorCat.get(i)).updateSlider();
            }
            settingColors = buttonState;
        } else if (!shouldInit) {
            setColorList(false, buttonState);
            this.buttonList.remove(r);
            this.buttonList.remove(g);
            this.buttonList.remove(b);
            this.buttonList.remove(a);
            settingColors = 0;
        }
    }

    private void setColorList(boolean init, int button) {
        if (button == 1) {
            if (init) currentRGBA = Utils.colorToList(Config.infoColor);
            else Config.infoColor = Utils.listTocolor(currentRGBA);
        } else if (button == 2) {
            if (init) currentRGBA = Utils.colorToList(Config.borderColor);
            else Config.borderColor = Utils.listTocolor(currentRGBA);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (infoWidth.dragging) {
            Config.infoWidth = new Point(infoWidth.getValueInt(), Config.infoWidth.y);
        } else if (infoHeight.dragging) {
            Config.infoWidth = new Point(Config.infoWidth.x, infoHeight.getValueInt());
        } else if (borderThickness.dragging) {
            Config.borderThickness = borderThickness.getValue() / 10D;
        } else if (settingColors != 0) {
            boolean save = true;
            if (r.dragging) {
                currentRGBA = Arrays.asList(r.getValueInt(), currentRGBA.get(1), currentRGBA.get(2), currentRGBA.get(3));
            } else if (g.dragging) {
                currentRGBA = Arrays.asList(currentRGBA.get(0), g.getValueInt(), currentRGBA.get(2), currentRGBA.get(3));
            } else if (b.dragging) {
                currentRGBA = Arrays.asList(currentRGBA.get(0), currentRGBA.get(1), b.getValueInt(), currentRGBA.get(3));
            } else if (a.dragging) {
                currentRGBA = Arrays.asList(currentRGBA.get(0), currentRGBA.get(1), currentRGBA.get(2), a.getValueInt());
            } else save = false;
            if (save) setColorList(false, settingColors);
        }

        ScaledResolution sr = new ScaledResolution(mc);
        int height = sr.getScaledHeight();
        int width = sr.getScaledWidth();

        String text1 = "§lPress enter to save info position for (clicked) cursor position";
        int text1Width = mc.fontRendererObj.getStringWidth(text1);
        TextRenderer.drawText(mc, EnumChatFormatting.WHITE + text1, width / 2 - text1Width / 2, height / 6 - 15, 1D, false);

        QuestInfo.renderInfo();
    }

    @Override
    public void handleMouseInput() throws IOException {
        if (positionEditorOpened &! buttonSelected()) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            Config.infoPos = new Point(mouseX, mouseY);
        }
        super.handleMouseInput();
    }

    private boolean buttonSelected() {
        return close.isMouseOver() || infoWidth.isMouseOver() || infoWidth.dragging || infoHeight.isMouseOver() || infoHeight.dragging || border.isMouseOver()
                || borderThickness.isMouseOver() || borderThickness.dragging || infoColor.isMouseOver() || borderColor.isMouseOver()
                || r.isMouseOver() || r.dragging || g.isMouseOver() || g.dragging || b.isMouseOver() || b.dragging || a.isMouseOver() || a.dragging;
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
        if (Keyboard.isKeyDown(Keyboard.KEY_RETURN) || Keyboard.isKeyDown(Keyboard.KEY_NUMPADENTER)) {
            oldInfoPos = Config.infoPos;
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button == close) mc.thePlayer.closeScreen();
        else if (button == border) {
            Config.infoBorder =! Config.infoBorder;
            Config.writeBooleanConfig("info", "border", Config.infoBorder);
            border.displayString = "Border: " + Config.understandMe(Config.infoBorder);
        } else if (button == infoColor) {
            RGBAState(1);
        } else if (button == borderColor) {
            RGBAState(2);
        }
    }

    @Override
    public void onGuiClosed() {
        positionEditorOpened = false;
        Config.infoPos = oldInfoPos;
        Config.writeIntConfig("info", "posX", Config.infoPos.x);
        Config.writeIntConfig("info", "posY", Config.infoPos.y);
        Config.writeIntConfig("info", "width", Config.infoWidth.x);
        Config.writeIntConfig("info", "height", Config.infoWidth.y);
        Config.writeDoubleConfig("info", "borderThickness", Config.borderThickness);
        Config.writeIntListConfig("info", "backgroundColor", Utils.colorToList(Config.infoColor));
        Config.writeIntListConfig("info", "borderColor", Utils.colorToList(Config.borderColor));
    }
}

