package com.dmc.invincible_dmc.conditions;

import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class YamatoDodgeCounterSuccessCondition implements Condition<ServerPlayerPatch> {
   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      return this;
   }

   public CompoundTag serializePredicate() {
      return new CompoundTag();
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
      return manager.hasData((SkillDataKey)DMCSkillDataKeys.DODGE_COUNTER_SUCCESS_TIMER.get())
         ? (Integer)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.DODGE_COUNTER_SUCCESS_TIMER.get()) > 0
         : false;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
