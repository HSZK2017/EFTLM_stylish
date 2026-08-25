package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.client.renderer.HookGunItemRenderer;
import com.pla.annoyingvillagers.entity.HookGunHookEntity;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class HookGunItem extends Item {
   private static final String TAG_BOUND_ITEM = "HookGunBoundItem";
   private static final String TAG_VISUAL_HOOK_OUT = "HookGunVisualHookOut";
   private static final String TAG_LEFT_HOOK_ANIMATION = "HookGunLeftHookAnimation";
   private static final String TAG_RIGHT_HOOK_ANIMATION = "HookGunRightHookAnimation";
   public static final double MAX_ROPE_LENGTH = 30.0;
   public static final double HOOK_DESPAWN_DISTANCE = 42.0;
   public static final double HOOK_DESPAWN_DISTANCE_SQR = 1764.0;
   private static final float THROW_SPEED = 2.0F;
   private static final double DOUBLE_HOOK_ANGLE = 20.0;
   private static final double SNEAKING_DOUBLE_HOOK_ANGLE = 10.0;
   private static final double MOTOR_ACCELERATION = 0.2;
   private static final double MOTOR_MAX_SPEED = 4.0;
   private static final double ROPE_CORRECTION_ACCELERATION = 0.1;
   private static final double CLOSE_TO_ANCHOR_DISTANCE = 2.35;
   private static final double COLLISION_DAMPING = 0.25;
   private static final int USE_COOLDOWN_TICKS = 8;
   private static final byte HOOK_ANIMATION_NONE = 0;
   private static final byte HOOK_ANIMATION_NORMAL = 1;
   private static final byte HOOK_ANIMATION_TOP = 2;
   private static final double HOOK_ANIMATION_TOP_Y = 0.55;
   private static final double HOOK_ANIMATION_BACK_DOT = -0.2;

   public HookGunItem() {
      super(new Properties().m_41487_(1).m_41503_(384));
   }

   public boolean m_8120_(@NotNull ItemStack stack) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack, @NotNull Enchantment enchantment) {
      return false;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return false;
   }

   public int m_6473_() {
      return 0;
   }

   public void m_7373_(@NotNull ItemStack stack, @NotNull Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
      super.m_7373_(stack, level, tooltip, flag);
      tooltip.add(Component.m_237115_("tooltip.annoyingvillagers.hook_gun"));
   }

   public void m_6883_(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
      super.m_6883_(stack, level, entity, slotId, isSelected);
      if (!level.f_46443_ && isVisualHookOut(stack) && entity instanceof LivingEntity owner) {
         if (stack != owner.m_21205_() || !hasActiveHook(level, owner, true)) {
            if (stack != owner.m_21206_() || !hasActiveHook(level, owner, false)) {
               setVisualHookOut(stack, false);
            }
         }
      }
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new IClientItemExtensions() {
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return HookGunItemRenderer.getInstance();
         }
      });
   }

   @NotNull
   public InteractionResultHolder<ItemStack> m_7203_(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      if (!level.f_46443_) {
         boolean retrievingHooks = hasActiveHook(level, player);
         if (!useHookGun(level, player, hand)) {
            return InteractionResultHolder.m_19098_(stack);
         } else {
            if (!retrievingHooks) {
               player.m_36335_().m_41524_(this, 8);
            }

            player.m_36246_(Stats.f_12982_.m_12902_(this));
            return InteractionResultHolder.m_19092_(stack, false);
         }
      } else {
         if (isHoldingHookGunInBothHands(player) && hasActiveHook(level, player)) {
            swingBothHands(player);
         } else {
            if (!hasLaunchableBoundItem(player, hand)) {
               return InteractionResultHolder.m_19098_(stack);
            }

            if (isHoldingHookGunInBothHands(player)) {
               swingLaunchableHands(player);
            }
         }

         return InteractionResultHolder.m_19092_(stack, true);
      }
   }

   public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
      return true;
   }

   public static boolean useHookGun(Level level, LivingEntity owner, InteractionHand hand) {
      if (level.f_46443_) {
         return false;
      } else {
         List<HookGunHookEntity> activeHooks = getHooks(level, owner, false);
         if (!activeHooks.isEmpty()) {
            if (isHoldingHookGunInBothHands(owner)) {
               swingBothHands(owner);
            }

            returnHooks(activeHooks, false);
            cancelHookHandAnimations(owner);
            playRetrieveSound(level, owner);
            return true;
         } else {
            boolean doubleMode = isHoldingHookGunInBothHands(owner);
            if (doubleMode) {
               ItemStack offHand = owner.m_21206_();
               ItemStack mainHand = owner.m_21205_();
               ItemStack offBoundItem = getBoundItem(offHand);
               ItemStack mainBoundItem = getBoundItem(mainHand);
               boolean launchOffHand = !offBoundItem.m_41619_();
               boolean launchMainHand = !mainBoundItem.m_41619_();
               if (!launchOffHand && !launchMainHand) {
                  return false;
               }

               if (launchOffHand && launchMainHand) {
                  double angle = getDoubleHookAngle(owner);
                  launchHook(level, owner, -angle, true, false, offBoundItem);
                  launchHook(level, owner, angle, true, true, mainBoundItem);
               } else if (launchOffHand) {
                  launchHook(level, owner, 0.0, false, false, offBoundItem);
               } else {
                  launchHook(level, owner, 0.0, false, true, mainBoundItem);
               }

               swingLaunchedHands(owner, launchMainHand, launchOffHand);
               damageLaunchedStacks(owner, launchMainHand, launchOffHand);
            } else {
               ItemStack hookGunStack = owner.m_21120_(hand);
               ItemStack boundItem = getBoundItem(hookGunStack);
               if (boundItem.m_41619_()) {
                  return false;
               }

               launchHook(level, owner, 0.0, false, hand == InteractionHand.MAIN_HAND, boundItem);
               damageStack(owner, hookGunStack, hand);
            }

            level.m_6263_(null, owner.m_20185_(), owner.m_20186_(), owner.m_20189_(), SoundEvents.f_11687_, SoundSource.PLAYERS, 0.9F, 1.35F);
            return true;
         }
      }
   }

   public static boolean isHoldingHookGun(LivingEntity entity) {
      return entity.m_21205_().m_41720_() instanceof HookGunItem || entity.m_21206_().m_41720_() instanceof HookGunItem;
   }

   public static boolean isHoldingHookGunInBothHands(LivingEntity entity) {
      return entity.m_21205_().m_41720_() instanceof HookGunItem && entity.m_21206_().m_41720_() instanceof HookGunItem;
   }

   public static boolean shouldForceOffhandHookGunRender(LivingEntity entity) {
      return entity != null && entity.m_21206_().m_41720_() instanceof HookGunItem;
   }

   public static boolean shouldOffhandHookGunTakeRightClick(Player player) {
      if (player == null) {
         return false;
      } else {
         ItemStack offhand = player.m_21206_();
         return offhand.m_41720_() instanceof HookGunItem && !player.m_36335_().m_41519_(offhand.m_41720_());
      }
   }

   public static double getDoubleHookAngle(LivingEntity entity) {
      return entity.m_6047_() ? 10.0 : 20.0;
   }

   private static boolean hasLaunchableBoundItem(LivingEntity owner, InteractionHand hand) {
      return !isHoldingHookGunInBothHands(owner)
         ? !getBoundItem(owner.m_21120_(hand)).m_41619_()
         : !getBoundItem(owner.m_21205_()).m_41619_() || !getBoundItem(owner.m_21206_()).m_41619_();
   }

   public static boolean hasAttachedHook(Level level, LivingEntity owner) {
      return level != null && owner != null && !getHooks(level, owner, true).isEmpty();
   }

   public static boolean hasActiveHook(Level level, LivingEntity owner) {
      return level != null && owner != null && !getHooks(level, owner, false).isEmpty();
   }

   public static boolean hasActiveHook(Level level, LivingEntity owner, boolean rightHand) {
      return level != null && owner != null && getHooks(level, owner, false).stream().anyMatch(hook -> hook.isRightHand() == rightHand);
   }

   public static boolean hasActiveGrappleHook(Level level, LivingEntity owner) {
      return level != null && owner != null && getHooks(level, owner, false).stream().anyMatch(HookGunHookEntity::isGrappleHook);
   }

   public static boolean hasAttachedGrappleHook(Level level, LivingEntity owner) {
      return level != null && owner != null && getHooks(level, owner, true).stream().anyMatch(HookGunHookEntity::isGrappleHook);
   }

   public static boolean returnActiveHooks(Level level, LivingEntity owner, boolean grappleOnly) {
      if (level != null && owner != null) {
         boolean returned = returnHooks(getHooks(level, owner, false), grappleOnly);
         if (returned) {
            cancelHookHandAnimations(owner);
            playRetrieveSound(level, owner);
         }

         return returned;
      } else {
         return false;
      }
   }

   public static ItemStack getBoundItem(ItemStack hookGunStack) {
      if (!hookGunStack.m_41619_() && hookGunStack.m_41720_() instanceof HookGunItem && hookGunStack.m_41782_()) {
         CompoundTag tag = hookGunStack.m_41783_();
         return tag != null && tag.m_128425_("HookGunBoundItem", 10) ? ItemStack.m_41712_(tag.m_128469_("HookGunBoundItem")) : ItemStack.f_41583_;
      } else {
         return ItemStack.f_41583_;
      }
   }

   public static void setBoundItem(ItemStack hookGunStack, ItemStack boundItem) {
      if (!hookGunStack.m_41619_() && hookGunStack.m_41720_() instanceof HookGunItem) {
         if (boundItem.m_41619_()) {
            clearBoundItem(hookGunStack);
         } else {
            ItemStack stored = boundItem.m_41777_();
            stored.m_41764_(1);
            hookGunStack.m_41784_().m_128365_("HookGunBoundItem", stored.m_41739_(new CompoundTag()));
         }
      }
   }

   public static boolean isVisualHookOut(ItemStack hookGunStack) {
      return !hookGunStack.m_41619_()
         && hookGunStack.m_41720_() instanceof HookGunItem
         && hookGunStack.m_41782_()
         && hookGunStack.m_41784_().m_128471_("HookGunVisualHookOut");
   }

   public static void setVisualHookOut(ItemStack hookGunStack, boolean visualHookOut) {
      if (!hookGunStack.m_41619_() && hookGunStack.m_41720_() instanceof HookGunItem) {
         if (visualHookOut) {
            hookGunStack.m_41784_().m_128379_("HookGunVisualHookOut", true);
         } else if (hookGunStack.m_41782_()) {
            CompoundTag tag = hookGunStack.m_41783_();
            if (tag != null) {
               tag.m_128473_("HookGunVisualHookOut");
            }
         }
      }
   }

   public static void clearBoundItem(ItemStack hookGunStack) {
      if (hookGunStack.m_41782_()) {
         CompoundTag tag = hookGunStack.m_41783_();
         if (tag != null) {
            tag.m_128473_("HookGunBoundItem");
            tag.m_128473_("HookGunVisualHookOut");
         }
      }
   }

   public static boolean tryBindFromSpecialAttack(Player player) {
      ItemStack mainHand = player.m_21205_();
      ItemStack offHand = player.m_21206_();
      boolean mainHookGun = mainHand.m_41720_() instanceof HookGunItem;
      boolean offHookGun = offHand.m_41720_() instanceof HookGunItem;
      if (mainHookGun && !offHookGun) {
         return bindOrUnbind(player, mainHand, offHand, InteractionHand.OFF_HAND);
      } else {
         return offHookGun && !mainHookGun ? bindOrUnbind(player, offHand, mainHand, InteractionHand.MAIN_HAND) : false;
      }
   }

   private static boolean bindOrUnbind(Player player, ItemStack hookGunStack, ItemStack sourceStack, InteractionHand sourceHand) {
      if (sourceStack.m_41619_()) {
         ItemStack boundItem = getBoundItem(hookGunStack);
         if (boundItem.m_41619_()) {
            return false;
         } else {
            clearBoundItem(hookGunStack);
            giveOrDrop(player, boundItem);
            player.m_9236_().m_6263_(null, player.m_20185_(), player.m_20186_(), player.m_20189_(), SoundEvents.f_12019_, SoundSource.PLAYERS, 0.6F, 0.8F);
            return true;
         }
      } else if (sourceStack.m_41720_() instanceof HookGunItem) {
         return false;
      } else {
         ItemStack previousBoundItem = getBoundItem(hookGunStack);
         ItemStack boundItem = sourceStack.m_41777_();
         boundItem.m_41764_(1);
         setBoundItem(hookGunStack, boundItem);
         if (!player.m_150110_().f_35937_) {
            sourceStack.m_41774_(1);
         }

         returnPreviousBoundItem(player, previousBoundItem, sourceHand);
         player.m_9236_().m_6263_(null, player.m_20185_(), player.m_20186_(), player.m_20189_(), SoundEvents.f_11678_, SoundSource.PLAYERS, 0.7F, 1.2F);
         return true;
      }
   }

   private static void giveOrDrop(Player player, ItemStack stack) {
      ItemStack remaining = stack.m_41777_();
      player.m_150109_().m_36054_(remaining);
      if (!remaining.m_41619_()) {
         player.m_36176_(remaining, false);
      }
   }

   private static void returnPreviousBoundItem(Player player, ItemStack previousBoundItem, InteractionHand sourceHand) {
      if (!previousBoundItem.m_41619_()) {
         ItemStack returned = previousBoundItem.m_41777_();
         if (!tryMoveToInventoryAwayFromSourceHand(player, returned, sourceHand)) {
            ItemStack sourceHandStack = player.m_21120_(sourceHand);
            if (sourceHandStack.m_41619_()) {
               player.m_21008_(sourceHand, returned);
            } else {
               giveOrDrop(player, returned);
            }
         }
      }
   }

   private static boolean tryMoveToInventoryAwayFromSourceHand(Player player, ItemStack stack, InteractionHand sourceHand) {
      int avoidedSlot = sourceHand == InteractionHand.MAIN_HAND ? player.m_150109_().f_35977_ : -1;

      for (int slot = 0; slot < player.m_150109_().f_35974_.size(); slot++) {
         if (slot != avoidedSlot) {
            ItemStack target = (ItemStack)player.m_150109_().f_35974_.get(slot);
            if (!target.m_41619_() && target.m_41753_() && ItemStack.m_150942_(target, stack)) {
               int maxCount = Math.min(target.m_41741_(), player.m_150109_().m_6893_());
               int moved = Math.min(stack.m_41613_(), maxCount - target.m_41613_());
               if (moved > 0) {
                  target.m_41769_(moved);
                  target.m_41754_(5);
                  stack.m_41774_(moved);
                  player.m_150109_().m_6596_();
                  if (stack.m_41619_()) {
                     return true;
                  }
               }
            }
         }
      }

      for (int slotx = 0; slotx < player.m_150109_().f_35974_.size(); slotx++) {
         if (slotx != avoidedSlot && ((ItemStack)player.m_150109_().f_35974_.get(slotx)).m_41619_()) {
            ItemStack inserted = stack.m_41777_();
            inserted.m_41754_(5);
            player.m_150109_().f_35974_.set(slotx, inserted);
            stack.m_41764_(0);
            player.m_150109_().m_6596_();
            return true;
         }
      }

      return stack.m_41619_();
   }

   public static HookGunHookEntity launchHook(Level level, LivingEntity owner, double yawOffset, boolean doubleMode, boolean rightHand, ItemStack boundItem) {
      Vec3 origin = getHookStartPosition(owner, rightHand);
      Vec3 target = getHookAimTarget(level, owner, yawOffset);
      Vec3 direction = target.m_82546_(origin);
      if (direction.m_82556_() <= 1.0E-7) {
         direction = getHookAimDirection(owner, yawOffset);
      }

      return launchHook(level, owner, origin, direction, doubleMode, rightHand, boundItem);
   }

   public static HookGunHookEntity launchHookAt(Level level, LivingEntity owner, Vec3 target, boolean doubleMode, boolean rightHand, ItemStack boundItem) {
      Vec3 origin = getHookStartPosition(owner, rightHand);
      Vec3 direction = target.m_82546_(origin);
      if (direction.m_82556_() <= 1.0E-7) {
         direction = owner.m_20154_();
      }

      return launchHook(level, owner, origin, direction.m_82541_(), doubleMode, rightHand, boundItem);
   }

   private static HookGunHookEntity launchHook(
      Level level, LivingEntity owner, Vec3 origin, Vec3 direction, boolean doubleMode, boolean rightHand, ItemStack boundItem
   ) {
      HookGunHookEntity hook = new HookGunHookEntity(level, owner, doubleMode, rightHand, boundItem);
      double horizontal = Math.sqrt(direction.f_82479_ * direction.f_82479_ + direction.f_82481_ * direction.f_82481_);
      float hookYaw = (float)(Mth.m_14136_(direction.f_82479_, direction.f_82481_) * 180.0F / (float)Math.PI);
      float hookPitch = (float)(Mth.m_14136_(direction.f_82480_, horizontal) * 180.0F / (float)Math.PI);
      hook.m_7678_(origin.f_82479_, origin.f_82480_, origin.f_82481_, hookYaw, hookPitch);
      double extraVelocity = Math.max(0.0, owner.m_20184_().m_82526_(direction));
      hook.m_6686_(direction.f_82479_, direction.f_82480_, direction.f_82481_, 2.0F + (float)extraVelocity, 0.0F);
      level.m_7967_(hook);
      setVisualHookOut(getHookGunStack(owner, rightHand), true);
      updateHookHandAnimation(owner, rightHand, hook);
      return hook;
   }

   public static Vec3 getHookAimDirection(LivingEntity owner, double yawOffset) {
      return Vec3.m_82498_(owner.m_146909_(), owner.m_146908_() + (float)yawOffset).m_82541_();
   }

   private static Vec3 getHookAimTarget(Level level, LivingEntity owner, double yawOffset) {
      Vec3 eye = owner.m_146892_();
      Vec3 direction = getHookAimDirection(owner, yawOffset);
      Vec3 end = eye.m_82549_(direction.m_82490_(42.0));
      HitResult hitResult = level.m_45547_(new ClipContext(eye, end, Block.COLLIDER, Fluid.NONE, owner));
      return hitResult.m_6662_() == Type.MISS ? end : hitResult.m_82450_();
   }

   public static Vec3 getHookStartPosition(LivingEntity owner, boolean rightHand) {
      try {
         Vec3 pos = EpicfightUtil.getJointWithTranslation(
            owner,
            new Vec3f(0.0F, -0.3F, 0.0F),
            rightHand ? ((HumanoidArmature)Armatures.BIPED.get()).toolR : ((HumanoidArmature)Armatures.BIPED.get()).toolL,
            0.0F,
            0.0
         );
         if (pos != null) {
            return pos;
         }
      } catch (Exception var4) {
      }

      Vec3 look = owner.m_20154_();
      Vec3 side = new Vec3(-look.f_82481_, 0.0, look.f_82479_);
      if (side.m_82556_() > 1.0E-7) {
         side = side.m_82541_();
      } else {
         side = Vec3.f_82478_;
      }

      return owner.m_146892_().m_82549_(look.m_82490_(0.45)).m_82549_(side.m_82490_(rightHand ? 0.35 : -0.35)).m_82520_(0.0, -0.18, 0.0);
   }

   public static ItemStack getHookGunStack(LivingEntity owner, boolean rightHand) {
      if (owner == null) {
         return ItemStack.f_41583_;
      } else {
         ItemStack stack = rightHand ? owner.m_21205_() : owner.m_21206_();
         return stack.m_41720_() instanceof HookGunItem ? stack : ItemStack.f_41583_;
      }
   }

   private static void tickMotor(LivingEntity owner) {
      List<HookGunHookEntity> hooks = getHooks(owner.m_9236_(), owner, true);
      if (!hooks.isEmpty()) {
         long grappleHookCount = hooks.stream().filter(HookGunHookEntity::isGrappleHook).count();
         if (grappleHookCount > 0L) {
            Vec3 eye = owner.m_146892_();
            Vec3 currentMotion = owner.m_20184_();
            Vec3 totalPull = Vec3.f_82478_;
            int pullingHooks = 0;
            boolean closeToAnchor = false;
            boolean correctedRopeMotion = false;

            for (HookGunHookEntity hook : hooks) {
               if (hook.isGrappleHook()) {
                  Vec3 toAnchor = hook.getAnchor().m_82546_(eye);
                  double distance = toAnchor.m_82553_();
                  if (!(distance <= 1.0E-5)) {
                     Vec3 direction = toAnchor.m_82490_(1.0 / distance);
                     if (distance <= 2.35) {
                        closeToAnchor = true;
                     } else {
                        totalPull = totalPull.m_82549_(direction.m_82490_(0.2 / (double)grappleHookCount));
                     }

                     if (distance > 30.0) {
                        Vec3 awayFromAnchor = direction.m_82490_(-1.0);
                        double outwardSpeed = currentMotion.m_82526_(awayFromAnchor);
                        if (outwardSpeed > 0.0) {
                           currentMotion = currentMotion.m_82546_(awayFromAnchor.m_82490_(outwardSpeed));
                           correctedRopeMotion = true;
                        }

                        totalPull = totalPull.m_82549_(direction.m_82490_(Math.min(0.45, (distance - 30.0) * 0.1)));
                     }

                     pullingHooks++;
                  }
               }
            }

            if (pullingHooks > 0) {
               if (!closeToAnchor || !owner.f_19862_ && !owner.f_19863_ && !owner.m_20096_()) {
                  Vec3 newMotion = currentMotion;
                  if (totalPull.m_82556_() > 1.0E-7) {
                     Vec3 pull = clampPullToMotorMaxSpeed(currentMotion, totalPull);
                     newMotion = currentMotion.m_82549_(pull);
                  }

                  if (correctedRopeMotion || totalPull.m_82556_() > 1.0E-7) {
                     owner.m_20256_(newMotion);
                  }

                  owner.f_19864_ = true;
                  owner.f_19789_ = 0.0F;
               } else {
                  Vec3 damped = currentMotion.m_82490_(0.25);
                  if (owner.f_19862_ || Math.abs(damped.f_82480_) < 0.18) {
                     damped = new Vec3(damped.f_82479_, 0.0, damped.f_82481_);
                  }

                  owner.m_20256_(damped);
                  owner.f_19864_ = true;
                  owner.f_19789_ = 0.0F;
               }
            }
         }
      }
   }

   private static Vec3 clampPullToMotorMaxSpeed(Vec3 currentMotion, Vec3 pull) {
      double pullLength = pull.m_82553_();
      if (pullLength <= 1.0E-7) {
         return Vec3.f_82478_;
      } else {
         Vec3 pullDirection = pull.m_82490_(1.0 / pullLength);
         double currentSpeedAlongPull = currentMotion.m_82526_(pullDirection);
         if (currentSpeedAlongPull + pullLength <= 4.0) {
            return pull;
         } else {
            double allowedPull = Math.max(0.0, 4.0 - currentSpeedAlongPull);
            return pullDirection.m_82490_(allowedPull);
         }
      }
   }

   private static List<HookGunHookEntity> getHooks(Level level, LivingEntity owner, boolean attachedOnly) {
      AABB searchBox = owner.m_20191_().m_82400_(42.0);
      return level.m_6443_(HookGunHookEntity.class, searchBox, hook -> hook.isOwnedBy(owner) && (!attachedOnly || hook.isAttached()))
         .stream()
         .sorted(Comparator.comparingDouble(owner::m_20280_))
         .toList();
   }

   private static boolean returnHooks(List<HookGunHookEntity> hooks, boolean grappleOnly) {
      boolean returned = false;

      for (HookGunHookEntity hook : hooks) {
         if (!grappleOnly || hook.isGrappleHook()) {
            hook.returnToOwner();
            returned = true;
         }
      }

      return returned;
   }

   private static void damageLaunchedStacks(LivingEntity owner, boolean mainHand, boolean offHand) {
      if (mainHand) {
         damageStack(owner, owner.m_21205_(), InteractionHand.MAIN_HAND);
      }

      if (offHand) {
         damageStack(owner, owner.m_21206_(), InteractionHand.OFF_HAND);
      }
   }

   private static void damageStack(LivingEntity owner, ItemStack stack, InteractionHand hand) {
      if ((!(owner instanceof Player player) || !player.m_150110_().f_35937_) && stack.m_41720_() instanceof HookGunItem) {
         stack.m_41622_(1, owner, brokenOwner -> {
            if (brokenOwner instanceof ServerPlayer serverPlayer) {
               serverPlayer.m_21190_(hand);
            } else {
               brokenOwner.m_21190_(hand);
            }
         });
      }
   }

   private static void swingBothHands(LivingEntity owner) {
      owner.m_21011_(InteractionHand.MAIN_HAND, true);
      owner.m_21011_(InteractionHand.OFF_HAND, true);
   }

   private static void swingLaunchableHands(LivingEntity owner) {
      swingLaunchedHands(owner, !getBoundItem(owner.m_21205_()).m_41619_(), !getBoundItem(owner.m_21206_()).m_41619_());
   }

   private static void swingLaunchedHands(LivingEntity owner, boolean mainHand, boolean offHand) {
      if (mainHand && offHand) {
         swingBothHands(owner);
      } else if (mainHand) {
         owner.m_21011_(InteractionHand.MAIN_HAND, true);
      } else if (offHand) {
         owner.m_21011_(InteractionHand.OFF_HAND, true);
      }
   }

   private static void playRetrieveSound(Level level, LivingEntity owner) {
      level.m_6263_(null, owner.m_20185_(), owner.m_20186_(), owner.m_20189_(), SoundEvents.f_11841_, SoundSource.PLAYERS, 0.8F, 0.8F);
   }

   private static void tickHookGunState(LivingEntity owner) {
      if (!isHoldingHookGun(owner)) {
         cancelHookHandAnimations(owner);
      } else {
         tickMotor(owner);
         updateHookHandAnimations(owner);
      }
   }

   private static void updateHookHandAnimations(LivingEntity owner) {
      HookGunHookEntity leftHook = null;
      HookGunHookEntity rightHook = null;

      for (HookGunHookEntity hook : getHooks(owner.m_9236_(), owner, false)) {
         if (hook.isRightHand()) {
            if (rightHook == null) {
               rightHook = hook;
            }
         } else if (leftHook == null) {
            leftHook = hook;
         }
      }

      updateHookHandAnimation(owner, false, leftHook);
      updateHookHandAnimation(owner, true, rightHook);
   }

   private static void updateHookHandAnimation(LivingEntity owner, boolean rightHand, HookGunHookEntity hook) {
      if (isHoldingHookGunInHand(owner, rightHand) && hook != null && !hook.m_213877_() && !hook.isReturning()) {
         setHookHandAnimationState(owner, rightHand, getHookHandAnimationState(owner, hook));
      } else {
         cancelHookHandAnimation(owner, rightHand);
      }
   }

   private static boolean isHoldingHookGunInHand(LivingEntity owner, boolean rightHand) {
      ItemStack stack = rightHand ? owner.m_21205_() : owner.m_21206_();
      return stack.m_41720_() instanceof HookGunItem;
   }

   private static byte getHookHandAnimationState(LivingEntity owner, HookGunHookEntity hook) {
      Vec3 toHook = hook.getAnchor().m_82546_(owner.m_146892_());
      if (toHook.m_82556_() <= 1.0E-7) {
         return 1;
      } else {
         Vec3 direction = toHook.m_82541_();
         Vec3 look = owner.m_20154_().m_82541_();
         return (byte)(!(direction.f_82480_ > 0.55) && !(direction.m_82526_(look) < -0.2) ? 1 : 2);
      }
   }

   private static void setHookHandAnimationState(LivingEntity owner, boolean rightHand, byte nextState) {
      if (nextState == 0) {
         cancelHookHandAnimation(owner, rightHand);
      } else {
         String tagName = getHookHandAnimationTag(rightHand);
         byte currentState = owner.getPersistentData().m_128445_(tagName);
         if (currentState != nextState) {
            stopHookHandAnimations(owner, rightHand);
            LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
            AssetAccessor<? extends StaticAnimation> nextAnimation = getHookHandAnimation(rightHand, nextState);
            if (livingEntityPatch != null && nextAnimation != null) {
               owner.getPersistentData().m_128344_(tagName, nextState);
               livingEntityPatch.playAnimationSynchronized(nextAnimation, 0.0F);
            } else {
               owner.getPersistentData().m_128473_(tagName);
            }
         }
      }
   }

   public static void cancelHookHandAnimations(LivingEntity owner) {
      cancelHookHandAnimation(owner, false);
      cancelHookHandAnimation(owner, true);
   }

   public static void cancelHookHandAnimation(LivingEntity owner, boolean rightHand) {
      stopHookHandAnimations(owner, rightHand);
      owner.getPersistentData().m_128473_(getHookHandAnimationTag(rightHand));
   }

   private static void stopHookHandAnimations(LivingEntity owner, boolean rightHand) {
      EpicfightUtil.stopAnimationSynchronized(owner, getHookHandAnimation(rightHand, (byte)1));
      EpicfightUtil.stopAnimationSynchronized(owner, getHookHandAnimation(rightHand, (byte)2));
   }

   private static String getHookHandAnimationTag(boolean rightHand) {
      return rightHand ? "HookGunRightHookAnimation" : "HookGunLeftHookAnimation";
   }

   private static AssetAccessor<? extends StaticAnimation> getHookHandAnimation(boolean rightHand, byte state) {
      if (state == 1) {
         return rightHand ? AVAnimations.HOOK_HAND_RIGHT : AVAnimations.HOOK_HAND_LEFT;
      } else if (state == 2) {
         return rightHand ? AVAnimations.HOOK_HAND_RIGHT_TOP : AVAnimations.HOOK_HAND_LEFT_TOP;
      } else {
         return null;
      }
   }

   @EventBusSubscriber(
      modid = "annoyingvillagers",
      bus = Bus.FORGE
   )
   public static class Events {
      @SubscribeEvent
      public static void onLivingTick(LivingTickEvent event) {
         LivingEntity owner = event.getEntity();
         if (!owner.m_9236_().f_46443_) {
            if (HookGunItem.isHoldingHookGun(owner)
               || owner.getPersistentData().m_128441_("HookGunLeftHookAnimation")
               || owner.getPersistentData().m_128441_("HookGunRightHookAnimation")) {
               HookGunItem.tickHookGunState(owner);
            }
         }
      }
   }
}
