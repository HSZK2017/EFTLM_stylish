package com.pla.annoyingvillagers.client.renderer;

import com.pla.annoyingvillagers.entity.HerobrineGregEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class HerobrineGregRenderer extends HumanoidMobRenderer<HerobrineGregEntity, PlayerModel<HerobrineGregEntity>> {
   public HerobrineGregRenderer(Context context) {
      super(context, new PlayerModel(context.m_174023_(ModelLayers.f_171162_), false), 0.5F);
      this.m_115326_(
         new HumanoidArmorLayer(
            this, new HumanoidModel(context.m_174023_(ModelLayers.f_171164_)), new HumanoidModel(context.m_174023_(ModelLayers.f_171165_)), context.m_266367_()
         )
      );
      ArrowLayer<HerobrineGregEntity, PlayerModel<HerobrineGregEntity>> arrowLayer = new ArrowLayer(context, this);
      this.m_115326_(arrowLayer);
   }

   @NotNull
   public ResourceLocation getTextureLocation(HerobrineGregEntity herobrineGregEntity) {
      return (herobrineGregEntity.isUseHerobrineTexture() || herobrineGregEntity.isSupportingHerobrine()) && !herobrineGregEntity.isHooked()
         ? ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/transporter_herobrine.png")
         : ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/greg.png");
   }
}
