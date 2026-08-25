package com.dmc.invincible_dmc.api.animation.types.yamato;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunPhase;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.epicfight.api.AnimationAttackEvent;
import com.merlin204.avalon.epicfight.api.AnimationAttackResultEvent;
import com.merlin204.avalon.epicfight.api.AvalonAnimationProperty;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PoseModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.JointColliderPair;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

public class JudgementCutEndAnimation extends YamatoAttackAnimation {
   private final Map<LivingEntityPatch<?>, Set<LivingEntity>> syncOnlyPhaseEntities = new ConcurrentHashMap<>();
   public static final TagKey<DamageType> SYNC_ATTACK = InvincibleMod_DMC.createDamageType("sync_attack");
   public static final ActionAnimationProperty<JudgementCutEndAnimation.SpecialPhase> MOVE_ROOT_PHASE = new ActionAnimationProperty();
   public static final ActionAnimationProperty<TimePairList> RAIN_FREEZE_TIME = new ActionAnimationProperty();

   public Set<LivingEntity> getSyncedEntities(LivingEntityPatch<?> ep) {
      return this.syncOnlyPhaseEntities.computeIfAbsent(ep, k -> new HashSet<>());
   }

   @Override
   public void begin(LivingEntityPatch<?> entitypatch) {
      super.begin(entitypatch);
      this.getSyncedEntities(entitypatch).clear();
   }

   @Override
   public void end(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
      super.end(entitypatch, nextAnimation, isEnd);
      this.syncOnlyPhaseEntities.remove(entitypatch);
   }

