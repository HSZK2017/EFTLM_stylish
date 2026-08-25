package com.dmc.invincible_dmc.client.particles.trail;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.effeks.EffekConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.particle.AnimationTrailParticle;
import yesman.epicfight.client.particle.AbstractTrailParticle.TrailEdge;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

@OnlyIn(Dist.CLIENT)
public class FlowingAnimationTrailParticle extends AnimationTrailParticle {
   protected float flowSpeed = 3.0F;
   protected float flowOffset = 0.0F;
   protected float textureScrollV = 0.01F;
   private static final Random RANDOM = new Random();
   private static final float MAX_DISTORTION = 0.45F;
   private static final float MOTION_FACTOR_MULTIPLIER = 1.6F;
   protected float motionFactor = 0.0F;
   protected float timeOffset = RANDOM.nextFloat() * 100.0F;

   protected FlowingAnimationTrailParticle(
      ClientLevel level, LivingEntityPatch<?> owner, Joint joint, AssetAccessor<? extends StaticAnimation> animation, TrailInfo trailInfo
   ) {
      super(level, owner, joint, animation, trailInfo);
   }

   public void m_5989_() {
      super.m_5989_();
      this.flowOffset = this.flowOffset + this.flowSpeed * 0.05F;
      this.textureScrollV = this.textureScrollV + this.flowSpeed * 0.03F;
   }

   private void updateMotionFactor() {
      if (this.owner != null && ((LivingEntityPatch)this.owner).getOriginal() != null) {
         Vec3 deltaMovement = ((LivingEntity)((LivingEntityPatch)this.owner).getOriginal()).m_20184_();
         this.motionFactor = Mth.m_14179_(0.15F, this.motionFactor, (float)deltaMovement.m_82553_() * 0.7F);
      }
   }

   private float calculateDynamicIntensity() {
      float baseIntensity = 0.4F;
      float intensity = baseIntensity * (0.5F + this.motionFactor * 1.6F);
      return Mth.m_14036_(intensity, 0.05F, 0.45F);
   }

   private Vector4f applyVertexDistortion(Vec3 position, float time, float intensity, Matrix4f matrix) {
      float speedMultiplier = 0.8F;
      float timeFactor = time * speedMultiplier;
      float x = (float)position.f_82479_;
      float y = (float)position.f_82480_;
      float z = (float)position.f_82481_;
      float offsetX = Mth.m_14031_(timeFactor + x * 0.5F) * intensity + Mth.m_14089_(timeFactor * 0.8F + z * 0.3F) * intensity * 0.6F;
      float offsetY = Mth.m_14031_(timeFactor * 1.2F + x * 0.2F) * intensity * 0.4F;
      float offsetZ = Mth.m_14089_(timeFactor * 0.7F + z * 0.4F) * intensity - Mth.m_14031_(timeFactor * 0.9F) * intensity * 0.3F;
      return new Vector4f(x + offsetX, y + offsetY, z + offsetZ, 1.0F).mul(matrix);
   }

   private void applyMotionBlur(Vector4f pos1, Vector4f pos2, Vector4f pos3, Vector4f pos4, int index, int totalEdges) {
      if (this.owner != null && ((LivingEntityPatch)this.owner).getOriginal() != null) {
         Vec3 velocity = ((LivingEntity)((LivingEntityPatch)this.owner).getOriginal()).m_20184_();
         float velocityFactor = 0.2F * (1.0F - (float)index / (float)totalEdges);
         Vector4f velocityOffset = new Vector4f(
            (float)velocity.f_82479_ * velocityFactor, (float)velocity.f_82480_ * velocityFactor, (float)velocity.f_82481_ * velocityFactor, 0.0F
         );
         pos1.add(velocityOffset);
         pos2.add(velocityOffset);
         pos3.add(velocityOffset);
         pos4.add(velocityOffset);
      }
   }

