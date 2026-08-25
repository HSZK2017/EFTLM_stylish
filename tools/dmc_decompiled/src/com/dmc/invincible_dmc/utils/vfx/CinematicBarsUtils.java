package com.dmc.invincible_dmc.utils.vfx;

import com.dmc.invincible_dmc.DMConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;

public final class CinematicBarsUtils {
   private static final float DEFAULT_OPEN_SPEED = 3.0F;
   private static final float DEFAULT_CLOSE_SPEED = 4.0F;
   private static final float DEFAULT_BAR_HEIGHT_RATIO = 0.08F;
   private static final CinematicBarsUtils INSTANCE = new CinematicBarsUtils();
   private long lastRenderTime;
   private float letterboxAnim;
   private float targetLetterbox;
   private float openSpeed = 3.0F;
   private float closeSpeed = 4.0F;
   private float barHeightRatio = 0.08F;
   private long openTime;
   private long durationMs;
   private long pauseStartMs;
   private long totalPauseMs;
   private boolean commandBarsEnabled;

   private CinematicBarsUtils() {
   }

   public static float getAnimProgress() {
      return prepareRead() ? INSTANCE.letterboxAnim : 0.0F;
   }

   public static float getTargetLetterbox() {
      return prepareRead() ? INSTANCE.targetLetterbox : 0.0F;
   }

   public static float getRenderedHeightRatio() {
      return prepareRead() ? INSTANCE.barHeightRatio * easeOutCubic(INSTANCE.letterboxAnim) : 0.0F;
   }

   public static boolean isCommandBarsEnabled() {
      return prepareRead() && INSTANCE.commandBarsEnabled;
   }

   public static void updateAnimation() {
      if (prepareRead()) {
         INSTANCE.updateAnimationState();
      }
   }

   private static boolean prepareRead() {
      if ((Boolean)DMConfig.CINEMATIC_BARS_ENABLED.get()) {
         return true;
      } else {
         clearDisabledState();
         return false;
      }
   }

   private static boolean rejectOpenWhenDisabled() {
      return !prepareRead();
   }

   private static void clearDisabledState() {
      if (INSTANCE.commandBarsEnabled || INSTANCE.letterboxAnim > 0.0F || INSTANCE.targetLetterbox > 0.0F || INSTANCE.durationMs > 0L || INSTANCE.openTime > 0L
         )
       {
         forceClose();
      }
   }

   private void initiateClose(float speed) {
      if (!this.commandBarsEnabled) {
         this.initiateCloseForced(speed);
      }
   }

   private void initiateCloseForced(float speed) {
      this.durationMs = 0L;
      this.closeSpeed = speed;
      this.targetLetterbox = 0.0F;
      this.totalPauseMs = 0L;
      CameraFovUtil.onCinematicBarsClosing(speed);
   }

   public static void open() {
      if (!rejectOpenWhenDisabled() && !INSTANCE.commandBarsEnabled) {
         open(3.0F, 4.0F, INSTANCE.barHeightRatio);
      }
   }

   public static void open(float barHeightRatio) {
      if (!rejectOpenWhenDisabled() && !INSTANCE.commandBarsEnabled) {
         open(INSTANCE.openSpeed, INSTANCE.closeSpeed, barHeightRatio);
      }
   }

   public static void open(float openSpeed, float closeSpeed, float barHeightRatio) {
      if (!rejectOpenWhenDisabled() && !INSTANCE.commandBarsEnabled) {
         INSTANCE.durationMs = 0L;
         INSTANCE.openSpeed = openSpeed;
         INSTANCE.closeSpeed = closeSpeed;
         INSTANCE.barHeightRatio = barHeightRatio;
         INSTANCE.targetLetterbox = 1.0F;
         INSTANCE.openTime = Util.m_137550_();
         INSTANCE.totalPauseMs = 0L;
      }
   }

   public static void openFor(float durationSeconds) {
      if (!rejectOpenWhenDisabled() && !INSTANCE.commandBarsEnabled) {
         openFor(durationSeconds, INSTANCE.openSpeed, INSTANCE.closeSpeed, INSTANCE.barHeightRatio);
      }
   }

   public static void openFor(float durationSeconds, float barHeightRatio) {
      if (!rejectOpenWhenDisabled() && !INSTANCE.commandBarsEnabled) {
         openFor(durationSeconds, INSTANCE.openSpeed, INSTANCE.closeSpeed, barHeightRatio);
      }
   }

