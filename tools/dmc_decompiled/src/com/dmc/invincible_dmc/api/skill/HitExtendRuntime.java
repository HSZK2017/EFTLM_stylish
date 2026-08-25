package com.dmc.invincible_dmc.api.skill;

import org.jetbrains.annotations.Nullable;

public final class HitExtendRuntime {
   @Nullable
   private IHitExtendNode activeNode;
   private int startTick;
   private boolean hitLatched;
   private int hitTick = -1;
   private int targetId = -1;

   public void begin(IHitExtendNode node, int startTick) {
      this.activeNode = node;
      this.startTick = startTick;
      this.clearHitLatch();
   }

   public void clear() {
      this.activeNode = null;
      this.startTick = 0;
      this.clearHitLatch();
   }

   public void copyFrom(HitExtendRuntime other) {
      this.activeNode = other.activeNode;
      this.startTick = other.startTick;
      this.clearHitLatch();
   }

   public boolean latchHit(int currentTick, int targetId) {
      if (this.activeNode != null && !this.hitLatched) {
         this.hitLatched = true;
         this.hitTick = currentTick;
         this.targetId = targetId;
         return true;
      } else {
         return false;
      }
   }

   public boolean isReady(int currentTick) {
      return this.activeNode != null && this.hitLatched && currentTick - this.startTick >= Math.max(0, this.activeNode.getMinimumHoldTicks());
   }

   public int heldTicks(int currentTick) {
      return this.activeNode == null ? 0 : Math.max(0, currentTick - this.startTick);
   }

   @Nullable
   public IHitExtendNode getActiveNode() {
      return this.activeNode;
   }

   public int getStartTick() {
      return this.startTick;
   }

   public boolean hasLatchedHit() {
      return this.hitLatched;
   }

   public int getHitTick() {
      return this.hitTick;
   }

   public int getTargetId() {
      return this.targetId;
   }

   private void clearHitLatch() {
      this.hitLatched = false;
      this.hitTick = -1;
      this.targetId = -1;
   }
}
