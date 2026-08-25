package com.dmc.invincible_dmc.client.render.cinematic;

import com.dmc.invincible_dmc.client.renderer.RenderYamato;
import com.dmc.invincible_dmc.utils.vfx.CinematicBarsUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public final class CinematicYamatoBreakoutRenderer {
   private static final List<CinematicYamatoBreakoutRenderer.RenderEntry> ENTRIES = new ArrayList<>();
   private static long capturedFrame = Long.MIN_VALUE;
   private static long renderedFrame = Long.MIN_VALUE;
   private static boolean replaying;

   private CinematicYamatoBreakoutRenderer() {
   }

   public static boolean isReplaying() {
      return replaying;
   }

   public static void capture(
      RenderYamato renderer,
      ItemStack stack,
      LivingEntityPatch<?> patch,
      InteractionHand hand,
      OpenMatrix4f[] poses,
      PoseStack poseStack,
      int packedLight,
      float partialTick
   ) {
      if (!replaying && CinematicBarsWorldRenderer.hasVisibleMask()) {
         Minecraft minecraft = Minecraft.m_91087_();
         long frame = minecraft.m_261169_();
         if (capturedFrame != frame) {
            capturedFrame = frame;
            ENTRIES.clear();
         }

         Pose pose = poseStack.m_85850_();
         ENTRIES.add(
            new CinematicYamatoBreakoutRenderer.RenderEntry(
               renderer,
               stack.m_41777_(),
               patch,
               hand,
               copyPoses(poses),
               new Matrix4f(pose.m_252922_()),
               new Matrix3f(pose.m_252943_()),
               new Matrix4f(RenderSystem.getProjectionMatrix()),
               new Matrix4f(RenderSystem.getModelViewMatrix()),
               packedLight,
               partialTick
            )
         );
      }
   }

   public static void renderAfterCinematicBars() {
      Minecraft minecraft = Minecraft.m_91087_();
      long frame = minecraft.m_261169_();
      if (renderedFrame != frame && capturedFrame == frame && !ENTRIES.isEmpty()) {
         renderedFrame = frame;
         float heightRatio = CinematicBarsUtils.getRenderedHeightRatio();
         boolean verticalMaskEnabled = CinematicBarsWorldRenderer.isCommandMaskEnabled();
         if (!(heightRatio <= 0.0F) || verticalMaskEnabled) {
            int windowWidth = minecraft.m_91268_().m_85441_();
            int windowHeight = minecraft.m_91268_().m_85442_();
            int barHeight = Math.min(windowHeight, Math.max(0, Math.round((float)windowHeight * heightRatio)));
            int verticalBarWidth = Math.max(1, Math.round((float)windowWidth * CinematicBarsWorldRenderer.getCommandMaskHalfWidthRatio() * 2.0F));
            int leftBarX = Math.round((float)windowWidth * 0.3F - (float)verticalBarWidth * 0.5F);
            int rightBarX = Math.round((float)windowWidth * 0.7F - (float)verticalBarWidth * 0.5F);
            int verticalHeight = Math.max(0, windowHeight - barHeight * 2);
            List<CinematicYamatoBreakoutRenderer.RenderEntry> entries = List.copyOf(ENTRIES);
            BufferSource buffers = minecraft.m_91269_().m_110104_();
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(515);
            RenderSystem.enablePolygonOffset();
            RenderSystem.polygonOffset(-1.0F, -8.0F);
            replaying = true;

            try {
               if (barHeight > 0) {
                  renderScissored(entries, buffers, 0, windowHeight - barHeight, windowWidth, barHeight);
                  renderScissored(entries, buffers, 0, 0, windowWidth, barHeight);
               }

               if (verticalMaskEnabled && verticalHeight > 0) {
                  renderScissored(entries, buffers, leftBarX, barHeight, verticalBarWidth, verticalHeight);
                  renderScissored(entries, buffers, rightBarX, barHeight, verticalBarWidth, verticalHeight);
               }
            } finally {
               replaying = false;
               RenderSystem.disableScissor();
               RenderSystem.polygonOffset(0.0F, 0.0F);
               RenderSystem.disablePolygonOffset();
               RenderSystem.depthMask(true);
            }
         }
      }
   }

   private static void renderScissored(List<CinematicYamatoBreakoutRenderer.RenderEntry> entries, BufferSource buffers, int x, int y, int width, int height) {
      RenderSystem.enableScissor(x, y, width, height);

      for (CinematicYamatoBreakoutRenderer.RenderEntry entry : entries) {
         renderEntry(entry, buffers);
      }

      buffers.m_109911_();
   }

   private static void renderEntry(CinematicYamatoBreakoutRenderer.RenderEntry entry, BufferSource buffers) {
      PoseStack modelViewStack = RenderSystem.getModelViewStack();
      RenderSystem.backupProjectionMatrix();
      modelViewStack.m_85836_();

      try {
         modelViewStack.m_166856_();
         modelViewStack.m_252931_(entry.modelViewMatrix());
         RenderSystem.applyModelViewMatrix();
         RenderSystem.setProjectionMatrix(new Matrix4f(entry.projectionMatrix()), VertexSorting.f_276450_);
         PoseStack poseStack = new PoseStack();
         poseStack.m_85850_().m_252922_().set(entry.poseMatrix());
         poseStack.m_85850_().m_252943_().set(entry.normalMatrix());
         entry.renderer()
            .renderItemInHand(entry.stack(), entry.patch(), entry.hand(), entry.poses(), buffers, poseStack, entry.packedLight(), entry.partialTick());
      } finally {
         modelViewStack.m_85849_();
         RenderSystem.applyModelViewMatrix();
         RenderSystem.restoreProjectionMatrix();
      }
   }

   private static OpenMatrix4f[] copyPoses(OpenMatrix4f[] poses) {
      OpenMatrix4f[] copy = new OpenMatrix4f[poses.length];

      for (int index = 0; index < poses.length; index++) {
         copy[index] = new OpenMatrix4f(poses[index]);
      }

      return copy;
   }

   private static record RenderEntry(
      RenderYamato renderer,
      ItemStack stack,
      LivingEntityPatch<?> patch,
      InteractionHand hand,
      OpenMatrix4f[] poses,
      Matrix4f poseMatrix,
      Matrix3f normalMatrix,
      Matrix4f projectionMatrix,
      Matrix4f modelViewMatrix,
      int packedLight,
      float partialTick
   ) {
   }
}
