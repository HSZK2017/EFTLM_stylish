package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.client.render.IDRenderType;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import yesman.epicfight.client.particle.HitParticle;

@OnlyIn(Dist.CLIENT)
public class SdtPhase2Particle extends HitParticle {
   public static SpriteSet SHARED_SPRITE_SET;
   private final Entity entity;
   private float hue;

   public SdtPhase2Particle(ClientLevel world, Entity entity) {
      super(world, entity.m_20185_(), entity.m_20186_() + (double)entity.m_20206_() / 2.0, entity.m_20189_(), SHARED_SPRITE_SET);
      this.entity = entity;
      this.hue = this.f_107223_.m_188501_();
      this.updateColorFromHue();
      this.f_107227_ = 1.0F;
      this.f_107228_ = 1.0F;
      this.f_107229_ = 1.0F;
      this.f_107663_ = 1.95F;
      this.f_107225_ = Integer.MAX_VALUE;
      if (SHARED_SPRITE_SET != null) {
         this.m_108339_(SHARED_SPRITE_SET);
      }
   }

   private void updateColorFromHue() {
      int rgb = Color.HSBtoRGB(this.hue, 1.0F, 1.0F);
      this.f_107227_ = (float)(rgb >> 16 & 0xFF) / 255.0F;
      this.f_107228_ = (float)(rgb >> 8 & 0xFF) / 255.0F;
      this.f_107229_ = (float)(rgb & 0xFF) / 255.0F;
   }

   public void m_5989_() {
      if (!this.entity.m_213877_() && this.entity.m_9236_() == this.f_107208_) {
         this.f_107209_ = this.f_107212_;
         this.f_107210_ = this.f_107213_;
         this.f_107211_ = this.f_107214_;
         this.f_107212_ = this.entity.m_20185_();
         this.f_107213_ = this.entity.m_20186_() + (double)this.entity.m_20206_() / 2.0;
         this.f_107214_ = this.entity.m_20189_();
         this.f_107204_ = this.f_107231_;
         if (SHARED_SPRITE_SET != null) {
            this.m_108339_(SHARED_SPRITE_SET);
         }

         float hueShift = (float)(System.currentTimeMillis() % 5000L) / 5000.0F * 0.05F;
         this.hue = (this.hue + hueShift) % 1.0F;
         this.updateColorFromHue();
      } else {
         this.m_107274_();
      }
   }

   public void m_5744_(@NotNull VertexConsumer buffer, @NotNull Camera renderInfo, float partialTicks) {
      if (PostEffectPipelines.isActive()) {
         IDRenderType.ChromaticAberrationRenderType(this.f_108321_.m_247685_()).callPipeline();
         Vec3 view = renderInfo.m_90583_();
         float x = (float)(Mth.m_14139_((double)partialTicks, this.f_107209_, this.f_107212_) - view.m_7096_());
         float y = (float)(Mth.m_14139_((double)partialTicks, this.f_107210_, this.f_107213_) - view.m_7098_());
         float z = (float)(Mth.m_14139_((double)partialTicks, this.f_107211_, this.f_107214_) - view.m_7094_());
         Quaternionf quaternion = this.f_107231_ == 0.0F
            ? renderInfo.m_253121_()
            : new Quaternionf(renderInfo.m_253121_()).rotateZ(Mth.m_14179_(partialTicks, this.f_107204_, this.f_107231_));
         int light = this.m_6355_(partialTicks);
         this.renderColoredQuad(buffer, quaternion, x, y, z, this.f_107663_, 1.0F, 1.0F, 1.0F, light);
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
      return IDRenderType.ChromaticAberrationRenderType(this.f_108321_.m_247685_());
   }
}
