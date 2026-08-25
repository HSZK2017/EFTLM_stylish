package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.TridentMode;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.BlueDemonChestplateItem;
import com.pla.annoyingvillagers.item.BlueDemonTridentItem;
import com.pla.annoyingvillagers.util.BlueDemonUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;

public class BlueDemonThrownTridentEntity extends ThrownTrident {
   private TridentMode mode = TridentMode.DEFAULT;
   private static final int MAX_GROUNDED_TRIDENTS_PER_OWNER = 20;
   private static final double OWNER_BOX_HALF_SIZE = 50.0;
   private static final String TAG_SPAWN_SEQUENCE = "BlueDemonSpawnSequence";
   private static final String TAG_OWNER_SHOT_COUNTER = "BlueDemonOwnerShotCounter";
   private static final int RELAUNCH_ANIMATION_DURATION = 20;
   private boolean relaunchAnimationActive = false;
   private int relaunchAnimationTick = 0;
   private Vec3 relaunchAnimationStart = Vec3.f_82478_;
   private Vec3 relaunchAnimationEnd = Vec3.f_82478_;
   private boolean relaunchDelayActive = false;
   private int relaunchDelayTicks = 0;
   private int relaunchDelayTick = 0;
   private boolean festivalGroundRiseActive = false;
   private boolean summonedGroundTridentFestival = false;
   private int festivalGroundRiseTick = 0;
   private Vec3 festivalGroundRiseStart = Vec3.f_82478_;
   private Vec3 festivalGroundRiseEnd = Vec3.f_82478_;
   private static final int FESTIVAL_GROUND_RISE_DURATION = 6;
   private static final double FESTIVAL_RISE_START_DEPTH = 1.0;
   private static final double FESTIVAL_RISE_END_OFFSET = 0.0;
   private float festivalPoseXRot = 90.0F;
   private float festivalPoseYRot = 0.0F;
   private double festivalPoseYOffset = 0.0;
   @Nullable
   private UUID queuedTargetUUID = null;
   @Nullable
   private Vec3 queuedFallbackDirection = null;
   private float queuedLaunchSpeed = 0.0F;
   private long spawnSequence;
   private static final float ABSORB_HEAL_AMOUNT = 2.0F;
   private static final double ABSORB_FINISH_DISTANCE_SQR = 1.0;
   private boolean absorbToWearerActive = false;
   @Nullable
   private UUID absorbWearerUUID = null;
   private Vec3 absorbStartGroundPos = Vec3.f_82478_;
   @Nullable
   private Direction absorbReturnFace = null;
   private boolean festivalGroundedPoseActive = false;
   private static final double FESTIVAL_FORCE_HITBLOCK_REMAINING_Y = 0.65;
   private static final EntityDataAccessor<Boolean> DATA_FESTIVAL_GROUNDED_POSE = SynchedEntityData.m_135353_(
      BlueDemonThrownTridentEntity.class, EntityDataSerializers.f_135035_
   );
   private static final EntityDataAccessor<Float> DATA_FESTIVAL_POSE_XROT = SynchedEntityData.m_135353_(
      BlueDemonThrownTridentEntity.class, EntityDataSerializers.f_135029_
   );
   private static final EntityDataAccessor<Float> DATA_FESTIVAL_POSE_YROT = SynchedEntityData.m_135353_(
      BlueDemonThrownTridentEntity.class, EntityDataSerializers.f_135029_
   );
   private boolean specialImpactTriggered = false;
   private static final EntityDataAccessor<Byte> DATA_STUCK_FACE = SynchedEntityData.m_135353_(
      BlueDemonThrownTridentEntity.class, EntityDataSerializers.f_135027_
   );
   private static final EntityDataAccessor<Boolean> DATA_FESTIVAL_RISE_ACTIVE = SynchedEntityData.m_135353_(
      BlueDemonThrownTridentEntity.class, EntityDataSerializers.f_135035_
   );
   private static final EntityDataAccessor<Float> DATA_FESTIVAL_START_Y = SynchedEntityData.m_135353_(
      BlueDemonThrownTridentEntity.class, EntityDataSerializers.f_135029_
   );
   private static final EntityDataAccessor<Float> DATA_FESTIVAL_END_Y = SynchedEntityData.m_135353_(
      BlueDemonThrownTridentEntity.class, EntityDataSerializers.f_135029_
   );

   private boolean isRelaunchLocked() {
      return this.festivalGroundRiseActive || this.relaunchAnimationActive || this.relaunchDelayActive || this.absorbToWearerActive;
   }

   public boolean isSummonedGroundTridentFestival() {
      return this.summonedGroundTridentFestival;
   }

   public void setSummonedGroundTridentFestival(boolean summonedGroundTridentFestival) {
      this.summonedGroundTridentFestival = summonedGroundTridentFestival;
   }

   public boolean isAbsorbingToWearer() {
      return this.absorbToWearerActive;
   }

   public TridentMode getMode() {
      return this.mode;
   }

   public void placeAsGroundedSupport(@NotNull LivingEntity owner, @NotNull BlockPos standPos) {
      this.m_5602_(owner);
      this.f_36705_ = Pickup.DISALLOWED;
      this.specialImpactTriggered = true;
      this.f_37556_ = false;
      Vec3 pos = new Vec3((double)standPos.m_123341_() + 0.5, (double)standPos.m_123342_() + 0.05, (double)standPos.m_123343_() + 0.5);
      this.m_6034_(pos.f_82479_, pos.f_82480_, pos.f_82481_);
      this.m_20256_(Vec3.f_82478_);
      this.m_36790_(false);
      this.m_20242_(false);
      this.f_19812_ = false;
      this.m_146915_(false);
      this.m_8060_(new BlockHitResult(pos, Direction.UP, standPos.m_7495_(), false));
   }

