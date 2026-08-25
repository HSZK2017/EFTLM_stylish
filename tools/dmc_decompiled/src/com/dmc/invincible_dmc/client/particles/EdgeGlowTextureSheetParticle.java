package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.client.render.IDRenderType;
import com.dmc.invincible_dmc.client.render.custom.EdgeGlowParticleRenderType;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public abstract class EdgeGlowTextureSheetParticle extends TextureSheetParticle {
   private EdgeGlowParticleRenderType cachedRenderType;

   protected EdgeGlowTextureSheetParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
      super(level, x, y, z);
      this.m_108337_(sprites.m_5819_(0, this.f_107225_));
   }

   protected float getEdgeIntensity() {
      return 0.8F;
   }

   protected float getGlowIntensity() {
      return 0.9F;
   }

   protected float getGlowRadius() {
      return 4.0F;
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      if (this.cachedRenderType == null) {
         this.cachedRenderType = IDRenderType.getEdgeGlowRenderType(
            this.f_108321_.m_247685_(), this.getEdgeIntensity(), this.getGlowIntensity(), this.getGlowRadius()
         );
      }

      return this.cachedRenderType;
   }

   public void m_5744_(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTick) {
      if (PostEffectPipelines.isActive()) {
         if (this.m_7556_() instanceof EdgeGlowParticleRenderType edgeRt) {
            edgeRt.callPipeline();
         }

         Vec3 camPos = camera.m_90583_();
         float px = (float)(Mth.m_14139_((double)partialTick, this.f_107209_, this.f_107212_) - camPos.m_7096_());
         float py = (float)(Mth.m_14139_((double)partialTick, this.f_107210_, this.f_107213_) - camPos.m_7098_());
         float pz = (float)(Mth.m_14139_((double)partialTick, this.f_107211_, this.f_107214_) - camPos.m_7094_());
         Quaternionf rotation = new Quaternionf(camera.m_253121_());
         if (this.f_107231_ != 0.0F) {
            rotation.rotateZ(Mth.m_14179_(partialTick, this.f_107204_, this.f_107231_));
         }

         Vector3f[] v = new Vector3f[]{
            new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
         };
         float size = this.m_5902_(partialTick);

         for (int i = 0; i < 4; i++) {
            v[i].rotate(rotation);
            v[i].mul(size);
            v[i].add(px, py, pz);
         }

         float u0 = this.m_5970_();
         float u1 = this.m_5952_();
         float v0 = this.m_5951_();
         float v1 = this.m_5950_();
         int light = this.m_6355_(partialTick);
         buffer.m_5483_((double)v[0].x(), (double)v[0].y(), (double)v[0].z())
            .m_7421_(u1, v1)
            .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
            .m_85969_(light)
            .m_5752_();
         buffer.m_5483_((double)v[1].x(), (double)v[1].y(), (double)v[1].z())
            .m_7421_(u1, v0)
            .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
            .m_85969_(light)
            .m_5752_();
         buffer.m_5483_((double)v[2].x(), (double)v[2].y(), (double)v[2].z())
            .m_7421_(u0, v0)
            .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
            .m_85969_(light)
            .m_5752_();
         buffer.m_5483_((double)v[3].x(), (double)v[3].y(), (double)v[3].z())
            .m_7421_(u0, v1)
            .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
            .m_85969_(light)
            .m_5752_();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Provider<T extends ParticleOptions> implements ParticleProvider<T> {
      protected final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }
   }
}
