package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.event.MobTargetRedirectEvent;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

public class RetargetCloserThreatGoal extends TargetGoal {
   private static final int DEFAULT_SCAN_INTERVAL = 5;
   private static final double SWITCH_MARGIN_SQR = 0.25;
   private final int scanInterval;
   private final TargetingConditions targetConditions;
   @Nullable
   private LivingEntity nextTarget;

   public RetargetCloserThreatGoal(Mob mob) {
      this(mob, 5);
   }

   public RetargetCloserThreatGoal(Mob mob, int scanInterval) {
      super(mob, true, false);
      this.scanInterval = Math.max(1, scanInterval);
      this.targetConditions = TargetingConditions.m_148352_().m_26883_(this.m_7623_());
      this.m_7021_(EnumSet.of(Flag.TARGET));
   }

   public boolean m_8036_() {
      if (!this.f_26135_.m_9236_().f_46443_
         && this.f_26135_.f_19797_ % this.scanInterval == 0
         && !MobTargetRedirectEvent.shouldPreserveRedirectTarget(this.f_26135_)) {
         this.nextTarget = this.findCloserThreatTarget();
         return this.nextTarget != null;
      } else {
         return false;
      }
   }

   public void m_8056_() {
      this.f_26135_.m_6710_(this.nextTarget);
      super.m_8056_();
   }

   public boolean m_8045_() {
      return false;
   }

   public void m_8041_() {
      this.nextTarget = null;
   }

   @Nullable
   private LivingEntity findCloserThreatTarget() {
      LivingEntity currentTarget = this.f_26135_.m_5448_();
      double currentDistanceSqr = currentTarget != null && currentTarget.m_6084_() ? this.f_26135_.m_20280_(currentTarget) : Double.MAX_VALUE;
      double followDistance = this.m_7623_();
      AABB searchBox = this.f_26135_.m_20191_().m_82377_(followDistance, 4.0, followDistance);
      LivingEntity bestTarget = null;
      double bestDistanceSqr = currentDistanceSqr;

      for (Mob candidate : this.f_26135_.m_9236_().m_6443_(Mob.class, searchBox, this::isThreatTargetingMob)) {
         double candidateDistanceSqr = this.f_26135_.m_20280_(candidate);
         if (!(candidateDistanceSqr + 0.25 >= bestDistanceSqr) && this.m_26150_(candidate, this.targetConditions)) {
            bestTarget = candidate;
            bestDistanceSqr = candidateDistanceSqr;
         }
      }

      return bestTarget;
   }

   private boolean isThreatTargetingMob(Mob candidate) {
      return candidate != this.f_26135_
         && candidate.m_6084_()
         && !candidate.m_5833_()
         && candidate.m_5448_() == this.f_26135_
         && !this.f_26135_.m_7307_(candidate)
         && !candidate.m_7307_(this.f_26135_);
   }
}
