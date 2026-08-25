package com.pla.annoyingvillagers.animations;

import com.pla.annoyingvillagers.util.BowFunction;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class BowAttackAnimation extends AttackAnimation {
   public BowAttackAnimation(
      float convertTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      InteractionHand hand,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends BowAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(convertTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, accessor, armature);
      this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
      this.addProperty(ActionAnimationProperty.STOP_MOVEMENT, true);
      this.addProperty(ActionAnimationProperty.MOVE_VERTICAL, false);
   }

   protected void bindPhaseState(Phase phase) {
      float start = phase.start;
      float end = phase.end;
      this.stateSpectrumBlueprint
         .newTimePair(start, end)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .addState(EntityState.CAN_BASIC_ATTACK, false)
         .addState(EntityState.CAN_SKILL_EXECUTION, false)
         .addState(EntityState.TURNING_LOCKED, true)
         .addState(EntityState.LOCKON_ROTATE, true)
         .addState(EntityState.INACTION, true);
   }

   public void begin(LivingEntityPatch<?> livingEntityPatch) {
      super.begin(livingEntityPatch);
      LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
      ItemStack stack = livingEntity.m_21120_(InteractionHand.MAIN_HAND);
      if (!livingEntity.m_9236_().m_5776_()
         && !stack.m_41619_()
         && stack.m_41783_() != null
         && stack.m_41720_() instanceof BowItem
         && BowFunction.hasArrowOrInfinity(livingEntity, stack)) {
         stack.m_41783_().m_128350_("Pulling", 0.1F);
      }
   }

   public void end(LivingEntityPatch<?> livingEntityPatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
      LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
      ItemStack stack = livingEntity.m_21120_(InteractionHand.MAIN_HAND);
      if (!livingEntity.m_9236_().m_5776_() && !stack.m_41619_() && stack.m_41783_() != null && stack.m_41720_() instanceof BowItem) {
         stack.m_41783_().m_128473_("Pulling");
      }

      super.end(livingEntityPatch, nextAnimation, isEnd);
   }
}
