package com.dmc.invincible_dmc.conditions;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class JumpCondition implements Condition<PlayerPatch<?>> {
   public Condition<PlayerPatch<?>> read(CompoundTag compoundTag) {
      return this;
   }

   public CompoundTag serializePredicate() {
      return new CompoundTag();
   }

   public boolean predicate(PlayerPatch<?> playerPatch) {
      Player player = (Player)playerPatch.getOriginal();
      return !player.m_20096_() && !player.m_20069_() && player.m_20184_().f_82480_ > 0.05;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
