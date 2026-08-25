package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.clazz.NullWeapon;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.network.ClientboundTeleportPortalFx;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;

public class PortalEntity extends Entity {
   public static final float WIDTH = 2.2F;
   public static final float HEIGHT = 3.0F;
   public static final int LIFETIME_TICKS = 200;
   private static final int AMBIENT_SOUND_INTERVAL_TICKS = 80;
   private static final String PORTAL_COOLDOWN_TAG = "AnnoyingVillagersPortalCooldown";
   private static final int TELEPORT_COOLDOWN_TICKS = 30;
   private static final double SNAKE_BLADE_ANCHOR_Y_OFFSET = 1.0;
   private static final EntityDataAccessor<Optional<UUID>> LINKED_PORTAL_UUID = SynchedEntityData.m_135353_(PortalEntity.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.m_135353_(PortalEntity.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Optional<UUID>> PORTAL_GROUP_UUID = SynchedEntityData.m_135353_(PortalEntity.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Integer> PORTAL_ORDER = SynchedEntityData.m_135353_(PortalEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> STARTER_PORTAL = SynchedEntityData.m_135353_(PortalEntity.class, EntityDataSerializers.f_135035_);

   public PortalEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.f_19794_ = true;
      this.m_20242_(true);
   }

   public PortalEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<?>)AnnoyingVillagersModEntities.PORTAL.get(), level);
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(LINKED_PORTAL_UUID, Optional.empty());
      this.f_19804_.m_135372_(OWNER_UUID, Optional.empty());
      this.f_19804_.m_135372_(PORTAL_GROUP_UUID, Optional.empty());
      this.f_19804_.m_135372_(PORTAL_ORDER, -1);
      this.f_19804_.m_135372_(STARTER_PORTAL, false);
   }

   public void m_8119_() {
      super.m_8119_();
      this.f_19794_ = true;
      this.m_20242_(true);
      this.m_20256_(Vec3.f_82478_);
      if (!this.m_9236_().f_46443_) {
         if (this.f_19797_ == 1) {
            this.playPortalSound((SoundEvent)AnnoyingVillagersModSounds.PORTAL_OPEN.get());
         }

         if (this.f_19797_ > 1 && this.f_19797_ % 80 == 0) {
            this.playPortalSound((SoundEvent)AnnoyingVillagersModSounds.PORTAL_AMBIENT.get());
         }

         if (this.f_19797_ >= 200) {
            this.playPortalSound((SoundEvent)AnnoyingVillagersModSounds.PORTAL_FIZZLE.get());
            this.m_146870_();
            return;
         }

         this.teleportIntersectingEntities();
      }
   }

   private void teleportIntersectingEntities() {
      PortalEntity linkedPortal = this.getLinkedPortal();
      if (linkedPortal != null && !linkedPortal.m_213877_()) {
         AABB portalBox = this.getTeleportBox();

         for (Entity entity : this.m_9236_().m_6249_(this, portalBox.m_82400_(0.35), this::canTeleportEntity)) {
            if (this.intersectsPortalPath(entity, portalBox)) {
               this.teleportEntity(entity, linkedPortal);
            }
         }
      }
   }

   private boolean canTeleportEntity(Entity entity) {
      if (entity instanceof PortalEntity || entity instanceof SnakeBladeEntity || entity instanceof HerobrineDragonEntity) {
         return false;
      } else if (!entity.m_213877_() && entity.m_6084_() && !entity.m_20159_()) {
         if (entity instanceof Player player && player.m_5833_()) {
            return false;
         }

         return entity.getPersistentData().m_128454_("AnnoyingVillagersPortalCooldown") > this.m_9236_().m_46467_()
            ? false
            : this.canTeleportByOwnerRule(entity);
      } else {
         return false;
      }
   }

   private boolean canTeleportByOwnerRule(Entity entity) {
      Entity owner = this.getOwnerEntity();
      if (owner == null) {
         return true;
      } else if (isHerobrinePortalOwner(owner)) {
         return canUseHerobrineOwnedPortal(entity, owner);
      } else {
         return owner instanceof Player ? canUsePlayerOwnedPortal(entity) : true;
      }
   }

   private Entity getOwnerEntity() {
      UUID ownerUuid = this.getOwnerUUID();
      return ownerUuid != null && this.m_9236_() instanceof ServerLevel serverLevel ? serverLevel.m_8791_(ownerUuid) : null;
   }

   private static boolean canUseHerobrineOwnedPortal(Entity entity, Entity owner) {
      return !isSupportPortalCaster(entity) && entity.m_20148_().equals(owner.m_20148_())
         || entity instanceof Projectile
         || isHerobrinePortalUser(entity) && !isSupportPortalCaster(entity);
   }

   private static boolean canUsePlayerOwnedPortal(Entity entity) {
      return entity instanceof Projectile ? true : !isHerobrinePortalUser(entity) && !(entity instanceof Monster);
   }

   private static boolean isHerobrinePortalOwner(Entity entity) {
      return isHerobrinePortalUser(entity);
   }

