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

public class SubComboNode extends ComboNode {
   SubComboNode() {
   }

   public static SubComboNode create(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      SubComboNode node = new SubComboNode();
      node.animationAccessor = anim;
      return node;
   }

   public SubComboNode setAnimation(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      this.animationAccessor = anim;
      return this;
   }

   @Override
   public AnimationAccessor<? extends StaticAnimation> getAnimationAccessor() {
      return this.animationAccessor;
   }

   public SubComboNode setRepeatNode(boolean repeatNode) {
      super.setRepeatNode(repeatNode);
      return this;
   }

   public SubComboNode setAllowBuffer(boolean allowBuffer) {
      super.setAllowBuffer(allowBuffer);
      return this;
   }

   public SubComboNode setActionTag(ActionTag tag) {
      super.setActionTag(tag);
      return this;
   }

   public SubComboNode setBufferDurationTicks(int bufferDurationTicks) {
      super.setBufferDurationTicks(bufferDurationTicks);
      return this;
   }

   public SubComboNode setAllowLongPress(boolean allowLongPress) {
      super.setAllowLongPress(allowLongPress);
      return this;
   }

   public SubComboNode setLongPressThresholdOverride(int v) {
      super.setLongPressThresholdOverride(v);
      return this;
   }

   public SubComboNode setArmorNegation(float v) {
      super.setArmorNegation(v);
      return this;
   }

   public SubComboNode setHurtDamageMultiplier(float v) {
      super.setHurtDamageMultiplier(v);
      return this;
   }

   public SubComboNode setDamageMultiplier(ValueModifier v) {
      super.setDamageMultiplier(v);
      return this;
   }

   public SubComboNode setImpactMultiplier(float v) {
      super.setImpactMultiplier(v);
      return this;
   }

   public SubComboNode setStunTypeModifier(StunType v) {
      super.setStunTypeModifier(v);
      return this;
   }

   public SubComboNode setCanBeInterrupt(boolean v) {
      super.setCanBeInterrupt(v);
      return this;
   }

   public SubComboNode setPriority(int v) {
      super.setPriority(v);
      return this;
   }

   public SubComboNode setCooldown(int v) {
      super.setCooldown(v);
      return this;
   }

   public SubComboNode setComboResetTicks(int v) {
      super.setComboResetTicks(v);
      return this;
   }

   public SubComboNode setComboResetAtTime(float v) {
      super.setComboResetAtTime(v);
      return this;
   }

   public SubComboNode setComboResetAtAnimTime(float v) {
      super.setComboResetAtAnimTime(v);
      return this;
   }

   public SubComboNode setIsAutoResetByMove(boolean v) {
      super.setIsAutoResetByMove(v);
      return this;
   }

   public SubComboNode setNewPhase(int v) {
      super.setNewPhase(v);
      return this;
   }

   public SubComboNode setNotCharge(boolean v) {
      super.setNotCharge(v);
      return this;
   }

   public SubComboNode setPlaySpeed(float v) {
      super.setPlaySpeed(v);
      return this;
   }

   public SubComboNode setConvertTime(float v) {
      super.setConvertTime(v);
      return this;
   }

   public SubComboNode addTimeEvent(TimeStampedEvent v) {
      super.addTimeEvent(v);
      return this;
   }

   public SubComboNode addTimeEvent(BaseEvent v) {
      super.addTimeEvent(v);
      return this;
   }

   public SubComboNode addDodgeSuccessEvent(BaseEvent v) {
      super.addDodgeSuccessEvent(v);
      return this;
   }

   public SubComboNode addHurtEvent(BaseEvent v) {
      super.addHurtEvent(v);
      return this;
   }

   public SubComboNode addHitEvent(BaseEvent v) {
      super.addHitEvent(v);
      return this;
   }

   public SubComboNode addBeginEvent(BaseEvent v) {
      super.addBeginEvent(v);
      return this;
   }

   public SubComboNode addTimePeriodEvent(TimePeriodEvent v) {
      super.addTimePeriodEvent(v);
      return this;
   }

   public <T extends LivingEntityPatch<?>> SubComboNode addCondition(@Nullable Condition<T> condition) {
      super.addCondition(condition);
      return this;
   }

   public <T extends LivingEntityPatch<?>> SubComboNode addCondition(@Nullable Condition<T> condition, Side side) {
      super.addCondition(condition, side);
      return this;
   }

   public SubComboNode addConditionNode(ComboNode conditionAnimation) {
      super.addConditionNode(conditionAnimation);
      return this;
   }

   public SubComboNode addChild(ComboType type, ComboNode child) {
      super.addChild(type, child);
      return this;
   }

   public SubComboNode addLeaf(ComboType type, @Nullable AnimationAccessor<? extends StaticAnimation> animation) {
      super.addLeaf(type, animation);
      return this;
   }
}
