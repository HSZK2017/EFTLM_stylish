package com.pla.annoyingvillagers.client.renderer;

import com.pla.annoyingvillagers.entity.EliteHerobrineKnockedEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class EliteHerobrineKnockedRenderer extends HumanoidMobRenderer<EliteHerobrineKnockedEntity, PlayerModel<EliteHerobrineKnockedEntity>> {
   public EliteHerobrineKnockedRenderer(Context context) {
      super(context, new PlayerModel(context.m_174023_(ModelLayers.f_171162_), false), 0.5F);
      this.m_115326_(
         new HumanoidArmorLayer(
            this, new HumanoidModel(context.m_174023_(ModelLayers.f_171164_)), new HumanoidModel(context.m_174023_(ModelLayers.f_171165_)), context.m_266367_()
         )
      );
      ArrowLayer<EliteHerobrineKnockedEntity, PlayerModel<EliteHerobrineKnockedEntity>> arrowLayer = new ArrowLayer(context, this);
      this.m_115326_(arrowLayer);
   }

   @NotNull
   public ResourceLocation getTextureLocation(EliteHerobrineKnockedEntity eliteHerobrineKnockedEntity) {
      return eliteHerobrineKnockedEntity.m_21224_()
         ? ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/elite_herobrine_dead.png")
         : ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/elite_herobrine.png");
   }
}
