package com.dmc.invincible_dmc.events;

import com.dmc.invincible_dmc.gameassets.DMCAnimationVariableKeys;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.api.animation.SynchedAnimationVariableKey;
import yesman.epicfight.api.animation.AnimationVariables.IndependentAnimationVariableKey;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.common.AnimationVariablePacket.Action;
import yesman.epicfight.network.server.SPAnimationVariablePacket;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@EventBusSubscriber(
   modid = "invincible_dmc"
)
public final class YamatoIdleSyncEvents {
   private YamatoIdleSyncEvents() {
   }

   @SubscribeEvent
   public static void onStartTracking(StartTracking event) {
      if (event.getEntity() instanceof ServerPlayer trackingPlayer) {
         EpicFightCapabilities.getUnparameterizedEntityPatch(event.getTarget(), LivingEntityPatch.class)
            .ifPresent(
               patch -> patch.getAnimator()
                     .getVariables()
                     .get((IndependentAnimationVariableKey)DMCAnimationVariableKeys.YAMATO_IDLE_STATE.get(), YamatoAnimations.YAMATO_IDLE)
                     .ifPresent(
                        state -> EpicFightNetworkManager.sendToPlayer(
                              new SPAnimationVariablePacket(
                                 patch,
                                 (SynchedAnimationVariableKey)DMCAnimationVariableKeys.YAMATO_IDLE_STATE.get(),
                                 YamatoAnimations.YAMATO_IDLE,
                                 state,
                                 Action.PUT
                              ),
                              trackingPlayer,
                              new Object[0]
                           )
                     )
            );
      }
   }
}
