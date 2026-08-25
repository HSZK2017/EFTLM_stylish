package com.pla.annoyingvillagers.client.renderer;

import com.pla.annoyingvillagers.entity.ArmoredHerobrineEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ArmoredHerobrineRenderer extends HumanoidMobRenderer<ArmoredHerobrineEntity, HumanoidModel<ArmoredHerobrineEntity>> {
   public ArmoredHerobrineRenderer(Context context) {
      super(context, new HumanoidModel(context.m_174023_(ModelLayers.f_171162_)), 0.5F);
      this.m_115326_(
         new HumanoidArmorLayer(
            this, new HumanoidModel(context.m_174023_(ModelLayers.f_171164_)), new HumanoidModel(context.m_174023_(ModelLayers.f_171165_)), context.m_266367_()
         )
      );
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull ArmoredHerobrineEntity armoredHerobrineEntity) {
      return ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/shadow_herobrine.png");
   }
}
