package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.compat.EpicFightNightFall;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.entity.FlyingShockwaveProjectile;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.util.EpicfightUtil.1;
import java.util.Objects;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import net.shelmarow.combat_evolution.ai.util.CEPatchUtils;
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import net.shelmarow.combat_evolution.execution.ExecutionHandler;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionHitAnimation;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.KnockdownAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

public class EpicfightUtil {
   public static Vec3 getJointWithTranslation(Entity entity, Vec3f translation, Joint joint, float handToTip, double yOffset) {
      LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
      if (livingEntityPatch == null) {
         return null;
      } else {
         float interpolation = 0.0F;
         OpenMatrix4f m = livingEntityPatch.getArmature().getBoundTransformFor(livingEntityPatch.getAnimator().getPose(interpolation), joint);
         if (translation != null) {
            OpenMatrix4f tLocal = new OpenMatrix4f().translate(translation);
            OpenMatrix4f.mul(m, tLocal, m);
         }

         if (handToTip != 0.0F) {
            OpenMatrix4f tipOffset = new OpenMatrix4f().translate(new Vec3f(0.0F, 0.0F, -handToTip));
            OpenMatrix4f.mul(m, tipOffset, m);
         }

         float yawRad = (float)(-Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F)));
         OpenMatrix4f worldYaw = new OpenMatrix4f().rotate(yawRad, new Vec3f(0.0F, 1.0F, 0.0F));
         OpenMatrix4f.mul(worldYaw, m, m);
         LivingEntity base = (LivingEntity)livingEntityPatch.getOriginal();
         return new Vec3(
            (double)m.m30 + base.m_20185_(),
            (double)m.m31 + (base.m_20186_() + (double)entity.m_20206_() / 1.8 - 1.0) + yOffset,
            (double)m.m32 + base.m_20189_()
         );
      }
   }

   public static boolean isLongHitAnimationNotExecutedAnimation(
      AssetAccessor<? extends StaticAnimation> dynamicAnimation, LivingEntityPatch<?> livingEntityPatch
   ) {
      return !(dynamicAnimation.get() instanceof ExecutionHitAnimation)
         && (
            dynamicAnimation.get() instanceof KnockdownAnimation
               || ModList.get().isLoaded("efn") && EpicFightNightFall.isEFNStun(dynamicAnimation)
               || ExecutionHandler.isTargetGuardBreak(dynamicAnimation, livingEntityPatch)
         );
   }

   public static boolean isLongHitAnimation(AssetAccessor<? extends StaticAnimation> dynamicAnimation, LivingEntityPatch<?> livingEntityPatch) {
      return dynamicAnimation.get() instanceof ExecutionHitAnimation
         || dynamicAnimation.get() instanceof KnockdownAnimation
         || ModList.get().isLoaded("efn") && EpicFightNightFall.isEFNStun(dynamicAnimation)
         || ExecutionHandler.isTargetGuardBreak(dynamicAnimation, livingEntityPatch);
   }

   public static boolean isDamagableHitAnimation(AssetAccessor<? extends StaticAnimation> dynamicAnimation, LivingEntityPatch<?> livingEntityPatch) {
      return dynamicAnimation.get() instanceof ExecutionHitAnimation
         || dynamicAnimation.get() instanceof KnockdownAnimation
         || ExecutionHandler.isTargetGuardBreak(dynamicAnimation, livingEntityPatch);
   }

   public static void stopAnimationSynchronized(LivingEntity entity, AssetAccessor<? extends StaticAnimation> animation) {
      if (entity != null && animation != null) {
         LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         if (livingEntityPatch != null) {
            if (livingEntityPatch.isLogicalClient()) {
               livingEntityPatch.getAnimator().stopPlaying(animation);
            } else {
               livingEntityPatch.stopPlaying(animation);
            }
         }
      }
   }

   public static boolean isPlaying(LivingEntity entity, AssetAccessor<? extends StaticAnimation> animation) {
      if (entity != null && animation != null) {
         LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         return livingEntityPatch != null && livingEntityPatch.getAnimator().getPlayerFor(null) != null
            ? livingEntityPatch.getAnimator().getPlayerFor(null).getRealAnimation() == animation
            : false;
      } else {
         return false;
      }
   }

   public static void cancel(LivingEntity entity, AssetAccessor<? extends StaticAnimation> animation) {
      if (isPlaying(entity, animation)) {
         stopAnimationSynchronized(entity, animation);
      }
   }

   public static void cancelLater(LivingEntity entity, AssetAccessor<? extends StaticAnimation> animation, int delayTicks) {
      if (entity != null && animation != null) {
         new 1(delayTicks, entity, animation);
      }
   }

   public static void dealStaminaDamageByPercentage(
      DamageSource damageSource, LivingEntityPatch<?> livingEntityPatch, double percentage, boolean playStunAnimation
   ) {
      float decrease = 0.0F;
      if (livingEntityPatch instanceof CEHumanoidPatch) {
         float currentStamina = CEPatchUtils.getStamina(livingEntityPatch);
         float maxStamina = CEPatchUtils.getMaxStamina(livingEntityPatch);
         float staminaToDecrease = (float)((double)maxStamina * percentage);
         decrease = Math.min(staminaToDecrease, currentStamina);
      } else if (livingEntityPatch instanceof PlayerPatch<?> playerPatch) {
         float currentStamina = playerPatch.getStamina();
         float maxStamina = playerPatch.getMaxStamina();
         float staminaToDecrease = (float)((double)maxStamina * percentage);
         decrease = Math.min(staminaToDecrease, currentStamina);
      }

      dealStaminaDamage(damageSource, decrease, livingEntityPatch, playStunAnimation);
   }

   public static void dealStaminaDamage(DamageSource damageSource, float amount, LivingEntityPatch<?> livingEntityPatch, boolean playStunAnimation) {
      if (livingEntityPatch instanceof CEHumanoidPatch<?> ceHumanoidPatch) {
         if (!ceHumanoidPatch.dealStaminaDamage(damageSource, amount) && playStunAnimation) {
            livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.GUARD_BREAK_ATTACK, 0.0F);
         }
      } else if (livingEntityPatch instanceof PlayerPatch<?> playerPatch) {
         float stamina = playerPatch.getStamina();
         playerPatch.setStamina(stamina - amount);
         if (amount >= stamina) {
            EpicFightDamageSource efSource = damageSource instanceof EpicFightDamageSource ? (EpicFightDamageSource)damageSource : null;
            if (efSource != null) {
               efSource.setStunType(StunType.NONE);
               Vec3 sourcePosition = efSource.getInitialPosition();
               if (sourcePosition != null) {
                  ((Player)playerPatch.getOriginal()).m_7618_(Anchor.FEET, sourcePosition);
               }
            }

            if (playerPatch.applyStun(StunType.NEUTRALIZE, 0.0F)) {
               ((Player)playerPatch.getOriginal())
                  .m_147215_(new MobEffectInstance((MobEffect)CEMobEffects.FULL_STUN_IMMUNITY.get(), 100), playerPatch.getOriginal());
               Vec3 eyePosition = ((Player)playerPatch.getOriginal()).m_146892_();
               Vec3 viewVec = ((Player)playerPatch.getOriginal()).m_20154_().m_82490_(2.0);
               Vec3 pos = new Vec3(eyePosition.f_82479_ + viewVec.f_82479_, eyePosition.f_82480_ + viewVec.f_82480_, eyePosition.f_82481_ + viewVec.f_82481_);
               ((Player)playerPatch.getOriginal())
                  .m_9236_()
                  .m_7106_((ParticleOptions)EpicFightParticles.NEUTRALIZE.get(), pos.f_82479_, pos.f_82480_, pos.f_82481_, 0.0, 0.0, 0.0);
               playerPatch.playSound((SoundEvent)EpicFightSounds.NEUTRALIZE_MOBS.get(), 1.0F, 1.0F);
            }
         }
      }
   }

   public static void breakWeaponOnParryOpAttack(DamageSource damageSource) {
      if (damageSource.m_7639_() instanceof Player player) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (playerPatch != null) {
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getRealAnimation();
            if (EscapeUtil.isAnimationDangerous(dynamicAnimation)) {
               int breakValue = (Integer)AnnoyingVillagersConfig.WEAPON_BREAKING_MECHANISM_VALUE.get();
               if (ModList.get().isLoaded("efn") && EpicFightNightFall.isEfnWeapons(player.m_21205_())) {
                  breakValue = (Integer)AnnoyingVillagersConfig.WEAPON_BREAKING_MECHANISM_VALUE.get() * 5;
               }

               player.m_21205_().m_41622_(breakValue, player, livingEntity -> livingEntity.m_21166_(EquipmentSlot.MAINHAND));
            }
         }
      }
   }

   public static void damageBlocked(DamageSource damagesource, Entity livingentity, ServerLevel level) {
      if (livingentity != null) {
         if (!damagesource.m_276093_(DamageTypes.f_268612_) && !damagesource.m_276093_(DamageTypes.f_268631_) && !damagesource.m_276093_(DamageTypes.f_268468_)
            )
          {
            livingentity.m_5496_((SoundEvent)EpicFightSounds.CLASH.get(), 1.0F, 1.0F);
         }

         ((HitParticleType)EpicFightParticles.HIT_BLUNT.get())
            .spawnParticleWithArgument(level, HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, livingentity, damagesource.m_7639_());
         if (damagesource.m_7639_() instanceof Player player) {
            ScreenShakeUtil.applyScreenShake(level, player.m_20097_().m_252807_(), 1.0, 20, 4);
         }
      }
   }

   public static void damageBlockedForce(Entity defender, Entity attacker, ServerLevel level) {
      defender.m_5496_((SoundEvent)EpicFightSounds.CLASH.get(), 1.0F, 1.0F);
      ((HitParticleType)EpicFightParticles.HIT_BLUNT.get())
         .spawnParticleWithArgument(level, HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, defender, attacker);
      if (attacker instanceof Player player) {
         ScreenShakeUtil.applyScreenShake(level, player.m_20097_().m_252807_(), 1.0, 20, 4);
      }
   }

   public static void shootFlyingShockwave(LivingEntityPatch<?> livingEntityPatch) {
      float ang = (float)((double)((livingEntityPatch.getYRot() + 90.0F) / 180.0F) * Math.PI);
      Vec3 shootVec = new Vec3(Math.cos((double)ang), 0.0, Math.sin((double)ang));
      Vec3 shootPos = ((LivingEntity)livingEntityPatch.getOriginal()).m_20182_().m_82520_(shootVec.f_82479_, 0.0, shootVec.f_82481_);
      FlyingShockwaveProjectile projectile = (FlyingShockwaveProjectile)((EntityType)AnnoyingVillagersModEntities.FLYING_SHOCKWAVE.get())
         .m_20615_(((LivingEntity)livingEntityPatch.getOriginal()).m_9236_());
      float multiplier = 1.5F;
      if (projectile != null) {
         projectile.setDamage((float)((LivingEntity)livingEntityPatch.getOriginal()).m_21133_(Attributes.f_22281_) * multiplier);
         projectile.m_146884_(shootPos);
         projectile.setMaxStrikes(3);
         projectile.m_5602_(livingEntityPatch.getOriginal());
         projectile.m_6686_(shootVec.m_7096_(), 0.0, shootVec.m_7094_(), 4.2F, 0.0F);
         ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_().m_7967_(projectile);
      }
   }
}