   public void m_5744_(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
      if (EffekConfig.isEnabled("flowing_trail", DMConfig.FLOWING_TRAIL)) {
         if (!this.trailEdges.isEmpty()) {
            PoseStack poseStack = new PoseStack();
            int light = this.m_6355_(partialTick);
            this.setupPoseStack(poseStack, camera, partialTick);
            Matrix4f matrix4f = poseStack.m_85850_().m_252922_();
            int edges = this.trailEdges.size() - 1;
            boolean startFade = ((TrailEdge)this.trailEdges.get(0)).lifetime == 1;
            boolean endFade = ((TrailEdge)this.trailEdges.get(edges)).lifetime == this.trailInfo.trailLifetime();
            float startEdge = (startFade ? (float)(this.trailInfo.interpolateCount() * 2) * partialTick : 0.0F) + this.startEdgeCorrection;
            float endEdge = endFade
               ? Math.min((float)edges - (float)(this.trailInfo.interpolateCount() * 2) * (1.0F - partialTick), (float)(edges - 1))
               : (float)(edges - 1);
            if (startEdge >= endEdge) {
               return;
            }

            float interval = 1.0F / (endEdge - startEdge);
            float fading = 1.0F;
            if (this.shouldRemove) {
               if (TrailInfo.isValidTime(this.trailInfo.fadeTime())) {
                  fading = (float)(this.f_107225_ - this.f_107224_) / (float)this.trailInfo.trailLifetime();
               } else {
                  fading = Mth.m_14036_(((float)(this.f_107225_ - this.f_107224_) + (1.0F - partialTick)) / (float)this.trailInfo.trailLifetime(), 0.0F, 1.0F);
               }
            }

            float time = ((float)this.f_107208_.m_46467_() + partialTick + this.timeOffset) * 1.2F;
            float dynamicIntensity = this.calculateDynamicIntensity();
            float currentFlow = this.flowOffset;
            float texVOffset = this.textureScrollV % 1.0F;
            float partialStartEdge = interval * (startEdge % 1.0F);
            float from = -partialStartEdge;
            float to = -partialStartEdge + interval;

            for (int i = (int)startEdge; i < (int)endEdge + 1 && i < this.trailEdges.size() - 1; i++) {
               TrailEdge e1 = (TrailEdge)this.trailEdges.get(i);
               TrailEdge e2 = (TrailEdge)this.trailEdges.get(i + 1);
               Vector4f pos1 = this.applyVertexDistortion(e1.start, time, dynamicIntensity, matrix4f);
               Vector4f pos2 = this.applyVertexDistortion(e1.end, time, dynamicIntensity, matrix4f);
               Vector4f pos3 = this.applyVertexDistortion(e2.end, time, dynamicIntensity, matrix4f);
               Vector4f pos4 = this.applyVertexDistortion(e2.start, time, dynamicIntensity, matrix4f);
               this.applyMotionBlur(pos1, pos2, pos3, pos4, i, edges);
               float uvFrom = (from + currentFlow) % 1.0F;
               float uvTo = (to + currentFlow) % 1.0F;
               if (uvFrom < 0.0F) {
                  uvFrom++;
               }

               if (uvTo < 0.0F) {
                  uvTo++;
               }

               float vTop = texVOffset + 1.0F;
               float alphaFrom = Mth.m_14036_(from, 0.0F, 1.0F) * fading;
               float alphaTo = Mth.m_14036_(to, 0.0F, 1.0F) * fading;
               vertexConsumer.m_5483_((double)pos1.x(), (double)pos1.y(), (double)pos1.z())
                  .m_7421_(uvFrom, vTop)
                  .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_ * alphaFrom)
                  .m_85969_(light)
                  .m_5752_();
               vertexConsumer.m_5483_((double)pos2.x(), (double)pos2.y(), (double)pos2.z())
                  .m_7421_(uvFrom, texVOffset)
                  .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_ * alphaFrom)
                  .m_85969_(light)
                  .m_5752_();
               vertexConsumer.m_5483_((double)pos3.x(), (double)pos3.y(), (double)pos3.z())
                  .m_7421_(uvTo, texVOffset)
                  .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_ * alphaTo)
                  .m_85969_(light)
                  .m_5752_();
               vertexConsumer.m_5483_((double)pos4.x(), (double)pos4.y(), (double)pos4.z())
                  .m_7421_(uvTo, vTop)
                  .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_ * alphaTo)
                  .m_85969_(light)
                  .m_5752_();
               from += interval;
               to += interval;
               this.updateMotionFactor();
            }
         }
      }
   }

   public boolean shouldCull() {
      return false;
   }

   public void setFlowSpeed(float speed) {
      this.flowSpeed = speed;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(
         @NotNull SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         int eid = (int)Double.doubleToRawLongBits(x);
         int animid = (int)Double.doubleToRawLongBits(z);
         int jointId = (int)Double.doubleToRawLongBits(xSpeed);
         int idx = (int)Double.doubleToRawLongBits(ySpeed);
         Entity entity = level.m_6815_(eid);
         if (entity == null) {
            return null;
         } else {
            LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
            LivingEntityPatch<?> owner = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
            if (owner == null) {
               return null;
            } else {
               AnimationAccessor<? extends StaticAnimation> animation = AnimationManager.byId(animid);
               if (animation == null) {
                  return null;
               } else {
                  Optional<List<TrailInfo>> trailInfo = ((StaticAnimation)animation.get()).getProperty(ClientAnimationProperties.TRAIL_EFFECT);
                  if (trailInfo.isEmpty()) {
                     return null;
                  } else {
                     TrailInfo result = trailInfo.get().get(idx);
                     if (result.hand() != null) {
                        ItemStack stack = ((LivingEntity)owner.getOriginal()).m_21120_(result.hand());
                        RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(stack);
                        if (renderItemBase != null && renderItemBase.trailInfo() != null) {
                           result = renderItemBase.trailInfo().overwrite(result);
                        }
                     }

                     if (entitypatch != null) {
                        result = entitypatch.getEntityDecorations()
                           .getModifiedTrailInfo(
                              result, result.hand() == null ? CapabilityItem.EMPTY : entitypatch.getAdvancedHoldingItemCapability(result.hand())
                           );
                     }

                     if (result.playable()) {
                        FlowingAnimationTrailParticle particle = new FlowingAnimationTrailParticle(
                           level, owner, owner.getArmature().searchJointById(jointId), animation, result
                        );
                        particle.setFlowSpeed(4.0F);
                        return particle;
                     } else {
                        return null;
                     }
                  }
               }
            }
         }
      }
   }
}
