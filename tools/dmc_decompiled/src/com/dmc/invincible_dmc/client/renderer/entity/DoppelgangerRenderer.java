package com.dmc.invincible_dmc.client.renderer.entity;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.renderer.SlashRenderStates;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class DoppelgangerRenderer extends HumanoidMobRenderer<DoppelgangerEntity, HumanoidModel<DoppelgangerEntity>> {
   public static final ResourceLocation WHITE_TEXTURE = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/misc/white.png");

   public DoppelgangerRenderer(Context context) {
      super(context, new HumanoidModel(Minecraft.m_91087_().m_167973_().m_171103_(ModelLayers.f_171162_)), 0.0F);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull DoppelgangerEntity entity) {
      if ((Boolean)DMConfig.DOPPEL_SILHOUETTE.get()) {
         return WHITE_TEXTURE;
      } else {
         LivingEntity owner = entity.getOwner();
         return owner instanceof AbstractClientPlayer clientPlayer ? clientPlayer.m_108560_() : DefaultPlayerSkin.m_118626_();
      }
   }

   @Nullable
   protected RenderType getRenderType(@NotNull DoppelgangerEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
      if (entity.getRenderAlpha() < 1.0F && showBody) {
         if ((Boolean)DMConfig.DOPPEL_SILHOUETTE.get()) {
            return DMConfig.DOPPEL_SILHOUETTE_EMISSIVE.get()
               ? SlashRenderStates.getEntityTranslucentEmissiveDepthWrite(this.getTextureLocation(entity))
               : SlashRenderStates.getEntityTranslucentDepthWrite(this.getTextureLocation(entity));
         } else {
            return RenderType.m_110473_(this.getTextureLocation(entity));
         }
      } else {
         return showBody && DMConfig.MODEL_FACE_CULLING.get()
            ? RenderType.m_110452_(this.getTextureLocation(entity))
            : super.m_7225_(entity, showBody, translucent, showOutline);
      }
   }
}
