package com.dmc.invincible_dmc.client.renderer.entity;

import com.dmc.invincible_dmc.entity.soul.SoulEntity;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SoulRenderer extends HumanoidMobRenderer<SoulEntity, HumanoidModel<SoulEntity>> {
   private static final ResourceLocation SKELETON_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png");

   public SoulRenderer(Context context) {
      super(context, new HumanoidModel(Minecraft.m_91087_().m_167973_().m_171103_(ModelLayers.f_171236_)), 0.0F);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull SoulEntity entity) {
      return SKELETON_TEXTURE;
   }

   @Nullable
   protected RenderType getRenderType(@NotNull SoulEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
      return entity.getRenderAlpha() < 1.0F && showBody
         ? RenderType.m_110473_(this.getTextureLocation(entity))
         : super.m_7225_(entity, showBody, translucent, showOutline);
   }
}
