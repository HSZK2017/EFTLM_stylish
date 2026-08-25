package com.dmc.invincible_dmc.conditions;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.fml.ModList;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class TargetBlockingCondition implements Condition<ServerPlayerPatch> {
   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      return this;
   }

   public CompoundTag serializePredicate() {
      return new CompoundTag();
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      if (!(serverPlayerPatch.getTarget() instanceof ServerPlayer serverPlayer)) {
         return ModList.get().isLoaded("indestructible") && serverPlayerPatch.getTarget() != null ? false : false;
      } else {
         SkillContainer guardSkill = ((ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(serverPlayer, ServerPlayerPatch.class))
            .getSkill(SkillSlots.GUARD);
         CapabilityItem itemCapability = serverPlayerPatch.getHoldingItemCapability(serverPlayer.m_7655_());
         return itemCapability.getUseAnimation(serverPlayerPatch) == UseAnim.BLOCK
            && serverPlayer.m_6117_()
            && guardSkill.getSkill() != null
            && guardSkill.getSkill().isExecutableState(serverPlayerPatch);
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
