package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.combatbehaviour.CombatCommon;
import com.pla.annoyingvillagers.entity.DragonMeteoriteEntity;
import com.pla.annoyingvillagers.entity.ObsidianSledgehammerProjectileEntity;
import com.pla.annoyingvillagers.task.DelayedTask;
import java.util.Random;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.FORGE
)
public final class MobPlaceBlockEvent {
   private static final double MAX_PLACE_BLOCK_GROUND_GAP = 2.0;
   private static final int CLEAR_BLOCK_DAMAGE_DELAY = 10;
   private static final int PLACE_BLOCK_INITIAL_DELAY = 1;
   private static final int PLACE_BLOCK_LAYER_INTERVAL = 3;

   private MobPlaceBlockEvent() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onLivingAttack(LivingAttackEvent livingAttackEvent) {
      if (!livingAttackEvent.isCanceled()) {
         if (livingAttackEvent.getEntity() instanceof AVNpc avNpc) {
            if (avNpc.m_9236_() instanceof ServerLevel serverLevel && avNpc.m_20096_() && CombatCommon.isGroundWithin(avNpc, 2.0) && !avNpc.m_20159_()) {
               boolean projectileDamage = livingAttackEvent.getSource().m_7640_() instanceof Projectile;
               Entity blockDamage = getBlockDamageSource(livingAttackEvent);
               if (blockDamage != null && isBlockDamageInFront(avNpc, blockDamage) && canPlaceBlockParry(avNpc, projectileDamage)) {
                  LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(avNpc, LivingEntityPatch.class);
                  if (patch instanceof MobPatch<?> mobPatch) {
                     if (!projectileDamage) {
                        avNpc.setPlaceBlockParryCooldown();
                     }

                     avNpc.setBlockDamage(blockDamage);
                     CombatCommon.swapToBlock(mobPatch);
                     int placementDelay = placeBlockWall(serverLevel, avNpc, blockDamage);
                     livingAttackEvent.setCanceled(true);
                     finishPlaceBlockParryLater(avNpc, placementDelay);
                     return;
                  }

                  return;
               }

               return;
            }
         }
      }
   }

   private static Entity getBlockDamageSource(LivingAttackEvent livingAttackEvent) {
      Entity directEntity = livingAttackEvent.getSource().m_7640_();
      if (directEntity instanceof Projectile) {
         return directEntity;
      } else if (directEntity == null) {
         return null;
      } else {
         ResourceLocation key = BuiltInRegistries.f_256780_.m_7981_(directEntity.m_6095_());
         boolean isDamageFromGunKnight = key.m_135827_().equals("torchesbecomesunlight")
            && (key.m_135815_().equals("gun_knight_patriot") || key.m_135815_().equals("turret"));
         boolean ignisFireBall = key.m_135827_().equals("cataclysm")
            && (
               key.m_135815_().equals("ignis_abyss_fireball")
                  || key.m_135815_().equals("ignis_fireball")
                  || key.m_135815_().equals("flame_jet")
                  || key.m_135815_().equals("flame_strike")
            );
         boolean isMeteorite = directEntity instanceof DragonMeteoriteEntity
            || livingAttackEvent.getSource().m_7639_() instanceof DragonMeteoriteEntity
            || directEntity instanceof ObsidianSledgehammerProjectileEntity
            || livingAttackEvent.getSource().m_7639_() instanceof ObsidianSledgehammerProjectileEntity;
         return !isDamageFromGunKnight && !ignisFireBall && !isMeteorite && !livingAttackEvent.getSource().m_276093_(DamageTypes.f_268565_)
            ? null
            : directEntity;
      }
   }

