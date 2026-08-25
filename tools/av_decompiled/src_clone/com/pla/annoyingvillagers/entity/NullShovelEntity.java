package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.NullWeapon;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class NullShovelEntity extends NullWeapon {
   public NullShovelEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<NullShovelEntity>)AnnoyingVillagersModEntities.NULL_SHOVEL.get(), level);
   }

   public NullShovelEntity(EntityType<NullShovelEntity> entitytype, Level level) {
      super(entitytype, level);
      this.setWeapon("shovel");
   }

   public static Builder createAttributes() {
      return NullWeapon.createAttributes();
   }
}
