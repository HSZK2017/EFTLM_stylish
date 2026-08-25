package com.dmc.invincible_dmc.api.animation;

import java.util.Objects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public final class JointGroundFracture {
   private static final double TRACE_START_OFFSET = 0.5;

   private JointGroundFracture() {
   }

   public static boolean spawnBelowJoint(
      LivingEntityPatch<?> entityPatch, DynamicAnimation animation, Joint joint, float elapsedTime, double maximumGroundDistance, double radius
   ) {
      return spawnBelowJoint(entityPatch, animation, joint, elapsedTime, maximumGroundDistance, radius, false, true, false);
   }

   public static boolean spawnBelowJoint(
      LivingEntityPatch<?> entityPatch,
      DynamicAnimation animation,
      Joint joint,
      float elapsedTime,
      double maximumGroundDistance,
      double radius,
      boolean noSound,
      boolean noParticle,
      boolean hurtEntities
   ) {
      Objects.requireNonNull(entityPatch, "entityPatch");
      Objects.requireNonNull(animation, "animation");
      Objects.requireNonNull(joint, "joint");
      if (maximumGroundDistance < 0.0) {
         throw new IllegalArgumentException("maximumGroundDistance must not be negative");
      } else if (radius <= 0.0) {
         throw new IllegalArgumentException("radius must be greater than zero");
      } else {
         LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
         Vec3 jointPosition = getJointWorldPosition(entityPatch, animation, joint, elapsedTime);
         BlockHitResult groundHit = findGroundBelow(entity, jointPosition, maximumGroundDistance);
         if (groundHit.m_6662_() == Type.BLOCK && !(jointPosition.f_82480_ - groundHit.m_82450_().f_82480_ >= maximumGroundDistance)) {
            if (!entity.m_9236_().f_46443_) {
               LevelUtil.circleSlamFracture(entity, entity.m_9236_(), Vec3.m_82539_(groundHit.m_82425_()), radius, noSound, noParticle, hurtEntities);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public static Vec3 getJointWorldPosition(LivingEntityPatch<?> entityPatch, DynamicAnimation animation, Joint joint, float elapsedTime) {
      Pose pose = animation.getPoseByTime(entityPatch, elapsedTime, 1.0F);
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      OpenMatrix4f modelToWorld = OpenMatrix4f.createTranslation((float)entity.m_20185_(), (float)entity.m_20186_(), (float)entity.m_20189_())
         .rotateDeg(180.0F, Vec3f.Y_AXIS)
         .mulBack(entityPatch.getModelMatrix(1.0F));
      OpenMatrix4f jointToWorld = entityPatch.getArmature().getBoundTransformFor(pose, joint).mulFront(modelToWorld);
      return OpenMatrix4f.transform(jointToWorld, Vec3.f_82478_);
   }

   private static BlockHitResult findGroundBelow(LivingEntity entity, Vec3 jointPosition, double maximumGroundDistance) {
      Vec3 start = jointPosition.m_82520_(0.0, 0.5, 0.0);
      Vec3 end = jointPosition.m_82520_(0.0, -maximumGroundDistance, 0.0);
      return entity.m_9236_().m_45547_(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, entity));
   }
}