   private static boolean canPlaceBlockParry(AVNpc avNpc, boolean projectileDamage) {
      Item currentItem = avNpc.m_21120_(InteractionHand.MAIN_HAND).m_41720_();
      boolean holdingValidWeapon = currentItem.equals(avNpc.getMainWeaponItem().m_41720_()) || currentItem instanceof BowItem;
      if (!holdingValidWeapon) {
         return false;
      } else {
         return !projectileDamage
            ? avNpc.rollsPlaceBlockToParryChance()
            : avNpc.getBlockDamage() == null && !avNpc.isHealing() && new Random().nextDouble() <= avNpc.getPlaceBlockToParryChance();
      }
   }

   private static boolean isBlockDamageInFront(AVNpc avNpc, Entity blockDamage) {
      Vec3 look = horizontal(avNpc.m_20154_());
      if (look.m_82556_() < 1.0E-6) {
         Direction facing = avNpc.m_6350_();
         look = new Vec3((double)facing.m_122429_(), 0.0, (double)facing.m_122431_());
      }

      if (look.m_82556_() < 1.0E-6) {
         return false;
      } else {
         look = look.m_82541_();
         boolean threatPositionInFront = false;
         Vec3 toThreat = horizontal(blockDamage.m_20182_().m_82546_(avNpc.m_20182_()));
         if (toThreat.m_82556_() > 1.0E-6) {
            threatPositionInFront = look.m_82526_(toThreat.m_82541_()) > 0.15;
         }

         Vec3 incomingFrom = horizontal(blockDamage.m_20184_()).m_82490_(-1.0);
         boolean incomingFromFront = incomingFrom.m_82556_() > 1.0E-6 && look.m_82526_(incomingFrom.m_82541_()) > 0.15;
         return threatPositionInFront || incomingFromFront;
      }
   }

   private static Vec3 horizontal(Vec3 vector) {
      return new Vec3(vector.f_82479_, 0.0, vector.f_82481_);
   }

   private static int placeBlockWall(final ServerLevel serverLevel, final AVNpc avNpc, Entity blockDamage) {
      Random random = new Random();
      int pattern = random.nextInt(11);
      int rot = random.nextInt(4);
      final BiFunction<Integer, Integer, int[]> toWorld = getIntegerIntegerBiFunction(avNpc, rot);
      int lastPlacementDelay = 0;
      final BlockState placeState = getPlaceState(avNpc);
      ResourceLocation key = BuiltInRegistries.f_256780_.m_7981_(blockDamage.m_6095_());
      BlockPos baseXZ;
      int topY;
      if (!key.m_135827_().equals("tacz")
         && (!key.m_135827_().equals("torchesbecomesunlight") || !key.m_135815_().equals("gun_knight_patriot") && !key.m_135815_().equals("turret"))) {
         baseXZ = BlockPos.m_274561_(blockDamage.m_20185_(), 0.0, blockDamage.m_20189_());
         topY = Mth.m_14107_(blockDamage.m_20186_());
      } else {
         Direction facing = avNpc.m_6350_();
         baseXZ = avNpc.m_20183_().m_5484_(facing, 1);
         topY = Mth.m_14107_(avNpc.m_20186_() + (double)avNpc.m_20206_());
      }

      int surfaceY = serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, baseXZ).m_123342_();
      BlockPos projXZ = new BlockPos(baseXZ.m_123341_(), 0, baseXZ.m_123343_());

      for (int y = surfaceY; y <= topY; y++) {
         int layer = y - surfaceY;
         final BlockPos center = new BlockPos(projXZ.m_123341_(), y, projXZ.m_123343_());
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
         int layerDelay = 1 + layer * 3;
         lastPlacementDelay = Math.max(lastPlacementDelay, layerDelay);
         new DelayedTask(layerDelay) {
            @Override
            public void run() {
               if (avNpc.m_6084_() && CombatCommon.isGroundWithin(avNpc, 2.0)) {
                  if (MobPlaceBlockEvent.placeIfReplaceable(serverLevel, center, placeState, avNpc)) {
                     for (int[] ab : extrasLocal) {
                        int[] dzdx = toWorld.apply(ab[0], ab[1]);
                        BlockPos p = center.m_7918_(dzdx[0], 0, dzdx[1]);
                        MobPlaceBlockEvent.placeIfReplaceable(serverLevel, p, placeState, avNpc);
                     }
                  }
               }
            }
         };
      }

