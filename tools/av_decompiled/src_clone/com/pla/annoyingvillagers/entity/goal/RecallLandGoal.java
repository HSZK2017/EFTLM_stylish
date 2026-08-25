package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RecallLandGoal extends Goal {
   private final HerobrineDragonEntity dragon;
   private int stage = 0;

   public RecallLandGoal(HerobrineDragonEntity dragon) {
      this.dragon = dragon;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean m_8036_() {
      return this.dragon.isRecallActive()
         && this.dragon.getSummoner() != null
         && this.dragon.getSummoner().m_6084_()
         && !this.dragon.m_20159_()
         && !this.dragon.m_217005_();
   }

   public boolean m_8045_() {
      return this.m_8036_();
   }

   public void m_8056_() {
      this.stage = 0;
      if (this.dragon.m_9236_() instanceof ServerLevel serverLevel) {
         this.dragon.setRecallLandPos(this.findLandingPosNearSummoner(serverLevel, this.dragon.getSummoner()));
      }

      if (!this.dragon.m_29443_() && this.dragon.canFly()) {
         this.dragon.liftOff();
      }

      this.dragon.setFlying(true);
      this.dragon.setNavigation(true);
      this.dragon.m_21573_().m_26573_();
      this.dragon.m_20242_(true);
   }

   public void m_8041_() {
      this.dragon.setRecallActive(false);
      this.dragon.setRecallLandPos(null);
      this.dragon.m_20242_(false);
      this.stage = 0;
   }

   public void m_8037_() {
      if (this.dragon.m_9236_() instanceof ServerLevel serverLevel) {
         LivingEntity owner = this.dragon.getSummoner();
         if (owner != null && owner.m_6084_()) {
            if (this.dragon.getRecallLandPos() == null) {
               this.dragon.setRecallLandPos(this.findLandingPosNearSummoner(serverLevel, owner));
            }

            Vec3 land = this.dragon.getRecallLandPos();
            this.dragon.m_21573_().m_26573_();
            this.dragon.m_20242_(true);
            if (!this.dragon.m_29443_() && this.dragon.canFly()) {
               this.dragon.liftOff();
            }

            this.dragon.setFlying(true);
            this.dragon.setNavigation(true);
            double aboveY = Math.max(owner.m_20186_() + 10.0, land.f_82480_ + 10.0);
            aboveY = this.clampYForWorld(serverLevel, land.f_82479_, land.f_82481_, aboveY);
            aboveY = this.findNearestFreeY(serverLevel, land.f_82479_, land.f_82481_, aboveY, serverLevel.m_6042_().f_63856_());
            Vec3 above = new Vec3(land.f_82479_, aboveY, land.f_82481_);
            if (this.stage == 0) {
               this.dragon.m_21566_().m_6849_(above.f_82479_, above.f_82480_, above.f_82481_, 1.8);
               this.dragon.aimBodyAndHeadAt(above, 25.0F, 18.0F);
               if (this.dragon.m_20238_(above) < 16.0) {
                  this.stage = 1;
               }
            } else {
               double landY = this.clampYForWorld(serverLevel, land.f_82479_, land.f_82481_, land.f_82480_);
               Vec3 landFixed = new Vec3(land.f_82479_, landY, land.f_82481_);
               this.dragon.m_21566_().m_6849_(landFixed.f_82479_, landFixed.f_82480_, landFixed.f_82481_, 1.2);
               this.dragon.aimBodyAndHeadAt(landFixed, 18.0F, 12.0F);
               if (this.dragon.m_20238_(landFixed) < 9.0) {
                  this.dragon.m_20242_(false);
                  this.dragon.m_20256_(Vec3.f_82478_);
                  if (this.dragon.isRecallAutoMount()) {
                     owner.m_7998_(this.dragon, true);
                  }

                  this.m_8041_();
               }
            }
         } else {
            this.m_8041_();
         }
      } else {
         this.m_8041_();
      }
   }

   private Vec3 findLandingPosNearSummoner(ServerLevel level, LivingEntity owner) {
      BlockPos base = owner.m_20183_();
      boolean hasCeiling = level.m_6042_().f_63856_();

      for (int r = 0; r <= 3; r++) {
         for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
               BlockPos col = base.m_7918_(dx, 0, dz);
               double x = (double)col.m_123341_() + 0.5;
               double z = (double)col.m_123343_() + 0.5;
               if (!hasCeiling) {
                  int groundY = level.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, col).m_123342_();
                  double y = (double)groundY + 1.0;
                  if (this.canFitAt(level, x, y, z)) {
                     return new Vec3(x, y, z);
                  }
               } else {
                  Vec3 found = this.findCeilingLandingAtColumn(level, owner, x, z);
                  if (found != null) {
                     return found;
                  }
               }
            }
         }
      }

      if (hasCeiling) {
         Vec3 found = this.findCeilingLandingAtColumn(level, owner, owner.m_20185_(), owner.m_20189_());
         if (found != null) {
            return found;
         } else {
            double y = this.clampYForWorld(level, owner.m_20185_(), owner.m_20189_(), owner.m_20186_() + 1.0);
            y = this.findNearestFreeY(level, owner.m_20185_(), owner.m_20189_(), y, true);
            return new Vec3(owner.m_20185_(), y, owner.m_20189_());
         }
      } else {
         BlockPos col = BlockPos.m_274561_(owner.m_20185_(), 0.0, owner.m_20189_());
         int groundY = level.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, col).m_123342_();
         return new Vec3(owner.m_20185_(), (double)groundY + 1.0, owner.m_20189_());
      }
   }

   @Nullable
   private Vec3 findCeilingLandingAtColumn(ServerLevel level, LivingEntity owner, double x, double z) {
      double minY = (double)level.m_141937_() + 6.0;
      int yStart = Mth.m_14107_(owner.m_20186_()) + 8;
      BlockPos col = BlockPos.m_274561_(x, 0.0, z);
      int roofAirY = level.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, col).m_123342_();
      double maxY = Math.min((double)level.m_151558_() - 2.0, (double)((float)roofAirY - this.dragon.m_20206_()) - 2.0);
      if (maxY < minY) {
         maxY = minY;
      }

      yStart = Math.min(yStart, Mth.m_14107_(maxY));
      yStart = Math.max(yStart, Mth.m_14107_(minY));
      int yMin = Mth.m_14107_(minY);

      for (int y = yStart; y >= yMin && yStart - y <= 96; y--) {
         if (this.canFitAt(level, x, (double)y, z) && this.hasGroundBelow(level, x, (double)y, z)) {
            return new Vec3(x, (double)y, z);
         }
      }

      return null;
   }

   private double clampYForWorld(ServerLevel level, double x, double z, double y) {
      double min = (double)level.m_141937_() + 6.0;
      double max = (double)level.m_151558_() - 6.0;
      if (level.m_6042_().f_63856_()) {
         BlockPos col = BlockPos.m_274561_(x, 0.0, z);
         int roofAirY = level.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, col).m_123342_();
         max = Math.min(max, (double)((float)roofAirY - this.dragon.m_20206_()) - 2.0);
      }

      if (max < min) {
         max = min;
      }

      return Mth.m_14008_(y, min, max);
   }

   private boolean canFitAt(ServerLevel level, double x, double y, double z) {
      AABB movedBox = this.dragon.m_20191_().m_82386_(x - this.dragon.m_20185_(), y - this.dragon.m_20186_(), z - this.dragon.m_20189_());
      return level.m_45756_(this.dragon, movedBox) && !level.m_46855_(movedBox);
   }

   private boolean hasGroundBelow(ServerLevel level, double x, double y, double z) {
      AABB box = this.dragon.m_20191_().m_82386_(x - this.dragon.m_20185_(), y - this.dragon.m_20186_(), z - this.dragon.m_20189_());
      AABB below = box.m_82386_(0.0, -0.25, 0.0);
      return !level.m_45756_(this.dragon, below);
   }

   private double findNearestFreeY(ServerLevel level, double x, double z, double desiredY, boolean preferDown) {
      double yClamped = this.clampYForWorld(level, x, z, desiredY);
      int base = Mth.m_14107_(yClamped);
      int min = Mth.m_14107_((double)level.m_141937_() + 6.0);
      int max = Mth.m_14107_((double)level.m_151558_() - 2.0);
      if (level.m_6042_().f_63856_()) {
         BlockPos col = BlockPos.m_274561_(x, 0.0, z);
         int roofAirY = level.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, col).m_123342_();
         max = Math.min(max, Mth.m_14107_((double)roofAirY - 2.0));
      }

      base = Mth.m_14045_(base, min, max);

      for (int step = 0; step <= 64; step++) {
         int y1 = preferDown ? base - step : base + step;
         int y2 = preferDown ? base + step : base - step;
         if (y1 >= min && y1 <= max && this.canFitAt(level, x, (double)y1, z)) {
            return (double)y1;
         }

         if (step != 0 && y2 >= min && y2 <= max && this.canFitAt(level, x, (double)y2, z)) {
            return (double)y2;
         }
      }

      return yClamped;
   }
}
