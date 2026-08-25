package com.pla.annoyingvillagers.combatbehaviour;

import com.pla.annoyingvillagers.block.ShadowObsidianBlock;
import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.clazz.HerobrineObsidianBlock;
import com.pla.annoyingvillagers.compat.EfKick;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.entity.AlexEntity;
import com.pla.annoyingvillagers.entity.AngrySteveEntity;
import com.pla.annoyingvillagers.entity.ArmoredHerobrineEntity;
import com.pla.annoyingvillagers.entity.BbqEntity;
import com.pla.annoyingvillagers.entity.BlueDemonEntity;
import com.pla.annoyingvillagers.entity.BlueDemonThrownTridentEntity;
import com.pla.annoyingvillagers.entity.BlueVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.ChrisEntity;
import com.pla.annoyingvillagers.entity.GreenVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.Herobrine7Entity;
import com.pla.annoyingvillagers.entity.HerobrineChrisEntity;
import com.pla.annoyingvillagers.entity.HerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import com.pla.annoyingvillagers.entity.LowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.LowShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.NullSkeletonEntity;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import com.pla.annoyingvillagers.entity.PurpleVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.RedVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.ShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.ShadowHerobrineEntity;
import com.pla.annoyingvillagers.entity.SteveEntity;
import com.pla.annoyingvillagers.entity.SwordsmanHerobrineEntity;
import com.pla.annoyingvillagers.entity.VillagerScoutCaptainEntity;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.item.BlueDemonTridentItem;
import com.pla.annoyingvillagers.item.FishingRodGrappleUtil;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.task.MobExecutionTask;
import com.pla.annoyingvillagers.util.BowFunction;
import com.pla.annoyingvillagers.util.CombatBehaviour;
import com.pla.annoyingvillagers.util.EscapeUtil;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
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
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.ModList;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorType;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder;
import net.shelmarow.combat_evolution.ai.util.BehaviorUtils;
import net.shelmarow.combat_evolution.ai.util.CEPatchUtils;
import net.shelmarow.combat_evolution.execution.ExecutionHandler;
import net.shelmarow.combat_evolution.execution.ExecutionHandler.ExecutionTransform;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionAttackAnimation;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionHitAnimation;
import net.shelmarow.combat_evolution.tickTask.TickTaskManager;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.KnockdownAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.damagesource.StunType;

public class CombatCommon {
   private static final int RANDOM_COMBAT_CHAIN_COUNT = 50;
   private static final int MAX_RANDOM_OPENING_STEPS = 2;
   private static final int RANDOM_FOLLOW_UP_STEPS = 3;
   private static final double MAX_PLACE_BLOCK_GROUND_GAP = 2.0;
   private static final int PLACE_BLOCK_INITIAL_DELAY = 1;
   private static final int PLACE_BLOCK_LAYER_INTERVAL = 3;
   private static final int PLACE_BLOCK_LANE_INTERVAL = 2;
   private static final double NPC_COMBAT_FISHING_ROD_RADIUS = 32.0;
   private static final double NPC_COMBAT_FISHING_ROD_RADIUS_SQR = 1024.0;
   private static final int NPC_COMBAT_FISHING_ROD_MIN_COOLDOWN = 120;
   private static final int NPC_COMBAT_FISHING_ROD_RANDOM_COOLDOWN = 120;
   private static final int NPC_COMBAT_FISHING_ROD_MAX_WAIT_TICKS = 80;
   private static final int NPC_COMBAT_FISHING_ROD_AROUND_SEARCH_RADIUS = 12;
   private static final double NPC_COMBAT_FISHING_ROD_STICK_CHANCE_MIN = 0.3;
   private static final double NPC_COMBAT_FISHING_ROD_STICK_CHANCE_MAX = 0.5;
   private static final double NPC_COMBAT_FISHING_ROD_STICK_LOSE_CHANCE = 0.35;
   private static final int NPC_FISHING_ROD_ACTION_PULL_TARGET = 0;
   private static final int NPC_FISHING_ROD_ACTION_SELF_TO_TARGET = 1;
   private static final int NPC_FISHING_ROD_ACTION_AROUND = 2;
   private static final int NPC_FISHING_ROD_ACTION_JESSICA_PULL_TARGET = 3;
   private static final int NPC_LAVA_BUCKET_MIN_COOLDOWN = 160;
   private static final int NPC_LAVA_BUCKET_RANDOM_COOLDOWN = 140;
   private static final int NPC_LAVA_BUCKET_ACTION_DELAY = 6;
   private static final int NPC_LAVA_BUCKET_PICKUP_DELAY = 40;
   private static final int NPC_LAVA_BUCKET_RESTORE_DELAY = 4;
   private static final int AVNPC_WATER_BUCKET_MIN_COOLDOWN = 220;
   private static final int AVNPC_WATER_BUCKET_RANDOM_COOLDOWN = 180;
   private static final int AVNPC_WATER_BUCKET_ACTION_DELAY = 4;
   private static final int AVNPC_WATER_BUCKET_PICKUP_DELAY = 40;
   private static final int AVNPC_WATER_BUCKET_RESTORE_DELAY = 4;
   private static final String KEY_NPC_COMBAT_FISHING_ROD_ACTIVE = "avNpcCombatFishingRodActive";
   private static final String KEY_NPC_COMBAT_FISHING_ROD_ORIGINAL_OFFHAND = "avNpcCombatFishingRodOriginalOffhand";
   private static final String KEY_NPC_COMBAT_FISHING_ROD_USE_COUNT = "avNpcCombatFishingRodUseCount";
   private static final String KEY_NPC_COMBAT_FISHING_ROD_COOLDOWN_UNTIL = "avNpcCombatFishingRodCooldownUntil";
   private static final String KEY_NPC_COMBAT_FISHING_ROD_STICKY_TARGET_ID = "avNpcCombatFishingRodStickyTargetId";
   private static final String KEY_NPC_LAVA_BUCKET_ORIGINAL_OFFHAND = "avNpcLavaBucketOriginalOffhand";
   private static final String KEY_NPC_LAVA_BUCKET_COOLDOWN_UNTIL = "avNpcLavaBucketCooldownUntil";
   private static final String KEY_AVNPC_WATER_BUCKET_ACTIVE = "avNpcWaterBucketActive";
   private static final String KEY_AVNPC_WATER_BUCKET_ORIGINAL_OFFHAND = "avNpcWaterBucketOriginalOffhand";
   private static final String KEY_AVNPC_WATER_BUCKET_COOLDOWN_UNTIL = "avNpcWaterBucketCooldownUntil";

   public static boolean isHoldingWeapon(LivingEntity entity) {
      CapabilityItem capabilityItem = EpicFightCapabilities.getItemStackCapability(entity.m_21120_(InteractionHand.MAIN_HAND));
      return capabilityItem.getWeaponCategory() != WeaponCategories.NOT_WEAPON && capabilityItem.getWeaponCategory() != WeaponCategories.FIST;
   }

   public static boolean targetIsInRange(LivingEntity attacker, LivingEntity target, double minDist, double maxDist, double maxAngleDegrees) {
      Vec3 targetPos = target.m_20182_();
      Vec3 playerPos = attacker.m_20182_();
      double distance = playerPos.m_82554_(targetPos);
      if (!(distance < minDist) && !(distance > maxDist)) {
         float yaw = target.m_146908_();
         double yawRad = Math.toRadians((double)yaw);
         Vec3 forward = new Vec3(-Math.sin(yawRad), 0.0, Math.cos(yawRad)).m_82541_();
         Vec3 toPlayer = playerPos.m_82546_(targetPos).m_82541_();
         double dot = forward.m_82526_(toPlayer);
         double angle = Math.toDegrees(Math.acos(dot));
         return angle <= maxAngleDegrees;
      } else {
         return false;
      }
   }

   public static boolean canExecute(
      LivingEntity attacker, LivingEntity victim, LivingEntityPatch<?> attackerEntityPatch, LivingEntityPatch<?> victimEntityPatch
   ) {
      float maxDist = 4.0F;
      return attacker.m_6084_()
         && victim.m_6084_()
         && (Boolean)AnnoyingVillagersConfig.AV_MOB_CAN_EXECUTE.get()
         && !ExecutionHandler.isExecutingTarget(attacker, victim)
         && ExecutionHandler.isTargetSupported(attackerEntityPatch, victimEntityPatch)
         && isHoldingWeapon(attacker)
         && targetIsInRange(attacker, victim, 0.0, (double)maxDist, 180.0);
   }

   @Nullable
   private static ExecutionTransform calculateExecutionPosition(Level level, LivingEntity executor, LivingEntity target, Vec3 offset) {
      float yaw = target.m_146908_();
      ExecutionTransform executionTransform = findPosAround(level, executor, target, offset, yaw, 360.0F, 0.5F);
      if (executionTransform == null) {
         Vec3 executorPos = executor.m_20182_();
         Vec3 targetPos = target.m_20182_();
         Vec3 deltaVec = executorPos.m_82546_(targetPos);
         float startAngle = (float)(Math.toDegrees(Mth.m_14136_(deltaVec.f_82481_, deltaVec.f_82479_)) - 90.0);
         float allowedY = 0.5F;
         executionTransform = findPosAround(level, executor, target, offset, startAngle, 12.0F, allowedY);
         if (executionTransform == null) {
            allowedY = 0.95F;
            executionTransform = findPosAround(level, executor, target, offset, startAngle, 12.0F, allowedY);
         }
      }

      return executionTransform;
   }

   @Nullable
   private static ExecutionTransform findPosAround(
      Level level, LivingEntity executor, LivingEntity target, Vec3 offset, float startAngle, float angleStep, float allowedY
   ) {
      float angleOffset = 0.0F;

      while (angleOffset < 360.0F) {
         float yaw = startAngle + angleOffset;
         double rad = Math.toRadians((double)yaw);
         double forwardX = -Math.sin(rad);
         double forwardZ = Math.cos(rad);
         double rightX = Math.cos(rad);
         double rightZ = Math.sin(rad);
         double offsetX = forwardX * offset.f_82479_ + rightX * offset.f_82481_;
         double offsetY = offset.f_82480_;
         double offsetZ = forwardZ * offset.f_82479_ + rightZ * offset.f_82481_;
         Vec3 testPos = target.m_20182_().m_82520_(offsetX, offsetY, offsetZ);
         Vec3 executionPos = canStandHere(level, testPos, executor, target, allowedY);
         if (executionPos != null) {
            return new ExecutionTransform(executionPos, yaw);
         }

         angleOffset += angleStep;
      }

      return null;
   }

   @Nullable
   public static Vec3 canStandHere(Level level, Vec3 pos, LivingEntity executor, LivingEntity target, float allowedY) {
      AABB entityBox = executor.m_20191_();
      double width = entityBox.m_82362_();
      double height = entityBox.m_82376_();

      for (float i = allowedY; i > -allowedY; i -= 0.05F) {
         BlockPos blockPosBelow = BlockPos.m_274561_(pos.f_82479_, pos.f_82480_ + (double)i, pos.f_82481_);
         BlockState stateBelow = level.m_8055_(blockPosBelow);
         VoxelShape shapeBelow = stateBelow.m_60812_(level, blockPosBelow);
         if (!shapeBelow.m_83281_()) {
            double offsetY = shapeBelow.m_83297_(Axis.Y);
            AABB checkBox = new AABB(
               pos.f_82479_ - width / 2.0,
               (double)blockPosBelow.m_123342_() + offsetY,
               pos.f_82481_ - width / 2.0,
               pos.f_82479_ + width / 2.0,
               (double)blockPosBelow.m_123342_() + offsetY + height,
               pos.f_82481_ + width / 2.0
            );
            Vec3 standPos = new Vec3(pos.f_82479_, (double)blockPosBelow.m_123342_() + offsetY, pos.f_82481_);
            if (level.m_45772_(checkBox)
               && getEntityInView(executor, new Vec3(standPos.f_82479_, executor.m_146892_().f_82480_, standPos.f_82481_), target) != null) {
               return standPos;
            }
         }
      }

      return null;
   }

