package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.entity.HerobrineGregEntity;
import com.pla.annoyingvillagers.entity.ItemProjectile;
import com.pla.annoyingvillagers.entity.TransporterHerobrineCloneEntity;
import com.pla.annoyingvillagers.mixin.FishingHookAccessor;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;

public final class FishingRodGrappleUtil {
   private static final String KEY_GRAPPLE_HOOK = "avGrappleFishingRod";
   private static final String KEY_RETURNING = "avReturningToRod";
   private static final String KEY_STICKY_TARGET_ID = "avStickyTargetId";
   private static final String KEY_STICKY_ITEM_PROJECTILE_ID = "avStickyItemProjectileId";
   private static final String KEY_COLLECT_RETURNING_ITEM = "avCollectReturningItem";
   private static final String KEY_SUPPRESS_STICKY_ITEM_RELEASE = "avSuppressStickyItemRelease";
   private static final String KEY_PENDING_RETURN_DAMAGE = "avPendingReturnDamage";
   private static final String KEY_LATCHED = "avLatched";
   private static final String KEY_TARGET_PLUNGED = "avTargetPlunged";
   private static final String KEY_HEROBRINE_ESCAPE_HOOK_ATTEMPTED_TARGET_ID = "avHerobrineEscapeHookAttemptedTargetId";
   private static final String KEY_NPC_COMBAT_HOOK = "avNpcCombatFishingHook";
   private static final String KEY_NPC_HOOK_RETURNING = "avNpcHookReturning";
   private static final String KEY_NPC_HOOK_LIFE = "avNpcHookLife";
   private static final String KEY_NPC_HOOK_RESOLVED = "avNpcHookResolved";
   private static final String KEY_NPC_HOOK_TIMED_OUT = "avNpcHookTimedOut";
   private static final String KEY_NPC_HOOK_TARGET_X = "avNpcHookTargetX";
   private static final String KEY_NPC_HOOK_TARGET_Y = "avNpcHookTargetY";
   private static final String KEY_NPC_HOOK_TARGET_Z = "avNpcHookTargetZ";
   private static final String KEY_NPC_HOOK_TARGET_ENTITY_ID = "avNpcHookTargetEntityId";
   private static final String KEY_ANCHOR_X = "avAX";
   private static final String KEY_ANCHOR_Y = "avAY";
   private static final String KEY_ANCHOR_Z = "avAZ";
   private static final double LATCH_STOPPED_SPEED_SQR = 0.001;
   private static final double TONY_PLAYER_GROUNDED_PLUNGE_POWER = 4.1;
   private static final double TONY_PLAYER_AIRBORNE_PLUNGE_POWER = 3.1;
   private static final double ADVANCED_PLAYER_GROUNDED_PLUNGE_POWER = 2.7;
   private static final double ADVANCED_PLAYER_AIRBORNE_PLUNGE_POWER = 2.0;
   private static final double TONY_TARGET_GROUNDED_PLUNGE_POWER = 2.0;
   private static final double TONY_TARGET_AIRBORNE_PLUNGE_POWER = 1.5;
   private static final double ADVANCED_TARGET_GROUNDED_PLUNGE_POWER = 1.2;
   private static final double ADVANCED_TARGET_AIRBORNE_PLUNGE_POWER = 0.9;
   private static final double HOOKED_TARGET_FACE_STOP_DISTANCE = 0.9;
   private static final double TONY_TARGET_DISTANCE_POWER_SCALE = 0.35;
   private static final double ADVANCED_TARGET_DISTANCE_POWER_SCALE = 0.22;
   private static final double HOOKED_TARGET_SEARCH_RADIUS = 0.65;
   private static final double TONY_RETURN_SPEED = 1.35;
   private static final double TONY_RETURN_ARRIVE_DISTANCE = 0.65;
   private static final double TONY_STICKY_TARGET_PUSH_DISTANCE = 2.0;
   private static final double TONY_ENTITY_STICK_CHANCE_MIN = 0.3;
   private static final double TONY_ENTITY_STICK_CHANCE_MAX = 0.5;
   private static final double TONY_DETACHED_HOOK_GRAVITY = 0.03;
   private static final double ITEM_ENTITY_STOP_SEARCH_INFLATION = 0.6;
   private static final double ITEM_ENTITY_STOP_BOX_INFLATION = 0.35;
   private static final double NPC_COMBAT_HOOK_CAST_SPEED = 1.65;
   private static final double NPC_COMBAT_HOOK_RETURN_SPEED = 1.85;
   private static final double NPC_COMBAT_HOOK_ARRIVE_DISTANCE = 0.55;
   private static final int NPC_COMBAT_HOOK_MAX_LIFE = 80;
   private static final int NPC_COMBAT_HOOK_MAX_RETURN_LIFE = 140;
   private static final int GRAPPLE_COOLDOWN_TICKS = 20;

   private FishingRodGrappleUtil() {
   }

   public static void inventoryTick(ItemStack stack, Level level, Entity entity) {
      if (!level.f_46443_ && entity instanceof Player player) {
         if (player.m_21205_() == stack || player.m_21206_() == stack) {
            FishingHook hook = player.f_36083_;
            if (hook != null && hook.m_6084_() && hook.getPersistentData().m_128471_("avGrappleFishingRod")) {
               latchHookIfReady(hook);
            }
         }
      }
   }

