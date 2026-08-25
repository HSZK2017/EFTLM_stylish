package com.dmc.invincible_dmc.client.input.crazyCombo;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboNodeManager;
import com.dmc.invincible_dmc.api.skill.ICrazyComboNode;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionSession;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionStage;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionType;
import com.dmc.invincible_dmc.client.input.IComboExecutor;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.types.DynamicAnimation;

final class CrazyComboNodeResolver {
   private CrazyComboNodeResolver() {
   }

   static boolean resolvePlayer(
      IComboExecutor executor, DynamicAnimation currentAnimation, int localSourceNodeId, CrazyComboNodeResolver.ResolvedContext result
   ) {
      WeaponActionSession authority = executor.getInvinciblePlayer().getActionSession();
      if (authority != null && authority.actionType() == WeaponActionType.CRAZY_COMBO && !authority.stage().isTerminal()) {
         ComboNode node = resolveMatchingNode(authority.sourceNodeId(), currentAnimation);
         if (node != null) {
            result.setFromAuthority(node, authority);
            return true;
         }
      }

      ComboNode localNode = resolveMatchingNode(localSourceNodeId, currentAnimation);
      if (localNode != null) {
         result.set(localNode, executor.getComboKeyIndex(), authority);
         return true;
      } else {
         ComboNode compatibilityNode = executor.getInvinciblePlayer().getActiveCrazyComboNode();
         if (matchesCurrentAnimation(compatibilityNode, currentAnimation)) {
            result.set(compatibilityNode, executor.getComboKeyIndex(), authority);
            return true;
         } else {
            ComboNode logicalNode = executor.getCurrentNode();
            if (matchesCurrentAnimation(logicalNode, currentAnimation)) {
               result.set(logicalNode, executor.getComboKeyIndex(), authority);
               return true;
            } else {
               result.clear();
               return false;
            }
         }
      }
   }

   @Nullable
   static ComboNode resolveMatchingNode(int nodeId, DynamicAnimation currentAnimation) {
      if (nodeId < 0) {
         return null;
      } else {
         ComboNode node = ComboNodeManager.get(nodeId);
         return matchesCurrentAnimation(node, currentAnimation) ? node : null;
      }
   }

   static boolean matchesCurrentAnimation(@Nullable ComboNode node, DynamicAnimation currentAnimation) {
      if (node instanceof ICrazyComboNode ccNode) {
         return CrazyComboAnimationHelper.isBaseAnimation(node, currentAnimation)
            ? true
            : currentAnimation.getRegistryName() != null && ICrazyComboNode.containsAnimation(ccNode, currentAnimation.getRegistryName());
      } else {
         return false;
      }
   }

   static final class ResolvedContext {
      @Nullable
      private ComboNode node;
      private int inputKeyIndex = -1;
      private long authoritySessionId;
      @Nullable
      private WeaponActionStage authorityStage;
      private int authorityActionStep;

      void set(ComboNode node, int fallbackInputKeyIndex, @Nullable WeaponActionSession authority) {
         if (authority != null
            && authority.actionType() == WeaponActionType.CRAZY_COMBO
            && !authority.stage().isTerminal()
            && authority.sourceNodeId() == node.getId()) {
            this.setFromAuthority(node, authority);
         } else {
            this.node = node;
            this.inputKeyIndex = fallbackInputKeyIndex;
            this.authoritySessionId = 0L;
            this.authorityStage = null;
            this.authorityActionStep = 0;
         }
      }

      void setFromAuthority(ComboNode node, WeaponActionSession authority) {
         this.node = node;
         this.inputKeyIndex = authority.inputKeyIndex();
         this.authoritySessionId = authority.sessionId();
         this.authorityStage = authority.stage();
         this.authorityActionStep = authority.actionStep();
      }

      void clear() {
         this.node = null;
         this.inputKeyIndex = -1;
         this.authoritySessionId = 0L;
         this.authorityStage = null;
         this.authorityActionStep = 0;
      }

      @Nullable
      ComboNode node() {
         return this.node;
      }

      int inputKeyIndex() {
         return this.inputKeyIndex;
      }

      long authoritySessionId() {
         return this.authoritySessionId;
      }

      @Nullable
      WeaponActionStage authorityStage() {
         return this.authorityStage;
      }

      int authorityActionStep() {
         return this.authorityActionStep;
      }
   }
}
