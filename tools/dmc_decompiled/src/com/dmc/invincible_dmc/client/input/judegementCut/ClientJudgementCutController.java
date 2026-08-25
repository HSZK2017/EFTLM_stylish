package com.dmc.invincible_dmc.client.input.judegementCut;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.IJudgementCutNode;
import com.dmc.invincible_dmc.api.skill.JudgementCutChargePhase;
import com.dmc.invincible_dmc.api.skill.crazycombo.CrazyComboSession;
import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.capability.weapon.DmcWeaponManager;
import com.dmc.invincible_dmc.client.DMCKeyMappings;
import com.dmc.invincible_dmc.client.gui.vergilstatus.VergilStatusOverlay;
import com.dmc.invincible_dmc.client.input.ComboInputSampler;
import com.dmc.invincible_dmc.client.input.IComboExecutor;
import com.dmc.invincible_dmc.client.input.InputLeaseManager;
import com.dmc.invincible_dmc.client.render.shake.CameraShakeManager;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPPlayJC;
import com.dmc.invincible_dmc.skill.dodge.VergilDodgeSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.dmc.invincible_dmc.utils.vfx.CameraFovUtil;
import com.dmc.invincible_dmc.utils.vfx.CinematicBarsUtils;
import com.dmc.invincible_dmc.utils.yamato.TargetTeleportUtils;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;

@OnlyIn(Dist.CLIENT)
public final class ClientJudgementCutController {
   private static volatile boolean anyJCCharging = false;
   private static final long HOLD_THRESHOLD_MS = 250L;
   private static final long DEFAULT_CHARGE_MS = 280L;
   private static final long JUST_RELEASE_WINDOW_MS = 230L;
   private long directChargeTimeMs = -1L;
   private final JudgementCutChargeStateMachine chargeState = new JudgementCutChargeStateMachine();
   private boolean chargeCompletedIsSheathPerfect = false;
   private DynamicAnimation chargeStartAnim = null;
   private float chargeStartAnimProgress = -1.0F;
   private final JudgementCutChainTracker chainTracker = new JudgementCutChainTracker();
   private boolean trickTargetInAir = false;
   private boolean trickTriggeredJC = false;
   private long inputLeaseToken;
   private long nextInputLeaseToken;
   private final IComboExecutor owningExecutor;

   public static boolean isAnyJCCharging() {
      return anyJCCharging;
   }

   public ClientJudgementCutController(IComboExecutor owningExecutor) {
      this.owningExecutor = owningExecutor;
   }

