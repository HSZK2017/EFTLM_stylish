package com.dmc.invincible_dmc.client.renderer;

import com.dmc.invincible_dmc.client.renderer.model.ProceduralSlashMesh;
import com.dmc.invincible_dmc.compat.oculus.OculusCompat;
import com.dmc.invincible_dmc.entity.vfx.SlashMotionMode;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class ProceduralSlashRenderer {
   private static final Map<Float, ProceduralSlashMesh> MESH_CACHE = new ConcurrentHashMap<>();
   private static final int FULL_LIGHT = 15728880;

   private static ProceduralSlashMesh getOrCreateMesh(float sweepAngle) {
      return MESH_CACHE.computeIfAbsent(sweepAngle, sa -> new ProceduralSlashMesh(1.8F, 2.8F, 0.15F, sa, 16));
   }

   private static RenderType getLuminousType(ResourceLocation texture) {
      return OculusCompat.isShaderActive() ? SlashRenderStates.getSlashLuminousOculus(texture) : SlashRenderStates.getSlashLuminous(texture);
   }

   private static RenderType getSlashColorWrite(ResourceLocation texture) {
      return OculusCompat.isShaderActive() ? SlashRenderStates.getSlashColorWriteOculus(texture) : SlashRenderStates.getSlashColorWrite(texture);
   }

   public static void renderSlashLegacyLike(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float linearProgress,
      float xzScale,
      float yScale,
      float alpha,
      Vector3f color,
      ResourceLocation texture,
      float meshSweepAngle,
      SlashMotionMode motionMode
   ) {
      ProceduralSlashMesh mesh = getOrCreateMesh(meshSweepAngle);
      RenderType colorWriteType = getSlashColorWrite(texture);
      RenderType luminousType = getLuminousType(texture);
      float windV = -0.8F + linearProgress * 0.3F;
      float bodyV = -0.35F + linearProgress * -0.15F;
      float outV = -0.5F + linearProgress * -0.2F;
      boolean fastBurst = motionMode == SlashMotionMode.FAST_BURST;
      poseStack.m_85836_();
      if (!fastBurst) {
         drawMesh(mesh, poseStack, bufferSource.m_6299_(colorWriteType), xzScale * 0.95F, yScale * 0.85F, 0.05F, 0.05F, 0.05F, alpha * 0.8F, 0.0F, windV);
      }

      drawMesh(mesh, poseStack, bufferSource.m_6299_(colorWriteType), xzScale, yScale, color.x(), color.y(), color.z(), alpha * 0.9F, 0.0F, bodyV);
      if (!fastBurst) {
         drawMesh(
            mesh, poseStack, bufferSource.m_6299_(luminousType), xzScale * 1.06F, yScale * 0.9F, color.x(), color.y(), color.z(), alpha * 0.85F, 0.0F, outV
         );
      }

      drawMesh(mesh, poseStack, bufferSource.m_6299_(luminousType), xzScale * 0.98F, yScale * 0.85F, 1.0F, 1.0F, 1.0F, alpha * 0.4F, 0.0F, bodyV);
      poseStack.m_85849_();
   }

   private static void drawMesh(
      ProceduralSlashMesh mesh,
      PoseStack poseStack,
      VertexConsumer consumer,
      float scale,
      float yScale,
      float r,
      float g,
      float b,
      float a,
      float uOffset,
      float vOffset
   ) {
      poseStack.m_85836_();
      poseStack.m_85841_(scale, yScale, scale);
      mesh.render(poseStack, consumer, 15728880, OverlayTexture.f_118083_, r, g, b, Math.max(0.0F, Math.min(1.0F, a)), uOffset, vOffset);
      poseStack.m_85849_();
   }
}
