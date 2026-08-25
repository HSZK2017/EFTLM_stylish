package com.dmc.invincible_dmc.utils.yamato;

import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import java.util.Objects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.JointColliderPair;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class AttackAnalyzer {
   public static AttackAnalyzer.AttackDirection analyzeAttackDirection(LivingEntityPatch<?> defenderPatch, LivingEntity targetEntity) {
      try {
         EntityPatch targetPatch = (EntityPatch)targetEntity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).resolve().orElse(null);
         if (targetPatch instanceof LivingEntityPatch<?> livingTargetPatch) {
            AnimationPlayer animPlayer = DMCAnimationUtils.getMainPlayer(livingTargetPatch);
            if (animPlayer != null) {
               DynamicAnimation currentAnimation = DMCAnimationUtils.getCurrentAnimation(animPlayer);
               AttackAnimation attackAnimation = DMCAnimationUtils.asAnimation(currentAnimation, AttackAnimation.class);
               if (attackAnimation != null) {
                  float elapsedTime = animPlayer.getElapsedTime();
                  Phase currentPhase = attackAnimation.getPhaseByTime(elapsedTime);
                  JointColliderPair[] colliderPairs = currentPhase.getColliders();
                  int var11 = colliderPairs.length;
                  byte var12 = 0;
                  if (var12 < var11) {
                     JointColliderPair pair = colliderPairs[var12];
                     Joint colliderJoint = (Joint)pair.getFirst();
                     return determineJointAttackSide(defenderPatch, livingTargetPatch, attackAnimation, colliderJoint, elapsedTime, targetEntity);
                  }
               }
            }
         }
      } catch (Exception var15) {
         var15.printStackTrace();
      }

      return AttackAnalyzer.AttackDirection.FRONT_ATTACK;
   }

   private static AttackAnalyzer.AttackDirection determineJointAttackSide(
      LivingEntityPatch<?> defenderPatch,
      LivingEntityPatch<?> targetPatch,
      AttackAnimation attackAnimation,
      Joint colliderJoint,
      float elapsedTime,
      LivingEntity targetEntity
   ) {
      Vec3 position = ((LivingEntity)defenderPatch.getOriginal()).m_20182_();
      Vec3 viewVector = ((LivingEntity)defenderPatch.getOriginal()).m_20252_(1.0F);
      float totalTime = attackAnimation.getTotalTime();
      float startTime = Math.max(elapsedTime - 0.3F, 0.0F);
      float endTime = Math.min(elapsedTime + 0.2F, totalTime);
      float[] sampleTimes = new float[]{
         startTime,
         Math.max(elapsedTime - 0.2F, 0.0F),
         Math.max(elapsedTime - 0.1F, 0.0F),
         elapsedTime - 0.05F,
         elapsedTime,
         elapsedTime + 0.05F,
         elapsedTime + 0.1F,
         endTime
      };
      Vec3[] samplePositions = new Vec3[sampleTimes.length];

      for (int i = 0; i < sampleTimes.length; i++) {
         samplePositions[i] = getJointWorldRawPos(targetPatch, colliderJoint, sampleTimes[i]);
      }

      Vec3 overallMovement = samplePositions[samplePositions.length - 1].m_82546_(samplePositions[0]);
      if (overallMovement.m_82556_() < 1.0E-4) {
         Vec3 currentPos = samplePositions[4];
         Vec3 toJoint = currentPos.m_82546_(position);
         return determineStaticPositionSide(viewVector, toJoint);
      } else {
         AttackAnalyzer.AttackType thrustResult = determineIfThrustAttackExtended(overallMovement, viewVector, samplePositions, targetEntity);
         return thrustResult != null
            ? AttackAnalyzer.AttackDirection.FRONT_ATTACK
            : determineSideByCrossProduct(viewVector, samplePositions[0], samplePositions[samplePositions.length - 1]);
      }
   }

   private static AttackAnalyzer.AttackType determineIfThrustAttackExtended(Vec3 movement, Vec3 lookVec, Vec3[] samplePositions, LivingEntity targetEntity) {
      Vec3 movementDirection = movement.m_82541_();
      Vec3 lookHorizontal = new Vec3(lookVec.f_82479_, 0.0, lookVec.f_82481_).m_82541_();
      Vec3 movementHorizontal = new Vec3(movementDirection.f_82479_, 0.0, movementDirection.f_82481_).m_82541_();
      double dotProduct = movementHorizontal.m_82526_(lookHorizontal);
      double movementLengthSqr = movement.m_82556_();
      double stability = calculateMovementStability(samplePositions, lookHorizontal);
      double forwardDominance = calculateForwardDominance(samplePositions, lookHorizontal);
      double verticalRatio = calculateVerticalRatio(samplePositions);
      boolean isTargetAirborne = isTargetAirborne(targetEntity, samplePositions);
      if (verticalRatio > 0.9) {
         return AttackAnalyzer.AttackType.VERTICAL_SLAM;
      } else {
         boolean isThrust = false;
         double verticalThreshold = isTargetAirborne ? 0.4 : 0.3;
         if (verticalRatio > verticalThreshold) {
            return null;
         } else {
            boolean hasGoodDirection = Math.abs(dotProduct) > 0.75;
            boolean hasGoodStability = stability > 0.8;
            boolean hasGoodForwardDominance = forwardDominance > 0.7;
            boolean hasReasonableDistance = movementLengthSqr > 0.64;
            if (isTargetAirborne) {
               hasGoodForwardDominance = forwardDominance > 0.6;
               hasGoodStability = stability > 0.7;
            }

            if (hasGoodDirection && hasGoodStability && hasGoodForwardDominance && hasReasonableDistance) {
               double thrustScore = Math.abs(dotProduct) * 0.25 + stability * 0.35 + forwardDominance * 0.3;
               double scoreThreshold = isTargetAirborne ? 0.75 : 0.78;
               isThrust = thrustScore > scoreThreshold;
               if (stability > 0.95 && forwardDominance > 0.8) {
                  isThrust = true;
               }

               if (Math.abs(dotProduct) > 0.99 && stability > 0.8) {
                  isThrust = true;
               }
            }

            if (isThrust) {
               return dotProduct > 0.0 ? AttackAnalyzer.AttackType.THRUST_FRONT : AttackAnalyzer.AttackType.THRUST_BACK;
            } else {
               return null;
            }
         }
      }
   }

   private static boolean isTargetAirborne(LivingEntity targetEntity, Vec3[] samplePositions) {
      if (targetEntity == null) {
         return false;
      } else {
         return !targetEntity.m_20096_() ? true : analyzeAirborneFromTrajectory(samplePositions);
      }
   }

   private static boolean analyzeAirborneFromTrajectory(Vec3[] samplePositions) {
      if (samplePositions.length < 3) {
         return false;
      } else {
         int peakIndex = findPeakIndex(samplePositions);
         if (peakIndex > 0 && peakIndex < samplePositions.length - 1) {
            double riseHeight = samplePositions[peakIndex].f_82480_ - samplePositions[0].f_82480_;
            double fallHeight = samplePositions[peakIndex].f_82480_ - samplePositions[samplePositions.length - 1].f_82480_;
            if (riseHeight > 0.3 && fallHeight > 0.2) {
               return true;
            }
         }

         double minY = samplePositions[0].f_82480_;
         double maxY = samplePositions[0].f_82480_;

         for (Vec3 pos : samplePositions) {
            minY = Math.min(minY, pos.f_82480_);
            maxY = Math.max(maxY, pos.f_82480_);
         }

         return maxY - minY > 1.0;
      }
   }

   private static int findPeakIndex(Vec3[] positions) {
      int peakIndex = 0;
      double maxHeight = positions[0].f_82480_;

      for (int i = 1; i < positions.length; i++) {
         if (positions[i].f_82480_ > maxHeight) {
            maxHeight = positions[i].f_82480_;
            peakIndex = i;
         }
      }

      return peakIndex;
   }

   private static double calculateVerticalRatio(Vec3[] samplePositions) {
      if (samplePositions.length < 2) {
         return 0.0;
      } else {
         double maxVerticalMovement = 0.0;
         double totalVerticalVariation = 0.0;

         for (int i = 1; i < samplePositions.length; i++) {
            double verticalChange = Math.abs(samplePositions[i].f_82480_ - samplePositions[i - 1].f_82480_);
            maxVerticalMovement = Math.max(maxVerticalMovement, verticalChange);
            totalVerticalVariation += verticalChange;
         }

         double totalHeightChange = Math.abs(samplePositions[samplePositions.length - 1].f_82480_ - samplePositions[0].f_82480_);
         double averageVerticalVariation = totalVerticalVariation / (double)(samplePositions.length - 1);
         double verticalScore = maxVerticalMovement * 0.4 + averageVerticalVariation * 0.3 + totalHeightChange * 0.3;
         return Math.min(verticalScore / 2.0, 1.0);
      }
   }

   private static double calculateMovementStability(Vec3[] positions, Vec3 referenceDirection) {
      if (positions.length < 3) {
         return 1.0;
      } else {
         double totalStability = 0.0;
         int validSegments = 0;

         for (int i = 1; i < positions.length; i++) {
            Vec3 segmentMovement = positions[i].m_82546_(positions[i - 1]);
            if (segmentMovement.m_82556_() > 1.0E-4) {
               Vec3 segmentDirection = segmentMovement.m_82541_();
               Vec3 segmentDirectionHorizontal = new Vec3(segmentDirection.f_82479_, 0.0, segmentDirection.f_82481_).m_82541_();
               double consistency = Math.abs(segmentDirectionHorizontal.m_82526_(referenceDirection));
               totalStability += consistency;
               validSegments++;
            }
         }

         return validSegments > 0 ? totalStability / (double)validSegments : 1.0;
      }
   }

   private static double calculateForwardDominance(Vec3[] positions, Vec3 forwardDirection) {
      if (positions.length < 2) {
         return 1.0;
      } else {
         double totalForward = 0.0;
         double totalLateral = 0.0;

         for (int i = 1; i < positions.length; i++) {
            Vec3 segment = positions[i].m_82546_(positions[i - 1]);
            if (segment.m_82556_() > 1.0E-4) {
               Vec3 segmentHorizontal = new Vec3(segment.f_82479_, 0.0, segment.f_82481_);
               double forwardComponent = segmentHorizontal.m_82526_(forwardDirection);
               totalForward += Math.abs(forwardComponent);
               Vec3 lateralComponent = segmentHorizontal.m_82546_(forwardDirection.m_82490_(forwardComponent));
               totalLateral += lateralComponent.m_82553_();
            }
         }

         double totalMovement = totalForward + totalLateral;
         return totalMovement > 0.0 ? totalForward / totalMovement : 1.0;
      }
   }

   private static AttackAnalyzer.AttackDirection determineSideByCrossProduct(Vec3 lookVec, Vec3 startPos, Vec3 endPos) {
      Vec3 movement = endPos.m_82546_(startPos);
      Vec3 movementDirection = movement.m_82541_();
      Vec3 lookHorizontal = new Vec3(lookVec.f_82479_, 0.0, lookVec.f_82481_).m_82541_();
      Vec3 movementHorizontal = new Vec3(movementDirection.f_82479_, 0.0, movementDirection.f_82481_).m_82541_();
      double crossY = lookHorizontal.f_82479_ * movementHorizontal.f_82481_ - lookHorizontal.f_82481_ * movementHorizontal.f_82479_;
      double dotProduct = lookHorizontal.m_82526_(movementHorizontal);
      double adjustedThreshold = Math.abs(dotProduct) > 0.9 ? 0.1 : 0.15;
      if (crossY > adjustedThreshold) {
         return AttackAnalyzer.AttackDirection.LEFT_ATTACK;
      } else if (crossY < -adjustedThreshold) {
         return AttackAnalyzer.AttackDirection.RIGHT_ATTACK;
      } else if (crossY > adjustedThreshold * 0.5) {
         return AttackAnalyzer.AttackDirection.LEFT_SLIGHT_ATTACK;
      } else {
         return crossY < -adjustedThreshold * 0.5 ? AttackAnalyzer.AttackDirection.RIGHT_SLIGHT_ATTACK : AttackAnalyzer.AttackDirection.FRONT_ATTACK;
      }
   }

   private static AttackAnalyzer.AttackDirection determineStaticPositionSide(Vec3 lookVec, Vec3 toJoint) {
      Vec3 lookHorizontal = new Vec3(lookVec.f_82479_, 0.0, lookVec.f_82481_).m_82541_();
      Vec3 toJointHorizontal = new Vec3(toJoint.f_82479_, 0.0, toJoint.f_82481_).m_82541_();
      double crossY = lookHorizontal.f_82479_ * toJointHorizontal.f_82481_ - lookHorizontal.f_82481_ * toJointHorizontal.f_82479_;
      if (crossY > 0.05) {
         return AttackAnalyzer.AttackDirection.LEFT_SIDE;
      } else {
         return crossY < -0.05 ? AttackAnalyzer.AttackDirection.RIGHT_SIDE : AttackAnalyzer.AttackDirection.FRONT_SIDE;
      }
   }

   public static Vec3 getJointWorldRawPos(LivingEntityPatch<?> entityPatch, Joint joint, float time) {
      return getJointWorldRawPos(entityPatch, joint, time, Vec3f.ZERO);
   }

   public static Vec3 getJointWorldRawPos(LivingEntityPatch<?> entityPatch, Joint joint, float time, Vec3f offset) {
      Animator animator = entityPatch.getAnimator();
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      DynamicAnimation animation = Objects.requireNonNull(DMCAnimationUtils.getCurrentAnimation(DMCAnimationUtils.getMainPlayer(animator)));
      Pose pose = animation.getRawPose(time);
      OpenMatrix4f transformMatrix = entityPatch.getArmature().getBoundTransformFor(pose, joint);
      transformMatrix.translate(offset);
      OpenMatrix4f rotation = new OpenMatrix4f().rotate(-((float)Math.toRadians((double)(entityPatch.getYRot() + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F));
      OpenMatrix4f.mul(rotation, transformMatrix, transformMatrix);
      return new Vec3(
         (double)(transformMatrix.m30 + (float)entity.m_20185_()),
         (double)(transformMatrix.m31 + (float)entity.m_20186_()),
         (double)(transformMatrix.m32 + (float)entity.m_20189_())
      );
   }

   public static enum AttackDirection {
      LEFT_ATTACK("left_attack"),
      RIGHT_ATTACK("right_attack"),
      LEFT_SLIGHT_ATTACK("left_slight_attack"),
      RIGHT_SLIGHT_ATTACK("right_slight_attack"),
      FRONT_ATTACK("front_attack"),
      LEFT_SIDE("left_side"),
      RIGHT_SIDE("right_side"),
      FRONT_SIDE("front_side");

      private final String displayName;

      private AttackDirection(String displayName) {
         this.displayName = displayName;
      }

      public String getDisplayName() {
         return this.displayName;
      }
   }

   public static enum AttackType {
      THRUST_FRONT("thrust_front"),
      THRUST_BACK("thrust_back"),
      VERTICAL_SLAM("vertical_slam"),
      NONE("none");

      private final String displayName;

      private AttackType(String displayName) {
         this.displayName = displayName;
      }

      public String getDisplayName() {
         return this.displayName;
      }
   }
}
