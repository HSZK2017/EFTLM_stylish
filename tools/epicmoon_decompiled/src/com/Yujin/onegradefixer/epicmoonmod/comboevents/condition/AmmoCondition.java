package com.Yujin.onegradefixer.epicmoonmod.comboevents.condition;

import com.Yujin.onegradefixer.epicmoonmod.item.EpicmoonItems;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class AmmoCondition implements Condition<ServerPlayerPatch> {
   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      ItemStack round = ((Item)EpicmoonItems.TIGERMARK_ROUND.get()).m_7968_();
      ItemStack round2 = ((Item)EpicmoonItems.SAVAGE_TIGERMARK_ROUND.get()).m_7968_();
      Player player = (Player)serverPlayerPatch.getOriginal();
      Inventory inventory = player.m_150109_();
      return inventory.m_36063_(round) ? true : inventory.m_36063_(round2);
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      return null;
   }

   public CompoundTag serializePredicate() {
      return null;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