      return lastPlacementDelay;
   }

   private static BlockState getPlaceState(AVNpc avNpc) {
      ItemStack handStack = avNpc.m_21120_(InteractionHand.MAIN_HAND);
      return handStack.m_41720_() instanceof BlockItem blockItem ? blockItem.m_40614_().m_49966_() : Blocks.f_50652_.m_49966_();
   }

   private static boolean placeIfReplaceable(ServerLevel serverLevel, BlockPos pos, BlockState placeState, AVNpc avNpc) {
      if (!serverLevel.m_8055_(pos).m_247087_()) {
         return false;
      } else {
         avNpc.m_21011_(InteractionHand.MAIN_HAND, true);
         avNpc.m_5496_(SoundEvents.f_12447_, 2.0F, 1.0F);
         serverLevel.m_46597_(pos, placeState);
         return true;
      }
   }

   private static void finishPlaceBlockParryLater(final AVNpc avNpc, int placementDelay) {
      new DelayedTask(placementDelay + 10) {
         @Override
         public void run() {
            avNpc.setBlockDamage(null);
            if (avNpc.m_6084_()) {
               LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(avNpc, LivingEntityPatch.class);
               if (patch instanceof MobPatch<?> mobPatch) {
                  MobPlaceBlockEvent.rollAndSwapAfterPlaceBlock(mobPatch);
               }
            }
         }
      };
   }

   private static void rollAndSwapAfterPlaceBlock(MobPatch<?> mobPatch) {
      double chance = new Random().nextDouble();
      if (CombatCommon.canSwapToBow(mobPatch)) {
         if (chance <= 0.25) {
            mobPatch.playAnimationSynchronized(Animations.BIPED_KNOCKDOWN_WAKEUP_RIGHT, 0.0F);
            CombatCommon.swapToBow(mobPatch);
         } else if (chance <= 0.5) {
            mobPatch.playAnimationSynchronized(Animations.BIPED_KNOCKDOWN_WAKEUP_LEFT, 0.0F);
            CombatCommon.swapToBow(mobPatch);
         } else if (chance <= 0.7) {
            mobPatch.playAnimationSynchronized(Animations.BIPED_ROLL_BACKWARD, 0.0F);
            CombatCommon.swapToBow(mobPatch);
         } else if (chance <= 0.8) {
            mobPatch.playAnimationSynchronized(Animations.BIPED_KNOCKDOWN_WAKEUP_RIGHT, 0.0F);
            CombatCommon.swapToMelee(mobPatch);
         } else if (chance <= 0.9) {
            mobPatch.playAnimationSynchronized(Animations.BIPED_KNOCKDOWN_WAKEUP_LEFT, 0.0F);
            CombatCommon.swapToMelee(mobPatch);
         } else {
            mobPatch.playAnimationSynchronized(Animations.BIPED_ROLL_BACKWARD, 0.0F);
            CombatCommon.swapToMelee(mobPatch);
         }
      } else if (chance <= 0.4) {
         mobPatch.playAnimationSynchronized(Animations.BIPED_KNOCKDOWN_WAKEUP_RIGHT, 0.0F);
         CombatCommon.swapToMelee(mobPatch);
      } else if (chance <= 0.5) {
         mobPatch.playAnimationSynchronized(Animations.BIPED_KNOCKDOWN_WAKEUP_LEFT, 0.0F);
         CombatCommon.swapToMelee(mobPatch);
      } else {
         mobPatch.playAnimationSynchronized(Animations.BIPED_ROLL_BACKWARD, 0.0F);
         CombatCommon.swapToMelee(mobPatch);
      }
   }

   private static BiFunction<Integer, Integer, int[]> getIntegerIntegerBiFunction(Entity anchor, int rot) {
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
}
