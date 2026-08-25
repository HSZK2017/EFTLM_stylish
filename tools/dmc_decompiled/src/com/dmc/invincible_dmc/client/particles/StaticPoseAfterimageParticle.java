package com.dmc.invincible_dmc.client.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Mesh.DrawingFunction;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.api.utils.EntitySnapshot.RenderableFigure;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.particle.CustomModelParticle;
import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class StaticPoseAfterimageParticle extends CustomModelParticle<SkinnedMesh> {
   private final EntitySnapshot<?> entitySnapshot;
   private final OpenMatrix4f[] customPoseMatrices;
   private final Matrix4f customModelMatrix;
   private final Armature armature;
   private final SkinnedMesh bodyMesh;
   private final ResourceLocation bodyTexture;
   private final List<RenderableFigure> armorFigures;

   StaticPoseAfterimageParticle(
      ClientLevel level,
      double x,
      double y,
      double z,
      double xd,
      double yd,
      double zd,
      int lifetime,
      AssetAccessor<SkinnedMesh> particleMesh,
      EntitySnapshot<?> entitySnapshot,
      OpenMatrix4f[] customPoseMatrices,
      Matrix4f customModelMatrix,
      Armature armature,
      SkinnedMesh bodyMesh,
      ResourceLocation bodyTexture,
      List<RenderableFigure> armorFigures
   ) {
      super(level, x, y, z, xd, yd, zd, particleMesh);
      this.entitySnapshot = entitySnapshot;
      this.customPoseMatrices = customPoseMatrices;
      this.customModelMatrix = customModelMatrix;
      this.armature = armature;
      this.bodyMesh = bodyMesh;
      this.bodyTexture = bodyTexture;
      this.armorFigures = armorFigures;
      this.f_107225_ = lifetime;
      this.f_107219_ = false;
      this.f_107227_ = 1.0F;
      this.f_107228_ = 1.0F;
      this.f_107229_ = 1.0F;
      this.f_107230_ = 0.7F;
   }

   @Nullable
   public static StaticPoseAfterimageParticle create(LivingEntityPatch<?> patch, StaticAnimation animation, float animTime, int lifetime) {
      if (patch != null && animation != null) {
         LivingEntity entity = (LivingEntity)patch.getOriginal();
         EntitySnapshot<?> snap = EntitySnapshot.captureLivingEntity(patch);
         if (snap == null) {
            return null;
         } else {
            SkinnedMesh bodyMesh = extractBodyMesh(snap);
            ResourceLocation bodyTexture = extractBodyTexture(snap);
            List<RenderableFigure> armorFigs = extractArmorFigures(snap);
            Armature arm = patch.getArmature();
            Pose pose = animation.getPoseByTime(patch, animTime, 0.0F);
            PatchedEntityRenderer renderer = ClientEngine.getInstance().renderEngine.getEntityRenderer(entity);
            renderer.setJointTransforms(patch, arm, pose, 1.0F);
            OpenMatrix4f[] poseMatrices = arm.getPoseAsTransformMatrix(pose, true);
            OpenMatrix4f modelMat = MathUtils.getModelMatrixIntegral(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            Matrix4f jomlModelMat = OpenMatrix4f.exportToMojangMatrix(modelMat);
            AssetAccessor<SkinnedMesh> meshAccessor = renderer.getMeshProvider(patch);
            Vec3 pos = entity.m_20182_();
            return new StaticPoseAfterimageParticle(
               (ClientLevel)entity.m_9236_(),
               pos.f_82479_,
               pos.f_82480_,
               pos.f_82481_,
               0.0,
               0.0,
               0.0,
               lifetime,
               meshAccessor,
               snap,
               poseMatrices,
               jomlModelMat,
               arm,
               bodyMesh,
               bodyTexture,
               armorFigs
            );
         }
      } else {
         return null;
      }
   }

   public void m_5744_(VertexConsumer ignored, Camera camera, float partialTicks) {
      float alpha = this.f_107230_;
      int lightColor = this.m_6355_(partialTicks);
      PoseStack poseStack = new PoseStack();
      this.setupPoseStack(poseStack, camera, partialTicks);
      BufferSource buffers = Minecraft.m_91087_().m_91269_().m_110104_();
      this.drawAllMeshes(poseStack, buffers, EpicFightRenderTypes::entityAfterimageStencil, DrawingFunction.POSITION_TEX, 0, 0.0F, 0.0F, 0.0F, 1.0F);
      buffers.m_173043_();
      this.drawAllMeshes(
         poseStack,
         buffers,
         EpicFightRenderTypes::entityAfterimageTranslucent,
         DrawingFunction.NEW_ENTITY,
         lightColor,
         this.f_107227_,
         this.f_107228_,
         this.f_107229_,
         alpha
      );
      buffers.m_173043_();
      this.revert(poseStack);
   }

   private void drawAllMeshes(
      PoseStack poseStack,
      BufferSource buffers,
      Function<ResourceLocation, RenderType> rendertypeFunction,
      DrawingFunction drawingFunction,
      int lightColor,
      float r,
      float g,
      float b,
      float a
   ) {
      if (this.bodyMesh != null && this.bodyTexture != null) {
         this.bodyMesh.initialize();
         this.bodyMesh
            .draw(
               poseStack,
               buffers,
               rendertypeFunction.apply(this.bodyTexture),
               drawingFunction,
               lightColor,
               r,
               g,
               b,
               a,
               OverlayTexture.f_118083_,
               this.armature,
               this.customPoseMatrices
            );
      }

      for (RenderableFigure armorFig : this.armorFigures) {
         if (armorFig.mesh() != null && armorFig.texture() != null) {
            armorFig.mesh().initialize();
            if (armorFig.mesh() instanceof SkinnedMesh skinned) {
               skinned.draw(
                  poseStack,
                  buffers,
                  rendertypeFunction.apply(armorFig.texture()),
                  drawingFunction,
                  lightColor,
                  r,
                  g,
                  b,
                  a,
                  OverlayTexture.f_118083_,
                  this.armature,
                  this.customPoseMatrices
               );
            }
         }
      }
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return EpicFightParticleRenderTypes.ENTITY_PARTICLE;
   }

   protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTick) {
      poseStack.m_85836_();
      poseStack.m_252931_(RenderSystem.getModelViewStack().m_85850_().m_252922_());
      RenderSystem.getModelViewStack().m_85836_();
      RenderSystem.getModelViewStack().m_166856_();
      RenderSystem.applyModelViewMatrix();
      Vec3 cameraPos = camera.m_90583_();
      float x = (float)(Mth.m_14139_((double)partialTick, this.f_107209_, this.f_107212_) - cameraPos.m_7096_());
      float y = (float)(Mth.m_14139_((double)partialTick, this.f_107210_, this.f_107213_) - cameraPos.m_7098_());
      float z = (float)(Mth.m_14139_((double)partialTick, this.f_107211_, this.f_107214_) - cameraPos.m_7094_());
      poseStack.m_252880_(x, y, z);
      Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
      rotation.mul(QuaternionUtils.YP.rotationDegrees(180.0F));
      poseStack.m_252781_(rotation);
      poseStack.m_252931_(this.customModelMatrix);
      float scale = Mth.m_14179_(partialTick, this.scaleO, this.scale);
      poseStack.m_252880_(0.0F, this.entitySnapshot.getHeightHalf(), 0.0F);
      poseStack.m_85841_(scale, scale, scale);
      poseStack.m_252880_(0.0F, -this.entitySnapshot.getHeightHalf(), 0.0F);
   }

   protected void revert(PoseStack poseStack) {
      poseStack.m_85849_();
      RenderSystem.getModelViewStack().m_85849_();
      RenderSystem.applyModelViewMatrix();
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      } else {
         this.scaleO = this.scale;
      }
   }

   private static SkinnedMesh extractBodyMesh(EntitySnapshot<?> snap) {
      try {
         Field f = EntitySnapshot.class.getDeclaredField("entityFigure");
         f.setAccessible(true);
         RenderableFigure fig = (RenderableFigure)f.get(snap);
         return fig != null ? (SkinnedMesh)fig.mesh() : null;
      } catch (Exception var3) {
         return null;
      }
   }

   private static ResourceLocation extractBodyTexture(EntitySnapshot<?> snap) {
      try {
         Field f = EntitySnapshot.class.getDeclaredField("entityFigure");
         f.setAccessible(true);
         RenderableFigure fig = (RenderableFigure)f.get(snap);
         return fig != null ? fig.texture() : null;
      } catch (Exception var3) {
         return null;
      }
   }

   private static List<RenderableFigure> extractArmorFigures(EntitySnapshot<?> snap) {
      try {
         Field f = EntitySnapshot.class.getDeclaredField("armorMeshes");
         f.setAccessible(true);
         return (List<RenderableFigure>)f.get(snap);
      } catch (Exception var2) {
         return Collections.emptyList();
      }
   }
}
