package com.QuestMaster.handlers;

import com.QuestMaster.QuestMaster;
import com.QuestMaster.classes.Quest;
import com.QuestMaster.config.Config;
import com.QuestMaster.events.PacketEvent;
import com.QuestMaster.utils.Utils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Map;

public class QuestEventHandler {

    @SubscribeEvent
    void playerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!Config.modToggle |! event.entity.equals(QuestMaster.mc.thePlayer)) return;
        questForLoop(Utils.vec3ToSerializable(event.entity.getPositionVector()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    void chat(ClientChatReceivedEvent event) {
        if (!Config.modToggle || event.type != 0) return;
        questForLoop(event.message);
    }

    @SubscribeEvent(receiveCanceled = true)
    void receivePacket(PacketEvent.ReceiveEvent event) {
        if (!Config.modToggle || QuestMaster.mc.thePlayer == null |!QuestMaster.inSkyblock) return;
        if (QuestMaster.mc.thePlayer.ticksExisted <= 1) return;
        if (event.packet instanceof S2FPacketSetSlot) {
            if (((S2FPacketSetSlot) event.packet).func_149175_c() != 0) return;
            ItemStack item = ((S2FPacketSetSlot) event.packet).func_149174_e();
            if (item == null) return;
            questForLoop(item);
        }
    }

    @SubscribeEvent
    void sendPacket(PacketEvent.SendEvent event) {
        EntityPlayerSP player = QuestMaster.mc.thePlayer;
        if (!Config.modToggle || player == null) return;
        if (event.packet instanceof C07PacketPlayerDigging || event.packet instanceof C08PacketPlayerBlockPlacement) {
            questForLoop(event.packet);
        }
    }

    private void questForLoop(Object object) {
        for (Map.Entry<String, List<Quest>> category : QuestMaster.quests.entrySet()){
            for (Quest quest : category.getValue()) {
                quest.checkTrigger(object);
            }
        }
    }
}
