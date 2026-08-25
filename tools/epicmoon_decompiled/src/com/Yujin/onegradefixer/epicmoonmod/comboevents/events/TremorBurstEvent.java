package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.Yujin.onegradefixer.epicmoonmod.effect.EMeffects;
import com.Yujin.onegradefixer.epicmoonmod.sound.EMsounds;
import com.p1nero.invincible.api.events.HitEvent;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class TremorBurstEvent {
   public static final ResourceLocation a = new ResourceLocation("epicmoonmod", "burst");

   public static HitEvent tremorburstevent() {
      return new HitEvent(
         0,
         (entityPatch, entity, invinciblePlayer) -> {
            if (entity instanceof LivingEntity) {
               MobEffectInstance effect = ((LivingEntity)entity).m_21124_((MobEffect)EMeffects.TREMOR.get());
               MobEffectInstance effect2 = ((LivingEntity)entity).m_21124_((MobEffect)EMeffects.TREMOR_SCORCH.get());
               if (effect2 != null) {
                  ((Player)entityPatch.getOriginal())
                     .m_9236_()
                     .m_6263_(
                        null, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), (SoundEvent)EMsounds.TREMORBURST.get(), SoundSource.AMBIENT, 1.0F, 2.0F
                     );
                  if (entity.m_6060_()) {
                     ParticleEmitterInfo info = ParticleEmitterInfo.create(entity.m_9236_(), a)
                        .position(entity.m_20185_(), entity.m_20186_(), entity.m_20189_());
                     AAALevel.addParticle(entity.m_9236_(), true, info.rotation(0.0F, -((float)Math.toRadians((double)entity.m_6080_())), 0.0F));
                     Player player = (Player)entityPatch.getOriginal();
                     int duration = effect2.m_19557_();
                     ((LivingEntity)entity).m_7292_(new MobEffectInstance(MobEffects.f_19597_, 30, 100, true, true));
                     ((LivingEntity)entity).m_7292_(new MobEffectInstance(MobEffects.f_19604_, 30, 2, true, true));
                     ((LivingEntity)entity).m_6469_(entity.m_269291_().m_269549_(), 10.0F);
                     ((LivingEntity)entity).m_7292_(new MobEffectInstance((MobEffect)EMeffects.TREMOR_SCORCH.get(), duration - 50, 0, true, true));
                  } else {
                     ParticleEmitterInfo info = ParticleEmitterInfo.create(entity.m_9236_(), a)
                        .position(entity.m_20185_(), entity.m_20186_(), entity.m_20189_());
                     AAALevel.addParticle(entity.m_9236_(), true, info.rotation(0.0F, -((float)Math.toRadians((double)entity.m_6080_())), 0.0F));
                     Player player = (Player)entityPatch.getOriginal();
                     int duration = effect2.m_19557_();
                     ((LivingEntity)entity).m_7292_(new MobEffectInstance(MobEffects.f_19597_, 30, 100, true, true));
                     ((LivingEntity)entity).m_7292_(new MobEffectInstance(MobEffects.f_19604_, 30, 2, true, true));
                     ((LivingEntity)entity).m_6469_(entity.m_269291_().m_269549_(), 5.0F);
                     ((LivingEntity)entity).m_7292_(new MobEffectInstance((MobEffect)EMeffects.TREMOR_SCORCH.get(), duration - 50, 0, true, true));
                  }
               } else if (effect != null) {
                  ((Player)entityPatch.getOriginal())
                     .m_9236_()
                     .m_6263_(
                        null, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), (SoundEvent)EMsounds.TREMORBURST.get(), SoundSource.AMBIENT, 1.0F, 2.0F
                     );
                  ParticleEmitterInfo info = ParticleEmitterInfo.create(entity.m_9236_(), a).position(entity.m_20185_(), entity.m_20186_(), entity.m_20189_());
                  AAALevel.addParticle(entity.m_9236_(), true, info.rotation(0.0F, -((float)Math.toRadians((double)entity.m_6080_())), 0.0F));
                  int duration = effect.m_19557_();
                  ((LivingEntity)entity).m_7292_(new MobEffectInstance(MobEffects.f_19597_, 30, 100, true, true));
                  ((LivingEntity)entity).m_7292_(new MobEffectInstance(MobEffects.f_19604_, 30, 2, true, true));
                  ((LivingEntity)entity).m_7292_(new MobEffectInstance((MobEffect)EMeffects.TREMOR.get(), duration - 50, 0, true, true));
               }
            }
         }
      );
   }
}
