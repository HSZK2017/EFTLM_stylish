package com.dmc.invincible_dmc.entity.portal;

import com.dmc.invincible_dmc.compat.waystones.WaystonesCompat;
import com.dmc.invincible_dmc.event.VoidEvents;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPPortalDestinationChosen;
import com.dmc.invincible_dmc.network.server.S2CPortalDestinationsPacket;
import com.dmc.invincible_dmc.particle.DMCParticles;
import com.dmc.invincible_dmc.utils.yamato.TeleportGroundUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PortalEntity extends Entity {
   private static final Map<UUID, PortalEntity> ACTIVE_PORTALS = new HashMap<>();
   protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.m_135353_(PortalEntity.class, EntityDataSerializers.f_135041_);
   protected static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.m_135353_(PortalEntity.class, EntityDataSerializers.f_135028_);
   protected static final EntityDataAccessor<Boolean> DATA_CLOSING = SynchedEntityData.m_135353_(PortalEntity.class, EntityDataSerializers.f_135035_);
   protected static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.m_135353_(PortalEntity.class, EntityDataSerializers.f_135029_);
   protected static final EntityDataAccessor<Byte> DATA_PORTAL_STYLE = SynchedEntityData.m_135353_(PortalEntity.class, EntityDataSerializers.f_135027_);
   protected static final EntityDataAccessor<Boolean> DATA_FORCE_ORIGINAL_STYLE = SynchedEntityData.m_135353_(
      PortalEntity.class, EntityDataSerializers.f_135035_
   );
   private final int lifetime = 300;
   private static final int CLOSE_ANIMATION_TICKS = 13;
   private static final int OPEN_ANIMATION_TICKS = 13;
   private static final int AMBIENT_PARTICLES_PER_TICK = 6;
   private static final double PARTICLE_CENTER_Y = 0.8;
   private static final double PARTICLE_HALF_WIDTH = 4.6;
   private static final double PARTICLE_HALF_HEIGHT = 2.85;
   private static final double PARTICLE_SHAPE_EXPONENT = 0.5714285714285714;
   private int closingTicks = 0;
   private boolean infiniteLifetime = false;
   private int interactionCooldown = 0;
   private static final int CANCEL_COOLDOWN_TICKS = 40;
   private boolean particleSpawned = false;
   private int awaitingTeleportResponse = -1;
   @Nullable
   private Player pendingPlayer = null;

   public void setInfiniteLifetime(boolean infinite) {
      this.infiniteLifetime = infinite;
      if (infinite) {
         this.setForceOriginalStyle(true);
      }
   }

   public PortalEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.f_19794_ = true;
      this.m_20242_(true);
      this.f_19811_ = true;
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(DATA_OWNER_UUID, Optional.empty());
      this.f_19804_.m_135372_(DATA_OWNER_ID, 0);
      this.f_19804_.m_135372_(DATA_CLOSING, false);
      this.f_19804_.m_135372_(DATA_SCALE, 1.0F);
      this.f_19804_.m_135372_(DATA_PORTAL_STYLE, (byte)1);
      this.f_19804_.m_135372_(DATA_FORCE_ORIGINAL_STYLE, false);
   }

   public void m_6210_() {
      double width = 5.0;
      double height = 3.0;
      double depth = 2.0;
      this.m_20011_(this.m_142242_().m_82377_(width / 2.0, 0.0, depth / 2.0).m_82363_(0.0, height, 0.0));
   }

   public void setOwner(@Nullable LivingEntity owner) {
      if (owner != null && !this.m_9236_().f_46443_) {
         UUID uuid = owner.m_20148_();
         PortalEntity old = ACTIVE_PORTALS.remove(uuid);
         if (old != null && old.m_6084_() && old != this) {
            old.triggerClose();
         }

         ACTIVE_PORTALS.put(uuid, this);
         this.f_19804_.m_135381_(DATA_OWNER_UUID, Optional.of(uuid));
         this.f_19804_.m_135381_(DATA_OWNER_ID, owner.m_19879_());
         this.setPortalStyle(PortalStyleSync.getOwnerStyle(owner));
      } else if (owner == null) {
         this.f_19804_.m_135381_(DATA_OWNER_UUID, Optional.empty());
         this.f_19804_.m_135381_(DATA_OWNER_ID, 0);
      }
   }

   @Nullable
   public UUID getOwnerUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(DATA_OWNER_UUID)).orElse(null);
   }

   public int getOwnerID() {
      return (Integer)this.f_19804_.m_135370_(DATA_OWNER_ID);
   }

   public float getScale() {
      return (Float)this.f_19804_.m_135370_(DATA_SCALE);
   }

   public void setScale(float value) {
      this.f_19804_.m_135381_(DATA_SCALE, value);
      this.m_6210_();
   }

   public void setPortalStyle(byte style) {
      this.f_19804_.m_135381_(DATA_PORTAL_STYLE, PortalStyleSync.normalize(style));
   }

   public void setForceOriginalStyle(boolean forceOriginal) {
      this.f_19804_.m_135381_(DATA_FORCE_ORIGINAL_STYLE, forceOriginal);
   }

   private boolean usesOriginalPortalStyle() {
      return this.m_9236_().m_46472_().equals(VoidEvents.VOID_KEY)
         || (Boolean)this.f_19804_.m_135370_(DATA_FORCE_ORIGINAL_STYLE)
         || (Byte)this.f_19804_.m_135370_(DATA_PORTAL_STYLE) == 1;
   }

   @NotNull
   public EntityDimensions m_6972_(@NotNull Pose pose) {
      return super.m_6972_(pose).m_20388_(this.getScale());
   }

   public boolean isClosing() {
      return (Boolean)this.f_19804_.m_135370_(DATA_CLOSING);
   }

   public int getClosingTicks() {
      return this.closingTicks;
   }

   public void triggerClose() {
      if (!this.isClosing()) {
         this.f_19804_.m_135381_(DATA_CLOSING, true);
      }
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_().f_46443_) {
         if (!this.particleSpawned) {
            this.particleSpawned = true;
            if (this.usesOriginalPortalStyle()) {
               this.m_9236_()
                  .m_7106_((ParticleOptions)DMCParticles.PORTAL.get(), this.m_20185_(), this.m_20186_(), this.m_20189_(), (double)this.m_19879_(), 0.0, 0.0);
            } else {
               this.m_9236_()
                  .m_7106_(
                     (ParticleOptions)DMCParticles.PROCEDURAL_END_PORTAL.get(),
                     this.m_20185_(),
                     this.m_20186_() + 0.9,
                     this.m_20189_(),
                     (double)this.m_19879_(),
                     (double)(-this.getScale()),
                     0.0
                  );
            }
         }

         if (!this.usesOriginalPortalStyle() && !this.isClosing()) {
            this.spawnAmbientPortalParticles();
         }

         if (this.isClosing()) {
            this.closingTicks++;
         }
      } else if (this.isClosing()) {
         this.closingTicks++;
         if (this.closingTicks >= 13) {
            this.m_146870_();
         }
      } else {
         if (this.awaitingTeleportResponse < 0) {
            if (this.interactionCooldown > 0) {
               this.interactionCooldown--;
            } else {
               for (Player player : this.m_9236_().m_45976_(Player.class, this.m_20191_())) {
                  if (player.m_6084_()) {
                     this.beginTeleportFlow(player);
                     break;
                  }
               }
            }
         }

         if (this.awaitingTeleportResponse < 0) {
            if (!this.infiniteLifetime && this.f_19797_ > 300) {
               this.triggerClose();
            }
         } else {
            if (this.pendingPlayer == null || !this.pendingPlayer.m_6084_() || this.pendingPlayer instanceof ServerPlayer sp && sp.m_9232_()) {
               this.awaitingTeleportResponse = -1;
               this.pendingPlayer = null;
               this.triggerClose();
            }
         }
      }
   }

   private void spawnAmbientPortalParticles() {
      double scale = Math.max(0.05, (double)this.getScale());
      double openProgress = Math.min(1.0, (double)this.f_19797_ / 13.0);
      int particleCount = Math.max(1, (int)Math.ceil(6.0 * openProgress));
      double horizontalRadius = 4.6 * scale * openProgress;
      double verticalRadius = 2.85 * scale * openProgress;
      double yawRadians = Math.toRadians((double)this.m_146908_());
      Vec3 normal = new Vec3(-Math.sin(yawRadians), 0.0, Math.cos(yawRadians));
      Vec3 right = new Vec3(normal.f_82481_, 0.0, -normal.f_82479_);

      for (int index = 0; index < particleCount; index++) {
         double localX;
         double localY;
         do {
            localX = (this.f_19796_.m_188500_() * 2.0 - 1.0) * horizontalRadius;
            localY = (this.f_19796_.m_188500_() * 2.0 - 1.0) * verticalRadius;
         } while (Math.pow(Math.abs(localX / horizontalRadius), 0.5714285714285714) + Math.pow(Math.abs(localY / verticalRadius), 0.5714285714285714) > 1.0);

         double side = this.f_19796_.m_188499_() ? 1.0 : -1.0;
         double depthOffset = side * (0.025 + this.f_19796_.m_188500_() * 0.06) * scale;
         Vec3 spawnPosition = this.m_20182_()
            .m_82520_(0.0, 0.8, 0.0)
            .m_82549_(right.m_82490_(localX))
            .m_82520_(0.0, localY, 0.0)
            .m_82549_(normal.m_82490_(depthOffset));
         double outwardSpeed = side * (0.16 + this.f_19796_.m_188500_() * 0.28) * scale;
         double lateralSpeed = (this.f_19796_.m_188500_() - 0.5) * 0.1 * scale;
         double verticalSpeed = (this.f_19796_.m_188500_() - 0.35) * 0.12 * scale;
         Vec3 velocity = normal.m_82490_(outwardSpeed).m_82549_(right.m_82490_(lateralSpeed)).m_82520_(0.0, verticalSpeed, 0.0);
         this.m_9236_()
            .m_7106_(
               ParticleTypes.f_123789_,
               spawnPosition.f_82479_,
               spawnPosition.f_82480_,
               spawnPosition.f_82481_,
               velocity.f_82479_,
               velocity.f_82480_,
               velocity.f_82481_
            );
      }
   }

   private void beginTeleportFlow(Player player) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if (player instanceof ServerPlayer serverPlayer) {
            ArrayList waystoneEntries = new ArrayList();
            if (ModList.get().isLoaded("waystones")) {
               for (IWaystone ws : WaystonesCompat.getPlayerWaystones(player)) {
                  waystoneEntries.add(
                     new S2CPortalDestinationsPacket.WaystoneEntry(ws.getWaystoneUid(), ws.getName(), ws.getDimension(), ws.getPos(), ws.isGlobal())
                  );
               }
            }

            BlockPos respawnPos = serverPlayer.m_8961_();
            ResourceKey<Level> respawnDim = serverPlayer.m_8963_();
            boolean hasRespawn = respawnPos != null;
            ServerLevel overworld = serverPlayer.f_8924_.m_129880_(Level.f_46428_);
            BlockPos worldSpawn = overworld != null ? overworld.m_220360_() : serverLevel.m_220360_();
            ResourceKey<Level> currentDim = serverLevel.m_46472_();
            S2CPortalDestinationsPacket packet = new S2CPortalDestinationsPacket(
               hasRespawn, respawnDim, respawnPos, worldSpawn, currentDim, waystoneEntries, this.m_19879_()
            );
            DMCNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
            this.awaitingTeleportResponse = 0;
            this.pendingPlayer = player;
         }
      }
   }

   public void onDestinationChosen(ServerPlayer player, CPPortalDestinationChosen msg) {
      if (this.awaitingTeleportResponse >= 0 && this.pendingPlayer == player) {
         switch (msg.type()) {
            case PLAYER_SPAWN:
               this.teleportVanillaPriority(player);
               this.triggerClose();
               break;
            case WORLD_SPAWN:
               this.teleportToWorldSpawn(player);
               this.triggerClose();
               break;
            case WAYSTONE:
               this.teleportToWaystone(player, msg.waystoneUid());
               this.triggerClose();
               break;
            case XAERO_WAYPOINT:
            case FTB_CHUNKS:
            case JOURNEYMAP:
               this.teleportToPosition(player, msg.dimension(), msg.pos());
               this.triggerClose();
               break;
            case CUSTOM_COORDINATES:
               this.teleportToCustomCoordinates(player, msg.pos(), msg.customCoordinateMask());
               this.triggerClose();
               break;
            case VOID:
               ServerLevel voidLevel = player.f_8924_
                  .m_129880_(ResourceKey.m_135785_(Registries.f_256858_, ResourceLocation.fromNamespaceAndPath("invincible_dmc", "void")));
               if (voidLevel != null) {
                  player.m_8999_(voidLevel, 0.5, 1.0, 0.5, player.m_146908_(), player.m_146909_());
                  this.triggerClose();
               }
               break;
            case CANCEL:
               this.interactionCooldown = 40;
         }

         this.awaitingTeleportResponse = -1;
         this.pendingPlayer = null;
      }
   }

   private void teleportVanillaPriority(ServerPlayer player) {
      BlockPos respawnPos = player.m_8961_();
      ResourceKey<Level> respawnDim = player.m_8963_();
      if (respawnPos != null) {
         ServerLevel respawnLevel = player.f_8924_.m_129880_(respawnDim);
         if (respawnLevel != null) {
            Optional<Vec3> safeRespawn = Player.m_36130_(respawnLevel, respawnPos, 0.0F, false, false);
            if (safeRespawn.isPresent()) {
               Vec3 pos = safeRespawn.get();
               Vec3 safe = TeleportGroundUtils.resolveVerticalCollision(player, pos);
               player.m_8999_(respawnLevel, safe.f_82479_, safe.f_82480_, safe.f_82481_, player.m_146908_(), player.m_146909_());
               return;
            }

            if (player.m_8964_()) {
               Vec3 safe = TeleportGroundUtils.resolveVerticalCollision(
                  player, new Vec3((double)respawnPos.m_123341_() + 0.5, (double)respawnPos.m_123342_(), (double)respawnPos.m_123343_() + 0.5)
               );
               player.m_8999_(respawnLevel, safe.f_82479_, safe.f_82480_, safe.f_82481_, player.m_146908_(), player.m_146909_());
               return;
            }
         }

         player.m_5661_(Component.m_237115_("block.minecraft.spawn.not_valid"), true);
      }

      this.teleportToWorldSpawn(player);
   }

   private void teleportToWorldSpawn(ServerPlayer player) {
      ServerLevel overworld = player.f_8924_.m_129880_(Level.f_46428_);
      if (overworld != null) {
         BlockPos worldSpawn = overworld.m_220360_();
         Vec3 target = new Vec3((double)worldSpawn.m_123341_() + 0.5, (double)worldSpawn.m_123342_(), (double)worldSpawn.m_123343_() + 0.5);
         Vec3 safe = TeleportGroundUtils.resolveVerticalCollision(player, target);
         player.m_8999_(overworld, safe.f_82479_, safe.f_82480_, safe.f_82481_, player.m_146908_(), player.m_146909_());
      }
   }

   private void teleportToWaystone(ServerPlayer player, UUID waystoneUid) {
      if (!ModList.get().isLoaded("waystones")) {
         this.teleportVanillaPriority(player);
      } else {
         boolean success = WaystonesCompat.teleportToWaystone(player, waystoneUid);
         if (!success) {
            this.teleportVanillaPriority(player);
         }
      }
   }

   private void teleportToPosition(ServerPlayer player, ResourceKey<Level> dimension, BlockPos pos) {
      if (dimension != null && pos != null) {
         ServerLevel targetLevel = player.f_8924_.m_129880_(dimension);
         if (targetLevel == null) {
            targetLevel = player.f_8924_.m_129880_(Level.f_46428_);
         }

         if (targetLevel == null) {
            this.teleportVanillaPriority(player);
         } else {
            targetLevel.m_8602_(pos.m_123341_() >> 4, pos.m_123343_() >> 4, true);

            try {
               Vec3 target = new Vec3((double)pos.m_123341_() + 0.5, (double)pos.m_123342_(), (double)pos.m_123343_() + 0.5);
               Vec3 safe = TeleportGroundUtils.resolveVerticalCollision(player, target);
               player.m_8999_(targetLevel, safe.f_82479_, safe.f_82480_, safe.f_82481_, player.m_146908_(), player.m_146909_());
            } finally {
               targetLevel.m_8602_(pos.m_123341_() >> 4, pos.m_123343_() >> 4, false);
            }
         }
      } else {
         this.teleportVanillaPriority(player);
      }
   }

   private void teleportToCustomCoordinates(ServerPlayer player, BlockPos requestedPos, int currentAxisMask) {
      if (requestedPos != null && (currentAxisMask & -8) == 0) {
         BlockPos currentPos = player.m_20183_();
         int targetX = (currentAxisMask & 1) != 0 ? currentPos.m_123341_() : requestedPos.m_123341_();
         int targetY = (currentAxisMask & 2) != 0 ? currentPos.m_123342_() : requestedPos.m_123342_();
         int targetZ = (currentAxisMask & 4) != 0 ? currentPos.m_123343_() : requestedPos.m_123343_();
         BlockPos targetPos = new BlockPos(targetX, targetY, targetZ);
         ServerLevel targetLevel = (ServerLevel)player.m_9236_();
         boolean outsideCoordinateLimit = Math.abs((long)targetX) > 30000000L || Math.abs((long)targetZ) > 30000000L;
         boolean outsideBuildHeight = targetY < targetLevel.m_141937_() || targetY >= targetLevel.m_151558_();
         if (!outsideCoordinateLimit && !outsideBuildHeight && targetLevel.m_6857_().m_61937_(targetPos)) {
            this.teleportToPosition(player, targetLevel.m_46472_(), targetPos);
         } else {
            player.m_5661_(Component.m_237115_("message.invincible_dmc.portal.invalid_coordinates"), true);
         }
      } else {
         player.m_5661_(Component.m_237115_("message.invincible_dmc.portal.invalid_coordinates"), true);
      }
   }

   public void m_142687_(@NotNull RemovalReason reason) {
      super.m_142687_(reason);
      if (!this.m_9236_().f_46443_) {
         UUID uuid = this.getOwnerUUID();
         if (uuid != null) {
            ACTIVE_PORTALS.remove(uuid, this);
         }
      }
   }

   protected void m_7378_(@NotNull CompoundTag tag) {
      this.infiniteLifetime = tag.m_128471_("InfiniteLifetime");
      this.setPortalStyle(tag.m_128425_("PortalStyle", 1) ? tag.m_128445_("PortalStyle") : 1);
      this.setForceOriginalStyle(tag.m_128471_("ForceOriginalPortalStyle") || this.infiniteLifetime);
   }

   protected void m_7380_(@NotNull CompoundTag tag) {
      tag.m_128379_("InfiniteLifetime", this.infiniteLifetime);
      tag.m_128344_("PortalStyle", (Byte)this.f_19804_.m_135370_(DATA_PORTAL_STYLE));
      tag.m_128379_("ForceOriginalPortalStyle", (Boolean)this.f_19804_.m_135370_(DATA_FORCE_ORIGINAL_STYLE));
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public boolean m_6087_() {
      return false;
   }
}
