package com.dmc.invincible_dmc.conditions;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class PressedTimeCondition implements Condition<ServerPlayerPatch> {
   private int min;
   private int max = Integer.MAX_VALUE;

   public PressedTimeCondition(int min) {
      this.min = min;
   }

   public PressedTimeCondition(int minTicks, int maxTicks) {
      this.min = minTicks;
      this.max = maxTicks;
   }

   public PressedTimeCondition() {
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      if (compoundTag.m_128441_("min") && compoundTag.m_128441_("max")) {
         this.min = compoundTag.m_128451_("min");
         this.max = compoundTag.m_128451_("max");
         return this;
      } else {
         throw new IllegalArgumentException("custom condition error: 'min' or 'max' not specified!");
      }
   }

   public int getMax() {
      return this.max;
   }

   public int getMin() {
      return this.min;
   }

   public CompoundTag serializePredicate() {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.m_128405_("min", this.min);
      compoundTag.m_128405_("max", this.max);
      return compoundTag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      throw new IllegalCallerException();
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
