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

public class TeleportPortalParticleEmitterInfo extends ParticleEmitterInfo {
   private Vec3 pos = null;
   private Vec3 normal = new Vec3(0.0, 0.0, 1.0);
   private TeleportPortalParticleEmitterInfo.ForwardAxis axis = TeleportPortalParticleEmitterInfo.ForwardAxis.PLUS_Z;
   private float roll = 0.0F;

   public TeleportPortalParticleEmitterInfo(ResourceLocation effek) {
      super(effek);
   }

   public TeleportPortalParticleEmitterInfo atPortal(Vec3 pos, Vec3 normal, TeleportPortalParticleEmitterInfo.ForwardAxis axis, float roll) {
      this.pos = pos;
      this.normal = normalizeOrDefault(normal);
      this.axis = axis == null ? TeleportPortalParticleEmitterInfo.ForwardAxis.PLUS_Z : axis;
      this.roll = roll;
      return this;
   }

   private static Vec3 normalizeOrDefault(Vec3 direction) {
      return direction != null && !(direction.m_82556_() < 1.0E-7) ? direction.m_82541_() : new Vec3(0.0, 0.0, 1.0);
   }

   private static void aim(ParticleEmitter emitter, Vec3 direction, TeleportPortalParticleEmitterInfo.ForwardAxis axis, float roll) {
      Vec3 d = normalizeOrDefault(direction);
      double xz = Math.sqrt(d.f_82479_ * d.f_82479_ + d.f_82481_ * d.f_82481_);
      switch (axis) {
         case PLUS_Z: {
            float yaw = (float)Math.atan2(d.f_82479_, d.f_82481_);
            float pitch = (float)(-Math.atan2(d.f_82480_, xz));
            emitter.setRotation(pitch, yaw, roll);
            break;
         }
         case PLUS_Y: {
            float yaw = (float)Math.atan2(d.f_82481_, d.f_82479_) + (float) (Math.PI / 2);
            float pitch = (float)Math.atan2(xz, d.f_82480_) - (float) (Math.PI / 2);
            emitter.setRotation(pitch, yaw, roll);
         }
      }
   }

   private void applyCommonSettings(ParticleEmitter emitter) {
      if (this.isScaleSet()) {
         emitter.setScale(this.scaleX, this.scaleY, this.scaleZ);
      }

      if (this.hasParameters()) {
         for (DynamicParameter parameter : this.parameters) {
            emitter.setDynamicInput(parameter.index(), parameter.value());
         }
      }

      if (this.hasTriggers()) {
         for (int i = 0; i < this.triggers.size(); i++) {
            emitter.sendTrigger(this.triggers.getInt(i));
         }
      }
   }

   public void spawnInWorld(Level level, Player player) {
      if (!NativePlatform.isRunningOnUnsupportedPlatform() && this.pos != null) {
         EffectRegistry.load(this.effek).thenAccept(effek -> {
            ParticleEmitter emitter = this.hasEmitter() ? effek.play(this.emitter) : effek.play();
            this.applyCommonSettings(emitter);
            emitter.setPosition((float)this.pos.f_82479_, (float)this.pos.f_82480_, (float)this.pos.f_82481_);
            aim(emitter, this.normal, this.axis, this.roll);
         });
      }
   }

   public static enum ForwardAxis {
      PLUS_Z,
      PLUS_Y;
   }
}
