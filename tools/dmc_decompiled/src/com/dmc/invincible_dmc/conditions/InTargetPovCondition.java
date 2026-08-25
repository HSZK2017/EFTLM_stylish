package com.dmc.invincible_dmc.conditions;

import yesman.epicfight.data.conditions.entity.TargetInPov;
import yesman.epicfight.data.conditions.entity.TargetInPov.TargetInPovHorizontal;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class InTargetPovCondition extends TargetInPov {
   public boolean predicate(LivingEntityPatch<?> entityPatch) {
      LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entityPatch.getTarget(), LivingEntityPatch.class);
      return livingEntityPatch != null && super.predicate(livingEntityPatch);
   }

   public static class InTargetPovHorizontal extends TargetInPovHorizontal {
      public boolean predicate(LivingEntityPatch<?> entityPatch) {
         LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entityPatch.getTarget(), LivingEntityPatch.class);
         return livingEntityPatch != null && super.predicate(livingEntityPatch);
      }
   }
}
