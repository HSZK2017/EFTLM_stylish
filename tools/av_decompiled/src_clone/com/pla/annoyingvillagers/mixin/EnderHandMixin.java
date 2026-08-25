package com.pla.annoyingvillagers.mixin;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import net.shelmarow.combat_evolution.ai.util.CEPatchUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reascer.wom.gameasset.animations.weapons.AnimsRuine;
import reascer.wom.particle.WOMParticles;
import reascer.wom.world.damagesources.WOMExtraDamageInstance;
import reascer.wom.world.entity.mob.EnderHand;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

@Mixin(
   value = {EnderHand.class},
   remap = true
)
public abstract class EnderHandMixin {
   @Inject(
      method = {"customServerAiStep"},
      at = {@At("TAIL")},
      cancellable = true
   )
   private void makeEnderHandCanDamagePlayer(CallbackInfo ci) {
      EnderHand self = (EnderHand)this;
      if (self.f_19797_ == 20
         && self.m_21805_() != null
         && self.m_9236_() instanceof ServerLevel serverLevel
         && serverLevel.m_8791_(self.m_21805_()) instanceof LivingEntity owner
         && !(owner instanceof Player)
         && self.m_5448_() != null
         && self.m_5448_().m_6084_()) {
         LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
         if (livingEntityPatch != null) {
            LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
            LivingEntity entity = self.m_5448_();
            EpicFightDamageSource damageSource = new EpicFightDamageSource(
               livingEntityPatch.getDamageSource(AnimsRuine.RUINE_PLUNDER, InteractionHand.MAIN_HAND)
            );
            damageSource.setBaseImpact(4.0F);
            damageSource.setStunType(StunType.HOLD);
            damageSource.attachDamageModifier(ValueModifier.multiplier(2.6F));
            damageSource.addExtraDamage(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(new float[]{0.8F}));
            entity.m_6469_(damageSource, (float)livingEntity.m_21133_(Attributes.f_22281_));
            if (entity.m_21023_(MobEffects.f_19597_)) {
               entity.m_21195_(MobEffects.f_19597_);
               entity.m_7292_(
                  new MobEffectInstance(MobEffects.f_19597_, (12 + 4 * EnchantmentHelper.m_44836_(Enchantments.f_44983_, livingEntity)) * 20, 0, false, true)
               );
            } else {
               entity.m_7292_(
                  new MobEffectInstance(MobEffects.f_19597_, (9 + 3 * EnchantmentHelper.m_44836_(Enchantments.f_44983_, livingEntity)) * 20, 0, false, true)
               );
            }

            if (entity.m_21023_(MobEffects.f_19599_)) {
               entity.m_21195_(MobEffects.f_19599_);
               entity.m_7292_(
                  new MobEffectInstance(MobEffects.f_19599_, (12 + 4 * EnchantmentHelper.m_44836_(Enchantments.f_44983_, livingEntity)) * 20, 0, false, true)
               );
            } else {
               entity.m_7292_(
                  new MobEffectInstance(MobEffects.f_19599_, (9 + 3 * EnchantmentHelper.m_44836_(Enchantments.f_44983_, livingEntity)) * 20, 0, false, true)
               );
            }

            if (livingEntityPatch instanceof CEHumanoidPatch<?> ceHumanoidPatch) {
               CEPatchUtils.setStamina(ceHumanoidPatch, CEPatchUtils.getStamina(ceHumanoidPatch) + CEPatchUtils.getMaxStamina(ceHumanoidPatch) * 0.05F);
            }

            ((LivingEntity)livingEntityPatch.getOriginal()).m_5634_((float)(1 + EnchantmentHelper.m_44836_(Enchantments.f_44983_, livingEntity)));
            serverLevel.m_8767_(
               (SimpleParticleType)WOMParticles.ENDERBLASTER_BULLET.get(),
               entity.m_20185_(),
               entity.m_20186_() + 1.2F,
               entity.m_20189_(),
               1,
               0.0,
               0.0,
               0.0,
               0.0
            );
            serverLevel.m_6263_(null, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), SoundEvents.f_11897_, livingEntity.m_5720_(), 1.0F, 0.5F);
         }
      }
   }
}