   private static boolean isSupportPortalCaster(Entity entity) {
      return entity instanceof HerobrineGregEntity || entity instanceof TransporterHerobrineCloneEntity;
   }

   private static boolean isHerobrinePortalUser(Entity entity) {
      return entity instanceof HerobrineMob
         || entity instanceof HerobrineGregEntity
         || entity instanceof LowHerobrineCloneEntity
         || entity instanceof LowShadowHerobrineCloneEntity
         || entity instanceof NullWeapon;
   }

   private boolean intersectsPortalPath(Entity entity, AABB portalBox) {
      AABB currentBox = entity.m_20191_();
      if (currentBox.m_82381_(portalBox)) {
         return true;
      } else {
         AABB previousBox = currentBox.m_82386_(entity.f_19854_ - entity.m_20185_(), entity.f_19855_ - entity.m_20186_(), entity.f_19856_ - entity.m_20189_());
         if (previousBox.m_82367_(currentBox).m_82381_(portalBox)) {
            return true;
         } else {
            Vec3 from = new Vec3(entity.f_19854_, entity.f_19855_ + (double)entity.m_20206_() * 0.5, entity.f_19856_);
            Vec3 to = entity.m_20182_().m_82520_(0.0, (double)entity.m_20206_() * 0.5, 0.0);
            return portalBox.m_82371_(from, to).isPresent();
         }
      }
   }

   private void teleportEntity(Entity entity, PortalEntity linkedPortal) {
      Vec3 motion = entity.m_20184_();
      Vec3 sourceNormal = this.getNormal();
      Vec3 entityCenter = entity.m_20182_().m_82520_(0.0, (double)entity.m_20206_() * 0.5, 0.0);
      double exitSide = entityCenter.m_82546_(this.getPortalCenter()).m_82526_(sourceNormal) >= 0.0 ? 1.0 : -1.0;
      double relativeY = Mth.m_14008_(entity.m_20186_() - this.m_20186_(), 0.05, Math.max(0.05, (double)(this.m_20206_() - entity.m_20206_())));
      Vec3 exitPos = linkedPortal.findExitPosition(entity, exitSide, relativeY);
      Vec3 exitMotion = this.transformMotion(motion, linkedPortal, exitSide);
      entity.getPersistentData().m_128356_("AnnoyingVillagersPortalCooldown", this.m_9236_().m_46467_() + 30L);
      entity.m_6021_(exitPos.f_82479_, exitPos.f_82480_, exitPos.f_82481_);
      entity.m_6478_(MoverType.SELF, Vec3.f_82478_);
      entity.m_20256_(exitMotion);
      entity.f_19789_ = 0.0F;
      float yawDelta = linkedPortal.m_146908_() - this.m_146908_();
      entity.m_146922_(entity.m_146908_() + yawDelta);
      if (entity instanceof LivingEntity livingEntity) {
         livingEntity.m_5616_(livingEntity.m_6080_() + yawDelta);
         livingEntity.f_20883_ += yawDelta;
      }

      this.playPortalSound((SoundEvent)AnnoyingVillagersModSounds.PORTAL_ENTER.get());
      linkedPortal.playPortalSound((SoundEvent)AnnoyingVillagersModSounds.PORTAL_EXIT.get());
      this.sendTeleportPortalFx();
      linkedPortal.sendTeleportPortalFx();
   }

