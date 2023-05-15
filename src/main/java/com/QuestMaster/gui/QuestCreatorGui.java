package com.QuestMaster.gui;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Quest;
import com.QuestMaster.classes.QuestElement;
import com.QuestMaster.config.Config;
import com.QuestMaster.utils.FileUtils;
import com.QuestMaster.utils.RenderUtils;
import com.QuestMaster.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.vecmath.Vector3f;
import java.io.IOException;
import java.util.*;

public class QuestCreatorGui extends GuiScreen {
    private GuiButton close;
    private GuiButton back;
    private GuiButton saveQuest;
    private GuiButton discardQuest;
    private GuiButton deleteQuest;
    public static boolean deleting = false;


    private GuiButton enableQuest;
    private static GuiTextField questName;
    private static GuiTextField categoryOwner;

    public static boolean exists = false;
    public static boolean enabled = false;
    public static String oldName = "";
    public static String name = "";
    public static String oldCategory = "";
    public static String category = "";
    public static List<QuestElement> elements = new ArrayList<>();
    public static List<Gui> questElements = new ArrayList<>();
    private GuiButton newElement;


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiManager.lastgui = "quest";

        close = new GuiButton(0, width / 2 - 75, height / 6 * 5, 150, 20, "Close");
        back = new GuiButton(1, width / 2 + 300, height / 6 * 5, 150, 20, "Back");
        saveQuest = new GuiButton(3, width / 2 + 120, height / 6 - 25, 150, 20, "§aSave Quest");
        discardQuest = new GuiButton(4, width / 2 - 270, height / 6 - 25, 150, 20, "§cDiscard edited Quest");
        deleteQuest = new GuiButton(5, width / 2 - 75, height / 6 * 4, 150, 20, "§cDelete Quest");

        enableQuest = new GuiButton(5, 0, 0, 150, 20, "Enable quest: " + Config.understandMe(enabled));
        questName = new GuiTextField(6, this.fontRendererObj, 0, 0, 200, 20);
        questName.setText(name.isEmpty() ? "Set name" : name);
        categoryOwner = new GuiTextField(7, this.fontRendererObj, 0, 0, 200, 20);
        categoryOwner.setText(category.isEmpty() ? "Set category" : category);
        List<Gui> list = Arrays.asList(enableQuest, questName, categoryOwner);
        GuiManager.displaycategory(list, width / 2f, height / 6f);
        setAllState(list, true);

        questElements.clear();
        for (QuestElement element : elements) {
            questElements.add(new GuiButton(90, 0, 0, 150, 20, element.name));
        }
        newElement = new GuiButton(91, 0, 0, 150, 20, "Create new element");
        questElements.add(newElement);
        GuiManager.displaycategory(questElements, width / 2f, height / 6f + 50);
        setAllState(questElements, true);


        this.buttonList.add(close);
        this.buttonList.add(back);
        this.buttonList.add(saveQuest);
        this.buttonList.add(discardQuest);
        this.buttonList.add(deleteQuest);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        Minecraft mc = Minecraft.getMinecraft();
        super.drawScreen(mouseX, mouseY, partialTicks);

        questName.drawTextBox();
        categoryOwner.drawTextBox();

        String text1 = "Quest: " + name;
        int text1Width = mc.fontRendererObj.getStringWidth(text1);
        RenderUtils.drawText(mc, text1, (width - text1Width) / 2f, height / 6f - 25, 1, 0);

