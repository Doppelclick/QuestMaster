package com.QuestMaster.gui;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.config.Config;
import com.QuestMaster.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GeneralConfigGui extends GuiScreen {
    private GuiButton close;
    private GuiButton back;

    private GuiButton toggle;
    private GuiButton autoEnableQuests;
    private GuiButton disableLast;

    private static HashMap<Gui, String> hovers = new HashMap<>();

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiManager.lastgui = "config";

        close = new GuiButton(0, width / 2 - 75, height / 6 * 5, 150, 20, "Close");
        back = new GuiButton(1, width / 2 + 300, height / 6 * 5, 150, 20, "Back");

        this.buttonList.add(close);
        this.buttonList.add(back);

        toggle = new GuiButton(2, width / 2 - 75, height / 6 * 5, 100, 20, "Mod toggle " + Config.understandMe(Config.modToggle));
        autoEnableQuests = new GuiButton(3, width / 2 - 75, height / 6 * 5, 100, 20, "Auto enable " +  Config.understandMe(Config.autoEnableQuests));
        disableLast = new GuiButton(4, width / 2 + 300, height / 6 * 5, 100, 20, "Disable last " +  Config.understandMe(Config.disableLast));

        List<Gui> list = new ArrayList<>(Arrays.asList(toggle, autoEnableQuests, disableLast));
        GuiManager.displaycategory(list, width / 2f, height / 6f);
        enableAll(list);

        hovers.put(autoEnableQuests, "Will enable a quest when the first element is triggered");
        hovers.put(disableLast, "Will disable the previous element when another is triggered");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        Minecraft mc = Minecraft.getMinecraft();
        super.drawScreen(mouseX, mouseY, partialTicks);

        String text1 = "§l§bQuestMaster Config";
        int text1Width = mc.fontRendererObj.getStringWidth(StringUtils.stripControlCodes(text1));
        RenderUtils.drawText(mc, text1, (width - text1Width) / 2f, height / 6f - 25, 1D, 0);

        for (Gui b : hovers.keySet()) {
            if (b instanceof GuiButton) {
                GuiButton button = (GuiButton) b;
                if (button.isMouseOver()) {
                    List<String> hovertext = Arrays.asList(hovers.get(button).split(" # "));
                    drawHoveringText(hovertext, mouseX - 5, mouseY);
                }
            } else if (b instanceof GuiTextField) {
                GuiTextField button = (GuiTextField) b;
                if (button.isFocused()) {
                    if ((mouseX >= button.xPosition && mouseX <= button.xPosition + button.width) && (mouseY >= button.yPosition && mouseY <= button.yPosition + button.height)) {
                        List<String> hovertext = Arrays.asList(hovers.get(button).split(" # "));
                        drawHoveringText(hovertext, mouseX - 5, mouseY);
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button == close) mc.thePlayer.closeScreen();
        else if (button == back) new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new MainGui()))).start();
        else if (button == toggle) {
            Config.modToggle =! Config.modToggle;
            Config.writeBooleanConfig("general", "modToggle", Config.modToggle);
            toggle.displayString = "Toggle " + Config.understandMe(Config.modToggle);
        } else if (button == autoEnableQuests) {
            Config.autoEnableQuests =! Config.autoEnableQuests;
            Config.writeBooleanConfig("general", "autoEnableQuests", Config.autoEnableQuests);
            autoEnableQuests.displayString = "Auto enable " +  Config.understandMe(Config.autoEnableQuests);
        } else if (button == disableLast) {
            Config.disableLast =! Config.disableLast;
            Config.writeBooleanConfig("general", "disableLast", Config.disableLast);
            disableLast.displayString = "Disable last " +  Config.understandMe(Config.disableLast);
        }
    }


    private void enableAll(List<Gui> elements) {
        for (Gui element : elements) {
            if (element instanceof GuiButton) if (!this.buttonList.contains((GuiButton) element)) this.buttonList.add((GuiButton) element);
        }
    }
}
