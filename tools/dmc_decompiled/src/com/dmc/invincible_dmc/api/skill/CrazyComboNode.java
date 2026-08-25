package com.dmc.invincible_dmc.api.skill;

import com.dmc.invincible_dmc.api.events.BaseEvent;
import com.dmc.invincible_dmc.api.events.Side;
import com.dmc.invincible_dmc.api.events.TimePeriodEvent;
import com.dmc.invincible_dmc.api.events.TimeStampedEvent;
import com.dmc.invincible_dmc.api.skill.crazycombo.CrazyComboPolicy;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

public class CrazyComboNode extends ComboNode implements ICrazyComboNode {
   @Nullable
   private SubComboNode ccBase;
   @Nullable
   private SubComboNode ccChase;
   @Nullable
   private ComboNode ccFinish;
   @Nullable
   private ComboNode ccFinishNoChase;
   private int ccMaxChases = 3;
   private CrazyComboPolicy ccPolicy = CrazyComboPolicy.DEFAULT;

   CrazyComboNode() {
   }

   public static CrazyComboNode create(@Nullable SubComboNode base) {
      CrazyComboNode node = new CrazyComboNode();
      node.ccBase = base;
      return node;
   }

   @Nullable
   @Override
   public SubComboNode getCcBase() {
      return this.ccBase;
   }

   public CrazyComboNode setCcBase(@Nullable SubComboNode base) {
      this.ccBase = base;
      return this;
   }

   public CrazyComboNode setCcBase(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      this.ccBase = SubComboNode.create(anim);
      return this;
   }

   @Nullable
   @Override
   public SubComboNode getCcChase() {
      return this.ccChase;
   }

   public CrazyComboNode setCcChase(@Nullable SubComboNode chase) {
      this.ccChase = chase;
      return this;
   }

   public CrazyComboNode setCcChase(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      this.ccChase = SubComboNode.create(anim);
      return this;
   }

   @Nullable
   @Override
   public ComboNode getCcFinish() {
      return this.ccFinish;
   }

   public CrazyComboNode setCcFinish(@Nullable ComboNode finish) {
      this.ccFinish = finish;
      return this;
   }

   public CrazyComboNode setCcFinish(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      this.ccFinish = SubComboNode.create(anim);
      return this;
   }

   @Nullable
   @Override
   public ComboNode getCcFinishNoChase() {
      return this.ccFinishNoChase;
   }

   public CrazyComboNode setCcFinishNoChase(@Nullable ComboNode finishNoChase) {
      this.ccFinishNoChase = finishNoChase;
      return this;
   }

   public CrazyComboNode setCcFinishNoChase(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      this.ccFinishNoChase = SubComboNode.create(anim);
      return this;
   }

   @Nullable
   @Override
   public AnimationAccessor<? extends StaticAnimation> getAnimationAccessor() {
      return this.ccBase != null ? this.ccBase.getAnimationAccessor() : null;
   }

   public CrazyComboNode setCcBaseAnim(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      return this.setCcBase(anim);
   }

   public CrazyComboNode setCcChaseAnim(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      return this.setCcChase(anim);
   }

   public CrazyComboNode setCcFinishAnim(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      return this.setCcFinish(anim);
   }

   public CrazyComboNode setCcFinishAnimNoChase(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      return this.setCcFinishNoChase(anim);
   }

   @Override
   public int getCcMaxChases() {
      return this.ccMaxChases;
   }

   @Override
   public CrazyComboPolicy getCrazyComboPolicy() {
      return this.ccPolicy;
   }

   public CrazyComboNode setCcMaxChases(int ccMaxChases) {
      this.ccMaxChases = ccMaxChases;
      return this;
   }

   public CrazyComboNode setCcResetCombo(boolean ccResetCombo) {
      this.ccPolicy = this.ccPolicy.withResetCombo(ccResetCombo);
      return this;
   }

   public CrazyComboNode setCcChaseRequiredPresses(int requiredPresses) {
      this.ccPolicy = this.ccPolicy.withChaseRequiredPresses(requiredPresses);
      return this;
   }

   public CrazyComboNode setCcBaseRequiredPresses(int requiredPresses) {
      this.ccPolicy = this.ccPolicy.withBaseRequiredPresses(requiredPresses);
      return this;
   }

   public CrazyComboNode setCcRapidMaxIntervalTicks(int maxIntervalTicks) {
      this.ccPolicy = this.ccPolicy.withRapidMaxIntervalTicks(maxIntervalTicks);
      return this;
   }

   public CrazyComboNode setCcWindowStart(float ccWindowStart) {
      this.ccPolicy = this.ccPolicy.withInputWindowStart(ccWindowStart);
      return this;
   }

   public CrazyComboNode setCcFinishMinPhase(int ccFinishMinPhase) {
      this.ccPolicy = this.ccPolicy.withFinishMinPhase(ccFinishMinPhase);
      return this;
   }

