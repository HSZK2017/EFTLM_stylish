package com.pla.annoyingvillagers.compat.aaa_particles.emitterinfo;

import com.pla.annoyingvillagers.entity.BlueDemonThunderBeamEntity;
import java.lang.ref.WeakReference;
import mod.chloeprime.aaaparticles.api.client.EffectRegistry;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.DynamicParameter;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BlueDemonThunderBeamParticleEmitterInfo extends ParticleEmitterInfo {
   private WeakReference<BlueDemonThunderBeamEntity> beamRef = new WeakReference<>(null);
   private int durationTicks = 0;
   private BlueDemonThunderBeamParticleEmitterInfo.ForwardAxis axis = BlueDemonThunderBeamParticleEmitterInfo.ForwardAxis.PLUS_Z;
   private float roll = 0.0F;
   private Vec3 lastStartPos = null;
   private Vec3 lastEndPos = null;

   public BlueDemonThunderBeamParticleEmitterInfo(ResourceLocation effek) {
      super(effek);
   }

   public BlueDemonThunderBeamParticleEmitterInfo fromTo(Vec3 from, Vec3 to, BlueDemonThunderBeamParticleEmitterInfo.ForwardAxis axis, float extraRollRad) {
      this.position(from.f_82479_, from.f_82480_, from.f_82481_);
      this.axis = axis;
      this.roll = extraRollRad;
      this.lastStartPos = from;
      this.lastEndPos = to;
      applyRotation(this, from, to, axis, extraRollRad);
      return this;
   }

   public BlueDemonThunderBeamParticleEmitterInfo followBeam(
      BlueDemonThunderBeamEntity beam, int durationTicks, BlueDemonThunderBeamParticleEmitterInfo.ForwardAxis axis, float extraRollRad
   ) {
      this.beamRef = new WeakReference<>(beam);
      this.durationTicks = durationTicks;
      this.axis = axis;
      this.roll = extraRollRad;
      return this;
   }

   private static void applyRotation(ParticleEmitterInfo info, Vec3 from, Vec3 to, BlueDemonThunderBeamParticleEmitterInfo.ForwardAxis axis, float roll) {
      Vec3 d = to.m_82546_(from);
      double xz = Math.sqrt(d.f_82479_ * d.f_82479_ + d.f_82481_ * d.f_82481_);
      switch (axis) {
         case PLUS_Z: {
            float yaw = (float)Math.atan2(d.f_82479_, d.f_82481_);
            float pitch = (float)(-Math.atan2(d.f_82480_, xz));
            info.rotation(pitch, yaw, roll);
            break;
         }
         case PLUS_Y: {
            float yaw = (float)Math.atan2(d.f_82481_, d.f_82479_) + (float) (Math.PI / 2);
            float pitch = (float)Math.atan2(xz, d.f_82480_) - (float) (Math.PI / 2);
            info.rotation(pitch, yaw, roll);
         }
      }
   }

   private static void aim(ParticleEmitter em, Vec3 from, Vec3 to, BlueDemonThunderBeamParticleEmitterInfo.ForwardAxis axis, float roll) {
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

   public void spawnInWorld(Level level, Player player) {
      if (!NativePlatform.isRunningOnUnsupportedPlatform()) {
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

            BlueDemonThunderBeamEntity beam = this.beamRef.get();
            if (beam != null && beam.m_6084_()) {
               int startTick = beam.f_19797_;
               Vec3 from0 = beam.getStartPos();
               Vec3 to0 = beam.getEndPos();
               this.lastStartPos = from0;
               this.lastEndPos = to0;
               em.setPosition((float)from0.f_82479_, (float)from0.f_82480_, (float)from0.f_82481_);
               aim(em, from0, to0, this.axis, this.roll);
               em.addPreDrawCallback((Emitter, partial) -> {
                  BlueDemonThunderBeamEntity b = this.beamRef.get();
                  if (b != null && b.m_6084_() && !b.m_213877_()) {
                     if (this.durationTicks > 0 && b.f_19797_ - startTick >= this.durationTicks) {
                        Emitter.stop();
                     } else {
                        Vec3 from = b.getStartPos();
                        Vec3 to = b.getEndPos();
                        if (this.lastStartPos == null) {
                           this.lastStartPos = from;
                        }

                        if (this.lastEndPos == null) {
                           this.lastEndPos = to;
                        }

                        this.lastStartPos = this.lastStartPos.m_165921_(from, 0.35);
                        this.lastEndPos = this.lastEndPos.m_165921_(to, 0.35);
                        Emitter.setPosition((float)this.lastStartPos.f_82479_, (float)this.lastStartPos.f_82480_, (float)this.lastStartPos.f_82481_);
                        aim(Emitter, this.lastStartPos, this.lastEndPos, this.axis, this.roll);
                     }
                  } else {
                     Emitter.stop();
                  }
               });
            } else {
               em.stop();
            }
         });
      }
   }

   public static enum ForwardAxis {
      PLUS_Z,
      PLUS_Y;
   }
}
