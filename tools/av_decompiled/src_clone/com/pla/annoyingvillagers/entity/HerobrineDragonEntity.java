package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.client.animation.DragonAnimator;
import com.pla.annoyingvillagers.client.engine.MountCameraManager;
import com.pla.annoyingvillagers.client.engine.MountControlsMessenger;
import com.pla.annoyingvillagers.config.AnnoyingVillagersClientConfig;
import com.pla.annoyingvillagers.entity.goal.DragonOrbitLeaderGoal;
import com.pla.annoyingvillagers.entity.goal.RecallLandGoal;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModKeyMappings;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.EnderSlayerScytheItem;
import com.pla.annoyingvillagers.util.HerobrinePortalCombatUtil;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import reascer.wom.world.entity.mob.EnderHand;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

public class HerobrineDragonEntity extends TamableAnimal implements FlyingAnimal, PlayerRideable {
   public static final double BASE_SPEED_GROUND = 0.3;
   public static final double BASE_SPEED_FLYING = 0.32;
   public static final double BASE_DAMAGE = 8.0;
   public static final double BASE_HEALTH = 150.0;
   public static final double BASE_FOLLOW_RANGE = 16.0;
   public static final int BASE_KB_RESISTANCE = 1;
   public static final float BASE_WIDTH = 2.75F;
   public static final float BASE_HEIGHT = 2.75F;
   public static final int GROUND_CLEARENCE_THRESHOLD = 3;
   private final DragonAnimator animator;
   private boolean flying;
   private boolean nearGround;
   private UUID summonerUUID;
   private LivingEntity summoner;
   private final GroundPathNavigation groundNavigation;
   private final FlyingPathNavigation flyingNavigation;
   private LivingEntity breathHoverTarget;
   private Vec3 breathHoverPos;
   private int breathHoverTimeToLiveTicks;
   private boolean recallActive = false;
   private boolean recallAutoMount = false;
   private Vec3 recallLandPos = null;
   @Nullable
   public EndCrystal nearestCrystal;
   private static final EntityDataAccessor<Boolean> DATA_CONTROL_LOCKED = SynchedEntityData.m_135353_(
      HerobrineDragonEntity.class, EntityDataSerializers.f_135035_
   );

   public boolean isRecallAutoMount() {
      return this.recallAutoMount;
   }

   public void setRecallLandPos(Vec3 recallLandPos) {
      this.recallLandPos = recallLandPos;
   }

   public Vec3 getRecallLandPos() {
      return this.recallLandPos;
   }

   public boolean isRecallActive() {
      return this.recallActive;
   }

   public void setRecallActive(boolean recallActive) {
      this.recallActive = recallActive;
   }

   public HerobrineDragonEntity(EntityType<? extends HerobrineDragonEntity> type, Level level) {
      super(type, level);
      this.f_19811_ = true;
      this.f_21342_ = new HerobrineDragonEntity.DragonMoveController(this);
      this.animator = level.f_46443_ ? new DragonAnimator(this) : null;
      this.flyingNavigation = new FlyingPathNavigation(this, level);
      this.groundNavigation = new GroundPathNavigation(this, level);
      this.flyingNavigation.m_7008_(true);
      this.groundNavigation.m_7008_(true);
      this.f_21344_ = this.groundNavigation;
   }

