package com.dmc.invincible_dmc.conditions;

import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class SDTConsumeCondition implements Condition<ServerPlayerPatch> {
   private float normalMin = 0.0F;
   private float sdtMin = 0.0F;

   public SDTConsumeCondition(float normalMin, float sdtMin) {
      this.normalMin = normalMin;
      this.sdtMin = sdtMin;
   }

   public SDTConsumeCondition() {
   }

   public Condition<ServerPlayerPatch> read(CompoundTag tag) {
      if (tag.m_128441_("normalMin")) {
         this.normalMin = tag.m_128457_("normalMin");
      }

      if (tag.m_128441_("sdtMin")) {
         this.sdtMin = tag.m_128457_("sdtMin");
      }

      return this;
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128350_("normalMin", this.normalMin);
      tag.m_128350_("sdtMin", this.sdtMin);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch spp) {
      SkillContainer container = spp.getSkill(SkillSlots.WEAPON_INNATE);
      if (container == null || container.isEmpty()) {
         return false;
      } else if (!(container.getSkill() instanceof VergilSkill)) {
         return false;
      } else if (((ServerPlayer)spp.getOriginal()).m_7500_()) {
         return true;
      } else {
         float current = SinDevilTriggerManager.getSDTValue(container);
         return SinDevilTriggerManager.isSDT(container) ? current > 0.0F : current >= this.normalMin;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
