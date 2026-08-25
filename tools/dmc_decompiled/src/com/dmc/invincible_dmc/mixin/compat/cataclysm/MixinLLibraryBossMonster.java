package com.dmc.invincible_dmc.mixin.compat.cataclysm;

import com.dmc.invincible_dmc.gameassets.DMCEffects;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.LLibrary_Boss_Monster;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LLibrary_Boss_Monster.class})
public abstract class MixinLLibraryBossMonster {
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
