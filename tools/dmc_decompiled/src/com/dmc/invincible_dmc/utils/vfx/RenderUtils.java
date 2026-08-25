package com.dmc.invincible_dmc.utils.vfx;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec2f;
import yesman.epicfight.api.utils.math.Vec3f;

public class RenderUtils {
   public static final int EmissiveLightPos = 15728880;

   public static void GLSetTexture(ResourceLocation texture) {
      TextureManager texturemanager = Minecraft.m_91087_().m_91097_();
      AbstractTexture abstracttexture = texturemanager.m_118506_(texture);
      RenderSystem.bindTexture(abstracttexture.m_117963_());
      RenderSystem.texParameter(3553, 10242, 33071);
      RenderSystem.texParameter(3553, 10243, 33071);
      RenderSystem.setShaderTexture(0, abstracttexture.m_117963_());
   }

   @OnlyIn(Dist.CLIENT)
   public static void AddParticle(ClientLevel level, Particle particle) {
      Minecraft mc = Minecraft.m_91087_();
      Camera camera = mc.f_91063_.m_109153_();
      if (mc.f_91073_ == level) {
         if (camera.m_90593_() && camera.m_90583_().m_82531_(particle.getPos().f_82479_, particle.getPos().f_82480_, particle.getPos().f_82481_) < 1024.0) {
            mc.f_91061_.m_107344_(particle);
         }
      }
   }

   public static void RenderQuadFaceOnCamera(
      VertexConsumer vertexConsumer, Camera camera, float posX, float posY, float posZ, float r, float g, float b, float a, float scale, float pt
   ) {
      Vector3f[] avector3f = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      Vec3 camPos = camera.m_90583_();
      float x = (float)((double)posX - camPos.m_7096_());
      float y = (float)((double)posY - camPos.m_7098_());
      float z = (float)((double)posZ - camPos.m_7094_());
      Quaternionf camRot = camera.m_253121_();

      for (int j = 0; j < 4; j++) {
         Vector3f vector3f = avector3f[j];
         vector3f.mul(scale);
         vector3f.add(0.0F, 0.0F, -0.2F);
         vector3f.rotate(camRot);
         vector3f.add(x, y, z);
      }

      int var19 = 15728880;
      vertexConsumer.m_5483_((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z())
         .m_85950_(r, g, b, a)
         .m_7421_(0.0F, 0.0F)
         .m_85969_(var19)
         .m_5752_();
      vertexConsumer.m_5483_((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z())
         .m_85950_(r, g, b, a)
         .m_7421_(0.0F, 1.0F)
         .m_85969_(var19)
         .m_5752_();
      vertexConsumer.m_5483_((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z())
         .m_85950_(r, g, b, a)
         .m_7421_(1.0F, 1.0F)
         .m_85969_(var19)
         .m_5752_();
      vertexConsumer.m_5483_((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z())
         .m_85950_(r, g, b, a)
         .m_7421_(1.0F, 0.0F)
         .m_85969_(var19)
         .m_5752_();
   }

   public static void RenderQuadFaceOnCamera2(
      VertexConsumer vertexConsumer, Camera camera, float posX, float posY, float posZ, float r, float g, float b, float a, float scale
   ) {
      Vector3f[] avector3f = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      Vec3 camPos = camera.m_90583_();
      float x = (float)((double)posX - camPos.m_7096_());
      float y = (float)((double)posY - camPos.m_7098_());
      float z = (float)((double)posZ - camPos.m_7094_());
      Quaternionf camRot = camera.m_253121_();

      for (int j = 0; j < 4; j++) {
         Vector3f vector3f = avector3f[j];
         vector3f.mul(scale);
         vector3f.add(0.0F, 0.0F, -0.2F);
         vector3f.rotate(camRot);
         vector3f.add(x, y, z);
      }

      int var18 = 15728880;
      vertexConsumer.m_5483_((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z())
         .m_7421_(0.0F, 0.0F)
         .m_85950_(r, g, b, a)
         .m_85969_(var18)
         .m_5752_();
      vertexConsumer.m_5483_((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z())
         .m_7421_(0.0F, 1.0F)
         .m_85950_(r, g, b, a)
         .m_85969_(var18)
         .m_5752_();
      vertexConsumer.m_5483_((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z())
         .m_7421_(1.0F, 1.0F)
         .m_85950_(r, g, b, a)
         .m_85969_(var18)
         .m_5752_();
      vertexConsumer.m_5483_((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z())
         .m_7421_(1.0F, 0.0F)
         .m_85950_(r, g, b, a)
         .m_85969_(var18)
         .m_5752_();
   }

   public static void translateStack(PoseStack poseStack, OpenMatrix4f mat) {
      poseStack.m_252880_(mat.m30, mat.m31, mat.m32);
   }

   public static void rotateStack(PoseStack poseStack, OpenMatrix4f mat) {
      OpenMatrix4f localBuffer = new OpenMatrix4f();
      OpenMatrix4f.transpose(mat, localBuffer);
      poseStack.m_252781_(getQuaternionFromMatrix(localBuffer));
   }

   public static void scaleStack(PoseStack poseStack, OpenMatrix4f mat) {
      OpenMatrix4f localBuffer = new OpenMatrix4f();
      OpenMatrix4f.transpose(mat, localBuffer);
      Vector3f vector = getScaleVectorFromMatrix(localBuffer);
      poseStack.m_85841_(vector.x(), vector.y(), vector.z());
   }

   private static Vector3f getScaleVectorFromMatrix(OpenMatrix4f mat) {
      Vec3f a = new Vec3f(mat.m00, mat.m10, mat.m20);
      Vec3f b = new Vec3f(mat.m01, mat.m11, mat.m21);
      Vec3f c = new Vec3f(mat.m02, mat.m12, mat.m22);
      return new Vector3f(a.length(), b.length(), c.length());
   }

   private static Quaternionf getQuaternionFromMatrix(OpenMatrix4f mat) {
      OpenMatrix4f t = mat.transpose(null);
      Matrix4f jomlMat = new Matrix4f(t.m00, t.m10, t.m20, t.m30, t.m01, t.m11, t.m21, t.m31, t.m02, t.m12, t.m22, t.m32, t.m03, t.m13, t.m23, t.m33);
      Quaternionf quat = new Quaternionf();
      jomlMat.getUnnormalizedRotation(quat);
      return quat;
   }

   public static class Quad {
      public final Vec2f[] uvs = new Vec2f[4];
      public final Vec3f[] vertexs = new Vec3f[4];

      public Quad() {
         for (int i = 0; i < 4; i++) {
            this.uvs[i] = new Vec2f(0.0F, 0.0F);
            this.vertexs[i] = new Vec3f(0.0F, 0.0F, 0.0F);
         }
      }
   }
}
