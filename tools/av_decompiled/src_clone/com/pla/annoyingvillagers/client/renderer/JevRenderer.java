package com.pla.annoyingvillagers.client.renderer;

import com.pla.annoyingvillagers.entity.JevEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class JevRenderer extends HumanoidMobRenderer<JevEntity, PlayerModel<JevEntity>> {
   public JevRenderer(Context context) {
      super(context, new PlayerModel(context.m_174023_(ModelLayers.f_171162_), false), 0.5F);
      this.m_115326_(
         new HumanoidArmorLayer(
            this, new HumanoidModel(context.m_174023_(ModelLayers.f_171164_)), new HumanoidModel(context.m_174023_(ModelLayers.f_171165_)), context.m_266367_()
         )
      );
      ArrowLayer<JevEntity, PlayerModel<JevEntity>> arrowLayer = new ArrowLayer(context, this);
      this.m_115326_(arrowLayer);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull JevEntity jevEntity) {
      return jevEntity.m_21224_()
         ? ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/jev_dead.png")
         : ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/jev.png");
   }
}
