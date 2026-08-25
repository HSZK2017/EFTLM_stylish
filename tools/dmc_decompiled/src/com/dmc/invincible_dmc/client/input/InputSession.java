package com.dmc.invincible_dmc.client.input;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class InputSession {
   private LocalPlayer player;
   private ResourceKey<Level> dimension;
   private long generation;

   public boolean update(LocalPlayer nextPlayer, ResourceKey<Level> nextDimension) {
      if (this.player == nextPlayer && this.dimension == nextDimension) {
         return false;
      } else {
         this.player = nextPlayer;
         this.dimension = nextDimension;
         this.generation++;
         return true;
      }
   }

   public void clear() {
      this.player = null;
      this.dimension = null;
      this.generation++;
   }

   public boolean isActive() {
      return this.player != null && this.dimension != null;
   }

   public long generation() {
      return this.generation;
   }
}
