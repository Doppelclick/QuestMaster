package com.QuestMaster.handlers;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Quest;
import com.QuestMaster.classes.QuestElement;
import com.QuestMaster.config.Config;
import com.QuestMaster.events.PacketEvent;
import com.QuestMaster.utils.Utils;
import com.QuestMaster.utils.WaypointUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S2FPacketSetSlot;
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
        if (!event.entity.equals(QuestMaster.mc.thePlayer)) return;
        questCheckForLoop(Utils.vec3ToSerializable(event.entity.getPositionVector()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    void chatMessageReceived(ClientChatReceivedEvent event) {
        if (event.type != 0) return;
        questCheckForLoop(event.message.getUnformattedText());
    }

    @SubscribeEvent(receiveCanceled = true)
    void receivePacket(PacketEvent.ReceiveEvent event) {
        if (QuestMaster.mc.thePlayer == null) return;
        if (QuestMaster.mc.thePlayer.ticksExisted <= 1) return;
        if (event.packet instanceof S2FPacketSetSlot) {
            if (((S2FPacketSetSlot) event.packet).func_149175_c() != 0) return;
            ItemStack item = ((S2FPacketSetSlot) event.packet).func_149174_e();
            if (item == null) return;
            questCheckForLoop(item);
        }
    }

    @SubscribeEvent
    void sendPacket(PacketEvent.SendEvent event) {
        if (QuestMaster.mc.thePlayer == null) return;
        if (event.packet instanceof C07PacketPlayerDigging || event.packet instanceof C08PacketPlayerBlockPlacement) {
            questCheckForLoop(event.packet);
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
        for (Map.Entry<String, List<Quest>> entry : QuestMaster.quests.entrySet()) {
            for (Quest quest : entry.getValue()) {
                if (quest.enabled) {
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
