package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.entity.HerobrineGregEntity;
import com.pla.annoyingvillagers.spawnhandler.GregData;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.FORGE
)
public class SleepPreventEvent {
   @SubscribeEvent
   public static void onPlayerSleep(PlayerSleepInBedEvent event) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         ServerLevel serverLevel = server.m_129880_(Level.f_46428_);
         if (serverLevel != null && serverLevel.m_46472_().equals(Level.f_46428_)) {
            GregData gregData = GregData.get(serverLevel);
            UUID gregUUID = gregData.getActiveId();
            if (gregUUID != null
               && serverLevel.m_8791_(gregUUID) instanceof HerobrineGregEntity herobrineGregEntity
               && herobrineGregEntity.m_6084_()
               && herobrineGregEntity.getSummonTimestamp() >= 0) {
               event.setResult(BedSleepingProblem.OTHER_PROBLEM);
               event.getEntity()
                  .m_5661_(
                     Component.m_237113_(
                           "Herobrine is preparing to invade near x: "
                              + herobrineGregEntity.m_20097_().m_123341_()
                              + " y: "
                              + herobrineGregEntity.m_20097_().m_123342_()
                              + " z: "
                              + herobrineGregEntity.m_20097_().m_123343_()
                              + ". You cannot sleep now!"
                        )
                        .m_130940_(ChatFormatting.RED),
                     false
                  );
            }
         }
      }
   }
}
