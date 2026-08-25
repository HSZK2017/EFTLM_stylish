package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.BurstProtectEntity;
import com.pla.annoyingvillagers.clazz.CombatVoiceLineEntity;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.clazz.SauceType;
import com.pla.annoyingvillagers.combatbehaviour.CombatCommon;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.entity.goal.KeepPositionGoal;
import com.pla.annoyingvillagers.entity.goal.RetargetCloserThreatGoal;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFight;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.BlueDemonChestplateItem;
import com.pla.annoyingvillagers.item.BlueDemonTridentItem;
import com.pla.annoyingvillagers.spawnhandler.BluedemonData;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.BlueDemonUtil;
import com.pla.annoyingvillagers.util.CombatBehaviour;
import com.pla.annoyingvillagers.util.CommonGoals;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class BlueDemonEntity extends Monster implements BurstProtectEntity, CombatVoiceLineEntity {
   private static final float WATER_SWIM_ACCELERATION = 0.08F;
   private static final double WATER_SWIM_HORIZONTAL_SPEED = 0.42;
   @Nullable
   private BbqEntity bbqSauce;
   @Nullable
   private UUID bbqSauceUUID;
   @Nullable
   private BbqEntity honeyMustardSauce;
   @Nullable
   private UUID honeyMustardSauceUUID;
   @Nullable
   private BbqEntity soySauce;
   @Nullable
   private UUID soySauceUUID;
   @Nullable
   private BbqEntity sweetOnionSauce;
   @Nullable
   private UUID sweetOnionSauceUUID;
   private int bbqResolveCooldown;
   private int bbqOrderCooldown;
   private int bbqHeadAttackCooldown;
   private int bbqModeCooldown;
   private int healingTick = 0;
   private int healingCooldown = 0;
   private int stunEscapeCooldown = 0;
   private Entity blockDamage = null;
   private int swapWeaponCooldown;
   private int efnGuardHitState = 0;
   private int efnGuardHitCooldown = 0;
   private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.m_135353_(BlueDemonEntity.class, EntityDataSerializers.f_135028_);
   private int stateTransformCooldown = -1;
   @Nullable
   private UUID savedTargetUUID;
   private int squadArrivalTicks = -1;
   private float sauceSquadAngle = 0.0F;
   private boolean spawnedBbqSauce = false;
   private int dieTick = -1;
   @Nullable
   private UUID savedKillerUUID;
   private boolean neverLeave = false;
   private int leaveTicks = 0;
   private Vec3 leaveDirection = Vec3.f_82478_;
   private int voiceCooldown = 0;
   protected float recentDamageTaken = 0.0F;
   protected int recentHitCounter = 0;

   @Override
   public int getVoiceCooldown() {
      return this.voiceCooldown;
   }

   @Override
   public void setVoiceCooldown(int cooldown) {
      this.voiceCooldown = cooldown;
   }

   @Override
   public float getRecentDamageTaken() {
      return this.recentDamageTaken;
   }

   @Override
   public void setRecentDamageTaken(float value) {
      this.recentDamageTaken = value;
   }

   @Override
   public int getRecentHitCounter() {
      return this.recentHitCounter;
   }

   @Override
   public void setRecentHitCounter(int value) {
      this.recentHitCounter = value;
   }

   public void setLeaveTicks(int leaveTicks) {
      this.leaveTicks = leaveTicks;
   }

   public int getLeaveTicks() {
      return this.leaveTicks;
   }

   public void setNeverLeave(boolean neverLeave) {
      this.neverLeave = neverLeave;
   }

   public void setStateTransformCooldown(int stateTransformCooldown) {
      this.stateTransformCooldown = stateTransformCooldown;
      if (stateTransformCooldown > 0) {
         this.sauceSquadAngle = this.f_19796_.m_188501_() * (float) (Math.PI * 2);
      }
   }

   private double getSauceLaneOffset(SauceType sauceType) {
      return switch (sauceType) {
         case BBQ_SAUCE -> -2.25;
         case HONEY_MUSTARD_SAUCE -> -0.75;
         case SOY_SAUCE -> 0.75;
         case SWEET_ONION_SAUCE -> 2.25;
      };
   }

   public float getSauceSquadAngle() {
      return this.sauceSquadAngle;
   }

   public int getEfnGuardHitState() {
      return this.efnGuardHitState;
   }

   public void postPlayEfnGuardHit() {
      if (this.efnGuardHitState == 2) {
         this.efnGuardHitState = 0;
      } else {
         this.efnGuardHitState++;
      }

      this.efnGuardHitCooldown = 100;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(STATE, 0);
   }

   public int getStunEscapeCooldown() {
      return this.stunEscapeCooldown;
   }

   public void setStunEscapeCooldown(int stunEscapeCooldown) {
      this.stunEscapeCooldown = stunEscapeCooldown;
   }

   public void setBlockDamage(Entity blockDamage) {
      this.blockDamage = blockDamage;
   }

   public Entity getBlockDamage() {
      return this.blockDamage;
   }

   public int getSwapWeaponCooldown() {
      return this.swapWeaponCooldown;
   }

   public void setSwapWeaponCooldown(int swapWeaponCooldown) {
      this.swapWeaponCooldown = swapWeaponCooldown;
   }

   public int getHealingTick() {
      return this.healingTick;
   }

   public void setHealingTick(int healingTick) {
      this.healingTick = healingTick;
   }

   public void setHealingCooldown() {
      this.healingCooldown = this.f_19796_.m_216339_(900, 1500);
   }

   public int getHealingCooldown() {
      return this.healingCooldown;
   }

   public boolean isSauceArrivalPending() {
      return this.squadArrivalTicks > 0;
   }

   private void backupCurrentTarget() {
      LivingEntity target = this.m_5448_();
      this.savedTargetUUID = target != null && target.m_6084_() ? target.m_20148_() : null;
   }

   private void restoreBackedUpTarget() {
      if (this.m_9236_() instanceof ServerLevel serverLevel && this.savedTargetUUID != null) {
         if (serverLevel.m_8791_(this.savedTargetUUID) instanceof LivingEntity living && living.m_6084_()) {
            this.m_6710_(living);
         }

         this.savedTargetUUID = null;
         return;
      }
   }

   private boolean isLeavingNow() {
      return !this.neverLeave && this.leaveTicks > 0 && this.leaveTicks <= 40;
   }

   private void updateLeaveDirectionFromThreat() {
      LivingEntity target = this.m_5448_();
      Vec3 away;
      if (target != null && target.m_6084_()) {
         away = this.m_20182_().m_82546_(target.m_20182_());
      } else if (this.m_21188_() != null && this.m_21188_().m_6084_()) {
         away = this.m_20182_().m_82546_(this.m_21188_().m_20182_());
      } else {
         away = new Vec3(-this.m_20154_().f_82479_, 0.0, -this.m_20154_().f_82481_);
      }

      away = new Vec3(away.f_82479_, 0.0, away.f_82481_);
      if (away.m_82556_() < 1.0E-4) {
         away = new Vec3(this.f_19796_.m_188500_() - 0.5, 0.0, this.f_19796_.m_188500_() - 0.5);
      }

      this.leaveDirection = away.m_82541_();
   }

   private void tickLeaveRetreat() {
      if (this.leaveDirection.m_82556_() < 1.0E-4) {
         this.updateLeaveDirectionFromThreat();
      }

      this.m_6710_(null);
      double x = this.m_20185_() + this.leaveDirection.f_82479_ * 12.0;
      double z = this.m_20189_() + this.leaveDirection.f_82481_ * 12.0;
      this.m_21573_().m_26519_(x, this.m_20186_(), z, 1.45);
      this.m_21563_().m_24946_(x, this.m_20188_(), z);
   }

   private void discardAllSauces(ServerLevel serverLevel) {
      BbqEntity bbq = this.resolveAliveSauce(serverLevel, SauceType.BBQ_SAUCE);
      BbqEntity honey = this.resolveAliveSauce(serverLevel, SauceType.HONEY_MUSTARD_SAUCE);
      BbqEntity soy = this.resolveAliveSauce(serverLevel, SauceType.SOY_SAUCE);
      BbqEntity sweet = this.resolveAliveSauce(serverLevel, SauceType.SWEET_ONION_SAUCE);
      if (bbq != null) {
         bbq.m_146870_();
      }

      if (honey != null) {
         honey.m_146870_();
      }

      if (soy != null) {
         soy.m_146870_();
      }

      if (sweet != null) {
         sweet.m_146870_();
      }

      this.setSauce(SauceType.BBQ_SAUCE, null);
      this.setSauce(SauceType.HONEY_MUSTARD_SAUCE, null);
      this.setSauce(SauceType.SOY_SAUCE, null);
      this.setSauce(SauceType.SWEET_ONION_SAUCE, null);
   }

   public void m_6710_(@org.jetbrains.annotations.Nullable LivingEntity pTarget) {
      if (this.isLeavingNow()) {
         super.m_6710_(null);
      } else {
         super.m_6710_(pTarget);
      }
   }

   private void startSauceRetreat(SauceType sauceType) {
      BbqEntity sauce = this.getSauce(sauceType);
      if (sauce != null && sauce.m_6084_()) {
         sauce.startRetreat();
      }

      this.setSauce(sauceType, null);
   }

   private void retreatAllSauces() {
      this.startSauceRetreat(SauceType.BBQ_SAUCE);
      this.startSauceRetreat(SauceType.HONEY_MUSTARD_SAUCE);
      this.startSauceRetreat(SauceType.SOY_SAUCE);
      this.startSauceRetreat(SauceType.SWEET_ONION_SAUCE);
   }

   public void beginStateTwoTransform() {
      this.backupCurrentTarget();
      this.retreatAllSauces();
      this.m_21557_(true);
      this.setHealingTick(0);
      this.setState(2);
      this.setStateTransformCooldown(600);
   }

   private void finishStateTwoTransform(ServerLevel serverLevel) {
      this.setHealingTick(-1);
      this.m_21557_(false);
      this.setState(3);
      this.restoreBackedUpTarget();
      this.squadArrivalTicks = 400;
      ItemStack armorStack = new ItemStack((ItemLike)AnnoyingVillagersModItems.BLUE_DEMON_CHESTPLATE.get());
      armorStack.m_41663_(Enchantments.f_44965_, 5);
      armorStack.m_41663_(Enchantments.f_44969_, 5);
      armorStack.m_41663_(Enchantments.f_44966_, 5);
      armorStack.m_41663_(Enchantments.f_44968_, 5);
      this.m_8061_(EquipmentSlot.CHEST, armorStack);
      this.setSwapWeaponCooldown(new Random().nextInt(200, 600));
      this.ensureStateSauces();
      serverLevel.m_7654_()
         .m_6846_()
         .m_240416_(
            Component.m_237113_(
               "<"
                  + Component.m_237115_(SauceType.BBQ_SAUCE.getTranslationKey()).getString()
                  + "> "
                  + Component.m_237115_("subtitles.bbq_sauce_squad_arrived").getString()
            ),
            false
         );
   }

   private void tickSquadArrival(ServerLevel serverLevel) {
      if (this.squadArrivalTicks > 0) {
         this.squadArrivalTicks--;
         if (this.squadArrivalTicks <= 0) {
            BbqEntity bbq = this.getSauce(SauceType.BBQ_SAUCE);
            BbqEntity honey = this.getSauce(SauceType.HONEY_MUSTARD_SAUCE);
            BbqEntity soy = this.getSauce(SauceType.SOY_SAUCE);
            BbqEntity onion = this.getSauce(SauceType.SWEET_ONION_SAUCE);
            if (bbq != null) {
               bbq.teleportNearLeaderIfTooFar();
            }

            if (honey != null) {
               honey.teleportNearLeaderIfTooFar();
            }

            if (soy != null) {
               soy.teleportNearLeaderIfTooFar();
            }

            if (onion != null) {
               onion.teleportNearLeaderIfTooFar();
            }
         }
      }
   }

   public int getState() {
      return (Integer)this.f_19804_.m_135370_(STATE);
   }

   public void setState(int state) {
      this.f_19804_.m_135381_(STATE, state);
   }

   @Nullable
   private BbqEntity getSauce(SauceType sauceType) {
      return switch (sauceType) {
         case BBQ_SAUCE -> this.bbqSauce;
         case HONEY_MUSTARD_SAUCE -> this.honeyMustardSauce;
         case SOY_SAUCE -> this.soySauce;
         case SWEET_ONION_SAUCE -> this.sweetOnionSauce;
      };
   }

   @Nullable
   private UUID getSauceUUID(SauceType sauceType) {
      return switch (sauceType) {
         case BBQ_SAUCE -> this.bbqSauceUUID;
         case HONEY_MUSTARD_SAUCE -> this.honeyMustardSauceUUID;
         case SOY_SAUCE -> this.soySauceUUID;
         case SWEET_ONION_SAUCE -> this.sweetOnionSauceUUID;
      };
   }

   private void setSauce(SauceType sauceType, @Nullable BbqEntity sauce) {
      UUID uuid = sauce == null ? null : sauce.m_20148_();
      switch (sauceType) {
         case BBQ_SAUCE:
            this.bbqSauce = sauce;
            this.bbqSauceUUID = uuid;
            break;
         case HONEY_MUSTARD_SAUCE:
            this.honeyMustardSauce = sauce;
            this.honeyMustardSauceUUID = uuid;
            break;
         case SOY_SAUCE:
            this.soySauce = sauce;
            this.soySauceUUID = uuid;
            break;
         case SWEET_ONION_SAUCE:
            this.sweetOnionSauce = sauce;
            this.sweetOnionSauceUUID = uuid;
      }
   }

   private void ensureSauceExists(SauceType sauceType) {
      BbqEntity current = this.getSauce(sauceType);
      if (current == null || !current.m_6084_()) {
         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            UUID uuid = this.getSauceUUID(sauceType);
            if (uuid != null && serverLevel.m_8791_(uuid) instanceof BbqEntity sauce && sauce.m_6084_()) {
               this.setSauce(sauceType, sauce);
            } else {
               BbqEntity sauce = new BbqEntity((EntityType<? extends BbqEntity>)AnnoyingVillagersModEntities.BBQ.get(), serverLevel);
               switch (sauceType) {
                  case BBQ_SAUCE:
                     double var31 = 0.0;
                     break;
                  case HONEY_MUSTARD_SAUCE:
                     double var30 = Math.PI / 2;
                     break;
                  case SOY_SAUCE:
                     double var29 = Math.PI;
                     break;
                  case SWEET_ONION_SAUCE:
                     double var10000 = Math.PI * 3.0 / 2.0;
                     break;
                  default:
                     throw new IncompatibleClassChangeError();
               }

               double spawnX;
               double spawnZ;
               if (this.getState() == 3) {
                  float angle = this.getSauceSquadAngle();
                  double distance = 18.0 + this.f_19796_.m_188500_() * 4.0;
                  double laneOffset = this.getSauceLaneOffset(sauceType);
                  double forwardX = (double)Mth.m_14089_(angle);
                  double forwardZ = (double)Mth.m_14031_(angle);
                  double sideX = -forwardZ;
                  spawnX = this.m_20185_() + forwardX * distance + sideX * laneOffset;
                  spawnZ = this.m_20189_() + forwardZ * distance + forwardX * laneOffset;
               } else {
                  double angle = this.f_19796_.m_188500_() * (Math.PI * 2);
                  double radius = 2.5 + this.f_19796_.m_188500_() * 1.5;
                  spawnX = this.m_20185_() + Math.cos(angle) * radius;
                  spawnZ = this.m_20189_() + Math.sin(angle) * radius;
               }

               int spawnY = serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.m_274561_(spawnX, this.m_20186_(), spawnZ)).m_123342_();
               sauce.m_7678_(spawnX, (double)spawnY, spawnZ, this.f_19796_.m_188501_() * 360.0F, 0.0F);
               sauce.setLeader(this);
               sauce.setSauceType(sauceType);
               sauce.m_6518_(serverLevel, serverLevel.m_6436_(sauce.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
               serverLevel.m_7967_(sauce);
               this.setSauce(sauceType, sauce);
            }
         }
      }
   }

   private void ensureStateSauces() {
      if (this.getState() != 0 && this.getState() != 1) {
         if (this.getState() == 3) {
            this.ensureSauceExists(SauceType.BBQ_SAUCE);
            this.ensureSauceExists(SauceType.HONEY_MUSTARD_SAUCE);
            this.ensureSauceExists(SauceType.SOY_SAUCE);
            this.ensureSauceExists(SauceType.SWEET_ONION_SAUCE);
         }
      } else {
         this.ensureSauceExists(SauceType.BBQ_SAUCE);
      }
   }

   @Nullable
   private LivingEntity resolveSauceTarget(@Nullable LivingEntity target) {
      if (target != null && target.m_6084_()) {
         if (target instanceof HerobrineMob herobrineMob && (herobrineMob.isSacrificing() || herobrineMob.isHealing())) {
            if (herobrineMob.getFirstPossessedHerobrine() instanceof LivingEntity living && living.m_6084_()) {
               return living;
            }

            if (herobrineMob.getSecondPossessedHerobrine() instanceof LivingEntity living && living.m_6084_()) {
               return living;
            }

            if (herobrineMob.getThirdPossessedHerobrine() instanceof LivingEntity living && living.m_6084_()) {
               return living;
            }

            if (herobrineMob.getFourthPossessedHerobrine() instanceof LivingEntity living && living.m_6084_()) {
               return living;
            }
         }

         if (target instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity
            && lowHerobrineCloneEntity.isHealing()
            && lowHerobrineCloneEntity.getPossessedByEntity() != null
            && lowHerobrineCloneEntity.getPossessedByEntity().m_6084_()) {
            return lowHerobrineCloneEntity.getPossessedByEntity();
         }

         if (target instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity
            && (lowShadowHerobrineCloneEntity.isSacrificing() || lowShadowHerobrineCloneEntity.isHealing())
            && lowShadowHerobrineCloneEntity.getPossessedByEntity() != null
            && lowShadowHerobrineCloneEntity.getPossessedByEntity().m_6084_()) {
            return lowShadowHerobrineCloneEntity.getPossessedByEntity();
         }

         return target;
      } else {
         return null;
      }
   }

   @Nullable
   public BlueDemonThrownTridentEntity getNearestGroundedOwnedTrident(double radius) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         AABB var13 = this.m_20191_().m_82400_(radius);
         List tridents = serverLevel.m_6443_(
            BlueDemonThrownTridentEntity.class,
            var13,
            tridentx -> tridentx.m_6084_()
                  && tridentx.isGroundedTrident()
                  && tridentx.belongsToOwner(this)
                  && !tridentx.isAbsorbingToWearer()
                  && tridentx.m_20280_(this) > 9.0
         );
         BlueDemonThrownTridentEntity best = null;
         double bestDistance = Double.MAX_VALUE;

         for (BlueDemonThrownTridentEntity trident : tridents) {
            double distance = this.m_20280_(trident);
            if (distance < bestDistance) {
               bestDistance = distance;
               best = trident;
            }
         }

         return best;
      } else {
         return null;
      }
   }

   @Nullable
   public LivingEntityPatch<?> getLivingEntityPatch() {
      return (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
   }

   public BlueDemonEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<? extends BlueDemonEntity>)AnnoyingVillagersModEntities.BLUE_DEMON.get(), level);
   }

   public BlueDemonEntity(EntityType<? extends BlueDemonEntity> type, Level level) {
      super(type, level);
      this.m_274367_(3.0F);
      this.m_21441_(BlockPathTypes.WATER, 0.0F);
      this.m_21441_(BlockPathTypes.WATER_BORDER, 0.0F);
      this.f_21364_ = 0;
      this.m_21557_(false);
      this.m_6593_(this.m_5446_());
      this.m_20340_(true);
      this.m_21530_();
      this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get()));
      this.m_8061_(EquipmentSlot.OFFHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get()));
      this.m_8061_(EquipmentSlot.CHEST, new ItemStack((ItemLike)AnnoyingVillagersModItems.BLUE_DEMON_CHESTPLATE.get()));
      this.m_21409_(EquipmentSlot.MAINHAND, 0.0F);
      this.m_21409_(EquipmentSlot.CHEST, 0.0F);
      this.m_21409_(EquipmentSlot.OFFHAND, 0.0F);
   }

   @NotNull
   protected PathNavigation m_6037_(@NotNull Level level) {
      return new BlueDemonEntity.BlueDemonWaterPathNavigation(this, level);
   }

   public void m_7023_(@NotNull Vec3 travelVector) {
      if (this.m_20069_() && !this.m_21525_() && !this.m_20159_()) {
         this.m_19920_(0.08F, travelVector);
         super.m_7023_(travelVector);
         this.m_20256_(this.limitWaterHorizontalMotion(this.m_20184_()));
      } else {
         super.m_7023_(travelVector);
      }
   }

   private Vec3 limitWaterHorizontalMotion(Vec3 motion) {
      double horizontalSpeedSqr = motion.f_82479_ * motion.f_82479_ + motion.f_82481_ * motion.f_82481_;
      double maxSpeedSqr = 0.17639999999999997;
      if (horizontalSpeedSqr <= maxSpeedSqr) {
         return motion;
      } else {
         double horizontalSpeed = Math.sqrt(horizontalSpeedSqr);
         return new Vec3(motion.f_82479_ / horizontalSpeed * 0.42, motion.f_82480_, motion.f_82481_ / horizontalSpeed * 0.42);
      }
   }

   @NotNull
   protected SoundEvent m_7975_(@NotNull DamageSource pDamageSource) {
      return this.m_20069_() ? SoundEvents.f_11820_ : SoundEvents.f_11819_;
   }

   @NotNull
   protected SoundEvent m_5592_() {
      return this.m_20069_() ? SoundEvents.f_11818_ : SoundEvents.f_11817_;
   }

   @NotNull
   protected SoundEvent m_5501_() {
      return SoundEvents.f_11876_;
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new KeepPositionGoal(this));
      this.f_21346_.m_25352_(0, new RetargetCloserThreatGoal(this));
      CommonGoals.registerGoalForBlueDemonNpc(this);
   }

   public boolean m_7301_(MobEffectInstance mobeffectinstance) {
      return (mobeffectinstance.m_19544_().m_19483_() == MobEffectCategory.BENEFICIAL || mobeffectinstance.m_19544_() == MobEffects.f_19619_)
         && super.m_7301_(mobeffectinstance);
   }

   private boolean isAliveSauce(@Nullable BbqEntity sauce) {
      return sauce != null && sauce.m_6084_();
   }

   private void clearSweetTemporaryCarriedTrident(@Nullable BbqEntity sweet) {
      if (this.isAliveSauce(sweet)) {
         ItemStack stack = sweet.m_21205_();
         if (stack.m_150930_((Item)AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get())) {
            CompoundTag tag = stack.m_41783_();
            if (tag != null && tag.m_128441_("CarriedTridentMode")) {
               sweet.m_21008_(InteractionHand.MAIN_HAND, ItemStack.f_41583_);
            }
         }
      }
   }

   public boolean isInFinalDeathSequence() {
      return this.dieTick > 0;
   }

   private void startFinalDeathSequence(ServerLevel serverLevel, DamageSource damageSource) {
      if (this.dieTick <= 0) {
         this.dieTick = 200;
         this.m_21153_(1.0F);
         this.m_21557_(true);
         this.m_6710_(null);
         this.m_20334_(0.0, 0.0, 0.0);
         if (damageSource.m_7639_() != null) {
            this.savedKillerUUID = damageSource.m_7639_().m_20148_();
         } else {
            this.savedKillerUUID = null;
         }

         if (this.getLivingEntityPatch() != null) {
            if (this.m_21205_().m_41720_() instanceof BlueDemonTridentItem) {
               this.getLivingEntityPatch().playAnimationSynchronized(AnimsPugilistSteve.BLUE_DEMON_DIE, 0.0F);
            } else {
               this.getLivingEntityPatch().playAnimationSynchronized(AnimsEpicFight.BLUE_DEMON_DIE_LEGENDARY_SWORD_START, 0.0F);
            }
         }

         this.startSauceDeathWatch(serverLevel);
      }
   }

   private void startSauceDeathWatch(ServerLevel serverLevel) {
      BbqEntity bbq = this.resolveAliveSauce(serverLevel, SauceType.BBQ_SAUCE);
      BbqEntity honey = this.resolveAliveSauce(serverLevel, SauceType.HONEY_MUSTARD_SAUCE);
      BbqEntity soy = this.resolveAliveSauce(serverLevel, SauceType.SOY_SAUCE);
      BbqEntity sweet = this.resolveAliveSauce(serverLevel, SauceType.SWEET_ONION_SAUCE);
      if (bbq != null) {
         bbq.startLeaderDeathWatch(this);
      }

      if (honey != null) {
         honey.startLeaderDeathWatch(this);
      }

      if (soy != null) {
         soy.startLeaderDeathWatch(this);
      }

      if (sweet != null) {
         sweet.startLeaderDeathWatch(this);
      }
   }

   private void tickFinalDeathSequence(ServerLevel serverLevel) {
      this.m_21557_(true);
      this.m_6710_(null);
      this.m_20334_(0.0, 0.0, 0.0);
      if (this.getLivingEntityPatch() != null && this.dieTick <= 180 && this.dieTick % 10 == 0) {
         if (this.m_21205_().m_41720_() instanceof BlueDemonTridentItem) {
            this.getLivingEntityPatch().playAnimationSynchronized(AnimsPugilistSteve.BLUE_DEMON_STATE_TRANSFORM, 0.0F);
         } else {
            this.getLivingEntityPatch().playAnimationSynchronized(AnimsEpicFight.BLUE_DEMON_DIE_LEGENDARY_SWORD_TICK, 0.0F);
         }
      }

      if (this.dieTick % 5 == 0 && new Random().nextBoolean()) {
         this.strikeDeathLightning(serverLevel);
      }

      this.dieTick--;
      if (this.dieTick <= 0) {
         this.finishFinalDeathSequence(serverLevel);
      }
   }

   private void strikeDeathLightning(ServerLevel serverLevel) {
      TridentLightningBolt tridentLightningBolt = new TridentLightningBolt(
         (EntityType<? extends LightningBolt>)AnnoyingVillagersModEntities.TRIDENT_LIGHTNING_BOLT.get(), serverLevel
      );
      tridentLightningBolt.setOwner(this);
      tridentLightningBolt.m_6027_(this.m_20185_(), this.m_20186_(), this.m_20189_());
      serverLevel.m_7967_(tridentLightningBolt);
   }

   private DamageSource buildSavedKillerDamageSource(ServerLevel serverLevel) {
      if (this.savedKillerUUID != null) {
         Entity entity = serverLevel.m_8791_(this.savedKillerUUID);
         if (entity instanceof Player player) {
            return this.m_269291_().m_269075_(player);
         }

         if (entity instanceof LivingEntity livingEntity) {
            return this.m_269291_().m_269333_(livingEntity);
         }
      }

      return this.m_269291_().m_269264_();
   }

   private void finishFinalDeathSequence(ServerLevel serverLevel) {
      DamageSource finalSource = this.buildSavedKillerDamageSource(serverLevel);
      this.m_21153_(0.0F);
      this.handleSaucesWhenBlueDemonDies(serverLevel);
      this.dieTick = -1;
      this.m_6667_(finalSource);
      this.m_142687_(RemovalReason.KILLED);
   }

   private void handleSaucesWhenBlueDemonDies(ServerLevel serverLevel) {
      BbqEntity bbq = this.resolveAliveSauce(serverLevel, SauceType.BBQ_SAUCE);
      BbqEntity honey = this.resolveAliveSauce(serverLevel, SauceType.HONEY_MUSTARD_SAUCE);
      BbqEntity soy = this.resolveAliveSauce(serverLevel, SauceType.SOY_SAUCE);
      BbqEntity sweet = this.resolveAliveSauce(serverLevel, SauceType.SWEET_ONION_SAUCE);
      this.clearSweetTemporaryCarriedTrident(sweet);
      boolean bbqAlive = this.isAliveSauce(bbq);
      boolean honeyAlive = this.isAliveSauce(honey);
      boolean soyAlive = this.isAliveSauce(soy);
      boolean sweetAlive = this.isAliveSauce(sweet);
      int aliveCount = (bbqAlive ? 1 : 0) + (honeyAlive ? 1 : 0) + (soyAlive ? 1 : 0) + (sweetAlive ? 1 : 0);
      int existingTridents = (honeyAlive ? 1 : 0) + (soyAlive ? 1 : 0);
      int missingTridents = Math.max(0, 2 - existingTridents);
      boolean giveBbqMainTrident = false;
      boolean giveSweetMainTrident = false;
      boolean giveBbqOffhandChestplate = false;
      boolean giveSweetOffhandChestplate = false;
      int rawTridentDrops = 0;
      boolean rawChestplateDrop = false;
      if (aliveCount == 4) {
         giveSweetOffhandChestplate = true;
      } else if (aliveCount == 3) {
         if (sweetAlive) {
            giveSweetOffhandChestplate = true;
         } else if (bbqAlive) {
            giveBbqOffhandChestplate = true;
         } else {
            rawChestplateDrop = true;
         }
      } else {
         rawChestplateDrop = true;
      }

      for (; missingTridents > 0; missingTridents--) {
         if (sweetAlive && !giveSweetMainTrident) {
            giveSweetMainTrident = true;
         } else if (bbqAlive && !giveBbqMainTrident) {
            giveBbqMainTrident = true;
         } else {
            rawTridentDrops++;
         }
      }

      double deathX = this.m_20185_();
      double deathY = this.m_20186_();
      double deathZ = this.m_20189_();
      if (bbqAlive) {
         bbq.startDeathAssembly(deathX, deathY, deathZ, giveBbqMainTrident, giveBbqOffhandChestplate, null);
      }

      if (honeyAlive) {
         honey.startDeathAssembly(deathX, deathY, deathZ, false, false, bbqAlive ? bbq : null);
      }

      if (soyAlive) {
         soy.startDeathAssembly(deathX, deathY, deathZ, false, false, bbqAlive ? bbq : null);
      }

      if (sweetAlive) {
         sweet.startDeathAssembly(deathX, deathY, deathZ, giveSweetMainTrident, giveSweetOffhandChestplate, bbqAlive ? bbq : null);
      }

      if (rawChestplateDrop) {
         this.m_19983_(new ItemStack((ItemLike)AnnoyingVillagersModItems.BLUE_DEMON_CHESTPLATE.get()));
      }

      for (int i = 0; i < rawTridentDrops; i++) {
         this.m_19983_(new ItemStack((ItemLike)AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get()));
      }
   }

   @Nullable
   private BbqEntity resolveAliveSauce(ServerLevel serverLevel, SauceType sauceType) {
      BbqEntity current = this.getSauce(sauceType);
      if (current != null && current.m_6084_()) {
         return current;
      } else {
         UUID uuid = this.getSauceUUID(sauceType);
         if (uuid == null) {
            return null;
         } else {
            if (serverLevel.m_8791_(uuid) instanceof BbqEntity sauce && sauce.m_6084_()) {
               this.setSauce(sauceType, sauce);
               return sauce;
            }

            return null;
         }
      }
   }

   @Override
   public float getBurstProtectCapRatio() {
      return 0.05F;
   }

   public boolean m_6469_(DamageSource damagesource, float f) {
      if (damagesource.m_276093_(DamageTypes.f_268671_)) {
         return false;
      } else if (damagesource.m_276093_(DamageTypes.f_268585_)) {
         return false;
      } else if (damagesource.m_276093_(DamageTypes.f_268722_)) {
         return false;
      } else if (damagesource.m_276093_(DamageTypes.f_268450_)) {
         return false;
      } else if (damagesource.m_276093_(DamageTypes.f_268493_)) {
         return false;
      } else if (damagesource.m_276093_(DamageTypes.f_268714_)) {
         return false;
      } else if (damagesource.m_276093_(DamageTypes.f_268641_)) {
         return false;
      } else if (damagesource.m_7640_() instanceof ThrownPoisonEggEntity) {
         return false;
      } else if (!damagesource.m_276093_(DamageTypes.f_268724_) && !damagesource.m_276093_(DamageTypes.f_286979_)) {
         if (this.m_9236_() instanceof ServerLevel serverLevel && (this.getState() == 2 || this.getState() == 1)) {
            EpicfightUtil.damageBlocked(damagesource, this, serverLevel);
            return false;
         }

         if (this.dieTick > 0) {
            if (this.m_9236_() instanceof ServerLevel serverLevel) {
               ((HitParticleType)EpicFightParticles.HIT_BLUNT.get())
                  .spawnParticleWithArgument(serverLevel, HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, this, damagesource.m_7639_());
            }

            return false;
         } else {
            if (this.m_9236_() instanceof ServerLevel serverLevel && this.getLivingEntityPatch() != null && this.dieTick <= 0) {
               AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(this.getLivingEntityPatch().getAnimator().getPlayerFor(null))
                  .getRealAnimation();
               if (dynamicAnimation == AnimsWom.CUT_ANTITHEUS_ASCENSION
                  || dynamicAnimation == AVAnimations.TRIDENT_ATTACK
                  || dynamicAnimation == AnimsWom.ELECTRIC_FIELD
                  || dynamicAnimation == AnimsPugilistSteve.TRIDENT_FESTIVAL
                  || dynamicAnimation == AnimsPugilistSteve.BLUE_DEMON_STATE_TRANSFORM
                  || dynamicAnimation == AnimsPugilistSteve.BLUE_DEMON_STATE_TRANSFORM_END) {
                  ((HitParticleType)EpicFightParticles.HIT_BLUNT.get())
                     .spawnParticleWithArgument(serverLevel, HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, this, damagesource.m_7639_());
                  return false;
               }
            }

            boolean result = super.m_6469_(damagesource, f);
            if (result) {
               this.sayHurtSound(this, damagesource);
            }

            return result;
         }
      } else {
         boolean result = super.m_6469_(damagesource, f);
         if (result) {
            this.sayHurtSound(this, damagesource);
         }

         return result;
      }
   }

   @Nullable
   public BbqEntity getBbqEntity() {
      if (this.bbqSauce != null && this.bbqSauce.m_6084_()) {
         return this.bbqSauce;
      } else if (this.bbqResolveCooldown > 0) {
         this.bbqResolveCooldown--;
         return null;
      } else {
         this.bbqResolveCooldown = 20;
         if (!this.m_9236_().f_46443_
            && this.bbqSauceUUID != null
            && ((ServerLevel)this.m_9236_()).m_8791_(this.bbqSauceUUID) instanceof BbqEntity bbqEntity
            && bbqEntity.m_6084_()) {
            this.bbqSauce = bbqEntity;
            return bbqEntity;
         } else {
            return null;
         }
      }
   }

   public void setBbqEntity(@Nullable BbqEntity bbqEntity) {
      this.bbqSauce = bbqEntity;
      this.bbqSauceUUID = bbqEntity == null ? null : bbqEntity.m_20148_();
   }

   @Nullable
   private LivingEntity resolveBbqTarget(@Nullable LivingEntity target) {
      if (target != null && target.m_6084_()) {
         if (target instanceof HerobrineMob herobrineMob && (herobrineMob.isSacrificing() || herobrineMob.isHealing())) {
            if (herobrineMob.getFirstPossessedHerobrine() instanceof LivingEntity living && living.m_6084_()) {
               return living;
            }

            if (herobrineMob.getSecondPossessedHerobrine() instanceof LivingEntity living && living.m_6084_()) {
               return living;
            }

            if (herobrineMob.getThirdPossessedHerobrine() instanceof LivingEntity living && living.m_6084_()) {
               return living;
            }

            if (herobrineMob.getFourthPossessedHerobrine() instanceof LivingEntity living && living.m_6084_()) {
               return living;
            }
         }

         if (target instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity
            && lowHerobrineCloneEntity.isHealing()
            && lowHerobrineCloneEntity.getPossessedByEntity() != null
            && !lowHerobrineCloneEntity.m_6084_()) {
            return lowHerobrineCloneEntity.getPossessedByEntity();
         }

         if (target instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity
            && (lowShadowHerobrineCloneEntity.isSacrificing() || lowShadowHerobrineCloneEntity.isHealing())
            && lowShadowHerobrineCloneEntity.getPossessedByEntity() != null
            && !lowShadowHerobrineCloneEntity.m_6084_()) {
            return lowShadowHerobrineCloneEntity.getPossessedByEntity();
         }

         return target;
      } else {
         return null;
      }
   }

   private void tickBbqOrders(@Nullable BbqEntity bbqEntity) {
      if (bbqEntity != null) {
         if (this.isSauceArrivalPending()) {
            bbqEntity.clearCombat();
         } else {
            LivingEntity blueDemonTarget = this.m_5448_();
            if (blueDemonTarget != null && blueDemonTarget.m_6084_()) {
               LivingEntity bbqTarget = this.resolveSauceTarget(blueDemonTarget);
               if (bbqTarget != null && bbqTarget.m_6084_()) {
                  bbqEntity.setCombatTarget(bbqTarget);
                  if (this.bbqHeadAttackCooldown > 0) {
                     this.bbqHeadAttackCooldown--;
                  }

                  if (this.bbqOrderCooldown > 0) {
                     this.bbqOrderCooldown--;
                  }

                  if (this.bbqModeCooldown > 0) {
                     this.bbqModeCooldown--;
                  }

                  double blueDistance = (double)this.m_20270_(blueDemonTarget);
                  double bbqDistance = (double)bbqEntity.m_20270_(bbqTarget);
                  if (blueDistance > 10.0) {
                     bbqEntity.startParallelPursuit(bbqTarget, 25);
                     if (this.bbqOrderCooldown <= 0) {
                        bbqEntity.shootChain(bbqTarget, 3, 10);
                        this.bbqOrderCooldown = 40;
                     }
                  } else if (this.bbqHeadAttackCooldown <= 0 && blueDistance < 4.5 && bbqDistance < 6.5 && !bbqEntity.isHeadAttacking()) {
                     bbqEntity.startHeadAttack(bbqTarget, 35);
                     this.bbqHeadAttackCooldown = 110;
                     this.bbqModeCooldown = 20;
                     this.bbqOrderCooldown = 16;
                  } else {
                     if (!bbqEntity.isHeadAttacking() && this.bbqModeCooldown <= 0) {
                        int roll = this.f_19796_.m_188503_(100);
                        if (roll < 25) {
                           bbqEntity.startParallelPursuit(bbqTarget, 28);
                           this.bbqModeCooldown = 40;
                        } else if (roll < 60) {
                           bbqEntity.startGroundOrbit(bbqTarget, 36);
                           this.bbqModeCooldown = 42;
                        } else {
                           bbqEntity.startOrbit(bbqTarget, 28);
                           this.bbqModeCooldown = 38;
                        }
                     }

                     if (!bbqEntity.isHeadAttacking() && this.bbqOrderCooldown <= 0) {
                        switch (bbqEntity.getCombatMode()) {
                           case PARALLEL:
                              bbqEntity.shootChain(bbqTarget, 3, 10);
                              this.bbqOrderCooldown = 40;
                              break;
                           case GROUND_ORBIT:
                              if (bbqDistance > 7.0) {
                                 bbqEntity.shootChain(bbqTarget, 3, 9);
                                 this.bbqOrderCooldown = 36;
                              } else {
                                 bbqEntity.shootCluster(bbqTarget, 3, 1.05F, 10.0F);
                                 this.bbqOrderCooldown = 48;
                              }
                              break;
                           case ORBIT:
                              if (bbqDistance > 7.0) {
                                 bbqEntity.shootChain(bbqTarget, 4, 8);
                                 this.bbqOrderCooldown = 38;
                              } else {
                                 bbqEntity.shootCluster(bbqTarget, 4, 1.1F, 10.0F);
                                 this.bbqOrderCooldown = 52;
                              }
                        }
                     }
                  }
               } else {
                  bbqEntity.clearCombat();
               }
            } else {
               bbqEntity.clearCombat();
            }
         }
      }
   }

   private void tickShockSauceOrders(@Nullable BbqEntity sauce) {
      if (sauce != null) {
         if (this.isSauceArrivalPending()) {
            sauce.clearCombat();
         } else {
            LivingEntity blueDemonTarget = this.m_5448_();
            if (blueDemonTarget != null && blueDemonTarget.m_6084_()) {
               LivingEntity sauceTarget = this.resolveSauceTarget(blueDemonTarget);
               if (sauceTarget != null && sauceTarget.m_6084_()) {
                  sauce.setCombatTarget(sauceTarget);
                  double blueDistance = (double)this.m_20270_(blueDemonTarget);
                  double sauceDistance = (double)sauce.m_20270_(sauceTarget);
                  if (blueDistance > 10.0) {
                     sauce.startParallelPursuit(sauceTarget, 25);
                  } else if (!sauce.isHeadAttacking()
                     && this.f_19797_ % 60 == 0
                     && blueDistance < 4.5
                     && sauceDistance < 6.5
                     && this.f_19796_.m_188503_(4) == 0) {
                     sauce.startHeadAttack(sauceTarget, 28);
                  } else {
                     if (!sauce.isHeadAttacking() && this.f_19797_ % 40 == 0) {
                        if (this.f_19796_.m_188499_()) {
                           sauce.startGroundOrbit(sauceTarget, 34);
                        } else {
                           sauce.startOrbit(sauceTarget, 28);
                        }
                     }
                  }
               } else {
                  sauce.clearCombat();
               }
            } else {
               sauce.clearCombat();
            }
         }
      }
   }

   private void tickSweetOnionOrders(@Nullable BbqEntity sauce) {
      if (sauce != null) {
         if (this.isSauceArrivalPending()) {
            sauce.clearCombat();
         } else {
            LivingEntity blueDemonTarget = this.m_5448_();
            if (blueDemonTarget != null && blueDemonTarget.m_6084_()) {
               LivingEntity sauceTarget = this.resolveSauceTarget(blueDemonTarget);
               if (sauceTarget != null && sauceTarget.m_6084_()) {
                  sauce.setCombatTarget(sauceTarget);
               } else {
                  sauce.clearCombat();
               }
            } else {
               sauce.clearCombat();
            }
         }
      }
   }

   private void tickArmorBuff(ServerLevel serverLevel) {
      this.m_7292_(new MobEffectInstance(MobEffects.f_19596_, 1, 1, false, false, false));
      this.m_7292_(new MobEffectInstance(MobEffects.f_19603_, 1, 1, false, false, false));
      this.m_7292_(new MobEffectInstance(MobEffects.f_19606_, 1, 2, false, false, false));
      if (serverLevel.f_46441_.m_188500_() <= 0.1) {
         BlueDemonUtil.spawnBlueDemonEffect(serverLevel, this);
         if (serverLevel.f_46441_.m_188500_() <= 0.8) {
            float volume = (float)Mth.m_216263_(serverLevel.f_46441_, 0.05, 0.5);
            float pitch = (float)Mth.m_216263_(serverLevel.f_46441_, 0.8, 1.1);
            serverLevel.m_5594_(
               null,
               BlockPos.m_274561_(this.m_20185_(), this.m_20186_(), this.m_20189_()),
               (SoundEvent)AnnoyingVillagersModSounds.ELECTRIFY.get(),
               SoundSource.NEUTRAL,
               volume,
               pitch
            );
         }
      }

      if (this.f_19797_ % 2 == 0 && this.getLivingEntityPatch() != null) {
         AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(this.getLivingEntityPatch().getAnimator().getPlayerFor(null))
            .getRealAnimation();
         if (dynamicAnimation != AVAnimations.TRIDENT_ATTACK && dynamicAnimation != AnimsPugilistSteve.TRIDENT_FESTIVAL) {
            this.absorbNearbyGroundedOwnerTridents(serverLevel);
         }
      }
   }

   private void absorbNearbyGroundedOwnerTridents(ServerLevel serverLevel) {
      AABB box = new AABB(
         this.m_20185_() - 2.5, this.m_20186_() - 2.5, this.m_20189_() - 2.5, this.m_20185_() + 2.5, this.m_20186_() + 2.5, this.m_20189_() + 2.5
      );

      for (BlueDemonThrownTridentEntity trident : serverLevel.m_6443_(
         BlueDemonThrownTridentEntity.class,
         box,
         tridentx -> tridentx.m_6084_() && tridentx.isGroundedTrident() && tridentx.belongsToOwner(this) && !tridentx.isAbsorbingToWearer()
      )) {
         trident.beginAbsorbToWearer(this);
      }
   }

   private void tickStateTwoPhysics() {
      this.m_20242_(false);
      Vec3 motion = this.m_20184_();
      motion = new Vec3(0.0, motion.f_82480_, 0.0);
      if (!this.m_20096_() && !this.m_20069_() && !this.m_20077_()) {
         motion = motion.m_82520_(0.0, -0.08, 0.0);
      }

      motion = new Vec3(motion.f_82479_ * 0.91, motion.f_82480_ * 0.98, motion.f_82481_ * 0.91);
      this.m_20256_(motion);
      this.m_6478_(MoverType.SELF, motion);
      if (this.m_20096_() && this.m_20184_().f_82480_ < 0.0) {
         this.m_20334_(this.m_20184_().f_82479_, 0.0, this.m_20184_().f_82481_);
      }
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.getState() == 2 || this.dieTick > 0) {
         this.tickStateTwoPhysics();
      }

      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         this.tickVoiceCooldown();
         this.tickBurstProtectionDecay(this);
         if (!this.spawnedBbqSauce) {
            this.ensureSauceExists(SauceType.BBQ_SAUCE);
            this.spawnedBbqSauce = true;
         }

         if (this.stunEscapeCooldown > 0) {
            this.stunEscapeCooldown--;
         }

         if (this.swapWeaponCooldown > 0) {
            this.swapWeaponCooldown--;
         }

         if (this.efnGuardHitCooldown > 0) {
            this.efnGuardHitCooldown--;
         }

         if (this.healingCooldown > 0) {
            this.healingCooldown--;
         }

         if (this.stateTransformCooldown > 0) {
            if (this.getLivingEntityPatch() != null) {
               if (this.stateTransformCooldown > 20) {
                  this.getLivingEntityPatch().playAnimationSynchronized(AnimsPugilistSteve.BLUE_DEMON_STATE_TRANSFORM, 0.0F);
               } else if (this.stateTransformCooldown == 20) {
                  if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
                     this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.BLUE_DEMON_SAY_PHASE_2_RELEASE.get(), 0.5F, 1.0F);
                  }

                  this.getLivingEntityPatch().playAnimationSynchronized(AnimsPugilistSteve.BLUE_DEMON_STATE_TRANSFORM_END, 0.0F);
               } else if (this.stateTransformCooldown == 10) {
                  ItemStack legendaryStack = new ItemStack((ItemLike)AnnoyingVillagersModItems.LEGENDARY_SWORD.get());
                  legendaryStack.m_41663_(Enchantments.f_44977_, 5);
                  legendaryStack.m_41663_(Enchantments.f_44978_, 5);
                  legendaryStack.m_41663_(Enchantments.f_44983_, 5);
                  this.m_21008_(InteractionHand.MAIN_HAND, legendaryStack);
                  this.m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
                  this.m_7292_(new MobEffectInstance(MobEffects.f_19606_, 4, 300));
               }
            }

            if (this.stateTransformCooldown % 2 == 0) {
               this.m_5634_(1.0F);
            }

            if (this.stateTransformCooldown <= 200 && this.stateTransformCooldown % 50 == 0) {
               BlueDemonTridentItem.spawnDamageZones(serverLevel, this);
            }

            this.stateTransformCooldown--;
            if (this.stateTransformCooldown == 0) {
               this.finishStateTwoTransform(serverLevel);
            }
         }

         if (this.dieTick > 0) {
            this.tickFinalDeathSequence(serverLevel);
            return;
         }

         if (!this.neverLeave) {
            this.leaveTicks--;
            int remaining = this.leaveTicks;
            if (remaining == 40) {
               this.m_6710_(null);
               this.updateLeaveDirectionFromThreat();
               this.retreatAllSauces();
            }

            if (remaining <= 0) {
               if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
                  this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.BLUE_DEMON_SAY_WHEN_RETREAT.get(), 0.5F, 1.0F);
               }

               serverLevel.m_7654_()
                  .m_6846_()
                  .m_240416_(
                     Component.m_237113_("<" + this.m_7755_().getString() + "> " + Component.m_237115_("subtitles.blue_demon_retreat").getString()), false
                  );
               this.discardAllSauces(serverLevel);
               this.m_146870_();
               return;
            }

            if (this.isLeavingNow()) {
               this.tickLeaveRetreat();
               return;
            }
         }

         this.tickSquadArrival(serverLevel);
         if (this.healingTick != 0) {
            if (this.healingTick > 0) {
               this.healingTick--;
            }

            this.tickArmorBuff(serverLevel);
         }

         this.syncChestplateHealingFoil();
         if (this.efnGuardHitCooldown == 0 && this.efnGuardHitState != 0) {
            this.efnGuardHitState = 0;
         }

         if (ModList.get().isLoaded("efkick") && this.stunEscapeCooldown == 0 && this.m_9236_() instanceof ServerLevel && this.getLivingEntityPatch() != null) {
            final AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(
                  this.getLivingEntityPatch().getAnimator().getPlayerFor(null)
               )
               .getRealAnimation();
            if (EpicfightUtil.isLongHitAnimationNotExecutedAnimation(dynamicAnimation, this.getLivingEntityPatch())
               && this.m_6084_()
               && (double)this.m_217043_().m_188501_() < CombatBehaviour.calculateGuardBreakWakeUpChance(this)) {
               this.stunEscapeCooldown = 60;
               final BlueDemonEntity entity = this;
               new DelayedTask(new Random().nextInt(5, 10)) {
                  @Override
                  public void run() {
                     if (BlueDemonEntity.this.getLivingEntityPatch() != null
                        && EpicfightUtil.isLongHitAnimationNotExecutedAnimation(dynamicAnimation, BlueDemonEntity.this.getLivingEntityPatch())
                        && entity.m_6084_()) {
                        CombatBehaviour.postGuardBreakWakeUp(entity, BlueDemonEntity.this.getLivingEntityPatch(), serverLevel);
                     } else {
                        entity.stunEscapeCooldown = 1;
                     }
                  }
               };
            }
         }

         if (this.getLivingEntityPatch() != null && CombatCommon.canEscape((MobPatch<?>)this.getLivingEntityPatch())) {
            this.f_21345_.m_25355_(Flag.MOVE);
            this.m_21573_().m_26573_();
            LivingEntity target = this.m_5448_();
            if (target != null) {
               this.m_21563_().m_24960_(target, 30.0F, 30.0F);
            }
         } else {
            this.f_21345_.m_25374_(Flag.MOVE);
         }

         if (this.getState() == 3) {
            this.tickShockSauceOrders(this.getSauce(SauceType.HONEY_MUSTARD_SAUCE));
            this.tickShockSauceOrders(this.getSauce(SauceType.SOY_SAUCE));
            this.tickSweetOnionOrders(this.getSauce(SauceType.SWEET_ONION_SAUCE));
         }

         this.tickBbqOrders(this.getSauce(SauceType.BBQ_SAUCE));
      }
   }

   @Override
   public SoundEvent getAttackVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.BLUE_DEMON_SAY.get();
   }

   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      if (this.bbqSauceUUID != null) {
         tag.m_128362_("BbqSauceUUID", this.bbqSauceUUID);
      }

      if (this.honeyMustardSauceUUID != null) {
         tag.m_128362_("HoneyMustardSauceUUID", this.honeyMustardSauceUUID);
      }

      if (this.soySauceUUID != null) {
         tag.m_128362_("SoySauceUUID", this.soySauceUUID);
      }

      if (this.sweetOnionSauceUUID != null) {
         tag.m_128362_("SweetOnionSauceUUID", this.sweetOnionSauceUUID);
      }

      tag.m_128405_("HealingCooldown", this.healingCooldown);
      tag.m_128405_("HealingTick", this.healingTick);
      tag.m_128405_("StateTransformCooldown", this.stateTransformCooldown);
      tag.m_128405_("State", this.getState());
      if (this.savedTargetUUID != null) {
         tag.m_128362_("SavedTargetUUID", this.savedTargetUUID);
      }

      if (this.savedKillerUUID != null) {
         tag.m_128362_("SavedKillerUUID", this.savedKillerUUID);
      }

      tag.m_128405_("SquadArrivalTicks", this.squadArrivalTicks);
      tag.m_128379_("SpawnedBbqSauce", this.spawnedBbqSauce);
      tag.m_128405_("DieTick", this.dieTick);
      tag.m_128405_("LeaveTicks", this.leaveTicks);
      tag.m_128379_("NeverLeave", this.neverLeave);
      tag.m_128405_("VoiceCooldown", this.voiceCooldown);
   }

   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128403_("BbqSauceUUID")) {
         this.bbqSauceUUID = tag.m_128342_("BbqSauceUUID");
      }

      if (tag.m_128403_("HoneyMustardSauceUUID")) {
         this.honeyMustardSauceUUID = tag.m_128342_("HoneyMustardSauceUUID");
      }

      if (tag.m_128403_("SoySauceUUID")) {
         this.soySauceUUID = tag.m_128342_("SoySauceUUID");
      }

      if (tag.m_128403_("SweetOnionSauceUUID")) {
         this.sweetOnionSauceUUID = tag.m_128342_("SweetOnionSauceUUID");
      }

      this.healingCooldown = tag.m_128451_("HealingCooldown");
      this.healingTick = tag.m_128451_("HealingTick");
      this.stateTransformCooldown = tag.m_128451_("StateTransformCooldown");
      this.setState(tag.m_128441_("State") ? tag.m_128451_("State") : 0);
      if (tag.m_128403_("SavedTargetUUID")) {
         this.savedTargetUUID = tag.m_128342_("SavedTargetUUID");
      } else {
         this.savedTargetUUID = null;
      }

      if (tag.m_128403_("SavedKillerUUID")) {
         this.savedKillerUUID = tag.m_128342_("SavedKillerUUID");
      } else {
         this.savedKillerUUID = null;
      }

      this.squadArrivalTicks = tag.m_128441_("SquadArrivalTicks") ? tag.m_128451_("SquadArrivalTicks") : -1;
      this.spawnedBbqSauce = tag.m_128471_("SpawnedBbqSauce");
      this.dieTick = tag.m_128451_("DieTick");
      this.leaveTicks = tag.m_128451_("LeaveTicks");
      this.neverLeave = tag.m_128471_("NeverLeave");
      this.voiceCooldown = tag.m_128451_("VoiceCooldown");
   }

   public void rollItem() {
      ItemStack legendaryStack = new ItemStack((ItemLike)AnnoyingVillagersModItems.LEGENDARY_SWORD.get());
      legendaryStack.m_41663_(Enchantments.f_44977_, 5);
      legendaryStack.m_41663_(Enchantments.f_44978_, 5);
      legendaryStack.m_41663_(Enchantments.f_44983_, 5);
      ItemStack tridentStack = new ItemStack((ItemLike)AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get());
      tridentStack.m_41663_(Enchantments.f_44977_, 5);
      tridentStack.m_41663_(Enchantments.f_44978_, 5);
      tridentStack.m_41663_(Enchantments.f_44983_, 5);
      if (this.m_21205_().m_41720_() instanceof BlueDemonTridentItem) {
         this.m_21008_(InteractionHand.MAIN_HAND, legendaryStack);
         this.m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
      } else {
         this.m_21008_(InteractionHand.MAIN_HAND, tridentStack);
         this.m_21008_(InteractionHand.OFF_HAND, tridentStack);
      }

      this.swapWeaponCooldown = new Random().nextInt(600, 900);
   }

   private void syncChestplateHealingFoil() {
      ItemStack chest = this.m_6844_(EquipmentSlot.CHEST);
      if (BlueDemonChestplateItem.isBlueDemonChestplate(chest)) {
         BlueDemonChestplateItem.setBlueDemonHealingFoil(chest, this.healingTick != 0);
      }
   }

   @Nullable
   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor serverlevelaccessor,
      @NotNull DifficultyInstance difficultyinstance,
      @NotNull MobSpawnType mobspawntype,
      @Nullable SpawnGroupData spawngroupdata,
      @Nullable CompoundTag compoundtag
   ) {
      SpawnGroupData data = super.m_6518_(serverlevelaccessor, difficultyinstance, mobspawntype, spawngroupdata, compoundtag);
      if (!this.m_9236_().m_5776_()) {
         TeamUtil.addOrJoinTeam(this, "blue_demon");
      }

      if (mobspawntype == MobSpawnType.NATURAL || mobspawntype == MobSpawnType.CHUNK_GENERATION) {
         ServerLevel serverLevel = serverlevelaccessor.m_6018_();
         BluedemonData bluedemonData = BluedemonData.get(serverLevel);
         if (!bluedemonData.tryClaim(serverLevel, this.m_20148_())) {
            this.m_146870_();
            return data;
         }

         BlockPos blockPos = this.m_20097_();
         int surfaceY = serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, blockPos).m_123342_();
         BlockPos spawnPos = new BlockPos(blockPos.m_123341_(), surfaceY, blockPos.m_123343_());
         this.m_20035_(spawnPos, this.m_146908_(), this.m_146909_());
      }

      int min = (Integer)AnnoyingVillagersConfig.BLUE_DEMON_LEAVE_MIN_TIME.get();
      int max = (Integer)AnnoyingVillagersConfig.BLUE_DEMON_LEAVE_MAX_TIME.get();
      int randomMin = Math.min(min, max);
      int randomMax = Math.max(min, max);
      this.leaveTicks = (randomMin + new Random().nextInt(randomMax - randomMin + 1)) * 60 * 20;
      return data;
   }

   public static boolean canSpawn(
      EntityType<BlueDemonEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random
   ) {
      ServerLevel serverLevel = level.m_6018_();
      if (!serverLevel.m_46470_()) {
         return false;
      } else if (serverLevel.m_46462_()) {
         return false;
      } else {
         return BluedemonData.get(serverLevel).isOccupied(serverLevel) ? false : Monster.m_219019_(entityType, level, spawnType, position, random);
      }
   }

   protected void m_6475_(@NotNull DamageSource pDamageSource, float pDamageAmount) {
      if (pDamageSource.m_276093_(DamageTypes.f_268724_)) {
         super.m_6475_(pDamageSource, pDamageAmount);
      } else if (!this.m_6673_(pDamageSource)) {
         pDamageAmount = ForgeHooks.onLivingHurt(this, pDamageSource, pDamageAmount);
         if (!(pDamageAmount <= 0.0F)) {
            pDamageAmount = this.m_21161_(pDamageSource, pDamageAmount);
            pDamageAmount = this.m_6515_(pDamageSource, pDamageAmount);
            float f1 = Math.max(pDamageAmount - this.m_6103_(), 0.0F);
            float absorbed = pDamageAmount - f1;
            if (absorbed > 0.0F) {
               this.m_7911_(this.m_6103_() - absorbed);
               if (this.m_6103_() < 0.0F) {
                  this.m_7911_(0.0F);
               }
            }

            f1 = ForgeHooks.onLivingDamage(this, pDamageSource, f1);
            f1 = this.applyBurstProtection(this, pDamageSource, f1);
            if (this.m_9236_() instanceof ServerLevel && this.getState() == 0 && this.m_21223_() - f1 <= 1.0F) {
               this.m_21153_(1.0F);
               BlueDemonTridentItem.addStormEnergy(this.m_21205_(), 100);
               BlueDemonTridentItem.addStormEnergy(this.m_21206_(), 100);
               if (this.getLivingEntityPatch() != null) {
                  this.getLivingEntityPatch().playAnimationSynchronized(AnimsPugilistSteve.TRIDENT_FESTIVAL, 0.0F);
               }
            } else {
               if (this.m_9236_() instanceof ServerLevel serverLevel && this.getState() == 3 && this.m_21223_() - f1 <= 1.0F) {
                  this.startFinalDeathSequence(serverLevel, pDamageSource);
                  return;
               }

               if (!(f1 <= 0.0F)) {
                  this.m_21231_().m_289194_(pDamageSource, f1);
                  this.m_21153_(this.m_21223_() - f1);
                  this.m_146850_(GameEvent.f_223706_);
               }
            }
         }
      }
   }

   public void m_142687_(@NotNull RemovalReason reason) {
      super.m_142687_(reason);
      if (!this.m_9236_().f_46443_
         && this.m_9236_() instanceof ServerLevel serverLevel
         && (reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED)) {
         BluedemonData.get(serverLevel).releaseIfMatches(serverLevel, this.m_20148_());
      }
   }

   public static Builder createAttributes() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22276_, 300.0)
         .m_22268_(Attributes.f_22279_, 0.45)
         .m_22268_(Attributes.f_22281_, 10.0)
         .m_22268_(Attributes.f_22277_, 64.0)
         .m_22268_(Attributes.f_22284_, 10.0)
         .m_22268_(Attributes.f_22285_, 20.0)
         .m_22268_(Attributes.f_22278_, 1.0)
         .m_22268_((Attribute)EpicFightAttributes.IMPACT.get(), 4.0)
         .m_22268_((Attribute)EpicFightAttributes.ARMOR_NEGATION.get(), 10.0)
         .m_22268_((Attribute)EpicFightAttributes.STUN_ARMOR.get(), 20.0)
         .m_22268_((Attribute)EpicFightAttributes.MAX_STRIKES.get(), 100.0)
         .m_22268_((Attribute)EpicFightAttributes.MAX_STAMINA.get(), 60.0)
         .m_22268_((Attribute)EpicFightAttributes.STAMINA_REGEN.get(), 1.5);
   }

   private static class BlueDemonWaterPathNavigation extends GroundPathNavigation {
      public BlueDemonWaterPathNavigation(Mob mob, Level level) {
         super(mob, level);
      }

      @NotNull
      protected PathFinder m_5532_(int maxVisitedNodes) {
         this.f_26508_ = new WalkNodeEvaluator();
         this.f_26508_.m_77351_(true);
         return new PathFinder(this.f_26508_, maxVisitedNodes);
      }

      protected boolean m_7367_(@NotNull BlockPathTypes type) {
         return type != BlockPathTypes.WATER && type != BlockPathTypes.WATER_BORDER ? super.m_7367_(type) : true;
      }

      public boolean m_6342_(@NotNull BlockPos blockPos) {
         return this.f_26495_.m_6425_(blockPos).m_205070_(FluidTags.f_13131_) || super.m_6342_(blockPos);
      }
   }
}
