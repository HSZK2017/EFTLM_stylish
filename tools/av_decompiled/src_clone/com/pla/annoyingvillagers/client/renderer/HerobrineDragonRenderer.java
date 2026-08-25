package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.client.animation.DragonAnimator;
import com.pla.annoyingvillagers.client.model.ModelHerobrineDragon;
import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HerobrineDragonRenderer extends MobRenderer<HerobrineDragonEntity, ModelHerobrineDragon> {
   private static final ResourceLocation BODY_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "annoyingvillagers", "textures/entities/herobrine_dragon/body.png"
   );
   private static final ResourceLocation GLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "annoyingvillagers", "textures/entities/herobrine_dragon/glow.png"
   );
   private static final ResourceLocation DISSOLVE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "annoyingvillagers", "textures/entities/herobrine_dragon/dissolve.png"
   );
   private static final RenderType DISSOLVE_TYPE = RenderType.m_173235_(DISSOLVE_TEXTURE);

   public HerobrineDragonRenderer(Context ctx) {
      super(ctx, new ModelHerobrineDragon(ctx.m_174023_(ModelHerobrineDragon.LAYER_LOCATION)), 2.0F);
      this.m_115326_(new HerobrineDragonRenderer.GlowLayer(this));
      this.m_115326_(new HerobrineDragonRenderer.DeathLayer(this));
   }

   public boolean shouldRender(@NotNull HerobrineDragonEntity dragon, @NotNull Frustum frustum, double camX, double camY, double camZ) {
      return super.m_5523_(dragon, frustum, camX, camY, camZ);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull HerobrineDragonEntity dragon) {
      return BODY_TEXTURE;
   }

   @Nullable
   protected RenderType getRenderType(HerobrineDragonEntity entity, boolean visible, boolean invisToClient, boolean glowing) {
      return entity.f_20919_ > 0 ? null : super.m_7225_(entity, visible, invisToClient, glowing);
   }

   protected void setupRotations(@NotNull HerobrineDragonEntity dragon, @NotNull PoseStack ps, float ageInTicks, float yaw, float partialTicks) {
      super.m_7523_(dragon, ps, ageInTicks, yaw, partialTicks);
      DragonAnimator animator = dragon.getAnimator();
      if (animator != null) {
         ps.m_252880_(animator.getModelOffsetX(), animator.getModelOffsetY(), animator.getModelOffsetZ());
         ps.m_85837_(0.0, 1.5, 0.5);
         ps.m_252781_(Axis.f_252529_.m_252977_(animator.getModelPitch(partialTicks)));
         ps.m_85837_(0.0, -1.5, -0.5);
      }
   }

   protected float getFlipDegrees(@NotNull HerobrineDragonEntity entity) {
      return 0.0F;
   }

   private static class DeathLayer extends RenderLayer<HerobrineDragonEntity, ModelHerobrineDragon> {
      public DeathLayer(HerobrineDragonRenderer parent) {
         super(parent);
      }

      public void render(
         @NotNull PoseStack ps,
         @NotNull MultiBufferSource buffer,
         int light,
         @NotNull HerobrineDragonEntity dragon,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         if (dragon.f_20919_ > 0) {
            float delta = (float)dragon.f_20919_ / (float)dragon.getMaxDeathTime();
            ((ModelHerobrineDragon)this.m_117386_())
               .m_7695_(ps, buffer.m_6299_(HerobrineDragonRenderer.DISSOLVE_TYPE), light, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, delta);
            ((ModelHerobrineDragon)this.m_117386_())
               .m_7695_(
                  ps,
                  buffer.m_6299_(RenderType.m_110479_(HerobrineDragonRenderer.BODY_TEXTURE)),
                  light,
                  OverlayTexture.m_118090_(0.0F, true),
                  1.0F,
                  1.0F,
                  1.0F,
                  1.0F
               );
         }
      }
   }

   private static class GlowLayer extends RenderLayer<HerobrineDragonEntity, ModelHerobrineDragon> {
      public GlowLayer(HerobrineDragonRenderer parent) {
         super(parent);
      }

      public void render(
         @NotNull PoseStack ps,
         @NotNull MultiBufferSource buffer,
         int light,
         @NotNull HerobrineDragonEntity dragon,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         if (dragon.f_20919_ <= 0) {
            RenderType type = RenderType.m_110488_(HerobrineDragonRenderer.GLOW_TEXTURE);
            ((ModelHerobrineDragon)this.m_117386_()).m_7695_(ps, buffer.m_6299_(type), light, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}
