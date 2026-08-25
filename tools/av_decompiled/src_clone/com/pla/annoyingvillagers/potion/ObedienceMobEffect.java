package com.pla.annoyingvillagers.potion;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import org.jetbrains.annotations.NotNull;

public class ObedienceMobEffect extends MobEffect {
   private static final String OWNER_UUID_KEY = "AVObedienceOwner";
   private static final String TEAM_CAPTURED_KEY = "AVObedienceTeamCaptured";
   private static final String ORIGINAL_TEAM_KEY = "AVObedienceOriginalTeam";
   private static final String REFRESHING_KEY = "AVObedienceRefreshing";
   private static final double SEARCH_RANGE = 24.0;

   public ObedienceMobEffect() {
      super(MobEffectCategory.BENEFICIAL, 2686760);
   }

   @NotNull
   public String m_19481_() {
      return "effect.annoyingvillagers.obedience";
   }

   public void m_6742_(LivingEntity entity, int amplifier) {
      if (!entity.m_9236_().m_5776_() && entity instanceof Mob mob) {
         tickObedience(mob);
      }
   }

   public boolean m_6584_(int duration, int amplifier) {
      return true;
   }

   public void m_6386_(@NotNull LivingEntity entity, @NotNull AttributeMap attributes, int amplifier) {
      super.m_6386_(entity, attributes, amplifier);
      if (!entity.m_9236_().m_5776_() && entity instanceof Mob mob) {
         CompoundTag tag = mob.getPersistentData();
         if (tag.m_128471_("AVObedienceRefreshing")) {
            return;
         }

         restoreOriginalTeamAndClear(mob);
      }
   }

   public static boolean canBeObedientMob(Entity entity) {
      return entity instanceof Zombie
         || entity instanceof AbstractSkeleton
         || entity instanceof Creeper
         || entity instanceof Spider
         || entity instanceof AbstractPiglin
         || entity instanceof AbstractIllager
         || entity instanceof Vex;
   }

   public static void applyObedience(Mob targetMob, LivingEntity owner, int durationTicks) {
      if (!targetMob.m_9236_().m_5776_()) {
         if (canBeObedientMob(targetMob)) {
            if (targetMob.m_6084_() && owner.m_6084_()) {
               if (!targetMob.m_20148_().equals(owner.m_20148_())) {
                  CompoundTag tag = targetMob.getPersistentData();
                  boolean alreadyHasObedience = targetMob.m_21023_((MobEffect)AnnoyingVillagersModMobEffects.OBEDIENCE.get());
                  if (alreadyHasObedience) {
                     tag.m_128379_("AVObedienceRefreshing", true);
                  }

                  try {
                     captureOriginalTeamAndLeave(targetMob);
                     tag.m_128362_("AVObedienceOwner", owner.m_20148_());
                     targetMob.m_147207_(
                        new MobEffectInstance((MobEffect)AnnoyingVillagersModMobEffects.OBEDIENCE.get(), durationTicks, 0, false, true, true), owner
                     );
                     captureOriginalTeamAndLeave(targetMob);
                     tag.m_128362_("AVObedienceOwner", owner.m_20148_());
                     if (targetMob.m_5448_() == owner) {
                        targetMob.m_6710_(null);
                     }
                  } finally {
                     tag.m_128473_("AVObedienceRefreshing");
                  }
               }
            }
         }
      }
   }

   public static void tickObedience(Mob mob) {
      if (mob.m_9236_() instanceof ServerLevel serverLevel) {
         if (!canBeObedientMob(mob)) {
            mob.m_21195_((MobEffect)AnnoyingVillagersModMobEffects.OBEDIENCE.get());
         } else {
            captureOriginalTeamAndLeave(mob);
            TeamUtil.leaveCurrentTeam(mob);
            UUID ownerUuid = getOwnerUUID(mob);
            if (ownerUuid == null) {
               mob.m_21195_((MobEffect)AnnoyingVillagersModMobEffects.OBEDIENCE.get());
            } else {
               if (serverLevel.m_8791_(ownerUuid) instanceof LivingEntity owner && owner.m_6084_()) {
                  LivingEntity currentTarget = mob.m_5448_();
                  if (currentTarget != null && shouldBlockTarget(mob, currentTarget)) {
                     mob.m_6710_(null);
                  }

                  LivingEntity bestTarget = findBestTarget(mob, owner);
                  if (bestTarget != null && mob.m_5448_() != bestTarget) {
                     mob.m_6710_(bestTarget);
                  }

                  return;
               }

               mob.m_21195_((MobEffect)AnnoyingVillagersModMobEffects.OBEDIENCE.get());
            }
         }
      }
   }

