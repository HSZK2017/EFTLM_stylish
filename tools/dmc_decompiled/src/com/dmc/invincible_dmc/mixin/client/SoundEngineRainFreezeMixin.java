package com.dmc.invincible_dmc.mixin.client;

import com.dmc.invincible_dmc.client.render.weather.JudgementCutRainFreezeController;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.audio.Channel;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.ChannelAccess.ChannelHandle;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SoundEngine.class})
public abstract class SoundEngineRainFreezeMixin {
   @Shadow
   @Final
   private Map<SoundInstance, ChannelHandle> f_120226_;
   @Unique
   private boolean invincibleDmc$environmentSoundsFrozen;

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void invincibleDmc$updateEnvironmentSoundFreeze(boolean paused, CallbackInfo ci) {
      boolean shouldFreeze = this.invincibleDmc$shouldFreezeEnvironmentSounds();
      if (shouldFreeze != this.invincibleDmc$environmentSoundsFrozen) {
         this.invincibleDmc$environmentSoundsFrozen = shouldFreeze;
         this.invincibleDmc$setEnvironmentChannelsPaused(shouldFreeze);
      }
   }

   @Inject(
      method = {"play"},
      at = {@At("RETURN")}
   )
   private void invincibleDmc$pauseNewEnvironmentChannel(SoundInstance sound, CallbackInfo ci) {
      if (this.invincibleDmc$environmentSoundsFrozen) {
         this.invincibleDmc$setEnvironmentChannelsPaused(true);
      }
   }

   @WrapOperation(
      method = {"tickNonPaused"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/resources/sounds/TickableSoundInstance;tick()V"
      )}
   )
   private void invincibleDmc$freezeEnvironmentSoundTick(TickableSoundInstance sound, Operation<Void> original) {
      if (!this.invincibleDmc$environmentSoundsFrozen || !invincibleDmc$isEnvironmentSound(sound)) {
         original.call(new Object[]{sound});
      }
   }

   @Unique
   private boolean invincibleDmc$shouldFreezeEnvironmentSounds() {
      Minecraft minecraft = Minecraft.m_91087_();
      ClientLevel level = minecraft.f_91073_;
      return level != null && JudgementCutRainFreezeController.shouldFreeze(level, minecraft.f_91063_.m_109153_().m_90583_());
   }

   @Unique
   private void invincibleDmc$setEnvironmentChannelsPaused(boolean paused) {
      this.f_120226_.forEach((sound, channel) -> {
         if (invincibleDmc$isEnvironmentSound(sound)) {
            channel.m_120154_(paused ? Channel::m_83677_ : Channel::m_83678_);
         }
      });
   }

   @Unique
   private static boolean invincibleDmc$isEnvironmentSound(SoundInstance sound) {
      SoundSource source = sound.m_8070_();
      return source == SoundSource.WEATHER
         || source == SoundSource.AMBIENT
         || source == SoundSource.MUSIC
         || source == SoundSource.HOSTILE
         || source == SoundSource.BLOCKS;
   }
}
