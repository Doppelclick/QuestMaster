package com.QuestMaster.gui;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Quest;
import com.QuestMaster.classes.QuestElement;
import com.QuestMaster.classes.Trigger;
import com.QuestMaster.classes.triggers.ChatMessage;
import com.QuestMaster.classes.triggers.ClickPos;
import com.QuestMaster.classes.triggers.PlayerCollect;
import com.QuestMaster.classes.triggers.PlayerPosition;
import com.QuestMaster.config.Config;
import com.QuestMaster.utils.FileUtils;
import com.QuestMaster.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.vecmath.Vector3f;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class QuestElementCreatorGui extends GuiScreen {
    private GuiButton close;
    private GuiButton back;
    private GuiButton discard;
    private GuiButton save;
    private GuiButton delete;
    public static boolean deleting = false;


    private static int elementIndex = -1;
    private static String category = "";
    private static int quest = -1;
    private static String oldName = "";
    public static QuestElement editing = new QuestElement("", null, new Vector3f(69420, 69420, 69420));
    

    private static GuiTextField elementName;
    private static GuiButton trigger;
    private static GuiButton triggerType;
    private static List<Gui> triggerTypes = new ArrayList<>();
    private static List<Gui> triggerSettings = new ArrayList<>();
    private static GuiButton manual;
    private static GuiButton chatMessage;
    private static GuiButton colorCodes;
    private static GuiTextField amountNeeded;
    private static GuiButton newPattern;
    private static List<Gui> chatMessages;
    private static GuiButton clickPos;
    private static GuiTextField heldItemName;
    private static HashMap<String, Integer> stringToMB = new HashMap<String, Integer>()  {{
       put("any", -1);
       put("left", 0);
       put("right", 1);
    }};
    private static GuiTextField requiredMouseButton;
    private static GuiTextField positionx;
    private static GuiTextField positiony;
    private static GuiTextField positionz;
    private static GuiTextField amountOfClicks;
    private static List<Gui> clickPosCat;
    private static GuiButton playerCollect;
    private static GuiTextField collectedItemName;
    private static GuiTextField collectedAmount;
    private static List<Gui> playerCollectCat;
    private static GuiButton playerPosition;
    private static GuiTextField playerx;
    private static GuiTextField playery;
    private static GuiTextField playerz;
    private static GuiTextField duration;
    private static GuiTextField distance;
    private static List<Gui> playerPosCat = new ArrayList<>();
    private static GuiButton waypoint;
    private static boolean showWaypointCat = false;
    private static GuiTextField x;
    private static GuiTextField y;
    private static GuiTextField z;
    private static List<Gui> waypointCat = new ArrayList<>();
    private static GuiButton enabled;
    private static List<Gui> elementSettings = new ArrayList<>();

    @Override
    public void initGui() {
        super.initGui();
        if (quest == -1 || category.isEmpty()) {
            new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new QuestCreatorGui()))).start();
            return;
        }
        GuiManager.lastgui = "element";

        close = new GuiButton(0, width / 2 - 75, height / 6 * 5, 150, 20, "Close");
        back = new GuiButton(1, width / 2 + 300, height / 6 * 5, 150, 20, "Back");
        discard = new GuiButton(2, width / 2 - 270, height / 6 - 25, 150, 20, "§cDiscard changes");
        save = new GuiButton(3, width / 2 + 120, height / 6 - 25, 150, 20, "§aSave changes");
        delete = new GuiButton(4, width / 2 - 75, height / 6 * 4, 150, 20, "§cDelete element");

        this.buttonList.add(close);
        this.buttonList.add(back);
        this.buttonList.add(discard);
        this.buttonList.add(save);
        this.buttonList.add(delete);

        trigger = new GuiButton(1, 0, 0, 150, 20, "Edit trigger");
        elementName = new GuiTextField(7, this.fontRendererObj, 0, 0, 250, 20);
        elementName.setText(editing.name.isEmpty() ? "Set displayed objective" : editing.name);
        enabled = new GuiButton(1, 0, 0, 150, 20, "Element toggle: " + Config.understandMe(editing.enabled));
        waypoint = new GuiButton(1, 0, 0, 150, 20, "Edit waypoint position");
        elementSettings = new ArrayList<>(Arrays.asList(elementName, trigger, waypoint, enabled));
        GuiManager.displaycategory(elementSettings, width / 2f, height / 6f);
        setAllState(elementSettings, true);

        x = new GuiTextField(20, this.fontRendererObj, 0, 0, 100, 20);
        y = new GuiTextField(21, this.fontRendererObj, 0, 0, 100, 20);
        z = new GuiTextField(22, this.fontRendererObj, 0, 0, 100, 20);
        Vector3f wp = editing.waypoint;
        if (wp == null) {
            wp = new Vector3f(69420, 69420, 69420);
        }
        x.setText(String.valueOf(wp.x));
        y.setText(String.valueOf(wp.y));
        z.setText(String.valueOf(wp.z));
        waypointCat = new ArrayList<>(Arrays.asList(x, y, z));
        GuiManager.displaycategory(waypointCat, width / 2f, height / 6f + 60);

        triggerType = new GuiButton(30, 0, 0, 150, 20, "Trigger type");
        manual = new GuiButton(31, 0, 0, 150, 20, "Manually enable");
        chatMessage = new GuiButton(32, 0, 0, 150, 20, "Chat message");
        clickPos = new GuiButton(33, 0, 0, 150, 20, "Click position");
        playerCollect = new GuiButton(34, 0, 0, 150, 20, "Item collection");
        playerPosition = new GuiButton(35, 0, 0, 150, 20, "Player position");
        triggerTypes = new ArrayList<>(Arrays.asList(manual, chatMessage, clickPos, playerCollect, playerPosition));

        colorCodes = new GuiButton(0, 0, 0, 150, 20,  "Match color codes: " + Config.understandMe(true));
        amountNeeded = new GuiTextField(7, this.fontRendererObj, 0, 0, 200, 20);
        newPattern = new GuiButton(0, 0, 0, 150, 20,  "New Pattern");
        chatMessages = new ArrayList<>(Arrays.asList(colorCodes, amountNeeded, newPattern));
        GuiManager.displaycategory(chatMessages, width / 2f, height / 6f + 90);

        heldItemName = new GuiTextField(0, this.fontRendererObj, 0, 0, 250, 20);
        requiredMouseButton = new GuiTextField(0, this.fontRendererObj, 0, 0, 100, 20);
        positionx = new GuiTextField(0, this.fontRendererObj, 0, 0, 100, 20);
        positiony = new GuiTextField(0, this.fontRendererObj, 0, 0, 100, 20);
        positionz = new GuiTextField(0, this.fontRendererObj, 0, 0, 100, 20);
        amountOfClicks = new GuiTextField(0, this.fontRendererObj, 0, 0, 180, 20);
        clickPosCat = new ArrayList<>(Arrays.asList(heldItemName, requiredMouseButton, positionx, positiony, positionz, amountOfClicks));
        GuiManager.displaycategory(clickPosCat, width / 2f, height / 6f + 90);

        collectedItemName = new GuiTextField(0, this.fontRendererObj, 0, 0, 250, 20);
        collectedAmount = new GuiTextField(0, this.fontRendererObj, 0, 0, 100, 20);
        playerCollectCat = new ArrayList<>(Arrays.asList(collectedItemName, collectedAmount));
        GuiManager.displaycategory(playerCollectCat, width / 2f, height / 6f + 90);

        playerx = new GuiTextField(0, this.fontRendererObj, 0, 0, 100, 20);
        playery = new GuiTextField(0, this.fontRendererObj, 0, 0, 100, 20);
        playerz = new GuiTextField(0, this.fontRendererObj, 0, 0, 100, 20);
        duration = new GuiTextField(0, this.fontRendererObj, 0, 0, 140, 20);
        distance = new GuiTextField(0, this.fontRendererObj, 0, 0, 120, 20);
        playerPosCat = new ArrayList<>(Arrays.asList(playerx, playery, playerz, duration, distance));
        GuiManager.displaycategory(playerPosCat, width / 2f, height / 6f + 90);

        loadTrigger();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        Minecraft mc = Minecraft.getMinecraft();
        super.drawScreen(mouseX, mouseY, partialTicks);

        elementName.drawTextBox();
        if (showWaypointCat) {
            for (Gui field : waypointCat) {
                ((GuiTextField)field).drawTextBox();
            }
        }

        if (!this.buttonList.contains(manual)) {
            if (this.buttonList.contains(chatMessage)) {
                amountNeeded.drawTextBox();
                for (Gui gui : chatMessages) {
                    if (gui instanceof GuiTextField) {
                        ((GuiTextField) gui).drawTextBox();
                    }
                }
            } else if (this.buttonList.contains(clickPos)) {
                heldItemName.drawTextBox();
                requiredMouseButton.drawTextBox();
                positionx.drawTextBox();
                positiony.drawTextBox();
                positionz.drawTextBox();
                amountOfClicks.drawTextBox();
            } else if (this.buttonList.contains(playerCollect)) {
                collectedItemName.drawTextBox();
                 collectedAmount.drawTextBox();
            } else if (this.buttonList.contains(playerPosition)) {
                playerx.drawTextBox();
                playery.drawTextBox();
                playerz.drawTextBox();
                duration.drawTextBox();
                distance.drawTextBox();
            }
        }
    }

    @Override
    protected void keyTyped(char c, int kc) throws IOException {
        super.keyTyped(c, kc);

        boolean intsOnly = ((int) c > 47 && (int) c < 58) || (int) c == 8 || (int) c == 127;
        if (elementName.isFocused()) {
            elementName.textboxKeyTyped(c, kc);
        } else if (intsOnly) {
            for (Gui field : waypointCat) {
                if (((GuiTextField) field).isFocused()) {
                    ((GuiTextField) field).textboxKeyTyped(c, kc);
                    return;
                }
            }
        }

        if (!this.buttonList.contains(manual)) {
            if (this.buttonList.contains(chatMessage)) {
                if (intsOnly) {
                    if (amountNeeded.isFocused()) {
                        amountNeeded.textboxKeyTyped(c, kc);
                    }
                }
                for (int i = 2; i < chatMessages.size() - 1; i++) {
                    if (chatMessages.get(i) instanceof GuiTextField) {
                        if (((GuiTextField) chatMessages.get(i)).isFocused()){
                            ((GuiTextField) chatMessages.get(i)).textboxKeyTyped(c, kc);
                        }
                    }
                }
            } else if (this.buttonList.contains(clickPos)) {
                if (heldItemName.isFocused()) heldItemName.textboxKeyTyped(c, kc);
                if (requiredMouseButton.isFocused()) requiredMouseButton.textboxKeyTyped(c, kc);
                if (intsOnly) {
                    if (positionx.isFocused()) positionx.textboxKeyTyped(c, kc);
                    if (positiony.isFocused()) positiony.textboxKeyTyped(c, kc);
                    if (positionz.isFocused()) positionz.textboxKeyTyped(c, kc);
                    if (amountOfClicks.isFocused()) amountOfClicks.textboxKeyTyped(c, kc);
                }
            } else if (this.buttonList.contains(playerCollect)) {
                if (collectedItemName.isFocused()) collectedItemName.textboxKeyTyped(c, kc);
                if (intsOnly && collectedAmount.isFocused()) collectedAmount.textboxKeyTyped(c, kc);
            } else if (this.buttonList.contains(playerPosition)) {
                if (intsOnly) {
                    if (playerx.isFocused()) playerx.textboxKeyTyped(c, kc);
                    if (playery.isFocused()) playery.textboxKeyTyped(c, kc);
                    if (playerz.isFocused()) playerz.textboxKeyTyped(c, kc);
                    if (duration.isFocused()) duration.textboxKeyTyped(c, kc);
                    if (distance.isFocused()) distance.textboxKeyTyped(c, kc);
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean enWas = elementName.isFocused();
        elementName.mouseClicked(mouseX, mouseY, mouseButton);
        if (elementName.isFocused()) {
            if (!enWas && elementName.getText().equals("Set displayed objective")) elementName.setText("");
        } else if (enWas) {
            if (elementName.getText().isEmpty()) elementName.setText("Set displayed objective");
        }
        for (Gui field : waypointCat) {
            ((GuiTextField)field).mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (!this.buttonList.contains(manual)) {
            if (this.buttonList.contains(chatMessage)) {
                boolean was = amountNeeded.isFocused();
                amountNeeded.mouseClicked(mouseX, mouseY, mouseButton);
                if (amountNeeded.isFocused()) {
                    if (!was) amountNeeded.setText(amountNeeded.getText().replace("Amount of messages needed: ", ""));
                } else {
                    if (was) {
                        amountNeeded.setText("Amount of messages needed: " + amountNeeded.getText());
                    }
                }
                List<Integer> remove = new ArrayList<>();
                for (int i = 2; i < chatMessages.size() - 1; i++) {
                    if (chatMessages.get(i) instanceof GuiTextField) {
                        boolean wasf = ((GuiTextField)chatMessages.get(i)).isFocused();
                        ((GuiTextField)chatMessages.get(i)).mouseClicked(mouseX, mouseY, mouseButton);
                        if (!((GuiTextField)chatMessages.get(i)).isFocused() && wasf) {
                            if (((GuiTextField)chatMessages.get(i)).getText().isEmpty()) {
                                remove.add(i);
                            }
                        }
                    }
                }
                remove.forEach(integer ->  chatMessages.remove((int) integer));
                if (!remove.isEmpty()) GuiManager.displaycategory(chatMessages, width / 2f, height / 6f + 90);
            } else if (this.buttonList.contains(clickPos)) {
                boolean hWas = heldItemName.isFocused();
                heldItemName.mouseClicked(mouseX, mouseY, mouseButton);
                if (heldItemName.isFocused()) {
                    if (!hWas) {
                        heldItemName.setText(heldItemName.getText().replace("Held item name: ", ""));
                    }
                } else {
                    if (hWas) {
                        heldItemName.setText("Held item name: " + heldItemName.getText());
                    }
                }

                boolean mbWas = requiredMouseButton.isFocused();
                requiredMouseButton.mouseClicked(mouseX, mouseY, mouseButton);
                if (requiredMouseButton.isFocused()) {
                    if (!mbWas) requiredMouseButton.setText(requiredMouseButton.getText().replace("MB: ", ""));
                } else {
                    if (mbWas) requiredMouseButton.setText("MB: " + requiredMouseButton.getText());
                }

                positionx.mouseClicked(mouseX, mouseY, mouseButton);
                positiony.mouseClicked(mouseX, mouseY, mouseButton);
                positionz.mouseClicked(mouseX, mouseY, mouseButton);

                boolean amountWas = amountOfClicks.isFocused();
                amountOfClicks.mouseClicked(mouseX, mouseY, mouseButton);
                if (amountOfClicks.isFocused()) {
                    if (!amountWas) {
                        amountOfClicks.setText(amountOfClicks.getText().replace("Required amount of clicks: ", ""));
                    }
                } else {
                    if (amountWas) {
                        amountOfClicks.setText("Required amount of clicks: " + amountOfClicks.getText());
                    }
                }
            } else if (this.buttonList.contains(playerCollect)) {
                boolean itemWas = collectedItemName.isFocused();
                collectedItemName.mouseClicked(mouseX, mouseY, mouseButton);
                if (collectedItemName.isFocused()) {
                    if (!itemWas) {
                        collectedItemName.setText(collectedItemName.getText().replace("Item: ", ""));
                    }
                } else {
                    if (itemWas) {
                        collectedItemName.setText("Item: " + collectedItemName.getText());
                    }
                }

                boolean amountWas = collectedAmount.isFocused();
                collectedAmount.mouseClicked(mouseX, mouseY, mouseButton);
                if (collectedAmount.isFocused()) {
                    if (!amountWas) {
                        collectedAmount.setText(collectedAmount.getText().replace("Amount: ", ""));
                    }
                } else {
                    if (amountWas) {
                        collectedAmount.setText("Amount: " + collectedAmount.getText());
                    }
                }
            } else if (this.buttonList.contains(playerPosition)) {
                playerx.mouseClicked(mouseX, mouseY, mouseButton);
                playery.mouseClicked(mouseX, mouseY, mouseButton);
                playerz.mouseClicked(mouseX, mouseY, mouseButton);

                boolean durationWas = duration.isFocused();
                duration.mouseClicked(mouseX, mouseY, mouseButton);
                if (duration.isFocused()) {
                    if (!durationWas) {
                        duration.setText(duration.getText().replace("Dwell millis: ", ""));
                    }
                } else {
                    if (durationWas) {
                        duration.setText("Dwell millis: " + duration.getText());
                    }
                }

                boolean distanceWas = distance.isFocused();
                distance.mouseClicked(mouseX, mouseY, mouseButton);
                if (distance.isFocused()) {
                    if (!distanceWas) {
                        distance.setText(distance.getText().replace("Distance: ", ""));
                    }
                } else {
                    if (distanceWas) {
                        distance.setText("Distance: " + distance.getText());
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button == close) mc.thePlayer.closeScreen();
        else if (button == back) new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new QuestCreatorGui()))).start();
        else if (button == discard) {
            clear();
            new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new QuestCreatorGui()))).start();
        } else if (button == save) {
            saveToElement();
            if (QuestCreatorGui.saveElement(elementName.getText(), elementIndex)) {
                new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new QuestCreatorGui()))).start();
                clear();
            }
        }
        else if (button == delete) {
            if (!elementName.getText().equals(oldName) || elementName.getText().isEmpty()) {
                Utils.sendModMessage("§cYou already changed this element's name. Please save changes first");
                return;
            }
            deleting = true;

            ChatComponentText delete = new ChatComponentText("§l§c[DELETE]§r");
            delete.setChatStyle(delete.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/questmaster configcommand deletequestelement")).
                    setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("DELETE the element " + elementName.getText()))));
            ChatComponentText cancel = new ChatComponentText("§l§a  [CANCEL]§r  ");
            cancel.setChatStyle(cancel.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/questmaster configcommand canceldeletequestelement")).
                    setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Cancel deletion"))));
            Utils.sendModMessage(new ChatComponentText("§cAre you sure you want to delete the quest element " + elementName.getText() + "?  ").
                    appendSibling(delete).appendSibling(new ChatComponentText("  ").appendSibling(cancel)));

            FMLClientHandler.instance().getClient().displayGuiScreen(new GuiChat());
        } else if (button == enabled) {
            editing.enabled =! editing.enabled;
            enabled.displayString = "Element toggle: " + Config.understandMe(editing.enabled);
        } else if (button == waypoint) {
            hideAllCategories();
            showWaypointCat =! showWaypointCat;
        } else if (button == trigger) {
            showWaypointCat = false;
            boolean enable = !this.buttonList.contains(triggerType);
            hideAllCategories();
            if (enable) {
                saveToElement();
                loadTrigger();
            }
        } else if (button == triggerType) {
            if (this.buttonList.contains(manual) && this.buttonList.contains(clickPos)) {
                setAllState(triggerTypes, false);
                if (editing.progressTrigger != null) {
                    saveToElement();
                    loadTrigger();
                }
            } else if (this.buttonList.contains(chatMessage) || this.buttonList.contains(clickPos) || this.buttonList.contains(playerCollect) || this.buttonList.contains(playerPosition) || this.buttonList.contains(manual)) {
                hideAllCategories();
                triggerSettings.clear();
                triggerSettings.add(triggerType);
                GuiManager.displaycategory(triggerSettings, width / 2f, height / 6f + 60);
                setAllState(triggerSettings, true);

                GuiManager.displaycategory(triggerTypes, width / 2f, height / 6f + 90);
                setAllState(triggerTypes, true);
            } else {
                GuiManager.displaycategory(triggerTypes, width / 2f, height / 6f + 90);
                setAllState(triggerTypes, true);
            }
        } else if (button == colorCodes) {
            ((ChatMessage) editing.progressTrigger).colorCodes =! ((ChatMessage) editing.progressTrigger).colorCodes;
            colorCodes.displayString = "Match color codes: " + Config.understandMe(((ChatMessage) editing.progressTrigger).colorCodes);
        } else if (button == newPattern) {
            GuiTextField field = new GuiTextField(0, this.fontRendererObj, 0, 0, 250, 20);
            chatMessages.add(chatMessages.size() - 1, field);
            GuiManager.displaycategory(chatMessages, width / 2f, height / 6f + 90);
        }
        else if (this.buttonList.contains(chatMessage) && this.buttonList.contains(clickPos)) {
            boolean clicked = true;
            if (button == manual) {
                editing.progressTrigger = new Trigger();
            } else if (button == chatMessage) {
                if (!(editing.progressTrigger instanceof ChatMessage)) editing.progressTrigger = new ChatMessage(new ArrayList<>(), 1, true);
            } else if (button == clickPos) {
                if (!(editing.progressTrigger instanceof ClickPos)) editing.progressTrigger = new ClickPos(-1, new Vector3f(69420, 69420, 69420), 1, "any");
            } else if (button == playerCollect) {
                if (!(editing.progressTrigger instanceof PlayerCollect)) editing.progressTrigger = new PlayerCollect("undefined", 1);
            } else if (button == playerPosition) {
                if (!(editing.progressTrigger instanceof PlayerPosition)) editing.progressTrigger = new PlayerPosition(new Vector3f(69420, 69420, 69420), 1, 1000);
            } else clicked = false;
            if (clicked) {
                loadTrigger();
            }
        }
    }

    private void hideAllCategories() {
        changeState(triggerType, false);
        setAllState(triggerTypes, false);
        setAllState(waypointCat, false);
        setAllState(triggerSettings, false);
        setAllState(chatMessages, false);
        setAllState(clickPosCat, false);
        setAllState(playerCollectCat, false);
        setAllState(playerPosCat, false);
    }

    private void loadTrigger() {
        setAllState(triggerTypes, false);

        triggerSettings.clear();
        triggerSettings.add(triggerType);
        if (editing.progressTrigger != null) {
            if (editing.progressTrigger instanceof ChatMessage) {
                triggerSettings.add(chatMessage);

                amountNeeded.setText("Amount of messages needed: " + ((ChatMessage) editing.progressTrigger).amountNeeded);
                colorCodes.displayString = "Match color codes: " + Config.understandMe(((ChatMessage) editing.progressTrigger).colorCodes);
                chatMessages = new ArrayList<>(Arrays.asList(colorCodes, amountNeeded, newPattern));
                for (String pattern : ((ChatMessage) editing.progressTrigger).patterns.keySet()) {
                    GuiTextField field = new GuiTextField(0, this.fontRendererObj, 0, 0, 250, 20);
                    field.setText(pattern);
                    chatMessages.add(chatMessages.size() - 1, field);
                }
                GuiManager.displaycategory(chatMessages, width / 2f, height / 6f + 90);
                setAllState(chatMessages, true);
            } else if (editing.progressTrigger instanceof ClickPos) {
                triggerSettings.add(clickPos);

                requiredMouseButton.setText("MB: " + stringToMB.keySet().toArray()[((ClickPos) editing.progressTrigger).mouseButton + 1]);
                positionx.setText(String.valueOf(((ClickPos) editing.progressTrigger).position.x));
                positiony.setText(String.valueOf(((ClickPos) editing.progressTrigger).position.y));
                positionz.setText(String.valueOf(((ClickPos) editing.progressTrigger).position.z));
                amountOfClicks.setText("Required amount of clicks: " + ((ClickPos) editing.progressTrigger).amount);
                heldItemName.setText("Held item name: " + ((ClickPos) editing.progressTrigger).heldItemName);

                setAllState(clickPosCat, true);
            } else if (editing.progressTrigger instanceof PlayerCollect) {
                triggerSettings.add(playerCollect);

                collectedItemName.setText("Item: " + ((PlayerCollect) editing.progressTrigger).itemName);
                collectedAmount.setText("Amount: " + ((PlayerCollect) editing.progressTrigger).amount);

                setAllState(playerCollectCat, true);
            } else if (editing.progressTrigger instanceof PlayerPosition) {
                triggerSettings.add(playerPosition);

                playerx.setText(String.valueOf(((PlayerPosition) editing.progressTrigger).position.x));
                playery.setText(String.valueOf(((PlayerPosition) editing.progressTrigger).position.y));
                playerz.setText(String.valueOf(((PlayerPosition) editing.progressTrigger).position.z));
                duration.setText("Dwell millis: " + ((PlayerPosition) editing.progressTrigger).durationMillis);
                distance.setText("Distance: " + ((PlayerPosition) editing.progressTrigger).distance);

                setAllState(playerPosCat, true);
            } else {
                triggerSettings.add(manual);
            }
        }
        GuiManager.displaycategory(triggerSettings, width / 2f, height / 6f + 60);
        setAllState(triggerSettings, true);
    }

    public static void loadElement(QuestElement element, int index) {
        elementIndex = index;
        editing = element;
        oldName = element.name;
    }

    public static void load(String cat, int index) {
        category = cat;
        quest = index;
        deleting = false;
    }

    public static void clear() {
        category = "";
        quest = -1;
        editing = new QuestElement("", null, new Vector3f(69420, 69420, 69420));;
        oldName = "";
        deleting = false;
        elementIndex = -1;

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
    
    public static void deleteElement() {
        if (!deleting) return;
        if (elementIndex != -1) {
            if (quest != -1 &! category.isEmpty()) {
                QuestMaster.quests.get(category).get(quest).remove(elementIndex);
                Utils.sendModMessage("§cDeleted element " + elementName.getText());
                Quest q = QuestMaster.quests.get(category).get(quest);
                FileUtils.save(q, FileUtils.questDir + category + "/", q.name + ".bin");
            } else Utils.sendModMessage("§cError deleting " + category + " - " + elementName.getText());
        }
        clear();
        deleting = false;
    }

    public static void saveToElement() {
        try {
            float x = Float.parseFloat(((GuiTextField) waypointCat.get(0)).getText());
            float y = Float.parseFloat(((GuiTextField) waypointCat.get(1)).getText());
            float z = Float.parseFloat(((GuiTextField) waypointCat.get(2)).getText());
            editing.waypoint = new Vector3f(x, y, z);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (editing.progressTrigger != null) {
            if (editing.progressTrigger instanceof ChatMessage) {
                ((ChatMessage) editing.progressTrigger).patterns.clear();
                for (int i = 2; i < chatMessages.size() - 1; i++) {
                    if (chatMessages.get(i) instanceof GuiTextField) {
                        ((ChatMessage) editing.progressTrigger).patterns.put(((GuiTextField) chatMessages.get(i)).getText(), false);
                    }
                }
                try {
                    String string = amountNeeded.getText().replace("Amount of messages needed: ", "");
                    ((ChatMessage) editing.progressTrigger).amountNeeded = string.isEmpty() ? 1 : Integer.parseInt(string);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (editing.progressTrigger instanceof ClickPos) {
                ((ClickPos) editing.progressTrigger).mouseButton = stringToMB.getOrDefault(requiredMouseButton.getText().replace("MB: ", ""), -1);
                try {
                    int x = Integer.parseInt(positionx.getText());
                    int y = Integer.parseInt(positiony.getText());
                    int z = Integer.parseInt(positionz.getText());
                    ((ClickPos) editing.progressTrigger).position = new Vector3f(x, y, z);
                    String string = amountOfClicks.getText().replace("Required amount of clicks: ", "");
                    ((ClickPos) editing.progressTrigger).amount = string.isEmpty() ? 1 : Integer.parseInt(string);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String itemName = heldItemName.getText().replace("Held item name: ", "");
                ((ClickPos) editing.progressTrigger).heldItemName = itemName.isEmpty() ? "any" : itemName;
            } else if (editing.progressTrigger instanceof PlayerCollect) {
                String itemName = collectedItemName.getText().replace("Item: ", "");
                ((PlayerCollect) editing.progressTrigger).itemName = itemName.isEmpty() ? "undefined" : itemName;
                try {
                    String amount = collectedAmount.getText().replace("Amount: ", "");
                    ((PlayerCollect) editing.progressTrigger).amount = amount.isEmpty() ? 1 : Integer.parseInt(amount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (editing.progressTrigger instanceof PlayerPosition) {
                try {
                    int x = Integer.parseInt(playerx.getText());
                    int y = Integer.parseInt(playery.getText());
                    int z = Integer.parseInt(playerz.getText());
                    ((PlayerPosition) editing.progressTrigger).position = new Vector3f(x, y, z);
                    String dur = duration.getText().replace("Dwell millis: ", "");
                    ((PlayerPosition) editing.progressTrigger).durationMillis = dur.isEmpty() ? 1000 : Integer.parseInt(dur);
                    String dist = distance.getText().replace("Distance ", "");
                    ((PlayerPosition) editing.progressTrigger).distance = dist.isEmpty() ? 1 : Integer.parseInt(dist);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        editing.name = elementName.getText().equals("Set displayed objective") ? "" : elementName.getText();
        showWaypointCat = false;

        saveToElement();
        hideAllCategories();
    }
}
