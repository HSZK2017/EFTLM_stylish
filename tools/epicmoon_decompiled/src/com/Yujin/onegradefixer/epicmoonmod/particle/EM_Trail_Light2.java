package com.Yujin.onegradefixer.epicmoonmod.particle;

import com.Yujin.onegradefixer.epicmoonmod.renderer.EMparticlerendertype;
import java.util.List;
import java.util.Optional;
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

public class EM_Trail_Light2 extends AnimationTrailParticle {
   public ParticleRenderType m_7556_() {
      return EMparticlerendertype.TRAIL_EFFECT_ADDITIVE2.apply(this.trailInfo.texturePath());
   }

   public int m_6355_(float partialTick) {
      return 15728880;
   }

   protected EM_Trail_Light2(
      ClientLevel level, LivingEntityPatch<?> owner, Joint joint, AssetAccessor<? extends StaticAnimation> animation, TrailInfo trailInfo
   ) {
      super(level, owner, joint, animation, trailInfo);
   }

   protected void createNextCurve() {
      TrailInfo trailInfo = this.trailInfo;
      AnimationPlayer animPlayer = ((LivingEntityPatch)this.owner).getAnimator().getPlayerFor(this.animation);
      if (!((DynamicAnimation)animPlayer.getAnimation().get()).isLinkAnimation() && !(animPlayer.getElapsedTime() <= this.trailInfo.startTime())) {
         boolean var11 = false;
      } else {
         boolean var10000 = true;
      }

      Pose prevPose = ((LivingEntityPatch)this.owner).getAnimator().getPose(0.0F);
      Vec3 posOld = ((LivingEntity)((LivingEntityPatch)this.owner).getOriginal()).m_20318_(0.0F);
      OpenMatrix4f prevModelMatrix = ((LivingEntityPatch)this.owner).getModelMatrix(0.0F);
      OpenMatrix4f prvmodelTf = OpenMatrix4f.createTranslation((float)posOld.f_82479_, (float)posOld.f_82480_, (float)posOld.f_82481_)
         .rotateDeg(180.0F, Vec3f.Y_AXIS)
         .mulBack(prevModelMatrix);
      OpenMatrix4f prevJointTf = ((LivingEntityPatch)this.owner).getArmature().getBoundTransformFor(prevPose, this.joint).mulFront(prvmodelTf);
      Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, trailInfo.start());
      Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, trailInfo.end());
      super.createNextCurve();
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         int eid = (int)Double.doubleToRawLongBits(x);
         int animid = (int)Double.doubleToRawLongBits(z);
         int jointId = (int)Double.doubleToRawLongBits(xSpeed);
         int idx = (int)Double.doubleToRawLongBits(ySpeed);
         Entity entity = level.m_6815_(eid);
         if (entity == null) {
            return null;
         } else {
            LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
            if (entitypatch == null) {
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
                        ItemStack stack = ((LivingEntity)entitypatch.getOriginal()).m_21120_(result.hand());
                        RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(stack);
                        if (renderItemBase != null && renderItemBase.trailInfo() != null) {
                           result = renderItemBase.trailInfo().overwrite(result);
                        }
                     }

                     result = entitypatch.getEntityDecorations()
                        .getModifiedTrailInfo(
                           result, result.hand() == null ? CapabilityItem.EMPTY : entitypatch.getAdvancedHoldingItemCapability(result.hand())
                        );
                     return result.playable()
                        ? new EM_Trail_Light2(level, entitypatch, entitypatch.getArmature().searchJointById(jointId), animation, result)
                        : null;
                  }
               }
            }
         }
      }
   }
}
