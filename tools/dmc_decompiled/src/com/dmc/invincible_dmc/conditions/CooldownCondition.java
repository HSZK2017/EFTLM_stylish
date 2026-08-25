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

public class CooldownCondition implements Condition<ServerPlayerPatch> {
   private boolean inCooldown;

   public CooldownCondition(boolean inCooldown) {
      this.inCooldown = inCooldown;
   }

   public CooldownCondition() {
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      if (!compoundTag.m_128441_("in_cooldown")) {
         throw new IllegalArgumentException("custom cooldown condition error: in_cooldown not specified!");
      } else {
         this.inCooldown = compoundTag.m_128471_("in_cooldown");
         return this;
      }
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128379_("in_cooldown", this.inCooldown);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
      return this.inCooldown == (Integer)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.COOLDOWN.get()) > 0;
   }

   public boolean isInCooldown() {
      return this.inCooldown;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
