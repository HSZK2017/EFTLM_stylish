package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.entity.BlueDemonEntity;
import com.pla.annoyingvillagers.entity.BlueDemonThrownTridentEntity;
import com.pla.annoyingvillagers.entity.ElectricAreaEntity;
import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.util.BlueDemonUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class BlueDemonTridentItem extends SwordItem {
   private static final double OWNER_HALF_BOX = 50.0;
   private static final int DAMAGE_ZONE_DURATION = 100;
   private static final float DAMAGE_ZONE_DAMAGE = 4.0F;
   private static final int DAMAGE_ZONE_INTERVAL = 10;
   private static final float RELAUNCH_SPEED = 2.5F;
   public static final String TAG_STORM_ENERGY = "BlueDemonStormEnergy";
   public static final int MAX_STORM_ENERGY = 100;
   private static final int ENERGY_METER_STEPS = 18;
   private static final int ENERGY_COLOR = 5634047;
   private static final int ENERGY_DIM_COLOR = 4082253;
   private static final int ENERGY_TEXT_COLOR = 12450815;
   private static final int ENERGY_FULL_COLOR = 8191999;
   private static final int FESTIVAL_GATHER_SIZE = 10;
   private static final int FESTIVAL_GATHER_MAX_Y_DIFF = 6;
   private static final double FESTIVAL_GATHER_MIN_OWNER_DISTANCE_SQR = 2.25;

   public BlueDemonTridentItem() {
      super(new Tier() {
         public int m_6609_() {
            return 1561;
         }

         public float m_6624_() {
            return 8.0F;
         }

         public float m_6631_() {
            return 3.5F;
         }

         public int m_6604_() {
            return 3;
         }

         public int m_6601_() {
            return 10;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_151265_();
         }
      }, 3, -2.7F, new Properties());
   }

   public static Vec3 getTridentThrowDirection(LivingEntity livingEntity, Vec3 startPos) {
      if (livingEntity instanceof Player) {
         return livingEntity.m_20252_(1.0F).m_82541_();
      } else {
         if (livingEntity instanceof Mob mob) {
            LivingEntity target = mob.m_5448_();
            if (target != null && target.m_6084_()) {
               Vec3 targetPos = target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.7, 0.0);
               Vec3 dir = targetPos.m_82546_(startPos);
               if (dir.m_82556_() > 1.0E-7) {
                  return dir.m_82541_();
               }
            }
         }

         Vec3 fallback = livingEntity.m_20252_(1.0F);
         return fallback.m_82556_() > 1.0E-7 ? fallback.m_82541_() : null;
      }
   }

   public boolean m_5812_(@NotNull ItemStack stack) {
      return super.m_5812_(stack) || isFullyCharged(stack);
   }

   public static boolean isBlueDemonTrident(ItemStack stack) {
      return !stack.m_41619_() && stack.m_41720_() instanceof BlueDemonTridentItem;
   }

   public static int getOnlyStormEnergy(ItemStack stack) {
      if (!isBlueDemonTrident(stack)) {
         return 0;
      } else {
         CompoundTag tag = stack.m_41783_();
         return tag == null ? 0 : Mth.m_14045_(tag.m_128451_("BlueDemonStormEnergy"), 0, 100);
      }
   }

   public static boolean checkOnlyFullyCharged(ItemStack stack) {
      return getOnlyStormEnergy(stack) >= 100;
   }

   public static int getStormEnergy(ItemStack stack) {
      if (!isBlueDemonTrident(stack)) {
         return 0;
      } else {
         CompoundTag tag = stack.m_41784_();
         return Mth.m_14045_(tag.m_128451_("BlueDemonStormEnergy"), 0, 100);
      }
   }

   public static void setStormEnergy(ItemStack stack, int value) {
      if (isBlueDemonTrident(stack)) {
         stack.m_41784_().m_128405_("BlueDemonStormEnergy", Mth.m_14045_(value, 0, 100));
      }
   }

   public static boolean isFullyCharged(ItemStack stack) {
      return getStormEnergy(stack) >= 100;
   }

   public static void addStormEnergy(ItemStack stack, int amount) {
      if (isBlueDemonTrident(stack) && amount > 0) {
         int current = getStormEnergy(stack);
         int added = Math.min(amount, 100 - current);
         if (added > 0) {
            setStormEnergy(stack, current + added);
         }
      }
   }

   public static void spawnDamageZones(ServerLevel serverLevel, LivingEntity owner) {
      ElectricAreaEntity ownerZone = new ElectricAreaEntity(serverLevel, owner, owner.m_20182_(), 2.5, 100, 4.0F, 10);
      serverLevel.m_7967_(ownerZone);

      for (BlueDemonThrownTridentEntity trident : getGroundedOwnerTridents(serverLevel, owner)) {
         ElectricAreaEntity tridentZone = new ElectricAreaEntity(serverLevel, owner, trident.m_20182_(), 1.5, 100, 4.0F, 10);
         serverLevel.m_7967_(tridentZone);
      }
   }

   public static void relaunchGroundedTridents(ServerLevel serverLevel, LivingEntity owner) {
      relaunchGroundedTridents(serverLevel, owner, false);
   }

   public static void relaunchGroundedTridents(ServerLevel serverLevel, LivingEntity owner, boolean skipSummoned) {
      List<BlueDemonThrownTridentEntity> tridents;
      if (!skipSummoned) {
         tridents = getGroundedOwnerTridents(serverLevel, owner);
      } else {
         tridents = getGroundedOwnerTridentsSkipSummoned(serverLevel, owner);
      }

      if (!tridents.isEmpty()) {
         List<LivingEntity> targets = getNearbyTargets(serverLevel, owner);

         for (int i = 0; i < tridents.size(); i++) {
            BlueDemonThrownTridentEntity trident = tridents.get(i);
            LivingEntity target = targets.isEmpty() ? null : targets.get(i % targets.size());
            Vec3 fallback = target == null ? getTridentThrowDirection(owner, trident.m_20182_()) : null;
            int extraDelay = 2 + i * 2 + serverLevel.f_46441_.m_188503_(3);
            trident.beginAnimatedRelaunch(target, fallback, 2.5F, 0.0F, extraDelay);
         }
      }
   }

   public static void summonLightningAtGroundedTridents(ServerLevel serverLevel, LivingEntity owner) {
      for (BlueDemonThrownTridentEntity trident : getAllOwnerTridents(serverLevel, owner)) {
         trident.summonLightningAtSelf();
      }
   }

   private static void spawnMissingFestivalSupportTridents(
      ServerLevel serverLevel, BlueDemonEntity owner, int missingCount, List<BlueDemonThrownTridentEntity> occupiedTridents, boolean strikeWhenFinished
   ) {
      if (missingCount > 0) {
         List<BlockPos> freePositions = getFreeGatherStandPositions(serverLevel, owner, occupiedTridents);
         int spawnCount = Math.min(missingCount, freePositions.size());

         for (int i = 0; i < spawnCount; i++) {
            BlueDemonThrownTridentEntity spawned = spawnFestivalSupportTrident(serverLevel, owner, freePositions.get(i), strikeWhenFinished);
            spawned.setSummonedGroundTridentFestival(true);
            occupiedTridents.add(spawned);
         }
      }
   }

   private static double horizontalDistanceToOwnerSqr(BlockPos pos, LivingEntity owner) {
      double dx = (double)pos.m_123341_() + 0.5 - owner.m_20185_();
      double dz = (double)pos.m_123343_() + 0.5 - owner.m_20189_();
      return dx * dx + dz * dz;
   }

   private static List<BlockPos> buildRandomGatherStandPositions(ServerLevel serverLevel, LivingEntity owner) {
      List<BlockPos> result = new ArrayList<>();
      int half = 5;
      int startX = Mth.m_14107_(owner.m_20185_()) - half;
      int startZ = Mth.m_14107_(owner.m_20189_()) - half;
      int ownerY = Mth.m_14107_(owner.m_20186_());

      for (int x = 0; x < 10; x++) {
         for (int z = 0; z < 10; z++) {
            double sampleX = (double)(startX + x) + 0.5;
            double sampleZ = (double)(startZ + z) + 0.5;
            BlockPos candidate = findNearestStandablePosNearY(serverLevel, sampleX, sampleZ, ownerY);
            if (candidate != null && Math.abs(candidate.m_123342_() - owner.m_20183_().m_123342_()) <= 6) {
               Vec3 center = new Vec3((double)candidate.m_123341_() + 0.5, (double)candidate.m_123342_() + 0.05, (double)candidate.m_123343_() + 0.5);
               if (!(center.m_82557_(owner.m_20182_()) < 2.25) && !result.contains(candidate)) {
                  result.add(candidate.m_7949_());
               }
            }
         }
      }

      Collections.shuffle(result, new Random(serverLevel.f_46441_.m_188505_()));
      return result;
   }

   private static List<BlockPos> buildCompactGatherStandPositions(ServerLevel serverLevel, LivingEntity owner) {
      List<BlockPos> result = new ArrayList<>();
      int half = 5;
      int startX = Mth.m_14107_(owner.m_20185_()) - half;
      int startZ = Mth.m_14107_(owner.m_20189_()) - half;
      int ownerY = Mth.m_14107_(owner.m_20186_());

      for (int x = 0; x < 10; x++) {
         for (int z = 0; z < 10; z++) {
            double sampleX = (double)(startX + x) + 0.5;
            double sampleZ = (double)(startZ + z) + 0.5;
            BlockPos standPos = findNearestStandablePosNearY(serverLevel, sampleX, sampleZ, ownerY);
            if (standPos != null
               && Math.abs(standPos.m_123342_() - owner.m_20183_().m_123342_()) <= 6
               && !(horizontalDistanceToOwnerSqr(standPos, owner) < 2.25)
               && !result.contains(standPos)) {
               result.add(standPos.m_7949_());
            }
         }
      }

      result.sort(
         Comparator.<BlockPos>comparingDouble(pos -> horizontalDistanceToOwnerSqr(pos, owner))
            .thenComparingInt(Vec3i::m_123342_)
            .thenComparingInt(Vec3i::m_123341_)
            .thenComparingInt(Vec3i::m_123343_)
      );
      return result;
   }

   private static List<BlockPos> getFreeGatherStandPositions(ServerLevel serverLevel, LivingEntity owner, List<BlueDemonThrownTridentEntity> occupiedTridents) {
      List<BlockPos> result = buildRandomGatherStandPositions(serverLevel, owner);
      result.removeIf(pos -> {
         Vec3 center = new Vec3((double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 0.05, (double)pos.m_123343_() + 0.5);

         for (BlueDemonThrownTridentEntity other : occupiedTridents) {
            if (other.m_6084_() && other.m_20182_().m_82557_(center) < 0.25) {
               return true;
            }
         }

         return false;
      });
      return result;
   }

   public static void gatherGroundedTridentsAroundOwner(ServerLevel serverLevel, LivingEntity owner) {
      List<BlueDemonThrownTridentEntity> tridents = new ArrayList<>(getGroundedOwnerTridents(serverLevel, owner));
      if (!tridents.isEmpty()) {
         List<BlockPos> standPositions = buildRandomGatherStandPositions(serverLevel, owner);
         if (!standPositions.isEmpty()) {
            tridents.sort(Comparator.comparingLong(BlueDemonThrownTridentEntity::getSpawnSequence).thenComparing(Entity::m_20148_));
            int count = Math.min(tridents.size(), standPositions.size());

            for (int i = 0; i < count; i++) {
               tridents.get(i).placeAsGroundedSupport(owner, standPositions.get(i));
            }
         }
      }
   }

   @Nullable
   private static BlockPos findNearestStandablePosNearY(ServerLevel serverLevel, double x, double z, int originY) {
      int blockX = Mth.m_14107_(x);
      int blockZ = Mth.m_14107_(z);

      for (int offset = 0; offset <= 8; offset++) {
         BlockPos downPos = new BlockPos(blockX, originY - offset, blockZ);
         if (isValidFestivalStandPos(serverLevel, downPos)) {
            return downPos;
         }

         if (offset != 0) {
            BlockPos upPos = new BlockPos(blockX, originY + offset, blockZ);
            if (isValidFestivalStandPos(serverLevel, upPos)) {
               return upPos;
            }
         }
      }

      return null;
   }

   private static boolean isValidFestivalStandPos(ServerLevel serverLevel, BlockPos standPos) {
      if (!serverLevel.m_46739_(standPos) || !serverLevel.m_46739_(standPos.m_7495_())) {
         return false;
      } else if (!serverLevel.m_46859_(standPos)) {
         return false;
      } else if (!serverLevel.m_6425_(standPos).m_76178_()) {
         return false;
      } else {
         return !serverLevel.m_6425_(standPos.m_7495_()).m_76178_() ? false : serverLevel.m_8055_(standPos.m_7495_()).m_280555_();
      }
   }

   private static BlueDemonThrownTridentEntity spawnFestivalSupportTrident(
      ServerLevel serverLevel, BlueDemonEntity owner, BlockPos standPos, boolean strikeWhenFinished
   ) {
      ItemStack stack = owner.m_21205_();
      BlueDemonThrownTridentEntity trident = new BlueDemonThrownTridentEntity(serverLevel, owner, stack);
      trident.assignSpawnSequence(owner);
      trident.trimOldGroundedTridentsAroundOwnerOnSpawn();
      trident.beginFestivalGroundRise(owner, standPos, strikeWhenFinished);
      serverLevel.m_7967_(trident);
      serverLevel.m_8767_(
         (SimpleParticleType)AnnoyingVillagersModParticleTypes.ELECTRIC_SPARK.get(),
         (double)standPos.m_123341_() + 0.5,
         (double)standPos.m_123342_() + 0.15,
         (double)standPos.m_123343_() + 0.5,
         12,
         0.18,
         0.25,
         0.18,
         0.02
      );
      serverLevel.m_5594_(
         null,
         BlockPos.m_274561_((double)standPos.m_123341_() + 0.5, (double)standPos.m_123342_(), (double)standPos.m_123343_() + 0.5),
         (SoundEvent)AnnoyingVillagersModSounds.ELECTRIFY.get(),
         SoundSource.NEUTRAL,
         0.8F,
         0.9F + serverLevel.f_46441_.m_188501_() * 0.2F
      );
      return trident;
   }

   public static void summonMissingTridentAndAnimate(ServerLevel serverLevel, LivingEntity owner) {
      if (owner instanceof BlueDemonEntity blueDemon) {
         gatherGroundedTridentsAroundOwner(serverLevel, owner);
         List<BlueDemonThrownTridentEntity> existingTridents = new ArrayList<>(getAllOwnerTridents(serverLevel, owner));
         if (existingTridents.size() < 20) {
            spawnMissingFestivalSupportTridents(serverLevel, blueDemon, 20 - existingTridents.size(), existingTridents, false);
         }
      }
   }

   public static void summonSuperLightningAtGroundedTridents(ServerLevel serverLevel, LivingEntity owner) {
      for (BlueDemonThrownTridentEntity trident : getGroundedOwnerTridents(serverLevel, owner)) {
         trident.summonSuperLightningAtSelf();
         if (trident.isSummonedGroundTridentFestival()) {
            trident.finishSummonedGroundTridentFestival();
         }
      }
   }

   public static List<BlueDemonThrownTridentEntity> getAllOwnerTridents(ServerLevel serverLevel, LivingEntity owner) {
      return serverLevel.m_6443_(BlueDemonThrownTridentEntity.class, makeOwnerBox(owner), trident -> trident.m_6084_() && trident.belongsToOwner(owner));
   }

   public static List<BlueDemonThrownTridentEntity> getGroundedOwnerTridents(ServerLevel serverLevel, LivingEntity owner) {
      return serverLevel.m_6443_(
         BlueDemonThrownTridentEntity.class, makeOwnerBox(owner), trident -> trident.m_6084_() && trident.isGroundedTrident() && trident.belongsToOwner(owner)
      );
   }

   public static List<BlueDemonThrownTridentEntity> getGroundedOwnerTridentsSkipSummoned(ServerLevel serverLevel, LivingEntity owner) {
      return serverLevel.m_6443_(
         BlueDemonThrownTridentEntity.class,
         makeOwnerBox(owner),
         trident -> trident.m_6084_() && trident.isGroundedTrident() && !trident.isSummonedGroundTridentFestival() && trident.belongsToOwner(owner)
      );
   }

   private static List<LivingEntity> getNearbyTargets(ServerLevel serverLevel, LivingEntity owner) {
      List<LivingEntity> targets = serverLevel.m_6443_(LivingEntity.class, makeOwnerBox(owner), target -> isValidTarget(owner, target));
      targets.sort(Comparator.comparingDouble(target -> target.m_20280_(owner)));
      return targets;
   }

   private static boolean isValidTarget(LivingEntity owner, LivingEntity target) {
      if (target == owner) {
         return false;
      } else if (target.m_6084_() && !target.m_5833_()) {
         if (target instanceof Player player && player.m_7500_()) {
            return false;
         }

         if (owner instanceof BlueDemonEntity blueDemonEntity && blueDemonEntity.getBbqEntity() != null && target == blueDemonEntity.getBbqEntity()) {
            return false;
         }

         return !owner.m_7307_(target);
      } else {
         return false;
      }
   }

   private static AABB makeOwnerBox(Entity owner) {
      return new AABB(
         owner.m_20185_() - 50.0,
         (double)owner.m_9236_().m_141937_(),
         owner.m_20189_() - 50.0,
         owner.m_20185_() + 50.0,
         (double)owner.m_9236_().m_151558_(),
         owner.m_20189_() + 50.0
      );
   }

   public void m_6883_(@NotNull ItemStack itemstack, @NotNull Level level, @NotNull Entity entity, int i, boolean flag) {
      super.m_6883_(itemstack, level, entity, i, flag);
      if (flag && entity instanceof Player player && entity.m_9236_() instanceof ServerLevel serverLevel) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer skillContainer = serverPlayerPatch.getSkill(AVSkills.TRIDENT_FESTIVAL);
            if (skillContainer != null && skillContainer.getStack() >= 1) {
               double d0 = entity.m_20185_();
               double d1 = entity.m_20186_();
               double d2 = entity.m_20189_();
               if (Math.random() <= 0.1) {
                  BlueDemonUtil.spawnBlueDemonEffect(serverLevel, entity);
                  if (serverLevel.f_46441_.m_188500_() <= 0.8) {
                     float volume = (float)Mth.m_216263_(serverLevel.f_46441_, 0.05, 0.5);
                     float pitch = (float)Mth.m_216263_(serverLevel.f_46441_, 0.8, 1.1);
                     serverLevel.m_5594_(
                        null, BlockPos.m_274561_(d0, d1, d2), (SoundEvent)AnnoyingVillagersModSounds.ELECTRIFY.get(), SoundSource.NEUTRAL, volume, pitch
                     );
                  }
               }
            }
         }
      }
   }

   public void m_7373_(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
      super.m_7373_(stack, level, tooltip, flag);
      int energy = getStormEnergy(stack);
      tooltip.add(Component.m_237115_("tooltip.annoyingvillagers.blue_demon_trident"));
      addStormChargeTooltip(tooltip, energy);
   }

   private static void addStormChargeTooltip(List<Component> tooltip, int energy) {
      tooltip.add(
         Component.m_237113_(Component.m_237115_("tooltip.annoyingvillagers.blue_demon_trident_thunder_charge").getString())
            .m_130938_(style -> style.m_131136_(true).m_131148_(TextColor.m_131266_(5634047)))
      );
      tooltip.add(Component.m_237113_(energy + " / 100").m_130938_(style -> style.m_131148_(TextColor.m_131266_(12450815))));
      tooltip.add(buildStormMeter(energy));
      if (energy >= 100) {
         tooltip.add(
            Component.m_237113_(Component.m_237115_("tooltip.annoyingvillagers.thunder_charged").getString())
               .m_130938_(style -> style.m_131136_(true).m_131148_(TextColor.m_131266_(8191999)))
         );
      }
   }

   private static Component buildStormMeter(int energy) {
      int filledSteps = Math.round((float)energy / 100.0F * 18.0F);
      filledSteps = Mth.m_14045_(filledSteps, 0, 18);
      MutableComponent meter = Component.m_237119_();
      meter.m_7220_(Component.m_237113_("⚡ ").m_130938_(style -> style.m_131148_(TextColor.m_131266_(5634047))));

      for (int i = 0; i < 18; i++) {
         boolean filled = i < filledSteps;
         meter.m_7220_(Component.m_237113_(filled ? "▰" : "▱").m_130938_(style -> style.m_131148_(TextColor.m_131266_(filled ? 5634047 : 4082253))));
      }

      return meter;
   }
}