        String text2 = "Quest elements: ";
        int text2Width = mc.fontRendererObj.getStringWidth(text2);
        RenderUtils.drawText(mc, text2, (width - text2Width) / 2f, height / 6f + 35, 1, 0);
    }

    @Override
    protected void keyTyped(char c, int kc) throws IOException {
        super.keyTyped(c, kc);

        if (questName.isFocused()) questName.textboxKeyTyped(c, kc);
        else if (categoryOwner.isFocused()) categoryOwner.textboxKeyTyped(c, kc);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean qnWas = questName.isFocused();
        questName.mouseClicked(mouseX, mouseY, mouseButton);
        if (questName.isFocused()) {
            if (!qnWas && questName.getText().equals("Set name")) questName.setText("");
        } else if (qnWas) {
            if (questName.getText().isEmpty()) questName.setText("Set name");
        }
        boolean coWas = categoryOwner.isFocused();
        categoryOwner.mouseClicked(mouseX, mouseY, mouseButton);
        if (categoryOwner.isFocused()) {
            if (!coWas && categoryOwner.getText().equals("Set category")) categoryOwner.setText("");
        } else if (coWas) {
            if (categoryOwner.getText().isEmpty()) categoryOwner.setText("Set category");
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button == close) mc.thePlayer.closeScreen();
        else if (button == back) new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new CategoryGui()))).start();
        else if (button == enableQuest) {
            enabled =! enabled;
            enableQuest.displayString = "Enable quest: " + Config.understandMe(enabled);
        }
        else if (button == saveQuest) {
            if (saveQuest()) Utils.sendModMessage("Saved quest " + name);
        }
        else if (button == discardQuest) {
            clear();
            new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new CategoryGui()))).start();
        } else if (questElements.contains(button)) {
            if (saveQuest()) {
                if (button != newElement) {
                    int ind = questElements.indexOf(button);
                    QuestElement element = elements.get(ind);
                    QuestElementCreatorGui.loadElement(element, ind);
                } else QuestElementCreatorGui.clear();
                int index = -1;
                String category = categoryOwner.getText();
                if (!category.isEmpty()) {
                    for (Quest quest : QuestMaster.quests.get(category)) {
                        if (quest.name.equals(questName.getText())) index = QuestMaster.quests.get(category).indexOf(quest);
                    }
                }
                QuestElementCreatorGui.load(category, index);
                new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new QuestElementCreatorGui()))).start();
            }
        } else if (button == deleteQuest) {
            if (!category.equals(oldCategory) || oldCategory.isEmpty()) {
                Utils.sendModMessage("§cYou already changed this quest category's name. Please save changes first");
                return;
            } else if (!name.equals(oldName) || oldName.isEmpty()) {
                Utils.sendModMessage("§cYou already changed this quest's name. Please save changes first");
                return;
            }
            deleting = true;

            ChatComponentText delete = new ChatComponentText("§l§c[DELETE]§r");
            delete.setChatStyle(delete.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/questmaster configcommand deletequest")).
                    setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("DELETE the quest " + questName.getText()))));
            ChatComponentText cancel = new ChatComponentText("§l§a  [CANCEL]§r  ");
            cancel.setChatStyle(cancel.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/questmaster configcommand canceldeletequest")).
                    setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Cancel deletion"))));
            Utils.sendModMessage(new ChatComponentText("§cAre you sure you want to delete the quest " + questName.getText() + "?  ").appendSibling(delete).appendSibling(new ChatComponentText("  ").appendSibling(cancel)));

            FMLClientHandler.instance().getClient().displayGuiScreen(new GuiChat());
        }
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

    public static boolean saveQuest() {
        String n = questName.getText();
        String c = categoryOwner.getText();
        if (n.isEmpty() || n.equals("Set name") || c.isEmpty() || c.equals("Set category")) return false;

        Quest creating = new Quest(n, enabled);
        creating.addAll(elements);

        if (QuestMaster.quests.containsKey(c)) {
            QuestMaster.quests.get(oldCategory).removeIf(quest -> quest.name.equals(oldName));
            QuestMaster.quests.get(c).add(creating);
        }
        else QuestMaster.quests.put(c, new ArrayList<>(Collections.singletonList(creating)));
        if (FileUtils.save(creating, FileUtils.questDir + c + "/", n + ".bin")) {
            if ((!oldName.equals(n) |! oldCategory.equals(c)) &! oldName.isEmpty() &! oldCategory.isEmpty()) {
                FileUtils.deleteFile(FileUtils.questDir + oldCategory + "/" + oldName + ".bin");
            }
            oldName = n;
            name = oldName;
            oldCategory = c;
            category = oldCategory;
            exists = true;
            elements = creating;
        } else Utils.sendModMessage("Error saving quest " + n + " to file. Only save locally");
        return true;
    }

    public static void deleteQuest() {
        if (!deleting) return;
        if (exists) {
            int index = -1;
            for (Quest quest : QuestMaster.quests.get(category)) {
                if (quest.name.equals(name)) index = QuestMaster.quests.get(category).indexOf(quest);
            }
            if (index != -1) {
                QuestMaster.quests.get(category).remove(index);
                FileUtils.deleteFile(Config.configDir + "Quests/" + category + "/" + name);
                Utils.sendModMessage("§cDeleted " + category + " - " + name);
            } else Utils.sendModMessage("§cError deleting " + category + " - " + name);
            clear();
        }
        deleting = false;
    }

    public static void loadQuest(Quest quest, String cat) {
        if (quest != null) {
            enabled = quest.enabled;
            oldName = quest.name;
            name = oldName;
            elements = quest;
        }
        oldCategory = cat;
        category = oldCategory;
        deleting = false;
        exists = true;
    }

    public static void clear() {
        enabled = false;
        oldName = "";
        name = "";
        oldCategory = "";
        category = "";
        elements = new ArrayList<>();
        deleting = false;
        exists = false;
    }


    public static boolean saveElement(String name, int index) {
        if (name.isEmpty() || name.equals("Set displayed objective") || QuestElementCreatorGui.editing.progressTrigger == null) {
            Utils.sendModMessage("§cValues not set");
            return false;
        }

        QuestElementCreatorGui.saveToElement();

        QuestElementCreatorGui.editing.name = name;
        if (QuestElementCreatorGui.editing.waypoint.equals(new Vector3f(69420, 69420, 69420))) QuestElementCreatorGui.editing.waypoint = null;

        if (index != -1) {
            elements.remove(index);
            elements.add(index, QuestElementCreatorGui.editing);
        } else elements.add(QuestElementCreatorGui.editing);
        saveQuest();
        Utils.sendModMessage("Saved quest element " + name);
        return true;
    }

    @Override
    public void onGuiClosed() {
        name = questName.getText().equals("Set name") ? "" : questName.getText();
        category = categoryOwner.getText().equals("Set category") ? "" : categoryOwner.getText();
    }
}
