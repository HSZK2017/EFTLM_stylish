package com.dmc.invincible_dmc.client.particles.trail;

import com.dmc.invincible_dmc.client.render.IDRenderType;
import com.dmc.invincible_dmc.client.render.custom.BloomParticleRenderType;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordPatch;
import com.dmc.invincible_dmc.gameassets.animations.yamato.SummonedSwordAnimations;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.particle.AnimationTrailParticle;
import yesman.epicfight.client.particle.AbstractTrailParticle.TrailEdge;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

@OnlyIn(Dist.CLIENT)
public class SummonedSwordTrailParticle extends AnimationTrailParticle {
   @Nullable
   private final DMCSummonedSwordEntity swordRef;
   @Nullable
   private final TrailInfo launchTrailInfo;
   private final boolean flightTrail;
   private int lastMotionEpoch;
   private boolean motionWasReady;
   private boolean launchTrailSpawned;

   protected SummonedSwordTrailParticle(
      ClientLevel level,
      LivingEntityPatch<?> owner,
      Joint joint,
      AssetAccessor<? extends StaticAnimation> animation,
      TrailInfo trailInfo,
      @Nullable DMCSummonedSwordEntity swordRef,
      @Nullable TrailInfo launchTrailInfo,
      boolean flightTrail
   ) {
      super(level, owner, joint, animation, trailInfo);
      this.swordRef = swordRef;
      this.launchTrailInfo = launchTrailInfo;
      this.flightTrail = flightTrail;
      if (swordRef != null) {
         this.lastMotionEpoch = swordRef.getMotionEpoch();
         this.motionWasReady = swordRef.isManagedMotionReady();
      }
   }

   protected void createNextCurve() {
      if (this.swordRef != null) {
         int currentEpoch = this.swordRef.getMotionEpoch();
         boolean currentReady = this.swordRef.isManagedMotionReady();
         if (currentEpoch != this.lastMotionEpoch || !this.motionWasReady && currentReady) {
            this.resetTrailHistoryAtCurrentPose();
            this.lastMotionEpoch = currentEpoch;
            this.motionWasReady = currentReady;
            return;
         }

         this.motionWasReady = currentReady;
      }

      super.createNextCurve();
   }

   private void resetTrailHistoryAtCurrentPose() {
      Pose currentPose = ((LivingEntityPatch)this.owner).getAnimator().getPose(1.0F);
      Vec3 currentPosition = ((LivingEntity)((LivingEntityPatch)this.owner).getOriginal()).m_20318_(1.0F);
      OpenMatrix4f modelTransform = OpenMatrix4f.createTranslation(
            (float)currentPosition.f_82479_, (float)currentPosition.f_82480_, (float)currentPosition.f_82481_
         )
         .rotateDeg(180.0F, Vec3f.Y_AXIS)
         .mulBack(((LivingEntityPatch)this.owner).getModelMatrix(1.0F));
      OpenMatrix4f jointTransform = ((LivingEntityPatch)this.owner).getArmature().getBoundTransformFor(currentPose, this.joint).mulFront(modelTransform);
      Vec3 start = OpenMatrix4f.transform(jointTransform, this.trailInfo.start());
      Vec3 end = OpenMatrix4f.transform(jointTransform, this.trailInfo.end());
      this.trailEdges.clear();
      this.invisibleTrailEdges.clear();

      for (int i = 0; i < 3; i++) {
         this.invisibleTrailEdges.add(new TrailEdge(start, end, this.trailInfo.trailLifetime()));
      }

      this.startEdgeCorrection = 0.0F;
   }

   public boolean shouldCull() {
      return Minecraft.m_91087_().f_91074_ != null && Minecraft.m_91087_().f_91074_.m_20275_(this.f_107212_, this.f_107213_, this.f_107214_) > 1024.0;
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return IDRenderType.getBloomTrailRT(this.trailInfo.texturePath());
   }

   public void m_5744_(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
      if (!this.trailEdges.isEmpty()) {
         super.m_5744_(vertexConsumer, camera, partialTick);
         BloomParticleRenderType.markBloomDrawn();
         IDRenderType.getBloomTrailRT(this.trailInfo.texturePath()).callPipeline();
      }
   }

   protected boolean canContinue() {
      if (!((LivingEntity)((LivingEntityPatch)this.owner).getOriginal()).m_6084_()) {
         return false;
      } else if (this.swordRef != null && this.swordRef.m_213877_()) {
         return false;
      } else if (!this.flightTrail) {
         if (usesFormationTrail(this.swordRef) && !this.swordRef.isInStandby()) {
            this.spawnLaunchTrail();
            return false;
         } else {
            return super.canContinue();
         }
      } else {
         return this.swordRef != null && !this.swordRef.isInStandby();
      }
   }

