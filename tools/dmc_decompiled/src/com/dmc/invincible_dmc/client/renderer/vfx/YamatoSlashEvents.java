package com.dmc.invincible_dmc.client.renderer.vfx;

import com.dmc.invincible_dmc.api.events.Side;
import com.dmc.invincible_dmc.api.events.TimeStampedEvent;
import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.entity.vfx.DMCSlashEffect;
import com.dmc.invincible_dmc.entity.vfx.DMCVoidSlashEffect;
import com.dmc.invincible_dmc.entity.vfx.SlashMotionMode;
import com.dmc.invincible_dmc.item.DMCItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class YamatoSlashEvents {
   public static final int DEFAULT_COLOR = 3355647;
   public static final int DEFAULT_LIFETIME = 10;
   public static final float DEFAULT_ROTATION_OFFSET = 0.0F;
   public static final float DEFAULT_BASE_SIZE = 1.0F;
   private static final int[] DIAG_BASES = new int[]{45, 135, 225, 315};
   private static final int[] SHALLOW_BASES = new int[]{20, 160, 200, 340};
   private static final int[] STEEP_BASES = new int[]{70, 110, 250, 290};

   public static Vec3 genRushOffset(LivingEntity entity) {
      return new Vec3((double)entity.m_217043_().m_188501_() * 2.0 - 1.0, 0.0, 0.0);
   }

   public static float genDistributedRoll(LivingEntity entity) {
      RandomSource r = entity.m_217043_();
      int cat = r.m_188503_(7);
      if (cat < 2) {
         return r.m_188501_() * 40.0F - 20.0F + (r.m_188499_() ? 0.0F : 180.0F);
      } else if (cat < 4) {
         return (float)SHALLOW_BASES[r.m_188503_(4)] + r.m_188501_() * 30.0F - 15.0F;
      } else {
         return cat < 6
            ? (float)DIAG_BASES[r.m_188503_(4)] + r.m_188501_() * 24.0F - 12.0F
            : (float)STEEP_BASES[r.m_188503_(4)] + r.m_188501_() * 20.0F - 10.0F;
      }
   }

   public static DMCVoidSlashEffect doVoidSlash(LivingEntity playerIn) {
      return doVoidSlash(playerIn, 3355647);
   }

   public static DMCVoidSlashEffect doVoidSlash(LivingEntity playerIn, int color) {
      Level level = playerIn.m_9236_();
      if (level.m_5776_()) {
         return null;
      } else {
         Item item = playerIn.m_21205_().m_41720_();
         if (item != DMCItems.YAMATO_DMC4.get()) {
            return null;
         } else {
            Vec3 pos = playerIn.m_20182_().m_82520_(0.0, (double)playerIn.m_20192_() * 0.75, 0.0).m_82549_(getHorizontal(playerIn).m_82490_(-0.2F));
            DMCVoidSlashEffect effect = new DMCVoidSlashEffect((EntityType<?>)DMCEntities.VOID_SLASH_EFFECT.get(), level);
            effect.m_6034_(pos.f_82479_, pos.f_82480_, pos.f_82481_);
            effect.setOwner(playerIn);
            effect.m_146922_(getPatchYRot(playerIn) - 22.0F);
            effect.m_146926_(0.0F);
            effect.setColor(color);
            effect.setRotationRoll(180.0F);
            effect.setBaseSize(1.05F);
            effect.setLifetime(170);
            level.m_7967_(effect);
            return effect;
         }
      }
   }

   public static DMCSlashEffect doSlash(LivingEntity playerIn, float roll) {
      return doSlash(playerIn, roll, 3355647);
   }

   public static DMCSlashEffect doSlash(LivingEntity playerIn, float roll, int color) {
      return doSlash(playerIn, roll, color, 0.0F);
   }

   public static DMCSlashEffect doSlash(LivingEntity playerIn, float roll, int color, float rotationOffset) {
      return doSlash(playerIn, roll, color, rotationOffset, 1.0F);
   }

   public static DMCSlashEffect doSlash(LivingEntity playerIn, float roll, int color, float rotationOffset, float baseSize) {
      return doSlash(playerIn, roll, color, rotationOffset, baseSize, 10);
   }

   public static DMCSlashEffect doSlash(LivingEntity playerIn, float roll, int color, float rotationOffset, float baseSize, SlashMotionMode mode) {
      return doSlash(playerIn, roll, color, rotationOffset, baseSize, mode.defaultLifetime(), mode);
   }

   public static DMCSlashEffect doSlash(LivingEntity playerIn, float roll, int color, float rotationOffset, float baseSize, int lifetime) {
      return doSlash(playerIn, roll, color, Vec3.f_82478_, rotationOffset, baseSize, lifetime);
   }

   public static DMCSlashEffect doSlash(LivingEntity playerIn, float roll, int color, float rotationOffset, float baseSize, int lifetime, SlashMotionMode mode) {
      return doSlash(playerIn, roll, color, Vec3.f_82478_, rotationOffset, baseSize, lifetime, mode);
   }

   public static DMCSlashEffect doSlash(LivingEntity playerIn, float roll, SlashMotionMode mode) {
      return doSlash(playerIn, roll, 3355647, Vec3.f_82478_, 0.0F, 1.0F, mode.defaultLifetime(), mode);
   }

   public static DMCSlashEffect doSlash(LivingEntity playerIn, float roll, int color, SlashMotionMode mode) {
      return doSlash(playerIn, roll, color, Vec3.f_82478_, 0.0F, 1.0F, mode.defaultLifetime(), mode);
   }

   public static DMCSlashEffect doSlashWithOffset(LivingEntity playerIn, float roll, Vec3 centerOffset) {
      return doSlash(playerIn, roll, 3355647, centerOffset, 0.0F, 1.0F, 10);
   }

   public static DMCSlashEffect doSlashWithOffset(LivingEntity playerIn, float roll, int color, Vec3 centerOffset) {
      return doSlash(playerIn, roll, color, centerOffset, 0.0F, 1.0F, 10);
   }

   public static DMCSlashEffect doSlashWithOffset(LivingEntity playerIn, float roll, int color, Vec3 centerOffset, float rotationOffset) {
      return doSlash(playerIn, roll, color, centerOffset, rotationOffset, 1.0F, 10);
   }

   public static DMCSlashEffect doSlashWithOffset(LivingEntity playerIn, float roll, int color, Vec3 centerOffset, float rotationOffset, float baseSize) {
      return doSlash(playerIn, roll, color, centerOffset, rotationOffset, baseSize, 10);
   }

   public static DMCSlashEffect doSlashWithOffset(
      LivingEntity playerIn, float roll, int color, Vec3 centerOffset, float rotationOffset, float baseSize, int lifetime
   ) {
      return doSlash(playerIn, roll, color, centerOffset, rotationOffset, baseSize, lifetime);
   }

   public static DMCSlashEffect doSlashWithOffset(
      LivingEntity playerIn, float roll, int color, Vec3 centerOffset, float rotationOffset, float baseSize, SlashMotionMode mode
   ) {
      return doSlash(playerIn, roll, color, centerOffset, rotationOffset, baseSize, mode.defaultLifetime(), mode);
   }

   public static DMCSlashEffect doSlashWithOffset(
      LivingEntity playerIn, float roll, int color, Vec3 centerOffset, float rotationOffset, float baseSize, int lifetime, SlashMotionMode mode
   ) {
      return doSlash(playerIn, roll, color, centerOffset, rotationOffset, baseSize, lifetime, mode);
   }

   public static DMCSlashEffect doSlash(LivingEntity playerIn, float roll, int color, Vec3 centerOffset, float rotationOffset, float baseSize, int lifetime) {
      return doSlash(playerIn, roll, color, centerOffset, rotationOffset, baseSize, lifetime, SlashMotionMode.NORMAL);
   }

   public static DMCSlashEffect doSlash(
      LivingEntity playerIn, float roll, int color, Vec3 centerOffset, float rotationOffset, float baseSize, int lifetime, SlashMotionMode mode
   ) {
      float yawDeg = getPatchYRot(playerIn);
      float yawRad = yawDeg * (float) Math.PI / 180.0F;
      Vec3 horizontal = new Vec3((double)(-((float)Math.sin((double)yawRad))), 0.0, (double)((float)Math.cos((double)yawRad)));
      Item item = playerIn.m_21205_().m_41720_();
      if (item != DMCItems.YAMATO_DMC4.get()) {
         return null;
      } else {
         Vec3 pos = playerIn.m_20182_().m_82520_(0.0, (double)playerIn.m_20192_() * 0.75, 0.0).m_82549_(horizontal.m_82490_(0.3F));
         pos = pos.m_82549_(yCross(-90.0F, yawDeg).m_82490_(centerOffset.f_82480_))
            .m_82549_(yCross(0.0F, yawDeg + 90.0F).m_82490_(centerOffset.f_82481_))
            .m_82549_(horizontal.m_82490_(centerOffset.f_82481_));
         DMCSlashEffect effect = new DMCSlashEffect((EntityType<?>)DMCEntities.SLASH_EFFECT.get(), playerIn.m_9236_());
         effect.m_6034_(pos.f_82479_, pos.f_82480_, pos.f_82481_);
         effect.m_146922_(yawDeg);
         effect.m_146926_(0.0F);
         effect.setColor(color);
         effect.setRotationRoll(roll);
         effect.setRotationOffset(rotationOffset);
         effect.setBaseSize(baseSize);
         effect.setLifetime(lifetime);
         effect.setMotionMode(mode);
         playerIn.m_9236_().m_7967_(effect);
         return effect;
      }
   }

   public static DMCSlashEffect doSlashAtTarget(LivingEntity shooter, LivingEntity target, float roll, int color) {
      return doSlashAtTarget(shooter, target, roll, color, 0.0F, 1.0F, 10);
   }

   public static DMCSlashEffect doSlashAtTarget(
      LivingEntity shooter, LivingEntity target, float roll, int color, float rotationOffset, float baseSize, int lifetime
   ) {
      return doSlashAtTarget(shooter, target, roll, color, rotationOffset, baseSize, lifetime, SlashMotionMode.NORMAL);
   }

   public static DMCSlashEffect doSlashAtTarget(
      LivingEntity shooter, LivingEntity target, float roll, int color, float rotationOffset, float baseSize, int lifetime, SlashMotionMode mode
   ) {
      Vec3 dir = target.m_20182_().m_82546_(shooter.m_20182_()).m_82541_();
      float yaw = (float)Math.toDegrees(Math.atan2(-dir.f_82479_, dir.f_82481_));
      DMCSlashEffect effect = new DMCSlashEffect((EntityType<?>)DMCEntities.SLASH_EFFECT.get(), shooter.m_9236_());
      effect.m_6034_(target.m_20185_(), target.m_20186_() + (double)target.m_20206_() * 0.5, target.m_20189_());
      effect.m_146922_(yaw);
      effect.m_146926_(0.0F);
      effect.setColor(color);
      effect.setRotationRoll(roll);
      effect.setRotationOffset(rotationOffset);
      effect.setBaseSize(baseSize);
      effect.setLifetime(lifetime);
      effect.setMotionMode(mode);
      shooter.m_9236_().m_7967_(effect);
      return effect;
   }

   public static DMCSlashEffect doSlashClient(LivingEntity playerIn, float roll, int color) {
      return doSlashClient(playerIn, roll, color, Vec3.f_82478_, 0.0F, 1.0F, 10);
   }

   public static DMCSlashEffect doSlashClient(LivingEntity playerIn, float roll, int color, Vec3 centerOffset) {
      return doSlashClient(playerIn, roll, color, centerOffset, 0.0F, 1.0F, 10);
   }

   public static DMCSlashEffect doSlashClient(
      LivingEntity playerIn, float roll, int color, Vec3 centerOffset, float rotationOffset, float baseSize, int lifetime
   ) {
      return doSlashClient(playerIn, roll, color, centerOffset, rotationOffset, baseSize, lifetime, SlashMotionMode.NORMAL);
   }

   public static DMCSlashEffect doSlashClient(
      LivingEntity playerIn, float roll, int color, Vec3 centerOffset, float rotationOffset, float baseSize, int lifetime, SlashMotionMode mode
   ) {
      Level level = playerIn.m_9236_();
      if (!level.m_5776_()) {
         return null;
      } else {
         float yawDeg = getPatchYRot(playerIn);
         float yawRad = yawDeg * (float) Math.PI / 180.0F;
         Vec3 horizontal = new Vec3((double)(-((float)Math.sin((double)yawRad))), 0.0, (double)((float)Math.cos((double)yawRad)));
         Vec3 pos = playerIn.m_20182_().m_82520_(0.0, (double)playerIn.m_20192_() * 0.75, 0.0).m_82549_(horizontal.m_82490_(0.3F));
         pos = pos.m_82549_(yCross(-90.0F, yawDeg).m_82490_(centerOffset.f_82480_))
            .m_82549_(yCross(0.0F, yawDeg + 90.0F).m_82490_(centerOffset.f_82481_))
            .m_82549_(horizontal.m_82490_(centerOffset.f_82481_));
         DMCSlashEffect effect = new DMCSlashEffect((EntityType<?>)DMCEntities.SLASH_EFFECT.get(), level);
         effect.m_6034_(pos.f_82479_, pos.f_82480_, pos.f_82481_);
         effect.m_146922_(yawDeg);
         effect.m_146926_(0.0F);
         effect.setColor(color);
         effect.setRotationRoll(roll);
         effect.setRotationOffset(rotationOffset);
         effect.setBaseSize(baseSize);
         effect.setLifetime(lifetime);
         effect.setMotionMode(mode);
         level.m_7967_(effect);
         return effect;
      }
   }

   public static TimeStampedEvent spawnSlash(float time, int color, float rotationOffset, float roll, float baseSize, int lifetime) {
      return spawnSlash(time, color, rotationOffset, roll, baseSize, lifetime, SlashMotionMode.NORMAL);
   }

   public static TimeStampedEvent spawnSlash(float time, int color, float rotationOffset, float roll, float baseSize, int lifetime, SlashMotionMode mode) {
      return new TimeStampedEvent(time, (playerPatch, target, invinciblePlayer) -> {
         if (playerPatch.isLogicalClient()) {
            doSlashClient((LivingEntity)playerPatch.getOriginal(), roll, color, Vec3.f_82478_, rotationOffset, baseSize, lifetime, mode);
         }
      }, Side.LOCAL_CLIENT);
   }

   public static TimeStampedEvent spawnSlash(float time, int color, float rotationOffset, float roll) {
      return spawnSlash(time, color, rotationOffset, roll, 1.0F, 10);
   }

   public static TimeStampedEvent spawnSlash(float time, int color) {
      return spawnSlash(time, color, 0.0F, 0.0F);
   }

   public static TimeStampedEvent spawnSlash(float time) {
      return spawnSlash(time, 3355647);
   }

   public static TimeStampedEvent spawnSlash(float time, SlashMotionMode mode) {
      return spawnSlash(time, 3355647, 0.0F, 0.0F, 1.0F, mode.defaultLifetime(), mode);
   }

   public static TimeStampedEvent spawnSlashWithOffset(float time, int color, float rotationOffset, float roll, Vec3 centerOffset, float baseSize, int lifetime) {
      return spawnSlashWithOffset(time, color, rotationOffset, roll, centerOffset, baseSize, lifetime, SlashMotionMode.NORMAL);
   }

   public static TimeStampedEvent spawnSlashWithOffset(
      float time, int color, float rotationOffset, float roll, Vec3 centerOffset, float baseSize, int lifetime, SlashMotionMode mode
   ) {
      return new TimeStampedEvent(time, (playerPatch, target, invinciblePlayer) -> {
         if (playerPatch.isLogicalClient()) {
            doSlashClient((LivingEntity)playerPatch.getOriginal(), roll, color, centerOffset, rotationOffset, baseSize, lifetime, mode);
         }
      }, Side.LOCAL_CLIENT);
   }

   public static TimeStampedEvent spawnSlashWithOffset(float time, int color, Vec3 centerOffset) {
      return spawnSlashWithOffset(time, color, 0.0F, 0.0F, centerOffset, 1.0F, 10);
   }

   public static TimeStampedEvent spawnSlashAtTarget(float time, int color, float rotationOffset, float roll, float baseSize, int lifetime) {
      return spawnSlashAtTarget(time, color, rotationOffset, roll, baseSize, lifetime, SlashMotionMode.NORMAL);
   }

   public static TimeStampedEvent spawnSlashAtTarget(
      float time, int color, float rotationOffset, float roll, float baseSize, int lifetime, SlashMotionMode mode
   ) {
      return new TimeStampedEvent(time, (playerPatch, target, invinciblePlayer) -> {
         if (playerPatch.isLogicalClient()) {
            if (target instanceof LivingEntity targetEntity) {
               doSlashAtTarget((LivingEntity)playerPatch.getOriginal(), targetEntity, roll, color, rotationOffset, baseSize, lifetime, mode);
            }
         }
      }, Side.LOCAL_CLIENT);
   }

   public static TimeStampedEvent spawnSlashAtTarget(float time, int color) {
      return spawnSlashAtTarget(time, color, 0.0F, 0.0F, 1.0F, 10);
   }

   private static float getPatchYRot(LivingEntity entity) {
      PlayerPatch<?> pp = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(entity, PlayerPatch.class);
      return pp != null ? pp.getYRot() : entity.m_146908_();
   }

   private static Vec3 getHorizontal(LivingEntity entity) {
      float yawRad = getPatchYRot(entity) * (float) Math.PI / 180.0F;
      return new Vec3((double)(-((float)Math.sin((double)yawRad))), 0.0, (double)((float)Math.cos((double)yawRad)));
   }

   private static Vec3 yCross(float pitch, float yaw) {
      float radPitch = (float)Math.toRadians((double)pitch);
      float radYaw = (float)Math.toRadians((double)yaw);
      float cosPitch = (float)Math.cos((double)radPitch);
      return new Vec3(
         (double)(-((float)Math.sin((double)radYaw)) * cosPitch),
         (double)((float)Math.sin((double)radPitch)),
         (double)((float)Math.cos((double)radYaw) * cosPitch)
      );
   }
}
