package com.dmc.invincible_dmc.client.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public final class JudgementCutDMC4ParticleLayer {
   private static final List<JudgementCutDMC4ParticleLayer.Entry> PARTICLES = new ArrayList<>();

   private JudgementCutDMC4ParticleLayer() {
   }

   public static void register(Particle particle, ClientLevel level) {
      if (particle instanceof JudgementCutDMC4ParticleLayer.LateRenderable lateRenderable) {
         PARTICLES.add(new JudgementCutDMC4ParticleLayer.Entry(particle, lateRenderable, level));
      }
   }

   public static void renderAfterEffeks(float partialTick, PoseStack poseStack, Matrix4f projectionMatrix, Camera camera) {
      Minecraft minecraft = Minecraft.m_91087_();
      PARTICLES.removeIf(entryx -> !entryx.particle().m_107276_() || entryx.level() != minecraft.f_91073_);
      if (!PARTICLES.isEmpty() && minecraft.f_91073_ != null && camera != null) {
         Vec3 cameraPosition = camera.m_90583_();
         Quaternionf cameraRotation = new Quaternionf(camera.m_253121_());
         PoseStack modelViewStack = RenderSystem.getModelViewStack();
         LightTexture lightTexture = minecraft.f_91063_.m_109154_();
         RenderSystem.backupProjectionMatrix();
         modelViewStack.m_85836_();

         try {
            modelViewStack.m_166856_();
            modelViewStack.m_252931_(poseStack.m_85850_().m_252922_());
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setProjectionMatrix(new Matrix4f(projectionMatrix), VertexSorting.f_276450_);
            RenderSystem.setShader(GameRenderer::m_172829_);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableDepthTest();
            lightTexture.m_109896_();
            ParticleRenderType renderType = ParticleRenderType.f_107431_;
            Tesselator tesselator = Tesselator.m_85913_();
            BufferBuilder builder = tesselator.m_85915_();
            boolean renderTypeStarted = false;

            try {
               renderType.m_6505_(builder, minecraft.m_91097_());
               renderTypeStarted = true;

               for (JudgementCutDMC4ParticleLayer.Entry entry : List.copyOf(PARTICLES)) {
                  entry.lateRenderable().renderLate(builder, cameraPosition, cameraRotation, partialTick);
               }
            } finally {
               if (renderTypeStarted) {
                  renderType.m_6294_(tesselator);
               }

               RenderSystem.depthMask(true);
               RenderSystem.disableBlend();
               lightTexture.m_109891_();
            }
         } finally {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            modelViewStack.m_85849_();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.restoreProjectionMatrix();
         }
      }
   }

   private static record Entry(Particle particle, JudgementCutDMC4ParticleLayer.LateRenderable lateRenderable, ClientLevel level) {
   }

   interface LateRenderable {
      void renderLate(VertexConsumer var1, Vec3 var2, Quaternionf var3, float var4);
   }
}
