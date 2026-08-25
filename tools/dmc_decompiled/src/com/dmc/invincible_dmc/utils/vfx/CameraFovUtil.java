package com.dmc.invincible_dmc.utils.vfx;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT},
   bus = Bus.FORGE
)
public class CameraFovUtil {
   private static boolean isActive = false;
   private static float currentTick = 0.0F;
   private static float prevTick = 0.0F;
   private static int fadeInTicks = 0;
   private static int sustainTicks = 0;
   private static int fadeOutTicks = 0;
   private static float targetFovScale = 1.0F;
   private static float startFovScale = 1.0F;
   private static int currentPriority = 0;
   private static CameraFovUtil.EaseType easeInType = CameraFovUtil.EaseType.SINE;
   private static CameraFovUtil.EaseType easeOutType = CameraFovUtil.EaseType.SINE;
   private static boolean isCinematicLinked = false;
   private static boolean isLinkedToCinematic = false;

   public static void triggerSlowInInstantOut(int fadeInDuration, int sustainDuration, float targetScale, int priority) {
      triggerZoom(fadeInDuration, sustainDuration, 1, targetScale, CameraFovUtil.EaseType.SLOW_IN, CameraFovUtil.EaseType.INSTANT, priority);
   }

   public static void triggerCinematicLinkedZoom(float targetScale, CameraFovUtil.EaseType easeType, int priority) {
      if (!isActive && !isCinematicLinked || priority >= currentPriority) {
         isActive = false;
         isLinkedToCinematic = false;
         isCinematicLinked = true;
         targetFovScale = targetScale;
         easeInType = easeType;
         currentPriority = priority;
      }
   }

   public static void triggerCinematicLinkedTickZoom(
      int fadeIn, int fadeOut, float targetScale, CameraFovUtil.EaseType easeIn, CameraFovUtil.EaseType easeOut, int priority
   ) {
      triggerZoom(fadeIn, -1, fadeOut, targetScale, easeIn, easeOut, priority);
      isLinkedToCinematic = true;
   }

   public static void triggerZoom(int fadeIn, int sustain, int fadeOut, float targetScale) {
      triggerZoom(fadeIn, sustain, fadeOut, targetScale, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.SINE, 1);
   }

   public static void triggerZoom(
      int fadeIn, int sustain, int fadeOut, float targetScale, CameraFovUtil.EaseType easeIn, CameraFovUtil.EaseType easeOut, int priority
   ) {
      if (!isActive && !isCinematicLinked || priority >= currentPriority) {
         isCinematicLinked = false;
         isLinkedToCinematic = false;
         if (isActive) {
            startFovScale = getCurrentRenderFov(Minecraft.m_91087_().m_91296_());
         } else {
            startFovScale = 1.0F;
         }

         fadeInTicks = Math.max(0, fadeIn);
         sustainTicks = sustain;
         fadeOutTicks = Math.max(0, fadeOut);
         targetFovScale = targetScale;
         easeInType = easeIn;
         easeOutType = easeOut;
         currentPriority = priority;
         currentTick = 0.0F;
         prevTick = 0.0F;
         isActive = true;
      }
   }

   public static void onCinematicBarsClosing(float closeSpeed) {
      if (!isCinematicLinked) {
         if (isActive && isLinkedToCinematic && sustainTicks == -1) {
            if (easeOutType == CameraFovUtil.EaseType.INSTANT) {
               fadeOutTicks = 1;
            } else {
               float currentProgress = CinematicBarsUtils.getAnimProgress();
               float remainingSeconds = currentProgress / closeSpeed;
               fadeOutTicks = Math.max(1, Math.round(remainingSeconds * 20.0F));
            }

            sustainTicks = 0;
            currentTick = (float)fadeInTicks;
            prevTick = (float)fadeInTicks;
         }
      }
   }

   public static void stopZoom() {
      if (isActive || isCinematicLinked) {
         triggerZoom(0, 0, 1, 1.0F, CameraFovUtil.EaseType.LINEAR, CameraFovUtil.EaseType.INSTANT, 999);
      }
   }

