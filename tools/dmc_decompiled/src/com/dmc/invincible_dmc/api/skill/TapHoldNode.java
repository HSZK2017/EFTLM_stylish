package com.dmc.invincible_dmc.api.skill;

import com.dmc.invincible_dmc.api.events.BaseEvent;
import com.dmc.invincible_dmc.api.events.Side;
import com.dmc.invincible_dmc.api.events.TimePeriodEvent;
import com.dmc.invincible_dmc.api.events.TimeStampedEvent;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

public class TapHoldNode extends ComboNode implements ITapHoldNode {
   @Nullable
   private SubComboNode tap;
   @Nullable
   private SubComboNode hold;
   private int windupDurationTicks = 4;

   TapHoldNode() {
   }

   public static TapHoldNode create(@Nullable SubComboNode tap) {
      TapHoldNode node = new TapHoldNode();
      node.tap = tap;
      return node;
   }

   @Nullable
   @Override
   public SubComboNode getTap() {
      return this.tap;
   }

   public TapHoldNode setTap(@Nullable SubComboNode tap) {
      this.tap = tap;
      return this;
   }

   public TapHoldNode setTap(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      this.tap = SubComboNode.create(anim);
      return this;
   }

   @Nullable
   @Override
   public SubComboNode getHold() {
      return this.hold;
   }

   public TapHoldNode setHold(@Nullable SubComboNode hold) {
      this.hold = hold;
      return this;
   }

   public TapHoldNode setHold(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      this.hold = SubComboNode.create(anim);
      return this;
   }

   public TapHoldNode setTapAnim(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      return this.setTap(anim);
   }

   public TapHoldNode setHoldAnim(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      return this.setHold(anim);
   }

   @Nullable
   @Override
   public AnimationAccessor<? extends StaticAnimation> getAnimationAccessor() {
      return this.tap != null ? this.tap.getAnimationAccessor() : null;
   }

   @Override
   public int getWindupDurationTicks() {
      return this.windupDurationTicks;
   }

   public TapHoldNode setWindupDurationTicks(int windupDurationTicks) {
      this.windupDurationTicks = windupDurationTicks;
      return this;
   }

   public TapHoldNode setRepeatNode(boolean repeatNode) {
      super.setRepeatNode(repeatNode);
      return this;
   }

   public TapHoldNode setAllowBuffer(boolean allowBuffer) {
      super.setAllowBuffer(allowBuffer);
      return this;
   }

   public TapHoldNode setBufferDurationTicks(int bufferDurationTicks) {
      super.setBufferDurationTicks(bufferDurationTicks);
      return this;
   }

   public TapHoldNode setAllowLongPress(boolean allowLongPress) {
      super.setAllowLongPress(allowLongPress);
      return this;
   }

   public TapHoldNode setLongPressThresholdOverride(int v) {
      super.setLongPressThresholdOverride(v);
      return this;
   }

   public TapHoldNode setArmorNegation(float v) {
      super.setArmorNegation(v);
      return this;
   }

   public TapHoldNode setHurtDamageMultiplier(float v) {
      super.setHurtDamageMultiplier(v);
      return this;
   }

   public TapHoldNode setDamageMultiplier(ValueModifier v) {
      super.setDamageMultiplier(v);
      return this;
   }

   public TapHoldNode setImpactMultiplier(float v) {
      super.setImpactMultiplier(v);
      return this;
   }

   public TapHoldNode setStunTypeModifier(StunType v) {
      super.setStunTypeModifier(v);
      return this;
   }

   public TapHoldNode setCanBeInterrupt(boolean v) {
      super.setCanBeInterrupt(v);
      return this;
   }

   public TapHoldNode setPriority(int v) {
      super.setPriority(v);
      return this;
   }

   public TapHoldNode setCooldown(int v) {
      super.setCooldown(v);
      return this;
   }

   public TapHoldNode setComboResetTicks(int v) {
      super.setComboResetTicks(v);
      return this;
   }

   public TapHoldNode setComboResetAtTime(float v) {
      super.setComboResetAtTime(v);
      return this;
   }

   public TapHoldNode setIsAutoResetByMove(boolean v) {
      super.setIsAutoResetByMove(v);
      return this;
   }

   public TapHoldNode setNewPhase(int v) {
      super.setNewPhase(v);
      return this;
   }

   public TapHoldNode setNotCharge(boolean v) {
      super.setNotCharge(v);
      return this;
   }

   public TapHoldNode setPlaySpeed(float v) {
      super.setPlaySpeed(v);
      return this;
   }

   public TapHoldNode setConvertTime(float v) {
      super.setConvertTime(v);
      return this;
   }

   public TapHoldNode addTimeEvent(TimeStampedEvent v) {
      super.addTimeEvent(v);
      return this;
   }

   public TapHoldNode addTimeEvent(BaseEvent v) {
      super.addTimeEvent(v);
      return this;
   }

   public TapHoldNode addDodgeSuccessEvent(BaseEvent v) {
      super.addDodgeSuccessEvent(v);
      return this;
   }

   public TapHoldNode addHurtEvent(BaseEvent v) {
      super.addHurtEvent(v);
      return this;
   }

   public TapHoldNode addHitEvent(BaseEvent v) {
      super.addHitEvent(v);
      return this;
   }

   public TapHoldNode addBeginEvent(BaseEvent v) {
      super.addBeginEvent(v);
      return this;
   }

   public TapHoldNode addTimePeriodEvent(TimePeriodEvent v) {
      super.addTimePeriodEvent(v);
      return this;
   }

   public <T extends LivingEntityPatch<?>> TapHoldNode addCondition(@Nullable Condition<T> condition) {
      super.addCondition(condition);
      return this;
   }

   public <T extends LivingEntityPatch<?>> TapHoldNode addCondition(@Nullable Condition<T> condition, Side side) {
      super.addCondition(condition, side);
      return this;
   }

   public TapHoldNode addConditionNode(ComboNode conditionAnimation) {
      super.addConditionNode(conditionAnimation);
      return this;
   }

   public TapHoldNode addChild(ComboType type, ComboNode child) {
      super.addChild(type, child);
      return this;
   }

   public TapHoldNode addLeaf(ComboType type, @Nullable AnimationAccessor<? extends StaticAnimation> animation) {
      super.addLeaf(type, animation);
      return this;
   }

   @Override
   protected ComboNode createCopyInstance() {
      return new TapHoldNode();
   }

   @Override
   public ComboNode copyForBranching() {
      TapHoldNode copy = (TapHoldNode)super.copyForBranching();
      copy.tap = this.tap;
      copy.hold = this.hold;
      copy.windupDurationTicks = this.windupDurationTicks;
      return copy;
   }
}
