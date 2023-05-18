package com.QuestMaster.gui;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Island;
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
import net.minecraftforge.fml.client.config.GuiSlider;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Mouse;

import javax.vecmath.Vector3f;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class QuestCreatorGui extends GuiScreen {
    public static Quest quest = null;

    private GuiButton close;
    private GuiButton back;
    private GuiButton saveQuest;
    private GuiButton discardQuest;
    private GuiButton deleteQuest;
    private GuiSlider rSlider;
    private GuiSlider gSlider;
    private GuiSlider bSlider;
    public static boolean deleting = false;

    private GuiButton enableQuest;
    private static GuiTextField questName;
    private static GuiTextField categoryOwner;

    public static boolean exists = false;

    public static String oldName = "";
    public static String category = "";
    public static String oldCategory = "";
    private static boolean editedElements = false;
    public static List<Gui> questElements = new ArrayList<>();
    private static GuiButton newElement;

    private static GuiButton newLocation;
    private static List<Gui> locations = new ArrayList<>();

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (quest == null) {
            new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new CategoryGui()))).start();
            return;
        }
        GuiManager.lastgui = "quest";

        close = new GuiButton(0, width / 2 - 75, height / 6 * 5, 150, 20, "Close");
        back = new GuiButton(1, width / 2 + 300, height / 6 * 5, 150, 20, "Back");
        saveQuest = new GuiButton(3, width / 2 + 120, height / 6 - 25, 150, 20, "§aSave quest");
        discardQuest = new GuiButton(4, width / 2 - 270, height / 6 - 25, 150, 20, "§cDiscard edited quest");
        deleteQuest = new GuiButton(5, width / 2 - 75, height / 6 * 4, 150, 20, "§cDelete quest");

        enableQuest = new GuiButton(5, 0, 0, 150, 20, "Enable quest: " + Config.understandMe(quest.enabled));
        questName = new GuiTextField(6, this.fontRendererObj, 0, 0, 200, 20);
        questName.setText(quest.name.isEmpty() ? "Set name" : quest.name);
        categoryOwner = new GuiTextField(7, this.fontRendererObj, 0, 0, 200, 20);
        categoryOwner.setText(category.isEmpty() ? "Set category" : category);
        rSlider = new GuiSlider(8, 0, 0, 100, 20, "Red: ", "", 0, 255, quest.color.getRed(), false, true);
        gSlider = new GuiSlider(9, 0, 0, 100, 20, "Green: ", "", 0, 255, quest.color.getGreen(), false, true);
        bSlider = new GuiSlider(10, 0, 0, 100, 20, "Blue: ", "", 0, 255, quest.color.getBlue(), false, true);
        List<Gui> list = Arrays.asList(enableQuest, questName, categoryOwner, rSlider, gSlider, bSlider);
        GuiManager.displaycategory(list, width / 2f, height / 6f);
        enableAll(list);

        locations.clear();
        for (Island island : quest.locations) {
            GuiTextField field = new GuiTextField(90, QuestMaster.mc.fontRendererObj, 0, 0, 120, 20);
            field.setMaxStringLength(100);
            field.setText(island.toString());
            locations.add(field);
            locations.add(new GuiButton(81, 0, 0, 20, 20, "§cx"));
        }
        newLocation = new GuiButton(82, 0, 0, 100, 20, "New location");
        locations.add(newLocation);
        GuiManager.displaycategory(locations, width / 2f + 200, height / 6f + 70 , 150, false);
        enableAll(locations);

        questElements.clear();
        for (QuestElement element : quest) {
            questElements.add(new GuiButton(80, 0, 0, 120, 20, element.name));
            questElements.add(new GuiButton(91, 0, 0, 20, 20, Config.understandMeIcon(element.enabled)));
        }
        newElement = new GuiButton(92, 0, 0, 150, 20, "Create new element");
        questElements.add(newElement);
        GuiManager.displaycategory(questElements, width / 2f - 100, height / 6f + 70, 400, false);
        enableAll(questElements);

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

        String text1 = "Quest: " + quest.name;
        int text1Width = mc.fontRendererObj.getStringWidth(text1);
        RenderUtils.drawText(mc, text1, (width - text1Width) / 2f, height / 6f - 25, 1, 0);

        String text2 = "Quest elements: ";
        int text2Width = mc.fontRendererObj.getStringWidth(text2);
        RenderUtils.drawText(mc, text2, (width - text2Width) / 2f - 100, height / 6f + 55, 1, 0);

        String text3 = "Locations: ";
        int text3Width = mc.fontRendererObj.getStringWidth(text3);
        RenderUtils.drawText(mc, text3, (width - text3Width) / 2f + 200, height / 6f + 55, 1, 0);

        if (locations.size() > 1) {
            int scrolled = - (int) Math.signum(Mouse.getDWheel());
            for (int i = 0; i < locations.size() - 2; i += 2) {
                if (locations.get(i) instanceof GuiTextField) {
                    ((GuiTextField) locations.get(i)).drawTextBox();
                    if (((GuiTextField) locations.get(i)).isFocused()) {
                        if (scrolled != 0) {
                            int index = (ArrayUtils.indexOf(Island.values(), Island.valueOf(((GuiTextField) locations.get(i)).getText())) + scrolled) % Island.values().length;
                            ((GuiTextField) locations.get(i)).setText(Island.values()[Math.max(index < 0 ? Island.values().length + index : index, 0)].toString());
                        }
                    }
                }
            }
        }

        if (newLocation.isMouseOver()) {
            drawHoveringText(Collections.singletonList("Where the quest will be enabled - no locations = enabled anywhere"), mouseX - 5, mouseY);
        }
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
        if (locations.size() > 1) {
            for (int i = 0; i < locations.size() - 2; i += 2) {
                ((GuiTextField) locations.get(i)).mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickButton, long time) {
        super.mouseClickMove(mouseX, mouseY, clickButton, time);

        if (rSlider.dragging) {
            quest.color = new Color(rSlider.getValueInt(), quest.color.getGreen(), quest.color.getBlue());
        } else if (gSlider.dragging) {
            quest.color = new Color(quest.color.getRed(), gSlider.getValueInt(), quest.color.getBlue());
        } else if (bSlider.dragging) {
            quest.color = new Color(quest.color.getRed(), quest.color.getGreen(), bSlider.getValueInt());
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button == close) mc.thePlayer.closeScreen();
        else if (button == back) new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new CategoryGui()))).start();
        else if (button == enableQuest) {
            quest.enabled =! quest.enabled;
            enableQuest.displayString = "Enable quest: " + Config.understandMe(quest.enabled);
        }
        else if (button == saveQuest) {
            if (saveQuest()) Utils.sendModMessage("Saved quest " + quest.name);
        } else if (button == discardQuest) {
            clear();
            new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new CategoryGui()))).start();
        } else if (questElements.contains(button)) {
            if (questElements.indexOf(button) % 2 == 1) {
                quest.get((questElements.indexOf(button) - 1) / 2).enabled = !quest.get((questElements.indexOf(button) - 1) / 2).enabled;
                button.displayString = Config.understandMeIcon(quest.get((questElements.indexOf(button) - 1) / 2).enabled);
                editedElements = true;
            } else if (saveQuest()) {
                if (button != newElement) {
                    int ind = questElements.indexOf(button) / 2;
                    QuestElement element = quest.get(ind);
                    QuestElementCreatorGui.loadElement(element, ind);
                } else QuestElementCreatorGui.clear();
                int index = -1;
                String category = categoryOwner.getText();
                if (!category.isEmpty()) {
                    for (Quest quest : QuestMaster.quests.get(category)) {
                        if (quest.name.equals(questName.getText()))
                            index = QuestMaster.quests.get(category).indexOf(quest);
                    }
                }
                QuestElementCreatorGui.load(category, index);
                new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new QuestElementCreatorGui()))).start();
            }
        } else if (locations.contains(button)) {
            if (button == newLocation) {
                GuiTextField field = new GuiTextField(0, QuestMaster.mc.fontRendererObj, 0, 0, 120, 20);
                field.setMaxStringLength(100);
                field.setText("NONE");
                locations.add(locations.size() - 1, field);
                locations.add(locations.size() - 1, new GuiButton(0, 0, 0, 20, 20, "§cx"));
                GuiManager.displaycategory(locations, width / 2f + 200, height / 6f + 70 , 150, false);
                enableAll(locations);
            } else if (locations.indexOf(button) % 2 == 1) {
                int index = locations.indexOf(button);
                this.buttonList.remove(button);
                locations.remove(index);
                locations.remove(index - 1);
                GuiManager.displaycategory(locations, width / 2f + 200, height / 6f + 70 , 150, false);
            }
        } else if (button == deleteQuest) {
            if (!category.equals(oldCategory) || oldCategory.isEmpty()) {
                Utils.sendModMessage("§cYou already changed this quest category's name. Please save changes first");
                return;
            } else if (!quest.name.equals(oldName) || oldName.isEmpty()) {
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

    private void enableAll(List<Gui> elements) {
        for (Gui element : elements) {
            if (element instanceof GuiButton) if (!this.buttonList.contains((GuiButton) element)) this.buttonList.add((GuiButton) element);
        }
    }

    public static boolean saveQuest() {
        String n = questName.getText();
        String c = categoryOwner.getText();
        if (n.isEmpty() || n.equals("Set name") || c.isEmpty() || c.equals("Set category")) return false;

        quest.locations = new ArrayList<>();
        quest.name = n;
        for (int i = 0; i < locations.size() - 2; i += 2) {
            if (locations.get(i) != newLocation) {
                quest.locations.add(Island.valueOf(((GuiTextField) locations.get(i)).getText()));
            }
        }

        if (QuestMaster.quests.containsKey(c)) {
            QuestMaster.quests.get(oldCategory).removeIf(quest -> quest.name.equals(oldName));
            QuestMaster.quests.get(c).add(quest);
        } else QuestMaster.quests.put(c, new ArrayList<>(Collections.singletonList(quest)));

        if (FileUtils.save(quest, FileUtils.questDir + c + "/", n + ".bin")) {
            if ((!oldName.equals(n) |! oldCategory.equals(c)) &! oldName.isEmpty() &! oldCategory.isEmpty()) {
                FileUtils.deleteFile(FileUtils.questDir + oldCategory + "/" + oldName + ".bin");
            }
            oldName = n;
            oldCategory = c;
            category = oldCategory;
            exists = true;
            editedElements = false;
        } else Utils.sendModMessage("Error saving quest " + n + " to file. Only save locally");
        return true;
    }

    public static void deleteQuest() {
        if (!deleting) return;
        if (exists) {
            int index = -1;
            for (Quest q : QuestMaster.quests.get(category)) {
                if (q.name.equals(quest.name)) index = QuestMaster.quests.get(category).indexOf(q);
            }
            if (index != -1) {
                QuestMaster.quests.get(category).remove(index);
                FileUtils.deleteFile(Config.configDir + "Quests/" + category + "/" + quest.name);
                Utils.sendModMessage("§cDeleted " + category + " - " + quest.name);
            } else Utils.sendModMessage("§cError deleting " + category + " - " + quest.name);
            clear();
        }
        deleting = false;
    }

    public static void loadQuest(Quest q, String cat) {
        oldCategory = cat;
        category = oldCategory;
        deleting = false;
        exists = true;
        if (q == null) {
            q = new Quest("", Color.black);
        }
        quest = q;
        oldName = quest.name;
        for (int i = 0; i < q.size(); i++) {
            if (q.get(i).name.equals("END_OF_QUEST") && q.size() -1 != i) {
                q.add(q.get(i));
                quest.remove(i);
                editedElements = true;
                break;
            }
        }
    }

    public static void clear() {
        oldName = "";
        oldCategory = "";
        category = "";
        quest = null;
        deleting = false;
        exists = false;
        editedElements = false;
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
            quest.remove(index);
            if (name.equals("END_OF_QUEST")) quest.add(QuestElementCreatorGui.editing);
            else quest.add(index, QuestElementCreatorGui.editing);
        } else quest.add(QuestElementCreatorGui.editing);
        if (!QuestElementCreatorGui.editing.enabled) {
            boolean keep = false;
            for (QuestElement element : quest) {
                if (element.enabled) {
                    keep = true;
                    break;
                }
            }
            if (!keep) {
                quest.enabled = false;
            }
        }
        saveQuest();
        Utils.sendModMessage("Saved quest element " + name);
        return true;
    }

    @Override
    public void onGuiClosed() {
        quest.name = questName.getText().equals("Set name") ? "" : questName.getText();
        category = categoryOwner.getText().equals("Set category") ? "" : categoryOwner.getText();
        if (editedElements) saveQuest();
    }
}
