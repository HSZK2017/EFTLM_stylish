package com.pla.annoyingvillagers.task;

import java.util.Objects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import net.shelmarow.combat_evolution.execution.ExecutionHandler;
import net.shelmarow.combat_evolution.execution.ExecutionTypeManager.Type;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionHitAnimation;
import net.shelmarow.combat_evolution.tickTask.TickTask;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MobExecutionTask extends TickTask {
   private final LivingEntity executor;
   private final LivingEntity target;
   private final Type executionType;
   private boolean cancelled = false;

   public MobExecutionTask(LivingEntity executor, LivingEntity target, Type executionType, int durationTicks) {
      super(durationTicks);
      this.executor = executor;
      this.target = target;
      this.executionType = executionType;
   }

   public void onStart() {
      ExecutionHandler.addExecutingTarget(this.target, this.executor);
      LivingEntityPatch<?> executorPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this.executor, LivingEntityPatch.class);
      LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this.target, LivingEntityPatch.class);
      this.executor.m_7292_(new MobEffectInstance((MobEffect)CEMobEffects.FULL_STUN_IMMUNITY.get(), 100, 1, true, false));
      this.target.m_7292_(new MobEffectInstance((MobEffect)CEMobEffects.FULL_STUN_IMMUNITY.get(), 100, 1, true, false));
      this.executor.m_7292_(new MobEffectInstance(MobEffects.f_19605_, 100, 4));
      if (executorPatch != null && targetPatch != null) {
         executorPatch.playAnimationSynchronized(this.executionType.executionAnimation(), 0.0F);
         targetPatch.playAnimationSynchronized(this.executionType.executedAnimation(), 0.0F);
         Vec3 from = this.executor.m_146892_();
         Vec3 to = this.target.m_146892_();
         double dx = to.f_82479_ - from.f_82479_;
         double dz = to.f_82481_ - from.f_82481_;
         float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0) + this.executionType.rotationOffset();
         executorPatch.setYRot(yaw);
      }
   }

   public void onTick() {
      if (!this.cancelled) {
         if (this.target.m_6084_()) {
            LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this.target, LivingEntityPatch.class);
            if (targetPatch == null) {
               this.cancelExecution(false);
               return;
            }

            if (targetPatch.getAnimator().getPlayerFor(null) == null) {
               this.cancelExecution(false);
               return;
            }

            AssetAccessor<? extends StaticAnimation> targetDynamicAnimation = Objects.requireNonNull(targetPatch.getAnimator().getPlayerFor(null))
               .getRealAnimation();
            if (!(targetDynamicAnimation.get() instanceof ExecutionHitAnimation)) {
               this.cancelExecution(true);
            }
         }
      }
   }

   private void cancelExecution(boolean rollExecutorBackward) {
      if (!this.cancelled) {
         this.cancelled = true;
         LivingEntityPatch<?> executorPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this.executor, LivingEntityPatch.class);
         LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this.target, LivingEntityPatch.class);
         if (executorPatch != null) {
            executorPatch.stopPlaying(this.executionType.executionAnimation());
            if (rollExecutorBackward) {
               executorPatch.playAnimationInstantly(Animations.BIPED_ROLL_BACKWARD);
            }
         }

         if (targetPatch != null) {
            targetPatch.stopPlaying(this.executionType.executedAnimation());
         }

         this.onFinish();
         this.tickTimer = this.maxTime;
      }
   }

   public void onFinish() {
      ExecutionHandler.removeExecutingTarget(this.target);
   }
}
