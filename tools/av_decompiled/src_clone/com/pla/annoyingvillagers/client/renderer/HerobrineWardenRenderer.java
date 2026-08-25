package com.pla.annoyingvillagers.client.renderer;

import com.pla.annoyingvillagers.client.model.ModelHerobrineWarden;
import com.pla.annoyingvillagers.entity.HerobrineWardenEntity;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.WardenEmissiveLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class HerobrineWardenRenderer extends MobRenderer<HerobrineWardenEntity, ModelHerobrineWarden> {
   private static final ResourceLocation BASE = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/herobrine_warden.png");
   private static final ResourceLocation BIOLUM = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/warden/warden_bioluminescent_layer.png");
   private static final ResourceLocation HEART = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/warden/warden_heart.png");
   private static final ResourceLocation SPOTS1 = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/warden/warden_pulsating_spots_1.png");
   private static final ResourceLocation SPOTS2 = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/warden/warden_pulsating_spots_2.png");

   public HerobrineWardenRenderer(Context ctx) {
      super(ctx, new ModelHerobrineWarden(ctx.m_174023_(ModelHerobrineWarden.LAYER_LOCATION)), 0.9F);
      this.m_115326_(new WardenEmissiveLayer(this, BIOLUM, (e, partial, age) -> 1.0F, WardenModel::m_233543_));
      this.m_115326_(new WardenEmissiveLayer(this, SPOTS1, (e, partial, age) -> Math.max(0.0F, Mth.m_14089_(age * 0.045F) * 0.25F), WardenModel::m_233544_));
      this.m_115326_(
         new WardenEmissiveLayer(
            this, SPOTS2, (e, partial, age) -> Math.max(0.0F, Mth.m_14089_(age * 0.045F + (float) Math.PI) * 0.25F), WardenModel::m_233544_
         )
      );
      this.m_115326_(new WardenEmissiveLayer(this, BASE, (e, partial, age) -> e.m_219467_(partial), WardenModel::m_233541_));
      this.m_115326_(new WardenEmissiveLayer(this, HEART, (e, partial, age) -> e.m_219469_(partial), WardenModel::m_233542_));
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull HerobrineWardenEntity herobrineWardenEntity) {
      return BASE;
   }
}
