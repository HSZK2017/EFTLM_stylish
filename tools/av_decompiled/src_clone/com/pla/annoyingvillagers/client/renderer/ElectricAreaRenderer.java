package com.pla.annoyingvillagers.client.renderer;

import com.pla.annoyingvillagers.entity.ElectricAreaEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ElectricAreaRenderer extends EntityRenderer<ElectricAreaEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/empty.png");

   public ElectricAreaRenderer(Context pContext) {
      super(pContext);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull ElectricAreaEntity dragonBeam) {
      return TEXTURE;
   }
}
