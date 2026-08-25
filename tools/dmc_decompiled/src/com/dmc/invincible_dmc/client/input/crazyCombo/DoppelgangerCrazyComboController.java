package com.dmc.invincible_dmc.client.input.crazyCombo;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboNodeManager;
import com.dmc.invincible_dmc.api.skill.ICrazyComboNode;
import com.dmc.invincible_dmc.api.skill.crazycombo.CrazyComboPhase;
import com.dmc.invincible_dmc.api.skill.crazycombo.CrazyComboSession;
import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerCapability;
import com.dmc.invincible_dmc.client.input.ComboInputSampler;
import com.dmc.invincible_dmc.client.input.ComboIntentResolver;
import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.client.input.IComboExecutor;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPPlayCC;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public final class DoppelgangerCrazyComboController {
   private static final int HOLD_FINISH_TICKS = 4;
   private final int doppelId;
   private final ComboNode root;
   private final CrazyComboSession session = new CrazyComboSession();
   private int tickCounter;

   public DoppelgangerCrazyComboController(int doppelId, ComboNode root) {
      this.doppelId = doppelId;
      this.root = root;
   }

   public void onTick(int key1Clicks) {
      this.tickCounter++;
      LocalPlayer player = Minecraft.m_91087_().f_91074_;
      if (player != null && !ComboInputSampler.BypassInput()) {
         DoppelgangerEntity doppel = this.findDoppel();
         if (doppel != null && doppel.m_19879_() == this.doppelId && doppel.m_6084_() && doppel.isCcMode()) {
            LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(doppel, LivingEntityPatch.class);
            if (patch != null) {
               int nodeId = doppel.getCcNodeId();
               ComboNode activeNode = ComboNodeManager.get(nodeId);
               if (activeNode == null) {
                  activeNode = VergilSkill.findNodeById(this.root, nodeId);
               }

               if (!(activeNode instanceof ICrazyComboNode ccNode)) {
                  this.reset();
               } else {
                  DynamicAnimation currentAnimation = CrazyComboAnimationHelper.getCurrentAnimation(patch);
                  if (currentAnimation != null) {
                     if (!this.session.isBoundTo(nodeId)) {
                        if (!CrazyComboAnimationHelper.isBaseAnimation(activeNode, currentAnimation)) {
                           this.reset();
                           return;
                        }

                        this.session.begin(nodeId, 0, 0L, ccNode.getCrazyComboPolicy());
                        DMCLog.info(
                           DMCLog.Category.DOPPEL_CC,
                           "[DoppelCCClient] SESSION_BEGIN doppel={} node={} animation={}",
                           this.doppelId,
                           nodeId,
                           currentAnimation.getRegistryName()
                        );
                     }

                     if (this.session.stage() == CrazyComboSession.Stage.FINISH) {
                        if (CrazyComboAnimationHelper.isFinishAnimation(activeNode, currentAnimation)) {
                           this.reset();
                        } else if (this.session.shouldSendFinishRequest((long)this.tickCounter)) {
                           this.sendFinishRequest();
                        }
                     } else if (this.session.stage() == CrazyComboSession.Stage.CHASE
                        && !this.session.waitingForChaseAnimation()
                        && currentAnimation.getRegistryName() != null
                        && !ICrazyComboNode.containsAnimation(ccNode, currentAnimation.getRegistryName())) {
                        this.reset();
                     } else {
                        StaticAnimation chaseAnimation = CrazyComboAnimationHelper.getChaseAnimation(activeNode);
                        boolean playingChaseAnimation = chaseAnimation != null && CrazyComboAnimationHelper.isPlayingAnimation(patch, chaseAnimation);
                        float chaseElapsedTime = DMCAnimationUtils.getElapsedTime(patch);
                        if (this.session.canConfirmChaseAnimation(playingChaseAnimation, chaseElapsedTime)) {
                           this.session.markChaseAnimationStarted((long)this.tickCounter, CrazyComboAnimationHelper.getCurrentPhaseOrder(patch));
                           DMCLog.info(
                              DMCLog.Category.DOPPEL_CC,
                              "[DoppelCCClient] CHASE_RESTART_CONFIRMED doppel={} node={} loop={} elapsed={}",
                              this.doppelId,
                              activeNode.getId(),
                              this.session.loopCount(),
                              chaseElapsedTime
                           );
                        }

                        float progress = CrazyComboAnimationHelper.getAnimationProgress(patch);
                        boolean canBasicAttack = patch.getEntityState().canBasicAttack();
                        boolean inWindow = progress >= ccNode.getCcWindowStart();
                        int phaseOrder = CrazyComboAnimationHelper.getCurrentPhaseOrder(patch);
                        int phaseCount = CrazyComboAnimationHelper.getCurrentPhaseCount(patch);
                        boolean finishGate = CrazyComboAnimationHelper.isFinishPhaseGateOpen(patch, activeNode);
                        boolean inputDown = ComboInputSampler.isPressed(ComboNode.ComboTypes.KEY_1);
                        boolean holdFinish = this.session.stage() == CrazyComboSession.Stage.CHASE
                           && inputDown
                           && ComboInputSampler.getPressDuration(ComboNode.ComboTypes.KEY_1, false) >= 4;
                        CrazyComboSession.Decision decision = this.session
                           .advance(
                              new CrazyComboSession.TickInput(
                                 (long)this.tickCounter,
                                 key1Clicks,
                                 inWindow,
                                 phaseOrder,
                                 phaseCount,
                                 canBasicAttack,
                                 finishGate,
                                 holdFinish,
                                 ccNode.getCcFinishNoChase() != null,
                                 ccNode.getCcMaxChases(patch),
                                 ccNode.getCcStartupFinishNoChasePhase(),
                                 inputDown
                              )
                           );
                        switch (decision) {
                           case CHASE:
                              this.executeChase(activeNode, patch);
                              break;
                           case FINISH:
                              this.executeFinish(activeNode, false);
                              break;
                           case FINISH_NO_CHASE:
                              this.executeFinish(activeNode, true);
                              break;
                           case RELEASE_TO_NORMAL_COMBO:
                              this.reset();
                              break;
                           case CANCEL:
                              this.reset();
                           case NONE:
                        }
                     }
                  }
               }
            }
         } else {
            this.reset();
         }
      } else {
         this.reset();
      }
   }

   private void executeChase(ComboNode activeNode, LivingEntityPatch<?> patch) {
      StaticAnimation chaseAnimation = CrazyComboAnimationHelper.getChaseAnimation(activeNode);
      if (chaseAnimation == null) {
         this.reset();
      } else {
         boolean restartRequired = CrazyComboAnimationHelper.isPlayingAnimation(patch, chaseAnimation);
         this.session.commitChase(restartRequired, DMCAnimationUtils.getElapsedTime(patch));
         DMCNetwork.sendToServer(new CPPlayCC(CPPlayCC.Type.CHASE, this.session.loopCount(), CPPlayCC.CCPlayTarget.DOPPEL));
         CrazyComboAudioHelper.playChaseTrigger(Minecraft.m_91087_().f_91074_);
      }
   }

   private void executeFinish(ComboNode activeNode, boolean noChase) {
      ICrazyComboNode ccNode = (ICrazyComboNode)activeNode;
      ComboNode finishNode = noChase ? ccNode.getCcFinishNoChase() : (ccNode.getCcFinish() != null ? ccNode.getCcFinish() : ccNode.getCcFinishNoChase());
      if (finishNode != null) {
         this.session.beginFinish();
         this.sendFinishRequest();
         CrazyComboAudioHelper.playFinishTrigger(Minecraft.m_91087_().f_91074_);
      } else {
         this.reset();
      }
   }

   private void sendFinishRequest() {
      IComboExecutor executor = DMComboEngine.getLocalPlayerDispatcher();
      ComboIntentResolver.ComboInputIntent snapshot = executor != null
         ? ComboIntentResolver.createSnapshotIntent(
            ComboNode.ComboTypes.KEY_1, ComboIntentResolver.ComboIntentType.SHORT_PRESS, 1, 0L, DMComboEngine.engineTick, executor.getDirectionTracker()
         )
         : new ComboIntentResolver.ComboInputIntent(
            ComboNode.ComboTypes.KEY_1, ComboIntentResolver.ComboIntentType.SHORT_PRESS, 1, 0L, 0, List.of(), DMComboEngine.engineTick
         );
      DMCNetwork.sendToServer(
         new CPPlayCC(
            CPPlayCC.Type.FINISH,
            this.session.loopCount(),
            CPPlayCC.CCPlayTarget.DOPPEL,
            0L,
            this.session.requiredFinishPhaseOrder(),
            snapshot.directionMask(),
            snapshot.directionEvents(),
            snapshot.captureTick()
         )
      );
      this.session.markFinishRequestSent((long)this.tickCounter);
      DMCLog.info(
         DMCLog.Category.DOPPEL_CC,
         "[DoppelCCClient] FINISH_REQUEST doppel={} node={} loop={} requiredPhase={} tick={}",
         this.doppelId,
         this.session.sourceNodeId(),
         this.session.loopCount(),
         this.session.requiredFinishPhaseOrder(),
         this.tickCounter
      );
   }

   private DoppelgangerEntity findDoppel() {
      LocalPlayer player = Minecraft.m_91087_().f_91074_;
      return player == null ? null : DoppelgangerCapability.getCachedDoppel(player);
   }

   public void reset() {
      this.session.reset();
   }

   public CrazyComboSession.Stage getCurrentState() {
      return this.session.stage();
   }

   public CrazyComboPhase getCurrentPhase() {
      return this.session.phase();
   }

   public int getPressCount() {
      return this.session.pressCount();
   }

   public int getLoopCount() {
      return this.session.loopCount();
   }
}
