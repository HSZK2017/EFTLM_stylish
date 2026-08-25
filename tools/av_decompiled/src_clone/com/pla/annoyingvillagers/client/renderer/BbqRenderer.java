package com.pla.annoyingvillagers.client.renderer;

import com.pla.annoyingvillagers.client.model.ModelBbq;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import org.jetbrains.annotations.NotNull;

public class BbqRenderer extends ChickenRenderer {
   public BbqRenderer(Context context) {
      super(context);
      this.f_115290_ = new ModelBbq(context.m_174023_(ModelLayers.f_171277_));
      this.m_115326_(new BbqHeldItemLayer(this, context.m_234598_()));
   }

   @NotNull
   public ResourceLocation m_5478_(@NotNull Chicken entity) {
      return ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/chicken.png");
   }
}
