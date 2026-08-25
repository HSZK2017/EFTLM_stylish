package com.dmc.invincible_dmc.client.renderer.entity;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.client.renderer.ProceduralSlashRenderer;
import com.dmc.invincible_dmc.entity.vfx.DMCSlashEffect;
import com.dmc.invincible_dmc.entity.vfx.SlashMotionMode;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class DMCSlashEffectRenderer extends EntityRenderer<DMCSlashEffect> {
   public DMCSlashEffectRenderer(Context context) {
      super(context);
   }

   @NotNull
   public ResourceLocation getTextureLocation(DMCSlashEffect entity) {
      return InvincibleMod_DMC.rl("textures/vfx/slash.png");
   }

   public void render(DMCSlashEffect entity, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
      poseStack.m_85836_();
      float yaw = Mth.m_14179_(partialTicks, entity.f_19859_, entity.m_146908_());
      float pitch = Mth.m_14179_(partialTicks, entity.f_19860_, entity.m_146909_());
      poseStack.m_252781_(Axis.f_252436_.m_252977_(-yaw + 90.0F));
      poseStack.m_252781_(Axis.f_252403_.m_252977_(pitch));
      poseStack.m_252781_(Axis.f_252529_.m_252977_(-entity.getRotationRoll()));
      int lifetime = Math.max(1, entity.getLifetime());
      float rawProgress = Mth.m_14036_(((float)entity.f_19797_ + partialTicks) / (float)lifetime, 0.0F, 1.0F);
      SlashMotionMode mode = entity.getMotionMode();
      float easedProgress = mode.ease(rawProgress);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(entity.getRotationOffset() - mode.sweepDegrees() * easedProgress));
      float baseScale = 1.2F * entity.getBaseSize();
      poseStack.m_85841_(baseScale, baseScale, baseScale);
      int colorInt = entity.getColor();
      float r = (float)(colorInt >> 16 & 0xFF) / 255.0F;
      float g = (float)(colorInt >> 8 & 0xFF) / 255.0F;
      float b = (float)(colorInt & 0xFF) / 255.0F;
      Vector3f color = new Vector3f(r, g, b);
      float alpha = mode.alpha(rawProgress);
      float xzScale = Mth.m_14179_(easedProgress, mode.xzScaleStart(), mode.xzScaleEnd());
      float yScale = Mth.m_14179_(easedProgress, mode.yScaleStart(), mode.yScaleEnd());
      ProceduralSlashRenderer.renderSlashLegacyLike(
         poseStack, bufferSource, rawProgress, xzScale, yScale, alpha, color, this.getTextureLocation(entity), mode.meshSweepAngle(), mode
      );
      poseStack.m_85849_();
      super.m_7392_(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
   }
}
