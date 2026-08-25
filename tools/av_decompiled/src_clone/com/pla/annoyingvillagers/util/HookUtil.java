package com.pla.annoyingvillagers.util;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.pla.annoyingvillagers.clazz.NullWeapon;
import com.pla.annoyingvillagers.entity.ArmoredHerobrineEntity;
import com.pla.annoyingvillagers.entity.BlueDemonEntity;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolActions;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.damagesource.StunType;

public final class HookUtil {
   private HookUtil() {
   }

   public static boolean isPickaxe(ItemStack stack) {
      return !stack.m_41619_()
         && (
            stack.m_41720_() instanceof PickaxeItem
               || stack.canPerformAction(ToolActions.PICKAXE_DIG)
               || hasEpicFightWeaponCategory(stack, WeaponCategories.PICKAXE)
         );
   }

   public static boolean shouldUseShieldFacing(ItemStack stack) {
      return !stack.m_41619_() && stack.m_41720_() instanceof ShieldItem;
   }

   public static boolean shouldAlignSharpEdge(ItemStack stack) {
      return stack.m_41619_()
         ? false
         : stack.m_41720_() instanceof SwordItem
            || stack.m_41720_() instanceof AxeItem
            || stack.m_41720_() instanceof HoeItem
            || stack.m_41720_() instanceof ShovelItem
            || stack.m_41720_() instanceof PickaxeItem
            || stack.canPerformAction(ToolActions.SWORD_DIG)
            || stack.canPerformAction(ToolActions.AXE_DIG)
            || stack.canPerformAction(ToolActions.HOE_DIG)
            || stack.canPerformAction(ToolActions.SHOVEL_DIG)
            || stack.canPerformAction(ToolActions.PICKAXE_DIG)
            || isEpicFightMeleeWeapon(stack);
   }

   public static boolean shouldRenderWithoutProjectileSpin(ItemStack stack) {
      return !stack.m_41619_() && stack.m_41720_() instanceof BlockItem;
   }

   public static HookUtil.HitResult handleEntityHit(Level level, ItemStack boundStack, Entity projectile, @Nullable LivingEntity owner, LivingEntity target) {
      return handleEntityHitWithResult(level, boundStack, projectile, owner, target).hitResult();
   }

   public static HookUtil.ItemInteractionResult handleEntityHitWithResult(
      Level level, ItemStack boundStack, Entity projectile, @Nullable LivingEntity owner, LivingEntity target
   ) {
      if (boundStack.m_41619_() || !target.m_6084_() || target.m_5833_()) {
         return pass(boundStack);
      } else if (owner != null && target == owner) {
         return handled(boundStack);
      } else if (owner != null && target.m_7307_(owner) && !canUseBoundItemOnAlly(boundStack, target)) {
         return handled(boundStack);
      } else if (boundStack.m_41720_() instanceof SpawnEggItem) {
         return result(spawnFromSpawnEgg(level, boundStack, owner, target.m_20183_(), false, false), boundStack);
      } else if (boundStack.m_41720_() instanceof EggItem) {
         return result(hatchChickenEgg(level, boundStack, hitPosition(target)), boundStack);
      } else {
         if (isShears(boundStack)) {
            HookUtil.HitResult shearResult = shearEntity(level, boundStack, owner, target);
            if (shearResult == HookUtil.HitResult.HANDLED) {
               return handled(boundStack);
            }
         }

         if (boundStack.m_150930_(Items.f_42446_)) {
            HookUtil.ItemInteractionResult bucketResult = fillBucketFromEntity(level, boundStack, target);
            if (bucketResult.handled()) {
               return bucketResult;
            }
         }

         if (boundStack.m_150930_(Items.f_42447_) && target.m_6060_()) {
            target.m_20095_();
            level.m_6263_(null, target.m_20185_(), target.m_20186_(), target.m_20189_(), SoundEvents.f_11778_, SoundSource.PLAYERS, 0.8F, 1.0F);
            return handled(new ItemStack(Items.f_42446_));
         } else if (boundStack.m_150930_(Items.f_42452_)) {
            return result(hitWithSnowball(level, boundStack, target), boundStack);
         } else if (boundStack.m_41720_() instanceof ShieldItem) {
            return result(hitWithShield(level, boundStack, projectile, owner, target), boundStack);
         } else if (isWeaponLike(boundStack)) {
            return result(hitWithWeapon(level, boundStack, projectile, owner, target), boundStack);
         } else if (boundStack.m_41720_() instanceof ArmorItem armorItem) {
            return result(equipArmor(boundStack, target, armorItem), boundStack);
         } else if (isPotion(boundStack)) {
            return result(applyPotion(level, boundStack, projectile, owner, target), boundStack);
         } else {
            FoodProperties food = boundStack.getFoodProperties(target);
            if (food != null) {
               return result(feedTarget(level, boundStack, target, food), boundStack);
            } else if (boundStack.m_41720_() instanceof FireChargeItem) {
               target.m_20254_(8);
               boundStack.m_41774_(1);
               level.m_6263_(null, target.m_20185_(), target.m_20186_(), target.m_20189_(), SoundEvents.f_11874_, SoundSource.PLAYERS, 1.0F, 1.0F);
               return handled(boundStack);
            } else if (boundStack.m_41720_() instanceof FlintAndSteelItem) {
               target.m_20254_(8);
               damageTool(boundStack, owner);
               level.m_6263_(null, target.m_20185_(), target.m_20186_(), target.m_20189_(), SoundEvents.f_11942_, SoundSource.PLAYERS, 1.0F, 1.0F);
               return handled(boundStack);
            } else {
               return pass(boundStack);
            }
         }
      }
   }

