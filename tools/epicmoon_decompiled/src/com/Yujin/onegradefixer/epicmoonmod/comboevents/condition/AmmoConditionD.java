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

public class AmmoConditionD implements Condition<ServerPlayerPatch> {
   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      ItemStack round = ((Item)EpicmoonItems.Accel_ROUND.get()).m_7968_();
      Player player = (Player)serverPlayerPatch.getOriginal();
      ItemStack itemStack = player.m_21205_();
      int ammo = itemStack.m_41784_().m_128451_("amount");
      Inventory inventory = player.m_150109_();
      return inventory.m_36063_(round) && ammo != 10;
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
