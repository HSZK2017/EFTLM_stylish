package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.compat.aaa_particles.emitterinfo.BlackFireParticleEmitterInfo;
import com.pla.annoyingvillagers.compat.aaa_particles.emitterinfo.BlueDemonThunderBeamParticleEmitterInfo;
import com.pla.annoyingvillagers.compat.aaa_particles.emitterinfo.DragonBeamParticleEmitterInfo;
import com.pla.annoyingvillagers.compat.aaa_particles.emitterinfo.EnderGlaiveExplosionParticleEmitterInfo;
import com.pla.annoyingvillagers.compat.aaa_particles.emitterinfo.TeleportPortalParticleEmitterInfo;
import com.pla.annoyingvillagers.entity.BlueDemonThunderBeamEntity;
import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

public class AAAParticlesUtil {
   public static void sendEnderGlaiveExplosion(Vec3 from, Vec3 to, Level level) {
      new EnderGlaiveExplosionParticleEmitterInfo(ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "ender_glaive_explosion"))
         .fromTo(from, to, EnderGlaiveExplosionParticleEmitterInfo.ForwardAxis.PLUS_Z, 0.0F, true)
         .spawnInWorld(level, null);
   }

   public static void sendDragonBeam(Vec3 from, Vec3 to, Level level, HerobrineDragonEntity caster, LivingEntity target) {
      new DragonBeamParticleEmitterInfo(ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "dragon_beam"))
         .fromTo(from, to, DragonBeamParticleEmitterInfo.ForwardAxis.PLUS_Z, 0.0F)
         .follow(caster, target, 120, DragonBeamParticleEmitterInfo.ForwardAxis.PLUS_Z, 0.0F)
         .spawnInWorld(level, null);
   }

   public static void sendDragonBeamHit(Level level, BlockPos hitBlock) {
      AAALevel.addParticle(
         level,
         false,
         new ParticleEmitterInfo(ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "dragon_beam_hit"))
            .clone()
            .position((double)hitBlock.m_123341_(), (double)hitBlock.m_123342_(), (double)hitBlock.m_123343_())
      );
   }

   public static void sendBlueDemonThunderBeam(Level level, BlueDemonThunderBeamEntity blueDemonThunderBeamEntity) {
      new BlueDemonThunderBeamParticleEmitterInfo(ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "blue_demon_thunder_beam"))
         .followBeam(blueDemonThunderBeamEntity, blueDemonThunderBeamEntity.getDuration(), BlueDemonThunderBeamParticleEmitterInfo.ForwardAxis.PLUS_Z, 0.0F)
         .spawnInWorld(level, null);
   }

   public static void sendHerobrinePortal(Level level, double x, double y, double z) {
      AAALevel.addParticle(
         level, false, new ParticleEmitterInfo(ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "herobrine_portal")).clone().position(x, y, z)
      );
   }

   public static void sendHerobrineAssistance(Level level, double x, double y, double z) {
      AAALevel.addParticle(
         level, false, new ParticleEmitterInfo(ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "requesting_assistance")).clone().position(x, y, z)
      );
   }

   public static void sendWoopieWind(Level level, double x, double y, double z) {
      AAALevel.addParticle(
         level, false, new ParticleEmitterInfo(ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "woopie_sword_wind")).clone().position(x, y, z)
      );
   }

   public static boolean sendTeleportPortal(Level level, Vec3 pos, Vec3 normal) {
      if (level != null && pos != null && level.m_5776_()) {
         new TeleportPortalParticleEmitterInfo(ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "teleport_portal"))
            .atPortal(pos, normal, TeleportPortalParticleEmitterInfo.ForwardAxis.PLUS_Z, 0.0F)
            .spawnInWorld(level, Minecraft.m_91087_().f_91074_);
         return true;
      } else {
         return false;
      }
   }

   public static void sendBlackFire(Level level, Entity entity) {
      if (level != null && entity != null) {
         if (level.m_5776_()) {
            new BlackFireParticleEmitterInfo(ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "black_fire"))
               .followEntity(entity, 60, Vec3.f_82478_)
               .smoothing(1.0)
               .spawnInWorld(level, Minecraft.m_91087_().f_91074_);
         }
      }
   }

   public static void sendDiamondAttractor(Level level, Entity entity) {
      if (level != null && entity != null) {
         if (level.m_5776_()) {
            Vec3 pos;
            try {
               pos = EpicfightUtil.getJointWithTranslation(
                  entity, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, Minecraft.m_91087_().m_91296_(), 0.0
               );
            } catch (Exception var4) {
               pos = null;
            }

            if (pos == null) {
               pos = entity.m_20182_().m_82520_(0.0, (double)entity.m_20206_() * 0.6, 0.0);
            }

            new ParticleEmitterInfo(ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "diamond_attractor"))
               .position(pos.f_82479_, pos.f_82480_, pos.f_82481_)
               .spawnInWorld(level, Minecraft.m_91087_().f_91074_);
         }
      }
   }
}
