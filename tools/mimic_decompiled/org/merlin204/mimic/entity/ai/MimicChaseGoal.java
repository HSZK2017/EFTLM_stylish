package org.merlin204.mimic.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.merlin204.mimic.entity.MimicPatch;

public class MimicChaseGoal extends Goal {
   private final MimicPatch mimicPatch;
   private final float dis;
   private static final float Y_SPEED = 2.0F;

   public MimicChaseGoal(MimicPatch mimicPatch, float dis) {
      this.mimicPatch = mimicPatch;
      this.dis = dis;
   }

   public boolean m_8036_() {
      LivingEntity target = this.mimicPatch.getTarget();
      return !this.mimicPatch.getEntityState().inaction() && target != null && target.m_6084_() && target.m_20270_(this.mimicPatch.getOriginal()) >= this.dis;
   }

   public void m_8037_() {
      if (!this.mimicPatch.getEntityState().inaction()) {
         LivingEntity target = this.mimicPatch.getTarget();
         if (target != null && target.m_6084_()) {
            Vec3 dir = target.m_20182_().m_82546_(this.mimicPatch.getOriginal().m_20182_()).m_82541_().m_82490_(0.2F);
            if (this.mimicPatch.getOriginal().f_19862_) {
               this.mimicPatch.getOriginal().m_6478_(MoverType.SELF, new Vec3(dir.f_82479_, 2.0, dir.f_82481_));
            } else {
               this.mimicPatch.getOriginal().m_6478_(MoverType.SELF, new Vec3(dir.f_82479_, 0.0, dir.f_82481_));
            }

            this.mimicPatch.rotateTo(target, 25.0F, true);
         }
      }
   }
}
