package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.entity.ShadowHerobrineEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class HerobrinePortalUtil {
   public static final int SHINK_TIME_START = 40;
   public static final String NBT_RISING = "rising";
   public static final String NBT_TARGET_Y = "rise_target_y";
   public static final String NBT_SPEED = "rise_speed";
   public static final String NBT_TICKS = "rise_ticks";
   public static final String NBT_MAX_TICKS = "rise_max_ticks";
   public static final String NBT_SINKING = "sinking";
   public static final String NBT_SINK_TARGET_Y = "sink_target_y";
   public static final String NBT_SINK_SPEED = "sink_speed";
   public static final String NBT_SINK_TICKS = "sink_ticks";
   public static final String NBT_SINK_MAX_TICKS = "sink_max_ticks";

   public static void spawnHerobrine(LivingEntity livingEntity) {
      if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
         if (livingEntity instanceof HerobrineMob herobrineMob) {
            if (herobrineMob.getGregUUID() == null) {
               herobrineMob.m_5496_((SoundEvent)AnnoyingVillagersModSounds.PORTAL_NATURAL.get(), 1.0F, 1.0F);
            }

            if (herobrineMob instanceof ShadowHerobrineEntity shadowHerobrineEntity) {
               shadowHerobrineEntity.spawnDarkObEntities();
            }
         }

         spawnRising(serverLevel, livingEntity, livingEntity.m_20185_(), livingEntity.m_20189_(), 0.03);
      }
   }

   public static <T extends LivingEntity> void spawnRising(ServerLevel level, T entity, double x, double z, double speedPerTick) {
      double groundY = entity.m_20186_();
      double startY = groundY - 2.0;
      moveTransitionEntity(entity, x, startY, z);
      entity.f_19794_ = true;
      entity.m_20242_(true);
      entity.m_20331_(true);
      CompoundTag tag = entity.getPersistentData();
      tag.m_128379_("rising", true);
      tag.m_128347_("rise_target_y", groundY + 0.02);
      tag.m_128347_("rise_speed", Math.max(0.01, speedPerTick));
      tag.m_128405_("rise_ticks", 0);
      tag.m_128405_("rise_max_ticks", 100);
      level.m_5594_(null, entity.m_20183_(), SoundEvents.f_12404_, SoundSource.HOSTILE, 0.6F, 0.8F + level.f_46441_.m_188501_() * 0.2F);
   }

   public static <T extends LivingEntity> void sinkIntoGround(ServerLevel level, T entity, double speedPerTick) {
      double groundY = entity.m_20186_();
      double targetY = groundY - 1.2;
      entity.f_19794_ = true;
      entity.m_20242_(true);
      entity.m_20331_(true);
      if (entity instanceof Mob mob) {
         mob.m_21557_(true);
      }

      CompoundTag tag = entity.getPersistentData();
      tag.m_128379_("sinking", true);
      tag.m_128347_("sink_target_y", targetY);
      tag.m_128347_("sink_speed", Math.max(0.01, speedPerTick));
      tag.m_128405_("sink_ticks", 0);
      tag.m_128405_("sink_max_ticks", 100);
      level.m_5594_(null, entity.m_20183_(), SoundEvents.f_12404_, SoundSource.HOSTILE, 0.5F, 1.2F + level.f_46441_.m_188501_() * 0.2F);
   }

   public static void finishGroundTransition(LivingEntity entity) {
      entity.f_19794_ = false;
      entity.m_20242_(false);
      entity.m_20331_(false);
      if (entity instanceof Mob mob) {
         mob.m_21557_(false);
      }
   }

   public static void clearRiseTransitionData(LivingEntity entity) {
      CompoundTag tag = entity.getPersistentData();
      tag.m_128473_("rising");
      tag.m_128473_("rise_target_y");
      tag.m_128473_("rise_speed");
      tag.m_128473_("rise_ticks");
      tag.m_128473_("rise_max_ticks");
   }

   public static void clearSinkTransitionData(LivingEntity entity) {
      CompoundTag tag = entity.getPersistentData();
      tag.m_128473_("sinking");
      tag.m_128473_("sink_target_y");
      tag.m_128473_("sink_speed");
      tag.m_128473_("sink_ticks");
      tag.m_128473_("sink_max_ticks");
   }

   public static void cancelSinkTransition(LivingEntity entity) {
      finishGroundTransition(entity);
      clearSinkTransitionData(entity);
   }

   public static void moveTransitionEntity(LivingEntity entity, double x, double y, double z) {
      entity.m_20256_(Vec3.f_82478_);
      entity.f_19789_ = 0.0F;
      if (entity instanceof ServerPlayer serverPlayer) {
         serverPlayer.m_6021_(x, y, z);
      } else {
         entity.m_6034_(x, y, z);
      }
   }
}
