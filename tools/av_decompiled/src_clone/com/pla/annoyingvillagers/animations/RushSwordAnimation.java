package com.pla.annoyingvillagers.animations;

import com.pla.annoyingvillagers.task.DelayedTask;
import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.effect.EpicFightMobEffects;

public class RushSwordAnimation extends BasicMultipleAttackAnimation {
   public RushSwordAnimation(
      float convertTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends BasicMultipleAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
   }

   public void begin(LivingEntityPatch<?> entitypatch) {
      super.begin(entitypatch);
      final LivingEntity livingEntity = (LivingEntity)entitypatch.getOriginal();
      Vec3 dashDir = livingEntity.m_20154_();
      if (livingEntity instanceof Mob mob) {
         LivingEntity target = mob.m_5448_();
         if (target != null && target.m_6084_()) {
            Vec3 toTarget = target.m_20182_().m_82546_(mob.m_20182_());
            dashDir = new Vec3(toTarget.f_82479_, 0.0, toTarget.f_82481_);
         }
      }

      final Vec3 dash = dashDir.m_82541_().m_82490_(2.2);
      livingEntity.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 60, 2, false, false));
      new DelayedTask(1) {
         @Override
         public void run() {
            Vec3 cur = livingEntity.m_20184_();
            livingEntity.m_20334_(cur.f_82479_ + dash.f_82479_, cur.f_82480_ + dash.f_82480_, cur.f_82481_ + dash.f_82481_);
            livingEntity.f_19812_ = true;
         }
      };
   }
}
