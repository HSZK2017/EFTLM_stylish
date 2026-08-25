package com.dmc.invincible_dmc.client;

import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerCapability;
import com.dmc.invincible_dmc.network.server.S2CDoppelgangerSyncPacket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT},
   bus = Bus.FORGE
)
public final class DoppelgangerClientBindingSync {
   private static final long PENDING_TTL_TICKS = 200L;
   private static final Map<UUID, DoppelgangerClientBindingSync.PendingSnapshot> PENDING = new HashMap<>();

   private DoppelgangerClientBindingSync() {
   }

   public static void applyOrQueue(S2CDoppelgangerSyncPacket packet) {
      Minecraft minecraft = Minecraft.m_91087_();
      ClientLevel level = minecraft.f_91073_;
      if (level != null) {
         if (apply(level, packet)) {
            PENDING.remove(packet.ownerUUID());
         } else {
            PENDING.put(packet.ownerUUID(), new DoppelgangerClientBindingSync.PendingSnapshot(packet, level.m_46467_() + 200L));
         }
      }
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END && !PENDING.isEmpty()) {
         ClientLevel level = Minecraft.m_91087_().f_91073_;
         if (level == null) {
            PENDING.clear();
         } else {
            Iterator<Entry<UUID, DoppelgangerClientBindingSync.PendingSnapshot>> iterator = PENDING.entrySet().iterator();

            while (iterator.hasNext()) {
               DoppelgangerClientBindingSync.PendingSnapshot pending = iterator.next().getValue();
               if (level.m_46467_() > pending.expiryGameTime || apply(level, pending.packet)) {
                  iterator.remove();
               }
            }
         }
      }
   }

   private static boolean apply(ClientLevel level, S2CDoppelgangerSyncPacket packet) {
      Player var10000;
      label16: {
         if (level.m_6815_(packet.ownerEntityId()) instanceof Player player && packet.ownerUUID().equals(player.m_20148_())) {
            var10000 = player;
            break label16;
         }

         var10000 = level.m_46003_(packet.ownerUUID());
      }

      Player owner = var10000;
      if (owner == null) {
         return false;
      } else {
         owner.getCapability(DoppelgangerCapability.INSTANCE)
            .ifPresent(
               cap -> cap.applyBindingSnapshot(
                     packet.doppelgangerUUID(), packet.generation(), packet.state(), packet.dimension(), packet.delayMode(), packet.doppelgangerEntityId()
                  )
            );
         return true;
      }
   }

   private static record PendingSnapshot(S2CDoppelgangerSyncPacket packet, long expiryGameTime) {
   }
}
