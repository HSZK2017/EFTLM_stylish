package com.dmc.invincible_dmc.capability.weapon;

import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class WeaponRuntimeState {
   private DmcWeaponType selectedWeapon = DmcWeaponType.YAMATO;
   private DmcWeaponType presentedWeapon = DmcWeaponType.YAMATO;
   @Nullable
   private DmcWeaponType pendingWeapon;
   @Nullable
   private ResourceLocation pendingAnimation;
   private long pendingBarrierSessionId;
   private boolean pendingEntryTransitionSuppressed;
   private boolean livingMotionRefreshPending;
   private int revision;

   public DmcWeaponType selectedWeapon() {
      return this.selectedWeapon;
   }

   public void setSelectedWeapon(DmcWeaponType weapon) {
      this.selectedWeapon = weapon == null ? DmcWeaponType.YAMATO : weapon;
   }

   public DmcWeaponType presentedWeapon() {
      return this.presentedWeapon;
   }

   public void setPresentedWeapon(DmcWeaponType weapon) {
      this.presentedWeapon = weapon == null ? DmcWeaponType.YAMATO : weapon;
   }

   @Nullable
   public DmcWeaponType pendingWeapon() {
      return this.pendingWeapon;
   }

   @Nullable
   public ResourceLocation pendingAnimation() {
      return this.pendingAnimation;
   }

   public long pendingBarrierSessionId() {
      return this.pendingBarrierSessionId;
   }

   public boolean isPendingEntryTransitionSuppressed() {
      return this.pendingEntryTransitionSuppressed;
   }

   public void queuePresentation(DmcWeaponType weapon, @Nullable ResourceLocation animation, long barrierSessionId) {
      this.queuePresentation(weapon, animation, barrierSessionId, false);
   }

   public void queuePresentation(DmcWeaponType weapon, @Nullable ResourceLocation animation, long barrierSessionId, boolean suppressEntryTransition) {
      this.pendingWeapon = weapon;
      this.pendingAnimation = animation;
      this.pendingBarrierSessionId = Math.max(0L, barrierSessionId);
      this.pendingEntryTransitionSuppressed = suppressEntryTransition;
   }

   public void clearPendingPresentation() {
      this.pendingWeapon = null;
      this.pendingAnimation = null;
      this.pendingBarrierSessionId = 0L;
      this.pendingEntryTransitionSuppressed = false;
   }

   public boolean isLivingMotionRefreshPending() {
      return this.livingMotionRefreshPending;
   }

   public void markLivingMotionRefreshPending() {
      this.livingMotionRefreshPending = true;
   }

   public void clearLivingMotionRefreshPending() {
      this.livingMotionRefreshPending = false;
   }

   public int revision() {
      return this.revision;
   }

   public void setRevision(int revision) {
      this.revision = Math.max(0, revision);
   }

   public int advanceRevision() {
      return ++this.revision;
   }

   public void restorePersistent(DmcWeaponType selectedWeapon, int revision) {
      this.setSelectedWeapon(selectedWeapon);
      this.setPresentedWeapon(selectedWeapon);
      this.clearPendingPresentation();
      this.clearLivingMotionRefreshPending();
      this.setRevision(revision);
   }
}
