package com.dmc.invincible_dmc.capability.doppelganger;

import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.server.S2CDoppelgangerSyncPacket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public final class DoppelgangerBindingService {
   private static final long SUMMON_TOKEN_TTL = 40L;

   private DoppelgangerBindingService() {
   }

   public static long requestSummon(ServerPlayer owner, int delayMode) {
      reconcile(owner);
      DoppelgangerEntity existing = findBoundEntity(owner);
      if (existing == null) {
         existing = findOwnedEntities(owner)
            .stream()
            .filter(entity -> entity.m_9236_() == owner.m_284548_())
            .max(Comparator.comparingLong(DoppelgangerEntity::getBindingGeneration))
            .orElse(null);
      }

      if (existing != null && existing.m_9236_() != owner.m_284548_()) {
         discard(existing);
         existing = null;
      }

      if (existing != null) {
         bindActive(owner, existing);
         DoppelgangerEntity.recallDoppelganger(existing);
         return 0L;
      } else {
         DoppelgangerCapability.IDoppelgangerData data = getData(owner);
         if (data == null) {
            return 0L;
         } else {
            long generation = Math.max(1L, data.getBindingGeneration() + 1L);
            long token = nextToken();
            int mode = Math.max(0, Math.min(2, delayMode));
            String dimension = owner.m_284548_().m_46472_().m_135782_().toString();
            data.setBinding(null, generation, DoppelgangerCapability.BindingState.SPAWNING, dimension, mode);
            data.beginPendingSummon(token, generation, owner.m_284548_().m_46467_() + 40L, mode);
            sync(owner);
            return token;
         }
      }
   }

   public static boolean spawnImmediate(ServerPlayer owner, int delayMode) {
      long token = requestSummon(owner, delayMode);
      return token == 0L || consumePendingSummon(owner);
   }

   public static boolean consumePendingSummon(ServerPlayer owner) {
      DoppelgangerCapability.IDoppelgangerData data = getData(owner);
      if (data != null && data.getPendingSummonToken() != 0L) {
         long gameTime = owner.m_284548_().m_46467_();
         long generation = data.getPendingSummonGeneration();
         int delayMode = data.getPendingDoppelDelayMode();
         boolean valid = data.getBindingState() == DoppelgangerCapability.BindingState.SPAWNING
            && generation == data.getBindingGeneration()
            && gameTime <= data.getPendingSummonExpiry();
         data.clearPendingSummon();
         if (!valid) {
            if (data.getBindingState() == DoppelgangerCapability.BindingState.SPAWNING) {
               data.setBinding(
                  null, data.getBindingGeneration(), DoppelgangerCapability.BindingState.NONE, owner.m_284548_().m_46472_().m_135782_().toString(), delayMode
               );
            }

            sync(owner);
            return false;
         } else {
            DoppelgangerEntity entity = DoppelgangerEntity.spawnBound(owner.m_284548_(), owner, delayMode, generation);
            if (entity == null) {
               data.setBinding(null, generation, DoppelgangerCapability.BindingState.NONE, owner.m_284548_().m_46472_().m_135782_().toString(), delayMode);
               sync(owner);
               return false;
            } else {
               bindActive(owner, entity);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static void cancelExpiredPendingSummon(ServerPlayer owner) {
      DoppelgangerCapability.IDoppelgangerData data = getData(owner);
      if (data != null && data.getPendingSummonToken() != 0L) {
         if (owner.m_284548_().m_46467_() > data.getPendingSummonExpiry()) {
            data.clearPendingSummon();
            if (data.getBindingState() == DoppelgangerCapability.BindingState.SPAWNING) {
               data.setBinding(
                  null,
                  data.getBindingGeneration(),
                  DoppelgangerCapability.BindingState.NONE,
                  owner.m_284548_().m_46472_().m_135782_().toString(),
                  data.getBindingDelayMode()
               );
            }

            sync(owner);
         }
      }
   }

   public static void cancelPendingSummon(ServerPlayer owner) {
      DoppelgangerCapability.IDoppelgangerData data = getData(owner);
      if (data != null && data.getPendingSummonToken() != 0L) {
         data.clearPendingSummon();
         if (data.getBindingState() == DoppelgangerCapability.BindingState.SPAWNING) {
            data.setBinding(
               null,
               data.getBindingGeneration(),
               DoppelgangerCapability.BindingState.NONE,
               owner.m_284548_().m_46472_().m_135782_().toString(),
               data.getBindingDelayMode()
            );
         }

         sync(owner);
      }
   }

   public static void discard(DoppelgangerEntity entity) {
      UUID ownerUUID = entity.getOwnerUUID();
      ServerPlayer owner = findOnlineOwner(entity, ownerUUID);
      if (owner != null) {
         DoppelgangerCapability.IDoppelgangerData data = getData(owner);
         if (matches(data, entity)) {
            data.setBinding(
               entity.m_20148_(),
               entity.getBindingGeneration(),
               DoppelgangerCapability.BindingState.DESPAWNING,
               entity.m_9236_().m_46472_().m_135782_().toString(),
               entity.getDoppelDelayMode()
            );
            data.clearPendingSummon();
            sync(owner);
         }
      }

      DoppelgangerEntity.discardWithoutBinding(entity);
      if (owner != null) {
         DoppelgangerCapability.IDoppelgangerData data = getData(owner);
         if (matches(data, entity)) {
            data.setBinding(
               null,
               entity.getBindingGeneration(),
               DoppelgangerCapability.BindingState.NONE,
               owner.m_284548_().m_46472_().m_135782_().toString(),
               entity.getDoppelDelayMode()
            );
            sync(owner);
         }
      }
   }

   public static void discardOwned(ServerPlayer owner) {
      for (DoppelgangerEntity entity : findOwnedEntities(owner)) {
         DoppelgangerEntity.discardWithoutBinding(entity);
      }

      DoppelgangerCapability.IDoppelgangerData data = getData(owner);
      if (data != null) {
         data.clearPendingSummon();
         data.setBinding(
            null,
            data.getBindingGeneration(),
            DoppelgangerCapability.BindingState.NONE,
            owner.m_284548_().m_46472_().m_135782_().toString(),
            data.getBindingDelayMode()
         );
         sync(owner);
      }
   }

   public static void reconcile(ServerPlayer owner) {
      DoppelgangerCapability.IDoppelgangerData data = getData(owner);
      if (data != null) {
         List<DoppelgangerEntity> owned = findOwnedEntities(owner);
         DoppelgangerEntity current = owned.stream()
            .filter(entityx -> Objects.equals(entityx.m_20148_(), data.getDoppelgangerUUID()))
            .filter(entityx -> entityx.getBindingGeneration() == data.getBindingGeneration())
            .findFirst()
            .orElse(null);
         if (current == null) {
            long minimumGeneration = data.getBindingState() == DoppelgangerCapability.BindingState.NONE
               ? data.getBindingGeneration() + 1L
               : data.getBindingGeneration();
            current = owned.stream()
               .filter(entityx -> entityx.getBindingGeneration() >= minimumGeneration)
               .filter(entityx -> entityx.m_9236_() == owner.m_284548_())
               .max(Comparator.comparingLong(DoppelgangerEntity::getBindingGeneration))
               .orElse(null);
         }

         for (DoppelgangerEntity entity : owned) {
            if (entity != current && (current != null || entity.getBindingGeneration() < data.getBindingGeneration())) {
               DoppelgangerEntity.discardWithoutBinding(entity);
            }
         }

         if (current != null) {
            bindActive(owner, current);
         } else {
            cancelExpiredPendingSummon(owner);
         }
      }
   }

   public static void clearBinding(ServerPlayer owner) {
      DoppelgangerCapability.IDoppelgangerData data = getData(owner);
      if (data != null) {
         data.clearPendingSummon();
         data.setBinding(
            null,
            data.getBindingGeneration(),
            DoppelgangerCapability.BindingState.NONE,
            owner.m_284548_().m_46472_().m_135782_().toString(),
            data.getBindingDelayMode()
         );
         sync(owner);
      }
   }

   public static void bindActive(ServerPlayer owner, DoppelgangerEntity entity) {
      DoppelgangerCapability.IDoppelgangerData data = getData(owner);
      if (data != null) {
         long generation = Math.max(1L, Math.max(data.getBindingGeneration(), entity.getBindingGeneration()));
         String dimension = entity.m_9236_().m_46472_().m_135782_().toString();
         boolean changed = !Objects.equals(data.getDoppelgangerUUID(), entity.m_20148_())
            || data.getBindingGeneration() != generation
            || data.getBindingState() != DoppelgangerCapability.BindingState.ACTIVE
            || !Objects.equals(data.getBindingDimension(), dimension)
            || data.getBindingDelayMode() != entity.getDoppelDelayMode()
            || data.getDoppelgangerEntityId() != entity.m_19879_();
         entity.setBindingGeneration(generation);
         data.clearPendingSummon();
         data.setBinding(entity.m_20148_(), generation, DoppelgangerCapability.BindingState.ACTIVE, dimension, entity.getDoppelDelayMode());
         data.setDoppelgangerEntityId(entity.m_19879_());
         if (changed) {
            sync(owner);
         }
      }
   }

   @Nullable
   public static DoppelgangerEntity findBoundEntity(ServerPlayer owner) {
      DoppelgangerCapability.IDoppelgangerData data = getData(owner);
      if (data != null && data.getDoppelgangerUUID() != null) {
         return data.getDoppelgangerEntityId() >= 0
               && owner.m_284548_().m_6815_(data.getDoppelgangerEntityId()) instanceof DoppelgangerEntity doppel
               && doppel.m_6084_()
               && data.getDoppelgangerUUID().equals(doppel.m_20148_())
               && data.getBindingGeneration() == doppel.getBindingGeneration()
            ? doppel
            : findOwnedEntities(owner)
               .stream()
               .filter(entity -> data.getDoppelgangerUUID().equals(entity.m_20148_()))
               .filter(entity -> data.getBindingGeneration() == entity.getBindingGeneration())
               .findFirst()
               .orElse(null);
      } else {
         return null;
      }
   }

   public static void sync(ServerPlayer owner) {
      S2CDoppelgangerSyncPacket packet = createPacket(owner);
      if (packet != null) {
         DMCNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> owner), packet);
      }
   }

   public static void syncTo(ServerPlayer owner, ServerPlayer recipient) {
      S2CDoppelgangerSyncPacket packet = createPacket(owner);
      if (packet != null) {
         DMCNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> recipient), packet);
      }
   }

   @Nullable
   private static S2CDoppelgangerSyncPacket createPacket(ServerPlayer owner) {
      DoppelgangerCapability.IDoppelgangerData data = getData(owner);
      if (data == null) {
         return null;
      } else {
         DoppelgangerEntity entity = findBoundEntity(owner);
         int entityId = entity != null ? entity.m_19879_() : -1;
         data.setDoppelgangerEntityId(entityId);
         return new S2CDoppelgangerSyncPacket(
            owner.m_19879_(),
            owner.m_20148_(),
            entityId,
            data.getDoppelgangerUUID(),
            data.getBindingGeneration(),
            data.getBindingState(),
            data.getBindingDimension(),
            data.getBindingDelayMode()
         );
      }
   }

   private static List<DoppelgangerEntity> findOwnedEntities(ServerPlayer owner) {
      List<DoppelgangerEntity> entities = new ArrayList<>();

      for (ServerLevel level : owner.f_8924_.m_129785_()) {
         for (Entity entity : level.m_142646_().m_142273_()) {
            if (entity instanceof DoppelgangerEntity) {
               DoppelgangerEntity doppel = (DoppelgangerEntity)entity;
               if (doppel.m_6084_() && owner.m_20148_().equals(doppel.getOwnerUUID())) {
                  entities.add(doppel);
               }
            }
         }
      }

      return entities;
   }

   @Nullable
   private static DoppelgangerCapability.IDoppelgangerData getData(ServerPlayer owner) {
      return (DoppelgangerCapability.IDoppelgangerData)owner.getCapability(DoppelgangerCapability.INSTANCE).resolve().orElse(null);
   }

   private static boolean matches(@Nullable DoppelgangerCapability.IDoppelgangerData data, DoppelgangerEntity entity) {
      return data != null && Objects.equals(data.getDoppelgangerUUID(), entity.m_20148_()) && data.getBindingGeneration() == entity.getBindingGeneration();
   }

   @Nullable
   private static ServerPlayer findOnlineOwner(DoppelgangerEntity entity, @Nullable UUID ownerUUID) {
      return ownerUUID != null && entity.m_9236_() instanceof ServerLevel level ? level.m_7654_().m_6846_().m_11259_(ownerUUID) : null;
   }

   private static long nextToken() {
      long token;
      do {
         token = ThreadLocalRandom.current().nextLong();
      } while (token == 0L);

      return token;
   }
}
