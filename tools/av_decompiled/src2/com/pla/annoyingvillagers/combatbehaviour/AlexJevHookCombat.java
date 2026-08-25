package com.pla.annoyingvillagers.combatbehaviour;

import com.pla.annoyingvillagers.entity.AlexEntity;
import com.pla.annoyingvillagers.entity.JevEntity;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.item.HookGunItem;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.HookUtil;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public final class AlexJevHookCombat {
   private static final String KEY_SESSION_ACTIVE = "AlexJevHookSessionActive";
   private static final String KEY_SESSION_STARTED_AT = "AlexJevHookSessionStartedAt";
   private static final String KEY_ORIGINAL_MAINHAND = "AlexJevHookOriginalMainhand";
   private static final String KEY_ORIGINAL_OFFHAND = "AlexJevHookOriginalOffhand";
   private static final String KEY_SAVED_MAINHAND = "AlexJevHookSavedMainhand";
   private static final String KEY_SAVED_OFFHAND = "AlexJevHookSavedOffhand";
   private static final String KEY_ALEX_COOLDOWN_UNTIL = "AlexHookCooldownUntil";
   private static final String KEY_JEV_COOLDOWN_UNTIL = "JevHookCooldownUntil";
   private static final String KEY_JEV_RUN_AWAY_UNTIL = "JevAlexDeathRunAwayUntil";
   private static final String KEY_LAST_MAINHAND_HOOK_BOUND_ITEM = "AlexJevLastMainhandHookBoundItem";
   private static final String KEY_LAST_OFFHAND_HOOK_BOUND_ITEM = "AlexJevLastOffhandHookBoundItem";
   private static final String KEY_ALEX_SWORD_HOOK_BURST_REMAINING = "AlexSwordHookBurstRemaining";
   private static final int SHOOT_DELAY_TICKS = 7;
   private static final int DEFAULT_RETRIEVE_DELAY_TICKS = 44;
   private static final int DEFAULT_RESTORE_DELAY_TICKS = 58;
   private static final int GRAPPLE_RETRIEVE_DELAY_TICKS = 54;
   private static final int GRAPPLE_RESTORE_DELAY_TICKS = 70;
   private static final long HOOK_SESSION_RESTORE_WITHOUT_HOOK_TICKS = 100L;
   private static final int PICKAXE_HOOK_ATTACH_TIMEOUT_TICKS = 60;
   private static final int HOOK_SESSION_ABSOLUTE_RESTORE_TICKS = 140;
   private static final int ALEX_MIN_COOLDOWN_TICKS = 90;
   private static final int ALEX_RANDOM_COOLDOWN_TICKS = 80;
   private static final int JEV_MIN_COOLDOWN_TICKS = 25;
   private static final int JEV_RANDOM_COOLDOWN_TICKS = 35;
   private static final int HOOK_SEARCH_RADIUS = 30;
   private static final int HOOK_SEARCH_MIN_HORIZONTAL_DISTANCE_SQR = 100;
   private static final int HOOK_SEARCH_FALLBACK_MIN_HORIZONTAL_DISTANCE_SQR = 36;
   private static final double HOOK_SEARCH_IDEAL_DISTANCE = 22.0;
   private static final int GROUND_HOOK_MIN_HORIZONTAL_DISTANCE_SQR = 100;
   private static final int GROUND_HOOK_MAX_HORIZONTAL_DISTANCE_SQR = 784;
   private static final double GROUND_HOOK_IDEAL_DISTANCE = 22.0;
   private static final double MAX_HOOK_TARGET_DISTANCE_SQR = 1156.0;
   private static final double PICKAXE_ENTITY_PULL_MAX_DISTANCE_SQR = 484.0;
   private static final double ALEX_PICKAXE_ENTITY_PULL_MIN_DISTANCE_SQR = 16.0;
   private static final double JEV_PICKAXE_ENTITY_PULL_MIN_DISTANCE_SQR = 49.0;
   private static final double ALEX_PICKAXE_ENTITY_PULL_CHANCE = 0.38;
   private static final double JEV_PICKAXE_ENTITY_PULL_CHANCE = 0.24;
   private static final double SAFE_PLACE_PULL_RADIUS = 3.0;
   private static final double SAFE_PLACE_PULL_MIN_DISTANCE_SQR = 9.0;
   private static final double ALEX_PULL_JEV_TO_SAFE_PLACE_CHANCE = 0.18;
   private static final double JEV_PULL_ALEX_TO_SAFE_PLACE_CHANCE = 0.22;
   private static final double ALEX_SWORD_HOOK_BURST_STATE_ZERO_MIN_DISTANCE_SQR = 121.0;
   private static final double ALEX_SWORD_HOOK_BURST_STATE_ONE_MIN_DISTANCE_SQR = 25.0;
   private static final double ALEX_SWORD_HOOK_BURST_STATE_ZERO_START_CHANCE = 0.24;
   private static final double ALEX_SWORD_HOOK_BURST_STATE_ONE_START_CHANCE = 0.86;
   private static final int ALEX_SWORD_HOOK_BURST_STATE_ZERO_MIN_SHOTS = 2;
   private static final int ALEX_SWORD_HOOK_BURST_STATE_ZERO_RANDOM_SHOTS = 1;
   private static final int ALEX_SWORD_HOOK_BURST_STATE_ONE_MIN_SHOTS = 4;
   private static final int ALEX_SWORD_HOOK_BURST_STATE_ONE_RANDOM_SHOTS = 3;

   private AlexJevHookCombat() {
   }

   public static void tickAlex(MobPatch<?> mobPatch) {
      if (mobPatch.getOriginal() instanceof AlexEntity alex && alex.m_9236_() instanceof ServerLevel serverLevel && alex.m_6084_()) {
         alex.ensureHookGunInventory();
         syncAlexAndJevTarget(alex, alex.getProtectingJev());
         cleanupFinishedSession(alex);
         LivingEntity target = alex.m_5448_();
         if (target != null && target.m_6084_() && !(alex.m_20280_(target) > 1156.0)) {
            if (!isHookSessionActive(alex) && !HookGunItem.hasActiveHook(alex.m_9236_(), alex)) {
               if (CombatCommon.canPerformNormalAttackLogic(mobPatch) && serverLevel.m_46467_() >= alex.getPersistentData().m_128454_("AlexHookCooldownUntil")) {
                  if (tryAlexSwordHookBurst(alex, target)) {
                     setCooldown(alex, "AlexHookCooldownUntil", 18, 12);
                     return;
                  }

                  JevEntity jev = alex.getProtectingJev();
                  if (jev != null && jev.m_6084_() && tryPullPartnerToSafePlace(alex, jev, createAlexDefaultPickaxe(), 0.18)) {
                     setCooldown(alex, "AlexHookCooldownUntil", 110, 80);
                     return;
                  }

                  if (alex.getState() == 1 && alex.canDualHookInSecondPhase() && alex.m_217043_().m_188500_() < 0.24 && performAlexDualHook(alex, target)) {
                     setCooldown(alex, "AlexHookCooldownUntil", 130, 80);
                     return;
                  }

                  if (alex.getState() == 1 && alex.m_217043_().m_188500_() < 0.12 && performAlexFlintHook(alex, target)) {
                     setCooldown(alex, "AlexHookCooldownUntil", 135, 80);
                     return;
                  }

                  maybeSwitchAlexBoundHook(alex);
                  ItemStack bound = alex.getCurrentBoundHook();
                  if (bound.m_41619_()) {
                     bound = createAlexDefaultPickaxe();
                     alex.setCurrentBoundHook(bound);
                  }

                  boolean fired;
                  if (HookUtil.isPickaxe(bound)) {
                     fired = shouldTryPickaxeEntityPull(alex, target, 0.38, 16.0)
                        && shootPickaxeHookAtEntity(alex, InteractionHand.OFF_HAND, bound, target, alex::setCurrentBoundHook);
                     if (!fired) {
                        Vec3 anchor = findHookAnchor(alex, target, false);
                        fired = anchor != null && shootHook(alex, InteractionHand.OFF_HAND, bound, () -> anchor, 54, 70, alex::setCurrentBoundHook);
                     }
                  } else {
                     fired = shootHookAtEntity(alex, InteractionHand.OFF_HAND, bound, target, 44, 58, alex::setCurrentBoundHook);
                  }

                  if (fired) {
                     setCooldown(alex, "AlexHookCooldownUntil", 90, 80);
                  }

                  return;
               }

               return;
            }

            return;
         }

         return;
      }
   }

   public static void tickJev(MobPatch<?> mobPatch) {
      if (mobPatch.getOriginal() instanceof JevEntity jev && jev.m_9236_() instanceof ServerLevel serverLevel && jev.m_6084_()) {
         AlexEntity alex = jev.getFollowTarget();
         cleanupFinishedSession(jev);
         moveJevAroundPartner(jev, alex);
         if (alex != null && alex.m_6084_()) {
            LivingEntity alexTarget = alex.m_5448_();
            if (alexTarget != null && alexTarget.m_6084_()) {
               syncAlexAndJevTarget(alex, jev);
               if (jev.getPersistentData().m_128454_("JevAlexDeathRunAwayUntil") > serverLevel.m_46467_()) {
                  tryJevHookAway(jev, alex);
               }

               if (!isHookSessionActive(jev)
                  && !HookGunItem.hasActiveHook(jev.m_9236_(), jev)
                  && serverLevel.m_46467_() >= jev.getPersistentData().m_128454_("JevHookCooldownUntil")) {
                  LivingEntity target = jev.m_5448_() != null && jev.m_5448_().m_6084_() ? jev.m_5448_() : alexTarget;
                  Random random = new Random();
                  if (alex.m_6060_() && shootJevBurningSupportSnowball(jev, alex)) {
                     setCooldown(jev, "JevHookCooldownUntil", 30, 20);
                     return;
                  }

                  if (alex.m_21223_() <= alex.m_21233_() * 0.55F
                     && shootHookAtEntity(jev, InteractionHand.OFF_HAND, createStrongHealingPotion(), alex, 44, 58, null)) {
                     setCooldown(jev, "JevHookCooldownUntil", 30, 25);
                     return;
                  }

                  if (isValidJevEnemyTarget(jev, alex, target)
                     && isHoldingBowLike(target)
                     && random.nextDouble() < 0.82
                     && shootJevCoverBlock(jev, alex, target)) {
                     setCooldown(jev, "JevHookCooldownUntil", 18, 18);
                     return;
                  }

                  if (target != null && target.m_6084_() && target.m_20280_(jev) < 64.0 && random.nextDouble() < 0.85 && tryJevHookAway(jev, target)) {
                     setCooldown(jev, "JevHookCooldownUntil", 25, 20);
                     return;
                  }

                  if (tryPullPartnerToSafePlace(jev, alex, createJevPickaxe(), 0.22)) {
                     setCooldown(jev, "JevHookCooldownUntil", 26, 28);
                     return;
                  }

                  if (isValidJevEnemyTarget(jev, alex, target)
                     && shouldTryPickaxeEntityPull(jev, target, 0.24, 49.0)
                     && shootPickaxeHookAtEntity(jev, InteractionHand.OFF_HAND, createJevPickaxe(), target, null)) {
                     setCooldown(jev, "JevHookCooldownUntil", 24, 26);
                     return;
                  }

                  if (isValidJevEnemyTarget(jev, alex, target) && random.nextDouble() < 0.62 && shootJevEnemyHarassment(jev, target, random)) {
                     setCooldown(jev, "JevHookCooldownUntil", 22, 22);
                     return;
                  }

                  if (isValidJevEnemyTarget(jev, alex, target) && random.nextDouble() < 0.38 && shootJevEnemyDistractionBlock(jev, target)) {
                     setCooldown(jev, "JevHookCooldownUntil", 20, 24);
                     return;
                  }

                  if (random.nextDouble() < 0.42 && shootJevBoneMealSapling(jev)) {
                     setCooldown(jev, "JevHookCooldownUntil", 18, 22);
                     return;
                  }

                  if (isMissingHealth(alex) && random.nextDouble() < 0.42 && shootJevSupportFood(jev, alex, random)) {
                     setCooldown(jev, "JevHookCooldownUntil", 18, 20);
                     return;
                  }

                  if (isMissingHealth(alex) && random.nextDouble() < 0.45 && shootJevSupportPotion(jev, alex, random)) {
                     setCooldown(jev, "JevHookCooldownUntil", 24, 24);
                     return;
                  }

                  if (isMissingHealth(alex)
                     && (alex.m_21223_() <= alex.m_21233_() * 0.75F || random.nextDouble() < 0.18)
                     && shootHookAtEntity(jev, InteractionHand.OFF_HAND, new ItemStack(Items.f_42436_), alex, 44, 58, null)) {
                     setCooldown(jev, "JevHookCooldownUntil", 35, 35);
                     return;
                  }

                  if (alex.getState() == 1
                     && alex.m_6844_(EquipmentSlot.HEAD).m_41619_()
                     && random.nextDouble() < 0.35
                     && shootHookAtEntity(jev, InteractionHand.OFF_HAND, createAlexHelmet(), alex, 44, 58, null)) {
                     setCooldown(jev, "JevHookCooldownUntil", 80, 50);
                     return;
                  }

                  if (random.nextDouble() < 0.65 && tryJevHookAway(jev, (LivingEntity)(target != null && target.m_6084_() ? target : alex))) {
                     setCooldown(jev, "JevHookCooldownUntil", 20, 25);
                     return;
                  }

                  if (alex.getState() == 1
                     && isMissingHealth(alex)
                     && random.nextDouble() < 0.22
                     && shootHookAtEntity(jev, InteractionHand.OFF_HAND, createGoodBuffPotion(), alex, 44, 58, null)) {
                     setCooldown(jev, "JevHookCooldownUntil", 55, 35);
                     return;
                  }

                  if (random.nextDouble() < 0.45 && shootJevSupportBlock(jev, alex)) {
                     setCooldown(jev, "JevHookCooldownUntil", 25, 35);
                     return;
                  }

                  if (isMissingHealth(alex) && random.nextDouble() < 0.55 && shootJevSupportFood(jev, alex, random)) {
                     setCooldown(jev, "JevHookCooldownUntil", 18, 20);
                  }

                  return;
               }

               return;
            }

            return;
         }

         return;
      }
   }

   public static void onAlexDeath(AlexEntity alex) {
      JevEntity jev = alex.getProtectingJev();
      if (jev != null && jev.m_6084_() && alex.m_9236_() instanceof ServerLevel serverLevel) {
         jev.getPersistentData().m_128356_("JevAlexDeathRunAwayUntil", serverLevel.m_46467_() + 360L);
         shootHook(jev, InteractionHand.OFF_HAND, createJevPickaxe(), alex::m_146892_, 54, 70, null);
      }
   }

   public static void onJevDeath(JevEntity jev) {
      AlexEntity alex = jev.getFollowTarget();
      if (alex != null && alex.m_6084_()) {
         shootHook(alex, InteractionHand.OFF_HAND, createAlexDefaultPickaxe(), jev::m_146892_, 54, 70, null);
      }
   }

   public static ItemStack createBoundHookGun(ItemStack boundItem) {
      ItemStack hookGun = new ItemStack((ItemLike)AnnoyingVillagersModItems.HOOK_GUN.get());
      HookGunItem.setBoundItem(hookGun, boundItem);
      return hookGun;
   }

   public static ItemStack createAlexDefaultPickaxe() {
      ItemStack pickaxe = new ItemStack(Items.f_42385_);
      pickaxe.m_41663_(Enchantments.f_44962_, 1);
      pickaxe.m_41663_(Enchantments.f_44986_, 3);
      pickaxe.m_41663_(Enchantments.f_44984_, 3);
      return pickaxe;
   }

   public static ItemStack createAlexHookSword() {
      ItemStack sword = new ItemStack(Items.f_42388_);
      sword.m_41663_(Enchantments.f_44977_, 5);
      sword.m_41663_(Enchantments.f_44978_, 5);
      return sword;
   }

   public static ItemStack createJevPickaxe() {
      return new ItemStack(Items.f_42385_);
   }

   public static ItemStack createAlexHelmet() {
      ItemStack helmet = new ItemStack(Items.f_42472_);
      helmet.m_41663_(Enchantments.f_44965_, 4);
      helmet.m_41663_(Enchantments.f_44986_, 3);
      return helmet;
   }

   public static ItemStack createRandomJevLootBlock(RandomSource random) {
      return random.m_188499_() ? randomCoverBlock(random) : randomDistractionBlock(random);
   }

   public static ItemStack createRandomJevLootFood(RandomSource random) {
      return switch (random.m_188503_(9)) {
         case 0 -> new ItemStack(Items.f_42406_);
         case 1 -> new ItemStack(Items.f_42620_);
         case 2 -> new ItemStack(Items.f_42580_);
         case 3 -> new ItemStack(Items.f_42582_);
         case 4 -> new ItemStack(Items.f_42619_);
         case 5 -> new ItemStack(Items.f_42436_);
         case 6 -> new ItemStack(Items.f_42437_);
         case 7 -> new ItemStack(Items.f_42675_);
         default -> new ItemStack(Items.f_42529_);
      };
   }

   public static ItemStack createRandomJevLootPotion(RandomSource random) {
      return switch (random.m_188503_(13)) {
         case 0 -> createStrongHealingPotion();
         case 1 -> PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43592_);
         case 2 -> PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43614_);
         case 3 -> PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43609_);
         case 4 -> createHastePotion();
         case 5 -> createGoodBuffPotion();
         case 6 -> PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43589_);
         case 7 -> createPoisonPotion();
         case 8 -> createWeaknessPotion();
         case 9 -> createSlownessPotion();
         case 10 -> createNauseaPotion();
         case 11 -> createBlindnessPotion();
         default -> createStrongHarmingPotion();
      };
   }

   public static ItemStack createRandomJevPlantLoot(RandomSource random) {
      return switch (random.m_188503_(14)) {
         case 0, 1, 2 -> new ItemStack(Items.f_42499_);
         case 3 -> new ItemStack(Blocks.f_50112_);
         case 4 -> new ItemStack(Blocks.f_50111_);
         case 5 -> new ItemStack(Blocks.f_50113_);
         case 6 -> new ItemStack(Blocks.f_50114_);
         case 7 -> new ItemStack(Blocks.f_50746_);
         case 8 -> new ItemStack(Blocks.f_50747_);
         case 9 -> new ItemStack(Blocks.f_50748_);
         case 10 -> new ItemStack(Blocks.f_50749_);
         case 11 -> new ItemStack(Blocks.f_50750_);
         case 12 -> new ItemStack(Blocks.f_50751_);
         default -> new ItemStack(Blocks.f_271334_);
      };
   }

   private static void maybeSwitchAlexBoundHook(AlexEntity alex) {
      ItemStack current = alex.getCurrentBoundHook();
      double roll = alex.m_217043_().m_188500_();
      if (HookUtil.isPickaxe(current)) {
         if (roll > 0.22) {
            return;
         }

         alex.setCurrentBoundHook(createAlexHookSword());
      } else {
         if (!(roll < 0.34)) {
            return;
         }

         alex.setCurrentBoundHook(createAlexDefaultPickaxe());
      }

      playHookGunAnimation(alex);
      rememberLastHookBoundItem(alex, InteractionHand.OFF_HAND, alex.getCurrentBoundHook());
      alex.m_9236_().m_6263_(null, alex.m_20185_(), alex.m_20186_(), alex.m_20189_(), SoundEvents.f_11678_, SoundSource.HOSTILE, 0.7F, 1.2F);
   }

   private static boolean tryAlexSwordHookBurst(AlexEntity alex, LivingEntity target) {
      CompoundTag data = alex.getPersistentData();
      int remaining = data.m_128451_("AlexSwordHookBurstRemaining");
      double distanceSqr = alex.m_20280_(target);
      if (!canAlexUseSwordHookBurst(alex, target, distanceSqr)) {
         data.m_128473_("AlexSwordHookBurstRemaining");
         return false;
      } else {
         if (remaining <= 0) {
            if (alex.m_217043_().m_188500_() >= getAlexSwordHookBurstStartChance(alex)) {
               return false;
            }

            remaining = getAlexSwordHookBurstMinShots(alex) + alex.m_217043_().m_188503_(getAlexSwordHookBurstRandomShots(alex) + 1);
            data.m_128405_("AlexSwordHookBurstRemaining", remaining);
         }

         ItemStack sword = createAlexHookSword();
         alex.setCurrentBoundHook(sword);
         boolean fired = shootHookAtEntity(alex, InteractionHand.OFF_HAND, sword, target, 44, 58, updatedBound -> alex.setCurrentBoundHook(sword));
         if (!fired) {
            return false;
         } else {
            if (--remaining <= 0) {
               data.m_128473_("AlexSwordHookBurstRemaining");
            } else {
               data.m_128405_("AlexSwordHookBurstRemaining", remaining);
            }

            return true;
         }
      }
   }

   private static boolean canAlexUseSwordHookBurst(AlexEntity alex, LivingEntity target, double distanceSqr) {
      if (!alex.m_142582_(target)) {
         return false;
      } else if (alex.getState() != 0 || !(alex.m_21223_() <= alex.m_21233_() * 0.45F) && !alex.m_21206_().m_150930_(Items.f_42747_)) {
         double minDistanceSqr = alex.getState() == 1 ? 25.0 : 121.0;
         return distanceSqr >= minDistanceSqr;
      } else {
         return false;
      }
   }

   private static double getAlexSwordHookBurstStartChance(AlexEntity alex) {
      return alex.getState() == 1 ? 0.86 : 0.24;
   }

   private static int getAlexSwordHookBurstMinShots(AlexEntity alex) {
      return alex.getState() == 1 ? 4 : 2;
   }

   private static int getAlexSwordHookBurstRandomShots(AlexEntity alex) {
      return alex.getState() == 1 ? 3 : 1;
   }

   private static boolean performAlexDualHook(AlexEntity alex, LivingEntity target) {
      if (!isHookSessionActive(alex) && !HookGunItem.hasActiveHook(alex.m_9236_(), alex)) {
         double roll = alex.m_217043_().m_188500_();
         ItemStack leftBound;
         ItemStack rightBound;
         Supplier<Vec3> leftTarget;
         Supplier<Vec3> rightTarget;
         if (roll < 0.18 && canPickaxeEntityPullTarget(alex, target, 16.0)) {
            leftBound = createAlexDefaultPickaxe();
            rightBound = createAlexHookSword();
            leftTarget = () -> target.m_146892_().m_82520_(0.35, 0.0, 0.0);
            rightTarget = () -> target.m_146892_().m_82520_(-0.35, 0.0, 0.0);
         } else if (roll < 0.34) {
            leftBound = createAlexDefaultPickaxe();
            rightBound = createAlexHookSword();
            Vec3 anchor = findHookAnchor(alex, target, false);
            if (anchor == null) {
               return false;
            }

            leftTarget = () -> anchor;
            rightTarget = () -> target.m_146892_();
         } else if (roll < 0.56) {
            leftBound = createAlexHookSword();
            rightBound = createAlexHookSword();
            leftTarget = () -> target.m_146892_().m_82520_(0.35, 0.0, 0.0);
            rightTarget = () -> target.m_146892_().m_82520_(-0.35, 0.0, 0.0);
         } else if (roll < 0.78) {
            leftBound = createAlexDefaultPickaxe();
            rightBound = createAlexDefaultPickaxe();
            Vec3 leftAnchor = findHookAnchor(alex, target, false);
            Vec3 rightAnchor = findHookAnchor(alex, target, true);
            if (leftAnchor == null || rightAnchor == null) {
               return false;
            }

            leftTarget = () -> leftAnchor;
            rightTarget = () -> rightAnchor;
         } else {
            BlockPos support = findPlacementSupportBlock(target.m_9236_(), target.m_20183_());
            if (support == null) {
               return false;
            }

            leftBound = new ItemStack(Items.f_42448_);
            rightBound = new ItemStack(Items.f_42447_);
            leftTarget = () -> Vec3.m_82512_(support);
            rightTarget = () -> Vec3.m_82512_(support.m_121945_(Plane.HORIZONTAL.m_235690_(alex.m_217043_())));
         }

         return shootDualHook(alex, leftBound, leftTarget, rightBound, rightTarget, 54, 70);
      } else {
         return false;
      }
   }

   private static boolean performAlexFlintHook(AlexEntity alex, LivingEntity target) {
      return shootHookAtEntity(alex, InteractionHand.OFF_HAND, new ItemStack(Items.f_42409_), target, 44, 58, null);
   }

   private static boolean shootJevBurningSupportSnowball(JevEntity jev, AlexEntity alex) {
      return shootHookAtEntity(jev, InteractionHand.OFF_HAND, new ItemStack(Items.f_42452_), alex, 44, 58, null);
   }

   private static boolean shootPickaxeHookAtEntity(
      LivingEntity shooter, InteractionHand hand, ItemStack pickaxe, LivingEntity target, @Nullable Consumer<ItemStack> completion
   ) {
      return shootHookAtEntity(shooter, hand, pickaxe, target, 54, 70, completion);
   }

   private static boolean tryPullPartnerToSafePlace(LivingEntity puller, LivingEntity partner, ItemStack pickaxe, double chance) {
      return puller.m_217043_().m_188500_() < chance
         && canPullPartnerToSafePlace(puller, partner)
         && shootPickaxeHookAtEntity(puller, InteractionHand.OFF_HAND, pickaxe, partner, null);
   }

   private static boolean canPullPartnerToSafePlace(LivingEntity puller, @Nullable LivingEntity partner) {
      if (partner != null && partner.m_6084_() && !partner.m_5833_() && partner != puller && puller.m_142582_(partner) && !hasNearbyEnemyTargeting(puller, 3.0)
         )
       {
         double distanceSqr = puller.m_20280_(partner);
         return distanceSqr >= 9.0 && distanceSqr <= 484.0;
      } else {
         return false;
      }
   }

   private static boolean hasNearbyEnemyTargeting(LivingEntity entity, double radius) {
      return !entity.m_9236_()
         .m_6443_(
            Mob.class,
            entity.m_20191_().m_82400_(radius),
            mob -> mob != entity && mob.m_6084_() && !mob.m_5833_() && mob.m_5448_() == entity && !entity.m_7307_(mob) && !mob.m_7307_(entity)
         )
         .isEmpty();
   }

   private static boolean tryJevHookAway(JevEntity jev, @Nullable LivingEntity awayFrom) {
      if (!isHookSessionActive(jev) && !HookGunItem.hasActiveHook(jev.m_9236_(), jev)) {
         Vec3 anchor = findHookAnchor(jev, awayFrom, true);
         if (anchor == null) {
            anchor = findNearbyGroundHookAnchor(jev, awayFrom);
         }

         if (anchor == null) {
            return false;
         } else {
            Vec3 selectedAnchor = anchor;
            return shootHook(jev, InteractionHand.OFF_HAND, createJevPickaxe(), () -> selectedAnchor, 54, 70, null);
         }
      } else {
         return false;
      }
   }

   private static boolean shootJevSupportBlock(JevEntity jev, AlexEntity alex) {
      return shootJevBlockAtArea(jev, alex.m_20183_(), 5, true);
   }

   private static boolean shootJevCoverBlock(JevEntity jev, AlexEntity alex, LivingEntity enemy) {
      return shootJevBlockAtArea(jev, getAlexCoverBlockCenter(alex, enemy), 3, true);
   }

   private static boolean shootJevEnemyDistractionBlock(JevEntity jev, LivingEntity target) {
      return shootJevBlockAtArea(jev, target.m_20183_(), 4, false);
   }

   private static boolean shootJevBlockAtArea(JevEntity jev, BlockPos center, int radius, boolean coverBlock) {
      for (int attempt = 0; attempt < 8; attempt++) {
         ItemStack block = coverBlock ? randomCoverBlock(jev.m_217043_()) : randomDistractionBlock(jev.m_217043_());
         BlockPos support = findSupportBlockAround(jev.m_9236_(), center, radius, block);
         if (support != null) {
            return shootHook(jev, InteractionHand.OFF_HAND, block, () -> getGroundHookAnchorAimPosition(support), 44, 58, null);
         }
      }

      return false;
   }

   private static boolean shootJevBoneMealSapling(JevEntity jev) {
      BlockPos sapling = findVisibleSaplingForBoneMeal(jev);
      if (sapling == null) {
         return false;
      } else {
         Vec3 aim = Vec3.m_82512_(sapling);
         return shootHook(jev, InteractionHand.OFF_HAND, new ItemStack(Items.f_42499_), () -> aim, 44, 58, null);
      }
   }

   private static boolean shootJevSupportFood(JevEntity jev, AlexEntity alex, Random random) {
      return shootHookAtEntity(jev, InteractionHand.OFF_HAND, randomFood(random), alex, 44, 58, null);
   }

   private static boolean shootJevSupportPotion(JevEntity jev, AlexEntity alex, Random random) {
      return shootHookAtEntity(jev, InteractionHand.OFF_HAND, randomPositivePotion(random), alex, 44, 58, null);
   }

   private static boolean shootJevEnemyHarassment(JevEntity jev, LivingEntity target, Random random) {
      return shootHookAtEntity(jev, InteractionHand.OFF_HAND, randomEnemyHarassItem(random), target, 44, 58, null);
   }

   private static boolean isValidJevEnemyTarget(JevEntity jev, AlexEntity alex, @Nullable LivingEntity target) {
      return target != null && target.m_6084_() && !target.m_5833_() && target != jev && target != alex && !target.m_7307_(jev) && !target.m_7307_(alex);
   }

   private static boolean isMissingHealth(LivingEntity entity) {
      return entity.m_21223_() < entity.m_21233_() - 0.5F;
   }

   private static boolean isHoldingBowLike(LivingEntity entity) {
      return isBowLike(entity.m_21205_()) || isBowLike(entity.m_21206_());
   }

   private static boolean isBowLike(ItemStack stack) {
      return stack.m_41720_() instanceof BowItem || stack.m_41720_() instanceof CrossbowItem;
   }

   private static boolean shouldTryPickaxeEntityPull(LivingEntity shooter, LivingEntity target, double chance, double minDistanceSqr) {
      return shooter.m_217043_().m_188500_() < chance && canPickaxeEntityPullTarget(shooter, target, minDistanceSqr);
   }

   private static boolean canPickaxeEntityPullTarget(LivingEntity shooter, @Nullable LivingEntity target, double minDistanceSqr) {
      if (target != null
         && target.m_6084_()
         && !target.m_5833_()
         && target != shooter
         && !shooter.m_7307_(target)
         && !target.m_7307_(shooter)
         && shooter.m_142582_(target)) {
         double distanceSqr = shooter.m_20280_(target);
         return distanceSqr >= minDistanceSqr && distanceSqr <= 484.0;
      } else {
         return false;
      }
   }

   private static boolean shootHookAtEntity(
      LivingEntity shooter,
      InteractionHand hand,
      ItemStack boundItem,
      LivingEntity target,
      int retrieveDelayTicks,
      int restoreDelayTicks,
      @Nullable Consumer<ItemStack> completion
   ) {
      return shootHook(shooter, hand, boundItem, target::m_146892_, retrieveDelayTicks, restoreDelayTicks, completion);
   }

   private static boolean shootHook(
      LivingEntity shooter,
      InteractionHand hand,
      ItemStack boundItem,
      Supplier<Vec3> targetSupplier,
      int retrieveDelayTicks,
      int restoreDelayTicks,
      @Nullable Consumer<ItemStack> completion
   ) {
      return shootHook(shooter, hand, boundItem, targetSupplier, retrieveDelayTicks, restoreDelayTicks, completion, true);
   }

   private static boolean shootHook(
      final LivingEntity shooter,
      final InteractionHand hand,
      ItemStack boundItem,
      final Supplier<Vec3> targetSupplier,
      int retrieveDelayTicks,
      int restoreDelayTicks,
      @Nullable Consumer<ItemStack> completion,
      boolean allowHookGunAnimation
   ) {
      if (!boundItem.m_41619_() && !shooter.m_9236_().f_46443_ && !isHookSessionActive(shooter) && !HookGunItem.hasActiveHook(shooter.m_9236_(), shooter)) {
         boolean playAnimation = allowHookGunAnimation && shouldPlayHookGunAnimationForHand(shooter, hand, boundItem);
         beginHookSession(shooter, hand == InteractionHand.MAIN_HAND, hand == InteractionHand.OFF_HAND);
         shooter.m_21008_(hand, createBoundHookGun(boundItem));
         shooter.m_21011_(hand, true);
         if (playAnimation) {
            playHookGunAnimation(shooter);
         }

         new DelayedTask(7) {
            public void run() {
               if (shooter.m_6084_() && AlexJevHookCombat.isHookSessionActive(shooter)) {
                  ItemStack hookGun = shooter.m_21120_(hand);
                  if (!(hookGun.m_41720_() instanceof HookGunItem)) {
                     AlexJevHookCombat.restoreHookSession(shooter);
                  } else {
                     Vec3 target = targetSupplier.get();
                     if (target == null) {
                        AlexJevHookCombat.restoreHookSession(shooter);
                     } else {
                        AlexJevHookCombat.aimAt(shooter, target);
                        ItemStack currentBound = HookGunItem.getBoundItem(hookGun);
                        HookGunItem.launchHookAt(shooter.m_9236_(), shooter, target, false, hand == InteractionHand.MAIN_HAND, currentBound);
                        shooter.m_9236_()
                           .m_6263_(null, shooter.m_20185_(), shooter.m_20186_(), shooter.m_20189_(), SoundEvents.f_11687_, SoundSource.HOSTILE, 0.9F, 1.35F);
                     }
                  }
               }
            }
         };
         scheduleRetrieveAndRestore(shooter, hand, HookUtil.isPickaxe(boundItem), completion);
         return true;
      } else {
         return false;
      }
   }

   private static boolean shootDualHook(
      final LivingEntity shooter,
      ItemStack leftBoundItem,
      final Supplier<Vec3> leftTargetSupplier,
      ItemStack rightBoundItem,
      final Supplier<Vec3> rightTargetSupplier,
      int retrieveDelayTicks,
      int restoreDelayTicks
   ) {
      if (!leftBoundItem.m_41619_()
         && !rightBoundItem.m_41619_()
         && !shooter.m_9236_().f_46443_
         && !isHookSessionActive(shooter)
         && !HookGunItem.hasActiveHook(shooter.m_9236_(), shooter)) {
         beginHookSession(shooter, true, true);
         boolean playLeftAnimation = shouldPlayHookGunAnimationForHand(shooter, InteractionHand.OFF_HAND, leftBoundItem);
         boolean playRightAnimation = shouldPlayHookGunAnimationForHand(shooter, InteractionHand.MAIN_HAND, rightBoundItem);
         shooter.m_21008_(InteractionHand.OFF_HAND, createBoundHookGun(leftBoundItem));
         shooter.m_21008_(InteractionHand.MAIN_HAND, createBoundHookGun(rightBoundItem));
         shooter.m_21011_(InteractionHand.OFF_HAND, true);
         shooter.m_21011_(InteractionHand.MAIN_HAND, true);
         if (playLeftAnimation || playRightAnimation) {
            playHookGunAnimation(shooter);
         }

         new DelayedTask(7) {
            public void run() {
               if (shooter.m_6084_() && AlexJevHookCombat.isHookSessionActive(shooter)) {
                  Vec3 leftTarget = leftTargetSupplier.get();
                  Vec3 rightTarget = rightTargetSupplier.get();
                  if (leftTarget != null && rightTarget != null) {
                     ItemStack leftHookGun = shooter.m_21206_();
                     ItemStack rightHookGun = shooter.m_21205_();
                     if (leftHookGun.m_41720_() instanceof HookGunItem && rightHookGun.m_41720_() instanceof HookGunItem) {
                        AlexJevHookCombat.aimAt(shooter, rightTarget);
                        HookGunItem.launchHookAt(shooter.m_9236_(), shooter, leftTarget, true, false, HookGunItem.getBoundItem(leftHookGun));
                        HookGunItem.launchHookAt(shooter.m_9236_(), shooter, rightTarget, true, true, HookGunItem.getBoundItem(rightHookGun));
                        shooter.m_9236_()
                           .m_6263_(null, shooter.m_20185_(), shooter.m_20186_(), shooter.m_20189_(), SoundEvents.f_11687_, SoundSource.HOSTILE, 1.0F, 1.25F);
                     } else {
                        AlexJevHookCombat.restoreHookSession(shooter);
                     }
                  } else {
                     AlexJevHookCombat.restoreHookSession(shooter);
                  }
               }
            }
         };
         scheduleRetrieveAndRestore(shooter, InteractionHand.MAIN_HAND, HookUtil.isPickaxe(leftBoundItem) || HookUtil.isPickaxe(rightBoundItem), null);
         return true;
      } else {
         return false;
      }
   }

   private static void scheduleRetrieveAndRestore(
      final LivingEntity shooter, final InteractionHand hand, final boolean waitForPickaxeHook, @Nullable final Consumer<ItemStack> completion
   ) {
      new DelayedTask(8) {
         public void run() {
            AlexJevHookCombat.scheduleHookSessionMonitor(shooter, hand, waitForPickaxeHook, completion, 8);
         }
      };
   }

   private static void scheduleHookSessionMonitor(
      final LivingEntity shooter,
      final InteractionHand hand,
      final boolean waitForPickaxeHook,
      @Nullable final Consumer<ItemStack> completion,
      final int waitedTicks
   ) {
      new DelayedTask(1) {
         public void run() {
            if (shooter.m_6084_() && AlexJevHookCombat.isHookSessionActive(shooter)) {
               boolean hasActiveHook = HookGunItem.hasActiveHook(shooter.m_9236_(), shooter);
               if (!hasActiveHook && waitedTicks >= 7) {
                  AlexJevHookCombat.completeAndRestoreHookSession(shooter, hand, completion);
               } else {
                  if (waitForPickaxeHook
                     && waitedTicks >= 60
                     && HookGunItem.hasActiveGrappleHook(shooter.m_9236_(), shooter)
                     && !HookGunItem.hasAttachedGrappleHook(shooter.m_9236_(), shooter)) {
                     HookGunItem.returnActiveHooks(shooter.m_9236_(), shooter, true);
                  }

                  if (waitForPickaxeHook && waitedTicks >= 140 && HookGunItem.hasActiveGrappleHook(shooter.m_9236_(), shooter)) {
                     HookGunItem.returnActiveHooks(shooter.m_9236_(), shooter, true);
                  }

                  AlexJevHookCombat.scheduleHookSessionMonitor(shooter, hand, waitForPickaxeHook, completion, waitedTicks + 1);
               }
            }
         }
      };
   }

   private static void completeAndRestoreHookSession(LivingEntity shooter, InteractionHand hand, @Nullable Consumer<ItemStack> completion) {
      ItemStack hookGun = shooter.m_21120_(hand);
      if (completion != null && hookGun.m_41720_() instanceof HookGunItem) {
         ItemStack updatedBound = HookGunItem.getBoundItem(hookGun);
         if (!updatedBound.m_41619_()) {
            completion.accept(updatedBound);
         }
      }

      restoreHookSession(shooter);
   }

   private static void beginHookSession(LivingEntity entity, boolean saveMainHand, boolean saveOffhand) {
      CompoundTag data = entity.getPersistentData();
      if (!data.m_128471_("AlexJevHookSessionActive")) {
         data.m_128379_("AlexJevHookSessionActive", true);
         data.m_128356_("AlexJevHookSessionStartedAt", entity.m_9236_().m_46467_());
         if (entity instanceof Mob mob) {
            mob.m_21573_().m_26573_();
         }

         if (saveMainHand) {
            data.m_128379_("AlexJevHookSavedMainhand", true);
            saveHand(entity, InteractionHand.MAIN_HAND, "AlexJevHookOriginalMainhand");
         }

         if (saveOffhand) {
            data.m_128379_("AlexJevHookSavedOffhand", true);
            saveHand(entity, InteractionHand.OFF_HAND, "AlexJevHookOriginalOffhand");
         }
      }
   }

   private static void cleanupFinishedSession(LivingEntity entity) {
      long startedAt = entity.getPersistentData().m_128454_("AlexJevHookSessionStartedAt");
      if (isHookSessionActive(entity)) {
         long age = entity.m_9236_().m_46467_() - startedAt;
         boolean hasActiveHook = HookGunItem.hasActiveHook(entity.m_9236_(), entity);
         if (HookGunItem.hasActiveGrappleHook(entity.m_9236_(), entity) && !HookGunItem.hasAttachedGrappleHook(entity.m_9236_(), entity) && age > 60L) {
            HookGunItem.returnActiveHooks(entity.m_9236_(), entity, true);
         } else {
            if (!hasActiveHook && age > 100L && entity.f_19797_ % 20 == 0) {
               restoreHookSession(entity);
            }
         }
      }
   }

   private static boolean isHookSessionActive(LivingEntity entity) {
      return entity.getPersistentData().m_128471_("AlexJevHookSessionActive");
   }

   private static void restoreHookSession(LivingEntity entity) {
      CompoundTag data = entity.getPersistentData();
      if (data.m_128471_("AlexJevHookSessionActive")) {
         if (data.m_128471_("AlexJevHookSavedMainhand")) {
            ItemStack stack = data.m_128425_("AlexJevHookOriginalMainhand", 10)
               ? ItemStack.m_41712_(data.m_128469_("AlexJevHookOriginalMainhand"))
               : ItemStack.f_41583_;
            entity.m_21008_(InteractionHand.MAIN_HAND, stack);
         }

         if (data.m_128471_("AlexJevHookSavedOffhand")) {
            ItemStack stack = data.m_128425_("AlexJevHookOriginalOffhand", 10)
               ? ItemStack.m_41712_(data.m_128469_("AlexJevHookOriginalOffhand"))
               : ItemStack.f_41583_;
            entity.m_21008_(InteractionHand.OFF_HAND, stack);
         }

         data.m_128473_("AlexJevHookSessionActive");
         data.m_128473_("AlexJevHookSessionStartedAt");
         data.m_128473_("AlexJevHookOriginalMainhand");
         data.m_128473_("AlexJevHookOriginalOffhand");
         data.m_128473_("AlexJevHookSavedMainhand");
         data.m_128473_("AlexJevHookSavedOffhand");
      }
   }

   private static InteractionHand getHookSessionHand(LivingEntity entity) {
      return entity.m_21205_().m_41720_() instanceof HookGunItem ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
   }

   private static void saveHand(LivingEntity entity, InteractionHand hand, String key) {
      CompoundTag data = entity.getPersistentData();
      ItemStack stack = entity.m_21120_(hand);
      if (stack.m_41619_()) {
         data.m_128473_(key);
      } else {
         CompoundTag stackTag = new CompoundTag();
         stack.m_41739_(stackTag);
         data.m_128365_(key, stackTag);
      }
   }

   private static boolean shouldPlayHookGunAnimationForHand(LivingEntity entity, InteractionHand hand, ItemStack boundItem) {
      ItemStack previousBoundItem = getLastHookBoundItem(entity, hand);
      boolean shouldPlay = isConsumableHookItem(entity, boundItem) || !isSameHookGunAnimationItem(previousBoundItem, boundItem);
      rememberLastHookBoundItem(entity, hand, boundItem);
      return shouldPlay;
   }

   private static ItemStack getLastHookBoundItem(LivingEntity entity, InteractionHand hand) {
      CompoundTag data = entity.getPersistentData();
      String key = getLastHookBoundItemKey(hand);
      return !data.m_128425_(key, 10) ? ItemStack.f_41583_ : ItemStack.m_41712_(data.m_128469_(key));
   }

   private static void rememberLastHookBoundItem(LivingEntity entity, InteractionHand hand, ItemStack boundItem) {
      CompoundTag data = entity.getPersistentData();
      String key = getLastHookBoundItemKey(hand);
      if (boundItem.m_41619_()) {
         data.m_128473_(key);
      } else {
         ItemStack stored = boundItem.m_41777_();
         stored.m_41764_(1);
         data.m_128365_(key, stored.m_41739_(new CompoundTag()));
      }
   }

   private static String getLastHookBoundItemKey(InteractionHand hand) {
      return hand == InteractionHand.MAIN_HAND ? "AlexJevLastMainhandHookBoundItem" : "AlexJevLastOffhandHookBoundItem";
   }

   private static boolean isSameHookGunAnimationItem(ItemStack previousBoundItem, ItemStack boundItem) {
      if (!previousBoundItem.m_41619_() && !boundItem.m_41619_()) {
         return previousBoundItem.m_41720_() instanceof BucketItem && boundItem.m_41720_() instanceof BucketItem
            ? true
            : ItemStack.m_150942_(previousBoundItem, boundItem);
      } else {
         return previousBoundItem.m_41619_() && boundItem.m_41619_();
      }
   }

   private static boolean isConsumableHookItem(LivingEntity entity, ItemStack boundItem) {
      return !boundItem.m_41619_()
         && (boundItem.getFoodProperties(entity) != null || boundItem.m_41720_() instanceof ThrowablePotionItem || !PotionUtils.m_43547_(boundItem).isEmpty());
   }

   private static void playHookGunAnimation(LivingEntity entity) {
      LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
      if (patch != null && !entity.m_9236_().m_5776_()) {
         patch.playAnimationSynchronized(AnimsPugilistSteve.HOOK_GUN, 0.0F);
      }
   }

   private static void aimAt(LivingEntity entity, Vec3 target) {
      Vec3 delta = target.m_82546_(entity.m_146892_());
      double horizontal = Math.sqrt(delta.f_82479_ * delta.f_82479_ + delta.f_82481_ * delta.f_82481_);
      if (!(horizontal <= 1.0E-7) || !(Math.abs(delta.f_82480_) <= 1.0E-7)) {
         float yaw = (float)(Mth.m_14136_(delta.f_82481_, delta.f_82479_) * 180.0F / (float)Math.PI) - 90.0F;
         float pitch = (float)(-(Mth.m_14136_(delta.f_82480_, horizontal) * 180.0F / (float)Math.PI));
         entity.m_146922_(yaw);
         entity.m_146926_(pitch);
         entity.m_5618_(yaw);
         entity.m_5616_(yaw);
         entity.f_19859_ = yaw;
         entity.f_19860_ = pitch;
         entity.f_20884_ = yaw;
         entity.f_20886_ = yaw;
      }
   }

   private static void setCooldown(LivingEntity entity, String key, int minTicks, int randomTicks) {
      int extra = randomTicks <= 0 ? 0 : entity.m_217043_().m_188503_(randomTicks + 1);
      entity.getPersistentData().m_128356_(key, entity.m_9236_().m_46467_() + (long)minTicks + (long)extra);
   }

   private static void syncAlexAndJevTarget(@Nullable AlexEntity alex, @Nullable JevEntity jev) {
      if (alex != null && jev != null && alex.m_6084_() && jev.m_6084_()) {
         LivingEntity alexTarget = alex.m_5448_();
         LivingEntity jevTarget = jev.m_5448_();
         if (alexTarget != null && alexTarget.m_6084_() && !alex.m_7307_(alexTarget)) {
            if (jevTarget == null || !jevTarget.m_6084_()) {
               jev.m_6710_(alexTarget);
            }
         } else if (jevTarget != null && jevTarget.m_6084_() && !jev.m_7307_(jevTarget)) {
            alex.m_6710_(jevTarget);
         }
      }
   }

   private static void moveJevAroundPartner(JevEntity jev, @Nullable AlexEntity alex) {
      if (alex != null
         && alex.m_6084_()
         && !isHookSessionActive(jev)
         && !HookGunItem.hasActiveHook(jev.m_9236_(), jev)
         && jev.f_19797_ % 45 == 0
         && !jev.m_21573_().m_26572_()) {
         Random random = new Random();
         double angle = random.nextDouble() * Math.PI * 2.0;
         double radius = 4.0 + random.nextDouble() * 5.0;
         jev.m_21573_().m_26519_(alex.m_20185_() + Math.cos(angle) * radius, alex.m_20186_(), alex.m_20189_() + Math.sin(angle) * radius, 1.25);
      }
   }

   @Nullable
   private static Vec3 findHookAnchor(LivingEntity shooter, @Nullable LivingEntity target, boolean escape) {
      Vec3 farAnchor = findHookAnchor(shooter, target, escape, 100);
      return farAnchor != null ? farAnchor : findHookAnchor(shooter, target, escape, 36);
   }

   @Nullable
   private static Vec3 findHookAnchor(LivingEntity shooter, @Nullable LivingEntity target, boolean escape, int minHorizontalDistanceSqr) {
      Level level = shooter.m_9236_();
      BlockPos origin = shooter.m_20183_();
      Vec3 targetDirection = Vec3.f_82478_;
      if (target != null) {
         Vec3 horizontal = target.m_20182_().m_82546_(shooter.m_20182_());
         horizontal = new Vec3(horizontal.f_82479_, 0.0, horizontal.f_82481_);
         if (horizontal.m_82556_() > 1.0E-6) {
            targetDirection = horizontal.m_82541_();
         }
      }

      Vec3 bestAnchor = null;
      double bestScore = -Double.MAX_VALUE;
      int radiusSqr = 900;

      for (int dy = -2; dy <= 12; dy++) {
         for (int dx = -30; dx <= 30; dx++) {
            for (int dz = -30; dz <= 30; dz++) {
               int distSqr = dx * dx + dy * dy + dz * dz;
               int horizontalDistSqr = dx * dx + dz * dz;
               if (distSqr >= 9 && distSqr <= radiusSqr && horizontalDistSqr >= minHorizontalDistanceSqr) {
                  BlockPos pos = origin.m_7918_(dx, dy, dz);
                  BlockState state = level.m_8055_(pos);
                  Vec3 anchor = getHookAnchorAimPosition(level, pos);
                  if (isHookAnchorBlock(level, pos, state) && hasHookLine(level, shooter, pos, anchor)) {
                     double horizontalDistance = Math.sqrt((double)horizontalDistSqr);
                     double score = 350.0 - Math.abs(horizontalDistance - 22.0) * 12.0;
                     score += Math.sqrt((double)distSqr) * 1.5;
                     if (state.m_204336_(BlockTags.f_13035_)) {
                        score += 1000.0;
                     }

                     if (dy > 0) {
                        score += (double)dy * 8.0;
                     } else if (dy < 0) {
                        score += (double)dy * 8.0;
                     }

                     if (targetDirection != Vec3.f_82478_) {
                        Vec3 toAnchor = new Vec3((double)dx, 0.0, (double)dz);
                        if (toAnchor.m_82556_() > 1.0E-6) {
                           double dot = toAnchor.m_82541_().m_82526_(targetDirection);
                           score += escape ? -dot * 100.0 : (0.35 - Math.min(dot, 0.35)) * 35.0;
                        }
                     }

                     if (score > bestScore) {
                        bestScore = score;
                        bestAnchor = anchor;
                     }
                  }
               }
            }
         }
      }

      return bestAnchor;
   }

   private static boolean isHookAnchorBlock(Level level, BlockPos pos, BlockState state) {
      return state.m_204336_(BlockTags.f_13035_) || !state.m_60812_(level, pos).m_83281_();
   }

   @Nullable
   private static Vec3 findNearbyGroundHookAnchor(LivingEntity shooter, @Nullable LivingEntity awayFrom) {
      Level level = shooter.m_9236_();
      BlockPos origin = shooter.m_20183_();
      Vec3 awayDirection = Vec3.f_82478_;
      if (awayFrom != null) {
         Vec3 away = shooter.m_20182_().m_82546_(awayFrom.m_20182_());
         away = new Vec3(away.f_82479_, 0.0, away.f_82481_);
         if (away.m_82556_() > 1.0E-6) {
            awayDirection = away.m_82541_();
         }
      }

      Vec3 bestAnchor = null;
      double bestScore = -Double.MAX_VALUE;

      for (int dx = -28; dx <= 28; dx++) {
         for (int dz = -28; dz <= 28; dz++) {
            int distSqr = dx * dx + dz * dz;
            if (distSqr >= 100 && distSqr <= 784) {
               for (int dy = 1; dy >= -3; dy--) {
                  BlockPos pos = origin.m_7918_(dx, dy, dz);
                  BlockState state = level.m_8055_(pos);
                  Vec3 anchor = getGroundHookAnchorAimPosition(pos);
                  if (!state.m_60812_(level, pos).m_83281_() && level.m_8055_(pos.m_7494_()).m_247087_() && hasHookLine(level, shooter, pos, anchor)) {
                     double horizontalDistance = Math.sqrt((double)distSqr);
                     double score = 320.0 - Math.abs(horizontalDistance - 22.0) * 11.0;
                     if (awayDirection != Vec3.f_82478_) {
                        Vec3 toAnchor = new Vec3((double)dx, 0.0, (double)dz);
                        if (toAnchor.m_82556_() > 1.0E-6) {
                           score += toAnchor.m_82541_().m_82526_(awayDirection) * 120.0;
                        }
                     }

                     if (dy <= 0) {
                        score += 10.0;
                     }

                     if (score > bestScore) {
                        bestScore = score;
                        bestAnchor = anchor;
                     }
                     break;
                  }
               }
            }
         }
      }

      return bestAnchor;
   }

   private static Vec3 getHookAnchorAimPosition(Level level, BlockPos pos) {
      return level.m_8055_(pos.m_7494_()).m_247087_() ? getGroundHookAnchorAimPosition(pos) : Vec3.m_82512_(pos);
   }

   private static Vec3 getGroundHookAnchorAimPosition(BlockPos pos) {
      return new Vec3((double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 0.95, (double)pos.m_123343_() + 0.5);
   }

   private static boolean hasHookLine(Level level, LivingEntity shooter, BlockPos pos, Vec3 anchor) {
      BlockHitResult hit = level.m_45547_(new ClipContext(shooter.m_146892_(), anchor, Block.COLLIDER, Fluid.NONE, shooter));
      return hit.m_6662_() == Type.MISS || hit.m_82425_().equals(pos);
   }

   @Nullable
   private static BlockPos findVisibleSaplingForBoneMeal(JevEntity jev) {
      Level level = jev.m_9236_();
      BlockPos origin = jev.m_20183_();
      BlockPos best = null;
      double bestScore = Double.MAX_VALUE;
      int radius = 18;

      for (int dy = -3; dy <= 5; dy++) {
         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               if (dx * dx + dz * dz <= radius * radius) {
                  BlockPos pos = origin.m_7918_(dx, dy, dz);
                  if (level.m_8055_(pos).m_204336_(BlockTags.f_13104_)) {
                     Vec3 aim = Vec3.m_82512_(pos);
                     if (hasOutlineHookLine(level, jev, pos, aim)) {
                        double score = jev.m_20238_(aim);
                        if (score < bestScore) {
                           bestScore = score;
                           best = pos;
                        }
                     }
                  }
               }
            }
         }
      }

      return best;
   }

   private static boolean hasOutlineHookLine(Level level, LivingEntity shooter, BlockPos pos, Vec3 anchor) {
      BlockHitResult hit = level.m_45547_(new ClipContext(shooter.m_146892_(), anchor, Block.OUTLINE, Fluid.NONE, shooter));
      return hit.m_6662_() == Type.MISS || hit.m_82425_().equals(pos);
   }

   @Nullable
   private static BlockPos findPlacementSupportBlock(Level level, BlockPos center) {
      if (canPlaceAt(level, center) && isSolidSupport(level, center.m_7495_())) {
         return center.m_7495_();
      } else {
         for (Direction direction : Plane.HORIZONTAL) {
            BlockPos side = center.m_121945_(direction);
            if (canPlaceAt(level, side) && isSolidSupport(level, side.m_7495_())) {
               return side.m_7495_();
            }
         }

         return findSupportBlockAround(level, center, 3);
      }
   }

   @Nullable
   private static BlockPos findSupportBlockAround(Level level, BlockPos center, int radius) {
      for (int dy = -1; dy <= 1; dy++) {
         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               BlockPos place = center.m_7918_(dx, dy, dz);
               if (canPlaceAt(level, place) && isSolidSupport(level, place.m_7495_())) {
                  return place.m_7495_();
               }
            }
         }
      }

      return null;
   }

   @Nullable
   private static BlockPos findSupportBlockAround(Level level, BlockPos center, int radius, ItemStack blockStack) {
      for (int dy = -1; dy <= 1; dy++) {
         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               BlockPos place = center.m_7918_(dx, dy, dz);
               BlockPos support = place.m_7495_();
               if (canPlaceBlockOnSupport(level, support, blockStack)) {
                  return support;
               }
            }
         }
      }

      return null;
   }

   private static boolean canPlaceAt(Level level, BlockPos pos) {
      return level.m_8055_(pos).m_247087_() && level.m_6425_(pos).m_76178_();
   }

   private static boolean isSolidSupport(Level level, BlockPos pos) {
      return !level.m_8055_(pos).m_60812_(level, pos).m_83281_();
   }

   private static boolean canPlaceBlockOnSupport(Level level, BlockPos support, ItemStack blockStack) {
      if (blockStack.m_41720_() instanceof BlockItem blockItem && isSolidSupport(level, support)) {
         BlockPos place = support.m_7494_();
         return canPlaceAt(level, place) && blockItem.m_40614_().m_49966_().m_60710_(level, place);
      }

      return false;
   }

   private static BlockPos getAlexCoverBlockCenter(AlexEntity alex, LivingEntity enemy) {
      Vec3 toEnemy = enemy.m_20182_().m_82546_(alex.m_20182_());
      Vec3 horizontal = new Vec3(toEnemy.f_82479_, 0.0, toEnemy.f_82481_);
      if (horizontal.m_82556_() < 1.0E-6) {
         return alex.m_20183_();
      } else {
         Vec3 direction = horizontal.m_82541_();
         return BlockPos.m_274561_(alex.m_20185_() + direction.f_82479_ * 2.0, alex.m_20186_(), alex.m_20189_() + direction.f_82481_ * 2.0);
      }
   }

   private static ItemStack randomCoverBlock(RandomSource random) {
      return switch (random.m_188503_(11)) {
         case 0 -> new ItemStack(Blocks.f_50154_);
         case 1 -> new ItemStack(Blocks.f_50216_);
         case 2 -> new ItemStack(Blocks.f_50132_);
         case 3 -> new ItemStack(Blocks.f_50192_);
         case 4 -> new ItemStack(Blocks.f_50058_);
         case 5 -> new ItemStack(Blocks.f_50185_);
         case 6 -> new ItemStack(Blocks.f_50050_);
         case 7 -> new ItemStack(Blocks.f_50335_);
         case 8 -> new ItemStack(Blocks.f_50741_);
         default -> new ItemStack(Blocks.f_50705_);
      };
   }

   private static ItemStack randomDistractionBlock(RandomSource random) {
      return switch (random.m_188503_(22)) {
         case 0 -> new ItemStack(Blocks.f_50705_);
         case 1 -> new ItemStack(Blocks.f_50741_);
         case 2 -> new ItemStack(Blocks.f_50058_);
         case 3 -> new ItemStack(Blocks.f_50185_);
         case 4 -> new ItemStack(Blocks.f_50132_);
         case 5 -> new ItemStack(Blocks.f_50192_);
         case 6 -> new ItemStack(Blocks.f_50154_);
         case 7 -> new ItemStack(Blocks.f_50216_);
         case 8 -> new ItemStack(Blocks.f_50050_);
         case 9 -> new ItemStack(Blocks.f_50335_);
         case 10 -> new ItemStack(Blocks.f_50618_);
         case 11 -> new ItemStack(Blocks.f_50091_);
         case 12 -> new ItemStack(Blocks.f_50133_);
         case 13 -> new ItemStack(Blocks.f_50144_);
         case 14 -> new ItemStack(Blocks.f_50681_);
         case 15 -> new ItemStack(Blocks.f_50276_);
         case 16 -> new ItemStack(Blocks.f_50112_);
         case 17 -> new ItemStack(Blocks.f_50111_);
         case 18 -> new ItemStack(Blocks.f_50746_);
         case 19 -> new ItemStack(Blocks.f_152541_);
         case 20 -> new ItemStack(Blocks.f_50128_);
         default -> new ItemStack(Blocks.f_50036_);
      };
   }

   private static ItemStack randomFood(Random random) {
      return switch (random.nextInt(5)) {
         case 0 -> new ItemStack(Items.f_42406_);
         case 1 -> new ItemStack(Items.f_42620_);
         case 2 -> new ItemStack(Items.f_42580_);
         case 3 -> new ItemStack(Items.f_42582_);
         default -> new ItemStack(Items.f_42619_);
      };
   }

   private static ItemStack randomPositivePotion(Random random) {
      return switch (random.nextInt(7)) {
         case 0 -> createStrongHealingPotion();
         case 1 -> PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43592_);
         case 2 -> PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43614_);
         case 3 -> PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43609_);
         case 4 -> createHastePotion();
         case 5 -> createGoodBuffPotion();
         default -> PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43589_);
      };
   }

   private static ItemStack randomEnemyHarassItem(Random random) {
      return switch (random.nextInt(11)) {
         case 0 -> createPoisonPotion();
         case 1 -> createWeaknessPotion();
         case 2 -> createSlownessPotion();
         case 3 -> createNauseaPotion();
         case 4 -> createBlindnessPotion();
         case 5 -> createWitherPotion();
         case 6 -> createStrongHarmingPotion();
         case 7 -> new ItemStack(Items.f_42675_);
         case 8 -> new ItemStack(Items.f_42529_);
         case 9 -> new ItemStack(Items.f_42409_);
         default -> new ItemStack(Items.f_42613_);
      };
   }

   private static ItemStack createStrongHealingPotion() {
      return PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43581_);
   }

   private static ItemStack createPoisonPotion() {
      return PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43586_);
   }

   private static ItemStack createGoodBuffPotion() {
      return PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43589_);
   }

   private static ItemStack createWeaknessPotion() {
      return PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43593_);
   }

   private static ItemStack createSlownessPotion() {
      return PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43617_);
   }

   private static ItemStack createStrongHarmingPotion() {
      return PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43583_);
   }

   private static ItemStack createNauseaPotion() {
      return customSplashPotion(new MobEffectInstance(MobEffects.f_19604_, 220, 0));
   }

   private static ItemStack createBlindnessPotion() {
      return customSplashPotion(new MobEffectInstance(MobEffects.f_19610_, 180, 0));
   }

   private static ItemStack createWitherPotion() {
      return customSplashPotion(new MobEffectInstance(MobEffects.f_19615_, 160, 0));
   }

   private static ItemStack createHastePotion() {
      return customSplashPotion(new MobEffectInstance(MobEffects.f_19598_, 360, 1));
   }

   private static ItemStack customSplashPotion(MobEffectInstance effect) {
      ItemStack potion = PotionUtils.m_43549_(new ItemStack(Items.f_42736_), Potions.f_43599_);
      PotionUtils.m_43552_(potion, List.of(effect));
      return potion;
   }
}
