package com.dmc.invincible_dmc.mixin.epicfight;

import com.dmc.invincible_dmc.api.events.TimePeriodEvent;
import com.dmc.invincible_dmc.api.events.TimeStampedEvent;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.gameassets.DMCEffects;
import com.dmc.invincible_dmc.skill.weapon_innate.AbstractDmcInnateSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(
   value = {AnimationPlayer.class},
   remap = false
)
public abstract class AnimationPlayerMixin {
   @Shadow
   protected float prevElapsedTime;
   @Shadow
   protected float elapsedTime;

   @ModifyVariable(
      method = {"tick"},
      at = @At(
         value = "FIELD",
         target = "Lyesman/epicfight/api/animation/AnimationPlayer;elapsedTime:F",
         opcode = 180,
         ordinal = 2
      ),
      ordinal = 0
   )
   private float invincible_dmc$modifyPlaybackSpeed(float playbackSpeed, LivingEntityPatch<?> entityPatch) {
      try {
         if (entityPatch != null
            && entityPatch.getOriginal() != null
            && DMCEffects.SLOW.isPresent()
            && ((LivingEntity)entityPatch.getOriginal()).m_21023_((MobEffect)DMCEffects.SLOW.get())) {
            playbackSpeed *= 0.1F;
         }
      } catch (Exception var7) {
      }

      if (entityPatch instanceof PlayerPatch<?> playerPatch && playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getSkill() instanceof AbstractDmcInnateSkill) {
         DMCPlayer DMCPlayer = DMCPlayerCapabilityProvider.get((Player)playerPatch.getOriginal());
         if (DMCPlayer.getPlaySpeedMultiplier() != 0.0F) {
            return playbackSpeed * DMCPlayer.getPlaySpeedMultiplier();
         }
      }

      AnimationPlayer self = (AnimationPlayer)this;
      DynamicAnimation currentAnimation = DMCAnimationUtils.getCurrentAnimation(self);
      if (entityPatch != null
         && entityPatch.getOriginal() instanceof DoppelgangerEntity de
         && playbackSpeed != 1.0F
         && currentAnimation != null
         && !currentAnimation.isLinkAnimation()) {
         float synced = de.getAnimationSpeed();
         if (synced != 1.0F) {
            return synced;
         }
      }

      return playbackSpeed;
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void invincible_dmc$injectTick(LivingEntityPatch<?> entityPatch, CallbackInfo ci) {
      if (entityPatch instanceof PlayerPatch<?> playerPatch && playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getSkill() instanceof AbstractDmcInnateSkill) {
         ((Player)playerPatch.getOriginal()).getCapability(DMCPlayerCapabilityProvider.DMC_PLAYER).ifPresent(invinciblePlayer -> {
            if (((LivingEntity)entityPatch.getOriginal()).m_6084_()) {
               if (invinciblePlayer.getTimeEventList() != null) {
                  for (TimeStampedEvent event : invinciblePlayer.getTimeEventList()) {
                     event.testAndExecute(playerPatch, this.prevElapsedTime, this.elapsedTime);
                  }
               }

               if (invinciblePlayer.getTimePeriodEvents() != null) {
                  for (TimePeriodEvent event : invinciblePlayer.getTimePeriodEvents()) {
                     event.testAndExecute(playerPatch, this.elapsedTime);
                  }
               }
            }
         });
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void tick(LivingEntityPatch<?> entitypatch, CallbackInfo ci) {
      try {
         if (entitypatch == null || entitypatch.getOriginal() == null) {
            return;
         }

         if (((LivingEntity)entitypatch.getOriginal()).m_21023_((MobEffect)DMCEffects.STOP.get())) {
            ci.cancel();
            this.prevElapsedTime = this.elapsedTime;
         }
      } catch (Exception var4) {
      }
   }
}
