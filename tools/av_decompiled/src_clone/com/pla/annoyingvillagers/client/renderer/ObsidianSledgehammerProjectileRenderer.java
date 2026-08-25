package com.pla.annoyingvillagers.client.renderer;

import com.pla.annoyingvillagers.client.model.ModelDragonMeteorite;
import com.pla.annoyingvillagers.entity.ObsidianSledgehammerProjectileEntity;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ObsidianSledgehammerProjectileRenderer
   extends MobRenderer<ObsidianSledgehammerProjectileEntity, ModelDragonMeteorite<ObsidianSledgehammerProjectileEntity>> {
   public ObsidianSledgehammerProjectileRenderer(Context context) {
      super(context, new ModelDragonMeteorite(context.m_174023_(ModelDragonMeteorite.LAYER_LOCATION)), 0.0F);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull ObsidianSledgehammerProjectileEntity obsidianSledgehammerProjectileEntity) {
      return ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/dragon_meteorite.png");
   }

   protected boolean isShaking(@NotNull ObsidianSledgehammerProjectileEntity obsidianSledgehammerProjectileEntity) {
      return true;
   }
}
