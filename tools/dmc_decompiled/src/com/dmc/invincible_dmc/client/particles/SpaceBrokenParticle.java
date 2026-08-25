package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.model.ACGModel;
import com.dmc.invincible_dmc.client.render.IDRenderType;
import com.guhao.vix.client.NoTextureJsonModel.Triangle;
import com.guhao.vix.client.NoTextureJsonModel.vec3f;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.util.MathUtils;
import com.guhao.vix.util.RenderUtils;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SpaceBrokenParticle extends Particle {
   final int layer;
   float yaw;

   public SpaceBrokenParticle(ClientLevel level, double x, double y, double z, float yaw, int lifetime, int layer) {
      super(level, x, y, z);
      this.f_107219_ = false;
      this.f_107225_ = lifetime;
      this.layer = layer;
      this.yaw = yaw;
   }

   public void m_5989_() {
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      }
   }

   public void m_5744_(VertexConsumer buffer, Camera camera, float pt) {
      if (PostEffectPipelines.isActive()) {
         Vec3 vec3 = camera.m_90583_();
         float f = (float)(this.f_107212_ - vec3.m_7096_());
         float f1 = (float)(this.f_107213_ - vec3.m_7098_() + (double)(this.layer == 0 ? 0.2F : 0.4F));
         float f2 = (float)(this.f_107214_ - vec3.m_7094_());
         float agef = (float)this.f_107224_ + pt;
         float progress = agef / (float)this.f_107225_;
         float shrink = 1.0F;
         if ((Boolean)DMConfig.SPACE_BROKEN_SHRINK_ENABLED.get()) {
            float shrinkStart = ((Double)DMConfig.SPACE_BROKEN_SHRINK_START.get()).floatValue();
            float shrinkEnd = ((Double)DMConfig.SPACE_BROKEN_SHRINK_END.get()).floatValue();
            float shrinkWindow = Math.max(shrinkEnd - shrinkStart, 0.001F);
            if (progress > shrinkStart) {
               float t = Math.min((progress - shrinkStart) / shrinkWindow, 1.0F);
               float smooth = t * t * (3.0F - 2.0F * t);
               shrink = 1.0F - smooth;
            }
         }

         float u0 = 0.0F;
         float u1 = 1.0F;
         float v0 = 0.0F;
         float v1 = 1.0F;
         int light = 15728880;
         float sss = (this.layer == 0 ? 1.3F : 1.4F) * shrink;
         Vector3f camNormal = new Vector3f(camera.m_253058_());
         camNormal.normalize();
         float camYaw = camera.m_90590_() + (float)(this.layer == 0 ? 0 : 45);
         float camPitch = camera.m_90589_();
         camPitch /= 90.0F;
         camPitch = camPitch > 0.0F ? camPitch : -camPitch;
         Quaternionf rot = MathUtils.fromEuler(
            (float)(this.layer == 0 ? 0 : 120), (float)((double)((this.yaw + 30.0F) / 180.0F) * Math.PI) + (float)(this.layer == 0 ? 0 : 75), 0.0F
         );
         rot.mul(this.layer == 0 ? MathUtils.Quat_One : MathUtils.fromEuler(45.0F, 90.0F, 45.0F));

         for (int index = 0; index < ACGModel.SpaceBrokenModel.Face.size(); index++) {
            Triangle triangle = (Triangle)ACGModel.SpaceBrokenModel.Face.get(index);
            Vector3f vertex1 = ((vec3f)ACGModel.SpaceBrokenModel.Positions.get(triangle.x - 1)).toBugJumpFormat();
            Vector3f vertex2 = ((vec3f)ACGModel.SpaceBrokenModel.Positions.get(triangle.y - 1)).toBugJumpFormat();
            Vector3f vertex3 = ((vec3f)ACGModel.SpaceBrokenModel.Positions.get(triangle.z - 1)).toBugJumpFormat();
            vertex1.rotate(rot);
            vertex2.rotate(rot);
            vertex3.rotate(rot);
            vertex1.mul(sss);
            vertex2.mul(sss);
            vertex3.mul(sss);
            vertex1.add(f, f1, f2);
            vertex2.add(f, f1, f2);
            vertex3.add(f, f1, f2);
            Vector3f col_normal = triangle.Normal.toBugJumpFormat();
            col_normal.rotate(rot);
            col_normal.normalize();
            float offset = Math.abs(camNormal.dot(col_normal));
            float ya = (camYaw % 360.0F + 360.0F + 180.0F * offset) % 360.0F / 360.0F;
            ya = (double)ya < 0.5 ? ya * 2.0F : -ya * 2.0F + 2.0F;
            buffer.m_5483_((double)vertex1.x(), (double)vertex1.y(), (double)vertex1.z())
               .m_85950_(offset, ya, camPitch, 1.0F)
               .m_7421_(u1, v0)
               .m_85969_(light)
               .m_5752_();
            buffer.m_5483_((double)vertex2.x(), (double)vertex2.y(), (double)vertex2.z())
               .m_85950_(offset, ya, camPitch, 1.0F)
               .m_7421_(u0, v0)
               .m_85969_(light)
               .m_5752_();
            buffer.m_5483_((double)vertex3.x(), (double)vertex3.y(), (double)vertex3.z())
               .m_85950_(offset, ya, camPitch, 1.0F)
               .m_7421_(u0, v1)
               .m_85969_(light)
               .m_5752_();
         }
      }
   }

   public boolean shouldCull() {
      return false;
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return IDRenderType.SpaceBroken1;
   }

   public void m_107274_() {
      super.m_107274_();
      int count = 5 + this.f_107223_.m_188503_(5);

      for (int i = 0; i < count; i++) {
         float angle = Mth.m_216267_(this.f_107223_, 0.0F, 360.0F);
         float r = Mth.m_216267_(this.f_107223_, 1.5F, 6.0F);
         double sx = Math.sin((double)angle / 180.0 * Math.PI) * (double)r;
         double sy = (double)Mth.m_216267_(this.f_107223_, -0.5F, 2.0F);
         double sz = Math.cos((double)angle / 180.0 * Math.PI) * (double)r;
         double var10003 = sx + this.f_107212_;
         double var10004 = sy + this.f_107213_ + 1.0;
         SpaceBrokenEndParticle spaceBrokenEndParticle = new SpaceBrokenEndParticle(
            Minecraft.m_91087_().f_91073_, var10003, var10004, sz + this.f_107214_, 25 + this.f_107223_.m_188503_(15)
         );
         RenderUtils.AddParticle(Minecraft.m_91087_().f_91073_, spaceBrokenEndParticle);
      }
   }
}
