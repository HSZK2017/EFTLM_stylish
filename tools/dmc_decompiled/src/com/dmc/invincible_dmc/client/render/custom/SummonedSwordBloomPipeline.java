package com.dmc.invincible_dmc.client.render.custom;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.client.render.PostPasses;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.guhao.vix.client.compat.oculus.OculusShaderCompat;
import com.guhao.vix.client.model.ShaderSkinnedMesh;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.targets.ScaledTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import yesman.epicfight.api.client.model.Mesh.DrawingFunction;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;

@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT},
   bus = Bus.FORGE
)
public final class SummonedSwordBloomPipeline {
   private static final int BUFFER_SIZE = 131072;
   private static final float BLOOM_RED = 0.1764706F;
   private static final float BLOOM_GREEN = 0.4F;
   private static final float BLOOM_BLUE = 0.54901963F;
   private static final SummonedSwordBloomPipeline.Pipeline PIPELINE = new SummonedSwordBloomPipeline.Pipeline(
      InvincibleMod_DMC.rl("summoned_sword_mesh_bloom")
   );
   private static final BufferSource BUFFER_SOURCE = MultiBufferSource.m_109898_(new BufferBuilder(131072));
   private static final Queue<SummonedSwordBloomPipeline.DrawRequest> DRAW_QUEUE = new ArrayDeque<>();

   private SummonedSwordBloomPipeline() {
   }

   public static void queue(ShaderSkinnedMesh mesh, RenderType renderType, PoseStack poseStack, int packedLight, Armature armature, OpenMatrix4f[] poseMatrices) {
      queue(mesh, renderType, poseStack, packedLight, armature, poseMatrices, 0.1764706F, 0.4F, 0.54901963F, 1.0F);
   }

   public static void queue(
      ShaderSkinnedMesh mesh,
      RenderType renderType,
      PoseStack poseStack,
      int packedLight,
      Armature armature,
      OpenMatrix4f[] poseMatrices,
      float red,
      float green,
      float blue,
      float alpha
   ) {
      if (!OculusShaderCompat.isShadowPass()) {
         DRAW_QUEUE.add(
            new SummonedSwordBloomPipeline.DrawRequest(
               mesh,
               renderType,
               new Matrix4f(poseStack.m_85850_().m_252922_()),
               new Matrix3f(poseStack.m_85850_().m_252943_()),
               packedLight,
               armature,
               copyPoseMatrices(poseMatrices),
               red,
               green,
               blue,
               alpha
            )
         );
      }
   }

