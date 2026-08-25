package com.dmc.invincible_dmc.client.input.judegementCut;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.IJudgementCutNode;
import com.dmc.invincible_dmc.api.skill.JudgementCutNode;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public final class JudgementCutAnimationHelper {
   @Nullable
   public static DynamicAnimation getCurrentAnimation(@Nullable LivingEntityPatch<?> lpp) {
      return DMCAnimationUtils.resolveRealAnimation(DMCAnimationUtils.getCurrentAnimation(lpp));
   }

   public static float getAnimationProgress(@Nullable LivingEntityPatch<?> lpp) {
      return DMCAnimationUtils.getProgress(lpp);
   }

   public static float getRawElapsedTime(@Nullable LivingEntityPatch<?> lpp) {
      return DMCAnimationUtils.getElapsedTime(lpp);
   }

   public static boolean isPlayingAnimation(@Nullable LivingEntityPatch<?> lpp, @Nullable StaticAnimation targetAnim) {
      return DMCAnimationUtils.isPlaying(lpp, targetAnim);
   }

   public static boolean isPlayingJudgementCutAnimation(@Nullable LivingEntityPatch<?> lpp) {
      return DMCAnimationUtils.isPlaying(
         lpp,
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND,
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND_FS,
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR,
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR_FS
      );
   }

   @Nullable
   public static StaticAnimation getRegisteredStaticAnimation(@Nullable DynamicAnimation animation) {
      DynamicAnimation realAnimation = DMCAnimationUtils.resolveRealAnimation(animation);
      StaticAnimation staticAnimation = DMCAnimationUtils.asAnimation(realAnimation, StaticAnimation.class);
      if (staticAnimation == null) {
         return null;
      } else {
         AssetAccessor<? extends DynamicAnimation> accessor = staticAnimation.getAccessor();
         return accessor != null ? staticAnimation : null;
      }
   }

   public static String getAnimationName(@Nullable DynamicAnimation animation) {
      return animation == null ? "null" : DMCAnimationUtils.describe(animation);
   }

   @Nullable
   public static StaticAnimation getJCTargetAnimation(@Nullable LocalPlayerPatch lpp, boolean isPerfect, boolean inAir) {
      if (lpp == null) {
         return null;
      } else if (!(lpp.getSkill(SkillSlots.WEAPON_INNATE).getSkill() instanceof VergilSkill vergilSkill)) {
         return null;
      } else {
         JudgementCutNode targetNode = vergilSkill.getTargetNode(isPerfect, inAir);
         return targetNode != null && targetNode.getAnimationAccessor() != null ? (StaticAnimation)targetNode.getAnimationAccessor().get() : null;
      }
   }

   public static float getPerfectWindowStart(ComboNode node) {
      return node instanceof IJudgementCutNode jc ? jc.getJcPerfWinStart() : -1.0F;
   }

   public static float getPerfectWindowEnd(ComboNode node) {
      return node instanceof IJudgementCutNode jc ? jc.getJcPerfWinEnd() : -1.0F;
   }
}
