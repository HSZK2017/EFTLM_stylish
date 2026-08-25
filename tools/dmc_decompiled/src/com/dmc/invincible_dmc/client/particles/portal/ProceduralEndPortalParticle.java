package com.dmc.invincible_dmc.client.particles.portal;

import com.dmc.invincible_dmc.compat.oculus.OculusCompat;
import com.dmc.invincible_dmc.entity.portal.PortalEntity;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT}
)
public final class ProceduralEndPortalParticle extends Particle {
   public static final int INFINITE_IDLE_DURATION = -1;
   private static final int OPEN_TICKS = 13;
   private static final int IDLE_LOOP_TICKS = 16;
   private static final int CLOSE_TICKS = 13;
   private static final int FILL_SEGMENTS = 36;
   private static final int OUTLINE_SEGMENTS = 96;
   private static final double SHAPE_POWER = 3.5;
   private static final double BASE_HALF_WIDTH = 5.508;
   private static final double BASE_HALF_HEIGHT = 3.5909999999999997;
   private static final ResourceLocation END_PORTAL_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/end_portal.png");
   private static final List<ProceduralEndPortalParticle> ACTIVE_PORTALS = new ArrayList<>();
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
         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }

      @Override
      public String toString() {
         return "invincible_dmc:procedural_end_portal_edge";
      }
   };
   private final int idleDuration;
   @Nullable
   private final Entity followTarget;
   private final double followYOffset;
   private ProceduralEndPortalParticle.PortalState state = ProceduralEndPortalParticle.PortalState.OPENING;
   private int stateTicks;
   private int idleTicks;
   private float yaw;
   private float previousYaw;
   private float scale;
   private final ProceduralEndPortalParticle.OutlineGeometry outlineGeometry = new ProceduralEndPortalParticle.OutlineGeometry(97);

   private ProceduralEndPortalParticle(
      ClientLevel level, Vec3 center, float yaw, float scale, int idleDuration, @Nullable Entity followTarget, double followYOffset
   ) {
      super(level, center.f_82479_, center.f_82480_, center.f_82481_);
      this.yaw = yaw;
      this.previousYaw = yaw;
      this.scale = Math.max(0.05F, scale);
      this.idleDuration = idleDuration;
      this.followTarget = followTarget;
      this.followYOffset = followYOffset;
      this.f_107219_ = false;
      this.f_107225_ = Integer.MAX_VALUE;
      ACTIVE_PORTALS.add(this);
   }

   public static ProceduralEndPortalParticle spawn(ClientLevel level, Vec3 center, float yaw, float scale) {
      return spawn(level, center, yaw, scale, -1);
   }

   public static ProceduralEndPortalParticle spawn(ClientLevel level, Vec3 center, float yaw, float scale, int idleDuration) {
      ProceduralEndPortalParticle particle = new ProceduralEndPortalParticle(level, center, yaw, scale, idleDuration, null, 0.0);
      Minecraft.m_91087_().f_91061_.m_107344_(particle);
      return particle;
   }

   public static ProceduralEndPortalParticle spawnFollowing(Entity entity, double yOffset, float scale) {
      return spawnFollowing(entity, yOffset, scale, -1);
   }

   public static ProceduralEndPortalParticle spawnFollowing(Entity entity, double yOffset, float scale, int idleDuration) {
      if (entity.m_9236_() instanceof ClientLevel level) {
         ProceduralEndPortalParticle var7 = new ProceduralEndPortalParticle(
            level, entity.m_20182_().m_82520_(0.0, yOffset, 0.0), entity.m_146908_(), scale, idleDuration, entity, yOffset
         );
         Minecraft.m_91087_().f_91061_.m_107344_(var7);
         return var7;
      } else {
         throw new IllegalArgumentException("Procedural end portal particles can only follow client entities");
      }
   }

   public void close() {
      if (this.state != ProceduralEndPortalParticle.PortalState.CLOSING) {
         this.state = ProceduralEndPortalParticle.PortalState.CLOSING;
         this.stateTicks = 0;
      }
   }

   public void setPortalPosition(Vec3 position) {
      if (this.followTarget == null) {
         this.m_107264_(position.f_82479_, position.f_82480_, position.f_82481_);
      }
   }

   public void setPortalYaw(float yaw) {
      if (this.followTarget == null) {
         this.previousYaw = this.yaw;
         this.yaw = yaw;
      }
   }

   public void setPortalScale(float scale) {
      this.scale = Math.max(0.05F, scale);
   }

   public boolean isClosing() {
      return this.state == ProceduralEndPortalParticle.PortalState.CLOSING;
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      this.previousYaw = this.yaw;
      this.f_107224_++;
      label36:
      if (this.followTarget != null) {
         if (this.followTarget instanceof PortalEntity portalEntity && portalEntity.isClosing()) {
            this.close();
            break label36;
         }

         if (this.followTarget.m_6084_() && !this.followTarget.m_213877_()) {
            this.m_107264_(this.followTarget.m_20185_(), this.followTarget.m_20186_() + this.followYOffset, this.followTarget.m_20189_());
            this.yaw = this.followTarget.m_146908_();
         } else {
            this.close();
         }
      }

      this.stateTicks++;
      switch (this.state) {
         case OPENING:
            if (this.stateTicks >= 13) {
               this.state = ProceduralEndPortalParticle.PortalState.IDLE;
               this.stateTicks = 0;
            }
            break;
         case IDLE:
            this.idleTicks++;
            if (this.idleDuration >= 0 && this.idleTicks >= this.idleDuration) {
               this.close();
            }
            break;
         case CLOSING:
            if (this.stateTicks >= 13) {
               this.m_107274_();
            }
      }
   }

   @SubscribeEvent
   public static void renderPortalLayer(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS && !ACTIVE_PORTALS.isEmpty()) {
         Minecraft minecraft = Minecraft.m_91087_();
         ACTIVE_PORTALS.removeIf(portal -> !portal.m_107276_() || portal.f_107208_ != minecraft.f_91073_);
         if (!ACTIVE_PORTALS.isEmpty()) {
            renderPortalInteriors(event, minecraft);
            renderPortalEdges(event, minecraft);
         }
      }
   }

   private static void renderPortalInteriors(RenderLevelStageEvent event, Minecraft minecraft) {
      BufferSource buffers = minecraft.m_91269_().m_110104_();
      boolean shaderPackActive = OculusCompat.isShaderActive();
      RenderType portalRenderType = shaderPackActive ? OculusCompat.wrapEndPortalRenderType(RenderType.m_110446_(END_PORTAL_TEXTURE)) : RenderType.m_173239_();
      int previousBlockEntityId = shaderPackActive ? OculusCompat.beginEndPortalBlockEntityContext() : 0;
      PoseStack poseStack = event.getPoseStack();
      Vec3 cameraPosition = event.getCamera().m_90583_();
      poseStack.m_85836_();

      try {
         poseStack.m_85837_(-cameraPosition.f_82479_, -cameraPosition.f_82480_, -cameraPosition.f_82481_);
         Matrix4f pose = poseStack.m_85850_().m_252922_();
         Matrix3f normal = poseStack.m_85850_().m_252943_();
         VertexConsumer buffer = buffers.m_6299_(portalRenderType);

         for (ProceduralEndPortalParticle portal : ACTIVE_PORTALS) {
            portal.renderInterior(buffer, pose, normal, cameraPosition, event.getPartialTick(), shaderPackActive);
         }

         buffers.m_109912_(portalRenderType);
      } finally {
         poseStack.m_85849_();
         if (shaderPackActive) {
            OculusCompat.restoreBlockEntityContext(previousBlockEntityId);
         }
      }
   }

   private static void renderPortalEdges(RenderLevelStageEvent event, Minecraft minecraft) {
      Tesselator tesselator = Tesselator.m_85913_();
      BufferBuilder buffer = tesselator.m_85915_();
      PoseStack poseStack = event.getPoseStack();
      Vec3 cameraPosition = event.getCamera().m_90583_();
      poseStack.m_85836_();

      try {
         poseStack.m_85837_(-cameraPosition.f_82479_, -cameraPosition.f_82480_, -cameraPosition.f_82481_);
         Matrix4f pose = poseStack.m_85850_().m_252922_();
         EDGE_RENDER_TYPE.m_6505_(buffer, minecraft.m_91097_());

         try {
            for (ProceduralEndPortalParticle portal : ACTIVE_PORTALS) {
               portal.renderEdge(buffer, pose, event.getPartialTick());
            }
         } finally {
            EDGE_RENDER_TYPE.m_6294_(tesselator);
         }
      } finally {
         poseStack.m_85849_();
      }
   }

   private void renderInterior(VertexConsumer buffer, Matrix4f pose, Matrix3f normalMatrix, Vec3 cameraPosition, float partialTicks, boolean shaderPackActive) {
      ProceduralEndPortalParticle.PortalTransform transform = this.getTransform(partialTicks);
      ProceduralEndPortalParticle.PortalDimensions dimensions = this.getDimensions(partialTicks);
      if (!(dimensions.visibility <= 0.001F)) {
         boolean renderFrontFace = cameraPosition.m_82546_(transform.center).m_82526_(transform.normal) >= 0.0;

         for (int segment = 0; segment < 36; segment++) {
            double normalizedY0 = -1.0 + 2.0 * (double)segment / 36.0;
            double normalizedY1 = -1.0 + 2.0 * (double)(segment + 1) / 36.0;
            double width0 = dimensions.halfWidth * astroidHalfWidth(normalizedY0);
            double width1 = dimensions.halfWidth * astroidHalfWidth(normalizedY1);
            double y0 = normalizedY0 * dimensions.halfHeight;
            double y1 = normalizedY1 * dimensions.halfHeight;
            Vec3 a = transform.local(-width0, y0);
            Vec3 b = transform.local(width0, y0);
            Vec3 c = transform.local(width1, y1);
            Vec3 d = transform.local(-width1, y1);
            float u0 = (float)segment / 36.0F;
            float u1 = (float)(segment + 1) / 36.0F;
            if (shaderPackActive) {
               if (renderFrontFace) {
                  quadPortalCompat(buffer, pose, normalMatrix, a, b, c, d, transform.normal, u0, u1);
               } else {
                  quadPortalCompat(buffer, pose, normalMatrix, b, a, d, c, transform.normal.m_82490_(-1.0), u0, u1);
               }
            } else if (renderFrontFace) {
               quadPortalVanilla(buffer, pose, a, b, c, d);
            } else {
               quadPortalVanilla(buffer, pose, b, a, d, c);
            }
         }
      }
   }

   private void renderEdge(VertexConsumer buffer, Matrix4f pose, float partialTicks) {
      ProceduralEndPortalParticle.PortalTransform transform = this.getTransform(partialTicks);
      ProceduralEndPortalParticle.PortalDimensions dimensions = this.getDimensions(partialTicks);
      if (!(dimensions.visibility <= 0.001F)) {
         double unit = (double)this.scale;
         int alpha = Mth.m_14045_((int)(255.0 * Math.pow((double)dimensions.visibility, 0.65)), 0, 255);
         double energyPhase = (double)(((float)this.f_107224_ + partialTicks) / 16.0F) * Math.PI * 2.0;
         updateOutlineGeometry(this.outlineGeometry, transform, dimensions.halfWidth, dimensions.halfHeight);
         ProceduralEndPortalParticle.OutlineGeometry geometry = this.outlineGeometry;
         renderOutlineBand(buffer, pose, geometry, -0.012 * unit, 0.022 * unit, 188, 205, 255, alpha);
         renderOutlineBand(buffer, pose, geometry, 0.022 * unit, 0.07 * unit, 70, 105, 255, Mth.m_14045_((int)(145.0F * dimensions.visibility), 0, 255));
         renderOutlineBand(buffer, pose, geometry, 0.07 * unit, 0.145 * unit, 145, 55, 255, Mth.m_14045_((int)(42.0F * dimensions.visibility), 0, 255));
         renderFlowingOutline(buffer, pose, geometry, 0.03 * unit, 0.052 * unit, energyPhase, dimensions.visibility);
         if (this.state == ProceduralEndPortalParticle.PortalState.OPENING) {
            float crossAlpha = (float)Math.pow((double)(1.0F - dimensions.visibility), 1.45);
            renderOpeningCross(buffer, pose, transform, dimensions.halfWidth, dimensions.halfHeight, crossAlpha, unit);
         }
      }
   }

   private ProceduralEndPortalParticle.PortalTransform getTransform(float partialTicks) {
      Vec3 center = new Vec3(
         Mth.m_14139_((double)partialTicks, this.f_107209_, this.f_107212_),
         Mth.m_14139_((double)partialTicks, this.f_107210_, this.f_107213_),
         Mth.m_14139_((double)partialTicks, this.f_107211_, this.f_107214_)
      );
      float interpolatedYaw = Mth.m_14189_(partialTicks, this.previousYaw, this.yaw);
      double radians = Math.toRadians((double)interpolatedYaw);
      Vec3 normal = new Vec3(-Math.sin(radians), 0.0, Math.cos(radians)).m_82541_();
      Vec3 right = new Vec3(normal.f_82481_, 0.0, -normal.f_82479_).m_82541_();
      return new ProceduralEndPortalParticle.PortalTransform(center, right, new Vec3(0.0, 1.0, 0.0), normal);
   }

   private ProceduralEndPortalParticle.PortalDimensions getDimensions(float partialTicks) {
      float visibility = switch (this.state) {
         case OPENING -> smootherStep(((float)this.stateTicks + partialTicks) / 13.0F);
         case IDLE -> 1.0F;
         case CLOSING -> 1.0F - smootherStep(((float)this.stateTicks + partialTicks) / 13.0F);
      };
      double halfWidth = 5.508 * (double)this.scale * (double)visibility;
      double halfHeight = 3.5909999999999997 * (double)this.scale * (0.28 + (double)visibility * 0.72);
      return new ProceduralEndPortalParticle.PortalDimensions(halfWidth, halfHeight, visibility);
   }

   private static double astroidHalfWidth(double normalizedY) {
      double y = Mth.m_14008_(Math.abs(normalizedY), 0.0, 1.0);
      double implicitPower = 0.5714285714285714;
      return Math.pow(Math.max(0.0, 1.0 - Math.pow(y, implicitPower)), 1.0 / implicitPower);
   }

   private static void renderOutlineBand(
      VertexConsumer buffer,
      Matrix4f pose,
      ProceduralEndPortalParticle.OutlineGeometry geometry,
      double innerExpansion,
      double outerExpansion,
      int red,
      int green,
      int blue,
      int alpha
   ) {
      for (int segment = 0; segment < 96; segment++) {
         outlineVertex(buffer, pose, geometry, segment, innerExpansion, red, green, blue, alpha);
         outlineVertex(buffer, pose, geometry, segment + 1, innerExpansion, red, green, blue, alpha);
         outlineVertex(buffer, pose, geometry, segment + 1, outerExpansion, red, green, blue, alpha);
         outlineVertex(buffer, pose, geometry, segment, outerExpansion, red, green, blue, alpha);
      }
   }

   private static void renderFlowingOutline(
      VertexConsumer buffer,
      Matrix4f pose,
      ProceduralEndPortalParticle.OutlineGeometry geometry,
      double innerExpansion,
      double outerExpansion,
      double phase,
      float visibility
   ) {
      for (int segment = 0; segment < 96; segment++) {
         double angle0 = (Math.PI * 2) * (double)segment / 96.0;
         double angle1 = (Math.PI * 2) * (double)(segment + 1) / 96.0;
         double wave0 = flowingWave(angle0, phase);
         double wave1 = flowingWave(angle1, phase);
         int red0 = flowRed(wave0);
         int green0 = flowGreen(wave0);
         int red1 = flowRed(wave1);
         int green1 = flowGreen(wave1);
         int alpha0 = flowInnerAlpha(wave0, visibility);
         int alpha1 = flowInnerAlpha(wave1, visibility);
         outlineVertex(buffer, pose, geometry, segment, innerExpansion, red0, green0, 255, alpha0);
         outlineVertex(buffer, pose, geometry, segment + 1, innerExpansion, red1, green1, 255, alpha1);
         outlineVertex(buffer, pose, geometry, segment + 1, outerExpansion, red1, green1, 255, alpha1);
         outlineVertex(buffer, pose, geometry, segment, outerExpansion, red0, green0, 255, alpha0);
      }
   }

   private static double flowingWave(double angle, double phase) {
      return Math.pow(0.5 + 0.5 * Math.sin(angle * 4.0 - phase * 2.25), 5.0);
   }

   private static int flowRed(double wave) {
      return Mth.m_14045_((int)(85.0 + wave * 115.0), 0, 255);
   }

   private static int flowGreen(double wave) {
      return Mth.m_14045_((int)(70.0 + wave * 95.0), 0, 255);
   }

   private static int flowInnerAlpha(double wave, float visibility) {
      return Mth.m_14045_((int)((55.0 + wave * 200.0) * (double)visibility), 0, 255);
   }

   private static void renderOpeningCross(
      VertexConsumer buffer,
      Matrix4f pose,
      ProceduralEndPortalParticle.PortalTransform transform,
      double halfWidth,
      double halfHeight,
      float alpha,
      double scale
   ) {
      int coreAlpha = Mth.m_14045_((int)(235.0F * alpha), 0, 255);
      int glowAlpha = Mth.m_14045_((int)(85.0F * alpha), 0, 255);
      if (coreAlpha > 0) {
         renderLineBand(buffer, pose, transform, -halfWidth, 0.0, halfWidth, 0.0, 0.018 * scale, 185, 205, 255, coreAlpha);
         renderLineBand(buffer, pose, transform, 0.0, -halfHeight, 0.0, halfHeight, 0.018 * scale, 185, 205, 255, coreAlpha);
         renderLineBand(buffer, pose, transform, -halfWidth, 0.0, halfWidth, 0.0, 0.07 * scale, 120, 50, 255, glowAlpha);
         renderLineBand(buffer, pose, transform, 0.0, -halfHeight, 0.0, halfHeight, 0.07 * scale, 120, 50, 255, glowAlpha);
      }
   }

   private static void renderLineBand(
      VertexConsumer buffer,
      Matrix4f pose,
      ProceduralEndPortalParticle.PortalTransform transform,
      double x0,
      double y0,
      double x1,
      double y1,
      double halfThickness,
      int red,
      int green,
      int blue,
      int alpha
   ) {
      double dx = x1 - x0;
      double dy = y1 - y0;
      double length = Math.sqrt(dx * dx + dy * dy);
      if (!(length <= 1.0E-6)) {
         double perpendicularX = -dy / length * halfThickness;
         double perpendicularY = dx / length * halfThickness;
         quadColor(
            buffer,
            pose,
            transform.local(x0 + perpendicularX, y0 + perpendicularY),
            transform.local(x0 - perpendicularX, y0 - perpendicularY),
            transform.local(x1 - perpendicularX, y1 - perpendicularY),
            transform.local(x1 + perpendicularX, y1 + perpendicularY),
            red,
            green,
            blue,
            alpha
         );
      }
   }

   private static void updateOutlineGeometry(
      ProceduralEndPortalParticle.OutlineGeometry geometry, ProceduralEndPortalParticle.PortalTransform transform, double halfWidth, double halfHeight
   ) {
      int sampleCount = 97;
      double sampleDelta = 0.032724923474893676;

      for (int sample = 0; sample < sampleCount; sample++) {
         double angle = (Math.PI * 2) * (double)sample / 96.0;
         double pointX = astroidLocalX(angle, halfWidth);
         double pointY = astroidLocalY(angle, halfHeight);
         double tangentX = astroidLocalX(angle + sampleDelta, halfWidth) - astroidLocalX(angle - sampleDelta, halfWidth);
         double tangentY = astroidLocalY(angle + sampleDelta, halfHeight) - astroidLocalY(angle - sampleDelta, halfHeight);
         double tangentLength = Math.sqrt(tangentX * tangentX + tangentY * tangentY);
         double normalX = tangentLength <= 1.0E-8 ? 0.0 : tangentY / tangentLength;
         double normalY = tangentLength <= 1.0E-8 ? 0.0 : -tangentX / tangentLength;
         if (normalX * pointX + normalY * pointY < 0.0) {
            normalX = -normalX;
            normalY = -normalY;
         }

         geometry.x[sample] = transform.center.f_82479_ + transform.right.f_82479_ * pointX + transform.up.f_82479_ * pointY;
         geometry.y[sample] = transform.center.f_82480_ + transform.right.f_82480_ * pointX + transform.up.f_82480_ * pointY;
         geometry.z[sample] = transform.center.f_82481_ + transform.right.f_82481_ * pointX + transform.up.f_82481_ * pointY;
         geometry.normalX[sample] = transform.right.f_82479_ * normalX + transform.up.f_82479_ * normalY;
         geometry.normalY[sample] = transform.right.f_82480_ * normalX + transform.up.f_82480_ * normalY;
         geometry.normalZ[sample] = transform.right.f_82481_ * normalX + transform.up.f_82481_ * normalY;
      }
   }

   private static double astroidLocalX(double angle, double halfWidth) {
      double cosine = Math.cos(angle);
      return Math.copySign(Math.pow(Math.abs(cosine), 3.5), cosine) * Math.max(0.0, halfWidth);
   }

   private static double astroidLocalY(double angle, double halfHeight) {
      double sine = Math.sin(angle);
      return Math.copySign(Math.pow(Math.abs(sine), 3.5), sine) * Math.max(0.0, halfHeight);
   }

   private static void outlineVertex(
      VertexConsumer buffer,
      Matrix4f pose,
      ProceduralEndPortalParticle.OutlineGeometry geometry,
      int sample,
      double offset,
      int red,
      int green,
      int blue,
      int alpha
   ) {
      buffer.m_252986_(
            pose,
            (float)(geometry.x[sample] + geometry.normalX[sample] * offset),
            (float)(geometry.y[sample] + geometry.normalY[sample] * offset),
            (float)(geometry.z[sample] + geometry.normalZ[sample] * offset)
         )
         .m_6122_(red, green, blue, alpha)
         .m_5752_();
   }

   private static float smootherStep(float value) {
      float clamped = Mth.m_14036_(value, 0.0F, 1.0F);
      return clamped * clamped * clamped * (clamped * (clamped * 6.0F - 15.0F) + 10.0F);
   }

   private static void quadPortalVanilla(VertexConsumer buffer, Matrix4f pose, Vec3 a, Vec3 b, Vec3 c, Vec3 d) {
      buffer.m_252986_(pose, (float)a.f_82479_, (float)a.f_82480_, (float)a.f_82481_).m_5752_();
      buffer.m_252986_(pose, (float)b.f_82479_, (float)b.f_82480_, (float)b.f_82481_).m_5752_();
      buffer.m_252986_(pose, (float)c.f_82479_, (float)c.f_82480_, (float)c.f_82481_).m_5752_();
      buffer.m_252986_(pose, (float)d.f_82479_, (float)d.f_82480_, (float)d.f_82481_).m_5752_();
   }

   private static void quadPortalCompat(
      VertexConsumer buffer, Matrix4f pose, Matrix3f normalMatrix, Vec3 a, Vec3 b, Vec3 c, Vec3 d, Vec3 normal, float u0, float u1
   ) {
      portalCompatVertex(buffer, pose, normalMatrix, a, normal, u0, 0.0F);
      portalCompatVertex(buffer, pose, normalMatrix, b, normal, u0, 1.0F);
      portalCompatVertex(buffer, pose, normalMatrix, c, normal, u1, 1.0F);
      portalCompatVertex(buffer, pose, normalMatrix, d, normal, u1, 0.0F);
   }

   private static void portalCompatVertex(VertexConsumer buffer, Matrix4f pose, Matrix3f normalMatrix, Vec3 position, Vec3 normal, float u, float v) {
      buffer.m_252986_(pose, (float)position.f_82479_, (float)position.f_82480_, (float)position.f_82481_)
         .m_85950_(0.075F, 0.15F, 0.2F, 1.0F)
         .m_7421_(u, v)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_252939_(normalMatrix, (float)normal.f_82479_, (float)normal.f_82480_, (float)normal.f_82481_)
         .m_5752_();
   }

   private static void quadColor(VertexConsumer buffer, Matrix4f pose, Vec3 a, Vec3 b, Vec3 c, Vec3 d, int red, int green, int blue, int alpha) {
      buffer.m_252986_(pose, (float)a.f_82479_, (float)a.f_82480_, (float)a.f_82481_).m_6122_(red, green, blue, alpha).m_5752_();
      buffer.m_252986_(pose, (float)b.f_82479_, (float)b.f_82480_, (float)b.f_82481_).m_6122_(red, green, blue, alpha).m_5752_();
      buffer.m_252986_(pose, (float)c.f_82479_, (float)c.f_82480_, (float)c.f_82481_).m_6122_(red, green, blue, alpha).m_5752_();
      buffer.m_252986_(pose, (float)d.f_82479_, (float)d.f_82480_, (float)d.f_82481_).m_6122_(red, green, blue, alpha).m_5752_();
   }

   public void m_5744_(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107434_;
   }

   public boolean shouldCull() {
      return false;
   }

   private static final class OutlineGeometry {
      private final double[] x;
      private final double[] y;
      private final double[] z;
      private final double[] normalX;
      private final double[] normalY;
      private final double[] normalZ;

      private OutlineGeometry(int sampleCount) {
         this.x = new double[sampleCount];
         this.y = new double[sampleCount];
         this.z = new double[sampleCount];
         this.normalX = new double[sampleCount];
         this.normalY = new double[sampleCount];
         this.normalZ = new double[sampleCount];
      }
   }

   private static record PortalDimensions(double halfWidth, double halfHeight, float visibility) {
   }

   private static enum PortalState {
      OPENING,
      IDLE,
      CLOSING;
   }

   private static record PortalTransform(Vec3 center, Vec3 right, Vec3 up, Vec3 normal) {
      private Vec3 local(double x, double y) {
         return this.center.m_82549_(this.right.m_82490_(x)).m_82549_(this.up.m_82490_(y));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Provider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         if (ySpeed < 0.0) {
            Entity entity = level.m_6815_((int)xSpeed);
            if (entity != null) {
               return new ProceduralEndPortalParticle(level, new Vec3(x, y, z), entity.m_146908_(), (float)(-ySpeed), -1, entity, y - entity.m_20186_());
            }
         }

         float scale = ySpeed > 0.0 ? (float)ySpeed : 1.0F;
         int idleDuration = zSpeed > 0.0 ? Mth.m_14107_(zSpeed) : -1;
         return new ProceduralEndPortalParticle(level, new Vec3(x, y, z), (float)xSpeed, scale, idleDuration, null, 0.0);
      }
   }
}
