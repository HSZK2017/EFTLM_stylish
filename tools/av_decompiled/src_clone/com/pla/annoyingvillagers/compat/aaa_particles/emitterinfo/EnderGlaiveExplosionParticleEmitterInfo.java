package com.pla.annoyingvillagers.compat.aaa_particles.emitterinfo;

import mod.chloeprime.aaaparticles.api.client.EffectRegistry;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.DynamicParameter;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EnderGlaiveExplosionParticleEmitterInfo extends ParticleEmitterInfo {
   private EnderGlaiveExplosionParticleEmitterInfo.ForwardAxis axis = EnderGlaiveExplosionParticleEmitterInfo.ForwardAxis.PLUS_Z;
   private float roll = 0.0F;
   private Vec3 from = null;
   private Vec3 to = null;

   public EnderGlaiveExplosionParticleEmitterInfo(ResourceLocation resourceLocation) {
      super(resourceLocation);
   }

   public EnderGlaiveExplosionParticleEmitterInfo fromTo(
      Vec3 from, Vec3 to, EnderGlaiveExplosionParticleEmitterInfo.ForwardAxis axis, float extraRollRad, boolean flip
   ) {
      this.from = from;
      this.to = to;
      this.axis = axis;
      this.roll = extraRollRad;
      return this;
   }

   private static void aim(ParticleEmitter em, Vec3 from, Vec3 to, EnderGlaiveExplosionParticleEmitterInfo.ForwardAxis axis, float roll, boolean flip) {
      Vec3 d = to.m_82546_(from);
      if (flip) {
         d = d.m_82490_(-1.0);
      }

      double xz = Math.sqrt(d.f_82479_ * d.f_82479_ + d.f_82481_ * d.f_82481_);
      switch (axis) {
         case PLUS_Z: {
            float yaw = (float)Math.atan2(d.f_82479_, d.f_82481_);
            float pitch = (float)(-Math.atan2(d.f_82480_, xz));
            em.setRotation(pitch, yaw, roll);
            break;
         }
         case PLUS_Y: {
            float yaw = (float)Math.atan2(d.f_82481_, d.f_82479_) + (float) (Math.PI / 2);
            float pitch = (float)Math.atan2(xz, d.f_82480_) - (float) (Math.PI / 2);
            em.setRotation(pitch, yaw, roll);
         }
      }
   }

   public void spawnInWorld(Level level, Player player) {
      if (!NativePlatform.isRunningOnUnsupportedPlatform()) {
         if (this.from != null && this.to != null) {
            EffectRegistry.load(this.effek).thenAccept(effek -> {
               ParticleEmitter em = this.hasEmitter() ? effek.play(this.emitter) : effek.play();
               if (this.hasParameters()) {
                  for (DynamicParameter p : this.parameters) {
                     em.setDynamicInput(p.index(), p.value());
                  }
               }

               if (this.hasTriggers()) {
                  this.triggers.forEach(em::sendTrigger);
               }

               em.setPosition((float)this.from.f_82479_, (float)this.from.f_82480_, (float)this.from.f_82481_);
               aim(em, this.from, this.to, this.axis, this.roll);
            });
         }
      }
   }

   private static void aim(ParticleEmitter em, Vec3 from, Vec3 to, EnderGlaiveExplosionParticleEmitterInfo.ForwardAxis axis, float roll) {
      Vec3 d = to.m_82546_(from);
      double xz = Math.sqrt(d.f_82479_ * d.f_82479_ + d.f_82481_ * d.f_82481_);
      switch (axis) {
         case PLUS_Z: {
            float yaw = (float)Math.atan2(d.f_82479_, d.f_82481_);
            float pitch = (float)(-Math.atan2(d.f_82480_, xz));
            em.setRotation(pitch, yaw, roll);
            break;
         }
         case PLUS_Y: {
            float yaw = (float)Math.atan2(d.f_82481_, d.f_82479_) + (float) (Math.PI / 2);
            float pitch = (float)Math.atan2(xz, d.f_82480_) - (float) (Math.PI / 2);
            em.setRotation(pitch, yaw, roll);
         }
      }
   }

   public static enum ForwardAxis {
      PLUS_Z,
      PLUS_Y;
   }
}
