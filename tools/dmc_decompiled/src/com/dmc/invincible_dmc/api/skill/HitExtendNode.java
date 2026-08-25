package com.dmc.invincible_dmc.api.skill;

import com.dmc.invincible_dmc.api.events.BaseEvent;
import com.dmc.invincible_dmc.api.events.Side;
import com.dmc.invincible_dmc.api.events.TimePeriodEvent;
import com.dmc.invincible_dmc.api.events.TimeStampedEvent;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

public class HitExtendNode extends ComboNode implements IHitExtendNode {
   @Nullable
   private SubComboNode base;
   @Nullable
   private SubComboNode extend;
   private int minimumHoldTicks = 3;
   private boolean stabilizeContact;
   private final List<AnimationAccessor<? extends StaticAnimation>> baseAnimationAliases = new ArrayList<>();

   HitExtendNode() {
   }

   public static HitExtendNode create(@Nullable SubComboNode base) {
      HitExtendNode node = new HitExtendNode();
      node.base = base;
      return node;
   }

   @Nullable
   @Override
   public SubComboNode getBase() {
      return this.base;
   }

   public HitExtendNode setBase(@Nullable SubComboNode base) {
      this.base = base;
      return this;
   }

   public HitExtendNode setBase(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      this.base = SubComboNode.create(anim);
      return this;
   }

   @Nullable
   @Override
   public SubComboNode getExtend() {
      return this.extend;
   }

   public HitExtendNode setExtend(@Nullable SubComboNode extend) {
      this.extend = extend;
      return this;
   }

   public HitExtendNode setExtend(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      this.extend = SubComboNode.create(anim);
      return this;
   }

   public HitExtendNode setBaseAnim(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      return this.setBase(anim);
   }

   public HitExtendNode setExtendAnim(@Nullable AnimationAccessor<? extends StaticAnimation> anim) {
      return this.setExtend(anim);
   }

   @Override
   public int getMinimumHoldTicks() {
      return this.minimumHoldTicks;
   }

   public HitExtendNode setMinimumHoldTicks(int minimumHoldTicks) {
      this.minimumHoldTicks = Math.max(0, minimumHoldTicks);
      return this;
   }

   @Override
   public boolean shouldStabilizeContact() {
      return this.stabilizeContact;
   }

   public HitExtendNode setStabilizeContact(boolean stabilizeContact) {
      this.stabilizeContact = stabilizeContact;
      return this;
   }

   public HitExtendNode addBaseAnimationAlias(AnimationAccessor<? extends StaticAnimation> animation) {
      if (animation != null && !this.baseAnimationAliases.contains(animation)) {
         this.baseAnimationAliases.add(animation);
      }

      return this;
   }

