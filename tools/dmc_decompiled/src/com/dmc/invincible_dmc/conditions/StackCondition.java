package com.dmc.invincible_dmc.conditions;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class StackCondition implements Condition<ServerPlayerPatch> {
   private int min;
   private int max;

   public StackCondition(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public StackCondition() {
      this.min = 1;
      this.max = 1;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      if (!compoundTag.m_128441_("min") && !compoundTag.m_128441_("max")) {
         throw new IllegalArgumentException("custom player stack condition error: min or max not specified!");
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
      int stack = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getStack();
      return stack >= this.min && stack <= this.max;
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
