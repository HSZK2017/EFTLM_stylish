package com.dmc.invincible_dmc.api.skill;

import com.dmc.invincible_dmc.api.events.Side;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

public class JudgementCutNode extends ComboNode implements IJudgementCutNode {
   private int jcChargeOverride = -1;
   private float jcPerfWinStart = -1.0F;
   private float jcPerfWinEnd = -1.0F;

   JudgementCutNode() {
   }

   public static JudgementCutNode create(@Nullable SubComboNode template) {
      JudgementCutNode node = new JudgementCutNode();
      if (template != null) {
         node.animationAccessor = template.getAnimationAccessor();
         node.playSpeed = template.getPlaySpeed();
         node.convertTime = template.getConvertTime();
         node.notCharge = template.isNotCharge();
         node.repeatNode = template.isRepeatNode();
         node.allowBuffer = template.isAllowBuffer();
         node.bufferDurationTicks = template.getBufferDurationTicks();
         node.allowLongPress = template.isAllowLongPress();
         node.longPressThresholdOverride = template.getLongPressThresholdOverride();
         node.comboResetTicks = template.getComboResetTicks();
         node.comboResetTime = template.getComboResetTime();
         node.isAutoResetByMove = template.isAutoResetByMove();
      }

      return node;
   }

   public static JudgementCutNode createNode(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      JudgementCutNode node = new JudgementCutNode();
      node.animationAccessor = anim;
      return node;
   }

   @Override
   protected ComboNode createCopyInstance() {
      JudgementCutNode copy = new JudgementCutNode();
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

   public JudgementCutNode setJcPerfWindow(float start, float end) {
      this.jcPerfWinStart = start;
      this.jcPerfWinEnd = end;
      return this;
   }

   public JudgementCutNode setJcChargeTime(int ms) {
      this.jcChargeOverride = ms;
      return this;
   }

   public JudgementCutNode setNewPhase(int v) {
      return (JudgementCutNode)super.setNewPhase(v);
   }

   public JudgementCutNode setRepeatNode(boolean v) {
      return (JudgementCutNode)super.setRepeatNode(v);
   }

   public JudgementCutNode setAllowBuffer(boolean v) {
      return (JudgementCutNode)super.setAllowBuffer(v);
   }

   public JudgementCutNode setAllowLongPress(boolean v) {
      return (JudgementCutNode)super.setAllowLongPress(v);
   }

   public JudgementCutNode setLongPressThresholdOverride(int v) {
      return (JudgementCutNode)super.setLongPressThresholdOverride(v);
   }

   public JudgementCutNode setArmorNegation(float v) {
      return (JudgementCutNode)super.setArmorNegation(v);
   }

   public JudgementCutNode setHurtDamageMultiplier(float v) {
      return (JudgementCutNode)super.setHurtDamageMultiplier(v);
   }

   public JudgementCutNode setDamageMultiplier(ValueModifier v) {
      return (JudgementCutNode)super.setDamageMultiplier(v);
   }

   public JudgementCutNode setImpactMultiplier(float v) {
      return (JudgementCutNode)super.setImpactMultiplier(v);
   }

   public JudgementCutNode setStunTypeModifier(StunType v) {
      return (JudgementCutNode)super.setStunTypeModifier(v);
   }

   public JudgementCutNode setCanBeInterrupt(boolean v) {
      return (JudgementCutNode)super.setCanBeInterrupt(v);
   }

   public JudgementCutNode setPriority(int v) {
      return (JudgementCutNode)super.setPriority(v);
   }

   public JudgementCutNode setCooldown(int v) {
      return (JudgementCutNode)super.setCooldown(v);
   }

   public JudgementCutNode setComboResetTicks(int v) {
      return (JudgementCutNode)super.setComboResetTicks(v);
   }

   public JudgementCutNode setComboResetAtTime(float v) {
      return (JudgementCutNode)super.setComboResetAtTime(v);
   }

   public JudgementCutNode setComboResetAtAnimTime(float v) {
      return (JudgementCutNode)super.setComboResetAtAnimTime(v);
   }

   public JudgementCutNode setIsAutoResetByMove(boolean v) {
      return (JudgementCutNode)super.setIsAutoResetByMove(v);
   }

   public JudgementCutNode setNotCharge(boolean v) {
      return (JudgementCutNode)super.setNotCharge(v);
   }

   public JudgementCutNode setPlaySpeed(float v) {
      return (JudgementCutNode)super.setPlaySpeed(v);
   }

   public JudgementCutNode setConvertTime(float v) {
      return (JudgementCutNode)super.setConvertTime(v);
   }

   public JudgementCutNode setBufferDurationTicks(int v) {
      return (JudgementCutNode)super.setBufferDurationTicks(v);
   }

   public <T extends LivingEntityPatch<?>> JudgementCutNode addCondition(@Nullable Condition<T> condition) {
      super.addCondition(condition);
      return this;
   }

   public <T extends LivingEntityPatch<?>> JudgementCutNode addCondition(@Nullable Condition<T> condition, Side side) {
      super.addCondition(condition, side);
      return this;
   }
}