   @NotNull
   public BodyRotationControl m_7560_() {
      return new HerobrineDragonEntity.DragonBodyController(this);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new RecallLandGoal(this));
      this.f_21345_.m_25352_(2, new SitWhenOrderedToGoal(this));
      this.f_21345_.m_25352_(3, new DragonOrbitLeaderGoal(this, 1.15, 20.0F, 50.0F, 180.0F));
      this.f_21345_.m_25352_(4, new WaterAvoidingRandomStrollGoal(this, 0.85F));
      this.f_21345_.m_25352_(5, new LookAtPlayerGoal(this, Player.class, 16.0F));
      this.f_21345_.m_25352_(6, new RandomLookAroundGoal(this));
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_CONTROL_LOCKED, false);
   }

   private boolean isControlLocked() {
      return (Boolean)this.f_19804_.m_135370_(DATA_CONTROL_LOCKED);
   }

   private void setControlLocked(boolean locked) {
      this.f_19804_.m_135381_(DATA_CONTROL_LOCKED, locked);
   }

   private boolean shouldApplyControlLocked() {
      return this.summoner instanceof Player;
   }

   public void m_7350_(@NotNull EntityDataAccessor<?> data) {
      if (f_21798_.equals(data)) {
         this.m_6210_();
      } else {
         super.m_7350_(data);
      }
   }

   public void m_7380_(@NotNull CompoundTag compound) {
      super.m_7380_(compound);
      if (this.summonerUUID != null) {
         compound.m_128362_("SummonerUUID", this.summonerUUID);
      }
   }

   public void m_7378_(@NotNull CompoundTag compound) {
      super.m_7378_(compound);
      this.m_146762_(0);
      if (compound.m_128403_("SummonerUUID")) {
         this.summonerUUID = compound.m_128342_("SummonerUUID");
      }
   }

   public boolean m_6072_() {
      return false;
   }

   public void setSummonerUUID(UUID summonerUUID) {
      this.summonerUUID = summonerUUID;
   }

   public UUID getSummonerUUID() {
      return this.summonerUUID;
   }

   public void setSummoner(LivingEntity summoner) {
      this.summoner = summoner;
   }

   public LivingEntity getSummoner() {
      return this.summoner;
   }

   public boolean canFly() {
      return true;
   }

   public boolean shouldFly() {
      return this.m_29443_() ? !this.m_20096_() : this.canFly() && !this.m_20069_() && !this.isNearGround();
   }

   public boolean m_29443_() {
      return this.flying;
   }

   public void setFlying(boolean flying) {
      this.flying = flying;
   }

   public boolean isNearGround() {
      return this.nearGround;
   }

   public void setNavigation(boolean flying) {
      this.f_21344_ = (PathNavigation)(flying ? this.flyingNavigation : this.groundNavigation);
   }

   @NotNull
   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor pLevel,
      @NotNull DifficultyInstance pDifficulty,
      @NotNull MobSpawnType pReason,
      @Nullable SpawnGroupData pSpawnData,
      @org.jetbrains.annotations.Nullable CompoundTag pDataTag
   ) {
      this.m_146762_(0);
      return super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
   }

   public static LivingEntity getNearestLivingEntity(Level level, Entity sourceEntity, double range) {
      AABB searchBox = sourceEntity.m_20191_().m_82400_(range);
      return level.m_45982_(
         level.m_6443_(
            LivingEntity.class,
            searchBox,
            e -> e != sourceEntity && !(e instanceof HerobrineDragonEntity) && !(e instanceof EnderHand) && !e.m_7307_(sourceEntity) && e.m_6084_()
         ),
         TargetingConditions.f_26872_,
         (LivingEntity)sourceEntity,
         sourceEntity.m_20185_(),
         sourceEntity.m_20186_(),
         sourceEntity.m_20189_()
      );
   }

   private void aimBodyAndHeadAt(LivingEntity target) {
      Vec3 from = this.m_20299_(1.0F);
      Vec3 to = target.m_20299_(1.0F);
      double dx = to.f_82479_ - from.f_82479_;
      double dz = to.f_82481_ - from.f_82481_;
      double dy = to.f_82480_ - from.f_82480_;
      double distXZ = Math.sqrt(dx * dx + dz * dz);
      float wantYaw = (float)(Mth.m_14136_(dz, dx) * (180.0 / Math.PI)) - 90.0F;
      float wantPitch = (float)(-(Mth.m_14136_(dy, distXZ) * (180.0 / Math.PI)));
      float yaw = Mth.m_14148_(this.m_146908_(), wantYaw, 10.0F);
      float pitch = Mth.m_14148_(this.m_146909_(), wantPitch, 6.0F);
      this.m_146922_(yaw);
      this.m_146926_(pitch);
      this.m_5616_(yaw);
      this.m_5618_(yaw);
   }

   public Vec3 beamMouthPos(float partial) {
      Vec3 eye = new Vec3(
         Mth.m_14139_((double)partial, this.f_19790_, this.m_20185_()),
         Mth.m_14139_((double)partial, this.f_19791_, this.m_20186_()) + (double)this.m_20192_(),
         Mth.m_14139_((double)partial, this.f_19792_, this.m_20189_())
      );
      float headYaw = Mth.m_14189_(partial, this.f_20886_, this.f_20885_);
      float headPitch = Mth.m_14179_(partial, this.f_19860_, this.m_146909_());
      Vec3 look = Vec3.m_82498_(headPitch, headYaw);
      double baseForward = Math.max(1.0, (double)this.m_20205_() * 0.7);
      boolean hasPlayerRider = this.m_146895_() instanceof Player;
      boolean usePhotonOffset = AnnoyingVillagersClientConfig.isPhotonModLoaded();
      if (this.m_9236_().m_5776_()) {
         usePhotonOffset = AnnoyingVillagersClientConfig.shouldUsePhotonWhenAvailable(AnnoyingVillagersClientConfig.VfxEffect.DRAGON_BEAM);
      }

      double extraUp = (hasPlayerRider ? 1.0 : (usePhotonOffset ? 3.0 : 4.0)) * (double)this.m_6134_();
      double extraForward = 5.2 * (double)this.m_6134_();
      double forward = baseForward + extraForward;
      return eye.m_82549_(look.m_82490_(forward)).m_82520_(0.0, extraUp, 0.0);
   }

   public void shootThunderBreathAtTarget(LivingEntity target) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if (target != null && target.m_6084_()) {
            this.breathHoverTarget = target;
            Vec3 position = this.m_20182_();
            if (this.m_20096_()) {
               position = position.m_82520_(0.0, 10.0, 0.0);
            }

            this.breathHoverPos = position;
            this.breathHoverTimeToLiveTicks = 110;
            this.m_21573_().m_26573_();
            if (!this.m_29443_() && this.canFly()) {
               this.liftOff();
            }

            this.setFlying(true);
            this.setNavigation(true);
            Vec3 mouth = this.m_146892_().m_82549_(this.m_20154_().m_82490_(Math.max(1.0, (double)this.m_20205_() * 0.6)));
            DragonBeamEntity beam = new DragonBeamEntity(
               (EntityType<? extends DragonBeamEntity>)AnnoyingVillagersModEntities.DRAGON_BEAM.get(),
               serverLevel,
               this,
               target,
               mouth.f_82479_,
               mouth.f_82480_,
               mouth.f_82481_,
               100,
               2
            );
            serverLevel.m_7967_(beam);
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.DRAGON_THUNDER_SHOOT_SOUND.get(), 2.0F, 1.0F);
         }
      }
   }

   public void shootMeteoriteAtTarget(LivingEntity target) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if (target != null && target.m_6084_()) {
            this.breathHoverTarget = target;
            Vec3 position = this.m_20182_();
            if (this.m_20096_()) {
               position = position.m_82520_(0.0, 10.0, 0.0);
            }

            this.breathHoverPos = position;
            this.breathHoverTimeToLiveTicks = 20;
            this.m_21573_().m_26573_();
            if (!this.m_29443_() && this.canFly()) {
               this.liftOff();
            }

            this.setFlying(true);
            this.setNavigation(true);
            Vec3 look = this.m_20154_();
            double baseForward = Math.max(1.0, (double)this.m_20205_() * 0.6);
            double extraForward = 7.5 * (double)this.m_6134_();
            double heightOffset = -1.0 * (double)this.m_6134_();
            Vec3 spawnPos = this.m_146892_().m_82549_(look.m_82490_(baseForward + extraForward)).m_82520_(0.0, heightOffset, 0.0);
            DragonMeteoriteEntity dragonMeteoriteEntity = new DragonMeteoriteEntity(
               (EntityType<DragonMeteoriteEntity>)AnnoyingVillagersModEntities.DRAGON_METEORITE.get(), serverLevel
            );
            dragonMeteoriteEntity.m_7678_(spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_, 0.0F, 0.0F);
            Vec3 aimPosition = new Vec3(target.m_20185_(), target.m_20227_(0.5), target.m_20189_());
            Vec3 portalAimPosition = HerobrinePortalCombatUtil.getProjectilePortalAim(this, target);
            if (portalAimPosition != null) {
               aimPosition = portalAimPosition;
            }

            dragonMeteoriteEntity.setPosToAim(aimPosition);
            dragonMeteoriteEntity.m_6518_(serverLevel, serverLevel.m_6436_(this.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
            dragonMeteoriteEntity.setOwner(this);
            serverLevel.m_7967_(dragonMeteoriteEntity);
            this.m_5496_(SoundEvents.f_11896_, 2.0F, 1.0F);
         }
      }
   }

   public void recallAndLand(boolean autoMount) {
      if (!this.m_9236_().m_5776_()) {
         if (this.summoner != null && this.summoner.m_6084_()) {
            this.recallActive = true;
            this.recallAutoMount = autoMount;
            this.recallLandPos = null;
            this.breathHoverTimeToLiveTicks = 0;
            this.breathHoverTarget = null;
            this.breathHoverPos = null;
         }
      }
   }

   private static boolean hasEnderSlayerScythe(Player p) {
      for (ItemStack s : p.m_150109_().f_35974_) {
         if (s.m_41720_() instanceof EnderSlayerScytheItem) {
            return true;
         }
      }

      for (ItemStack sx : p.m_150109_().f_35976_) {
         if (sx.m_41720_() instanceof EnderSlayerScytheItem) {
            return true;
         }
      }

      return false;
   }

   private static boolean isAllowedHeldCategory(Player p) {
      ItemStack main = p.m_21205_();
      if (main.m_41720_() instanceof EnderSlayerScytheItem) {
         return true;
      } else if (!(EpicFightCapabilities.getItemStackCapability(main) instanceof WeaponCapability weaponCap)) {
         return true;
      } else {
         WeaponCategory cat = weaponCap.getWeaponCategory();
         return cat == WeaponCategories.BOW || cat == WeaponCategories.CROSSBOW || cat == WeaponCategories.NOT_WEAPON;
      }
   }

   private void checkCrystals() {
      if (this.m_146895_() == null || !(this.m_146895_() instanceof EndCrystal)) {
         if (this.nearestCrystal != null) {
            if (this.nearestCrystal.m_213877_()) {
               this.nearestCrystal = null;
            } else if (this.f_19797_ % 10 == 0 && this.m_21223_() < this.m_21233_()) {
               this.m_21153_(this.m_21223_() + 1.0F);
            }
         }

         if (this.f_19796_.m_188503_(10) == 0) {
            List<EndCrystal> list = this.m_9236_().m_45976_(EndCrystal.class, this.m_20191_().m_82400_(32.0));
            EndCrystal endcrystalTemp = null;
            double d0 = Double.MAX_VALUE;

            for (EndCrystal endCrystal : list) {
               double d1 = endCrystal.m_20280_(this);
               if (d1 < d0) {
                  d0 = d1;
                  endcrystalTemp = endCrystal;
               }
            }

            if (endcrystalTemp == null && this.nearestCrystal != null) {
               this.nearestCrystal.m_31052_(null);
            }

            this.nearestCrystal = endcrystalTemp;
         }
      }
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         this.checkCrystals();
         if (this.nearestCrystal != null && !this.nearestCrystal.m_213877_()) {
            this.nearestCrystal.m_31052_(this.m_20183_());
         }

         if (this.breathHoverTimeToLiveTicks > 0) {
            if (this.shouldApplyControlLocked()) {
               if (!this.isControlLocked()) {
                  this.setControlLocked(true);
               }
            } else if (this.isControlLocked()) {
               this.setControlLocked(false);
            }

            this.breathHoverTimeToLiveTicks--;
            if (this.breathHoverPos == null) {
               this.breathHoverPos = this.m_20182_();
            }

            if (!this.m_29443_() && this.canFly()) {
               this.liftOff();
            }

            this.setFlying(true);
            this.setNavigation(true);
            this.m_21573_().m_26573_();
            this.m_20242_(true);
            if (this.breathHoverTarget != null) {
               this.aimBodyAndHeadAt(this.breathHoverTarget);
            }

            double y = this.breathHoverPos.f_82480_ + Math.sin((double)this.f_19797_ * 0.25) * 0.25;
            this.m_21566_().m_6849_(this.breathHoverPos.f_82479_, y, this.breathHoverPos.f_82481_, 0.12);
            Vec3 dv = this.m_20184_();
            this.m_20334_(dv.f_82479_ * 0.2, dv.f_82480_ * 0.6, dv.f_82481_ * 0.2);
            if (this.breathHoverTimeToLiveTicks <= 0) {
               this.m_20242_(false);
               this.breathHoverTarget = null;
               this.breathHoverPos = null;
            }
         } else {
            if (this.m_20068_()) {
               this.m_20242_(false);
            }

            if (this.isControlLocked()) {
               this.setControlLocked(false);
            }
         }

         if (this.summoner == null && this.summonerUUID != null) {
            Entity entity = ((ServerLevel)this.m_9236_()).m_8791_(this.summonerUUID);
            if (!(entity instanceof Player) && entity instanceof LivingEntity livingEntity) {
               this.summoner = livingEntity;
            } else {
               Player player = serverLevel.m_46003_(this.summonerUUID);
               if (player != null) {
                  this.summoner = player;
               }
            }
         }

         if (this.summoner != null && !this.summoner.m_6084_()) {
            this.summoner = null;
            this.summonerUUID = null;
            this.m_146870_();
         }

         if (this.summoner != null && this.summoner.m_6084_() && this.summoner instanceof Player player) {
            if (this.summoner.getPersistentData().m_128441_("DragonUUID") && !this.m_20148_().equals(this.summoner.getPersistentData().m_128342_("DragonUUID"))
               )
             {
               this.m_146870_();
               return;
            }

            if (!this.summoner.getPersistentData().m_128441_("DragonUUID")) {
               this.m_146870_();
               return;
            }

            if (!hasEnderSlayerScythe(player) || !isAllowedHeldCategory(player)) {
               player.getPersistentData().m_128473_("DragonUUID");
               this.m_146870_();
               return;
            }
         }

         if (this.summoner != null && this.summoner.m_6084_() && !this.m_21523_() && !this.m_20159_() && !this.m_217005_()) {
            double distSqr = this.m_20280_(this.summoner);
            double farDist = 320.0;
            if (distSqr > farDist * farDist) {
               if (!this.m_29443_() && this.canFly()) {
                  this.liftOff();
               }

               this.m_21573_().m_26573_();
               double toY = Mth.m_14008_(this.summoner.m_20186_() + 18.0, (double)this.m_9236_().m_141937_() + 6.0, (double)this.m_9236_().m_151558_() - 6.0);
               this.m_21566_().m_6849_(this.summoner.m_20185_(), toY, this.summoner.m_20189_(), 1.8);
            }
         }
      } else {
         this.animator.tick();
      }

      this.nearGround = this.m_20096_()
         || !this.m_9236_()
            .m_45756_(
               this,
               new AABB(this.m_20185_(), this.m_20186_(), this.m_20189_(), this.m_20185_(), this.m_20186_() - (double)(3.0F * this.m_6134_()), this.m_20189_())
            );
      boolean flying = this.shouldFly();
      if (flying != this.m_29443_()) {
         this.setFlying(flying);
         if (!this.m_9236_().m_5776_()) {
            this.setNavigation(flying);
         }
      }
   }

   public void m_7023_(@NotNull Vec3 vec3) {
      if (this.m_29443_()) {
         if (this.m_6109_()) {
            this.m_19920_(this.m_6113_(), vec3);
            this.m_6478_(MoverType.SELF, this.m_20184_());
            if (this.m_20184_().m_82556_() < 0.1) {
               this.m_20256_(this.m_20184_().m_82520_(0.0, Math.sin((double)((float)this.f_19797_ / 4.0F)) * 0.03, 0.0));
            }

            this.m_20256_(this.m_20184_().m_82490_(0.9F));
         }

         this.m_267651_(true);
      } else {
         super.m_7023_(vec3);
      }
   }

   @NotNull
   protected Vec3 m_274312_(@NotNull Player driver, @NotNull Vec3 move) {
      if (this.isControlLocked()) {
         return Vec3.f_82478_;
      } else {
         double moveSideways = move.f_82479_;
         double moveY = move.f_82480_;
         double moveForward = (double)Math.min(Math.abs(driver.f_20902_) + Math.abs(driver.f_20900_), 1.0F);
         if (this.m_29443_() && this.hasLocalDriver()) {
            moveForward = moveForward > 0.0 ? moveForward : 0.0;
            if (driver.f_20899_) {
               moveY = 1.0;
            } else if (AnnoyingVillagersModKeyMappings.DRAGON_FLIGHT_DESCENT_KEY.m_90857_()) {
               moveY = -1.0;
            } else if (moveForward > 0.0) {
               moveY = (double)(-driver.m_146909_() / 90.0F);
            }
         }

         float speed = this.m_245547_(driver);
         return new Vec3(moveSideways * (double)speed, moveY * (double)speed, moveForward * (double)speed);
      }
   }

   protected void m_274498_(@NotNull Player driver, @NotNull Vec3 move) {
      if (!this.isControlLocked()) {
         float yaw = driver.f_20885_;
         if (move.f_82481_ > 0.0) {
            yaw += (float)Mth.m_14136_((double)driver.f_20902_, (double)driver.f_20900_) * (180.0F / (float)Math.PI) - 90.0F;
         }

         this.f_20885_ = yaw;
         this.m_146926_(driver.m_146909_() * 0.68F);
         this.m_146922_(Mth.m_14094_(this.f_20885_, this.m_146908_(), 4.0F));
         if (this.m_6109_() && !this.m_29443_() && this.canFly() && driver.f_20899_) {
            this.liftOff();
         }
      }
   }

   public boolean m_6109_() {
      return this.isControlLocked() ? false : super.m_6109_();
   }

   protected float m_245547_(@NotNull Player driver) {
      return (float)this.m_21133_(this.m_29443_() ? Attributes.f_22280_ : Attributes.f_22279_);
   }

   public void liftOff() {
      if (this.canFly() && !this.m_20069_()) {
         Vec3 dv = this.m_20184_();
         if (this.m_20096_() || dv.f_82480_ < 0.15) {
            this.m_20334_(dv.f_82479_, 0.42, dv.f_82481_);
         }

         this.setFlying(true);
         if (!this.m_9236_().m_5776_()) {
            this.setNavigation(true);
         }
      }
   }

   protected float m_6118_() {
      return super.m_6118_() * (float)(this.canFly() ? 3 : 1);
   }

   public boolean m_142535_(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
      return !this.canFly() && super.m_142535_(pFallDistance, pMultiplier, pSource);
   }

   protected void m_6153_() {
      this.m_20153_();
      this.m_20256_(Vec3.f_82478_);
      this.m_146922_(this.f_19859_);
      this.m_5616_(this.f_20886_);
      if (this.f_20919_ >= this.getMaxDeathTime()) {
         this.m_142687_(RemovalReason.KILLED);
      }

      this.f_20919_++;
   }

   protected SoundEvent m_7515_() {
      return SoundEvents.f_11890_;
   }

   @Nullable
   protected SoundEvent m_7975_(@NotNull DamageSource damageSourceIn) {
      return SoundEvents.f_11895_;
   }

   public SoundEvent getStepSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.DRAGON_STEP_SOUND.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AnnoyingVillagersModSounds.DRAGON_DEATH_SOUND.get();
   }

   @NotNull
   public SoundEvent m_7866_(@NotNull ItemStack itemStackIn) {
      return SoundEvents.f_11912_;
   }

   public SoundEvent getWingsSound() {
      return SoundEvents.f_11893_;
   }

   protected void m_7355_(@NotNull BlockPos entityPos, @NotNull BlockState state) {
      if (!this.m_20069_()) {
         SoundType soundType = state.m_60827_();
         if (this.m_9236_().m_8055_(entityPos.m_7494_()).m_60734_() == Blocks.f_50125_) {
            soundType = Blocks.f_50125_.getSoundType(state, this.m_9236_(), entityPos, this);
         }

         this.m_5496_(this.getStepSound(), soundType.m_56773_(), soundType.m_56774_() * this.m_6100_());
      }
   }

   public int m_8100_() {
      return 240;
   }

   protected float m_6121_() {
      return this.m_6134_();
   }

   public float m_6100_() {
      return 2.0F - this.m_6134_();
   }

   public boolean isTamedFor(Player player) {
      return this.m_21824_() && this.m_21830_(player);
   }

   protected float m_6431_(@NotNull Pose poseIn, EntityDimensions sizeIn) {
      return sizeIn.f_20378_ * 1.2F;
   }

   public double m_6048_() {
      return (double)this.m_20206_() - 0.175;
   }

   public boolean m_6785_(double distanceToClosestPlayer) {
      return false;
   }

   public boolean m_6147_() {
      return false;
   }

   public void onWingsDown(float speed) {
      if (!this.m_20069_()) {
         float pitch = 1.0F - speed;
         float volume = 0.3F + (1.0F - speed) * 0.2F;
         float loudMul = 5.0F;
         pitch *= this.m_6100_();
         volume *= this.m_6121_();
         volume *= loudMul;
         this.m_9236_().m_7785_(this.m_20185_(), this.m_20186_(), this.m_20189_(), this.getWingsSound(), SoundSource.HOSTILE, volume, pitch, true);
      }
   }

   public boolean m_6469_(@NotNull DamageSource src, float par2) {
      if (this.m_6673_(src)) {
         return false;
      } else if (src.m_7639_() == this.summoner) {
         return false;
      } else {
         return src.m_7639_() instanceof Projectile ? super.m_6469_(src, par2 * 0.1F) : super.m_6469_(src, par2);
      }
   }

   public boolean m_7848_(@NotNull Animal mate) {
      if (mate == this) {
         return false;
      } else {
         return !(mate instanceof HerobrineDragonEntity) ? false : this.m_27593_() && mate.m_27593_();
      }
   }

   public AgeableMob m_142606_(@NotNull ServerLevel level, @NotNull AgeableMob mob) {
      return (AgeableMob)((EntityType)AnnoyingVillagersModEntities.HEROBRINE_DRAGON.get()).m_20615_(level);
   }

   public boolean m_7757_(@NotNull LivingEntity target, @NotNull LivingEntity owner) {
      return false;
   }

   public boolean m_6779_(@NotNull LivingEntity target) {
      return false;
   }

   public LivingEntity m_6688_() {
      return this.m_146895_() instanceof Player player ? player : null;
   }

   protected void m_20348_(@NotNull Entity passenger) {
      super.m_20348_(passenger);
      if (passenger instanceof Player) {
         passenger.m_146922_(this.m_146908_());
         passenger.m_146926_(this.m_146909_());
      }

      if (this.hasLocalDriver()) {
         MountControlsMessenger.sendControlsMessage();
         MountCameraManager.onDragonMount();
      }
   }

   protected void m_20351_(@NotNull Entity passenger) {
      if (this.hasLocalDriver()) {
         MountCameraManager.onDragonDismount();
      }

      super.m_20351_(passenger);
   }

   protected void m_19956_(@NotNull Entity ridden, @NotNull MoveFunction pCallback) {
      if (this.m_20363_(ridden)) {
         Vec3 rePos = new Vec3(0.0, this.m_6048_() + ridden.m_6049_(), (double)this.m_6134_())
            .m_82524_((float)Math.toRadians((double)(-this.f_20883_)))
            .m_82549_(this.m_20182_());
         pCallback.m_20372_(ridden, rePos.f_82479_, rePos.f_82480_, rePos.f_82481_);
         if (this.m_146895_() instanceof LivingEntity) {
            ridden.f_19860_ = ridden.m_146909_();
            ridden.f_19859_ = ridden.m_146908_();
            ridden.m_5618_(this.f_20883_);
         }
      }
   }

   public boolean m_6673_(DamageSource src) {
      Entity srcEnt = src.m_7639_();
      return srcEnt == null || srcEnt != this && !this.m_20363_(srcEnt) ? super.m_6673_(src) : true;
   }

   public float getHealthFraction() {
      return this.m_21223_() / this.m_21233_();
   }

   public int getMaxDeathTime() {
      return 120;
   }

   public void m_142687_(@NotNull RemovalReason pReason) {
      if (this.m_146895_() instanceof EndCrystal endCrystal && this.m_9236_() instanceof ServerLevel serverLevel) {
         endCrystal.m_6469_(serverLevel.m_269111_().m_269264_(), 1.0F);
      }

      if (this.m_146895_() == null || !(this.m_146895_() instanceof EndCrystal)) {
         if (this.nearestCrystal != null) {
            this.nearestCrystal.m_31052_(null);
         }

         super.m_142687_(pReason);
      }
   }

   public void m_6210_() {
      double posXTmp = this.m_20185_();
      double posYTmp = this.m_20186_();
      double posZTmp = this.m_20189_();
      boolean onGroundTmp = this.m_20096_();
      super.m_6210_();
      this.m_6034_(posXTmp, posYTmp, posZTmp);
      this.m_6853_(onGroundTmp);
   }

   @NotNull
   public EntityDimensions m_6972_(@NotNull Pose poseIn) {
      float height = this.m_21825_() ? 2.15F : 2.75F;
      float scale = this.m_6134_();
      return new EntityDimensions(2.75F * scale, height * scale, false);
   }

   public boolean m_6162_() {
      return false;
   }

   public DragonAnimator getAnimator() {
      return this.animator;
   }

   public boolean m_6040_() {
      return true;
   }

   public boolean m_5825_() {
      return true;
   }

   public boolean m_5830_() {
      if (this.f_19794_) {
         return false;
      } else {
         AABB collider = this.m_20191_().m_82406_((double)(this.m_20205_() * 0.2F));
         return BlockPos.m_121921_(collider)
            .anyMatch(
               pos -> {
                  BlockState state = this.m_9236_().m_8055_(pos);
                  return !state.m_60795_()
                     && state.m_60828_(this.m_9236_(), pos)
                     && Shapes.m_83157_(
                        state.m_60812_(this.m_9236_(), pos).m_83216_((double)pos.m_123341_(), (double)pos.m_123342_(), (double)pos.m_123343_()),
                        Shapes.m_83064_(collider),
                        BooleanOp.f_82689_
                     );
               }
            );
      }
   }

   @NotNull
   public Vec3 m_7371_(float p_20309_) {
      return new Vec3(this.m_20185_(), this.m_20186_() + (double)this.m_20206_(), this.m_20189_());
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public boolean hasLocalDriver() {
      if (this.m_146895_() instanceof Player p && p.m_7578_()) {
         return true;
      }

      return false;
   }

   public static Builder createAttributes() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22279_, 0.3)
         .m_22268_(Attributes.f_22276_, 150.0)
         .m_22268_(Attributes.f_22277_, 16.0)
         .m_22268_(Attributes.f_22278_, 1.0)
         .m_22268_(Attributes.f_22281_, 8.0)
         .m_22268_(Attributes.f_22280_, 0.32);
   }

   public void aimBodyAndHeadAt(Vec3 to, float maxYawStep, float maxPitchStep) {
      Vec3 from = this.m_20299_(1.0F);
      double dx = to.f_82479_ - from.f_82479_;
      double dz = to.f_82481_ - from.f_82481_;
      double dy = to.f_82480_ - from.f_82480_;
      double distXZ = Math.sqrt(dx * dx + dz * dz);
      float wantYaw = (float)(Mth.m_14136_(dz, dx) * (180.0 / Math.PI)) - 90.0F;
      float wantPitch = (float)(-(Mth.m_14136_(dy, distXZ) * (180.0 / Math.PI)));
      float yaw = Mth.m_14148_(this.m_146908_(), wantYaw, maxYawStep);
      float pitch = Mth.m_14148_(this.m_146909_(), wantPitch, maxPitchStep);
      this.m_146922_(yaw);
      this.m_146926_(pitch);
      this.m_5616_(yaw);
      this.m_5618_(yaw);
   }

   public void m_6667_(@NotNull DamageSource source) {
      if (this.m_9236_() instanceof ServerLevel && this.summoner != null && this.summoner instanceof Player player && player.m_6084_()) {
         player.m_36335_().m_41524_((Item)AnnoyingVillagersModItems.ENDER_SLAYER_SCYTHE.get(), 3600);
         if (player.getPersistentData().m_128441_("DragonUUID") && this.m_20148_().equals(player.getPersistentData().m_128342_("DragonUUID"))) {
            player.getPersistentData().m_128473_("DragonUUID");
         }
      }

      super.m_6667_(source);
   }

   public static class DragonBodyController extends BodyRotationControl {
      private final HerobrineDragonEntity dragon;

      public DragonBodyController(HerobrineDragonEntity dragon) {
         super(dragon);
         this.dragon = dragon;
      }

      public void m_8121_() {
         this.dragon.f_20883_ = this.dragon.m_146908_();
         this.dragon.f_20885_ = Mth.m_14094_(this.dragon.f_20885_, this.dragon.f_20883_, (float)this.dragon.m_8085_());
      }
   }

   public static class DragonMoveController extends MoveControl {
      private final HerobrineDragonEntity dragon;

      public DragonMoveController(HerobrineDragonEntity dragon) {
         super(dragon);
         this.dragon = dragon;
      }

      public void m_8126_() {
         if (!this.dragon.m_29443_()) {
            super.m_8126_();
         } else {
            if (this.f_24981_ == Operation.MOVE_TO) {
               this.f_24981_ = Operation.WAIT;
               double xDif = this.f_24975_ - this.f_24974_.m_20185_();
               double yDif = this.f_24976_ - this.f_24974_.m_20186_();
               double zDif = this.f_24977_ - this.f_24974_.m_20189_();
               double sq = xDif * xDif + yDif * yDif + zDif * zDif;
               if (sq < 2.5000003E-7F) {
                  this.f_24974_.m_21567_(0.0F);
                  this.f_24974_.m_21564_(0.0F);
                  return;
               }

               float speed = (float)(this.f_24978_ * this.f_24974_.m_21133_(Attributes.f_22280_));
               double distSq = Math.sqrt(xDif * xDif + zDif * zDif);
               this.f_24974_.m_7910_(speed);
               if (Math.abs(yDif) > 1.0E-5F || Math.abs(distSq) > 1.0E-5F) {
                  this.f_24974_.m_21567_((float)yDif * speed);
               }

               float yaw = (float)(Mth.m_14136_(zDif, xDif) * 180.0F / (float)Math.PI) - 90.0F;
               this.f_24974_.m_146922_(this.m_24991_(this.f_24974_.m_146908_(), yaw, 6.0F));
            } else {
               this.f_24974_.m_21567_(0.0F);
               this.f_24974_.m_21564_(0.0F);
            }
         }
      }
   }
}