   @Override
   public boolean matchesBaseAnimation(@Nullable AssetAccessor<?> animation) {
      if (IHitExtendNode.super.matchesBaseAnimation(animation)) {
         return true;
      } else if (animation != null && animation.registryName() != null) {
         for (AssetAccessor<?> alias : this.baseAnimationAliases) {
            if (alias == animation || alias.registryName() != null && alias.registryName().equals(animation.registryName())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Nullable
   @Override
   public AnimationAccessor<? extends StaticAnimation> getAnimationAccessor() {
      return this.base != null ? this.base.getAnimationAccessor() : null;
   }

   public HitExtendNode setRepeatNode(boolean repeatNode) {
      super.setRepeatNode(repeatNode);
      return this;
   }

   public HitExtendNode setAllowBuffer(boolean allowBuffer) {
      super.setAllowBuffer(allowBuffer);
      return this;
   }

   public HitExtendNode setBufferDurationTicks(int bufferDurationTicks) {
      super.setBufferDurationTicks(bufferDurationTicks);
      return this;
   }

   public HitExtendNode setAllowLongPress(boolean allowLongPress) {
      super.setAllowLongPress(allowLongPress);
      return this;
   }

   public HitExtendNode setLongPressThresholdOverride(int v) {
      super.setLongPressThresholdOverride(v);
      return this;
   }

   public HitExtendNode setArmorNegation(float v) {
      super.setArmorNegation(v);
      return this;
   }

   public HitExtendNode setHurtDamageMultiplier(float v) {
      super.setHurtDamageMultiplier(v);
      return this;
   }

   public HitExtendNode setDamageMultiplier(ValueModifier v) {
      super.setDamageMultiplier(v);
      return this;
   }

   public HitExtendNode setImpactMultiplier(float v) {
      super.setImpactMultiplier(v);
      return this;
   }

   public HitExtendNode setStunTypeModifier(StunType v) {
      super.setStunTypeModifier(v);
      return this;
   }

   public HitExtendNode setCanBeInterrupt(boolean v) {
      super.setCanBeInterrupt(v);
      return this;
   }

   public HitExtendNode setPriority(int v) {
      super.setPriority(v);
      return this;
   }

   public HitExtendNode setCooldown(int v) {
      super.setCooldown(v);
      return this;
   }

   public HitExtendNode setComboResetTicks(int v) {
      super.setComboResetTicks(v);
      return this;
   }

   public HitExtendNode setComboResetAtTime(float v) {
      super.setComboResetAtTime(v);
      return this;
   }

   public HitExtendNode setIsAutoResetByMove(boolean v) {
      super.setIsAutoResetByMove(v);
      return this;
   }

   public HitExtendNode setNewPhase(int v) {
      super.setNewPhase(v);
      return this;
   }

   public HitExtendNode setNotCharge(boolean v) {
      super.setNotCharge(v);
      return this;
   }

   public HitExtendNode setPlaySpeed(float v) {
      super.setPlaySpeed(v);
      return this;
   }

   public HitExtendNode setConvertTime(float v) {
      super.setConvertTime(v);
      return this;
   }

   public HitExtendNode addTimeEvent(TimeStampedEvent v) {
      super.addTimeEvent(v);
      return this;
   }

   public HitExtendNode addTimeEvent(BaseEvent v) {
      super.addTimeEvent(v);
      return this;
   }

   public HitExtendNode addDodgeSuccessEvent(BaseEvent v) {
      super.addDodgeSuccessEvent(v);
      return this;
   }

   public HitExtendNode addHurtEvent(BaseEvent v) {
      super.addHurtEvent(v);
      return this;
   }

   public HitExtendNode addHitEvent(BaseEvent v) {
      super.addHitEvent(v);
      return this;
   }

   public HitExtendNode addBeginEvent(BaseEvent v) {
      super.addBeginEvent(v);
      return this;
   }

   public HitExtendNode addTimePeriodEvent(TimePeriodEvent v) {
      super.addTimePeriodEvent(v);
      return this;
   }

   public <T extends LivingEntityPatch<?>> HitExtendNode addCondition(@Nullable Condition<T> condition) {
      super.addCondition(condition);
      return this;
   }

   public <T extends LivingEntityPatch<?>> HitExtendNode addCondition(@Nullable Condition<T> condition, Side side) {
      super.addCondition(condition, side);
      return this;
   }

   public HitExtendNode addConditionNode(ComboNode conditionAnimation) {
      super.addConditionNode(conditionAnimation);
      return this;
   }

   public HitExtendNode addChild(ComboType type, ComboNode child) {
      super.addChild(type, child);
      return this;
   }

   public HitExtendNode addLeaf(ComboType type, @Nullable AnimationAccessor<? extends StaticAnimation> animation) {
      super.addLeaf(type, animation);
      return this;
   }

   @Override
   protected ComboNode createCopyInstance() {
      return new HitExtendNode();
   }

   @Override
   public ComboNode copyForBranching() {
      HitExtendNode copy = (HitExtendNode)super.copyForBranching();
      copy.base = this.base;
      copy.extend = this.extend;
      copy.minimumHoldTicks = this.minimumHoldTicks;
      copy.stabilizeContact = this.stabilizeContact;
      copy.baseAnimationAliases.addAll(this.baseAnimationAliases);
      return copy;
   }
}
