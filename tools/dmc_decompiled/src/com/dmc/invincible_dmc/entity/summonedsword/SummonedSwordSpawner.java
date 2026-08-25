package com.dmc.invincible_dmc.entity.summonedsword;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public final class SummonedSwordSpawner {
   private static final int DEFAULT_IMPALE_DETONATE_TICKS = 400;
   private static final int IMPALE_LIFETIME = 420;

   private SummonedSwordSpawner() {
   }

   @Nullable
   public static DMCSummonedSwordEntity createSword(Level level, @Nullable Entity caster, float scale, boolean standby) {
      return level != null && !level.f_46443_ && caster instanceof LivingEntity livingCaster
         ? new DMCSummonedSwordEntity((EntityType<? extends Mob>)DMCEntities.SUMMONED_SWORD.get(), livingCaster, scale, level, standby)
         : null;
   }

   @Nullable
   public static DMCSummonedSwordEntity spawnSword(Level level, @Nullable Entity caster, float scale, boolean standby) {
      DMCSummonedSwordEntity sword = createSword(level, caster, scale, standby);
      if (sword != null) {
         level.m_7967_(sword);
      }

      return sword;
   }

   @Nullable
   public static DMCSummonedSwordEntity summonNormal(ServerPlayerPatch patch, boolean trick) {
      return patch == null ? null : summonNormal((LivingEntity)patch.getOriginal(), patch.getTarget(), trick, patch);
   }

   @Nullable
   public static DMCSummonedSwordEntity summonNormal(Level level, @Nullable Entity caster, @Nullable LivingEntity target, boolean trick) {
      return caster instanceof LivingEntity livingCaster ? summonNormal(livingCaster, target, trick, null) : null;
   }

   @Nullable
   public static DMCSummonedSwordEntity summonNormal(LivingEntity caster, @Nullable LivingEntity target, boolean trick) {
      return summonNormal(caster, target, trick, null);
   }

   @Nullable
   private static DMCSummonedSwordEntity summonNormal(
      LivingEntity caster, @Nullable LivingEntity target, boolean trick, @Nullable ServerPlayerPatch playerPatch
   ) {
      if (caster != null && !caster.m_9236_().f_46443_) {
         double baseDistance = -2.5;
         Vec3 lookVec = caster.m_20154_();
         Vec3 basePos = caster.m_146892_().m_82549_(lookVec.m_82490_(baseDistance));
         boolean isLeft = caster.m_217043_().m_188499_();
         double randomAngle = caster.m_217043_().m_188500_() * (Math.PI / 6) + (Math.PI / 3);
         if (isLeft) {
            randomAngle = -randomAngle;
         }

         double randomRadius = caster.m_217043_().m_188500_() * 2.5 + 1.0;
         Vec3 right = new Vec3(-lookVec.f_82481_, 0.0, lookVec.f_82479_).m_82541_();
         Vec3 spawnPos = basePos.m_82549_(right.m_82490_(Math.sin(randomAngle) * randomRadius))
            .m_82520_(0.0, -0.4 + (caster.m_217043_().m_188500_() * 0.8 - 0.3), 0.0);
         boolean delayed = !trick;
         DMCSummonedSwordEntity sword = createSword(caster.m_9236_(), caster, 2.5F, delayed);
         if (sword == null) {
            return null;
         } else {
            sword.setLifetimeTicks(80);
            sword.setOrdinarySummonedSword(true);
            sword.setTrick(trick);
            sword.m_146884_(spawnPos);
            LivingEntity resolvedTarget = target;
            if (target == null) {
               DMCSummonedSwordPatch<?> swordPatch = (DMCSummonedSwordPatch<?>)EpicFightCapabilities.getEntityPatch(sword, DMCSummonedSwordPatch.class);
               if (swordPatch != null) {
                  resolvedTarget = swordPatch.target();
               }
            }

            if (trick && playerPatch != null) {
               SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
               if (container != null && container.getSkill() instanceof ComboBasicAttack comboBasicAttack) {
                  comboBasicAttack.resetCombo(container);
               }
            }

            if (resolvedTarget != null && resolvedTarget.m_6084_()) {
               sword.aimAtEntity(resolvedTarget);
            } else {
               LivingEntityPatch<?> casterPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(caster, LivingEntityPatch.class);
               float yaw = casterPatch != null ? casterPatch.getYRot() : caster.m_6080_();
               sword.m_146922_(yaw);
               sword.m_146926_(caster.m_146909_());
               sword.setSyncXRot(caster.m_146909_());
            }

            caster.m_9236_().m_7967_(sword);
            if (delayed) {
               LivingEntity finalTarget = resolvedTarget;
               InvincibleMod_DMC.queueServerWork(2, () -> {
                  if (sword.m_6084_()) {
                     sword.launch(finalTarget);
                     if (!trick) {
                        sword.m_5496_((SoundEvent)DMCSounds.SUMMONED_SWORD_SHOOT.get(), 0.8F, 1.0F);
                     }
                  }
               });
            }

            return sword;
         }
      } else {
         return null;
      }
   }

   @Nullable
   public static DMCSummonedSwordEntity summonImpale(ServerPlayerPatch patch) {
      return patch == null ? null : summonImpale((LivingEntity)patch.getOriginal(), patch.getTarget(), 400, patch.getYRot());
   }

   @Nullable
   public static DMCSummonedSwordEntity summonImpale(ServerPlayerPatch patch, int detonateTicks) {
      return patch == null ? null : summonImpale((LivingEntity)patch.getOriginal(), patch.getTarget(), detonateTicks, patch.getYRot());
   }

   @Nullable
   public static DMCSummonedSwordEntity summonImpale(Level level, @Nullable Entity caster, @Nullable LivingEntity target, int detonateTicks) {
      if (caster instanceof LivingEntity livingCaster) {
         LivingEntityPatch<?> casterPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingCaster, LivingEntityPatch.class);
         float fallbackYaw = casterPatch != null ? casterPatch.getYRot() : livingCaster.m_146908_();
         return summonImpale(livingCaster, target, detonateTicks, fallbackYaw);
      } else {
         return null;
      }
   }

   @Nullable
   public static DMCSummonedSwordEntity summonImpale(LivingEntity caster, @Nullable LivingEntity target, int detonateTicks) {
      LivingEntityPatch<?> casterPatch = caster == null ? null : (LivingEntityPatch)EpicFightCapabilities.getEntityPatch(caster, LivingEntityPatch.class);
      float fallbackYaw = casterPatch != null ? casterPatch.getYRot() : (caster != null ? caster.m_146908_() : 0.0F);
      return summonImpale(caster, target, detonateTicks, fallbackYaw);
   }

   @Nullable
   private static DMCSummonedSwordEntity summonImpale(LivingEntity caster, @Nullable LivingEntity target, int detonateTicks, float fallbackYaw) {
      if (caster != null && !caster.m_9236_().f_46443_) {
         Vec3 lookVec = caster.m_20154_();
         Vec3 hLook = new Vec3(lookVec.f_82479_, 0.0, lookVec.f_82481_).m_82541_();
         if (hLook.m_82556_() < 0.001) {
            hLook = new Vec3(0.0, 0.0, 1.0);
         }

         Vec3 spawnPos = caster.m_20182_().m_82549_(hLook.m_82490_(2.0)).m_82520_(0.0, 5.0, 0.0);
         DMCSummonedSwordEntity sword = createSword(caster.m_9236_(), caster, 1.5F, true);
         if (sword == null) {
            return null;
         } else {
            sword.setImpale(true);
            sword.setLifetimeTicks(420);
            sword.setImpaleDetonateTicks(detonateTicks);
            sword.setShootSpeed(4);
            sword.m_146884_(spawnPos);
            LivingEntity resolvedTarget = target;
            if (target == null) {
               DMCSummonedSwordPatch<?> swordPatch = (DMCSummonedSwordPatch<?>)EpicFightCapabilities.getEntityPatch(sword, DMCSummonedSwordPatch.class);
               if (swordPatch != null) {
                  resolvedTarget = swordPatch.target();
               }
            }

            if (resolvedTarget != null && resolvedTarget.m_6084_()) {
               sword.aimAtEntity(resolvedTarget);
            } else {
               sword.m_146922_(fallbackYaw);
               sword.m_146926_(caster.m_146909_());
               sword.setSyncXRot(caster.m_146909_());
            }

            caster.m_9236_().m_7967_(sword);
            LivingEntity finalTarget = resolvedTarget;
            InvincibleMod_DMC.queueServerWork(3, () -> {
               if (sword.m_6084_()) {
                  sword.launch(finalTarget);
               }
            });
            return sword;
         }
      } else {
         return null;
      }
   }

   @Nullable
   public static TripleBladesEntity triple(Level level, @Nullable Entity caster) {
      if (level != null && !level.f_46443_ && caster instanceof LivingEntity livingCaster) {
         TripleBladesEntity controller = new TripleBladesEntity(level, livingCaster);
         level.m_7967_(controller);
         return controller;
      } else {
         return null;
      }
   }

   @Nullable
   public static ProvocationBladesEntity provocation(Level level, @Nullable Entity caster) {
      if (level != null && !level.f_46443_ && caster instanceof LivingEntity livingCaster) {
         ProvocationBladesEntity controller = new ProvocationBladesEntity(level, livingCaster);
         level.m_7967_(controller);
         return controller;
      } else {
         return null;
      }
   }

   @Nullable
   public static SpineBladeEntity spine(@Nullable Entity caster) {
      if (caster instanceof LivingEntity livingCaster && !livingCaster.m_9236_().f_46443_) {
         SpineBladeEntity controller = new SpineBladeEntity(livingCaster.m_9236_(), livingCaster);
         livingCaster.m_9236_().m_7967_(controller);
         controller.spawnChildSword();
         return controller;
      }

      return null;
   }

   @Nullable
   public static StormBladesEntity storm(Level level, @Nullable Entity caster, @Nullable LivingEntity target) {
      if (level != null && !level.f_46443_ && caster instanceof LivingEntity livingCaster && target != null && target.m_6084_()) {
         StormBladesEntity controller = new StormBladesEntity(level, livingCaster, target);
         level.m_7967_(controller);
         controller.spawnChildSwords();
         return controller;
      }

      return null;
   }

   @Nullable
   public static SpiralBladesEntity spiral(Level level, @Nullable Entity caster) {
      if (level != null && !level.f_46443_ && caster instanceof LivingEntity livingCaster) {
         SpiralBladesEntity controller = new SpiralBladesEntity(level, livingCaster);
         level.m_7967_(controller);
         controller.spawnChildSwords();
         return controller;
      } else {
         return null;
      }
   }

   @Nullable
   public static BlisteringBladesEntity blistering(
      Level level, @Nullable Entity caster, int totalSwords, int standbyTicks, int launchInterval, int spawnsPerTick
   ) {
      if (level != null && !level.f_46443_ && caster instanceof LivingEntity livingCaster) {
         BlisteringBladesEntity controller = new BlisteringBladesEntity(level, livingCaster);
         controller.setConfig(totalSwords, standbyTicks, launchInterval, spawnsPerTick);
         level.m_7967_(controller);
         controller.spawnChildSwords();
         return controller;
      } else {
         return null;
      }
   }

   @Nullable
   public static HeavyRainBladesEntity heavyRain(
      Level level,
      @Nullable Entity caster,
      @Nullable LivingEntity target,
      int standbyTicks,
      int launchInterval,
      int spawnsPerTick,
      @Nullable double[][] customRings
   ) {
      if (level != null && !level.f_46443_ && caster instanceof LivingEntity livingCaster) {
         if (HeavyRainBladesEntity.isActiveFor(livingCaster)) {
            return null;
         } else {
            Vec3 targetPos;
            if (target != null && target.m_6084_()) {
               targetPos = target.m_20182_();
            } else {
               Vec3 look = livingCaster.m_20154_();
               Vec3 horizontalLook = new Vec3(look.f_82479_, 0.0, look.f_82481_).m_82541_();
               if (horizontalLook.m_82556_() == 0.0) {
                  horizontalLook = new Vec3(0.0, 0.0, 1.0);
               }

               targetPos = livingCaster.m_20182_().m_82549_(horizontalLook.m_82490_(2.0));
            }

            HeavyRainBladesEntity controller = new HeavyRainBladesEntity(level, livingCaster, targetPos);
            controller.setConfig(standbyTicks, launchInterval, spawnsPerTick, customRings);
            level.m_7967_(controller);
            controller.spawnChildSwords();
            return controller;
         }
      } else {
         return null;
      }
   }

   public static void playThrowSound(Entity entity) {
      if (entity != null) {
         entity.m_5496_(SoundEvents.f_12520_, 0.8F, 1.4F);
      }
   }

   public static void playBreakSound(Level level, Vec3 pos, SoundSource source, float volume, float pitch) {
      if (level != null && pos != null) {
         level.m_6263_(null, pos.f_82479_, pos.f_82480_, pos.f_82481_, SoundEvents.f_11983_, source, volume, pitch);
      }
   }

   public static float clampedDownwardPitchFromTarget(Entity sword, Entity target, float minDownPitch, float maxDownPitch) {
      if (sword != null && target != null) {
         Vec3 targetPos = target instanceof LivingEntity living ? living.m_146892_().m_82520_(0.0, 0.5, 0.0) : target.m_146892_().m_82520_(0.0, 0.5, 0.0);
         Vec3 direction = targetPos.m_82546_(sword.m_146892_());
         double horizontal = direction.m_165924_();
         if (!(direction.m_82556_() < 0.001) && !(horizontal < 0.001)) {
            float pitchToTarget = (float)(-(Mth.m_14136_(direction.f_82480_, horizontal) * (180.0 / Math.PI)));
            return Mth.m_14036_(pitchToTarget, minDownPitch, maxDownPitch);
         } else {
            return minDownPitch;
         }
      } else {
         return minDownPitch;
      }
   }

   public static void setSwordRotation(DMCSummonedSwordEntity sword, float yaw, float pitch) {
      if (sword != null) {
         sword.m_146922_(yaw);
         sword.m_146926_(pitch);
         sword.setSyncXRot(pitch);
         sword.f_19859_ = yaw;
         sword.f_19860_ = pitch;
         sword.m_5618_(yaw);
         sword.f_20884_ = yaw;
         sword.m_5616_(yaw);
         sword.f_20886_ = yaw;
      }
   }
}
