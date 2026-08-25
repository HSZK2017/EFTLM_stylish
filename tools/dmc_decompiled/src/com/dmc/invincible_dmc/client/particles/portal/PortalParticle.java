package com.dmc.invincible_dmc.client.particles.portal;

import com.dmc.invincible_dmc.client.particles.EdgeGlowTextureSheetParticle;
import com.dmc.invincible_dmc.client.render.custom.EdgeGlowParticleRenderType;
import com.dmc.invincible_dmc.entity.portal.PortalEntity;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class PortalParticle extends EdgeGlowTextureSheetParticle {
   private static final int SWITCH_FRAMES = 13;
   private static final int IDLE_FRAMES = 16;
   private static final int TOTAL_FRAMES = 29;
   private static final float PORTAL_SIZE = 10.0F;
   private final int targetEntityId;
   private PortalEntity targetEntity;
   private final SpriteSet sprites;
   private static EdgeGlowParticleRenderType portalRenderType;

   public PortalParticle(ClientLevel level, double x, double y, double z, int entityId, SpriteSet sprites) {
      super(level, x, y, z, sprites);
      this.targetEntityId = entityId;
      this.sprites = sprites;
      this.f_107225_ = 1000;
      this.f_107226_ = 0.0F;
      this.f_107219_ = false;
      this.m_108337_(this.sprites.m_5819_(0, 28));
   }

   @Override
   protected float getEdgeIntensity() {
      return 0.6F;
   }

   @Override
   protected float getGlowIntensity() {
      return 1.45F;
   }

   @Override
   protected float getGlowRadius() {
      return 6.0F;
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      if (this.targetEntity == null) {
         if (!(this.f_107208_.m_6815_(this.targetEntityId) instanceof PortalEntity portal)) {
            this.m_107274_();
            return;
         }

         this.targetEntity = portal;
      }

      if (!this.targetEntity.m_6084_()) {
         this.m_107274_();
      } else {
         this.m_107264_(this.targetEntity.m_20185_(), this.targetEntity.m_20186_() + 1.0, this.targetEntity.m_20189_());
      }
   }

   private int getFrameIndex(float partialTicks) {
      if (this.targetEntity != null && this.targetEntity.isClosing()) {
         int closeFrame = Math.min(this.targetEntity.getClosingTicks(), 12);
         return 12 - closeFrame;
      } else {
         int frame = Mth.m_14143_(Math.max(0.0F, (float)(this.targetEntity.f_19797_ - 1) + partialTicks));
         return frame < 13 ? frame : 13 + (frame - 13) % 16;
      }
   }

   @Override
   public void m_5744_(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
      if (this.targetEntity != null) {
         if (PostEffectPipelines.isActive()) {
            if (this.m_7556_() instanceof EdgeGlowParticleRenderType edgeRt) {
               edgeRt.callPipeline();
            }

            int frameIndex = this.getFrameIndex(partialTicks);
            this.m_108337_(this.sprites.m_5819_(frameIndex, 28));
            Vec3 camPos = camera.m_90583_();
            float px = (float)(Mth.m_14139_((double)partialTicks, this.f_107209_, this.f_107212_) - camPos.m_7096_());
            float py = (float)(Mth.m_14139_((double)partialTicks, this.f_107210_, this.f_107213_) - camPos.m_7098_());
            float pz = (float)(Mth.m_14139_((double)partialTicks, this.f_107211_, this.f_107214_) - camPos.m_7094_());
            float entityYaw = Mth.m_14179_(partialTicks, this.targetEntity.f_19859_, this.targetEntity.m_146908_());
            Quaternionf rotation = Axis.f_252436_.m_252977_(180.0F - entityYaw);
            Vector3f[] v = new Vector3f[]{
               new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F)
            };
            float scaledSize = 10.0F * this.targetEntity.getScale();
            float halfSize = scaledSize / 2.0F;

            for (int i = 0; i < 4; i++) {
               v[i].mul(halfSize);
               v[i].rotate(rotation);
               v[i].add(px, py, pz);
            }

            float u0 = this.m_5970_();
            float u1 = this.m_5952_();
            float v0 = this.m_5951_();
            float v1 = this.m_5950_();
            int light = this.m_6355_(partialTicks);
            buffer.m_5483_((double)v[0].x(), (double)v[0].y(), (double)v[0].z()).m_7421_(u1, v1).m_6122_(255, 255, 255, 255).m_85969_(light).m_5752_();
            buffer.m_5483_((double)v[1].x(), (double)v[1].y(), (double)v[1].z()).m_7421_(u0, v1).m_6122_(255, 255, 255, 255).m_85969_(light).m_5752_();
            buffer.m_5483_((double)v[2].x(), (double)v[2].y(), (double)v[2].z()).m_7421_(u0, v0).m_6122_(255, 255, 255, 255).m_85969_(light).m_5752_();
            buffer.m_5483_((double)v[3].x(), (double)v[3].y(), (double)v[3].z()).m_7421_(u1, v0).m_6122_(255, 255, 255, 255).m_85969_(light).m_5752_();
         }
      }
   }

   @NotNull
   @Override
   public ParticleRenderType m_7556_() {
      if (portalRenderType == null) {
         portalRenderType = new PortalParticle.PortalEdgeGlowRenderType(
            this.f_108321_.m_247685_(), this.getEdgeIntensity(), this.getGlowIntensity(), this.getGlowRadius()
         );
      }

      return portalRenderType;
   }

   private static final class PortalEdgeGlowRenderType extends EdgeGlowParticleRenderType {
      private PortalEdgeGlowRenderType(ResourceLocation texture, float edgeIntensity, float glowIntensity, float glowRadius) {
         super(ResourceLocation.fromNamespaceAndPath("invincible_dmc", "portal_edge_glow"), texture, edgeIntensity, glowIntensity, glowRadius);
      }

      @Override
      public void m_6505_(BufferBuilder bufferBuilder, TextureManager textureManager) {
         super.m_6505_(bufferBuilder, textureManager);
         RenderSystem.depthMask(false);
      }

      @Override
      public void m_6294_(Tesselator tesselator) {
         super.m_6294_(tesselator);
         RenderSystem.depthMask(true);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider extends EdgeGlowTextureSheetParticle.Provider<SimpleParticleType> {
      public Provider(SpriteSet sprites) {
         super(sprites);
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new PortalParticle(level, x, y, z, (int)xSpeed, this.sprites);
      }
   }
}
