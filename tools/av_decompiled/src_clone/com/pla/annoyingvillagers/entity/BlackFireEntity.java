package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.BlackFireSwordItem;
import com.pla.annoyingvillagers.network.ClientboundBlackFireFx;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

public class BlackFireEntity extends Entity implements IEntityAdditionalSpawnData {
   private static final String TAG_OWNER_UUID = "OwnerUUID";
   private static final String TAG_HALF_SIZE = "HalfSize";
   private static final String TAG_DURATION_TICKS = "DurationTicks";
   private static final String TAG_DAMAGE_AMOUNT = "DamageAmount";
   private static final String TAG_DAMAGE_INTERVAL = "DamageInterval";
   private static final String TAG_KNOCKBACK = "Knockback";
   private static final String TAG_FIRE_SECONDS = "FireSeconds";
   private static final String TAG_MODE = "Mode";
   private static final String TAG_VEL_X = "VelX";
   private static final String TAG_VEL_Y = "VelY";
   private static final String TAG_VEL_Z = "VelZ";
   private static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.m_135353_(BlackFireEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> DATA_MODE = SynchedEntityData.m_135353_(BlackFireEntity.class, EntityDataSerializers.f_135028_);
   @Nullable
   private UUID ownerUUID;
   private double halfSize = 0.5;
   private int durationTicks = 60;
   private float damageAmount = 4.0F;
   private int damageInterval = 10;
   private double knockback = 0.65;
   private int fireSeconds = 4;
   private Vec3 projectileVelocity = Vec3.f_82478_;

   public BlackFireEntity(EntityType<? extends BlackFireEntity> type, Level level) {
      super(type, level);
      this.f_19794_ = true;
      this.m_20242_(true);
   }

   public BlackFireEntity(Level level, LivingEntity owner, Vec3 pos) {
      this(level, owner, pos, 1.0, 40, 4.0F, 10, 0.65, 4);
   }

   public BlackFireEntity(
      Level level, LivingEntity owner, Vec3 pos, double halfSize, int durationTicks, float damageAmount, int damageInterval, double knockback, int fireSeconds
   ) {
      this((EntityType<? extends BlackFireEntity>)AnnoyingVillagersModEntities.BLACK_FIRE.get(), level);
      this.setOwner(owner);
      this.halfSize = halfSize;
      this.durationTicks = durationTicks;
      this.damageAmount = damageAmount;
      this.damageInterval = Math.max(1, damageInterval);
      this.knockback = knockback;
      this.fireSeconds = fireSeconds;
      this.m_6034_(pos.f_82479_, pos.f_82480_, pos.f_82481_);
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(DATA_OWNER_ID, -1);
      this.f_19804_.m_135372_(DATA_MODE, BlackFireEntity.Mode.PROJECTILE.id());
   }

   private static boolean isHoldingBlackFireSword(LivingEntity owner) {
      return owner.m_21205_().m_41720_() instanceof BlackFireSwordItem;
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

   public BlackFireEntity.Mode getMode() {
      return BlackFireEntity.Mode.byId((Integer)this.f_19804_.m_135370_(DATA_MODE));
   }

   public void setMode(BlackFireEntity.Mode mode) {
      this.f_19804_.m_135381_(DATA_MODE, mode.id());
   }

   public boolean isFollowOwnerSwordMode() {
      return this.getMode() == BlackFireEntity.Mode.FOLLOW_OWNER_SWORD;
   }

   public boolean isProjectileMode() {
      return this.getMode() == BlackFireEntity.Mode.PROJECTILE;
   }

   public void setProjectileVelocity(Vec3 velocity) {
      this.projectileVelocity = velocity == null ? Vec3.f_82478_ : velocity;
      this.m_20256_(this.projectileVelocity);
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_() instanceof ServerLevel) {
         if (this.isFollowOwnerSwordMode()) {
            this.updateFollowOwnerSwordPosition();
         } else if (this.isProjectileMode()) {
            this.moveProjectile();
         }

         if (this.f_19797_ == 1) {
            AnnoyingVillagers.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new ClientboundBlackFireFx(this));
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.BLACK_FIRE.get(), 1.0F, 1.0F);
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
      if (owner == null || !owner.m_6084_() || owner.m_213877_()) {
         this.m_146870_();
      } else if (!isHoldingBlackFireSword(owner)) {
         this.m_146870_();
      } else {
         Vec3 swordPos = getOwnerSwordPosition(owner);
         this.m_6034_(swordPos.f_82479_, swordPos.f_82480_, swordPos.f_82481_);
         this.m_20256_(Vec3.f_82478_);
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

   private static Vec3 getOwnerSwordPosition(LivingEntity owner) {
      try {
         Vec3 pos = EpicfightUtil.getJointWithTranslation(owner, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 1.0F, 0.0);
         if (pos != null) {
            return pos;
         }
      } catch (Exception var2) {
      }

      Vec3 look = owner.m_20154_();
      return owner.m_20182_().m_82520_(0.0, (double)owner.m_20206_() * 0.65, 0.0).m_82549_(look.m_82490_(0.75));
   }

   private void damageEntitiesInZone() {
      LivingEntity owner = this.getOwnerLiving();

      for (LivingEntity target : this.m_9236_().m_6443_(LivingEntity.class, this.makeDamageBox(), living -> this.isValidTarget(owner, living))) {
         DamageSource source = this.makeDamageSource(owner);
         if (target.m_6469_(source, this.damageAmount)) {
            if (this.fireSeconds > 0) {
               target.m_20254_(this.fireSeconds);
            }

            this.knockbackTarget(target);
         }
      }
   }

   private DamageSource makeDamageSource(@Nullable LivingEntity owner) {
      return owner != null ? this.m_269291_().m_269104_(this, owner) : this.m_269291_().m_269425_();
   }

   private void knockbackTarget(LivingEntity target) {
      Vec3 dir = target.m_20182_().m_82546_(this.m_20182_());
      dir = new Vec3(dir.f_82479_, 0.0, dir.f_82481_);
      if (dir.m_82556_() < 1.0E-6) {
         dir = new Vec3(this.f_19796_.m_188500_() - 0.5, 0.0, this.f_19796_.m_188500_() - 0.5);
      }

      dir = dir.m_82541_();
      target.m_5997_(dir.f_82479_ * this.knockback, 0.25, dir.f_82481_ * this.knockback);
      target.f_19864_ = true;
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

   public static BlackFireEntity spawnOnOwnerSword(Level level, LivingEntity owner) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 pos = getOwnerSwordPosition(owner);
         BlackFireEntity fire = new BlackFireEntity(level, owner, pos, 1.0, 40, 2.0F, 10, 0.65, 4);
         fire.setMode(BlackFireEntity.Mode.FOLLOW_OWNER_SWORD);
         serverLevel.m_7967_(fire);
         return fire;
      } else {
         return null;
      }
   }

   public static BlackFireEntity shootFromOwnerLook(Level level, LivingEntity owner) {
      return shootFromOwnerLook(level, owner, 0.55, 40, 1.0, 6.0F, 5, 0.65, 4);
   }

   public static BlackFireEntity shootFromOwnerLook(
      Level level,
      LivingEntity owner,
      double speed,
      int durationTicks,
      double halfSize,
      float damageAmount,
      int damageInterval,
      double knockback,
      int fireSeconds
   ) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 look = owner.m_20252_(1.0F);
         if (look.m_82556_() < 1.0E-7) {
            look = owner.m_20154_();
         }

         look = look.m_82541_();
         Vec3 startPos = getOwnerSwordPosition(owner);
         Vec3 velocity = look.m_82490_(speed);
         BlackFireEntity fire = new BlackFireEntity(
            level, owner, startPos, halfSize, durationTicks, damageAmount, Math.max(1, damageInterval), knockback, fireSeconds
         );
         fire.setMode(BlackFireEntity.Mode.PROJECTILE);
         fire.setProjectileVelocity(velocity);
         fire.m_146922_(owner.m_146908_());
         fire.m_146926_(owner.m_146909_());
         serverLevel.m_7967_(fire);
         return fire;
      } else {
         return null;
      }
   }

