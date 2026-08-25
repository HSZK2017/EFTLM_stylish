package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DragonOrbitLeaderGoal extends Goal {
   private static final double TWO_PI = Math.PI * 2;
   private static final double ORBIT_RING_INNER_FACTOR = 0.8;
   private static final double ORBIT_RING_OUTER_FACTOR = 1.35;
   private final HerobrineDragonEntity dragon;
   private final Level level;
   private final double baseSpeed;
   private final float orbitRadiusMin;
   private final float orbitRadiusMax;
   private final float farCatchUpDistance;
   private LivingEntity leader;
   private int updateCooldownTicks;
   private double orbitAngleRadians;
   private int orbitDirectionSign;
   private double orbitRadiusCurrent;
   private double orbitRadiusDesired;
   private double orbitAngularSpeedCurrent;
   private double orbitAngularSpeedDesired;
   private double orbitBaseHeightCurrent;
   private double orbitBaseHeightDesired;
   private double verticalWaveAmplitude;
   private double verticalWaveSpeed;
   private double verticalWavePhase;
   private int paramsTimeToLiveTicks;
   private double yJitterCurrent;
   private double yJitterDesired;

   public DragonOrbitLeaderGoal(HerobrineDragonEntity dragon, double baseSpeed, float orbitRadiusMin, float orbitRadiusMax, float farCatchUpDistance) {
      this.dragon = dragon;
      this.level = dragon.m_9236_();
      this.baseSpeed = baseSpeed;
      this.orbitRadiusMin = orbitRadiusMin;
      this.orbitRadiusMax = orbitRadiusMax;
      this.farCatchUpDistance = farCatchUpDistance;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   @Nullable
   private LivingEntity resolveOrbitCenter(HerobrineDragonEntity herobrineDragonEntity) {
      LivingEntity livingEntity = herobrineDragonEntity.getSummoner();
      if (livingEntity == null) {
         return herobrineDragonEntity.m_269323_();
      } else if (!(livingEntity instanceof Player)
         && (
            herobrineDragonEntity.m_20363_(livingEntity)
               || livingEntity.m_20202_() instanceof HerobrineDragonEntity herobrineDragon
                  && !herobrineDragon.m_20148_().equals(herobrineDragonEntity.m_20148_())
         )) {
         LivingEntity target = null;
         if (livingEntity instanceof Mob mob) {
            target = mob.m_5448_();
         }

         if (target == null || !target.m_6084_()) {
            target = livingEntity.m_21214_();
         }

         if (target == null || !target.m_6084_()) {
            target = livingEntity.m_21188_();
         }

         if (target == null) {
            livingEntity.m_8127_();
         }

         return target;
      } else {
         return livingEntity;
      }
   }

   public boolean m_8036_() {
      LivingEntity resolved = this.resolveOrbitCenter(this.dragon);
      if (resolved == null) {
         return false;
      } else if (!resolved.m_6084_()) {
         return false;
      } else if (resolved.m_5833_()) {
         return false;
      } else if (this.dragon.m_21523_()) {
         return false;
      } else if (this.dragon.m_20159_()) {
         return false;
      } else if (this.dragon.m_217005_()) {
         return false;
      } else if (this.dragon.m_21827_() && this.dragon.getSummoner() == null) {
         return false;
      } else if (this.dragon.isRecallActive()) {
         return false;
      } else {
         this.leader = resolved;
         return true;
      }
   }

   public boolean m_8045_() {
      LivingEntity resolved = this.resolveOrbitCenter(this.dragon);
      if (resolved == null) {
         return false;
      } else if (!resolved.m_6084_()) {
         return false;
      } else if (resolved.m_5833_()) {
         return false;
      } else if (this.dragon.m_21523_()) {
         return false;
      } else if (this.dragon.m_20159_()) {
         return false;
      } else if (this.dragon.m_217005_()) {
         return false;
      } else if (this.dragon.m_21827_() && this.dragon.getSummoner() == null) {
         return false;
      } else if (this.dragon.isRecallActive()) {
         return false;
      } else {
         this.leader = resolved;
         return true;
      }
   }

   public void m_8056_() {
      this.updateCooldownTicks = 0;
      this.orbitAngleRadians = Mth.m_216263_(this.dragon.m_217043_(), 0.0, Math.PI * 2);
      this.orbitDirectionSign = this.dragon.m_217043_().m_188499_() ? 1 : -1;
      this.orbitRadiusCurrent = this.orbitRadiusDesired = Mth.m_216263_(this.dragon.m_217043_(), (double)this.orbitRadiusMin, (double)this.orbitRadiusMax);
      this.orbitAngularSpeedCurrent = this.orbitAngularSpeedDesired = Mth.m_216263_(this.dragon.m_217043_(), 0.045, 0.11);
      this.orbitBaseHeightCurrent = this.orbitBaseHeightDesired = 14.0 + (double)this.dragon.m_217043_().m_188503_(14);
      this.verticalWaveAmplitude = Mth.m_216263_(this.dragon.m_217043_(), 2.0, 7.0);
      this.verticalWaveSpeed = Mth.m_216263_(this.dragon.m_217043_(), 0.018, 0.045);
      this.verticalWavePhase = Mth.m_216263_(this.dragon.m_217043_(), 0.0, Math.PI * 2);
      this.paramsTimeToLiveTicks = 80 + this.dragon.m_217043_().m_188503_(140);
      this.dragon.m_21573_().m_26573_();
      this.yJitterCurrent = this.yJitterDesired = Mth.m_216263_(this.dragon.m_217043_(), -6.0, 6.0);
   }

   public void m_8041_() {
      this.leader = null;
      this.dragon.m_21573_().m_26573_();
   }

   public void m_8037_() {
      if (this.leader != null) {
         this.dragon.m_21563_().m_24960_(this.leader, 10.0F, (float)this.dragon.m_8132_());
         this.orbitAngleRadians = wrapAngle(this.orbitAngleRadians + (double)this.orbitDirectionSign * this.orbitAngularSpeedCurrent);
         this.verticalWavePhase = wrapAngle(this.verticalWavePhase + this.verticalWaveSpeed);
         if (--this.paramsTimeToLiveTicks <= 0 || this.dragon.m_217043_().m_188503_(220) == 0) {
            this.rerollOrbitParameters();
         }

         this.orbitRadiusCurrent = Mth.m_14139_(0.08, this.orbitRadiusCurrent, this.orbitRadiusDesired);
         this.orbitAngularSpeedCurrent = Mth.m_14139_(0.08, this.orbitAngularSpeedCurrent, this.orbitAngularSpeedDesired);
         this.orbitBaseHeightCurrent = Mth.m_14139_(0.08, this.orbitBaseHeightCurrent, this.orbitBaseHeightDesired);
         if (--this.updateCooldownTicks <= 0) {
            this.updateCooldownTicks = this.m_183277_(2);
            Vec3 leaderPosition = this.leader.m_20182_();
            Vec3 dragonOffsetFromLeader = this.dragon.m_20182_().m_82546_(leaderPosition);
            double distanceToLeader = dragonOffsetFromLeader.m_82553_();
            double distanceToLeaderSquared = distanceToLeader * distanceToLeader;
            double farCatchUpDistanceSquared = (double)this.farCatchUpDistance * (double)this.farCatchUpDistance;
            if (distanceToLeaderSquared >= farCatchUpDistanceSquared) {
               if (!this.dragon.m_29443_() && this.dragon.canFly()) {
                  this.dragon.liftOff();
               }

               double catchUpY = this.computeDesiredY(leaderPosition.f_82479_, leaderPosition.f_82481_, leaderPosition.f_82480_) + 6.0;
               catchUpY = this.clampYForWorld(leaderPosition.f_82479_, leaderPosition.f_82481_, catchUpY);
               catchUpY = this.findNearestFreeY(leaderPosition.f_82479_, leaderPosition.f_82481_, catchUpY, this.hasCeiling(), 24);
               Vec3 catchUpTarget = new Vec3(leaderPosition.f_82479_, catchUpY, leaderPosition.f_82481_);
               this.setMoveTarget(catchUpTarget, this.baseSpeed * 1.65);
            } else {
               if (!this.dragon.m_29443_()
                  && this.dragon.canFly()
                  && (this.dragon.m_20096_() || this.leader.m_20186_() - this.dragon.m_20186_() > 2.0 || distanceToLeader > (double)this.orbitRadiusMin)) {
                  this.dragon.liftOff();
               }

               double orbitRingMinDistance = (double)this.orbitRadiusMin * 0.8;
               double orbitRingMaxDistance = (double)this.orbitRadiusMax * 1.35;
               Vec3 targetPosition;
               if (!(distanceToLeader < orbitRingMinDistance) && !(distanceToLeader > orbitRingMaxDistance)) {
                  double orbitX = leaderPosition.f_82479_ + Math.cos(this.orbitAngleRadians) * this.orbitRadiusCurrent;
                  double orbitZ = leaderPosition.f_82481_ + Math.sin(this.orbitAngleRadians) * this.orbitRadiusCurrent;
                  double orbitY = this.computeDesiredY(orbitX, orbitZ, leaderPosition.f_82480_);
                  targetPosition = new Vec3(orbitX, orbitY, orbitZ);
               } else {
                  Vec3 outwardDirection = distanceToLeader > 1.0E-4 ? dragonOffsetFromLeader.m_82490_(1.0 / distanceToLeader) : new Vec3(1.0, 0.0, 0.0);
                  Vec3 ringPoint = leaderPosition.m_82549_(outwardDirection.m_82490_(this.orbitRadiusDesired));
                  double ringY = this.computeDesiredY(ringPoint.f_82479_, ringPoint.f_82481_, leaderPosition.f_82480_);
                  targetPosition = new Vec3(ringPoint.f_82479_, ringY, ringPoint.f_82481_);
               }

               if (!this.canMoveTo(targetPosition)) {
                  boolean preferDown = this.hasCeiling();
                  double yFixed = this.findNearestFreeY(targetPosition.f_82479_, targetPosition.f_82481_, targetPosition.f_82480_, preferDown, 32);
                  Vec3 fixed = new Vec3(targetPosition.f_82479_, yFixed, targetPosition.f_82481_);
                  if (this.canMoveTo(fixed)) {
                     targetPosition = fixed;
                  } else {
                     double[] offs = preferDown ? new double[]{-6.0, -10.0, -14.0, 6.0, 10.0, 14.0} : new double[]{6.0, 10.0, 14.0, -6.0, -10.0, -14.0};
                     boolean found = false;

                     for (double off : offs) {
                        double yy = this.clampYForWorld(targetPosition.f_82479_, targetPosition.f_82481_, targetPosition.f_82480_ + off);
                        Vec3 t = new Vec3(targetPosition.f_82479_, yy, targetPosition.f_82481_);
                        if (this.canMoveTo(t)) {
                           targetPosition = t;
                           found = true;
                           break;
                        }
                     }

                     if (!found) {
                        this.orbitAngleRadians = Mth.m_216263_(this.dragon.m_217043_(), 0.0, Math.PI * 2);
                        double fallbackY = this.clampYForWorld(
                           leaderPosition.f_82479_, leaderPosition.f_82481_, leaderPosition.f_82480_ + this.orbitBaseHeightCurrent + 18.0
                        );
                        fallbackY = this.findNearestFreeY(leaderPosition.f_82479_, leaderPosition.f_82481_, fallbackY, preferDown, 32);
                        targetPosition = new Vec3(leaderPosition.f_82479_, fallbackY, leaderPosition.f_82481_);
                     }
                  }
               }

               this.setMoveTarget(targetPosition, this.baseSpeed);
               this.yJitterCurrent = Mth.m_14139_(0.05, this.yJitterCurrent, this.yJitterDesired);
            }
         }
      }
   }

   private void setMoveTarget(Vec3 target, double speed) {
      if (this.dragon.m_29443_()) {
         this.dragon.m_21573_().m_26573_();
         this.dragon.m_21566_().m_6849_(target.f_82479_, target.f_82480_, target.f_82481_, speed);
      } else {
         this.dragon.m_21573_().m_26519_(target.f_82479_, target.f_82480_, target.f_82481_, speed);
      }
   }

   private void rerollOrbitParameters() {
      if (this.dragon.m_217043_().m_188501_() < 0.3F) {
         this.orbitDirectionSign *= -1;
      }

      this.orbitRadiusDesired = Mth.m_216263_(this.dragon.m_217043_(), (double)this.orbitRadiusMin, (double)this.orbitRadiusMax);
      this.orbitAngularSpeedDesired = Mth.m_216263_(this.dragon.m_217043_(), 0.04, 0.13);
      this.orbitBaseHeightDesired = 14.0 + (double)this.dragon.m_217043_().m_188503_(18);
      this.verticalWaveAmplitude = Mth.m_216263_(this.dragon.m_217043_(), 2.0, 8.0);
      this.verticalWaveSpeed = Mth.m_216263_(this.dragon.m_217043_(), 0.016, 0.05);
      if (this.dragon.m_217043_().m_188501_() < 0.35F) {
         this.orbitAngleRadians = Mth.m_216263_(this.dragon.m_217043_(), 0.0, Math.PI * 2);
      }

      this.paramsTimeToLiveTicks = 70 + this.dragon.m_217043_().m_188503_(160);
      this.yJitterDesired = Mth.m_216263_(this.dragon.m_217043_(), -10.0, 10.0);
   }

   private double computeDesiredY(double x, double z, double leaderY) {
      double y = leaderY + this.orbitBaseHeightCurrent;
      y += Math.sin(this.verticalWavePhase) * this.verticalWaveAmplitude;
      y += this.yJitterCurrent;
      y = this.clampYForWorld(x, z, y);
      return this.findNearestFreeY(x, z, y, this.hasCeiling(), 24);
   }

   private boolean hasCeiling() {
      return this.level.m_6042_().f_63856_();
   }

   private double minY() {
      return (double)this.level.m_141937_() + 6.0;
   }

   private double maxY(double x, double z) {
      double max = (double)this.level.m_151558_() - 6.0;
      if (this.hasCeiling()) {
         BlockPos col = BlockPos.m_274561_(x, 0.0, z);
         int roofAirY = this.level.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, col).m_123342_();
         max = Math.min(max, (double)((float)roofAirY - this.dragon.m_20206_()) - 2.0);
      }

      if (max < this.minY()) {
         max = this.minY();
      }

      return max;
   }

   private double clampYForWorld(double x, double z, double y) {
      return Mth.m_14008_(y, this.minY(), this.maxY(x, z));
   }

   private double findNearestFreeY(double x, double z, double desiredY, boolean preferDown, int maxSteps) {
      double yClamped = this.clampYForWorld(x, z, desiredY);
      int base = Mth.m_14107_(yClamped);
      int min = Mth.m_14107_(this.minY());
      int max = Mth.m_14107_(this.maxY(x, z));
      base = Mth.m_14045_(base, min, max);

      for (int step = 0; step <= maxSteps; step++) {
         int y1 = preferDown ? base - step : base + step;
         int y2 = preferDown ? base + step : base - step;
         if (y1 >= min && y1 <= max && this.canMoveTo(new Vec3(x, (double)y1, z))) {
            return (double)y1;
         }

         if (step != 0 && y2 >= min && y2 <= max && this.canMoveTo(new Vec3(x, (double)y2, z))) {
            return (double)y2;
         }
      }

      return yClamped;
   }

   private boolean canMoveTo(Vec3 pos) {
      Vec3 delta = pos.m_82546_(this.dragon.m_20182_());
      AABB moved = this.dragon.m_20191_().m_82383_(delta);
      return this.level.m_45756_(this.dragon, moved) && !this.level.m_46855_(moved);
   }

   private static double wrapAngle(double angle) {
      angle %= Math.PI * 2;
      return angle < 0.0 ? angle + (Math.PI * 2) : angle;
   }
}
