package com.pla.annoyingvillagers.client.renderer;

import com.pla.annoyingvillagers.entity.SteveEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SteveRenderer extends HumanoidMobRenderer<SteveEntity, PlayerModel<SteveEntity>> {
   public SteveRenderer(Context context) {
      super(context, new PlayerModel(context.m_174023_(ModelLayers.f_171162_), false), 0.5F);
      this.m_115326_(
         new HumanoidArmorLayer(
            this, new HumanoidModel(context.m_174023_(ModelLayers.f_171164_)), new HumanoidModel(context.m_174023_(ModelLayers.f_171165_)), context.m_266367_()
         )
      );
      ArrowLayer<SteveEntity, PlayerModel<SteveEntity>> arrowLayer = new ArrowLayer(context, this);
      this.m_115326_(arrowLayer);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull SteveEntity steveEntity) {
      return ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/steve.png");
   }
}