   public static InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      FishingHook hook = player.f_36083_;
      boolean disablePlunge = player.m_6144_();
      if (hook != null) {
         if (item instanceof TonyTheFishingRod || item instanceof AdvancedFishingRod) {
            if (hook.getPersistentData().m_128471_("avReturningToRod")) {
               level.m_6263_(
                  null,
                  player.m_20185_(),
                  player.m_20186_(),
                  player.m_20189_(),
                  SoundEvents.f_11941_,
                  SoundSource.NEUTRAL,
                  0.5F,
                  0.4F / (level.m_213780_().m_188501_() * 0.4F + 0.8F)
               );
               if (!level.f_46443_ && !handleTonyReturningStickyLivingTargetOnPull(item, player, hook, disablePlunge)) {
                  recastHookFromReturn(item, level, player, stack, hook, disablePlunge);
               }

               player.m_36246_(Stats.f_12982_.m_12902_(item));
               player.m_146850_(GameEvent.f_223698_);
               return InteractionResultHolder.m_19092_(stack, level.m_5776_());
            }

            if (!level.f_46443_) {
               int damage;
               if (item instanceof TonyTheFishingRod) {
                  if (disablePlunge) {
                     if (!handleTonySneakItemTargetOnPull(player, hook)) {
                        releaseTonyPlungePayloads(hook, player);
                     }
                  } else if (!handleTonyStickyLivingTargetOnPull(item, player, hook) && !handleTonyHookedTargetOnPull(item, player, hook)) {
                     tryPlunge(item, player, hook);
                  }

                  damage = getTonyReturnDamage(hook);
               } else {
                  if (!disablePlunge && !tryPlungeHookedTarget(item, player, hook, true)) {
                     tryPlunge(item, player, hook);
                  }

                  damage = getReturnDamage(hook);
               }

               stack.m_41622_(damage, player, brokenPlayer -> brokenPlayer.m_21190_(hand));
            }

            startHookReturn(hook);
            level.m_6263_(
               null,
               player.m_20185_(),
               player.m_20186_(),
               player.m_20189_(),
               SoundEvents.f_11939_,
               SoundSource.NEUTRAL,
               1.0F,
               0.4F / (level.m_213780_().m_188501_() * 0.4F + 0.8F)
            );
            player.m_146850_(GameEvent.f_223697_);
            return InteractionResultHolder.m_19092_(stack, level.m_5776_());
         }

         if (!level.f_46443_) {
            if (!disablePlunge && !tryPlungeHookedTarget(item, player, hook, true)) {
               tryPlunge(item, player, hook);
            }

            int damage = hook.m_37156_(stack);
            stack.m_41622_(damage, player, brokenPlayer -> brokenPlayer.m_21190_(hand));
         }

         level.m_6263_(
            null,
            player.m_20185_(),
            player.m_20186_(),
            player.m_20189_(),
            SoundEvents.f_11939_,
            SoundSource.NEUTRAL,
            1.0F,
            0.4F / (level.m_213780_().m_188501_() * 0.4F + 0.8F)
         );
         player.m_146850_(GameEvent.f_223697_);
      } else {
         level.m_6263_(
            null,
            player.m_20185_(),
            player.m_20186_(),
            player.m_20189_(),
            SoundEvents.f_11941_,
            SoundSource.NEUTRAL,
            0.5F,
            0.4F / (level.m_213780_().m_188501_() * 0.4F + 0.8F)
         );
         if (!level.f_46443_) {
            int lureSpeed = EnchantmentHelper.m_44916_(stack);
            int luck = EnchantmentHelper.m_44904_(stack);
            FishingHook grappleHook = new FishingHook(player, level, luck, lureSpeed);
            grappleHook.getPersistentData().m_128379_("avGrappleFishingRod", true);
            level.m_7967_(grappleHook);
         }

         player.m_36246_(Stats.f_12982_.m_12902_(item));
         player.m_146850_(GameEvent.f_223698_);
      }

