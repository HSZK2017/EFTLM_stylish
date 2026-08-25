package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.client.render.screenshader.RiftAttractionEffect;
import com.dmc.invincible_dmc.utils.vfx.LocalScreenEffectGate;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public final class YamatoSpaceRiftParticle extends Particle {
   private static final double FORWARD_DISTANCE = 9.0;
   private static final double RIGHT_OFFSET = -0.15;
   private static final double HEIGHT_OFFSET = 0.48;
   private static final double HALF_LENGTH = 9.75;
   private static final double LOWER_FORWARD_OFFSET = -2.1;
   private static final double SLASH_ANGLE_RADIANS = Math.toRadians(50.0);
   private static final double MAX_HALF_WIDTH = 0.1;
   private static final double SECONDARY_HALF_WIDTH = 0.016;
   private static final double EDGE_HALF_WIDTH = 0.0015;
   private static final double EDGE_INNER_FEATHER_WIDTH = 0.004;
   private static final double EDGE_MIDDLE_FEATHER_WIDTH = 0.009;
   private static final double EDGE_OUTER_FEATHER_WIDTH = 0.032;
   private static final double SECONDARY_PLANE_ANGLE = Math.toRadians(38.0);
   private static final double HORIZONTAL_FORWARD_DISTANCE = 6.0;
   private static final double HORIZONTAL_HEIGHT_OFFSET = 1.1;
   private static final float HORIZONTAL_REVEAL_TICKS = 2.0F;
   private static final float HORIZONTAL_REVEAL_FEATHER = 0.08F;
   private static final int SEGMENTS = 28;
   private static final int LIFETIME = 16;
   private static final ParticleRenderType CORE_RENDER_TYPE = new ParticleRenderType() {
      public void m_6505_(BufferBuilder builder, TextureManager textureManager) {
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.setShader(GameRenderer::m_172811_);
         builder.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85815_);
      }

      public void m_6294_(Tesselator tesselator) {
         tesselator.m_85914_();
         YamatoSpaceRiftParticle.restoreRenderState();
      }

      @Override
      public String toString() {
         return "invincible_dmc:yamato_space_rift_core";
      }
   };
   private static final ParticleRenderType EDGE_RENDER_TYPE = new ParticleRenderType() {
      public void m_6505_(BufferBuilder builder, TextureManager textureManager) {
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.setShader(GameRenderer::m_172811_);
         builder.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85815_);
      }

      public void m_6294_(Tesselator tesselator) {
         tesselator.m_85914_();
         YamatoSpaceRiftParticle.restoreRenderState();
      }

      @Override
      public String toString() {
         return "invincible_dmc:yamato_space_rift_edge";
      }
   };
   private final Vec3 tangent;
   private final Vec3 forwardAxis;
   private final Vec3 widthAxis;
   private final Vec3 secondaryWidthAxis;
   private final YamatoSpaceRiftParticle.Layer layer;
   private final double halfLength;
   private final double startForwardOffset;
   private final boolean directionalReveal;

   private static void restoreRenderState() {
      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
   }

   private YamatoSpaceRiftParticle(
      ClientLevel level,
      Vec3 center,
      Vec3 tangent,
      Vec3 planeNormal,
      YamatoSpaceRiftParticle.Layer layer,
      double halfLength,
      double startForwardOffset,
      boolean directionalReveal
   ) {
      super(level, center.f_82479_, center.f_82480_, center.f_82481_);
      this.tangent = tangent.m_82541_();
      this.forwardAxis = planeNormal.m_82541_();
      this.widthAxis = this.forwardAxis.m_82537_(this.tangent).m_82541_();
      this.secondaryWidthAxis = rotateAroundAxis(this.widthAxis, this.tangent, SECONDARY_PLANE_ANGLE).m_82541_();
      this.layer = layer;
      this.halfLength = halfLength;
      this.startForwardOffset = startForwardOffset;
      this.directionalReveal = directionalReveal;
      this.f_107225_ = 16;
      this.f_107219_ = false;
   }

   public static void spawn(LivingEntityPatch<?> patch) {
      if (patch != null && patch.getOriginal() != null && ((LivingEntity)patch.getOriginal()).m_9236_() instanceof ClientLevel level) {
         LivingEntity var8 = (LivingEntity)patch.getOriginal();
         Vec3 forward = MathUtils.getVectorForRotation(0.0F, patch.getYRot()).m_82541_();
         Vec3 left = new Vec3(forward.f_82481_, 0.0, -forward.f_82479_).m_82541_();
         Vec3 right = left.m_82490_(-1.0);
         Vec3 tangent = new Vec3(0.0, Math.cos(SLASH_ANGLE_RADIANS), 0.0).m_82549_(left.m_82490_(Math.sin(SLASH_ANGLE_RADIANS))).m_82541_();
         Vec3 center = var8.m_20182_()
            .m_82520_(0.0, (double)var8.m_20206_() * 0.62 + 0.48, 0.0)
            .m_82549_(forward.m_82490_(9.0))
            .m_82549_(right.m_82490_(-0.15));
         spawnRift(level, center, tangent, forward, 9.75, -2.1, false);
      }
   }

   public static void spawnHorizontal(LivingEntityPatch<?> patch) {
      if (patch != null && patch.getOriginal() != null && ((LivingEntity)patch.getOriginal()).m_9236_() instanceof ClientLevel level) {
         LivingEntity var6 = (LivingEntity)patch.getOriginal();
         Vec3 forward = MathUtils.getVectorForRotation(0.0F, patch.getYRot()).m_82541_();
         Vec3 left = new Vec3(forward.f_82481_, 0.0, -forward.f_82479_).m_82541_();
         Vec3 center = var6.m_20182_().m_82520_(0.0, 1.1, 0.0).m_82549_(forward.m_82490_(6.0));
         spawnRift(level, center, left, forward, 9.75, 0.0, true);
      }
   }

   private static void spawnRift(
      ClientLevel level, Vec3 center, Vec3 tangent, Vec3 forward, double halfLength, double startForwardOffset, boolean directionalReveal
   ) {
      Minecraft minecraft = Minecraft.m_91087_();
      minecraft.f_91061_
         .m_107344_(
            new YamatoSpaceRiftParticle(level, center, tangent, forward, YamatoSpaceRiftParticle.Layer.CORE, halfLength, startForwardOffset, directionalReveal)
         );
      minecraft.f_91061_
         .m_107344_(
            new YamatoSpaceRiftParticle(level, center, tangent, forward, YamatoSpaceRiftParticle.Layer.EDGE, halfLength, startForwardOffset, directionalReveal)
         );
      Vec3 worldStart = center.m_82549_(tangent.m_82490_(-halfLength)).m_82549_(forward.m_82490_(startForwardOffset));
      Vec3 worldEnd = center.m_82549_(tangent.m_82490_(halfLength));
      LocalScreenEffectGate.pushNearby(level, center, 48.0, new RiftAttractionEffect(center, worldStart, worldEnd));
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      }
   }

   public void m_5744_(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTick) {
      float elapsed = (float)this.f_107224_ + partialTick;
      float open = opening(elapsed);
      float alpha = opacity(elapsed);
      if (!(open <= 0.001F) && !(alpha <= 0.001F)) {
         Vec3 cameraPosition = camera.m_90583_();
         Vec3 center = new Vec3(
               Mth.m_14139_((double)partialTick, this.f_107209_, this.f_107212_),
               Mth.m_14139_((double)partialTick, this.f_107210_, this.f_107213_),
               Mth.m_14139_((double)partialTick, this.f_107211_, this.f_107214_)
            )
            .m_82546_(cameraPosition);
         if (this.layer == YamatoSpaceRiftParticle.Layer.CORE) {
            this.renderSpaceCut(buffer, center, this.widthAxis, 0.1 * (double)open, alpha, elapsed);
            this.renderSpaceCut(buffer, center, this.secondaryWidthAxis, 0.016 * (double)open, alpha * 0.72F, elapsed);
         } else {
            this.renderEdges(buffer, center, this.widthAxis, 0.1 * (double)open, alpha, elapsed);
            this.renderEdges(buffer, center, this.secondaryWidthAxis, 0.016 * (double)open, alpha * 0.58F, elapsed);
            this.renderInteriorFilaments(buffer, center, this.widthAxis, 0.1 * (double)open, alpha, elapsed);
         }
      }
   }

   private void renderSpaceCut(VertexConsumer buffer, Vec3 center, Vec3 bladeWidthAxis, double halfWidth, float alpha, float elapsed) {
      for (int segment = 0; segment < 28; segment++) {
         double progress0 = (double)segment / 28.0;
         double progress1 = (double)(segment + 1) / 28.0;
         float reveal0 = this.lengthReveal(progress0, elapsed);
         float reveal1 = this.lengthReveal(progress1, elapsed);
         float segmentAlpha = alpha * Math.max(reveal0, reveal1);
         if (!(segmentAlpha <= 0.001F)) {
            int outerAlpha = Mth.m_14045_((int)(205.0F * segmentAlpha), 0, 255);
            int innerAlpha = Mth.m_14045_((int)(232.0F * segmentAlpha), 0, 255);
            Vec3 center0 = this.sampleCenter(center, progress0);
            Vec3 center1 = this.sampleCenter(center, progress1);
            double width0 = halfWidth * widthEnvelope(progress0) * (double)reveal0;
            double width1 = halfWidth * widthEnvelope(progress1) * (double)reveal1;
            Vec3 positive0 = center0.m_82549_(bladeWidthAxis.m_82490_(width0));
            Vec3 negative0 = center0.m_82546_(bladeWidthAxis.m_82490_(width0));
            Vec3 positive1 = center1.m_82549_(bladeWidthAxis.m_82490_(width1));
            Vec3 negative1 = center1.m_82546_(bladeWidthAxis.m_82490_(width1));
            quadGradient(buffer, positive0, positive1, center1, center0, 64, 138, 232, outerAlpha, innerAlpha);
            quadGradient(buffer, negative0, negative1, center1, center0, 18, 58, 168, outerAlpha, innerAlpha);
         }
      }
   }

   private void renderEdges(VertexConsumer buffer, Vec3 center, Vec3 bladeWidthAxis, double halfWidth, float alpha, float elapsed) {
      for (int segment = 0; segment < 28; segment++) {
         double progress0 = (double)segment / 28.0;
         double progress1 = (double)(segment + 1) / 28.0;
         float reveal0 = this.lengthReveal(progress0, elapsed);
         float reveal1 = this.lengthReveal(progress1, elapsed);
         float segmentAlpha = alpha * Math.max(reveal0, reveal1);
         if (!(segmentAlpha <= 0.001F)) {
            int innerFeatherAlpha = Mth.m_14045_((int)(190.0F * segmentAlpha), 0, 255);
            int middleFeatherAlpha = Mth.m_14045_((int)(92.0F * segmentAlpha), 0, 255);
            int outerFeatherAlpha = Mth.m_14045_((int)(28.0F * segmentAlpha), 0, 255);
            int coreAlpha = Mth.m_14045_((int)(255.0F * segmentAlpha), 0, 255);
            Vec3 center0 = this.sampleCenter(center, progress0);
            Vec3 center1 = this.sampleCenter(center, progress1);
            double width0 = halfWidth * widthEnvelope(progress0) * (double)reveal0;
            double width1 = halfWidth * widthEnvelope(progress1) * (double)reveal1;
            Vec3 positiveStart = center0.m_82549_(bladeWidthAxis.m_82490_(width0));
            Vec3 positiveEnd = center1.m_82549_(bladeWidthAxis.m_82490_(width1));
            Vec3 negativeStart = center0.m_82546_(bladeWidthAxis.m_82490_(width0));
            Vec3 negativeEnd = center1.m_82546_(bladeWidthAxis.m_82490_(width1));
            renderFeatheredEdge(buffer, positiveStart, positiveEnd, bladeWidthAxis, innerFeatherAlpha, middleFeatherAlpha, outerFeatherAlpha);
            renderFeatheredEdge(buffer, negativeStart, negativeEnd, bladeWidthAxis.m_82490_(-1.0), innerFeatherAlpha, middleFeatherAlpha, outerFeatherAlpha);
            edgeQuad(buffer, positiveStart, positiveEnd, bladeWidthAxis, 0.0015, 126, 205, 255, coreAlpha);
            edgeQuad(buffer, negativeStart, negativeEnd, bladeWidthAxis, 0.0015, 126, 205, 255, coreAlpha);
         }
      }
   }

   private void renderInteriorFilaments(VertexConsumer buffer, Vec3 center, Vec3 bladeWidthAxis, double halfWidth, float alpha, float elapsed) {
      int centerAlpha = Mth.m_14045_((int)(235.0F * alpha), 0, 255);
      int filamentAlpha = Mth.m_14045_((int)(155.0F * alpha), 0, 255);

      for (int segment = 0; segment < 28; segment++) {
         double progress0 = (double)segment / 28.0;
         double progress1 = (double)(segment + 1) / 28.0;
         float reveal0 = this.lengthReveal(progress0, elapsed);
         float reveal1 = this.lengthReveal(progress1, elapsed);
         float reveal = Math.max(reveal0, reveal1);
         if (!(reveal <= 0.001F)) {
            Vec3 center0 = this.sampleCenter(center, progress0);
            Vec3 center1 = this.sampleCenter(center, progress1);
            double envelope0 = widthEnvelope(progress0) * (double)reveal0;
            double envelope1 = widthEnvelope(progress1) * (double)reveal1;
            int revealedCenterAlpha = Mth.m_14045_((int)((float)centerAlpha * reveal), 0, 255);
            int revealedFilamentAlpha = Mth.m_14045_((int)((float)filamentAlpha * reveal), 0, 255);
            edgeQuad(buffer, center0, center1, bladeWidthAxis, 0.00123, 148, 216, 255, revealedCenterAlpha);
            double wave0 = Math.sin(progress0 * Math.PI * 6.0 - (double)elapsed * 0.82);
            double wave1 = Math.sin(progress1 * Math.PI * 6.0 - (double)elapsed * 0.82);
            Vec3 filament0 = center0.m_82549_(bladeWidthAxis.m_82490_(halfWidth * envelope0 * wave0 * 0.48));
            Vec3 filament1 = center1.m_82549_(bladeWidthAxis.m_82490_(halfWidth * envelope1 * wave1 * 0.48));
            edgeQuad(buffer, filament0, filament1, bladeWidthAxis, 7.199999999999999E-4, 48, 142, 255, revealedFilamentAlpha);
         }
      }
   }

   private static void renderFeatheredEdge(VertexConsumer buffer, Vec3 start, Vec3 end, Vec3 outwardAxis, int innerAlpha, int middleAlpha, int outerAlpha) {
      Vec3 innerOffset = outwardAxis.m_82490_(0.004);
      Vec3 middleOffset = outwardAxis.m_82490_(0.009);
      Vec3 outerOffset = outwardAxis.m_82490_(0.032);
      featherBand(buffer, start, end, start.m_82549_(innerOffset), end.m_82549_(innerOffset), 104, 188, 255, innerAlpha, middleAlpha);
      featherBand(
         buffer,
         start.m_82549_(innerOffset),
         end.m_82549_(innerOffset),
         start.m_82549_(middleOffset),
         end.m_82549_(middleOffset),
         42,
         105,
         238,
         middleAlpha,
         outerAlpha
      );
      featherBand(
         buffer, start.m_82549_(middleOffset), end.m_82549_(middleOffset), start.m_82549_(outerOffset), end.m_82549_(outerOffset), 14, 42, 170, outerAlpha, 0
      );
   }

   private Vec3 sampleCenter(Vec3 center, double progress) {
      double alongSlash = Mth.m_14139_(progress, -this.halfLength, this.halfLength);
      double lowerForward = this.startForwardOffset * (1.0 - Mth.m_14008_(progress, 0.0, 1.0));
      return center.m_82549_(this.tangent.m_82490_(alongSlash)).m_82549_(this.forwardAxis.m_82490_(lowerForward));
   }

   private float lengthReveal(double progress, float elapsed) {
      if (!this.directionalReveal) {
         return 1.0F;
      } else {
         float head = Mth.m_14036_(elapsed / 2.0F, 0.0F, 1.0F);
         float distanceAhead = ((float)progress - head) / 0.08F;
         return 1.0F - smootherStep(Mth.m_14036_(distanceAhead, 0.0F, 1.0F));
      }
   }

   private static void edgeQuad(VertexConsumer buffer, Vec3 start, Vec3 end, Vec3 widthAxis, double width, int red, int green, int blue, int alpha) {
      Vec3 offset = widthAxis.m_82490_(width);
      quadColor(buffer, start.m_82549_(offset), start.m_82546_(offset), end.m_82546_(offset), end.m_82549_(offset), red, green, blue, alpha);
   }

   private static void featherBand(
      VertexConsumer buffer, Vec3 innerStart, Vec3 innerEnd, Vec3 outerStart, Vec3 outerEnd, int red, int green, int blue, int innerAlpha, int outerAlpha
   ) {
      quadGradient(buffer, innerStart, innerEnd, outerEnd, outerStart, red, green, blue, innerAlpha, outerAlpha);
   }

   private static float opening(float elapsed) {
      if (elapsed < 2.0F) {
         return smootherStep(elapsed / 2.0F);
      } else if (elapsed < 9.0F) {
         return 1.0F;
      } else {
         float closeProgress = Mth.m_14036_((elapsed - 9.0F) / 7.0F, 0.0F, 1.0F);
         return 0.5F + 0.5F * Mth.m_14089_((float) Math.PI * closeProgress);
      }
   }

   private static float opacity(float elapsed) {
      return (float)Math.pow((double)opening(elapsed), 0.72);
   }

   private static double widthEnvelope(double progress) {
      double clamped = Mth.m_14008_(progress, 0.0, 1.0);
      double diamond = 1.0 - Math.abs(clamped * 2.0 - 1.0);
      return Math.pow(diamond, 1.45);
   }

   private static float smootherStep(float value) {
      float clamped = Mth.m_14036_(value, 0.0F, 1.0F);
      return clamped * clamped * clamped * (clamped * (clamped * 6.0F - 15.0F) + 10.0F);
   }

   private static Vec3 rotateAroundAxis(Vec3 vector, Vec3 axis, double angle) {
      Vec3 normalizedAxis = axis.m_82541_();
      double cosine = Math.cos(angle);
      double sine = Math.sin(angle);
      return vector.m_82490_(cosine)
         .m_82549_(normalizedAxis.m_82537_(vector).m_82490_(sine))
         .m_82549_(normalizedAxis.m_82490_(normalizedAxis.m_82526_(vector) * (1.0 - cosine)));
   }

   private static void quadColor(VertexConsumer buffer, Vec3 a, Vec3 b, Vec3 c, Vec3 d, int red, int green, int blue, int alpha) {
      buffer.m_5483_(a.f_82479_, a.f_82480_, a.f_82481_).m_6122_(red, green, blue, alpha).m_5752_();
      buffer.m_5483_(b.f_82479_, b.f_82480_, b.f_82481_).m_6122_(red, green, blue, alpha).m_5752_();
      buffer.m_5483_(c.f_82479_, c.f_82480_, c.f_82481_).m_6122_(red, green, blue, alpha).m_5752_();
      buffer.m_5483_(d.f_82479_, d.f_82480_, d.f_82481_).m_6122_(red, green, blue, alpha).m_5752_();
   }

   private static void quadGradient(VertexConsumer buffer, Vec3 a, Vec3 b, Vec3 c, Vec3 d, int red, int green, int blue, int innerAlpha, int outerAlpha) {
      buffer.m_5483_(a.f_82479_, a.f_82480_, a.f_82481_).m_6122_(red, green, blue, innerAlpha).m_5752_();
      buffer.m_5483_(b.f_82479_, b.f_82480_, b.f_82481_).m_6122_(red, green, blue, innerAlpha).m_5752_();
      buffer.m_5483_(c.f_82479_, c.f_82480_, c.f_82481_).m_6122_(red, green, blue, outerAlpha).m_5752_();
      buffer.m_5483_(d.f_82479_, d.f_82480_, d.f_82481_).m_6122_(red, green, blue, outerAlpha).m_5752_();
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return this.layer == YamatoSpaceRiftParticle.Layer.CORE ? CORE_RENDER_TYPE : EDGE_RENDER_TYPE;
   }

   public boolean shouldCull() {
      return false;
   }

   private static enum Layer {
      CORE,
      EDGE;
   }
}
