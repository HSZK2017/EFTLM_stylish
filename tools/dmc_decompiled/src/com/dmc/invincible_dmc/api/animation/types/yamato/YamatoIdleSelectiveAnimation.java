package com.dmc.invincible_dmc.api.animation.types.yamato;

import com.dmc.invincible_dmc.gameassets.DMCAnimationVariableKeys;
import com.dmc.invincible_dmc.gameassets.DMCWeaponCategories;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationVariables;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationVariables.IndependentAnimationVariableKey;
import yesman.epicfight.api.animation.types.SelectiveAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class YamatoIdleSelectiveAnimation extends SelectiveAnimation {
   private static final int SECONDARY_IDLE_DELAY_TICKS = 200;
   private static final int DEFAULT_IDLE_STATE = 0;
   private static final int SECONDARY_IDLE_START_STATE = 1;
   private static final int SECONDARY_IDLE_STATE = 2;
   private static final int TERTIARY_IDLE_STATE = 3;
   private static final IndependentAnimationVariableKey<Integer> IDLE_TICKS = AnimationVariables.independent(animator -> 0, true);
   private static final IndependentAnimationVariableKey<Integer> IDLE_VARIANT_SEQUENCE = AnimationVariables.independent(animator -> 0, true);
   private final AnimationAccessor<? extends SelectiveAnimation> selectorAccessor;
   private final AssetAccessor<? extends StaticAnimation> secondaryIdleStart;

   public YamatoIdleSelectiveAnimation(
      AnimationAccessor<? extends SelectiveAnimation> accessor,
      AssetAccessor<? extends StaticAnimation> defaultIdle,
      AssetAccessor<? extends StaticAnimation> secondaryIdleStart,
      AssetAccessor<? extends StaticAnimation> secondaryIdle,
      AssetAccessor<? extends StaticAnimation> tertiaryIdle
   ) {
      super(
         patch -> selectState(patch, accessor, defaultIdle, secondaryIdleStart, tertiaryIdle),
         accessor,
         new AssetAccessor[]{defaultIdle, secondaryIdleStart, secondaryIdle, tertiaryIdle}
      );
      this.selectorAccessor = accessor;
      this.secondaryIdleStart = secondaryIdleStart;
   }

   public void begin(LivingEntityPatch<?> patch) {
      if (!this.isCompletingSecondaryIdleStart(patch)) {
         resetState(patch, this.selectorAccessor, isIdleStateAuthority(patch));
      }

      super.begin(patch);
   }

   private boolean isCompletingSecondaryIdleStart(LivingEntityPatch<?> patch) {
      int previousState = (Integer)patch.getAnimator().getVariables().getOrDefault(PREVIOUS_STATE, this.selectorAccessor);
      return previousState == 1 && isStandingYamatoIdle(patch)
         ? patch.getAnimator().getPlayer(this.secondaryIdleStart).<Boolean>map(AnimationPlayer::isEnd).orElse(false)
         : false;
   }

   private static int selectState(
      LivingEntityPatch<?> patch,
      AnimationAccessor<? extends SelectiveAnimation> accessor,
      AssetAccessor<? extends StaticAnimation> defaultIdle,
      AssetAccessor<? extends StaticAnimation> secondaryIdleStart,
      AssetAccessor<? extends StaticAnimation> tertiaryIdle
   ) {
      if (!isStandingYamatoIdle(patch)) {
         resetState(patch, accessor, isIdleStateAuthority(patch));
         return 0;
      } else if (!isIdleStateAuthority(patch)) {
         return (Integer)patch.getAnimator()
            .getVariables()
            .getOrDefault((IndependentAnimationVariableKey)DMCAnimationVariableKeys.YAMATO_IDLE_STATE.get(), accessor);
      } else {
         int previousState = (Integer)patch.getAnimator().getVariables().getOrDefault(PREVIOUS_STATE, accessor);
         if (previousState == 1) {
            int state = patch.getAnimator().getPlayer(secondaryIdleStart).filter(player -> !player.isEnd()).map(player -> 1).orElse(2);
            syncIdleState(patch, accessor, state);
            return state;
         } else if (previousState == 2) {
            return 2;
         } else if (previousState == 3) {
            boolean tertiaryIdlePlaying = patch.getAnimator().getPlayer(tertiaryIdle).filter(player -> !player.isEnd()).isPresent();
            if (tertiaryIdlePlaying) {
               return 3;
            } else {
               patch.getAnimator().getVariables().put(IDLE_TICKS, accessor, 0);
               syncIdleState(patch, accessor, 0);
               return 0;
            }
         } else {
            boolean defaultIdlePlaying = patch.getAnimator().getPlayer(defaultIdle).filter(player -> !player.isEnd()).isPresent();
            if (!defaultIdlePlaying) {
               return 0;
            } else {
               int idleTicks = (Integer)patch.getAnimator().getVariables().getOrDefault(IDLE_TICKS, accessor) + 1;
               patch.getAnimator().getVariables().put(IDLE_TICKS, accessor, idleTicks);
               if (idleTicks < 200) {
                  return 0;
               } else {
                  int sequence = (Integer)patch.getAnimator().getVariables().getOrDefault(IDLE_VARIANT_SEQUENCE, accessor) + 1;
                  patch.getAnimator().getVariables().put(IDLE_VARIANT_SEQUENCE, accessor, sequence);
                  int state = useTertiaryIdle(patch, sequence) ? 3 : 1;
                  syncIdleState(patch, accessor, state);
                  return state;
               }
            }
         }
      }
   }

   private static boolean useTertiaryIdle(LivingEntityPatch<?> patch, int sequence) {
      long value = ((LivingEntity)patch.getOriginal()).m_20148_().getMostSignificantBits()
         ^ Long.rotateLeft(((LivingEntity)patch.getOriginal()).m_20148_().getLeastSignificantBits(), 21)
         ^ (long)sequence * -7046029254386353131L;
      value ^= value >>> 33;
      value *= -49064778989728563L;
      value ^= value >>> 33;
      return (value & 1L) == 0L;
   }

   private static boolean isStandingYamatoIdle(LivingEntityPatch<?> patch) {
      if (patch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() != DMCWeaponCategories.DMC5_YAMATO) {
         return false;
      } else if (patch.getCurrentLivingMotion() == LivingMotions.IDLE
         && patch.currentCompositeMotion == LivingMotions.IDLE
         && !patch.getEntityState().inaction()
         && ((LivingEntity)patch.getOriginal()).m_20096_()
         && !((LivingEntity)patch.getOriginal()).m_6047_()) {
         if (patch instanceof PlayerPatch<?> playerPatch && (Math.abs(playerPatch.dx) > 0.01 || Math.abs(playerPatch.dz) > 0.01)) {
            return false;
         }

         return ((LivingEntity)patch.getOriginal()).m_20184_().m_165925_() <= 1.0E-4;
      } else {
         return false;
      }
   }

   private static boolean isIdleStateAuthority(LivingEntityPatch<?> patch) {
      if (patch.getOriginal() instanceof Player player && player.m_7578_()) {
         return true;
      }

      return false;
   }

   private static void syncIdleState(LivingEntityPatch<?> patch, AnimationAccessor<? extends SelectiveAnimation> accessor, int state) {
      int current = (Integer)patch.getAnimator()
         .getVariables()
         .getOrDefault((IndependentAnimationVariableKey)DMCAnimationVariableKeys.YAMATO_IDLE_STATE.get(), accessor);
      if (current != state) {
         patch.getAnimator().getVariables().put((IndependentAnimationVariableKey)DMCAnimationVariableKeys.YAMATO_IDLE_STATE.get(), accessor, state);
      }
   }

   private static void resetState(LivingEntityPatch<?> patch, AnimationAccessor<? extends SelectiveAnimation> accessor, boolean synchronize) {
      patch.getAnimator().getVariables().put(IDLE_TICKS, accessor, 0);
      patch.getAnimator().getVariables().put(PREVIOUS_STATE, accessor, 0);
      if (synchronize) {
         syncIdleState(patch, accessor, 0);
      }
   }
}
