package com.QuestMaster.handlers;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Island;
import com.QuestMaster.classes.Quest;
import com.QuestMaster.classes.QuestElement;
import com.QuestMaster.config.Config;
import com.QuestMaster.events.PacketEvent;
import com.QuestMaster.utils.SkyblockItemHandler;
import com.QuestMaster.utils.Utils;
import com.QuestMaster.utils.WaypointUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Map;

public class QuestEventHandler {
    private static boolean notMet() {
        return !Config.modToggle |! QuestMaster.inSkyblock;
    }

    @SubscribeEvent
    void playerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!event.entity.equals(QuestMaster.mc.thePlayer) || notMet()) return;
        questCheckForLoop(Utils.vec3ToSerializable(event.entity.getPositionVector()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    void chatMessageReceived(ClientChatReceivedEvent event) {
        if (event.type != 0 || notMet()) return;
        questCheckForLoop(event.message.getUnformattedText());
    }

    @SubscribeEvent(receiveCanceled = true)
    void receivePacket(PacketEvent.ReceiveEvent event) {
        if (QuestMaster.mc.thePlayer == null || notMet()) return;
        if (QuestMaster.mc.thePlayer.ticksExisted <= 1) return;
        if (event.packet instanceof S2FPacketSetSlot) {
            if (((S2FPacketSetSlot) event.packet).func_149175_c() != 0) return;
            ItemStack item = ((S2FPacketSetSlot) event.packet).func_149174_e();
            if (item == null) return;
            questCheckForLoop(item);
            if (QuestMaster.dev) Utils.sendModMessage("DEV: " + ((S2FPacketSetSlot) event.packet).func_149174_e().stackSize + " " + ((S2FPacketSetSlot) event.packet).func_149174_e().getDisplayName()
                    + " " + SkyblockItemHandler.getSkyBlockItemID(((S2FPacketSetSlot) event.packet).func_149174_e()));
        }
    }

    @SubscribeEvent
    void sendPacket(PacketEvent.SendEvent event) {
        if (QuestMaster.mc.thePlayer == null || QuestMaster.mc.theWorld == null || notMet()) return;
        if (event.packet instanceof C07PacketPlayerDigging || event.packet instanceof C08PacketPlayerBlockPlacement || event.packet instanceof C02PacketUseEntity) {
            if (event.packet instanceof C02PacketUseEntity) if (((C02PacketUseEntity) event.packet).getEntityFromWorld(QuestMaster.mc.theWorld).getPosition().equals(new BlockPos(0,0,0))
                    || ((C02PacketUseEntity) event.packet).getEntityFromWorld(QuestMaster.mc.theWorld).getPosition().equals(new BlockPos(-1,-1,-1))) return;
            questCheckForLoop(event.packet);
            if (QuestMaster.dev) {
                BlockPos pos = null;
                int button = -1;
                if (event.packet instanceof C07PacketPlayerDigging) {
                    pos = ((C07PacketPlayerDigging) event.packet).getPosition();
                    button = 0;
                }
                else if (event.packet instanceof C08PacketPlayerBlockPlacement) {
                    pos = ((C08PacketPlayerBlockPlacement) event.packet).getPosition();
                    button = 1;
                }
                else if (event.packet instanceof C02PacketUseEntity) {
                    pos = ((C02PacketUseEntity) event.packet).getEntityFromWorld(QuestMaster.mc.theWorld).getPosition();
                    button = ((C02PacketUseEntity) event.packet).getAction().equals(C02PacketUseEntity.Action.ATTACK) ? 0 : 1;
                }
                Utils.sendModMessage("DEV: " + button + " " + pos);
            }
        }
    }

    private void questCheckForLoop(Object object) {
        for (Map.Entry<String, List<Quest>> category : QuestMaster.quests.entrySet()){
            for (Quest quest : category.getValue()) {
                quest.checkTrigger(object);
            }
        }
    }

    @SubscribeEvent
    void worldRender(RenderWorldLastEvent event) {
        if (QuestMaster.mc.thePlayer == null || notMet()) return;
        for (Map.Entry<String, List<Quest>> entry : QuestMaster.quests.entrySet()) {
            for (Quest quest : entry.getValue()) {
                if (quest.enabled && ((quest.locations.contains(QuestMaster.island) &! QuestMaster.island.equals(Island.NONE)) || quest.locations.isEmpty())) {
                    for (QuestElement element : quest) {
                        if (element.enabled && element.waypoint != null &! element.name.equals("END_OF_QUEST")) {
                            String text = element.name;
                            WaypointUtils.renderBeacon(event.partialTicks, text, 1f, Utils.serializableToVec3(element.waypoint), quest.color);
                        }
                    }
                }
            }
        }
    }
}
