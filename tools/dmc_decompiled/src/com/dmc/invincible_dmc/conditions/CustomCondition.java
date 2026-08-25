package com.dmc.invincible_dmc.conditions;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class CustomCondition implements Condition<LivingEntityPatch<?>> {
   public Condition<LivingEntityPatch<?>> read(CompoundTag compoundTag) {
      return null;
   }

   public CompoundTag serializePredicate() {
      return null;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
