package com.QuestMaster.gui;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StringUtils;

import java.io.IOException;
import java.util.*;

public class MainGui extends GuiScreen {
    private GuiButton close;
    private GuiButton config;
    private GuiButton infoEditor;
    private static List<Gui> elements = new ArrayList<>();
    private GuiButton newCat;
    private static List<Gui> categories = new ArrayList<>();

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiManager.lastgui = "main";

        close = new GuiButton(0, width / 2 - 75, height / 6 * 5, 150, 20, "Close");
        this.buttonList.add(close);

        config = new GuiButton(1, 0, 0, 150, 20, "Config");
        infoEditor = new GuiButton(2, 0, 0, 150, 20, "Info display config");
        elements = new ArrayList<>(Arrays.asList(config, infoEditor));
        GuiManager.displaycategory(elements, width / 2f, height / 6f);
        setAllState(elements, true);

        categories.clear();
        for (String category : QuestMaster.quests.keySet()) {
            GuiButton cat = new GuiButton(0, 0, 0, 150, 20, category);
            categories.add(cat);
        }
        newCat = new GuiButton(0, 0, 0, 150, 20, "§aNew category");
        categories.add(newCat);
        GuiManager.displaycategory(categories, width / 2f, height / 6f + 50);
        setAllState(categories, true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        Minecraft mc = Minecraft.getMinecraft();

        String text1 = "§l§bQuestMaster";
        int text1Width = mc.fontRendererObj.getStringWidth(StringUtils.stripControlCodes(text1));
        RenderUtils.drawText(mc, text1, (width - text1Width) / 2f, height / 6f - 25, 1D, 0);

        String text2 = "Categories: ";
        int text2Width = mc.fontRendererObj.getStringWidth(text2);
        RenderUtils.drawText(mc, text2, (width - text2Width) / 2f, height / 6f + 35, 1, 0);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    private void setAllState(List<Gui> elements, boolean enable) {
        for (Gui element : elements) {
            changeState(element, enable);
        }
    }

    private void changeState(Gui element, boolean enable) {
        if (element instanceof GuiButton) {
            if (enable) {
                if (!this.buttonList.contains(element)) this.buttonList.add((GuiButton) element);
            } else {
                this.buttonList.remove(element);
            }
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button == close) mc.thePlayer.closeScreen();
        else if (button == config) new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new GeneralConfigGui()))).start();
        else if (button == infoEditor) new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new InfoEditorGui()))).start();
        else if (categories.contains(button)) {
            String name = button.displayString;
            if (button == newCat) new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new CategoryGui()))).start();
            else if (QuestMaster.quests.containsKey(name)) {
                CategoryGui.loadCategory(name);
                new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new CategoryGui()))).start();
            }
        }
    }

    @Override
    public void onGuiClosed() {
    }
}
