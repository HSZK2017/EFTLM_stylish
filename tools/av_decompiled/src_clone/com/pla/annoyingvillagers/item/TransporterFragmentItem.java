package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.entity.PortalEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.network.ClientboundHerobrinePortalFx;
import com.pla.annoyingvillagers.util.HerobrinePortalUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.network.PacketDistributor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class TransporterFragmentItem extends Item {
   public static final int MAX_DURABILITY = 300;
   public static final int SAVED_TELEPORT_DURABILITY_COST = 10;
   public static final int SAVED_TELEPORT_SINK_TICKS = 40;
   public static final String NBT_SAVED_TELEPORT_PENDING = "TransporterFragmentTeleportPending";
   private static final int PORTAL_COUNT = 6;
   private static final int MAX_ACTIVE_PORTALS_PER_OWNER = 6;
   private static final int SINGLE_PORTAL_DURABILITY_COST = 1;
   private static final double LOOK_PORTAL_RANGE = 32.0;
   private static final double SAVED_TELEPORT_ENTITY_RADIUS = 5.0;
   private static final double SAVED_TELEPORT_SINK_SPEED = 0.06;
   private static final double SAVED_TELEPORT_RISE_SPEED = 0.06;
   private static final int HORIZONTAL_SEARCH_RADIUS = 30;
   private static final int VERTICAL_SEARCH_RADIUS = 15;
   private static final int TARGET_PRIORITY_RADIUS = 16;
   private static final double MIN_PORTAL_GAP = 3.0;
   private static final double MAX_PORTAL_GAP = 6.0;
   private static final double TARGET_CLUSTER_DISTANCE = 8.0;
   private static final double CASTER_PORTAL_MIN_DISTANCE = 3.0;
   private static final double CASTER_PORTAL_MAX_DISTANCE = 5.0;
   private static final double DISTRIBUTED_PORTAL_NEAR_MIN_DISTANCE = 9.0;
   private static final double DISTRIBUTED_PORTAL_NEAR_MAX_DISTANCE = 14.0;
   private static final double DISTRIBUTED_PORTAL_MID_MIN_DISTANCE = 15.0;
   private static final double DISTRIBUTED_PORTAL_MID_MAX_DISTANCE = 22.0;
   private static final double DISTRIBUTED_PORTAL_FAR_MIN_DISTANCE = 22.0;
   private static final double DISTRIBUTED_PORTAL_FAR_MAX_DISTANCE = 29.0;
   private static final int COOLDOWN_TICKS = 20;
   private static final String TAG_SAVED_LOCATION = "TransporterSavedLocation";
   private static final String TAG_DIMENSION = "Dimension";
   private static final String TAG_X = "X";
   private static final String TAG_Y = "Y";
   private static final String TAG_Z = "Z";
   private static final String TAG_TELEPORT_ORIGIN_X = "TransporterFragmentOriginX";
   private static final String TAG_TELEPORT_ORIGIN_Y = "TransporterFragmentOriginY";
   private static final String TAG_TELEPORT_ORIGIN_Z = "TransporterFragmentOriginZ";
   private static final String TAG_TELEPORT_TARGET_X = "TransporterFragmentTargetX";
   private static final String TAG_TELEPORT_TARGET_Y = "TransporterFragmentTargetY";
   private static final String TAG_TELEPORT_TARGET_Z = "TransporterFragmentTargetZ";
   private static final String TAG_TELEPORT_ENTITIES = "TransporterFragmentEntities";
   private static final String TAG_ENTITY_COUNT = "Count";
   private static final String TAG_ENTITY_UUID = "UUID";
   private static final String TAG_ENTITY_DX = "DX";
   private static final String TAG_ENTITY_DY = "DY";
   private static final String TAG_ENTITY_DZ = "DZ";

   public TransporterFragmentItem() {
      super(new Properties().m_41487_(1).m_41503_(300).m_41486_().m_41497_(Rarity.EPIC));
   }

   public boolean m_8120_(ItemStack stack) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return false;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return false;
   }

   public int m_6473_() {
      return 0;
   }

   public static TransporterFragmentItem.UseResult tryUseSpecialAttack(Player player) {
      return tryUseSpecialAttack(player, null);
   }

   public static TransporterFragmentItem.UseResult tryUseSpecialAttack(Player player, Vec3 crosshairTarget) {
      Item transporterFragment = (Item)AnnoyingVillagersModItems.TRANSPORTER_FRAGMENT.get();
      TransporterFragmentItem.UseMode mode = getUseMode(player, transporterFragment);
      if (mode == TransporterFragmentItem.UseMode.NONE) {
         return TransporterFragmentItem.UseResult.missed();
      } else if (player.m_36335_().m_41519_(transporterFragment)) {
         return TransporterFragmentItem.UseResult.consumed(mode, false);
      } else {
         ItemStack stack = getStackForMode(player, mode);
         int requestedPortals = isSixPortalMode(mode) ? 6 : 1;
         if (!hasDurability(stack, requestedPortals)) {
            return TransporterFragmentItem.UseResult.consumed(mode, false);
         } else {
            boolean activated = false;
            if (player.m_9236_() instanceof ServerLevel serverLevel) {
               List<PortalEntity> activePortals = findOwnedActivePortals(serverLevel, player);
               if (activePortals.size() + requestedPortals > 6) {
                  return TransporterFragmentItem.UseResult.consumed(mode, false);
               }

               int spawned = isSixPortalMode(mode)
                  ? spawnPortalPairs(serverLevel, player)
                  : spawnLookPortal(serverLevel, player, activePortals, crosshairTarget);
               if (spawned > 0) {
                  damageStack(player, stack, mode == TransporterFragmentItem.UseMode.OFF_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, spawned);
                  player.m_36335_().m_41524_(transporterFragment, 20);
                  activated = true;
               }
            }

            return TransporterFragmentItem.UseResult.consumed(mode, activated);
         }
      }
   }

   public static TransporterFragmentItem.UseResult tryUseHeldSpecialAttack(Player player) {
      Item transporterFragment = (Item)AnnoyingVillagersModItems.TRANSPORTER_FRAGMENT.get();
      ItemStack stack = player.m_21205_();
      if (!stack.m_150930_(transporterFragment)) {
         return TransporterFragmentItem.UseResult.missed();
      } else {
         TransporterFragmentItem.UseMode mode = TransporterFragmentItem.UseMode.MAIN_HAND;
         if (!player.m_36335_().m_41519_(transporterFragment)
            && !player.getPersistentData().m_128471_("TransporterFragmentTeleportPending")
            && !player.getPersistentData().m_128471_("rising")
            && !player.getPersistentData().m_128471_("sinking")
            && hasSavedLocation(stack)
            && hasDurability(stack, 10)) {
            if (player.m_9236_() instanceof ServerLevel serverLevel) {
               CompoundTag savedLocation = stack.m_41783_().m_128469_("TransporterSavedLocation");
               String savedDimension = savedLocation.m_128461_("Dimension");
               if (!savedDimension.equals(serverLevel.m_46472_().m_135782_().toString())) {
                  return TransporterFragmentItem.UseResult.consumed(mode, false);
               } else {
                  Vec3 target = new Vec3(savedLocation.m_128459_("X"), savedLocation.m_128459_("Y"), savedLocation.m_128459_("Z"));
                  if (!serverLevel.m_6857_().m_61937_(BlockPos.m_274446_(target))) {
                     return TransporterFragmentItem.UseResult.consumed(mode, false);
                  } else {
                     beginSavedTeleport(serverLevel, player, target, null);
                     damageStack(player, stack, InteractionHand.MAIN_HAND, 10);
                     player.m_36335_().m_41524_(transporterFragment, 20);
                     return TransporterFragmentItem.UseResult.consumed(mode, true);
                  }
               }
            } else {
               return TransporterFragmentItem.UseResult.consumed(mode, false);
            }
         } else {
            return TransporterFragmentItem.UseResult.consumed(mode, false);
         }
      }
   }

   public InteractionResult m_6225_(UseOnContext context) {
      Player player = context.m_43723_();
      ItemStack stack = context.m_43722_();
      if (player != null && context.m_43724_() == InteractionHand.MAIN_HAND && stack.m_150930_((Item)AnnoyingVillagersModItems.TRANSPORTER_FRAGMENT.get())) {
         if (!context.m_43725_().m_5776_()) {
            if (player.m_6144_()) {
               clearSavedLocation(stack, player);
            } else {
               saveLocation(stack, context.m_43725_(), Vec3.m_82539_(context.m_8083_().m_121945_(context.m_43719_())), player);
            }
         }

         return InteractionResult.m_19078_(context.m_43725_().m_5776_());
      } else {
         return super.m_6225_(context);
      }
   }

   public InteractionResult m_6880_(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
      if (hand == InteractionHand.MAIN_HAND && stack.m_150930_((Item)AnnoyingVillagersModItems.TRANSPORTER_FRAGMENT.get())) {
         if (!player.m_9236_().m_5776_()) {
            if (player.m_6144_()) {
               clearSavedLocation(stack, player);
            } else {
               saveLocation(stack, player.m_9236_(), target.m_20182_(), player);
            }
         }

         return InteractionResult.m_19078_(player.m_9236_().m_5776_());
      } else {
         return super.m_6880_(stack, player, target, hand);
      }
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      if (hand == InteractionHand.MAIN_HAND && stack.m_150930_((Item)AnnoyingVillagersModItems.TRANSPORTER_FRAGMENT.get())) {
         if (!level.m_5776_()) {
            if (player.m_6144_()) {
               clearSavedLocation(stack, player);
            } else if (level instanceof ServerLevel serverLevel) {
               TransporterFragmentItem.LookPortalTarget target = findLookPortalTarget(serverLevel, player);
               saveLocation(stack, level, snapPortalPosition(target.portalPos), player);
            }
         }

         return InteractionResultHolder.m_19092_(stack, level.m_5776_());
      } else {
         return super.m_7203_(level, player, hand);
      }
   }

   public void m_7373_(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
      super.m_7373_(stack, level, tooltip, flag);
      tooltip.add(Component.m_237115_("tooltip.annoyingvillagers.transporter_fragment"));
      if (!hasSavedLocation(stack)) {
         tooltip.add(Component.m_237113_("Saved Location: none").m_130940_(ChatFormatting.DARK_GRAY));
      } else {
         CompoundTag savedLocation = stack.m_41783_().m_128469_("TransporterSavedLocation");
         tooltip.add(Component.m_237113_("Saved Location").m_130940_(ChatFormatting.AQUA));
         tooltip.add(
            Component.m_237113_(
                  "Saved pos: "
                     + Mth.m_14107_(savedLocation.m_128459_("X"))
                     + " "
                     + Mth.m_14107_(savedLocation.m_128459_("Y"))
                     + " "
                     + Mth.m_14107_(savedLocation.m_128459_("Z"))
               )
               .m_130940_(ChatFormatting.GRAY)
         );
         tooltip.add(Component.m_237113_(savedLocation.m_128461_("Dimension")).m_130940_(ChatFormatting.DARK_GRAY));
      }
   }

   private static TransporterFragmentItem.UseMode getUseMode(Player player, Item transporterFragment) {
      boolean hasMainHandFragment = player.m_21205_().m_150930_(transporterFragment);
      boolean hasOffHandFragment = player.m_21206_().m_150930_(transporterFragment);
      if (hasMainHandFragment && hasOffHandFragment) {
         return TransporterFragmentItem.UseMode.BOTH_HANDS;
      } else if (hasMainHandFragment) {
         return TransporterFragmentItem.UseMode.MAIN_HAND;
      } else {
         return hasOffHandFragment ? TransporterFragmentItem.UseMode.OFF_HAND : TransporterFragmentItem.UseMode.NONE;
      }
   }

   private static boolean isSixPortalMode(TransporterFragmentItem.UseMode mode) {
      return mode == TransporterFragmentItem.UseMode.MAIN_HAND || mode == TransporterFragmentItem.UseMode.BOTH_HANDS;
   }

   private static ItemStack getStackForMode(Player player, TransporterFragmentItem.UseMode mode) {
      return mode == TransporterFragmentItem.UseMode.OFF_HAND ? player.m_21206_() : player.m_21205_();
   }

   private static boolean hasDurability(ItemStack stack, int cost) {
      return !stack.m_41619_() && stack.m_41776_() - stack.m_41773_() >= cost;
   }

   private static void damageStack(Player player, ItemStack stack, InteractionHand hand, int damage) {
      if (damage > 0) {
         stack.m_41622_(damage, player, brokenPlayer -> brokenPlayer.m_21190_(hand));
      }
   }

   private static boolean hasSavedLocation(ItemStack stack) {
      return stack.m_41782_() && stack.m_41783_().m_128441_("TransporterSavedLocation");
   }

   private static void saveLocation(ItemStack stack, Level level, Vec3 pos, Player player) {
      CompoundTag savedLocation = new CompoundTag();
      savedLocation.m_128347_("X", pos.f_82479_);
      savedLocation.m_128347_("Y", pos.f_82480_);
      savedLocation.m_128347_("Z", pos.f_82481_);
      savedLocation.m_128359_("Dimension", level.m_46472_().m_135782_().toString());
      stack.m_41784_().m_128365_("TransporterSavedLocation", savedLocation);
      player.m_5661_(
         Component.m_237113_("Saved Location: " + Mth.m_14107_(pos.f_82479_) + " " + Mth.m_14107_(pos.f_82480_) + " " + Mth.m_14107_(pos.f_82481_))
            .m_130940_(ChatFormatting.AQUA),
         true
      );
   }

   private static void clearSavedLocation(ItemStack stack, Player player) {
      if (stack.m_41782_()) {
         stack.m_41783_().m_128473_("TransporterSavedLocation");
      }

      player.m_5661_(Component.m_237113_("Saved Location cleared").m_130940_(ChatFormatting.GRAY), true);
   }

   private static void beginSavedTeleport(ServerLevel level, Player player, Vec3 target, LivingEntityPatch<?> livingEntityPatch) {
      Vec3 origin = player.m_20182_();
      List<Entity> teleportEntities = collectTeleportEntities(level, player);
      CompoundTag tag = player.getPersistentData();
      tag.m_128379_("TransporterFragmentTeleportPending", true);
      tag.m_128347_("TransporterFragmentOriginX", origin.f_82479_);
      tag.m_128347_("TransporterFragmentOriginY", origin.f_82480_);
      tag.m_128347_("TransporterFragmentOriginZ", origin.f_82481_);
      tag.m_128347_("TransporterFragmentTargetX", target.f_82479_);
      tag.m_128347_("TransporterFragmentTargetY", target.f_82480_);
      tag.m_128347_("TransporterFragmentTargetZ", target.f_82481_);
      tag.m_128365_("TransporterFragmentEntities", buildTeleportEntityTag(teleportEntities, origin));
      sendGroundPortalFx(player, origin);
      level.m_5594_(null, player.m_20183_(), (SoundEvent)AnnoyingVillagersModSounds.PORTAL_NATURAL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

      for (Entity entity : teleportEntities) {
         if (entity instanceof LivingEntity livingEntity) {
            HerobrinePortalUtil.sinkIntoGround(level, livingEntity, 0.06);
         }
      }
   }

   private static List<Entity> collectTeleportEntities(ServerLevel level, Player player) {
      List<Entity> entities = new ArrayList<>();
      entities.add(player);

      for (Entity entity : level.m_6249_(player, player.m_20191_().m_82400_(5.0), entityx -> entityx.m_6084_() && !entityx.m_5833_())) {
         entities.add(entity);
      }

      return entities;
   }

   private static CompoundTag buildTeleportEntityTag(List<Entity> entities, Vec3 origin) {
      CompoundTag entitiesTag = new CompoundTag();
      int count = 0;

      for (Entity entity : entities) {
         count = addTeleportEntity(entitiesTag, count, entity, origin);
      }

      entitiesTag.m_128405_("Count", count);
      return entitiesTag;
   }

   private static int addTeleportEntity(CompoundTag entitiesTag, int index, Entity entity, Vec3 origin) {
      CompoundTag entityTag = new CompoundTag();
      Vec3 offset = entity.m_20182_().m_82546_(origin);
      entityTag.m_128362_("UUID", entity.m_20148_());
      entityTag.m_128347_("DX", offset.f_82479_);
      entityTag.m_128347_("DY", offset.f_82480_);
      entityTag.m_128347_("DZ", offset.f_82481_);
      entitiesTag.m_128365_(String.valueOf(index), entityTag);
      return index + 1;
   }

   public static void finishPendingSavedTeleport(LivingEntity caster) {
      if (caster.m_9236_() instanceof ServerLevel level) {
         CompoundTag tag = caster.getPersistentData();
         if (tag.m_128471_("TransporterFragmentTeleportPending")) {
            Vec3 target = new Vec3(
               tag.m_128459_("TransporterFragmentTargetX"), tag.m_128459_("TransporterFragmentTargetY"), tag.m_128459_("TransporterFragmentTargetZ")
            );
            CompoundTag entitiesTag = tag.m_128469_("TransporterFragmentEntities");
            int count = entitiesTag.m_128451_("Count");
            sendGroundPortalFx(caster, target);
            level.m_5594_(null, BlockPos.m_274446_(target), (SoundEvent)AnnoyingVillagersModSounds.PORTAL_NATURAL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

            for (int i = 0; i < count; i++) {
               CompoundTag entityTag = entitiesTag.m_128469_(String.valueOf(i));
               if (entityTag.m_128403_("UUID")) {
                  Entity entity = level.m_8791_(entityTag.m_128342_("UUID"));
                  if (entity != null && !entity.m_213877_()) {
                     Vec3 destination = target.m_82520_(entityTag.m_128459_("DX"), entityTag.m_128459_("DY"), entityTag.m_128459_("DZ"));
                     teleportEntityWithRise(level, entity, destination);
                  }
               }
            }

            clearSavedTeleportState(tag);
         }
      }
   }

   private static void teleportEntityWithRise(ServerLevel level, Entity entity, Vec3 destination) {
      entity.m_20256_(Vec3.f_82478_);
      if (entity instanceof ServerPlayer serverPlayer) {
         serverPlayer.m_6021_(destination.f_82479_, destination.f_82480_, destination.f_82481_);
      } else {
         entity.m_6021_(destination.f_82479_, destination.f_82480_, destination.f_82481_);
      }

      if (entity instanceof LivingEntity livingEntity) {
         clearSinkState(livingEntity);
         HerobrinePortalUtil.spawnRising(level, livingEntity, destination.f_82479_, destination.f_82481_, 0.06);
      }
   }

   private static void clearSavedTeleportState(CompoundTag tag) {
      tag.m_128473_("TransporterFragmentTeleportPending");
      tag.m_128473_("TransporterFragmentOriginX");
      tag.m_128473_("TransporterFragmentOriginY");
      tag.m_128473_("TransporterFragmentOriginZ");
      tag.m_128473_("TransporterFragmentTargetX");
      tag.m_128473_("TransporterFragmentTargetY");
      tag.m_128473_("TransporterFragmentTargetZ");
      tag.m_128473_("TransporterFragmentEntities");
   }

   private static void clearSinkState(LivingEntity entity) {
      CompoundTag tag = entity.getPersistentData();
      tag.m_128473_("sinking");
      tag.m_128473_("sink_target_y");
      tag.m_128473_("sink_speed");
      tag.m_128473_("sink_ticks");
      tag.m_128473_("sink_max_ticks");
   }

   private static void sendGroundPortalFx(Entity trackedEntity, Vec3 pos) {
      AnnoyingVillagers.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> trackedEntity), new ClientboundHerobrinePortalFx(pos));
   }

   public static TransporterFragmentItem.PortalSpawnBatch spawnPortalPairsBatch(Level level, LivingEntity caster) {
      return spawnPortalPairsBatch(level, caster, caster);
   }

   public static TransporterFragmentItem.PortalSpawnBatch spawnPortalPairsBatch(Level level, LivingEntity caster, LivingEntity placementAnchor) {
      if (level instanceof ServerLevel serverLevel && findOwnedActivePortals(serverLevel, caster).size() + 6 > 6) {
         return new TransporterFragmentItem.PortalSpawnBatch(null, 0);
      }

      RandomSource random = level.m_213780_();
      List<LivingEntity> priorityTargets = clusterPriorityTargets(findPriorityTargets(level, placementAnchor));
      List<Vec3> portalPositions = buildPortalPositions(level, caster, placementAnchor, priorityTargets, 6, random);
      UUID portalGroup = UUID.randomUUID();
      int spawned = 0;

      for (int order = 0; order < portalPositions.size(); order += 2) {
         if (order + 1 < portalPositions.size() && spawnPair(level, caster, portalGroup, order, portalPositions.get(order), portalPositions.get(order + 1))) {
            spawned += 2;
         } else if (order == portalPositions.size() - 1
            && spawnSinglePortal(
               level, caster, portalGroup, order, portalPositions.get(order), yawFacing(portalPositions.get(order), caster.m_146892_()), order == 0
            )) {
            spawned++;
         }
      }

      return new TransporterFragmentItem.PortalSpawnBatch(spawned > 0 ? portalGroup : null, spawned);
   }

   public static int spawnPortalPairs(Level level, LivingEntity caster) {
      return spawnPortalPairsBatch(level, caster).spawned();
   }

   public static int spawnLinkedPortalPair(Level level, LivingEntity caster, Vec3 firstPreferredPos, Vec3 secondPreferredPos) {
      if (level instanceof ServerLevel serverLevel) {
         List<PortalEntity> activePortals = findOwnedActivePortals(serverLevel, caster);
         if (activePortals.size() + 2 > 6) {
            return 0;
         } else {
            Vec3 firstPortalPos = findLookPortalPosition(level, firstPreferredPos);
            Vec3 secondPortalPos = findLookPortalPosition(level, secondPreferredPos);
            if (firstPortalPos != null && secondPortalPos != null) {
               UUID portalGroup = selectPortalGroup(activePortals, null);
               int portalOrder = nextPortalOrder(activePortals);
               return spawnPair(level, caster, portalGroup, portalOrder, firstPortalPos, secondPortalPos) ? 2 : 0;
            } else {
               return 0;
            }
         }
      } else {
         return 0;
      }
   }

   public static boolean canSpawnOwnedPortals(ServerLevel level, LivingEntity caster, int portalCount) {
      return findOwnedActivePortals(level, caster).size() + portalCount <= 6;
   }

   private static int spawnLookPortal(ServerLevel level, Player caster, List<PortalEntity> activePortals) {
      return spawnLookPortal(level, caster, activePortals, null);
   }

   private static int spawnLookPortal(ServerLevel level, Player caster, List<PortalEntity> activePortals, Vec3 crosshairTarget) {
      TransporterFragmentItem.LookPortalTarget target = findLookPortalTarget(level, caster, crosshairTarget);
      Vec3 portalPos = findLookPortalPosition(level, target.portalPos);
      if (portalPos == null) {
         return 0;
      } else {
         PortalEntity pendingPortal = findPendingPortal(activePortals);
         UUID portalGroup = selectPortalGroup(activePortals, pendingPortal);
         int portalOrder = nextPortalOrder(activePortals);
         float yaw = yawFacing(portalPos, target.facingTarget);
         PortalEntity portal = createPortal(level, caster, portalGroup, portalOrder, portalPos, yaw, portalOrder == 0);
         if (portal == null) {
            return 0;
         } else {
            if (pendingPortal != null) {
               linkPortalPair(pendingPortal, portal, portalGroup);
            }

            return 1;
         }
      }
   }

   private static TransporterFragmentItem.LookPortalTarget findLookPortalTarget(ServerLevel level, Player caster) {
      return findLookPortalTarget(level, caster, null);
   }

   private static TransporterFragmentItem.LookPortalTarget findLookPortalTarget(ServerLevel level, Player caster, Vec3 crosshairTarget) {
      Vec3 eyePos = caster.m_20299_(1.0F);
      Vec3 maxPos = resolveLookEnd(eyePos, caster, crosshairTarget);
      BlockHitResult blockHit = level.m_45547_(new ClipContext(eyePos, maxPos, Block.COLLIDER, Fluid.NONE, caster));
      double blockDistanceSqr = blockHit.m_6662_() == Type.BLOCK ? eyePos.m_82557_(blockHit.m_82450_()) : 1024.0;
      TransporterFragmentItem.LookEntityHit entityHit = findLookEntity(level, caster, eyePos, maxPos);
      if (entityHit != null && entityHit.distanceSqr <= blockDistanceSqr) {
         return new TransporterFragmentItem.LookPortalTarget(
            new Vec3(entityHit.entity.m_20185_(), entityHit.entity.m_20186_(), entityHit.entity.m_20189_()), getEntityCenter(entityHit.entity)
         );
      } else if (blockHit.m_6662_() == Type.BLOCK) {
         BlockPos spawnBlock = blockHit.m_82425_().m_121945_(blockHit.m_82434_());
         return new TransporterFragmentItem.LookPortalTarget(Vec3.m_82539_(spawnBlock), eyePos);
      } else {
         return new TransporterFragmentItem.LookPortalTarget(maxPos, eyePos);
      }
   }

   private static Vec3 resolveLookEnd(Vec3 eyePos, Player caster, Vec3 crosshairTarget) {
      if (crosshairTarget != null) {
         Vec3 offset = crosshairTarget.m_82546_(eyePos);
         double distanceSqr = offset.m_82556_();
         if (distanceSqr > 1.0E-6) {
            double distance = Math.sqrt(distanceSqr);
            double clampedDistance = Math.min(distance, 32.0);
            return eyePos.m_82549_(offset.m_82490_(clampedDistance / distance));
         }
      }

      return eyePos.m_82549_(caster.m_20154_().m_82490_(32.0));
   }

   private static TransporterFragmentItem.LookEntityHit findLookEntity(ServerLevel level, Player caster, Vec3 start, Vec3 end) {
      AABB searchBox = caster.m_20191_().m_82369_(end.m_82546_(start)).m_82400_(1.0);
      Entity closestEntity = null;
      double closestDistanceSqr = 1024.0;

      for (Entity entity : level.m_6249_(caster, searchBox, TransporterFragmentItem::canLookTargetEntity)) {
         AABB targetBox = entity.m_20191_().m_82400_(Math.max(0.3, (double)entity.m_6143_()));
         Optional<Vec3> clip = targetBox.m_82371_(start, end);
         Vec3 hitPos = null;
         if (targetBox.m_82390_(start)) {
            hitPos = start;
         } else if (clip.isPresent()) {
            hitPos = clip.get();
         }

         if (hitPos != null) {
            double distanceSqr = start.m_82557_(hitPos);
            if (distanceSqr < closestDistanceSqr) {
               closestEntity = entity;
               closestDistanceSqr = distanceSqr;
            }
         }
      }

      return closestEntity == null ? null : new TransporterFragmentItem.LookEntityHit(closestEntity, closestDistanceSqr);
   }

   private static boolean canLookTargetEntity(Entity entity) {
      return entity.m_6084_() && !entity.m_5833_() && entity.m_6087_();
   }

   private static Vec3 findLookPortalPosition(Level level, Vec3 preferredPos) {
      Vec3 base = snapPortalPosition(preferredPos);
      if (isLookPortalPositionValid(level, base)) {
         return base;
      } else {
         for (int radius = 1; radius <= 3; radius++) {
            for (int dy = -1; dy <= 2; dy++) {
               for (int dx = -radius; dx <= radius; dx++) {
                  for (int dz = -radius; dz <= radius; dz++) {
                     if (Math.abs(dx) == radius || Math.abs(dz) == radius) {
                        Vec3 candidate = base.m_82520_((double)dx, (double)dy, (double)dz);
                        if (isLookPortalPositionValid(level, candidate)) {
                           return candidate;
                        }
                     }
                  }
               }
            }
         }

         return null;
      }
   }

   private static Vec3 snapPortalPosition(Vec3 pos) {
      return new Vec3(Math.floor(pos.f_82479_) + 0.5, Math.floor(pos.f_82480_), Math.floor(pos.f_82481_) + 0.5);
   }

   private static List<LivingEntity> findPriorityTargets(Level level, LivingEntity attacker) {
      List<LivingEntity> targets = new ArrayList<>();

      for (Entity entity : level.m_45976_(LivingEntity.class, attacker.m_20191_().m_82400_(16.0))) {
         if (!entity.equals(attacker) && !attacker.m_7307_(entity) && !entity.m_7307_(attacker) && !entity.m_5833_()) {
            if (entity instanceof Player) {
               Player player = (Player)entity;
               if (player.m_7500_()) {
                  continue;
               }
            }

            if ((entity instanceof Mob || entity instanceof Player) && attacker.m_142582_(entity)) {
               targets.add((LivingEntity)entity);
            }
         }
      }

      targets.sort(Comparator.comparingDouble(attacker::m_20270_));
      return targets;
   }

   private static List<LivingEntity> clusterPriorityTargets(List<LivingEntity> targets) {
      List<LivingEntity> clusteredTargets = new ArrayList<>();

      for (LivingEntity target : targets) {
         boolean joinedExistingCluster = false;

         for (LivingEntity clusteredTarget : clusteredTargets) {
            if ((double)target.m_20270_(clusteredTarget) <= 8.0) {
               joinedExistingCluster = true;
               break;
            }
         }

         if (!joinedExistingCluster) {
            clusteredTargets.add(target);
         }
      }

      return clusteredTargets;
   }

   private static List<Vec3> buildPortalPositions(
      Level level, LivingEntity owner, LivingEntity placementAnchor, List<LivingEntity> priorityTargets, int portalCount, RandomSource random
   ) {
      List<Vec3> positions = new ArrayList<>();
      Vec3 casterPortal = findCasterPortalPosition(level, owner, placementAnchor, positions, random);
      if (casterPortal == null) {
         return positions;
      } else {
         positions.add(casterPortal);
         int targetIndex = 0;

         while (positions.size() < portalCount) {
            Vec3 candidate = null;
            boolean exitSlot = positions.size() % 2 == 1;
            if (exitSlot && targetIndex < priorityTargets.size()) {
               candidate = findPortalNearTarget(level, owner, placementAnchor, priorityTargets.get(targetIndex), positions, random);
               targetIndex++;
            }

            if (candidate == null) {
               candidate = findRandomDistributedPortal(level, owner, placementAnchor, positions, random, positions.size());
            }

            if (candidate == null) {
               break;
            }

            positions.add(candidate);
         }

         return positions;
      }
   }

   private static Vec3 findCasterPortalPosition(Level level, LivingEntity owner, LivingEntity placementAnchor, List<Vec3> usedPositions, RandomSource random) {
      for (int attempt = 0; attempt < 80; attempt++) {
         double angle = (double)(placementAnchor.m_146908_() * (float) (Math.PI / 180.0))
            + (attempt < 8 ? (Math.PI / 4) * (double)attempt : random.m_188500_() * Math.PI * 2.0);
         double distance = 3.0 + random.m_188500_() * 2.0;
         double y = Math.floor(placementAnchor.m_20186_()) + (double)(attempt > 30 ? random.m_188503_(16) : random.m_188503_(4));
         Vec3 candidate = new Vec3(
            placementAnchor.m_20185_() - Math.sin(angle) * distance,
            Mth.m_14008_(y, Math.floor(placementAnchor.m_20186_()), Math.floor(placementAnchor.m_20186_()) + 15.0),
            placementAnchor.m_20189_() + Math.cos(angle) * distance
         );
         if (isValidPortalPosition(level, placementAnchor, candidate, usedPositions)) {
            return candidate;
         }
      }

      return findRandomDistributedPortal(level, owner, placementAnchor, usedPositions, random, 0);
   }

   private static Vec3 findPortalNearTarget(
      Level level, LivingEntity owner, LivingEntity placementAnchor, LivingEntity target, List<Vec3> usedPositions, RandomSource random
   ) {
      for (int attempt = 0; attempt < 32; attempt++) {
         Vec3 candidate = randomPositionNearEntity(placementAnchor, target, random);
         if (isValidPortalPosition(level, placementAnchor, candidate, usedPositions)) {
            return candidate;
         }
      }

      return null;
   }

   private static Vec3 findRandomDistributedPortal(
      Level level, LivingEntity owner, LivingEntity placementAnchor, List<Vec3> usedPositions, RandomSource random, int slotIndex
   ) {
      for (int attempt = 0; attempt < 140; attempt++) {
         Vec3 candidate = randomDistributedPositionAroundCaster(placementAnchor, usedPositions, random, slotIndex, attempt);
         if (isValidPortalPosition(level, placementAnchor, candidate, usedPositions)) {
            return candidate;
         }
      }

      for (int attemptx = 0; attemptx < 120; attemptx++) {
         Vec3 candidate = randomPositionAroundCaster(placementAnchor, random);
         if (isValidPortalPosition(level, placementAnchor, candidate, usedPositions)) {
            return candidate;
         }
      }

      return null;
   }

   private static Vec3 randomDistributedPositionAroundCaster(LivingEntity caster, List<Vec3> usedPositions, RandomSource random, int slotIndex, int attempt) {
      double angle = preferredSpreadAngle(caster, usedPositions, random, attempt);
      double angleJitter = attempt < 60 ? 0.35 : 0.95;
      angle += (random.m_188500_() - 0.5) * angleJitter;
      int distanceTier = slotIndex <= 1 ? 0 : (slotIndex + attempt) % 3;
      double distance;
      if (attempt > 95) {
         distance = 8.0 + random.m_188500_() * 22.0;
      } else if (distanceTier == 0) {
         distance = 9.0 + random.m_188500_() * 5.0;
      } else if (distanceTier == 1) {
         distance = 15.0 + random.m_188500_() * 7.0;
      } else {
         distance = 22.0 + random.m_188500_() * 7.0;
      }

      double y = Math.floor(caster.m_20186_()) + (double)random.m_188503_(16);
      return new Vec3(
         caster.m_20185_() + Math.cos(angle) * distance,
         Mth.m_14008_(y, Math.floor(caster.m_20186_()), Math.floor(caster.m_20186_()) + 15.0),
         caster.m_20189_() + Math.sin(angle) * distance
      );
   }

   private static double preferredSpreadAngle(LivingEntity caster, List<Vec3> usedPositions, RandomSource random, int attempt) {
      if (usedPositions.isEmpty()) {
         return random.m_188500_() * Math.PI * 2.0;
      } else {
         List<Double> angles = new ArrayList<>(usedPositions.size());

         for (Vec3 used : usedPositions) {
            double angle = Math.atan2(used.f_82481_ - caster.m_20189_(), used.f_82479_ - caster.m_20185_());
            if (angle < 0.0) {
               angle += Math.PI * 2;
            }

            angles.add(angle);
         }

         angles.sort(Double::compareTo);
         double bestStart = angles.get(0);
         double bestGap = -1.0;

         for (int index = 0; index < angles.size(); index++) {
            double start = angles.get(index);
            double end = index == angles.size() - 1 ? angles.get(0) + (Math.PI * 2) : angles.get(index + 1);
            double gap = end - start;
            if (gap > bestGap) {
               bestGap = gap;
               bestStart = start;
            }
         }

         double midpoint = bestStart + bestGap * 0.5;
         if (attempt > 40) {
            midpoint += (double)(attempt % 6) * (Math.PI / 12);
         }

         midpoint %= Math.PI * 2;
         return midpoint < 0.0 ? midpoint + (Math.PI * 2) : midpoint;
      }
   }

   private static Vec3 randomPositionNearEntity(LivingEntity caster, LivingEntity target, RandomSource random) {
      double angle = random.m_188500_() * Math.PI * 2.0;
      double distance = 3.0 + random.m_188500_() * 3.0;
      double y = Math.max(Math.floor(caster.m_20186_()), Math.floor(target.m_20186_()));
      return new Vec3(
         target.m_20185_() + Math.cos(angle) * distance,
         Mth.m_14008_(y, Math.floor(caster.m_20186_()), Math.floor(caster.m_20186_()) + 15.0),
         target.m_20189_() + Math.sin(angle) * distance
      );
   }

   private static Vec3 randomPositionAroundCaster(LivingEntity caster, RandomSource random) {
      return new Vec3(
         caster.m_20185_() + (double)random.m_188503_(61) - 30.0,
         Math.floor(caster.m_20186_()) + (double)random.m_188503_(16),
         caster.m_20189_() + (double)random.m_188503_(61) - 30.0
      );
   }

   private static boolean isValidPortalPosition(Level level, LivingEntity caster, Vec3 pos, List<Vec3> usedPositions) {
      if (pos.f_82480_ < Math.floor(caster.m_20186_()) || pos.f_82480_ > Math.floor(caster.m_20186_()) + 15.0) {
         return false;
      } else if (Math.abs(pos.f_82479_ - caster.m_20185_()) > 30.0 || Math.abs(pos.f_82481_ - caster.m_20189_()) > 30.0) {
         return false;
      } else if (!level.m_6857_().m_61937_(BlockPos.m_274446_(pos))) {
         return false;
      } else {
         return !isFarEnoughFromExisting(pos, usedPositions) ? false : isAreaClear(level, pos);
      }
   }

   private static boolean isLookPortalPositionValid(Level level, Vec3 pos) {
      return level.m_6857_().m_61937_(BlockPos.m_274446_(pos)) && isAreaClear(level, pos);
   }

   private static boolean isFarEnoughFromExisting(Vec3 pos, List<Vec3> usedPositions) {
      for (Vec3 used : usedPositions) {
         if (used.m_82554_(pos) < 3.0) {
            return false;
         }
      }

      return true;
   }

   private static boolean isAreaClear(Level level, Vec3 pos) {
      if (!(pos.f_82480_ < (double)level.m_141937_()) && !(pos.f_82480_ + 3.0 + 1.0 >= (double)level.m_151558_())) {
         AABB portalBox = new AABB(pos.f_82479_ - 1.1F, pos.f_82480_, pos.f_82481_ - 1.1F, pos.f_82479_ + 1.1F, pos.f_82480_ + 3.0, pos.f_82481_ + 1.1F);
         if (!level.m_45772_(portalBox)) {
            return false;
         } else {
            BlockPos min = BlockPos.m_274561_(pos.f_82479_ - 2.0, pos.f_82480_, pos.f_82481_ - 2.0);
            BlockPos max = BlockPos.m_274561_(pos.f_82479_ + 2.0, pos.f_82480_ + 3.0, pos.f_82481_ + 2.0);

            for (BlockPos checkPos : BlockPos.m_121940_(min, max)) {
               BlockState state = level.m_8055_(checkPos);
               if (!state.m_60795_() || !level.m_6425_(checkPos).m_76178_()) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static boolean spawnSinglePortal(Level level, LivingEntity caster, UUID portalGroup, int order, Vec3 pos, float yaw, boolean starterPortal) {
      return createPortal(level, caster, portalGroup, order, pos, yaw, starterPortal) != null;
   }

   private static PortalEntity createPortal(Level level, LivingEntity caster, UUID portalGroup, int order, Vec3 pos, float yaw, boolean starterPortal) {
      PortalEntity portal = (PortalEntity)((EntityType)AnnoyingVillagersModEntities.PORTAL.get()).m_20615_(level);
      if (portal == null) {
         return null;
      } else {
         portal.setOwnerUUID(caster.m_20148_());
         portal.setPortalGroupUUID(portalGroup);
         portal.setPortalOrder(order);
         portal.setStarterPortal(starterPortal);
         placePortal(portal, pos, yaw);
         level.m_7967_(portal);
         return portal;
      }
   }

   private static boolean spawnPair(Level level, LivingEntity caster, UUID portalGroup, int firstOrder, Vec3 firstPos, Vec3 secondPos) {
      PortalEntity first = (PortalEntity)((EntityType)AnnoyingVillagersModEntities.PORTAL.get()).m_20615_(level);
      PortalEntity second = (PortalEntity)((EntityType)AnnoyingVillagersModEntities.PORTAL.get()).m_20615_(level);
      if (first != null && second != null) {
         UUID owner = caster.m_20148_();
         float firstYaw = yawFacing(firstPos, secondPos);
         float secondYaw = yawFacing(secondPos, firstPos);
         first.setOwnerUUID(owner);
         second.setOwnerUUID(owner);
         first.setLinkedPortalUUID(second.m_20148_());
         second.setLinkedPortalUUID(first.m_20148_());
         first.setPortalGroupUUID(portalGroup);
         second.setPortalGroupUUID(portalGroup);
         first.setPortalOrder(firstOrder);
         second.setPortalOrder(firstOrder + 1);
         first.setStarterPortal(firstOrder == 0);
         second.setStarterPortal(false);
         placePortal(first, firstPos, firstYaw);
         placePortal(second, secondPos, secondYaw);
         level.m_7967_(first);
         level.m_7967_(second);
         return true;
      } else {
         return false;
      }
   }

   private static void placePortal(PortalEntity portal, Vec3 pos, float yaw) {
      portal.m_6034_(pos.f_82479_, pos.f_82480_, pos.f_82481_);
      setPortalYaw(portal, yaw);
   }

   private static void setPortalYaw(PortalEntity portal, float yaw) {
      portal.m_146922_(yaw);
      portal.f_19859_ = yaw;
   }

   private static float yawFacing(Vec3 from, Vec3 to) {
      Vec3 delta = to.m_82546_(from);
      return (float)(Mth.m_14136_(-delta.f_82479_, delta.f_82481_) * 180.0F / (float)Math.PI);
   }

   private static Vec3 getEntityCenter(Entity entity) {
      return new Vec3(entity.m_20185_(), entity.m_20186_() + (double)entity.m_20206_() * 0.5, entity.m_20189_());
   }

   private static List<PortalEntity> findOwnedActivePortals(ServerLevel level, LivingEntity caster) {
      List<PortalEntity> portals = new ArrayList<>();
      UUID owner = caster.m_20148_();

      for (Entity entity : level.m_8583_()) {
         if (entity instanceof PortalEntity) {
            PortalEntity portal = (PortalEntity)entity;
            if (!portal.m_213877_() && portal.m_6084_() && portal.f_19797_ < 200 && owner.equals(portal.getOwnerUUID())) {
               portals.add(portal);
            }
         }
      }

      portals.sort(Comparator.comparingInt(PortalEntity::getPortalOrder).thenComparingInt(Entity::m_19879_));
      return portals;
   }

   private static PortalEntity findPendingPortal(List<PortalEntity> activePortals) {
      PortalEntity pendingPortal = null;

      for (PortalEntity portal : activePortals) {
         if (portal.getLinkedPortalUUID() == null && (pendingPortal == null || portal.getPortalOrder() > pendingPortal.getPortalOrder())) {
            pendingPortal = portal;
         }
      }

      return pendingPortal;
   }

   private static UUID selectPortalGroup(List<PortalEntity> activePortals, PortalEntity pendingPortal) {
      if (pendingPortal != null && pendingPortal.getPortalGroupUUID() != null) {
         return pendingPortal.getPortalGroupUUID();
      } else {
         for (int i = activePortals.size() - 1; i >= 0; i--) {
            UUID portalGroup = activePortals.get(i).getPortalGroupUUID();
            if (portalGroup != null) {
               return portalGroup;
            }
         }

         return UUID.randomUUID();
      }
   }

   private static int nextPortalOrder(List<PortalEntity> activePortals) {
      int nextOrder = 0;

      for (PortalEntity portal : activePortals) {
         nextOrder = Math.max(nextOrder, portal.getPortalOrder() + 1);
      }

      return nextOrder;
   }

   private static void linkPortalPair(PortalEntity first, PortalEntity second, UUID portalGroup) {
      first.setLinkedPortalUUID(second.m_20148_());
      second.setLinkedPortalUUID(first.m_20148_());
      first.setPortalGroupUUID(portalGroup);
      second.setPortalGroupUUID(portalGroup);
      setPortalYaw(first, yawFacing(first.m_20182_(), second.m_20182_()));
      setPortalYaw(second, yawFacing(second.m_20182_(), first.m_20182_()));
   }

   private static final class LookEntityHit {
      private final Entity entity;
      private final double distanceSqr;

      private LookEntityHit(Entity entity, double distanceSqr) {
         this.entity = entity;
         this.distanceSqr = distanceSqr;
      }
   }

   private static final class LookPortalTarget {
      private final Vec3 portalPos;
      private final Vec3 facingTarget;

      private LookPortalTarget(Vec3 portalPos, Vec3 facingTarget) {
         this.portalPos = portalPos;
         this.facingTarget = facingTarget;
      }
   }

   public static record PortalSpawnBatch(UUID portalGroup, int spawned) {
   }

   public static enum UseMode {
      NONE,
      MAIN_HAND,
      OFF_HAND,
      BOTH_HANDS;
   }

   public static final class UseResult {
      private static final TransporterFragmentItem.UseResult MISSED = new TransporterFragmentItem.UseResult(false, false, TransporterFragmentItem.UseMode.NONE);
      private final boolean consumed;
      private final boolean activated;
      private final TransporterFragmentItem.UseMode mode;

      private UseResult(boolean consumed, boolean activated, TransporterFragmentItem.UseMode mode) {
         this.consumed = consumed;
         this.activated = activated;
         this.mode = mode;
      }

      public static TransporterFragmentItem.UseResult missed() {
         return MISSED;
      }

      public static TransporterFragmentItem.UseResult consumed(TransporterFragmentItem.UseMode mode, boolean activated) {
         return new TransporterFragmentItem.UseResult(true, activated, mode);
      }

      public boolean consumed() {
         return this.consumed;
      }

      public boolean activated() {
         return this.activated;
      }

      public TransporterFragmentItem.UseMode mode() {
         return this.mode;
      }
   }
}
