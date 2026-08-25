package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.NullWeapon;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class NullAxeEntity extends NullWeapon {
   public NullAxeEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<NullAxeEntity>)AnnoyingVillagersModEntities.NULL_AXE.get(), level);
   }

   public NullAxeEntity(EntityType<NullAxeEntity> entitytype, Level level) {
      super(entitytype, level);
      this.setWeapon("axe");
   }

   public static Builder createAttributes() {
      return NullWeapon.createAttributes();
   }
}
