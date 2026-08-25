package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.NullWeapon;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class NullHoeEntity extends NullWeapon {
   public NullHoeEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<NullHoeEntity>)AnnoyingVillagersModEntities.NULL_HOE.get(), level);
   }

   public NullHoeEntity(EntityType<NullHoeEntity> entitytype, Level level) {
      super(entitytype, level);
      this.setWeapon("hoe");
   }

   public static Builder createAttributes() {
      return NullWeapon.createAttributes();
   }
}
