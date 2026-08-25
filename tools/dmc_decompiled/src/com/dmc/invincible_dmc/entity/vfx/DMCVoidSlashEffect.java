package com.dmc.invincible_dmc.entity.vfx;

import com.dmc.invincible_dmc.entity.DMCEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class DMCVoidSlashEffect extends DMCSlashEffect {
   private LivingEntity owner;

   public DMCVoidSlashEffect(EntityType<?> entityTypeIn, Level worldIn) {
      super(entityTypeIn, worldIn);
   }

   public static DMCVoidSlashEffect createInstance(SpawnEntity packet, Level worldIn) {
      return new DMCVoidSlashEffect((EntityType<?>)DMCEntities.VOID_SLASH_EFFECT.get(), worldIn);
   }

   public void setOwner(LivingEntity owner) {
      this.owner = owner;
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
   }

   public LivingEntity getOwner() {
      return this.owner;
   }
}
