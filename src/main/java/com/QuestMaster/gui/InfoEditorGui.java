package com.QuestMaster.gui;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.config.Config;
import com.QuestMaster.utils.RenderUtils;
import com.QuestMaster.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfoEditorGui extends GuiScreen {
    public static boolean guiOpened = false;

    private GuiButton close;
    private GuiButton back;
    private GuiButton toggle;
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
    private static List<Integer> currentRGBA = new ArrayList<>(Arrays.asList(0, 0, 0, 0));
    private GuiButton spaceQuests;
    private GuiButton textOutline;
    private GuiSlider textScale;

    private static int settingColors = 0;
    private static Point oldInfoPos = new Point(0, 0);

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiManager.lastgui = "info";
        guiOpened = true;

        close = new GuiButton(0, width / 2 - 75, height / 6 * 5, 150, 20, "Close");
        back = new GuiButton(1, width / 2 + 300, height / 6 * 5, 150, 20, "Back");
        toggle = new GuiButton(2, width / 2 - 75, height / 6, 150, 20, "Toggle info gui: " + Config.understandMe(Config.infoToggle));
        infoWidth = new GuiSlider(3, width / 2 - 75, height / 6 + 25, 150, 20, "Info width: ", "", 1, 500, Config.infoWidth.x, false, true);
        infoHeight = new GuiSlider(4, width / 2 - 75, height / 6 + 50, 150, 20, "Minimum info height: ", "", 1, 500, Config.infoWidth.y, false, true);
        border = new GuiButton(5, width / 2 - 75, height / 6 + 75, 150, 20, "Border: " + Config.understandMe(Config.infoBorder));
        borderThickness = new GuiSlider(6, width / 2 - 75, height / 6 + 100, 150, 20, "Border thickness: ", "", 0, 100, Config.infoBorderThickness * 10, false, true);
        infoColor = new GuiButton(7, width / 2 - 75, height / 6 + 125, 150, 20, "Set info color");
        borderColor = new GuiButton(8, width / 2 - 75, height / 6 + 150, 150, 20, "Set border color");
        spaceQuests = new GuiButton(9, width / 2 - 75, height / 6 + 175, 150, 20, "Quest spacing: " + Config.understandMe(Config.spaceQuests));
        textOutline = new GuiButton(10, width / 2 - 75, height / 6 + 200, 150, 20, "Text outline: " + Config.intToName(Config.infoTextOutline));
        textScale = new GuiSlider(11, width / 2 - 75, height / 6 + 225, 150, 20, "Text scale: ", "", 1, 50, Config.infoTextScale * 10, false, true);

        r = new GuiSlider(20, 0, 0, 150, 20, "Red: ", "", 0, 255, 0, false, true);
        g = new GuiSlider(21, 0, 0, 150, 20, "Green: ", "", 0, 255, 0, false, true);
        b = new GuiSlider(22, 0, 0, 150, 20, "Blue: ", "", 0, 255, 0, false, true);
        a = new GuiSlider(23, 0, 0, 150, 20, "Alpha: ", "", 0, 255, 0, false, true);
        colorCat = new ArrayList<>(Arrays.asList(r, g, b, a));
        settingColors = 0;


        this.buttonList.add(close);
        this.buttonList.add(toggle);
        this.buttonList.add(infoWidth);
        this.buttonList.add(infoHeight);
        this.buttonList.add(border);
        this.buttonList.add(borderThickness);
        this.buttonList.add(infoColor);
        this.buttonList.add(borderColor);
        this.buttonList.add(spaceQuests);
        this.buttonList.add(textOutline);
        this.buttonList.add(textScale);

        oldInfoPos = Config.infoPos;
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
            if (init) currentRGBA = Utils.colorToList(Config.infoBorderColor);
            else Config.infoBorderColor = Utils.listTocolor(currentRGBA);
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
            Config.infoBorderThickness = borderThickness.getValue() / 10D;
        } else if (textScale.dragging) {
            Config.infoTextScale = textScale.getValue() / 10D;
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

        String text = "§r§lPress enter to save info position for (clicked) cursor position";
        float textWidth = mc.fontRendererObj.getStringWidth(text);
        RenderUtils.drawText(mc, text, width / 2 - textWidth / 2, height / 6 - 15, 1D, 0);

        QuestInfo.renderInfo();
    }

    @Override
    public void handleMouseInput() throws IOException {
        if (!buttonSelected() && Mouse.isButtonDown(0)) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            Config.infoPos = new Point(mouseX, mouseY);
        }
        super.handleMouseInput();
    }

    private boolean buttonSelected() {
        return close.isMouseOver() || toggle.isMouseOver() || infoWidth.isMouseOver() || infoWidth.dragging || infoHeight.isMouseOver() || infoHeight.dragging || border.isMouseOver()
                || borderThickness.isMouseOver() || borderThickness.dragging || infoColor.isMouseOver() || borderColor.isMouseOver()
                || r.isMouseOver() || r.dragging || g.isMouseOver() || g.dragging || b.isMouseOver() || b.dragging || a.isMouseOver() || a.dragging
                || spaceQuests.isMouseOver() || textOutline.isMouseOver() || textScale.isMouseOver() || textScale.dragging;
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
        else if (button == back) new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new MainGui()))).start();
        else if (button == toggle) {
            Config.infoToggle =! Config.infoToggle;
            Config.writeBooleanConfig("info", "toggle", Config.infoToggle);
            toggle.displayString = "Toggle info gui: " + Config.understandMe(Config.infoToggle);
        } else if (button == border) {
            Config.infoBorder =! Config.infoBorder;
            Config.writeBooleanConfig("info", "border", Config.infoBorder);
            border.displayString = "Border: " + Config.understandMe(Config.infoBorder);
        } else if (button == infoColor) {
            RGBAState(1);
        } else if (button == borderColor) {
            RGBAState(2);
        } else if (button == spaceQuests) {
            Config.spaceQuests =! Config.spaceQuests;
            Config.writeBooleanConfig("info", "spaceQuests", Config.spaceQuests);
            spaceQuests.displayString = "Quest spacing: " + Config.understandMe(Config.spaceQuests);
        } else if (button == textOutline) {
            Config.infoTextOutline = (Config.infoTextOutline + 1) % 3;
            Config.writeIntConfig("info", "textOutline", Config.infoTextOutline);
            textOutline.displayString = "Text outline: " + Config.intToName(Config.infoTextOutline);
        }
    }

    @Override
    public void onGuiClosed() {
        guiOpened = false;

        Config.infoPos = oldInfoPos;
        Config.writeIntConfig("info", "posX", Config.infoPos.x);
        Config.writeIntConfig("info", "posY", Config.infoPos.y);
        Config.writeIntConfig("info", "width", Config.infoWidth.x);
        Config.writeIntConfig("info", "height", Config.infoWidth.y);
        Config.writeDoubleConfig("info", "borderThickness", Config.infoBorderThickness);
        Config.writeIntListConfig("info", "backgroundColor", Utils.colorToList(Config.infoColor));
        Config.writeIntListConfig("info", "borderColor", Utils.colorToList(Config.infoBorderColor));
        Config.writeDoubleConfig("info", "textScale", Config.infoTextScale);
    }
}