   @SubscribeEvent
   public static void onRenderLevelStage(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_PARTICLES) {
         flushQueue();
      } else if (event.getStage() == Stage.AFTER_LEVEL) {
         DRAW_QUEUE.clear();
      }
   }

   public static void releaseCachedTargets() {
      DRAW_QUEUE.clear();
      PIPELINE.destroyBlurTargets();
   }

   private static void flushQueue() {
      if (!DRAW_QUEUE.isEmpty()) {
         if (PostEffectPipelines.isActive() && !OculusShaderCompat.isShadowPass()) {
            boolean drewMesh = false;
            PIPELINE.start();

            try {
               if (PIPELINE.bufferTarget != null) {
                  SummonedSwordBloomPipeline.DrawRequest request;
                  while ((request = DRAW_QUEUE.poll()) != null) {
                     PoseStack poseStack = new PoseStack();
                     poseStack.m_85850_().m_252922_().set(request.poseMatrix());
                     poseStack.m_85850_().m_252943_().set(request.normalMatrix());
                     request.mesh()
                        .draw(
                           poseStack,
                           BUFFER_SOURCE,
                           request.renderType(),
                           DrawingFunction.NEW_ENTITY,
                           request.packedLight(),
                           request.red(),
                           request.green(),
                           request.blue(),
                           request.alpha(),
                           OverlayTexture.f_118083_,
                           request.armature(),
                           request.poseMatrices()
                        );
                     drewMesh = true;
                  }

                  if (drewMesh) {
                     BUFFER_SOURCE.m_109911_();
                     PIPELINE.call();
                  }

                  return;
               }
            } finally {
               DRAW_QUEUE.clear();
               PIPELINE.suspend();
            }
         } else {
            DRAW_QUEUE.clear();
         }
      }
   }

   private static OpenMatrix4f[] copyPoseMatrices(OpenMatrix4f[] poseMatrices) {
      OpenMatrix4f[] copiedMatrices = new OpenMatrix4f[poseMatrices.length];

      for (int index = 0; index < poseMatrices.length; index++) {
         copiedMatrices[index] = new OpenMatrix4f(poseMatrices[index]);
      }

      return copiedMatrices;
   }

   private static record DrawRequest(
      ShaderSkinnedMesh mesh,
      RenderType renderType,
      Matrix4f poseMatrix,
      Matrix3f normalMatrix,
      int packedLight,
      Armature armature,
      OpenMatrix4f[] poseMatrices,
      float red,
      float green,
      float blue,
      float alpha
   ) {
   }

   private static final class Pipeline extends com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline {
      private RenderTarget[] downTargets;
      private RenderTarget[] upTargets;
      private RenderTarget compositeTarget;
      private int lastWidth = -1;
      private int lastHeight = -1;

      private Pipeline(ResourceLocation name) {
         super(name);
      }

      public void PostEffectHandler() {
         if (this.bufferTarget != null
            && PostPasses.downSampler != null
            && PostPasses.upSampler != null
            && PostPasses.unity_composite != null
            && PostPasses.blit != null) {
            this.initTargets();
            if (this.downTargets != null && this.upTargets != null && this.compositeTarget != null) {
               RenderSystem.texParameter(3553, 10242, 33071);
               RenderSystem.texParameter(3553, 10243, 33071);
               RenderSystem.texParameter(3553, 10240, 9729);
               RenderSystem.texParameter(3553, 10241, 9729);
               PostPasses.downSampler.process(this.bufferTarget, this.downTargets[0]);
               PostPasses.downSampler.process(this.downTargets[0], this.downTargets[1]);
               PostPasses.downSampler.process(this.downTargets[1], this.downTargets[2]);
               PostPasses.downSampler.process(this.downTargets[2], this.downTargets[3]);
               PostPasses.downSampler.process(this.downTargets[3], this.downTargets[4]);
               PostPasses.upSampler.process(this.downTargets[4], this.upTargets[3], this.downTargets[3]);
               PostPasses.upSampler.process(this.upTargets[3], this.upTargets[2], this.downTargets[2]);
               PostPasses.upSampler.process(this.upTargets[2], this.upTargets[1], this.downTargets[1]);
               PostPasses.upSampler.process(this.upTargets[1], this.upTargets[0], this.downTargets[0]);
               RenderTarget sceneTarget = PostEffectPipelines.getSource();
               PostPasses.unity_composite.process(this.upTargets[0], this.compositeTarget, this.bufferTarget, sceneTarget);
               PostPasses.blit.process(this.compositeTarget, sceneTarget);
            }
         }
      }

      private void initTargets() {
         if (this.bufferTarget.f_83915_ != this.lastWidth
            || this.bufferTarget.f_83916_ != this.lastHeight
            || this.downTargets == null
            || this.upTargets == null
            || this.compositeTarget == null) {
            this.destroyBlurTargets();

            try {
               this.downTargets = new RenderTarget[5];
               float scale = 0.5F;

               for (int index = 0; index < this.downTargets.length; index++) {
                  this.downTargets[index] = this.createScaledTarget(scale);
                  scale *= 0.5F;
               }

               this.upTargets = new RenderTarget[4];
               scale = 0.5F;

               for (int index = 0; index < this.upTargets.length; index++) {
                  this.upTargets[index] = this.createScaledTarget(scale);
                  scale *= 0.5F;
               }

               this.compositeTarget = new TextureTarget(this.bufferTarget.f_83915_, this.bufferTarget.f_83916_, true, Minecraft.f_91002_);
               this.compositeTarget.m_83931_(0.0F, 0.0F, 0.0F, 0.0F);
               this.compositeTarget.m_83954_(Minecraft.f_91002_);
               this.lastWidth = this.bufferTarget.f_83915_;
               this.lastHeight = this.bufferTarget.f_83916_;
            } catch (RuntimeException var3) {
               DMCLog.warn(DMCLog.Category.RENDER, "SummonedSwordBloomPipeline: Failed to create render targets.", var3);
               this.destroyBlurTargets();
            }
         }
      }

      private RenderTarget createScaledTarget(float scale) {
         RenderTarget target = new ScaledTarget(scale, scale, this.bufferTarget.f_83915_, this.bufferTarget.f_83916_, false, Minecraft.f_91002_);
         target.m_83931_(0.0F, 0.0F, 0.0F, 0.0F);
         target.m_83954_(Minecraft.f_91002_);
         return target;
      }

      private void destroyBlurTargets() {
         destroyTargets(this.downTargets);
         this.downTargets = null;
         destroyTargets(this.upTargets);
         this.upTargets = null;
         if (this.compositeTarget != null) {
            this.compositeTarget.m_83930_();
            this.compositeTarget = null;
         }

         this.lastWidth = -1;
         this.lastHeight = -1;
      }

      private static void destroyTargets(RenderTarget[] targets) {
         if (targets != null) {
            for (RenderTarget target : targets) {
               if (target != null) {
                  target.m_83930_();
               }
            }
         }
      }
   }
}
