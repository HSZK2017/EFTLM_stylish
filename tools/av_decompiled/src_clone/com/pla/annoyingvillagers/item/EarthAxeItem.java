package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.entity.FloatingLookBlockEntity;
import com.pla.annoyingvillagers.entity.RisingWallBlockEntity;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EarthAxeItem extends SwordItem {
   private static final int WALL_WIDTH = 5;
   private static final int WALL_HEIGHT = 4;
   private static final int WALL_DISTANCE = 2;
   private static final int RISE_TICKS = 10;

   public EarthAxeItem() {
      super(new Tier() {
         public int m_6609_() {
            return 2031;
         }

         public float m_6624_() {
            return 6.0F;
         }

         public float m_6631_() {
            return 6.0F;
         }

         public int m_6604_() {
            return 5;
         }

         public int m_6601_() {
            return 21;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Items.f_42418_)});
         }
      }, 3, -2.8F, new Properties());
   }

   public static void summonEarthWall(ServerLevel level, LivingEntity caster) {
      if (!caster.m_20069_()) {
         BlockPos groundPos = caster.m_20097_();
         BlockState groundState = level.m_8055_(groundPos);
         if (!groundState.m_60819_().m_205070_(FluidTags.f_13131_)) {
            BlockState wallState = chooseWallBlock(groundState);
            if (wallState != null) {
               Direction forward = caster.m_6350_();
               Direction right = forward.m_122427_();
               BlockPos casterFeet = caster.m_20183_();
               BlockPos centerBase = new BlockPos(casterFeet.m_123341_(), groundPos.m_123342_() + 1, casterFeet.m_123343_()).m_5484_(forward, 2);
               int halfWidth = 2;
               boolean spawnedAny = false;

               for (int y = 0; y < 4; y++) {
                  for (int x = -halfWidth; x <= halfWidth; x++) {
                     BlockPos finalPos = centerBase.m_5484_(right, x).m_6630_(y);
                     if (canSpawnWallBlockAt(level, finalPos)) {
                        int delay = y * 3 + Math.abs(x) * 2;
                        RisingWallBlockEntity blockEntity = new RisingWallBlockEntity(level, finalPos, wallState, delay, 10);
                        level.m_7967_(blockEntity);
                        spawnedAny = true;
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean canLiftBlock(ServerLevel level, BlockPos pos, BlockState state) {
      if (state.m_60795_()) {
         return false;
      } else if (!state.m_60819_().m_76178_()) {
         return false;
      } else if (state.m_60799_() != RenderShape.MODEL) {
         return false;
      } else if (state.m_60800_(level, pos) < 0.0F) {
         return false;
      } else if (!state.m_60713_(Blocks.f_50752_)
         && !state.m_60713_(Blocks.f_50375_)
         && !state.m_60713_(Blocks.f_50677_)
         && !state.m_60713_(Blocks.f_50454_)
         && !state.m_60713_(Blocks.f_50678_)
         && !state.m_60713_(Blocks.f_50272_)
         && !state.m_60713_(Blocks.f_50448_)
         && !state.m_60713_(Blocks.f_50447_)) {
         BlockEntity blockEntity = level.m_7702_(pos);
         return !(blockEntity instanceof Container);
      } else {
         return false;
      }
   }

   @Nullable
   public static BlockPos findLiftableBlockUnderPoint(ServerLevel level, Vec3 worldPos, int maxDown, int horizontalRadius) {
      BlockPos center = BlockPos.m_274446_(worldPos);

      for (int dy = 0; dy <= maxDown; dy++) {
         int y = center.m_123342_() - dy;
         BlockPos bestPos = null;
         double bestDistance = Double.MAX_VALUE;

         for (int dx = -horizontalRadius; dx <= horizontalRadius; dx++) {
            for (int dz = -horizontalRadius; dz <= horizontalRadius; dz++) {
               BlockPos candidate = new BlockPos(center.m_123341_() + dx, y, center.m_123343_() + dz);
               if (level.m_6857_().m_61937_(candidate) && level.m_46805_(candidate)) {
                  BlockState state = level.m_8055_(candidate);
                  if (canLiftBlock(level, candidate, state)) {
                     double distance = Vec3.m_82512_(candidate).m_82557_(worldPos);
                     if (distance < bestDistance) {
                        bestDistance = distance;
                        bestPos = candidate;
                     }
                  }
               }
            }
         }

         if (bestPos != null) {
            return bestPos.m_7949_();
         }
      }

      return null;
   }

   private static boolean canSpawnWallBlockAt(ServerLevel level, BlockPos pos) {
      if (!level.m_6425_(pos).m_76178_()) {
         return false;
      } else {
         BlockState current = level.m_8055_(pos);
         return current.m_60795_() || current.m_247087_();
      }
   }

   @Nullable
   private static BlockState chooseWallBlock(BlockState groundState) {
      if (groundState.m_204336_(BlockTags.f_13029_)
         || groundState.m_60713_(Blocks.f_49992_)
         || groundState.m_60713_(Blocks.f_49993_)
         || groundState.m_60713_(Blocks.f_50062_)
         || groundState.m_60713_(Blocks.f_50394_)
         || groundState.m_60713_(Blocks.f_50471_)
         || groundState.m_60713_(Blocks.f_50473_)) {
         return Blocks.f_49992_.m_49966_();
      } else if (groundState.m_204336_(BlockTags.f_144274_)
         || groundState.m_60713_(Blocks.f_50440_)
         || groundState.m_60713_(Blocks.f_50493_)
         || groundState.m_60713_(Blocks.f_50546_)
         || groundState.m_60713_(Blocks.f_152549_)
         || groundState.m_60713_(Blocks.f_50599_)
         || groundState.m_60713_(Blocks.f_50195_)
         || groundState.m_60713_(Blocks.f_50093_)
         || groundState.m_60713_(Blocks.f_220864_)) {
         return Blocks.f_50493_.m_49966_();
      } else if (isWoodRelated(groundState)) {
         return Blocks.f_50493_.m_49966_();
      } else {
         return !isStoneRelated(groundState) && !groundState.m_60713_(Blocks.f_49994_) ? null : Blocks.f_49994_.m_49966_();
      }
   }

   private static boolean isWoodRelated(BlockState state) {
      return state.m_204336_(BlockTags.f_13106_)
         || state.m_204336_(BlockTags.f_13090_)
         || state.m_204336_(BlockTags.f_13096_)
         || state.m_204336_(BlockTags.f_13097_)
         || state.m_204336_(BlockTags.f_13098_)
         || state.m_204336_(BlockTags.f_13095_)
         || state.m_204336_(BlockTags.f_13102_)
         || state.m_204336_(BlockTags.f_13100_)
         || state.m_204336_(BlockTags.f_13092_);
   }

   private static boolean isStoneRelated(BlockState state) {
      return state.m_204336_(BlockTags.f_13061_)
         || state.m_204336_(BlockTags.f_13062_)
         || state.m_204336_(BlockTags.f_13091_)
         || state.m_60713_(Blocks.f_50069_)
         || state.m_60713_(Blocks.f_50652_)
         || state.m_60713_(Blocks.f_50079_)
         || state.m_60713_(Blocks.f_152550_)
         || state.m_60713_(Blocks.f_152551_)
         || state.m_60713_(Blocks.f_50730_)
         || state.m_60713_(Blocks.f_50137_)
         || state.m_60713_(Blocks.f_152597_)
         || state.m_60713_(Blocks.f_50134_)
         || state.m_60713_(Blocks.f_50259_);
   }

   public static boolean liftBlockAt(ServerLevel level, BlockPos pos, @Nullable LivingEntity owner) {
      return liftBlockAt(level, pos, owner == null ? null : owner.m_20148_());
   }

   public static boolean liftBlockAt(ServerLevel level, BlockPos pos, @Nullable UUID ownerUuid) {
      pos = pos.m_7949_();
      if (!level.m_6857_().m_61937_(pos)) {
         return false;
      } else if (!level.m_46805_(pos)) {
         return false;
      } else {
         BlockState state = level.m_8055_(pos);
         if (!canLiftBlock(level, pos, state)) {
            return false;
         } else {
            CompoundTag blockEntityTag = saveBlockEntityTag(level, pos);
            level.m_46747_(pos);
            level.m_7731_(pos, Blocks.f_50016_.m_49966_(), 3);
            FloatingLookBlockEntity floatingBlock = new FloatingLookBlockEntity(level, pos, state, ownerUuid, blockEntityTag);
            level.m_7967_(floatingBlock);
            return true;
         }
      }
   }

   @Nullable
   private static CompoundTag saveBlockEntityTag(ServerLevel level, BlockPos pos) {
      BlockEntity blockEntity = level.m_7702_(pos);
      return blockEntity == null ? null : blockEntity.m_187480_();
   }
}
