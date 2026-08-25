package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.entity.PortalEntity;
import com.pla.annoyingvillagers.util.HerobrinePortalCombatUtil;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class PortalApproachGoal extends Goal {
   private static final String PORTAL_APPROACH_COOLDOWN_TAG = "AnnoyingVillagersPortalApproachCooldown";
   private static final int PORTAL_APPROACH_COOLDOWN_TICKS = 200;
   private final Mob mob;
   private HerobrinePortalCombatUtil.PortalRoute route;

   public PortalApproachGoal(Mob mob) {
      this.mob = mob;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean m_8036_() {
      LivingEntity target = this.mob.m_5448_();
      if (target != null && target.m_6084_()) {
         if (this.mob.getPersistentData().m_128454_("AnnoyingVillagersPortalApproachCooldown") > this.mob.m_9236_().m_46467_()) {
            return false;
         } else {
            HerobrinePortalCombatUtil.PortalRoute foundRoute = HerobrinePortalCombatUtil.findRouteToTarget(this.mob, target);
            if (foundRoute == null) {
               return false;
            } else {
               PortalEntity entrance = foundRoute.entrance();
               if (this.mob.m_20280_(entrance) < 3.0) {
                  return false;
               } else {
                  this.route = foundRoute;
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   public void m_8056_() {
      this.mob.getPersistentData().m_128356_("AnnoyingVillagersPortalApproachCooldown", this.mob.m_9236_().m_46467_() + 200L);
   }

   public boolean m_8045_() {
      LivingEntity target = this.mob.m_5448_();
      if (target == null || !target.m_6084_()) {
         return false;
      } else if (this.route != null && !this.route.entrance().m_213877_() && !this.route.exit().m_213877_()) {
         HerobrinePortalCombatUtil.PortalRoute foundRoute = HerobrinePortalCombatUtil.findRouteToTarget(this.mob, target);
         if (foundRoute == null) {
            return false;
         } else {
            this.route = foundRoute;
            return this.mob.m_20280_(this.route.entrance()) > 1.6;
         }
      } else {
         return false;
      }
   }

   public void m_8037_() {
      if (this.route != null) {
         Vec3 center = this.route.entrance().getPortalCenter();
         this.mob.m_21573_().m_26519_(center.f_82479_, this.route.entrance().m_20186_(), center.f_82481_, 1.45);
         this.mob.m_21563_().m_24950_(center.f_82479_, center.f_82480_, center.f_82481_, 30.0F, 30.0F);
      }
   }

   public void m_8041_() {
      this.route = null;
   }
}
