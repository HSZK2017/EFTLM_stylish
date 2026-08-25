package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.clazz.BbqCombatMode;
import com.pla.annoyingvillagers.clazz.BurstProtectEntity;
import com.pla.annoyingvillagers.clazz.CombatVoiceLineEntity;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.clazz.SauceType;
import com.pla.annoyingvillagers.clazz.TridentMode;
import com.pla.annoyingvillagers.entity.goal.EscapeAvoidGoal;
import com.pla.annoyingvillagers.entity.goal.FollowEscapeLeaderGoal;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BbqEntity extends Chicken implements BurstProtectEntity, CombatVoiceLineEntity {
   @Nullable
   private BlueDemonEntity leader;
   @Nullable
   private UUID leaderUUID;
   @Nullable
   private UUID combatTargetUUID;
   private BbqCombatMode combatMode = BbqCombatMode.IDLE;
   private int formationSide = 1;
   private int chainShotsRemaining;
   private int chainShotInterval;
   private int chainShotCooldown;
   private int combatModeTicks;
   private int meleeCooldown;
   private float orbitAngle;
   private float orbitRadius = 5.0F;
   private SauceType sauceType = SauceType.BBQ_SAUCE;
   private int retreatTicks;
   @Nullable
   private BbqEntity sauceLeader;
   @Nullable
   private UUID sauceLeaderUUID;
   private boolean escapeMode;
   private boolean escapeFlying;
   private int escapeLocomotionTicks;
   private float escapeFlightHeight = 1.5F;
   private boolean deathAssemblyMode;
   private int deathAssemblyTicks;
   private double deathAssemblyX;
   private double deathAssemblyY;
   private double deathAssemblyZ;
   private boolean pendingDeathMainhandTrident;
   private boolean pendingDeathOffhandChestplate;
   @Nullable
   private UUID pendingDeathEscapeLeaderUUID;
   private boolean deathWatchMode;
   private boolean selfKill = false;
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

   public boolean isEscapeFlying() {
      return this.escapeMode && this.escapeFlying;
   }

   public double getEscapeFlightHeight() {
      return (double)this.escapeFlightHeight;
   }

   public void tickEscapeLocomotionMode() {
      if (!this.escapeMode) {
         this.escapeFlying = false;
         this.escapeLocomotionTicks = 0;
         this.escapeFlightHeight = 1.5F;
         this.m_20242_(false);
      } else if (this.escapeLocomotionTicks > 0) {
         this.escapeLocomotionTicks--;
      } else {
         float airChance;
         if (this.getSauceLeader() != null) {
            airChance = 0.45F;
         } else {
            airChance = 0.3F;
         }

         if (this.sauceType == SauceType.SWEET_ONION_SAUCE) {
            airChance += 0.2F;
         }

         this.escapeFlying = this.f_19796_.m_188501_() < airChance;
         this.escapeLocomotionTicks = this.f_19796_.m_216339_(25, 60);
         this.escapeFlightHeight = 1.0F + this.f_19796_.m_188501_() * 4.0F;
         if (!this.escapeFlying) {
            this.m_20242_(false);
            this.f_19789_ = 0.0F;
         }
      }
   }

   public void moveEscapeAerialTowards(double x, double y, double z, double accel, double drag) {
      this.moveAerialTowards(x, y, z, accel, drag);
   }

   public BbqEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<? extends BbqEntity>)AnnoyingVillagersModEntities.BBQ.get(), level);
   }

   public BbqEntity(EntityType<? extends BbqEntity> type, Level level) {
      super(type, level);
      this.m_274367_(0.6F);
      this.f_21364_ = 0;
      this.m_21557_(false);
      this.m_20340_(true);
      this.m_21530_();
      this.m_21409_(EquipmentSlot.MAINHAND, 0.0F);
      this.m_21409_(EquipmentSlot.OFFHAND, 0.0F);
   }

   public void startLeaderDeathWatch(@Nullable BlueDemonEntity leader) {
      if (leader != null && leader.m_6084_()) {
         this.deathWatchMode = true;
         this.deathAssemblyMode = false;
         this.escapeMode = false;
         this.retreatTicks = 0;
         this.clearCombat();
         this.setLeader(leader);
         this.sauceLeader = null;
         this.sauceLeaderUUID = null;
         this.escapeFlying = false;
         this.escapeLocomotionTicks = 0;
         this.m_21573_().m_26573_();
         this.m_20256_(Vec3.f_82478_);
         this.m_20242_(false);
         this.f_19789_ = 0.0F;
      }
   }

   private void tickLeaderDeathWatch() {
      BlueDemonEntity leader = this.getLeader();
      if (leader != null && leader.m_6084_() && leader.isInFinalDeathSequence()) {
         this.clearCombat();
         this.m_20242_(false);
         this.f_19789_ = 0.0F;
         this.m_21563_().m_24960_(leader, 45.0F, 45.0F);

         float baseOffset = switch (this.sauceType) {
            case BBQ_SAUCE -> 0.0F;
            case HONEY_MUSTARD_SAUCE -> (float) (Math.PI / 2);
            case SOY_SAUCE -> (float) Math.PI;
            case SWEET_ONION_SAUCE -> (float) (Math.PI * 3.0 / 2.0);
         };

         double radius = switch (this.sauceType) {
            case BBQ_SAUCE -> 1.1;
            case HONEY_MUSTARD_SAUCE -> 1.45;
            case SOY_SAUCE -> 1.45;
            case SWEET_ONION_SAUCE -> 1.8;
         };
         float angle = (float)leader.f_19797_ * 0.18F + baseOffset;
         double x = leader.m_20185_() + (double)Mth.m_14089_(angle) * radius;
         double z = leader.m_20189_() + (double)Mth.m_14031_(angle) * radius;
         if (this.m_20280_(leader) > 16.0) {
            this.m_21573_().m_5624_(leader, 1.8);
         } else {
            this.m_21573_().m_26519_(x, leader.m_20186_(), z, 1.35);
         }
      } else {
         this.deathWatchMode = false;
      }
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new EscapeAvoidGoal(this, Player.class, 12.0F, 2.0, 2.0));
      this.f_21345_.m_25352_(1, new EscapeAvoidGoal(this, HerobrineMob.class, 12.0F, 2.0, 2.0));
      this.f_21345_.m_25352_(1, new EscapeAvoidGoal(this, Monster.class, 12.0F, 2.0, 2.0));
      this.f_21345_.m_25352_(1, new EscapeAvoidGoal(this, PlayerNpcEntity.class, 12.0F, 2.0, 2.0));
      this.f_21345_.m_25352_(1, new EscapeAvoidGoal(this, AVNpc.class, 12.0F, 2.0, 2.0));
      this.f_21345_.m_25352_(2, new FollowEscapeLeaderGoal(this));
      this.f_21345_.m_25352_(3, new RandomLookAroundGoal(this));
   }

   public boolean m_7301_(MobEffectInstance effect) {
      if (effect.m_19544_() == MobEffects.f_19614_) {
         if (!this.m_9236_().f_46443_ && this.m_6084_()) {
            this.m_5634_(4.0F);
         }

         return false;
      } else {
         return super.m_7301_(effect);
      }
   }

   public void setLeader(@Nullable BlueDemonEntity leader) {
      this.leader = leader;
      this.leaderUUID = leader == null ? null : leader.m_20148_();
   }

   public boolean isEscapeMode() {
      return this.escapeMode;
   }

   public void enterEscapeMode(@Nullable BbqEntity sauceLeader) {
      this.escapeMode = true;
      this.deathWatchMode = false;
      this.retreatTicks = 0;
      this.clearCombat();
      this.leader = null;
      this.leaderUUID = null;
      if (sauceLeader != null && sauceLeader.m_6084_() && sauceLeader != this) {
         this.sauceLeader = sauceLeader;
         this.sauceLeaderUUID = sauceLeader.m_20148_();
      } else {
         this.sauceLeader = null;
         this.sauceLeaderUUID = null;
      }

      this.escapeFlying = false;
      this.escapeLocomotionTicks = 0;
      this.escapeFlightHeight = 1.5F;
      this.m_21573_().m_26573_();
      this.m_20256_(Vec3.f_82478_);
      this.m_20242_(false);
      this.f_19789_ = 0.0F;
   }

   public void startDeathAssembly(double x, double y, double z, boolean giveMainhandTrident, boolean giveOffhandChestplate, @Nullable BbqEntity escapeLeader) {
      this.escapeMode = false;
      this.clearCombat();
      this.retreatTicks = 0;
      this.deathWatchMode = false;
      this.deathAssemblyMode = true;
      this.deathAssemblyTicks = 1;
      this.deathAssemblyX = x;
      this.deathAssemblyY = y;
      this.deathAssemblyZ = z;
      this.pendingDeathMainhandTrident = giveMainhandTrident;
      this.pendingDeathOffhandChestplate = giveOffhandChestplate;
      this.pendingDeathEscapeLeaderUUID = escapeLeader != null && escapeLeader != this && escapeLeader.m_6084_() ? escapeLeader.m_20148_() : null;
      this.leader = null;
      this.leaderUUID = null;
      this.sauceLeader = null;
      this.sauceLeaderUUID = null;
      this.escapeFlying = false;
      this.escapeLocomotionTicks = 0;
      this.m_21573_().m_26573_();
      this.m_20256_(Vec3.f_82478_);
      double offsetX = 0.0;
      double offsetZ = 0.0;
      switch (this.sauceType) {
         case BBQ_SAUCE:
            offsetX = 0.0;
            offsetZ = 0.0;
            break;
         case HONEY_MUSTARD_SAUCE:
            offsetX = 0.9;
            offsetZ = 0.0;
            break;
         case SOY_SAUCE:
            offsetX = -0.9;
            offsetZ = 0.0;
            break;
         case SWEET_ONION_SAUCE:
            offsetX = 0.0;
            offsetZ = 0.9;
      }

      this.m_6027_(x + offsetX, y, z + offsetZ);
      this.m_20242_(false);
      this.f_19789_ = 0.0F;
   }

   private void tickDeathAssembly() {
      this.clearCombat();
      this.m_21573_().m_26573_();
      this.m_20256_(Vec3.f_82478_);
      this.m_20242_(false);
      this.f_19789_ = 0.0F;
      this.m_21563_().m_24946_(this.deathAssemblyX, this.deathAssemblyY + 0.5, this.deathAssemblyZ);
      if (this.deathAssemblyTicks > 0) {
         this.deathAssemblyTicks--;
      }

      if (this.deathAssemblyTicks <= 0) {
         this.deathAssemblyMode = false;
         if (this.pendingDeathMainhandTrident) {
            this.m_21008_(InteractionHand.MAIN_HAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get()));
         }

         if (this.pendingDeathOffhandChestplate) {
            this.m_21008_(InteractionHand.OFF_HAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.BLUE_DEMON_CHESTPLATE.get()));
         }

         BbqEntity escapeLeader = null;
         if (this.pendingDeathEscapeLeaderUUID != null
            && this.m_9236_() instanceof ServerLevel serverLevel
            && serverLevel.m_8791_(this.pendingDeathEscapeLeaderUUID) instanceof BbqEntity bbq
            && bbq.m_6084_()) {
            escapeLeader = bbq;
         }

         this.pendingDeathMainhandTrident = false;
         this.pendingDeathOffhandChestplate = false;
         this.pendingDeathEscapeLeaderUUID = null;
         this.enterEscapeMode(escapeLeader);
      }
   }

   @Nullable
   public BbqEntity getSauceLeader() {
      if (this.sauceLeader != null && this.sauceLeader.m_6084_()) {
         return this.sauceLeader;
      } else if (!this.m_9236_().f_46443_
         && this.sauceLeaderUUID != null
         && ((ServerLevel)this.m_9236_()).m_8791_(this.sauceLeaderUUID) instanceof BbqEntity bbq
         && bbq.m_6084_()) {
         this.sauceLeader = bbq;
         return bbq;
      } else {
         this.sauceLeader = null;
         this.sauceLeaderUUID = null;
         return null;
      }
   }

   @Nullable
   public BlueDemonEntity getLeader() {
      if (this.leader != null && this.leader.m_6084_()) {
         return this.leader;
      } else if (!this.m_9236_().f_46443_
         && this.leaderUUID != null
         && ((ServerLevel)this.m_9236_()).m_8791_(this.leaderUUID) instanceof BlueDemonEntity blueDemon
         && blueDemon.m_6084_()) {
         this.leader = blueDemon;
         return blueDemon;
      } else {
         return null;
      }
   }

   public SauceType getSauceType() {
      return this.sauceType;
   }

   public void setSauceType(SauceType sauceType) {
      this.sauceType = sauceType == null ? SauceType.BBQ_SAUCE : sauceType;
      this.m_6593_(Component.m_237115_(this.sauceType.getTranslationKey()));
      if (this.sauceType.isShockSauce() && this.m_21205_().m_41619_()) {
         this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get()));
      }
   }

   public BbqCombatMode getCombatMode() {
      return this.combatMode;
   }

   public void setCombatTarget(@Nullable LivingEntity target) {
      this.combatTargetUUID = target == null ? null : target.m_20148_();
   }

   @Nullable
   public LivingEntity getCombatTarget() {
      if (this.escapeMode) {
         return null;
      } else {
         if (!this.m_9236_().f_46443_ && this.combatTargetUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.combatTargetUUID) instanceof LivingEntity livingEntity && livingEntity.m_6084_()) {
               return livingEntity;
            }

            this.combatTargetUUID = null;
         }

         BlueDemonEntity leader = this.getLeader();
         if (leader != null) {
            LivingEntity target = leader.m_5448_();
            if (target != null && target.m_6084_()) {
               return target;
            }
         }

         return null;
      }
   }

   public void startRetreat() {
      if (this.m_6084_()) {
         this.clearCombat();
         this.retreatTicks = 60 + this.f_19796_.m_188503_(20);
         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            serverLevel.m_7654_()
               .m_6846_()
               .m_240416_(Component.m_237113_("<" + this.m_7755_().getString() + "> " + Component.m_237115_("subtitles.bbq_retreat").getString()), false);
         }
      }
   }

   private void tickRetreat() {
      BlueDemonEntity leader = this.getLeader();
      if (leader != null && leader.m_6084_()) {
         Vec3 away = this.m_20182_().m_82546_(leader.m_20182_());
         away = new Vec3(away.f_82479_, 0.0, away.f_82481_);
         if (away.m_82556_() < 1.0E-4) {
            away = new Vec3(this.f_19796_.m_188500_() - 0.5, 0.0, this.f_19796_.m_188500_() - 0.5);
         }

         away = away.m_82541_();
         double x = this.m_20185_() + away.f_82479_ * 10.0;
         double z = this.m_20189_() + away.f_82481_ * 10.0;
         if (this.sauceType == SauceType.SWEET_ONION_SAUCE) {
            this.m_20242_(true);
            this.m_21573_().m_26573_();
            this.moveAerialTowards(x, this.m_20186_() + 1.5, z, 0.22, 0.88);
         } else {
            this.m_20242_(false);
            this.m_21573_().m_26519_(x, this.m_20186_(), z, 1.45);
         }

         this.retreatTicks--;
         if (this.retreatTicks <= 0) {
            this.m_146870_();
         }
      } else {
         this.m_146870_();
      }
   }

   public void startOrbit(@Nullable LivingEntity target, int ticks) {
      if (target == null) {
         this.clearCombat();
      } else {
         this.setCombatTarget(target);
         this.combatMode = BbqCombatMode.ORBIT;
         this.combatModeTicks = Math.max(this.combatModeTicks, ticks);
         if (this.f_19796_.m_188503_(4) == 0) {
            this.orbitRadius = new Random().nextFloat(3.5F, 6.5F);
         }

         if (this.f_19796_.m_188503_(6) == 0) {
            this.formationSide = -this.formationSide;
         }
      }
   }

   public void startHeadAttack(@Nullable LivingEntity target, int ticks) {
      if (target == null) {
         this.clearCombat();
      } else {
         this.setCombatTarget(target);
         this.combatMode = BbqCombatMode.HEAD_ATTACK;
         this.combatModeTicks = ticks;
         this.chainShotsRemaining = 0;
         this.chainShotCooldown = 0;
         this.orbitAngle = this.f_19796_.m_188501_() * (float) (Math.PI * 2);
         this.m_21573_().m_26573_();
      }
   }

   public boolean isHeadAttacking() {
      return this.combatMode == BbqCombatMode.HEAD_ATTACK && this.combatModeTicks > 0;
   }

   public void clearCombat() {
      this.combatMode = BbqCombatMode.IDLE;
      this.combatModeTicks = 0;
      this.combatTargetUUID = null;
      this.chainShotsRemaining = 0;
      this.chainShotCooldown = 0;
      this.m_20242_(false);
   }

   public void shootChain(LivingEntity target, int shots, int intervalTicks) {
      if (target != null && this.chainShotsRemaining <= 0 && this.chainShotCooldown <= 0) {
         this.setCombatTarget(target);
         this.chainShotsRemaining = Math.max(0, shots);
         this.chainShotInterval = Math.max(1, intervalTicks);
         this.chainShotCooldown = this.chainShotInterval;
      }
   }

   public void shootCluster(LivingEntity target, int eggCount, float power, float inaccuracy) {
      if (target != null && this.chainShotsRemaining <= 0 && this.chainShotCooldown <= 0) {
         this.setCombatTarget(target);

         for (int i = 0; i < eggCount; i++) {
            this.firePoisonEgg(target, power, inaccuracy);
         }

         this.chainShotCooldown = 45;
      }
   }

   private void tickManualAttacks() {
      if (this.chainShotsRemaining > 0) {
         LivingEntity target = this.getCombatTarget();
         if (target == null) {
            this.chainShotsRemaining = 0;
         } else if (this.chainShotCooldown <= 0) {
            this.firePoisonEgg(target, 1.45F, 6.0F);
            this.chainShotsRemaining--;
            if (this.chainShotsRemaining > 0) {
               this.chainShotCooldown = this.chainShotInterval;
            } else {
               this.chainShotCooldown = 35;
            }
         }
      }
   }

   private void tickShockTouch(LivingEntity target) {
      if (this.sauceType.isShockSauce()) {
         if (this.meleeCooldown <= 0) {
            if (!(this.m_20275_(target.m_20185_(), target.m_20188_(), target.m_20189_()) > 2.25)) {
               target.m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21133_(Attributes.f_22281_));
               if (this.f_19796_.m_188501_() < 0.35F) {
                  target.m_7292_(new MobEffectInstance((MobEffect)AnnoyingVillagersModMobEffects.ELECTRIFY.get(), 20, 1));
               }

               this.m_5496_(SoundEvents.f_12514_, 1.0F, 1.0F + this.f_19796_.m_188501_() * 0.2F);
               this.meleeCooldown = 12;
            }
         }
      }
   }

   private void tickSweetOnionSupport() {
      BlueDemonEntity leader = this.getLeader();
      LivingEntity enemy = this.getCombatTarget();
      this.m_20242_(false);
      this.f_19789_ = 0.0F;
      if (leader != null && leader.m_6084_()) {
         if (this.m_21205_().m_41619_()) {
            BlueDemonThrownTridentEntity trident = leader.getNearestGroundedOwnedTrident(12.0);
            if (trident != null) {
               if (this.m_20280_(trident) > 2.25) {
                  this.m_21573_().m_26519_(trident.m_20185_(), trident.m_20186_(), trident.m_20189_(), 1.35);
               } else {
                  ItemStack carried = new ItemStack((ItemLike)AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get());
                  carried.m_41784_().m_128359_("CarriedTridentMode", trident.getMode().name());
                  trident.m_146870_();
                  this.m_8061_(EquipmentSlot.MAINHAND, carried);
                  this.m_21573_().m_26573_();
               }

               return;
            }
         }

         if (this.m_21205_().m_150930_((Item)AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get())) {
            if ((double)this.m_20270_(leader) > 2.5) {
               this.m_21573_().m_5624_(leader, 1.35);
            } else if (this.m_9236_() instanceof ServerLevel serverLevel) {
               TridentMode mode = TridentMode.DEFAULT;
               CompoundTag tag = this.m_21205_().m_41783_();
               if (tag != null && tag.m_128441_("CarriedTridentMode")) {
                  try {
                     mode = TridentMode.valueOf(tag.m_128461_("CarriedTridentMode"));
                  } catch (IllegalArgumentException var8) {
                  }
               }

               BlockPos standPos = serverLevel.m_5452_(
                  Types.MOTION_BLOCKING_NO_LEAVES,
                  BlockPos.m_274561_(
                     leader.m_20185_() + (this.f_19796_.m_188500_() - 0.5) * 2.0,
                     leader.m_20186_(),
                     leader.m_20189_() + (this.f_19796_.m_188500_() - 0.5) * 2.0
                  )
               );
               BlueDemonThrownTridentEntity placed = new BlueDemonThrownTridentEntity(
                  (EntityType<? extends ThrownTrident>)AnnoyingVillagersModEntities.BLUE_DEMON_THROWN_TRIDENT.get(), serverLevel
               );
               placed.setMode(mode);
               placed.assignSpawnSequence(leader);
               serverLevel.m_7967_(placed);
               placed.placeAsGroundedSupport(leader, standPos);
               placed.trimOldGroundedTridentsAroundOwnerOnSpawn();
               this.m_8061_(EquipmentSlot.MAINHAND, ItemStack.f_41583_);
               this.m_21573_().m_26573_();
            }
         } else if (enemy != null && enemy.m_6084_()) {
            Vec3 away = leader.m_20182_().m_82546_(enemy.m_20182_());
            away = new Vec3(away.f_82479_, 0.0, away.f_82481_);
            if (away.m_82556_() < 1.0E-4) {
               away = this.m_20182_().m_82546_(enemy.m_20182_());
               away = new Vec3(away.f_82479_, 0.0, away.f_82481_);
            }

            if (away.m_82556_() < 1.0E-4) {
               away = new Vec3(1.0, 0.0, 0.0);
            }

            away = away.m_82541_().m_82490_(8.0 + this.f_19796_.m_188500_() * 3.0);
            Vec3 desired = leader.m_20182_().m_82549_(away);
            this.m_21573_().m_26519_(desired.f_82479_, leader.m_20186_(), desired.f_82481_, 1.4);
            this.m_21563_().m_24960_(enemy, 30.0F, 30.0F);
         } else {
            this.tickLeaderFollow();
         }
      } else {
         this.tickLeaderFollow();
      }
   }

   private void firePoisonEgg(LivingEntity target, float power, float inaccuracy) {
      if (!this.m_9236_().f_46443_) {
         ThrownPoisonEggEntity projectile = new ThrownPoisonEggEntity(
            (EntityType<? extends ThrownPoisonEggEntity>)AnnoyingVillagersModEntities.THROWN_POISON_EGG.get(), this, this.m_9236_()
         );
         double dX = target.m_20185_() - this.m_20185_();
         double dY = target.m_20188_() - projectile.m_20186_();
         double dZ = target.m_20189_() - this.m_20189_();
         projectile.m_5602_(this);
         projectile.m_6034_(this.m_20185_(), this.m_20188_() - 0.1, this.m_20189_());
         projectile.m_6686_(dX, dY, dZ, power, inaccuracy);
         this.m_9236_().m_7967_(projectile);
      }
   }

   private void moveAerialTowards(double x, double y, double z, double accel, double drag) {
      Vec3 wanted = new Vec3(x - this.m_20185_(), y - this.m_20186_(), z - this.m_20189_());
      double len = wanted.m_82553_();
      if (len < 0.05) {
         this.m_20256_(this.m_20184_().m_82490_(drag));
      } else {
         Vec3 desired = wanted.m_82541_().m_82490_(accel);
         Vec3 next = this.m_20184_().m_82490_(drag).m_82549_(desired);
         this.m_20256_(next);
         this.f_19812_ = true;
         float yaw = (float)(Mth.m_14136_(next.f_82481_, next.f_82479_) * 180.0F / (float)Math.PI) - 90.0F;
         this.m_146922_(Mth.m_14189_(0.3F, this.m_146908_(), yaw));
         this.f_20883_ = this.m_146908_();
         this.f_20885_ = this.m_146908_();
      }
   }

   public void startGroundOrbit(@Nullable LivingEntity target, int ticks) {
      if (target == null) {
         this.clearCombat();
      } else {
         this.setCombatTarget(target);
         this.combatMode = BbqCombatMode.GROUND_ORBIT;
         this.combatModeTicks = Math.max(this.combatModeTicks, ticks);
         this.m_20242_(false);
         if (this.f_19796_.m_188503_(4) == 0) {
            this.orbitRadius = new Random().nextFloat(3.5F, 6.5F);
         }

         if (this.f_19796_.m_188503_(6) == 0) {
            this.formationSide = -this.formationSide;
         }
      }
   }

   private void tickGroundOrbit(LivingEntity target) {
      this.m_20242_(false);
      this.f_19789_ = 0.0F;
      if (this.f_19796_.m_188503_(70) == 0) {
         this.formationSide = -this.formationSide;
      }

      if (this.f_19796_.m_188503_(50) == 0) {
         this.orbitRadius = new Random().nextFloat(3.5F, 6.5F);
      }

      this.orbitAngle = this.orbitAngle + 0.12F * (float)this.formationSide;
      double x = target.m_20185_() + (double)(Mth.m_14089_(this.orbitAngle) * this.orbitRadius);
      double z = target.m_20189_() + (double)(Mth.m_14031_(this.orbitAngle) * this.orbitRadius);
      if ((double)this.m_20270_(target) < 2.5) {
         Vec3 away = this.m_20182_().m_82546_(target.m_20182_());
         away = new Vec3(away.f_82479_, 0.0, away.f_82481_);
         if (away.m_82556_() > 1.0E-4) {
            Vec3 desired = this.m_20182_().m_82549_(away.m_82541_().m_82490_(1.75));
            this.m_21573_().m_26519_(desired.f_82479_, this.m_20186_(), desired.f_82481_, 1.35);
            return;
         }
      }

      this.m_21573_().m_26519_(x, target.m_20186_(), z, 1.25);
   }

   private void tickOrbit(LivingEntity target) {
      this.m_20242_(true);
      this.f_19789_ = 0.0F;
      this.m_21573_().m_26573_();
      if (this.m_20096_()) {
         this.m_20256_(this.m_20184_().m_82520_(0.0, 0.32, 0.0));
      }

      if (this.f_19796_.m_188503_(70) == 0) {
         this.formationSide = -this.formationSide;
      }

      if (this.f_19796_.m_188503_(50) == 0) {
         this.orbitRadius = new Random().nextFloat(3.5F, 6.5F);
      }

      this.orbitAngle = this.orbitAngle + 0.16F * (float)this.formationSide;
      double x = target.m_20185_() + (double)(Mth.m_14089_(this.orbitAngle) * this.orbitRadius);
      double z = target.m_20189_() + (double)(Mth.m_14031_(this.orbitAngle) * this.orbitRadius);
      double y = target.m_20188_() + 0.4 + (double)Mth.m_14031_((float)(this.f_19797_ + this.m_19879_()) * 0.25F) * 0.9;
      this.moveAerialTowards(x, y, z, 0.18, 0.86);
   }

   private void dropSpecialHeldItemsBeforeDeath() {
      if (this.escapeMode || this.deathAssemblyMode) {
         ItemStack main = this.m_21205_();
         if (main.m_150930_((Item)AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get())) {
            this.m_19983_(new ItemStack((ItemLike)AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get()));
            this.m_21008_(InteractionHand.MAIN_HAND, ItemStack.f_41583_);
         }

         ItemStack off = this.m_21206_();
         if (off.m_150930_((Item)AnnoyingVillagersModItems.BLUE_DEMON_CHESTPLATE.get())) {
            this.m_19983_(new ItemStack((ItemLike)AnnoyingVillagersModItems.BLUE_DEMON_CHESTPLATE.get()));
            this.m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
         }
      }
   }

   public void m_6667_(@NotNull DamageSource source) {
      if (!this.m_9236_().f_46443_) {
         this.dropSpecialHeldItemsBeforeDeath();
      }

      super.m_6667_(source);
   }

   private void tickHeadAttack(LivingEntity target) {
      if (this.combatModeTicks <= 0) {
         this.startOrbit(target, 20);
      } else {
         this.m_20242_(true);
         this.f_19789_ = 0.0F;
         this.m_21573_().m_26573_();
         if (this.m_20096_()) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, 0.38, 0.0));
         }

         this.orbitAngle = this.orbitAngle + 0.42F * (float)this.formationSide;
         double x = target.m_20185_() + (double)Mth.m_14089_(this.orbitAngle) * 0.85;
         double z = target.m_20189_() + (double)Mth.m_14031_(this.orbitAngle) * 0.85;
         double y = target.m_20188_() + 0.15 + (double)Mth.m_14031_((float)(this.f_19797_ + this.m_19879_()) * 0.55F) * 0.35;
         this.moveAerialTowards(x, y, z, 0.26, 0.82);
         if (this.meleeCooldown <= 0 && this.m_20275_(target.m_20185_(), target.m_20188_(), target.m_20189_()) < 2.25) {
            target.m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21133_(Attributes.f_22281_));
            this.m_19970_(this, target);
            this.m_5496_(SoundEvents.f_11753_, 1.0F, 1.1F + this.f_19796_.m_188501_() * 0.2F);
            this.m_5496_(SoundEvents.f_11750_, 0.75F, 1.2F + this.f_19796_.m_188501_() * 0.3F);
            this.meleeCooldown = 8;
         }
      }
   }

   private void tickLeaderFollow() {
      BlueDemonEntity leader = this.getLeader();
      this.m_20242_(false);
      if (leader != null && leader.m_6084_()) {
         if ((double)this.m_20270_(leader) > 5.0) {
            this.m_21573_().m_5624_(leader, 1.35);
         } else {
            this.m_21573_().m_26573_();
         }
      } else {
         this.m_21573_().m_26573_();
      }
   }

   public void startParallelPursuit(@Nullable LivingEntity target, int ticks) {
      if (target == null) {
         this.clearCombat();
      } else {
         this.setCombatTarget(target);
         this.combatMode = BbqCombatMode.PARALLEL;
         this.combatModeTicks = Math.max(this.combatModeTicks, ticks);
         this.m_20242_(false);
         if (this.f_19796_.m_188503_(8) == 0) {
            this.formationSide = -this.formationSide;
         }
      }
   }

   private void tickParallelPursuit(LivingEntity target) {
      BlueDemonEntity leader = this.getLeader();
      this.m_20242_(false);
      if (leader != null && leader.m_6084_()) {
         if (this.f_19796_.m_188503_(80) == 0) {
            this.formationSide = -this.formationSide;
         }

         Vec3 forward = target.m_20182_().m_82546_(leader.m_20182_());
         forward = new Vec3(forward.f_82479_, 0.0, forward.f_82481_);
         if (forward.m_82556_() < 1.0E-4) {
            forward = new Vec3(leader.m_20154_().f_82479_, 0.0, leader.m_20154_().f_82481_);
         }

         if (forward.m_82556_() < 1.0E-4) {
            forward = new Vec3(1.0, 0.0, 0.0);
         }

         forward = forward.m_82541_();
         Vec3 side = new Vec3(-forward.f_82481_, 0.0, forward.f_82479_).m_82490_(2.2 * (double)this.formationSide);
         Vec3 back = forward.m_82490_(-0.75);
         Vec3 desired = leader.m_20182_().m_82549_(side).m_82549_(back);
         this.m_21563_().m_24960_(target, 45.0F, 45.0F);
         if (this.m_20275_(desired.f_82479_, leader.m_20186_(), desired.f_82481_) > 2.25) {
            this.m_21573_().m_26519_(desired.f_82479_, leader.m_20186_(), desired.f_82481_, 1.45);
         } else {
            this.m_21573_().m_26573_();
         }
      } else {
         this.tickLeaderFollow();
      }
   }

   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_) {
         if (this.selfKill) {
            this.m_6074_();
         } else {
            this.tickVoiceCooldown();
            this.tickBurstProtectionDecay(this);
            if (this.deathWatchMode) {
               this.tickLeaderDeathWatch();
            } else if (this.deathAssemblyMode) {
               this.tickDeathAssembly();
            } else if (this.escapeMode) {
               this.tickEscapeLocomotionMode();
               if (this.sauceLeaderUUID != null && this.getSauceLeader() == null) {
                  this.sauceLeaderUUID = null;
                  this.sauceLeader = null;
                  this.escapeLocomotionTicks = 0;
               }

               if (!this.escapeFlying) {
                  this.m_20242_(false);
                  this.f_19789_ = 0.0F;
               }
            } else if (this.retreatTicks > 0) {
               this.tickRetreat();
            } else {
               this.teleportNearLeaderIfTooFar();
               BlueDemonEntity leader = this.getLeader();
               if (this.combatModeTicks > 0) {
                  this.combatModeTicks--;
               }

               if (this.meleeCooldown > 0) {
                  this.meleeCooldown--;
               }

               if (this.chainShotCooldown > 0) {
                  this.chainShotCooldown--;
               }

               if (this.sauceType.isSupport()) {
                  this.tickSweetOnionSupport();
               } else {
                  LivingEntity target = this.getCombatTarget();
                  if (target != null && target.m_6084_()) {
                     if (leader != null
                        && leader.m_6084_()
                        && leader.m_5448_() != null
                        && (double)leader.m_20270_(leader.m_5448_()) > 10.0
                        && this.combatMode != BbqCombatMode.PARALLEL) {
                        this.startParallelPursuit(target, 20);
                     }

                     this.m_21563_().m_24960_(target, 45.0F, 45.0F);
                     if (this.sauceType == SauceType.BBQ_SAUCE) {
                        this.tickManualAttacks();
                     }

                     switch (this.combatMode) {
                        case HEAD_ATTACK:
                           if (this.combatModeTicks > 0) {
                              this.tickHeadAttack(target);
                           } else {
                              this.startGroundOrbit(target, 20);
                              this.tickGroundOrbit(target);
                           }
                           break;
                        case ORBIT:
                           this.tickOrbit(target);
                           break;
                        case GROUND_ORBIT:
                           this.tickGroundOrbit(target);
                           break;
                        case PARALLEL:
                           this.tickParallelPursuit(target);
                           break;
                        default:
                           if (leader != null && leader.m_6084_() && (double)leader.m_20270_(target) > 10.0) {
                              this.startParallelPursuit(target, 20);
                              this.tickParallelPursuit(target);
                           } else {
                              this.startGroundOrbit(target, 20);
                              this.tickGroundOrbit(target);
                           }
                     }

                     this.tickShockTouch(target);
                  } else {
                     this.clearCombat();
                     this.tickLeaderFollow();
                  }
               }
            }
         }
      }
   }

   public void teleportNearLeaderIfTooFar() {
      if (!this.m_9236_().f_46443_ && this.retreatTicks <= 0 && !this.escapeMode) {
         BlueDemonEntity leader = this.getLeader();
         if (leader != null && leader.m_6084_() && !leader.isSauceArrivalPending()) {
            if (!(this.m_20280_(leader) <= 400.0)) {
               float angle = leader.getSauceSquadAngle();

               double laneOffset = switch (this.getSauceType()) {
                  case BBQ_SAUCE -> -2.25;
                  case HONEY_MUSTARD_SAUCE -> -0.75;
                  case SOY_SAUCE -> 0.75;
                  case SWEET_ONION_SAUCE -> 2.25;
               };
               double forwardX = (double)Mth.m_14089_(angle);
               double forwardZ = (double)Mth.m_14031_(angle);
               double sideX = -forwardZ;
               double radius = 1.8;
               double x = leader.m_20185_() - forwardX * radius + sideX * laneOffset;
               double z = leader.m_20189_() - forwardZ * radius + forwardX * laneOffset;
               double y = leader.m_20186_();
               this.m_6021_(x, y, z);
               this.m_21573_().m_26573_();
               this.m_20256_(Vec3.f_82478_);
               this.m_20242_(false);
               this.f_19789_ = 0.0F;
            }
         }
      }
   }

   public boolean m_6469_(@NotNull DamageSource damageSource, float amount) {
      if (damageSource.m_276093_(DamageTypes.f_268450_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268565_)) {
         return false;
      } else if (!damageSource.m_276093_(DamageTypes.f_268724_) && !damageSource.m_276093_(DamageTypes.f_286979_)) {
         if (this.m_21223_() <= 1.0F && !this.selfKill) {
            this.selfKill = true;
            return false;
         } else {
            boolean result = super.m_6469_(damageSource, amount);
            if (result && !this.m_9236_().f_46443_ && damageSource.m_7639_() instanceof LivingEntity livingEntity) {
               if (this.deathAssemblyMode || this.deathWatchMode) {
                  return false;
               }

               if (this.escapeMode) {
                  return true;
               }

               BlueDemonEntity leader = this.getLeader();
               this.startOrbit(livingEntity, 40);
               if (leader != null) {
                  leader.m_6710_(livingEntity);
               }
            }

            if (result) {
               this.sayHurtSound(this, damageSource);
            }

            return result;
         }
      } else {
         boolean resultx = super.m_6469_(damageSource, amount);
         if (resultx) {
            this.sayHurtSound(this, damageSource);
         }

         return resultx;
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
            if (!pDamageSource.m_269533_(DamageTypeTags.f_268738_)) {
               float cap = this.m_21233_() * 0.025F;
               f1 = Mth.m_14036_(f1, 0.0F, cap);
               float damageScale = 1.0F - Mth.m_14036_(this.recentDamageTaken / (this.m_21233_() * 0.07F), 0.0F, 0.9F);
               float hitScale = 1.0F - Mth.m_14036_((float)this.recentHitCounter / 5.0F, 0.0F, 0.9F);
               f1 *= damageScale;
               if (this.recentHitCounter >= 5) {
                  f1 = 0.1F;
               } else {
                  f1 *= hitScale;
               }

               this.recentHitCounter++;
               this.recentDamageTaken += f1;
            }

            if (!(f1 <= 0.0F)) {
               this.m_21231_().m_289194_(pDamageSource, f1);
               this.m_21153_(this.m_21223_() - f1);
               this.m_146850_(GameEvent.f_223706_);
            }
         }
      }
   }

   @NotNull
   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor level,
      @NotNull DifficultyInstance difficulty,
      @NotNull MobSpawnType reason,
      @Nullable SpawnGroupData spawnData,
      @Nullable CompoundTag dataTag
   ) {
      SpawnGroupData data = super.m_6518_(level, difficulty, reason, spawnData, dataTag);
      if (!this.m_9236_().m_5776_()) {
         TeamUtil.addOrJoinTeam(this, "blue_demon");
      }

      return data;
   }

   @Nullable
   @Override
   public SoundEvent getHurtVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.BBQ_SAY.get();
   }

   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      if (this.leaderUUID != null) {
         tag.m_128362_("LeaderUUID", this.leaderUUID);
      }

      tag.m_128359_("SauceType", this.sauceType.name());
      tag.m_128379_("EscapeMode", this.escapeMode);
      if (this.sauceLeaderUUID != null) {
         tag.m_128362_("SauceLeaderUUID", this.sauceLeaderUUID);
      }

      tag.m_128405_("VoiceCooldown", this.voiceCooldown);
   }

   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128403_("LeaderUUID")) {
         this.leaderUUID = tag.m_128342_("LeaderUUID");
      }

      if (tag.m_128441_("SauceType")) {
         try {
            this.setSauceType(SauceType.valueOf(tag.m_128461_("SauceType")));
         } catch (IllegalArgumentException var3) {
            this.setSauceType(SauceType.BBQ_SAUCE);
         }
      } else {
         this.setSauceType(SauceType.BBQ_SAUCE);
      }

      this.escapeMode = tag.m_128471_("EscapeMode");
      if (tag.m_128403_("SauceLeaderUUID")) {
         this.sauceLeaderUUID = tag.m_128342_("SauceLeaderUUID");
      } else {
         this.sauceLeaderUUID = null;
      }

      this.sauceLeader = null;
      this.voiceCooldown = tag.m_128451_("VoiceCooldown");
   }

   @NotNull
   public static Builder m_28263_() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22279_, 0.27)
         .m_22268_(Attributes.f_22276_, 75.0)
         .m_22268_(Attributes.f_22284_, 10.0)
         .m_22268_(Attributes.f_22281_, 7.0)
         .m_22268_(Attributes.f_22277_, 24.0)
         .m_22268_(Attributes.f_22282_, 1.0);
   }
}
