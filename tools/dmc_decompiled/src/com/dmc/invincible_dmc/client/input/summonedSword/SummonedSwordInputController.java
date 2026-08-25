package com.dmc.invincible_dmc.client.input.summonedSword;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.client.input.ComboInputSampler;
import com.dmc.invincible_dmc.client.input.ComboIntentResolver;
import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.client.input.IComboExecutor;
import com.dmc.invincible_dmc.client.input.PlayerInputState;
import com.dmc.invincible_dmc.client.input.judegementCut.ClientJudgementCutController;
import com.dmc.invincible_dmc.conditions.DirectionalSequenceCondition;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPSummonedSword;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;

@OnlyIn(Dist.CLIENT)
public final class SummonedSwordInputController {
   private final IComboExecutor owningExecutor;
   private long key2PressStartMs = -1L;
   private boolean key2StormBladesFired = false;
   private boolean key2SpiralBladesFired = false;
   private boolean key2BlisteringBladesFired = false;
   private boolean key2HeavyRainFired = false;

   public SummonedSwordInputController(IComboExecutor owningExecutor) {
      this.owningExecutor = owningExecutor;
   }

   public void onTick(@Nullable IComboExecutor dispatcher) {
      IComboExecutor exec = dispatcher != null ? dispatcher : this.owningExecutor;
      LocalPlayerPatch lpp = exec.getExecutorPatch();
      if (lpp == null || lpp.isStunned()) {
         this.resetHoldState();
      } else if (!isYamatoSkillActive(exec)) {
         this.resetHoldState();
      } else {
         boolean key2Held = ComboInputSampler.isPressed(ComboNode.ComboTypes.KEY_2);
         if (key2Held) {
            if (this.key2PressStartMs < 0L) {
               this.key2PressStartMs = System.currentTimeMillis();
            }

            long elapsed = System.currentTimeMillis() - this.key2PressStartMs;
            boolean backHeld = isBackHeld();
            boolean forwardHeld = isForwardHeld();
            boolean bothDir = backHeld && forwardHeld;
            boolean backFwdSeq = checkBackForward(exec);
            if (elapsed >= 200L) {
               if (backFwdSeq && !this.key2HeavyRainFired && !this.key2BlisteringBladesFired) {
                  this.key2HeavyRainFired = true;
                  this.key2BlisteringBladesFired = true;
                  this.key2StormBladesFired = true;
                  this.key2SpiralBladesFired = true;
                  DMCLog.info(DMCLog.Category.SWORD, "[SummonedSword] KEY2 hold + BACK_FORWARD seq → HEAVY_RAIN (hold standby)");
                  DMCNetwork.sendToServer(new CPSummonedSword(CPSummonedSword.SwordType.HEAVY_RAIN));
               }

               if (!this.key2StormBladesFired && backHeld && !bothDir && !backFwdSeq) {
                  this.key2StormBladesFired = true;
                  this.key2SpiralBladesFired = true;
                  this.key2BlisteringBladesFired = true;
                  DMCLog.info(DMCLog.Category.SWORD, "[SummonedSword] KEY2 hold+back → STORM_BLADES");
                  DMCNetwork.sendToServer(new CPSummonedSword(CPSummonedSword.SwordType.STORM_BLADES));
               }

               if (!this.key2BlisteringBladesFired && forwardHeld) {
                  this.key2BlisteringBladesFired = true;
                  this.key2StormBladesFired = true;
                  this.key2SpiralBladesFired = true;
                  DMCLog.info(DMCLog.Category.SWORD, "[SummonedSword] KEY2 hold+forward → BLISTERING_BLADES");
                  DMCNetwork.sendToServer(new CPSummonedSword(CPSummonedSword.SwordType.BLISTERING_BLADES));
               }
            }

            if (elapsed >= 500L && !this.key2SpiralBladesFired && !backHeld && !forwardHeld && !isAnyDirectionHeld()) {
               this.key2SpiralBladesFired = true;
               this.key2StormBladesFired = true;
               this.key2BlisteringBladesFired = true;
               DMCLog.info(DMCLog.Category.SWORD, "[SummonedSword] KEY2 hold no-dir → SPIRAL_BLADES");
               DMCNetwork.sendToServer(new CPSummonedSword(CPSummonedSword.SwordType.SPIRAL_BLADES));
            }
         } else {
            this.resetHoldState();
         }
      }
   }

