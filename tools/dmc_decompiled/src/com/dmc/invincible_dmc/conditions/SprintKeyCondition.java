package com.dmc.invincible_dmc.conditions;

import com.dmc.invincible_dmc.client.input.PlayerInputState;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class SprintKeyCondition implements Condition<ServerPlayerPatch> {
   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      return this;
   }

   public CompoundTag serializePredicate() {
      return new CompoundTag();
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      return PlayerInputState.isRemoteDown((Player)serverPlayerPatch.getOriginal(), 5);
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
