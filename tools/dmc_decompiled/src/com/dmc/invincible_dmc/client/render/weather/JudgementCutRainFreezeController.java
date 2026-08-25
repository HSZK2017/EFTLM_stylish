package com.dmc.invincible_dmc.client.render.weather;

import com.dmc.invincible_dmc.api.animation.types.yamato.JudgementCutEndAnimation;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;

@OnlyIn(Dist.CLIENT)
public final class JudgementCutRainFreezeController {
   private static final double FREEZE_RADIUS = 96.0;
   private static final double FREEZE_RADIUS_SQR = 9216.0;
   private static ClientLevel trackedLevel;
   private static boolean frozen;
   private static double pausedTimeOffset;
   private static double freezeStartTime;
   private static double frozenVisualTime;

   private JudgementCutRainFreezeController() {
   }

   public static double updateVisualTime(ClientLevel level, Vec3 cameraPosition, double weatherTime) {
      if (trackedLevel != level) {
         trackedLevel = level;
         frozen = false;
         pausedTimeOffset = 0.0;
      }

      boolean shouldFreeze = shouldFreeze(level, cameraPosition);
      if (shouldFreeze) {
         if (!frozen) {
            frozen = true;
            freezeStartTime = weatherTime;
            frozenVisualTime = weatherTime - pausedTimeOffset;
         }

         return frozenVisualTime;
      } else {
         if (frozen) {
            pausedTimeOffset = pausedTimeOffset + (weatherTime - freezeStartTime);
            frozen = false;
         }

         return weatherTime - pausedTimeOffset;
      }
   }

   public static boolean shouldFreeze(ClientLevel level, Vec3 cameraPosition) {
      for (AbstractClientPlayer player : level.m_6907_()) {
         if (!(player.m_20238_(cameraPosition) > 9216.0)) {
            AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(player);
            if (animationPlayer != null
               && DMCAnimationUtils.getRealAnimation(animationPlayer) instanceof JudgementCutEndAnimation judgementCutEndAnimation
               && judgementCutEndAnimation.getProperty(JudgementCutEndAnimation.RAIN_FREEZE_TIME)
                  .filter(time -> time.isTimeInPairs(animationPlayer.getElapsedTime()))
                  .isPresent()) {
               return true;
            }
         }
      }

      return false;
   }
}
