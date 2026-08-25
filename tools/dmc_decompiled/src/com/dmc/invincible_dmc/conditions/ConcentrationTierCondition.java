package com.dmc.invincible_dmc.conditions;

import com.dmc.invincible_dmc.skill.weapon_innate.vergil.ConcentrationManager;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class ConcentrationTierCondition implements Condition<ServerPlayerPatch> {
   private int min;
   private int max;

   public ConcentrationTierCondition(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public ConcentrationTierCondition() {
      this.min = 0;
      this.max = 2;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      if (compoundTag.m_128441_("min") && compoundTag.m_128441_("max")) {
         this.min = compoundTag.m_128451_("min");
         this.max = compoundTag.m_128451_("max");
         return this;
      } else {
         throw new IllegalArgumentException("concentration_tier condition error: min and max must be specified!");
      }
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128405_("min", this.min);
      tag.m_128405_("max", this.max);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      int tier = ConcentrationManager.getConcentrationTier(serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE));
      return tier >= this.min && tier <= this.max;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
