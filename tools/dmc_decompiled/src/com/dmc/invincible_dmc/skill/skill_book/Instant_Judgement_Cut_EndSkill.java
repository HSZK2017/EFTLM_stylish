package com.dmc.invincible_dmc.skill.skill_book;

import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.gameassets.DMCSkillSlots;
import net.minecraft.network.FriendlyByteBuf;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class Instant_Judgement_Cut_EndSkill extends Skill {
   public Instant_Judgement_Cut_EndSkill(Instant_Judgement_Cut_EndSkill.Builder builder) {
      super(builder);
   }

   public static Instant_Judgement_Cut_EndSkill.Builder createSkill() {
      return (Instant_Judgement_Cut_EndSkill.Builder)new Instant_Judgement_Cut_EndSkill.Builder()
         .setCategory(DMCSkillSlots.INSTANT_JUDGEMENT_CUT_END)
         .setActivateType(ActivateType.DURATION)
         .setResource(Resource.NONE);
   }

   public static boolean isLearned(PlayerPatch<?> playerPatch) {
      if (playerPatch == null) {
         return false;
      } else {
         SkillContainer container = playerPatch.getSkill(DMCSkillSlots.SKILL_BOOK);
         return container != null && container.getSkill() == DMCSkills.INSTANT_JUDGEMENT_CUT_END;
      }
   }

   public static boolean isEnabled(ServerPlayerPatch playerPatch) {
      return isLearned(playerPatch) && DMCPlayerCapabilityProvider.get(playerPatch).isInstantJudgementCutEndEnabled();
   }

   public boolean canExecute(SkillContainer container) {
      return false;
   }

   public boolean isExecutableState(PlayerPatch<?> executor) {
      return false;
   }

   public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
   }

   public static class Builder extends SkillBuilder<Instant_Judgement_Cut_EndSkill> {
   }
}
