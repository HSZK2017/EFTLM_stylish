package com.pla.annoyingvillagers.clazz;

import com.pla.annoyingvillagers.util.EpicfightUtil;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public interface BurstProtectEntity {
   float getRecentDamageTaken();

   void setRecentDamageTaken(float var1);

   int getRecentHitCounter();

   void setRecentHitCounter(int var1);

   default float getBurstProtectCapRatio() {
      return 0.2F;
   }

   default float getBurstProtectMinDamage() {
      return 0.3F;
   }

   default void tickBurstProtectionDecay(LivingEntity self) {
      if (this.getRecentDamageTaken() > 0.0F) {
         this.setRecentDamageTaken(Mth.m_14121_(this.getRecentDamageTaken(), 0.0F, self.m_21233_() * 0.07F / 160.0F));
      }

      if (self.f_19797_ % 4 == 0 && this.getRecentHitCounter() > 0) {
         this.setRecentHitCounter(Mth.m_14045_(this.getRecentHitCounter() - 1, 0, 5));
      }
   }

   default boolean shouldIgnoreBurstProtection(LivingEntity self, DamageSource source) {
      LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(self, LivingEntityPatch.class);
      if (patch == null) {
         return false;
      } else {
         AnimationPlayer player = patch.getAnimator().getPlayerFor(null);
         if (player == null) {
            return false;
         } else {
            AssetAccessor<? extends StaticAnimation> anim = player.getRealAnimation();
            return EpicfightUtil.isDamagableHitAnimation(anim, patch);
         }
      }
   }

   default float applyBurstProtection(LivingEntity self, DamageSource source, float damage) {
      if (this.shouldIgnoreBurstProtection(self, source)) {
         return damage;
      } else if (damage <= 0.0F) {
         return 0.0F;
      } else if (source.m_269533_(DamageTypeTags.f_268738_)) {
         return damage;
      } else {
         float cap = self.m_21233_() * this.getBurstProtectCapRatio();
         damage = Mth.m_14036_(damage, 0.0F, cap);
         float damageScale = 1.0F - Mth.m_14036_(this.getRecentDamageTaken() / (self.m_21233_() * 0.07F), 0.0F, 0.9F);
         float hitScale = 1.0F - Mth.m_14036_((float)this.getRecentHitCounter() / 5.0F, 0.0F, 0.9F);
         damage *= damageScale;
         if (this.getRecentHitCounter() >= 5) {
            damage = this.getBurstProtectMinDamage();
         } else {
            damage *= hitScale;
         }

         this.setRecentHitCounter(this.getRecentHitCounter() + 1);
         this.setRecentDamageTaken(this.getRecentDamageTaken() + damage);
         return damage;
      }
   }
}
