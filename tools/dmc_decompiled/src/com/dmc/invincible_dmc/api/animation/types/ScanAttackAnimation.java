package com.dmc.invincible_dmc.api.animation.types;

import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ScanAttackAnimation extends AttackAnimation {
   public ScanAttackAnimation(
      float convertTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      InteractionHand hand,
      @Nullable Collider collider,
      Joint colliderJoint,
      String path,
      AssetAccessor<? extends Armature> armature
   ) {
      super(convertTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, path, armature);
   }

   public ScanAttackAnimation(float convertTime, String path, AssetAccessor<? extends Armature> armature, Phase... phases) {
      super(convertTime, path, armature, phases);
   }

   public ScanAttackAnimation(
      float transitionTime, AnimationAccessor<? extends AttackAnimation> accessor, AssetAccessor<? extends Armature> armature, Phase... phases
   ) {
      super(transitionTime, accessor, armature, phases);
   }

   public ScanAttackAnimation(
      float transitionTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends AttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
   }

   public ScanAttackAnimation(
      float transitionTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      InteractionHand hand,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends AttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, accessor, armature);
   }

   @Nullable
   public static LivingEntity getTarget(LivingEntityPatch<?> entityPatch) {
      return entityPatch.getTarget() != null ? entityPatch.getTarget() : getNearestScannedTarget(entityPatch);
   }

   @Nullable
   public static LivingEntity getNearestScannedTarget(LivingEntityPatch<?> entityPatch) {
      if (entityPatch.getCurrentlyAttackTriedEntities().isEmpty()) {
         return null;
      } else {
         Entity entity = (Entity)entityPatch.getCurrentlyAttackTriedEntities().get(0);
         return entity instanceof LivingEntity living ? living : null;
      }
   }

   public void begin(LivingEntityPatch<?> entitypatch) {
      entitypatch.removeHurtEntities();
      super.begin(entitypatch);
   }

   protected Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> dynamicAnimation) {
      Vec3 vec3 = super.getCoordVector(entitypatch, dynamicAnimation);
      if (entitypatch.shouldBlockMoving() && this.getProperty(ActionAnimationProperty.CANCELABLE_MOVE).orElse(false)) {
         vec3 = vec3.m_82490_(0.0);
      }

      return vec3;
   }

   protected void attackTick(LivingEntityPatch<?> entityPatch, AssetAccessor<? extends DynamicAnimation> animation) {
      AnimationPlayer player = DMCAnimationUtils.getPlayerFor(entityPatch, this.getAccessor());
      if (player != null) {
         float prevElapsedTime = player.getPrevElapsedTime();
         float elapsedTime = player.getElapsedTime();
         EntityState prevState = ((DynamicAnimation)animation.get()).getState(entityPatch, prevElapsedTime);
         EntityState state = ((DynamicAnimation)animation.get()).getState(entityPatch, elapsedTime);
         Phase phase = this.getPhaseByTime(((DynamicAnimation)animation.get()).isLinkAnimation() ? 0.0F : elapsedTime);
         LivingEntity target = entityPatch.getTarget();
         if (target == null) {
            target = getNearestScannedTarget(entityPatch);
         }

         if (target != null && elapsedTime < phase.contact) {
            Vec3 playerPosition = ((LivingEntity)entityPatch.getOriginal()).m_20182_();
            Vec3 targetPosition = target.m_20182_();
            float yaw = (float)MathUtils.getYRotOfVector(targetPosition.m_82546_(playerPosition));
            entityPatch.setYRot(yaw);
         }

         if (prevState.attacking() || state.attacking() || prevState.getLevel() <= 2 && state.getLevel() > 2) {
            if (!prevState.attacking()
               || phase != this.getPhaseByTime(prevElapsedTime) && (state.attacking() || prevState.getLevel() <= 2 && state.getLevel() > 2)) {
               entityPatch.onStrike(this, phase.hand);
               entityPatch.removeHurtEntities();
            }

            this.searchNearestEntity(entityPatch, prevElapsedTime, elapsedTime, prevState, state, phase);
         }
      }
   }

   protected void searchNearestEntity(
      LivingEntityPatch<?> entityPatch, float prevElapsedTime, float elapsedTime, EntityState prevState, EntityState state, Phase phase
   ) {
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      float prevPoseTime = prevState.attacking() ? prevElapsedTime : phase.preDelay;
      float poseTime = state.attacking() ? elapsedTime : phase.contact;
      List<Entity> list = this.getPhaseByTime(elapsedTime)
         .getCollidingEntities(entityPatch, this, prevPoseTime, poseTime, this.getPlaySpeed(entityPatch, this));
      if (!list.isEmpty()) {
         HitEntityList hitEntities = new HitEntityList(entityPatch, list, phase.getProperty(AttackPhaseProperty.HIT_PRIORITY).orElse(Priority.DISTANCE));

         while (hitEntities.next()) {
            Entity target = hitEntities.getEntity();
            LivingEntity trueEntity = this.getTrueEntity(target);
            if (trueEntity != null
               && trueEntity.m_6084_()
               && !entityPatch.getCurrentlyAttackTriedEntities().contains(trueEntity)
               && !entityPatch.isTargetInvulnerable(target)
               && (target instanceof LivingEntity || target instanceof PartEntity)
               && entity.m_142582_(target)) {
               entityPatch.getCurrentlyAttackTriedEntities().add(trueEntity);
               entityPatch.getCurrentlyAttackTriedEntities().sort((e1, e2) -> Float.compare(e1.m_20270_(entity), e2.m_20270_(entity)));
            }
         }
      }
   }
}
