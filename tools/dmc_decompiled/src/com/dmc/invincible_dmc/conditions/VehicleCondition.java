package com.dmc.invincible_dmc.conditions;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class VehicleCondition implements Condition<ServerPlayerPatch> {
   private boolean hasVehicle;

   public VehicleCondition() {
   }

   public VehicleCondition(boolean hasVehicle) {
      this.hasVehicle = hasVehicle;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      if (!compoundTag.m_128441_("has_vehicle")) {
         throw new IllegalArgumentException("custom vehicle condition error: has_vehicle not specified!");
      } else {
         this.hasVehicle = compoundTag.m_128471_("has_vehicle");
         return this;
      }
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128379_("has_vehicle", this.hasVehicle);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      return this.hasVehicle == ((ServerPlayer)serverPlayerPatch.getOriginal()).m_20159_();
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
