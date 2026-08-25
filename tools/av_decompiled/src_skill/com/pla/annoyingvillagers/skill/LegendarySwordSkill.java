package com.pla.annoyingvillagers.skill;

import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.task.DelayedTask;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import reascer.wom.gameasset.animations.weapons.AnimsAgony;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.effect.EpicFightMobEffects;

public class LegendarySwordSkill extends WeaponInnateSkill {
   public LegendarySwordSkill(SkillBuilder<? extends WeaponInnateSkill> builder) {
      super(builder);
   }

   public void executeOnServer(final SkillContainer skillContainer, FriendlyByteBuf friendlyByteBuf) {
      final LivingEntity entity = (LivingEntity)skillContainer.getExecutor().getOriginal();
      final ServerLevel serverLevel = (ServerLevel)entity.m_9236_();
      skillContainer.getExecutor().playAnimationSynchronized(AnimsAgony.AGONY_RISING_EAGLE, 0.0F);
      entity.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 60, 2));
      new DelayedTask(10) {
         public void run() {
            serverLevel.m_6263_(
               null,
               entity.m_20185_(),
               entity.m_20186_(),
               entity.m_20189_(),
               (SoundEvent)AnnoyingVillagersModSounds.HEAVY_ATTACK_START.get(),
               SoundSource.NEUTRAL,
               1.0F,
               1.0F
            );
            serverLevel.m_6263_(
               null,
               entity.m_20185_(),
               entity.m_20186_(),
               entity.m_20189_(),
               (SoundEvent)AnnoyingVillagersModSounds.HEAVY_ATTACK_LEGENDARY_SWORD.get(),
               SoundSource.NEUTRAL,
               1.0F,
               1.0F
            );
            serverLevel.m_6263_(
               null,
               entity.m_20185_(),
               entity.m_20186_(),
               entity.m_20189_(),
               (SoundEvent)AnnoyingVillagersModSounds.HEAVY_ATTACK_LEGENDARY_SWORD_2.get(),
               SoundSource.NEUTRAL,
               1.0F,
               1.0F
            );
            serverLevel.m_8767_(ParticleTypes.f_123767_, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), 15, 0.0, 0.0, 0.0, 0.2);
            serverLevel.m_8767_(ParticleTypes.f_123767_, entity.m_20185_(), entity.m_20188_(), entity.m_20189_(), 100, 0.0, 0.0, 0.0, 0.5);
            skillContainer.getExecutor().playAnimationSynchronized(AnimsPugilistSteve.LEGENDARY_SWORD_HEAVY_ATTACK, 0.0F);
         }
      };
      super.executeOnServer(skillContainer, friendlyByteBuf);
   }
}
