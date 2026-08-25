package com.dmc.invincible_dmc.api.skill;

import com.dmc.invincible_dmc.api.skill.crazycombo.CrazyComboPolicy;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public interface ICrazyComboNode {
   CrazyComboPolicy getCrazyComboPolicy();

   @Nullable
   SubComboNode getCcBase();

   @Nullable
   SubComboNode getCcChase();

   @Nullable
   ComboNode getCcFinish();

   @Nullable
   ComboNode getCcFinishNoChase();

   int getCcMaxChases();

   default int getCcMaxChases(LivingEntityPatch<?> entitypatch) {
      return this.getCcMaxChases();
   }

   default int getCcBaseRequiredPresses() {
      return this.getCrazyComboPolicy().baseRequiredPresses();
   }

   default int getCcChaseRequiredPresses() {
      return this.getCrazyComboPolicy().chaseRequiredPresses();
   }

   default int getCcRapidMaxIntervalTicks() {
      return this.getCrazyComboPolicy().rapidMaxIntervalTicks();
   }

   default float getCcWindowStart() {
      return this.getCrazyComboPolicy().inputWindowStart();
   }

   default int getCcFinishMinPhase() {
      return this.getCrazyComboPolicy().finishMinPhase();
   }

   default int getCcStartupFinishNoChasePhase() {
      return this.getCrazyComboPolicy().startupFinishNoChasePhase();
   }

   default boolean isCcResetCombo() {
      return this.getCrazyComboPolicy().resetCombo();
   }

   default boolean isCcHoldFinishFollowupEnabled() {
      return false;
   }

   static boolean containsAnimation(ICrazyComboNode node, ResourceLocation animation) {
      return matches(node.getCcBase(), animation)
         || matches(node.getCcChase(), animation)
         || matches(node.getCcFinish(), animation)
         || matches(node.getCcFinishNoChase(), animation);
   }

   static boolean matches(@Nullable ComboNode node, ResourceLocation animation) {
      if (node == null) {
         return false;
      } else if (node.getAnimationAccessor() != null && Objects.equals(node.getAnimationAccessor().registryName(), animation)) {
         return true;
      } else {
         if (node instanceof ITapHoldNode tapHoldNode && (matches(tapHoldNode.getTap(), animation) || matches(tapHoldNode.getHold(), animation))) {
            return true;
         }

         if (node instanceof IHitExtendNode hitExtendNode && (matches(hitExtendNode.getBase(), animation) || matches(hitExtendNode.getExtend(), animation))) {
            return true;
         }

         for (ComboNode child : node.getConditionNodes()) {
            if (matches(child, animation)) {
               return true;
            }
         }

         return false;
      }
   }
}
