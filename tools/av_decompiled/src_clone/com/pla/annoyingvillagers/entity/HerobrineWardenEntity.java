package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HerobrineWardenEntity extends Warden {
   public final AnimationState idleAnimationState = new AnimationState();
   public final AnimationState eatingAnimationState = new AnimationState();
   private int idleAnimationTimeout = 0;
   private int eatingAnimationTimeout = 0;
   private EliteHerobrineKnockedEntity eatingHerobrine;
   private UUID eatingUUID;
   private boolean burrowStarted = false;
   private int burrowRemoveAt = -1;
   private static final int DIG_TICKS = 100;
   private static final EntityDataAccessor<Boolean> DATA_BONE_OPEN = SynchedEntityData.m_135353_(HerobrineWardenEntity.class, EntityDataSerializers.f_135035_);
   private boolean infectedSculk = false;

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_BONE_OPEN, false);
   }

   private boolean isBoneOpen() {
      return (Boolean)this.f_19804_.m_135370_(DATA_BONE_OPEN);
   }

   private void setBoneOpen(boolean open) {
      if (!this.m_9236_().m_5776_()) {
         this.f_19804_.m_135381_(DATA_BONE_OPEN, open);
      }
   }

   public void setEatingUUID(UUID eatingUUID) {
      this.eatingUUID = eatingUUID;
   }

   public HerobrineWardenEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   private void setupIdleAnimationStates() {
      if (this.idleAnimationTimeout <= 0) {
         this.idleAnimationTimeout = 40;
         this.idleAnimationState.m_216977_(this.f_19797_);
      } else {
         this.idleAnimationTimeout--;
      }
   }

   private void setupEatingAnimationStates() {
      if (this.eatingAnimationTimeout <= 0) {
         this.eatingAnimationTimeout = 40;
         this.eatingAnimationState.m_216977_(this.f_19797_);
      } else {
         this.eatingAnimationTimeout--;
      }
   }

   public void burrowThenDespawn() {
      if (!this.m_9236_().m_5776_() && !this.burrowStarted) {
         this.burrowStarted = true;
         this.m_21573_().m_26573_();
         this.m_20256_(Vec3.f_82478_);
         Brain<Warden> brain = this.m_6274_();
         brain.m_21936_(MemoryModuleType.f_26372_);
         brain.m_21936_(MemoryModuleType.f_217782_);
         brain.m_21936_(MemoryModuleType.f_26370_);
         brain.m_21936_(MemoryModuleType.f_217783_);
         brain.m_21936_(MemoryModuleType.f_217772_);
         brain.m_21936_(MemoryModuleType.f_217770_);
         this.m_20124_(Pose.DIGGING);
         this.burrowRemoveAt = this.f_19797_ + 100 + 1;
      }
   }

   protected void m_8024_() {
      super.m_8024_();
      if (!this.m_9236_().m_5776_()) {
         if (this.eatingHerobrine == null
            && this.eatingUUID != null
            && ((ServerLevel)this.m_9236_()).m_8791_(this.eatingUUID) instanceof EliteHerobrineKnockedEntity herobrine
            && herobrine.m_6084_()) {
            this.eatingHerobrine = herobrine;
         }

         if (this.eatingUUID != null && this.eatingHerobrine != null && this.eatingHerobrine.m_6084_()) {
            LivingEntity brainTarget = this.m_5448_();
            if (brainTarget == null || !this.eatingUUID.equals(brainTarget.m_20148_())) {
               this.m_219459_(this.eatingHerobrine);
            }

            int bump = AngerLevel.ANGRY.m_219226_() + 20;
            this.m_219387_(this.eatingHerobrine, bump, false);
            this.m_219448_().ifPresent(angry -> {
               if (!this.eatingUUID.equals(angry.m_20148_())) {
                  this.m_219428_(angry);
               }
            });
            Brain<Warden> brain = this.m_6274_();
            brain.m_21936_(MemoryModuleType.f_26381_);
            brain.m_21936_(MemoryModuleType.f_26382_);
            brain.m_21936_(MemoryModuleType.f_217784_);
            WardenAi.m_219512_(this);
            brain.m_21952_(MemoryModuleType.f_26372_);
            this.m_219448_().map(Entity::m_20148_);
         }
      }
   }

   public boolean m_219385_(@Nullable Entity e) {
      if (e instanceof LivingEntity living) {
         return this.eatingUUID == null ? false : this.eatingUUID.equals(living.m_20148_()) && super.m_219385_(e);
      } else {
         return false;
      }
   }

   public void m_219459_(@NotNull LivingEntity target) {
      if (this.eatingUUID == null || this.eatingUUID.equals(target.m_20148_())) {
         super.m_219459_(target);
      }
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.f_19797_ == 1 && !this.m_9236_().m_5776_() && !this.infectedSculk) {
         RandomSource randomSource = this.m_217043_();
         int attempts = new Random().nextInt(25, 50);

         for (int i = 0; i < attempts; i++) {
            int dx = Mth.m_216271_(randomSource, -5, 5);
            int dz = Mth.m_216271_(randomSource, -5, 5);
            BlockPos pos = new BlockPos((int)(this.m_20185_() + (double)dx), this.m_20097_().m_123342_(), (int)(this.m_20189_() + (double)dz));
            BlockState current = this.m_9236_().m_8055_(pos);
            if (!current.m_60795_() && current.m_60819_().m_76178_() && current.m_60800_(this.m_9236_(), pos) != -1.0F && !current.m_155947_()) {
               BlockState newState = randomSource.m_188501_() < 0.2F ? Blocks.f_220857_.m_49966_() : Blocks.f_220855_.m_49966_();
               this.m_9236_().m_7731_(pos, newState, 3);
            }
         }

         this.infectedSculk = true;
      }

      if (this.m_9236_().f_46443_) {
         this.setupIdleAnimationStates();
         if (this.isBoneOpen()) {
            this.setupEatingAnimationStates();
         }
      }

      if (this.burrowStarted && this.f_19797_ >= this.burrowRemoveAt) {
         super.m_142687_(RemovalReason.DISCARDED);
      }

      if (this.eatingHerobrine != null && !this.eatingHerobrine.m_6084_()) {
         this.eatingHerobrine = null;
         this.eatingUUID = null;
         if (this.isBoneOpen()) {
            this.setBoneOpen(false);
         }

         this.burrowThenDespawn();
      }
   }

   public boolean m_7327_(@NotNull Entity pEntity) {
      if (!this.m_9236_().m_5776_() && !this.isBoneOpen()) {
         this.setBoneOpen(true);
      }

      return super.m_7327_(pEntity);
   }

   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      if (this.eatingUUID != null) {
         tag.m_128362_("EatingUUID", this.eatingUUID);
      }

      tag.m_128379_("BurrowStarted", this.burrowStarted);
      tag.m_128405_("BurrowRemoveAt", this.burrowRemoveAt);
      tag.m_128379_("InfectedSculk", this.infectedSculk);
   }

   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128403_("EatingUUID")) {
         this.eatingUUID = tag.m_128342_("EatingUUID");
      }

      this.burrowStarted = tag.m_128471_("BurrowStarted");
      this.burrowRemoveAt = tag.m_128451_("BurrowRemoveAt");
      this.infectedSculk = tag.m_128471_("InfectedSculk");
   }

   public void m_7350_(@NotNull EntityDataAccessor<?> key) {
      super.m_7350_(key);
      if (f_19806_.equals(key) && this.m_217003_(Pose.DIGGING)) {
         this.f_219347_.m_216977_(this.f_19797_);
      }

      if (this.m_9236_().m_5776_() && DATA_BONE_OPEN.equals(key)) {
         if (this.isBoneOpen()) {
            this.eatingAnimationTimeout = 0;
            this.setupEatingAnimationStates();
         } else {
            this.eatingAnimationState.m_216973_();
         }
      }
   }

   @Nullable
   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor pLevel,
      @NotNull DifficultyInstance pDifficulty,
      @NotNull MobSpawnType pReason,
      @Nullable SpawnGroupData pSpawnData,
      @Nullable CompoundTag pDataTag
   ) {
      SpawnGroupData returnSpawnGroupData = super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
      this.m_6274_().m_21882_(MemoryModuleType.f_217770_, Unit.INSTANCE, 1200L);
      this.m_20124_(Pose.EMERGING);
      this.m_6274_().m_21882_(MemoryModuleType.f_217786_, Unit.INSTANCE, (long)WardenAi.f_219490_);
      return returnSpawnGroupData;
   }

   public boolean m_6469_(@NotNull DamageSource pSource, float pAmount) {
      if (!pSource.m_276093_(DamageTypes.f_268724_) && !pSource.m_276093_(DamageTypes.f_286979_)) {
         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            EpicfightUtil.damageBlocked(pSource, this, serverLevel);
         }

         return false;
      } else {
         return super.m_6469_(pSource, pAmount);
      }
   }

   @NotNull
   public static Builder m_219463_() {
      Builder builder = Mob.m_21552_();
      builder = builder.m_22268_(Attributes.f_22279_, 0.26);
      builder = builder.m_22268_(Attributes.f_22276_, 500.0);
      builder = builder.m_22268_(Attributes.f_22284_, 20.0);
      builder = builder.m_22268_(Attributes.f_22281_, 0.0);
      return builder.m_22268_(Attributes.f_22277_, 24.0);
   }
}
