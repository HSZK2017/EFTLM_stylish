package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.combatbehaviour.CombatCommon;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.CombatBehaviour.3.1;
import com.pla.annoyingvillagers.util.CombatBehaviour.3.2;
import com.pla.annoyingvillagers.util.CombatBehaviour.3.3;
import java.util.Objects;
import java.util.Random;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class CombatBehaviour {
   public static final ItemStack HOSTILE_HEALING_POTION = PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43582_);
   public static final ItemStack HEALING_POTION = PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43581_);

   private static Vec3 getFrontLeftPos(Entity entity) {
      Vec3 base = entity instanceof LivingEntity le ? le.m_20299_(1.0F) : entity.m_20182_().m_82520_(0.0, (double)entity.m_20206_() * 0.85, 0.0);
      base = base.m_82520_(0.0, -0.1, 0.0);
      Vec3 forward = entity.m_20154_();
      Vec3 forwardH = new Vec3(forward.f_82479_, 0.0, forward.f_82481_);
      if (forwardH.m_82556_() < 1.0E-6) {
         forwardH = entity.m_20156_();
         forwardH = new Vec3(forwardH.f_82479_, 0.0, forwardH.f_82481_);
      }

      forwardH = forwardH.m_82541_();
      Vec3 left = new Vec3(0.0, 1.0, 0.0).m_82537_(forwardH);
      if (left.m_82556_() < 1.0E-6) {
         left = new Vec3(1.0, 0.0, 0.0);
      } else {
         left = left.m_82541_();
      }

      return base.m_82549_(forwardH.m_82490_(0.35)).m_82549_(left.m_82490_(0.25));
   }

   public static void throwEnderPearl(final Entity entity, float xRot) {
      if (xRot != 0.0F) {
         entity.m_146922_(0.0F);
         entity.m_146926_(xRot);
         entity.m_5618_(entity.m_146908_());
         entity.m_5616_(entity.m_146908_());
         entity.f_19859_ = entity.m_146908_();
         entity.f_19860_ = entity.m_146909_();
         LivingEntity livingEntity = (LivingEntity)entity;
         livingEntity.f_20884_ = livingEntity.m_146908_();
         livingEntity.f_20886_ = livingEntity.m_146908_();
      }

      if (entity.m_9236_() instanceof ServerLevel serverLevel) {
         new DelayedTask(5) {
            public void run() {
               Vec3 handPos = CombatBehaviour.getFrontLeftPos(entity);
               Projectile projectile = new ThrownEnderpearl(EntityType.f_20484_, serverLevel);
               projectile.m_5602_(entity);
               projectile.m_6034_(handPos.f_82479_, handPos.f_82480_, handPos.f_82481_);
               projectile.m_6686_(
                  entity.m_20154_().f_82479_, entity.m_20154_().f_82480_, entity.m_20154_().f_82481_, new Random().nextBoolean() ? 1.0F : 2.0F, 0.0F
               );
               serverLevel.m_7967_(projectile);
               entity.m_9236_()
                  .m_6263_(
                     null,
                     entity.m_20185_(),
                     entity.m_20186_(),
                     entity.m_20189_(),
                     SoundEvents.f_11857_,
                     SoundSource.NEUTRAL,
                     0.5F,
                     0.4F / (entity.m_9236_().m_213780_().m_188501_() * 0.4F + 0.8F)
                  );
            }
         };
      }
   }

   public static void throwEnderPearlAt(final Entity entity, final Vec3 target) {
      facePosition(entity, target);
      if (entity.m_9236_() instanceof ServerLevel serverLevel) {
         new DelayedTask(5) {
            public void run() {
               if (entity.m_6084_() && !entity.m_213877_()) {
                  Vec3 handPos = CombatBehaviour.getFrontLeftPos(entity);
                  Vec3 delta = target.m_82546_(handPos);
                  double horizontal = Math.sqrt(delta.f_82479_ * delta.f_82479_ + delta.f_82481_ * delta.f_82481_);
                  Projectile projectile = new ThrownEnderpearl(EntityType.f_20484_, serverLevel);
                  projectile.m_5602_(entity);
                  projectile.m_6034_(handPos.f_82479_, handPos.f_82480_, handPos.f_82481_);
                  projectile.m_6686_(delta.f_82479_, delta.f_82480_ + horizontal * 0.08, delta.f_82481_, 1.8F, 0.0F);
                  serverLevel.m_7967_(projectile);
                  entity.m_9236_()
                     .m_6263_(
                        null,
                        entity.m_20185_(),
                        entity.m_20186_(),
                        entity.m_20189_(),
                        SoundEvents.f_11857_,
                        SoundSource.NEUTRAL,
                        0.5F,
                        0.4F / (entity.m_9236_().m_213780_().m_188501_() * 0.4F + 0.8F)
                     );
               }
            }
         };
      }
   }

   private static void facePosition(Entity entity, Vec3 target) {
      Vec3 origin = entity instanceof LivingEntity livingEntity
         ? livingEntity.m_20299_(1.0F)
         : entity.m_20182_().m_82520_(0.0, (double)entity.m_20206_() * 0.85, 0.0);
      double dx = target.f_82479_ - origin.f_82479_;
      double dy = target.f_82480_ - origin.f_82480_;
      double dz = target.f_82481_ - origin.f_82481_;
      double horizontal = Math.sqrt(dx * dx + dz * dz);
      float yaw = (float)(Mth.m_14136_(dz, dx) * 180.0F / (float)Math.PI) - 90.0F;
      float pitch = (float)(-(Mth.m_14136_(dy, horizontal) * 180.0F / (float)Math.PI));
      entity.m_146922_(yaw);
      entity.m_146926_(pitch);
      entity.m_5618_(yaw);
      entity.m_5616_(yaw);
      entity.f_19859_ = yaw;
      entity.f_19860_ = pitch;
      if (entity instanceof LivingEntity livingEntityx) {
         livingEntityx.f_20884_ = yaw;
         livingEntityx.f_20886_ = yaw;
      }
   }

   private static void recoverItemDueToFailure(Entity entity) {
      if (entity instanceof PlayerNpcEntity playerNpcEntity) {
         playerNpcEntity.m_21008_(InteractionHand.MAIN_HAND, playerNpcEntity.getMainWeaponItem().m_41777_());
         playerNpcEntity.setHealing(false);
         playerNpcEntity.resetGapCooldown();
      }

      if (entity instanceof AVNpc AVNpc) {
         AVNpc.m_21008_(InteractionHand.MAIN_HAND, AVNpc.getMainWeaponItem().m_41777_());
         AVNpc.setHealing(false);
         AVNpc.resetGapCooldown();
      }
   }

   private static boolean isTrackedHealingCancelled(Entity entity) {
      if (entity instanceof PlayerNpcEntity playerNpcEntity) {
         return !playerNpcEntity.isHealing();
      } else {
         return entity instanceof AVNpc AVNpc ? !AVNpc.isHealing() : false;
      }
   }

   private static boolean trackedNpcHoldsMainHandItem(Entity entity, Item expectedItem) {
      if (!(entity instanceof PlayerNpcEntity) && !(entity instanceof AVNpc)) {
         return true;
      } else {
         if (entity instanceof LivingEntity livingEntity && livingEntity.m_21205_().m_41720_().equals(expectedItem)) {
            return true;
         }

         return false;
      }
   }

   private static void performEatingGoldenAppleActionMainHand(
      Entity entity, LevelAccessor levelaccessor, LivingEntityPatch<?> livingEntityPatch, boolean isEnchanted
   ) {
      if (!isTrackedHealingCancelled(entity)) {
         if (!trackedNpcHoldsMainHandItem(entity, isEnchanted ? Items.f_42437_ : Items.f_42436_)) {
            recoverItemDueToFailure(entity);
         } else {
            AssetAccessor<? extends StaticAnimation> currentAnim = Objects.requireNonNull(livingEntityPatch.getAnimator().getPlayerFor(null))
               .getRealAnimation();
            if (!(currentAnim.get() instanceof AttackAnimation)
               && !EpicfightUtil.isLongHitAnimation(currentAnim, livingEntityPatch)
               && !CombatCommon.canEscape((MobPatch<?>)livingEntityPatch)) {
               if (!entity.m_9236_().m_5776_() && entity.m_20194_() != null && entity instanceof LivingEntity livingEntity) {
                  livingEntityPatch.playAnimationSynchronized(Animations.BIPED_EAT, 0.0F);
               }

               if (levelaccessor instanceof ServerLevel serverLevel) {
                  serverLevel.m_5594_(null, entity.m_20183_(), SoundEvents.f_11912_, SoundSource.NEUTRAL, 1.0F, 1.0F);
               }

               if (entity.m_9236_() instanceof ServerLevel serverLevel) {
                  Vec3 forward = entity.m_20252_(1.0F);
                  Vec3 up = entity.m_20289_(1.0F);
                  Vec3 left = up.m_82537_(forward).m_82541_();
                  Vec3 spawnPos = entity.m_20182_().m_82549_(left.m_82490_(0.0)).m_82549_(up.m_82490_(1.5)).m_82549_(forward.m_82490_(0.5));
                  serverLevel.m_8767_(
                     new ItemParticleOption(ParticleTypes.f_123752_, new ItemStack(Items.f_42436_)),
                     spawnPos.f_82479_,
                     spawnPos.f_82480_,
                     spawnPos.f_82481_,
                     10,
                     0.0,
                     0.0,
                     0.0,
                     0.01
                  );
               }
            } else {
               recoverItemDueToFailure(entity);
            }
         }
      }
   }

   private static void performDrinkingHealingPotionActionMainhand(Entity entity, LevelAccessor levelaccessor, LivingEntityPatch<?> livingEntityPatch) {
      if (!isTrackedHealingCancelled(entity)) {
         if (!trackedNpcHoldsMainHandItem(entity, Items.f_42589_)) {
            recoverItemDueToFailure(entity);
         } else {
            AssetAccessor<? extends StaticAnimation> currentAnim = Objects.requireNonNull(livingEntityPatch.getAnimator().getPlayerFor(null))
               .getRealAnimation();
            if (!(currentAnim.get() instanceof AttackAnimation)
               && !EpicfightUtil.isLongHitAnimation(currentAnim, livingEntityPatch)
               && !CombatCommon.canEscape((MobPatch<?>)livingEntityPatch)) {
               if (!entity.m_9236_().m_5776_() && entity.m_20194_() != null && entity instanceof LivingEntity livingEntity) {
                  livingEntityPatch.playAnimationSynchronized(Animations.BIPED_DRINK, 0.0F);
               }

               if (levelaccessor instanceof ServerLevel serverLevel) {
                  serverLevel.m_5594_(null, entity.m_20183_(), SoundEvents.f_11911_, SoundSource.NEUTRAL, 1.0F, 1.0F);
               }
            } else {
               recoverItemDueToFailure(entity);
            }
         }
      }
   }

   public static void eatingGoldenApple(final Entity entity, final LevelAccessor levelaccessor, double amount, final boolean isEnchanted) {
      final LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
      if (livingEntityPatch != null && entity instanceof LivingEntity livingEntity) {
         if (entity instanceof PlayerNpcEntity playerNpcEntity && playerNpcEntity.isHealing()) {
            return;
         }

         if (entity instanceof AVNpc AVNpc && AVNpc.isHealing()) {
            return;
         }

         livingEntity.m_7292_(new MobEffectInstance(MobEffects.f_19597_, (int)(amount * 2.0), 2, false, false));
         if (entity instanceof PlayerNpcEntity playerNpcEntity) {
            if (playerNpcEntity.isHealing()) {
               return;
            }

            playerNpcEntity.setHealing(true);
         }

         if (entity instanceof AVNpc AVNpc) {
            if (AVNpc.isHealing()) {
               return;
            }

            AVNpc.setHealing(true);
         }

         new DelayedTask(20) {
            public void run() {
               if (entity.m_6084_()) {
                  if (!CombatBehaviour.isTrackedHealingCancelled(entity)) {
                     if (!CombatBehaviour.trackedNpcHoldsMainHandItem(entity, isEnchanted ? Items.f_42437_ : Items.f_42436_)) {
                        CombatBehaviour.recoverItemDueToFailure(entity);
                     } else {
                        LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                        if (patch == null) {
                           CombatBehaviour.recoverItemDueToFailure(entity);
                        } else {
                           AssetAccessor<? extends StaticAnimation> currentAnim = Objects.requireNonNull(patch.getAnimator().getPlayerFor(null))
                              .getRealAnimation();
                           if (!(currentAnim.get() instanceof AttackAnimation)
                              && !EpicfightUtil.isLongHitAnimation(currentAnim, patch)
                              && !CombatCommon.canEscape((MobPatch<?>)livingEntityPatch)) {
                              Runnable bite = () -> CombatBehaviour.performEatingGoldenAppleActionMainHand(entity, levelaccessor, patch, isEnchanted);
                              int biteDelay = 4;
                              int totalBites = 7;

                              for (int i = 0; i < totalBites; i++) {
                                 int delay = 4 + i * biteDelay;
                                 new 1(this, delay, bite);
                              }

                              new 2(this, 4 + totalBites * biteDelay - 1);
                              new 3(this, 4 + totalBites * biteDelay);
                           } else {
                              CombatBehaviour.recoverItemDueToFailure(entity);
                           }
                        }
                     }
                  }
               }
            }
         };
      }
   }

   public static void drinkingHealingPotion(final Entity entity, final LevelAccessor levelaccessor, final boolean isHostile, double amount) {
      final LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
      if (livingEntityPatch != null && entity instanceof LivingEntity livingEntity) {
         if (entity instanceof PlayerNpcEntity playerNpcEntity && playerNpcEntity.isHealing()) {
            return;
         }

         if (entity instanceof AVNpc AVNpc && AVNpc.isHealing()) {
            return;
         }

         livingEntity.m_7292_(new MobEffectInstance(MobEffects.f_19597_, (int)(amount * 2.0), 2, false, false));
         if (entity instanceof PlayerNpcEntity playerNpcEntity) {
            if (playerNpcEntity.isHealing()) {
               return;
            }

            playerNpcEntity.setHealing(true);
         }

         if (entity instanceof AVNpc AVNpc) {
            if (AVNpc.isHealing()) {
               return;
            }

            AVNpc.setHealing(true);
         }

         new DelayedTask(20) {
            public void run() {
               if (entity.m_6084_()) {
                  if (!CombatBehaviour.isTrackedHealingCancelled(entity)) {
                     if (!CombatBehaviour.trackedNpcHoldsMainHandItem(entity, Items.f_42589_)) {
                        CombatBehaviour.recoverItemDueToFailure(entity);
                     } else {
                        LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                        if (patch == null) {
                           CombatBehaviour.recoverItemDueToFailure(entity);
                        } else {
                           AssetAccessor<? extends StaticAnimation> currentAnim = Objects.requireNonNull(patch.getAnimator().getPlayerFor(null))
                              .getRealAnimation();
                           if (!(currentAnim.get() instanceof AttackAnimation)
                              && !EpicfightUtil.isLongHitAnimation(currentAnim, patch)
                              && !CombatCommon.canEscape((MobPatch<?>)livingEntityPatch)) {
                              Runnable bite = () -> CombatBehaviour.performDrinkingHealingPotionActionMainhand(entity, levelaccessor, patch);
                              int biteDelay = 4;
                              int totalBites = 7;

                              for (int i = 0; i < totalBites; i++) {
                                 int delay = 4 + i * biteDelay;
                                 new com.pla.annoyingvillagers.util.CombatBehaviour.4.1(this, delay, bite);
                              }

                              new com.pla.annoyingvillagers.util.CombatBehaviour.4.2(this, 4 + totalBites * biteDelay - 1);
                              new com.pla.annoyingvillagers.util.CombatBehaviour.4.3(this, 4 + totalBites * biteDelay);
                           } else {
                              CombatBehaviour.recoverItemDueToFailure(entity);
                           }
                        }
                     }
                  }
               }
            }
         };
      }
   }

   public static void forceLookAt(Entity self, Entity target, float maxYawChange, float maxPitchChange) {
      if (target != null) {
         Vec3 eye = self.m_146892_();
         Vec3 to = target.m_146892_().m_82546_(eye);
         double dx = to.f_82479_;
         double dy = to.f_82480_;
         double dz = to.f_82481_;
         double flat = Math.sqrt(dx * dx + dz * dz);
         float targetYaw = (float)(Mth.m_14136_(dz, dx) * (180.0 / Math.PI)) - 90.0F;
         float targetPitch = (float)(-(Mth.m_14136_(dy, flat) * (180.0 / Math.PI)));
         float yaw = Mth.m_14148_(self.m_146908_(), targetYaw, maxYawChange);
         float pitch = Mth.m_14036_(Mth.m_14148_(self.m_146909_(), targetPitch, maxPitchChange), -90.0F, 90.0F);
         self.m_146922_(yaw);
         self.m_146926_(pitch);
         self.f_19859_ = yaw;
         self.f_19860_ = pitch;
         if (self instanceof Mob mob) {
            mob.f_20883_ = yaw;
            mob.f_20884_ = yaw;
            mob.f_20885_ = yaw;
            mob.f_20886_ = yaw;
         }
      }
   }

   public static double calculateGuardBreakWakeUpChance(LivingEntity entity) {
      float hpPct = entity.m_21223_() / entity.m_21233_();
      double min = (Double)AnnoyingVillagersConfig.MOB_GUARD_BREAK_WAKE_UP_MIN_CHANCE.get();
      double max = (Double)AnnoyingVillagersConfig.MOB_GUARD_BREAK_WAKE_UP_MAX_CHANCE.get();
      if (max < min) {
         double tmp = max;
         max = min;
         min = tmp;
      }

      double chance;
      if (max == min) {
         chance = max;
      } else {
         double t = (1.0 - (double)hpPct) / 0.5;
         t = Mth.m_14008_(t, 0.0, 1.0);
         chance = max - t * (max - min);
      }

      return chance;
   }

   public static void postGuardBreakWakeUp(LivingEntity entity, LivingEntityPatch<?> livingEntityPatch, ServerLevel serverLevel) {
      serverLevel.m_8767_(
         (SimpleParticleType)EpicFightParticles.WHITE_AFTERIMAGE.get(),
         entity.m_20185_(),
         entity.m_20186_(),
         entity.m_20189_(),
         1,
         0.0,
         0.0,
         0.0,
         Double.longBitsToDouble((long)entity.m_19879_())
      );
      entity.m_7292_(new MobEffectInstance(MobEffects.f_19613_, 60, 1, false, false));
      entity.m_7292_(new MobEffectInstance(MobEffects.f_19599_, 60, 1, false, false));
      entity.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 60, 1, false, false));
      double chooseAnimation = new Random().nextDouble(0.0, 1.0);
      if (chooseAnimation <= 0.4) {
         livingEntityPatch.playAnimationSynchronized(Animations.BIPED_KNOCKDOWN_WAKEUP_LEFT, 0.0F);
      } else if (chooseAnimation <= 0.8) {
         livingEntityPatch.playAnimationSynchronized(Animations.BIPED_KNOCKDOWN_WAKEUP_RIGHT, 0.0F);
      } else {
         livingEntityPatch.playAnimationSynchronized(Animations.BIPED_ROLL_BACKWARD, 0.0F);
      }
   }

   static {
      HOSTILE_HEALING_POTION.m_41764_(1);
      HEALING_POTION.m_41764_(1);
   }
}