   public static void openFor(float durationSeconds, float openSpeed, float closeSpeed, float barHeightRatio) {
      if (!rejectOpenWhenDisabled() && !INSTANCE.commandBarsEnabled) {
         INSTANCE.durationMs = (long)(durationSeconds * 1000.0F);
         INSTANCE.openSpeed = openSpeed;
         INSTANCE.closeSpeed = closeSpeed;
         INSTANCE.barHeightRatio = barHeightRatio;
         INSTANCE.targetLetterbox = 1.0F;
         INSTANCE.openTime = Util.m_137550_();
         INSTANCE.totalPauseMs = 0L;
      }
   }

   public static void close() {
      INSTANCE.initiateClose(INSTANCE.closeSpeed);
   }

   public static void close(float closeSpeed) {
      INSTANCE.initiateClose(closeSpeed);
   }

   public static void forceOpen() {
      if (!rejectOpenWhenDisabled()) {
         INSTANCE.durationMs = 0L;
         INSTANCE.letterboxAnim = 1.0F;
         INSTANCE.targetLetterbox = 1.0F;
         INSTANCE.lastRenderTime = 0L;
         INSTANCE.openTime = 0L;
         INSTANCE.pauseStartMs = 0L;
         INSTANCE.totalPauseMs = 0L;
      }
   }

   public static void forceClose() {
      INSTANCE.commandBarsEnabled = false;
      INSTANCE.durationMs = 0L;
      INSTANCE.letterboxAnim = 0.0F;
      INSTANCE.targetLetterbox = 0.0F;
      INSTANCE.lastRenderTime = 0L;
      INSTANCE.openTime = 0L;
      INSTANCE.pauseStartMs = 0L;
      INSTANCE.totalPauseMs = 0L;
      CameraFovUtil.onCinematicBarsClosing(999.0F);
   }

   public static void enableCommandBars(float barHeightRatio) {
      if (!rejectOpenWhenDisabled()) {
         INSTANCE.commandBarsEnabled = true;
         INSTANCE.durationMs = 0L;
         INSTANCE.barHeightRatio = barHeightRatio;
         INSTANCE.targetLetterbox = 1.0F;
         INSTANCE.openTime = Util.m_137550_();
         INSTANCE.totalPauseMs = 0L;
      }
   }

   public static void disableCommandBars() {
      INSTANCE.commandBarsEnabled = false;
      INSTANCE.initiateCloseForced(INSTANCE.closeSpeed);
   }

   public static void resetConfig() {
      INSTANCE.openSpeed = 3.0F;
      INSTANCE.closeSpeed = 4.0F;
      INSTANCE.barHeightRatio = 0.08F;
   }

   public static boolean isVisible() {
      return prepareRead() && INSTANCE.letterboxAnim > 0.001F;
   }

   public static boolean isAnimating() {
      return prepareRead() && Math.abs(INSTANCE.letterboxAnim - INSTANCE.targetLetterbox) > 0.001F;
   }

   public static float getAnimProgressStatic() {
      return getAnimProgress();
   }

   private void updateAnimationState() {
      if (Minecraft.m_91087_().m_91104_()) {
         if (this.pauseStartMs == 0L) {
            this.pauseStartMs = Util.m_137550_();
         }
      } else {
         if (this.pauseStartMs > 0L) {
            this.totalPauseMs = this.totalPauseMs + (Util.m_137550_() - this.pauseStartMs);
            this.pauseStartMs = 0L;
         }

         long now = Util.m_137550_();
         if (this.lastRenderTime == 0L) {
            this.lastRenderTime = now;
         }

         float deltaSeconds = Math.min((float)(now - this.lastRenderTime) / 1000.0F, 0.1F);
         this.lastRenderTime = now;
         if (this.durationMs > 0L && this.targetLetterbox == 1.0F && this.openTime > 0L && now - this.openTime - this.totalPauseMs >= this.durationMs) {
            this.initiateClose(this.closeSpeed);
         }

         float speed = this.targetLetterbox > this.letterboxAnim ? this.openSpeed : this.closeSpeed;
         this.letterboxAnim = step(this.letterboxAnim, this.targetLetterbox, speed, deltaSeconds);
      }
   }

   private static float step(float current, float target, float speedPerSecond, float deltaSeconds) {
      float amount = speedPerSecond * deltaSeconds;
      if (current < target) {
         return Math.min(current + amount, target);
      } else {
         return current > target ? Math.max(current - amount, target) : current;
      }
   }

   private static float easeOutCubic(float value) {
      float inverse = 1.0F - value;
      return 1.0F - inverse * inverse * inverse;
   }
}