   public void beginAbsorbToWearer(@NotNull LivingEntity entity) {
      if (!this.absorbToWearerActive && !this.relaunchAnimationActive && !this.relaunchDelayActive) {
         if (this.f_36703_ && this.belongsToOwner(entity)) {
            this.absorbToWearerActive = true;
            this.absorbWearerUUID = entity.m_20148_();
            this.absorbStartGroundPos = this.m_20182_();
            this.absorbReturnFace = this.getStuckFace();
            this.f_36705_ = Pickup.DISALLOWED;
            this.setStuckFace(null);
            this.f_36703_ = false;
            this.f_36704_ = 0;
            this.f_36706_ = 0;
            this.m_36790_(true);
            this.m_20242_(true);
            this.m_20256_(Vec3.f_82478_);
            this.f_19812_ = false;
            this.m_146915_(true);
         }
      }
   }

   @Nullable
   private LivingEntity getAbsorbWearer() {
      if (!(this.m_9236_() instanceof ServerLevel serverLevel) || this.absorbWearerUUID == null) {
         return null;
      }

      if (serverLevel.m_8791_(this.absorbWearerUUID) instanceof LivingEntity living && living.m_6084_()) {
         return living;
      }

      return null;
   }

   private boolean canContinueAbsorbToWearer(@NotNull LivingEntity entity) {
      if (!this.belongsToOwner(entity)) {
         return false;
      } else if (!(entity instanceof Player player)) {
         return entity instanceof BlueDemonEntity blueDemonEntity ? blueDemonEntity.getHealingTick() != 0 : false;
      } else {
         ItemStack chest = player.m_6844_(EquipmentSlot.CHEST);
         return BlueDemonChestplateItem.isBlueDemonChestplate(chest) && BlueDemonChestplateItem.isBuffActive(chest);
      }
   }

   private void cancelAbsorbToWearer() {
      this.absorbToWearerActive = false;
      this.absorbWearerUUID = null;
      this.m_36790_(false);
      this.m_20242_(false);
      this.f_19812_ = false;
      this.f_36705_ = Pickup.DISALLOWED;
      this.m_6034_(this.absorbStartGroundPos.f_82479_, this.absorbStartGroundPos.f_82480_, this.absorbStartGroundPos.f_82481_);
      this.m_20256_(Vec3.f_82478_);
      this.f_36703_ = true;
      this.f_36704_ = 0;
      this.f_36706_ = 0;
      this.setStuckFace(this.absorbReturnFace);
      this.m_146915_(false);
   }

