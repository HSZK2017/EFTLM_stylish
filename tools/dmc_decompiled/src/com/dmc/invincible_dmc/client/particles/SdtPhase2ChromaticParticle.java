package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.client.render.IDRenderType;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import yesman.epicfight.client.particle.HitParticle;

@OnlyIn(Dist.CLIENT)
public class SdtPhase2ChromaticParticle extends HitParticle {
   public static SpriteSet SHARED_SPRITE_SET;
   private float hue = this.f_107223_.m_188501_();

   public SdtPhase2ChromaticParticle(ClientLevel world, double x, double y, double z, SpriteSet animatedSprite) {
      super(world, x, y, z, animatedSprite);
      this.updateColorFromHue();
      this.f_107227_ = 1.0F;
      this.f_107228_ = 1.0F;
      this.f_107229_ = 1.0F;
      this.f_107663_ = 2.535F;
      this.f_107225_ = 9;
      this.m_108339_(animatedSprite);
   }

   private void updateColorFromHue() {
      int rgb = Color.HSBtoRGB(this.hue, 1.0F, 1.0F);
      this.f_107227_ = (float)(rgb >> 16 & 0xFF) / 255.0F;
      this.f_107228_ = (float)(rgb >> 8 & 0xFF) / 255.0F;
      this.f_107229_ = (float)(rgb & 0xFF) / 255.0F;
   }

   public void m_5989_() {
      super.m_5989_();
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      } else {
         this.m_108339_(this.animatedSprite);
      }

      float ageRatio = (float)this.f_107224_ / (float)this.f_107225_;
      this.hue = (this.hue + ageRatio * 0.1F) % 1.0F;
      this.updateColorFromHue();
   }

   public void m_5744_(@NotNull VertexConsumer buffer, @NotNull Camera renderInfo, float partialTicks) {
      if (PostEffectPipelines.isActive()) {
         IDRenderType.enhancedChromaticAberrationRenderType(this.f_108321_.m_247685_()).callPipeline();
         Vec3 view = renderInfo.m_90583_();
         float x = (float)(Mth.m_14139_((double)partialTicks, this.f_107209_, this.f_107212_) - view.m_7096_());
         float y = (float)(Mth.m_14139_((double)partialTicks, this.f_107210_, this.f_107213_) - view.m_7098_());
         float z = (float)(Mth.m_14139_((double)partialTicks, this.f_107211_, this.f_107214_) - view.m_7094_());
         Quaternionf quaternion = this.f_107231_ == 0.0F
            ? renderInfo.m_253121_()
            : new Quaternionf(renderInfo.m_253121_()).rotateZ(Mth.m_14179_(partialTicks, this.f_107204_, this.f_107231_));
         float ageRatio = Mth.m_14036_(((float)this.f_107224_ + partialTicks) / (float)this.f_107225_, 0.0F, 1.0F);
         float size = this.f_107663_ * (1.0F - ageRatio);
         int light = this.m_6355_(partialTicks);
         this.renderColoredQuad(buffer, quaternion, x, y, z, size, 1.0F, 1.0F, 1.0F, light);
      }
   }

   private void renderColoredQuad(VertexConsumer buffer, Quaternionf rotation, float x, float y, float z, float size, float r, float g, float b, int light) {
      Vector3f[] vectors = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };

      for (int i = 0; i < 4; i++) {
         vectors[i].rotate(rotation);
         vectors[i].mul(size);
         vectors[i].add(x, y, z);
      }

      float u0 = this.m_5970_();
      float u1 = this.m_5952_();
      float v0 = this.m_5951_();
      float v1 = this.m_5950_();
      float finalR = r * this.f_107227_;
      float finalG = g * this.f_107228_;
      float finalB = b * this.f_107229_;
      buffer.m_5483_((double)vectors[0].x(), (double)vectors[0].y(), (double)vectors[0].z())
         .m_7421_(u1, v1)
         .m_85950_(finalR, finalG, finalB, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
      buffer.m_5483_((double)vectors[1].x(), (double)vectors[1].y(), (double)vectors[1].z())
         .m_7421_(u1, v0)
         .m_85950_(finalR, finalG, finalB, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
      buffer.m_5483_((double)vectors[2].x(), (double)vectors[2].y(), (double)vectors[2].z())
         .m_7421_(u0, v0)
         .m_85950_(finalR, finalG, finalB, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
      buffer.m_5483_((double)vectors[3].x(), (double)vectors[3].y(), (double)vectors[3].z())
         .m_7421_(u0, v1)
         .m_85950_(finalR, finalG, finalB, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return IDRenderType.enhancedChromaticAberrationRenderType(this.f_108321_.m_247685_());
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Provider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
         SdtPhase2ChromaticParticle.SHARED_SPRITE_SET = spriteSet;
      }

      public Particle createParticle(
         @NotNull SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new SdtPhase2ChromaticParticle(world, x, y, z, this.spriteSet);
      }
   }
}
