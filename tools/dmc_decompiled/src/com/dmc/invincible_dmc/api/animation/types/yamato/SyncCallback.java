package com.dmc.invincible_dmc.api.animation.types.yamato;

import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@FunctionalInterface
public interface SyncCallback {
   void accept(LivingEntityPatch<?> var1, Entity var2, AttackResult var3);
}
