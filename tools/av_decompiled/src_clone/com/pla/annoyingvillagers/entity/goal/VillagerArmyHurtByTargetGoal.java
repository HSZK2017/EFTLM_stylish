package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.clazz.VillagerArmyEntity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.phys.AABB;

public class VillagerArmyHurtByTargetGoal extends HurtByTargetGoal {
   private static final double ALERT_RANGE_Y = 40.0;
   private final VillagerArmyEntity mob;

   public VillagerArmyHurtByTargetGoal(VillagerArmyEntity mob) {
      super(mob, new Class[0]);
      this.mob = mob;
      this.m_26044_(new Class[0]);
   }

   protected void m_26047_() {
      LivingEntity target = this.mob.m_21188_();
      if (target != null) {
         double followRange = this.m_7623_();
         AABB alertBox = AABB.m_82333_(this.mob.m_20182_()).m_82377_(followRange, 40.0, followRange);
         this.mob
            .m_9236_()
            .m_6443_(VillagerArmyEntity.class, alertBox, EntitySelector.f_20408_)
            .stream()
            .filter(other -> other != this.mob)
            .filter(LivingEntity::m_6084_)
            .filter(other -> other.m_5448_() == null)
            .filter(other -> !other.m_7307_(target))
            .forEach(other -> this.m_5766_(other, target));
      }
   }
}
