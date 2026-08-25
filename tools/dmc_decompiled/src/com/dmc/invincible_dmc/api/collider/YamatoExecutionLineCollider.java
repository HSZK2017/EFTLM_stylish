package com.dmc.invincible_dmc.api.collider;

import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public final class YamatoExecutionLineCollider {
   private static final double MIN_LINE_LENGTH_SQR = 1.0E-8;
   private final int sampleCount;
   private final Vec3 localStart;
   private final Vec3 localEnd;

   public YamatoExecutionLineCollider(int sampleCount, Vec3 localStart, Vec3 localEnd) {
      this.sampleCount = Math.max(sampleCount, 1);
      this.localStart = localStart;
      this.localEnd = localEnd;
   }

   @Nullable
   public YamatoExecutionLineCollider.HitSample resolveHitSample(LivingEntityPatch<?> attackerPatch, AttackAnimation animation, Entity target) {
      AnimationPlayer player = DMCAnimationUtils.getPlayerFor(attackerPatch, animation.getAccessor());
      if (player == null) {
         return null;
      } else {
         Armature armature = attackerPatch.getArmature();
         Joint toolJoint = armature.searchJointByName(((HumanoidArmature)Armatures.BIPED.get()).toolR.getName());
         if (toolJoint == null) {
            return null;
         } else {
            LivingEntity attacker = (LivingEntity)attackerPatch.getOriginal();
            Vec3 targetCenter = target.m_20191_().m_82399_();
            float prevElapsedTime = player.getPrevElapsedTime();
            float elapsedTime = player.getElapsedTime();
            YamatoExecutionLineCollider.HitSample closestSample = null;
            double closestDistance = Double.POSITIVE_INFINITY;

            for (int index = 0; index < this.sampleCount; index++) {
               float interpolation = this.sampleCount == 1 ? 1.0F : (float)index / (float)(this.sampleCount - 1);
               float sampleTime = Mth.m_14179_(interpolation, prevElapsedTime, elapsedTime);
               Pose pose = animation.getPoseByTime(attackerPatch, sampleTime, 1.0F);
               double sampleX = attackerPatch.getXOld() + (attacker.m_20185_() - attackerPatch.getXOld()) * (double)interpolation;
               double sampleY = attackerPatch.getYOld() + (attacker.m_20186_() - attackerPatch.getYOld()) * (double)interpolation;
               double sampleZ = attackerPatch.getZOld() + (attacker.m_20189_() - attackerPatch.getZOld()) * (double)interpolation;
               OpenMatrix4f modelToWorld = OpenMatrix4f.createTranslation((float)sampleX, (float)sampleY, (float)sampleZ)
                  .rotateDeg(180.0F, Vec3f.Y_AXIS)
                  .mulBack(attackerPatch.getModelMatrix(interpolation));
               OpenMatrix4f toolToWorld = armature.getBoundTransformFor(pose, toolJoint).mulFront(modelToWorld);
               Vec3 worldStart = OpenMatrix4f.transform(toolToWorld, this.localStart);
               Vec3 worldEnd = OpenMatrix4f.transform(toolToWorld, this.localEnd);
               Vec3 worldLine = worldEnd.m_82546_(worldStart);
               double lineLengthSqr = worldLine.m_82556_();
               if (!(lineLengthSqr <= 1.0E-8)) {
                  double linePosition = Mth.m_14008_(targetCenter.m_82546_(worldStart).m_82526_(worldLine) / lineLengthSqr, 0.0, 1.0);
                  Vec3 worldAnchor = worldStart.m_82549_(worldLine.m_82490_(linePosition));
                  double distance = worldAnchor.m_82557_(targetCenter);
                  if (distance < closestDistance) {
                     closestDistance = distance;
                     closestSample = new YamatoExecutionLineCollider.HitSample(
                        this.localStart.m_165921_(this.localEnd, linePosition),
                        worldAnchor,
                        Mth.m_14189_(interpolation, attackerPatch.getYRotO(), attackerPatch.getYRot())
                     );
                  }
               }
            }

            return closestSample;
         }
      }
   }

   public static record HitSample(Vec3 localAnchor, Vec3 worldAnchor, float attackerYaw) {
   }
}
