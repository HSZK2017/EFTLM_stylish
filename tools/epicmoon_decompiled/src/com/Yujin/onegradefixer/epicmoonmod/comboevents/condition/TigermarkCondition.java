package com.Yujin.onegradefixer.epicmoonmod.comboevents.condition;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class TigermarkCondition implements Condition<ServerPlayerPatch> {
   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      Player player = (Player)serverPlayerPatch.getOriginal();
      Inventory inventory = player.m_150109_();
      ItemStack ts = inventory.f_35978_.m_21205_();
      CompoundTag TSTG = ts.m_41783_();
      int tgmk = TSTG.m_128451_("ammotype");
      return tgmk == 1;
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
