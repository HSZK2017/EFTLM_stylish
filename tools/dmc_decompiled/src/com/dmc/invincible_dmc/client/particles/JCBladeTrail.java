package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.client.render.IDRenderType;
import com.dmc.invincible_dmc.client.render.custom.BloomParticleRenderType;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class JCBladeTrail extends SingleQuadParticle {
   static ResourceLocation texture = IDRenderType.GetTexture("particle/sparks");
   static BloomParticleRenderType renderType = IDRenderType.getBloomRenderTypeByTexture(texture);
   protected final double X;
   protected final double Y;
   protected final double Z;
   protected float timeOffset = 0.0F;

   public JCBladeTrail(ClientLevel level, double x, double y, double z, double rx, double ry, double rz) {
      super(level, x, y, z, rx, ry, rz);
      this.f_107225_ = 9;
      this.timeOffset = Mth.m_216267_(this.f_107223_, 0.0F, 1.0F);
      this.X = x;
      this.Y = y;
      this.Z = z;
      this.f_107215_ = rx;
      this.f_107216_ = ry;
      this.f_107217_ = rz;
      this.f_107227_ = 0.55F;
      this.f_107228_ = 0.6902F;
      this.f_107229_ = 1.0F;
      this.f_107230_ = 0.8F;
   }

   public boolean shouldCull() {
      return false;
   }

   protected float m_5970_() {
      return 0.0F;
   }

   protected float m_5952_() {
      return 0.0F;
   }

   protected float m_5951_() {
      return 0.0F;
   }

   protected float m_5950_() {
      return 0.0F;
   }

   public void m_5989_() {
      if (this.f_107224_++ > this.f_107225_) {
         this.m_107274_();
      }
   }

   public void m_5744_(VertexConsumer buffer, Camera camera, float pt) {
      if (PostEffectPipelines.isActive()) {
         float at = (float)this.f_107224_ + pt;
         if (!(at < this.timeOffset) && !(at > (float)this.f_107225_ + this.timeOffset)) {
            BloomParticleRenderType.markBloomDrawn();
            renderType.callPipeline();
            float t = Math.min(1.0F, at / (float)this.f_107225_ * 2.5F);
            Vec3 vec3 = camera.m_90583_();
            float f = (float)(this.X - vec3.m_7096_());
            float f1 = (float)(this.Y - vec3.m_7098_());
            float f2 = (float)(this.Z - vec3.m_7094_());
            Vector3f right = new Vector3f(f, f1, f2);
            Vector3f dir = new Vector3f((float)this.f_107215_, (float)this.f_107216_, (float)this.f_107217_);
            dir.mul(t);
            right.cross(dir);
            right.normalize();
            float _t = (float)Math.sqrt((double)Math.min(1.0F, ((float)this.f_107225_ - at) / (float)this.f_107225_ * 2.5F));
            right.mul(0.015F * _t);
            Vector3f left = new Vector3f(right);
            left.mul(-1.0F);
            Vector3f[] points = new Vector3f[]{new Vector3f(right), new Vector3f(left), new Vector3f(right), new Vector3f(right)};
            points[2].add(dir);
            points[3].add(dir);

            for (int i = 0; i < 4; i++) {
               Vector3f vector3f = points[i];
               vector3f.add(f, f1, f2);
            }

            float u0 = 0.0F;
            float u1 = 1.0F;
            float v0 = 0.0F;
            float v1 = 1.0F;
            int light = 15728880;
            buffer.m_5483_((double)points[0].x(), (double)points[0].y(), (double)points[0].z())
               .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
               .m_7421_(u1, v1)
               .m_85969_(light)
               .m_5752_();
            buffer.m_5483_((double)points[1].x(), (double)points[1].y(), (double)points[1].z())
               .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
               .m_7421_(u1, v0)
               .m_85969_(light)
               .m_5752_();
            buffer.m_5483_((double)points[2].x(), (double)points[2].y(), (double)points[2].z())
               .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
               .m_7421_(u0, v0)
               .m_85969_(light)
               .m_5752_();
            buffer.m_5483_((double)points[3].x(), (double)points[3].y(), (double)points[3].z())
               .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
               .m_7421_(u0, v1)
               .m_85969_(light)
               .m_5752_();
         }
      }
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return renderType;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new JCBladeTrail(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}
