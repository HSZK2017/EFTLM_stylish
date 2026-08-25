package com.dmc.invincible_dmc.conditions;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class AirborneCondition implements Condition<ServerPlayerPatch> {
   private float minHeight;

   public AirborneCondition() {
      this.minHeight = 0.0F;
   }

   public AirborneCondition(float minHeight) {
      this.minHeight = minHeight;
   }

   public float getMinHeight() {
      return this.minHeight;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      this.minHeight = compoundTag.m_128457_("minHeight");
      return this;
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128350_("minHeight", this.minHeight);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
      double heightAboveGround = this.getHeightAboveGround(player);
      return heightAboveGround < (double)this.minHeight ? false : player.m_20069_() || !player.m_20096_();
   }

   private double getHeightAboveGround(ServerPlayer player) {
      double playerY = player.m_20186_();
      BlockPos playerPos = player.m_20183_();
      int scanDown = Math.min(playerPos.m_123342_() - player.m_9236_().m_141937_(), 20);

      for (int y = playerPos.m_123342_() - 1; y >= playerPos.m_123342_() - scanDown; y--) {
         BlockPos checkPos = new BlockPos(playerPos.m_123341_(), y, playerPos.m_123343_());
         BlockState state = player.m_9236_().m_8055_(checkPos);
         if (state.m_280555_()) {
            return playerY - ((double)y + 1.0);
         }
      }

      return Double.MAX_VALUE;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
