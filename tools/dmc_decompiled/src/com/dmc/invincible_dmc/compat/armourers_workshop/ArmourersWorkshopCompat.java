package com.dmc.invincible_dmc.compat.armourers_workshop;

import com.dmc.invincible_dmc.client.renderer.patched.entity.PSdtPlayerRenderer;
import com.dmc.invincible_dmc.compat.ICompatModule;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public final class ArmourersWorkshopCompat implements ICompatModule {
   private static boolean loaded;

   @Override
   public void onLoad(FMLJavaModLoadingContext context) {
      loaded = true;
   }

   public static boolean shouldSuppressWardrobe(AbstractClientPlayer player) {
      return loaded && PSdtPlayerRenderer.shouldRenderSdtMesh(player);
   }
}
