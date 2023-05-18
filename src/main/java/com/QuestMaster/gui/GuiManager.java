package com.QuestMaster.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

import java.util.*;

public class GuiManager {
    public static String lastgui = "main";
    static int buttonspace = 600;

    public static void displaycategory(List<Gui> buttons, float x, float y) {
        displaycategory(buttons, x, y, buttonspace, false);
    }

    public static void displaycategory(List<Gui> buttons, float middlex, float topy, float buttonspace, boolean single) {
        if (buttons.size() < 1) return;
        float space = 0;
        Map<Integer, Float> buttonplacing = new LinkedHashMap<>(); //Hashmap changes the order
        for (Gui br : buttons) {
            int in = buttons.indexOf(br);
            float bw = 0;
            if (br instanceof GuiButton) bw = ((GuiButton) br).getButtonWidth();
            else if (br instanceof GuiTextField) bw = (((GuiTextField) br).getWidth());
            space += bw + 5;
            if (single || (in + 1 == buttons.size() &! (space - 5 > buttonspace))) {
                buttonplacing.put(in, space - 5);
                space = 0;
            } else if (space - 5 > buttonspace) { //if the current button makes the previous buttons too large for the row
                if (space - bw - 5 > 0) {
                    buttonplacing.put(in - 1, space - bw - 10);
                    if (in + 1 == buttons.size()) {
                        buttonplacing.put(in, bw); //because the last button would not be added
                    }
                    space = bw + 5;
                } else {
                    buttonplacing.put(in, bw); //if there is only one button in the row, that exceeds the buttonspace limit
                    space = 0;
                }
            }
        }
        int h = 0;
        int y = 0;
        for (Map.Entry<Integer, Float> e : buttonplacing.entrySet()) {
            int left = Math.round(middlex - (e.getValue() / 2));
            for (int i = h; i <= e.getKey(); i++) {
                if (buttons.get(i) instanceof GuiButton) {
                    ((GuiButton) buttons.get(i)).xPosition = left;
                    ((GuiButton) buttons.get(i)).yPosition = Math.round(topy + y * 30);
                    left += ((GuiButton) buttons.get(i)).getButtonWidth() + 5;
                }
                else if (buttons.get(i) instanceof GuiTextField) {
                    ((GuiTextField) buttons.get(i)).xPosition = left;
                    ((GuiTextField) buttons.get(i)).yPosition = Math.round(topy + y * 30);
                    left += ((GuiTextField) buttons.get(i)).getWidth() + 15;
                }
            }
            h = e.getKey()+1;
            y++;
        }
    }

    public static void openLastGui() {
        Minecraft mc = Minecraft.getMinecraft();
        switch (lastgui) {
            case "main":
                new Thread(() -> mc.addScheduledTask(() -> mc.displayGuiScreen(new MainGui()))).start();
                break;
            case "category":
                new Thread(() -> mc.addScheduledTask(() -> mc.displayGuiScreen(new CategoryGui()))).start();
                break;
            case "quest":
                new Thread(() -> mc.addScheduledTask(() -> mc.displayGuiScreen(new QuestCreatorGui()))).start();
                break;
            case "element":
                new Thread(() -> mc.addScheduledTask(() -> mc.displayGuiScreen(new QuestElementCreatorGui()))).start();
                break;
            case "config":
                new Thread(() -> mc.addScheduledTask(() -> mc.displayGuiScreen(new GeneralConfigGui()))).start();
                break;
            case "info":
                new Thread(() -> mc.addScheduledTask(() -> mc.displayGuiScreen(new InfoEditorGui()))).start();
                break;
        }
    }
}

