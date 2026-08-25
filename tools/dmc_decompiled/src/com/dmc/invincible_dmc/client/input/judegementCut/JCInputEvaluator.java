package com.dmc.invincible_dmc.client.input.judegementCut;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;

@OnlyIn(Dist.CLIENT)
public final class JCInputEvaluator {
   public static boolean evaluateSheathPerfect(
      boolean isCharging,
      ComboNode activeNode,
      DynamicAnimation currentAnim,
      float rawElapsed,
      float animProgress,
      DynamicAnimation chargeStartAnim,
      float chargeStartAnimProgress
   ) {
      if (isCharging && currentAnim != null) {
         StaticAnimation realAnim = JudgementCutAnimationHelper.getRegisteredStaticAnimation(currentAnim);
         float[] animWindow = realAnim != null ? YamatoAttackAnimation.getJcPerfWindow(realAnim) : null;
         float w1s;
         float w1e;
         boolean isTimeMode;
         if (animWindow != null && animWindow[0] < animWindow[1]) {
            w1s = animWindow[0];
            w1e = animWindow[1];
            isTimeMode = true;
         } else if (realAnim != null) {
            float[] animWindowRatio = YamatoAttackAnimation.getJcPerfWindowRatio(realAnim);
            if (animWindowRatio != null && animWindowRatio[0] < animWindowRatio[1]) {
               w1s = animWindowRatio[0];
               w1e = animWindowRatio[1];
               isTimeMode = false;
            } else {
               if (activeNode == null) {
                  return false;
               }

               w1s = JudgementCutAnimationHelper.getPerfectWindowStart(activeNode);
               w1e = JudgementCutAnimationHelper.getPerfectWindowEnd(activeNode);
               if (w1s < 0.0F || w1e < w1s) {
                  return false;
               }

               isTimeMode = false;
            }
         } else {
            if (activeNode == null) {
               return false;
            }

            w1s = JudgementCutAnimationHelper.getPerfectWindowStart(activeNode);
            w1e = JudgementCutAnimationHelper.getPerfectWindowEnd(activeNode);
            if (w1s < 0.0F || w1e < w1s) {
               return false;
            }

            isTimeMode = false;
         }

         boolean isSameAnimation = DMCAnimationUtils.sameAnimation(chargeStartAnim, currentAnim);
         boolean pressedBeforeWindow;
         boolean isCurrentlyInWindow;
         if (isTimeMode) {
            float chargeStartElapsed = chargeStartAnimProgress * chargeStartAnim.getTotalTime();
            pressedBeforeWindow = !isSameAnimation || chargeStartElapsed < w1s;
            isCurrentlyInWindow = rawElapsed >= w1s && rawElapsed <= w1e;
         } else {
            pressedBeforeWindow = !isSameAnimation || chargeStartAnimProgress < w1s;
            isCurrentlyInWindow = animProgress >= w1s && animProgress <= w1e;
         }

         return pressedBeforeWindow && isCurrentlyInWindow;
      } else {
         return false;
      }
   }

   public static boolean evaluateJustReleasePerfect(boolean isCharging, long elapsed, long needed, long windowMs) {
      return isCharging && elapsed >= needed && elapsed - needed <= windowMs;
   }
}
