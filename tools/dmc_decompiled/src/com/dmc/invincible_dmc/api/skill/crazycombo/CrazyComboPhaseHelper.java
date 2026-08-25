package com.dmc.invincible_dmc.api.skill.crazycombo;

import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public final class CrazyComboPhaseHelper {
   private CrazyComboPhaseHelper() {
   }

   public static int getCurrentPhaseOrder(@Nullable LivingEntityPatch<?> patch) {
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(patch);
      AttackAnimation animation = getCurrentAttackAnimation(patch);
      return player != null && animation != null ? animation.getPhaseOrderByTime(player.getElapsedTime()) : -1;
   }

   public static int getCurrentPhaseCount(@Nullable LivingEntityPatch<?> patch) {
      AttackAnimation animation = getCurrentAttackAnimation(patch);
      return animation != null ? animation.phases.length : -1;
   }

   @Nullable
   private static AttackAnimation getCurrentAttackAnimation(@Nullable LivingEntityPatch<?> patch) {
      StaticAnimation realAnimation = DMCAnimationUtils.getRealAnimation(patch);
      return DMCAnimationUtils.asAnimation(realAnimation, AttackAnimation.class);
   }
}
