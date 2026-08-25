package com.pla.annoyingvillagers.capabilities;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public final class SnakeBladeCapability {
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "snake_blade_cap");
   private static final String NBT_HAS_SNAKE_BLADE = "hasSnakeBlade";
   private static final String NBT_LAST_ID = "getLastSnakeBladeID";
   private static final String NBT_LAST_UUID = "getLastSnakeBladeUUID";

   private SnakeBladeCapability() {
   }

   public interface ISnakeBladeCapability extends INBTSerializable<CompoundTag> {
      void setHasSnakeBlade(boolean var1);

      boolean hasSnakeBlade();

      void setLastSnakeBladeID(int var1);

      int getLastSnakeBladeID();

      @Nullable
      UUID getLastSnakeBladeUUID();

      void setLastSnakeBladeUUID(@Nullable UUID var1);
   }

   public static final class SnakeBladeCapabilityImp implements SnakeBladeCapability.ISnakeBladeCapability {
      private boolean hasSnakeBlade;
      private int lastSnakeBladeId = -1;
      @Nullable
      private UUID lastSnakeBladeUuid;

      @Override
      public void setHasSnakeBlade(boolean hasSnakeBlade) {
         this.hasSnakeBlade = hasSnakeBlade;
      }

      @Override
      public boolean hasSnakeBlade() {
         return this.hasSnakeBlade;
      }

      @Override
      public void setLastSnakeBladeID(int id) {
         this.lastSnakeBladeId = id;
      }

      @Override
      public int getLastSnakeBladeID() {
         return this.lastSnakeBladeId;
      }

      @Nullable
      @Override
      public UUID getLastSnakeBladeUUID() {
         return this.lastSnakeBladeUuid;
      }

      @Override
      public void setLastSnakeBladeUUID(@Nullable UUID uuid) {
         this.lastSnakeBladeUuid = uuid;
      }

      public CompoundTag serializeNBT() {
         CompoundTag tag = new CompoundTag();
         tag.m_128379_("hasSnakeBlade", this.hasSnakeBlade);
         tag.m_128405_("getLastSnakeBladeID", this.lastSnakeBladeId);
         if (this.lastSnakeBladeUuid != null) {
            tag.m_128362_("getLastSnakeBladeUUID", this.lastSnakeBladeUuid);
         }

         return tag;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.hasSnakeBlade = nbt.m_128471_("hasSnakeBlade");
         this.lastSnakeBladeId = nbt.m_128441_("getLastSnakeBladeID") ? nbt.m_128451_("getLastSnakeBladeID") : -1;
         this.lastSnakeBladeUuid = nbt.m_128403_("getLastSnakeBladeUUID") ? nbt.m_128342_("getLastSnakeBladeUUID") : null;
      }
   }
}