   private static LivingEntity getEntityInView(LivingEntity executor, Vec3 startPos, Entity target) {
      BlockHitResult blockHit = executor.m_9236_().m_45547_(new ClipContext(startPos, target.m_146892_(), Block.COLLIDER, Fluid.NONE, executor));
      double blockDistanceSqr = blockHit.m_6662_() != Type.MISS ? startPos.m_82557_(blockHit.m_82450_()) : Double.MAX_VALUE;
      double entityDistanceSqr = startPos.m_82557_(target.m_146892_());
      return entityDistanceSqr < blockDistanceSqr && blockDistanceSqr - entityDistanceSqr > target.m_20191_().f_82288_ ? (LivingEntity)target : null;
   }

   public static boolean canExecute(MobPatch<?> mobPatch) {
      Mob attacker = (Mob)mobPatch.getOriginal();
      LivingEntity victim = attacker.m_5448_();
      if (victim != null && victim.m_6084_()) {
         LivingEntityPatch<?> victimEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(victim, LivingEntityPatch.class);
         if (victimEntityPatch != null
            && (
               attacker instanceof PlayerNpcEntity
                  || attacker instanceof AVNpc
                  || attacker instanceof HerobrineMob
                  || attacker instanceof NullSkeletonEntity
                  || attacker instanceof BlueDemonEntity
            )) {
            AssetAccessor<? extends StaticAnimation> currentAnimation = Objects.requireNonNull(victimEntityPatch.getAnimator().getPlayerFor(null))
               .getRealAnimation();
            if (ExecutionHandler.isTargetGuardBreak(currentAnimation, victimEntityPatch) && canExecute(attacker, victim, mobPatch, victimEntityPatch)) {
               net.shelmarow.combat_evolution.execution.ExecutionTypeManager.Type executionType = ExecutionHandler.getExecutionType(mobPatch, victimEntityPatch);
               return calculateExecutionPosition(attacker.m_9236_(), attacker, victim, executionType.offset()) != null;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean isTargetKnockedDown(MobPatch<?> mobpatch) {
      LivingEntity victim = ((Mob)mobpatch.getOriginal()).m_5448_();
      if (victim != null) {
         LivingEntityPatch<?> victimPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(victim, LivingEntityPatch.class);
         if (victimPatch != null) {
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(victimPatch.getAnimator().getPlayerFor(null)).getRealAnimation();
            return dynamicAnimation.get() instanceof KnockdownAnimation;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean canPerformNormalAttackLogic(MobPatch<?> mobpatch) {
      LivingEntity attacker = (LivingEntity)mobpatch.getOriginal();
      LivingEntity victim = ((Mob)mobpatch.getOriginal()).m_5448_();
      if (!mobpatch.getEntityState().canBasicAttack()) {
         return false;
      } else {
         if (attacker instanceof PlayerNpcEntity playerNpcEntity && playerNpcEntity.isHealing()) {
            return false;
         }

         if (attacker instanceof AVNpc AVNpc && AVNpc.isHealing()) {
            return false;
         }

         if (attacker instanceof AVNpc AVNpc && AVNpc.hasPlaceBlockParryCooldown()) {
            return false;
         }

         if (attacker instanceof SwordsmanHerobrineEntity swordsmanHerobrineEntity
            && swordsmanHerobrineEntity.m_21205_().m_41783_() != null
            && swordsmanHerobrineEntity.m_21205_().m_41783_().m_128441_("SnakeAnimation")) {
            return false;
         }

         if (victim != null) {
            return !isTargetKnockedDown(mobpatch) && !canExecute(mobpatch) && !canEscape(mobpatch)
               ? !ExecutionHandler.isExecutingTarget(attacker, victim)
               : false;
         } else {
            return false;
         }
      }
   }

   public static boolean canJump(MobPatch<?> mobpatch) {
      return ((Mob)mobpatch.getOriginal()).m_20096_() && !((Mob)mobpatch.getOriginal()).m_20159_();
   }

   public static boolean canPerformTridentAttack(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof BlueDemonEntity blueDemonEntity && blueDemonEntity.m_9236_() instanceof ServerLevel serverLevel) {
         List<BlueDemonThrownTridentEntity> tridents = BlueDemonTridentItem.getGroundedOwnerTridents(serverLevel, blueDemonEntity);
         return !tridents.isEmpty();
      }

      return false;
   }

   public static boolean isNotRiding(MobPatch<?> mobpatch) {
      return !((Mob)mobpatch.getOriginal()).m_20159_();
   }

   public static boolean isRiding(MobPatch<?> mobpatch) {
      return ((Mob)mobpatch.getOriginal()).m_20159_();
   }

   public static boolean hasClearBowShot(MobPatch<?> mobpatch) {
      Mob mob = (Mob)mobpatch.getOriginal();
      LivingEntity target = mob.m_5448_();
      return target != null && target.m_6084_() && BowFunction.hasClearShot(mob, target);
   }

   public static boolean usesStepMoveset(MobPatch<?> mobpatch) {
      return mobpatch.getOriginal() instanceof AVNpc;
   }

   public static boolean usesRollMoveset(MobPatch<?> mobpatch) {
      return mobpatch.getOriginal() instanceof PlayerNpcEntity
         || mobpatch.getOriginal() instanceof LowHerobrineCloneEntity
         || mobpatch.getOriginal() instanceof LowShadowHerobrineCloneEntity;
   }

   public static boolean canAttackWhileNotHealing(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof PlayerNpcEntity playerNpcEntity) {
         return !playerNpcEntity.isHealing();
      } else {
         return mobpatch.getOriginal() instanceof AVNpc AVNpc
            ? !AVNpc.isHealing()
            : mobpatch.getOriginal() instanceof LowShadowHerobrineCloneEntity || mobpatch.getOriginal() instanceof LowHerobrineCloneEntity;
      }
   }

   public static boolean canEscape(MobPatch<?> mobpatch) {
      Mob entity = (Mob)mobpatch.getOriginal();
      AnimationPlayer animationPlayer = mobpatch.getAnimator().getPlayerFor(null);
      if (animationPlayer == null) {
         return false;
      } else {
         AssetAccessor<? extends StaticAnimation> dynamicAnimation = animationPlayer.getRealAnimation();
         StaticAnimation currentAnimation = dynamicAnimation != null ? (StaticAnimation)dynamicAnimation.get() : null;
         if (currentAnimation instanceof ExecutionAttackAnimation || currentAnimation instanceof ExecutionHitAnimation) {
            return false;
         } else if (!EscapeUtil.checkEscape(entity)) {
            return false;
         } else if (!(entity instanceof HerobrineMob) && !(entity instanceof BlueDemonEntity)) {
            if (entity instanceof AVNpc avNpc && avNpc.rollsPlaceBlockToParryChance()) {
               return true;
            }

            if (entity instanceof PlayerNpcEntity playerNpcEntity && new Random().nextDouble() <= playerNpcEntity.getPlaceBlockToParryChance()) {
               return true;
            }

            return false;
         } else {
            return true;
         }
      }
   }

   public static boolean isWrongWeapon(MobPatch<?> mobpatch) {
      if (!canEscape(mobpatch)
         && mobpatch.getOriginal() instanceof LivingEntity livingEntity
         && !(livingEntity.m_21120_(InteractionHand.MAIN_HAND).m_41720_() instanceof SwordItem)
         && !(livingEntity.m_21120_(InteractionHand.MAIN_HAND).m_41720_() instanceof AxeItem)
         && !(livingEntity.m_21120_(InteractionHand.MAIN_HAND).m_41720_() instanceof BowItem)) {
         return true;
      }

      return false;
   }

   public static boolean canBlueDemonPerformHealing(MobPatch<?> mobpatch) {
      if (canExecute(mobpatch)) {
         return false;
      } else if (mobpatch.getOriginal() instanceof BlueDemonEntity blueDemonEntity) {
         return blueDemonEntity.getHealingCooldown() > 0 ? false : blueDemonEntity.getHealingTick() == 0;
      } else {
         return false;
      }
   }

   public static boolean canPerformEating(MobPatch<?> mobpatch) {
      if (canExecute(mobpatch)) {
         return false;
      } else if (!mobpatch.getEntityState().canBasicAttack()) {
         return false;
      } else if (mobpatch.getOriginal() instanceof PlayerNpcEntity playerNpcEntity) {
         return playerNpcEntity.getGapCooldown() > 0 ? false : !playerNpcEntity.isHealing();
      } else if (mobpatch.getOriginal() instanceof AVNpc AVNpc) {
         return AVNpc.getGapCooldown() > 0 ? false : !AVNpc.isHealing();
      } else {
         return false;
      }
   }

   public static boolean canPerformGuarding(MobPatch<?> mobpatch) {
      if (canEscape(mobpatch)) {
         return false;
      } else if (!mobpatch.getEntityState().canBasicAttack()) {
         return false;
      } else if (mobpatch.getOriginal() instanceof PlayerNpcEntity playerNpcEntity) {
         return !playerNpcEntity.isHealing();
      } else if (mobpatch.getOriginal() instanceof AVNpc AVNpc) {
         return !AVNpc.isHealing();
      } else {
         if (mobpatch.getOriginal() instanceof BlueDemonEntity blueDemonEntity
            && blueDemonEntity.getBbqEntity() != null
            && blueDemonEntity.m_5448_() instanceof Mob mob) {
            return !(mob.m_5448_() instanceof BbqEntity);
         }

         return false;
      }
   }

   public static boolean isTargetingHerobrineDragon(MobPatch<?> mobpatch) {
      return ((Mob)mobpatch.getOriginal()).m_5448_() instanceof HerobrineDragonEntity;
   }

   public static boolean canThrowEnderPearl(MobPatch<?> mobpatch) {
      if (((Mob)mobpatch.getOriginal()).m_20159_()) {
         return false;
      } else {
         LivingEntity target = ((Mob)mobpatch.getOriginal()).m_5448_();
         if (target != null && target.m_6084_()) {
            if (mobpatch.getOriginal() instanceof PlayerNpcEntity playerNpcEntity) {
               return playerNpcEntity.isHealing() ? false : playerNpcEntity.getEnderPearlCooldown() == 0;
            } else if (mobpatch.getOriginal() instanceof AVNpc AVNpc) {
               return AVNpc.isHealing() ? false : AVNpc.getEnderPearlCooldown() == 0;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static boolean isGeneral(MobPatch<?> mobpatch) {
      return isGeneralMob((Mob)mobpatch.getOriginal());
   }

   public static boolean canUseNpcCombatFishingRod(MobPatch<?> mobpatch) {
      Mob mob = (Mob)mobpatch.getOriginal();
      LivingEntity target = getNpcCombatFishingRodStickyTarget(mob);
      if (target == null) {
         target = mob.m_5448_();
      }

      if (!isNpcCombatFishingRodUser(mobpatch) || target == null || !target.m_6084_()) {
         return false;
      } else if (!mob.m_9236_().f_46443_ && !mob.m_20159_()) {
         if (mob.m_20280_(target) > 1024.0) {
            return false;
         } else {
            return isStevePhaseOneFishingRodBlocked(mob)
               ? false
               : isNpcCombatFishingRodSessionActive(mob) || mob.m_9236_().m_46467_() >= getPersistentLong(mob, "avNpcCombatFishingRodCooldownUntil");
         }
      } else {
         return false;
      }
   }

   public static boolean canUseNpcCombatFishingRodEscape(MobPatch<?> mobpatch) {
      Mob mob = (Mob)mobpatch.getOriginal();
      if (isNpcCombatFishingRodUser(mobpatch) && !mob.m_9236_().f_46443_ && !mob.m_20159_()) {
         LivingEntity target = mob.m_5448_();
         if (target != null && mob.m_20280_(target) > 1024.0) {
            return false;
         } else {
            return isStevePhaseOneFishingRodBlocked(mob)
               ? false
               : isNpcCombatFishingRodSessionActive(mob) || mob.m_9236_().m_46467_() >= getPersistentLong(mob, "avNpcCombatFishingRodCooldownUntil");
         }
      } else {
         return false;
      }
   }

   public static boolean canUseVillagerKnightLavaBucket(MobPatch<?> mobpatch) {
      Mob mob = (Mob)mobpatch.getOriginal();
      LivingEntity target = mob.m_5448_();
      if (isGeneral(mobpatch) && target != null && target.m_6084_()) {
         if (!(mob.m_9236_() instanceof ServerLevel serverLevel) || !mob.m_20096_() || mob.m_20159_() || isNpcCombatFishingRodSessionActive(mob)) {
            return false;
         }

         if (mob.m_20280_(target) > 144.0) {
            return false;
         } else {
            return mob.m_9236_().m_46467_() < getPersistentLong(mob, "avNpcLavaBucketCooldownUntil") ? false : findLavaPlacement(serverLevel, target) != null;
         }
      } else {
         return false;
      }
   }

   public static boolean tryPerformAvNpcWaterBucketSelfExtinguish(final AVNpc avNpc) {
      if (canUseAvNpcWaterBucketSelfExtinguish(avNpc) && avNpc.m_9236_() instanceof ServerLevel serverLevel) {
         LivingEntityPatch<?> entityPatch = avNpc.getLivingEntityPatch();
         if (entityPatch != null) {
            entityPatch.playAnimationSynchronized(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F);
         }

         avNpc.getPersistentData().m_128379_("avNpcWaterBucketActive", true);
         setPersistentLong(avNpc, "avNpcWaterBucketCooldownUntil", avNpc.m_9236_().m_46467_() + 220L + (long)new Random().nextInt(181));
         equipTemporaryOffhand(avNpc, new ItemStack(Items.f_42447_), "avNpcWaterBucketOriginalOffhand");
         avNpc.m_21573_().m_26573_();
         avNpc.m_21011_(InteractionHand.OFF_HAND, true);
         new DelayedTask(4) {
            @Override
            public void run() {
               if (!avNpc.m_6084_()) {
                  avNpc.getPersistentData().m_128473_("avNpcWaterBucketActive");
               } else if (!avNpc.m_20096_()) {
                  CombatCommon.finishAvNpcWaterBucketSelfExtinguish(avNpc);
               } else {
                  final BlockPos placement = CombatCommon.findSelfWaterPlacement(serverLevel, avNpc);
                  if (placement == null) {
                     CombatCommon.finishAvNpcWaterBucketSelfExtinguish(avNpc);
                  } else {
                     avNpc.m_5496_(SoundEvents.f_11778_, 1.0F, 1.0F);
                     serverLevel.m_46597_(placement, Blocks.f_49990_.m_49966_());
                     avNpc.m_20095_();
                     avNpc.m_21008_(InteractionHand.OFF_HAND, new ItemStack(Items.f_42446_));
                     new DelayedTask(40) {
                        @Override
                        public void run() {
                           if (!avNpc.m_6084_()) {
                              avNpc.getPersistentData().m_128473_("avNpcWaterBucketActive");
                           } else {
                              avNpc.m_21011_(InteractionHand.OFF_HAND, true);
                              BlockState placementState = serverLevel.m_8055_(placement);
                              if (placementState.m_60713_(Blocks.f_49990_)) {
                                 avNpc.m_5496_(SoundEvents.f_11781_, 1.0F, 1.0F);
                                 serverLevel.m_46597_(placement, Blocks.f_50016_.m_49966_());
                              } else if (placementState.m_60734_() instanceof HerobrineObsidianBlock
                                 && placementState.m_61138_(HerobrineObsidianBlock.REPLACE_BY_LIQUID)) {
                                 serverLevel.m_7731_(placement, (BlockState)placementState.m_61124_(HerobrineObsidianBlock.REPLACE_BY_LIQUID, 0), 3);
                              }

                              avNpc.m_21008_(InteractionHand.OFF_HAND, new ItemStack(Items.f_42447_));
                              new DelayedTask(4) {
                                 @Override
                                 public void run() {
                                    if (avNpc.m_6084_()) {
                                       CombatCommon.finishAvNpcWaterBucketSelfExtinguish(avNpc);
                                    } else {
                                       avNpc.getPersistentData().m_128473_("avNpcWaterBucketActive");
                                    }
                                 }
                              };
                           }
                        }
                     };
                  }
               }
            }
         };
         return true;
      } else {
         return false;
      }
   }

   private static boolean canUseAvNpcWaterBucketSelfExtinguish(AVNpc avNpc) {
      if (avNpc.m_6084_()
         && avNpc.m_6060_()
         && !avNpc.m_9236_().f_46443_
         && avNpc.m_20096_()
         && !avNpc.m_20159_()
         && !avNpc.isHealing()
         && !isNpcCombatFishingRodSessionActive(avNpc)
         && !avNpc.getPersistentData().m_128471_("avNpcWaterBucketActive")
         && avNpc.m_9236_().m_46467_() >= getPersistentLong(avNpc, "avNpcWaterBucketCooldownUntil")) {
         if (avNpc.m_9236_() instanceof ServerLevel serverLevel && findSelfWaterPlacement(serverLevel, avNpc) != null) {
            return true;
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean canSwapToBow(MobPatch<?> mobpatch) {
      LivingEntity target = ((Mob)mobpatch.getOriginal()).m_5448_();
      if (target != null && target.m_6084_()) {
         if (mobpatch.getOriginal() instanceof PlayerNpcEntity playerNpcEntity) {
            return playerNpcEntity.isUseBow() && playerNpcEntity.getSwapToBowCooldown() == 0;
         } else if (!(mobpatch.getOriginal() instanceof AVNpc AVNpc)) {
            return false;
         } else {
            if (AVNpc instanceof SteveEntity || AVNpc instanceof AngrySteveEntity || AVNpc instanceof AlexEntity || AVNpc instanceof ChrisEntity) {
               if (target instanceof HerobrineMob) {
                  return false;
               }

               ResourceLocation key = BuiltInRegistries.f_256780_.m_7981_(target.m_6095_());
               if (key.m_135827_().equals("torchesbecomesunlight") && (key.m_135815_().equals("gun_knight_patriot") || key.m_135815_().equals("turret"))) {
                  return false;
               }

               if (key.m_135827_().equals("nightfall_invade") && key.m_135815_().equals("arterius")) {
                  return false;
               }

               if (AVNpc instanceof SteveEntity steveEntity && steveEntity.m_21120_(InteractionHand.OFF_HAND).m_41720_().equals(Items.f_42747_)) {
                  return false;
               }
            }

            return AVNpc.isUseBow() && AVNpc.getSwapToBowCooldown() == 0;
         }
      } else {
         return false;
      }
   }

   public static boolean canSwitchWeapon(MobPatch<?> mobpatch) {
      LivingEntity target = ((Mob)mobpatch.getOriginal()).m_5448_();
      if (target != null && target.m_6084_()) {
         if (mobpatch.getOriginal() instanceof SteveEntity steveEntity) {
            return steveEntity.getBlockDamage() == null && steveEntity.getSwapWeaponCooldown() == 0
               || steveEntity.getState() == 0 && steveEntity.m_21223_() <= 20.0F && !steveEntity.m_21205_().m_41720_().equals(Items.f_42388_);
         } else if (mobpatch.getOriginal() instanceof HerobrineMob herobrineMob) {
            return (herobrineMob instanceof ArmoredHerobrineEntity || herobrineMob instanceof ShadowHerobrineEntity)
               && herobrineMob.getSwapWeaponCooldown() == 0;
         } else {
            return !(mobpatch.getOriginal() instanceof BlueDemonEntity blueDemonEntity)
               ? false
               : blueDemonEntity.getState() == 3 && blueDemonEntity.getSwapWeaponCooldown() == 0;
         }
      } else {
         return false;
      }
   }

   public static void performEnderPearlToTarget(MobPatch<?> mobpatch) {
      LivingEntity target = ((Mob)mobpatch.getOriginal()).m_5448_();
      if (target != null && target.m_6084_()) {
         LivingEntity entity = (LivingEntity)mobpatch.getOriginal();
         double dx = target.m_20185_() - entity.m_20185_();
         double dz = target.m_20189_() - entity.m_20189_();
         double dy = target.m_20188_() - entity.m_20188_();
         double horizontal = Math.sqrt(dx * dx + dz * dz);
         float yaw = (float)(Mth.m_14136_(dz, dx) * 180.0F / (float)Math.PI) - 90.0F;
         float pitch = (float)(-(Mth.m_14136_(dy, horizontal) * 180.0F / (float)Math.PI));
         entity.m_146922_(yaw);
         entity.m_146926_(pitch);
         entity.m_5618_(yaw);
         entity.m_5616_(yaw);
         entity.f_19859_ = yaw;
         entity.f_19860_ = pitch;
         entity.f_20884_ = yaw;
         entity.f_20886_ = yaw;
         if (entity instanceof PlayerNpcEntity playerNpcEntity) {
            playerNpcEntity.setEnderPearlCooldown();
         }

         if (entity instanceof AVNpc AVNpc) {
            AVNpc.setEnderPearlCooldown();
         }

         CombatBehaviour.throwEnderPearl(entity, 0.0F);
      }
   }

   public static void performEnderPearlAway(MobPatch<?> mobpatch) {
      LivingEntity target = ((Mob)mobpatch.getOriginal()).m_5448_();
      if (target != null && target.m_6084_()) {
         LivingEntity entity = (LivingEntity)mobpatch.getOriginal();
         double dx = entity.m_20185_() - target.m_20185_();
         double dz = entity.m_20189_() - target.m_20189_();
         float yaw = (float)(Mth.m_14136_(dz, dx) * 180.0F / (float)Math.PI) - 90.0F;
         float basePitch = -15.0F;
         float randomPitchOffset = (entity.m_217043_().m_188501_() - 0.5F) * 10.0F;
         float randomYawOffset = (entity.m_217043_().m_188501_() - 0.5F) * 30.0F;
         float pitch = basePitch + randomPitchOffset;
         yaw += randomYawOffset;
         entity.m_146922_(yaw);
         entity.m_146926_(pitch);
         entity.m_5618_(yaw);
         entity.m_5616_(yaw);
         entity.f_19859_ = yaw;
         entity.f_19860_ = pitch;
         entity.f_20884_ = yaw;
         entity.f_20886_ = yaw;
         if (entity instanceof PlayerNpcEntity playerNpcEntity) {
            playerNpcEntity.setEnderPearlCooldown();
         }

         if (entity instanceof AVNpc AVNpc) {
            AVNpc.setEnderPearlCooldown();
         }

         CombatBehaviour.throwEnderPearl(entity, 0.0F);
      }
   }

   public static void performNpcCombatFishingRod(MobPatch<?> mobpatch) {
      performNpcCombatFishingRod(mobpatch, false);
   }

   public static void performNpcCombatFishingRodEscape(MobPatch<?> mobpatch) {
      performNpcCombatFishingRod(mobpatch, true);
   }

   private static void performNpcCombatFishingRod(MobPatch<?> mobpatch, boolean escape) {
      if (escape) {
         if (!canUseNpcCombatFishingRodEscape(mobpatch)) {
            return;
         }
      } else if (!canUseNpcCombatFishingRod(mobpatch)) {
         return;
      }

      Mob mob = (Mob)mobpatch.getOriginal();
      LivingEntity stickyTarget = getNpcCombatFishingRodStickyTarget(mob);
      LivingEntity target = stickyTarget != null ? stickyTarget : mob.m_5448_();
      if (!tryRestoreNpcCombatFishingRodBeforeNextHook(mob)) {
         beginNpcCombatFishingRodSession(mob);
         cancelCombatEvolutionGuard(mobpatch);
         mob.m_21573_().m_26573_();
         mob.m_21011_(InteractionHand.OFF_HAND, true);
         mob.m_5496_(SoundEvents.f_11941_, 1.0F, 1.0F);
         if (target != null) {
            mob.m_21563_().m_24960_(target, 30.0F, 30.0F);
         }

         int action = escape ? 2 : chooseNpcCombatFishingRodAction(mob, target);
         Vec3 hookAnchor = resolveNpcCombatFishingRodAnchor(mob, target, action, escape);
         Vec3 visualHookTarget = hookAnchor;
         if (hookAnchor == null && target != null) {
            visualHookTarget = target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.55, 0.0);
         }

         Entity trackedHookTarget = target == null || !isNpcCombatFishingRodTargetPullAction(action) && hookAnchor != null ? null : target;
         FishingHook hook = visualHookTarget != null ? FishingRodGrappleUtil.spawnNpcCombatFishingHook(mob, visualHookTarget, trackedHookTarget) : null;
         ItemStack stuckItem = shouldUseSteveJessicaHook(mob, action)
            ? new ItemStack((ItemLike)AnnoyingVillagersModItems.JESSICA_THE_DARK_SHIELD.get())
            : ItemStack.f_41583_;
         if (!stuckItem.m_41619_()) {
            FishingRodGrappleUtil.attachNpcCombatFishingHookPayload(hook, mob, stuckItem);
         }

         scheduleNpcCombatFishingRodResolution(mob, target, action, hookAnchor, stuckItem, hook, 0);
         incrementNpcCombatFishingRodUseCount(mob);
      }
   }

   private static void scheduleNpcCombatFishingRodResolution(
      final Mob mob,
      @Nullable final LivingEntity target,
      final int action,
      @Nullable final Vec3 hookAnchor,
      final ItemStack stuckItem,
      @Nullable final FishingHook hook,
      final int waitedTicks
   ) {
      new DelayedTask(1) {
         @Override
         public void run() {
            if (!mob.m_6084_()) {
               FishingRodGrappleUtil.forceNpcCombatFishingHookReturn(hook);
            } else {
               boolean maxWaitReached = waitedTicks >= 80;
               if (!FishingRodGrappleUtil.isNpcCombatFishingHookResolved(hook) && !maxWaitReached) {
                  CombatCommon.scheduleNpcCombatFishingRodResolution(mob, target, action, hookAnchor, stuckItem, hook, waitedTicks + 1);
               } else {
                  if (maxWaitReached) {
                     FishingRodGrappleUtil.forceNpcCombatFishingHookReturn(hook);
                  }

                  LivingEntity currentTarget = target != null && target.m_6084_() ? target : mob.m_5448_();
                  if (currentTarget != null) {
                     mob.m_21563_().m_24960_(currentTarget, 30.0F, 30.0F);
                  }

                  if (CombatCommon.isNpcCombatFishingRodTargetPullAction(action) && currentTarget != null && currentTarget.m_6084_()) {
                     CombatCommon.pullTargetToMob(mob, currentTarget);
                     CombatCommon.updateNpcCombatFishingRodStickyTarget(mob, currentTarget, action);
                     if (!stuckItem.m_41619_()) {
                        CombatCommon.damageEnemyHitByNpcHookedFishingRodItem(mob, currentTarget, stuckItem);
                     }
                  } else {
                     Vec3 destination = hookAnchor;
                     if (destination == null && currentTarget != null) {
                        destination = currentTarget.m_20182_().m_82520_(0.0, (double)currentTarget.m_20206_() * 0.45, 0.0);
                     }

                     if (destination != null) {
                        CombatCommon.pullEntityToward(mob, destination, 1.25, 0.25);
                     }
                  }

                  mob.m_5496_(SoundEvents.f_11939_, 1.0F, 1.0F);
               }
            }
         }
      };
   }

   public static boolean damageEnemyHitByNpcHookedFishingRodItem(Mob owner, LivingEntity target, ItemStack stuckItem) {
      if (owner.m_6084_() && target.m_6084_() && !target.m_5833_() && !owner.m_7307_(target)) {
         float damage = calculateNpcHookedFishingRodItemDamage(stuckItem, target);
         if (!target.m_6469_(target.m_9236_().m_269111_().m_269333_(owner), damage)) {
            return false;
         } else {
            if (stuckItem.m_150930_((Item)AnnoyingVillagersModItems.JESSICA_THE_DARK_SHIELD.get())) {
               LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
               if (targetPatch != null) {
                  if (!targetPatch.isStunned()) {
                     targetPatch.applyStun(StunType.LONG, 0.0F);
                  }

                  if (targetPatch.isStunned()) {
                     targetPatch.playAnimationSynchronized(AnimsPugilistSteve.GUARD_BREAK_ATTACK, 0.0F);
                  }
               }

               target.m_5496_(SoundEvents.f_12346_, 1.0F, 0.8F);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static void performVillagerKnightLavaBucket(MobPatch<?> mobpatch) {
      if (canUseVillagerKnightLavaBucket(mobpatch)) {
         final Mob mob = (Mob)mobpatch.getOriginal();
         final LivingEntity target = mob.m_5448_();
         if (mob.m_9236_() instanceof ServerLevel serverLevel && target != null) {
            cancelCombatEvolutionGuard(mobpatch);
            equipTemporaryOffhand(mob, new ItemStack(Items.f_42448_), "avNpcLavaBucketOriginalOffhand");
            setPersistentLong(mob, "avNpcLavaBucketCooldownUntil", mob.m_9236_().m_46467_() + 160L + (long)new Random().nextInt(141));
            mob.m_21011_(InteractionHand.OFF_HAND, true);
            mob.m_21563_().m_24960_(target, 30.0F, 30.0F);
            new DelayedTask(6) {
               @Override
               public void run() {
                  if (mob.m_6084_()) {
                     if (!target.m_6084_()) {
                        CombatCommon.restoreTemporaryOffhand(mob, "avNpcLavaBucketOriginalOffhand");
                     } else if (!mob.m_20096_()) {
                        CombatCommon.restoreTemporaryOffhand(mob, "avNpcLavaBucketOriginalOffhand");
                     } else {
                        final BlockPos placement = CombatCommon.findLavaPlacement(serverLevel, target);
                        if (placement != null) {
                           mob.m_21563_().m_24960_(target, 30.0F, 30.0F);
                           mob.m_5496_(SoundEvents.f_11780_, 1.0F, 1.0F);
                           serverLevel.m_46597_(placement, Blocks.f_49991_.m_49966_());
                           mob.m_21008_(InteractionHand.OFF_HAND, new ItemStack(Items.f_42446_));
                           new DelayedTask(40) {
                              @Override
                              public void run() {
                                 if (mob.m_6084_()) {
                                    mob.m_21011_(InteractionHand.OFF_HAND, true);
                                    if (serverLevel.m_8055_(placement).m_60713_(Blocks.f_49991_)) {
                                       mob.m_5496_(SoundEvents.f_11783_, 1.0F, 1.0F);
                                       serverLevel.m_46597_(placement, Blocks.f_50016_.m_49966_());
                                    }

                                    mob.m_21008_(InteractionHand.OFF_HAND, new ItemStack(Items.f_42448_));
                                    new DelayedTask(4) {
                                       @Override
                                       public void run() {
                                          if (mob.m_6084_()) {
                                             CombatCommon.restoreTemporaryOffhand(mob, "avNpcLavaBucketOriginalOffhand");
                                          }
                                       }
                                    };
                                 }
                              }
                           };
                        } else {
                           CombatCommon.restoreTemporaryOffhand(mob, "avNpcLavaBucketOriginalOffhand");
                        }
                     }
                  }
               }
            };
            return;
         }
      }
   }

   public static void placeRandomFrontWall(MobPatch<?> mobpatch) {
      final Mob mob = (Mob)mobpatch.getOriginal();
      if (mob.m_9236_() instanceof ServerLevel serverLevel) {
         if (isGroundWithin(mob, 2.0)) {
            LivingEntity target = mob.m_5448_();
            Direction dir = target != null ? Direction.m_122366_(target.m_20185_() - mob.m_20185_(), 0.0, target.m_20189_() - mob.m_20189_()) : mob.m_6350_();
            BlockState placeState;
            if (mob instanceof HerobrineChrisEntity || mob instanceof HerobrineCloneEntity) {
               placeState = (BlockState)((net.minecraft.world.level.block.Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
                  .m_49966_()
                  .m_61124_(ShadowObsidianBlock.FROM_PLAYER, false);
            } else if (!(mob instanceof ShadowHerobrineCloneEntity)
               && !(mob instanceof Herobrine7Entity)
               && !(mob instanceof ArmoredHerobrineEntity)
               && !(mob instanceof ShadowHerobrineEntity)) {
               ItemStack handStack = mob.m_21120_(InteractionHand.MAIN_HAND);
               placeState = Blocks.f_50652_.m_49966_();
               if (handStack.m_41720_() instanceof BlockItem blockItem) {
                  placeState = blockItem.m_40614_().m_49966_();
               }
            } else {
               placeState = (BlockState)((net.minecraft.world.level.block.Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                  .m_49966_()
                  .m_61124_(ShadowObsidianBlock.FROM_PLAYER, false);
            }

            Random random = new Random();
            int lanes = 1 + random.nextInt(3);
            float missChancePerLane = 0.25F;

            for (int dist = 1; dist <= lanes; dist++) {
               if (!(random.nextFloat() < 0.25F)) {
                  int pattern = random.nextInt(11);
                  int rot = random.nextInt(4);
                  final BiFunction<Integer, Integer, int[]> toWorld = getIntegerIntegerBiFunction(mob, rot);
                  final BlockState finalPlaceState = placeState;
                  BlockPos baseXZ = mob.m_20183_().m_5484_(dir, dist);
                  int topY = Mth.m_14107_(mob.m_20186_() + (double)mob.m_20206_());
                  int laneStartDelay = 1 + (dist - 1) * 2;
                  int surfaceY = serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, baseXZ).m_123342_();
                  BlockPos projXZ = new BlockPos(baseXZ.m_123341_(), 0, baseXZ.m_123343_());

                  for (int y = surfaceY; y <= topY; y++) {
                     int layer = y - surfaceY;
                     BlockPos center = new BlockPos(projXZ.m_123341_(), y, projXZ.m_123343_());
                     if (!serverLevel.m_8055_(center).m_247087_()) {
                        break;
                     }
                     final int[][] extrasLocal = switch (pattern) {
                        case 0 -> new int[0][];
                        case 1 -> layer == 3 ? new int[][]{{1, 0}} : new int[0][];
                        case 2 -> layer == 0 ? new int[][]{{-1, 0}, {1, 0}, {2, 0}} : (layer == 1 ? new int[][]{{1, 0}} : new int[0][]);
                        case 3 -> layer == 1 ? new int[][]{{-1, 0}, {1, 0}} : new int[0][];
                        case 4 -> layer == 0 ? new int[][]{{-1, 0}, {1, 0}} : new int[0][];
                        case 5 -> new int[][]{{1, 0}};
                        case 6 -> layer <= 1 ? new int[][]{{1, 0}} : new int[0][];
                        case 7 -> layer == 0 ? new int[][]{{1, 0}} : new int[0][];
                        case 8 -> layer == 1 ? new int[][]{{1, 0}} : new int[0][];
                        case 9 -> layer == 0 ? new int[][]{{-1, 0}} : new int[0][];
                        default -> layer == 1 ? new int[][]{{-1, 0}} : new int[0][];
                     };
                     final BlockPos layerCenter = center;
                     int delay = laneStartDelay + layer * 3;
                     new DelayedTask(delay) {
                        @Override
                        public void run() {
                           if (mob.m_6084_() && CombatCommon.isGroundWithin(mob, 2.0)) {
                              if (serverLevel.m_8055_(layerCenter).m_247087_()) {
                                 CombatCommon.placeIfReplaceable(serverLevel, layerCenter, finalPlaceState, mob);

                                 for (int[] ab : extrasLocal) {
                                    int[] dzdx = toWorld.apply(ab[0], ab[1]);
                                    int dx = dzdx[0];
                                    int dz = dzdx[1];
                                    BlockPos p = layerCenter.m_7918_(dx, 0, dz);
                                    CombatCommon.placeIfReplaceable(serverLevel, p, finalPlaceState, mob);
                                 }
                              }
                           }
                        }
                     };
                  }
               }
            }
         }
      }
   }

   static BiFunction<Integer, Integer, int[]> getIntegerIntegerBiFunction(Entity anchor, int rot) {
      Direction facing = anchor.m_6350_();
      int fx = facing.m_122429_();
      int fz = facing.m_122431_();
      int rx = -fz;
      int rz = fx;

      for (int i = 0; i < rot; i++) {
         int nrx = -fz;
         int nrz = fx;
         fx = rx;
         fz = rz;
         rx = nrx;
         rz = nrz;
      }

      int finalRx = rx;
      int finalFx = fx;
      int finalRz = rz;
      int finalFz = fz;
      return (a, b) -> new int[]{a * finalRx + b * finalFx, a * finalRz + b * finalFz};
   }

   private static void placeIfReplaceable(ServerLevel level, BlockPos pos, BlockState state, Mob mob) {
      if (mob instanceof HerobrineMob) {
         mob.m_21011_(InteractionHand.MAIN_HAND, true);
         HerobrineUtil.placeIfReplaceable(level, pos, state, mob);
      } else {
         if (!level.m_8055_(pos).m_247087_()) {
            return;
         }

         mob.m_21011_(InteractionHand.MAIN_HAND, true);
         mob.m_5496_(SoundEvents.f_12447_, 2.0F, 1.0F);
         level.m_46597_(pos, state);
      }
   }

   public static void performEscapeRunAway(final MobPatch<?> mobpatch) {
      final Mob mob = (Mob)mobpatch.getOriginal();
      if (mob.m_9236_() instanceof ServerLevel) {
         LivingEntity target = mob.m_5448_();
         if (target != null) {
            mob.m_21563_().m_24960_(target, 30.0F, 30.0F);
         }

         Vec3 away;
         if (target != null) {
            Vec3 toTarget = new Vec3(target.m_20185_() - mob.m_20185_(), 0.0, target.m_20189_() - mob.m_20189_());
            away = toTarget.m_82556_() > 1.0E-6 ? toTarget.m_82541_().m_82490_(-1.0) : Vec3.f_82478_;
         } else {
            float yawRad = mob.f_20883_ * (float) (Math.PI / 180.0);
            away = new Vec3((double)(-Mth.m_14031_(yawRad)), 0.0, (double)Mth.m_14089_(yawRad)).m_82541_().m_82490_(-1.0);
         }

         if (away != Vec3.f_82478_) {
            Vec3 right = new Vec3(-away.f_82481_, 0.0, away.f_82479_).m_82541_();
            mob.m_21573_().m_26573_();
            Random r = new Random();
            double backMag = 0.55 + r.nextDouble() * 0.35;
            double strafeMag = (double)(r.nextBoolean() ? 1 : -1) * (0.05 + r.nextDouble() * 0.15);
            Vec3 impulse = away.m_82490_(backMag).m_82549_(right.m_82490_(strafeMag));
            mob.m_20256_(mob.m_20184_().m_82520_(impulse.f_82479_, 0.0, impulse.f_82481_));
            mob.f_19812_ = true;
            int pulses = 2 + r.nextInt(2);

            for (int i = 1; i <= pulses; i++) {
               final Vec3 tail = away.m_82490_(0.16 + r.nextDouble() * 0.1).m_82549_(right.m_82490_((r.nextDouble() - 0.5) * 0.1));
               int delay = i * 2;
               new DelayedTask(delay) {
                  @Override
                  public void run() {
                     if (mob.m_6084_()) {
                        mob.m_20256_(mob.m_20184_().m_82520_(tail.f_82479_, 0.0, tail.f_82481_));
                        mob.f_19812_ = true;
                     }
                  }
               };
            }

            int jumpDelay = pulses * 2 + 1;
            new DelayedTask(jumpDelay) {
               @Override
               public void run() {
                  if (mob.m_6084_() && mob.m_20096_()) {
                     if (mob instanceof AVNpc AVNpc) {
                        AVNpc.shortPillarJump();
                     }

                     if (mob instanceof PlayerNpcEntity playerNpcEntity) {
                        playerNpcEntity.shortPillarJump();
                     }

                     mobpatch.playAnimationSynchronized(Animations.BIPED_JUMP, 0.0F);
                  }
               }
            };
            if (mob instanceof SteveEntity
               || mob instanceof AngrySteveEntity
               || mob instanceof HerobrineCloneEntity
               || mob instanceof HerobrineChrisEntity
               || mob instanceof ShadowHerobrineCloneEntity
               || mob instanceof Herobrine7Entity
               || mob instanceof ArmoredHerobrineEntity
               || mob instanceof ShadowHerobrineEntity) {
               new DelayedTask(1) {
                  @Override
                  public void run() {
                     if (CombatCommon.isGroundWithin(mob, 2.0)) {
                        CombatCommon.placeRandomFrontWall(mobpatch);
                     }
                  }
               };
            }
         }
      }
   }

   public static void swapToBlockAndPerformEscapeRunAway(MobPatch<?> mobpatch) {
      swapToBlockToEscape(mobpatch);
      performEscapeRunAway(mobpatch);
   }

   public static boolean isGroundWithin(Entity e, double maxGap) {
      Level level = e.m_9236_();
      AABB bb = e.m_20191_();
      double feetY = bb.f_82289_;
      int x = Mth.m_14107_(e.m_20185_());
      int z = Mth.m_14107_(e.m_20189_());
      int startY = Mth.m_14107_(feetY - 1.0E-4);
      int maxSteps = Mth.m_14165_(maxGap) + 2;
      MutableBlockPos pos = new MutableBlockPos(x, startY, z);

      for (int i = 0; i <= maxSteps; i++) {
         pos.m_142448_(startY - i);
         BlockState state = level.m_8055_(pos);
         if (!state.m_60795_()) {
            VoxelShape shape = state.m_60812_(level, pos);
            if (!shape.m_83281_()) {
               double topY = (double)pos.m_123342_() + shape.m_83297_(Axis.Y);
               double gap = feetY - topY;
               if (gap >= -0.001 && gap <= maxGap + 0.001) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static void performEatingAnimation(MobPatch<?> mobpatch) {
      LivingEntity entity;
      boolean isEnchanted;
      label32: {
         entity = (LivingEntity)mobpatch.getOriginal();
         if (entity instanceof AVNpc AVNpc && new Random().nextDouble() <= AVNpc.getPlaceBlockToParryChance()) {
            entity.m_21008_(InteractionHand.MAIN_HAND, new ItemStack(Items.f_42437_));
            isEnchanted = true;
            break label32;
         }

         entity.m_21008_(InteractionHand.MAIN_HAND, new ItemStack(Items.f_42436_));
         isEnchanted = false;
      }

      if (new Random().nextBoolean()) {
         CombatBehaviour.throwEnderPearl(entity, new Random().nextFloat(0.0F, 180.0F));
         if (entity instanceof PlayerNpcEntity playerNpcEntity) {
            playerNpcEntity.setEnderPearlCooldown();
         }

         if (entity instanceof AVNpc AVNpc) {
            AVNpc.setEnderPearlCooldown();
         }
      } else {
         performEscapeRunAway(mobpatch);
      }

      if (entity instanceof PlayerNpcEntity playerNpcEntity) {
         playerNpcEntity.setGapCooldown();
      }

      if (entity instanceof AVNpc AVNpc) {
         AVNpc.setGapCooldown();
      }

      CombatBehaviour.eatingGoldenApple(entity, entity.m_9236_(), 20.0, isEnchanted);
   }

   public static void performDrinkingAnimation(MobPatch<?> mobpatch) {
      LivingEntity entity = (LivingEntity)mobpatch.getOriginal();
      if (!entity.m_9236_().f_46443_) {
         ItemStack stack = PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43581_);
         entity.m_21008_(InteractionHand.MAIN_HAND, stack);
      }

      if (entity instanceof AVNpc AVNpc) {
         AVNpc.setGapCooldown();
      }

      CombatBehaviour.drinkingHealingPotion(entity, entity.m_9236_(), false, 20.0);
   }

   public static void swapToBow(MobPatch<?> mobpatch) {
      LivingEntity entity = (LivingEntity)mobpatch.getOriginal();
      ItemStack bow = new ItemStack(Items.f_42411_);
      if (entity instanceof AVNpc avNpc) {
         bow = avNpc.getBowItem();
      }

      if (entity instanceof VillagerScoutCaptainEntity) {
         bow.m_41663_(Enchantments.f_44988_, 1);
         bow.m_41663_(Enchantments.f_44989_, 1);
      }

      if (entity instanceof RedVillagerKnightEntity) {
         bow.m_41663_(Enchantments.f_44990_, 2);
      }

      if (entity instanceof BlueVillagerKnightEntity) {
         bow.m_41663_(Enchantments.f_44988_, 2);
      }

      if (entity instanceof GreenVillagerKnightEntity) {
         bow.m_41663_(Enchantments.f_44988_, 1);
         bow.m_41663_(Enchantments.f_44990_, 1);
      }

      if (entity instanceof PurpleVillagerKnightEntity) {
         bow.m_41663_(Enchantments.f_44989_, 2);
      }

      if (entity instanceof SteveEntity steveEntity && steveEntity.getState() == 1 || entity instanceof AngrySteveEntity) {
         bow.m_41663_(Enchantments.f_44988_, 2);
         bow.m_41663_(Enchantments.f_44989_, 2);
         if (entity instanceof AngrySteveEntity) {
            bow.m_41663_(Enchantments.f_44990_, 2);
         }
      }

      if (entity instanceof AlexEntity alexEntity && alexEntity.getState() == 1) {
         bow.m_41663_(Enchantments.f_44989_, 2);
         bow.m_41663_(Enchantments.f_44988_, 2);
         bow.m_41663_(Enchantments.f_44990_, 1);
      }

      if (entity instanceof ChrisEntity chrisEntity && chrisEntity.getState() == 1) {
         bow.m_41663_(Enchantments.f_44988_, 2);
         bow.m_41663_(Enchantments.f_44989_, 2);
      }

      entity.m_21008_(InteractionHand.MAIN_HAND, bow.m_41777_());
      entity.m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
   }

   public static void switchWeapon(MobPatch<?> mobpatch) {
      LivingEntity entity = (LivingEntity)mobpatch.getOriginal();
      if (entity instanceof SteveEntity steveEntity) {
         steveEntity.rollItem();
      }

      if (entity instanceof HerobrineMob herobrineMob) {
         herobrineMob.rollItem();
      }

      if (entity instanceof BlueDemonEntity blueDemonEntity) {
         blueDemonEntity.rollItem();
      }
   }

   public static void swapToMelee(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof PlayerNpcEntity playerNpcEntity) {
         ItemStack mainWeaponItem = playerNpcEntity.getMainWeaponItem();
         ItemStack offWeaponItem = playerNpcEntity.getOffWeaponItem();
         playerNpcEntity.m_21008_(InteractionHand.MAIN_HAND, mainWeaponItem.m_41777_());
         playerNpcEntity.m_21008_(InteractionHand.OFF_HAND, offWeaponItem.m_41777_());
         playerNpcEntity.setSwapToBowCooldown();
      }

      if (mobpatch.getOriginal() instanceof AVNpc AVNpc) {
         ItemStack mainWeaponItem = AVNpc.getMainWeaponItem();
         ItemStack offWeaponItem = AVNpc.getOffWeaponItem();
         if (AVNpc instanceof SteveEntity) {
            if (canSwitchWeapon(mobpatch)) {
               switchWeapon(mobpatch);
            } else {
               AVNpc.m_21008_(InteractionHand.MAIN_HAND, mainWeaponItem.m_41777_());
               AVNpc.m_21008_(InteractionHand.OFF_HAND, offWeaponItem.m_41777_());
            }
         } else {
            AVNpc.m_21008_(InteractionHand.MAIN_HAND, mainWeaponItem.m_41777_());
            AVNpc.m_21008_(InteractionHand.OFF_HAND, offWeaponItem.m_41777_());
         }

         AVNpc.setSwapToBowCooldown();
      }
   }

   private static boolean isNpcCombatFishingRodUser(MobPatch<?> mobpatch) {
      Mob mob = (Mob)mobpatch.getOriginal();
      return isGeneral(mobpatch) || mob instanceof SteveEntity || mob instanceof AngrySteveEntity;
   }

   private static boolean isGeneralMob(Mob mob) {
      return mob instanceof RedVillagerKnightEntity
         || mob instanceof BlueVillagerKnightEntity
         || mob instanceof GreenVillagerKnightEntity
         || mob instanceof PurpleVillagerKnightEntity;
   }

   private static boolean isNpcCombatFishingRodSessionActive(Mob mob) {
      return mob.getPersistentData().m_128471_("avNpcCombatFishingRodActive");
   }

   private static boolean isStevePhaseOneFishingRodBlocked(Mob mob) {
      if (mob instanceof SteveEntity steveEntity && steveEntity.getState() == 0 && !isNpcCombatFishingRodSessionActive(mob)) {
         return true;
      }

      return false;
   }

   private static Item getNpcCombatFishingRodItem(Mob mob) {
      return isGeneralMob(mob) ? (Item)AnnoyingVillagersModItems.ADVANCED_FISHING_ROD.get() : (Item)AnnoyingVillagersModItems.TONY_THE_FISHING_ROD.get();
   }

   private static void beginNpcCombatFishingRodSession(Mob mob) {
      CompoundTag data = mob.getPersistentData();
      if (!data.m_128471_("avNpcCombatFishingRodActive")) {
         saveOffhand(mob, "avNpcCombatFishingRodOriginalOffhand");
         data.m_128379_("avNpcCombatFishingRodActive", true);
         data.m_128405_("avNpcCombatFishingRodUseCount", 0);
      }

      Item rodItem = getNpcCombatFishingRodItem(mob);
      if (!mob.m_21206_().m_150930_(rodItem)) {
         mob.m_21008_(InteractionHand.OFF_HAND, new ItemStack(rodItem));
      }
   }

   private static boolean tryRestoreNpcCombatFishingRodBeforeNextHook(Mob mob) {
      if (!isNpcCombatFishingRodSessionActive(mob)) {
         return false;
      } else if (getNpcCombatFishingRodStickyTarget(mob) != null) {
         return false;
      } else {
         int useCount = Math.max(0, mob.getPersistentData().m_128451_("avNpcCombatFishingRodUseCount"));
         if (useCount == 0) {
            return false;
         } else {
            double restoreChance = Math.min(0.6, (double)useCount * 0.2);
            if (new Random().nextDouble() > restoreChance) {
               return false;
            } else {
               restoreNpcCombatFishingRodSession(mob, true);
               return true;
            }
         }
      }
   }

   private static void incrementNpcCombatFishingRodUseCount(Mob mob) {
      CompoundTag data = mob.getPersistentData();
      data.m_128405_("avNpcCombatFishingRodUseCount", data.m_128451_("avNpcCombatFishingRodUseCount") + 1);
   }

   private static void restoreNpcCombatFishingRodSession(Mob mob, boolean setCooldown) {
      ItemStack originalOffhand = loadOffhand(mob, "avNpcCombatFishingRodOriginalOffhand");
      mob.m_21008_(InteractionHand.OFF_HAND, originalOffhand.m_41777_());
      CompoundTag data = mob.getPersistentData();
      data.m_128473_("avNpcCombatFishingRodActive");
      data.m_128473_("avNpcCombatFishingRodOriginalOffhand");
      data.m_128473_("avNpcCombatFishingRodUseCount");
      data.m_128473_("avNpcCombatFishingRodStickyTargetId");
      if (setCooldown) {
         setPersistentLong(mob, "avNpcCombatFishingRodCooldownUntil", mob.m_9236_().m_46467_() + 120L + (long)new Random().nextInt(121));
      }
   }

   private static int chooseNpcCombatFishingRodAction(Mob mob, @Nullable LivingEntity target) {
      if (target == null) {
         return 2;
      } else {
         double roll = new Random().nextDouble();
         double distance = (double)mob.m_20270_(target);
         if (getNpcCombatFishingRodStickyTarget(mob) != null) {
            return 0;
         } else {
            if (mob instanceof SteveEntity steveEntity && steveEntity.getState() == 1) {
               if (roll < 0.5) {
                  return 3;
               }

               if (roll < 0.7) {
                  return 0;
               }

               if (roll < 0.9) {
                  return 1;
               }

               return 2;
            }

            if (mob instanceof AngrySteveEntity) {
               if (roll < 0.3) {
                  return 0;
               } else {
                  return !(roll < 0.7) && !(distance > 8.0) ? 2 : 1;
               }
            } else if (distance > 12.0) {
               return roll < 0.55 ? 1 : 0;
            } else if (distance < 3.0) {
               return roll < 0.45 ? 0 : 2;
            } else if (roll < 0.45) {
               return 0;
            } else {
               return roll < 0.8 ? 1 : 2;
            }
         }
      }
   }

   private static boolean shouldUseSteveJessicaHook(Mob mob, int action) {
      if (action == 3 && mob instanceof SteveEntity steveEntity && steveEntity.getState() == 1) {
         return true;
      }

      return false;
   }

   private static boolean isNpcCombatFishingRodTargetPullAction(int action) {
      return action == 0 || action == 3;
   }

   @Nullable
   private static LivingEntity getNpcCombatFishingRodStickyTarget(Mob mob) {
      int stickyTargetId = mob.getPersistentData().m_128451_("avNpcCombatFishingRodStickyTargetId");
      if (stickyTargetId <= 0) {
         return null;
      } else {
         if (mob.m_9236_().m_6815_(stickyTargetId) instanceof LivingEntity livingEntity
            && livingEntity.m_6084_()
            && !livingEntity.m_213877_()
            && livingEntity != mob
            && !mob.m_7307_(livingEntity)) {
            return livingEntity;
         }

         mob.getPersistentData().m_128473_("avNpcCombatFishingRodStickyTargetId");
         return null;
      }
   }

   private static void updateNpcCombatFishingRodStickyTarget(Mob mob, LivingEntity target, int action) {
      if (action == 0) {
         CompoundTag data = mob.getPersistentData();
         int stickyTargetId = data.m_128451_("avNpcCombatFishingRodStickyTargetId");
         if (stickyTargetId == target.m_19879_()) {
            if (new Random().nextDouble() < 0.35) {
               data.m_128473_("avNpcCombatFishingRodStickyTargetId");
            }
         } else {
            double stickChance = 0.3 + new Random().nextDouble() * 0.2;
            if (new Random().nextDouble() < stickChance) {
               data.m_128405_("avNpcCombatFishingRodStickyTargetId", target.m_19879_());
            }
         }
      }
   }

   @Nullable
   private static Vec3 resolveNpcCombatFishingRodAnchor(Mob mob, @Nullable LivingEntity target, int action, boolean escape) {
      if (action == 1) {
         Vec3 blockBetween = findHookBlockBetween(mob, target);
         if (blockBetween != null) {
            return blockBetween;
         } else {
            return target == null ? null : target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.45, 0.0);
         }
      } else if (action == 2) {
         Vec3 aroundAnchor = findNpcCombatFishingRodAroundAnchor(mob, target, escape);
         if (aroundAnchor != null) {
            return aroundAnchor;
         } else {
            if (escape && target != null) {
               Vec3 away = mob.m_20182_().m_82546_(target.m_20182_());
               if (away.m_82556_() > 1.0E-6) {
                  Vec3 horizontal = new Vec3(away.f_82479_, 0.0, away.f_82481_).m_82541_();
                  return mob.m_20182_().m_82549_(horizontal.m_82490_(6.0)).m_82520_(0.0, 2.0, 0.0);
               }
            }

            return target == null ? null : target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.45, 0.0);
         }
      } else {
         return null;
      }
   }

   @Nullable
   private static Vec3 findHookBlockBetween(Mob mob, @Nullable LivingEntity target) {
      if (target == null) {
         return null;
      } else {
         Level level = mob.m_9236_();
         Vec3 start = mob.m_146892_();
         Vec3 end = target.m_146892_();
         Vec3 delta = end.m_82546_(start);

         for (int i = 2; i <= 14; i++) {
            double t = (double)i / 16.0;
            BlockPos pos = BlockPos.m_274446_(start.m_82549_(delta.m_82490_(t)));
            BlockState state = level.m_8055_(pos);
            if (isHookAnchorBlock(level, pos, state) && hasHookLine(level, mob, pos)) {
               return Vec3.m_82512_(pos);
            }
         }

         return null;
      }
   }

   @Nullable
   private static Vec3 findNpcCombatFishingRodAroundAnchor(Mob mob, @Nullable LivingEntity target, boolean escape) {
      Level level = mob.m_9236_();
      BlockPos origin = mob.m_20183_();
      Vec3 targetDirection = Vec3.f_82478_;
      if (target != null) {
         Vec3 towardTarget = target.m_20182_().m_82546_(mob.m_20182_());
         Vec3 horizontal = new Vec3(towardTarget.f_82479_, 0.0, towardTarget.f_82481_);
         if (horizontal.m_82556_() > 1.0E-6) {
            targetDirection = horizontal.m_82541_();
         }
      }

      BlockPos bestPos = null;
      double bestScore = -Double.MAX_VALUE;
      int radius = 12;
      int radiusSqr = radius * radius;

      for (int dy = -2; dy <= 12; dy++) {
         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               int distSqr = dx * dx + dy * dy + dz * dz;
               if (distSqr >= 9 && distSqr <= radiusSqr) {
                  BlockPos pos = origin.m_7918_(dx, dy, dz);
                  BlockState state = level.m_8055_(pos);
                  if (isHookAnchorBlock(level, pos, state) && hasHookLine(level, mob, pos)) {
                     double score = Math.sqrt((double)distSqr);
                     if (state.m_204336_(BlockTags.f_13035_)) {
                        score += 1000.0;
                     }

                     if (dy > 0) {
                        score += (double)dy * 4.0;
                     }

                     if (targetDirection != Vec3.f_82478_) {
                        Vec3 toAnchor = new Vec3((double)dx, 0.0, (double)dz);
                        if (toAnchor.m_82556_() > 1.0E-6) {
                           double dot = toAnchor.m_82541_().m_82526_(targetDirection);
                           if (escape) {
                              score -= dot * 90.0;
                           } else {
                              score += dot > 0.75 ? -60.0 : (0.35 - Math.min(dot, 0.35)) * 35.0;
                           }
                        }
                     }

                     if (score > bestScore) {
                        bestScore = score;
                        bestPos = pos;
                     }
                  }
               }
            }
         }
      }

      return bestPos == null ? null : Vec3.m_82512_(bestPos);
   }

   private static boolean isHookAnchorBlock(Level level, BlockPos pos, BlockState state) {
      return state.m_204336_(BlockTags.f_13035_) || !state.m_60812_(level, pos).m_83281_();
   }

   private static boolean hasHookLine(Level level, Mob mob, BlockPos pos) {
      BlockHitResult hit = level.m_45547_(new ClipContext(mob.m_146892_(), Vec3.m_82512_(pos), Block.COLLIDER, Fluid.NONE, mob));
      return hit.m_6662_() == Type.MISS || hit.m_82425_().equals(pos);
   }

   private static void pullTargetToMob(Mob mob, LivingEntity target) {
      Vec3 destination = mob.m_20182_().m_82520_(0.0, (double)mob.m_20206_() * 0.45, 0.0);
      pullEntityToward(target, destination, 1.05, 0.2);
   }

   private static void pullEntityToward(Entity entity, Vec3 destination, double power, double yBoost) {
      Vec3 center = entity.m_20182_().m_82520_(0.0, (double)entity.m_20206_() * 0.5, 0.0);
      Vec3 delta = destination.m_82546_(center);
      if (!(delta.m_82556_() < 1.0E-4)) {
         Vec3 impulse = delta.m_82541_().m_82490_(power);
         impulse = new Vec3(impulse.f_82479_, Math.max(impulse.f_82480_ + yBoost, yBoost), impulse.f_82481_);
         entity.m_20256_(entity.m_20184_().m_82549_(impulse));
         entity.f_19812_ = true;
         entity.f_19864_ = true;
         entity.f_19789_ = 0.0F;
         if (entity instanceof Mob mob) {
            mob.m_21573_().m_26573_();
         }
      }
   }

   private static float calculateNpcHookedFishingRodItemDamage(ItemStack stuckItem, LivingEntity target) {
      if (stuckItem.m_150930_((Item)AnnoyingVillagersModItems.JESSICA_THE_DARK_SHIELD.get())) {
         return 10.0F;
      } else {
         return stuckItem.m_41720_() instanceof ShieldItem ? 8.0F : 4.0F;
      }
   }

   @Nullable
   private static BlockPos findLavaPlacement(ServerLevel level, LivingEntity target) {
      BlockPos foot = target.m_20183_();
      if (canPlaceLavaAt(level, foot)) {
         return foot;
      } else {
         BlockPos above = foot.m_7494_();
         if (canPlaceLavaAt(level, above)) {
            return above;
         } else {
            for (Direction direction : Plane.HORIZONTAL) {
               BlockPos side = foot.m_121945_(direction);
               if (canPlaceLavaAt(level, side)) {
                  return side;
               }
            }

            return null;
         }
      }
   }

   private static boolean canPlaceLavaAt(ServerLevel level, BlockPos pos) {
      return level.m_8055_(pos).m_247087_();
   }

   @Nullable
   private static BlockPos findSelfWaterPlacement(ServerLevel level, AVNpc avNpc) {
      BlockPos feet = avNpc.m_20183_();
      if (canPlaceSelfWaterAt(level, feet)) {
         return feet;
      } else {
         BlockPos above = feet.m_7494_();
         return canPlaceSelfWaterAt(level, above) ? above : null;
      }
   }

   private static boolean canPlaceSelfWaterAt(ServerLevel level, BlockPos pos) {
      return level.m_8055_(pos).m_247087_() && level.m_6425_(pos).m_76178_();
   }

   private static void finishAvNpcWaterBucketSelfExtinguish(AVNpc avNpc) {
      restoreTemporaryOffhand(avNpc, "avNpcWaterBucketOriginalOffhand");
      avNpc.getPersistentData().m_128473_("avNpcWaterBucketActive");
   }

   private static void equipTemporaryOffhand(Mob mob, ItemStack stack, String originalKey) {
      saveOffhand(mob, originalKey);
      mob.m_21008_(InteractionHand.OFF_HAND, stack.m_41777_());
   }

   private static void restoreTemporaryOffhand(Mob mob, String originalKey) {
      ItemStack original = loadOffhand(mob, originalKey);
      mob.m_21008_(InteractionHand.OFF_HAND, original.m_41777_());
      mob.getPersistentData().m_128473_(originalKey);
   }

   private static void saveOffhand(Mob mob, String key) {
      CompoundTag data = mob.getPersistentData();
      ItemStack stack = mob.m_21206_();
      if (stack.m_41619_()) {
         data.m_128473_(key);
      } else {
         CompoundTag stackTag = new CompoundTag();
         stack.m_41739_(stackTag);
         data.m_128365_(key, stackTag);
      }
   }

   private static ItemStack loadOffhand(Mob mob, String key) {
      CompoundTag data = mob.getPersistentData();
      return !data.m_128425_(key, 10) ? ItemStack.f_41583_ : ItemStack.m_41712_(data.m_128469_(key));
   }

   private static long getPersistentLong(Mob mob, String key) {
      return mob.getPersistentData().m_128454_(key);
   }

   private static void setPersistentLong(Mob mob, String key, long value) {
      mob.getPersistentData().m_128356_(key, value);
   }

   public static void jump(MobPatch<?> mobpatch) {
      Entity entity = mobpatch.getOriginal();
      if (entity instanceof PlayerNpcEntity playerNpcEntity) {
         playerNpcEntity.jump();
      }

      if (entity instanceof AVNpc AVNpc) {
         AVNpc.jump();
      }
   }

   public static void shortPillarJump(MobPatch<?> mobpatch) {
      Entity entity = mobpatch.getOriginal();
      if (entity instanceof PlayerNpcEntity playerNpcEntity) {
         playerNpcEntity.shortPillarJump();
      }

      if (entity instanceof AVNpc AVNpc) {
         AVNpc.shortPillarJump();
      }
   }

   public static void swapToBlockToEscape(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof LivingEntity livingEntity) {
         cancelCombatEvolutionGuard(mobpatch);
         double chance = new Random().nextDouble(0.0, 1.0);
         if (chance <= 0.2) {
            livingEntity.m_21008_(InteractionHand.MAIN_HAND, new ItemStack(Items.f_42594_));
         } else if (chance <= 0.4) {
            livingEntity.m_21008_(InteractionHand.MAIN_HAND, new ItemStack(Items.f_41998_));
         } else if (chance <= 0.6) {
            livingEntity.m_21008_(InteractionHand.MAIN_HAND, new ItemStack(Items.f_42329_));
         } else if (chance <= 0.8) {
            livingEntity.m_21008_(InteractionHand.MAIN_HAND, new ItemStack(Items.f_42796_));
         } else {
            livingEntity.m_21008_(InteractionHand.MAIN_HAND, new ItemStack(Items.f_42647_));
         }

         if (livingEntity instanceof AVNpc avNpc) {
            avNpc.setPlaceBlockParryCooldown();
         }
      }
   }

   public static void swapToBlock(MobPatch<?> mobpatch) {
      LivingEntity entity = (LivingEntity)mobpatch.getOriginal();
      if (entity instanceof PlayerNpcEntity || entity instanceof AVNpc) {
         cancelCombatEvolutionGuard(mobpatch);
         double chance = new Random().nextDouble(0.0, 1.0);
         if (chance <= 0.2) {
            entity.m_21008_(InteractionHand.MAIN_HAND, new ItemStack(Items.f_42594_));
         } else if (chance <= 0.4) {
            entity.m_21008_(InteractionHand.MAIN_HAND, new ItemStack(Items.f_41998_));
         } else if (chance <= 0.6) {
            entity.m_21008_(InteractionHand.MAIN_HAND, new ItemStack(Items.f_42329_));
         } else if (chance <= 0.8) {
            entity.m_21008_(InteractionHand.MAIN_HAND, new ItemStack(Items.f_42796_));
         } else {
            entity.m_21008_(InteractionHand.MAIN_HAND, new ItemStack(Items.f_42647_));
         }

         if (entity instanceof AVNpc avNpc) {
            avNpc.setPlaceBlockParryCooldown();
         }
      }
   }

   public static void cancelCombatEvolutionGuard(MobPatch<?> mobpatch) {
      LivingEntity livingEntity = (LivingEntity)mobpatch.getOriginal();
      Behavior<?> currentBehavior = BehaviorUtils.getCurrentBehavior(mobpatch);
      if (isCombatEvolutionGuardBehavior(currentBehavior)) {
         BehaviorUtils.stopCurrentBehavior(livingEntity);
      }

      CEPatchUtils.setGuard(mobpatch, false);
      CEPatchUtils.setWander(mobpatch, false);
      CEPatchUtils.setInCounter(mobpatch, false);
      livingEntity.m_5810_();
      stopCombatEvolutionBlockAnimation(mobpatch);
   }

   private static boolean isCombatEvolutionGuardBehavior(Behavior<?> behavior) {
      if (behavior == null) {
         return false;
      } else {
         BehaviorType type = behavior.getType();
         return type == BehaviorType.GUARD || type == BehaviorType.GUARD_WANDER;
      }
   }

   private static void stopCombatEvolutionBlockAnimation(MobPatch<?> mobpatch) {
      AssetAccessor<? extends StaticAnimation> blockAnimation = mobpatch.getAnimator().getLivingAnimation(LivingMotions.BLOCK, Animations.SWORD_GUARD);
      if (mobpatch.isLogicalClient()) {
         mobpatch.getAnimator().stopPlaying(blockAnimation);
      } else {
         mobpatch.stopPlaying(blockAnimation);
      }
   }

   public static void performExecute(MobPatch<?> mobPatch) {
      Mob attacker = (Mob)mobPatch.getOriginal();
      LivingEntity victim = attacker.m_5448_();
      if (victim != null) {
         if (attacker.m_20159_()) {
            attacker.m_8127_();
         }

         LivingEntityPatch<?> victimPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(victim, LivingEntityPatch.class);
         if (victimPatch != null) {
            net.shelmarow.combat_evolution.execution.ExecutionTypeManager.Type execType = ExecutionHandler.getExecutionType(mobPatch, victimPatch);
            faceTargetHard(attacker, victim);
            ExecutionTransform transform = calculateExecutionPosition(attacker.m_9236_(), attacker, victim, execType.offset());
            if (transform != null) {
               Vec3 executionPos = transform.position();
               attacker.m_6021_(executionPos.f_82479_, executionPos.f_82480_, executionPos.f_82481_);
               faceTargetHard(attacker, victim);
               TickTaskManager.addTask(victim.m_20148_(), new MobExecutionTask(attacker, victim, execType, execType.totalTick()));
            }
         }
      }
   }

   private static void faceTargetHard(Mob self, LivingEntity target) {
      Vec3 from = self.m_20299_(1.0F);
      Vec3 to = target.m_20299_(1.0F);
      double dx = to.f_82479_ - from.f_82479_;
      double dy = to.f_82480_ - from.f_82480_;
      double dz = to.f_82481_ - from.f_82481_;
      double horiz = Math.sqrt(dx * dx + dz * dz);
      if (horiz < 1.0E-6) {
         horiz = 1.0E-6;
      }

      float yaw = (float)(Mth.m_14136_(dz, dx) * (180.0 / Math.PI)) - 90.0F;
      float pitch = (float)(-(Mth.m_14136_(dy, horiz) * (180.0 / Math.PI)));
      self.m_21573_().m_26573_();
      self.m_146922_(yaw);
      self.m_146926_(pitch);
      self.m_5618_(yaw);
      self.m_5616_(yaw);
      self.f_19859_ = yaw;
      self.f_19860_ = pitch;
      self.f_20884_ = yaw;
      self.f_20886_ = yaw;
      self.m_21563_().m_24960_(target, 90.0F, 90.0F);
   }

   public static void performBlueDemonHealing(MobPatch<?> mobpatch) {
      LivingEntity entity = (LivingEntity)mobpatch.getOriginal();
      if (entity instanceof BlueDemonEntity blueDemonEntity && blueDemonEntity.m_9236_() instanceof ServerLevel) {
         blueDemonEntity.setHealingCooldown();
         blueDemonEntity.setHealingTick(600);
      }
   }

   public static Builder<MobPatch<?>> addRandomCombatChains(
      Builder<MobPatch<?>> root,
      AnimationAccessor<? extends StaticAnimation>[] group1,
      AnimationAccessor<? extends StaticAnimation>[] group2,
      AnimationAccessor<? extends StaticAnimation>[] group3,
      AnimationAccessor<? extends StaticAnimation>[] kicks,
      AnimationAccessor<? extends StaticAnimation>[] rolls
   ) {
      return addRandomCombatChainsFromGroups(root, conditions(), group1, group2, group3, kicks, rolls);
   }

   @SafeVarargs
   public static Builder<MobPatch<?>> addRandomCombatChains(
      Builder<MobPatch<?>> root,
      CombatCommon.MobPatchCondition[] customConditions,
      AnimationAccessor<? extends StaticAnimation>[] group1,
      AnimationAccessor<? extends StaticAnimation>[]... groups
   ) {
      return addRandomCombatChainsFromGroups(root, customConditions, group1, groups);
   }

   @SafeVarargs
   public static Builder<MobPatch<?>> addRandomCombatChains(
      Builder<MobPatch<?>> root, AnimationAccessor<? extends StaticAnimation>[] group1, AnimationAccessor<? extends StaticAnimation>[]... groups
   ) {
      return addRandomCombatChainsFromGroups(root, conditions(), group1, groups);
   }

   @SafeVarargs
   public static Builder<MobPatch<?>> addRandomCombatChains(
      Builder<MobPatch<?>> root, CombatCommon.CombatChainStep[] group1, CombatCommon.CombatChainStep[]... groups
   ) {
      return addRandomCombatChainsFromStepGroups(root, conditions(), group1, groups);
   }

   @SafeVarargs
   public static Builder<MobPatch<?>> addRandomCombatChains(
      Builder<MobPatch<?>> root,
      CombatCommon.MobPatchCondition[] customConditions,
      CombatCommon.CombatChainStep[] group1,
      CombatCommon.CombatChainStep[]... groups
   ) {
      return addRandomCombatChainsFromStepGroups(root, customConditions, group1, groups);
   }

   public static Builder<MobPatch<?>> addAnimationBehaviors(
      Builder<MobPatch<?>> root, double minDistance, double maxDistance, AnimationAccessor<? extends StaticAnimation>[] animations
   ) {
      return addAnimationBehaviors(root, minDistance, maxDistance, conditions(), animations);
   }

   public static Builder<MobPatch<?>> addAnimationBehaviors(
      Builder<MobPatch<?>> root,
      double minDistance,
      double maxDistance,
      CombatCommon.MobPatchCondition[] customConditions,
      AnimationAccessor<? extends StaticAnimation>[] animations
   ) {
      for (AnimationAccessor<? extends StaticAnimation> animation : animations) {
         root = root.addFirstBehavior(animationStep(animation, minDistance, maxDistance, customConditions));
      }

      return root;
   }

   @SafeVarargs
   public static AnimationAccessor<? extends StaticAnimation>[] animations(AnimationAccessor<? extends StaticAnimation>... animations) {
      return animations;
   }

   @SafeVarargs
   public static CombatCommon.MobPatchCondition[] conditions(CombatCommon.MobPatchCondition... conditions) {
      return conditions;
   }

   @SafeVarargs
   public static CombatCommon.CombatChainStep[] steps(CombatCommon.CombatChainStep... steps) {
      return steps;
   }

   @SafeVarargs
   public static CombatCommon.CombatChainStep[] steps(AnimationAccessor<? extends StaticAnimation>... animations) {
      CombatCommon.CombatChainStep[] steps = new CombatCommon.CombatChainStep[animations.length];

      for (int i = 0; i < animations.length; i++) {
         steps[i] = animation(animations[i]);
      }

      return steps;
   }

   @SafeVarargs
   public static CombatCommon.CombatChainStep animation(
      AnimationAccessor<? extends StaticAnimation> animation, CombatCommon.MobPatchCondition... customConditions
   ) {
      return new CombatCommon.CombatChainStep(animation, -1, null, customConditions);
   }

   @SafeVarargs
   public static CombatCommon.CombatChainStep animation(
      AnimationAccessor<? extends StaticAnimation> animation, double maxDistance, CombatCommon.MobPatchCondition... customConditions
   ) {
      return new CombatCommon.CombatChainStep(animation, -1, maxDistance, customConditions);
   }

   @SafeVarargs
   public static CombatCommon.CombatChainStep guard(int ticks, CombatCommon.MobPatchCondition... customConditions) {
      return new CombatCommon.CombatChainStep(null, ticks, null, customConditions);
   }

   @SafeVarargs
   public static CombatCommon.CombatChainStep guard(int ticks, double maxDistance, CombatCommon.MobPatchCondition... customConditions) {
      return new CombatCommon.CombatChainStep(null, ticks, maxDistance, customConditions);
   }

   public static AnimationAccessor<? extends StaticAnimation>[] kickAnimations() {
      return ModList.get().isLoaded("efkick") ? EfKick.kickAnimations() : animations();
   }

   public static AnimationAccessor<? extends StaticAnimation>[] fistKickAnimations() {
      return ModList.get().isLoaded("efkick") ? EfKick.fistKickAnimations() : animations();
   }

   public static AnimationAccessor<? extends StaticAnimation>[] basicKickAnimations() {
      return ModList.get().isLoaded("efkick") ? EfKick.basicKickAnimations() : animations();
   }

   public static AnimationAccessor<? extends StaticAnimation>[] rollAnimations() {
      return animations(Animations.BIPED_ROLL_BACKWARD, Animations.BIPED_ROLL_FORWARD);
   }

   public static AnimationAccessor<? extends StaticAnimation>[] stepAnimations() {
      return animations(Animations.BIPED_STEP_BACKWARD, Animations.BIPED_STEP_FORWARD, Animations.BIPED_STEP_LEFT, Animations.BIPED_STEP_RIGHT);
   }

   public static AnimationAccessor<? extends StaticAnimation>[] rollStepAnimations() {
      return animations(
         Animations.BIPED_ROLL_BACKWARD,
         Animations.BIPED_ROLL_FORWARD,
         Animations.BIPED_STEP_BACKWARD,
         Animations.BIPED_STEP_FORWARD,
         Animations.BIPED_STEP_LEFT,
         Animations.BIPED_STEP_RIGHT
      );
   }

   public static AnimationAccessor<? extends StaticAnimation>[] enderStepAnimations() {
      return animations(WOMAnimations.ENDERSTEP_FORWARD, WOMAnimations.ENDERSTEP_BACKWARD, WOMAnimations.ENDERSTEP_LEFT, WOMAnimations.ENDERSTEP_RIGHT);
   }

   public static AnimationAccessor<? extends StaticAnimation>[] shadowStepAnimations() {
      return animations(WOMAnimations.SHADOWSTEP_FORWARD, WOMAnimations.SHADOWSTEP_BACKWARD, WOMAnimations.SHADOWSTEP_RIGHT, WOMAnimations.SHADOWSTEP_LEFT);
   }

   public static AnimationAccessor<? extends StaticAnimation>[] enderStepRollAnimations() {
      return animations(
         WOMAnimations.ENDERSTEP_FORWARD,
         WOMAnimations.ENDERSTEP_BACKWARD,
         WOMAnimations.ENDERSTEP_LEFT,
         WOMAnimations.ENDERSTEP_RIGHT,
         Animations.BIPED_STEP_BACKWARD,
         Animations.BIPED_STEP_FORWARD,
         Animations.BIPED_STEP_LEFT,
         Animations.BIPED_STEP_RIGHT,
         Animations.BIPED_ROLL_BACKWARD,
         Animations.BIPED_ROLL_FORWARD
      );
   }

   private static Builder<MobPatch<?>> addRandomCombatChainsFromGroups(
      Builder<MobPatch<?>> root,
      CombatCommon.MobPatchCondition[] customConditions,
      AnimationAccessor<? extends StaticAnimation>[] group1,
      AnimationAccessor<? extends StaticAnimation>[]... groups
   ) {
      CombatCommon.CombatChainStep[][] stepGroups = new CombatCommon.CombatChainStep[groups.length][];

      for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
         stepGroups[groupIndex] = toSteps(groups[groupIndex]);
      }

      return addRandomCombatChainsFromStepGroups(root, customConditions, toSteps(group1), stepGroups);
   }

   private static Builder<MobPatch<?>> addRandomCombatChainsFromStepGroups(
      Builder<MobPatch<?>> root,
      CombatCommon.MobPatchCondition[] customConditions,
      CombatCommon.CombatChainStep[] group1,
      CombatCommon.CombatChainStep[]... groups
   ) {
      List<CombatCommon.CombatChainStep[]> openingGroups = new ArrayList<>(2);
      int openingStepCount = 0;
      if (group1.length > 0) {
         openingGroups.add(group1);
         openingStepCount += group1.length;
      }

      if (groups.length > 0 && groups[0].length > 0) {
         openingGroups.add(groups[0]);
         openingStepCount += groups[0].length;
      }

      if (openingGroups.isEmpty()) {
         return root;
      } else {
         List<CombatCommon.CombatChainStep[]> followUpGroups = new ArrayList<>(Math.max(0, groups.length - 1));

         for (int groupIndex = 1; groupIndex < groups.length; groupIndex++) {
            if (groups[groupIndex].length > 0) {
               followUpGroups.add(groups[groupIndex]);
            }
         }

         int maxOpeningSteps = Math.min(2, openingStepCount);
         Random random = new Random(687115L);

         for (int combo = 0; combo < 50; combo++) {
            int openingSteps = maxOpeningSteps == 1 ? 1 : 1 + random.nextInt(maxOpeningSteps);
            int followUpSteps = followUpGroups.isEmpty() ? 0 : 3;
            CombatCommon.CombatChainStep[] chain = new CombatCommon.CombatChainStep[openingSteps + followUpSteps];
            int index = 0;

            for (int openingIndex = 0; openingIndex < openingSteps; openingIndex++) {
               CombatCommon.CombatChainStep[] group = openingGroups.get(random.nextInt(openingGroups.size()));
               chain[index++] = group[random.nextInt(group.length)];
            }

            for (int followUpIndex = 0; followUpIndex < followUpSteps; followUpIndex++) {
               CombatCommon.CombatChainStep[] group = followUpGroups.get(random.nextInt(followUpGroups.size()));
               chain[index++] = group[random.nextInt(group.length)];
            }

            root = root.addFirstBehavior(combatChain(customConditions, chain));
         }

         return root;
      }
   }

   @SafeVarargs
   private static net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior.Builder<MobPatch<?>> combatChain(
      CombatCommon.MobPatchCondition[] customConditions, CombatCommon.CombatChainStep... steps
   ) {
      net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior.Builder<MobPatch<?>> chain = combatStep(
         steps[steps.length - 1], steps.length - 1, customConditions
      );

      for (int i = steps.length - 2; i >= 0; i--) {
         chain = combatStep(steps[i], i, customConditions).addNextBehavior(chain);
      }

      return chain;
   }

   private static net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior.Builder<MobPatch<?>> combatStep(
      CombatCommon.CombatChainStep step, int index, CombatCommon.MobPatchCondition[] customConditions
   ) {
      double maxDistance = step.maxDistance != null ? step.maxDistance : (index < 2 ? 3.0 : (index < 4 ? 4.0 : 5.0));
      net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior.Builder<MobPatch<?>> builder = applyCombatConditions(
            Behavior.builder(), customConditions, step.customConditions
         )
         .withinDistance(0.0, maxDistance);
      return step.guardTicks >= 0 ? builder.guard(step.guardTicks) : builder.animationBehavior(step.animation, 0.0F);
   }

   private static net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior.Builder<MobPatch<?>> animationStep(
      AnimationAccessor<? extends StaticAnimation> animation, double minDistance, double maxDistance, CombatCommon.MobPatchCondition[] customConditions
   ) {
      return applyCombatConditions(Behavior.builder(), customConditions).withinDistance(minDistance, maxDistance).animationBehavior(animation, 0.0F);
   }

   private static net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior.Builder<MobPatch<?>> applyCombatConditions(
      net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior.Builder<MobPatch<?>> builder, CombatCommon.MobPatchCondition[]... conditionGroups
   ) {
      builder = builder.custom(CombatCommon::canPerformNormalAttackLogic);

      for (CombatCommon.MobPatchCondition[] conditionGroup : conditionGroups) {
         for (CombatCommon.MobPatchCondition condition : conditionGroup) {
            builder = builder.custom(condition);
         }
      }

      return builder;
   }

   private static CombatCommon.CombatChainStep[] toSteps(AnimationAccessor<? extends StaticAnimation>[] animations) {
      CombatCommon.CombatChainStep[] steps = new CombatCommon.CombatChainStep[animations.length];

      for (int i = 0; i < animations.length; i++) {
         steps[i] = animation(animations[i]);
      }

      return steps;
   }

   public static final class CombatChainStep {
      private final AnimationAccessor<? extends StaticAnimation> animation;
      private final int guardTicks;
      private final Double maxDistance;
      private final CombatCommon.MobPatchCondition[] customConditions;

      private CombatChainStep(
         AnimationAccessor<? extends StaticAnimation> animation, int guardTicks, Double maxDistance, CombatCommon.MobPatchCondition[] customConditions
      ) {
         this.animation = animation;
         this.guardTicks = guardTicks;
         this.maxDistance = maxDistance;
         this.customConditions = customConditions;
      }
   }

   @FunctionalInterface
   public interface MobPatchCondition extends Function<MobPatch<?>, Boolean> {
   }
}
