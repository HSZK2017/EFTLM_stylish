package com.dmc.invincible_dmc.conditions;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class SprintingCondition implements Condition<ServerPlayerPatch> {
   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      return this;
   }

   public CompoundTag serializePredicate() {
      return new CompoundTag();
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
      return player.m_20142_();
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
