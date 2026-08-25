package com.dmc.invincible_dmc.api.skill;

import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.world.damagesource.StunType;

public class SubJudgementCutNode extends SubComboNode implements IJudgementCutNode {
   private int jcChargeOverride = -1;
   private float jcPerfWinStart = -1.0F;
   private float jcPerfWinEnd = -1.0F;

   SubJudgementCutNode() {
   }

   public static SubJudgementCutNode create(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      SubJudgementCutNode node = new SubJudgementCutNode();
      node.animationAccessor = anim;
      return node;
   }

   @Override
   protected ComboNode createCopyInstance() {
      SubJudgementCutNode copy = new SubJudgementCutNode();
      copy.jcChargeOverride = this.jcChargeOverride;
      copy.jcPerfWinStart = this.jcPerfWinStart;
      copy.jcPerfWinEnd = this.jcPerfWinEnd;
      return copy;
   }

   @Override
   public ComboNode copyForBranching() {
      SubJudgementCutNode copy = (SubJudgementCutNode)super.copyForBranching();
      copy.jcChargeOverride = this.jcChargeOverride;
      copy.jcPerfWinStart = this.jcPerfWinStart;
      copy.jcPerfWinEnd = this.jcPerfWinEnd;
      return copy;
   }

   @Override
   public int getJcChargeOverride() {
      return this.jcChargeOverride;
   }

   @Override
   public float getJcPerfWinStart() {
      return this.jcPerfWinStart;
   }

   @Override
   public float getJcPerfWinEnd() {
      return this.jcPerfWinEnd;
   }

   public SubJudgementCutNode setJcChargeOverride(int ticks) {
      this.jcChargeOverride = ticks;
      return this;
   }

   public SubJudgementCutNode setJcChargeTime(int ms) {
      this.jcChargeOverride = ms;
      return this;
   }

   public SubJudgementCutNode setJcPerfWindow(float start, float end) {
      this.jcPerfWinStart = start;
      this.jcPerfWinEnd = end;
      return this;
   }

   public SubJudgementCutNode setAnimation(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      return (SubJudgementCutNode)super.setAnimation(anim);
   }

   public SubJudgementCutNode setNewPhase(int v) {
      return (SubJudgementCutNode)super.setNewPhase(v);
   }

   public SubJudgementCutNode setRepeatNode(boolean v) {
      return (SubJudgementCutNode)super.setRepeatNode(v);
   }

   public SubJudgementCutNode setAllowBuffer(boolean v) {
      return (SubJudgementCutNode)super.setAllowBuffer(v);
   }

   public SubJudgementCutNode setBufferDurationTicks(int v) {
      return (SubJudgementCutNode)super.setBufferDurationTicks(v);
   }

   public SubJudgementCutNode setAllowLongPress(boolean v) {
      return (SubJudgementCutNode)super.setAllowLongPress(v);
   }

   public SubJudgementCutNode setLongPressThresholdOverride(int v) {
      return (SubJudgementCutNode)super.setLongPressThresholdOverride(v);
   }

   public SubJudgementCutNode setArmorNegation(float v) {
      return (SubJudgementCutNode)super.setArmorNegation(v);
   }

   public SubJudgementCutNode setHurtDamageMultiplier(float v) {
      return (SubJudgementCutNode)super.setHurtDamageMultiplier(v);
   }

   public SubJudgementCutNode setDamageMultiplier(ValueModifier v) {
      return (SubJudgementCutNode)super.setDamageMultiplier(v);
   }

   public SubJudgementCutNode setImpactMultiplier(float v) {
      return (SubJudgementCutNode)super.setImpactMultiplier(v);
   }

   public SubJudgementCutNode setStunTypeModifier(StunType v) {
      return (SubJudgementCutNode)super.setStunTypeModifier(v);
   }

   public SubJudgementCutNode setCanBeInterrupt(boolean v) {
      return (SubJudgementCutNode)super.setCanBeInterrupt(v);
   }

   public SubJudgementCutNode setActionTag(ActionTag tag) {
      super.setActionTag(tag);
      return this;
   }

   public SubJudgementCutNode setPriority(int v) {
      return (SubJudgementCutNode)super.setPriority(v);
   }

   public SubJudgementCutNode setCooldown(int v) {
      return (SubJudgementCutNode)super.setCooldown(v);
   }

   public SubJudgementCutNode setComboResetTicks(int v) {
      return (SubJudgementCutNode)super.setComboResetTicks(v);
   }

   public SubJudgementCutNode setComboResetAtTime(float v) {
      return (SubJudgementCutNode)super.setComboResetAtTime(v);
   }

   public SubJudgementCutNode setIsAutoResetByMove(boolean v) {
      return (SubJudgementCutNode)super.setIsAutoResetByMove(v);
   }

   public SubJudgementCutNode setNotCharge(boolean v) {
      return (SubJudgementCutNode)super.setNotCharge(v);
   }

   public SubJudgementCutNode setPlaySpeed(float v) {
      return (SubJudgementCutNode)super.setPlaySpeed(v);
   }

   public SubJudgementCutNode setConvertTime(float v) {
      return (SubJudgementCutNode)super.setConvertTime(v);
   }
}
