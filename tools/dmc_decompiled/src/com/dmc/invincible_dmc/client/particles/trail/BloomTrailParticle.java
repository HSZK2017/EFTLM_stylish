package com.dmc.invincible_dmc.client.particles.trail;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.effeks.EffekConfig;
import com.dmc.invincible_dmc.client.render.IDRenderType;
import com.dmc.invincible_dmc.client.render.custom.BloomParticleRenderType;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.particle.AnimationTrailParticle;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

@OnlyIn(Dist.CLIENT)
public class BloomTrailParticle extends AnimationTrailParticle {
   protected BloomTrailParticle(
      ClientLevel level, LivingEntityPatch<?> owner, Joint joint, AssetAccessor<? extends StaticAnimation> animation, TrailInfo trailInfo
   ) {
      super(level, owner, joint, animation, trailInfo);
   }

   public boolean shouldCull() {
      return false;
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return IDRenderType.getBloomTrailRT(this.trailInfo.texturePath());
   }

   public void m_5744_(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
      boolean enabled = EffekConfig.isEnabled("yamato_bloom_trail", DMConfig.YAMATO_BLOOM_TRAIL);
      if (enabled && !this.trailEdges.isEmpty()) {
         BloomParticleRenderType.markBloomDrawn();
         super.m_5744_(vertexConsumer, camera, partialTick);
         if (PostEffectPipelines.isActive()) {
            IDRenderType.getBloomTrailRT(this.trailInfo.texturePath()).callPipeline();
         }
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

            Optional<List<TrailInfo>> trailInfo = ((StaticAnimation)animation.get()).getProperty(ClientAnimationProperties.TRAIL_EFFECT);
            if (trailInfo.isEmpty() || idx >= trailInfo.get().size()) {
               return null;
            }

            TrailInfo result = trailInfo.get().get(idx);
            if (result.hand() != null) {
               ItemStack stack = ((LivingEntity)entitypatch.getOriginal()).m_21120_(result.hand());
               RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(stack);
               if (renderItemBase != null && renderItemBase.trailInfo() != null) {
                  result = renderItemBase.trailInfo().overwrite(result);
               }
            }

            result = entitypatch.getEntityDecorations()
               .getModifiedTrailInfo(result, result.hand() == null ? CapabilityItem.EMPTY : entitypatch.getAdvancedHoldingItemCapability(result.hand()));
            if (result.playable()) {
               Joint joint = entitypatch.getArmature().searchJointById(jointId);
               if (joint == null) {
                  return null;
               }

               return new BloomTrailParticle(level, entitypatch, joint, animation, result);
            }
         } catch (Exception var26) {
         }

         return null;
      }
   }
}