   private void spawnLaunchTrail() {
      if (!this.launchTrailSpawned && this.launchTrailInfo != null && this.swordRef != null) {
         this.launchTrailSpawned = true;
         this.trailEdges.clear();
         this.invisibleTrailEdges.clear();
         Minecraft.m_91087_()
            .f_91061_
            .m_107344_(
               new SummonedSwordTrailParticle(
                  this.f_107208_, (LivingEntityPatch<?>)this.owner, this.joint, this.animation, this.launchTrailInfo, this.swordRef, null, true
               )
            );
      }
   }

   private static boolean usesFormationTrail(@Nullable DMCSummonedSwordEntity sword) {
      return sword != null && (sword.isStorm() || sword.isSpiral());
   }

   protected boolean canCreateNextCurve() {
      if (this.swordRef != null) {
         if (this.swordRef.isManagedMotionBound() && !this.swordRef.isManagedMotionReady()) {
            return false;
         }

         if (this.swordRef.isStuckInBlock()) {
            return false;
         }

         if (this.swordRef.isInStandby() && this.swordRef.isHeavyRain()) {
            return false;
         }

         if (!this.flightTrail && usesFormationTrail(this.swordRef) && !this.swordRef.isInStandby()) {
            return false;
         }
      }

      return !this.flightTrail ? super.canCreateNextCurve() : this.f_107224_ % this.trailInfo.updateInterval() == 0 && !this.f_107220_;
   }

   @Nullable
   private static TrailInfo resolveTrailInfo(AssetAccessor<? extends StaticAnimation> animation, int index, LivingEntityPatch<?> entitypatch) {
      Optional<List<TrailInfo>> trailInfos = ((StaticAnimation)animation.get()).getProperty(ClientAnimationProperties.TRAIL_EFFECT);
      if (!trailInfos.isEmpty() && index >= 0 && index < trailInfos.get().size()) {
         TrailInfo result = trailInfos.get().get(index);
         if (result.hand() != null) {
            ItemStack stack = ((LivingEntity)entitypatch.getOriginal()).m_21120_(result.hand());
            RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(stack);
            if (renderItemBase != null && renderItemBase.trailInfo() != null) {
               result = renderItemBase.trailInfo().overwrite(result);
            }
         }

         return entitypatch.getEntityDecorations()
            .getModifiedTrailInfo(result, result.hand() == null ? CapabilityItem.EMPTY : entitypatch.getAdvancedHoldingItemCapability(result.hand()));
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(
         @NotNull SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         try {
            int eid = (int)Double.doubleToRawLongBits(x);
            int animid = (int)Double.doubleToRawLongBits(z);
            int jointId = (int)Double.doubleToRawLongBits(xSpeed);
            int idx = (int)Double.doubleToRawLongBits(ySpeed);
            Entity entity = level.m_6815_(eid);
            if (entity == null) {
               return null;
            }

            if (Minecraft.m_91087_().f_91074_ != null && Minecraft.m_91087_().f_91074_.m_20280_(entity) > 4096.0) {
               return null;
            }

            LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
            if (entitypatch == null) {
               return null;
            }

            AnimationAccessor<? extends StaticAnimation> animation = AnimationManager.byId(animid);
            if (animation == null) {
               return null;
            }

            DMCSummonedSwordEntity swordEntity = null;
            if (entitypatch instanceof DMCSummonedSwordPatch<?> swordPatch) {
               swordEntity = (DMCSummonedSwordEntity)swordPatch.getOriginal();
            }

            TrailInfo result = SummonedSwordTrailParticle.resolveTrailInfo(animation, idx, entitypatch);
            if (result == null) {
               return null;
            }

            TrailInfo launchTrailInfo = null;
            boolean flightTrail = false;
            if (SummonedSwordTrailParticle.usesFormationTrail(swordEntity)) {
               launchTrailInfo = SummonedSwordTrailParticle.resolveTrailInfo(SummonedSwordAnimations.SUMMONED_SWORD, idx, entitypatch);
               if (launchTrailInfo != null && !launchTrailInfo.playable()) {
                  launchTrailInfo = null;
               }

               if (!swordEntity.isInStandby() && launchTrailInfo != null) {
                  result = launchTrailInfo;
                  launchTrailInfo = null;
                  flightTrail = true;
               }
            }

            if (result.playable()) {
               Joint joint = entitypatch.getArmature().searchJointById(jointId);
               if (joint == null) {
                  return null;
               }

               return new SummonedSwordTrailParticle(level, entitypatch, joint, animation, result, swordEntity, launchTrailInfo, flightTrail);
            }
         } catch (Exception var27) {
         }

         return null;
      }
   }
}
