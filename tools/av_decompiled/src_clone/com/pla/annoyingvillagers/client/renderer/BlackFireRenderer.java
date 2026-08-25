package com.pla.annoyingvillagers.client.renderer;

import com.pla.annoyingvillagers.entity.BlackFireEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BlackFireRenderer extends EntityRenderer<BlackFireEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/empty.png");

   public BlackFireRenderer(Context pContext) {
      super(pContext);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull BlackFireEntity dragonBeam) {
      return TEXTURE;
   }
}
