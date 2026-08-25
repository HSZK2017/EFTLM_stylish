package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.ThunderDiamondBladeItem;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

public class ElectricPhaseEntity extends Entity implements IEntityAdditionalSpawnData {
   private static final String TAG_OWNER_UUID = "OwnerUUID";
   private static final String TAG_HALF_SIZE = "HalfSize";
   private static final String TAG_DURATION_TICKS = "DurationTicks";
   private static final String TAG_DAMAGE_AMOUNT = "DamageAmount";
   private static final String TAG_DAMAGE_INTERVAL = "DamageInterval";
   private static final String TAG_KNOCKBACK = "Knockback";
   private static final String TAG_ELECTRIFY_TICKS = "ElectrifyTicks";
   private static final String TAG_ELECTRIFY_AMPLIFIER = "ElectrifyAmplifier";
   private static final int PARTICLE_INTERVAL_TICKS = 10;
   private static final String TAG_MODE = "Mode";
   private static final String TAG_VEL_X = "VelX";
   private static final String TAG_VEL_Y = "VelY";
   private static final String TAG_VEL_Z = "VelZ";
   private static final String TAG_OFFHAND = "Offhand";
   private static final EntityDataAccessor<Boolean> DATA_OFFHAND = SynchedEntityData.m_135353_(ElectricPhaseEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.m_135353_(ElectricPhaseEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> DATA_MODE = SynchedEntityData.m_135353_(ElectricPhaseEntity.class, EntityDataSerializers.f_135028_);
   @Nullable
   private UUID ownerUUID;
   private double halfSize = 0.5;
   private int durationTicks = 40;
   private float damageAmount = 3.0F;
   private int damageInterval = 8;
   private double knockback = 0.45;
   private int electrifyTicks = 60;
   private int electrifyAmplifier = 0;
   private Vec3 projectileVelocity = Vec3.f_82478_;

   public ElectricPhaseEntity(EntityType<? extends ElectricPhaseEntity> type, Level level) {
      super(type, level);
      this.f_19794_ = true;
      this.m_20242_(true);
   }

   public ElectricPhaseEntity(Level level, LivingEntity owner, Vec3 pos) {
      this(level, owner, pos, 1.0, 40, 3.0F, 8, 0.45, 80, 0);
   }

   public ElectricPhaseEntity(
      Level level,
      LivingEntity owner,
      Vec3 pos,
      double halfSize,
      int durationTicks,
      float damageAmount,
      int damageInterval,
      double knockback,
      int electrifyTicks,
      int electrifyAmplifier
   ) {
      this((EntityType<? extends ElectricPhaseEntity>)AnnoyingVillagersModEntities.ELECTRIC_PHASE.get(), level);
      this.setOwner(owner);
      this.halfSize = halfSize;
      this.durationTicks = durationTicks;
      this.damageAmount = damageAmount;
      this.damageInterval = Math.max(1, damageInterval);
      this.knockback = knockback;
      this.electrifyTicks = electrifyTicks;
      this.electrifyAmplifier = electrifyAmplifier;
      this.m_6034_(pos.f_82479_, pos.f_82480_, pos.f_82481_);
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(DATA_OWNER_ID, -1);
      this.f_19804_.m_135372_(DATA_MODE, ElectricPhaseEntity.Mode.PROJECTILE.id());
      this.f_19804_.m_135372_(DATA_OFFHAND, false);
   }

   public boolean isOffhand() {
      return (Boolean)this.f_19804_.m_135370_(DATA_OFFHAND);
   }

   public boolean isMainhand() {
      return !this.isOffhand();
   }

   public void setOffhand(boolean offhand) {
      this.f_19804_.m_135381_(DATA_OFFHAND, offhand);
   }

   private static boolean isHoldingThunderDiamondBlade(LivingEntity owner, boolean offhand) {
      ItemStack stack = offhand ? owner.m_21206_() : owner.m_21205_();
      return stack.m_41720_() instanceof ThunderDiamondBladeItem;
   }

   public void setOwner(@Nullable LivingEntity owner) {
      if (owner == null) {
         this.ownerUUID = null;
         this.f_19804_.m_135381_(DATA_OWNER_ID, -1);
      } else {
         this.ownerUUID = owner.m_20148_();
         this.f_19804_.m_135381_(DATA_OWNER_ID, owner.m_19879_());
      }
   }

   @Nullable
   public Entity getOwnerEntity() {
      int ownerId = (Integer)this.f_19804_.m_135370_(DATA_OWNER_ID);
      if (ownerId != -1) {
         Entity entity = this.m_9236_().m_6815_(ownerId);
         if (entity != null) {
            return entity;
         }
      }

      if (this.m_9236_() instanceof ServerLevel serverLevel && this.ownerUUID != null) {
         return serverLevel.m_8791_(this.ownerUUID);
      }

      return null;
   }

   @Nullable
   public LivingEntity getOwnerLiving() {
      return this.getOwnerEntity() instanceof LivingEntity living ? living : null;
   }

   public ElectricPhaseEntity.Mode getMode() {
      return ElectricPhaseEntity.Mode.byId((Integer)this.f_19804_.m_135370_(DATA_MODE));
   }

   public void setMode(ElectricPhaseEntity.Mode mode) {
      this.f_19804_.m_135381_(DATA_MODE, mode.id());
   }

   public boolean isFollowOwnerSwordMode() {
      return this.getMode() == ElectricPhaseEntity.Mode.FOLLOW_OWNER_SWORD;
   }

   public boolean isProjectileMode() {
      return this.getMode() == ElectricPhaseEntity.Mode.PROJECTILE;
   }

   public void setProjectileVelocity(Vec3 velocity) {
      this.projectileVelocity = velocity == null ? Vec3.f_82478_ : velocity;
      this.m_20256_(this.projectileVelocity);
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if (this.isFollowOwnerSwordMode()) {
            this.updateFollowOwnerSwordPosition();
         } else if (this.isProjectileMode()) {
            this.moveProjectile();
         }

         this.spawnElectricParticlesAroundWeapon(serverLevel);
         if (this.f_19797_ == 1) {
            this.playElectricSound(serverLevel);
         }

         if (this.f_19797_ == 1 || this.f_19797_ % this.damageInterval == 0) {
            this.damageEntitiesInZone();
         }

         if (this.f_19797_ >= this.durationTicks) {
            this.m_146870_();
         }
      }
   }

   private void updateFollowOwnerSwordPosition() {
      LivingEntity owner = this.getOwnerLiving();
      if (owner != null && owner.m_6084_() && !owner.m_213877_()) {
         boolean offhand = this.isOffhand();
         if (!isHoldingThunderDiamondBlade(owner, offhand)) {
            boolean otherHand = !offhand;
            if (!isHoldingThunderDiamondBlade(owner, otherHand)) {
               this.m_146870_();
               return;
            }

            this.setOffhand(otherHand);
            offhand = otherHand;
         }

         Vec3 swordPos = getOwnerSwordPosition(owner, offhand);
         this.m_6034_(swordPos.f_82479_, swordPos.f_82480_, swordPos.f_82481_);
         this.m_20256_(Vec3.f_82478_);
      } else {
         this.m_146870_();
      }
   }

   private void moveProjectile() {
      if (this.projectileVelocity.m_82556_() < 1.0E-7) {
         this.m_20256_(Vec3.f_82478_);
      } else {
         this.m_20256_(this.projectileVelocity);
         this.m_6034_(
            this.m_20185_() + this.projectileVelocity.f_82479_,
            this.m_20186_() + this.projectileVelocity.f_82480_,
            this.m_20189_() + this.projectileVelocity.f_82481_
         );
      }
   }

   private void spawnElectricParticlesAroundWeapon(ServerLevel serverLevel) {
      if (this.isFollowOwnerSwordMode()) {
         if (this.f_19797_ % 10 == 0) {
            LivingEntity owner = this.getOwnerLiving();
            if (owner != null && owner.m_6084_() && !owner.m_213877_()) {
               boolean offhand = this.isOffhand();
               if (!isHoldingThunderDiamondBlade(owner, offhand)) {
                  boolean otherHand = !offhand;
                  if (!isHoldingThunderDiamondBlade(owner, otherHand)) {
                     return;
                  }

                  this.setOffhand(otherHand);
                  offhand = otherHand;
               }

               Vec3 swordPos = getOwnerSwordPosition(owner, offhand);
               serverLevel.m_8767_(
                  (SimpleParticleType)AnnoyingVillagersModParticleTypes.ELECTRIC_SPARK.get(),
                  swordPos.f_82479_,
                  swordPos.f_82480_,
                  swordPos.f_82481_,
                  1,
                  0.0,
                  0.0,
                  0.0,
                  0.0
               );
            }
         }
      }
   }

   private void playElectricSound(ServerLevel serverLevel) {
      float volume = (float)Mth.m_216263_(serverLevel.f_46441_, 0.35, 0.8);
      float pitch = (float)Mth.m_216263_(serverLevel.f_46441_, 0.9, 1.25);
      serverLevel.m_5594_(null, this.m_20183_(), (SoundEvent)AnnoyingVillagersModSounds.ELECTRIFY.get(), SoundSource.NEUTRAL, volume, pitch);
   }

   private static Vec3 getOwnerSwordPosition(LivingEntity owner, boolean offhand) {
      try {
         Vec3 pos = EpicfightUtil.getJointWithTranslation(
            owner,
            new Vec3f(0.0F, 0.0F, 0.0F),
            offhand ? ((HumanoidArmature)Armatures.BIPED.get()).toolL : ((HumanoidArmature)Armatures.BIPED.get()).toolR,
            1.0F,
            0.25
         );
         if (pos != null) {
            return pos;
         }
      } catch (Exception var6) {
      }

      Vec3 look = owner.m_20154_();
      Vec3 side = new Vec3(-look.f_82481_, 0.0, look.f_82479_);
      if (side.m_82556_() > 1.0E-7) {
         side = side.m_82541_();
      } else {
         side = Vec3.f_82478_;
      }

      double sideOffset = offhand ? -0.35 : 0.35;
      return owner.m_20182_().m_82520_(0.0, (double)owner.m_20206_() * 0.65, 0.0).m_82549_(look.m_82490_(0.75)).m_82549_(side.m_82490_(sideOffset));
   }

   private void damageEntitiesInZone() {
      LivingEntity owner = this.getOwnerLiving();

      for (LivingEntity target : this.m_9236_().m_6443_(LivingEntity.class, this.makeDamageBox(), living -> this.isValidTarget(owner, living))) {
         this.applyElectrify(target);
      }
   }

   private void applyElectrify(LivingEntity target) {
      if (this.electrifyTicks > 0) {
         target.m_7292_(
            new MobEffectInstance((MobEffect)AnnoyingVillagersModMobEffects.ELECTRIFY.get(), this.electrifyTicks, this.electrifyAmplifier, false, true, true)
         );
      }
   }

   private boolean isValidTarget(@Nullable LivingEntity owner, LivingEntity target) {
      if (target.m_6084_() && !target.m_5833_()) {
         if (target instanceof Player player && player.m_7500_()) {
            return false;
         }

         if (owner != null) {
            if (target == owner) {
               return false;
            }

            if (owner.m_7307_(target)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private AABB makeDamageBox() {
      return new AABB(
         this.m_20185_() - this.halfSize,
         this.m_20186_() - 0.25,
         this.m_20189_() - this.halfSize,
         this.m_20185_() + this.halfSize,
         this.m_20186_() + 1.75,
         this.m_20189_() + this.halfSize
      );
   }

   public static void spawnOnOwnerSword(Level level, LivingEntity owner) {
      boolean offhand = shouldUseOffhand(owner);
      spawnOnOwnerSword(level, owner, offhand);
   }

   public static void spawnOnOwnerSword(Level level, LivingEntity owner, boolean offhand) {
      if (level instanceof ServerLevel serverLevel) {
         if (!isHoldingThunderDiamondBlade(owner, offhand)) {
            boolean otherHand = !offhand;
            if (!isHoldingThunderDiamondBlade(owner, otherHand)) {
               return;
            }

            offhand = otherHand;
         }

         Vec3 pos = getOwnerSwordPosition(owner, offhand);
         ElectricPhaseEntity electricPhase = new ElectricPhaseEntity(level, owner, pos, 1.0, 20, 2.5F, 7, 0.45, 20, 0);
         electricPhase.setOffhand(offhand);
         electricPhase.setMode(ElectricPhaseEntity.Mode.FOLLOW_OWNER_SWORD);
         serverLevel.m_7967_(electricPhase);
      }
   }

   private static boolean shouldUseOffhand(LivingEntity owner) {
      boolean holdingMainhand = isHoldingThunderDiamondBlade(owner, false);
      boolean holdingOffhand = isHoldingThunderDiamondBlade(owner, true);
      return !holdingMainhand && holdingOffhand;
   }

   protected void m_7378_(@NotNull CompoundTag tag) {
      if (tag.m_128441_("Offhand")) {
         this.setOffhand(tag.m_128471_("Offhand"));
      }

      if (tag.m_128403_("OwnerUUID")) {
         this.ownerUUID = tag.m_128342_("OwnerUUID");
      }

      if (tag.m_128441_("HalfSize")) {
         this.halfSize = tag.m_128459_("HalfSize");
      }

      if (tag.m_128441_("DurationTicks")) {
         this.durationTicks = tag.m_128451_("DurationTicks");
      }

      if (tag.m_128441_("DamageAmount")) {
         this.damageAmount = tag.m_128457_("DamageAmount");
      }

      if (tag.m_128441_("DamageInterval")) {
         this.damageInterval = Math.max(1, tag.m_128451_("DamageInterval"));
      }

      if (tag.m_128441_("Knockback")) {
         this.knockback = tag.m_128459_("Knockback");
      }

      if (tag.m_128441_("ElectrifyTicks")) {
         this.electrifyTicks = tag.m_128451_("ElectrifyTicks");
      }

      if (tag.m_128441_("ElectrifyAmplifier")) {
         this.electrifyAmplifier = tag.m_128451_("ElectrifyAmplifier");
      }

      if (tag.m_128441_("Mode")) {
         this.setMode(ElectricPhaseEntity.Mode.byId(tag.m_128451_("Mode")));
      }

      if (tag.m_128441_("VelX") && tag.m_128441_("VelY") && tag.m_128441_("VelZ")) {
         this.projectileVelocity = new Vec3(tag.m_128459_("VelX"), tag.m_128459_("VelY"), tag.m_128459_("VelZ"));
         this.m_20256_(this.projectileVelocity);
      }
   }

   protected void m_7380_(@NotNull CompoundTag tag) {
      if (this.ownerUUID != null) {
         tag.m_128362_("OwnerUUID", this.ownerUUID);
      }

      tag.m_128347_("HalfSize", this.halfSize);
      tag.m_128405_("DurationTicks", this.durationTicks);
      tag.m_128350_("DamageAmount", this.damageAmount);
      tag.m_128405_("DamageInterval", this.damageInterval);
      tag.m_128347_("Knockback", this.knockback);
      tag.m_128405_("ElectrifyTicks", this.electrifyTicks);
      tag.m_128405_("ElectrifyAmplifier", this.electrifyAmplifier);
      tag.m_128405_("Mode", this.getMode().id());
      tag.m_128347_("VelX", this.projectileVelocity.f_82479_);
      tag.m_128347_("VelY", this.projectileVelocity.f_82480_);
      tag.m_128347_("VelZ", this.projectileVelocity.f_82481_);
      tag.m_128379_("Offhand", this.isOffhand());
   }

   public void writeSpawnData(FriendlyByteBuf buf) {
      buf.m_130130_((Integer)this.f_19804_.m_135370_(DATA_OWNER_ID));
      buf.m_130130_((Integer)this.f_19804_.m_135370_(DATA_MODE));
      buf.writeBoolean(this.isOffhand());
      buf.writeDouble(this.projectileVelocity.f_82479_);
      buf.writeDouble(this.projectileVelocity.f_82480_);
      buf.writeDouble(this.projectileVelocity.f_82481_);
   }

   public void readSpawnData(FriendlyByteBuf buf) {
      this.f_19804_.m_135381_(DATA_OWNER_ID, buf.m_130242_());
      this.f_19804_.m_135381_(DATA_MODE, buf.m_130242_());
      this.f_19804_.m_135381_(DATA_OFFHAND, buf.readBoolean());
      this.projectileVelocity = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
      this.m_20256_(this.projectileVelocity);
   }

   public boolean m_6469_(@NotNull DamageSource source, float amount) {
      return false;
   }

   public boolean m_6087_() {
      return false;
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public static enum Mode {
      FOLLOW_OWNER_SWORD(0),
      PROJECTILE(1);

      private final int id;

      private Mode(int id) {
         this.id = id;
      }

      public int id() {
         return this.id;
      }

      public static ElectricPhaseEntity.Mode byId(int id) {
         for (ElectricPhaseEntity.Mode mode : values()) {
            if (mode.id == id) {
               return mode;
            }
         }

         return PROJECTILE;
      }
   }
}
