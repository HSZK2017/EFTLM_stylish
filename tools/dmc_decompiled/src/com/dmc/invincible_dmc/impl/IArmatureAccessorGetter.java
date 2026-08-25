package com.dmc.invincible_dmc.impl;

import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

public interface IArmatureAccessorGetter {
   AssetAccessor<? extends Armature> dmc$getArmatureAccessor(EntityPatch<?> var1);
}
