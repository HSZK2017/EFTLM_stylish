package com.dmc.invincible_dmc.entity.doppelganger;

import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerCapability;
import com.dmc.invincible_dmc.input.PlayerMovementFrame;
import java.util.Comparator;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

final class DoppelgangerTargetingController {
   private static final int PASSIVE_TARGET_MEMORY_TICKS = 60;
   private static final double PASSIVE_TARGET_MAX_DISTANCE = 32.0;
   private static final double PASSIVE_TARGET_MAX_DISTANCE_SQR = 1024.0;
   private static final double PASSIVE_TARGET_HALF_ANGLE_DEGREES = 55.0;
   private static final double PASSIVE_TARGET_MIN_DOT = Math.cos(Math.toRadians(55.0));
   private static final double ATTACK_TARGET_ACQUIRE_DISTANCE = 12.0;
   private static final double ATTACK_TARGET_ACQUIRE_DISTANCE_SQR = 144.0;

   private DoppelgangerTargetingController() {
   }

   @Nullable
   static LivingEntity selectFacingTarget(PlayerPatch<?> ownerPatch, DoppelgangerEntity doppel) {
      Player owner = (Player)ownerPatch.getOriginal();
      LivingEntity lockedTarget = ownerPatch.getTarget();
      if (isValidTarget(owner, doppel, lockedTarget)) {
         return lockedTarget;
      } else {
         LivingEntity recentTarget = owner.m_21214_();
         return isVisiblePassiveTarget(owner, doppel, recentTarget) ? recentTarget : null;
      }
   }

   @Nullable
   static LivingEntity selectAttackTarget(PlayerPatch<?> ownerPatch, DoppelgangerEntity doppel) {
      Player owner = (Player)ownerPatch.getOriginal();
      LivingEntity lockedTarget = ownerPatch.getTarget();
      if (isValidTarget(owner, doppel, lockedTarget)) {
         return lockedTarget;
      } else {
         LivingEntity recentTarget = owner.m_21214_();
         if (isVisiblePassiveTarget(owner, doppel, recentTarget) && doppel.m_20280_(recentTarget) <= 144.0) {
            return recentTarget;
         } else {
            LivingEntity currentTarget = doppel.m_5448_();
            return currentTarget != recentTarget && isVisibleAttackTarget(owner, doppel, currentTarget)
               ? currentTarget
               : doppel.m_9236_()
                  .m_6443_(LivingEntity.class, doppel.m_20191_().m_82400_(12.0), target -> isVisibleAttackTarget(owner, doppel, target))
                  .stream()
                  .min(Comparator.comparingDouble(doppel::m_20280_))
                  .orElse(null);
         }
      }
   }

   private static boolean isVisiblePassiveTarget(Player owner, DoppelgangerEntity doppel, @Nullable LivingEntity target) {
      if (!isValidTarget(owner, doppel, target)) {
         return false;
      } else if (owner.f_19797_ - owner.m_21215_() > 60) {
         return false;
      } else if (owner.m_20280_(target) > 1024.0) {
         return false;
      } else {
         return !owner.m_142582_(target) ? false : isInsideCameraCone(owner, target);
      }
   }

   private static boolean isVisibleAttackTarget(Player owner, DoppelgangerEntity doppel, LivingEntity target) {
      return isValidTarget(owner, doppel, target)
         && target.m_142066_()
         && doppel.m_20280_(target) <= 144.0
         && owner.m_20280_(target) <= 1024.0
         && owner.m_142582_(target)
         && doppel.m_142582_(target)
         && isInsideCameraCone(owner, target);
   }

   private static boolean isInsideCameraCone(Player owner, LivingEntity target) {
      PlayerMovementFrame movementFrame = owner.getCapability(DoppelgangerCapability.INSTANCE)
         .map(DoppelgangerCapability.IDoppelgangerData::getLastMovementFrame)
         .orElse(PlayerMovementFrame.EMPTY);
      float cameraYaw = movementFrame == PlayerMovementFrame.EMPTY ? owner.m_146908_() : movementFrame.cameraYaw();
      Vec3 cameraForward = horizontalDirection(cameraYaw);
      Vec3 toTarget = target.m_20191_().m_82399_().m_82546_(owner.m_146892_()).m_82542_(1.0, 0.0, 1.0);
      return toTarget.m_82556_() < 1.0E-6 || cameraForward.m_82526_(toTarget.m_82541_()) >= PASSIVE_TARGET_MIN_DOT;
   }

   private static boolean isValidTarget(Player owner, DoppelgangerEntity doppel, @Nullable LivingEntity target) {
      return target != null && target.m_6084_() && target.m_9236_() == owner.m_9236_() && target != owner && target != doppel && !owner.m_7307_(target);
   }

   private static Vec3 horizontalDirection(float yawDegrees) {
      double radians = Math.toRadians((double)yawDegrees);
      return new Vec3(-Math.sin(radians), 0.0, Math.cos(radians));
   }
}
