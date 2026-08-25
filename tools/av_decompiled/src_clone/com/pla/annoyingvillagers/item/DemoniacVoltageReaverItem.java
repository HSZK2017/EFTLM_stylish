package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.capabilities.SnakeBladeCapability;
import com.pla.annoyingvillagers.entity.PortalEntity;
import com.pla.annoyingvillagers.entity.SnakeBladeEntity;
import com.pla.annoyingvillagers.entity.SwordsmanHerobrineEntity;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModCapabilities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.util.HerobrinePortalCombatUtil;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class DemoniacVoltageReaverItem extends SwordItem {
   private static final String TAG_PREFERRED_PORTAL_GROUP = "PreferredPortalGroup";
   private static final String TAG_PREFERRED_PORTAL_OWNER = "PreferredPortalOwner";
   private static final double TARGET_SEARCH_RADIUS = 16.0;
   private static final double PORTAL_TARGET_SEARCH_RADIUS = 64.0;

   public DemoniacVoltageReaverItem() {
      super(new Tier() {
         public int m_6609_() {
            return 1561;
         }

         public float m_6624_() {
            return 4.0F;
         }

         public float m_6631_() {
            return 3.0F;
         }

         public int m_6604_() {
            return 1;
         }

         public int m_6601_() {
            return 4;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43929_(new ItemLike[]{(ItemLike)AnnoyingVillagersModItems.ELITE_OBSIDIAN.get()});
         }
      }, 3, -3.0F, new Properties());
   }

   public static boolean checkNearbyTarget(LivingEntity attacker) {
      Level level = attacker.m_9236_();
      Entity closestValid = null;
      Vec3 attackerEyes = attacker.m_20299_(1.0F);
      level.m_45547_(new ClipContext(attackerEyes, attackerEyes.m_82549_(attacker.m_20154_().m_82490_(16.0)), Block.VISUAL, Fluid.NONE, attacker));

      for (Entity entity : level.m_45976_(LivingEntity.class, attacker.m_20191_().m_82400_(16.0))) {
         if (isValidSnakeBladeTarget(attacker, entity) && (closestValid == null || attacker.m_20270_(entity) < attacker.m_20270_(closestValid))) {
            closestValid = entity;
         }
      }

      return closestValid != null || findClosestPortalTarget(attacker) != null;
   }

   public static boolean hasSnakeAnimation(ItemStack stack) {
      return stack.m_41782_() && stack.m_41783_() != null && stack.m_41783_().m_128471_("SnakeAnimation");
   }

   public static void clearSnakeAnimation(ItemStack stack) {
      if (stack.m_41782_()) {
         stack.m_41749_("SnakeAnimation");
         clearPreferredPortalTarget(stack);
      }
   }

   public static boolean tryStartSnakeAnimation(ItemStack stack, LivingEntity livingEntity, boolean guard) {
      boolean launched = guard ? processGuard(stack, livingEntity) : process(stack, livingEntity);
      if (!launched && getLastFragment(livingEntity) == null) {
         clearSnakeAnimation(stack);
         setLastFragment(livingEntity, null);
         return false;
      } else {
         stack.m_41784_().m_128379_("SnakeAnimation", true);
         return true;
      }
   }

   public static void clearInterruptedSnakeAnimation(LivingEntity livingEntity) {
      ItemStack stack = livingEntity.m_21205_();
      if (stack.m_41720_() instanceof DemoniacVoltageReaverItem && hasSnakeAnimation(stack)) {
         SnakeBladeEntity lastFragment = getLastFragment(livingEntity);
         if (lastFragment == null || !lastFragment.m_6084_() || lastFragment.m_213877_()) {
            if (!isPlayingSnakeBladeAnimation(livingEntity)) {
               clearSnakeAnimation(stack);
               setLastFragment(livingEntity, null);
            }
         }
      }
   }

   private static boolean isPlayingSnakeBladeAnimation(LivingEntity livingEntity) {
      LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingEntity, LivingEntityPatch.class);
      if (patch != null && patch.getAnimator() != null) {
         AnimationPlayer animationPlayer = patch.getAnimator().getPlayerFor(null);
         if (animationPlayer == null) {
            return false;
         } else {
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = animationPlayer.getRealAnimation();
            return dynamicAnimation == AVAnimations.SNAKE_BLADE || dynamicAnimation == AVAnimations.SNAKE_BLADE_GUARD;
         }
      } else {
         return false;
      }
   }

   public static boolean process(ItemStack stack, LivingEntity attacker) {
      Level level = attacker.m_9236_();
      Entity closestValid = findPreferredPortalTarget(stack, attacker);
      if (closestValid == null) {
         closestValid = findClosestPortalTarget(attacker);
      }

      Vec3 attackerEyes = attacker.m_20299_(1.0F);
      level.m_45547_(new ClipContext(attackerEyes, attackerEyes.m_82549_(attacker.m_20154_().m_82490_(16.0)), Block.VISUAL, Fluid.NONE, attacker));
      if (closestValid == null) {
         for (Entity entity : level.m_45976_(LivingEntity.class, attacker.m_20191_().m_82400_(16.0))) {
            if (isValidSnakeBladeTarget(attacker, entity) && (closestValid == null || attacker.m_20270_(entity) < attacker.m_20270_(closestValid))) {
               closestValid = entity;
            }
         }
      }

      return launchSnakeBladeAt(attacker, closestValid, stack);
   }

   public static void setPreferredPortalTarget(ItemStack stack, UUID portalGroupUuid, @Nullable UUID portalOwnerUuid) {
      if (portalGroupUuid == null) {
         clearPreferredPortalTarget(stack);
      } else {
         stack.m_41784_().m_128362_("PreferredPortalGroup", portalGroupUuid);
         if (portalOwnerUuid != null) {
            stack.m_41784_().m_128362_("PreferredPortalOwner", portalOwnerUuid);
         } else if (stack.m_41782_()) {
            stack.m_41783_().m_128473_("PreferredPortalOwner");
         }
      }
   }

   public static void clearPreferredPortalTarget(ItemStack stack) {
      if (stack.m_41782_()) {
         stack.m_41749_("PreferredPortalGroup");
         stack.m_41749_("PreferredPortalOwner");
      }
   }

   private static PortalEntity findPreferredPortalTarget(ItemStack stack, LivingEntity attacker) {
      if (stack.m_41782_() && stack.m_41783_().m_128403_("PreferredPortalGroup")) {
         UUID preferredGroup = stack.m_41783_().m_128342_("PreferredPortalGroup");
         UUID preferredOwner = stack.m_41783_().m_128403_("PreferredPortalOwner") ? stack.m_41783_().m_128342_("PreferredPortalOwner") : null;
         PortalEntity bestPortal = null;

         for (PortalEntity portal : attacker.m_9236_().m_45976_(PortalEntity.class, attacker.m_20191_().m_82400_(64.0))) {
            if (!portal.m_213877_()
               && preferredGroup.equals(portal.getPortalGroupUUID())
               && (preferredOwner == null || preferredOwner.equals(portal.getOwnerUUID()))
               && HerobrinePortalCombatUtil.canUsePortalOwnedBy(attacker, portal.getOwnerUUID())
               && (bestPortal == null || isBetterPreferredPortal(attacker, portal, bestPortal))) {
               bestPortal = portal;
            }
         }

         if (bestPortal != null) {
            clearPreferredPortalTarget(stack);
         }

         return bestPortal;
      } else {
         return null;
      }
   }

   private static PortalEntity findClosestPortalTarget(LivingEntity attacker) {
      Level level = attacker.m_9236_();
      PortalEntity closestPortal = null;
      UUID attackerUuid = attacker.m_20148_();

      for (PortalEntity portal : level.m_45976_(PortalEntity.class, attacker.m_20191_().m_82400_(64.0))) {
         if (!portal.m_213877_()) {
            UUID ownerUuid = portal.getOwnerUUID();
            if ((ownerUuid == null || ownerUuid.equals(attackerUuid) || HerobrinePortalCombatUtil.canUsePortalOwnedBy(attacker, ownerUuid))
               && (closestPortal == null || isBetterInitialPortal(attacker, portal, closestPortal))) {
               closestPortal = portal;
            }
         }
      }

      return closestPortal;
   }

   private static boolean isBetterInitialPortal(LivingEntity attacker, PortalEntity candidate, PortalEntity current) {
      double candidateDistance = (double)attacker.m_20270_(candidate);
      double currentDistance = (double)attacker.m_20270_(current);
      if (candidateDistance < currentDistance) {
         return true;
      } else if (candidateDistance > currentDistance) {
         return false;
      } else if (candidate.isStarterPortal() != current.isStarterPortal()) {
         return candidate.isStarterPortal();
      } else {
         int candidateOrder = candidate.getPortalOrder() < 0 ? Integer.MAX_VALUE : candidate.getPortalOrder();
         int currentOrder = current.getPortalOrder() < 0 ? Integer.MAX_VALUE : current.getPortalOrder();
         return candidateOrder != currentOrder ? candidateOrder < currentOrder : false;
      }
   }

   private static boolean isBetterPreferredPortal(LivingEntity attacker, PortalEntity candidate, PortalEntity current) {
      if (candidate.isStarterPortal() != current.isStarterPortal()) {
         return candidate.isStarterPortal();
      } else {
         int candidateOrder = candidate.getPortalOrder() < 0 ? Integer.MAX_VALUE : candidate.getPortalOrder();
         int currentOrder = current.getPortalOrder() < 0 ? Integer.MAX_VALUE : current.getPortalOrder();
         return candidateOrder != currentOrder ? candidateOrder < currentOrder : isBetterInitialPortal(attacker, candidate, current);
      }
   }

   private static boolean isValidSnakeBladeTarget(LivingEntity attacker, Entity entity) {
      if (entity.equals(attacker)
         || entity.m_5833_()
         || !(entity instanceof Mob) && !(entity instanceof Player)
         || entity instanceof Player player && player.m_7500_()
         || !attacker.m_142582_(entity)) {
         return false;
      } else {
         return HerobrinePortalCombatUtil.isHerobrineSide(attacker) && HerobrinePortalCombatUtil.isHerobrineSide(entity)
            ? false
            : !attacker.m_7307_(entity) && !entity.m_7307_(attacker);
      }
   }

   public static boolean processGuard(ItemStack stack, LivingEntity entityToGuard) {
      if (entityToGuard instanceof SwordsmanHerobrineEntity swordsmanHerobrineEntity
         && (
            swordsmanHerobrineEntity.getGregUUID() != null
                  && HerobrinePortalCombatUtil.hasNearbyPortalGroup(swordsmanHerobrineEntity, swordsmanHerobrineEntity.getGregUUID(), 6, 48.0)
               || HerobrinePortalCombatUtil.hasNearbyPortalGroup(swordsmanHerobrineEntity, null, 6, 48.0)
         )) {
         return false;
      }

      Level level = entityToGuard.m_9236_();
      SnakeBladeCapability.ISnakeBladeCapability snakeBladeCapability = AnnoyingVillagersModCapabilities.getCapability(
         entityToGuard, AnnoyingVillagersModCapabilities.SNAKE_BLADE_CAPABILITY
      );
      if (snakeBladeCapability != null && canLaunchSnakeBlades(level, entityToGuard)) {
         retractFarFragments(level, entityToGuard);
         if (!level.f_46443_) {
            return launchSnakeBladeAt(entityToGuard, stack);
         }
      }

      return false;
   }

   public static boolean launchSnakeBladeAt(LivingEntity attacker, Entity closestValid, ItemStack stack) {
      Level level = attacker.m_9236_();
      SnakeBladeCapability.ISnakeBladeCapability snakeBladeCapability = AnnoyingVillagersModCapabilities.getCapability(
         attacker, AnnoyingVillagersModCapabilities.SNAKE_BLADE_CAPABILITY
      );
      if (snakeBladeCapability != null && canLaunchSnakeBlades(level, attacker)) {
         retractFarFragments(level, attacker);
         if (!level.f_46443_ && closestValid != null) {
            SnakeBladeEntity snakeBladeEntity = (SnakeBladeEntity)((EntityType)AnnoyingVillagersModEntities.SNAKE_BLADE.get()).m_20615_(level);
            if (snakeBladeEntity != null) {
               if (stack.m_41790_()) {
                  snakeBladeEntity.setEnchanted(true);
               }

               snakeBladeEntity.m_20359_(attacker);
               level.m_7967_(snakeBladeEntity);
               snakeBladeEntity.setCreatorEntityUUID(attacker.m_20148_());
               snakeBladeEntity.setFromEntityID(attacker.m_19879_());
               snakeBladeEntity.setToEntityID(closestValid.m_19879_());
               snakeBladeEntity.m_20359_(attacker);
               snakeBladeEntity.setProgress(0.0F);
               setLastFragment(attacker, snakeBladeEntity);
               return true;
            }
         }
      }

      return false;
   }

   public static boolean launchSnakeBladeAt(LivingEntity attacker, ItemStack stack) {
      Level level = attacker.m_9236_();
      SnakeBladeEntity snakeBladeEntity = (SnakeBladeEntity)((EntityType)AnnoyingVillagersModEntities.SNAKE_BLADE.get()).m_20615_(level);
      if (snakeBladeEntity == null) {
         return false;
      } else {
         if (stack.m_41790_()) {
            snakeBladeEntity.setEnchanted(true);
         }

         snakeBladeEntity.setCreatorEntityUUID(attacker.m_20148_());
         snakeBladeEntity.setFromEntityID(attacker.m_19879_());
         snakeBladeEntity.setToEntityID(-1);
         snakeBladeEntity.setProgress(0.0F);
         snakeBladeEntity.setGuardDirection("forward_left");
         Vec3 spawn = guardTargetFor(attacker, "forward_left");
         snakeBladeEntity.m_6034_(spawn.f_82479_, spawn.f_82480_, spawn.f_82481_);
         level.m_7967_(snakeBladeEntity);
         setLastFragment(attacker, snakeBladeEntity);
         return true;
      }
   }

   public static Vec3 guardTargetFor(LivingEntity ent, String direction) {
      new Random();
      if ("forward_left".equalsIgnoreCase(direction)) {
         return DemoniacVoltageReaverItem.LocalSpace.localOffsetPos(ent, 1.0, 0.0, -1.0);
      } else if ("forward_right".equalsIgnoreCase(direction)) {
         return DemoniacVoltageReaverItem.LocalSpace.localOffsetPos(ent, 2.0, 1.0, 1.0);
      } else {
         return "backward_right".equalsIgnoreCase(direction)
            ? DemoniacVoltageReaverItem.LocalSpace.localOffsetPos(ent, -1.0, 0.0, 2.0)
            : DemoniacVoltageReaverItem.LocalSpace.localOffsetPos(ent, -1.0, 2.0, -1.0);
      }
   }

   public static void setLastFragment(LivingEntity entity, SnakeBladeEntity snakeBladeEntity) {
      SnakeBladeCapability.ISnakeBladeCapability snakeBladeCapability = AnnoyingVillagersModCapabilities.getCapability(
         entity, AnnoyingVillagersModCapabilities.SNAKE_BLADE_CAPABILITY
      );
      if (snakeBladeCapability != null) {
         snakeBladeCapability.setHasSnakeBlade(snakeBladeEntity != null);
         if (snakeBladeEntity != null) {
            snakeBladeCapability.setLastSnakeBladeID(snakeBladeEntity.m_19879_());
            snakeBladeCapability.setLastSnakeBladeUUID(snakeBladeEntity.m_20148_());
         } else {
            snakeBladeCapability.setLastSnakeBladeID(-1);
            snakeBladeCapability.setLastSnakeBladeUUID(null);
         }
      }
   }

   public static void retractFarFragments(Level level, LivingEntity livingEntity) {
      SnakeBladeEntity last = getLastFragment(livingEntity);
      if (last != null) {
         last.m_142687_(RemovalReason.DISCARDED);
         setLastFragment(livingEntity, null);
      }
   }

   public static boolean canLaunchSnakeBlades(Level level, LivingEntity livingEntity) {
      SnakeBladeEntity last = getLastFragment(livingEntity);
      return last != null ? last.m_213877_() : true;
   }

   public static SnakeBladeEntity getLastFragment(LivingEntity livingEntity) {
      SnakeBladeCapability.ISnakeBladeCapability snakeBladeCapability = AnnoyingVillagersModCapabilities.getCapability(
         livingEntity, AnnoyingVillagersModCapabilities.SNAKE_BLADE_CAPABILITY
      );
      if (snakeBladeCapability == null) {
         return null;
      } else {
         UUID uuid = snakeBladeCapability.getLastSnakeBladeUUID();
         int id = snakeBladeCapability.getLastSnakeBladeID();
         Level level = livingEntity.m_9236_();
         Entity found = null;
         if (!level.f_46443_) {
            if (uuid != null && level instanceof ServerLevel serverLevel) {
               found = serverLevel.m_8791_(uuid);
            }

            if (found == null && id != -1) {
               found = level.m_6815_(id);
            }
         } else if (id != -1) {
            found = level.m_6815_(id);
         }

         if (found instanceof SnakeBladeEntity snakeBladeEntity && found.m_6084_()) {
            return snakeBladeEntity;
         }

         return null;
      }
   }

   public static Vec3 getToolTipPos(Entity ent, float partialTicks, float handToTip) {
      LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(ent, LivingEntityPatch.class);
      if (patch == null) {
         return null;
      } else {
         OpenMatrix4f joint = patch.getArmature()
            .getBoundTransformFor(patch.getAnimator().getPose(partialTicks), ((HumanoidArmature)Armatures.BIPED.get()).toolR);
         OpenMatrix4f localOffset = new OpenMatrix4f().translate(new Vec3f(0.0F, 0.0F, -handToTip));
         OpenMatrix4f.mul(joint, localOffset, joint);
         float yawRad = (float)(-Math.toRadians((double)(((LivingEntity)ent).f_20884_ + 180.0F)));
         OpenMatrix4f worldYaw = new OpenMatrix4f().rotate(yawRad, new Vec3f(0.0F, 1.0F, 0.0F));
         OpenMatrix4f.mul(worldYaw, joint, joint);
         return new Vec3(
            (double)joint.m30 + ent.m_20185_(),
            (double)joint.m31 + (ent.m_20186_() + (double)(ent.m_20206_() / 1.8F) - 1.0),
            (double)joint.m32 + ent.m_20189_()
         );
      }
   }

   public void m_7373_(@NotNull ItemStack itemstack, Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
      list.add(Component.m_237113_(Component.m_237115_("tooltip.annoyingvillagers.demoniac_voltage_reaver").getString()));
   }

   public void m_6883_(@NotNull ItemStack itemstack, @NotNull Level level, @NotNull Entity entity, int i, boolean flag) {
      super.m_6883_(itemstack, level, entity, i, flag);
      if (flag && entity instanceof Player player) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer skillContainer = serverPlayerPatch.getSkill(AVSkills.DEMONIAC_VOLTAGE_REAVER);
            if (skillContainer != null) {
               if (skillContainer.getStack() >= 1) {
                  HerobrineUtil.spawnEliteEffect(level, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), entity);
                  if (itemstack.m_41783_() != null && !itemstack.m_41783_().m_128471_("SecondForm")) {
                     itemstack.m_41783_().m_128379_("SecondForm", true);
                  }
               } else if (skillContainer.getStack() < 1 && itemstack.m_41783_() != null && itemstack.m_41783_().m_128471_("SecondForm")) {
                  itemstack.m_41783_().m_128473_("SecondForm");
               }
            }
         }
      }

      if (entity instanceof Player && !flag && itemstack.m_41782_() && itemstack.m_41783_().m_128471_("SnakeAnimation")) {
         clearSnakeAnimation(itemstack);
      }
   }

   public static final class LocalSpace {
      private static final Vec3 UP = new Vec3(0.0, 1.0, 0.0);

      public static Vec3 forward(LivingEntity e) {
         float yawRad = e.f_20883_ * (float) (Math.PI / 180.0);
         return new Vec3((double)(-Mth.m_14031_(yawRad)), 0.0, (double)Mth.m_14089_(yawRad)).m_82541_();
      }

      public static Vec3 right(LivingEntity e) {
         Vec3 f = forward(e);
         return UP.m_82537_(f).m_82541_();
      }

      public static Vec3 left(LivingEntity e) {
         return right(e).m_82490_(-1.0);
      }

      public static Vec3 back(LivingEntity e) {
         return forward(e).m_82490_(-1.0);
      }

      public static Vec3 localOffsetPos(LivingEntity e, double leftU, double upU, double forwardU) {
         Vec3 base = e.m_20182_();
         Vec3 off = left(e).m_82490_(leftU).m_82549_(UP.m_82490_(upU)).m_82549_(forward(e).m_82490_(forwardU));
         return base.m_82549_(off);
      }
   }
}
