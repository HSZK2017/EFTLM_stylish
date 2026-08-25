package com.dmc.invincible_dmc.conditions;

import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class ComboInterruptOrBasicAttackCondition extends ComboInterruptWindowCondition {
   @Override
   public boolean predicate(PlayerPatch<?> patch) {
      return patch.getEntityState().canBasicAttack() || check(patch);
   }
}
