package com.pla.annoyingvillagers.blockentity;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlockEntities;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ShadowObsidianBlockEntity extends BlockEntity {
   @Nullable
   private UUID owner;

   public ShadowObsidianBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)AnnoyingVillagersModBlockEntities.SHADOW_OBSIDIAN_BLOCK.get(), pos, state);
   }

   public void setOwner(@Nullable UUID id) {
      this.owner = id;
      this.m_6596_();
   }

   @Nullable
   public UUID getOwner() {
      return this.owner;
   }

   protected void m_183515_(@NotNull CompoundTag tag) {
      super.m_183515_(tag);
      if (this.owner != null) {
         tag.m_128362_("Owner", this.owner);
      }
   }

   public void m_142466_(@NotNull CompoundTag tag) {
      super.m_142466_(tag);
      this.owner = tag.m_128403_("Owner") ? tag.m_128342_("Owner") : null;
   }

   @NotNull
   public CompoundTag m_5995_() {
      CompoundTag tag = super.m_5995_();
      if (this.owner != null) {
         tag.m_128362_("Owner", this.owner);
      }

      return tag;
   }
}
