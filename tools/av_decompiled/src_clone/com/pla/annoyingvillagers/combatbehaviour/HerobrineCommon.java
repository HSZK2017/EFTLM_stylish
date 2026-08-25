package com.pla.annoyingvillagers.combatbehaviour;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.clazz.NullWeapon;
import com.pla.annoyingvillagers.entity.AegisHerobrineEntity;
import com.pla.annoyingvillagers.entity.ArmoredHerobrineEntity;
import com.pla.annoyingvillagers.entity.GlaiveHerobrineEntity;
import com.pla.annoyingvillagers.entity.Herobrine7Entity;
import com.pla.annoyingvillagers.entity.HerobrineChrisEntity;
import com.pla.annoyingvillagers.entity.HerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import com.pla.annoyingvillagers.entity.HerobrineGregEntity;
import com.pla.annoyingvillagers.entity.LowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.LowShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.NullEntity;
import com.pla.annoyingvillagers.entity.ReaperHerobrineEntity;
import com.pla.annoyingvillagers.entity.ShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.ShadowHerobrineEntity;
import com.pla.annoyingvillagers.entity.SledgehammerHerobrineEntity;
import com.pla.annoyingvillagers.entity.SwordsmanHerobrineEntity;
import com.pla.annoyingvillagers.entity.TransporterHerobrineCloneEntity;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.DemoniacVoltageReaverItem;
import com.pla.annoyingvillagers.item.TransporterFragmentItem;
import com.pla.annoyingvillagers.network.ClientboundHerobrinePortalFx;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.EscapeUtil;
import com.pla.annoyingvillagers.util.HerobrinePortalCombatUtil;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiFunction;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reascer.wom.gameasset.animations.weapons.AnimsAgony;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class HerobrineCommon {
   private static final double SWORDSMAN_SIX_PORTAL_RADIUS = 48.0;

   public static boolean canJump(MobPatch<?> mobpatch) {
      return ((Mob)mobpatch.getOriginal()).m_20096_() && !((Mob)mobpatch.getOriginal()).m_20159_();
   }

   public static boolean canPerformHealing(MobPatch<?> mobpatch) {
      if (!(mobpatch.getOriginal() instanceof HerobrineMob herobrineMob)) {
         return false;
      } else {
         return (
                  herobrineMob instanceof HerobrineCloneEntity
                     || herobrineMob instanceof ShadowHerobrineCloneEntity
                     || herobrineMob instanceof ArmoredHerobrineEntity
                     || herobrineMob instanceof HerobrineChrisEntity
                     || herobrineMob instanceof Herobrine7Entity
               )
               && getEntities(herobrineMob).isEmpty()
            ? false
            : !herobrineMob.isSacrificing() && !herobrineMob.isHealing() && herobrineMob.getHealingCooldown() == 0;
      }
   }

   public static boolean canSpinning(MobPatch<?> mobpatch) {
      return mobpatch.getOriginal() instanceof NullWeapon nullWeapon ? nullWeapon.isSpinning() : false;
   }

   public static boolean canSummonDarkOb(MobPatch<?> mobpatch) {
      return !(mobpatch.getOriginal() instanceof ShadowHerobrineEntity shadowHerobrineEntity)
         ? false
         : !shadowHerobrineEntity.isDarkObReady() && shadowHerobrineEntity.getSummonDarkObCooldown() == 0;
   }

   public static boolean canShootDarkOb(MobPatch<?> mobpatch) {
      return mobpatch.getOriginal() instanceof ShadowHerobrineEntity shadowHerobrineEntity ? shadowHerobrineEntity.isDarkObReady() : false;
   }

   public static boolean canPlayObsidianMachine(MobPatch<?> mobpatch) {
      return !(mobpatch.getOriginal() instanceof ShadowHerobrineEntity shadowHerobrineEntity)
         ? false
         : shadowHerobrineEntity.getState() == 2
            && shadowHerobrineEntity.getObsidianMachineGunCooldown() == 0
            && shadowHerobrineEntity.getObsidianMachineGunTick() == 0;
   }

   public static boolean canMountOrDismountDragon(MobPatch<?> mobpatch) {
      return !(mobpatch.getOriginal() instanceof ReaperHerobrineEntity reaperHerobrineEntity)
         ? false
         : reaperHerobrineEntity.getHealingHerobrineDragon() != null
            || reaperHerobrineEntity.getThunderHerobrineDragon() != null
            || reaperHerobrineEntity.getMeteoriteHerobrineDragon() != null;
   }

   public static boolean canChangeToSecondForm(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof HerobrineMob herobrineMob) {
         ItemStack item = herobrineMob.m_21205_();
         if (herobrineMob instanceof SwordsmanHerobrineEntity && item.m_41783_() != null && item.m_41783_().m_128441_("SnakeAnimation")) {
            return false;
         } else {
            if (herobrineMob instanceof ReaperHerobrineEntity reaperHerobrineEntity && reaperHerobrineEntity.getThunderHerobrineDragon() == null) {
               return false;
            }

            return !(herobrineMob instanceof HerobrineCloneEntity)
                  && !(herobrineMob instanceof ShadowHerobrineEntity)
                  && !(herobrineMob instanceof Herobrine7Entity)
                  && !(herobrineMob instanceof ArmoredHerobrineEntity)
               ? herobrineMob.getState() == 0
               : false;
         }
      } else {
         return false;
      }
   }

   public static boolean canPlaySecondFormAnimation(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof HerobrineMob herobrineMob) {
         ItemStack item = herobrineMob.m_21205_();
         return herobrineMob instanceof SwordsmanHerobrineEntity && item.m_41783_() != null && item.m_41783_().m_128441_("SnakeAnimation")
            ? false
            : herobrineMob.getState() != 0;
      } else {
         return false;
      }
   }

   public static boolean hasNearbySixPortalSupport(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof SwordsmanHerobrineEntity swordsmanHerobrineEntity && hasNearbySixPortalSupport(swordsmanHerobrineEntity)) {
         return true;
      }

      return false;
   }

   public static boolean hasNoNearbySixPortalSupport(MobPatch<?> mobpatch) {
      return !hasNearbySixPortalSupport(mobpatch);
   }

   public static boolean canPlaySecondFormGuardAnimation(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof SwordsmanHerobrineEntity swordsmanHerobrineEntity && hasNearbySixPortalSupport(swordsmanHerobrineEntity)) {
         return false;
      }

      return canPlaySecondFormAnimation(mobpatch);
   }

   public static boolean canCastMeteorite(MobPatch<?> mobpatch) {
      if (!(mobpatch.getOriginal() instanceof HerobrineMob herobrineMob)) {
         return false;
      } else {
         if (herobrineMob instanceof ReaperHerobrineEntity reaperHerobrineEntity
            && (reaperHerobrineEntity.getMeteoriteHerobrineDragon() == null || reaperHerobrineEntity.getMeteoriteHerobrineDragon().isRecallActive())) {
            return false;
         }

         return herobrineMob.getState() != 0;
      }
   }

   public static boolean canSummonNullSkeleton(MobPatch<?> mobpatch) {
      return mobpatch.getOriginal() instanceof NullEntity nullEntity ? nullEntity.isAvailableWitherSkeletonSlot() : false;
   }

   public static boolean canRespawnCrystal(MobPatch<?> mobpatch) {
      if (!(mobpatch.getOriginal() instanceof HerobrineMob herobrineMob)) {
         return false;
      } else {
         if (herobrineMob instanceof ReaperHerobrineEntity reaperHerobrineEntity
            && (reaperHerobrineEntity.getHealingHerobrineDragon() == null || !reaperHerobrineEntity.getHealingHerobrineDragon().m_20197_().isEmpty())) {
            return false;
         }

         return herobrineMob.getState() != 0;
      }
   }

   public static boolean canCastThunder(MobPatch<?> mobpatch) {
      if (!(mobpatch.getOriginal() instanceof HerobrineMob herobrineMob)) {
         return false;
      } else {
         if (herobrineMob instanceof ReaperHerobrineEntity reaperHerobrineEntity
            && (reaperHerobrineEntity.getThunderHerobrineDragon() == null || reaperHerobrineEntity.getThunderHerobrineDragon().isRecallActive())) {
            return false;
         }

         return herobrineMob.getState() != 0;
      }
   }

   public static boolean canPerformGuarding(MobPatch<?> mobpatch) {
      Entity entity = mobpatch.getOriginal();
      return !(entity instanceof HerobrineCloneEntity)
         && !(entity instanceof ShadowHerobrineCloneEntity)
         && !(entity instanceof HerobrineChrisEntity)
         && !(entity instanceof ArmoredHerobrineEntity)
         && !(entity instanceof Herobrine7Entity);
   }

   public static void performHealingAnimation(MobPatch<?> mobpatch) {
      LivingEntity entity = (LivingEntity)mobpatch.getOriginal();
      if (entity.m_9236_() instanceof ServerLevel serverLevel) {
         if (entity instanceof HerobrineMob herobrineMob) {
            herobrineMob.setHealing(true);
            List bound = getEntities(herobrineMob);
            Random random = new Random();
            Entity chosen;
            if (bound.isEmpty()) {
               double radius = 3.0 + random.nextDouble() * 3.0;
               double angle = random.nextDouble() * (Math.PI * 2);
               double dx = Math.cos(angle) * radius;
               double dz = Math.sin(angle) * radius;
               Vec3 rawPos = new Vec3(entity.m_20185_() + dx, entity.m_20186_(), entity.m_20189_() + dz);
               BlockPos xz = BlockPos.m_274561_(rawPos.f_82479_, 0.0, rawPos.f_82481_);
               int y = serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, xz).m_123342_();
               Vec3 spawnPos = new Vec3(rawPos.f_82479_, (double)y, rawPos.f_82481_);
               Entity spawned;
               if (random.nextBoolean()) {
                  LowHerobrineCloneEntity low = new LowHerobrineCloneEntity(
                     (EntityType<? extends LowHerobrineCloneEntity>)AnnoyingVillagersModEntities.LOW_HEROBRINE_CLONE.get(), serverLevel
                  );
                  low.m_7678_(spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_, herobrineMob.m_146908_(), herobrineMob.m_146909_());
                  low.setPossessedByEntity(herobrineMob);
                  low.setRenderPortal(false);
                  low.setPossessedByUuid(herobrineMob.m_20148_());
                  low.m_21557_(true);
                  TeamUtil.addOrJoinTeam(low, "herobrine");
                  serverLevel.m_7967_(low);
                  spawned = low;
               } else {
                  LowShadowHerobrineCloneEntity low = new LowShadowHerobrineCloneEntity(
                     (EntityType<LowShadowHerobrineCloneEntity>)AnnoyingVillagersModEntities.LOW_SHADOW_HEROBRINE_CLONE.get(), serverLevel
                  );
                  low.m_7678_(spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_, herobrineMob.m_146908_(), herobrineMob.m_146909_());
                  low.setPossessedByEntity(herobrineMob);
                  low.setRenderPortal(false);
                  low.setPossessedByUuid(herobrineMob.m_20148_());
                  low.m_21557_(true);
                  TeamUtil.addOrJoinTeam(low, "herobrine");
                  serverLevel.m_7967_(low);
                  spawned = low;
               }

               herobrineMob.boundPossessed(spawned);
               chosen = spawned;
            } else {
               chosen = (Entity)bound.get(random.nextInt(bound.size()));
            }

            if (chosen instanceof LowShadowHerobrineCloneEntity lowShadow) {
               if (!lowShadow.isHealing()) {
                  lowShadow.setPossessedByEntity(herobrineMob);
                  lowShadow.setPossessedByUuid(herobrineMob.m_20148_());
                  lowShadow.setSacrificing(false);
                  lowShadow.setHealing(true);
                  lowShadow.m_21557_(true);
               }
            } else {
               if (chosen instanceof LowHerobrineCloneEntity low) {
                  if (low.isHealing()) {
                     return;
                  }

                  low.setPossessedByEntity(herobrineMob);
                  low.setPossessedByUuid(herobrineMob.m_20148_());
                  low.setHealing(true);
                  low.m_21557_(true);
               }

               chosen.m_5496_((SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_UNDERSTOOD.get(), 1.0F, 1.0F);
            }
         }
      }
   }

   @NotNull
   public static List<Entity> getEntities(HerobrineMob herobrineMob) {
      List<Entity> bound = new ArrayList<>(4);
      Entity c1 = herobrineMob.getFirstPossessedHerobrine();
      Entity c2 = herobrineMob.getSecondPossessedHerobrine();
      Entity c3 = herobrineMob.getThirdPossessedHerobrine();
      Entity c4 = herobrineMob.getFourthPossessedHerobrine();
      if (c1 != null && c1.m_6084_()) {
         bound.add(c1);
      }

      if (c2 != null && c2.m_6084_()) {
         bound.add(c2);
      }

      if (c3 != null && c3.m_6084_()) {
         bound.add(c3);
      }

      if (c4 != null && c4.m_6084_()) {
         bound.add(c4);
      }

      return bound;
   }

   public static void changeToSecondForm(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof HerobrineMob herobrineMob) {
         herobrineMob.setState(1);
         herobrineMob.setSecondFormHitLeft(new Random().nextInt(2, 3));
         if (herobrineMob instanceof AegisHerobrineEntity
            || herobrineMob instanceof SwordsmanHerobrineEntity
            || herobrineMob instanceof SledgehammerHerobrineEntity
            || herobrineMob instanceof ReaperHerobrineEntity
            || herobrineMob instanceof GlaiveHerobrineEntity) {
            herobrineMob.m_5496_((SoundEvent)AnnoyingVillagersModSounds.ELITE_HEROBRINE_WEAPON_SCREAMING.get(), 0.5F, 1.0F);
         }
      }
   }

   public static void releaseWeapon(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof NullEntity nullEntity) {
         nullEntity.releaseRandomWeapons(nullEntity.getState() < 2 ? new Random().nextInt(1, 3) : new Random().nextInt(3, 5));
      }
   }

   public static void playSecondFormAnimation(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof HerobrineMob herobrineMob) {
         ItemStack item = herobrineMob.m_21205_();
         if (herobrineMob.getState() < 2) {
            herobrineMob.setSecondFormHitLeft(herobrineMob.getSecondFormHitLeft() - 1);
         }

         if (herobrineMob instanceof SwordsmanHerobrineEntity && herobrineMob.m_9236_() instanceof ServerLevel) {
            DemoniacVoltageReaverItem.tryStartSnakeAnimation(item, herobrineMob, false);
         } else if (herobrineMob instanceof ReaperHerobrineEntity reaperHerobrineEntity && herobrineMob.m_9236_() instanceof ServerLevel) {
            HerobrineDragonEntity herobrineDragonEntity = reaperHerobrineEntity.getThunderHerobrineDragon();
            if (herobrineDragonEntity != null) {
               reaperHerobrineEntity.m_5496_((SoundEvent)AnnoyingVillagersModSounds.REAPER_FIRE.get(), 1.0F, 1.0F);
               herobrineDragonEntity.shootThunderBreathAtTarget(herobrineMob.m_5448_());
            }
         }
      }
   }

   public static void playSecondFormSpecialAnimation(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof HerobrineMob herobrineMob) {
         if (herobrineMob.getState() < 2) {
            herobrineMob.setSecondFormHitLeft(herobrineMob.getSecondFormHitLeft() - 1);
         }

         if (herobrineMob instanceof ReaperHerobrineEntity reaperHerobrineEntity && herobrineMob.m_9236_() instanceof ServerLevel) {
            HerobrineDragonEntity herobrineDragonEntity = reaperHerobrineEntity.getMeteoriteHerobrineDragon();
            if (herobrineDragonEntity != null) {
               herobrineDragonEntity.shootMeteoriteAtTarget(herobrineMob.m_5448_());
            }
         }
      }
   }

   public static void playSecondFormGuardAnimation(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof HerobrineMob herobrineMob) {
         if (herobrineMob instanceof SwordsmanHerobrineEntity swordsmanHerobrineEntity && hasNearbySixPortalSupport(swordsmanHerobrineEntity)) {
            return;
         }

         ItemStack item = herobrineMob.m_21205_();
         if (herobrineMob.getState() < 2) {
            herobrineMob.setSecondFormHitLeft(herobrineMob.getSecondFormHitLeft() - 1);
         }

         if (herobrineMob instanceof SwordsmanHerobrineEntity && herobrineMob.m_9236_() instanceof ServerLevel) {
            DemoniacVoltageReaverItem.tryStartSnakeAnimation(item, herobrineMob, true);
         }
      }
   }

   public static void respawnCrystal(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof HerobrineMob herobrineMob
         && herobrineMob instanceof ReaperHerobrineEntity reaperHerobrineEntity
         && herobrineMob.m_9236_() instanceof ServerLevel serverLevel
         && reaperHerobrineEntity.getHealingHerobrineDragon() != null
         && reaperHerobrineEntity.getHealingHerobrineDragon().m_6084_()
         && reaperHerobrineEntity.getHealingHerobrineDragon().m_20197_().isEmpty()) {
         EndCrystal endCrystal = new EndCrystal(EntityType.f_20564_, serverLevel);
         endCrystal.m_6027_(
            reaperHerobrineEntity.getHealingHerobrineDragon().m_20185_(),
            reaperHerobrineEntity.getHealingHerobrineDragon().m_20186_(),
            reaperHerobrineEntity.getHealingHerobrineDragon().m_20189_()
         );
         serverLevel.m_7967_(endCrystal);
         endCrystal.m_7998_(reaperHerobrineEntity.getHealingHerobrineDragon(), true);
      }
   }

   public static void jump(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof HerobrineMob herobrineMob) {
         herobrineMob.jump();
      }
   }

   public static void giveSlowFalling(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof HerobrineMob herobrineMob) {
         herobrineMob.m_7292_(new MobEffectInstance(MobEffects.f_19591_, 60, 1));
      }
   }

   public static boolean isSupportingHerobrineEscaping(MobPatch<?> mobpatch) {
      LivingEntity caster = (LivingEntity)mobpatch.getOriginal();
      return canUseSupportPortalAction(caster)
         && canUseSupportEscapePortal(caster)
         && caster.m_9236_() instanceof ServerLevel serverLevel
         && TransporterFragmentItem.canSpawnOwnedPortals(serverLevel, caster, 2)
         && findEscapingSupportHerobrine(caster) != null;
   }

   public static void summonSupportingHerobrineEscapePortal(MobPatch<?> mobpatch) {
      LivingEntity caster = (LivingEntity)mobpatch.getOriginal();
      LivingEntity support = findEscapingSupportHerobrine(caster);
      if (support != null && spawnEscapePortalPair(caster, support) > 0) {
         markPortalSupportCaster(caster);
         setSupportEscapePortalCooldown(caster);
      }
   }

   public static boolean isSupportingHerobrineGettingShot(MobPatch<?> mobpatch) {
      LivingEntity caster = (LivingEntity)mobpatch.getOriginal();
      return canUseSupportPortalAction(caster) && canUseRangedCounterPortal(caster) && HerobrinePortalCombatUtil.canBowCounterPortalSupport(caster);
   }

   public static void summonSupportCounterPortal(MobPatch<?> mobpatch) {
      LivingEntity caster = (LivingEntity)mobpatch.getOriginal();
      if (HerobrinePortalCombatUtil.tryBowCounterPortalSupport(caster)) {
         markPortalSupportCaster(caster);
         setRangedCounterPortalCooldown(caster);
      }
   }

   public static boolean canSummon2Portal(MobPatch<?> mobpatch) {
      LivingEntity caster = (LivingEntity)mobpatch.getOriginal();
      if (caster instanceof HerobrineGregEntity greg) {
         return greg.canUseSupportPortalAction() && greg.getPortalPairCooldown() <= 0 && HerobrinePortalCombatUtil.canGregPortalSupport(greg);
      } else {
         if (caster instanceof TransporterHerobrineCloneEntity transporter
            && transporter.canUseSupportPortalAction()
            && transporter.getPortalPairCooldown() <= 0
            && HerobrinePortalCombatUtil.canTransporterPortalSupport(transporter)) {
            return true;
         }

         return false;
      }
   }

   public static void summon2Portal(MobPatch<?> mobpatch) {
      LivingEntity caster = (LivingEntity)mobpatch.getOriginal();
      if (caster instanceof HerobrineGregEntity greg) {
         if (HerobrinePortalCombatUtil.tryGregPortalSupport(greg)) {
            greg.setPortalPairCooldown();
         }
      } else if (caster instanceof TransporterHerobrineCloneEntity transporter && HerobrinePortalCombatUtil.tryTransporterPortalSupport(transporter)) {
         transporter.setPortalPairCooldown();
      }
   }

   public static boolean canDo6Portal(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof HerobrineGregEntity herobrineGregEntity
         && herobrineGregEntity.canAnswerSixPortalSupportRequest()
         && findGregSixPortalSupportTarget(herobrineGregEntity) != null) {
         return true;
      }

      return false;
   }

   public static void do6Portal(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof HerobrineGregEntity herobrineGregEntity) {
         SwordsmanHerobrineEntity swordsmanHerobrineEntity = findGregSixPortalSupportTarget(herobrineGregEntity);
         if (swordsmanHerobrineEntity == null) {
            return;
         }

         TransporterFragmentItem.PortalSpawnBatch portalBatch = TransporterFragmentItem.spawnPortalPairsBatch(
            herobrineGregEntity.m_9236_(), herobrineGregEntity, swordsmanHerobrineEntity
         );
         if (portalBatch.spawned() <= 0) {
            return;
         }

         if (portalBatch.portalGroup() != null) {
            DemoniacVoltageReaverItem.setPreferredPortalTarget(swordsmanHerobrineEntity.m_21205_(), portalBatch.portalGroup(), herobrineGregEntity.m_20148_());
         }

         herobrineGregEntity.markSupportingHerobrine();
         herobrineGregEntity.m_21563_().m_24960_(swordsmanHerobrineEntity, 30.0F, 30.0F);
         herobrineGregEntity.setSixPortalSupportCooldown();
      }
   }

   public static boolean canPerformPortalEscapeStepBack(MobPatch<?> mobpatch) {
      LivingEntity livingEntity = (LivingEntity)mobpatch.getOriginal();
      return canUseSupportPortalAction(livingEntity)
         && canUsePortalEscapeStepBack(livingEntity)
         && livingEntity.m_9236_() instanceof ServerLevel serverLevel
         && TransporterFragmentItem.canSpawnOwnedPortals(serverLevel, livingEntity, 2);
   }

   public static void performPortalEscapeStepBack(final MobPatch<?> mobpatch) {
      final LivingEntity livingEntity = (LivingEntity)mobpatch.getOriginal();
      if (livingEntity.m_9236_() instanceof ServerLevel serverLevel
         && TransporterFragmentItem.canSpawnOwnedPortals(serverLevel, livingEntity, 2)
         && spawnEscapePortalPair(livingEntity, livingEntity) > 0) {
         setPortalEscapeStepBackCooldown(livingEntity);
         if (livingEntity instanceof Mob mob) {
            mob.m_21573_().m_26573_();
            mob.m_6710_(null);
         }

         livingEntity.m_6858_(false);
         new DelayedTask(10) {
            @Override
            public void run() {
               if (livingEntity.m_6084_()) {
                  mobpatch.playAnimationSynchronized(Animations.BIPED_STEP_BACKWARD, 0.0F);
                  HerobrineCommon.pushStepBackIntoPortal(livingEntity, 0.65);
                  new DelayedTask(2) {
                     @Override
                     public void run() {
                        if (livingEntity.m_6084_()) {
                           HerobrineCommon.pushStepBackIntoPortal(livingEntity, 0.35);
                        }
                     }
                  };
               }
            }
         };
         return;
      }
   }

   public static boolean canSummonLowCloneSupport(MobPatch<?> mobpatch) {
      LivingEntity caster = (LivingEntity)mobpatch.getOriginal();
      if (!caster.m_20096_()) {
         return false;
      } else if (caster instanceof HerobrineGregEntity greg) {
         return greg.canSummonLowCloneSupport() && findGregLowCloneSupportEnemy(greg) != null;
      } else {
         if (caster instanceof TransporterHerobrineCloneEntity transporter
            && transporter.canSummonLowCloneSupport()
            && canFindTransporterLowCloneSupportSpawn(transporter)) {
            return true;
         }

         return false;
      }
   }

   public static void summonLowCloneSupport(MobPatch<?> mobpatch) {
      LivingEntity caster = (LivingEntity)mobpatch.getOriginal();
      if (caster instanceof HerobrineGregEntity greg) {
         if (greg.m_9236_() instanceof ServerLevel serverLevel) {
            LivingEntity var14 = greg.findGregFollowSupportHerobrine();
            LivingEntity var15 = findGregLowCloneSupportEnemy(greg);
            if (var14 != null && var15 != null) {
               int availableSlots = greg.getAvailableCombatLowCloneSupportSlotCount();
               int count = Math.min(1 + greg.m_217043_().m_188503_(3), availableSlots);
               int spawned = 0;

               for (int i = 0; i < count && greg.hasAvailableCombatLowCloneSupportSlot(); i++) {
                  if (spawnGregCombatLowCloneNear(serverLevel, greg, var14, var15)) {
                     spawned++;
                  }
               }

               if (spawned > 0) {
                  greg.markSupportingHerobrine();
                  HerobrinePortalCombatUtil.playClonePortalSummon(greg);
                  greg.setLowCloneSupportCooldown();
               }
            }
         }
      } else {
         if (caster instanceof TransporterHerobrineCloneEntity transporter) {
            if (!(transporter.m_9236_() instanceof ServerLevel serverLevelx)) {
               return;
            }

            int var12 = transporter.getAvailableCombatLowCloneSupportSlotCount();
            int count = Math.min(1 + transporter.m_217043_().m_188503_(3), var12);
            int spawned = 0;

            for (int ix = 0; ix < count && transporter.hasAvailableCombatLowCloneSupportSlot(); ix++) {
               if (spawnTransporterLowClone(serverLevelx, transporter)) {
                  spawned++;
               }
            }

            if (spawned > 0) {
               HerobrinePortalCombatUtil.playClonePortalSummon(transporter);
               transporter.setLowCloneSupportCooldown();
            }
         }
      }
   }

   private static boolean canUseSupportPortalAction(LivingEntity caster) {
      if (caster instanceof HerobrineGregEntity greg) {
         return greg.canUseSupportPortalAction();
      } else {
         if (caster instanceof TransporterHerobrineCloneEntity transporter && transporter.canUseSupportPortalAction()) {
            return true;
         }

         return false;
      }
   }

   private static boolean canUseSupportEscapePortal(LivingEntity caster) {
      if (caster instanceof HerobrineGregEntity greg) {
         return greg.getSupportEscapePortalCooldown() <= 0;
      } else {
         if (caster instanceof TransporterHerobrineCloneEntity transporter && transporter.getSupportEscapePortalCooldown() <= 0) {
            return true;
         }

         return false;
      }
   }

   private static void setSupportEscapePortalCooldown(LivingEntity caster) {
      if (caster instanceof HerobrineGregEntity greg) {
         greg.setSupportEscapePortalCooldown();
      } else if (caster instanceof TransporterHerobrineCloneEntity transporter) {
         transporter.setSupportEscapePortalCooldown();
      }
   }

   private static boolean canUseRangedCounterPortal(LivingEntity caster) {
      if (caster instanceof HerobrineGregEntity greg) {
         return greg.getRangedCounterPortalCooldown() <= 0;
      } else {
         if (caster instanceof TransporterHerobrineCloneEntity transporter && transporter.getRangedCounterPortalCooldown() <= 0) {
            return true;
         }

         return false;
      }
   }

   private static void setRangedCounterPortalCooldown(LivingEntity caster) {
      if (caster instanceof HerobrineGregEntity greg) {
         greg.setRangedCounterPortalCooldown();
      } else if (caster instanceof TransporterHerobrineCloneEntity transporter) {
         transporter.setRangedCounterPortalCooldown();
      }
   }

   private static boolean canUsePortalEscapeStepBack(LivingEntity caster) {
      if (caster instanceof HerobrineGregEntity greg) {
         return greg.getPortalEscapeStepBackCooldown() <= 0;
      } else {
         if (caster instanceof TransporterHerobrineCloneEntity transporter && transporter.getPortalEscapeStepBackCooldown() <= 0) {
            return true;
         }

         return false;
      }
   }

   private static void setPortalEscapeStepBackCooldown(LivingEntity caster) {
      if (caster instanceof HerobrineGregEntity greg) {
         greg.setPortalEscapeStepBackCooldown();
      } else if (caster instanceof TransporterHerobrineCloneEntity transporter) {
         transporter.setPortalEscapeStepBackCooldown();
      }
   }

   private static void markPortalSupportCaster(LivingEntity caster) {
      if (caster instanceof HerobrineGregEntity greg) {
         greg.markSupportingHerobrine();
      }
   }

   @Nullable
   private static LivingEntity findEscapingSupportHerobrine(LivingEntity caster) {
      if (caster instanceof HerobrineGregEntity greg) {
         return greg.findEscapingSupportHerobrine();
      } else {
         for (LivingEntity support : HerobrinePortalCombatUtil.findSupportHerobrines(caster, 40.0)) {
            if (support instanceof Mob mob
               && support.m_6084_()
               && (!support.m_20159_() || !(support.m_20202_() instanceof HerobrineDragonEntity))
               && mob.m_5448_() != null
               && mob.m_5448_().m_6084_()
               && EscapeUtil.checkEscape(mob)) {
               return support;
            }
         }

         return null;
      }
   }

   private static int spawnEscapePortalPair(LivingEntity caster, LivingEntity portalUser) {
      Vec3 entrance = getPortalBehind(portalUser, 1.75);
      Vec3 exit = getRandomPortalEscapeExit(caster.m_9236_() instanceof ServerLevel serverLevel ? serverLevel : null, portalUser);
      return TransporterFragmentItem.spawnLinkedPortalPair(caster.m_9236_(), caster, entrance, exit);
   }

   private static Vec3 getPortalBehind(LivingEntity livingEntity, double distance) {
      double yawRad = Math.toRadians((double)livingEntity.m_146908_());
      double x = livingEntity.m_20185_() + Math.sin(yawRad) * distance;
      double z = livingEntity.m_20189_() - Math.cos(yawRad) * distance;
      return new Vec3(x, livingEntity.m_20186_(), z);
   }

   private static Vec3 getRandomPortalEscapeExit(ServerLevel serverLevel, LivingEntity anchor) {
      Random random = new Random(anchor.m_217043_().m_188505_());
      if (serverLevel != null) {
         for (int attempt = 0; attempt < 16; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double distance = 9.0 + random.nextDouble() * 2.0;
            double x = anchor.m_20185_() + Math.cos(angle) * distance;
            double z = anchor.m_20189_() + Math.sin(angle) * distance;
            BlockPos surface = serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.m_274561_(x, anchor.m_20186_(), z));
            int yOffset = random.nextInt(3);
            BlockPos portalPos = surface.m_6630_(yOffset);
            if (serverLevel.m_46749_(portalPos)
               && serverLevel.m_6857_().m_61937_(portalPos)
               && serverLevel.m_46859_(portalPos)
               && serverLevel.m_46859_(portalPos.m_7494_())) {
               return new Vec3((double)portalPos.m_123341_() + 0.5, (double)portalPos.m_123342_(), (double)portalPos.m_123343_() + 0.5);
            }
         }
      }

      double angle = random.nextDouble() * Math.PI * 2.0;
      return anchor.m_20182_().m_82520_(Math.cos(angle) * 10.0, (double)random.nextInt(3), Math.sin(angle) * 10.0);
   }

   private static void pushStepBackIntoPortal(LivingEntity livingEntity, double strength) {
      double yawRad = Math.toRadians((double)livingEntity.m_146908_());
      Vec3 backward = new Vec3(Math.sin(yawRad), 0.0, -Math.cos(yawRad)).m_82490_(strength);
      livingEntity.m_20256_(livingEntity.m_20184_().m_82520_(backward.f_82479_, 0.0, backward.f_82481_));
      livingEntity.f_19812_ = true;
   }

   @Nullable
   private static LivingEntity findGregLowCloneSupportEnemy(HerobrineGregEntity greg) {
      LivingEntity support = greg.findGregFollowSupportHerobrine();
      if (support != null && support.m_6084_() && (!support.m_20159_() || !(support.m_20202_() instanceof HerobrineDragonEntity))) {
         LivingEntity enemy = HerobrinePortalCombatUtil.findThreateningEnemy(greg, support, 48.0);
         return enemy != null ? enemy : HerobrinePortalCombatUtil.findEnemyForSupport(support, greg.m_5448_(), 48.0);
      } else {
         return null;
      }
   }

   private static boolean spawnGregCombatLowCloneNear(ServerLevel serverLevel, HerobrineGregEntity greg, Entity anchor, LivingEntity enemy) {
      if (!greg.hasAvailableCombatLowCloneSupportSlot()) {
         return false;
      } else {
         RandomSource random = greg.m_217043_();

         for (int attempt = 0; attempt < 24; attempt++) {
            double angle = random.m_188500_() * Math.PI * 2.0;
            double radius = 2.5 + random.m_188500_() * 5.5;
            double x = anchor.m_20185_() + Math.cos(angle) * radius;
            double z = anchor.m_20189_() + Math.sin(angle) * radius;
            int y = serverLevel.m_6924_(Types.MOTION_BLOCKING_NO_LEAVES, Mth.m_14107_(x), Mth.m_14107_(z));
            BlockPos spawnPos = BlockPos.m_274561_(x, (double)y, z);
            if (isValidCombatLowCloneSpawn(serverLevel, spawnPos)) {
               Mob clone = createCombatLowClone(serverLevel, random.m_188499_());
               clone.m_7678_(x, (double)y, z, greg.m_146908_(), greg.m_146909_());
               if (serverLevel.m_45786_(clone)) {
                  configureCombatLowClone(clone);
                  equipLowCloneGear(clone, random);
                  clone.m_6710_(enemy);
                  clone.m_7618_(Anchor.EYES, enemy.m_146892_());
                  clone.m_6518_(serverLevel, serverLevel.m_6436_(spawnPos), MobSpawnType.MOB_SUMMONED, null, null);
                  if (!serverLevel.m_7967_(clone)) {
                     return false;
                  }

                  if (!greg.claimCombatLowCloneSupportSlot(clone)) {
                     clone.m_146870_();
                     return false;
                  }

                  AnnoyingVillagers.PACKET_HANDLER
                     .send(PacketDistributor.TRACKING_ENTITY.with(() -> clone), new ClientboundHerobrinePortalFx(new Vec3(x, (double)y, z)));
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static boolean canFindTransporterLowCloneSupportSpawn(TransporterHerobrineCloneEntity transporter) {
      if (transporter.m_9236_() instanceof ServerLevel serverLevel) {
         return findTransporterLowCloneEnemy(transporter) == null ? false : findTransporterLowCloneSpawnPosition(transporter, serverLevel) != null;
      } else {
         return false;
      }
   }

   private static boolean spawnTransporterLowClone(ServerLevel serverLevel, TransporterHerobrineCloneEntity transporter) {
      if (!transporter.hasAvailableCombatLowCloneSupportSlot()) {
         return false;
      } else {
         Vec3 spawnPos = findTransporterLowCloneSpawnPosition(transporter, serverLevel);
         if (spawnPos == null) {
            return false;
         } else {
            Mob clone = createCombatLowClone(serverLevel, transporter.m_217043_().m_188499_());
            clone.m_7678_(spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_, transporter.m_146908_(), transporter.m_146909_());
            if (!serverLevel.m_45786_(clone)) {
               return false;
            } else {
               clone.m_7618_(Anchor.EYES, transporter.m_146892_());
               configureCombatLowClone(clone);
               equipLowCloneGear(clone, transporter.m_217043_());
               LivingEntity enemy = findTransporterLowCloneEnemy(transporter);
               if (enemy != null && enemy.m_6084_()) {
                  clone.m_6710_(enemy);
                  clone.m_7618_(Anchor.EYES, enemy.m_146892_());
               }

               clone.m_6518_(serverLevel, serverLevel.m_6436_(clone.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
               if (!serverLevel.m_7967_(clone)) {
                  return false;
               } else if (!transporter.claimCombatLowCloneSupportSlot(clone)) {
                  clone.m_146870_();
                  return false;
               } else {
                  TeamUtil.addOrJoinTeam(clone, "herobrine");
                  AnnoyingVillagers.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> clone), new ClientboundHerobrinePortalFx(spawnPos));
                  return true;
               }
            }
         }
      }
   }

   @Nullable
   private static LivingEntity findTransporterLowCloneEnemy(TransporterHerobrineCloneEntity transporter) {
      LivingEntity enemy = HerobrinePortalCombatUtil.findThreateningEnemy(transporter, null, 32.0);
      return enemy != null ? enemy : HerobrinePortalCombatUtil.findEnemyForSupport(transporter, null, 32.0);
   }

   @Nullable
   private static Vec3 findTransporterLowCloneSpawnPosition(TransporterHerobrineCloneEntity transporter, ServerLevel serverLevel) {
      RandomSource random = transporter.m_217043_();

      for (int attempt = 0; attempt < 32; attempt++) {
         double angle = random.m_188500_() * Math.PI * 2.0;
         double distance = 3.0 + random.m_188500_() * 7.0;
         double x = transporter.m_20185_() + Math.cos(angle) * distance;
         double z = transporter.m_20189_() + Math.sin(angle) * distance;
         int groundX = Mth.m_14107_(x);
         int groundZ = Mth.m_14107_(z);
         int y = serverLevel.m_6924_(Types.MOTION_BLOCKING_NO_LEAVES, groundX, groundZ);
         BlockPos surface = BlockPos.m_274561_((double)groundX, (double)y, (double)groundZ);
         if (isValidCombatLowCloneSpawn(serverLevel, surface)) {
            return new Vec3(x, (double)surface.m_123342_(), z);
         }
      }

      return null;
   }

   private static boolean isValidCombatLowCloneSpawn(ServerLevel serverLevel, BlockPos spawnPos) {
      return serverLevel.m_46749_(spawnPos)
         && serverLevel.m_6857_().m_61937_(spawnPos)
         && serverLevel.m_46859_(spawnPos)
         && serverLevel.m_46859_(spawnPos.m_7494_())
         && !serverLevel.m_46859_(spawnPos.m_7495_());
   }

   private static Mob createCombatLowClone(ServerLevel serverLevel, boolean shadowClone) {
      return (Mob)(shadowClone
         ? new LowShadowHerobrineCloneEntity(
            (EntityType<LowShadowHerobrineCloneEntity>)AnnoyingVillagersModEntities.LOW_SHADOW_HEROBRINE_CLONE.get(), serverLevel
         )
         : new LowHerobrineCloneEntity((EntityType<? extends LowHerobrineCloneEntity>)AnnoyingVillagersModEntities.LOW_HEROBRINE_CLONE.get(), serverLevel));
   }

   private static void configureCombatLowClone(Mob clone) {
      if (clone instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity) {
         lowHerobrineCloneEntity.setSummoned(true);
         lowHerobrineCloneEntity.setRenderPortal(false);
      } else if (clone instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity) {
         lowShadowHerobrineCloneEntity.setSummoned(true);
         lowShadowHerobrineCloneEntity.setRenderPortal(false);
      }
   }

   private static void equipLowCloneGear(Mob clone, RandomSource random) {
      if (random.m_188501_() < 0.3F) {
         clone.m_8061_(EquipmentSlot.HEAD, damageRandomly(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_HELMET.get()), random));
      }

      if (random.m_188501_() < 0.3F) {
         clone.m_8061_(EquipmentSlot.CHEST, damageRandomly(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_CHESTPLATE.get()), random));
      }

      if (random.m_188501_() < 0.3F) {
         clone.m_8061_(EquipmentSlot.LEGS, damageRandomly(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_LEGGINGS.get()), random));
      }

      if (random.m_188501_() < 0.3F) {
         clone.m_8061_(EquipmentSlot.FEET, damageRandomly(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_BOOTS.get()), random));
      }

      clone.m_8061_(
         EquipmentSlot.MAINHAND,
         damageRandomly(new ItemStack((ItemLike)HerobrineGregEntity.listWeapons.get(random.m_188503_(HerobrineGregEntity.listWeapons.size()))), random)
      );
   }

   private static ItemStack damageRandomly(ItemStack itemStack, RandomSource random) {
      if (!itemStack.m_41763_()) {
         return itemStack;
      } else {
         int maxDamage = itemStack.m_41776_();
         itemStack.m_41721_(random.m_216339_(Math.max(1, maxDamage / 3), Math.max(2, maxDamage * 3 / 4)));
         return itemStack;
      }
   }

   private static boolean hasNearbySixPortalSupport(SwordsmanHerobrineEntity swordsmanHerobrineEntity) {
      UUID gregUuid = swordsmanHerobrineEntity.getGregUUID();
      return gregUuid != null && HerobrinePortalCombatUtil.hasNearbyPortalGroup(swordsmanHerobrineEntity, gregUuid, 6, 48.0)
         ? true
         : HerobrinePortalCombatUtil.hasNearbyPortalGroup(swordsmanHerobrineEntity, null, 6, 48.0);
   }

   private static SwordsmanHerobrineEntity findGregSixPortalSupportTarget(HerobrineGregEntity herobrineGregEntity) {
      if (herobrineGregEntity.m_9236_() instanceof ServerLevel serverLevel && TransporterFragmentItem.canSpawnOwnedPortals(serverLevel, herobrineGregEntity, 6)
         )
       {
         for (LivingEntity support : HerobrinePortalCombatUtil.findSupportHerobrines(herobrineGregEntity, 40.0)) {
            if (support instanceof SwordsmanHerobrineEntity swordsmanHerobrineEntity
               && canUseGregSixPortalSupport(herobrineGregEntity, swordsmanHerobrineEntity)) {
               return swordsmanHerobrineEntity;
            }
         }

         return null;
      }

      return null;
   }

   private static boolean canUseGregSixPortalSupport(HerobrineGregEntity herobrineGregEntity, SwordsmanHerobrineEntity swordsmanHerobrineEntity) {
      return swordsmanHerobrineEntity.m_6084_()
         && swordsmanHerobrineEntity.getState() > 0
         && swordsmanHerobrineEntity.m_5448_() != null
         && swordsmanHerobrineEntity.m_5448_().m_6084_()
         && swordsmanHerobrineEntity.m_21205_().m_150930_((Item)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER.get())
         && !DemoniacVoltageReaverItem.hasSnakeAnimation(swordsmanHerobrineEntity.m_21205_())
         && !hasNearbySixPortalSupport(swordsmanHerobrineEntity)
         && (swordsmanHerobrineEntity.getGregUUID() == null || swordsmanHerobrineEntity.getGregUUID().equals(herobrineGregEntity.m_20148_()));
   }

   public static void performEscapeRunAwayWithLowClone(final MobPatch<?> mobpatch) {
      final Mob mob = (Mob)mobpatch.getOriginal();
      if (mob.m_9236_() instanceof ServerLevel serverLevel) {
         CombatCommon.performEscapeRunAway(mobpatch);
         if (mob.f_19797_ % 10 == 0) {
            new DelayedTask(1) {
               @Override
               public void run() {
                  if (mob.m_6084_()) {
                     mobpatch.playAnimationSynchronized(AnimsEpicFightIronSpell.CASTING_ONE_HAND_INWARD, 0.0F);
                     LivingEntity target = mob.m_5448_();
                     Direction dir = target != null
                        ? Direction.m_122366_(target.m_20185_() - mob.m_20185_(), 0.0, target.m_20189_() - mob.m_20189_())
                        : mob.m_6350_();
                     Random random = new Random();
                     int dist = 1 + random.nextInt(3);
                     int rot = random.nextInt(4);
                     BiFunction<Integer, Integer, int[]> toWorld = CombatCommon.getIntegerIntegerBiFunction(mob, rot);
                     int lateral = random.nextInt(3) - 1;
                     int[] dxz = toWorld.apply(lateral, 0);
                     BlockPos baseXZ = mob.m_20183_().m_5484_(dir, dist).m_7918_(dxz[0], 0, dxz[1]);
                     int surfaceY = serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, baseXZ).m_123342_();
                     BlockPos spawnPos = new BlockPos(baseXZ.m_123341_(), surfaceY, baseXZ.m_123343_());
                     LowShadowHerobrineCloneEntity clone = new LowShadowHerobrineCloneEntity(
                        (EntityType<LowShadowHerobrineCloneEntity>)AnnoyingVillagersModEntities.LOW_SHADOW_HEROBRINE_CLONE.get(), serverLevel
                     );
                     float yaw = dir.m_122435_();
                     clone.m_7678_((double)spawnPos.m_123341_() + 0.5, (double)spawnPos.m_123342_(), (double)spawnPos.m_123343_() + 0.5, yaw, 0.0F);
                     clone.setRenderPortal(false);
                     clone.setForEscaping(true);
                     clone.m_21557_(true);
                     if (mob instanceof HerobrineMob herobrineMob) {
                        clone.setPossessedByEntity(herobrineMob);
                        clone.setPossessedByUuid(herobrineMob.m_20148_());
                     }

                     serverLevel.m_7967_(clone);
                  }
               }
            };
         }
      }
   }

   public static void performAgonySpecialAttack(final MobPatch<?> mobpatch) {
      Entity entity = mobpatch.getOriginal();
      if (entity instanceof HerobrineMob) {
         new DelayedTask(10) {
            @Override
            public void run() {
               mobpatch.playAnimationSynchronized(AnimsAgony.AGONY_RIPPING_FANGS, 0.0F);
            }
         };
      }
   }

   public static void performSpinning(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof NullWeapon nullWeapon) {
         nullWeapon.setSpinning(false);
      }
   }

   public static void performGuardWeaponSpinning(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof NullEntity nullEntity) {
         nullEntity.setSpinningToAllWeaponsAvailable(true);
      }
   }

   public static void mountOrDismountDragon(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof HerobrineMob herobrineMob && herobrineMob instanceof ReaperHerobrineEntity reaperHerobrineEntity) {
         if (reaperHerobrineEntity.m_20159_()) {
            reaperHerobrineEntity.m_8127_();
         } else if (reaperHerobrineEntity.getThunderHerobrineDragon() != null) {
            reaperHerobrineEntity.getThunderHerobrineDragon().recallAndLand(true);
         } else if (reaperHerobrineEntity.getMeteoriteHerobrineDragon() != null) {
            reaperHerobrineEntity.getMeteoriteHerobrineDragon().recallAndLand(true);
         }
      }
   }

   public static void performSummonDarkOb(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof ShadowHerobrineEntity shadowHerobrineEntity) {
         shadowHerobrineEntity.spawnDarkObEntities();
      }
   }

   public static void performShootDarkOb(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof ShadowHerobrineEntity shadowHerobrineEntity) {
         shadowHerobrineEntity.shootDarkObsAtTarget(2.0);
      }
   }

   public static void performObsidianMachine(MobPatch<?> mobpatch) {
      if (mobpatch.getOriginal() instanceof ShadowHerobrineEntity shadowHerobrineEntity) {
         shadowHerobrineEntity.m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
         shadowHerobrineEntity.m_21008_(InteractionHand.MAIN_HAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.HEROBRINE_ENDER_EYE.get()));
         if (shadowHerobrineEntity.m_9236_() instanceof ServerLevel) {
            shadowHerobrineEntity.m_5496_((SoundEvent)AnnoyingVillagersModSounds.SHADOW_HEROBRINE_SAY_OBSIDIAN_MACHINE_GUN.get(), 1.0F, 1.0F);
         }
      }
   }
}
