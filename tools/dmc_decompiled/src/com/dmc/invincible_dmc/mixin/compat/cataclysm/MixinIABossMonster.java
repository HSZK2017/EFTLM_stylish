package com.dmc.invincible_dmc.mixin.compat.cataclysm;

import com.dmc.invincible_dmc.gameassets.DMCEffects;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({IABoss_monster.class})
public abstract class MixinIABossMonster {
   @Inject(
      method = {"canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void dmc$allowYamatoStunEffect(MobEffectInstance p_34192_, CallbackInfoReturnable<Boolean> cir) {
      if (p_34192_.m_19544_() == DMCEffects.STOP.get() || p_34192_.m_19544_() == DMCEffects.SLOW.get()) {
         cir.setReturnValue(true);
         cir.cancel();
      }
   }
}
