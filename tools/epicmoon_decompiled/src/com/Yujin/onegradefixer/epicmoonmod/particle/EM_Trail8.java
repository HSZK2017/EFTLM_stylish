package com.Yujin.onegradefixer.epicmoonmod.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Optional;
import mod.chloeprime.aaaparticles.api.client.EffectRegistry;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.particle.AnimationTrailParticle;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class EM_Trail8 extends AnimationTrailParticle {
   public static final ResourceLocation EFFECT = new ResourceLocation("epicmoonmod", "reload");
   private volatile ParticleEmitter aaaEmitter;
   private boolean aaaLoading;
   private Vec3 aaaPosition;
   private float aaaRotX;
   private float aaaRotY;
   private boolean aaaVisible;

   protected EM_Trail8(ClientLevel level, LivingEntityPatch<?> owner, Joint joint, AssetAccessor<? extends StaticAnimation> animation, TrailInfo trailInfo) {
      super(level, owner, joint, animation, trailInfo);
   }

   private void createAAAEmitter() {
      if (this.aaaEmitter == null && !this.aaaLoading) {
         this.aaaLoading = true;
         EffectRegistry.load(EFFECT).thenAccept(effectDefinition -> this.aaaEmitter = effectDefinition.play());
      }
   }

   public void m_5744_(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
      super.m_5744_(vertexConsumer, camera, partialTick);
      ParticleEmitter emitter = this.aaaEmitter;
      Vec3 position = this.aaaPosition;
      if (emitter != null && emitter.exists() && position != null) {
         emitter.setVisibility(this.aaaVisible);
         if (this.aaaVisible) {
            emitter.setPosition((float)position.f_82479_, (float)position.f_82480_, (float)position.f_82481_);
            emitter.setRotation(this.aaaRotX, this.aaaRotY, 0.0F);
         }
      }
   }

   protected void createNextCurve() {
      TrailInfo trailInfo = this.trailInfo;
      AnimationPlayer animPlayer = ((LivingEntityPatch)this.owner).getAnimator().getPlayerFor(this.animation);
      boolean isTrailInvisible = ((DynamicAnimation)animPlayer.getAnimation().get()).isLinkAnimation() || animPlayer.getElapsedTime() <= trailInfo.startTime();
      Pose prevPose = ((LivingEntityPatch)this.owner).getAnimator().getPose(0.0F);
      LivingEntity entity = (LivingEntity)((LivingEntityPatch)this.owner).getOriginal();
      Vec3 posOld = entity.m_20318_(0.0F);
      OpenMatrix4f prevModelMatrix = ((LivingEntityPatch)this.owner).getModelMatrix(0.0F);
      OpenMatrix4f prvmodelTf = OpenMatrix4f.createTranslation((float)posOld.f_82479_, (float)posOld.f_82480_, (float)posOld.f_82481_)
         .rotateDeg(180.0F, Vec3f.Y_AXIS)
         .mulBack(prevModelMatrix);
      OpenMatrix4f prevJointTf = ((LivingEntityPatch)this.owner).getArmature().getBoundTransformFor(prevPose, this.joint).mulFront(prvmodelTf);
      Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, trailInfo.start());
      Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, trailInfo.end());
      super.createNextCurve();
      if (isTrailInvisible) {
         this.aaaVisible = false;
      } else {
         this.aaaPosition = prevEndPos;
         this.aaaRotX = (float)Math.toRadians((double)entity.m_146909_());
         this.aaaRotY = -((float)Math.toRadians((double)entity.m_146908_()));
         this.aaaVisible = true;
         this.createAAAEmitter();
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         int entityId = (int)Double.doubleToRawLongBits(x);
         int animationId = (int)Double.doubleToRawLongBits(z);
         int jointId = (int)Double.doubleToRawLongBits(xSpeed);
         int trailIndex = (int)Double.doubleToRawLongBits(ySpeed);
         Entity entity = level.m_6815_(entityId);
         if (entity == null) {
            return null;
         } else {
            LivingEntityPatch<?> entityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
            if (entityPatch == null) {
               return null;
            } else {
               AnimationAccessor<? extends StaticAnimation> animation = AnimationManager.byId(animationId);
               if (animation == null) {
                  return null;
               } else {
                  Optional<List<TrailInfo>> trailInfos = ((StaticAnimation)animation.get()).getProperty(ClientAnimationProperties.TRAIL_EFFECT);
                  if (trailInfos.isEmpty()) {
                     return null;
                  } else {
                     List<TrailInfo> infos = trailInfos.get();
                     if (trailIndex >= 0 && trailIndex < infos.size()) {
                        TrailInfo result = infos.get(trailIndex);
                        if (result.hand() != null) {
                           ItemStack stack = ((LivingEntity)entityPatch.getOriginal()).m_21120_(result.hand());
                           RenderItemBase renderer = ClientEngine.getInstance().renderEngine.getItemRenderer(stack);
                           if (renderer != null && renderer.trailInfo() != null) {
                              result = renderer.trailInfo().overwrite(result);
                           }
                        }

                        result = entityPatch.getEntityDecorations()
                           .getModifiedTrailInfo(
                              result, result.hand() == null ? CapabilityItem.EMPTY : entityPatch.getAdvancedHoldingItemCapability(result.hand())
                           );
                        if (!result.playable()) {
                           return null;
                        } else {
                           Joint joint = entityPatch.getArmature().searchJointById(jointId);
                           return joint == null ? null : new EM_Trail8(level, entityPatch, joint, animation, result);
                        }
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