   private void playPortalSound(SoundEvent soundEvent) {
      this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_() + 1.5, this.m_20189_(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   private void sendTeleportPortalFx() {
      if (!this.m_9236_().f_46443_) {
         AnnoyingVillagers.PACKET_HANDLER
            .send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new ClientboundTeleportPortalFx(this.getPortalCenter(), this.getNormal()));
      }
   }

   private Vec3 findExitPosition(Entity entity, double exitSide, double relativeY) {
      Vec3 normal = this.getNormal().m_82490_(exitSide);
      double offset = Math.max(1.15, (double)entity.m_20205_() * 0.5 + 0.75);
      double y = this.m_20186_() + Mth.m_14008_(relativeY, 0.05, Math.max(0.05, (double)(this.m_20206_() - entity.m_20206_())));

      for (int step = 0; step <= 6; step++) {
         Vec3 candidate = new Vec3(this.m_20185_(), y, this.m_20189_()).m_82549_(normal.m_82490_(offset + (double)step * 0.35));
         if (this.canFit(entity, candidate)) {
            return candidate;
         }
      }

      for (int vertical = 1; vertical <= 3; vertical++) {
         Vec3 candidate = new Vec3(this.m_20185_(), y + (double)vertical * 0.35, this.m_20189_()).m_82549_(normal.m_82490_(offset + 1.0));
         if (this.canFit(entity, candidate)) {
            return candidate;
         }
      }

      return new Vec3(this.m_20185_(), y, this.m_20189_()).m_82549_(normal.m_82490_(offset + 1.0));
   }

   private boolean canFit(Entity entity, Vec3 pos) {
      AABB movedBox = entity.m_20191_().m_82383_(pos.m_82546_(entity.m_20182_()));
      return this.m_9236_().m_45756_(entity, movedBox.m_82406_(1.0E-4));
   }

   private Vec3 transformMotion(Vec3 motion, PortalEntity linkedPortal, double exitSide) {
      Vec3 sourceNormal = this.getNormal();
      Vec3 sourceRight = rightOf(sourceNormal);
      Vec3 exitNormal = linkedPortal.getNormal().m_82490_(exitSide);
      Vec3 exitRight = rightOf(linkedPortal.getNormal());
      double speed = motion.m_82553_();
      double forwardSpeed = Math.max(Math.abs(motion.m_82526_(sourceNormal)), speed * 0.35);
      double rightSpeed = motion.m_82526_(sourceRight);
      Vec3 transformed = exitNormal.m_82490_(forwardSpeed).m_82549_(exitRight.m_82490_(rightSpeed)).m_82520_(0.0, motion.f_82480_, 0.0);
      return transformed.m_82556_() < 0.035 ? exitNormal.m_82490_(0.25) : transformed;
   }

   public AABB getTeleportBox() {
      return this.m_20191_().m_82400_(0.1);
   }

   public Vec3 getPortalCenter() {
      return new Vec3(this.m_20185_(), this.m_20186_() + (double)this.m_20206_() * 0.5, this.m_20189_());
   }

   public Vec3 getSnakeBladeAnchor() {
      return this.getPortalCenter().m_82492_(0.0, 1.0, 0.0);
   }

   public Vec3 getNormal() {
      float yaw = this.m_146908_() * (float) (Math.PI / 180.0);
      return new Vec3((double)(-Mth.m_14031_(yaw)), 0.0, (double)Mth.m_14089_(yaw)).m_82541_();
   }

   private static Vec3 rightOf(Vec3 normal) {
      Vec3 right = new Vec3(normal.f_82481_, 0.0, -normal.f_82479_);
      return right.m_82556_() < 1.0E-7 ? new Vec3(1.0, 0.0, 0.0) : right.m_82541_();
   }

   public UUID getLinkedPortalUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(LINKED_PORTAL_UUID)).orElse(null);
   }

   public void setLinkedPortalUUID(UUID uuid) {
      this.f_19804_.m_135381_(LINKED_PORTAL_UUID, Optional.ofNullable(uuid));
   }

   public PortalEntity getLinkedPortal() {
      UUID uuid = this.getLinkedPortalUUID();
      if (uuid != null && this.m_9236_() instanceof ServerLevel serverLevel) {
         return serverLevel.m_8791_(uuid) instanceof PortalEntity portalEntity ? portalEntity : null;
      } else {
         return null;
      }
   }

   public UUID getOwnerUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(OWNER_UUID)).orElse(null);
   }

   public void setOwnerUUID(UUID uuid) {
      this.f_19804_.m_135381_(OWNER_UUID, Optional.ofNullable(uuid));
   }

   public UUID getPortalGroupUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(PORTAL_GROUP_UUID)).orElse(null);
   }

   public void setPortalGroupUUID(UUID uuid) {
      this.f_19804_.m_135381_(PORTAL_GROUP_UUID, Optional.ofNullable(uuid));
   }

   public int getPortalOrder() {
      return (Integer)this.f_19804_.m_135370_(PORTAL_ORDER);
   }

   public void setPortalOrder(int order) {
      this.f_19804_.m_135381_(PORTAL_ORDER, order);
   }

   public boolean isStarterPortal() {
      return (Boolean)this.f_19804_.m_135370_(STARTER_PORTAL);
   }

   public void setStarterPortal(boolean starterPortal) {
      this.f_19804_.m_135381_(STARTER_PORTAL, starterPortal);
   }

   public boolean m_6087_() {
      return false;
   }

   public boolean m_6097_() {
      return false;
   }

   public boolean m_6469_(@NotNull DamageSource source, float amount) {
      return false;
   }

   protected void m_7378_(@NotNull CompoundTag tag) {
      this.setLinkedPortalUUID(tag.m_128403_("LinkedPortal") ? tag.m_128342_("LinkedPortal") : null);
      this.setOwnerUUID(tag.m_128403_("Owner") ? tag.m_128342_("Owner") : null);
      this.setPortalGroupUUID(tag.m_128403_("PortalGroup") ? tag.m_128342_("PortalGroup") : null);
      this.setPortalOrder(tag.m_128441_("PortalOrder") ? tag.m_128451_("PortalOrder") : -1);
      this.setStarterPortal(tag.m_128471_("StarterPortal"));
   }

   protected void m_7380_(@NotNull CompoundTag tag) {
      UUID linkedPortal = this.getLinkedPortalUUID();
      if (linkedPortal != null) {
         tag.m_128362_("LinkedPortal", linkedPortal);
      }

      UUID owner = this.getOwnerUUID();
      if (owner != null) {
         tag.m_128362_("Owner", owner);
      }

      UUID portalGroup = this.getPortalGroupUUID();
      if (portalGroup != null) {
         tag.m_128362_("PortalGroup", portalGroup);
      }

      tag.m_128405_("PortalOrder", this.getPortalOrder());
      tag.m_128379_("StarterPortal", this.isStarterPortal());
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
