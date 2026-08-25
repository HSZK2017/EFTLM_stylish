package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.entity.BbqEntity;
import com.pla.annoyingvillagers.entity.BlueDemonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class EscapeAvoidGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
   private final BbqEntity mob;

   public EscapeAvoidGoal(BbqEntity mob, Class<T> avoidClass, float maxDistance, double walkSpeed, double sprintSpeed) {
      super(mob, avoidClass, maxDistance, walkSpeed, sprintSpeed);
      this.mob = mob;
   }

   private boolean isRealEscapeThreat(@Nullable LivingEntity target) {
      if (target == null || !target.m_6084_()) {
         return false;
      } else if (target == this.mob) {
         return false;
      } else if (target instanceof BlueDemonEntity) {
         return false;
      } else {
         if (target instanceof Player player && (player.m_7500_() || player.m_5833_())) {
            return false;
         }

         return true;
      }
   }

   public boolean m_8036_() {
      if (!this.mob.isEscapeMode() || this.mob.getSauceLeader() != null) {
         return false;
      } else {
         return !super.m_8036_() ? false : this.isRealEscapeThreat(this.f_25016_);
      }
   }

   public boolean m_8045_() {
      return this.mob.isEscapeMode() && this.mob.getSauceLeader() == null ? super.m_8045_() && this.isRealEscapeThreat(this.f_25016_) : false;
   }
}
