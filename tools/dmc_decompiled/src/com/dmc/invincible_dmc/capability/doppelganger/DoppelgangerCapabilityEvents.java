package com.dmc.invincible_dmc.capability.doppelganger;

import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE
)
public class DoppelgangerCapabilityEvents {
   private static final ResourceLocation CAP_ID = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "doppelganger_data");

   @SubscribeEvent
   public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof Player) {
         event.addCapability(CAP_ID, new DoppelgangerCapability.Provider());
      }
   }

   @SubscribeEvent
   public static void onPlayerClone(Clone event) {
      if (event.getOriginal() instanceof ServerPlayer oldPlayer) {
         DoppelgangerBindingService.discardOwned(oldPlayer);
      }

      event.getEntity().getCapability(DoppelgangerCapability.INSTANCE).ifPresent(cap -> {
         cap.clearPendingSummon();
         cap.setBinding(null, cap.getBindingGeneration(), DoppelgangerCapability.BindingState.NONE, "", 1);
      });
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         player.getCapability(DoppelgangerCapability.INSTANCE).ifPresent(DoppelgangerCapability.IDoppelgangerData::clearPendingSummon);
         DoppelgangerBindingService.reconcile(player);
         DoppelgangerBindingService.sync(player);
      }
   }

   @SubscribeEvent
   public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         DoppelgangerBindingService.discardOwned(player);
      }
   }

   @SubscribeEvent
   public static void onDoppelgangerDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof DoppelgangerEntity doppel && !doppel.m_9236_().f_46443_) {
         DoppelgangerBindingService.discard(doppel);
      }
   }

   @SubscribeEvent
   public static void onDoppelgangerJoinLevel(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof DoppelgangerEntity doppel && event.getLevel() instanceof ServerLevel level) {
         UUID ownerUUID = doppel.getOwnerUUID();
         if (ownerUUID == null) {
            return;
         }

         ServerPlayer owner = level.m_7654_().m_6846_().m_11259_(ownerUUID);
         if (owner == null) {
            return;
         }

         level.m_7654_().execute(() -> {
            if (doppel.m_6084_()) {
               DoppelgangerBindingService.reconcile(owner);
            }
         });
         return;
      }
   }

   @SubscribeEvent
   public static void onStartTracking(StartTracking event) {
      if (event.getEntity() instanceof ServerPlayer recipient) {
         if (event.getTarget() instanceof ServerPlayer owner) {
            DoppelgangerBindingService.syncTo(owner, recipient);
         } else if (event.getTarget() instanceof DoppelgangerEntity doppel) {
            UUID ownerUUID = doppel.getOwnerUUID();
            if (ownerUUID == null) {
               return;
            }

            ServerPlayer owner = recipient.f_8924_.m_6846_().m_11259_(ownerUUID);
            if (owner != null) {
               DoppelgangerBindingService.syncTo(owner, recipient);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && event.player instanceof ServerPlayer player) {
         if (player.f_19797_ % 10 == 0) {
            DoppelgangerBindingService.cancelExpiredPendingSummon(player);
         }
      }
   }

   public static void discardPlayersDoppel(ServerPlayer player) {
      DoppelgangerBindingService.discardOwned(player);
   }

   @EventBusSubscriber(
      modid = "invincible_dmc",
      bus = Bus.MOD
   )
   public static class ModBus {
      @SubscribeEvent
      public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
         event.register(DoppelgangerCapability.IDoppelgangerData.class);
      }
   }
}
