package com.dmc.invincible_dmc.api.animation.types.yamato;

import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunPhase;
import com.dmc.invincible_dmc.api.animation.types.customStun.ICustomStunDamageSource;
import com.dmc.invincible_dmc.api.collider.YamatoExecutionLineCollider;
import com.dmc.invincible_dmc.api.stun.StrongStunController;
import com.dmc.invincible_dmc.gameassets.animations.stun.CustomStunAnimations;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.YamatoExecutionTargetManager;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.merlin204.avalon.epicfight.api.AnimationAttackResultEvent;
import com.merlin204.avalon.epicfight.api.AnimationAttackResultEvent.SimpleEvent;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class YamatoExecutionAnimation extends YamatoAttackAnimation {
   private static final double POSITION_EPSILON_SQR = 1.0E-6;
   private final YamatoExecutionAnimation.Stage stage;
   @Nullable
   private final YamatoExecutionLineCollider positionResolver;
   @Nullable
   private final Supplier<AnimationAccessor<? extends StaticAnimation>> finisherAnimation;
   @Nullable
   private YamatoExecutionTargetManager.CapturedTarget cachedCapturedTarget;

   public YamatoExecutionAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float playSpeed,
      float damageMultiplier,
      YamatoExecutionAnimation.Stage stage,
      @Nullable YamatoExecutionLineCollider positionResolver,
      @Nullable Supplier<AnimationAccessor<? extends StaticAnimation>> finisherAnimation,
      CustomStunPhase... phases
   ) {
      super(transitionTime, accessor, armature, playSpeed, damageMultiplier, phases);
      this.stage = stage;
      this.positionResolver = positionResolver;
      this.finisherAnimation = finisherAnimation;
      this.addProperty(StaticAnimationProperty.POSE_MODIFIER, null);
      this.addAttackResultEvents(new AnimationAttackResultEvent[]{SimpleEvent.create(this::onExecutionAttackResult)});
   }

   @Override
   public void begin(LivingEntityPatch<?> entityPatch) {
      super.begin(entityPatch);
      if (!entityPatch.isLogicalClient()) {
         if (this.stage == YamatoExecutionAnimation.Stage.DASH_GRAB) {
            YamatoExecutionTargetManager.beginDash(entityPatch);
         }
      }
   }

   @Override
   public void tick(LivingEntityPatch<?> entityPatch) {
      super.tick(entityPatch);
      if (!entityPatch.isLogicalClient() && this.stage == YamatoExecutionAnimation.Stage.FINISH_WITHDRAWAL) {
         YamatoExecutionTargetManager.CapturedTarget capturedTarget = YamatoExecutionTargetManager.consumeCapturedTarget(entityPatch);
         if (capturedTarget != null) {
            this.cachedCapturedTarget = capturedTarget;
            this.correctTargetPositionOnce(entityPatch, capturedTarget);
         }
      }
   }

   @Override
   public void end(LivingEntityPatch<?> entityPatch, @Nullable AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
      AnimationPlayer animationPlayer = DMCAnimationUtils.getPlayerFor(entityPatch, this.getAccessor());
      float elapsedTime = animationPlayer == null ? Float.POSITIVE_INFINITY : animationPlayer.getElapsedTime();
      boolean interruptedBeforeDodge = !entityPatch.isLogicalClient()
         && this.stage == YamatoExecutionAnimation.Stage.FINISH_WITHDRAWAL
         && !isEnd
         && this.getProperty(CAN_DODGE_TIME).map(timePairs -> !timePairs.isTimeInPairs(elapsedTime)).orElse(false);
      YamatoExecutionTargetManager.CapturedTarget capturedTarget = interruptedBeforeDodge ? this.getCapturedTarget(entityPatch) : null;
      super.end(entityPatch, nextAnimation, isEnd);
      if (!entityPatch.isLogicalClient()) {
         if (this.stage == YamatoExecutionAnimation.Stage.DASH_GRAB) {
            if (!this.isFinisherTransition(nextAnimation)) {
               StrongStunController.finishOwnedTargets((LivingEntity)entityPatch.getOriginal(), "execution_dash_interrupted");
               YamatoExecutionTargetManager.abort(entityPatch);
            }
         } else {
            StrongStunController.finishOwnedTargets((LivingEntity)entityPatch.getOriginal(), "execution_finished");
            if (capturedTarget != null) {
               this.applyInterruptedExecutionHit(entityPatch, capturedTarget, elapsedTime);
            }

            this.cachedCapturedTarget = null;
            YamatoExecutionTargetManager.finish(entityPatch);
         }
      }
   }

   public void onSecondaryPositionCorrection(LivingEntityPatch<?> entityPatch) {
      if (!entityPatch.isLogicalClient() && this.stage == YamatoExecutionAnimation.Stage.FINISH_WITHDRAWAL && this.cachedCapturedTarget != null) {
         this.correctTargetPositionOnce(entityPatch, this.cachedCapturedTarget);
      }
   }

   private void onExecutionAttackResult(LivingEntityPatch<?> entityPatch, Entity target, AttackResult attackResult) {
      if (!entityPatch.isLogicalClient() && attackResult.resultType.dealtDamage()) {
         if (this.stage == YamatoExecutionAnimation.Stage.DASH_GRAB) {
            YamatoExecutionLineCollider.HitSample hitSample = this.positionResolver == null
               ? null
               : this.positionResolver.resolveHitSample(entityPatch, this, target);
            if (hitSample == null || !YamatoExecutionTargetManager.capture(entityPatch, target, hitSample) || this.finisherAnimation == null) {
               return;
            }

            AnimationAccessor<? extends StaticAnimation> finisher = this.finisherAnimation.get();
            if (finisher != null) {
               entityPatch.playAnimationInstantly(finisher);
               entityPatch.playSound(SoundEvents.f_12516_, 1.0F, 1.0F);
            }
         }
      }
   }

   private boolean isFinisherTransition(@Nullable AssetAccessor<? extends DynamicAnimation> nextAnimation) {
      if (nextAnimation != null && this.finisherAnimation != null) {
         AnimationAccessor<? extends StaticAnimation> finisher = this.finisherAnimation.get();
         return finisher != null && nextAnimation == finisher;
      } else {
         return false;
      }
   }

   @Nullable
   private YamatoExecutionTargetManager.CapturedTarget getCapturedTarget(LivingEntityPatch<?> entityPatch) {
      return this.cachedCapturedTarget != null ? this.cachedCapturedTarget : YamatoExecutionTargetManager.consumeCapturedTarget(entityPatch);
   }

   private void applyInterruptedExecutionHit(LivingEntityPatch<?> attackerPatch, YamatoExecutionTargetManager.CapturedTarget capturedTarget, float elapsedTime) {
      LivingEntity target = this.resolveCapturedTarget(attackerPatch, capturedTarget);
      if (target != null) {
         Phase phase = this.getPhaseByTime(elapsedTime);
         EpicFightDamageSource damageSource = this.getEpicFightDamageSource(attackerPatch, target, phase);
         if (damageSource instanceof ICustomStunDamageSource customStunDamageSource) {
            customStunDamageSource.invincible$setCustomStunAnimations(CustomStunAnimations.HIT_UP_1, null, CustomStunAnimations.HIT_UP_1, null);
         }

         int previousInvulnerableTime = target.f_19802_;
         target.f_19802_ = 0;

         try {
            attackerPatch.attack(damageSource, target, phase.hand);
         } finally {
            target.f_19802_ = previousInvulnerableTime;
         }
      }
   }

   @Nullable
   private LivingEntity resolveCapturedTarget(LivingEntityPatch<?> attackerPatch, YamatoExecutionTargetManager.CapturedTarget capturedTarget) {
      LivingEntity attacker = (LivingEntity)attackerPatch.getOriginal();
      if (!(attacker.m_9236_() instanceof ServerLevel serverLevel) || !serverLevel.m_46472_().equals(capturedTarget.dimension())) {
         return null;
      }

      if (serverLevel.m_8791_(capturedTarget.targetId()) instanceof LivingEntity target && target.m_6084_() && target.m_9236_() == attacker.m_9236_()) {
         return target;
      }

      return null;
   }

   private void correctTargetPositionOnce(LivingEntityPatch<?> attackerPatch, YamatoExecutionTargetManager.CapturedTarget capturedTarget) {
      LivingEntity attacker = (LivingEntity)attackerPatch.getOriginal();
      if (!(attacker.m_9236_() instanceof ServerLevel serverLevel) || !serverLevel.m_46472_().equals(capturedTarget.dimension())) {
         return;
      }

      if (serverLevel.m_8791_(capturedTarget.targetId()) instanceof LivingEntity target && target.m_6084_() && target.m_9236_() == attacker.m_9236_()) {
         Vec3 bladeAnchor = this.resolveBladeAnchor(attackerPatch, capturedTarget.localBladeAnchor());
         if (bladeAnchor == null) {
            return;
         }

         Vec3 targetOrigin = resolveTargetOriginForCenter(target, bladeAnchor);
         Vec3 attackerPos = attacker.m_20182_();
         double maxDistance = 1.5;
         if (targetOrigin.m_82557_(attackerPos) > maxDistance * maxDistance) {
            Vec3 direction = targetOrigin.m_82546_(attackerPos).m_82541_();
            targetOrigin = attackerPos.m_82549_(direction.m_82490_(1.35));
         }

         double minDistance = 1.2;
         if (targetOrigin.m_82557_(attackerPos) < minDistance * minDistance) {
            Vec3 direction = targetOrigin.m_82546_(attackerPos).m_82541_();
            targetOrigin = attackerPos.m_82549_(direction.m_82490_(1.35));
         }

         applyTargetTransform(target, targetOrigin, capturedTarget.targetYaw(), attacker.m_20186_());
         return;
      }
   }

   @Nullable
   private Vec3 resolveBladeAnchor(LivingEntityPatch<?> attackerPatch, Vec3 localBladeAnchor) {
      AnimationPlayer player = DMCAnimationUtils.getPlayerFor(attackerPatch, this.getAccessor());
      if (player != null && DMCAnimationUtils.getRealAnimationAccessor(player) != null) {
         Armature armature = attackerPatch.getArmature();
         Joint toolJoint = armature.searchJointByName(((HumanoidArmature)Armatures.BIPED.get()).toolR.getName());
         if (toolJoint == null) {
            return null;
         } else {
            Pose pose = this.getPoseByTime(attackerPatch, player.getElapsedTime(), 1.0F);
            LivingEntity attacker = (LivingEntity)attackerPatch.getOriginal();
            OpenMatrix4f modelToWorld = OpenMatrix4f.createTranslation((float)attacker.m_20185_(), (float)attacker.m_20186_(), (float)attacker.m_20189_())
               .rotateDeg(180.0F, Vec3f.Y_AXIS)
               .mulBack(attackerPatch.getModelMatrix(1.0F));
            OpenMatrix4f toolToWorld = armature.getBoundTransformFor(pose, toolJoint).mulFront(modelToWorld);
            return OpenMatrix4f.transform(toolToWorld, localBladeAnchor);
         }
      } else {
         return null;
      }
   }

   private static Vec3 resolveTargetOriginForCenter(LivingEntity target, Vec3 desiredCenter) {
      Vec3 centerOffset = target.m_20191_().m_82399_().m_82546_(target.m_20182_());
      return desiredCenter.m_82546_(centerOffset);
   }

   private static void applyTargetTransform(LivingEntity target, Vec3 destination, float targetYaw, double minimumY) {
      destination = new Vec3(destination.f_82479_, Math.max(destination.f_82480_, minimumY), destination.f_82481_);
      target.m_20256_(Vec3.f_82478_);
      target.f_19789_ = 0.0F;
      if (target instanceof Mob mob) {
         mob.m_21573_().m_26573_();
      }

      boolean positionChanged = target.m_20182_().m_82557_(destination) > 1.0E-6;
      boolean rotationChanged = Math.abs(Mth.m_14177_(target.m_146908_() - targetYaw)) > 0.01F || Math.abs(target.m_146909_()) > 0.01F;
      if (target instanceof ServerPlayer serverPlayer) {
         if (positionChanged || rotationChanged) {
            serverPlayer.f_8906_.m_9774_(destination.f_82479_, destination.f_82480_, destination.f_82481_, targetYaw, 0.0F);
         }
      } else if (positionChanged) {
         target.m_6021_(destination.f_82479_, destination.f_82480_, destination.f_82481_);
      }

      target.m_146922_(targetYaw);
      target.m_146926_(0.0F);
      target.m_5618_(targetYaw);
      target.m_5616_(targetYaw);
      target.f_19859_ = targetYaw;
      target.f_19860_ = 0.0F;
      target.f_20884_ = targetYaw;
      target.f_20886_ = targetYaw;
      target.f_19864_ = true;
   }

   public static enum Stage {
      DASH_GRAB,
      FINISH_WITHDRAWAL;
   }
}
