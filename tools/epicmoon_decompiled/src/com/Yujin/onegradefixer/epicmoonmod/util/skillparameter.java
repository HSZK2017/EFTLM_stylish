package com.Yujin.onegradefixer.epicmoonmod.util;

import com.Yujin.onegradefixer.epicmoonmod.particle.EMparticles;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import mod.chloeprime.aaaparticles.api.client.EffectRegistry;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.property.AnimationEvent.E0;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public final class skillparameter {
   private static final Map<UUID, Float> PRE_ARMOR_DAMAGE = new ConcurrentHashMap<>();
   public static final Map<UUID, Vec3> PREVIOUS_SPARK_POS = new HashMap<>();
   public static final Map<UUID, Vec3> PREVIOUS_RIGHT_EYE = new HashMap<>();

   private skillparameter() {
   }

   public static void set(UUID playerId, float damage) {
      PRE_ARMOR_DAMAGE.put(playerId, damage);
   }

   public static float consume(UUID playerId) {
      Float damage = PRE_ARMOR_DAMAGE.remove(playerId);
      return damage != null ? damage : -1.0F;
   }

   public static void clear(UUID playerId) {
      PRE_ARMOR_DAMAGE.remove(playerId);
   }

   public static InPeriodEvent<E0> sparkleTrail(float startTime, float endTime, InteractionHand hand) {
      return InPeriodEvent.create(
         startTime,
         endTime,
         (E0)(livingEntityPatch, animation, params) -> {
            LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
            if (entity.m_9236_().f_46443_) {
               Joint joint = livingEntityPatch.getParentJointOfHand(hand);
               if (joint != null) {
                  float partialTick = Minecraft.m_91087_().m_91296_();
                  Pose pose = livingEntityPatch.getAnimator().getPose(partialTick);
                  OpenMatrix4f modelMatrix = livingEntityPatch.getModelMatrix(partialTick);
                  OpenMatrix4f jointMatrix = livingEntityPatch.getArmature().getBoundTransformFor(pose, joint);
                  OpenMatrix4f worldMatrix = OpenMatrix4f.mul(modelMatrix, jointMatrix, null);
                  Vec3 bladeTipLocal = new Vec3(0.0, 0.0, -2.3);
                  Vec3 worldOffset = OpenMatrix4f.transform(worldMatrix, bladeTipLocal);
                  Vec3 currentPosition = new Vec3(
                     entity.m_20185_() + worldOffset.f_82479_, entity.m_20186_() + worldOffset.f_82480_, entity.m_20189_() + worldOffset.f_82481_
                  );
                  UUID entityId = entity.m_20148_();
                  Vec3 previousPosition = PREVIOUS_SPARK_POS.get(entityId);
                  if (previousPosition == null) {
                     previousPosition = currentPosition;
                  }

                  double distance = previousPosition.m_82554_(currentPosition);
                  int steps = Math.max(1, (int)Math.ceil(distance / 0.12));
                  RandomSource random = entity.m_217043_();

                  for (int i = 0; i <= steps; i++) {
                     double progress = (double)i / (double)steps;
                     Vec3 position = previousPosition.m_165921_(currentPosition, progress);
                     if (random.m_188501_() > 0.8F) {
                        return;
                     }

                     double spread = 0.12;
                     double offsetX = (random.m_188500_() - 0.5) * spread;
                     double offsetY = (random.m_188500_() - 0.5) * spread;
                     double offsetZ = (random.m_188500_() - 0.5) * spread;
                     entity.m_9236_()
                        .m_7106_(
                           (ParticleOptions)EMparticles.STAR.get(),
                           currentPosition.f_82479_ + offsetX,
                           currentPosition.f_82480_ + offsetY,
                           currentPosition.f_82481_ + offsetZ,
                           0.0,
                           0.0,
                           0.0
                        );
                  }

                  PREVIOUS_SPARK_POS.put(entityId, currentPosition);
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InPeriodEvent<E0> sparkleTrail2(float startTime, float endTime, InteractionHand hand) {
      return InPeriodEvent.create(
         startTime,
         endTime,
         (E0)(livingEntityPatch, animation, params) -> {
            LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
            if (entity.m_9236_().f_46443_) {
               Joint joint = livingEntityPatch.getParentJointOfHand(hand);
               if (joint != null) {
                  float partialTick = Minecraft.m_91087_().m_91296_();
                  Pose pose = livingEntityPatch.getAnimator().getPose(partialTick);
                  OpenMatrix4f modelMatrix = livingEntityPatch.getModelMatrix(partialTick);
                  OpenMatrix4f jointMatrix = livingEntityPatch.getArmature().getBoundTransformFor(pose, joint);
                  OpenMatrix4f worldMatrix = OpenMatrix4f.mul(modelMatrix, jointMatrix, null);
                  Vec3 bladeTipLocal = new Vec3(0.0, 0.0, 2.3);
                  Vec3 worldOffset = OpenMatrix4f.transform(worldMatrix, bladeTipLocal);
                  Vec3 currentPosition = new Vec3(
                     entity.m_20185_() + worldOffset.f_82479_, entity.m_20186_() + worldOffset.f_82480_, entity.m_20189_() + worldOffset.f_82481_
                  );
                  UUID entityId = entity.m_20148_();
                  Vec3 previousPosition = PREVIOUS_SPARK_POS.get(entityId);
                  if (previousPosition == null) {
                     previousPosition = currentPosition;
                  }

                  double distance = previousPosition.m_82554_(currentPosition);
                  int steps = Math.max(1, (int)Math.ceil(distance / 0.12));
                  RandomSource random = entity.m_217043_();

                  for (int i = 0; i <= steps; i++) {
                     double progress = (double)i / (double)steps;
                     Vec3 position = previousPosition.m_165921_(currentPosition, progress);
                     if (random.m_188501_() > 0.9F) {
                        return;
                     }

                     double spread = 0.12;
                     double offsetX = (random.m_188500_() - 0.5) * spread;
                     double offsetY = (random.m_188500_() - 0.5) * spread;
                     double offsetZ = (random.m_188500_() - 0.5) * spread;
                     entity.m_9236_()
                        .m_7106_(
                           (ParticleOptions)EMparticles.STAR.get(),
                           currentPosition.f_82479_ + offsetX,
                           currentPosition.f_82480_ + offsetY,
                           currentPosition.f_82481_ + offsetZ,
                           0.0,
                           0.0,
                           0.0
                        );
                  }

                  PREVIOUS_SPARK_POS.put(entityId, currentPosition);
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InTimeEvent<E0> attachedEffek(
      float time,
      String effekName,
      skillparameter.EffekAttachPart attachPart,
      double offsetX,
      double offsetY,
      double offsetZ,
      float rotationX,
      float rotationY,
      float rotationZ,
      float scale
   ) {
      return InTimeEvent.create(
         time,
         (E0)(livingEntityPatch, animation, params) -> {
            LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
            if (entity.m_9236_().f_46443_) {
               ResourceLocation resourceLocation = new ResourceLocation("epicmoonmod", effekName);
               ParticleEmitterInfo info = ParticleEmitterInfo.create(entity.m_9236_(), resourceLocation)
                  .bindOnEntity(entity)
                  .entitySpaceRelativePosition(offsetX, offsetY, offsetZ)
                  .rotation((float)Math.toRadians((double)rotationX), (float)Math.toRadians((double)rotationY), (float)Math.toRadians((double)rotationZ))
                  .scale(scale);
               info.useEntityHeadSpace(attachPart == skillparameter.EffekAttachPart.HEAD);
               AAALevel.addParticle(entity.m_9236_(), false, info);
            }
         },
         Side.CLIENT
      );
   }

   public static InPeriodEvent<E0> bladeEffekTrail(
      float startTime,
      float endTime,
      InteractionHand hand,
      String effekName,
      double bladeStartX,
      double bladeStartY,
      double bladeStartZ,
      double bladeEndX,
      double bladeEndY,
      double bladeEndZ,
      float rotationOffsetX,
      float rotationOffsetY,
      float rotationOffsetZ,
      float scale,
      double movementInterval,
      double bladeInterval
   ) {
      return InPeriodEvent.create(
         startTime,
         endTime,
         (E0)(livingEntityPatch, animation, params) -> {
            LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
            if (entity.m_9236_().f_46443_) {
               Joint joint = livingEntityPatch.getParentJointOfHand(hand);
               if (joint != null) {
                  float[] partialTicks = new float[]{0.0F, 0.5F, 1.0F};
                  Vec3[] edgeStarts = new Vec3[partialTicks.length];
                  Vec3[] edgeEnds = new Vec3[partialTicks.length];
                  Vec3 localStart = new Vec3(bladeStartX, bladeStartY, bladeStartZ);
                  Vec3 localEnd = new Vec3(bladeEndX, bladeEndY, bladeEndZ);

                  for (int sampleIndex = 0; sampleIndex < partialTicks.length; sampleIndex++) {
                     float partialTick = partialTicks[sampleIndex];
                     Pose pose = livingEntityPatch.getAnimator().getPose(partialTick);
                     Vec3 entityPosition = entity.m_20318_(partialTick);
                     OpenMatrix4f modelWorldMatrix = OpenMatrix4f.createTranslation(
                           (float)entityPosition.f_82479_, (float)entityPosition.f_82480_, (float)entityPosition.f_82481_
                        )
                        .rotateDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(livingEntityPatch.getModelMatrix(partialTick));
                     OpenMatrix4f jointWorldMatrix = livingEntityPatch.getArmature().getBoundTransformFor(pose, joint).mulFront(modelWorldMatrix);
                     edgeStarts[sampleIndex] = OpenMatrix4f.transform(jointWorldMatrix, localStart);
                     edgeEnds[sampleIndex] = OpenMatrix4f.transform(jointWorldMatrix, localEnd);
                  }

                  ResourceLocation resourceLocation = new ResourceLocation("epicmoonmod", effekName);
                  double safeMovementInterval = Math.max(0.01, movementInterval);
                  double safeBladeInterval = Math.max(0.01, bladeInterval);
                  float offsetX = (float)Math.toRadians((double)rotationOffsetX);
                  float offsetY = (float)Math.toRadians((double)rotationOffsetY);
                  float roll = (float)Math.toRadians((double)rotationOffsetZ);

                  for (int segmentIndex = 0; segmentIndex < partialTicks.length - 1; segmentIndex++) {
                     Vec3 previousStart = edgeStarts[segmentIndex];
                     Vec3 previousEnd = edgeEnds[segmentIndex];
                     Vec3 nextStart = edgeStarts[segmentIndex + 1];
                     Vec3 nextEnd = edgeEnds[segmentIndex + 1];
                     double rootMovement = previousStart.m_82554_(nextStart);
                     double tipMovement = previousEnd.m_82554_(nextEnd);
                     double maximumMovement = Math.max(rootMovement, tipMovement);
                     int movementSteps = Math.max(1, (int)Math.ceil(maximumMovement / safeMovementInterval));

                     for (int movementIndex = 0; movementIndex <= movementSteps; movementIndex++) {
                        double movementProgress = (double)movementIndex / (double)movementSteps;
                        Vec3 interpolatedStart = previousStart.m_165921_(nextStart, movementProgress);
                        Vec3 interpolatedEnd = previousEnd.m_165921_(nextEnd, movementProgress);
                        Vec3 bladeVector = interpolatedEnd.m_82546_(interpolatedStart);
                        double bladeLength = bladeVector.m_82553_();
                        if (!(bladeLength < 1.0E-6)) {
                           Vec3 bladeDirection = bladeVector.m_82541_();
                           Vector3f rotatedDirection = new Vector3f(
                              (float)bladeDirection.f_82479_, (float)bladeDirection.f_82480_, (float)bladeDirection.f_82481_
                           );
                           rotatedDirection.rotateX(offsetX);
                           rotatedDirection.rotateY(offsetY);
                           Vec3 finalDirection = new Vec3((double)rotatedDirection.x, (double)rotatedDirection.y, (double)rotatedDirection.z);
                           if (!(finalDirection.m_82556_() < 1.0E-8)) {
                              finalDirection = finalDirection.m_82541_();
                              int bladeSteps = Math.max(1, (int)Math.ceil(bladeLength / safeBladeInterval));

                              for (int bladeIndex = 0; bladeIndex <= bladeSteps; bladeIndex++) {
                                 double bladeProgress = (double)bladeIndex / (double)bladeSteps;
                                 Vec3 position = interpolatedStart.m_165921_(interpolatedEnd, bladeProgress);
                                 ParticleEmitterInfo info = ParticleEmitterInfo.create(entity.m_9236_(), resourceLocation)
                                    .position(position.f_82479_, position.f_82480_, position.f_82481_)
                                    .rotationFromForward(finalDirection, roll)
                                    .scale(scale);
                                 AAALevel.addParticle(entity.m_9236_(), false, info);
                              }
                           }
                        }
                     }
                  }
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InTimeEvent<E0> attachedWeaponEffek(
      float startTime,
      InteractionHand hand,
      String effekName,
      double offsetX,
      double offsetY,
      double offsetZ,
      float rotationX,
      float rotationY,
      float rotationZ,
      float scale
   ) {
      return InTimeEvent.create(
         startTime,
         (E0)(livingEntityPatch, animation, params) -> {
            LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
            if (entity.m_9236_().f_46443_) {
               ResourceLocation effectId = new ResourceLocation("epicmoonmod", effekName);
               EffectRegistry.load(effectId)
                  .thenAccept(
                     effectDefinition -> Minecraft.m_91087_()
                           .execute(
                              () -> {
                                 if (!entity.m_213877_()) {
                                    Joint handJoint = livingEntityPatch.getParentJointOfHand(hand);
                                    if (handJoint != null) {
                                       ParticleEmitter emitter = effectDefinition.play();
                                       if (emitter != null && emitter.exists()) {
                                          updateWeaponEffekTransform(
                                             emitter, livingEntityPatch, entity, hand, 1.0F, offsetX, offsetY, offsetZ, rotationX, rotationY, rotationZ, scale
                                          );
                                          emitter.addPreDrawCallback(
                                             (currentEmitter, partialTick) -> {
                                                if (currentEmitter.exists()) {
                                                   if (!entity.m_213877_() && entity.m_6084_()) {
                                                      updateWeaponEffekTransform(
                                                         currentEmitter,
                                                         livingEntityPatch,
                                                         entity,
                                                         hand,
                                                         partialTick,
                                                         offsetX,
                                                         offsetY,
                                                         offsetZ,
                                                         rotationX,
                                                         rotationY,
                                                         rotationZ,
                                                         scale
                                                      );
                                                   } else {
                                                      currentEmitter.stop();
                                                   }
                                                }
                                             }
                                          );
                                       }
                                    }
                                 }
                              }
                           )
                  );
            }
         },
         Side.CLIENT
      );
   }

   private static void updateWeaponEffekTransform(
      ParticleEmitter emitter,
      LivingEntityPatch<?> livingEntityPatch,
      LivingEntity entity,
      InteractionHand hand,
      float partialTick,
      double offsetX,
      double offsetY,
      double offsetZ,
      float rotationX,
      float rotationY,
      float rotationZ,
      float scale
   ) {
      Joint handJoint = livingEntityPatch.getParentJointOfHand(hand);
      if (handJoint == null) {
         emitter.stop();
      } else {
         Pose pose = livingEntityPatch.getAnimator().getPose(partialTick);
         Vec3 entityPosition = entity.m_20318_(partialTick);
         OpenMatrix4f modelWorldMatrix = OpenMatrix4f.createTranslation(
               (float)entityPosition.f_82479_, (float)entityPosition.f_82480_, (float)entityPosition.f_82481_
            )
            .rotateDeg(180.0F, Vec3f.Y_AXIS)
            .mulBack(livingEntityPatch.getModelMatrix(partialTick));
         OpenMatrix4f jointWorldMatrix = livingEntityPatch.getArmature().getBoundTransformFor(pose, handJoint).mulFront(modelWorldMatrix);
         OpenMatrix4f effekMatrix = new OpenMatrix4f(jointWorldMatrix);
         effekMatrix.translate((float)offsetX, (float)offsetY, (float)offsetZ);
         if (rotationX != 0.0F) {
            effekMatrix.rotateDeg(rotationX, Vec3f.X_AXIS);
         }

         if (rotationY != 0.0F) {
            effekMatrix.rotateDeg(rotationY, Vec3f.Y_AXIS);
         }

         if (rotationZ != 0.0F) {
            effekMatrix.rotateDeg(rotationZ, Vec3f.Z_AXIS);
         }

         effekMatrix.scale(scale, scale, scale);
         float[][] transform = new float[][]{
            {effekMatrix.m00, effekMatrix.m10, effekMatrix.m20, effekMatrix.m30},
            {effekMatrix.m01, effekMatrix.m11, effekMatrix.m21, effekMatrix.m31},
            {effekMatrix.m02, effekMatrix.m12, effekMatrix.m22, effekMatrix.m32}
         };
         emitter.setTransformMatrix(transform);
      }
   }

   private static record BladeFrame(Vec3 root, Vec3 tip) {
   }

   public static enum EffekAttachPart {
      BODY,
      HEAD;
   }
}