   public void onTick(@Nullable IComboExecutor dispatcher) {
      LocalPlayer player = Minecraft.m_91087_().f_91074_;
      if (player != null && !ComboInputSampler.BypassInput()) {
         Minecraft minecraft = Minecraft.m_91087_();
         if (minecraft.f_91080_ == null && !minecraft.m_91104_()) {
            LocalPlayerPatch lpp = (dispatcher != null ? dispatcher : this.owningExecutor).getExecutorPatch();
            if (lpp != null && lpp.isStunned()) {
               this.reset();
            } else {
               boolean rawKeyHeld = ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY1);
               InputLeaseManager.observePhysicalState(0, rawKeyHeld);
               if (lpp == null || !DmcWeaponManager.isActiveWeapon((Player)lpp.getOriginal(), DmcWeaponType.YAMATO)) {
                  this.reset();
               } else if (!DmcWeaponManager.isActionWeapon((Player)lpp.getOriginal(), DmcWeaponType.YAMATO)) {
                  this.reset();
               } else if (this.owningExecutor.getCrazyComboController().getCurrentState() != CrazyComboSession.Stage.IDLE) {
                  this.reset();
               } else {
                  if (rawKeyHeld && this.inputLeaseToken == 0L) {
                     long candidateToken = ++this.nextInputLeaseToken;
                     if (!InputLeaseManager.acquire(0, InputLeaseManager.Owner.YAMATO_JUDGEMENT_CUT, candidateToken)) {
                        this.resetPressState(true);
                        return;
                     }

                     this.inputLeaseToken = candidateToken;
                  }

                  if (this.chainTracker.getChainCount() > 0) {
                     if (this.chainTracker.isJustFired()) {
                        if (JudgementCutAnimationHelper.isPlayingAnimation(lpp, this.chainTracker.getLastFiredAnim())) {
                           this.chainTracker.setJustFired(false);
                        } else if (this.chargeState.isActive()) {
                           this.chainTracker.setJustFired(false);
                        }
                     } else if (!JudgementCutAnimationHelper.isPlayingAnimation(lpp, this.chainTracker.getLastFiredAnim())) {
                        DMCLog.info(DMCLog.Category.JC, "[JCClient] JC animation ended or changed -> reset chain");
                        this.chainTracker.reset();
                     }
                  }

                  boolean keyHeld = this.inputLeaseToken != 0L
                     && InputLeaseManager.isOwnedBy(0, InputLeaseManager.Owner.YAMATO_JUDGEMENT_CUT, this.inputLeaseToken)
                     && ComboInputSampler.isPressed(ComboNode.ComboTypes.KEY_1);
                  long nowMs = Util.m_137550_();
                  if (this.chargeState.isReady() && !this.chargeState.isSuspended()) {
                     DynamicAnimation currentAnim = JudgementCutAnimationHelper.getCurrentAnimation(lpp);
                     if (!DMCAnimationUtils.sameAnimation(currentAnim, this.chargeStartAnim) && this.isJumpOrDodgeOrEnemyStepAnim(currentAnim, lpp)) {
                        this.chargeState.suspend(nowMs);
                        this.refreshChargingFlag();
                        boolean suspendedPerfect = this.evaluateSuspendedPerfect(dispatcher, player, nowMs);
                        DMCLog.info(DMCLog.Category.JC, "[JCClient] CHARGE_SUSPENDED perfect={} anim={}", suspendedPerfect, safeAnimName(currentAnim));
                     }
                  }

                  if (this.chargeState.isSuspended()) {
                     DynamicAnimation currentAnim = JudgementCutAnimationHelper.getCurrentAnimation(lpp);
                     if (!this.isJumpOrDodgeOrEnemyStepAnim(currentAnim, lpp)) {
                        if (ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY1)) {
                           this.beginChargeSession(nowMs, lpp, currentAnim);
                           ComboInputSampler.forceSetPressed(ComboNode.ComboTypes.KEY_1);
                           DMCLog.info(DMCLog.Category.JC, "[JCClient] suspended animation ended -> restart charge");
                        } else {
                           this.reset();
                        }
                     } else if (!ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY1)) {
                        this.handleKeyRelease(dispatcher, player, lpp, nowMs);
                     }
                  } else {
                     if (keyHeld) {
                        this.handleKeyPress(dispatcher, player, lpp, nowMs);
                     } else {
                        this.handleKeyRelease(dispatcher, player, lpp, nowMs);
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

   private void handleKeyPress(@Nullable IComboExecutor dispatcher, LocalPlayer player, LocalPlayerPatch lpp, long nowMs) {
      if (!this.chargeState.isActive()) {
         this.beginChargeSession(nowMs, lpp, JudgementCutAnimationHelper.getCurrentAnimation(lpp));
      } else {
         DynamicAnimation currentAnim = JudgementCutAnimationHelper.getCurrentAnimation(lpp);
         if (this.chargeStartAnim != null
            && !DMCAnimationUtils.sameAnimation(currentAnim, this.chargeStartAnim)
            && DMCAnimationUtils.isAnimationType(currentAnim, AttackAnimation.class)) {
            if (this.chargeState.phase() != JudgementCutChargePhase.ARMED && this.chargeState.phase() != JudgementCutChargePhase.CHARGING) {
               this.resetPressState();
               return;
            }

            DMCLog.info(
               DMCLog.Category.JC,
               "[JCClient] attack animation changed {} -> {} -> preserve press start",
               safeAnimName(this.chargeStartAnim),
               safeAnimName(currentAnim)
            );
            this.chargeStartAnim = currentAnim;
            this.chargeStartAnimProgress = JudgementCutAnimationHelper.getAnimationProgress(lpp);
            this.chargeCompletedIsSheathPerfect = false;
            this.chargeState.rebindRequiredChargeTime(this.resolveChargeTimeMs(dispatcher, player));
         }

         this.tickCharging(dispatcher, player, nowMs);
         if (this.chargeState.isReady()
            && !DMCAnimationUtils.sameAnimation(currentAnim, this.chargeStartAnim)
            && this.isJumpOrDodgeOrEnemyStepAnim(currentAnim, lpp)) {
            this.chargeState.suspend(nowMs);
            this.refreshChargingFlag();
            boolean suspendedPerfect = this.evaluateSuspendedPerfect(dispatcher, player, nowMs);
            DMCLog.info(DMCLog.Category.JC, "[JCClient] CHARGE_SUSPENDED perfect={} anim={}", suspendedPerfect, safeAnimName(currentAnim));
         }
      }
   }

   private void tickCharging(@Nullable IComboExecutor dispatcher, LocalPlayer player, long nowMs) {
      long resolvedChargeTimeMs = this.getChargeTimeMs(dispatcher, player);
      JudgementCutChargeStateMachine.Update update = this.chargeState.advance(nowMs, 250L, resolvedChargeTimeMs, 230L);
      this.refreshChargingFlag();
      if (update.chargeStarted()) {
         JudgementCutAudioHelper.playChargeStart(player);
         DMCLog.info(
            DMCLog.Category.JC,
            "[JCClient] CHARGING start={}ms anim={} progress={}",
            this.chargeState.pressedAtMs(),
            safeAnimName(this.chargeStartAnim),
            this.chargeStartAnimProgress
         );
      }

      if (update.chargeReady()) {
         DMCLog.info(
            DMCLog.Category.JC, "[JCClient] READY elapsed={}ms required={}ms", this.chargeState.elapsedMs(nowMs), this.chargeState.requiredChargeTimeMs()
         );
      }

      if (update.perfectReleaseWindowEnded()) {
         LocalPlayerPatch lpp = (dispatcher != null ? dispatcher : this.owningExecutor).getExecutorPatch();
         if (JudgementCutAnimationHelper.isPlayingJudgementCutAnimation(lpp)) {
            DMCLog.info(DMCLog.Category.JC, "[JCClient] perfect release window ended during JC animation -> suppress charge complete sound");
         } else {
            JudgementCutAudioHelper.playChargeComplete(player);
            DMCLog.info(DMCLog.Category.JC, "[JCClient] perfect release window ended -> play charge complete sound");
         }
      }

      if (this.chargeState.isReady()) {
         LocalPlayerPatch lpp = (dispatcher != null ? dispatcher : this.owningExecutor).getExecutorPatch();
         DynamicAnimation currentAnim = JudgementCutAnimationHelper.getCurrentAnimation(lpp);
         float animProgress = JudgementCutAnimationHelper.getAnimationProgress(lpp);
         ComboNode activeNode = dispatcher != null ? dispatcher.getCurrentNode() : null;
         boolean previous = this.chargeCompletedIsSheathPerfect;
         this.chargeCompletedIsSheathPerfect = JCInputEvaluator.evaluateSheathPerfect(
            true, activeNode, currentAnim, JudgementCutAnimationHelper.getRawElapsedTime(lpp), animProgress, this.chargeStartAnim, this.chargeStartAnimProgress
         );
         if (!previous && this.chargeCompletedIsSheathPerfect) {
            DMCLog.info(DMCLog.Category.JC, "[JCClient] SheathPerfect -> TRUE anim={} progress={}", safeAnimName(currentAnim), animProgress);
         }
      }
   }

   private void handleKeyRelease(@Nullable IComboExecutor dispatcher, LocalPlayer player, LocalPlayerPatch lpp, long nowMs) {
      if (this.chargeState.isActive()) {
         if (this.isInReleaseBlockWindow(lpp)) {
            this.resetPressState();
         } else {
            this.tickCharging(dispatcher, player, nowMs);
            if (this.chargeState.isSuspended()) {
               boolean perfect = this.evaluateSuspendedPerfect(dispatcher, player, nowMs);
               DMCLog.info(DMCLog.Category.JC, "[JCClient] RELEASE suspended perfect={}", perfect);
               this.executeJudgementCut(dispatcher, player, lpp, perfect);
            } else {
               long elapsed = this.chargeState.elapsedMs(nowMs);
               long needed = this.chargeState.requiredChargeTimeMs() > 0L ? this.chargeState.requiredChargeTimeMs() : this.getChargeTimeMs(dispatcher, player);
               boolean charging = this.chargeState.isCharging();
               boolean ready = this.chargeState.isReady();
               ComboNode activeNode = dispatcher != null ? dispatcher.getCurrentNode() : null;
               DynamicAnimation currentAnim = JudgementCutAnimationHelper.getCurrentAnimation(lpp);
               float animProgress = JudgementCutAnimationHelper.getAnimationProgress(lpp);
               float rawElapsed = JudgementCutAnimationHelper.getRawElapsedTime(lpp);
               boolean sheathPerfect = JCInputEvaluator.evaluateSheathPerfect(
                  charging, activeNode, currentAnim, rawElapsed, animProgress, this.chargeStartAnim, this.chargeStartAnimProgress
               );
               if (!sheathPerfect && charging) {
                  DMCPlayer invinciblePlayer = dispatcher != null ? dispatcher.getInvinciblePlayer() : DMCPlayerCapabilityProvider.get(player);
                  ComboNode dataNode = invinciblePlayer.getCurrentDataNode();
                  float windowStart = JudgementCutAnimationHelper.getPerfectWindowStart(dataNode);
                  float windowEnd = JudgementCutAnimationHelper.getPerfectWindowEnd(dataNode);
                  if (windowStart >= 0.0F && windowEnd >= windowStart) {
                     sheathPerfect = JCInputEvaluator.evaluateSheathPerfect(
                        true, dataNode, currentAnim, rawElapsed, animProgress, this.chargeStartAnim, this.chargeStartAnimProgress
                     );
                  }
               }

               int currentChain = this.chainTracker.getChainCount();
               boolean justReleasePerfect = currentChain == 0 && ready && JCInputEvaluator.evaluateJustReleasePerfect(true, elapsed, needed, 230L);
               boolean perfect = currentChain > 0 ? sheathPerfect : sheathPerfect || justReleasePerfect;
               if (this.trickTriggeredJC) {
                  perfect = false;
               }

               boolean canExecute = currentChain > 0 ? sheathPerfect : ready;
               DMCLog.info(
                  DMCLog.Category.JC,
                  "[JCClient] RELEASE phase={} elapsed={}ms required={}ms sheath={} just={} perfect={} execute={} chain={}",
                  this.chargeState.phase(),
                  elapsed,
                  needed,
                  sheathPerfect,
                  justReleasePerfect,
                  perfect,
                  canExecute,
                  currentChain
               );
               if (canExecute) {
                  this.executeJudgementCut(dispatcher, player, lpp, perfect);
               } else {
                  this.handleExecutionFailure(dispatcher, nowMs);
               }
            }
         }
      }
   }

   private void executeJudgementCut(@Nullable IComboExecutor dispatcher, LocalPlayer player, LocalPlayerPatch lpp, boolean isPerfect) {
      this.owningExecutor.getCrazyComboController().reset();
      boolean inAir = this.trickTargetInAir || !TargetTeleportUtils.isNearGround(player);
      this.trickTargetInAir = false;
      StaticAnimation targetAnim = JudgementCutAnimationHelper.getJCTargetAnimation(lpp, isPerfect, inAir);
      DMCLog.info(
         DMCLog.Category.JC,
         "[JCClient] >>> EXECUTE: isPerfect={} inAir={} targetAnim={} chain={}/{}",
         isPerfect,
         inAir,
         safeAnimName(targetAnim),
         this.chainTracker.getChainCount(),
         this.chainTracker.getMaxChain()
      );
      if (this.chainTracker.getChainCount() == 0) {
         this.chainTracker.startChain(isPerfect, targetAnim);
      } else {
         this.chainTracker.advanceChain(targetAnim);
      }

      if (this.chainTracker.getChainCount() <= this.chainTracker.getMaxChain()) {
         DMCLog.info(DMCLog.Category.JC, "[JCClient] 释放次元斩 -> 完美={} 连击数={}/{}", isPerfect, this.chainTracker.getChainCount(), this.chainTracker.getMaxChain());
         DMCNetwork.sendToServer(new CPPlayJC(isPerfect, this.chainTracker.getChainCount(), inAir));
         float time = inAir ? 0.6F : 0.5F;
         if (isPerfect) {
            CinematicBarsUtils.openFor(time, 4.0F, 5.0F, 0.09F);
            CameraShakeManager.addShake(((LocalPlayer)lpp.getOriginal()).m_146892_(), 0.4F, 6, 6.0F);
            CameraFovUtil.triggerCinematicLinkedZoom(0.8F, CameraFovUtil.EaseType.SINE, 3);
            VergilStatusOverlay.triggerConcFlash();
         }

         this.resetPressState();
      } else {
         DMCLog.info(DMCLog.Category.JC, "[JCClient] 达到最大连击数 -> 重置");
         this.reset();
      }
   }

   private void handleExecutionFailure(@Nullable IComboExecutor dispatcher, long nowMs) {
      if (this.chargeState.phase() == JudgementCutChargePhase.ARMED) {
         DMCLog.info(DMCLog.Category.JC, "[JCClient] release before charge threshold -> preserve normal combo flow");
         this.resetPressState();
      } else {
         long elapsed = this.chargeState.elapsedMs(nowMs);
         long needed = this.chargeState.requiredChargeTimeMs();
         DMCLog.info(
            DMCLog.Category.JC,
            "[JCClient] charge release rejected phase={} elapsed={}ms required={}ms chain={}",
            this.chargeState.phase(),
            elapsed,
            needed,
            this.chainTracker.getChainCount()
         );
         if (this.chainTracker.getChainCount() > 0) {
            CinematicBarsUtils.close();
         }

         this.reset();
      }
   }

   private void beginChargeSession(long nowMs, LocalPlayerPatch lpp, DynamicAnimation currentAnim) {
      this.chargeState.begin(nowMs);
      this.refreshChargingFlag();
      this.chargeCompletedIsSheathPerfect = false;
      this.chargeStartAnim = currentAnim;
      this.chargeStartAnimProgress = JudgementCutAnimationHelper.getAnimationProgress(lpp);
      this.trickTargetInAir = false;
      this.trickTriggeredJC = false;
   }

   private void refreshChargingFlag() {
      JudgementCutChargePhase phase = this.chargeState.phase();
      anyJCCharging = phase.isCharging();
      this.syncChargePhase(phase);
   }

   private void syncChargePhase(JudgementCutChargePhase phase) {
      LocalPlayerPatch playerPatch = this.owningExecutor.getExecutorPatch();
      if (playerPatch != null) {
         SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
         if (container != null && !container.isEmpty() && container.getDataManager().hasData((SkillDataKey)DMCSkillDataKeys.JUDGEMENT_CUT_CHARGE_PHASE.get())) {
            int phaseId = phase.networkId();
            if ((Integer)container.getDataManager().getDataValue((SkillDataKey)DMCSkillDataKeys.JUDGEMENT_CUT_CHARGE_PHASE.get()) != phaseId) {
               container.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.JUDGEMENT_CUT_CHARGE_PHASE.get(), phaseId);
            }
         }
      }
   }

   public void reset() {
      this.resetPressState(true);
      this.chainTracker.reset();
      this.directChargeTimeMs = -1L;
      this.trickTargetInAir = false;
      this.trickTriggeredJC = false;
   }

   private void resetPressState() {
      this.resetPressState(false);
   }

   private void resetPressState(boolean requireFreshRelease) {
      if (this.inputLeaseToken != 0L) {
         boolean physicallyHeld = ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY1);
         InputLeaseManager.release(0, InputLeaseManager.Owner.YAMATO_JUDGEMENT_CUT, this.inputLeaseToken, requireFreshRelease, physicallyHeld);
         this.inputLeaseToken = 0L;
      }

      this.chargeState.reset();
      this.refreshChargingFlag();
      this.chargeCompletedIsSheathPerfect = false;
      this.chargeStartAnim = null;
      this.chargeStartAnimProgress = -1.0F;
      this.trickTargetInAir = false;
      this.trickTriggeredJC = false;
   }

   public void restartChargingAndResetChain(@Nullable IComboExecutor dispatcher) {
      LocalPlayerPatch lpp = (dispatcher != null ? dispatcher : this.owningExecutor).getExecutorPatch();
      this.chainTracker.reset();
      this.directChargeTimeMs = -1L;
      if (ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY1)) {
         if (this.inputLeaseToken == 0L) {
            long candidateToken = ++this.nextInputLeaseToken;
            if (!InputLeaseManager.acquire(0, InputLeaseManager.Owner.YAMATO_JUDGEMENT_CUT, candidateToken)) {
               this.resetPressState(true);
               return;
            }

            this.inputLeaseToken = candidateToken;
         }

         LivingEntity target = lpp != null ? lpp.getTarget() : null;
         this.trickTargetInAir = target != null && !TargetTeleportUtils.isNearGround(target);
         this.chargeCompletedIsSheathPerfect = false;
         this.chargeState.forceReady(Util.m_137550_(), 280L);
         this.refreshChargingFlag();
         this.chargeStartAnim = JudgementCutAnimationHelper.getCurrentAnimation(lpp);
         this.chargeStartAnimProgress = JudgementCutAnimationHelper.getAnimationProgress(lpp);
         ComboInputSampler.forceSetPressed(ComboNode.ComboTypes.KEY_1);
         this.trickTriggeredJC = true;
         DMCLog.info(DMCLog.Category.JC, "[JCClient] KEY3 trick -> READY targetInAir={}", this.trickTargetInAir);
      } else {
         this.resetPressState();
         DMCLog.info(DMCLog.Category.JC, "[JCClient] KEY3 trick -> reset because KEY1 is released");
      }
   }

   public boolean isCharging() {
      return this.chargeState.isCharging();
   }

   public boolean isChargeSuspended() {
      return this.chargeState.isSuspended();
   }

   public float getChargeStartAnimProgress() {
      return this.chargeStartAnimProgress;
   }

   public long getChargeStartMs() {
      return this.chargeState.isCharging() ? this.chargeState.pressedAtMs() : -1L;
   }

   public long getChargeReadyAtMs() {
      if (!this.chargeState.isCharging()) {
         return -1L;
      } else if (this.chargeState.readyAtMs() >= 0L) {
         return this.chargeState.readyAtMs();
      } else {
         long pressedAtMs = this.chargeState.pressedAtMs();
         long requiredChargeTimeMs = this.chargeState.requiredChargeTimeMs();
         return pressedAtMs >= 0L && requiredChargeTimeMs >= 0L ? pressedAtMs + requiredChargeTimeMs : -1L;
      }
   }

   public int getChainCount() {
      return this.chainTracker.getChainCount();
   }

   public boolean isFirstWasPerfect() {
      return this.chainTracker.isFirstWasPerfect();
   }

   public long getJustReleaseWindowMs() {
      return 230L;
   }

   public long getChargeTimeMs(LocalPlayer player) {
      return this.getChargeTimeMs(null, player);
   }

   public void setDirectChargeTime(long ms) {
      this.directChargeTimeMs = ms;
   }

   public long getChargeTimeMs(@Nullable IComboExecutor dispatcher, LocalPlayer player) {
      return this.chargeState.requiredChargeTimeMs() >= 0L ? this.chargeState.requiredChargeTimeMs() : this.resolveChargeTimeMs(dispatcher, player);
   }

   private long resolveChargeTimeMs(@Nullable IComboExecutor dispatcher, LocalPlayer player) {
      if (this.directChargeTimeMs > 0L) {
         return this.directChargeTimeMs;
      } else {
         LocalPlayerPatch lpp = (dispatcher != null ? dispatcher : this.owningExecutor).getExecutorPatch();
         if (lpp != null && lpp.getSkill(SkillSlots.DODGE).getSkill() instanceof VergilDodgeSkill yds) {
            DynamicAnimation currentAnim = JudgementCutAnimationHelper.getCurrentAnimation(lpp);
            if (yds.isDodgeAnimation(currentAnim)) {
               int perAnim = yds.getJcChargeTimeForAnimation(currentAnim != null ? DMCAnimationUtils.getRealAnimation(currentAnim) : null);
               if (perAnim > 0) {
                  return (long)perAnim;
               }
            }
         }

         if (lpp != null) {
            DynamicAnimation currentAnim = JudgementCutAnimationHelper.getCurrentAnimation(lpp);
            if (currentAnim != null) {
               StaticAnimation real = DMCAnimationUtils.getRealAnimation(currentAnim);
               if (DMCAnimationUtils.isAnimationType(real, YamatoAttackAnimation.class)) {
                  Integer animCharge = YamatoAttackAnimation.getJcChargeTime(real);
                  if (animCharge != null && animCharge > 0) {
                     return (long)animCharge.intValue();
                  }
               }
            }
         }

         DMCPlayer ip = dispatcher != null ? dispatcher.getInvinciblePlayer() : DMCPlayerCapabilityProvider.get(player);
         if (ip.getCurrentDataNode() instanceof IJudgementCutNode jc && jc.getJcChargeOverride() > 0) {
            return (long)jc.getJcChargeOverride();
         }

         return 280L;
      }
   }

   private boolean isJumpOrDodgeOrEnemyStepAnim(DynamicAnimation anim, LocalPlayerPatch lpp) {
      if (anim != null && lpp != null) {
         StaticAnimation real = DMCAnimationUtils.getRealAnimation(anim);
         if (real == null) {
            return false;
         } else {
            if (lpp.getSkill(SkillSlots.DODGE).getSkill() instanceof VergilDodgeSkill yds && yds.isDodgeAnimation(anim)) {
               return true;
            }

            return real == Animations.BIPED_JUMP
               ? true
               : DMCAnimationUtils.sameAccessor(DMCAnimationUtils.getRealAnimationAccessor(anim), lpp.getClientAnimator().getJumpAnimation());
         }
      } else {
         return false;
      }
   }

   private boolean isInReleaseBlockWindow(@Nullable LocalPlayerPatch lpp) {
      if (lpp == null) {
         return false;
      } else {
         DynamicAnimation currentAnim = JudgementCutAnimationHelper.getCurrentAnimation(lpp);
         if (currentAnim == null) {
            return false;
         } else {
            StaticAnimation real = DMCAnimationUtils.getRealAnimation(currentAnim);
            if (real == null) {
               return false;
            } else {
               Optional<TimePairList> blockTime = real.getProperty(YamatoAttackAnimation.JC_RELEASE_BLOCK_TIME);
               return blockTime.isPresent() && blockTime.get().isTimeInPairs(JudgementCutAnimationHelper.getRawElapsedTime(lpp));
            }
         }
      }
   }

   private boolean evaluateSuspendedPerfect(@Nullable IComboExecutor dispatcher, LocalPlayer player, long nowMs) {
      if (this.chargeCompletedIsSheathPerfect) {
         return true;
      } else {
         if (this.chainTracker.getChainCount() == 0 && this.chargeState.readyAtMs() >= 0L) {
            long sinceReadyMs = nowMs - this.chargeState.readyAtMs();
            if (sinceReadyMs >= 0L && sinceReadyMs <= 230L) {
               return true;
            }
         }

         if (this.chargeState.suspendedAtMs() >= 0L) {
            LocalPlayerPatch lpp = (dispatcher != null ? dispatcher : this.owningExecutor).getExecutorPatch();
            if ((lpp != null ? lpp.getSkill(SkillSlots.DODGE).getSkill() : null) instanceof VergilDodgeSkill vergilDodgeSkill) {
               DynamicAnimation currentAnim = JudgementCutAnimationHelper.getCurrentAnimation(lpp);
               StaticAnimation real = JudgementCutAnimationHelper.getRegisteredStaticAnimation(currentAnim);
               int dodgeNeededMs = vergilDodgeSkill.getJcChargeTimeForAnimation(real);
               long sinceSuspendMs = nowMs - this.chargeState.suspendedAtMs();
               if (dodgeNeededMs > 0 && sinceSuspendMs >= (long)dodgeNeededMs && sinceSuspendMs - (long)dodgeNeededMs <= 230L) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static String safeAnimName(@Nullable DynamicAnimation anim) {
      return JudgementCutAnimationHelper.getAnimationName(anim);
   }
}
