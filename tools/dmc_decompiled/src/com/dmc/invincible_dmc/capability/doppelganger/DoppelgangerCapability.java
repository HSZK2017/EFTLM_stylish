package com.dmc.invincible_dmc.capability.doppelganger;

import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.input.PlayerMovementFrame;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DoppelgangerCapability {
   public static final Capability<DoppelgangerCapability.IDoppelgangerData> INSTANCE = CapabilityManager.get(
      new CapabilityToken<DoppelgangerCapability.IDoppelgangerData>() {
      }
   );
   @Nullable
   private static DoppelgangerEntity cachedDoppel;
   @Nullable
   private static UUID cachedDoppelOwner;

   @Nullable
   public static UUID getCachedDoppelOwner() {
      return cachedDoppelOwner;
   }

   @OnlyIn(Dist.CLIENT)
   @Nullable
   public static DoppelgangerEntity getCachedDoppel(LocalPlayer player) {
      if (player != null && player.m_9236_() instanceof ClientLevel level) {
         DoppelgangerCapability.IDoppelgangerData data = (DoppelgangerCapability.IDoppelgangerData)player.getCapability(INSTANCE).resolve().orElse(null);
         if (data != null && data.getBindingState() == DoppelgangerCapability.BindingState.ACTIVE) {
            UUID capabilityUUID = data.getDoppelgangerUUID();
            if (capabilityUUID == null) {
               clearClientCache();
               return null;
            } else if (cachedDoppel != null
               && cachedDoppel.m_6084_()
               && player.m_20148_().equals(cachedDoppel.getOwnerUUID())
               && capabilityUUID.equals(cachedDoppel.m_20148_())
               && data.getBindingGeneration() == cachedDoppel.getBindingGeneration()
               && level.m_6815_(cachedDoppel.m_19879_()) == cachedDoppel) {
               return cachedDoppel;
            } else if (data.getDoppelgangerEntityId() >= 0
               && level.m_6815_(data.getDoppelgangerEntityId()) instanceof DoppelgangerEntity doppel
               && doppel.m_6084_()
               && capabilityUUID.equals(doppel.m_20148_())
               && data.getBindingGeneration() == doppel.getBindingGeneration()) {
               cachedDoppel = doppel;
               cachedDoppelOwner = player.m_20148_();
               return doppel;
            } else {
               for (Entity entity : level.m_104735_()) {
                  if (entity instanceof DoppelgangerEntity doppel
                     && doppel.m_6084_()
                     && player.m_20148_().equals(doppel.getOwnerUUID())
                     && capabilityUUID.equals(doppel.m_20148_())
                     && data.getBindingGeneration() == doppel.getBindingGeneration()) {
                     cachedDoppel = doppel;
                     cachedDoppelOwner = player.m_20148_();
                     return doppel;
                  }
               }

               clearClientCache();
               return null;
            }
         } else {
            clearClientCache();
            return null;
         }
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void clearClientCache() {
      cachedDoppel = null;
      cachedDoppelOwner = null;
   }

   public static enum BindingState {
      NONE,
      SPAWNING,
      ACTIVE,
      DESPAWNING;
   }

   public interface IDoppelgangerData {
      @Nullable
      UUID getDoppelgangerUUID();

      long getBindingGeneration();

      DoppelgangerCapability.BindingState getBindingState();

      String getBindingDimension();

      int getBindingDelayMode();

      int getDoppelgangerEntityId();

      void setDoppelgangerEntityId(int var1);

      void setBinding(@Nullable UUID var1, long var2, DoppelgangerCapability.BindingState var4, String var5, int var6);

      PlayerMovementFrame getLastMovementFrame();

      void setLastMovementFrame(PlayerMovementFrame var1);

      long getPendingSummonToken();

      long getPendingSummonGeneration();

      long getPendingSummonExpiry();

      int getPendingDoppelDelayMode();

      void beginPendingSummon(long var1, long var3, long var5, int var7);

      void clearPendingSummon();

      void applyBindingSnapshot(@Nullable UUID var1, long var2, DoppelgangerCapability.BindingState var4, String var5, int var6, int var7);

      CompoundTag serializeNBT();

      void deserializeNBT(CompoundTag var1);
   }

   public static class Impl implements DoppelgangerCapability.IDoppelgangerData {
      @Nullable
      private UUID doppelgangerUUID;
      private long bindingGeneration;
      private DoppelgangerCapability.BindingState bindingState = DoppelgangerCapability.BindingState.NONE;
      private String bindingDimension = "";
      private int bindingDelayMode = 1;
      private int doppelgangerEntityId = -1;
      private PlayerMovementFrame lastMovementFrame = PlayerMovementFrame.EMPTY;
      private long pendingSummonToken;
      private long pendingSummonGeneration;
      private long pendingSummonExpiry;
      private int pendingDoppelDelayMode = -1;

      @Nullable
      @Override
      public UUID getDoppelgangerUUID() {
         return this.doppelgangerUUID;
      }

      @Override
      public long getBindingGeneration() {
         return this.bindingGeneration;
      }

      @Override
      public DoppelgangerCapability.BindingState getBindingState() {
         return this.bindingState;
      }

      @Override
      public String getBindingDimension() {
         return this.bindingDimension;
      }

      @Override
      public int getBindingDelayMode() {
         return this.bindingDelayMode;
      }

      @Override
      public int getDoppelgangerEntityId() {
         return this.doppelgangerEntityId;
      }

      @Override
      public void setDoppelgangerEntityId(int entityId) {
         this.doppelgangerEntityId = entityId;
      }

      @Override
      public void setBinding(@Nullable UUID uuid, long generation, DoppelgangerCapability.BindingState state, String dimension, int delayMode) {
         this.doppelgangerUUID = uuid;
         this.bindingGeneration = Math.max(0L, generation);
         this.bindingState = state == null ? DoppelgangerCapability.BindingState.NONE : state;
         this.bindingDimension = dimension == null ? "" : dimension;
         this.bindingDelayMode = Math.max(0, Math.min(2, delayMode));
         if (uuid == null || this.bindingState != DoppelgangerCapability.BindingState.ACTIVE) {
            this.doppelgangerEntityId = -1;
         }
      }

      @Override
      public PlayerMovementFrame getLastMovementFrame() {
         return this.lastMovementFrame;
      }

      @Override
      public void setLastMovementFrame(PlayerMovementFrame frame) {
         this.lastMovementFrame = frame;
      }

      @Override
      public long getPendingSummonToken() {
         return this.pendingSummonToken;
      }

      @Override
      public long getPendingSummonGeneration() {
         return this.pendingSummonGeneration;
      }

      @Override
      public long getPendingSummonExpiry() {
         return this.pendingSummonExpiry;
      }

      @Override
      public int getPendingDoppelDelayMode() {
         return this.pendingDoppelDelayMode;
      }

      @Override
      public void beginPendingSummon(long token, long generation, long expiry, int mode) {
         this.pendingSummonToken = token;
         this.pendingSummonGeneration = generation;
         this.pendingSummonExpiry = expiry;
         this.pendingDoppelDelayMode = Math.max(0, Math.min(2, mode));
      }

      @Override
      public void clearPendingSummon() {
         this.pendingSummonToken = 0L;
         this.pendingSummonGeneration = 0L;
         this.pendingSummonExpiry = 0L;
         this.pendingDoppelDelayMode = -1;
      }

      @Override
      public void applyBindingSnapshot(
         @Nullable UUID uuid, long generation, DoppelgangerCapability.BindingState state, String dimension, int delayMode, int entityId
      ) {
         if (generation >= this.bindingGeneration) {
            if (generation != this.bindingGeneration || bindingPhase(state) >= bindingPhase(this.bindingState)) {
               this.setBinding(uuid, generation, state, dimension, delayMode);
               this.doppelgangerEntityId = entityId;
            }
         }
      }

      private static int bindingPhase(DoppelgangerCapability.BindingState state) {
         return switch (state) {
            case SPAWNING -> 0;
            case ACTIVE -> 1;
            case DESPAWNING -> 2;
            case NONE -> 3;
         };
      }

      @Override
      public CompoundTag serializeNBT() {
         CompoundTag tag = new CompoundTag();
         if (this.doppelgangerUUID != null) {
            tag.m_128362_("doppelUUID", this.doppelgangerUUID);
         }

         tag.m_128356_("bindingGeneration", this.bindingGeneration);
         tag.m_128359_("bindingState", this.bindingState.name());
         tag.m_128359_("bindingDimension", this.bindingDimension);
         tag.m_128405_("bindingDelayMode", this.bindingDelayMode);
         return tag;
      }

      @Override
      public void deserializeNBT(CompoundTag nbt) {
         this.doppelgangerUUID = nbt.m_128403_("doppelUUID") ? nbt.m_128342_("doppelUUID") : null;
         this.bindingGeneration = Math.max(0L, nbt.m_128454_("bindingGeneration"));
         this.bindingState = parseState(nbt.m_128461_("bindingState"), this.doppelgangerUUID);
         if (this.bindingState == DoppelgangerCapability.BindingState.SPAWNING || this.bindingState == DoppelgangerCapability.BindingState.DESPAWNING) {
            this.bindingState = DoppelgangerCapability.BindingState.NONE;
            this.doppelgangerUUID = null;
         }

         this.bindingDimension = nbt.m_128461_("bindingDimension");
         this.bindingDelayMode = nbt.m_128441_("bindingDelayMode") ? Math.max(0, Math.min(2, nbt.m_128451_("bindingDelayMode"))) : 1;
         this.doppelgangerEntityId = -1;
         this.clearPendingSummon();
      }

      private static DoppelgangerCapability.BindingState parseState(String value, @Nullable UUID uuid) {
         if (value != null && !value.isEmpty()) {
            try {
               return DoppelgangerCapability.BindingState.valueOf(value);
            } catch (IllegalArgumentException var3) {
               return uuid == null ? DoppelgangerCapability.BindingState.NONE : DoppelgangerCapability.BindingState.ACTIVE;
            }
         } else {
            return uuid == null ? DoppelgangerCapability.BindingState.NONE : DoppelgangerCapability.BindingState.ACTIVE;
         }
      }
   }

   public static class Provider implements ICapabilitySerializable<CompoundTag> {
      private final DoppelgangerCapability.IDoppelgangerData data = new DoppelgangerCapability.Impl();
      private final LazyOptional<DoppelgangerCapability.IDoppelgangerData> optional = LazyOptional.of(() -> this.data);

      @NotNull
      public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
         return cap == DoppelgangerCapability.INSTANCE ? this.optional.cast() : LazyOptional.empty();
      }

      @NotNull
      public CompoundTag serializeNBT() {
         return this.data.serializeNBT();
      }

      public void deserializeNBT(@NotNull CompoundTag nbt) {
         this.data.deserializeNBT(nbt);
      }
   }
}
