package com.pla.annoyingvillagers.compat.aaa_particles.emitterinfo;

import com.pla.annoyingvillagers.entity.BlackFireEntity;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.lang.ref.WeakReference;
import java.util.function.Supplier;
import mod.chloeprime.aaaparticles.api.client.EffectRegistry;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.DynamicParameter;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

public class BlackFireParticleEmitterInfo extends ParticleEmitterInfo {
   private WeakReference<Entity> followEntityRef = new WeakReference<>(null);
   private Supplier<Vec3> followPositionSupplier = null;
   private boolean followEnabled = false;
   private boolean useEyePosition = false;
   private Vec3 offset = Vec3.f_82478_;
   private Vec3 lastPos = null;
   private int durationTicks = 0;
   private double smoothing = 1.0;

   public BlackFireParticleEmitterInfo(ResourceLocation effek) {
      super(effek);
   }

   public BlackFireParticleEmitterInfo(ResourceLocation effek, ResourceLocation emitter) {
      super(effek, emitter);
   }

   public BlackFireParticleEmitterInfo followEntity(Entity entity) {
      return this.followEntity(entity, 0, Vec3.f_82478_);
   }

   public BlackFireParticleEmitterInfo followEntity(Entity entity, int durationTicks) {
      return this.followEntity(entity, durationTicks, Vec3.f_82478_);
   }

   public BlackFireParticleEmitterInfo followEntity(Entity entity, int durationTicks, Vec3 offset) {
      this.followEnabled = true;
      this.followEntityRef = new WeakReference<>(entity);
      this.followPositionSupplier = null;
      this.durationTicks = durationTicks;
      this.offset = offset == null ? Vec3.f_82478_ : offset;
      this.useEyePosition = false;
      return this;
   }

   public BlackFireParticleEmitterInfo followEntityEye(Entity entity, int durationTicks, Vec3 offset) {
      this.followEntity(entity, durationTicks, offset);
      this.useEyePosition = true;
      return this;
   }

   public BlackFireParticleEmitterInfo followPosition(Supplier<Vec3> positionSupplier, int durationTicks) {
      this.followEnabled = true;
      this.followEntityRef = new WeakReference<>(null);
      this.followPositionSupplier = positionSupplier;
      this.durationTicks = durationTicks;
      this.offset = Vec3.f_82478_;
      this.useEyePosition = false;
      return this;
   }

   private static Vec3 getInterpolatedEntityPosition(Entity entity, float partialTick) {
      double x = Mth.m_14139_((double)partialTick, entity.f_19790_, entity.m_20185_());
      double y = Mth.m_14139_((double)partialTick, entity.f_19791_, entity.m_20186_());
      double z = Mth.m_14139_((double)partialTick, entity.f_19792_, entity.m_20189_());
      return new Vec3(x, y, z);
   }

   private static Vec3 getSwordPosition(Entity entity, float partialTick) {
      try {
         return EpicfightUtil.getJointWithTranslation(entity, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, partialTick, 0.0);
      } catch (Exception var3) {
         return null;
      }
   }

   private Vec3 getCurrentFollowPosition(float partialTick) {
      Entity entity = this.followEntityRef.get();
      if (entity != null) {
         if (entity.m_6084_() && !entity.m_213877_()) {
            Vec3 pos;
            if (entity instanceof BlackFireEntity blackFire) {
               pos = getBlackFireFollowPosition(blackFire, partialTick);
            } else {
               pos = getSwordPosition(entity, partialTick);
               if (pos == null) {
                  pos = getInterpolatedEntityPosition(entity, partialTick);
               }
            }

            return pos == null ? null : pos.m_82549_(this.offset);
         } else {
            return null;
         }
      } else if (this.followPositionSupplier != null) {
         Vec3 pos = this.followPositionSupplier.get();
         return pos == null ? null : pos.m_82549_(this.offset);
      } else {
         return null;
      }
   }

   private static Vec3 getBlackFireFollowPosition(BlackFireEntity blackFire, float partialTick) {
      if (blackFire.isFollowOwnerSwordMode()) {
         Entity owner = blackFire.getOwnerEntity();
         if (owner != null && owner.m_6084_() && !owner.m_213877_()) {
            Vec3 swordPos = getSwordPosition(owner, partialTick);
            if (swordPos != null) {
               return swordPos;
            }
         }
      }

      return getInterpolatedEntityPosition(blackFire, partialTick);
   }

   public BlackFireParticleEmitterInfo smoothing(double value) {
      this.smoothing = Math.max(0.0, Math.min(1.0, value));
      return this;
   }

   private void applyCommonSettings(ParticleEmitter emitter) {
      if (this.isRotationSet()) {
         emitter.setRotation(this.rotX, this.rotY, this.rotZ);
      }

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
      if (!NativePlatform.isRunningOnUnsupportedPlatform()) {
         if (!this.followEnabled) {
            super.spawnInWorld(level, player);
         } else {
            EffectRegistry.load(this.effek).thenAccept(effek -> {
               ParticleEmitter emitter = this.hasEmitter() ? effek.play(this.emitter) : effek.play();
               this.applyCommonSettings(emitter);
               long startGameTime = level.m_46467_();
               Vec3 startPos = this.getCurrentFollowPosition(0.0F);
               if (startPos == null) {
                  emitter.stop();
               } else {
                  this.lastPos = startPos;
                  emitter.setPosition((float)startPos.f_82479_, (float)startPos.f_82480_, (float)startPos.f_82481_);
                  emitter.addPreDrawCallback((em, partial) -> {
                     if (this.durationTicks > 0 && level.m_46467_() - startGameTime >= (long)this.durationTicks) {
                        em.stop();
                     } else {
                        Vec3 currentPos = this.getCurrentFollowPosition(partial);
                        if (currentPos == null) {
                           em.stop();
                        } else {
                           if (this.lastPos != null && !(this.smoothing >= 1.0)) {
                              this.lastPos = this.lastPos.m_165921_(currentPos, this.smoothing);
                           } else {
                              this.lastPos = currentPos;
                           }

                           em.setPosition((float)this.lastPos.f_82479_, (float)this.lastPos.f_82480_, (float)this.lastPos.f_82481_);
                        }
                     }
                  });
               }
            });
         }
      }
   }
}
