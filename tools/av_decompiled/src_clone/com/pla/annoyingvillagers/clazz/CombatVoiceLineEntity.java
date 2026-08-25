package com.pla.annoyingvillagers.clazz;

import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

public interface CombatVoiceLineEntity {
   int getVoiceCooldown();

   void setVoiceCooldown(int var1);

   default int getMinVoiceCooldown() {
      return 300;
   }

   default int getMaxVoiceCooldown() {
      return 600;
   }

   default void tickVoiceCooldown() {
      if (this.getVoiceCooldown() > 0) {
         this.setVoiceCooldown(this.getVoiceCooldown() - 1);
      }
   }

   default void resetVoiceCooldown(Mob self) {
      int min = Math.min(this.getMinVoiceCooldown(), this.getMaxVoiceCooldown());
      int max = Math.max(this.getMinVoiceCooldown(), this.getMaxVoiceCooldown());
      this.setVoiceCooldown(Mth.m_216271_(self.m_217043_(), min, max));
   }

   default boolean hasValidVoiceTarget(Mob self) {
      LivingEntity target = self.m_5448_();
      return target != null && target.m_6084_();
   }

   default boolean canPlayVoiceLine(Mob self) {
      return !self.m_9236_().f_46443_ && self.m_6084_() && this.getVoiceCooldown() <= 0 && this.hasValidVoiceTarget(self);
   }

   @Nullable
   default SoundEvent getHurtVoiceSound() {
      return null;
   }

   @Nullable
   default SoundEvent getAttackVoiceSound() {
      return null;
   }

   default boolean canSayHurtSound(Mob self, DamageSource source) {
      return this.canPlayVoiceLine(self);
   }

   default boolean canSayAttackSound(Mob self, Entity target) {
      return this.canPlayVoiceLine(self);
   }

   default void sayHurtSound(Mob self, DamageSource source) {
      if (self.m_9236_() instanceof ServerLevel serverLevel) {
         if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
            SoundEvent sound = this.getHurtVoiceSound();
            if (sound != null) {
               if (this.canSayHurtSound(self, source)) {
                  serverLevel.m_6263_(null, self.m_20185_(), self.m_20186_(), self.m_20189_(), sound, SoundSource.HOSTILE, 0.5F, 1.0F);
                  this.resetVoiceCooldown(self);
               }
            }
         }
      }
   }

   default void sayAttackSound(Mob self, Entity target) {
      if (self.m_9236_() instanceof ServerLevel serverLevel) {
         if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
            SoundEvent sound = this.getAttackVoiceSound();
            if (sound != null) {
               if (this.canSayAttackSound(self, target)) {
                  serverLevel.m_6263_(null, self.m_20185_(), self.m_20186_(), self.m_20189_(), sound, SoundSource.HOSTILE, 0.5F, 1.0F);
                  this.resetVoiceCooldown(self);
               }
            }
         }
      }
   }
}
