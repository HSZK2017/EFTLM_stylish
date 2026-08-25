package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.clazz.SauceType;
import com.pla.annoyingvillagers.entity.BbqEntity;
import java.util.EnumSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class FollowEscapeLeaderGoal extends Goal {
   private final BbqEntity mob;

   public FollowEscapeLeaderGoal(BbqEntity mob) {
      this.mob = mob;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean m_8036_() {
      return this.mob.isEscapeMode() && this.mob.getSauceLeader() != null;
   }

   public boolean m_8045_() {
      return this.mob.isEscapeMode() && this.mob.getSauceLeader() != null;
   }

   public void m_8037_() {
      BbqEntity leader = this.mob.getSauceLeader();
      if (leader != null) {
         if (this.mob.m_20280_(leader) > 324.0) {
            this.mob.m_6021_(leader.m_20185_(), leader.m_20186_(), leader.m_20189_());
         } else {
            Vec3 forward = leader.m_20184_();
            forward = new Vec3(forward.f_82479_, 0.0, forward.f_82481_);
            if (forward.m_82556_() < 1.0E-4) {
               float yawRad = leader.m_146908_() * (float) (Math.PI / 180.0);
               forward = new Vec3((double)(-Mth.m_14031_(yawRad)), 0.0, (double)Mth.m_14089_(yawRad));
            } else {
               forward = forward.m_82541_();
            }
            double followDistance = switch (this.mob.getSauceType()) {
               case HONEY_MUSTARD_SAUCE -> 1.2;
               case SOY_SAUCE -> 2.4;
               case SWEET_ONION_SAUCE -> 3.6;
               default -> 1.2;
            };
            Vec3 desired = leader.m_20182_().m_82546_(forward.m_82490_(followDistance));
            this.mob.m_21563_().m_24960_(leader, 30.0F, 30.0F);
            if (this.mob.isEscapeFlying()) {
               double y = leader.m_20186_() + this.mob.getEscapeFlightHeight();
               if (this.mob.getSauceType() == SauceType.SWEET_ONION_SAUCE) {
                  y += 0.35;
               }

               this.mob.m_21573_().m_26573_();
               this.mob.m_20242_(true);
               this.mob.f_19789_ = 0.0F;
               this.mob.moveEscapeAerialTowards(desired.f_82479_, y, desired.f_82481_, 0.2, 0.88);
            } else {
               this.mob.m_20242_(false);
               this.mob.f_19789_ = 0.0F;
               if (this.mob.m_20275_(desired.f_82479_, leader.m_20186_(), desired.f_82481_) > 1.0) {
                  this.mob.m_21573_().m_26519_(desired.f_82479_, leader.m_20186_(), desired.f_82481_, 2.0);
               } else {
                  this.mob.m_21573_().m_26573_();
               }
            }
         }
      }
   }

   public void m_8041_() {
      this.mob.m_21573_().m_26573_();
      this.mob.m_20242_(false);
      this.mob.f_19789_ = 0.0F;
   }
}
