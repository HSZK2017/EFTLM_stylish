package com.dmc.invincible_dmc.capability.exclusiveSkill;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OriginalSkillCapability {
   private static final int DATA_VERSION = 3;
   public static final Capability<OriginalSkillCapability.IOriginalSkillMemory> INSTANCE = CapabilityManager.get(
      new CapabilityToken<OriginalSkillCapability.IOriginalSkillMemory>() {
      }
   );

   public interface IOriginalSkillMemory {
      @Nullable
      OriginalSkillCapability.SkillSnapshot getSnapshot(String var1);

      void saveSnapshot(String var1, OriginalSkillCapability.SkillSnapshot var2);

      void removeSnapshot(String var1);

      void copyFrom(OriginalSkillCapability.IOriginalSkillMemory var1);

      CompoundTag serializeNBT();

      void deserializeNBT(CompoundTag var1);
   }

   public static class OriginalSkillMemory implements OriginalSkillCapability.IOriginalSkillMemory {
      private final Map<String, OriginalSkillCapability.SkillSnapshot> snapshots = new HashMap<>();

      @Nullable
      @Override
      public OriginalSkillCapability.SkillSnapshot getSnapshot(String slotName) {
         return this.snapshots.get(normalizeSlotName(slotName));
      }

      @Override
      public void saveSnapshot(String slotName, OriginalSkillCapability.SkillSnapshot snapshot) {
         this.snapshots.put(normalizeSlotName(slotName), snapshot);
      }

      @Override
      public void removeSnapshot(String slotName) {
         this.snapshots.remove(normalizeSlotName(slotName));
      }

      @Override
      public void copyFrom(OriginalSkillCapability.IOriginalSkillMemory other) {
         this.deserializeNBT(other.serializeNBT());
      }

      @Override
      public CompoundTag serializeNBT() {
         CompoundTag root = new CompoundTag();
         CompoundTag slots = new CompoundTag();
         root.m_128405_("Version", 3);
         this.snapshots.forEach((slotName, snapshot) -> {
            CompoundTag slot = new CompoundTag();
            putResourceLocation(slot, "Original", snapshot.originalSkill());
            putResourceLocation(slot, "Replacement", snapshot.replacementSkill());
            putResourceLocation(slot, "SourceItem", snapshot.sourceItem());
            slot.m_128379_("OriginalDisabled", snapshot.originalDisabled());
            slots.m_128365_(slotName, slot);
         });
         root.m_128365_("Slots", slots);
         return root;
      }

      @Override
      public void deserializeNBT(CompoundTag nbt) {
         this.snapshots.clear();
         if (nbt.m_128425_("Slots", 10)) {
            CompoundTag slots = nbt.m_128469_("Slots");

            for (String slotName : slots.m_128431_()) {
               CompoundTag slot = slots.m_128469_(slotName);
               this.snapshots
                  .put(
                     normalizeSlotName(slotName),
                     new OriginalSkillCapability.SkillSnapshot(
                        readResourceLocation(slot, "Original"),
                        readResourceLocation(slot, "Replacement"),
                        readResourceLocation(slot, "SourceItem"),
                        slot.m_128425_("OriginalDisabled", 1) ? slot.m_128471_("OriginalDisabled") : readResourceLocation(slot, "Original") == null,
                        false
                     )
                  );
            }
         } else {
            for (String slotName : nbt.m_128431_()) {
               if (!"Version".equals(slotName)) {
                  String legacySkillId = nbt.m_128461_(slotName);
                  ResourceLocation originalSkill = "none".equals(legacySkillId) ? null : ResourceLocation.m_135820_(legacySkillId);
                  this.snapshots
                     .put(normalizeSlotName(slotName), new OriginalSkillCapability.SkillSnapshot(originalSkill, null, null, originalSkill == null, true));
               }
            }
         }
      }

      private static String normalizeSlotName(String slotName) {
         return slotName.toLowerCase(Locale.ROOT);
      }

      private static void putResourceLocation(CompoundTag tag, String key, @Nullable ResourceLocation value) {
         if (value != null) {
            tag.m_128359_(key, value.toString());
         }
      }

      @Nullable
      private static ResourceLocation readResourceLocation(CompoundTag tag, String key) {
         return tag.m_128425_(key, 8) ? ResourceLocation.m_135820_(tag.m_128461_(key)) : null;
      }
   }

   public static class Provider implements ICapabilitySerializable<CompoundTag> {
      private final OriginalSkillCapability.IOriginalSkillMemory memory = new OriginalSkillCapability.OriginalSkillMemory();
      private final LazyOptional<OriginalSkillCapability.IOriginalSkillMemory> optional = LazyOptional.of(() -> this.memory);

      @NotNull
      public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
         return cap == OriginalSkillCapability.INSTANCE ? this.optional.cast() : LazyOptional.empty();
      }

      public CompoundTag serializeNBT() {
         return this.memory.serializeNBT();
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.memory.deserializeNBT(nbt);
      }

      public void invalidate() {
         this.optional.invalidate();
      }
   }

   public static record SkillSnapshot(
      @Nullable ResourceLocation originalSkill,
      @Nullable ResourceLocation replacementSkill,
      @Nullable ResourceLocation sourceItem,
      boolean originalDisabled,
      boolean legacy
   ) {
   }
}
