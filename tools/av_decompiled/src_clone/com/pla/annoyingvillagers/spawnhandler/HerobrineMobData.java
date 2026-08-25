package com.pla.annoyingvillagers.spawnhandler;

import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.entity.LowShadowHerobrineCloneEntity;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class HerobrineMobData extends SavedData {
   public static final String ID = "av_singleton_herobrine";
   private UUID activeId = null;
   private long claimTick = 0L;
   private static final long COOLDOWN_TICKS = 12000L;

   public static HerobrineMobData get(ServerLevel serverLevel) {
      return (HerobrineMobData)serverLevel.m_8895_().m_164861_(HerobrineMobData::load, HerobrineMobData::new, "av_singleton_herobrine");
   }

   public static HerobrineMobData load(CompoundTag compoundTag) {
      HerobrineMobData herobrineData = new HerobrineMobData();
      if (compoundTag.m_128403_("activeId")) {
         herobrineData.activeId = compoundTag.m_128342_("activeId");
      }

      if (compoundTag.m_128425_("claimTick", 4)) {
         herobrineData.claimTick = compoundTag.m_128454_("claimTick");
      }

      return herobrineData;
   }

   @NotNull
   public CompoundTag m_7176_(@NotNull CompoundTag compoundTag) {
      if (this.activeId != null) {
         compoundTag.m_128362_("activeId", this.activeId);
      }

      compoundTag.m_128356_("claimTick", this.claimTick);
      return compoundTag;
   }

   private static long now(ServerLevel level) {
      return level.m_46467_();
   }

   public boolean isOccupied(ServerLevel serverLevel) {
      if (this.activeId == null) {
         if (this.claimTick <= 0L) {
            return false;
         } else {
            long elapsed = now(serverLevel) - this.claimTick;
            return elapsed < 12000L;
         }
      } else {
         Entity entity = serverLevel.m_8791_(this.activeId);
         if ((entity instanceof HerobrineMob || entity instanceof LowShadowHerobrineCloneEntity) && entity.m_6084_()) {
            return true;
         } else {
            this.activeId = null;
            this.m_77762_();
            return false;
         }
      }
   }

   public boolean tryClaim(ServerLevel serverLevel, UUID id) {
      if (this.isOccupied(serverLevel)) {
         return false;
      } else {
         this.activeId = id;
         this.claimTick = now(serverLevel);
         this.m_77762_();
         return true;
      }
   }

   public void releaseIfMatches(ServerLevel serverLevel, UUID id) {
      if (this.activeId != null && this.activeId.equals(id)) {
         this.activeId = null;
         this.claimTick = now(serverLevel);
         this.m_77762_();
      }
   }
}
