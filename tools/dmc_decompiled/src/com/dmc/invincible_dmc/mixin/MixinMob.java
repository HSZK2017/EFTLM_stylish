package com.dmc.invincible_dmc.mixin;

import com.dmc.invincible_dmc.gameassets.DMCEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Mob.class})
public abstract class MixinMob {
   @Inject(
      at = {@At("HEAD")},
      method = {"serverAiStep"},
      cancellable = true
   )
   private void serverAiStep(CallbackInfo info) {
      Mob self = (Mob)this;
      MobEffectInstance stopEffect = self.m_21124_((MobEffect)DMCEffects.STOP.get());
      MobEffectInstance slowEffect = self.m_21124_((MobEffect)DMCEffects.SLOW.get());
      if (stopEffect != null || slowEffect != null) {
         info.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"tickHeadTurn"},
      cancellable = true
   )
   private void dmc$tickHeadTurn(float p_21538_, float p_21539_, CallbackInfoReturnable<Float> callback) {
      Mob self = (Mob)this;
      MobEffectInstance stopEffect = self.m_21124_((MobEffect)DMCEffects.STOP.get());
      MobEffectInstance slowEffect = self.m_21124_((MobEffect)DMCEffects.SLOW.get());
      if (stopEffect != null || slowEffect != null) {
         callback.setReturnValue(0.0F);
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"createBodyControl"},
      cancellable = true
   )
   protected void dmc$createBodyControl(CallbackInfoReturnable<BodyRotationControl> cir) {
      Mob self = (Mob)this;
      MobEffectInstance stopEffect = self.m_21124_((MobEffect)DMCEffects.STOP.get());
      MobEffectInstance slowEffect = self.m_21124_((MobEffect)DMCEffects.SLOW.get());
      if (stopEffect != null || slowEffect != null) {
         cir.setReturnValue(null);
      }
   }
}
