package com.dmc.invincible_dmc.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ProceduralSlashMesh {
   private static final int STRIDE = 6;
   private static final Matrix3f IDENTITY_NORMAL = new Matrix3f();
   private final float[] data;

   public ProceduralSlashMesh(float innerRadius, float outerRadius, float thickness, float sweepAngle, int segments) {
      this.data = buildArc(innerRadius, outerRadius, thickness, sweepAngle, segments);
   }

   private static float[] buildArc(float innerR, float outerR, float thickness, float sweepAngle, int segments) {
      int radialSegments = 2;
      float sweepRad = (float)Math.toRadians((double)sweepAngle);
      int vertCount = segments * radialSegments * 12;
      float[] vd = new float[vertCount * 6];
      int vi = 0;

      for (int i = 0; i < segments; i++) {
         float u0 = (float)i / (float)segments;
         float u1 = (float)(i + 1) / (float)segments;
         float a0 = u0 * sweepRad;
         float a1 = u1 * sweepRad;
         float sin0 = (float)Math.sin((double)a0);
         float cos0 = (float)Math.cos((double)a0);
         float sin1 = (float)Math.sin((double)a1);
         float cos1 = (float)Math.cos((double)a1);
         float su0 = (float)Math.sin((double)u0 * Math.PI);
         float su1 = (float)Math.sin((double)u1 * Math.PI);
         float ef0 = (float)Math.pow((double)su0, 0.6);
         float ef1 = (float)Math.pow((double)su1, 0.6);

         for (int j = 0; j < radialSegments; j++) {
            float v0 = (float)j / (float)radialSegments;
            float v1 = (float)(j + 1) / (float)radialSegments;
            float r0 = innerR + v0 * (outerR - innerR);
            float r1 = innerR + v1 * (outerR - innerR);
            float sv0 = (float)Math.sin((double)v0 * Math.PI);
            float sv1 = (float)Math.sin((double)v1 * Math.PI);
            float am0 = (float)Math.pow((double)sv0, 0.5);
            float am1 = (float)Math.pow((double)sv1, 0.5);
            float ey0 = thickness * sv0;
            float ey1 = thickness * sv1;
            float x00 = r0 * sin0;
            float z00 = r0 * cos0;
            float x10 = r0 * sin1;
            float z10 = r0 * cos1;
            float x01 = r1 * sin0;
            float z01 = r1 * cos0;
            float x11 = r1 * sin1;
            float z11 = r1 * cos1;
            float y00t = ey0 * ef0;
            float y00b = -ey0 * ef0;
            float y10t = ey0 * ef1;
            float y10b = -ey0 * ef1;
            float y01t = ey1 * ef0;
            float y01b = -ey1 * ef0;
            float y11t = ey1 * ef1;
            float y11b = -ey1 * ef1;
            vi = put(vd, vi, x00, y00t, z00, u0, v0, am0, x10, y10t, z10, u1, v0, am0, x11, y11t, z11, u1, v1, am1);
            vi = put(vd, vi, x00, y00t, z00, u0, v0, am0, x11, y11t, z11, u1, v1, am1, x01, y01t, z01, u0, v1, am1);
            vi = put(vd, vi, x01, y01b, z01, u0, v1, am1, x11, y11b, z11, u1, v1, am1, x10, y10b, z10, u1, v0, am0);
            vi = put(vd, vi, x01, y01b, z01, u0, v1, am1, x10, y10b, z10, u1, v0, am0, x00, y00b, z00, u0, v0, am0);
         }
      }

      return vd;
   }

   private static int put(
      float[] vd,
      int vi,
      float x0,
      float y0,
      float z0,
      float u0,
      float v0,
      float a0,
      float x1,
      float y1,
      float z1,
      float u1,
      float v1,
      float a1,
      float x2,
      float y2,
      float z2,
      float u2,
      float v2,
      float a2
   ) {
      vd[vi] = x0;
      vd[vi + 1] = y0;
      vd[vi + 2] = z0;
      vd[vi + 3] = u0;
      vd[vi + 4] = v0;
      vd[vi + 5] = a0;
      vi += 6;
      vd[vi] = x1;
      vd[vi + 1] = y1;
      vd[vi + 2] = z1;
      vd[vi + 3] = u1;
      vd[vi + 4] = v1;
      vd[vi + 5] = a1;
      vi += 6;
      vd[vi] = x2;
      vd[vi + 1] = y2;
      vd[vi + 2] = z2;
      vd[vi + 3] = u2;
      vd[vi + 4] = v2;
      vd[vi + 5] = a2;
      return vi + 6;
   }

   public void render(
      PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, float r, float g, float b, float alpha, float uOffset, float vOffset
   ) {
      Matrix4f mm = poseStack.m_85850_().m_252922_();
      float[] d = this.data;
      int i = 0;

      for (int n = d.length; i < n; i += 6) {
         float fa = alpha * d[i + 5];
         consumer.m_252986_(mm, d[i], d[i + 1], d[i + 2])
            .m_85950_(r, g, b, fa)
            .m_7421_(d[i + 3] + uOffset, d[i + 4] + vOffset)
            .m_86008_(packedOverlay)
            .m_85969_(packedLight)
            .m_252939_(IDENTITY_NORMAL, 0.0F, 1.0F, 0.0F)
            .m_5752_();
      }
   }
}
