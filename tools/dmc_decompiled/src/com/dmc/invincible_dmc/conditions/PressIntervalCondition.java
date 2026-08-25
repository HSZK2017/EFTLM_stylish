package com.dmc.invincible_dmc.conditions;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class PressIntervalCondition implements Condition<ServerPlayerPatch> {
   private long min;
   private long max = Long.MAX_VALUE;

   public PressIntervalCondition(long min) {
      this.min = min;
   }

   public PressIntervalCondition(long minTicks, long maxTicks) {
      this.min = minTicks;
      this.max = maxTicks;
   }

   public PressIntervalCondition() {
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      if (compoundTag.m_128441_("min") && compoundTag.m_128441_("max")) {
         this.min = compoundTag.m_128454_("min");
         this.max = compoundTag.m_128454_("max");
         return this;
      } else {
         throw new IllegalArgumentException("custom condition error: 'min' or 'max' not specified!");
      }
   }

   public long getMax() {
      return this.max;
   }

   public long getMin() {
      return this.min;
   }

   public CompoundTag serializePredicate() {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.m_128356_("min", this.min);
      compoundTag.m_128356_("max", this.max);
      return compoundTag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      throw new IllegalCallerException();
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
