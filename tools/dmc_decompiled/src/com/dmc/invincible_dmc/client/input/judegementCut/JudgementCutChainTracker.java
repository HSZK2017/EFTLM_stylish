package com.dmc.invincible_dmc.client.input.judegementCut;

import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.StaticAnimation;

@OnlyIn(Dist.CLIENT)
public final class JudgementCutChainTracker {
   private int chainCount = 0;
   private boolean firstWasPerfect = false;
   private boolean justFired = false;
   private StaticAnimation lastFiredAnim = null;

   public void startChain(boolean isPerfect, StaticAnimation firedAnim) {
      this.chainCount = 1;
      this.firstWasPerfect = isPerfect;
      this.justFired = true;
      this.lastFiredAnim = firedAnim;
   }

   public void advanceChain(StaticAnimation firedAnim) {
      this.chainCount++;
      this.justFired = true;
      this.lastFiredAnim = firedAnim;
   }

   public boolean canAdvance() {
      return this.chainCount < this.getMaxChain();
   }

   public int getMaxChain() {
      return this.firstWasPerfect ? 3 : 4;
   }

   public void reset() {
      this.chainCount = 0;
      this.firstWasPerfect = false;
      this.justFired = false;
      this.lastFiredAnim = null;
   }

   public int getChainCount() {
      return this.chainCount;
   }

   public boolean isFirstWasPerfect() {
      return this.firstWasPerfect;
   }

   public boolean isJustFired() {
      return this.justFired;
   }

   public void setJustFired(boolean justFired) {
      this.justFired = justFired;
   }

   @Nullable
   public StaticAnimation getLastFiredAnim() {
      return this.lastFiredAnim;
   }
}
