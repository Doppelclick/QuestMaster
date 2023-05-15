package com.QuestMaster.gui;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Quest;
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

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CategoryGui extends GuiScreen {
    private GuiButton close;
    private GuiButton back;
    private GuiButton saveCategory;
    private GuiButton discardCategory;
    private static String oldCategory = "";
    private static String category = "";
    private static GuiTextField categoryName;
    private static GuiButton deleteCategory;
    public static boolean deleting = false;
    private static List<Gui> categoryMain = new ArrayList<>();
    private GuiButton newQuest;
    private static HashMap<Gui, Quest> quests = new LinkedHashMap<>();

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (category.isEmpty()) {
            category = "Set name";
        }
        if (!oldCategory.isEmpty() &! QuestMaster.quests.containsKey(oldCategory)) {
            new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new MainGui()))).start();
            return;
        }
        GuiManager.lastgui = "category";

        close = new GuiButton(0, width / 2 - 75, height / 6 * 5, 150, 20, "Close");
        back = new GuiButton(1, width / 2 + 300, height / 6 * 5, 150, 20, "Back");
        saveCategory = new GuiButton(3, width / 2 + 120, height / 6 - 35, 150, 20, "§aSave category");
        deleteCategory = new GuiButton(92, width / 2 - 75, height / 6 * 4, 150, 20, "§cDelete category");

        this.buttonList.add(close);
        this.buttonList.add(back);
        this.buttonList.add(saveCategory);
        this.buttonList.add(deleteCategory);

        categoryName = new GuiTextField(91, this.fontRendererObj, 0, 0, 200, 20);
        categoryName.setText(category);
        discardCategory = new GuiButton(4, 0, 0, 150, 20, "§cDiscard edited category");
        categoryMain = new ArrayList<>(Arrays.asList(categoryName, discardCategory));
        GuiManager.displaycategory(categoryMain, width / 2f, height / 6f);
        this.buttonList.add(discardCategory);

        quests.clear();
        if (!category.equals("Set name")) {
            for (Quest quest : QuestMaster.quests.get(category)) {
                GuiButton button = new GuiButton(1, width / 2 + 300, height / 6 * 5, 150, 20, quest.name);
                quests.put(button, quest);
            }
        }
        newQuest = new GuiButton(0, width / 2 + 120, height / 6 - 25, 150, 20, "Create new quest");
        quests.put(newQuest, null);
        GuiManager.displaycategory(new ArrayList<>(quests.keySet()), width / 2f, height / 6f + 50);
        setAllState(new ArrayList<>(quests.keySet()), true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        Minecraft mc = Minecraft.getMinecraft();
        super.drawScreen(mouseX, mouseY, partialTicks);

        String text1 = "Category: " + categoryName.getText();
        int text1Width = mc.fontRendererObj.getStringWidth(text1);
        RenderUtils.drawText(mc, text1, (width - text1Width) / 2f, height / 6f - 25, 1, 0);

        String text2 = "Quests:";
        int text2Width = mc.fontRendererObj.getStringWidth(text2);
        RenderUtils.drawText(mc, text2, (width - text2Width) / 2f, height / 6f + 35, 1, 0);

        categoryName.drawTextBox();
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
    protected void keyTyped(char c, int kc) throws IOException {
        super.keyTyped(c, kc);

        if (categoryName.isFocused()) categoryName.textboxKeyTyped(c, kc);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean wasFocused = categoryName.isFocused();
        categoryName.mouseClicked(mouseX, mouseY, mouseButton);
        if (!wasFocused && categoryName.isFocused()) if (categoryName.getText().equals("Set name")) categoryName.setText("");
        else if (!categoryName.isFocused() && categoryName.getText().isEmpty()) categoryName.setText("Set name");
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button == close) mc.thePlayer.closeScreen();
        else if (button == back) new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new MainGui()))).start();
        else if (quests.containsKey(button)) {
            if (saveCategory()) {
                QuestCreatorGui.loadQuest(quests.get(button), category);
                new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new QuestCreatorGui()))).start();
            }
        } else if (button == deleteCategory) {
            if (!category.equals(oldCategory) || oldCategory.isEmpty()) {
                Utils.sendModMessage("§cYou already changed this category's name. Please save changes first");
                return;
            }
            deleting = true;

            ChatComponentText delete = new ChatComponentText("§l§c[DELETE]§r");
            delete.setChatStyle(delete.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/questmaster configcommand deletecategory")).
                    setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("DELETE the category " + category))));
            ChatComponentText cancel = new ChatComponentText("§l§a  [CANCEL]§r  ");
            cancel.setChatStyle(cancel.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/questmaster configcommand canceldeletecategory")).
                    setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Cancel deletion"))));
            Utils.sendModMessage(new ChatComponentText("§cAre you sure you want to delete the quest category " + category + "?  ").appendSibling(delete).appendSibling(new ChatComponentText("  ").appendSibling(cancel)));

            FMLClientHandler.instance().getClient().displayGuiScreen(new GuiChat());
        } else if (button == saveCategory) {
            if (saveCategory()) new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new MainGui()))).start();
        } else if (button == discardCategory) {
            new Thread(() -> QuestMaster.mc.addScheduledTask(() -> QuestMaster.mc.displayGuiScreen(new MainGui()))).start();
            loadCategory("");
            deleting = false;
        }
    }

    public static void loadCategory(String cat) {
        oldCategory = cat;
        category = oldCategory;
        deleting = false;
    }

    private static boolean saveCategory() {
        if (categoryName.getText().isEmpty() || categoryName.getText().equals("Set name")) {
            Utils.sendModMessage("§cPlease set a category name");
            return false;
        }

        if (oldCategory.isEmpty()) {
            QuestMaster.quests.put(categoryName.getText(), new ArrayList<>());
            return new File(Config.configDir + "Quests/" + categoryName.getText() + "/").mkdirs();
        } else if (!categoryName.getText().equals(oldCategory)) {
            if (QuestMaster.quests.containsKey(categoryName.getText())) {
                QuestMaster.quests.get(categoryName.getText()).addAll(QuestMaster.quests.get(oldCategory));
                QuestMaster.quests.remove(oldCategory);
            } else {
                QuestMaster.quests.put(categoryName.getText(), QuestMaster.quests.get(oldCategory));
                QuestMaster.quests.remove(oldCategory);
            }
        }
        return true;
    }

    public static void deleteCategory() {
        if (!deleting) return;
        if (QuestMaster.quests.remove(category) != null) {
            FileUtils.deleteFile(Config.configDir + "Quests/" + category);
            Utils.sendModMessage("§cDeleted " + category);
        } else Utils.sendModMessage("§cError deleting " + category);
        loadCategory("");
        deleting = false;
    }

    @Override
    public void onGuiClosed() {
        String text = categoryName.getText();
        category = text.equals("Set name") ? "" : text;
    }
}
