package com.pla.annoyingvillagers.compat.aaa_particles.emitterinfo;

import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import java.lang.ref.WeakReference;
import mod.chloeprime.aaaparticles.api.client.EffectRegistry;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.DynamicParameter;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DragonBeamParticleEmitterInfo extends ParticleEmitterInfo {
   private WeakReference<HerobrineDragonEntity> casterRef = new WeakReference<>(null);
   private WeakReference<LivingEntity> targetRef = new WeakReference<>(null);
   private int durationTicks = 0;
   private DragonBeamParticleEmitterInfo.ForwardAxis axis = DragonBeamParticleEmitterInfo.ForwardAxis.PLUS_Z;
   private float roll = 0.0F;
   private Vec3 lastTargetPos = null;

   public DragonBeamParticleEmitterInfo(ResourceLocation effek) {
      super(effek);
   }

   public DragonBeamParticleEmitterInfo fromTo(Vec3 from, Vec3 to, DragonBeamParticleEmitterInfo.ForwardAxis axis, float extraRollRad) {
      this.position(from.f_82479_, from.f_82480_, from.f_82481_);
      this.axis = axis;
      this.roll = extraRollRad;
      this.lastTargetPos = to;
      Vec3 d = to.m_82546_(from);
      double xz = Math.sqrt(d.f_82479_ * d.f_82479_ + d.f_82481_ * d.f_82481_);
      switch (axis) {
         case PLUS_Z: {
            float yaw = (float)Math.atan2(d.f_82479_, d.f_82481_);
            float pitch = (float)(-Math.atan2(d.f_82480_, xz));
            this.rotation(pitch, yaw, extraRollRad);
            break;
         }
         case PLUS_Y: {
            float yaw = (float)Math.atan2(d.f_82481_, d.f_82479_) + (float) (Math.PI / 2);
            float pitch = (float)Math.atan2(xz, d.f_82480_) - (float) (Math.PI / 2);
            this.rotation(pitch, yaw, extraRollRad);
         }
      }

      return this;
   }

   public DragonBeamParticleEmitterInfo follow(
      HerobrineDragonEntity caster, LivingEntity target, int durationTicks, DragonBeamParticleEmitterInfo.ForwardAxis axis, float extraRollRad
   ) {
      this.casterRef = new WeakReference<>(caster);
      this.targetRef = new WeakReference<>(target);
      this.durationTicks = durationTicks;
      this.axis = axis;
      this.roll = extraRollRad;
      return this;
   }

   private static Vec3 eyeLerped(EnderDragonPart e, float partial) {
      double x = Mth.m_14139_((double)partial, e.f_19790_, e.m_20185_());
      double y = Mth.m_14139_((double)partial, e.f_19791_, e.m_20186_()) + (double)e.m_20192_();
      double z = Mth.m_14139_((double)partial, e.f_19792_, e.m_20189_());
      return new Vec3(x, y, z);
   }

   private static Vec3 eyeLerped(LivingEntity e, float partial) {
      double x = Mth.m_14139_((double)partial, e.f_19790_, e.m_20185_());
      double y = Mth.m_14139_((double)partial, e.f_19791_, e.m_20186_()) + (double)e.m_20192_();
      double z = Mth.m_14139_((double)partial, e.f_19792_, e.m_20189_());
      return new Vec3(x, y, z);
   }

   private static void aim(ParticleEmitter em, Vec3 from, Vec3 to, DragonBeamParticleEmitterInfo.ForwardAxis axis, float roll) {
      Vec3 d = to.m_82546_(from);
      double len = d.m_82553_();
      if (len < 1.0E-6) {
         len = 1.0E-6;
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

   private static Vec3 mouthLerped(HerobrineDragonEntity herobrineDragonEntity, float partial) {
      return herobrineDragonEntity.beamMouthPos(partial);
   }

   public void spawnInWorld(Level level, Player player) {
      if (!NativePlatform.isRunningOnUnsupportedPlatform()) {
         EffectRegistry.load(this.effek)
            .thenAccept(
               effek -> {
                  ParticleEmitter em = this.hasEmitter() ? effek.play(this.emitter) : effek.play();
                  if (this.hasParameters()) {
                     for (DynamicParameter p : this.parameters) {
                        em.setDynamicInput(p.index(), p.value());
                     }
                  }

                  if (this.hasTriggers()) {
                     this.triggers.forEach(em::sendTrigger);
                  }

                  HerobrineDragonEntity caster0 = this.casterRef.get();
                  if (caster0 != null && caster0.m_6084_()) {
                     int startTick = caster0.f_19797_;
                     Vec3 from0 = mouthLerped(caster0, 1.0F);
                     LivingEntity t0 = this.targetRef.get();
                     Vec3 to0 = t0 != null && t0.m_6084_()
                        ? new Vec3(t0.m_20185_(), t0.m_20188_(), t0.m_20189_())
                        : (this.lastTargetPos != null ? this.lastTargetPos : from0.m_82549_(caster0.m_20154_()));
                     this.lastTargetPos = to0;
                     em.setPosition((float)from0.f_82479_, (float)from0.f_82480_, (float)from0.f_82481_);
                     aim(em, from0, to0, this.axis, this.roll);
                     em.addPreDrawCallback((Emitter, partial) -> {
                        HerobrineDragonEntity c = this.casterRef.get();
                        if (c != null && c.m_6084_()) {
                           if (this.durationTicks > 0 && c.f_19797_ - startTick >= this.durationTicks) {
                              Emitter.stop();
                           } else {
                              Vec3 from = mouthLerped(c, partial);
                              LivingEntity t = this.targetRef.get();
                              Vec3 to;
                              if (t != null && t.m_6084_()) {
                                 to = eyeLerped(t, partial);
                                 this.lastTargetPos = to;
                              } else {
                                 to = this.lastTargetPos != null ? this.lastTargetPos : from.m_82549_(c.m_20154_());
                              }

                              Emitter.setPosition((float)from.f_82479_, (float)from.f_82480_, (float)from.f_82481_);
                              aim(Emitter, from, to, this.axis, this.roll);
                           }
                        } else {
                           Emitter.stop();
                        }
                     });
                  } else {
                     em.stop();
                  }
               }
            );
      }
   }

   public static enum ForwardAxis {
      PLUS_Z,
      PLUS_Y;
   }
}
