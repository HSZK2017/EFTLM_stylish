package com.dmc.invincible_dmc.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public final class DMCAnimationUtils {
   private DMCAnimationUtils() {
   }

   @Nullable
   public static LivingEntityPatch<?> getPatch(@Nullable LivingEntity entity) {
      return entity == null ? null : (LivingEntityPatch)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
   }

   @Nullable
   public static AnimationPlayer getMainPlayer(@Nullable LivingEntity entity) {
      return getMainPlayer(getPatch(entity));
   }

   @Nullable
   public static AnimationPlayer getMainPlayer(@Nullable LivingEntityPatch<?> patch) {
      return patch != null ? getMainPlayer(patch.getAnimator()) : null;
   }

   @Nullable
   public static AnimationPlayer getMainPlayer(@Nullable Animator animator) {
      if (animator == null) {
         return null;
      } else {
         AnimationPlayer player = animator.getPlayerFor(null);
         return player != null && !player.isEmpty() ? player : null;
      }
   }

   @Nullable
   public static AnimationPlayer getPlayerFor(@Nullable LivingEntityPatch<?> patch, @Nullable AssetAccessor<? extends DynamicAnimation> animation) {
      return patch != null ? getPlayerFor(patch.getAnimator(), animation) : null;
   }

   @Nullable
   public static AnimationPlayer getPlayerFor(@Nullable Animator animator, @Nullable AssetAccessor<? extends DynamicAnimation> animation) {
      if (animator == null) {
         return null;
      } else {
         AnimationPlayer player = animator.getPlayerFor(animation);
         return player != null && !player.isEmpty() ? player : null;
      }
   }

   @Nullable
   public static DynamicAnimation getCurrentAnimation(@Nullable LivingEntity entity) {
      return getCurrentAnimation(getPatch(entity));
   }

   @Nullable
   public static DynamicAnimation getCurrentAnimation(@Nullable LivingEntityPatch<?> patch) {
      return getCurrentAnimation(getMainPlayer(patch));
   }

   @Nullable
   public static DynamicAnimation getCurrentAnimation(@Nullable AnimationPlayer player) {
      AssetAccessor<? extends DynamicAnimation> accessor = getCurrentAnimationAccessor(player);
      return accessor != null ? (DynamicAnimation)accessor.get() : null;
   }

   @Nullable
   public static DynamicAnimation getAnimation(@Nullable AssetAccessor<?> accessor) {
      if (accessor == null) {
         return null;
      } else {
         return accessor.get() instanceof DynamicAnimation dynamicAnimation ? dynamicAnimation : null;
      }
   }

   @Nullable
   public static AssetAccessor<? extends DynamicAnimation> getCurrentAnimationAccessor(@Nullable LivingEntityPatch<?> patch) {
      return getCurrentAnimationAccessor(getMainPlayer(patch));
   }

   @Nullable
   public static AssetAccessor<? extends DynamicAnimation> getCurrentAnimationAccessor(@Nullable AnimationPlayer player) {
      return player != null ? player.getAnimation() : null;
   }

   @Nullable
   public static DynamicAnimation getCurrentAnimationClient(@Nullable LivingEntityPatch<?> patch) {
      return patch != null && patch.isLogicalClient() ? getCurrentAnimation(patch) : null;
   }

   @Nullable
   public static DynamicAnimation getCurrentAnimationServer(@Nullable LivingEntityPatch<?> patch) {
      return patch != null && !patch.isLogicalClient() ? getCurrentAnimation(patch) : null;
   }

   @Nullable
   public static StaticAnimation getRealAnimation(@Nullable LivingEntity entity) {
      return getRealAnimation(getPatch(entity));
   }

   @Nullable
   public static StaticAnimation getRealAnimation(@Nullable LivingEntityPatch<?> patch) {
      return getRealAnimation(getMainPlayer(patch));
   }

   @Nullable
   public static StaticAnimation getRealAnimation(@Nullable AnimationPlayer player) {
      AssetAccessor<? extends StaticAnimation> accessor = getRealAnimationAccessor(player);
      return accessor != null ? (StaticAnimation)accessor.get() : null;
   }

   @Nullable
   public static StaticAnimation getRealAnimationClient(@Nullable LivingEntityPatch<?> patch) {
      return patch != null && patch.isLogicalClient() ? getRealAnimation(patch) : null;
   }

   @Nullable
   public static StaticAnimation getRealAnimationServer(@Nullable LivingEntityPatch<?> patch) {
      return patch != null && !patch.isLogicalClient() ? getRealAnimation(patch) : null;
   }

   @Nullable
   public static AssetAccessor<? extends StaticAnimation> getRealAnimationAccessor(@Nullable LivingEntity entity) {
      return getRealAnimationAccessor(getPatch(entity));
   }

   @Nullable
   public static AssetAccessor<? extends StaticAnimation> getRealAnimationAccessor(@Nullable LivingEntityPatch<?> patch) {
      return getRealAnimationAccessor(getMainPlayer(patch));
   }

   @Nullable
   public static AssetAccessor<? extends StaticAnimation> getRealAnimationAccessor(@Nullable AnimationPlayer player) {
      return player != null ? player.getRealAnimation() : null;
   }

   @Nullable
   public static DynamicAnimation resolveRealAnimation(@Nullable DynamicAnimation animation) {
      if (animation == null) {
         return null;
      } else {
         AssetAccessor<? extends StaticAnimation> realAccessor = getRealAnimationAccessor(animation);
         if (realAccessor == null) {
            return animation;
         } else {
            StaticAnimation realAnimation = (StaticAnimation)realAccessor.get();
            return (DynamicAnimation)(realAnimation != null ? realAnimation : animation);
         }
      }
   }

   @Nullable
   public static StaticAnimation getRealAnimation(@Nullable DynamicAnimation animation) {
      return resolveRealAnimation(animation) instanceof StaticAnimation staticAnimation ? staticAnimation : null;
   }

   @Nullable
   public static StaticAnimation getRealAnimation(@Nullable AssetAccessor<? extends DynamicAnimation> accessor) {
      return getRealAnimation(getAnimation(accessor));
   }

   @Nullable
   public static <T extends DynamicAnimation> T asAnimation(@Nullable DynamicAnimation animation, Class<T> animationType) {
      return animation != null && animationType.isInstance(animation) ? animationType.cast(animation) : null;
   }

   @Nullable
   public static <T extends DynamicAnimation> T getCurrentAnimationAs(@Nullable LivingEntityPatch<?> patch, Class<T> animationType) {
      return asAnimation(getCurrentAnimation(patch), animationType);
   }

   @Nullable
   public static <T extends StaticAnimation> T getRealAnimationAs(@Nullable LivingEntityPatch<?> patch, Class<T> animationType) {
      return asAnimation(getRealAnimation(patch), animationType);
   }

   @Nullable
   public static <T extends StaticAnimation> T getRealAnimationAs(@Nullable AnimationPlayer player, Class<T> animationType) {
      return asAnimation(getRealAnimation(player), animationType);
   }

   @Nullable
   public static <T extends StaticAnimation> T getRealAnimationAs(@Nullable DynamicAnimation animation, Class<T> animationType) {
      return asAnimation(getRealAnimation(animation), animationType);
   }

   @Nullable
   public static <T extends StaticAnimation> T getRealAnimationAs(@Nullable AssetAccessor<? extends DynamicAnimation> accessor, Class<T> animationType) {
      return asAnimation(getRealAnimation(accessor), animationType);
   }

   public static boolean isAnimationType(@Nullable DynamicAnimation animation, Class<? extends DynamicAnimation> animationType) {
      return animation != null && animationType.isInstance(animation);
   }

   public static boolean isRealAnimationType(@Nullable DynamicAnimation animation, Class<? extends StaticAnimation> animationType) {
      return animationType.isInstance(getRealAnimation(animation));
   }

   public static boolean sameAnimation(@Nullable DynamicAnimation first, @Nullable DynamicAnimation second) {
      if (first == second) {
         return first != null;
      } else {
         ResourceLocation firstName = getAnimationName(first);
         ResourceLocation secondName = getAnimationName(second);
         return firstName != null && firstName.equals(secondName);
      }
   }

   public static boolean sameRealAnimation(@Nullable DynamicAnimation first, @Nullable DynamicAnimation second) {
      return sameAnimation(resolveRealAnimation(first), resolveRealAnimation(second));
   }

   public static boolean isAnimation(@Nullable DynamicAnimation animation, @Nullable AssetAccessor<? extends DynamicAnimation> expected) {
      if (animation != null && expected != null) {
         ResourceLocation animationName = getAnimationName(animation);
         return animationName != null && animationName.equals(expected.registryName());
      } else {
         return false;
      }
   }

   public static boolean isRealAnimation(@Nullable DynamicAnimation animation, @Nullable AssetAccessor<? extends StaticAnimation> expected) {
      return isAnimation(resolveRealAnimation(animation), expected);
   }

   public static boolean sameAccessor(@Nullable AssetAccessor<?> first, @Nullable AssetAccessor<?> second) {
      return first == second
         ? first != null
         : first != null && second != null && first.registryName() != null && first.registryName().equals(second.registryName());
   }

   public static boolean isOneOfAccessor(@Nullable AssetAccessor<?> actual, AssetAccessor<?>... expected) {
      if (actual != null && expected != null) {
         for (AssetAccessor<?> candidate : expected) {
            if (sameAccessor(actual, candidate)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Nullable
   public static AssetAccessor<? extends StaticAnimation> getRealAnimationAccessor(@Nullable DynamicAnimation animation) {
      return animation != null ? animation.getRealAnimation() : null;
   }

   @Nullable
   public static ResourceLocation getCurrentAnimationName(@Nullable LivingEntityPatch<?> patch) {
      return getAnimationName(getCurrentAnimation(patch));
   }

   @Nullable
   public static ResourceLocation getRealAnimationName(@Nullable LivingEntity entity) {
      return getRealAnimationName(getPatch(entity));
   }

   @Nullable
   public static ResourceLocation getRealAnimationName(@Nullable LivingEntityPatch<?> patch) {
      StaticAnimation animation = getRealAnimation(patch);
      return animation != null ? animation.getRegistryName() : null;
   }

   @Nullable
   public static ResourceLocation getAnimationName(@Nullable DynamicAnimation animation) {
      if (animation == null) {
         return null;
      } else {
         AssetAccessor<? extends DynamicAnimation> accessor = animation.getAccessor();
         if (accessor != null) {
            return accessor.registryName();
         } else {
            return animation instanceof StaticAnimation staticAnimation ? staticAnimation.getRegistryName() : null;
         }
      }
   }

   public static String describe(@Nullable DynamicAnimation animation) {
      if (animation == null) {
         return "none";
      } else {
         ResourceLocation name = getAnimationName(animation);
         if (name != null) {
            return name.toString();
         } else {
            return animation instanceof StaticAnimation staticAnimation
               ? "unregistered:" + staticAnimation.getLocation()
               : animation.getClass().getSimpleName();
         }
      }
   }

   public static float getElapsedTime(@Nullable LivingEntity entity) {
      return getElapsedTime(getPatch(entity));
   }

   public static float getElapsedTime(@Nullable LivingEntityPatch<?> patch) {
      AnimationPlayer player = getMainPlayer(patch);
      return player != null ? player.getElapsedTime() : -1.0F;
   }

   public static float getPreviousElapsedTime(@Nullable LivingEntity entity) {
      return getPreviousElapsedTime(getPatch(entity));
   }

   public static float getPreviousElapsedTime(@Nullable LivingEntityPatch<?> patch) {
      AnimationPlayer player = getMainPlayer(patch);
      return player != null ? player.getPrevElapsedTime() : -1.0F;
   }

   public static float getProgress(@Nullable LivingEntityPatch<?> patch) {
      AnimationPlayer player = getMainPlayer(patch);
      StaticAnimation animation = getRealAnimation(patch);
      return player != null && animation != null && !(animation.getTotalTime() <= 0.0F) ? player.getElapsedTime() / animation.getTotalTime() : -1.0F;
   }

   public static boolean isPlaying(@Nullable LivingEntity entity, AssetAccessor<? extends StaticAnimation>... animations) {
      return isPlaying(getPatch(entity), animations);
   }

   @SafeVarargs
   public static boolean isPlaying(@Nullable LivingEntityPatch<?> patch, AssetAccessor<? extends StaticAnimation>... animations) {
      ResourceLocation current = getRealAnimationName(patch);
      if (current != null && animations != null) {
         for (AssetAccessor<? extends StaticAnimation> animation : animations) {
            if (animation != null && current.equals(animation.registryName())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean isPlaying(@Nullable LivingEntityPatch<?> patch, @Nullable StaticAnimation animation) {
      ResourceLocation current = getRealAnimationName(patch);
      if (animation == null) {
         return false;
      } else {
         ResourceLocation expected = animation.getRegistryName();
         return expected != null ? expected.equals(current) : getRealAnimation(patch) == animation;
      }
   }

   public static boolean isPlayingClient(@Nullable LivingEntityPatch<?> patch, @Nullable StaticAnimation animation) {
      return patch != null && patch.isLogicalClient() && isPlaying(patch, animation);
   }

   public static boolean isPlayingServer(@Nullable LivingEntityPatch<?> patch, @Nullable StaticAnimation animation) {
      return patch != null && !patch.isLogicalClient() && isPlaying(patch, animation);
   }

   @SafeVarargs
   public static boolean isPlayingClient(@Nullable LivingEntityPatch<?> patch, AssetAccessor<? extends StaticAnimation>... animations) {
      return patch != null && patch.isLogicalClient() && isPlaying(patch, animations);
   }

   @SafeVarargs
   public static boolean isPlayingServer(@Nullable LivingEntityPatch<?> patch, AssetAccessor<? extends StaticAnimation>... animations) {
      return patch != null && !patch.isLogicalClient() && isPlaying(patch, animations);
   }

   public static boolean isElapsedTimeInRange(@Nullable LivingEntityPatch<?> patch, float minTime, float maxTime) {
      AnimationPlayer player = getMainPlayer(patch);
      return player != null && player.getElapsedTime() >= minTime && player.getPrevElapsedTime() <= maxTime;
   }

   public static boolean isCurrentAnimationType(@Nullable LivingEntityPatch<?> patch, Class<? extends DynamicAnimation> animationType) {
      DynamicAnimation animation = getCurrentAnimation(patch);
      return animation != null && animationType.isInstance(animation);
   }

   public static boolean isRealAnimationType(@Nullable LivingEntityPatch<?> patch, Class<? extends StaticAnimation> animationType) {
      StaticAnimation animation = getRealAnimation(patch);
      return animation != null && animationType.isInstance(animation);
   }

   public static boolean intersectsHorizontalSegment(@Nullable Vec3 segmentStart, @Nullable Vec3 segmentEnd, @Nullable AABB bounds) {
      if (segmentStart != null && segmentEnd != null && bounds != null) {
         double[] interval = new double[]{0.0, 1.0};
         return clipHorizontalAxis(segmentStart.f_82479_, segmentEnd.f_82479_ - segmentStart.f_82479_, bounds.f_82288_, bounds.f_82291_, interval)
            && clipHorizontalAxis(segmentStart.f_82481_, segmentEnd.f_82481_ - segmentStart.f_82481_, bounds.f_82290_, bounds.f_82293_, interval);
      } else {
         return false;
      }
   }

   private static boolean clipHorizontalAxis(double start, double delta, double min, double max, double[] interval) {
      if (!(Math.abs(delta) <= 1.0E-8)) {
         double first = (min - start) / delta;
         double second = (max - start) / delta;
         if (first > second) {
            double swap = first;
            first = second;
            second = swap;
         }

         interval[0] = Math.max(interval[0], first);
         interval[1] = Math.min(interval[1], second);
         return interval[0] <= interval[1];
      } else {
         return start >= min && start <= max;
      }
   }

   public static Vec3[] createHorizontalCappedRectangle(@Nullable Vec3 segmentStart, @Nullable Vec3 segmentEnd, double halfWidth, double apexAngleDegrees) {
      if (segmentStart != null && segmentEnd != null && !(halfWidth < 0.0) && !(apexAngleDegrees <= 0.0) && !(apexAngleDegrees >= 180.0)) {
         double segmentX = segmentEnd.f_82479_ - segmentStart.f_82479_;
         double segmentZ = segmentEnd.f_82481_ - segmentStart.f_82481_;
         double segmentLength = Math.sqrt(segmentX * segmentX + segmentZ * segmentZ);
         if (segmentLength <= 1.0E-8) {
            return new Vec3[0];
         } else {
            double halfAngleRadians = Math.toRadians(apexAngleDegrees * 0.5);
            double halfAngleTangent = Math.tan(halfAngleRadians);
            if (Double.isFinite(halfAngleTangent) && !(halfAngleTangent <= 1.0E-8)) {
               double capLength = halfWidth / halfAngleTangent;
               double effectiveHalfWidth = halfWidth;
               if (capLength * 2.0 > segmentLength) {
                  capLength = segmentLength * 0.5;
                  effectiveHalfWidth = capLength * halfAngleTangent;
               }

               double axisX = segmentX / segmentLength;
               double axisZ = segmentZ / segmentLength;
               double sideX = -axisZ;
               Vec3 startBaseCenter = new Vec3(segmentStart.f_82479_ + axisX * capLength, segmentStart.f_82480_, segmentStart.f_82481_ + axisZ * capLength);
               Vec3 endBaseCenter = new Vec3(segmentEnd.f_82479_ - axisX * capLength, segmentEnd.f_82480_, segmentEnd.f_82481_ - axisZ * capLength);
               Vec3 sideOffset = new Vec3(sideX * effectiveHalfWidth, 0.0, axisX * effectiveHalfWidth);
               return new Vec3[]{
                  segmentStart,
                  startBaseCenter.m_82549_(sideOffset),
                  endBaseCenter.m_82549_(sideOffset),
                  segmentEnd,
                  endBaseCenter.m_82546_(sideOffset),
                  startBaseCenter.m_82546_(sideOffset)
               };
            } else {
               return new Vec3[0];
            }
         }
      } else {
         return new Vec3[0];
      }
   }

   public static boolean intersectsHorizontalCappedRectangle(
      @Nullable Vec3 segmentStart, @Nullable Vec3 segmentEnd, double halfWidth, double apexAngleDegrees, @Nullable AABB bounds
   ) {
      if (bounds == null) {
         return false;
      } else {
         Vec3[] vertices = createHorizontalCappedRectangle(segmentStart, segmentEnd, halfWidth, apexAngleDegrees);
         return intersectsHorizontalConvexPolygon(vertices, bounds);
      }
   }

   private static boolean intersectsHorizontalConvexPolygon(Vec3[] vertices, AABB bounds) {
      if (vertices.length >= 3 && overlapsHorizontalProjection(vertices, bounds, 1.0, 0.0) && overlapsHorizontalProjection(vertices, bounds, 0.0, 1.0)) {
         for (int index = 0; index < vertices.length; index++) {
            Vec3 current = vertices[index];
            Vec3 next = vertices[(index + 1) % vertices.length];
            double edgeX = next.f_82479_ - current.f_82479_;
            double edgeZ = next.f_82481_ - current.f_82481_;
            if (!(edgeX * edgeX + edgeZ * edgeZ <= 1.0E-12) && !overlapsHorizontalProjection(vertices, bounds, -edgeZ, edgeX)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean overlapsHorizontalProjection(Vec3[] vertices, AABB bounds, double axisX, double axisZ) {
      double polygonMin = Double.POSITIVE_INFINITY;
      double polygonMax = Double.NEGATIVE_INFINITY;

      for (Vec3 vertex : vertices) {
         double projection = vertex.f_82479_ * axisX + vertex.f_82481_ * axisZ;
         polygonMin = Math.min(polygonMin, projection);
         polygonMax = Math.max(polygonMax, projection);
      }

      double boundsCenterX = (bounds.f_82288_ + bounds.f_82291_) * 0.5;
      double boundsCenterZ = (bounds.f_82290_ + bounds.f_82293_) * 0.5;
      double boundsCenterProjection = boundsCenterX * axisX + boundsCenterZ * axisZ;
      double boundsRadius = bounds.m_82362_() * 0.5 * Math.abs(axisX) + bounds.m_82385_() * 0.5 * Math.abs(axisZ);
      double boundsMin = boundsCenterProjection - boundsRadius;
      double boundsMax = boundsCenterProjection + boundsRadius;
      return polygonMax >= boundsMin && boundsMax >= polygonMin;
   }

   @Nullable
   public static Pose getCurrentPose(@Nullable LivingEntityPatch<?> patch, float partialTicks) {
      return patch != null && patch.getAnimator() != null ? patch.getAnimator().getPose(partialTicks) : null;
   }

   @Nullable
   public static OpenMatrix4f getJointModelTransform(@Nullable LivingEntityPatch<?> patch, @Nullable Joint joint, float partialTicks) {
      Pose pose = getCurrentPose(patch, partialTicks);
      return patch != null && joint != null && pose != null ? patch.getArmature().getBoundTransformFor(pose, joint) : null;
   }

   @Nullable
   public static OpenMatrix4f getJointWorldTransform(@Nullable LivingEntityPatch<?> patch, @Nullable Joint joint, float partialTicks) {
      OpenMatrix4f jointToModel = getJointModelTransform(patch, joint, partialTicks);
      if (patch != null && jointToModel != null) {
         LivingEntity entity = (LivingEntity)patch.getOriginal();
         Vec3 entityPosition = entity.m_20318_(partialTicks);
         OpenMatrix4f modelToWorld = OpenMatrix4f.createTranslation(
               (float)entityPosition.f_82479_, (float)entityPosition.f_82480_, (float)entityPosition.f_82481_
            )
            .rotateDeg(180.0F, Vec3f.Y_AXIS)
            .mulBack(patch.getModelMatrix(partialTicks));
         return jointToModel.mulFront(modelToWorld);
      } else {
         return null;
      }
   }

   @Nullable
   public static Vec3 getJointWorldForward(@Nullable LivingEntityPatch<?> patch, @Nullable Joint joint, float partialTicks) {
      OpenMatrix4f jointToWorld = getJointWorldTransform(patch, joint, partialTicks);
      if (jointToWorld == null) {
         return null;
      } else {
         OpenMatrix4f rotationOnly = jointToWorld.removeTranslation().removeScale();
         Vec3f forward = OpenMatrix4f.transform3v(rotationOnly, Vec3f.M_Z_AXIS, null);
         Vec3 result = new Vec3((double)forward.x, (double)forward.y, (double)forward.z);
         return result.m_82556_() > 1.0E-8 ? result.m_82541_() : null;
      }
   }

   public static float getJointWorldYaw(@Nullable LivingEntityPatch<?> patch, @Nullable Joint joint, float partialTicks, float fallbackYaw) {
      Vec3 forward = getJointWorldForward(patch, joint, partialTicks);
      if (forward == null) {
         return Mth.m_14177_(fallbackYaw);
      } else {
         Vec3 horizontalForward = new Vec3(forward.f_82479_, 0.0, forward.f_82481_);
         return horizontalForward.m_82556_() <= 1.0E-8 ? Mth.m_14177_(fallbackYaw) : Mth.m_14177_((float)MathUtils.getYRotOfVector(horizontalForward));
      }
   }

   public static float getRootWorldYaw(@Nullable LivingEntityPatch<?> patch, float partialTicks, float fallbackYaw) {
      Joint rootJoint = patch != null ? patch.getArmature().rootJoint : null;
      return getJointWorldYaw(patch, rootJoint, partialTicks, fallbackYaw);
   }
}
