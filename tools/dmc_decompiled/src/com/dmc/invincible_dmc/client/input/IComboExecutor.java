package com.dmc.invincible_dmc.client.input;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.api.skill.IHitExtendNode;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.client.input.crazyCombo.ClientCrazyComboController;
import com.dmc.invincible_dmc.client.input.judegementCut.ClientJudgementCutController;
import com.dmc.invincible_dmc.client.input.summonedSword.SummonedSwordInputController;
import java.util.Deque;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

public interface IComboExecutor {
   LocalPlayerPatch getExecutorPatch();

   @Nullable
   ComboNode getCurrentNode();

   @Nullable
   ComboNode getComboRoot();

   @Nullable
   ComboNode getCurrentLogicNode();

   @Nullable
   ComboNode getCurrentDataNode();

   void setCurrentDataNode(@Nullable ComboNode var1);

   @NotNull
   DMCPlayer getInvinciblePlayer();

   int getComboKeyIndex();

   boolean isJumpCancelExecutable();

   ClientJudgementCutController getJudgementCutController();

   ClientCrazyComboController getCrazyComboController();

   SummonedSwordInputController getSummonedSwordController();

   DirectionTracker getDirectionTracker();

   void dispatchIntent(ComboIntentResolver.ComboInputIntent var1);

   void tickReserve();

   void tickJumpCancel(long var1);

   default void tickHitExtend() {
      DMCPlayer ip = this.getInvinciblePlayer();
      IHitExtendNode activeHE = ip.getActiveHitExtendNode();
      if (activeHE != null) {
         int keyIndex = ip.getComboKeyIndex();
         if (keyIndex >= 0 && keyIndex < 5) {
            if (!PlayerInputState.isLocalDown(9 + keyIndex)) {
               ip.setActiveHitExtendNode(null);
            }
         }
      }
   }

   boolean isReserved(ComboType var1);

   void consumeJumpBuffer();

   int getJumpBufferTicks();

   void clearReserve();

   Deque<IComboExecutor.ReservedIntent> getReservedInputs();

   default void resetCrazyCombo() {
   }

   public static record ReservedIntent(ComboIntentResolver.ComboInputIntent intent, int remainingTicks, int totalTicks, boolean routed) {
      public IComboExecutor.ReservedIntent tick() {
         return new IComboExecutor.ReservedIntent(this.intent, this.remainingTicks - 1, this.totalTicks, this.routed);
      }

      public IComboExecutor.ReservedIntent keepAlive() {
         return new IComboExecutor.ReservedIntent(this.intent, this.remainingTicks, this.totalTicks, this.routed);
      }

      public IComboExecutor.ReservedIntent markRouted() {
         return new IComboExecutor.ReservedIntent(this.intent, this.remainingTicks, this.totalTicks, true);
      }
   }
}
