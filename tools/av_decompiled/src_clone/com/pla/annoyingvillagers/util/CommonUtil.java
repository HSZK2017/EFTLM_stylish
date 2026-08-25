package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.clazz.HookDisarmLaunch;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.entity.AngrySteveEntity;
import com.pla.annoyingvillagers.entity.BlueDemonEntity;
import com.pla.annoyingvillagers.entity.HerobrineGregEntity;
import com.pla.annoyingvillagers.entity.ItemProjectile;
import com.pla.annoyingvillagers.entity.LowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.LowShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFight;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.item.FlankerHookedSwordItem;
import com.pla.annoyingvillagers.item.HookedDiamondSwordItem;
import com.pla.annoyingvillagers.item.HookedGoldenSwordItem;
import com.pla.annoyingvillagers.item.HookedIronSwordItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class CommonUtil {
   public static boolean isAvDamageableEfnWeaponsMob(Entity livingEntity) {
      return livingEntity instanceof BlueDemonEntity
         || livingEntity instanceof AngrySteveEntity
         || livingEntity instanceof HerobrineMob
         || livingEntity instanceof HerobrineGregEntity
         || livingEntity instanceof LowHerobrineCloneEntity
         || livingEntity instanceof LowShadowHerobrineCloneEntity;
   }

   public static boolean isAvRunawayJudgementCutEndMob(Entity livingEntity) {
      return livingEntity instanceof BlueDemonEntity
         || livingEntity instanceof AVNpc
         || livingEntity instanceof HerobrineMob
         || livingEntity instanceof HerobrineGregEntity
         || livingEntity instanceof LowHerobrineCloneEntity
         || livingEntity instanceof LowShadowHerobrineCloneEntity;
   }

   public static void forceRotate(LivingEntity entity, LivingEntity lookAtEntity) {
      if (entity != null && lookAtEntity != null) {
         forceRotate(entity, lookAtEntity.m_146892_());
      }
   }

   public static void forceRotate(LivingEntity entity, BlockPos lookAtPos) {
      if (entity != null && lookAtPos != null) {
         forceRotate(entity, Vec3.m_82512_(lookAtPos));
      }
   }

   public static void forceRotate(LivingEntity entity, Vec3 lookAtPos) {
      if (entity != null && lookAtPos != null) {
         Vec3 eyePos = entity.m_146892_();
         double dx = lookAtPos.f_82479_ - eyePos.f_82479_;
         double dy = lookAtPos.f_82480_ - eyePos.f_82480_;
         double dz = lookAtPos.f_82481_ - eyePos.f_82481_;
         double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
         if (!(horizontalDistance < 1.0E-7)) {
            float yaw = (float)(Mth.m_14136_(dz, dx) * 180.0F / (float)Math.PI) - 90.0F;
            float pitch = (float)(-(Mth.m_14136_(dy, horizontalDistance) * 180.0F / (float)Math.PI));
            yaw = Mth.m_14177_(yaw);
            pitch = Mth.m_14036_(Mth.m_14177_(pitch), -90.0F, 90.0F);
            entity.m_146922_(yaw);
            entity.m_146926_(pitch);
            entity.f_19859_ = yaw;
            entity.f_19860_ = pitch;
            entity.m_5616_(yaw);
            entity.f_20886_ = yaw;
            entity.f_20883_ = yaw;
            entity.f_20884_ = yaw;
         }
      }
   }

   public static void pullEntityTowardCaster(LivingEntity target, LivingEntity caster) {
      pullEntityTowardCaster(target, caster, 0.22, 0.04, true);
   }

   public static void pullEntityTowardCaster(LivingEntity target, LivingEntity caster, double strength, double yBoost, boolean forceLookAtCaster) {
      if (target != null && caster != null) {
         if (target.m_6084_() && caster.m_6084_()) {
            if (forceLookAtCaster) {
               forceRotate(target, caster);
            }

            pullEntityTowardPosition(target, caster.m_20182_(), strength, yBoost);
         }
      }
   }

   public static void pullEntityTowardPosition(Entity target, Vec3 targetPos, double strength, double yBoost) {
      if (target != null && targetPos != null) {
         Vec3 direction = targetPos.m_82546_(target.m_20182_());
         applyHorizontalDirectionalMotion(target, direction, strength, yBoost);
      }
   }

   public static void pushEntityFromCaster(LivingEntity target, LivingEntity caster) {
      forceRotate(target, caster);
      pushEntityFromCaster(target, caster, 0.35, 0.08);
   }

   public static void pushEntityFromCaster(Entity target, Entity caster, double strength, double yBoost) {
      if (target != null && caster != null) {
         pushEntityFromPosition(target, caster.m_20182_(), strength, yBoost);
      }
   }

   public static void pushEntityFromPosition(Entity target, Vec3 sourcePos, double strength, double yBoost) {
      if (target != null && sourcePos != null) {
         Vec3 direction = target.m_20182_().m_82546_(sourcePos);
         if (direction.m_165925_() < 1.0E-7) {
            direction = target.m_20154_();
         }

         applyHorizontalDirectionalMotion(target, direction, strength, yBoost);
      }
   }

   private static void applyHorizontalDirectionalMotion(Entity entity, Vec3 direction, double strength, double yBoost) {
      if (entity != null && direction != null) {
         if (!(strength <= 0.0)) {
            Vec3 horizontal = new Vec3(direction.f_82479_, 0.0, direction.f_82481_);
            if (!(horizontal.m_82556_() < 1.0E-7)) {
               Vec3 motion = horizontal.m_82541_().m_82490_(strength);
               entity.m_20256_(entity.m_20184_().m_82520_(motion.f_82479_, yBoost, motion.f_82481_));
               entity.f_19812_ = true;
               entity.f_19864_ = true;
            }
         }
      }
   }

   private static boolean matchesItemEntry(ItemStack stack, String entry) {
      if (!stack.m_41619_() && entry != null && !entry.isBlank()) {
         ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.m_41720_());
         if (itemId == null) {
            return false;
         } else if (entry.startsWith("#")) {
            ResourceLocation tagId = ResourceLocation.m_135820_(entry.substring(1));
            return tagId == null ? false : stack.m_204117_(TagKey.m_203882_(Registries.f_256913_, tagId));
         } else if (entry.endsWith(":*")) {
            String namespace = entry.substring(0, entry.length() - 2);
            return itemId.m_135827_().equals(namespace);
         } else {
            return !entry.contains(":") ? itemId.m_135827_().equals(entry) : itemId.toString().equals(entry);
         }
      } else {
         return false;
      }
   }

   private static boolean matchesEntityEntry(LivingEntity entity, String entry) {
      if (entry != null && !entry.isBlank()) {
         EntityType<?> type = entity.m_6095_();
         ResourceLocation typeId = ForgeRegistries.ENTITY_TYPES.getKey(type);
         if (typeId == null) {
            return false;
         } else if (entry.startsWith("#")) {
            ResourceLocation tagId = ResourceLocation.m_135820_(entry.substring(1));
            return tagId == null ? false : type.m_204039_(TagKey.m_203882_(Registries.f_256939_, tagId));
         } else if (entry.endsWith(":*")) {
            String namespace = entry.substring(0, entry.length() - 2);
            return typeId.m_135827_().equals(namespace);
         } else {
            return !entry.contains(":") ? typeId.m_135827_().equals(entry) : typeId.toString().equals(entry);
         }
      } else {
         return false;
      }
   }

   public static boolean isPullableWeapon(ItemStack stack) {
      if (stack.m_41619_()) {
         return false;
      } else {
         Item item = stack.m_41720_();
         return item instanceof SwordItem || item instanceof DiggerItem || item instanceof TridentItem;
      }
   }

   public static boolean isBlacklistedWeapon(ItemStack stack) {
      if (stack.m_41619_()) {
         return false;
      } else {
         for (String entry : (List)AnnoyingVillagersConfig.WEAPON_DISARMS_BLACKLIST.get()) {
            if (matchesItemEntry(stack, entry)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean entityCanBeDisarmed(LivingEntity entity) {
      List<? extends String> entries = (List<? extends String>)AnnoyingVillagersConfig.WEAPON_DISARMS_AFFECTED_ENTITY_TYPES.get();
      if (entries.isEmpty()) {
         return false;
      } else {
         for (String entry : entries) {
            if (matchesEntityEntry(entity, entry)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isHookSword(ItemStack stack) {
      if (stack.m_41619_()) {
         return false;
      } else {
         Item item = stack.m_41720_();
         return item instanceof HookedIronSwordItem
            || item instanceof HookedGoldenSwordItem
            || item instanceof HookedDiamondSwordItem
            || item instanceof FlankerHookedSwordItem;
      }
   }

   public static boolean isHookSwordClashAnimation(AssetAccessor<? extends StaticAnimation> dynamicAnimation) {
      return dynamicAnimation == AnimsEpicFight.HOOK_AXE_AUTO1
         || dynamicAnimation == AnimsEpicFight.HOOK_AXE_AUTO2
         || dynamicAnimation == AnimsEpicFight.HOOK_DANCING_EDGE
         || dynamicAnimation == AnimsWom.HOOK_HERRSCHER_UP;
   }

   public static void applyHookClashDisarmLogic(
      LivingEntity livingEntity,
      LivingEntity attackerLivingEntity,
      ServerLevel serverLevel,
      AssetAccessor<? extends StaticAnimation> knockdownAnimation,
      HookDisarmLaunch launch
   ) {
      if (attackerLivingEntity != null && attackerLivingEntity.m_6084_()) {
         forceRotate(attackerLivingEntity, livingEntity);
         playForcedKnockdown(attackerLivingEntity, knockdownAnimation);
         tryDisarmAndLaunchWeapon(serverLevel, livingEntity, attackerLivingEntity, launch);
      }
   }

   public static void applyHookClashDisarmLogic(
      LivingEntity livingEntity,
      DamageSource damageSource,
      ServerLevel serverLevel,
      AssetAccessor<? extends StaticAnimation> knockdownAnimation,
      HookDisarmLaunch launch
   ) {
      LivingEntity target = getClashLivingTarget(damageSource, livingEntity);
      applyHookClashDisarmLogic(livingEntity, target, serverLevel, knockdownAnimation, launch);
   }

   private static LivingEntity getClashLivingTarget(DamageSource damageSource, LivingEntity player) {
      Entity entity = damageSource.m_7639_();
      if (entity == player || !(entity instanceof LivingEntity)) {
         entity = damageSource.m_7640_();
      }

      if (entity instanceof LivingEntity livingEntity && livingEntity != player) {
         return livingEntity;
      }

      return null;
   }

   private static void playForcedKnockdown(LivingEntity target, AssetAccessor<? extends StaticAnimation> knockdownAnimation) {
      LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
      if (targetPatch != null) {
         targetPatch.playAnimationSynchronized(knockdownAnimation, 0.0F);
      }
   }

   public static void fallBackOnBlackListWeapon(LivingEntity owner, Entity source, ItemStack blacklistedStack) {
   }

   private static void tryDisarmAndLaunchWeapon(ServerLevel serverLevel, LivingEntity livingEntity, LivingEntity target, HookDisarmLaunch launch) {
      if (entityCanBeDisarmed(target)) {
         List<InteractionHand> candidateHands = new ArrayList<>(2);
         ItemStack blacklistedStack = ItemStack.f_41583_;
         ItemStack mainHand = target.m_21205_();
         ItemStack offHand = target.m_21206_();
         if (isPullableWeapon(mainHand)) {
            if (isBlacklistedWeapon(mainHand)) {
               blacklistedStack = mainHand;
            } else {
               candidateHands.add(InteractionHand.MAIN_HAND);
            }
         }

         if (isPullableWeapon(offHand)) {
            if (isBlacklistedWeapon(offHand)) {
               blacklistedStack = offHand;
            } else {
               candidateHands.add(InteractionHand.OFF_HAND);
            }
         }

         if (candidateHands.isEmpty()) {
            if (!blacklistedStack.m_41619_()) {
               fallBackOnBlackListWeapon(livingEntity, target, blacklistedStack);
            }
         } else {
            InteractionHand chosenHand = candidateHands.get(serverLevel.f_46441_.m_188503_(candidateHands.size()));
            ItemStack chosenStack = target.m_21120_(chosenHand);
            if (!chosenStack.m_41619_()) {
               ItemStack droppedStack = chosenStack.m_41777_();
               clearCachedNpcWeapon(target, chosenHand);
               target.m_21008_(chosenHand, ItemStack.f_41583_);
               if (chosenHand == InteractionHand.MAIN_HAND) {
                  tryMoveOffhandWeaponToMainhand(target);
               }

               spawnDisarmedItem(serverLevel, livingEntity, target, droppedStack, launch);
            }
         }
      }
   }

   private static void tryMoveOffhandWeaponToMainhand(LivingEntity target) {
      ItemStack offhandStack = target.m_21206_();
      if (!offhandStack.m_41619_()) {
         if (isPullableWeapon(offhandStack)) {
            ItemStack movedStack = offhandStack.m_41777_();
            target.m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
            target.m_21008_(InteractionHand.MAIN_HAND, movedStack.m_41777_());
            if (target instanceof AVNpc avNpc) {
               avNpc.setOffWeaponItem(ItemStack.f_41583_);
               avNpc.setMainWeaponItem(movedStack.m_41777_());
               avNpc.setMainWeaponDisarmed(false);
            }

            if (target instanceof PlayerNpcEntity playerNpcEntity) {
               playerNpcEntity.setOffWeaponItem(ItemStack.f_41583_);
               playerNpcEntity.setMainWeaponItem(movedStack.m_41777_());
               playerNpcEntity.setMainWeaponDisarmed(false);
            }
         }
      }
   }

   private static void clearCachedNpcWeapon(LivingEntity target, InteractionHand hand) {
      if (target instanceof AVNpc avNpc) {
         if (hand == InteractionHand.MAIN_HAND) {
            avNpc.setMainWeaponItem(ItemStack.f_41583_);
            avNpc.setMainWeaponDisarmed(true);
         } else {
            avNpc.setOffWeaponItem(ItemStack.f_41583_);
         }
      }

      if (target instanceof PlayerNpcEntity playerNpcEntity) {
         if (hand == InteractionHand.MAIN_HAND) {
            playerNpcEntity.setMainWeaponItem(ItemStack.f_41583_);
            playerNpcEntity.setMainWeaponDisarmed(true);
         } else {
            playerNpcEntity.setOffWeaponItem(ItemStack.f_41583_);
         }
      }
   }

   private static void spawnDisarmedItem(ServerLevel serverLevel, LivingEntity livingEntity, LivingEntity target, ItemStack stack, HookDisarmLaunch launch) {
      if (!stack.m_41619_()) {
         Vec3 spawnPos = EpicfightUtil.getJointWithTranslation(target, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handR, 0.0F, 0.0);
         if (spawnPos == null) {
            spawnPos = target.m_146892_().m_82492_(0.0, 0.35, 0.0);
         }

         Vec3 towardAttacker = livingEntity.m_20182_().m_82546_(target.m_20182_());
         towardAttacker = new Vec3(towardAttacker.f_82479_, 0.0, towardAttacker.f_82481_);
         if (towardAttacker.m_82556_() < 1.0E-7) {
            towardAttacker = target.m_20154_();
            towardAttacker = new Vec3(towardAttacker.f_82479_, 0.0, towardAttacker.f_82481_);
         }

         if (towardAttacker.m_82556_() < 1.0E-7) {
            towardAttacker = new Vec3(0.0, 0.0, 1.0);
         }

         towardAttacker = towardAttacker.m_82541_();
         Vec3 right = new Vec3(-towardAttacker.f_82481_, 0.0, towardAttacker.f_82479_).m_82541_();
         Vec3 motion;
         int dropAfterTicks;
         switch (launch) {
            case RIGHT:
               motion = right.m_82490_(0.72).m_82549_(towardAttacker.m_82490_(0.12)).m_82520_(0.0, 0.4, 0.0);
               dropAfterTicks = 16;
               break;
            case LEFT:
               motion = right.m_82490_(-0.72).m_82549_(towardAttacker.m_82490_(0.12)).m_82520_(0.0, 0.4, 0.0);
               dropAfterTicks = 16;
               break;
            case BACKWARD:
               Vec3 backward = towardAttacker.m_82490_(-1.0);
               spawnPos = target.m_146892_().m_82520_(0.0, 0.1, 0.0);
               motion = backward.m_82490_(0.85).m_82520_(0.0, 0.78, 0.0);
               dropAfterTicks = 22;
               break;
            default:
               motion = new Vec3(0.0, 0.45, 0.0);
               dropAfterTicks = 16;
         }

         ItemProjectile projectile = ItemProjectile.createDisarmLaunch(serverLevel, livingEntity, stack.m_41777_(), spawnPos, motion, dropAfterTicks);
         serverLevel.m_7967_(projectile);
      }
   }
}
