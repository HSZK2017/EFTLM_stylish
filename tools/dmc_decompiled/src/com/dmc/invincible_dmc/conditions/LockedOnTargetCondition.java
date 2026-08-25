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

public class LockedOnTargetCondition implements Condition<ServerPlayerPatch> {
   public Condition<ServerPlayerPatch> read(CompoundTag tag) {
      return this;
   }

   public CompoundTag serializePredicate() {
      return new CompoundTag();
   }

   public boolean predicate(ServerPlayerPatch playerPatch) {
      SkillDataManager sdm = playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
      if (sdm != null) {
         return sdm.hasData((SkillDataKey)DMCSkillDataKeys.CAMERA_LOCKING_ON.get())
            ? (Boolean)sdm.getDataValue((SkillDataKey)DMCSkillDataKeys.CAMERA_LOCKING_ON.get())
            : false;
      } else {
         return false;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
