package com.dmc.invincible_dmc.entity.doppelganger;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.conditions.DirectionCondition;
import com.dmc.invincible_dmc.conditions.DirectionalSequenceCondition;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public final class DoppelgangerMirrorController {
   private static final float ROOT_FACING_CONE_HALF_ANGLE = 60.0F;
   private static final double TARGET_MIRROR_HALF_WIDTH = 2.5;
   private static final double TARGET_MIRROR_CAP_ANGLE_DEGREES = 90.0;
   private static final DustParticleOptions DEBUG_MIRROR_REGION_PARTICLE = new DustParticleOptions(new Vector3f(0.1F, 0.6F, 1.0F), 0.8F);
   private static final DustParticleOptions DEBUG_TARGET_AABB_PARTICLE = new DustParticleOptions(new Vector3f(1.0F, 0.8F, 0.1F), 0.7F);
   private static final DustParticleOptions DEBUG_INTERSECT_PARTICLE = new DustParticleOptions(new Vector3f(0.1F, 1.0F, 0.1F), 0.9F);
   private static final DustParticleOptions DEBUG_MISS_PARTICLE = new DustParticleOptions(new Vector3f(1.0F, 0.1F, 0.1F), 0.9F);
   private final DoppelgangerPatch patch;
   private boolean mirrored;
   private boolean rootReferenceInitialized;

   public DoppelgangerMirrorController(DoppelgangerPatch patch) {
      this.patch = patch;
   }

   public void refresh() {
      if (!isEnabled()) {
         this.mirrored = false;
         this.rootReferenceInitialized = false;
      } else {
         this.mirrored = this.resolveMirrorState();
      }
   }

   public boolean isMirrored() {
      return isEnabled() && this.mirrored;
   }

   public DirectionCondition.Direction resolveDirection(DirectionCondition.Direction direction) {
      return this.isMirrored() ? mirrorDirection(direction) : direction;
   }

   public boolean isHoldingDirection(DoppelgangerInputEvent event, DirectionCondition.Direction direction) {
      return isHoldingRawDirection(event, this.resolveDirection(direction));
   }

   public DirectionalSequenceCondition.Sequence resolveSequence(DirectionalSequenceCondition.Sequence sequence) {
      return this.isMirrored() ? mirrorSequence(sequence) : sequence;
   }

   private static boolean isEnabled() {
      return (Boolean)DMConfig.DOPPEL_MIRROR_CONTROL_ENABLED.get();
   }

   private boolean resolveMirrorState() {
      DoppelgangerEntity doppel = (DoppelgangerEntity)this.patch.getOriginal();
      PlayerPatch<?> ownerPatch = doppel.getOwnerPatch();
      Player owner = doppel.getOwner();
      if (ownerPatch != null && owner != null && owner.m_6084_()) {
         LivingEntity target = ownerPatch.getTarget();
         if (target != null && target.m_6084_()) {
            this.rootReferenceInitialized = false;
            AABB targetBounds = target.m_20191_();
            boolean intersects = DMCAnimationUtils.intersectsHorizontalCappedRectangle(owner.m_20182_(), doppel.m_20182_(), 2.5, 90.0, targetBounds);
            debugTargetIntersection(owner, doppel, target, intersects);
            DMCLog.info(
               DMCLog.Category.DOPPEL_COMBO,
               "[DoppelSvr] MIRROR source=TARGET_CAPPED_RECT result={} owner=({},{}) doppel=({},{}) halfWidth={} capAngle={} target={} targetPos=({},{}) bounds=[{},{} -> {},{}]",
               intersects,
               owner.m_20185_(),
               owner.m_20189_(),
               doppel.m_20185_(),
               doppel.m_20189_(),
               2.5,
               90.0,
               target.m_7755_().getString(),
               target.m_20185_(),
               target.m_20189_(),
               targetBounds.f_82288_,
               targetBounds.f_82290_,
               targetBounds.f_82291_,
               targetBounds.f_82293_
            );
            return intersects;
         } else {
            Vec3 ownerToDoppel = doppel.m_20182_().m_82546_(owner.m_20182_()).m_82542_(1.0, 0.0, 1.0);
            if (ownerToDoppel.m_82556_() <= 1.0E-8) {
               this.rootReferenceInitialized = false;
               return false;
            } else {
               float ownerYaw = ownerPatch.getYRot();
               float ownerToDoppelYaw = (float)MathUtils.getYRotOfVector(ownerToDoppel);
               float ownerFacingDelta = Math.abs(Mth.m_14177_(ownerYaw - ownerToDoppelYaw));
               if (ownerFacingDelta > 60.0F) {
                  this.rootReferenceInitialized = false;
                  DMCLog.info(
                     DMCLog.Category.DOPPEL_COMBO,
                     "[DoppelSvr] MIRROR source=ROOT result=false reason=owner_cone ownerYaw={} ownerToDoppelYaw={} delta={}",
                     ownerYaw,
                     ownerToDoppelYaw,
                     ownerFacingDelta
                  );
                  return false;
               } else if (this.rootReferenceInitialized && DMCAnimationUtils.isRealAnimationType(this.patch, AttackAnimation.class)) {
                  DMCLog.info(DMCLog.Category.DOPPEL_COMBO, "[DoppelSvr] MIRROR source=ROOT_LATCH result={}", this.mirrored);
                  return this.mirrored;
               } else {
                  Vec3 doppelToOwner = ownerToDoppel.m_82490_(-1.0);
                  float doppelToOwnerYaw = (float)MathUtils.getYRotOfVector(doppelToOwner);
                  float doppelModelYaw = DMCAnimationUtils.getRootWorldYaw(this.patch, 1.0F, this.patch.getYRot());
                  this.rootReferenceInitialized = true;
                  float doppelFacingDelta = Math.abs(Mth.m_14177_(doppelModelYaw - doppelToOwnerYaw));
                  boolean result = doppelFacingDelta <= 60.0F;
                  DMCLog.info(
                     DMCLog.Category.DOPPEL_COMBO,
                     "[DoppelSvr] MIRROR source=ROOT result={} doppelRootYaw={} doppelToOwnerYaw={} delta={}",
                     result,
                     doppelModelYaw,
                     doppelToOwnerYaw,
                     doppelFacingDelta
                  );
                  return result;
               }
            }
         }
      } else {
         this.rootReferenceInitialized = false;
         return false;
      }
   }

   private static DirectionCondition.Direction mirrorDirection(DirectionCondition.Direction direction) {
      return switch (direction) {
         case UP -> DirectionCondition.Direction.DOWN;
         case DOWN -> DirectionCondition.Direction.UP;
         case LEFT -> DirectionCondition.Direction.RIGHT;
         case RIGHT -> DirectionCondition.Direction.LEFT;
      };
   }

   private static boolean isHoldingRawDirection(DoppelgangerInputEvent event, DirectionCondition.Direction direction) {
      return switch (direction) {
         case UP -> event.holdingUp();
         case DOWN -> event.holdingDown();
         case LEFT -> event.holdingLeft();
         case RIGHT -> event.holdingRight();
      };
   }

   private static DirectionalSequenceCondition.Sequence mirrorSequence(DirectionalSequenceCondition.Sequence sequence) {
      return switch (sequence) {
         case BACK_FORWARD -> DirectionalSequenceCondition.Sequence.FORWARD_BACK;
         case FORWARD_BACK -> DirectionalSequenceCondition.Sequence.BACK_FORWARD;
         case LEFT_RIGHT -> DirectionalSequenceCondition.Sequence.RIGHT_LEFT;
         case RIGHT_LEFT -> DirectionalSequenceCondition.Sequence.LEFT_RIGHT;
         case BACK_BACK -> DirectionalSequenceCondition.Sequence.FORWARD_FORWARD;
         case FORWARD_FORWARD -> DirectionalSequenceCondition.Sequence.BACK_BACK;
         case LEFT_LEFT -> DirectionalSequenceCondition.Sequence.RIGHT_RIGHT;
         case RIGHT_RIGHT -> DirectionalSequenceCondition.Sequence.LEFT_LEFT;
      };
   }

   private static void debugTargetIntersection(Player owner, DoppelgangerEntity doppel, LivingEntity target, boolean intersects) {
      if (DMCLog.isEnabled(DMCLog.Category.DOPPEL_COMBO) && doppel.m_9236_() instanceof ServerLevel serverLevel) {
         AABB bounds = target.m_20191_();
         double debugY = bounds.f_82289_ + Math.max(0.1, (double)target.m_20206_() * 0.5);
         Vec3 ownerPoint = new Vec3(owner.m_20185_(), debugY, owner.m_20189_());
         Vec3 doppelPoint = new Vec3(doppel.m_20185_(), debugY, doppel.m_20189_());
         DustParticleOptions segmentParticle = intersects ? DEBUG_INTERSECT_PARTICLE : DEBUG_MISS_PARTICLE;
         spawnDebugLine(serverLevel, ownerPoint, doppelPoint, segmentParticle);
         Vec3 minMin = new Vec3(bounds.f_82288_, debugY, bounds.f_82290_);
         Vec3 maxMin = new Vec3(bounds.f_82291_, debugY, bounds.f_82290_);
         Vec3 maxMax = new Vec3(bounds.f_82291_, debugY, bounds.f_82293_);
         Vec3 minMax = new Vec3(bounds.f_82288_, debugY, bounds.f_82293_);
         spawnDebugLine(serverLevel, minMin, maxMin, DEBUG_TARGET_AABB_PARTICLE);
         spawnDebugLine(serverLevel, maxMin, maxMax, DEBUG_TARGET_AABB_PARTICLE);
         spawnDebugLine(serverLevel, maxMax, minMax, DEBUG_TARGET_AABB_PARTICLE);
         spawnDebugLine(serverLevel, minMax, minMin, DEBUG_TARGET_AABB_PARTICLE);
         Vec3[] regionVertices = DMCAnimationUtils.createHorizontalCappedRectangle(ownerPoint, doppelPoint, 2.5, 90.0);
         if (regionVertices.length != 0) {
            for (int index = 0; index < regionVertices.length; index++) {
               spawnDebugLine(serverLevel, regionVertices[index], regionVertices[(index + 1) % regionVertices.length], DEBUG_MIRROR_REGION_PARTICLE);
            }
         }
      }
   }

   private static void spawnDebugLine(ServerLevel level, Vec3 start, Vec3 end, DustParticleOptions particle) {
      Vec3 delta = end.m_82546_(start);
      int samples = Mth.m_14045_((int)Math.ceil(delta.m_82553_() / 0.2), 1, 64);

      for (int sample = 0; sample <= samples; sample++) {
         double progress = (double)sample / (double)samples;
         Vec3 point = start.m_82549_(delta.m_82490_(progress));
         level.m_8767_(particle, point.f_82479_, point.f_82480_, point.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
      }
   }
}
