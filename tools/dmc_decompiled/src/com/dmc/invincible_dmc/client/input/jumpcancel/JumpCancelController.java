package com.dmc.invincible_dmc.client.input.jumpcancel;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.mixin.LivingEntityAccessor;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.input.InputManager;
import yesman.epicfight.api.client.input.PlayerInputState;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;

@OnlyIn(Dist.CLIENT)
public final class JumpCancelController {
   public boolean tryExecute(Input input, LocalPlayer player, LocalPlayerPatch playerPatch, int tickSinceLastJump) {
      SkillContainer skillContainer = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      SkillDataManager dataManager = skillContainer.getDataManager();
      if (dataManager.hasData((SkillDataKey)DMCSkillDataKeys.IS_ON_GROUND.get())
         && (Boolean)dataManager.getDataValue((SkillDataKey)DMCSkillDataKeys.IS_ON_GROUND.get())
         && playerPatch.isEpicFightMode()) {
         if (tickSinceLastJump <= 0
            && (playerPatch.getEntityState().inaction() || isInJumpCancelWindow(playerPatch))
            && DMComboEngine.getJumpBufferTicks() > 0
            && DMComboEngine.isJumpCancelExecutable()) {
            executeGroundJump(input, player, playerPatch);
            DMComboEngine.resetCrazyComboForPlayer();
            ComboBasicAttack comboSkill = getComboSkill(playerPatch);
            if (comboSkill != null) {
               comboSkill.resetComboFromClient();
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static void executeGroundJump(Input input, LocalPlayer player, LocalPlayerPatch playerPatch) {
      ((LivingEntityAccessor)player).setNoJumpDelay(0);
      PlayerInputState inputState = InputManager.getInputState(input).withJumping(true);
      InputManager.setInputState(inputState);
      player.m_6862_(false);
      player.m_6135_();
      playerPatch.playAnimationInClientSide(playerPatch.getClientAnimator().getJumpAnimation(), 0.0F);
   }

   @Nullable
   public static ComboBasicAttack getComboSkill(LocalPlayerPatch playerPatch) {
      Skill var2 = playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getSkill();
      return var2 instanceof ComboBasicAttack ? (ComboBasicAttack)var2 : null;
   }

   public static boolean isInJumpCancelWindow(LocalPlayerPatch playerPatch) {
      AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(playerPatch);
      if (animationPlayer != null && !animationPlayer.isEmpty()) {
         DynamicAnimation animation = DMCAnimationUtils.getCurrentAnimation(animationPlayer);
         if (animation == null) {
            return false;
         } else {
            StaticAnimation realAnimation = DMCAnimationUtils.getRealAnimation(animation);
            return realAnimation == null
               ? false
               : realAnimation.getProperty(YamatoAttackAnimation.JUMP_CANCEL_TIME)
                  .map(timePairs -> timePairs.isTimeInPairs(animationPlayer.getElapsedTime()))
                  .orElse(false);
         }
      } else {
         return false;
      }
   }
}