   public static HookUtil.HitResult handleBlockHit(Level level, ItemStack boundStack, Entity projectile, @Nullable LivingEntity owner, BlockHitResult hitResult) {
      return handleBlockHitWithResult(level, boundStack, projectile, owner, hitResult).hitResult();
   }

   public static HookUtil.ItemInteractionResult handleBlockHitWithResult(
      Level level, ItemStack boundStack, Entity projectile, @Nullable LivingEntity owner, BlockHitResult hitResult
   ) {
      if (boundStack.m_41619_()) {
         return pass(boundStack);
      } else if (!(boundStack.m_41720_() instanceof SpawnEggItem)) {
         if (boundStack.m_41720_() instanceof EggItem) {
            return result(hatchChickenEgg(level, boundStack, hitResult.m_82450_()), boundStack);
         } else {
            if (isShears(boundStack)) {
               HookUtil.HitResult shearResult = shearBlock(level, boundStack, owner, hitResult);
               if (shearResult == HookUtil.HitResult.HANDLED) {
                  return handled(boundStack);
               }
            }

            if (boundStack.m_41720_() instanceof FireChargeItem) {
               HookUtil.ItemInteractionResult fireChargeResult = useFireCharge(level, boundStack, owner, hitResult);
               if (fireChargeResult.handled()) {
                  return fireChargeResult;
               }
            }

            if (boundStack.m_41720_() instanceof BucketItem bucketItem) {
               HookUtil.ItemInteractionResult bucketResult = useBucket(level, boundStack, owner, bucketItem, hitResult);
               if (bucketResult.handled()) {
                  return bucketResult;
               }
            }

            if (boundStack.m_41720_() instanceof FlintAndSteelItem) {
               if (!igniteTntBlock(level, hitResult.m_82425_(), owner) && !placeFire(level, hitResult)) {
                  return pass(boundStack);
               } else {
                  damageTool(boundStack, owner);
                  level.m_6263_(
                     null,
                     hitResult.m_82450_().f_82479_,
                     hitResult.m_82450_().f_82480_,
                     hitResult.m_82450_().f_82481_,
                     SoundEvents.f_11942_,
                     SoundSource.PLAYERS,
                     1.0F,
                     1.0F
                  );
                  return handled(boundStack);
               }
            } else if (boundStack.m_41720_() instanceof BoneMealItem) {
               return result(applyBoneMeal(level, boundStack, hitResult), boundStack);
            } else {
               return boundStack.m_41720_() instanceof BlockItem blockItem
                  ? result(placeBoundBlock(level, boundStack, owner, blockItem, hitResult), boundStack)
                  : pass(boundStack);
            }
         }
      } else {
         BlockPos blockPos = getSpawnEggBlockPos(level, hitResult);
         boolean offsetForFace = hitResult.m_82434_() == Direction.UP && !blockPos.equals(hitResult.m_82425_());
         return result(spawnFromSpawnEgg(level, boundStack, owner, blockPos, true, offsetForFace), boundStack);
      }
   }

