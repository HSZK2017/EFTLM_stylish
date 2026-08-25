package com.dmc.invincible_dmc.client.render.afterimage;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Mesh.DrawingFunction;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class AfterimageSnapshot {
   final EntitySnapshot<?> entitySnapshot;
   final Armature armature;
   final List<AfterimageSnapshot.AfterimageItemMesh> itemMeshes;
   int age;
   final int maxAge;
   final float baseAlpha;
   final boolean whiteMode;
   final int contourRgb;
   final float offsetForward;
   final float offsetRight;
   final float offsetUp;
   private static final float FADE_IN_RATIO = 0.15F;

   AfterimageSnapshot(
      EntitySnapshot<?> entitySnapshot,
      int maxAge,
      float baseAlpha,
      boolean whiteMode,
      int contourRgb,
      float offsetForward,
      float offsetRight,
      float offsetUp,
      Armature armature,
      List<AfterimageSnapshot.AfterimageItemMesh> itemMeshes
   ) {
      this.entitySnapshot = entitySnapshot;
      this.maxAge = maxAge;
      this.baseAlpha = baseAlpha;
      this.whiteMode = whiteMode;
      this.contourRgb = contourRgb;
      this.offsetForward = offsetForward;
      this.offsetRight = offsetRight;
      this.offsetUp = offsetUp;
      this.armature = armature;
      this.itemMeshes = itemMeshes;
   }

   boolean isAlive() {
      return this.age < this.maxAge;
   }

   float getAlpha() {
      float progress = (float)this.age / (float)this.maxAge;
      progress = Mth.m_14036_(progress, 0.0F, 1.0F);
      if (progress < 0.15F) {
         float t = progress / 0.15F;
         return this.baseAlpha * t * (2.0F - t);
      } else {
         float t = (progress - 0.15F) / 0.85F;
         return this.baseAlpha * (0.5F * (float)Math.cos((double)t * Math.PI) + 0.5F);
      }
   }

   Vector3f getDynamicColor() {
      float rTarget = (float)(this.contourRgb >> 16 & 0xFF) / 255.0F;
      float gTarget = (float)(this.contourRgb >> 8 & 0xFF) / 255.0F;
      float bTarget = (float)(this.contourRgb & 0xFF) / 255.0F;
      if (this.whiteMode) {
         return new Vector3f(1.0F, 1.0F, 1.0F);
      } else {
         float progress = (float)this.age / (float)this.maxAge;
         progress = Mth.m_14036_(progress, 0.0F, 1.0F);
         if (progress < 0.15F) {
            float t = progress / 0.15F;
            return new Vector3f(Mth.m_14179_(t, 1.0F, rTarget), Mth.m_14179_(t, 1.0F, gTarget), Mth.m_14179_(t, 1.0F, bTarget));
         } else {
            return new Vector3f(rTarget, gTarget, bTarget);
         }
      }
   }

   void render(PoseStack poseStack, BufferSource buffers, Camera camera, float partialTick) {
      float alpha = this.getAlpha();
      Vector3f color = this.getDynamicColor();
      Vec3 snapPos = this.entitySnapshot.getPosition();
      int lightColor = Minecraft.m_91087_().f_91073_ != null ? LevelRenderer.m_109541_(Minecraft.m_91087_().f_91073_, BlockPos.m_274446_(snapPos)) : 15728880;
      poseStack.m_85836_();
      Vec3 camPos = camera.m_90583_();
      float dx = (float)(snapPos.f_82479_ - camPos.f_82479_);
      float dy = (float)(snapPos.f_82480_ - camPos.f_82480_);
      float dz = (float)(snapPos.f_82481_ - camPos.f_82481_);
      poseStack.m_252931_(RenderSystem.getModelViewStack().m_85850_().m_252922_());
      RenderSystem.getModelViewStack().m_85836_();
      RenderSystem.getModelViewStack().m_166856_();
      RenderSystem.applyModelViewMatrix();
      poseStack.m_252880_(dx, dy, dz);
      Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
      rotation.mul(QuaternionUtils.YP.rotationDegrees(180.0F));
      poseStack.m_252781_(rotation);
      poseStack.m_252931_(OpenMatrix4f.exportToMojangMatrix(this.entitySnapshot.getModelMatrix()));
      poseStack.m_252880_(this.offsetRight, this.offsetUp, this.offsetForward);
      poseStack.m_252880_(0.0F, this.entitySnapshot.getHeightHalf(), 0.0F);
      poseStack.m_85841_(1.0F, 1.0F, 1.0F);
      poseStack.m_252880_(0.0F, -this.entitySnapshot.getHeightHalf(), 0.0F);
      this.entitySnapshot
         .renderTextured(poseStack, buffers, EpicFightRenderTypes::entityAfterimageStencil, DrawingFunction.POSITION_TEX, 0, 0.0F, 0.0F, 0.0F, 1.0F);
      buffers.m_173043_();
      this.entitySnapshot
         .render(
            poseStack,
            buffers,
            EpicFightRenderTypes.entityAfterimageWhite(),
            DrawingFunction.POSITION_TEX_COLOR_LIGHTMAP,
            lightColor,
            color.x(),
            color.y(),
            color.z(),
            alpha
         );
      buffers.m_173043_();

      for (AfterimageSnapshot.AfterimageItemMesh itemMesh : this.itemMeshes) {
         if (itemMesh.mesh() != null && itemMesh.texture() != null) {
            itemMesh.mesh().initialize();
            itemMesh.mesh()
               .draw(
                  poseStack,
                  buffers,
                  EpicFightRenderTypes.itemAfterimageStencil(),
                  DrawingFunction.POSITION_TEX,
                  lightColor,
                  0.0F,
                  0.0F,
                  0.0F,
                  1.0F,
                  OverlayTexture.f_118083_,
                  this.armature,
                  this.entitySnapshot.poseMatrices()
               );
            buffers.m_173043_();
            itemMesh.mesh()
               .draw(
                  poseStack,
                  buffers,
                  EpicFightRenderTypes.itemAfterimageWhite(),
                  DrawingFunction.POSITION_TEX_COLOR_LIGHTMAP,
                  lightColor,
                  color.x(),
                  color.y(),
                  color.z(),
                  alpha,
                  OverlayTexture.f_118083_,
                  this.armature,
                  this.entitySnapshot.poseMatrices()
               );
            if (itemMesh.textureL() != null) {
               itemMesh.mesh()
                  .draw(
                     poseStack,
                     buffers,
                     EpicFightRenderTypes.itemAfterimageWhite(),
                     DrawingFunction.POSITION_TEX_COLOR_LIGHTMAP,
                     lightColor,
                     color.x(),
                     color.y(),
                     color.z(),
                     alpha * 0.6F,
                     OverlayTexture.f_118083_,
                     this.armature,
                     this.entitySnapshot.poseMatrices()
                  );
            }

            buffers.m_173043_();
         }
      }

      RenderSystem.getModelViewStack().m_85849_();
      RenderSystem.applyModelViewMatrix();
      poseStack.m_85849_();
   }

   public static record AfterimageItemMesh(SkinnedMesh mesh, ResourceLocation texture, ResourceLocation textureL) {
   }

   @OnlyIn(Dist.CLIENT)
   static class Buffer {
      final Deque<AfterimageSnapshot> snapshots = new ArrayDeque<>();
      int maxSnapshots = 5;
      int intervalFrames = 2;
      int maxAgeFrames = 10;
      float alpha = 0.5F;
      boolean whiteMode = false;
      int contourRgb = 2236979;
      float offsetForward;
      float offsetRight;
      float offsetUp;
      int frameCounter;
      boolean active;
      final LivingEntityPatch<?> patch;

      Buffer(LivingEntityPatch<?> patch) {
         this.patch = patch;
      }

      void configure(
         int maxSnapshots,
         int intervalFrames,
         int maxAgeFrames,
         float alpha,
         boolean whiteMode,
         int contourRgb,
         float offsetForward,
         float offsetRight,
         float offsetUp
      ) {
         this.maxSnapshots = maxSnapshots;
         this.intervalFrames = intervalFrames;
         this.maxAgeFrames = maxAgeFrames;
         this.alpha = alpha;
         this.whiteMode = whiteMode;
         this.contourRgb = contourRgb;
         this.offsetForward = -offsetForward;
         this.offsetRight = offsetRight;
         this.offsetUp = offsetUp;
         this.active = true;
         this.frameCounter = 0;
         this.snapshots.clear();
      }

      void stop() {
         this.active = false;
      }

      void captureIfNeeded() {
         if (this.active) {
            Entity entity = this.patch.getOriginal();
            if (!entity.m_6084_()) {
               this.stop();
               this.snapshots.clear();
            } else {
               if (this.frameCounter % this.intervalFrames == 0) {
                  if (this.snapshots.size() >= this.maxSnapshots) {
                     this.snapshots.pollFirst();
                  }

                  EntitySnapshot<?> snap = EntitySnapshot.captureLivingEntity(this.patch);
                  if (snap != null) {
                     List<AfterimageSnapshot.AfterimageItemMesh> itemMeshes = this.captureItemMeshes();
                     this.snapshots
                        .addLast(
                           new AfterimageSnapshot(
                              snap,
                              this.maxAgeFrames,
                              this.alpha,
                              this.whiteMode,
                              this.contourRgb,
                              this.offsetForward,
                              this.offsetRight,
                              this.offsetUp,
                              this.patch.getArmature(),
                              itemMeshes
                           )
                        );
                  }
               }

               this.frameCounter++;
            }
         }
      }

      private List<AfterimageSnapshot.AfterimageItemMesh> captureItemMeshes() {
         List<AfterimageSnapshot.AfterimageItemMesh> result = new ArrayList<>();

         for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack;
            if (hand == InteractionHand.MAIN_HAND) {
               stack = ((LivingEntity)this.patch.getOriginal()).m_21205_();
            } else {
               if (!this.patch.isOffhandItemValid()) {
                  continue;
               }

               stack = ((LivingEntity)this.patch.getOriginal()).m_21206_();
            }

            if (!stack.m_41619_()) {
               RenderItemBase renderer = ClientEngine.getInstance().renderEngine.getItemRenderer(stack);
               AfterimageSnapshot.AfterimageItemMesh mesh = this.tryCaptureItemMesh(renderer, hand);
               if (mesh != null) {
                  result.add(mesh);
               }
            }
         }

         return result;
      }

      private AfterimageSnapshot.AfterimageItemMesh tryCaptureItemMesh(RenderItemBase renderer, InteractionHand hand) {
         return null;
      }

      void ageAndCull() {
         Iterator<AfterimageSnapshot> it = this.snapshots.iterator();

         while (it.hasNext()) {
            AfterimageSnapshot s = it.next();
            s.age++;
            if (!s.isAlive()) {
               it.remove();
            }
         }
      }

      void renderAll(PoseStack poseStack, BufferSource buffers, Camera camera, float partialTick) {
         for (AfterimageSnapshot s : this.snapshots) {
            s.render(poseStack, buffers, camera, partialTick);
         }
      }
   }
}
