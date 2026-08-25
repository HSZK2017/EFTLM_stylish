package com.pla.annoyingvillagers.client.renderer;

import com.pla.annoyingvillagers.entity.ElectricPhaseEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ElectricPhaseRenderer extends EntityRenderer<ElectricPhaseEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/empty.png");

   public ElectricPhaseRenderer(Context pContext) {
      super(pContext);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull ElectricPhaseEntity dragonBeam) {
      return TEXTURE;
   }
}
