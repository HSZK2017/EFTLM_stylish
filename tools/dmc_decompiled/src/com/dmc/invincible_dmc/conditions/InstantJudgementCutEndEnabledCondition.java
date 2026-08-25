package com.dmc.invincible_dmc.conditions;

import com.dmc.invincible_dmc.skill.skill_book.Instant_Judgement_Cut_EndSkill;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class InstantJudgementCutEndEnabledCondition implements Condition<ServerPlayerPatch> {
   public Condition<ServerPlayerPatch> read(CompoundTag tag) {
      return this;
   }

   public CompoundTag serializePredicate() {
      return new CompoundTag();
   }

   public boolean predicate(ServerPlayerPatch playerPatch) {
      return Instant_Judgement_Cut_EndSkill.isEnabled(playerPatch);
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
