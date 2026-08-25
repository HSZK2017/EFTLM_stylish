package com.dmc.invincible_dmc.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@EventBusSubscriber(
   modid = "invincible_dmc"
)
public class DMCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
   public static Capability<DMCEntity> DMC_ENTITY = CapabilityManager.get(new CapabilityToken<DMCEntity>() {
   });
   private DMCEntity dmcEntity = null;
   private final LazyOptional<DMCEntity> optional = LazyOptional.of(this::createInvincibleEntity);

   public static DMCEntity get(LivingEntity entity) {
      return (DMCEntity)entity.getCapability(DMC_ENTITY).orElse(DMCEntity.EMPTY);
   }

   public static DMCEntity get(LivingEntityPatch<?> patch) {
      return get((LivingEntity)patch.getOriginal());
   }

   private DMCEntity createInvincibleEntity() {
      if (this.dmcEntity == null) {
         this.dmcEntity = new DMCEntity();
      }

      return this.dmcEntity;
   }

   @NotNull
   public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
      return capability == DMC_ENTITY ? this.optional.cast() : LazyOptional.empty();
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      this.createInvincibleEntity().saveNBTData(tag);
      return tag;
   }

   public void deserializeNBT(CompoundTag tag) {
      this.createInvincibleEntity().loadNBTData(tag);
   }
}
