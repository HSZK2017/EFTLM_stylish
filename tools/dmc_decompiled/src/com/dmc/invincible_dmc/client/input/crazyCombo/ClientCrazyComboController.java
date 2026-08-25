package com.dmc.invincible_dmc.client.input.crazyCombo;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboNodeManager;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.api.skill.ICrazyComboNode;
import com.dmc.invincible_dmc.api.skill.IJudgementCutNode;
import com.dmc.invincible_dmc.api.skill.SubComboNode;
import com.dmc.invincible_dmc.api.skill.crazycombo.CrazyComboPhase;
import com.dmc.invincible_dmc.api.skill.crazycombo.CrazyComboSession;
import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionStage;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionType;
import com.dmc.invincible_dmc.client.input.ComboInputSampler;
import com.dmc.invincible_dmc.client.input.ComboIntentResolver;
import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.client.input.IComboExecutor;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPPlayCC;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.dmc.invincible_dmc.utils.vfx.CameraFovUtil;
import com.dmc.invincible_dmc.utils.vfx.CinematicBarsUtils;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public final class ClientCrazyComboController {
   private static final int HOLD_FINISH_TICKS = 4;
   private final IComboExecutor owningExecutor;
   private final CrazyComboSession session = new CrazyComboSession();
   private final CrazyComboNodeResolver.ResolvedContext resolvedContext = new CrazyComboNodeResolver.ResolvedContext();
   @Nullable
   private ComboIntentResolver.ComboInputIntent pendingFollowupIntent;
   private boolean followupCaptureEnabled;
   private boolean pendingHoldFinishFollowup;
   private boolean finishAnimationObserved;
   private boolean suppressComboDispatch;
   private int restartGuardNodeId = -1;
   @Nullable
   private ResourceLocation restartGuardAnimation;

   public ClientCrazyComboController(IComboExecutor owningExecutor) {
      this.owningExecutor = owningExecutor;
   }

   public void onTick(@Nullable IComboExecutor dispatcher) {
      this.suppressComboDispatch = false;
      LocalPlayer player = Minecraft.m_91087_().f_91074_;
      IComboExecutor executor = dispatcher != null ? dispatcher : this.owningExecutor;
      if (player != null && !ComboInputSampler.BypassInput() && !executor.getExecutorPatch().isStunned()) {
         LivingEntityPatch<?> patch = executor.getExecutorPatch();
         DynamicAnimation currentAnimation = CrazyComboAnimationHelper.getCurrentAnimation(patch);
         if (currentAnimation != null) {
            this.clearRestartGuardIfAnimationChanged(currentAnimation);
            if (this.session.stage() == CrazyComboSession.Stage.FINISH
               && this.pendingHoldFinishFollowup
               && this.finishAnimationObserved
               && !isCurrentFinishAnimation(this.session.sourceNodeId(), currentAnimation)) {
               this.releaseHeldFinishFollowup(executor, currentAnimation);
            } else {
               boolean resolved = CrazyComboNodeResolver.resolvePlayer(executor, currentAnimation, this.session.sourceNodeId(), this.resolvedContext);
               ComboNode activeNode = resolved ? this.resolvedContext.node() : null;
               if (!(activeNode instanceof ICrazyComboNode ccNode)) {
                  this.reset();
               } else {
                  if (this.isRestartGuarded(activeNode, currentAnimation)) {
                     if (this.resolvedContext.authoritySessionId() <= 0L) {
                        this.session.reset();
                        return;
                     }

                     this.clearRestartGuard("new_authority_session");
                  }

                  if (!this.session.isBoundTo(activeNode.getId())) {
                     boolean startup = CrazyComboAnimationHelper.isBaseAnimation(activeNode, currentAnimation);
                     boolean restoringChase = this.resolvedContext.authorityStage() == WeaponActionStage.LOOP;
                     if (!startup && !restoringChase) {
                        this.reset();
                        return;
                     }

                     this.session
                        .begin(
                           activeNode.getId(), this.resolvedContext.inputKeyIndex(), this.resolvedContext.authoritySessionId(), ccNode.getCrazyComboPolicy()
                        );
                     if (restoringChase) {
                        this.session.restoreChase(this.resolvedContext.authorityActionStep(), (long)player.f_19797_);
                     }

                     DMCLog.info(
                        DMCLog.Category.DOPPEL_CC,
                        "[CCClient] SESSION_BEGIN node={} stage={} authoritySession={} animation={}",
                        activeNode.getId(),
                        this.session.stage(),
                        this.session.authoritySessionId(),
                        currentAnimation.getRegistryName()
                     );
                  } else {
                     this.session.bindAuthoritySession(this.resolvedContext.authoritySessionId());
                  }

                  if (this.session.stage() == CrazyComboSession.Stage.FINISH) {
                     this.handleFinishTransition(executor, activeNode, currentAnimation);
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
                        this.session.markChaseAnimationStarted((long)player.f_19797_, CrazyComboAnimationHelper.getCurrentPhaseOrder(patch));
                        DMCLog.info(
                           DMCLog.Category.DOPPEL_CC,
                           "[CCClient] CHASE_RESTART_CONFIRMED node={} loop={} elapsed={}",
                           activeNode.getId(),
                           this.session.loopCount(),
                           chaseElapsedTime
                        );
                     }

                     ComboType comboType = ComboInputSampler.getComboTypeByIndex(this.session.inputKeyIndex());
                     if (comboType == null) {
                        this.reset();
                     } else {
                        float progress = CrazyComboAnimationHelper.getAnimationProgress(patch);
                        boolean inWindow = progress >= ccNode.getCcWindowStart();
                        int clicks = DMComboEngine.consumeClicks(comboType);
                        int phaseOrder = CrazyComboAnimationHelper.getCurrentPhaseOrder(patch);
                        int phaseCount = CrazyComboAnimationHelper.getCurrentPhaseCount(patch);
                        boolean canBasicAttack = patch.getEntityState().canBasicAttack();
                        boolean finishGate = CrazyComboAnimationHelper.isFinishPhaseGateOpen(patch, activeNode);
                        boolean inputDown = ComboInputSampler.isPressed(comboType);
                        boolean holdFinish = this.session.stage() == CrazyComboSession.Stage.CHASE
                           && inputDown
                           && ComboInputSampler.getPressDuration(comboType, false) >= 4;
                        int maxChases = ccNode.getCcMaxChases(patch);
                        this.followupCaptureEnabled = this.session.stage() == CrazyComboSession.Stage.CHASE;
                        CrazyComboSession.Decision decision = this.session
                           .advance(
                              new CrazyComboSession.TickInput(
                                 (long)player.f_19797_,
                                 clicks,
                                 inWindow,
                                 phaseOrder,
                                 phaseCount,
                                 canBasicAttack,
                                 finishGate,
                                 holdFinish,
                                 ccNode.getCcFinishNoChase() != null,
                                 maxChases,
                                 ccNode.getCcStartupFinishNoChasePhase(),
                                 inputDown
                              )
                           );
                        switch (decision) {
                           case CHASE:
                              this.executeChase(executor, player, activeNode);
                              break;
                           case FINISH:
                              this.executeFinish(executor, player, activeNode, false, holdFinish);
                              break;
                           case FINISH_NO_CHASE:
                              this.executeFinish(executor, player, activeNode, true);
                              break;
                           case RELEASE_TO_NORMAL_COMBO:
                              this.releaseToNormalCombo(executor, activeNode, currentAnimation);
                              break;
                           case CANCEL:
                              this.cancelCrazyCombo(executor, activeNode, currentAnimation);
                           case NONE:
                        }
                     }
                  }
               }
            }
         }
      } else {
         this.reset();
      }
   }

   private void executeChase(IComboExecutor executor, LocalPlayer player, ComboNode activeNode) {
      StaticAnimation chaseAnimation = CrazyComboAnimationHelper.getChaseAnimation(activeNode);
      long sessionId = this.session.authoritySessionId();
      if (chaseAnimation != null && sessionId > 0L) {
         this.pendingFollowupIntent = null;
         this.followupCaptureEnabled = false;
         LivingEntityPatch<?> patch = executor.getExecutorPatch();
         boolean restartRequired = CrazyComboAnimationHelper.isPlayingAnimation(patch, chaseAnimation);
         this.session.commitChase(restartRequired, DMCAnimationUtils.getElapsedTime(patch));
         executor.clearReserve();
         this.suppressComboDispatch = true;
         DMCNetwork.sendToServer(new CPPlayCC(CPPlayCC.Type.CHASE, this.session.loopCount(), CPPlayCC.CCPlayTarget.PLAYER, sessionId));
         CrazyComboAudioHelper.playChaseTrigger(player);
         if (shouldPlayCinematicEffects(activeNode)) {
            CinematicBarsUtils.openFor(0.85F, 3.0F, 3.0F, 0.09F);
            CameraFovUtil.triggerCinematicLinkedZoom(0.9F, CameraFovUtil.EaseType.SINE, 3);
         }
      }
   }

   private void executeFinish(IComboExecutor executor, LocalPlayer player, ComboNode activeNode, boolean noChase) {
      this.executeFinish(executor, player, activeNode, noChase, false);
   }

   private void executeFinish(IComboExecutor executor, LocalPlayer player, ComboNode activeNode, boolean noChase, boolean causedByHold) {
      ICrazyComboNode ccNode = (ICrazyComboNode)activeNode;
      ComboNode finishNode = noChase ? ccNode.getCcFinishNoChase() : (ccNode.getCcFinish() != null ? ccNode.getCcFinish() : ccNode.getCcFinishNoChase());
      long sessionId = this.session.authoritySessionId();
      if (finishNode != null && sessionId > 0L) {
         if (finishNode instanceof SubComboNode) {
            executor.setCurrentDataNode(finishNode);
         }

         if (ccNode.isCcResetCombo()) {
            ComboNode root = executor.getComboRoot();
            if (root != null) {
               executor.getInvinciblePlayer().setCurrentLogicNode(root);
            }
         }

         if (finishNode instanceof IJudgementCutNode jc && jc.getJcChargeOverride() > 0) {
            executor.getJudgementCutController().setDirectChargeTime((long)jc.getJcChargeOverride());
         }

         this.suppressComboDispatch = true;
         this.session.beginFinish();
         this.sendFinishRequest(executor, (long)player.f_19797_);
         CrazyComboAudioHelper.playFinishTrigger(player);
         if (this.session.loopCount() > 0 && shouldPlayCinematicEffects(activeNode)) {
            CinematicBarsUtils.openFor(0.45F, 2.0F, 4.0F, 0.09F);
            CameraFovUtil.triggerZoom(5, 2, 2, 0.6F, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.INSTANT, 5);
         }

         if (!noChase && causedByHold && ccNode.isCcHoldFinishFollowupEnabled()) {
            this.pendingHoldFinishFollowup = true;
            this.finishAnimationObserved = false;
            this.pendingFollowupIntent = ComboIntentResolver.createSnapshotIntent(
               ComboNode.ComboTypes.KEY_1,
               ComboIntentResolver.ComboIntentType.SHORT_PRESS,
               Math.max(1, ComboInputSampler.getPressDuration(ComboNode.ComboTypes.KEY_1, false)),
               0L,
               DMComboEngine.engineTick,
               executor.getDirectionTracker()
            );
            DMCLog.info(
               DMCLog.Category.DOPPEL_CC,
               "[CCClient] HOLD_FINISH_FOLLOWUP_ARM node={} type={} captureTick={}",
               activeNode.getId(),
               this.pendingFollowupIntent.type(),
               this.pendingFollowupIntent.captureTick()
            );
         }
      }
   }

   private void handleFinishTransition(IComboExecutor executor, ComboNode activeNode, DynamicAnimation currentAnimation) {
      boolean finishStarted = CrazyComboAnimationHelper.isFinishAnimation(activeNode, currentAnimation);
      if (!finishStarted) {
         long tick = playerTick(executor);
         if (CrazyComboNodeResolver.matchesCurrentAnimation(activeNode, currentAnimation) && this.session.shouldSendFinishRequest(tick)) {
            this.sendFinishRequest(executor, tick);
         }
      } else if (this.pendingHoldFinishFollowup) {
         this.finishAnimationObserved = true;
         if (executor.getExecutorPatch().getEntityState().canBasicAttack()) {
            this.releaseHeldFinishFollowup(executor, currentAnimation);
         }
      } else {
         if (this.pendingFollowupIntent != null) {
            ComboIntentResolver.ComboInputIntent followup = this.pendingFollowupIntent;
            this.pendingFollowupIntent = null;
            executor.dispatchIntent(followup);
            DMCLog.info(
               DMCLog.Category.DOPPEL_CC,
               "[CCClient] FOLLOWUP_RELEASE node={} type={} captureTick={} animation={}",
               activeNode.getId(),
               followup.type(),
               followup.captureTick(),
               currentAnimation.getRegistryName()
            );
         }

         this.followupCaptureEnabled = false;
         this.session.reset();
      }
   }

   private void releaseHeldFinishFollowup(IComboExecutor executor, DynamicAnimation currentAnimation) {
      ComboNode currentLogicNode = executor.getCurrentLogicNode();
      if (currentLogicNode instanceof ICrazyComboNode && currentLogicNode.getId() == this.session.sourceNodeId()) {
         ComboIntentResolver.ComboInputIntent followup = this.pendingFollowupIntent;
         this.pendingFollowupIntent = null;
         this.pendingHoldFinishFollowup = false;
         this.finishAnimationObserved = false;
         if (followup != null) {
            executor.dispatchIntent(followup);
            DMCLog.info(
               DMCLog.Category.DOPPEL_CC,
               "[CCClient] HOLD_FINISH_FOLLOWUP_RELEASE node={} type={} captureTick={} animation={}",
               currentLogicNode.getId(),
               followup.type(),
               followup.captureTick(),
               currentAnimation.getRegistryName()
            );
         }

         this.followupCaptureEnabled = false;
         this.session.reset();
      } else {
         DMCLog.info(
            DMCLog.Category.DOPPEL_CC,
            "[CCClient] HOLD_FINISH_FOLLOWUP_DROP sourceNode={} currentLogicNode={} animation={}",
            this.session.sourceNodeId(),
            currentLogicNode != null ? currentLogicNode.getId() : -1,
            currentAnimation.getRegistryName()
         );
         this.pendingFollowupIntent = null;
         this.pendingHoldFinishFollowup = false;
         this.finishAnimationObserved = false;
         this.session.reset();
      }
   }

   private void releaseToNormalCombo(IComboExecutor executor, ComboNode activeNode, DynamicAnimation currentAnimation) {
      long sessionId = this.session.authoritySessionId();
      if (sessionId > 0L) {
         DMCNetwork.sendToServer(new CPPlayCC(CPPlayCC.Type.RELEASE_NORMAL, this.session.loopCount(), CPPlayCC.CCPlayTarget.PLAYER, sessionId));
         this.armRestartGuard(activeNode, currentAnimation, sessionId, "release_normal");
         executor.clearReserve();
         this.suppressComboDispatch = true;
         this.session.reset();
      }
   }

   private void cancelCrazyCombo(IComboExecutor executor, ComboNode activeNode, DynamicAnimation currentAnimation) {
      long sessionId = this.session.authoritySessionId();
      if (sessionId > 0L) {
         DMCNetwork.sendToServer(new CPPlayCC(CPPlayCC.Type.CANCEL, this.session.loopCount(), CPPlayCC.CCPlayTarget.PLAYER, sessionId));
         this.armRestartGuard(activeNode, currentAnimation, sessionId, "cancel");
         executor.clearReserve();
         this.suppressComboDispatch = true;
         this.session.reset();
      }
   }

   private static boolean isCurrentFinishAnimation(int sourceNodeId, DynamicAnimation currentAnimation) {
      ComboNode sourceNode = ComboNodeManager.get(sourceNodeId);
      return sourceNode != null && CrazyComboAnimationHelper.isFinishAnimation(sourceNode, currentAnimation);
   }

   public void reset() {
      this.session.reset();
      this.pendingFollowupIntent = null;
      this.pendingHoldFinishFollowup = false;
      this.finishAnimationObserved = false;
      this.followupCaptureEnabled = false;
      this.suppressComboDispatch = false;
      this.restartGuardNodeId = -1;
      this.restartGuardAnimation = null;
   }

   private void armRestartGuard(ComboNode activeNode, DynamicAnimation currentAnimation, long authoritySessionId, String reason) {
      ResourceLocation animationId = currentAnimation.getRegistryName();
      if (animationId != null) {
         this.restartGuardNodeId = activeNode.getId();
         this.restartGuardAnimation = animationId;
         DMCLog.info(
            DMCLog.Category.DOPPEL_CC,
            "[CCClient] RESTART_GUARD_SET node={} authoritySession={} animation={} reason={}",
            this.restartGuardNodeId,
            authoritySessionId,
            this.restartGuardAnimation,
            reason
         );
      }
   }

   private boolean isRestartGuarded(ComboNode activeNode, DynamicAnimation currentAnimation) {
      return this.restartGuardNodeId == activeNode.getId()
         && this.restartGuardAnimation != null
         && this.restartGuardAnimation.equals(currentAnimation.getRegistryName());
   }

   private void clearRestartGuardIfAnimationChanged(DynamicAnimation currentAnimation) {
      if (this.restartGuardAnimation != null && !this.restartGuardAnimation.equals(currentAnimation.getRegistryName())) {
         this.clearRestartGuard("animation_changed");
      }
   }

   private void clearRestartGuard(String reason) {
      if (this.restartGuardAnimation != null) {
         DMCLog.info(
            DMCLog.Category.DOPPEL_CC,
            "[CCClient] RESTART_GUARD_CLEAR node={} animation={} reason={}",
            this.restartGuardNodeId,
            this.restartGuardAnimation,
            reason
         );
      }

      this.restartGuardNodeId = -1;
      this.restartGuardAnimation = null;
   }

   public boolean isTrackingActionChain() {
      return this.session.stage() != CrazyComboSession.Stage.IDLE
         || this.owningExecutor.getInvinciblePlayer().hasActionSession(DmcWeaponType.YAMATO, WeaponActionType.CRAZY_COMBO);
   }

   public boolean isBlockingComboDispatch() {
      return this.suppressComboDispatch || this.session.acceptsCrazyComboInput();
   }

   public boolean isAcceptingCrazyComboInput() {
      return this.session.acceptsCrazyComboInput();
   }

   public void captureFollowupIntent(ComboIntentResolver.ComboInputIntent intent) {
      if (this.followupCaptureEnabled || this.session.stage() == CrazyComboSession.Stage.FINISH) {
         if (!this.session.waitingForChaseAnimation()) {
            ComboType inputType = ComboInputSampler.getComboTypeByIndex(this.session.inputKeyIndex());
            if (inputType != null && intent.type() == inputType) {
               this.pendingFollowupIntent = intent;
               DMCLog.info(
                  DMCLog.Category.DOPPEL_CC,
                  "[CCClient] FOLLOWUP_CAPTURE node={} type={} captureTick={} loop={}",
                  this.session.sourceNodeId(),
                  intent.type(),
                  intent.captureTick(),
                  this.session.loopCount()
               );
            }
         }
      }
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

   public int getSourceNodeId() {
      return this.session.sourceNodeId();
   }

   private void sendFinishRequest(IComboExecutor executor, long tick) {
      if (this.session.authoritySessionId() > 0L) {
         ComboIntentResolver.ComboInputIntent snapshot = ComboIntentResolver.createSnapshotIntent(
            ComboNode.ComboTypes.KEY_1, ComboIntentResolver.ComboIntentType.SHORT_PRESS, 1, 0L, DMComboEngine.engineTick, executor.getDirectionTracker()
         );
         DMCNetwork.sendToServer(
            new CPPlayCC(
               CPPlayCC.Type.FINISH,
               this.session.loopCount(),
               CPPlayCC.CCPlayTarget.PLAYER,
               this.session.authoritySessionId(),
               this.session.requiredFinishPhaseOrder(),
               snapshot.directionMask(),
               snapshot.directionEvents(),
               snapshot.captureTick()
            )
         );
         this.session.markFinishRequestSent(tick);
         DMCLog.info(
            DMCLog.Category.DOPPEL_CC,
            "[CCClient] FINISH_REQUEST node={} loop={} noChase={} requiredPhase={} tick={}",
            this.session.sourceNodeId(),
            this.session.loopCount(),
            this.session.isFinishNoChase(),
            this.session.requiredFinishPhaseOrder(),
            tick
         );
      }
   }

   private static long playerTick(IComboExecutor executor) {
      return (long)((LocalPlayer)executor.getExecutorPatch().getOriginal()).f_19797_;
   }

   private static boolean shouldPlayCinematicEffects(ComboNode activeNode) {
      return true;
   }
}
