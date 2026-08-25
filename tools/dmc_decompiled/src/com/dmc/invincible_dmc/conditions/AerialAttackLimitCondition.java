package com.dmc.invincible_dmc.conditions;

import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class AerialAttackLimitCondition implements Condition<ServerPlayerPatch> {
   private int maxCount;

   public int getMaxCount() {
      return this.maxCount;
   }

   public AerialAttackLimitCondition() {
      this.maxCount = 1;
   }

   public AerialAttackLimitCondition(int maxCount) {
      this.maxCount = maxCount;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      if (!compoundTag.m_128441_("maxCount")) {
         throw new IllegalArgumentException("aerial_attack_limit condition error: maxCount not specified!");
      } else {
         this.maxCount = compoundTag.m_128451_("maxCount");
         return this;
      }
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128405_("maxCount", this.maxCount);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
      SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
      if (!player.m_20096_() && !player.m_7500_()) {
         int count = manager.hasData((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get())
            ? (Integer)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get())
            : 0;
         if (count < this.maxCount) {
            DMCPlayerCapabilityProvider.get(player).setAerialAttackPending();
            return true;
         } else {
            return false;
         }
      } else {
         manager.setData((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get(), 0);
         return true;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
