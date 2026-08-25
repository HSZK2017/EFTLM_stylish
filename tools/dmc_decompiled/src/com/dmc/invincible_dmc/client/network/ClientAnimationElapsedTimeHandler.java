package com.dmc.invincible_dmc.client.network;

import com.dmc.invincible_dmc.network.server.S2CAnimationElapsedTimePacket;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public final class ClientAnimationElapsedTimeHandler {
   private ClientAnimationElapsedTimeHandler() {
   }

   public static void apply(S2CAnimationElapsedTimePacket packet) {
      Minecraft minecraft = Minecraft.m_91087_();
      if (minecraft.f_91073_ != null) {
         if (minecraft.f_91073_.m_6815_(packet.entityId()) instanceof LivingEntity livingEntity) {
            LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingEntity, LivingEntityPatch.class);
            AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(patch);
            AnimationAccessor expectedAnimation = AnimationManager.byId(packet.animationId());
            if (animationPlayer != null && expectedAnimation != null && DMCAnimationUtils.sameAccessor(animationPlayer.getRealAnimation(), expectedAnimation)) {
               animationPlayer.setElapsedTime(packet.elapsedTime());
            }
         }
      }
   }
}
