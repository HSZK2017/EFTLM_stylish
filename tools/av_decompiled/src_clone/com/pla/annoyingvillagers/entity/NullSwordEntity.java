package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.NullWeapon;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class NullSwordEntity extends NullWeapon {
   public NullSwordEntity(SpawnEntity spawnentity, Level level) {
      this((EntityType<NullSwordEntity>)AnnoyingVillagersModEntities.NULL_SWORD.get(), level);
   }

   public NullSwordEntity(EntityType<NullSwordEntity> entitytype, Level level) {
      super(entitytype, level);
      this.setWeapon("sword");
   }

   public static Builder createAttributes() {
      return NullWeapon.createAttributes();
   }
}