   public CrazyComboNode setCcStartupFinishNoChasePhase(int phaseOrder) {
      this.ccPolicy = this.ccPolicy.withStartupFinishNoChasePhase(phaseOrder);
      return this;
   }

   public CrazyComboNode setRepeatNode(boolean repeatNode) {
      super.setRepeatNode(repeatNode);
      return this;
   }

   public CrazyComboNode setAllowBuffer(boolean allowBuffer) {
      super.setAllowBuffer(allowBuffer);
      return this;
   }

   public CrazyComboNode setBufferDurationTicks(int bufferDurationTicks) {
      super.setBufferDurationTicks(bufferDurationTicks);
      return this;
   }

   public CrazyComboNode setAllowLongPress(boolean allowLongPress) {
      super.setAllowLongPress(allowLongPress);
      return this;
   }

   public CrazyComboNode setLongPressThresholdOverride(int v) {
      super.setLongPressThresholdOverride(v);
      return this;
   }

   public CrazyComboNode setArmorNegation(float v) {
      super.setArmorNegation(v);
      return this;
   }

   public CrazyComboNode setHurtDamageMultiplier(float v) {
      super.setHurtDamageMultiplier(v);
      return this;
   }

   public CrazyComboNode setDamageMultiplier(ValueModifier v) {
      super.setDamageMultiplier(v);
      return this;
   }

   public CrazyComboNode setImpactMultiplier(float v) {
      super.setImpactMultiplier(v);
      return this;
   }

   public CrazyComboNode setStunTypeModifier(StunType v) {
      super.setStunTypeModifier(v);
      return this;
   }

   public CrazyComboNode setCanBeInterrupt(boolean v) {
      super.setCanBeInterrupt(v);
      return this;
   }

   public CrazyComboNode setPriority(int v) {
      super.setPriority(v);
      return this;
   }

   public CrazyComboNode setCooldown(int v) {
      super.setCooldown(v);
      return this;
   }

   public CrazyComboNode setComboResetTicks(int v) {
      super.setComboResetTicks(v);
      return this;
   }

   public CrazyComboNode setComboResetAtTime(float v) {
      super.setComboResetAtTime(v);
      return this;
   }

   public CrazyComboNode setIsAutoResetByMove(boolean v) {
      super.setIsAutoResetByMove(v);
      return this;
   }

   public CrazyComboNode setNewPhase(int v) {
      super.setNewPhase(v);
      return this;
   }

   public CrazyComboNode setNotCharge(boolean v) {
      super.setNotCharge(v);
      return this;
   }

   public CrazyComboNode setPlaySpeed(float v) {
      super.setPlaySpeed(v);
      return this;
   }

   public CrazyComboNode setConvertTime(float v) {
      super.setConvertTime(v);
      return this;
   }

   public CrazyComboNode addTimeEvent(TimeStampedEvent v) {
      super.addTimeEvent(v);
      return this;
   }

   public CrazyComboNode addTimeEvent(BaseEvent v) {
      super.addTimeEvent(v);
      return this;
   }

   public CrazyComboNode addDodgeSuccessEvent(BaseEvent v) {
      super.addDodgeSuccessEvent(v);
      return this;
   }

   public CrazyComboNode addHurtEvent(BaseEvent v) {
      super.addHurtEvent(v);
      return this;
   }

   public CrazyComboNode addHitEvent(BaseEvent v) {
      super.addHitEvent(v);
      return this;
   }

   public CrazyComboNode addBeginEvent(BaseEvent v) {
      super.addBeginEvent(v);
      return this;
   }

   public CrazyComboNode addTimePeriodEvent(TimePeriodEvent v) {
      super.addTimePeriodEvent(v);
      return this;
   }

   public <T extends LivingEntityPatch<?>> CrazyComboNode addCondition(@Nullable Condition<T> condition) {
      super.addCondition(condition);
      return this;
   }

   public <T extends LivingEntityPatch<?>> CrazyComboNode addCondition(@Nullable Condition<T> condition, Side side) {
      super.addCondition(condition, side);
      return this;
   }

   public CrazyComboNode addConditionNode(ComboNode conditionAnimation) {
      super.addConditionNode(conditionAnimation);
      return this;
   }

   public CrazyComboNode addChild(ComboType type, ComboNode child) {
      super.addChild(type, child);
      return this;
   }

   public CrazyComboNode addLeaf(ComboType type, @Nullable AnimationAccessor<? extends StaticAnimation> animation) {
      super.addLeaf(type, animation);
      return this;
   }

   @Override
   protected ComboNode createCopyInstance() {
      return new CrazyComboNode();
   }

   @Override
   public ComboNode copyForBranching() {
      CrazyComboNode copy = (CrazyComboNode)super.copyForBranching();
      copy.ccBase = this.ccBase;
      copy.ccChase = this.ccChase;
      copy.ccFinish = this.ccFinish;
      copy.ccFinishNoChase = this.ccFinishNoChase;
      copy.ccMaxChases = this.ccMaxChases;
      copy.ccPolicy = this.ccPolicy;
      return copy;
   }
}
