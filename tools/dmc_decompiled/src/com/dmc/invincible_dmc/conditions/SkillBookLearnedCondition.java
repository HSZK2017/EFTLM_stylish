package com.dmc.invincible_dmc.conditions;

import com.dmc.invincible_dmc.gameassets.DMCSkillSlots;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class SkillBookLearnedCondition implements Condition<ServerPlayerPatch> {
   private String skillRegistryName;

   public SkillBookLearnedCondition(String skillRegistryName) {
      this.skillRegistryName = skillRegistryName;
   }

   public SkillBookLearnedCondition() {
      this.skillRegistryName = "";
   }

   public Condition<ServerPlayerPatch> read(CompoundTag tag) {
      if (tag.m_128441_("skill")) {
         this.skillRegistryName = tag.m_128461_("skill");
      }

      return this;
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128359_("skill", this.skillRegistryName);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      if (this.skillRegistryName.isEmpty()) {
         return false;
      } else {
         Skill skill = SkillManager.getSkill(this.skillRegistryName);
         if (skill == null) {
            return false;
         } else {
            SkillContainer container = serverPlayerPatch.getSkill(DMCSkillSlots.SKILL_BOOK);
            return container.getSkill() == skill;
         }
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
