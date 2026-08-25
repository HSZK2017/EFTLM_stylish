package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import com.pla.annoyingvillagers.util.BowFunction;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BowLineOfSightGoal extends Goal {
   private static final int ANGLE_STEPS = 16;
   private static final int[] Y_OFFSETS = new int[]{1, 0, -1, 2, -2, 3, -3, -4};
   private final Mob mob;
   private final double speedModifier;
   private final double minShootDistance;
   private final double maxShootDistance;
   private int repathDelay;

   public BowLineOfSightGoal(Mob mob, double speedModifier, double minShootDistance, double maxShootDistance) {
      this.mob = mob;
      this.speedModifier = speedModifier;
      this.minShootDistance = minShootDistance;
      this.maxShootDistance = maxShootDistance;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean m_8036_() {
      return this.shouldReposition();
   }

   public boolean m_8045_() {
      return this.shouldReposition();
   }

   public void m_8056_() {
      this.repathDelay = 0;
      this.repath();
   }

   public void m_8037_() {
      LivingEntity target = this.mob.m_5448_();
      if (target != null) {
         this.mob.m_21563_().m_24960_(target, 30.0F, 30.0F);
         if (!this.needsBetterBowPosition(target)) {
            this.mob.m_21573_().m_26573_();
         } else {
            if (this.repathDelay-- <= 0 || this.mob.m_21573_().m_26571_()) {
               this.repathDelay = 10 + this.mob.m_217043_().m_188503_(10);
               this.repath();
            }
         }
      }
   }

   public void m_8041_() {
      this.mob.m_21573_().m_26573_();
   }

   private boolean shouldReposition() {
      LivingEntity target = this.mob.m_5448_();
      return !this.mob.m_9236_().f_46443_
         && this.mob.m_6084_()
         && !this.mob.m_213877_()
         && !this.mob.m_21224_()
         && !this.mob.m_21525_()
         && !this.mob.m_20159_()
         && this.mob.m_21205_().m_41720_() instanceof BowItem
         && target != null
         && target.m_6084_()
         && this.needsBetterBowPosition(target);
   }

   private boolean needsBetterBowPosition(LivingEntity target) {
      double distance = (double)this.mob.m_20270_(target);
      return distance > this.getMaxShootDistance(target) || !BowFunction.hasClearShot(this.mob, target);
   }

   private void repath() {
      LivingEntity target = this.mob.m_5448_();
      if (target != null) {
         BlockPos clearShotPos = this.findClearShotPosition(target);
         if (clearShotPos != null) {
            Path path = this.mob.m_21573_().m_7864_(clearShotPos, 0);
            if (path != null) {
               this.mob.m_21573_().m_26536_(path, this.speedModifier);
               return;
            }
         }

         this.mob.m_21573_().m_5624_(target, this.speedModifier);
      }
   }

   private BlockPos findClearShotPosition(LivingEntity target) {
      double currentDistance = (double)this.mob.m_20270_(target);
      double effectiveMaxShootDistance = this.getMaxShootDistance(target);
      double preferredDistance = clamp(currentDistance, this.minShootDistance + 1.0, effectiveMaxShootDistance - 1.0);
      double[] distances = new double[]{
         preferredDistance, effectiveMaxShootDistance - 1.0, (this.minShootDistance + effectiveMaxShootDistance) * 0.5, this.minShootDistance + 1.0
      };
      double startAngle = this.mob.m_217043_().m_188500_() * Math.PI * 2.0;

      for (double distance : distances) {
         for (int i = 0; i < 16; i++) {
            double angle = startAngle + (Math.PI * 2) * (double)i / 16.0;
            double x = target.m_20185_() + Math.cos(angle) * distance;
            double z = target.m_20189_() + Math.sin(angle) * distance;
            BlockPos standPos = this.findStandPosition(x, z);
            if (standPos != null && this.isInBowRange(standPos, target)) {
               Path path = this.mob.m_21573_().m_7864_(standPos, 0);
               if (path != null) {
                  Vec3 eyePos = Vec3.m_82539_(standPos).m_82520_(0.0, (double)this.mob.m_20192_(), 0.0);
                  if (BowFunction.hasClearShotFrom(this.mob.m_9236_(), this.mob, eyePos, target)) {
                     return standPos;
                  }
               }
            }
         }
      }

      return null;
   }

   private BlockPos findStandPosition(double x, double z) {
      BlockPos base = BlockPos.m_274561_(x, this.mob.m_20186_(), z);

      for (int yOffset : Y_OFFSETS) {
         BlockPos standPos = base.m_7918_(0, yOffset, 0);
         if (this.canStandAt(standPos)) {
            return standPos;
         }
      }

      Level level = this.mob.m_9236_();
      BlockPos surface = level.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.m_274561_(x, 0.0, z));
      return this.canStandAt(surface) ? surface : null;
   }

   private boolean canStandAt(BlockPos standPos) {
      if (this.mob.m_9236_().m_8055_(standPos.m_7495_()).m_60812_(this.mob.m_9236_(), standPos.m_7495_()).m_83281_()) {
         return false;
      } else {
         Vec3 feet = Vec3.m_82539_(standPos);
         AABB box = this.mob.m_20191_().m_82386_(feet.f_82479_ - this.mob.m_20185_(), feet.f_82480_ - this.mob.m_20186_(), feet.f_82481_ - this.mob.m_20189_());
         return this.mob.m_9236_().m_45756_(this.mob, box);
      }
   }

   private boolean isInBowRange(BlockPos standPos, LivingEntity target) {
      double distanceSqr = Vec3.m_82539_(standPos).m_82557_(target.m_20182_());
      return distanceSqr >= this.minShootDistance * this.minShootDistance && distanceSqr <= this.getMaxShootDistance(target) * this.getMaxShootDistance(target);
   }

   private double getMaxShootDistance(LivingEntity target) {
      return target instanceof HerobrineDragonEntity ? 80.0 : this.maxShootDistance;
   }

   private static double clamp(double value, double min, double max) {
      return Math.max(min, Math.min(max, value));
   }
}