      return InteractionResultHolder.m_19092_(stack, level.m_5776_());
   }

   public static boolean tickTonyReturningHook(FishingHook hook) {
      if (!hook.getPersistentData().m_128471_("avReturningToRod") && getStickyTarget(hook) != null) {
         if (!hasValidLivingOwner(hook)) {
            clearTonyPayload(hook);
            hook.m_146870_();
            return true;
         } else {
            followStickyTargetWithHook(hook);
            return true;
         }
      } else if (hasTonyStickyPayload(hook) && shouldLetStickyHookFlyWithoutRod(hook)) {
         tickDetachedStickyHook(hook);
         return true;
      } else if (!hook.getPersistentData().m_128471_("avReturningToRod")) {
         return false;
      } else {
         if (!(hook.m_19749_() instanceof LivingEntity owner) || !owner.m_6084_() || owner.m_213877_()) {
            releaseTonyPayloadWithoutOwner(hook);
            hook.m_146870_();
            return true;
         }

         if (!isHoldingGrappleRod(owner) && !hasTonyStickyPayload(hook)) {
            hook.m_146870_();
            return true;
         } else {
            Vec3 target = getTonyReturnTarget(owner);
            Vec3 current = hook.m_20182_();
            Vec3 toTarget = target.m_82546_(current);
            double distance = toTarget.m_82553_();
            if (distance <= 0.65) {
               hook.m_20256_(Vec3.f_82478_);
               hook.m_6034_(target.f_82479_, target.f_82480_, target.f_82481_);
               moveTonyPayloadWithHook(hook, Vec3.f_82478_);
               if (hook.m_9236_().f_46443_) {
                  return true;
               } else if (hook.getPersistentData().m_128471_("avCollectReturningItem")) {
                  collectReturningItemPayload(hook, owner);
                  hook.m_146870_();
                  return true;
               } else {
                  if (!hasTonyStickyPayload(hook)) {
                     hook.m_146870_();
                  } else if (getStickyTarget(hook) != null) {
                     hook.getPersistentData().m_128379_("avReturningToRod", false);
                     followStickyTargetWithHook(hook);
                  }

                  return true;
               }
            } else {
               Vec3 step = toTarget.m_82490_(Math.min(1.35, distance) / distance);
               hook.m_20242_(true);
               hook.f_19789_ = 0.0F;
               hook.m_20256_(step);
               hook.m_6034_(current.f_82479_ + step.f_82479_, current.f_82480_ + step.f_82480_, current.f_82481_ + step.f_82481_);
               rotateHookToward(hook, step);
               moveTonyPayloadWithHook(hook, step);
               hook.f_19812_ = true;
               return true;
            }
         }
      }
   }

   public static void afterTonyHookVanillaTick(FishingHook hook) {
      if (hook.getPersistentData().m_128471_("avGrappleFishingRod")) {
         ItemProjectile stickyProjectile = getStickyItemProjectile(hook);
         if (stickyProjectile != null && hook.m_37170_() != null && hook.m_37170_() != stickyProjectile) {
            setVanillaHookedEntity(hook, null);
         }

         resolveHerobrineEscapeHookOnHit(hook);
         if (!hook.getPersistentData().m_128471_("avReturningToRod") && !hasTonyStickyPayload(hook)) {
            stopHookAtHitItemEntity(hook);
         }

         if (!hook.getPersistentData().m_128471_("avReturningToRod") && hasTonyStickyPayload(hook)) {
            moveTonyPayloadWithHook(hook, hook.m_20184_());
         }
      }
   }

   public static boolean shouldIgnoreHookEntityHit(FishingHook hook, Entity target) {
      if (!hook.getPersistentData().m_128471_("avGrappleFishingRod")) {
         return false;
      } else {
         ItemProjectile stickyProjectile = getStickyItemProjectile(hook);
         return stickyProjectile != null && stickyProjectile.isHookAttached();
      }
   }

   public static boolean isHookControllingItemProjectile(FishingHook hook, ItemProjectile projectile) {
      return hook != null
         && projectile != null
         && hook.m_6084_()
         && (hook.getPersistentData().m_128471_("avGrappleFishingRod") || hook.getPersistentData().m_128471_("avNpcCombatFishingHook"))
         && hook.getPersistentData().m_128451_("avStickyItemProjectileId") == projectile.m_19879_();
   }

   @Nullable
   public static FishingHook spawnNpcCombatFishingHook(LivingEntity owner, Vec3 destination) {
      return spawnNpcCombatFishingHook(owner, destination, null);
   }

   @Nullable
   public static FishingHook spawnNpcCombatFishingHook(LivingEntity owner, Vec3 destination, @Nullable Entity trackedTarget) {
      if (!owner.m_9236_().f_46443_ && destination != null) {
         Vec3 start = getNpcCombatHookCastOrigin(owner);
         Vec3 toDestination = destination.m_82546_(start);
         if (toDestination.m_82556_() < 1.0E-6) {
            toDestination = owner.m_20154_().m_82490_(4.0);
            destination = start.m_82549_(toDestination);
         }

         FishingHook hook = new FishingHook(EntityType.f_20533_, owner.m_9236_());
         hook.m_5602_(owner);
         hook.m_7678_(start.f_82479_, start.f_82480_, start.f_82481_, owner.m_146908_(), owner.m_146909_());
         Vec3 velocity = toDestination.m_82541_().m_82490_(1.65);
         hook.m_20256_(velocity);
         rotateHookToward(hook, velocity);
         hook.getPersistentData().m_128379_("avNpcCombatFishingHook", true);
         hook.getPersistentData().m_128379_("avNpcHookReturning", false);
         hook.getPersistentData().m_128405_("avNpcHookLife", 0);
         hook.getPersistentData().m_128379_("avNpcHookResolved", false);
         hook.getPersistentData().m_128379_("avNpcHookTimedOut", false);
         hook.getPersistentData().m_128347_("avNpcHookTargetX", destination.f_82479_);
         hook.getPersistentData().m_128347_("avNpcHookTargetY", destination.f_82480_);
         hook.getPersistentData().m_128347_("avNpcHookTargetZ", destination.f_82481_);
         if (trackedTarget != null && trackedTarget.m_6084_() && trackedTarget != owner) {
            hook.getPersistentData().m_128405_("avNpcHookTargetEntityId", trackedTarget.m_19879_());
         }

         owner.m_9236_().m_7967_(hook);
         return hook;
      } else {
         return null;
      }
   }

   public static void attachNpcCombatFishingHookPayload(@Nullable FishingHook hook, LivingEntity owner, ItemStack stack) {
      if (hook != null && hook.m_6084_() && !stack.m_41619_() && hook.getPersistentData().m_128471_("avNpcCombatFishingHook")) {
         ItemProjectile projectile = ItemProjectile.createHookPayload(hook.m_9236_(), hook, stack.m_41777_(), hook.m_20182_());
         projectile.setDiscardWhenHookLost(true);
         hook.m_9236_().m_7967_(projectile);
         hook.getPersistentData().m_128405_("avStickyItemProjectileId", projectile.m_19879_());
         hook.getPersistentData().m_128379_("avCollectReturningItem", false);
         hook.getPersistentData().m_128473_("avStickyTargetId");
         projectile.moveWithHook(hook.m_20182_(), owner);
      }
   }

   public static boolean isNpcCombatFishingHookResolved(@Nullable FishingHook hook) {
      return hook == null
         || !hook.m_6084_()
         || hook.getPersistentData().m_128471_("avNpcHookResolved")
         || hook.getPersistentData().m_128471_("avNpcHookTimedOut");
   }

   public static void forceNpcCombatFishingHookReturn(@Nullable FishingHook hook) {
      if (hook != null && hook.m_6084_() && hook.getPersistentData().m_128471_("avNpcCombatFishingHook")) {
         markNpcCombatHookResolved(hook, true);
      }
   }

   public static boolean tickNpcCombatFishingHook(FishingHook hook) {
      Entity ownerEntity = hook.m_19749_();
      boolean serverHook = hook.getPersistentData().m_128471_("avNpcCombatFishingHook");
      if (!serverHook && !isNpcCombatFishingHookOwner(ownerEntity)) {
         return false;
      } else {
         if (!(ownerEntity instanceof LivingEntity owner) || !owner.m_6084_() || owner.m_213877_()) {
            discardNpcCombatHookPayload(hook);
            hook.m_146870_();
            return true;
         }

         if (!serverHook) {
            tickClientNpcCombatFishingHook(hook);
            return true;
         } else {
            int life = hook.getPersistentData().m_128451_("avNpcHookLife") + 1;
            hook.getPersistentData().m_128405_("avNpcHookLife", life);
            boolean returning = hook.getPersistentData().m_128471_("avNpcHookReturning");
            if (!returning && life >= 80) {
               markNpcCombatHookResolved(hook, true);
               returning = true;
            } else if (returning && life > 140) {
               discardNpcCombatHookPayload(hook);
               hook.m_146870_();
               return true;
            }

            Vec3 destination = returning ? getNpcCombatHookCastOrigin(owner) : getNpcCombatHookTarget(hook);
            Vec3 current = hook.m_20182_();
            Vec3 toDestination = destination.m_82546_(current);
            double distance = toDestination.m_82553_();
            if (distance <= 0.55) {
               hook.m_20256_(Vec3.f_82478_);
               hook.m_20242_(true);
               hook.m_6034_(destination.f_82479_, destination.f_82480_, destination.f_82481_);
               moveTonyPayloadWithHook(hook, Vec3.f_82478_);
               if (returning) {
                  discardNpcCombatHookPayload(hook);
                  hook.m_146870_();
               } else {
                  markNpcCombatHookResolved(hook, false);
               }

               return true;
            } else if (distance <= 1.0E-6) {
               return true;
            } else {
               double speed = returning ? 1.85 : 1.65;
               Vec3 step = toDestination.m_82490_(Math.min(speed, distance) / distance);
               hook.m_20242_(true);
               hook.f_19789_ = 0.0F;
               hook.m_20256_(step);
               hook.m_6034_(current.f_82479_ + step.f_82479_, current.f_82480_ + step.f_82480_, current.f_82481_ + step.f_82481_);
               rotateHookToward(hook, step);
               moveTonyPayloadWithHook(hook, step);
               hook.f_19812_ = true;
               return true;
            }
         }
      }
   }

   private static void discardNpcCombatHookPayload(FishingHook hook) {
      ItemProjectile projectile = getStickyItemProjectile(hook);
      if (projectile != null) {
         projectile.m_146870_();
      }

      hook.getPersistentData().m_128473_("avStickyItemProjectileId");
      hook.getPersistentData().m_128379_("avCollectReturningItem", false);
      setVanillaHookedEntity(hook, null);
   }

   private static void markNpcCombatHookResolved(FishingHook hook, boolean timedOut) {
      hook.getPersistentData().m_128379_("avNpcHookResolved", true);
      hook.getPersistentData().m_128379_("avNpcHookTimedOut", timedOut);
      hook.getPersistentData().m_128379_("avNpcHookReturning", true);
   }

   public static boolean isNpcCombatFishingHookOwner(Entity entity) {
      if (entity instanceof LivingEntity owner && !(owner instanceof Player) && isHoldingGrappleRod(owner)) {
         return true;
      }

      return false;
   }

   public static void onGrappleHookRemoved(FishingHook hook) {
      if (hook.getPersistentData().m_128471_("avGrappleFishingRod") && !hook.getPersistentData().m_128471_("avSuppressStickyItemRelease")) {
         ItemProjectile projectile = getStickyItemProjectile(hook);
         if (projectile != null && projectile.isHookAttached()) {
            projectile.dropAsItem(hook.m_20184_());
         }

         clearTonyPayload(hook);
      }
   }

   public static float getCastProperty(ItemStack stack, LivingEntity entity) {
      if (entity == null) {
         return 0.0F;
      } else {
         boolean mainHand = entity.m_21205_() == stack;
         boolean offHand = entity.m_21206_() == stack;
         if (entity.m_21205_().m_41720_() instanceof FishingRodItem) {
            offHand = false;
         }

         if ((mainHand || offHand) && entity instanceof Player player && player.f_36083_ != null) {
            return 1.0F;
         }

         return 0.0F;
      }
   }

   public static boolean shouldOffhandFishingRodTakeRightClick(Player player) {
      ItemStack offhand = player.m_21206_();
      return (offhand.m_41720_() instanceof FishingRodItem || offhand.canPerformAction(ToolActions.FISHING_ROD_CAST))
         && !player.m_36335_().m_41519_(offhand.m_41720_());
   }

   public static boolean shouldForceOffhandFishingRodRender(LivingEntity entity) {
      Item offhandItem = entity.m_21206_().m_41720_();
      return offhandItem instanceof TonyTheFishingRod || offhandItem instanceof AdvancedFishingRod;
   }

   private static void startHookReturn(FishingHook hook) {
      hook.getPersistentData().m_128379_("avReturningToRod", true);
      hook.getPersistentData().m_128379_("avLatched", false);
      hook.m_20242_(true);
      hook.m_20256_(Vec3.f_82478_);
   }

   private static void recastHookFromReturn(Item item, Level level, Player player, ItemStack stack, FishingHook returningHook, boolean disablePlunge) {
      Vec3 start = returningHook.m_20182_();
      boolean tonyRod = item instanceof TonyTheFishingRod;
      Entity stickyTarget = tonyRod ? getStickyTarget(returningHook) : null;
      ItemProjectile stickyProjectile = tonyRod ? getStickyItemProjectile(returningHook) : null;
      boolean collectReturningItem = tonyRod && returningHook.getPersistentData().m_128471_("avCollectReturningItem");
      boolean collectItemNow = tonyRod && disablePlunge && stickyProjectile != null;
      returningHook.getPersistentData().m_128379_("avSuppressStickyItemRelease", true);
      returningHook.m_146870_();
      if (collectItemNow) {
         stickyProjectile.giveToOwnerOrDrop(player);
         stickyProjectile = null;
         collectReturningItem = false;
      }

      int lureSpeed = EnchantmentHelper.m_44916_(stack);
      int luck = EnchantmentHelper.m_44904_(stack);
      FishingHook grappleHook = new FishingHook(player, level, luck, lureSpeed);
      Vec3 castVelocity = grappleHook.m_20184_();
      grappleHook.m_7678_(start.f_82479_, start.f_82480_, start.f_82481_, player.m_146908_(), player.m_146909_());
      grappleHook.m_20256_(castVelocity);
      rotateHookToward(grappleHook, castVelocity);
      grappleHook.getPersistentData().m_128379_("avGrappleFishingRod", true);
      if (tonyRod && !disablePlunge && stickyProjectile != null && stickyProjectile.m_6084_()) {
         grappleHook.getPersistentData().m_128405_("avStickyItemProjectileId", stickyProjectile.m_19879_());
         grappleHook.getPersistentData().m_128379_("avCollectReturningItem", collectReturningItem);
         stickyProjectile.moveWithHook(start, player);
      }

      if (tonyRod && !disablePlunge && stickyTarget != null && stickyTarget.m_6084_()) {
         if (isStickyTargetCloseToOwner(player, stickyTarget)) {
            plungeTargetAlongHookCast(item, player, stickyTarget, castVelocity);
         } else {
            plungeTargetTowardOwner(item, player, stickyTarget);
         }

         if (shouldKeepStickyEntityAttachment(level)) {
            grappleHook.getPersistentData().m_128405_("avStickyTargetId", stickyTarget.m_19879_());
            setVanillaHookedEntity(grappleHook, stickyTarget);
         }
      }

      level.m_7967_(grappleHook);
   }

   private static int getTonyReturnDamage(FishingHook hook) {
      if (hook.getPersistentData().m_128441_("avPendingReturnDamage")) {
         int damage = hook.getPersistentData().m_128451_("avPendingReturnDamage");
         hook.getPersistentData().m_128473_("avPendingReturnDamage");
         return damage;
      } else {
         return getReturnDamage(hook);
      }
   }

   private static int getReturnDamage(FishingHook hook) {
      if (hook.getPersistentData().m_128451_("avStickyItemProjectileId") > 0) {
         return 3;
      } else {
         Entity hookedTarget = hook.m_37170_();
         if (hookedTarget instanceof ItemEntity || hookedTarget instanceof ItemProjectile) {
            return 3;
         } else if (hookedTarget != null) {
            return 5;
         } else {
            return hook.m_20096_() ? 2 : 0;
         }
      }
   }

   private static Vec3 getTonyReturnTarget(LivingEntity owner) {
      return new Vec3(owner.m_20185_(), owner.m_20188_() - 0.1, owner.m_20189_());
   }

   private static boolean isHoldingTonyRod(LivingEntity owner) {
      return owner.m_21205_().m_41720_() instanceof TonyTheFishingRod || owner.m_21206_().m_41720_() instanceof TonyTheFishingRod;
   }

   private static boolean isHoldingGrappleRod(LivingEntity owner) {
      return owner.m_21205_().m_41720_() instanceof TonyTheFishingRod
         || owner.m_21206_().m_41720_() instanceof TonyTheFishingRod
         || owner.m_21205_().m_41720_() instanceof AdvancedFishingRod
         || owner.m_21206_().m_41720_() instanceof AdvancedFishingRod;
   }

   private static Vec3 getNpcCombatHookCastOrigin(LivingEntity owner) {
      return new Vec3(owner.m_20185_(), owner.m_20188_() - 0.1, owner.m_20189_());
   }

   private static Vec3 getNpcCombatHookTarget(FishingHook hook) {
      Entity trackedTarget = getNpcCombatHookTrackedTarget(hook);
      return trackedTarget != null
         ? trackedTarget.m_20182_().m_82520_(0.0, (double)trackedTarget.m_20206_() * 0.55, 0.0)
         : new Vec3(
            hook.getPersistentData().m_128459_("avNpcHookTargetX"),
            hook.getPersistentData().m_128459_("avNpcHookTargetY"),
            hook.getPersistentData().m_128459_("avNpcHookTargetZ")
         );
   }

   @Nullable
   private static Entity getNpcCombatHookTrackedTarget(FishingHook hook) {
      int targetId = hook.getPersistentData().m_128451_("avNpcHookTargetEntityId");
      if (targetId <= 0) {
         return null;
      } else {
         Entity target = hook.m_9236_().m_6815_(targetId);
         if (target != null && target.m_6084_() && !target.m_213877_() && target != hook.m_19749_()) {
            return target;
         } else {
            hook.getPersistentData().m_128473_("avNpcHookTargetEntityId");
            return null;
         }
      }
   }

   private static void tickClientNpcCombatFishingHook(FishingHook hook) {
      Vec3 current = hook.m_20182_();
      Vec3 movement = hook.m_20184_();
      hook.m_6034_(current.f_82479_ + movement.f_82479_, current.f_82480_ + movement.f_82480_, current.f_82481_ + movement.f_82481_);
      if (!hook.m_20068_()) {
         hook.m_20334_(movement.f_82479_ * 0.98, movement.f_82480_ - 0.03, movement.f_82481_ * 0.98);
      }

      rotateHookToward(hook, movement);
      hook.f_19812_ = true;
   }

   private static void rotateHookToward(FishingHook hook, Vec3 movement) {
      if (!(movement.m_82556_() < 1.0E-6)) {
         hook.m_146922_((float)(Mth.m_14136_(movement.f_82479_, movement.f_82481_) * 180.0F / (float)Math.PI));
         hook.m_146926_((float)(Mth.m_14136_(movement.f_82480_, movement.m_165924_()) * 180.0F / (float)Math.PI));
      }
   }

   private static void stopHookAtHitItemEntity(FishingHook hook) {
      Entity hookedTarget = hook.m_37170_();
      if (hookedTarget instanceof ItemEntity itemEntity && itemEntity.m_6084_() && !itemEntity.m_213877_()) {
         stopHookAtItemEntity(hook, itemEntity, getItemHookPosition(itemEntity));
         return;
      }

      if (hookedTarget == null) {
         if (hook.m_19749_() instanceof LivingEntity owner) {
            Vec3 var17 = new Vec3(hook.f_19854_, hook.f_19855_, hook.f_19856_);
            Vec3 to = hook.m_20182_();
            if (!(var17.m_82557_(to) < 1.0E-7)) {
               AABB searchBox = new AABB(var17, to).m_82400_(0.6);
               ItemEntity closestItem = null;
               Vec3 closestHit = null;
               double closestDistance = Double.MAX_VALUE;

               for (ItemEntity itemEntity : hook.m_9236_().m_6443_(ItemEntity.class, searchBox, target -> isPullableHookTarget(owner, target))) {
                  AABB itemBox = itemEntity.m_20191_().m_82400_(0.35);
                  Vec3 hitPosition;
                  if (itemBox.m_82390_(var17)) {
                     hitPosition = var17;
                  } else {
                     Optional<Vec3> hit = itemBox.m_82371_(var17, to);
                     if (hit.isEmpty()) {
                        continue;
                     }

                     hitPosition = hit.get();
                  }

                  double distance = var17.m_82557_(hitPosition);
                  if (distance < closestDistance) {
                     closestItem = itemEntity;
                     closestHit = hitPosition;
                     closestDistance = distance;
                  }
               }

               if (closestItem != null) {
                  stopHookAtItemEntity(hook, closestItem, closestHit);
               }
            }
         }
      }
   }

   private static Vec3 getItemHookPosition(ItemEntity itemEntity) {
      return itemEntity.m_20182_().m_82520_(0.0, (double)itemEntity.m_20206_() * 0.5, 0.0);
   }

   private static void stopHookAtItemEntity(FishingHook hook, ItemEntity itemEntity, Vec3 position) {
      hook.m_20242_(true);
      hook.f_19789_ = 0.0F;
      hook.m_20256_(Vec3.f_82478_);
      hook.m_6034_(position.f_82479_, position.f_82480_, position.f_82481_);
      setVanillaHookedEntity(hook, itemEntity);
      hook.f_19812_ = true;
   }

   private static boolean handleTonyReturningStickyLivingTargetOnPull(Item item, LivingEntity owner, FishingHook hook, boolean disablePlunge) {
      if (!disablePlunge && item instanceof TonyTheFishingRod) {
         Entity target = getStickyTarget(hook);
         if (!(target instanceof LivingEntity)) {
            return false;
         } else if (isStickyTargetCloseToOwner(owner, target)) {
            return false;
         } else {
            pullStickyLivingTargetTowardOwner(item, owner, hook, target);
            return true;
         }
      } else {
         return false;
      }
   }

   private static boolean handleTonyStickyLivingTargetOnPull(Item item, LivingEntity owner, FishingHook hook) {
      if (!(item instanceof TonyTheFishingRod)) {
         return false;
      } else {
         Entity target = getStickyTarget(hook);
         if (!(target instanceof LivingEntity)) {
            return false;
         } else {
            if (isStickyTargetCloseToOwner(owner, target)) {
               plungeTargetAwayFromOwner(item, owner, target);
            } else {
               plungeTargetTowardOwner(item, owner, target);
            }

            rollStickyLivingTargetAttachment(hook, target);
            return true;
         }
      }
   }

   private static void pullStickyLivingTargetTowardOwner(Item item, LivingEntity owner, FishingHook hook, Entity target) {
      plungeTargetTowardOwner(item, owner, target);
      rollStickyLivingTargetAttachment(hook, target);
   }

   private static void rollStickyLivingTargetAttachment(FishingHook hook, Entity target) {
      hook.getPersistentData().m_128379_("avTargetPlunged", true);
      hook.getPersistentData().m_128405_("avPendingReturnDamage", 5);
      hook.getPersistentData().m_128473_("avStickyItemProjectileId");
      hook.getPersistentData().m_128379_("avCollectReturningItem", false);
      if (shouldKeepStickyEntityAttachment(hook.m_9236_())) {
         hook.getPersistentData().m_128405_("avStickyTargetId", target.m_19879_());
         setVanillaHookedEntity(hook, target);
      } else {
         hook.getPersistentData().m_128473_("avStickyTargetId");
         setVanillaHookedEntity(hook, null);
      }
   }

   private static boolean isStickyTargetCloseToOwner(LivingEntity owner, Entity target) {
      return owner.m_20280_(target) <= 4.0;
   }

   private static boolean handleTonyHookedTargetOnPull(Item item, LivingEntity owner, FishingHook hook) {
      if (hook.getPersistentData().m_128471_("avGrappleFishingRod") && !hook.getPersistentData().m_128471_("avTargetPlunged")) {
         ItemProjectile stickyProjectile = getStickyItemProjectile(hook);
         if (stickyProjectile != null) {
            hook.getPersistentData().m_128379_("avTargetPlunged", true);
            hook.getPersistentData().m_128405_("avPendingReturnDamage", 3);
            attachItemProjectileForReturn(hook, stickyProjectile, false);
            return true;
         } else {
            Entity target = getHookedTarget(owner, hook);
            if (target == null) {
               clearTonyPayload(hook);
               return false;
            } else {
               FishingRodGrappleUtil.HerobrineEscapeHookResult escapeHookResult = tryCancelHerobrineEscapeWithFishingHook(hook, target);
               if (escapeHookResult == FishingRodGrappleUtil.HerobrineEscapeHookResult.FAILED) {
                  hook.getPersistentData().m_128379_("avTargetPlunged", true);
                  hook.getPersistentData().m_128473_("avStickyItemProjectileId");
                  hook.getPersistentData().m_128379_("avCollectReturningItem", false);
                  setVanillaHookedEntity(hook, null);
                  return true;
               } else {
                  hook.getPersistentData().m_128379_("avTargetPlunged", true);
                  hook.getPersistentData().m_128405_("avPendingReturnDamage", !(target instanceof ItemEntity) && !(target instanceof ItemProjectile) ? 5 : 3);
                  if (target instanceof ItemEntity itemEntity) {
                     ItemProjectile projectile = convertItemEntityToProjectile(owner, hook, itemEntity);
                     attachItemProjectileForReturn(hook, projectile, false);
                     return true;
                  } else if (target instanceof ItemProjectile projectile) {
                     attachItemProjectileForReturn(hook, projectile, false);
                     return true;
                  } else {
                     plungeTargetTowardOwner(item, owner, target);
                     if (shouldKeepStickyEntityAttachment(hook.m_9236_())) {
                        hook.getPersistentData().m_128405_("avStickyTargetId", target.m_19879_());
                        setVanillaHookedEntity(hook, target);
                     } else {
                        hook.getPersistentData().m_128473_("avStickyTargetId");
                        setVanillaHookedEntity(hook, null);
                     }

                     hook.getPersistentData().m_128473_("avStickyItemProjectileId");
                     hook.getPersistentData().m_128379_("avCollectReturningItem", false);
                     return true;
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   private static boolean handleTonySneakItemTargetOnPull(LivingEntity owner, FishingHook hook) {
      if (!hook.getPersistentData().m_128471_("avGrappleFishingRod")) {
         return false;
      } else {
         Entity target = getHookedTarget(owner, hook);
         if (target instanceof ItemEntity itemEntity) {
            ItemProjectile projectile = convertItemEntityToProjectile(owner, hook, itemEntity);
            attachItemProjectileForReturn(hook, projectile, false);
            return true;
         } else if (target instanceof ItemProjectile projectile) {
            attachItemProjectileForReturn(hook, projectile, false);
            return true;
         } else {
            return false;
         }
      }
   }

   private static ItemProjectile convertItemEntityToProjectile(LivingEntity owner, FishingHook hook, ItemEntity itemEntity) {
      ItemStack itemStack = itemEntity.m_32055_().m_41777_();
      ItemProjectile projectile = ItemProjectile.createHookPayload(hook.m_9236_(), owner, itemStack, hook.m_20182_());
      hook.m_9236_().m_7967_(projectile);
      itemEntity.m_146870_();
      return projectile;
   }

   private static void attachItemProjectileForReturn(FishingHook hook, ItemProjectile projectile, boolean keepStuck) {
      hook.getPersistentData().m_128405_("avStickyItemProjectileId", projectile.m_19879_());
      hook.getPersistentData().m_128379_("avCollectReturningItem", !keepStuck);
      hook.getPersistentData().m_128473_("avStickyTargetId");
      setVanillaHookedEntity(hook, keepStuck ? projectile : null);
      projectile.moveWithHook(hook.m_20182_(), (Entity)(hook.m_19749_() != null ? hook.m_19749_() : hook));
   }

   private static boolean shouldKeepStickyEntityAttachment(Level level) {
      return rollStickyAttachment(level, 0.3, 0.5);
   }

   private static boolean rollStickyAttachment(Level level, double minChance, double maxChance) {
      double chance = minChance + level.m_213780_().m_188500_() * (maxChance - minChance);
      return level.m_213780_().m_188500_() < chance;
   }

   private static boolean hasTonyStickyPayload(FishingHook hook) {
      return hook.getPersistentData().m_128451_("avStickyTargetId") > 0
         || hook.getPersistentData().m_128451_("avStickyItemProjectileId") > 0
         || getClientSyncedStickyTarget(hook) != null
         || getClientSyncedStickyItemProjectile(hook) != null;
   }

   private static Entity getStickyTarget(FishingHook hook) {
      int targetId = hook.getPersistentData().m_128451_("avStickyTargetId");
      if (targetId <= 0) {
         return getClientSyncedStickyTarget(hook);
      } else {
         Entity target = hook.m_9236_().m_6815_(targetId);
         if (!isValidStickyTarget(hook, target)) {
            hook.getPersistentData().m_128473_("avStickyTargetId");
            return null;
         } else {
            return target;
         }
      }
   }

   private static ItemProjectile getStickyItemProjectile(FishingHook hook) {
      int projectileId = hook.getPersistentData().m_128451_("avStickyItemProjectileId");
      if (projectileId <= 0) {
         return getClientSyncedStickyItemProjectile(hook);
      } else {
         if (hook.m_9236_().m_6815_(projectileId) instanceof ItemProjectile projectile && projectile.m_6084_()) {
            return projectile;
         }

         hook.getPersistentData().m_128473_("avStickyItemProjectileId");
         hook.getPersistentData().m_128379_("avCollectReturningItem", false);
         return null;
      }
   }

   private static Entity getClientSyncedStickyTarget(FishingHook hook) {
      if (!hook.m_9236_().f_46443_) {
         return null;
      } else {
         Entity target = hook.m_37170_();
         if (!(target instanceof ItemEntity) && !(target instanceof ItemProjectile)) {
            return isValidStickyTarget(hook, target) ? target : null;
         } else {
            return null;
         }
      }
   }

   private static ItemProjectile getClientSyncedStickyItemProjectile(FishingHook hook) {
      if (!hook.m_9236_().f_46443_) {
         return null;
      } else {
         if (hook.m_37170_() instanceof ItemProjectile projectile && projectile.m_6084_() && !projectile.m_213877_()) {
            return projectile;
         }

         return null;
      }
   }

   private static boolean isValidStickyTarget(FishingHook hook, Entity target) {
      if (target != null && target.m_6084_() && !target.m_213877_()) {
         Entity owner = hook.m_19749_();
         return owner == null || target != owner && !target.m_20148_().equals(owner.m_20148_());
      } else {
         return false;
      }
   }

   private static void setVanillaHookedEntity(FishingHook hook, Entity target) {
      ((FishingHookAccessor)hook).annoyingVillagers$invokeSetHookedEntity(target);
   }

   private static void moveTonyPayloadWithHook(FishingHook hook, Vec3 hookMotion) {
      Entity owner = (Entity)(hook.m_19749_() != null ? hook.m_19749_() : hook);
      Entity stickyTarget = getStickyTarget(hook);
      if (stickyTarget != null) {
         followStickyTargetWithHook(hook, stickyTarget);
      }

      ItemProjectile projectile = getStickyItemProjectile(hook);
      if (projectile != null) {
         projectile.moveWithHook(hook.m_20182_(), owner);
      }
   }

   private static void followStickyTargetWithHook(FishingHook hook) {
      Entity stickyTarget = getStickyTarget(hook);
      if (stickyTarget != null) {
         followStickyTargetWithHook(hook, stickyTarget);
      }
   }

   private static void followStickyTargetWithHook(FishingHook hook, Entity target) {
      Vec3 oldPos = hook.m_20182_();
      Vec3 targetPos = target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.65, 0.0);
      Vec3 motion = targetPos.m_82546_(oldPos);
      hook.m_20242_(true);
      hook.f_19789_ = 0.0F;
      hook.m_20256_(Vec3.f_82478_);
      hook.m_6034_(targetPos.f_82479_, targetPos.f_82480_, targetPos.f_82481_);
      rotateHookToward(hook, motion);
   }

   private static boolean shouldLetStickyHookFlyWithoutRod(FishingHook hook) {
      if (hook.getPersistentData().m_128471_("avCollectReturningItem")) {
         return false;
      } else {
         if (hook.m_19749_() instanceof LivingEntity livingOwner && livingOwner.m_6084_() && !isHoldingTonyRod(livingOwner)) {
            return true;
         }

         return false;
      }
   }

   private static boolean hasValidLivingOwner(FishingHook hook) {
      if (hook.m_19749_() instanceof LivingEntity owner && owner.m_6084_() && !owner.m_213877_()) {
         return true;
      }

      return false;
   }

   private static void tickDetachedStickyHook(FishingHook hook) {
      Vec3 motion = hook.m_20184_();
      if (motion.m_82556_() < 1.0E-7) {
         if (hook.m_20096_()) {
            moveTonyPayloadWithHook(hook, Vec3.f_82478_);
            return;
         }

         motion = hook.m_20154_().m_82490_(0.2);
      }

      hook.m_20242_(false);
      hook.m_6478_(MoverType.SELF, motion);
      rotateHookToward(hook, motion);
      moveTonyPayloadWithHook(hook, motion);
      Vec3 nextMotion = motion.m_82490_(0.92).m_82520_(0.0, -0.03, 0.0);
      if (hook.m_20096_()) {
         nextMotion = Vec3.f_82478_;
      }

      hook.m_20256_(nextMotion);
   }

   private static void releaseTonyPayloadWithoutOwner(FishingHook hook) {
      ItemProjectile projectile = getStickyItemProjectile(hook);
      if (projectile != null && hook.getPersistentData().m_128471_("avCollectReturningItem")) {
         projectile.dropAsItem(hook.m_20184_());
      }

      clearTonyPayload(hook);
   }

   private static void collectReturningItemPayload(FishingHook hook, Entity receiver) {
      ItemProjectile projectile = getStickyItemProjectile(hook);
      if (projectile != null) {
         projectile.giveToOwnerOrDrop(receiver);
      }

      hook.getPersistentData().m_128473_("avStickyItemProjectileId");
      hook.getPersistentData().m_128379_("avCollectReturningItem", false);
      setVanillaHookedEntity(hook, null);
   }

   private static void clearTonyPayload(FishingHook hook) {
      hook.getPersistentData().m_128473_("avStickyTargetId");
      hook.getPersistentData().m_128473_("avStickyItemProjectileId");
      hook.getPersistentData().m_128379_("avCollectReturningItem", false);
      hook.getPersistentData().m_128473_("avPendingReturnDamage");
      hook.getPersistentData().m_128473_("avHerobrineEscapeHookAttemptedTargetId");
      setVanillaHookedEntity(hook, null);
   }

   private static void releaseTonyPlungePayloads(FishingHook hook, Entity receiver) {
      ItemProjectile projectile = getStickyItemProjectile(hook);
      if (projectile != null) {
         projectile.giveToOwnerOrDrop(receiver);
      }

      clearTonyPayload(hook);
   }

   private static void plungeTargetAlongHookCast(Item item, LivingEntity owner, Entity target, Vec3 castVelocity) {
      Vec3 direction = castVelocity.m_82556_() > 1.0E-6 ? castVelocity.m_82541_() : owner.m_20154_();
      boolean grounded = target.m_20096_();
      Vec3 velocity = target.m_20184_();
      if (grounded) {
         velocity = velocity.m_82520_(0.0, 0.35, 0.0);
      }

      velocity = velocity.m_82549_(direction.m_82490_(getTargetPlungePower(item, grounded)));
      target.m_20256_(velocity);
      target.f_19812_ = true;
      target.f_19864_ = true;
      target.f_19789_ = 0.0F;
   }

   private static void plungeTargetAwayFromOwner(Item item, LivingEntity owner, Entity target) {
      Vec3 playerCenter = owner.m_20182_().m_82520_(0.0, (double)owner.m_20192_(), 0.0);
      Vec3 targetCenter = target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.5, 0.0);
      Vec3 direction = targetCenter.m_82546_(playerCenter);
      if (direction.m_82556_() < 1.0E-6) {
         direction = owner.m_20154_();
      }

      direction = direction.m_82541_();
      boolean grounded = target.m_20096_();
      double maxY = grounded ? 1.0 : 0.7;
      direction = new Vec3(direction.f_82479_, Math.max(-maxY, Math.min(maxY, direction.f_82480_)), direction.f_82481_);
      Vec3 velocity = target.m_20184_();
      if (grounded) {
         velocity = velocity.m_82520_(0.0, 0.35, 0.0);
      }

      velocity = velocity.m_82549_(direction.m_82490_(getTargetPlungePower(item, grounded)));
      target.m_20256_(velocity);
      target.f_19812_ = true;
      target.f_19864_ = true;
      target.f_19789_ = 0.0F;
   }

   private static void tryPlunge(Item item, Player player, FishingHook hook) {
      if (hook.getPersistentData().m_128471_("avGrappleFishingRod")) {
         if (getHookedTarget(player, hook) == null) {
            if (!player.m_36335_().m_41519_(item)) {
               if (!hook.m_9236_().m_6425_(hook.m_20183_()).m_205070_(FluidTags.f_13131_)) {
                  latchHookIfReady(hook);
                  if (hook.getPersistentData().m_128471_("avLatched")) {
                     Vec3 anchor = new Vec3(
                        hook.getPersistentData().m_128459_("avAX"), hook.getPersistentData().m_128459_("avAY"), hook.getPersistentData().m_128459_("avAZ")
                     );
                     Vec3 eye = player.m_20182_().m_82520_(0.0, (double)player.m_20192_(), 0.0);
                     Vec3 direction = anchor.m_82546_(eye);
                     if (!(direction.m_82556_() < 1.0E-6)) {
                        direction = direction.m_82541_();
                        boolean grounded = player.m_20096_();
                        double maxY = grounded ? 1.0 : 0.7;
                        direction = new Vec3(direction.f_82479_, Math.max(-maxY, Math.min(maxY, direction.f_82480_)), direction.f_82481_);
                        Vec3 velocity = player.m_20184_();
                        if (grounded) {
                           velocity = velocity.m_82520_(0.0, 0.42, 0.0);
                        }

                        player.m_7292_(new MobEffectInstance(MobEffects.f_19620_, 5, 1, false, false));
                        velocity = velocity.m_82549_(direction.m_82490_(getPlayerPlungePower(item, grounded)));
                        player.m_20256_(velocity);
                        player.f_19864_ = true;
                        player.f_19789_ = 0.0F;
                        player.m_36335_().m_41524_(item, 20);
                        hook.getPersistentData().m_128379_("avLatched", false);
                     }
                  }
               }
            }
         }
      }
   }

   private static void latchHookIfReady(FishingHook hook) {
      if (!hook.getPersistentData().m_128471_("avLatched")) {
         if (!hook.m_9236_().m_6425_(hook.m_20183_()).m_205070_(FluidTags.f_13131_)) {
            if (hook.m_20096_() || !(hook.m_20184_().m_82556_() >= 0.001)) {
               Vec3 anchor = hook.m_20182_();
               hook.getPersistentData().m_128379_("avLatched", true);
               hook.getPersistentData().m_128347_("avAX", anchor.f_82479_);
               hook.getPersistentData().m_128347_("avAY", anchor.f_82480_);
               hook.getPersistentData().m_128347_("avAZ", anchor.f_82481_);
            }
         }
      }
   }

   private static boolean tryPlungeHookedTarget(Item item, Player player, FishingHook hook, boolean allowFallbackSearch) {
      if (hook.getPersistentData().m_128471_("avGrappleFishingRod") && !hook.getPersistentData().m_128471_("avTargetPlunged")) {
         Entity hookedTarget = hook.m_37170_();
         Entity target = hookedTarget != null && isPullableHookTarget(player, hookedTarget)
            ? hookedTarget
            : (allowFallbackSearch ? findHookedTargetNearHook(player, hook) : null);
         if (target == null) {
            return false;
         } else {
            FishingRodGrappleUtil.HerobrineEscapeHookResult escapeHookResult = tryCancelHerobrineEscapeWithFishingHook(hook, target);
            if (escapeHookResult == FishingRodGrappleUtil.HerobrineEscapeHookResult.FAILED) {
               hook.getPersistentData().m_128379_("avTargetPlunged", true);
               setVanillaHookedEntity(hook, null);
               return true;
            } else {
               plungeTargetTowardOwner(item, player, target);
               hook.getPersistentData().m_128379_("avTargetPlunged", true);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private static FishingRodGrappleUtil.HerobrineEscapeHookResult tryCancelHerobrineEscapeWithFishingHook(FishingHook hook, Entity target) {
      if (target instanceof HerobrineGregEntity greg && greg.canFishingHookCancelEscape()) {
         if (greg.tryFishingHookCancelEscape()) {
            setVanillaHookedEntity(hook, greg);
            return FishingRodGrappleUtil.HerobrineEscapeHookResult.CANCELLED;
         }

         setVanillaHookedEntity(hook, null);
         return FishingRodGrappleUtil.HerobrineEscapeHookResult.FAILED;
      }

      if (!(target instanceof TransporterHerobrineCloneEntity transporter) || !transporter.canFishingHookCancelEscape()) {
         return FishingRodGrappleUtil.HerobrineEscapeHookResult.NONE;
      }

      if (transporter.tryFishingHookCancelEscape()) {
         setVanillaHookedEntity(hook, transporter);
         return FishingRodGrappleUtil.HerobrineEscapeHookResult.CANCELLED;
      } else {
         setVanillaHookedEntity(hook, null);
         return FishingRodGrappleUtil.HerobrineEscapeHookResult.FAILED;
      }
   }

   private static void resolveHerobrineEscapeHookOnHit(FishingHook hook) {
      if (hook.getPersistentData().m_128471_("avGrappleFishingRod")) {
         Entity target = hook.m_37170_();
         if (target != null) {
            int targetId = target.m_19879_();
            if (hook.getPersistentData().m_128451_("avHerobrineEscapeHookAttemptedTargetId") != targetId) {
               FishingRodGrappleUtil.HerobrineEscapeHookResult result = tryCancelHerobrineEscapeWithFishingHook(hook, target);
               if (result != FishingRodGrappleUtil.HerobrineEscapeHookResult.NONE) {
                  hook.getPersistentData().m_128405_("avHerobrineEscapeHookAttemptedTargetId", targetId);
                  if (result == FishingRodGrappleUtil.HerobrineEscapeHookResult.FAILED) {
                     hook.getPersistentData().m_128379_("avTargetPlunged", true);
                  }
               }
            }
         }
      }
   }

   private static Entity getHookedTarget(LivingEntity owner, FishingHook hook) {
      Entity hookedTarget = hook.m_37170_();
      return hookedTarget != null && isPullableHookTarget(owner, hookedTarget) ? hookedTarget : findHookedTargetNearHook(owner, hook);
   }

   private static Entity findHookedTargetNearHook(LivingEntity owner, FishingHook hook) {
      AABB searchBox = hook.m_20191_().m_82400_(0.65);
      return hook.m_9236_()
         .m_6249_(hook, searchBox, target -> isPullableHookTarget(owner, target))
         .stream()
         .min((left, right) -> Double.compare(left.m_20280_(hook), right.m_20280_(hook)))
         .orElse(null);
   }

   private static boolean isPullableHookTarget(LivingEntity owner, Entity target) {
      return target != owner && !target.m_5833_() && target.m_6084_()
         ? target instanceof ItemEntity || target instanceof ItemProjectile || target.m_6087_()
         : false;
   }

   private static void plungeTargetTowardOwner(Item item, LivingEntity owner, Entity target) {
      Vec3 playerCenter = owner.m_20182_().m_82520_(0.0, (double)owner.m_20192_(), 0.0);
      Vec3 targetCenter = target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.5, 0.0);
      Vec3 direction = playerCenter.m_82546_(targetCenter);
      double distance = direction.m_82553_();
      if (!(distance < 1.0E-6)) {
         double plungeDistance = Math.max(0.0, distance - 0.9);
         if (!(plungeDistance <= 0.0)) {
            direction = direction.m_82490_(1.0 / distance);
            boolean grounded = target.m_20096_();
            double maxY = grounded ? 1.0 : 0.7;
            direction = new Vec3(direction.f_82479_, Math.max(-maxY, Math.min(maxY, direction.f_82480_)), direction.f_82481_);
            Vec3 velocity = Vec3.f_82478_;
            if (grounded) {
               velocity = velocity.m_82520_(0.0, 0.42, 0.0);
            }

            if (target instanceof LivingEntity livingTarget) {
               livingTarget.m_7292_(new MobEffectInstance(MobEffects.f_19620_, 5, 1, false, false));
            }

            double maxPower = getTargetPlungePower(item, grounded);
            double distancePowerScale = item instanceof AdvancedFishingRod ? 0.22 : 0.35;
            double power = Math.min(maxPower, plungeDistance * distancePowerScale);
            target.m_20256_(velocity.m_82549_(direction.m_82490_(power)));
            target.f_19812_ = true;
            target.f_19864_ = true;
            target.f_19789_ = 0.0F;
         }
      }
   }

   private static double getPlayerPlungePower(Item item, boolean grounded) {
      if (item instanceof AdvancedFishingRod) {
         return grounded ? 2.7 : 2.0;
      } else {
         return grounded ? 4.1 : 3.1;
      }
   }

   private static double getTargetPlungePower(Item item, boolean grounded) {
      if (item instanceof AdvancedFishingRod) {
         return grounded ? 1.2 : 0.9;
      } else {
         return grounded ? 2.0 : 1.5;
      }
   }

   private static enum HerobrineEscapeHookResult {
      NONE,
      CANCELLED,
      FAILED;
   }
}
