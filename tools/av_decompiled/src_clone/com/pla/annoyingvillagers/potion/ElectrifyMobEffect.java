package com.pla.annoyingvillagers.potion;

import com.pla.annoyingvillagers.gameasset.AnimsTacticalImbuements;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.util.BlueDemonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.shelmarow.combat_evolution.execution.ExecutionHandler;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionAttackAnimation;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionHitAnimation;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ElectrifyMobEffect extends MobEffect {
   public ElectrifyMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -16711681);
   }

   @NotNull
   public String m_19481_() {
      return "effect.annoyingvillagers.electrify";
   }

   public void m_6742_(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
      super.m_6742_(pLivingEntity, pAmplifier);
      double d0 = pLivingEntity.m_20185_();
      double d1 = pLivingEntity.m_20186_();
      double d2 = pLivingEntity.m_20189_();
      if (pLivingEntity.f_19797_ % 20 == 0) {
         LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(pLivingEntity, LivingEntityPatch.class);
         if (livingEntityPatch != null) {
            AnimationPlayer animationPlayer = livingEntityPatch.getAnimator().getPlayerFor(null);
            if (animationPlayer != null) {
               AssetAccessor<? extends StaticAnimation> dynamicAnimation = animationPlayer.getRealAnimation();
               StaticAnimation currentAnimation = dynamicAnimation != null ? (StaticAnimation)dynamicAnimation.get() : null;
               if (dynamicAnimation != null
                  && currentAnimation != null
                  && !livingEntityPatch.isStunned()
                  && !ExecutionHandler.isTargetGuardBreak(dynamicAnimation, livingEntityPatch)
                  && !(currentAnimation instanceof ExecutionAttackAnimation)
                  && !(currentAnimation instanceof ExecutionHitAnimation)) {
                  playElectrifyAnimation(livingEntityPatch, pAmplifier > 1 ? AnimsTacticalImbuements.ZAP_LONG : AnimsTacticalImbuements.ZAP);
               }
            }
         }
      }

      if (Math.random() <= 0.1 && pLivingEntity.m_9236_() instanceof ServerLevel serverLevel) {
         BlueDemonUtil.spawnBlueDemonEffect(serverLevel, pLivingEntity);
         if (serverLevel.f_46441_.m_188500_() <= 0.8) {
            float volume = (float)Mth.m_216263_(serverLevel.f_46441_, 0.05, 0.5);
            float pitch = (float)Mth.m_216263_(serverLevel.f_46441_, 0.8, 1.1);
            serverLevel.m_5594_(
               null, BlockPos.m_274561_(d0, d1, d2), (SoundEvent)AnnoyingVillagersModSounds.ELECTRIFY.get(), SoundSource.NEUTRAL, volume, pitch
            );
         }
      }

      if (Math.random() <= (pAmplifier > 1 ? 1.0 : 0.1)) {
         pLivingEntity.m_6469_(pLivingEntity.m_9236_().m_269111_().m_269264_(), pAmplifier > 1 ? 5.0F : 0.2F);
      }
   }

   public boolean m_6584_(int i, int j) {
      return true;
   }

   private static void playElectrifyAnimation(LivingEntityPatch<?> livingEntityPatch, AssetAccessor<? extends StaticAnimation> animation) {
      if (animation != null && animation.get() != null) {
         livingEntityPatch.playAnimationSynchronized(animation, 0.0F);
      }
   }
}