   public static void fadeOut() {
      if (isActive || isCinematicLinked) {
         float currentFovScale = getCurrentRenderFov(Minecraft.m_91087_().m_91296_());
         int duration = Math.max(5, fadeOutTicks);
         isCinematicLinked = false;
         isLinkedToCinematic = false;
         startFovScale = currentFovScale;
         targetFovScale = currentFovScale;
         fadeInTicks = 0;
         sustainTicks = 0;
         fadeOutTicks = duration;
         easeInType = CameraFovUtil.EaseType.LINEAR;
         easeOutType = CameraFovUtil.EaseType.SINE;
         currentTick = 0.0F;
         prevTick = 0.0F;
         isActive = true;
      }
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END && isActive) {
         if (Minecraft.m_91087_().m_91104_()) {
            return;
         }

         prevTick = currentTick;
         if (sustainTicks == -1) {
            currentTick = (float)fadeInTicks;
         } else {
            currentTick++;
            int totalDuration = fadeInTicks + sustainTicks + fadeOutTicks;
            if (currentTick >= (float)totalDuration) {
               isActive = false;
               isLinkedToCinematic = false;
               currentPriority = 0;
            }
         }
      }
   }

   @SubscribeEvent
   public static void onComputeFov(ComputeFovModifierEvent event) {
      if (isCinematicLinked) {
         float progress = CinematicBarsUtils.getAnimProgress();
         if (progress <= 0.001F && !CinematicBarsUtils.isAnimating()) {
            isCinematicLinked = false;
            currentPriority = 0;
            return;
         }

         float ease = applyEasing(easeInType, progress);
         float fovModifier = Mth.m_14179_(ease, 1.0F, targetFovScale);
         event.setNewFovModifier(event.getNewFovModifier() * fovModifier);
      } else if (isActive) {
         float partialTick = Minecraft.m_91087_().m_91296_();
         float interpolatedTick = Mth.m_14179_(partialTick, prevTick, currentTick);
         float fovModifier = calculateFovAt(interpolatedTick);
         event.setNewFovModifier(event.getNewFovModifier() * fovModifier);
      }
   }

   private static float calculateFovAt(float tick) {
      if (tick <= (float)fadeInTicks) {
         float progress = fadeInTicks > 0 ? tick / (float)fadeInTicks : 1.0F;
         float ease = applyEasing(easeInType, progress);
         return Mth.m_14179_(ease, startFovScale, targetFovScale);
      } else if (sustainTicks != -1 && !(tick <= (float)(fadeInTicks + sustainTicks))) {
         int fadeOutStart = fadeInTicks + (sustainTicks == -1 ? 0 : sustainTicks);
         float progress = fadeOutTicks > 0 ? (tick - (float)fadeOutStart) / (float)fadeOutTicks : 1.0F;
         float ease = applyEasing(easeOutType, progress);
         return Mth.m_14179_(ease, targetFovScale, 1.0F);
      } else {
         return targetFovScale;
      }
   }

   private static float getCurrentRenderFov(float partialTick) {
      if (isCinematicLinked) {
         float ease = applyEasing(easeInType, CinematicBarsUtils.getAnimProgress());
         return Mth.m_14179_(ease, 1.0F, targetFovScale);
      } else {
         float interpolatedTick = Mth.m_14179_(partialTick, prevTick, currentTick);
         return calculateFovAt(interpolatedTick);
      }
   }

   private static float applyEasing(CameraFovUtil.EaseType type, float x) {
      x = Mth.m_14036_(x, 0.0F, 1.0F);

      return switch (type) {
         case LINEAR -> x;
         case SINE -> 0.5F - 0.5F * Mth.m_14089_(x * (float) Math.PI);
         case EXPO_OUT -> x == 1.0F ? 1.0F : 1.0F - (float)Math.pow(2.0, (double)(-10.0F * x));
         case EXPO_IN -> x == 0.0F ? 0.0F : (float)Math.pow(2.0, (double)(10.0F * x - 10.0F));
         case BACK_OUT -> {
            float c1 = 1.70158F;
            float c3 = c1 + 1.0F;
            yield 1.0F + c3 * (float)Math.pow((double)(x - 1.0F), 3.0) + c1 * (float)Math.pow((double)(x - 1.0F), 2.0);
         }
         case SLOW_IN -> x * x * x * x * x;
         case INSTANT -> x > 0.0F ? 1.0F : 0.0F;
      };
   }

   public static enum EaseType {
      LINEAR,
      SINE,
      EXPO_OUT,
      EXPO_IN,
      BACK_OUT,
      SLOW_IN,
      INSTANT;
   }
}
