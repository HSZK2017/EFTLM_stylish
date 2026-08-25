package com.dmc.invincible_dmc.conditions;

import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class SDTCondition implements Condition<ServerPlayerPatch> {
   private boolean wanted = true;

   public SDTCondition(boolean wanted) {
      this.wanted = wanted;
   }

   public SDTCondition() {
      this.wanted = true;
   }

   public boolean isWanted() {
      return this.wanted;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      if (compoundTag.m_128441_("wanted")) {
         this.wanted = compoundTag.m_128471_("wanted");
      }

      return this;
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128379_("wanted", this.wanted);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      SkillContainer container = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (container == null || container.isEmpty()) {
         return !this.wanted;
      } else {
         return !(container.getSkill() instanceof VergilSkill) ? !this.wanted : SinDevilTriggerManager.isSDT(container) == this.wanted;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