   public List<ComboIntentResolver.ComboInputIntent> interceptSwordInputs(
      List<ComboIntentResolver.ComboInputIntent> intents, @Nullable IComboExecutor dispatcher
   ) {
      IComboExecutor exec = dispatcher != null ? dispatcher : this.owningExecutor;
      if (!isYamatoSkillActive(exec)) {
         return intents;
      } else {
         List<ComboIntentResolver.ComboInputIntent> intercepted = new ArrayList<>();

         for (ComboIntentResolver.ComboInputIntent intent : intents) {
            CPSummonedSword.SwordType swordType = null;
            if (intent.type() == ComboNode.ComboTypes.KEY_2) {
               if (intent.intentType() == ComboIntentResolver.ComboIntentType.SHORT_PRESS && !this.key2StormBladesFired) {
                  if ((intent.directionMask() & 1 << DirectionalSequenceCondition.Sequence.BACK_FORWARD.ordinal()) != 0) {
                     this.key2HeavyRainFired = true;
                     this.key2StormBladesFired = true;
                     this.key2SpiralBladesFired = true;
                     this.key2BlisteringBladesFired = true;
                     DMCNetwork.sendToServer(new CPSummonedSword(CPSummonedSword.SwordType.HEAVY_RAIN));
                     intercepted.add(intent);
                     DMCLog.info(DMCLog.Category.SWORD, "[SummonedSword] KEY2 + BACK_FORWARD seq → HEAVY_RAIN (spawn, hold to extend standby)");
                  } else {
                     swordType = CPSummonedSword.SwordType.NORMAL;
                  }
               }
            } else if (intent.type() == ComboNode.ComboTypes.KEY_3 && intent.intentType() == ComboIntentResolver.ComboIntentType.SHORT_PRESS) {
               swordType = CPSummonedSword.SwordType.TRICK;
               exec.getJudgementCutController().restartChargingAndResetChain(exec);
            }

            if (swordType != null) {
               DMCLog.info(DMCLog.Category.SWORD, "[SummonedSword] {} {} → {}", intent.type(), intent.intentType(), swordType);
               DMCNetwork.sendToServer(new CPSummonedSword(swordType));
               intercepted.add(intent);
            }
         }

         for (ComboIntentResolver.ComboInputIntent intent : intents) {
            if (!intent.type().getSubTypes().isEmpty()
               && intent.type().getSubTypes().contains(ComboNode.ComboTypes.KEY_3)
               && intent.intentType() == ComboIntentResolver.ComboIntentType.SHORT_PRESS) {
               ClientJudgementCutController jcCtrl = exec.getJudgementCutController();
               if (jcCtrl != null && jcCtrl.isCharging()) {
                  DMCLog.info(DMCLog.Category.SWORD, "[SummonedSword] KEY_3 via composite {} (JC charging) → TRICK + restart", intent.type());
                  DMCNetwork.sendToServer(new CPSummonedSword(CPSummonedSword.SwordType.TRICK));
                  jcCtrl.restartChargingAndResetChain(exec);
               }
            }
         }

         intents.removeAll(intercepted);
         return intents;
      }
   }

   private void resetHoldState() {
      if (this.key2BlisteringBladesFired) {
         DMCNetwork.sendToServer(new CPSummonedSword(CPSummonedSword.SwordType.TRIGGER_BLISTERING_BLADES));
      }

      if (this.key2HeavyRainFired) {
         DMCNetwork.sendToServer(new CPSummonedSword(CPSummonedSword.SwordType.TRIGGER_HEAVY_RAIN));
      }

      if (this.key2StormBladesFired) {
         DMCNetwork.sendToServer(new CPSummonedSword(CPSummonedSword.SwordType.TRIGGER_STORM_BLADES));
      }

      if (this.key2SpiralBladesFired) {
         DMCNetwork.sendToServer(new CPSummonedSword(CPSummonedSword.SwordType.TRIGGER_SPIRAL_BLADES));
      }

      this.key2PressStartMs = -1L;
      this.key2StormBladesFired = false;
      this.key2SpiralBladesFired = false;
      this.key2BlisteringBladesFired = false;
      this.key2HeavyRainFired = false;
   }

   private static boolean isForwardHeld() {
      return PlayerInputState.isLocalDown(0);
   }

   private static boolean isBackHeld() {
      return PlayerInputState.isLocalDown(1);
   }

   private static boolean checkBackForward(IComboExecutor exec) {
      int mask = exec.getDirectionTracker()
         .getMatchedSequencesMask(
            (long)((Integer)DMConfig.DIRECTION_SEQUENCE_MATCH_WINDOW.get()).intValue(),
            (long)((Integer)DMConfig.DIRECTION_SEQUENCE_ACTIVATION_WINDOW.get()).intValue(),
            DMComboEngine.engineTick
         );
      return (mask & 1 << DirectionalSequenceCondition.Sequence.BACK_FORWARD.ordinal()) != 0;
   }

   public static void consumeDirectionOnHeavyRainSuccess() {
      IComboExecutor dispatcher = DMComboEngine.getLocalPlayerDispatcher();
      if (dispatcher != null) {
         dispatcher.getDirectionTracker().consume(DMComboEngine.engineTick);
         DMCLog.info(DMCLog.Category.SWORD, "[SummonedSword] Direction sequence consumed after HEAVY_RAIN confirmed by server");
      }
   }

   private static boolean isAnyDirectionHeld() {
      return PlayerInputState.isLocalDown(0) || PlayerInputState.isLocalDown(1) || PlayerInputState.isLocalDown(2) || PlayerInputState.isLocalDown(3);
   }

   public static boolean isYamatoSkillActive(IComboExecutor exec) {
      LocalPlayerPatch lpp = exec.getExecutorPatch();
      if (lpp == null) {
         return false;
      } else {
         SkillContainer container = lpp.getSkill(SkillSlots.WEAPON_INNATE);
         return !container.isEmpty() && container.getSkill() instanceof VergilSkill;
      }
   }
}