   private static HookUtil.ItemInteractionResult handled(ItemStack itemStack) {
      return new HookUtil.ItemInteractionResult(HookUtil.HitResult.HANDLED, itemStack);
   }

   private static HookUtil.ItemInteractionResult pass(ItemStack itemStack) {
      return new HookUtil.ItemInteractionResult(HookUtil.HitResult.PASS, itemStack);
   }

   private static HookUtil.ItemInteractionResult result(HookUtil.HitResult hitResult, ItemStack itemStack) {
      return new HookUtil.ItemInteractionResult(hitResult, itemStack);
   }

   private static boolean isShears(ItemStack stack) {
      return !stack.m_41619_()
         && (stack.m_41720_() instanceof ShearsItem || stack.canPerformAction(ToolActions.SHEARS_DIG) || stack.canPerformAction(ToolActions.SHEARS_HARVEST));
   }

   private static boolean canUseBoundItemOnAlly(ItemStack stack, LivingEntity target) {
      return stack.m_41720_() instanceof ArmorItem
         || stack.m_150930_(Items.f_42447_)
         || stack.m_150930_(Items.f_42452_)
         || isPotion(stack)
         || stack.getFoodProperties(target) != null;
   }

   private static HookUtil.HitResult hitWithSnowball(Level level, ItemStack boundStack, LivingEntity target) {
      target.m_20095_();
      target.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 100, 1));
      level.m_6263_(null, target.m_20185_(), target.m_20186_(), target.m_20189_(), SoundEvents.f_12473_, SoundSource.PLAYERS, 0.8F, 0.75F);
      boundStack.m_41774_(1);
      return HookUtil.HitResult.HANDLED;
   }

   private static HookUtil.HitResult shearEntity(Level level, ItemStack boundStack, @Nullable LivingEntity owner, LivingEntity target) {
      if (target instanceof IForgeShearable shearable) {
         BlockPos pos = target.m_20183_();
         if (!shearable.isShearable(boundStack, level, pos)) {
            return HookUtil.HitResult.PASS;
         } else {
            Player player = owner instanceof Player ownerPlayer ? ownerPlayer : null;
            int fortune = EnchantmentHelper.m_44843_(Enchantments.f_44987_, boundStack);
            List<ItemStack> drops = shearable.onSheared(player, boundStack, level, pos, fortune);
            RandomSource random = target.m_217043_();

            for (ItemStack drop : drops) {
               ItemEntity itemEntity = target.m_5552_(drop, 1.0F);
               if (itemEntity != null) {
                  itemEntity.m_20256_(
                     itemEntity.m_20184_()
                        .m_82520_(
                           (double)((random.m_188501_() - random.m_188501_()) * 0.1F),
                           (double)(random.m_188501_() * 0.05F),
                           (double)((random.m_188501_() - random.m_188501_()) * 0.1F)
                        )
                  );
               }
            }

            damageTool(boundStack, owner);
            return HookUtil.HitResult.HANDLED;
         }
      } else {
         return HookUtil.HitResult.PASS;
      }
   }

   private static HookUtil.HitResult shearBlock(Level level, ItemStack boundStack, @Nullable LivingEntity owner, BlockHitResult hitResult) {
      BlockPos pos = hitResult.m_82425_();
      BlockState state = level.m_8055_(pos);
      if (!state.m_204336_(BlockTags.f_13035_)) {
         return HookUtil.HitResult.PASS;
      } else {
         Block.m_49881_(state, level, pos, level.m_7702_(pos), owner, boundStack);
         level.m_46796_(2001, pos, Block.m_49956_(state));
         level.m_7471_(pos, false);
         level.m_142346_(owner, GameEvent.f_157794_, pos);
         damageTool(boundStack, owner);
         return HookUtil.HitResult.HANDLED;
      }
   }

   private static HookUtil.ItemInteractionResult useFireCharge(Level level, ItemStack boundStack, @Nullable LivingEntity owner, BlockHitResult hitResult) {
      if (!igniteTntBlock(level, hitResult.m_82425_(), owner) && !placeFireChargeFire(level, owner, hitResult)) {
         return pass(boundStack);
      } else {
         boundStack.m_41774_(1);
         RandomSource random = level.m_213780_();
         level.m_5594_(null, hitResult.m_82425_(), SoundEvents.f_11874_, SoundSource.BLOCKS, 1.0F, (random.m_188501_() - random.m_188501_()) * 0.2F + 1.0F);
         return handled(boundStack);
      }
   }

   private static boolean placeFireChargeFire(Level level, @Nullable LivingEntity owner, BlockHitResult hitResult) {
      BlockPos firePos = hitResult.m_82425_().m_121945_(hitResult.m_82434_());
      Direction direction = owner != null ? owner.m_6350_() : Direction.NORTH;
      if (!BaseFireBlock.m_49255_(level, firePos, direction)) {
         return false;
      } else {
         level.m_46597_(firePos, BaseFireBlock.m_49245_(level, firePos));
         level.m_142346_(owner, GameEvent.f_157797_, firePos);
         return true;
      }
   }

   private static HookUtil.ItemInteractionResult useBucket(
      Level level, ItemStack boundStack, @Nullable LivingEntity owner, BucketItem bucketItem, BlockHitResult hitResult
   ) {
      if (boundStack.m_150930_(Items.f_42446_)) {
         return fillBucketFromBlock(level, boundStack, owner, hitResult);
      } else {
         return bucketItem.getFluid() == Fluids.f_76191_ ? pass(boundStack) : emptyBucket(level, boundStack, owner, bucketItem, hitResult);
      }
   }

   private static HookUtil.ItemInteractionResult emptyBucket(
      Level level, ItemStack boundStack, @Nullable LivingEntity owner, BucketItem bucketItem, BlockHitResult hitResult
   ) {
      BlockPos hitPos = hitResult.m_82425_();
      BlockState hitState = level.m_8055_(hitPos);
      Fluid fluid = bucketItem.getFluid();
      BlockPos placePos = canPlaceBucketFluidInBlock(level, hitPos, hitState, fluid) ? hitPos : hitPos.m_121945_(hitResult.m_82434_());
      Player player = owner instanceof Player ownerPlayer ? ownerPlayer : null;
      if (!bucketItem.emptyContents(player, level, placePos, hitResult, boundStack)) {
         return pass(boundStack);
      } else {
         bucketItem.m_142131_(player, level, boundStack, placePos);
         return handled(new ItemStack(Items.f_42446_));
      }
   }

   private static boolean canPlaceBucketFluidInBlock(Level level, BlockPos pos, BlockState state, Fluid fluid) {
      if (state.m_60734_() instanceof LiquidBlockContainer liquidBlockContainer && liquidBlockContainer.m_6044_(level, pos, state, fluid)) {
         return true;
      }

      return false;
   }

   private static HookUtil.ItemInteractionResult fillBucketFromBlock(Level level, ItemStack boundStack, @Nullable LivingEntity owner, BlockHitResult hitResult) {
      BlockPos pos = hitResult.m_82425_();
      BlockState state = level.m_8055_(pos);
      boolean pickedUpWater = state.m_60819_().m_205070_(FluidTags.f_13131_);
      if (state.m_60734_() instanceof BucketPickup bucketPickup) {
         ItemStack filledBucket = bucketPickup.m_142598_(level, pos, state);
         if (filledBucket.m_41619_()) {
            return pass(boundStack);
         } else {
            bucketPickup.getPickupSound(state).ifPresent(soundEvent -> level.m_5594_(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F));
            level.m_142346_(owner, GameEvent.f_157816_, pos);
            if (pickedUpWater) {
               HookUtil.ItemInteractionResult bucketableResult = fillBucketFromNearbyBucketable(level, boundStack, pos);
               if (bucketableResult.handled()) {
                  return bucketableResult;
               }
            }

            return handled(filledBucket);
         }
      } else {
         return pass(boundStack);
      }
   }

   private static HookUtil.ItemInteractionResult fillBucketFromNearbyBucketable(Level level, ItemStack boundStack, BlockPos pos) {
      List<LivingEntity> bucketableTargets = level.m_6443_(
         LivingEntity.class, new AABB(pos).m_82400_(0.75), entity -> entity.m_6084_() && entity instanceof Bucketable
      );
      return bucketableTargets.isEmpty() ? pass(boundStack) : bucketEntity(level, bucketableTargets.get(0));
   }

   private static HookUtil.ItemInteractionResult fillBucketFromEntity(Level level, ItemStack boundStack, LivingEntity target) {
      if (target instanceof Bucketable bucketable) {
         return !pickupNearbyWaterSource(level, target) ? pass(boundStack) : bucketEntity(level, target);
      } else {
         return pass(boundStack);
      }
   }

   private static boolean pickupNearbyWaterSource(Level level, LivingEntity target) {
      BlockPos center = target.m_20183_();

      for (BlockPos pos : BlockPos.m_121940_(center.m_7918_(-1, -1, -1), center.m_7918_(1, 1, 1))) {
         if (level.m_6425_(pos).m_205070_(FluidTags.f_13131_) && level.m_6425_(pos).m_76170_() && pickupFluidBlock(level, target, pos.m_7949_())) {
            return true;
         }
      }

      return false;
   }

   private static boolean pickupFluidBlock(Level level, @Nullable LivingEntity owner, BlockPos pos) {
      BlockState state = level.m_8055_(pos);
      if (state.m_60734_() instanceof BucketPickup bucketPickup) {
         ItemStack pickedBucket = bucketPickup.m_142598_(level, pos, state);
         if (pickedBucket.m_41619_()) {
            return false;
         } else {
            bucketPickup.getPickupSound(state).ifPresent(soundEvent -> level.m_5594_(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F));
            level.m_142346_(owner, GameEvent.f_157816_, pos);
            return true;
         }
      } else {
         return false;
      }
   }

   private static HookUtil.ItemInteractionResult bucketEntity(Level level, LivingEntity target) {
      if (target instanceof Bucketable bucketable) {
         ItemStack filledBucket = bucketable.m_28282_();
         bucketable.m_6872_(filledBucket);
         target.m_5496_(bucketable.m_142623_(), 1.0F, 1.0F);
         if (!level.f_46443_) {
            target.m_146870_();
         }

         return handled(filledBucket);
      } else {
         return pass(ItemStack.f_41583_);
      }
   }

   public static float calculateWeaponDamage(ItemStack stack, LivingEntity target) {
      double damage = 1.0;
      Multimap<Attribute, AttributeModifier> modifiers = stack.m_41638_(EquipmentSlot.MAINHAND);

      for (AttributeModifier modifier : modifiers.get(Attributes.f_22281_)) {
         if (modifier.m_22217_() == Operation.ADDITION) {
            damage += modifier.m_22218_();
         } else if (modifier.m_22217_() == Operation.MULTIPLY_BASE) {
            damage += damage * modifier.m_22218_();
         } else if (modifier.m_22217_() == Operation.MULTIPLY_TOTAL) {
            damage *= 1.0 + modifier.m_22218_();
         }
      }

      damage += (double)EnchantmentHelper.m_44833_(stack, target.m_6336_());
      return (float)Math.max(1.0, damage);
   }

   private static boolean isWeaponLike(ItemStack stack) {
      return stack.m_41720_() instanceof SwordItem
         || stack.m_41720_() instanceof AxeItem
         || stack.m_41720_() instanceof HoeItem
         || stack.m_41720_() instanceof ShovelItem
         || stack.m_41720_() instanceof PickaxeItem
         || shouldAlignSharpEdge(stack);
   }

   private static boolean isEpicFightMeleeWeapon(ItemStack stack) {
      return hasEpicFightWeaponCategory(
         stack,
         WeaponCategories.AXE,
         WeaponCategories.GREATSWORD,
         WeaponCategories.HOE,
         WeaponCategories.PICKAXE,
         WeaponCategories.SHOVEL,
         WeaponCategories.SWORD,
         WeaponCategories.UCHIGATANA,
         WeaponCategories.SPEAR,
         WeaponCategories.TACHI,
         WeaponCategories.TRIDENT,
         WeaponCategories.LONGSWORD,
         WeaponCategories.DAGGER
      );
   }

   private static boolean hasEpicFightWeaponCategory(ItemStack stack, WeaponCategories... categories) {
      if (stack.m_41619_()) {
         return false;
      } else {
         CapabilityItem capability = EpicFightCapabilities.getItemStackCapability(stack);
         if (capability != null && !capability.isEmpty()) {
            WeaponCategory weaponCategory = capability.getWeaponCategory();

            for (WeaponCategories category : categories) {
               if (weaponCategory == category) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }

   private static boolean isPotion(ItemStack stack) {
      return !PotionUtils.m_43547_(stack).isEmpty() || stack.m_41720_() instanceof ThrowablePotionItem;
   }

   private static BlockPos getSpawnEggBlockPos(Level level, BlockHitResult hitResult) {
      BlockPos hitPos = hitResult.m_82425_();
      BlockState hitState = level.m_8055_(hitPos);
      return hitState.m_60812_(level, hitPos).m_83281_() ? hitPos : hitPos.m_121945_(hitResult.m_82434_());
   }

   private static HookUtil.HitResult spawnFromSpawnEgg(
      Level level, ItemStack boundStack, @Nullable LivingEntity owner, BlockPos spawnPos, boolean shouldOffsetY, boolean shouldOffsetYMore
   ) {
      if (level instanceof ServerLevel serverLevel && boundStack.m_41720_() instanceof SpawnEggItem spawnEggItem) {
         EntityType var11 = spawnEggItem.m_43228_(boundStack.m_41783_());
         Player player = owner instanceof Player ownerPlayer ? ownerPlayer : null;
         Entity spawned = var11.m_20592_(serverLevel, boundStack, player, spawnPos, MobSpawnType.SPAWN_EGG, shouldOffsetY, shouldOffsetYMore);
         if (spawned != null) {
            boundStack.m_41774_(1);
         }

         return HookUtil.HitResult.HANDLED;
      }

      return HookUtil.HitResult.HANDLED;
   }

   private static HookUtil.HitResult hatchChickenEgg(Level level, ItemStack boundStack, Vec3 hitPos) {
      if (!(level instanceof ServerLevel serverLevel)) {
         return HookUtil.HitResult.HANDLED;
      } else {
         serverLevel.m_6263_(
            null,
            hitPos.f_82479_,
            hitPos.f_82480_,
            hitPos.f_82481_,
            SoundEvents.f_11877_,
            SoundSource.PLAYERS,
            0.5F,
            0.4F / (serverLevel.f_46441_.m_188501_() * 0.4F + 0.8F)
         );
         if (serverLevel.f_46441_.m_188503_(8) == 0) {
            int count = serverLevel.f_46441_.m_188503_(32) == 0 ? 4 : 1;

            for (int i = 0; i < count; i++) {
               Chicken chicken = (Chicken)EntityType.f_20555_.m_20615_(serverLevel);
               if (chicken != null) {
                  chicken.m_146762_(-24000);
                  chicken.m_7678_(hitPos.f_82479_, hitPos.f_82480_, hitPos.f_82481_, 0.0F, 0.0F);
                  serverLevel.m_7967_(chicken);
               }
            }
         }

         boundStack.m_41774_(1);
         return HookUtil.HitResult.HANDLED;
      }
   }

   private static Vec3 hitPosition(LivingEntity target) {
      return new Vec3(target.m_20185_(), target.m_20186_() + (double)target.m_20206_() * 0.5, target.m_20189_());
   }

   private static HookUtil.HitResult hitWithShield(Level level, ItemStack boundStack, Entity projectile, @Nullable LivingEntity owner, LivingEntity target) {
      DamageSource source = level.m_269111_().m_269390_(projectile, owner);
      if (!target.m_6469_(source, 15.0F)) {
         return HookUtil.HitResult.PASS;
      } else {
         LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
         if (targetPatch != null && !targetPatch.isStunned()) {
            targetPatch.applyStun(StunType.LONG, 0.0F);
         }

         damageTool(boundStack, owner);
         level.m_6263_(
            null, target.m_20185_(), target.m_20186_(), target.m_20189_(), (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get(), SoundSource.PLAYERS, 0.8F, 1.0F
         );
         return HookUtil.HitResult.HANDLED;
      }
   }

   private static HookUtil.HitResult hitWithWeapon(Level level, ItemStack boundStack, Entity projectile, @Nullable LivingEntity owner, LivingEntity target) {
      DamageSource source = level.m_269111_().m_269390_(projectile, owner);
      if (!target.m_6469_(source, calculateWeaponDamage(boundStack, target))) {
         return HookUtil.HitResult.PASS;
      } else {
         if (owner != null) {
            applyWeaponEnchantEffects(boundStack, owner, target);
         }

         damageTool(boundStack, owner);
         level.m_6263_(
            null, target.m_20185_(), target.m_20186_(), target.m_20189_(), (SoundEvent)EpicFightSounds.BLADE_HIT.get(), SoundSource.PLAYERS, 0.8F, 1.0F
         );
         return HookUtil.HitResult.HANDLED;
      }
   }

   private static void applyWeaponEnchantEffects(ItemStack stack, LivingEntity owner, LivingEntity target) {
      int fireAspect = EnchantmentHelper.m_44843_(Enchantments.f_44981_, stack);
      if (fireAspect > 0) {
         target.m_20254_(fireAspect * 4);
      }

      EnchantmentHelper.m_44823_(target, owner);
      EnchantmentHelper.m_44896_(owner, target);
   }

   private static HookUtil.HitResult equipArmor(ItemStack boundStack, LivingEntity target, ArmorItem armorItem) {
      if (isArmorTargetBlacklisted(target)) {
         return HookUtil.HitResult.HANDLED;
      } else {
         EquipmentSlot slot = armorItem.m_40402_();
         if (!target.m_6844_(slot).m_41619_()) {
            return HookUtil.HitResult.HANDLED;
         } else {
            ItemStack equipped = boundStack.m_41777_();
            equipped.m_41764_(1);
            target.m_8061_(slot, equipped);
            if (target instanceof Mob mob) {
               mob.m_21409_(slot, 1.0F);
            }

            boundStack.m_41774_(1);
            return HookUtil.HitResult.HANDLED;
         }
      }
   }

   private static boolean isArmorTargetBlacklisted(LivingEntity target) {
      return target instanceof NullWeapon || target instanceof BlueDemonEntity || target instanceof ArmoredHerobrineEntity;
   }

   private static HookUtil.HitResult applyPotion(Level level, ItemStack boundStack, Entity projectile, @Nullable LivingEntity owner, LivingEntity target) {
      for (MobEffectInstance effect : PotionUtils.m_43547_(boundStack)) {
         if (effect.m_19544_().m_8093_()) {
            effect.m_19544_().m_19461_(projectile, owner, target, effect.m_19564_(), 1.0);
         } else {
            target.m_7292_(new MobEffectInstance(effect));
         }
      }

      if (isFlashPotion(boundStack) && level instanceof ServerLevel serverLevel) {
         serverLevel.m_8767_(ParticleTypes.f_123747_, target.m_20185_(), target.m_20188_(), target.m_20189_(), 1, 0.0, 0.0, 0.0, 0.0);
      }

      level.m_6263_(null, target.m_20185_(), target.m_20186_(), target.m_20189_(), SoundEvents.f_12436_, SoundSource.PLAYERS, 0.8F, 1.0F);
      boundStack.m_41774_(1);
      return HookUtil.HitResult.HANDLED;
   }

   private static boolean isFlashPotion(ItemStack stack) {
      String descriptionId = stack.m_41778_().toLowerCase();
      return descriptionId.contains("flash");
   }

   private static HookUtil.HitResult feedTarget(Level level, ItemStack boundStack, LivingEntity target, FoodProperties food) {
      if (target.m_21222_()) {
         float damage = Math.max(1.0F, (float)food.m_38744_());
         target.m_6469_(level.m_269111_().m_269425_(), damage);
      } else {
         target.m_5634_(Math.max(1.0F, (float)food.m_38744_()));

         for (Pair<MobEffectInstance, Float> effectPair : food.m_38749_()) {
            if (target.m_217043_().m_188501_() < (Float)effectPair.getSecond()) {
               target.m_7292_(new MobEffectInstance((MobEffectInstance)effectPair.getFirst()));
            }
         }
      }

      level.m_6263_(null, target.m_20185_(), target.m_20186_(), target.m_20189_(), SoundEvents.f_11912_, SoundSource.PLAYERS, 0.8F, 1.0F);
      boundStack.m_41774_(1);
      return HookUtil.HitResult.HANDLED;
   }

   private static HookUtil.HitResult placeBoundBlock(
      Level level, ItemStack boundStack, @Nullable LivingEntity owner, BlockItem blockItem, BlockHitResult hitResult
   ) {
      BlockPos hitPos = hitResult.m_82425_();
      BlockState hitState = level.m_8055_(hitPos);
      BlockPos placePos = hitState.m_247087_() ? hitPos : hitPos.m_121945_(hitResult.m_82434_());
      BlockState existingState = level.m_8055_(placePos);
      if (existingState.m_247087_() && level.m_6425_(placePos).m_76178_()) {
         BlockState placeState = blockItem.m_40614_().m_49966_();
         if (!placeState.m_60710_(level, placePos)) {
            return HookUtil.HitResult.PASS;
         } else if (!level.m_7731_(placePos, placeState, 3)) {
            return HookUtil.HitResult.PASS;
         } else {
            blockItem.m_40614_().m_6402_(level, placePos, placeState, owner, boundStack);
            level.m_5594_(null, placePos, placeState.getSoundType(level, placePos, owner).m_56777_(), SoundSource.BLOCKS, 1.0F, 1.0F);
            boundStack.m_41774_(1);
            return HookUtil.HitResult.HANDLED;
         }
      } else {
         return HookUtil.HitResult.PASS;
      }
   }

   private static HookUtil.HitResult applyBoneMeal(Level level, ItemStack boundStack, BlockHitResult hitResult) {
      BlockPos pos = hitResult.m_82425_();
      BlockState state = level.m_8055_(pos);
      if (state.m_60734_() instanceof BonemealableBlock bonemealableBlock) {
         if (level instanceof ServerLevel serverLevel
            && bonemealableBlock.m_7370_(level, pos, state, false)
            && bonemealableBlock.m_214167_(level, level.f_46441_, pos, state)) {
            bonemealableBlock.m_214148_(serverLevel, level.f_46441_, pos, state);
            level.m_46796_(1505, pos, 0);
            boundStack.m_41774_(1);
            return HookUtil.HitResult.HANDLED;
         }

         return HookUtil.HitResult.PASS;
      } else {
         return HookUtil.HitResult.PASS;
      }
   }

   private static boolean igniteTntBlock(Level level, BlockPos pos, @Nullable LivingEntity owner) {
      BlockState state = level.m_8055_(pos);
      if (!state.m_60713_(Blocks.f_50077_)) {
         return false;
      } else {
         if (!level.f_46443_) {
            PrimedTnt primedTnt = new PrimedTnt(level, (double)pos.m_123341_() + 0.5, (double)pos.m_123342_(), (double)pos.m_123343_() + 0.5, owner);
            level.m_7967_(primedTnt);
            level.m_7471_(pos, false);
            level.m_5594_(null, pos, SoundEvents.f_12512_, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         return true;
      }
   }

   private static boolean placeFire(Level level, BlockHitResult hitResult) {
      BlockPos firePos = hitResult.m_82425_().m_121945_(hitResult.m_82434_());
      BlockState fireState = Blocks.f_50083_.m_49966_();
      if (level.m_8055_(firePos).m_247087_() && fireState.m_60710_(level, firePos)) {
         level.m_7731_(firePos, fireState, 3);
         return true;
      } else {
         return false;
      }
   }

   private static void damageTool(ItemStack stack, @Nullable LivingEntity owner) {
      if (stack.m_41763_()) {
         ServerPlayer serverPlayer = owner instanceof ServerPlayer player ? player : null;
         RandomSource random = owner != null ? owner.m_217043_() : RandomSource.m_216327_();
         if (stack.m_220157_(1, random, serverPlayer)) {
            stack.m_41774_(1);
            stack.m_41721_(0);
         }
      }
   }

   public static enum HitResult {
      PASS,
      HANDLED;
   }

   public static record ItemInteractionResult(HookUtil.HitResult hitResult, ItemStack itemStack) {
      public boolean handled() {
         return this.hitResult == HookUtil.HitResult.HANDLED;
      }
   }
}
