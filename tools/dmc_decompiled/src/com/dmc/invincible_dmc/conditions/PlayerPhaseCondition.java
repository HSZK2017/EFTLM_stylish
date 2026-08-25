package com.dmc.invincible_dmc.conditions;

import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class PlayerPhaseCondition implements Condition<ServerPlayerPatch> {
   private int min;
   private int max;

   public PlayerPhaseCondition(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public PlayerPhaseCondition() {
      this.min = 1;
      this.max = 1;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      if (!compoundTag.m_128441_("min") && !compoundTag.m_128441_("max")) {
         throw new IllegalArgumentException("custom player phase condition error: min or max not specified!");
      } else {
         this.min = compoundTag.m_128451_("min");
         this.max = compoundTag.m_128451_("max");
         return this;
      }
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128405_("min", this.min);
      tag.m_128405_("max", this.max);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      int phase = DMCPlayerCapabilityProvider.get((Player)serverPlayerPatch.getOriginal()).getPhase();
      return phase >= this.min && phase <= this.max;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }
}
