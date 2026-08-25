package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.client.render.IDRenderType;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.util.MathUtils;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SpaceBrokenEndParticle extends Particle {
   private final float rotation;
   private final Vector3f vector = new Vector3f(
      Mth.m_216267_(this.f_107223_, -1.0F, 1.0F), Mth.m_216267_(this.f_107223_, -1.0F, 1.0F), Mth.m_216267_(this.f_107223_, -1.0F, 1.0F)
   );
   private final float rotSpeed;
   private final int style;

   public SpaceBrokenEndParticle(ClientLevel level, double x, double y, double z, int lifetime) {
      super(level, x, y, z);
      this.vector.normalize();
      this.rotation = Mth.m_216267_(this.f_107223_, 0.0F, 360.0F);
      this.rotSpeed = Mth.m_216267_(this.f_107223_, 5.0F, 10.0F);
      this.f_107225_ = lifetime;
      this.style = this.f_107223_.m_188503_(4);
      this.f_107226_ = Mth.m_216267_(this.f_107223_, 1.0F, 2.0F);
      this.f_107216_ = -0.1F;
   }

   public void m_5989_() {
      super.m_5989_();
      if (this.f_107224_ > 1 && this.f_107213_ - this.f_107210_ > -0.1F) {
         this.m_107274_();
      }
   }

   public void m_5744_(VertexConsumer buffer, Camera camera, float pt) {
      if (PostEffectPipelines.isActive()) {
         IDRenderType.SpaceBrokenEnd.callPipeline();
         float agef = (float)this.f_107224_ + pt;
         Vec3 camPos = camera.m_90583_();
         float f = (float)(Mth.m_14139_((double)pt, this.f_107209_, this.f_107212_) - camPos.m_7096_());
         float f1 = (float)(Mth.m_14139_((double)pt, this.f_107210_, this.f_107213_) - camPos.m_7098_());
         float f2 = (float)(Mth.m_14139_((double)pt, this.f_107211_, this.f_107214_) - camPos.m_7094_());
         Vector3f[] avector3f = new Vector3f[]{
            new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
         };
         Vector3f normal = new Vector3f(0.0F, 0.0F, 1.0F);
         float f4 = 1.2F;
         Quaternionf quaternion = new Quaternionf(new AxisAngle4f(MathUtils.toDegrees(this.rotation + this.rotSpeed * agef), this.vector));

         for (int i = 0; i < 4; i++) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
         }

         normal.rotate(quaternion);
         int c = this.style % 2;
         int r = this.style / 2;
         float f7 = (float)c * 0.5F;
         float f8 = (float)(c + 1) * 0.5F;
         float f5 = (float)r * 0.5F;
         float f6 = (float)(r + 1) * 0.5F;
         int lightColor = 15728880;
         Vector3f camNormal = new Vector3f(camera.m_253058_());
         camNormal.normalize();
         normal.normalize();
         float offset = Math.abs(camNormal.dot(normal));
         float camYaw = camera.m_90590_();
         camYaw = (camYaw % 360.0F + 360.0F + 180.0F * offset) % 360.0F / 360.0F;
         camYaw = (double)camYaw < 0.5 ? camYaw * 2.0F : -camYaw * 2.0F + 2.0F;
         float camPitch = camera.m_90589_();
         camPitch /= 90.0F;
         camPitch = camPitch > 0.0F ? camPitch : -camPitch;
         buffer.m_5483_((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z())
            .m_85950_(offset, camYaw, camPitch, 1.0F)
            .m_7421_(f8, f6)
            .m_85969_(lightColor)
            .m_5752_();
         buffer.m_5483_((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z())
            .m_85950_(offset, camYaw, camPitch, 1.0F)
            .m_7421_(f8, f5)
            .m_85969_(lightColor)
            .m_5752_();
         buffer.m_5483_((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z())
            .m_85950_(offset, camYaw, camPitch, 1.0F)
            .m_7421_(f7, f5)
            .m_85969_(lightColor)
            .m_5752_();
         buffer.m_5483_((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z())
            .m_85950_(offset, camYaw, camPitch, 1.0F)
            .m_7421_(f7, f6)
            .m_85969_(lightColor)
            .m_5752_();
      }
   }

   public boolean shouldCull() {
      return false;
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return IDRenderType.SpaceBrokenEnd;
   }
}
