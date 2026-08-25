package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class VillagerUtil {
   public static ItemStack generateMainWeaponItem() {
      float chance = new Random().nextFloat();
      ItemStack itemStack;
      if (chance <= 0.2F) {
         itemStack = new ItemStack((ItemLike)AnnoyingVillagersModItems.DIAMOND_KNIFE.get());
      } else if (chance <= 0.4F) {
         itemStack = new ItemStack((ItemLike)AnnoyingVillagersModItems.DIAMOND_FALCHION.get());
      } else if (chance <= 0.6F) {
         itemStack = new ItemStack((ItemLike)AnnoyingVillagersModItems.HOOKED_DIAMOND_SWORD.get());
      } else {
         itemStack = new ItemStack((ItemLike)AnnoyingVillagersModItems.DIAMOND_KNIGHT_SWORD.get());
      }

      float enchantChance = new Random().nextFloat();
      if ((double)enchantChance <= 0.2) {
         itemStack.m_41663_(Enchantments.f_44981_, new Random().nextInt(1, 3));
      }

      if ((double)enchantChance <= 0.4) {
         itemStack.m_41663_(Enchantments.f_44983_, new Random().nextInt(1, 3));
      }

      if ((double)enchantChance <= 0.6) {
         itemStack.m_41663_(Enchantments.f_44978_, new Random().nextInt(1, 3));
      }

      if ((double)enchantChance <= 0.8) {
         itemStack.m_41663_(Enchantments.f_44980_, new Random().nextInt(1, 3));
      }

      itemStack.m_41663_(Enchantments.f_44977_, new Random().nextInt(1, 3));
      return itemStack;
   }

   private static Vec3 localCommandOffset(Vec3 origin, float yaw, double localX, double localY, double localZ) {
      Vec3 forward = Vec3.m_82498_(0.0F, yaw).m_82541_();
      Vec3 left = new Vec3(forward.f_82481_, 0.0, -forward.f_82479_).m_82541_();
      return origin.m_82549_(left.m_82490_(localX)).m_82520_(0.0, localY, 0.0).m_82549_(forward.m_82490_(localZ));
   }

   private static boolean isValidGroundSpawn(ServerLevel level, Mob probe, BlockPos feetPos, float yaw) {
      BlockPos floorPos = feetPos.m_7495_();
      BlockPos headPos = feetPos.m_7494_();
      BlockState floorState = level.m_8055_(floorPos);
      BlockState feetState = level.m_8055_(feetPos);
      BlockState headState = level.m_8055_(headPos);
      if (floorState.m_60795_()) {
         return false;
      } else if (floorState.m_60812_(level, floorPos).m_83281_()) {
         return false;
      } else if (!level.m_6425_(floorPos).m_76178_()) {
         return false;
      } else if (!feetState.m_60812_(level, feetPos).m_83281_()) {
         return false;
      } else if (!headState.m_60812_(level, headPos).m_83281_()) {
         return false;
      } else if (!level.m_6425_(feetPos).m_76178_()) {
         return false;
      } else if (!level.m_6425_(headPos).m_76178_()) {
         return false;
      } else {
         probe.m_7678_((double)feetPos.m_123341_() + 0.5, (double)feetPos.m_123342_(), (double)feetPos.m_123343_() + 0.5, yaw, 0.0F);
         return level.m_45786_(probe) && !level.m_46855_(probe.m_20191_());
      }
   }

   @Nullable
   private static Vec3 findSurfaceNearDeathY(ServerLevel level, Mob probe, BlockPos columnBase, float yaw, int maxDown, int maxUp) {
      int startY = columnBase.m_123342_();
      int minY = Math.max(level.m_141937_() + 1, startY - maxDown);
      int maxY = Math.min(level.m_151558_() - 2, startY + maxUp);

      for (int y = startY; y >= minY; y--) {
         BlockPos feetPos = new BlockPos(columnBase.m_123341_(), y, columnBase.m_123343_());
         if (isValidGroundSpawn(level, probe, feetPos, yaw)) {
            return Vec3.m_82539_(feetPos);
         }
      }

      for (int yx = startY + 1; yx <= maxY; yx++) {
         BlockPos feetPos = new BlockPos(columnBase.m_123341_(), yx, columnBase.m_123343_());
         if (isValidGroundSpawn(level, probe, feetPos, yaw)) {
            return Vec3.m_82539_(feetPos);
         }
      }

      return null;
   }

   @Nullable
   private static Vec3 findSafeSpawnNearLocalOffset(ServerLevel level, Vec3 origin, float yaw, Mob probe, double localX, double localY, double localZ) {
      Vec3 wanted = localCommandOffset(origin, yaw, localX, localY, localZ);
      int baseX = Mth.m_14107_(wanted.f_82479_);
      int baseY = Mth.m_14107_(origin.f_82480_ + localY);
      int baseZ = Mth.m_14107_(wanted.f_82481_);

      for (int radius = 0; radius <= 2; radius++) {
         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               if (radius == 0 || Math.abs(dx) == radius || Math.abs(dz) == radius) {
                  BlockPos column = new BlockPos(baseX + dx, baseY, baseZ + dz);
                  Vec3 found = findSurfaceNearDeathY(level, probe, column, yaw, 16, 4);
                  if (found != null) {
                     return found;
                  }
               }
            }
         }
      }

      return null;
   }

   @Nullable
   public static <T extends Mob> T spawnMobAtLocalOffset(
      ServerLevel level, Vec3 origin, float yaw, EntityType<T> type, double localX, double localY, double localZ
   ) {
      T mob = (T)type.m_20615_(level);
      if (mob == null) {
         return null;
      } else {
         Vec3 spawnPos = findSafeSpawnNearLocalOffset(level, origin, yaw, mob, localX, localY, localZ);
         if (spawnPos == null) {
            mob.m_146870_();
            return null;
         } else {
            mob.m_7678_(spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_, yaw, 0.0F);
            mob.m_6518_(level, level.m_6436_(BlockPos.m_274446_(spawnPos)), MobSpawnType.MOB_SUMMONED, null, null);
            level.m_7967_(mob);
            return mob;
         }
      }
   }

   public static void spawnBackupFirework(ServerLevel level, Vec3 origin) {
      ItemStack rocketStack = new ItemStack(Items.f_42688_);
      CompoundTag fireworksTag = rocketStack.m_41698_("Fireworks");
      fireworksTag.m_128344_("Flight", (byte)1);
      CompoundTag explosion = new CompoundTag();
      explosion.m_128344_("Type", (byte)3);
      explosion.m_128385_("Colors", new int[]{0});
      explosion.m_128379_("Flicker", true);
      ListTag explosions = new ListTag();
      explosions.add(explosion);
      fireworksTag.m_128365_("Explosions", explosions);
      FireworkRocketEntity rocket = new FireworkRocketEntity(level, origin.f_82479_, origin.f_82480_ + 10.0, origin.f_82481_, rocketStack);
      level.m_7967_(rocket);
   }

   public static ItemStack createBlackCreeperSignalFirework() {
      ItemStack stack = new ItemStack(Items.f_42688_);
      CompoundTag tag = stack.m_41784_();
      CompoundTag fireworksTag = new CompoundTag();
      fireworksTag.m_128344_("Flight", (byte)1);
      ListTag explosions = new ListTag();
      CompoundTag explosion = new CompoundTag();
      explosion.m_128344_("Type", (byte)3);
      explosion.m_128385_("Colors", new int[]{0});
      explosion.m_128379_("Flicker", true);
      explosions.add(explosion);
      fireworksTag.m_128365_("Explosions", explosions);
      tag.m_128365_("Fireworks", fireworksTag);
      stack.m_41714_(Component.m_237113_("Black Creeper Firework"));
      return stack;
   }

   public static boolean isBlackCreeperSignalFirework(ItemStack stack) {
      if (!stack.m_41619_() && stack.m_150930_(Items.f_42688_) && stack.m_41782_()) {
         CompoundTag tag = stack.m_41783_();
         if (tag == null) {
            return false;
         } else if (!tag.m_128425_("Fireworks", 10)) {
            return false;
         } else {
            CompoundTag fireworksTag = tag.m_128469_("Fireworks");
            if (!fireworksTag.m_128425_("Explosions", 9)) {
               return false;
            } else {
               ListTag explosions = fireworksTag.m_128437_("Explosions", 10);
               if (explosions.size() != 1) {
                  return false;
               } else {
                  CompoundTag explosion = explosions.m_128728_(0);
                  if (explosion.m_128445_("Type") != 3) {
                     return false;
                  } else {
                     int[] colors = explosion.m_128465_("Colors");
                     return colors.length == 1 && colors[0] == 0;
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   public static void launchBlackCreeperSignalFirework(ServerLevel serverLevel, double x, double y, double z) {
      ItemStack rocketStack = createBlackCreeperSignalFirework();
      FireworkRocketEntity rocket = new FireworkRocketEntity(serverLevel, x, y, z, rocketStack);
      serverLevel.m_7967_(rocket);
   }

   public static <T extends Mob> void summonSupportAt(ServerLevel serverLevel, EntityType<T> entityType, double baseX, double baseY, double baseZ) {
      BlockPos spawnPos = findSafeSupportSpawn(serverLevel, baseX + new Random().nextDouble(-10.0, 10.0), baseY, baseZ + new Random().nextDouble(-10.0, 10.0));
      if (spawnPos != null) {
         T mob = (T)entityType.m_20615_(serverLevel);
         if (mob != null) {
            mob.m_7678_(
               (double)spawnPos.m_123341_() + 0.5,
               (double)spawnPos.m_123342_(),
               (double)spawnPos.m_123343_() + 0.5,
               serverLevel.f_46441_.m_188501_() * 360.0F,
               0.0F
            );
            if (serverLevel.m_45786_(mob) && serverLevel.m_45784_(mob)) {
               mob.m_6518_(serverLevel, serverLevel.m_6436_(spawnPos), MobSpawnType.MOB_SUMMONED, null, null);
               serverLevel.m_7967_(mob);
            }
         }
      }
   }

   @Nullable
   public static BlockPos findSafeSupportSpawn(ServerLevel serverLevel, double x, double y, double z) {
      int baseX = Mth.m_14107_(x);
      int baseZ = Mth.m_14107_(z);
      int refY = Mth.m_14045_(Mth.m_14107_(y), serverLevel.m_141937_() + 1, serverLevel.m_151558_() - 2);

      for (int radius = 0; radius <= 2; radius++) {
         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               BlockPos found = findSafeSupportSpawnInColumn(serverLevel, baseX + dx, refY, baseZ + dz);
               if (found != null) {
                  return found;
               }
            }
         }
      }

      return null;
   }

   @Nullable
   private static BlockPos findSafeSupportSpawnInColumn(ServerLevel serverLevel, int x, int refY, int z) {
      int minY = serverLevel.m_141937_() + 1;
      int maxUpY = Math.min(serverLevel.m_151558_() - 2, refY + 3);

      for (int y = refY; y >= minY; y--) {
         BlockPos feetPos = new BlockPos(x, y, z);
         if (isSafeSupportSpawn(serverLevel, feetPos)) {
            return feetPos;
         }
      }

      for (int yx = refY + 1; yx <= maxUpY; yx++) {
         BlockPos feetPos = new BlockPos(x, yx, z);
         if (isSafeSupportSpawn(serverLevel, feetPos)) {
            return feetPos;
         }
      }

      return null;
   }

   private static boolean isSafeSupportSpawn(ServerLevel serverLevel, BlockPos feetPos) {
      BlockPos belowPos = feetPos.m_7495_();
      return serverLevel.m_8055_(belowPos).m_60783_(serverLevel, belowPos, Direction.UP)
         && serverLevel.m_6425_(feetPos).m_76178_()
         && serverLevel.m_6425_(feetPos.m_7494_()).m_76178_()
         && serverLevel.m_8055_(feetPos).m_247087_()
         && serverLevel.m_8055_(feetPos.m_7494_()).m_247087_();
   }

   public static void summonSupportAtLocalOffset(
      ServerLevel level, Vec3 origin, float yaw, EntityType<? extends Mob> type, double localX, double localY, double localZ
   ) {
      Mob mob = (Mob)type.m_20615_(level);
      if (mob != null) {
         Vec3 spawnPos = findSafeSpawnNearLocalOffset(level, origin, yaw, mob, localX, localY, localZ);
         if (spawnPos == null) {
            mob.m_146870_();
         } else {
            mob.m_7678_(spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_, yaw, 0.0F);
            if (level.m_45786_(mob) && !level.m_46855_(mob.m_20191_())) {
               mob.m_6518_(level, level.m_6436_(BlockPos.m_274446_(spawnPos)), MobSpawnType.MOB_SUMMONED, null, null);
               level.m_7967_(mob);
            } else {
               mob.m_146870_();
            }
         }
      }
   }

   private static EntityType<? extends Mob> rollRandomVillagerReinforcementType() {
      double roll = new Random().nextDouble();
      if (roll < 0.2) {
         return (EntityType<? extends Mob>)AnnoyingVillagersModEntities.PURPLE_VILLAGER_KNIGHT.get();
      } else if (roll < 0.4) {
         return (EntityType<? extends Mob>)AnnoyingVillagersModEntities.RED_VILLAGER_KNIGHT.get();
      } else if (roll < 0.6) {
         return (EntityType<? extends Mob>)AnnoyingVillagersModEntities.GREEN_VILLAGER_KNIGHT.get();
      } else {
         return roll < 0.8
            ? (EntityType)AnnoyingVillagersModEntities.BLUE_VILLAGER_KNIGHT.get()
            : (EntityType)AnnoyingVillagersModEntities.VILLAGER_SCOUT_CAPTAIN.get();
      }
   }

   public static void summonRandomVillagerSupportWave(ServerLevel level, Vec3 origin, float yaw) {
      Random random = new Random();
      summonSupportAtLocalOffset(level, origin, yaw, (EntityType<? extends Mob>)AnnoyingVillagersModEntities.VILLAGER_SCOUT.get(), 0.0, 0.0, 10.0);
      summonSupportAtLocalOffset(level, origin, yaw, (EntityType<? extends Mob>)AnnoyingVillagersModEntities.VILLAGER_SCOUT.get(), -5.0, 0.0, 12.0);
      summonSupportAtLocalOffset(level, origin, yaw, (EntityType<? extends Mob>)AnnoyingVillagersModEntities.VILLAGER_SCOUT.get(), 5.0, 0.0, 12.0);
      if (random.nextBoolean()) {
         summonSupportAtLocalOffset(level, origin, yaw, (EntityType<? extends Mob>)AnnoyingVillagersModEntities.VILLAGER_SCOUT.get(), -10.0, 0.0, 18.0);
         summonSupportAtLocalOffset(level, origin, yaw, (EntityType<? extends Mob>)AnnoyingVillagersModEntities.VILLAGER_SCOUT.get(), 10.0, 0.0, 18.0);
      }

      summonSupportAtLocalOffset(level, origin, yaw, rollRandomVillagerReinforcementType(), 0.0, 0.0, 22.0);
      if (random.nextBoolean()) {
         summonSupportAtLocalOffset(level, origin, yaw, (EntityType<? extends Mob>)AnnoyingVillagersModEntities.VILLAGER_SCOUT_CAPTAIN.get(), 10.0, 0.0, 24.0);
      }

      if (random.nextBoolean()) {
         summonSupportAtLocalOffset(level, origin, yaw, rollRandomVillagerReinforcementType(), -10.0, 0.0, 24.0);
      }
   }
}
