package com.dmc.invincible_dmc.capability.yamato;

import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.capability.weapon.WeaponRuntimeState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class YamatoPlayerState {
   public static final YamatoPlayerState EMPTY = new YamatoPlayerState();
   private float concentration;
   private int concentrationTier;
   private float sdtValue;
   private boolean sdtActive;
   private int dtStack;
   private float dtResource;
   private final WeaponRuntimeState weaponRuntime = new WeaponRuntimeState();

   public float getConcentration() {
      return this.concentration;
   }

   public void setConcentration(float v) {
      this.concentration = Math.max(0.0F, Math.min(v, 10000.0F));
   }

   public int getConcentrationTier() {
      return this.concentrationTier;
   }

   public void setConcentrationTier(int value) {
      this.concentrationTier = Math.max(0, Math.min(value, 2));
   }

   public float getSdtValue() {
      return this.sdtValue;
   }

   public void setSdtValue(float v) {
      this.sdtValue = Math.max(0.0F, Math.min(v, 1000.0F));
   }

   public boolean isSdtActive() {
      return this.sdtActive;
   }

   public void setSdtActive(boolean value) {
      this.sdtActive = value;
   }

   public int getDtStack() {
      return this.dtStack;
   }

   public void setDtStack(int v) {
      this.dtStack = Math.max(0, v);
   }

   public float getDtResource() {
      return this.dtResource;
   }

   public void setDtResource(float v) {
      this.dtResource = Math.max(0.0F, v);
   }

   public DmcWeaponType getActiveWeapon() {
      return this.weaponRuntime.selectedWeapon();
   }

   public void setActiveWeapon(DmcWeaponType activeWeapon) {
      this.weaponRuntime.setSelectedWeapon(activeWeapon);
   }

   public DmcWeaponType getRenderedWeapon() {
      return this.weaponRuntime.presentedWeapon();
   }

   public void setRenderedWeapon(DmcWeaponType renderedWeapon) {
      this.weaponRuntime.setPresentedWeapon(renderedWeapon);
   }

   @Nullable
   public DmcWeaponType getPendingRenderedWeapon() {
      return this.weaponRuntime.pendingWeapon();
   }

   @Nullable
   public ResourceLocation getPendingRenderAnimation() {
      return this.weaponRuntime.pendingAnimation();
   }

   public long getPendingRenderBarrierSessionId() {
      return this.weaponRuntime.pendingBarrierSessionId();
   }

   public boolean isPendingEntryTransitionSuppressed() {
      return this.weaponRuntime.isPendingEntryTransitionSuppressed();
   }

   public void queueRenderedWeapon(DmcWeaponType weaponType, ResourceLocation animation) {
      this.queueRenderedWeapon(weaponType, animation, 0L);
   }

   public void queueRenderedWeapon(DmcWeaponType weaponType, @Nullable ResourceLocation animation, long barrierSessionId) {
      this.weaponRuntime.queuePresentation(weaponType, animation, barrierSessionId);
   }

   public void queueRenderedWeapon(DmcWeaponType weaponType, @Nullable ResourceLocation animation, long barrierSessionId, boolean suppressEntryTransition) {
      this.weaponRuntime.queuePresentation(weaponType, animation, barrierSessionId, suppressEntryTransition);
   }

   public void clearPendingRenderedWeapon() {
      this.weaponRuntime.clearPendingPresentation();
   }

   public boolean isLivingMotionRefreshPending() {
      return this.weaponRuntime.isLivingMotionRefreshPending();
   }

   public void markLivingMotionRefreshPending() {
      this.weaponRuntime.markLivingMotionRefreshPending();
   }

   public void clearLivingMotionRefreshPending() {
      this.weaponRuntime.clearLivingMotionRefreshPending();
   }

   public int getWeaponSwitchSequence() {
      return this.weaponRuntime.revision();
   }

   public void setWeaponSwitchSequence(int weaponSwitchSequence) {
      this.weaponRuntime.setRevision(weaponSwitchSequence);
   }

   public int advanceWeaponSwitchSequence() {
      return this.weaponRuntime.advanceRevision();
   }

   public CompoundTag saveNBTData(CompoundTag tag) {
      if (this.concentration != 0.0F) {
         tag.m_128350_("concentration", this.concentration);
      }

      if (this.concentrationTier != 0) {
         tag.m_128405_("concentrationTier", this.concentrationTier);
      }

      if (this.sdtValue != 0.0F) {
         tag.m_128350_("sdtValue", this.sdtValue);
      }

      if (this.dtStack != 0) {
         tag.m_128405_("dtStack", this.dtStack);
      }

      if (this.dtResource != 0.0F) {
         tag.m_128350_("dtResource", this.dtResource);
      }

      tag.m_128359_("activeWeapon", this.getActiveWeapon().serializedName());
      if (this.getWeaponSwitchSequence() != 0) {
         tag.m_128405_("weaponSwitchSequence", this.getWeaponSwitchSequence());
      }

      return tag;
   }

   public void loadNBTData(CompoundTag tag) {
      this.concentration = tag.m_128457_("concentration");
      this.concentrationTier = tag.m_128451_("concentrationTier");
      this.sdtValue = tag.m_128457_("sdtValue");
      this.sdtActive = false;
      this.dtStack = tag.m_128451_("dtStack");
      this.dtResource = tag.m_128457_("dtResource");
      this.weaponRuntime.restorePersistent(DmcWeaponType.bySerializedName(tag.m_128461_("activeWeapon")), tag.m_128451_("weaponSwitchSequence"));
   }

   public void copyFrom(YamatoPlayerState old) {
      this.concentration = old.concentration;
      this.concentrationTier = old.concentrationTier;
      this.sdtValue = old.sdtValue;
      this.sdtActive = false;
      this.dtStack = old.dtStack;
      this.dtResource = old.dtResource;
      this.weaponRuntime.restorePersistent(old.getActiveWeapon(), old.getWeaponSwitchSequence());
   }
}