   public JudgementCutEndAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      Supplier<? extends StaticAnimation> hitAnimation,
      CustomStunPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, hitAnimation, phases);
   }

   public JudgementCutEndAnimation(
      float transitionTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti
   ) {
      super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature, play_speed, damageMulti);
   }

   public JudgementCutEndAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
   }

   public JudgementCutEndAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, damageMulti, phases);
   }

   public JudgementCutEndAnimation(
      float transitionTime, AnimationAccessor<? extends BasicAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, phases);
   }

   public JudgementCutEndAnimation(
      float convertTime, String path, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti, AvalonPhase... phases
   ) {
      super(convertTime, path, armature, play_speed, damageMulti, phases);
   }

   public JudgementCutEndAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      CustomStunPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
   }

   public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
      if (this.getProperty(ActionAnimationProperty.COORD).isEmpty()) {
         JointTransform jt = pose.orElseEmpty("Root");
         Vec3f jointPosition = jt.translation();
         OpenMatrix4f toRootTransformApplied = entitypatch.getArmature().searchJointByName("Root").getLocalTransform().removeTranslation();
         OpenMatrix4f toOrigin = OpenMatrix4f.invert(toRootTransformApplied, null);
         Vec3f worldPosition = OpenMatrix4f.transform3v(toRootTransformApplied, jointPosition, null);
         if (!this.getProperty(MOVE_ROOT_PHASE).isPresent()
            || !((JudgementCutEndAnimation.SpecialPhase)this.getProperty(MOVE_ROOT_PHASE).get()).isInPhase(time)) {
            worldPosition.x = 0.0F;
            worldPosition.y = this.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false) && worldPosition.y > 0.0F ? 0.0F : worldPosition.y;
            worldPosition.z = 0.0F;
         }

         OpenMatrix4f.transform3v(toOrigin, worldPosition, worldPosition);
         jointPosition.x = worldPosition.x;
         jointPosition.y = worldPosition.y;
         jointPosition.z = worldPosition.z;
      }

      PoseModifier modifier = (PoseModifier)this.getProperty(StaticAnimationProperty.POSE_MODIFIER).orElse(null);
      if (modifier != null) {
         modifier.modify(animation, pose, entitypatch, time, partialTicks);
      }
   }

   @Override
   protected Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> dynamicAnimation) {
      Vec3 vec3 = super.getCoordVector(entitypatch, dynamicAnimation);
      AnimationPlayer animPlayer = DMCAnimationUtils.getPlayerFor(entitypatch, dynamicAnimation);
      if (animPlayer == null) {
         return vec3;
      } else {
         float t = animPlayer.getElapsedTime();
         if (this.getProperty(MOVE_ROOT_PHASE).isPresent() && ((JudgementCutEndAnimation.SpecialPhase)this.getProperty(MOVE_ROOT_PHASE).get()).isInPhase(t)) {
            vec3 = vec3.m_82542_(0.0, 0.0, 0.0);
         }

         return vec3;
      }
   }

   protected void hurtCollidingEntities(
      LivingEntityPatch<?> entitypatch, float prevElapsedTime, float elapsedTime, EntityState prevState, EntityState state, Phase phase
   ) {
      if (phase instanceof JudgementCutEndAnimation.SyncOnlyPhase syncOnlyPhase) {
         if (entitypatch.isLogicalClient()) {
            return;
         }

         LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
         if (prevElapsedTime < phase.start && elapsedTime >= phase.start) {
            entitypatch.getCurrentlyActuallyHitEntities().clear();
            entitypatch.getCurrentlyAttackTriedEntities().clear();
            syncOnlyPhase.resetAttackRecord(entitypatch);
         }

         float phasePrevTime = Math.max(prevElapsedTime, phase.start);
         float phaseCurrentTime = Math.min(elapsedTime, phase.end);
         float phasePreDelay = phase.start + phase.preDelay;
         float phaseContact = phase.start + phase.contact;
         if (phaseCurrentTime < phasePreDelay || phasePrevTime >= phaseContact) {
            return;
         }

         List<Entity> list = phase.getCollidingEntities(entitypatch, this, phasePrevTime, phaseCurrentTime, this.getPlaySpeed(entitypatch, this));
         if (!list.isEmpty()) {
            HitEntityList hitEntities = new HitEntityList(entitypatch, list, phase.getProperty(AttackPhaseProperty.HIT_PRIORITY).orElse(Priority.DISTANCE));
            int maxStrikes = 999;

            while (entitypatch.getCurrentlyActuallyHitEntities().size() < maxStrikes && hitEntities.next()) {
               Entity target = hitEntities.getEntity();
               LivingEntity trueTarget = this.getTrueEntity(target);
               boolean canAttack = trueTarget != null
                  && trueTarget.m_6084_()
                  && !entitypatch.getCurrentlyActuallyHitEntities().contains(trueTarget)
                  && !entitypatch.isTargetInvulnerable(target)
                  && syncOnlyPhase.tryAttack(entitypatch, trueTarget);
               if (canAttack) {
                  EpicFightDamageSource damageSource = this.getEpicFightDamageSource(entitypatch, trueTarget, syncOnlyPhase)
                     .addRuntimeTag(SYNC_ATTACK)
                     .addRuntimeTag(EpicFightDamageTypeTags.WEAPON_INNATE)
                     .addRuntimeTag(EpicFightDamageTypeTags.UNBLOCKALBE)
                     .addRuntimeTag(EpicFightDamageTypeTags.GUARD_PUNCTURE)
                     .addRuntimeTag(EpicFightDamageTypeTags.BYPASS_DODGE)
                     .addRuntimeTag(EpicFightDamageTypeTags.IS_MELEE)
                     .setBaseImpact(0.0F)
                     .setBaseArmorNegation(0.0F)
                     .attachDamageModifier(ValueModifier.setter(0.0F))
                     .setStunType(StunType.NONE);
                  LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(trueTarget, LivingEntityPatch.class);
                  AttackResult attackResult = targetPatch != null ? targetPatch.tryHurt(damageSource, 0.0F) : AttackResult.success(0.0F);
                  if (attackResult.resultType.shouldCount()) {
                     entitypatch.getCurrentlyActuallyHitEntities().add(trueTarget);
                     entitypatch.getCurrentlyAttackTriedEntities().add(trueTarget);
                     this.getSyncedEntities(entitypatch).add(trueTarget);
                     syncOnlyPhase.callback.accept(entitypatch, trueTarget, attackResult);
                  }
               }
            }
         }
      } else {
         if (entitypatch.isLogicalClient()) {
            return;
         }

         LivingEntity entityx = (LivingEntity)entitypatch.getOriginal();
         if (prevElapsedTime < phase.start && elapsedTime >= phase.start) {
            entitypatch.getCurrentlyActuallyHitEntities().clear();
            entitypatch.getCurrentlyAttackTriedEntities().clear();
            if (phase instanceof AvalonPhase avalonPhase) {
               avalonPhase.resetAttackRecord(entitypatch);
            }
         }

         float phasePrevTimex = Math.max(prevElapsedTime, phase.start);
         float phaseCurrentTimex = Math.min(elapsedTime, phase.end);
         float phasePreDelayx = phase.start + phase.preDelay;
         float phaseContactx = phase.start + phase.contact;
         if (phaseCurrentTimex < phasePreDelayx || phasePrevTimex >= phaseContactx) {
            return;
         }

         for (LivingEntity noDamageEntity : this.getSyncedEntities(entitypatch)) {
            if (noDamageEntity.m_6084_()
               && !entitypatch.getCurrentlyActuallyHitEntities().contains(noDamageEntity)
               && phase.getCollidingEntities(entitypatch, this, phasePrevTimex, phaseCurrentTimex, this.getPlaySpeed(entitypatch, this))
                  .contains(noDamageEntity)) {
               boolean canAttack = !entitypatch.isTargetInvulnerable(noDamageEntity);
               if (phase instanceof AvalonPhase avalonPhase) {
                  canAttack = canAttack && avalonPhase.tryAttack(entitypatch, noDamageEntity);
               }

               if (canAttack) {
                  EpicFightDamageSource epicFightDamageSource = this.getEpicFightDamageSource(entitypatch, noDamageEntity, phase);
                  int invulnerableTime = noDamageEntity.f_19802_;
                  noDamageEntity.f_19802_ = 0;
                  this.getProperty(AvalonAnimationProperty.ATTACK_EVENTS).ifPresent(events -> {
                     for (AnimationAttackEvent<?> event : events) {
                        event.execute(entitypatch, noDamageEntity, epicFightDamageSource);
                     }
                  });
                  AttackResult attackResult = entitypatch.attack(epicFightDamageSource, noDamageEntity, phase.hand);
                  noDamageEntity.f_19802_ = invulnerableTime;
                  this.getProperty(AvalonAnimationProperty.ATTACK_RESULT_EVENTS).ifPresent(events -> {
                     for (AnimationAttackResultEvent<?> event : events) {
                        event.execute(entitypatch, noDamageEntity, attackResult);
                     }
                  });
                  if (attackResult.resultType.dealtDamage()) {
                     entityx.m_20193_()
                        .m_6263_(
                           null,
                           noDamageEntity.m_20185_(),
                           noDamageEntity.m_20186_(),
                           noDamageEntity.m_20189_(),
                           this.getHitSound(entitypatch, phase),
                           noDamageEntity.m_5720_(),
                           1.0F,
                           1.0F
                        );
                     this.spawnHitParticle((ServerLevel)noDamageEntity.m_9236_(), entitypatch, noDamageEntity, phase);
                  }

                  entitypatch.getCurrentlyActuallyHitEntities().add(noDamageEntity);
                  if (attackResult.resultType.shouldCount()) {
                     entitypatch.getCurrentlyAttackTriedEntities().add(noDamageEntity);
                  }
               }
            }
         }
      }
   }

   public static JudgementCutEndAnimation.SyncOnlyPhase createSyncOnlyPhase(
      int startFrame, int endFrame, int waitFrame, InteractionHand hand, Joint joint, Collider collider, SyncCallback callback
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new JudgementCutEndAnimation.SyncOnlyPhase(0.0F, start, start, end, wait, Float.MAX_VALUE, hand, joint, collider, callback);
   }

   public static JudgementCutEndAnimation.SyncOnlyPhase createSyncOnlyPhase(
      int startFrame, int endFrame, int waitFrame, InteractionHand hand, float damageMulti, Joint joint, Collider collider, SyncCallback callback
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new JudgementCutEndAnimation.SyncOnlyPhase(0.0F, start, start, end, wait, Float.MAX_VALUE, hand, damageMulti, joint, collider, callback);
   }

   public static JudgementCutEndAnimation.SyncOnlyPhase createSyncOnlyPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      float impactMulti,
      Joint joint,
      Collider collider,
      SyncCallback callback
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new JudgementCutEndAnimation.SyncOnlyPhase(
         0.0F, start, start, end, wait, Float.MAX_VALUE, hand, damageMulti, impactMulti, joint, collider, callback
      );
   }

   public static JudgementCutEndAnimation.SyncOnlyPhase createSyncOnlyPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      float impactMulti,
      float armorNegationMulti,
      Joint joint,
      Collider collider,
      SyncCallback callback
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new JudgementCutEndAnimation.SyncOnlyPhase(
         0.0F, start, start, end, wait, Float.MAX_VALUE, hand, damageMulti, impactMulti, armorNegationMulti, joint, collider, callback
      );
   }

   public static JudgementCutEndAnimation.SyncOnlyPhase createSyncOnlyPhase(
      int startFrame, int endFrame, int waitFrame, InteractionHand hand, JointColliderPair[] colliders, SyncCallback callback
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new JudgementCutEndAnimation.SyncOnlyPhase(0.0F, start, start, end, wait, Float.MAX_VALUE, hand, colliders, callback);
   }

   public static JudgementCutEndAnimation.SyncOnlyPhase createSyncOnlyPhase(
      int startFrame, int endFrame, int waitFrame, InteractionHand hand, float damageMulti, JointColliderPair[] colliders, SyncCallback callback
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new JudgementCutEndAnimation.SyncOnlyPhase(0.0F, start, start, end, wait, Float.MAX_VALUE, hand, damageMulti, colliders, callback);
   }

   public static JudgementCutEndAnimation.SyncOnlyPhase createSyncOnlyPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      float impactMulti,
      JointColliderPair[] colliders,
      SyncCallback callback
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new JudgementCutEndAnimation.SyncOnlyPhase(0.0F, start, start, end, wait, Float.MAX_VALUE, hand, damageMulti, impactMulti, colliders, callback);
   }

   public static JudgementCutEndAnimation.SyncOnlyPhase createSyncOnlyPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      float impactMulti,
      float armorNegationMulti,
      JointColliderPair[] colliders,
      SyncCallback callback
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new JudgementCutEndAnimation.SyncOnlyPhase(
         0.0F, start, start, end, wait, Float.MAX_VALUE, hand, damageMulti, impactMulti, armorNegationMulti, colliders, callback
      );
   }

   public static record SpecialPhase(float start, float end) {
      public boolean isInPhase(float t) {
         return t >= this.start && t <= this.end;
      }
   }

   public static class SyncOnlyPhase extends AvalonPhase {
      public final SyncCallback callback;

      public SyncOnlyPhase(
         float start,
         float antic,
         float preDelay,
         float contact,
         float recovery,
         float end,
         InteractionHand hand,
         Joint joint,
         Collider collider,
         SyncCallback callback
      ) {
         super(start, antic, preDelay, contact, recovery, end, hand, joint, collider);
         this.callback = callback;
      }

      public SyncOnlyPhase(
         float start,
         float antic,
         float preDelay,
         float contact,
         float recovery,
         float end,
         InteractionHand hand,
         float damageMulti,
         Joint joint,
         Collider collider,
         SyncCallback callback
      ) {
         super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, joint, collider);
         this.callback = callback;
      }

      public SyncOnlyPhase(
         float start,
         float antic,
         float preDelay,
         float contact,
         float recovery,
         float end,
         InteractionHand hand,
         float damageMulti,
         float phaseImpactMulti,
         Joint joint,
         Collider collider,
         SyncCallback callback
      ) {
         super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, phaseImpactMulti, joint, collider);
         this.callback = callback;
      }

      public SyncOnlyPhase(
         float start,
         float antic,
         float preDelay,
         float contact,
         float recovery,
         float end,
         InteractionHand hand,
         float damageMulti,
         float phaseImpactMulti,
         float phaseArmorNegationMulti,
         Joint joint,
         Collider collider,
         SyncCallback callback
      ) {
         super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, phaseImpactMulti, phaseArmorNegationMulti, joint, collider);
         this.callback = callback;
      }

      public SyncOnlyPhase(InteractionHand hand, Joint joint, Collider collider, SyncCallback callback) {
         super(hand, joint, collider);
         this.callback = callback;
      }

      public SyncOnlyPhase(
         float start,
         float antic,
         float preDelay,
         float contact,
         float recovery,
         float end,
         InteractionHand hand,
         JointColliderPair[] colliders,
         SyncCallback callback
      ) {
         super(start, antic, preDelay, contact, recovery, end, hand, colliders);
         this.callback = callback;
      }

      public SyncOnlyPhase(
         float start,
         float antic,
         float preDelay,
         float contact,
         float recovery,
         float end,
         InteractionHand hand,
         float damageMulti,
         JointColliderPair[] colliders,
         SyncCallback callback
      ) {
         super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, colliders);
         this.callback = callback;
      }

      public SyncOnlyPhase(
         float start,
         float antic,
         float preDelay,
         float contact,
         float recovery,
         float end,
         InteractionHand hand,
         float damageMulti,
         float phaseImpactMulti,
         JointColliderPair[] colliders,
         SyncCallback callback
      ) {
         super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, phaseImpactMulti, colliders);
         this.callback = callback;
      }

      public SyncOnlyPhase(
         float start,
         float antic,
         float preDelay,
         float contact,
         float recovery,
         float end,
         InteractionHand hand,
         float damageMulti,
         float phaseImpactMulti,
         float phaseArmorNegationMulti,
         JointColliderPair[] colliders,
         SyncCallback callback
      ) {
         super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, phaseImpactMulti, phaseArmorNegationMulti, colliders);
         this.callback = callback;
      }
   }
}
