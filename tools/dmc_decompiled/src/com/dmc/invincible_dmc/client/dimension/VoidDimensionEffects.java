package com.dmc.invincible_dmc.client.dimension;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.DimensionSpecialEffects.SkyType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class VoidDimensionEffects extends DimensionSpecialEffects {
   public VoidDimensionEffects() {
      super(Float.NaN, false, SkyType.NONE, false, true);
   }

   @NotNull
   public Vec3 m_5927_(@NotNull Vec3 color, float brightness) {
      return new Vec3(0.16, 0.23, 0.36);
   }

   public boolean m_5781_(int x, int y) {
      return false;
   }
}
