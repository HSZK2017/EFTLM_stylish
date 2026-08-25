package com.dmc.invincible_dmc.api.animation;

import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public record RuntimeArmatureProfile(ResourceLocation id, AssetAccessor<? extends Armature> armature) {
   public RuntimeArmatureProfile(ResourceLocation id, AssetAccessor<? extends Armature> armature) {
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(armature, "armature");
      this.id = id;
      this.armature = armature;
   }
}
