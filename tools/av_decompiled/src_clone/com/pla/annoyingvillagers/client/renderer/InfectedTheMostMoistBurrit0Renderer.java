package com.pla.annoyingvillagers.client.renderer;

import com.pla.annoyingvillagers.entity.InfectedTheMostMoistBurrit0Entity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class InfectedTheMostMoistBurrit0Renderer extends HumanoidMobRenderer<InfectedTheMostMoistBurrit0Entity, PlayerModel<InfectedTheMostMoistBurrit0Entity>> {
   public InfectedTheMostMoistBurrit0Renderer(Context context) {
      super(context, new PlayerModel(context.m_174023_(ModelLayers.f_171162_), false), 0.5F);
      this.m_115326_(
         new HumanoidArmorLayer(
            this, new HumanoidModel(context.m_174023_(ModelLayers.f_171164_)), new HumanoidModel(context.m_174023_(ModelLayers.f_171165_)), context.m_266367_()
         )
      );
      ArrowLayer<InfectedTheMostMoistBurrit0Entity, PlayerModel<InfectedTheMostMoistBurrit0Entity>> arrowLayer = new ArrowLayer(context, this);
      this.m_115326_(arrowLayer);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull InfectedTheMostMoistBurrit0Entity infectedTheMostMoistBurrit0Entity) {
      return infectedTheMostMoistBurrit0Entity.m_21224_()
         ? ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/infected_themostmoistburrit0.png")
         : ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/infected_themostmoistburrit0.png");
   }
}
