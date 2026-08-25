package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.combatbehaviour.CombatCommon;
import com.pla.annoyingvillagers.entity.BlueDemonEntity;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class KeepPositionGoal extends Goal {
   private final Mob mob;
   private double anchorX;
   private double anchorY;
   private double anchorZ;

   public KeepPositionGoal(Mob mob) {
      this.mob = mob;
      this.m_7021_(EnumSet.of(Flag.MOVE));
   }

   public boolean m_8036_() {
      if (this.mob instanceof HerobrineMob herobrineMob) {
         return CombatCommon.canEscape((MobPatch<?>)herobrineMob.getLivingEntityPatch());
      } else if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
         return CombatCommon.canEscape((MobPatch<?>)playerNpcEntity.getLivingEntityPatch());
      } else if (this.mob instanceof AVNpc avNpc) {
         return CombatCommon.canEscape((MobPatch<?>)avNpc.getLivingEntityPatch());
      } else {
         return this.mob instanceof BlueDemonEntity blueDemonEntity ? CombatCommon.canEscape((MobPatch<?>)blueDemonEntity.getLivingEntityPatch()) : false;
      }
   }

   public boolean m_8045_() {
      if (this.mob instanceof HerobrineMob herobrineMob) {
         return CombatCommon.canEscape((MobPatch<?>)herobrineMob.getLivingEntityPatch());
      } else if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
         return CombatCommon.canEscape((MobPatch<?>)playerNpcEntity.getLivingEntityPatch());
      } else if (this.mob instanceof AVNpc avNpc) {
         return CombatCommon.canEscape((MobPatch<?>)avNpc.getLivingEntityPatch());
      } else {
         return this.mob instanceof BlueDemonEntity blueDemonEntity ? CombatCommon.canEscape((MobPatch<?>)blueDemonEntity.getLivingEntityPatch()) : false;
      }
   }

   public void m_8056_() {
      this.anchorX = this.mob.m_20185_();
      this.anchorY = this.mob.m_20186_();
      this.anchorZ = this.mob.m_20189_();
      this.mob.m_21573_().m_26573_();
   }

   public void m_8041_() {
      this.mob.m_21573_().m_26573_();
   }

   public boolean m_183429_() {
      return true;
   }

   public void m_8037_() {
      this.mob.m_21573_().m_26573_();
      LivingEntity target = this.mob.m_5448_();
      if (target != null) {
         this.mob.m_21563_().m_24960_(target, 30.0F, 30.0F);
      }

      if (this.mob.m_20275_(this.anchorX, this.anchorY, this.anchorZ) > 0.25) {
         this.mob.m_21566_().m_6849_(this.anchorX, this.anchorY, this.anchorZ, 1.0);
      }
   }
}