   private void finishAbsorbToWearer(@NotNull LivingEntity entity) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         entity.m_5634_(2.0F);
         serverLevel.m_8767_(
            (SimpleParticleType)AnnoyingVillagersModParticleTypes.ELECTRIC_SPARK.get(),
            this.m_20185_(),
            this.m_20186_(),
            this.m_20189_(),
            6,
            0.15,
            0.15,
            0.15,
            0.02
         );
         serverLevel.m_5594_(
            null, BlockPos.m_274561_(this.m_20185_(), this.m_20186_(), this.m_20189_()), SoundEvents.f_12516_, SoundSource.PLAYERS, 0.8F, 1.35F
         );
      }

      this.m_146870_();
   }

   private void tickAbsorbToWearer() {
      LivingEntity entity = this.getAbsorbWearer();
      if (entity != null && this.canContinueAbsorbToWearer(entity)) {
         Vec3 targetPos = entity.m_20182_().m_82520_(0.0, (double)entity.m_20206_() * 0.55, 0.0);
         Vec3 toTarget = targetPos.m_82546_(this.m_20182_());
         double distanceSqr = toTarget.m_82556_();
         if (distanceSqr <= 1.0) {
            this.finishAbsorbToWearer(entity);
         } else {
            double distance = Math.sqrt(distanceSqr);
            Vec3 move = toTarget.m_82541_().m_82490_(Math.min(0.85, 0.18 + distance * 0.12));
            this.m_6034_(this.m_20185_() + move.f_82479_, this.m_20186_() + move.f_82480_, this.m_20189_() + move.f_82481_);
            this.m_20256_(Vec3.f_82478_);
            this.updateRotationFromMovement(move);
            if (this.m_9236_() instanceof ServerLevel serverLevel && serverLevel.f_46441_.m_188500_() <= 0.25) {
               serverLevel.m_8767_(
                  (SimpleParticleType)AnnoyingVillagersModParticleTypes.ELECTRIC_SPARK.get(),
                  this.m_20185_(),
                  this.m_20186_(),
                  this.m_20189_(),
                  1,
                  0.05,
                  0.05,
                  0.05,
                  0.0
               );
            }
         }
      } else {
         this.cancelAbsorbToWearer();
      }
   }

   public void m_6123_(@NotNull Player player) {
      if (!this.isRelaunchLocked()) {
         super.m_6123_(player);
      }
   }

   protected boolean m_142470_(@NotNull Player player) {
      return false;
   }

   @Nullable
   public Direction getStuckFace() {
      byte value = (Byte)this.f_19804_.m_135370_(DATA_STUCK_FACE);
      return value == -1 ? null : Direction.m_122376_(value);
   }

   public void setStuckFace(@Nullable Direction direction) {
      this.f_19804_.m_135381_(DATA_STUCK_FACE, direction == null ? -1 : (byte)direction.m_122411_());
   }

   public void beginFestivalGroundRise(@NotNull LivingEntity owner, @NotNull BlockPos standPos, boolean strikeWhenFinished) {
      if (!this.festivalGroundRiseActive && !this.relaunchAnimationActive && !this.relaunchDelayActive && !this.absorbToWearerActive) {
         this.clearFestivalGroundedPose();
         this.m_5602_(owner);
         this.f_36705_ = Pickup.DISALLOWED;
         this.specialImpactTriggered = true;
         this.f_37556_ = false;
         this.f_19804_.m_135381_(DATA_FESTIVAL_RISE_ACTIVE, true);
         this.summonedGroundTridentFestival = true;
         this.festivalGroundRiseActive = true;
         this.festivalGroundRiseTick = 0;
         this.rollFestivalPose();
         double endX = (double)standPos.m_123341_() + 0.5;
         double endY = (double)standPos.m_123342_() + 0.0 + this.festivalPoseYOffset;
         double endZ = (double)standPos.m_123343_() + 0.5;
         this.festivalGroundRiseEnd = new Vec3(endX, endY, endZ);
         this.festivalGroundRiseStart = new Vec3(endX, endY - 1.0, endZ);
         this.f_19804_.m_135381_(DATA_FESTIVAL_START_Y, (float)this.festivalGroundRiseStart.f_82480_);
         this.f_19804_.m_135381_(DATA_FESTIVAL_END_Y, (float)this.festivalGroundRiseEnd.f_82480_);
         this.f_36703_ = false;
         this.f_36704_ = 0;
         this.f_36706_ = 0;
         this.setStuckFace(null);
         this.m_6034_(this.festivalGroundRiseStart.f_82479_, this.festivalGroundRiseStart.f_82480_, this.festivalGroundRiseStart.f_82481_);
         this.m_20256_(Vec3.f_82478_);
         this.m_36790_(true);
         this.m_20242_(true);
         this.f_19812_ = false;
         this.m_146915_(true);
         this.applyFestivalVerticalPose();
      }
   }

   private void tickFestivalGroundRise() {
      this.festivalGroundRiseTick++;
      float t = Math.min(1.0F, (float)this.festivalGroundRiseTick / 6.0F);
      double nextY = Mth.m_14139_((double)t, this.festivalGroundRiseStart.f_82480_, this.festivalGroundRiseEnd.f_82480_);
      if (this.festivalGroundRiseEnd.f_82480_ - nextY <= 0.65) {
         this.finishFestivalGroundRise();
      } else {
         this.f_19854_ = this.m_20185_();
         this.f_19855_ = this.m_20186_();
         this.f_19856_ = this.m_20189_();
         this.m_6034_(this.festivalGroundRiseStart.f_82479_, nextY, this.festivalGroundRiseStart.f_82481_);
         this.m_20256_(Vec3.f_82478_);
         this.applyFestivalVerticalPose();
         this.m_146915_(true);
         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            serverLevel.m_8767_(
               (SimpleParticleType)AnnoyingVillagersModParticleTypes.ELECTRIC_SPARK.get(),
               this.m_20185_(),
               this.m_20186_(),
               this.m_20189_(),
               2,
               0.08,
               0.1,
               0.08,
               0.01
            );
         }

         if (this.festivalGroundRiseTick >= 6) {
            this.finishFestivalGroundRise();
         }
      }
   }

   private void finishFestivalGroundRise() {
      if (!this.m_9236_().f_46443_) {
         this.f_19804_.m_135381_(DATA_FESTIVAL_RISE_ACTIVE, false);
      }

      this.festivalGroundRiseActive = false;
      this.festivalGroundRiseTick = 0;
      this.releaseFestivalGroundedPose(true);
   }

   public boolean m_142391_() {
      return this.f_36703_ && !this.festivalGroundRiseActive && !this.relaunchAnimationActive && !this.relaunchDelayActive && !this.absorbToWearerActive;
   }

   public BlueDemonThrownTridentEntity(EntityType<? extends ThrownTrident> type, Level level) {
      super(type, level);
   }

   public BlueDemonThrownTridentEntity(SpawnEntity packet, Level level) {
      this((EntityType<? extends ThrownTrident>)AnnoyingVillagersModEntities.BLUE_DEMON_THROWN_TRIDENT.get(), level);
   }

   public BlueDemonThrownTridentEntity(Level level, LivingEntity shooter, ItemStack stack) {
      super((EntityType)AnnoyingVillagersModEntities.BLUE_DEMON_THROWN_TRIDENT.get(), level);
      this.m_5602_(shooter);
      this.f_36705_ = Pickup.DISALLOWED;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_STUCK_FACE, (byte)-1);
      this.f_19804_.m_135372_(DATA_FESTIVAL_RISE_ACTIVE, false);
      this.f_19804_.m_135372_(DATA_FESTIVAL_GROUNDED_POSE, false);
      this.f_19804_.m_135372_(DATA_FESTIVAL_POSE_XROT, 90.0F);
      this.f_19804_.m_135372_(DATA_FESTIVAL_POSE_YROT, 0.0F);
      this.f_19804_.m_135372_(DATA_FESTIVAL_START_Y, 0.0F);
      this.f_19804_.m_135372_(DATA_FESTIVAL_END_Y, 0.0F);
   }

   private void syncFestivalPoseFromData() {
      this.festivalPoseXRot = (Float)this.f_19804_.m_135370_(DATA_FESTIVAL_POSE_XROT);
      this.festivalPoseYRot = (Float)this.f_19804_.m_135370_(DATA_FESTIVAL_POSE_YROT);
   }

   private void clearFestivalGroundedPose() {
      this.festivalGroundedPoseActive = false;
      if (!this.m_9236_().f_46443_) {
         this.f_19804_.m_135381_(DATA_FESTIVAL_GROUNDED_POSE, false);
      }

      this.m_20256_(Vec3.f_82478_);
      this.f_19812_ = false;
      this.m_36790_(false);
      this.m_20242_(false);
   }

   public void setMode(TridentMode mode) {
      this.mode = mode == null ? TridentMode.DEFAULT : mode;
   }

   @Nullable
   public LivingEntity getOwnerLiving() {
      return this.m_19749_() instanceof LivingEntity living ? living : null;
   }

   protected void m_5790_(EntityHitResult result) {
      Entity target = result.m_82443_();
      Entity owner = this.m_19749_();
      float damage = 8.0F;
      DamageSource damageSource = this.m_269291_().m_269525_(this, (Entity)(owner == null ? this : owner));
      this.f_37556_ = true;
      SoundEvent sound = SoundEvents.f_12514_;
      boolean hurtSuccess = target.m_6469_(damageSource, damage);
      if (hurtSuccess) {
         if (target instanceof LivingEntity livingTarget && new Random().nextFloat() <= 0.15F) {
            livingTarget.m_7292_(new MobEffectInstance((MobEffect)AnnoyingVillagersModMobEffects.ELECTRIFY.get(), 20, 1));
         }

         if (!this.m_9236_().f_46443_ && !this.specialImpactTriggered) {
            this.specialImpactTriggered = true;
            this.handleModeImpact(target.m_20183_(), target);
         }
      }

      this.m_20256_(this.m_20184_().m_82542_(-0.01, -0.1, -0.01));
      this.m_5496_(sound, 1.0F, 1.0F);
   }

   public void m_8119_() {
      if (this.m_9236_().f_46443_) {
         boolean riseActive = (Boolean)this.f_19804_.m_135370_(DATA_FESTIVAL_RISE_ACTIVE);
         boolean groundedPoseActive = (Boolean)this.f_19804_.m_135370_(DATA_FESTIVAL_GROUNDED_POSE);
         if (riseActive) {
            if (!this.festivalGroundRiseActive) {
               this.startFestivalGroundRiseClient();
            }
         } else if (groundedPoseActive) {
            if (!this.festivalGroundedPoseActive) {
               this.startFestivalGroundedPoseClient();
            }
         } else if (this.festivalGroundedPoseActive) {
            this.clearFestivalGroundedPose();
         }
      }

      if (this.festivalGroundRiseActive) {
         this.m_6075_();
         this.f_36705_ = Pickup.DISALLOWED;
         this.tickFestivalGroundRise();
         this.tickElectricEffects();
      } else if (this.festivalGroundedPoseActive) {
         this.m_6075_();
         this.f_36705_ = Pickup.DISALLOWED;
         this.m_20256_(Vec3.f_82478_);
         this.f_19812_ = false;
         this.f_36703_ = true;
         this.f_36704_ = 0;
         this.f_36706_ = 0;
         this.setStuckFace(Direction.UP);
         this.m_36790_(true);
         this.m_20242_(true);
         this.applyFestivalVerticalPose();
         if (!this.m_9236_().f_46443_ && this.f_19797_ % 10 == 0) {
            this.discardIfGroundedAndFarFromOwner();
            if (!this.m_6084_()) {
               return;
            }
         }

         this.tickElectricEffects();
      } else if (!this.m_9236_().f_46443_ && this.absorbToWearerActive) {
         this.m_6075_();
         this.f_36705_ = Pickup.DISALLOWED;
         this.tickAbsorbToWearer();
         this.tickElectricEffects();
      } else if (this.m_9236_().f_46443_ || !this.relaunchAnimationActive && !this.relaunchDelayActive) {
         super.m_8119_();
         if (!this.m_9236_().f_46443_ && this.f_36703_ && this.f_19797_ % 10 == 0) {
            this.discardIfGroundedAndFarFromOwner();
            if (!this.m_6084_()) {
               return;
            }
         }

         this.tickElectricEffects();
      } else {
         this.m_6075_();
         this.f_36705_ = Pickup.DISALLOWED;
         if (this.relaunchAnimationActive) {
            this.tickAnimatedRelaunch();
         } else {
            this.tickRelaunchDelay();
         }

         this.tickElectricEffects();
      }
   }

   protected void handleModeImpact(BlockPos pos, @Nullable Entity hitTarget) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         switch (this.mode) {
            case DEFAULT:
            default:
               break;
            case LIGHTNING:
               this.spawnTridentLightning(serverLevel, pos, hitTarget);
               break;
            case EXPLOSION:
               this.spawnTridentExplosion(serverLevel, pos, hitTarget);
         }
      }
   }

   protected void spawnTridentLightning(ServerLevel serverLevel, BlockPos pos, Entity hitTarget) {
      TridentLightningBolt lightning = new TridentLightningBolt(
         (EntityType<? extends LightningBolt>)AnnoyingVillagersModEntities.TRIDENT_LIGHTNING_BOLT.get(), serverLevel
      );
      lightning.m_20219_(Vec3.m_82539_(pos));
      lightning.setDamage(5.0F);
      this.m_146915_(false);
      LivingEntity owner = this.getOwnerLiving();
      if (owner != null) {
         lightning.setOwner(owner);
      }

      serverLevel.m_7967_(lightning);
   }

   protected void spawnSuperTridentLightning(ServerLevel serverLevel, BlockPos pos, Entity hitTarget) {
      TridentLightningBolt lightning = new TridentLightningBolt(
         (EntityType<? extends LightningBolt>)AnnoyingVillagersModEntities.TRIDENT_LIGHTNING_BOLT.get(), serverLevel
      );
      this.m_146915_(false);
      lightning.m_20219_(Vec3.m_82539_(pos));
      lightning.setSuperLightning(true);
      lightning.setDamage(15.0F);
      LivingEntity owner = this.getOwnerLiving();
      if (owner != null) {
         lightning.setOwner(owner);
      }

      serverLevel.m_7967_(lightning);
   }

   protected void spawnTridentExplosion(ServerLevel serverLevel, BlockPos pos, Entity hitTarget) {
      Entity owner = this.m_19749_();
      serverLevel.m_254849_(owner, this.m_20185_(), this.m_20186_(), this.m_20189_(), 2.5F, ExplosionInteraction.TNT);
   }

   public void assignSpawnSequence(@NotNull LivingEntity owner) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         CompoundTag ownerData = owner.getPersistentData();
         int shotCounter = ownerData.m_128451_("BlueDemonOwnerShotCounter") + 1 & 65535;
         ownerData.m_128405_("BlueDemonOwnerShotCounter", shotCounter);
         this.spawnSequence = serverLevel.m_46467_() << 16 | (long)shotCounter & 65535L;
      }
   }

   public boolean m_6128_() {
      return true;
   }

   public boolean m_6673_(@NotNull DamageSource source) {
      return source.m_269533_(DamageTypeTags.f_268415_) || super.m_6673_(source);
   }

   public boolean m_6469_(@NotNull DamageSource source, float amount) {
      return source.m_269533_(DamageTypeTags.f_268415_) ? false : super.m_6469_(source, amount);
   }

   public long getSpawnSequence() {
      return this.spawnSequence;
   }

   private static AABB makeOwnerGroundBox(Entity owner) {
      Level level = owner.m_9236_();
      return new AABB(
         owner.m_20185_() - 50.0,
         (double)level.m_141937_(),
         owner.m_20189_() - 50.0,
         owner.m_20185_() + 50.0,
         (double)level.m_151558_(),
         owner.m_20189_() + 50.0
      );
   }

   private boolean isGroundedForLimit() {
      return this.f_36703_;
   }

   private boolean hasSameOwner(UUID ownerUuid) {
      Entity owner = this.m_19749_();
      return owner != null && owner.m_20148_().equals(ownerUuid);
   }

   private boolean isOutsideOwnerGroundBox(Entity owner) {
      return Math.abs(this.m_20185_() - owner.m_20185_()) > 50.0 || Math.abs(this.m_20189_() - owner.m_20189_()) > 50.0;
   }

   private void startFestivalGroundRiseClient() {
      this.festivalGroundedPoseActive = false;
      this.festivalGroundRiseActive = true;
      this.festivalGroundRiseTick = 0;
      this.syncFestivalPoseFromData();
      double x = this.m_20185_();
      double z = this.m_20189_();
      double startY = (double)((Float)this.f_19804_.m_135370_(DATA_FESTIVAL_START_Y)).floatValue();
      double endY = (double)((Float)this.f_19804_.m_135370_(DATA_FESTIVAL_END_Y)).floatValue();
      this.festivalGroundRiseStart = new Vec3(x, startY, z);
      this.festivalGroundRiseEnd = new Vec3(x, endY, z);
      this.m_6034_(x, startY, z);
      this.f_19854_ = x;
      this.f_19855_ = startY;
      this.f_19856_ = z;
      this.f_36703_ = false;
      this.f_36704_ = 0;
      this.f_36706_ = 0;
      this.setStuckFace(null);
      this.m_36790_(true);
      this.m_20242_(true);
      this.m_20256_(Vec3.f_82478_);
      this.f_19812_ = false;
      this.m_146915_(true);
      this.applyFestivalVerticalPose();
   }

   private void startFestivalGroundedPoseClient() {
      this.festivalGroundedPoseActive = true;
      this.syncFestivalPoseFromData();
      this.m_20256_(Vec3.f_82478_);
      this.f_19812_ = false;
      this.f_36703_ = true;
      this.f_36704_ = 0;
      this.f_36706_ = 0;
      this.setStuckFace(Direction.UP);
      this.m_36790_(true);
      this.m_20242_(true);
      this.applyFestivalVerticalPose();
   }

   private void releaseFestivalGroundedPose(boolean glowing) {
      this.releaseFestivalGroundedPose(glowing, true);
   }

   private void releaseFestivalGroundedPose(boolean glowing, boolean noPhysicGravity) {
      Vec3 finalPos = this.festivalGroundRiseEnd;
      this.festivalGroundedPoseActive = true;
      if (!this.m_9236_().f_46443_) {
         this.f_19804_.m_135381_(DATA_FESTIVAL_GROUNDED_POSE, true);
      }

      this.m_6034_(finalPos.f_82479_, finalPos.f_82480_, finalPos.f_82481_);
      this.f_19854_ = finalPos.f_82479_;
      this.f_19855_ = finalPos.f_82480_;
      this.f_19856_ = finalPos.f_82481_;
      this.m_20256_(Vec3.f_82478_);
      this.f_19812_ = false;
      this.f_36703_ = true;
      this.f_36704_ = 0;
      this.f_36706_ = 0;
      this.setStuckFace(Direction.UP);
      this.m_36790_(noPhysicGravity);
      this.m_20242_(noPhysicGravity);
      this.m_146915_(glowing);
      this.applyFestivalVerticalPose();
   }

   private void rollFestivalPose() {
      this.festivalPoseXRot = 90.0F + (this.f_19796_.m_188501_() - 0.5F) * 12.0F;
      this.festivalPoseYRot = this.f_19796_.m_188501_() * 360.0F;
      this.festivalPoseYOffset = 0.05 + this.f_19796_.m_188500_() * 0.14;
      this.f_19804_.m_135381_(DATA_FESTIVAL_POSE_XROT, this.festivalPoseXRot);
      this.f_19804_.m_135381_(DATA_FESTIVAL_POSE_YROT, this.festivalPoseYRot);
   }

   private void applyFestivalVerticalPose() {
      if (this.m_9236_().f_46443_) {
         this.syncFestivalPoseFromData();
      }

      this.m_146926_(this.festivalPoseXRot);
      this.f_19860_ = this.festivalPoseXRot;
      this.m_146922_(this.festivalPoseYRot);
      this.f_19859_ = this.festivalPoseYRot;
   }

   public void finishSummonedGroundTridentFestival() {
      if (this.summonedGroundTridentFestival) {
         this.summonedGroundTridentFestival = false;
         this.clearFestivalGroundedPose();
         this.m_146915_(false);
         this.f_36703_ = false;
         this.f_36704_ = 0;
         this.f_36706_ = 0;
         this.setStuckFace(null);
         this.m_36790_(false);
         this.m_20242_(false);
         this.f_19812_ = true;
         this.m_6034_(this.m_20185_(), this.m_20186_() + 0.25, this.m_20189_());
         this.f_19854_ = this.m_20185_();
         this.f_19855_ = this.m_20186_();
         this.f_19856_ = this.m_20189_();
         this.m_20334_(0.0, -0.12, 0.0);
      }
   }

   public void trimOldGroundedTridentsAroundOwnerOnSpawn() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         LivingEntity owner = this.getOwnerLiving();
         if (owner != null) {
            UUID ownerUuid = owner.m_20148_();
            List<BlueDemonThrownTridentEntity> grounded = serverLevel.m_6443_(
               BlueDemonThrownTridentEntity.class,
               makeOwnerGroundBox(owner),
               trident -> trident != this && trident.m_6084_() && trident.isGroundedForLimit() && trident.hasSameOwner(ownerUuid)
            );
            int removeCount = grounded.size() - 20 + 1;
            if (removeCount > 0) {
               grounded.sort(Comparator.comparingLong(BlueDemonThrownTridentEntity::getSpawnSequence).thenComparing(Entity::m_20148_));

               for (int i = 0; i < removeCount; i++) {
                  grounded.get(i).m_146870_();
               }
            }
         }
      }
   }

   private void discardIfGroundedAndFarFromOwner() {
      if (this.f_36703_) {
         LivingEntity owner = this.getOwnerLiving();
         if (owner != null && this.isOutsideOwnerGroundBox(owner)) {
            this.m_146870_();
         }
      }
   }

   protected void m_8060_(@NotNull BlockHitResult result) {
      if (this.m_9236_().f_46443_) {
         super.m_8060_(result);
      } else if (this.summonedGroundTridentFestival) {
         this.releaseFestivalGroundedPose(true);
         this.discardIfGroundedAndFarFromOwner();
      } else {
         super.m_8060_(result);
         this.setStuckFace(result.m_82434_());
         if (result.m_82434_() == Direction.UP) {
            float[] pitchChoices = new float[]{-90.0F, -60.0F, -45.0F, -30.0F};
            float[] yawChoices = new float[]{-90.0F, -60.0F, -45.0F, -30.0F, 0.0F, 30.0F, 45.0F, 60.0F, 90.0F};
            float pitch = pitchChoices[this.f_19796_.m_188503_(pitchChoices.length)];
            float yawOffset = yawChoices[this.f_19796_.m_188503_(yawChoices.length)];
            this.m_146926_(pitch);
            this.f_19860_ = this.m_146909_();
            this.m_146922_(this.m_146908_() + yawOffset);
            this.f_19859_ = this.m_146908_();
         }

         if (!this.specialImpactTriggered) {
            this.specialImpactTriggered = true;
            this.handleModeImpact(result.m_82425_(), null);
         }

         this.discardIfGroundedAndFarFromOwner();
      }
   }

   public boolean isGroundedTrident() {
      return this.f_36703_;
   }

   public boolean belongsToOwner(@NotNull LivingEntity owner) {
      Entity projectileOwner = this.m_19749_();
      return projectileOwner != null && projectileOwner.m_20148_().equals(owner.m_20148_());
   }

   public void relaunchTowards(@NotNull Vec3 direction, float speed, float inaccuracy) {
      if (!(direction.m_82556_() < 1.0E-7)) {
         this.f_36705_ = Pickup.DISALLOWED;
         Vec3 normalized = direction.m_82541_();
         this.setStuckFace(null);
         this.f_36703_ = false;
         this.f_36704_ = 0;
         this.f_36706_ = 0;
         this.f_37556_ = false;
         this.specialImpactTriggered = false;
         this.m_36790_(false);
         this.m_20242_(false);
         this.f_19812_ = true;
         this.m_146915_(true);
         this.m_20256_(Vec3.f_82478_);
         this.m_146922_((float)(Mth.m_14136_(normalized.f_82479_, normalized.f_82481_) * (180.0 / Math.PI)));
         this.m_146926_(
            (float)(
               Mth.m_14136_(normalized.f_82480_, Math.sqrt(normalized.f_82479_ * normalized.f_82479_ + normalized.f_82481_ * normalized.f_82481_))
                  * (180.0 / Math.PI)
            )
         );
         this.f_19859_ = this.m_146908_();
         this.f_19860_ = this.m_146909_();
         this.m_6686_(normalized.f_82479_, normalized.f_82480_, normalized.f_82481_, speed, inaccuracy);
         this.m_5496_(SoundEvents.f_12520_, 1.0F, 1.0F);
      }
   }

   public void summonSuperLightningAtSelf() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         this.spawnSuperTridentLightning(serverLevel, BlockPos.m_274446_(this.m_20182_()), null);
      }
   }

   public void summonLightningAtSelf() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         this.spawnTridentLightning(serverLevel, BlockPos.m_274446_(this.m_20182_()), null);
      }
   }

   public void beginAnimatedRelaunch(@Nullable LivingEntity target, @Nullable Vec3 fallbackDirection, float speed, float inaccuracy, int launchDelayTicks) {
      if (!this.relaunchAnimationActive && !this.relaunchDelayActive) {
         int offsetX = this.f_19796_.m_188503_(3) - 1;
         int offsetZ = this.f_19796_.m_188503_(3) - 1;
         if (offsetX == 0 && offsetZ == 0) {
            if (this.f_19796_.m_188499_()) {
               offsetX = this.f_19796_.m_188499_() ? 1 : -1;
            } else {
               offsetZ = this.f_19796_.m_188499_() ? 1 : -1;
            }
         }

         double riseY = 1.0 + this.f_19796_.m_188500_() * 2.0;
         this.relaunchAnimationStart = this.m_20182_();
         this.relaunchAnimationEnd = this.relaunchAnimationStart.m_82520_((double)offsetX, riseY, (double)offsetZ);
         this.queuedTargetUUID = target != null ? target.m_20148_() : null;
         this.queuedFallbackDirection = fallbackDirection != null && fallbackDirection.m_82556_() > 1.0E-7 ? fallbackDirection.m_82541_() : null;
         this.queuedLaunchSpeed = speed;
         this.relaunchAnimationTick = 0;
         this.relaunchAnimationActive = true;
         this.relaunchDelayActive = false;
         this.relaunchDelayTicks = Math.max(0, launchDelayTicks);
         this.relaunchDelayTick = 0;
         this.f_36705_ = Pickup.DISALLOWED;
         this.setStuckFace(null);
         this.f_36703_ = false;
         this.f_36704_ = 0;
         this.f_36706_ = 0;
         this.f_37556_ = false;
         this.specialImpactTriggered = false;
         this.m_36790_(true);
         this.m_20242_(true);
         this.m_20256_(Vec3.f_82478_);
         this.f_19812_ = false;
         this.m_146915_(true);
      }
   }

   private void tickRelaunchDelay() {
      this.relaunchDelayTick++;
      this.m_20256_(Vec3.f_82478_);
      Vec3 direction = this.resolveQueuedLaunchDirection();
      if (direction != null) {
         this.updateRotationFromMovement(direction);
      }

      if (this.relaunchDelayTick >= this.relaunchDelayTicks) {
         this.relaunchDelayActive = false;
         this.launchQueuedRelaunch();
      }
   }

   private void launchQueuedRelaunch() {
      float speed = this.queuedLaunchSpeed;
      Vec3 direction = this.resolveQueuedLaunchDirection();
      this.queuedTargetUUID = null;
      this.queuedFallbackDirection = null;
      this.m_36790_(false);
      this.m_20242_(false);
      this.f_19812_ = true;
      this.specialImpactTriggered = false;
      this.f_36703_ = false;
      this.f_36704_ = 0;
      this.f_36706_ = 0;
      this.f_37556_ = false;
      this.f_36705_ = Pickup.DISALLOWED;
      if (direction != null && direction.m_82556_() > 1.0E-7) {
         this.relaunchTowards(direction, speed, 0.0F);
      }
   }

   @Nullable
   private LivingEntity getQueuedTargetLiving() {
      if (!(this.m_9236_() instanceof ServerLevel serverLevel) || this.queuedTargetUUID == null) {
         return null;
      }

      if (serverLevel.m_8791_(this.queuedTargetUUID) instanceof LivingEntity living && living.m_6084_()) {
         return living;
      }

      return null;
   }

   @Nullable
   private Vec3 resolveQueuedLaunchDirection() {
      LivingEntity target = this.getQueuedTargetLiving();
      if (target != null) {
         Vec3 computed = this.computeLaunchDirectionTo(target, this.queuedLaunchSpeed);
         if (computed != null) {
            return computed;
         }
      }

      if (this.queuedFallbackDirection != null && this.queuedFallbackDirection.m_82556_() > 1.0E-7) {
         return this.queuedFallbackDirection.m_82541_();
      } else {
         LivingEntity owner = this.getOwnerLiving();
         return owner != null ? BlueDemonTridentItem.getTridentThrowDirection(owner, this.m_20182_()) : null;
      }
   }

   @Nullable
   private Vec3 computeLaunchDirectionTo(@NotNull LivingEntity target, float speed) {
      Vec3 start = this.m_20182_();
      Vec3 targetPos = target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.72, 0.0);
      double dx = targetPos.f_82479_ - start.f_82479_;
      double dz = targetPos.f_82481_ - start.f_82481_;
      double horizontal = Math.sqrt(dx * dx + dz * dz);
      double dy = targetPos.f_82480_ - start.f_82480_ + horizontal * 0.065;
      Vec3 direction = new Vec3(dx, dy, dz);
      return direction.m_82556_() > 1.0E-7 ? direction.m_82541_() : null;
   }

   private void tickAnimatedRelaunch() {
      this.relaunchAnimationTick++;
      float t = Math.min(1.0F, (float)this.relaunchAnimationTick / 20.0F);
      float eased = t * t * (3.0F - 2.0F * t);
      Vec3 nextPos = new Vec3(
         Mth.m_14139_((double)eased, this.relaunchAnimationStart.f_82479_, this.relaunchAnimationEnd.f_82479_),
         Mth.m_14139_((double)eased, this.relaunchAnimationStart.f_82480_, this.relaunchAnimationEnd.f_82480_),
         Mth.m_14139_((double)eased, this.relaunchAnimationStart.f_82481_, this.relaunchAnimationEnd.f_82481_)
      );
      Vec3 moveDelta = nextPos.m_82546_(this.m_20182_());
      this.m_6034_(nextPos.f_82479_, nextPos.f_82480_, nextPos.f_82481_);
      this.m_20256_(Vec3.f_82478_);
      this.updateRotationFromMovement(moveDelta);
      if (this.relaunchAnimationTick >= 20) {
         this.relaunchAnimationActive = false;
         this.relaunchAnimationTick = 0;
         if (this.relaunchDelayTicks > 0) {
            this.relaunchDelayActive = true;
            this.relaunchDelayTick = 0;
         } else {
            this.launchQueuedRelaunch();
         }
      }
   }

   private void updateRotationFromMovement(Vec3 delta) {
      if (!(delta.m_82556_() < 1.0E-7)) {
         double horizontal = Math.sqrt(delta.f_82479_ * delta.f_82479_ + delta.f_82481_ * delta.f_82481_);
         this.m_146922_((float)(Mth.m_14136_(delta.f_82479_, delta.f_82481_) * (180.0 / Math.PI)));
         this.m_146926_((float)(Mth.m_14136_(delta.f_82480_, horizontal) * (180.0 / Math.PI)));
         this.f_19859_ = this.m_146908_();
         this.f_19860_ = this.m_146909_();
      }
   }

   protected boolean m_5603_(@NotNull Entity target) {
      if (target == this) {
         return false;
      } else if (target instanceof BlueDemonThrownTridentEntity) {
         return false;
      } else if (target instanceof TridentLightningBolt) {
         return false;
      } else {
         return target == this.m_19749_() ? false : super.m_5603_(target);
      }
   }

   private void tickElectricEffects() {
      if (this.m_9236_() instanceof ServerLevel serverLevel && this.f_19797_ % 5 == 0 && Math.random() <= 0.1) {
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
   }

   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128359_("BlueDemonMode", this.mode.name());
      tag.m_128379_("SpecialImpactTriggered", this.specialImpactTriggered);
      tag.m_128356_("BlueDemonSpawnSequence", this.spawnSequence);
      tag.m_128379_("SummonedGroundTridentFestival", this.summonedGroundTridentFestival);
      Direction face = this.getStuckFace();
      if (face != null) {
         tag.m_128344_("StuckFace", (byte)face.m_122411_());
      }
   }

   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128441_("BlueDemonMode")) {
         try {
            this.mode = TridentMode.valueOf(tag.m_128461_("BlueDemonMode"));
         } catch (IllegalArgumentException var3) {
            this.mode = TridentMode.DEFAULT;
         }
      } else {
         this.mode = TridentMode.DEFAULT;
      }

      this.specialImpactTriggered = tag.m_128471_("SpecialImpactTriggered");
      this.spawnSequence = tag.m_128454_("BlueDemonSpawnSequence");
      this.summonedGroundTridentFestival = tag.m_128471_("SummonedGroundTridentFestival");
      if (tag.m_128441_("StuckFace")) {
         this.setStuckFace(Direction.m_122376_(tag.m_128445_("StuckFace")));
      } else {
         this.setStuckFace(null);
      }

      this.relaunchAnimationActive = false;
      this.relaunchAnimationTick = 0;
      this.relaunchAnimationStart = Vec3.f_82478_;
      this.relaunchAnimationEnd = Vec3.f_82478_;
      this.relaunchDelayActive = false;
      this.relaunchDelayTicks = 0;
      this.relaunchDelayTick = 0;
      this.queuedTargetUUID = null;
      this.queuedFallbackDirection = null;
      this.queuedLaunchSpeed = 0.0F;
      this.absorbToWearerActive = false;
      this.absorbWearerUUID = null;
      this.absorbStartGroundPos = Vec3.f_82478_;
      this.absorbReturnFace = null;
      this.m_36790_(false);
      this.m_20242_(false);
      this.f_19812_ = false;
      this.m_146915_(false);
      this.festivalGroundRiseActive = false;
      this.festivalGroundRiseTick = 0;
      this.festivalGroundRiseStart = Vec3.f_82478_;
      this.festivalGroundRiseEnd = Vec3.f_82478_;
   }
}
