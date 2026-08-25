package com.dmc.invincible_dmc.client.input.crazyCombo;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ICrazyComboNode;
import com.dmc.invincible_dmc.api.skill.SubComboNode;
import com.dmc.invincible_dmc.api.skill.crazycombo.CrazyComboPhaseHelper;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public final class CrazyComboAnimationHelper {
   @Nullable
   public static DynamicAnimation getCurrentAnimation(@Nullable LocalPlayerPatch lpp) {
      return getCurrentAnimation((LivingEntityPatch<?>)lpp);
   }

   @Nullable
   public static DynamicAnimation getCurrentAnimation(@Nullable LivingEntityPatch<?> patch) {
      return DMCAnimationUtils.resolveRealAnimation(DMCAnimationUtils.getCurrentAnimation(patch));
   }

   public static float getAnimationProgress(@Nullable LocalPlayerPatch lpp) {
      return getAnimationProgress((LivingEntityPatch<?>)lpp);
   }

   public static float getAnimationProgress(@Nullable LivingEntityPatch<?> patch) {
      return DMCAnimationUtils.getProgress(patch);
   }

   public static boolean isPlayingAnimation(@Nullable LocalPlayerPatch lpp, @Nullable StaticAnimation targetAnim) {
      return isPlayingAnimation((LivingEntityPatch<?>)lpp, targetAnim);
   }

   public static boolean isPlayingAnimation(@Nullable LivingEntityPatch<?> lpp, @Nullable StaticAnimation targetAnim) {
      return DMCAnimationUtils.isPlaying(lpp, targetAnim);
   }

   public static boolean isBaseAnimation(ComboNode node, DynamicAnimation currentAnim) {
      if (node.getAnimationAccessor() != null && DMCAnimationUtils.sameAnimation(DMCAnimationUtils.getAnimation(node.getAnimationAccessor()), currentAnim)) {
         return true;
      } else if (!(node instanceof ICrazyComboNode ccNode)) {
         return false;
      } else {
         SubComboNode base = ccNode.getCcBase();
         return base != null
            && base.getAnimationAccessor() != null
            && DMCAnimationUtils.sameAnimation(DMCAnimationUtils.getAnimation(base.getAnimationAccessor()), currentAnim);
      }
   }

   @Nullable
   public static StaticAnimation getChaseAnimation(ComboNode activeNode) {
      if (activeNode instanceof ICrazyComboNode ccNode) {
         SubComboNode chase = ccNode.getCcChase();
         if (chase != null && chase.getAnimationAccessor() != null) {
            return (StaticAnimation)chase.getAnimationAccessor().get();
         }
      }

      return null;
   }

   @Nullable
   public static StaticAnimation getFinishAnimation(ComboNode activeNode) {
      if (activeNode instanceof ICrazyComboNode ccNode) {
         ComboNode finish = ccNode.getCcFinish();
         if (finish == null) {
            finish = ccNode.getCcFinishNoChase();
         }

         if (finish != null && finish.getAnimationAccessor() != null) {
            return (StaticAnimation)finish.getAnimationAccessor().get();
         }
      }

      return null;
   }

   @Nullable
   public static StaticAnimation getFinishNoChaseAnimation(ComboNode activeNode) {
      if (activeNode instanceof ICrazyComboNode ccNode) {
         ComboNode finish = ccNode.getCcFinishNoChase();
         if (finish != null && finish.getAnimationAccessor() != null) {
            return (StaticAnimation)finish.getAnimationAccessor().get();
         }
      }

      return null;
   }

   public static boolean isFinishAnimation(ComboNode activeNode, DynamicAnimation currentAnimation) {
      if (activeNode instanceof ICrazyComboNode ccNode && currentAnimation != null && currentAnimation.getRegistryName() != null) {
         return ICrazyComboNode.matches(ccNode.getCcFinish(), currentAnimation.getRegistryName())
            || ICrazyComboNode.matches(ccNode.getCcFinishNoChase(), currentAnimation.getRegistryName());
      }

      return false;
   }

   public static int getCurrentPhaseOrder(@Nullable LivingEntityPatch<?> patch) {
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(patch);
      StaticAnimation realAnim = DMCAnimationUtils.getRealAnimation(patch);
      if (player != null && realAnim != null) {
         AttackAnimation attackAnim = DMCAnimationUtils.asAnimation(realAnim, AttackAnimation.class);
         return attackAnim != null ? attackAnim.getPhaseOrderByTime(player.getElapsedTime()) : -1;
      } else {
         return -1;
      }
   }

   public static int getCurrentPhaseCount(@Nullable LivingEntityPatch<?> patch) {
      return CrazyComboPhaseHelper.getCurrentPhaseCount(patch);
   }

   public static boolean isFinishPhaseGateOpen(@Nullable LivingEntityPatch<?> patch, ComboNode activeNode) {
      if (!(activeNode instanceof ICrazyComboNode ccNode)) {
         return false;
      } else {
         int minPhase = ccNode.getCcFinishMinPhase();
         int phaseOrder = getCurrentPhaseOrder(patch);
         return phaseOrder >= 0 && phaseOrder >= minPhase;
      }
   }
}
