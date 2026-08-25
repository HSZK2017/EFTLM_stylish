package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.client.effeks.JudgementCut4Effek;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class JudgementCutSequenceNormalDMC4Particle extends TextureSheetParticle implements JudgementCutDMC4ParticleLayer.LateRenderable {
   private static final float SOURCE_HEIGHT = 636.0F;
   private static final float WIDTH_SCALE = 0.8867925F;
   private static final float HEIGHT_SCALE = 0.9748428F;
   private final SpriteSet sprites;
   private final Quaternionf renderRotation = new Quaternionf();

   protected JudgementCutSequenceNormalDMC4Particle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
      super(level, x, y, z);
      this.sprites = sprites;
      this.f_107225_ = 10;
      this.f_107663_ = 4.2F;
      this.m_108337_(sprites.m_5819_(0, this.f_107225_));
      JudgementCutDMC4ParticleLayer.register(this, level);
      JudgementCut4Effek.playJC(JudgementCut4Effek.Type.LEVEL1, this.f_107208_, this.f_107212_, this.f_107213_, this.f_107214_, 0.6912F);
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      }
   }

   public void m_5744_(VertexConsumer buffer, Camera camera, float partialTick) {
   }

   @Override
   public void renderLate(VertexConsumer buffer, Vec3 cameraPosition, Quaternionf cameraRotation, float partialTick) {
      this.renderQuad(buffer, cameraPosition, cameraRotation, partialTick);
   }

   private void renderQuad(VertexConsumer buffer, Vec3 cameraPosition, Quaternionf cameraRotation, float partialTick) {
      float x = (float)(Mth.m_14139_((double)partialTick, this.f_107209_, this.f_107212_) - cameraPosition.m_7096_());
      float y = (float)(Mth.m_14139_((double)partialTick, this.f_107210_, this.f_107213_) - cameraPosition.m_7098_());
      float z = (float)(Mth.m_14139_((double)partialTick, this.f_107211_, this.f_107214_) - cameraPosition.m_7094_());
      float ageWithPartial = Math.min((float)this.f_107224_ + partialTick, (float)this.f_107225_);
      int virtualAge = (int)(ageWithPartial * 1000.0F);
      int virtualLifetime = this.f_107225_ * 1000;
      this.m_108337_(this.sprites.m_5819_(virtualAge, virtualLifetime));
      this.renderRotation.set(cameraRotation);
      if (this.f_107231_ != 0.0F) {
         this.renderRotation.mul(Axis.f_252403_.m_252961_(Mth.m_14179_(partialTick, this.f_107204_, this.f_107231_)));
      }

      Vector3f[] vertices = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      float size = this.m_5902_(partialTick);
      float halfWidth = size * 0.8867925F;
      float halfHeight = size * 0.9748428F;

      for (Vector3f vertex : vertices) {
         vertex.mul(halfWidth, halfHeight, 1.0F);
         vertex.rotate(this.renderRotation);
         vertex.add(x, y, z);
      }

      float minU = this.m_5970_();
      float maxU = this.m_5952_();
      float minV = this.m_5951_();
      float maxV = this.m_5950_();
      int light = this.m_6355_(partialTick);
      buffer.m_5483_((double)vertices[0].x(), (double)vertices[0].y(), (double)vertices[0].z())
         .m_7421_(maxU, maxV)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
      buffer.m_5483_((double)vertices[1].x(), (double)vertices[1].y(), (double)vertices[1].z())
         .m_7421_(maxU, minV)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
      buffer.m_5483_((double)vertices[2].x(), (double)vertices[2].y(), (double)vertices[2].z())
         .m_7421_(minU, minV)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
      buffer.m_5483_((double)vertices[3].x(), (double)vertices[3].y(), (double)vertices[3].z())
         .m_7421_(minU, maxV)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   public int m_6355_(float partialTick) {
      return 15728880;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(
         SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new JudgementCutSequenceNormalDMC4Particle(level, x, y, z, this.sprites);
      }
   }
}