   private static void captureOriginalTeamAndLeave(Mob mob) {
      CompoundTag tag = mob.getPersistentData();
      if (!tag.m_128471_("AVObedienceTeamCaptured")) {
         tag.m_128379_("AVObedienceTeamCaptured", true);
         String originalTeamName = TeamUtil.getTeamName(mob);
         if (originalTeamName != null) {
            tag.m_128359_("AVObedienceOriginalTeam", originalTeamName);
         }
      }

      TeamUtil.leaveCurrentTeam(mob);
   }

   public static void restoreOriginalTeamAndClear(Mob mob) {
      CompoundTag tag = mob.getPersistentData();
      if (tag.m_128471_("AVObedienceTeamCaptured") && tag.m_128425_("AVObedienceOriginalTeam", 8)) {
         String originalTeamName = tag.m_128461_("AVObedienceOriginalTeam");
         if (!originalTeamName.isEmpty()) {
            TeamUtil.addOrJoinTeam(mob, originalTeamName);
         }
      }

      tag.m_128473_("AVObedienceOwner");
      tag.m_128473_("AVObedienceTeamCaptured");
      tag.m_128473_("AVObedienceOriginalTeam");
      tag.m_128473_("AVObedienceRefreshing");
      if (mob.m_5448_() != null) {
         mob.m_6710_(null);
      }
   }

   @Nullable
   private static LivingEntity findBestTarget(Mob mob, LivingEntity owner) {
      LivingEntity ownerLastHurtMob = owner.m_21214_();
      if (isValidObedienceTarget(mob, owner, ownerLastHurtMob)) {
         return ownerLastHurtMob;
      } else {
         LivingEntity ownerLastHurtByMob = owner.m_21188_();
         if (isValidObedienceTarget(mob, owner, ownerLastHurtByMob)) {
            return ownerLastHurtByMob;
         } else {
            LivingEntity mobLastHurtByMob = mob.m_21188_();
            if (isValidObedienceTarget(mob, owner, mobLastHurtByMob)) {
               return mobLastHurtByMob;
            } else {
               List<Monster> nearbyMonsters = mob.m_9236_()
                  .m_6443_(Monster.class, mob.m_20191_().m_82400_(24.0), monster -> isValidObedienceTarget(mob, owner, monster));
               return (LivingEntity)nearbyMonsters.stream().min(Comparator.comparingDouble(mob::m_20280_)).orElse(null);
            }
         }
      }
   }

   private static boolean isValidObedienceTarget(Mob controlledMob, LivingEntity owner, @Nullable LivingEntity target) {
      if (target instanceof Monster targetMob) {
         if (!target.m_6084_()) {
            return false;
         } else if (target != controlledMob && target != owner) {
            UUID ownerUuid = getOwnerUUID(controlledMob);
            if (ownerUuid != null) {
               return target.m_20148_().equals(ownerUuid) ? false : !isControlledBy(targetMob, ownerUuid);
            } else {
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean shouldBlockTarget(Mob mob, LivingEntity target) {
      UUID ownerUuid = getOwnerUUID(mob);
      if (ownerUuid == null) {
         return false;
      } else if (target.m_20148_().equals(ownerUuid)) {
         return true;
      } else {
         if (target instanceof Mob targetMob && isControlledBy(targetMob, ownerUuid)) {
            return true;
         }

         return false;
      }
   }

   public static boolean isObedientMob(Entity entity) {
      if (entity instanceof Mob mob && mob.m_21023_((MobEffect)AnnoyingVillagersModMobEffects.OBEDIENCE.get()) && getOwnerUUID(mob) != null) {
         return true;
      }

      return false;
   }

   public static boolean isControlledBy(Mob mob, UUID ownerUuid) {
      UUID storedOwnerUuid = getOwnerUUID(mob);
      return storedOwnerUuid != null && storedOwnerUuid.equals(ownerUuid);
   }

   @Nullable
   public static UUID getOwnerUUID(Entity entity) {
      CompoundTag tag = entity.getPersistentData();
      return tag.m_128403_("AVObedienceOwner") ? tag.m_128342_("AVObedienceOwner") : null;
   }
}