   protected void m_7378_(@NotNull CompoundTag tag) {
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

      if (tag.m_128441_("FireSeconds")) {
         this.fireSeconds = tag.m_128451_("FireSeconds");
      }

      if (tag.m_128441_("Mode")) {
         this.setMode(BlackFireEntity.Mode.byId(tag.m_128451_("Mode")));
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
      tag.m_128405_("FireSeconds", this.fireSeconds);
      tag.m_128405_("Mode", this.getMode().id());
      tag.m_128347_("VelX", this.projectileVelocity.f_82479_);
      tag.m_128347_("VelY", this.projectileVelocity.f_82480_);
      tag.m_128347_("VelZ", this.projectileVelocity.f_82481_);
   }

   public void writeSpawnData(FriendlyByteBuf buf) {
      buf.m_130130_((Integer)this.f_19804_.m_135370_(DATA_OWNER_ID));
      buf.m_130130_((Integer)this.f_19804_.m_135370_(DATA_MODE));
      buf.writeDouble(this.projectileVelocity.f_82479_);
      buf.writeDouble(this.projectileVelocity.f_82480_);
      buf.writeDouble(this.projectileVelocity.f_82481_);
   }

   public void readSpawnData(FriendlyByteBuf buf) {
      this.f_19804_.m_135381_(DATA_OWNER_ID, buf.m_130242_());
      this.f_19804_.m_135381_(DATA_MODE, buf.m_130242_());
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

      public static BlackFireEntity.Mode byId(int id) {
         for (BlackFireEntity.Mode mode : values()) {
            if (mode.id == id) {
               return mode;
            }
         }

         return PROJECTILE;
      }
   }
}
