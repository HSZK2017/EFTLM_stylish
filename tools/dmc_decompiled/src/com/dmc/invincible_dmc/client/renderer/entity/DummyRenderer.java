package com.dmc.invincible_dmc.client.renderer.entity;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.entity.dummy.DummyEntity;
import javax.annotation.Nullable;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DummyRenderer extends HumanoidMobRenderer<DummyEntity, PlayerModel<DummyEntity>> {
   public DummyRenderer(Context context) {
      super(context, new PlayerModel(context.m_174023_(ModelLayers.f_171162_), false), 0.5F);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull DummyEntity entity) {
      return DefaultPlayerSkin.m_118626_();
   }

   @Nullable
   protected RenderType getRenderType(@NotNull DummyEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
      return showBody && DMConfig.MODEL_FACE_CULLING.get()
         ? RenderType.m_110452_(this.getTextureLocation(entity))
         : super.m_7225_(entity, showBody, translucent, showOutline);
   }
}
