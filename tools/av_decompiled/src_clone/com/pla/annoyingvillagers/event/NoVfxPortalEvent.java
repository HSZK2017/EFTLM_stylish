package com.pla.annoyingvillagers.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.joml.Matrix4f;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   value = {Dist.CLIENT}
)
public final class NoVfxPortalEvent {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/portal.png");
   private static final RenderType PORTAL_TYPE = RenderType.m_110473_(TEXTURE);
   private static final int FULL_BRIGHT_LIGHT = LightTexture.m_109885_(15, 15);
   private static final float PORTAL_HALF_SIZE = 2.5F;
   private static final int GROW_TICKS = 20;
   private static final int SHRINK_TICKS = 20;
   private static final List<NoVfxPortalEvent.PortalInstance> ACTIVE = new ArrayList<>();

   private NoVfxPortalEvent() {
   }

   public static void spawn(Vec3 pos, int holdTicks) {
      Minecraft mc = Minecraft.m_91087_();
      if (mc.f_91073_ != null) {
         ACTIVE.add(new NoVfxPortalEvent.PortalInstance(pos, mc.f_91073_.m_46467_(), holdTicks));
      }
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent e) {
      if (e.phase == Phase.END) {
         Minecraft mc = Minecraft.m_91087_();
         if (mc.f_91073_ == null) {
            ACTIVE.clear();
         } else {
            long nowTick = mc.f_91073_.m_46467_();
            Iterator<NoVfxPortalEvent.PortalInstance> it = ACTIVE.iterator();

            while (it.hasNext()) {
               NoVfxPortalEvent.PortalInstance p = it.next();
               if (p.isExpired(nowTick)) {
                  it.remove();
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onRenderLevel(RenderLevelStageEvent e) {
      if (e.getStage() == Stage.AFTER_PARTICLES) {
         Minecraft mc = Minecraft.m_91087_();
         if (mc.f_91073_ != null && !ACTIVE.isEmpty()) {
            PoseStack poseStack = e.getPoseStack();
            Vec3 cam = e.getCamera().m_90583_();
            float partial = e.getPartialTick();
            poseStack.m_85836_();
            poseStack.m_85837_(-cam.f_82479_, -cam.f_82480_, -cam.f_82481_);
            BufferSource buffer = mc.m_91269_().m_110104_();
            long nowTick = mc.f_91073_.m_46467_();

            for (NoVfxPortalEvent.PortalInstance p : ACTIVE) {
               float time = (float)(nowTick - p.startTick) + partial;
               renderPortal(poseStack, buffer, p.pos, time, p.holdTicks);
            }

            buffer.m_109912_(PORTAL_TYPE);
            poseStack.m_85849_();
         }
      }
   }

   private static void renderPortal(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 basePos, float animationTime, int holdTicks) {
      float rotationDegrees = animationTime * Mth.m_14036_(animationTime / 30.0F, 1.0F, 10.0F);
      float scale = computeScale(animationTime, holdTicks);
      scale = Math.max(0.001F, scale);
      int alpha = Mth.m_14045_((int)(255.0F * Mth.m_14036_(scale * 1.1F, 0.0F, 1.0F)), 0, 255);
      poseStack.m_85836_();
      poseStack.m_85837_(basePos.f_82479_, basePos.f_82480_ + 0.015, basePos.f_82481_);
      poseStack.m_252781_(Axis.f_252436_.m_252977_(rotationDegrees));
      poseStack.m_85841_(scale, 1.0F, scale);
      Pose pose = poseStack.m_85850_();
      Matrix4f mat = pose.m_252922_();
      VertexConsumer vc = bufferSource.m_6299_(PORTAL_TYPE);
      int r = 255;
      int g = 255;
      int b = 255;
      vc.m_252986_(mat, -2.5F, 0.0F, -2.5F)
         .m_6122_(r, g, b, alpha)
         .m_7421_(0.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(FULL_BRIGHT_LIGHT)
         .m_252939_(pose.m_252943_(), 0.0F, 1.0F, 0.0F)
         .m_5752_();
      vc.m_252986_(mat, 2.5F, 0.0F, -2.5F)
         .m_6122_(r, g, b, alpha)
         .m_7421_(1.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(FULL_BRIGHT_LIGHT)
         .m_252939_(pose.m_252943_(), 0.0F, 1.0F, 0.0F)
         .m_5752_();
      vc.m_252986_(mat, 2.5F, 0.0F, 2.5F)
         .m_6122_(r, g, b, alpha)
         .m_7421_(1.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(FULL_BRIGHT_LIGHT)
         .m_252939_(pose.m_252943_(), 0.0F, 1.0F, 0.0F)
         .m_5752_();
      vc.m_252986_(mat, -2.5F, 0.0F, 2.5F)
         .m_6122_(r, g, b, alpha)
         .m_7421_(0.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(FULL_BRIGHT_LIGHT)
         .m_252939_(pose.m_252943_(), 0.0F, 1.0F, 0.0F)
         .m_5752_();
      poseStack.m_85849_();
   }

   private static float computeScale(float t, int holdTicks) {
      if (t <= 20.0F) {
         float p = t / 20.0F;
         return easeOutCubic(p);
      } else {
         float shrinkProgress = (t - 20.0F - (float)holdTicks) / 20.0F;
         return 1.0F - easeInCubic(Mth.m_14036_(shrinkProgress, 0.0F, 1.0F));
      }
   }

   private static float easeOutCubic(float x) {
      return 1.0F - (float)Math.pow((double)(1.0F - x), 3.0);
   }

   private static float easeInCubic(float x) {
      return x * x * x;
   }

   private static final class PortalInstance {
      final Vec3 pos;
      final long startTick;
      final int holdTicks;
      final int durationTicks;

      PortalInstance(Vec3 pos, long startTick, int holdTicks) {
         this.pos = pos;
         this.startTick = startTick;
         this.holdTicks = holdTicks;
         this.durationTicks = 20 + holdTicks + 20;
      }

      boolean isExpired(long nowTick) {
         return nowTick - this.startTick > (long)this.durationTicks;
      }
   }
}
