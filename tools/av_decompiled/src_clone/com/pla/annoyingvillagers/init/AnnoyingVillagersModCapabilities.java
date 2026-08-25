package com.pla.annoyingvillagers.init;

import com.pla.annoyingvillagers.capabilities.SnakeBladeCapability;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class AnnoyingVillagersModCapabilities {
   public static final Capability<SnakeBladeCapability.ISnakeBladeCapability> SNAKE_BLADE_CAPABILITY = CapabilityManager.get(
      new CapabilityToken<SnakeBladeCapability.ISnakeBladeCapability>() {
      }
   );

   private AnnoyingVillagersModCapabilities() {
   }

   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.register(SnakeBladeCapability.ISnakeBladeCapability.class);
   }

   public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof LivingEntity) {
         event.addCapability(SnakeBladeCapability.ID, new SnakeBladeCapability.SnakeBladeProvider());
      }
   }

   @Nullable
   public static <T> T getCapability(@Nullable Entity entity, Capability<T> capability) {
      return (T)(entity != null && entity.m_6084_() ? entity.getCapability(capability).orElse(null) : null);
   }
}
