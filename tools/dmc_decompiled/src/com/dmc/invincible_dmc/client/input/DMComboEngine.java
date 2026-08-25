package com.dmc.invincible_dmc.client.input;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.api.skill.ITapHoldNode;
import com.dmc.invincible_dmc.api.skill.crazycombo.CrazyComboSession;
import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerCapability;
import com.dmc.invincible_dmc.capability.weapon.DmcWeaponManager;
import com.dmc.invincible_dmc.client.DMCKeyMappings;
import com.dmc.invincible_dmc.client.input.crazyCombo.ClientCrazyComboController;
import com.dmc.invincible_dmc.client.input.crazyCombo.DoppelgangerCrazyComboController;
import com.dmc.invincible_dmc.client.input.judegementCut.JudgementCutAnimationHelper;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.impl.IEpicFightCameraAPI;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPCrazyComboReset;
import com.dmc.invincible_dmc.network.client.CPDoppelgangerControl;
import com.dmc.invincible_dmc.network.client.CPDoppelgangerDelayMode;
import com.dmc.invincible_dmc.network.client.CPDoppelgangerTapHold;
import com.dmc.invincible_dmc.network.client.CPPlayerInputEvent;
import com.dmc.invincible_dmc.network.client.CPPlayerInputSync;
import com.dmc.invincible_dmc.network.client.CPTapHoldTrigger;
import com.dmc.invincible_dmc.network.client.CPWeaponSwitch;
import com.dmc.invincible_dmc.skill.dodge.VergilDodgeSkill;
import com.dmc.invincible_dmc.skill.weapon_combo.Yamato;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.dmc.invincible_dmc.utils.yamato.CameraLockUtil;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nullable;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.Key;
import net.minecraftforge.client.event.InputEvent.MouseButton;
import net.minecraftforge.client.event.ScreenEvent.Closing;
import net.minecraftforge.client.event.ScreenEvent.Opening;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT}
)
public class DMComboEngine {
   public static final boolean debugLog = false;
   public static long engineTick;
   private static volatile InputFrame currentInputFrame = new InputFrame(0L, 0L, 0L, (short)0);
   private static long inputSyncSequence;
   private static final Map<Integer, DoppelgangerCrazyComboController> doppelCcControllers = new ConcurrentHashMap<>();
   private static final Map<UUID, Long> DOPPEL_TAP_HOLD_CHARGE_START = new ConcurrentHashMap<>();
   private static final InputSession inputSession = new InputSession();
   private static IComboExecutor localPlayerDispatcher;
   private static final List<IComboExecutor> activeExecutors = new CopyOnWriteArrayList<>();
   private static long lastDispatchedEngineTick = -1L;
   private static ComboType lastDispatchedType;
   private static long dispatchedNodeId = -1L;
   private static String serverFailureMessage;
   private static long serverFailureExpireTick;
   private static boolean holdLockOnActive;
   private static boolean lockOnInitiatedByUs;
   private static int lastLockOnTargetId = -1;
   private static boolean playerWasDead;
   private static boolean suppressNextInputEvent;
   private static final HoldToActionHelper DOPPEL_CONTROL_HELPER = new HoldToActionHelper(5);
   private static boolean doppelControlPressClaimedBySdt;
   private static final Map<ComboType, Integer> HIGH_FREQUENCY_CLICK_QUEUE = new ConcurrentHashMap<>();
   private static boolean prevUp;
   private static boolean prevDown;
   private static boolean prevLeft;
   private static boolean prevRight;
   private static boolean prevStatesStale;
   private static short lastSentInputMask = -1;

   public static void init() {
      ComboInputSampler.init();
      DMCLog.info(DMCLog.Category.COMBO_ENGINE, "[ComboEngine] Initialized with {} ComboType slots", ComboInputSampler.STATES_TO_TYPE.length);
   }

   public static void registerExecutor(IComboExecutor executor) {
      if (!activeExecutors.contains(executor)) {
         activeExecutors.add(executor);
         DMCLog.info(
            DMCLog.Category.COMBO_ENGINE,
            "[ComboEngine] Registered new execution pipeline for entity: {}",
            ((LocalPlayer)executor.getExecutorPatch().getOriginal()).m_7755_().getString()
         );
      }
   }

   public static void unregisterExecutor(IComboExecutor executor) {
      activeExecutors.remove(executor);
      DMCLog.info(
         DMCLog.Category.COMBO_ENGINE,
         "[ComboEngine] Unregistered execution pipeline for entity: {}",
         ((LocalPlayer)executor.getExecutorPatch().getOriginal()).m_7755_().getString()
      );
   }

   public static IComboExecutor getLocalPlayerDispatcher() {
      return localPlayerDispatcher;
   }

   public static int consumeClicks(ComboType type) {
      Integer previous = HIGH_FREQUENCY_CLICK_QUEUE.put(type, 0);
      return previous == null ? 0 : previous;
   }

   public static void recordDispatch(ComboType type) {
      lastDispatchedEngineTick = engineTick;
      lastDispatchedType = type;
      if (localPlayerDispatcher != null) {
         ComboNode node = localPlayerDispatcher.getCurrentNode();
         dispatchedNodeId = node != null ? (long)node.getId() : -1L;
      }
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (Minecraft.m_91087_().f_91080_ == null) {
         if (Minecraft.m_91087_().f_91074_ != null) {
            if (event.phase == Phase.START) {
               if (DOPPEL_CONTROL_HELPER.isDown()
                  && isDmcComboSkillActive()
                  && DMCKeyMappings.DOPPEL_CONTROL.getKey().equals(Minecraft.m_91087_().f_91066_.f_92093_.getKey())) {
                  Minecraft.m_91087_().f_91066_.f_92093_.m_7249_(false);
               }
            } else if (updateClientLifecycleSnapshot()) {
               handleWeaponSwitchInput();
               InputClock.beginTick(engineTick);
               checkAndResetLockOnDeath();
               boolean doppelHeld = ComboInputSampler.isRawKeyDown(DMCKeyMappings.DOPPEL_CONTROL);
               boolean doppelSameAsSDT = DMCKeyMappings.DOPPEL_CONTROL.getKey().equals(DMCKeyMappings.SDT_CHARGE.getKey());
               boolean doppelAllowed = isDoppelgangerControlAllowed();
               if (!doppelAllowed) {
                  DOPPEL_CONTROL_HELPER.reset();
               }

               if (doppelAllowed && !doppelControlPressClaimedBySdt && hasDoppelganger()) {
                  if (doppelSameAsSDT) {
                     DOPPEL_CONTROL_HELPER.tick(
                        doppelHeld, null, () -> DMCNetwork.sendToServer(new CPDoppelgangerControl(CPDoppelgangerControl.Action.EXECUTE_SCRIPT))
                     );
                  } else {
                     DOPPEL_CONTROL_HELPER.tick(
                        doppelHeld,
                        () -> DMCNetwork.sendToServer(new CPDoppelgangerControl(CPDoppelgangerControl.Action.DISCARD)),
                        () -> DMCNetwork.sendToServer(new CPDoppelgangerControl(CPDoppelgangerControl.Action.EXECUTE_SCRIPT))
                     );
                  }
               } else if (doppelAllowed && !doppelControlPressClaimedBySdt && doppelSameAsSDT) {
                  DOPPEL_CONTROL_HELPER.tick(doppelHeld, null, () -> DMCNetwork.sendToServer(new CPDoppelgangerControl(CPDoppelgangerControl.Action.CREATE)));
               }

               ComboInputSampler.sampleInput();
               observeInputLeasePhysicalStates();
               syncPlayerInputState();
               boolean playerHandlesInput = shouldPlayerHandleInput();
               if (!playerHandlesInput && activeExecutors.isEmpty()) {
                  ComboInputSampler.forceClearAll();
                  if (localPlayerDispatcher != null) {
                     localPlayerDispatcher.getJudgementCutController().reset();
                     localPlayerDispatcher.getCrazyComboController().reset();
                     localPlayerDispatcher.consumeJumpBuffer();
                  }
               } else {
                  for (IComboExecutor executor : activeExecutors) {
                     executor.getDirectionTracker().tickExpiration(engineTick);
                     executor.tickJumpCancel(engineTick);
                     executor.tickHitExtend();
                  }

                  if (playerHandlesInput && localPlayerDispatcher != null) {
                     trackPlayerDirectionKeys(localPlayerDispatcher.getDirectionTracker());
                  } else {
                     if (localPlayerDispatcher != null) {
                        localPlayerDispatcher.getDirectionTracker().clear();
                     }

                     prevStatesStale = true;
                  }

                  ComboNode playerCurrentNode = localPlayerDispatcher != null ? localPlayerDispatcher.getCurrentNode() : null;
                  DirectionTracker playerTracker = localPlayerDispatcher != null ? localPlayerDispatcher.getDirectionTracker() : new DirectionTracker();
                  LocalPlayerPatch localPlayerPatch = localPlayerDispatcher != null ? localPlayerDispatcher.getExecutorPatch() : null;
                  boolean suppressKey13Composite = localPlayerPatch != null
                     && !localPlayerPatch.getEntityState().canBasicAttack()
                     && JudgementCutAnimationHelper.isPlayingJudgementCutAnimation(localPlayerPatch);
                  List<ComboIntentResolver.ComboInputIntent> intents = ComboIntentResolver.detectPressRelease(
                     ComboInputSampler.INPUT_STATES,
                     ComboInputSampler.STATES_TO_TYPE,
                     engineTick,
                     playerCurrentNode,
                     playerTracker,
                     localPlayerDispatcher != null && localPlayerDispatcher.isReserved((ComboType)ComboType.ENUM_MANAGER.universalValues().iterator().next()),
                     suppressKey13Composite
                  );
                  if (playerHandlesInput && playerCurrentNode != null) {
                     List<ComboIntentResolver.ComboInputIntent> toRemove = new ArrayList<>();

                     for (ComboIntentResolver.ComboInputIntent intent : intents) {
                        List<ComboType> subs = intent.type().getSubTypes();
                        if (!subs.isEmpty() && ComboRoutePlanner.getNextNode(playerCurrentNode, intent.type()) != null) {
                           for (ComboIntentResolver.ComboInputIntent other : intents) {
                              if (subs.contains(other.type())) {
                                 toRemove.add(other);
                              }
                           }
                        }
                     }

                     intents.removeAll(toRemove);
                  }

                  if (localPlayerDispatcher != null) {
                     localPlayerDispatcher.getSummonedSwordController().interceptSwordInputs(intents, localPlayerDispatcher);
                  }

                  int key1Clicks = HIGH_FREQUENCY_CLICK_QUEUE.getOrDefault(ComboNode.ComboTypes.KEY_1, 0);
                  if (playerHandlesInput) {
                     tickDoppelCcControllers(key1Clicks);
                  }

                  ClientCrazyComboController cc = localPlayerDispatcher != null ? localPlayerDispatcher.getCrazyComboController() : null;
                  if (cc != null && cc.isAcceptingCrazyComboInput()) {
                     HIGH_FREQUENCY_CLICK_QUEUE.put(ComboNode.ComboTypes.KEY_1, key1Clicks);
                  } else {
                     HIGH_FREQUENCY_CLICK_QUEUE.put(ComboNode.ComboTypes.KEY_1, 0);
                  }

                  if (playerHandlesInput && localPlayerDispatcher != null) {
                     localPlayerDispatcher.getJudgementCutController().onTick(localPlayerDispatcher);
                     localPlayerDispatcher.getCrazyComboController().onTick(localPlayerDispatcher);
                     localPlayerDispatcher.getSummonedSwordController().onTick(localPlayerDispatcher);
                     checkTapHold(getLocalPlayerPatch());
                     checkDoppelTapHold();
                  }

                  engineTick++;
                  ClientCrazyComboController ccController = localPlayerDispatcher != null ? localPlayerDispatcher.getCrazyComboController() : null;
                  if (ccController != null && ccController.isBlockingComboDispatch()) {
                     if (!intents.isEmpty()) {
                        DMCLog.info(
                           DMCLog.Category.COMBO_ENGINE,
                           "[CCInputRoute] BLOCK state={} intents={} reason=cc_exclusive",
                           ccController.getCurrentState(),
                           intents.size()
                        );
                     }

                     for (ComboIntentResolver.ComboInputIntent intentx : intents) {
                        ccController.captureFollowupIntent(intentx);
                        CPPlayerInputEvent.send(intentx);
                     }

                     for (IComboExecutor executor : activeExecutors) {
                        executor.clearReserve();
                     }
                  } else {
                     for (ComboIntentResolver.ComboInputIntent intentx : intents) {
                        for (IComboExecutor executor : activeExecutors) {
                           executor.dispatchIntent(intentx);
                        }
                     }

                     for (IComboExecutor executor : activeExecutors) {
                        executor.tickReserve();
                     }

                     if (playerHandlesInput) {
                        checkServerNodeFailure();
                     }
                  }
               }
            }
         }
      }
   }

   private static void checkAndResetLockOnDeath() {
      LocalPlayer player = Minecraft.m_91087_().f_91074_;
      if (player != null) {
         boolean deadNow = player.m_21224_();
         if (deadNow && (holdLockOnActive || lockOnInitiatedByUs)) {
            EpicFightCameraAPI api = EpicFightCameraAPI.getInstance();
            if (api.isLockingOnTarget()) {
               api.setLockOn(false);
            }

            holdLockOnActive = false;
            lockOnInitiatedByUs = false;
            lastLockOnTargetId = -1;
         } else if (!deadNow && playerWasDead) {
            EpicFightCameraAPI api = EpicFightCameraAPI.getInstance();
            if (api.isLockingOnTarget()) {
               api.setLockOn(false);
            }

            holdLockOnActive = false;
            lockOnInitiatedByUs = false;
            lastLockOnTargetId = -1;
         }

         playerWasDead = deadNow;
      }
   }

   public static boolean shouldPlayerHandleInput() {
      Minecraft minecraft = Minecraft.m_91087_();
      if (minecraft.f_91080_ == null && !minecraft.m_91104_()) {
         LocalPlayerPatch playerPatch = getLocalPlayerPatch();
         if (playerPatch == null) {
            return false;
         } else {
            ItemStack mainHandItem = ((LocalPlayer)playerPatch.getOriginal()).m_21205_();
            return EpicFightCapabilities.getItemCapability(mainHandItem)
               .map(cap -> cap.getInnateSkill(playerPatch, mainHandItem) instanceof ComboBasicAttack)
               .orElse(false);
         }
      } else {
         return false;
      }
   }

   private static void trackPlayerDirectionKeys(DirectionTracker tracker) {
      Options options = Minecraft.m_91087_().f_91066_;
      boolean curUp = ComboInputSampler.isRawKeyDown(options.f_92085_);
      boolean curDown = ComboInputSampler.isRawKeyDown(options.f_92087_);
      boolean curLeft = ComboInputSampler.isRawKeyDown(options.f_92086_);
      boolean curRight = ComboInputSampler.isRawKeyDown(options.f_92088_);
      if (prevStatesStale) {
         tracker.clear();
         prevStatesStale = false;
      }

      tracker.update(curUp, curDown, curLeft, curRight, engineTick);
   }

   private static boolean updateClientLifecycleSnapshot() {
      Minecraft minecraft = Minecraft.m_91087_();
      LocalPlayer player = minecraft.f_91074_;
      if (player != null && minecraft.f_91073_ != null) {
         ResourceKey<Level> dimension = minecraft.f_91073_.m_46472_();
         if (inputSession.update(player, dimension)) {
            resetForPlayerStateChange();
            LocalPlayerPatch lpp = getLocalPlayerPatch();
            if (lpp != null) {
               localPlayerDispatcher = new ComboExecutionDispatcher(lpp);
               registerExecutor(localPlayerDispatcher);
            }
         }

         return true;
      } else {
         if (inputSession.isActive()) {
            resetForPlayerStateChange();
         }

         inputSession.clear();
         return false;
      }
   }

   public static void resetForPlayerStateChange() {
      ComboInputSampler.forceClearAll();
      if (localPlayerDispatcher != null) {
         localPlayerDispatcher.clearReserve();
         localPlayerDispatcher.consumeJumpBuffer();
      }

      activeExecutors.clear();
      localPlayerDispatcher = null;
      DOPPEL_TAP_HOLD_CHARGE_START.clear();
      ComboIntentResolver.resetTimestamps();
      holdLockOnActive = false;
      lockOnInitiatedByUs = false;
      lastLockOnTargetId = -1;
      playerWasDead = false;
      suppressNextInputEvent = false;
      doppelControlPressClaimedBySdt = false;
      resetInputSync();
      currentInputFrame = new InputFrame(engineTick, inputSyncSequence, InputClock.nowMillis(), (short)0);
      InputClock.reset();
      InputLeaseManager.clear();
   }

   public static void resetForWeaponStateChange(DmcWeaponType activeWeapon) {
      boolean preserveCrazyCombo = localPlayerDispatcher != null && localPlayerDispatcher.getCrazyComboController().isTrackingActionChain();
      boolean handoffHeldKey1ToYamato = activeWeapon == DmcWeaponType.YAMATO && ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY1) && !preserveCrazyCombo;
      DOPPEL_CONTROL_HELPER.reset();
      doppelControlPressClaimedBySdt = false;
      if (preserveCrazyCombo) {
         ComboIntentResolver.resetTimestamps();
         HIGH_FREQUENCY_CLICK_QUEUE.clear();
         prevStatesStale = true;
         localPlayerDispatcher.clearReserve();
         localPlayerDispatcher.getDirectionTracker().clear();
         localPlayerDispatcher.getJudgementCutController().reset();
         if (!preserveCrazyCombo) {
            localPlayerDispatcher.getCrazyComboController().reset();
         }
      } else {
         ComboInputSampler.forceClearAll();
         ComboIntentResolver.resetTimestamps();
         HIGH_FREQUENCY_CLICK_QUEUE.clear();
         prevStatesStale = true;
         resetInputSync();
         LocalPlayerPatch playerPatch = getLocalPlayerPatch();
         if (localPlayerDispatcher != null) {
            localPlayerDispatcher.clearReserve();
            localPlayerDispatcher.getDirectionTracker().clear();
            localPlayerDispatcher.getJudgementCutController().reset();
            localPlayerDispatcher.getCrazyComboController().reset();
            activeExecutors.remove(localPlayerDispatcher);
            localPlayerDispatcher = null;
         }

         InputLeaseManager.clear();
         if (playerPatch != null) {
            localPlayerDispatcher = new ComboExecutionDispatcher(playerPatch);
            registerExecutor(localPlayerDispatcher);
         }

         if (handoffHeldKey1ToYamato) {
            ComboInputSampler.forceSetPressed(ComboNode.ComboTypes.KEY_1);
            DMCLog.info(DMCLog.Category.COMBO_ENGINE, "[WeaponInputHandoff] KEY_1 held state transferred to YAMATO judgement-cut charge");
         }
      }
   }

   private static void resetAllInputOnScreenOpen() {
      ComboInputSampler.forceClearAll();
      ComboIntentResolver.resetTimestamps();
      DOPPEL_CONTROL_HELPER.reset();
      doppelControlPressClaimedBySdt = false;
      holdLockOnActive = false;
      lockOnInitiatedByUs = false;
      lastLockOnTargetId = -1;
      Options opts = Minecraft.m_91087_().f_91066_;
      ComboInputSampler.setRawKeyState(opts.f_92085_, false);
      ComboInputSampler.setRawKeyState(opts.f_92087_, false);
      ComboInputSampler.setRawKeyState(opts.f_92086_, false);
      ComboInputSampler.setRawKeyState(opts.f_92088_, false);
      ComboInputSampler.setRawKeyState(opts.f_92089_, false);
      ComboInputSampler.setRawKeyState(opts.f_92091_, false);
      ComboInputSampler.setRawKeyState(opts.f_92090_, false);
      ComboInputSampler.setRawKeyState(DMCKeyMappings.KEY1, false);
      ComboInputSampler.setRawKeyState(DMCKeyMappings.KEY2, false);
      ComboInputSampler.setRawKeyState(DMCKeyMappings.KEY3, false);
      ComboInputSampler.setRawKeyState(DMCKeyMappings.KEY4, false);
      ComboInputSampler.setRawKeyState(EpicFightKeyMappings.WEAPON_INNATE_SKILL, false);
      prevStatesStale = true;
      resetInputSync();

      for (IComboExecutor executor : activeExecutors) {
         executor.getDirectionTracker().clear();
         executor.clearReserve();
         executor.consumeJumpBuffer();
      }

      if (localPlayerDispatcher != null) {
         localPlayerDispatcher.getDirectionTracker().clear();
         localPlayerDispatcher.clearReserve();
         localPlayerDispatcher.consumeJumpBuffer();
         localPlayerDispatcher.getJudgementCutController().reset();
         localPlayerDispatcher.getCrazyComboController().reset();
      }

      DMCLog.info(DMCLog.Category.COMBO_ENGINE, "[ComboEngine] Screen opened — global input state reset");
   }

   private static void resetAllInputOnScreenClose() {
      ComboInputSampler.forceClearAll();
      DOPPEL_CONTROL_HELPER.reset();
      doppelControlPressClaimedBySdt = false;
      Options opts = Minecraft.m_91087_().f_91066_;
      ComboInputSampler.setRawKeyState(opts.f_92085_, false);
      ComboInputSampler.setRawKeyState(opts.f_92087_, false);
      ComboInputSampler.setRawKeyState(opts.f_92086_, false);
      ComboInputSampler.setRawKeyState(opts.f_92088_, false);
      ComboInputSampler.setRawKeyState(opts.f_92089_, false);
      ComboInputSampler.setRawKeyState(opts.f_92091_, false);
      ComboInputSampler.setRawKeyState(opts.f_92090_, false);
      resetInputSync();
      prevStatesStale = true;

      for (IComboExecutor executor : activeExecutors) {
         executor.getDirectionTracker().clear();
         executor.clearReserve();
         executor.consumeJumpBuffer();
      }

      if (localPlayerDispatcher != null) {
         localPlayerDispatcher.getDirectionTracker().clear();
         localPlayerDispatcher.clearReserve();
         localPlayerDispatcher.consumeJumpBuffer();
      }

      ComboIntentResolver.resetTimestamps();
      holdLockOnActive = false;
      lockOnInitiatedByUs = false;
      DMCLog.info(DMCLog.Category.COMBO_ENGINE, "[ComboEngine] Screen closed — aggressive input state reset");
   }

   private static void tickDoppelCcControllers(int key1Clicks) {
      LocalPlayer player = Minecraft.m_91087_().f_91074_;
      if (player != null) {
         DoppelgangerEntity doppel = DoppelgangerCapability.getCachedDoppel(player);
         if (doppel != null && doppel.m_6084_()) {
            ComboNode root = Yamato.YAMATO instanceof ComboBasicAttack yamatoSkill ? yamatoSkill.getRoot() : null;
            if (root != null) {
               if (doppel.isCcMode()) {
                  DoppelgangerCrazyComboController dcc = doppelCcControllers.computeIfAbsent(
                     doppel.m_19879_(), id -> new DoppelgangerCrazyComboController(id, root)
                  );
                  dcc.onTick(key1Clicks);
               } else {
                  DoppelgangerCrazyComboController removed = doppelCcControllers.remove(doppel.m_19879_());
                  if (removed != null) {
                     removed.reset();
                  }
               }
            }
         } else {
            doppelCcControllers.clear();
         }
      }
   }

   public static void resetCrazyComboForPlayer() {
      if (localPlayerDispatcher != null) {
         localPlayerDispatcher.resetCrazyCombo();
      }

      DMCNetwork.sendToServer(new CPCrazyComboReset());
   }

   public static void resetCrazyComboForAll() {
      if (localPlayerDispatcher != null) {
         localPlayerDispatcher.resetCrazyCombo();
      }

      for (IComboExecutor executor : activeExecutors) {
         if (executor != localPlayerDispatcher) {
            executor.resetCrazyCombo();
         }
      }

      DMCNetwork.sendToServer(new CPCrazyComboReset());
   }

   @Nullable
   public static LocalPlayerPatch getLocalPlayerPatch() {
      LocalPlayer localPlayer = Minecraft.m_91087_().f_91074_;
      return localPlayer == null ? null : EpicFightCapabilities.getLocalPlayerPatch(localPlayer);
   }

   @SubscribeEvent
   public static void onScreenOpen(Opening event) {
      resetAllInputOnScreenOpen();
   }

   @SubscribeEvent
   public static void onScreenClose(Closing event) {
      resetAllInputOnScreenClose();
      suppressNextInputEvent = true;
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onKeyInput(Key event) {
      if (event.getAction() != 2) {
         com.mojang.blaze3d.platform.InputConstants.Key ik = InputConstants.m_84827_(event.getKey(), event.getScanCode());
         int action = event.getAction();
         captureSharedDoppelPressOwner(ik, action);
         syncPlayerInputState();
         if (suppressNextInputEvent) {
            suppressNextInputEvent = false;
            ComboInputSampler.forceClearAll();
         } else if (Minecraft.m_91087_().f_91080_ == null && Minecraft.m_91087_().f_91074_ != null) {
            LocalPlayerPatch lpp = getLocalPlayerPatch();
            if (lpp != null) {
               switch (InputActionArbiter.resolve(ik)) {
                  case DOPPELGANGER_CONTROL:
                     handleDoppelControl(ik, action);
                     break;
                  case DOPPELGANGER_SPEED:
                     handleDoppelSpeed(ik, action);
                     break;
                  case DOPPELGANGER_DISCARD:
                     handleDoppelDiscard(ik, action);
                     break;
                  case LOCK_ON:
                     handleLockOn(ik, action);
                  case NONE:
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onMouseInput(MouseButton event) {
      if (event.getAction() != 2) {
         com.mojang.blaze3d.platform.InputConstants.Key ik = Type.MOUSE.m_84895_(event.getButton());
         int action = event.getAction();
         captureSharedDoppelPressOwner(ik, action);
         syncPlayerInputState();
         if (suppressNextInputEvent) {
            suppressNextInputEvent = false;
            ComboInputSampler.forceClearAll();
         } else if (Minecraft.m_91087_().f_91080_ == null && Minecraft.m_91087_().f_91074_ != null) {
            LocalPlayerPatch lpp = getLocalPlayerPatch();
            if (lpp != null) {
               switch (InputActionArbiter.resolve(ik)) {
                  case DOPPELGANGER_CONTROL:
                     handleDoppelControl(ik, action);
                     break;
                  case DOPPELGANGER_SPEED:
                     handleDoppelSpeed(ik, action);
                     break;
                  case DOPPELGANGER_DISCARD:
                     handleDoppelDiscard(ik, action);
                     break;
                  case LOCK_ON:
                     handleLockOn(ik, action);
                  case NONE:
               }
            }
         }
      }
   }

   private static void syncPlayerInputState() {
      short mask = buildInputMask();
      currentInputFrame = new InputFrame(InputClock.currentTick(), ++inputSyncSequence, InputClock.nowMillis(), mask);
      syncPlayerInputState(currentInputFrame);
   }

   private static void syncPlayerInputState(InputFrame frame) {
      short mask = frame.mask();
      PlayerInputState.updateLocal(mask);
      short prev = lastSentInputMask;

      for (int i = 0; i < 4; i++) {
         if (!isMaskBit(prev, 9 + i) && isMaskBit(mask, 9 + i)) {
            ComboType type = ComboInputSampler.STATES_TO_TYPE[i];
            HIGH_FREQUENCY_CLICK_QUEUE.merge(type, 1, Integer::sum);
         }
      }

      if (mask != lastSentInputMask && Minecraft.m_91087_().m_91403_() != null) {
         DMCNetwork.sendToServer(new CPPlayerInputSync(mask, frame.sequence()));
         lastSentInputMask = mask;
      }
   }

   public static InputFrame getCurrentInputFrame() {
      return currentInputFrame;
   }

   private static void observeInputLeasePhysicalStates() {
      InputLeaseManager.observePhysicalState(0, ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY1));
      InputLeaseManager.observePhysicalState(1, ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY2));
      InputLeaseManager.observePhysicalState(2, ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY3));
      InputLeaseManager.observePhysicalState(3, ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY4));
      InputLeaseManager.observePhysicalState(4, ComboInputSampler.isRawKeyDown(EpicFightKeyMappings.WEAPON_INNATE_SKILL));
   }

   public static void resetInputSync() {
      lastSentInputMask = -1;
      PlayerInputState.updateLocal((short)0);
   }

   private static short buildInputMask() {
      Options options = Minecraft.m_91087_().f_91066_;
      short mask = 0;
      if (ComboInputSampler.isRawKeyDown(options.f_92085_)) {
         mask = (short)(mask | 1);
      }

      if (ComboInputSampler.isRawKeyDown(options.f_92087_)) {
         mask = (short)(mask | 2);
      }

      if (ComboInputSampler.isRawKeyDown(options.f_92086_)) {
         mask = (short)(mask | 4);
      }

      if (ComboInputSampler.isRawKeyDown(options.f_92088_)) {
         mask = (short)(mask | 8);
      }

      if (ComboInputSampler.isRawKeyDown(options.f_92089_)) {
         mask = (short)(mask | 16);
      }

      if (ComboInputSampler.isRawKeyDown(options.f_92091_)) {
         mask = (short)(mask | 32);
      }

      if (ComboInputSampler.isRawKeyDown(options.f_92090_)) {
         mask = (short)(mask | 64);
      }

      if (ComboInputSampler.isRawKeyDown(DMCKeyMappings.DMC_LOCK_ON)) {
         mask = (short)(mask | 128);
      }

      if (ComboInputSampler.isRawKeyDown(DMCKeyMappings.SDT_CHARGE)) {
         mask = (short)(mask | 256);
      }

      if (ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY1)) {
         mask = (short)(mask | 512);
      }

      if (ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY2)) {
         mask = (short)(mask | 1024);
      }

      if (ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY3)) {
         mask = (short)(mask | 2048);
      }

      if (ComboInputSampler.isRawKeyDown(DMCKeyMappings.KEY4)) {
         mask = (short)(mask | 4096);
      }

      if (ComboInputSampler.isRawKeyDown(EpicFightKeyMappings.WEAPON_INNATE_SKILL)) {
         mask = (short)(mask | 8192);
      }

      return mask;
   }

   private static boolean isMaskBit(short mask, int bit) {
      return (mask & 1 << bit) != 0;
   }

   private static boolean handleDoppelControl(com.mojang.blaze3d.platform.InputConstants.Key ik, int action) {
      if (!DMCKeyMappings.DOPPEL_CONTROL.isActiveAndMatches(ik)) {
         return false;
      } else if (doppelControlPressClaimedBySdt) {
         DOPPEL_CONTROL_HELPER.reset();
         if (action == 0) {
            doppelControlPressClaimedBySdt = false;
         }

         return false;
      } else if (!isDoppelgangerControlAllowed()) {
         DOPPEL_CONTROL_HELPER.reset();
         return false;
      } else {
         if (action == 1) {
            if (hasDoppelganger()) {
               DOPPEL_CONTROL_HELPER.press();
            } else {
               boolean sameKeyAsSDT = DMCKeyMappings.DOPPEL_CONTROL.getKey().equals(DMCKeyMappings.SDT_CHARGE.getKey());
               if (sameKeyAsSDT) {
                  DOPPEL_CONTROL_HELPER.press();
               } else {
                  DMCNetwork.sendToServer(new CPDoppelgangerControl(CPDoppelgangerControl.Action.CREATE));
               }
            }
         }

         return false;
      }
   }

   private static void captureSharedDoppelPressOwner(com.mojang.blaze3d.platform.InputConstants.Key key, int action) {
      if (action == 1
         && DMCKeyMappings.DOPPEL_CONTROL.isActiveAndMatches(key)
         && DMCKeyMappings.DOPPEL_CONTROL.getKey().equals(DMCKeyMappings.SDT_CHARGE.getKey())) {
         doppelControlPressClaimedBySdt = isPlayerInSDT();
         if (doppelControlPressClaimedBySdt) {
            DOPPEL_CONTROL_HELPER.reset();
         }
      }
   }

   private static boolean handleDoppelSpeed(com.mojang.blaze3d.platform.InputConstants.Key ik, int action) {
      if (action != 1) {
         return false;
      } else {
         int mode = -1;
         if (DMCKeyMappings.DOPPEL_FAST.isActiveAndMatches(ik)) {
            mode = 0;
         } else if (DMCKeyMappings.DOPPEL_MEDIUM.isActiveAndMatches(ik)) {
            mode = 1;
         } else if (DMCKeyMappings.DOPPEL_SLOW.isActiveAndMatches(ik)) {
            mode = 2;
         }

         if (mode < 0) {
            return false;
         } else {
            if (!hasDoppelganger()) {
               DMCNetwork.sendToServer(new CPDoppelgangerControl(CPDoppelgangerControl.Action.CREATE, mode));
            } else {
               DMCNetwork.sendToServer(new CPDoppelgangerDelayMode(mode));
               if (Minecraft.m_91087_().f_91074_ != null) {
                  Minecraft.m_91087_().f_91074_.m_216990_((SoundEvent)DMCSounds.DOPPELGANGER_SWITCH.get());
               }
            }

            return true;
         }
      }
   }

   private static boolean handleDoppelDiscard(com.mojang.blaze3d.platform.InputConstants.Key ik, int action) {
      if (action != 1) {
         return false;
      } else if (!DMCKeyMappings.DOPPEL_DISCARD.isActiveAndMatches(ik)) {
         return false;
      } else {
         if (!hasDoppelganger()) {
            DMCNetwork.sendToServer(new CPDoppelgangerControl(CPDoppelgangerControl.Action.CREATE, 1));
         } else {
            DMCNetwork.sendToServer(new CPDoppelgangerControl(CPDoppelgangerControl.Action.DISCARD));
         }

         return true;
      }
   }

   private static void handleLockOn(com.mojang.blaze3d.platform.InputConstants.Key ik, int action) {
      if (isYamatoSkillActive()) {
         if (DMCKeyMappings.DMC_LOCK_ON.isActiveAndMatches(ik)) {
            if (action == 1) {
               EpicFightCameraAPI api = EpicFightCameraAPI.getInstance();
               boolean wasAlreadyLocking = api.isLockingOnTarget();
               if (!wasAlreadyLocking) {
                  if (lastLockOnTargetId >= 0 && api.getFocusingEntity() == null) {
                     LivingEntity prevTarget = tryGetValidEntity(lastLockOnTargetId);
                     if (prevTarget != null) {
                        ((IEpicFightCameraAPI)api).dmc$forceSetFocusingEntity(prevTarget);
                        DMCLog.info(DMCLog.Category.COMBO_ENGINE, "[LockOn] Resumed last target id={}", lastLockOnTargetId);
                     }
                  }

                  api.setLockOn(true);
                  if (api.isLockingOnTarget() && api.getFocusingEntity() == null) {
                     api.setLockOn(false);
                     DMCLog.warn(DMCLog.Category.COMBO_ENGINE, "[LockOn] No valid target found, lock-on reverted");
                  }

                  lockOnInitiatedByUs = true;
               } else if (CameraLockUtil.handOffForcedLockOn()) {
                  lockOnInitiatedByUs = true;
               }

               holdLockOnActive = true;
            } else if (action == 0) {
               if (lockOnInitiatedByUs) {
                  EpicFightCameraAPI api = EpicFightCameraAPI.getInstance();
                  LivingEntity current = api.getFocusingEntity();
                  lastLockOnTargetId = current != null ? current.m_19879_() : -1;
                  api.setLockOn(false);
               }

               holdLockOnActive = false;
               lockOnInitiatedByUs = false;
            }
         }
      }
   }

   private static void handleDodgeKey(com.mojang.blaze3d.platform.InputConstants.Key ik, int action, LocalPlayerPatch lpp) {
      if (isYamatoSkillActive()) {
         while (EpicFightKeyMappings.DODGE.m_90859_()) {
         }

         if (DMCKeyMappings.DMC_DODGE.isActiveAndMatches(ik) && action == 1) {
            KeyMapping pickItem = Minecraft.m_91087_().f_91066_.f_92097_;

            while (pickItem.m_90859_()) {
            }

            SkillContainer dodgeContainer = lpp.getSkill(SkillSlots.DODGE);
            if (!dodgeContainer.isEmpty() && dodgeContainer.getSkill() instanceof VergilDodgeSkill) {
               Object packet = dodgeContainer.getSkill().getExecutionPacket(dodgeContainer, null);
               if (packet != null) {
                  if (lpp.getEntityState().canBasicAttack()) {
                     EpicFightNetworkManager.sendToServer(packet);
                  } else if (isInDodgeAnimation(lpp)) {
                     if (localPlayerDispatcher instanceof ComboExecutionDispatcher ced) {
                        int duration = 10;
                        if (dodgeContainer.getSkill() instanceof VergilDodgeSkill yds && yds.getDodgeBufferDurationTicks() >= 0) {
                           duration = yds.getDodgeBufferDurationTicks();
                        }

                        ced.bufferDodge(packet, duration);
                     }
                  } else {
                     EpicFightNetworkManager.sendToServer(packet);
                  }
               }
            }
         }
      }
   }

   private static boolean isInDodgeAnimation(LocalPlayerPatch lpp) {
      DynamicAnimation currentAnim = JudgementCutAnimationHelper.getCurrentAnimation(lpp);
      if (currentAnim == null) {
         return false;
      } else {
         if (lpp.getSkill(SkillSlots.DODGE).getSkill() instanceof VergilDodgeSkill yds && yds.isDodgeAnimation(currentAnim)) {
            return true;
         }

         return false;
      }
   }

   private static void checkTapHold(LocalPlayerPatch lpp) {
      if (lpp != null) {
         ClientCrazyComboController cc = localPlayerDispatcher != null ? localPlayerDispatcher.getCrazyComboController() : null;
         if (cc == null || cc.getCurrentState() == CrazyComboSession.Stage.IDLE) {
            DMCPlayer ip = DMCPlayerCapabilityProvider.get((Player)lpp.getOriginal());
            int keyIndex = ip.getComboKeyIndex();
            ComboType holdKeyType = getComboTypeByKeyIndex(keyIndex);
            if (holdKeyType != null) {
               if (!ComboInputSampler.isPressed(holdKeyType)) {
                  ip.setActiveTapHoldNode(null);
               } else {
                  ITapHoldNode activeTH = ip.getActiveTapHoldNode();
                  if (activeTH != null) {
                     int heldTicks = ComboInputSampler.getPressDuration(holdKeyType, false);
                     int requiredTicks = activeTH.getWindupDurationTicks();
                     if (heldTicks >= requiredTicks - 1) {
                        ip.setActiveTapHoldNode(null);
                        DMCNetwork.sendToServer(new CPTapHoldTrigger());
                        triggerDoppelTapHoldIfActive((LocalPlayer)lpp.getOriginal());
                     }
                  }
               }
            }
         }
      }
   }

   private static void triggerDoppelTapHoldIfActive(LocalPlayer player) {
      DoppelgangerEntity doppel = DoppelgangerCapability.getCachedDoppel(player);
      if (doppel != null && doppel.m_6084_() && doppel.isTapHoldActive()) {
         int keyIndex = doppel.getTapHoldKeyIndex();
         ComboType holdKeyType = getComboTypeByKeyIndex(keyIndex);
         if (holdKeyType != null) {
            int physicalHeld = ComboInputSampler.getPressDuration(holdKeyType, false);
            int requiredTicks = doppel.getTapHoldWindupTicks();
            if (physicalHeld >= requiredTicks - 1) {
               DOPPEL_TAP_HOLD_CHARGE_START.remove(doppel.m_20148_());
               DMCLog.info(
                  DMCLog.Category.COMBO_ENGINE,
                  "[TapHold-DoppelClient] HOLD_TRIGGER_VIA_PLAYER uuid={} eng={} physicalHeld={} required={}",
                  doppel.m_20148_(),
                  engineTick,
                  physicalHeld,
                  requiredTicks
               );
               DMCNetwork.sendToServer(new CPDoppelgangerTapHold(true));
            }
         }
      }
   }

   @Nullable
   private static ComboType getComboTypeByKeyIndex(int keyIndex) {
      return switch (keyIndex) {
         case 0 -> ComboNode.ComboTypes.KEY_1;
         case 1 -> ComboNode.ComboTypes.KEY_2;
         case 2 -> ComboNode.ComboTypes.KEY_3;
         case 3 -> ComboNode.ComboTypes.KEY_4;
         case 4 -> ComboNode.ComboTypes.WEAPON_INNATE;
         default -> null;
      };
   }

   private static void checkDoppelTapHold() {
      Minecraft mc = Minecraft.m_91087_();
      if (mc.f_91074_ != null) {
         DoppelgangerEntity doppel = DoppelgangerCapability.getCachedDoppel(mc.f_91074_);
         if (doppel != null && doppel.m_6084_() && doppel.isTapHoldActive()) {
            int keyIndex = doppel.getTapHoldKeyIndex();
            ComboType holdKeyType = getComboTypeByKeyIndex(keyIndex);
            if (holdKeyType != null) {
               if (!ComboInputSampler.isPressed(holdKeyType)) {
                  DMCNetwork.sendToServer(new CPDoppelgangerTapHold(false));
               } else {
                  int physicalHeld = ComboInputSampler.getPressDuration(holdKeyType, false);
                  int delayTicks = doppel.getDoppelDelayMode() >= 0 ? DoppelgangerEntity.getDelayTicks(doppel.getDoppelDelayMode()) : 0;
                  int effectiveHeld = physicalHeld - delayTicks - 1;
                  DMCPlayer ip = DMCPlayerCapabilityProvider.get(mc.f_91074_);
                  ITapHoldNode playerTH = ip.getActiveTapHoldNode();
                  int requiredTicks = playerTH != null ? playerTH.getWindupDurationTicks() : doppel.getTapHoldWindupTicks();
                  if (effectiveHeld >= requiredTicks - 1) {
                     DMCLog.info(
                        DMCLog.Category.COMBO_ENGINE,
                        "[TapHold-DoppelClient] HOLD_TRIGGER uuid={} eng={} effectiveHeld={} physicalHeld={} required={} delay={}",
                        doppel.m_20148_(),
                        engineTick,
                        effectiveHeld,
                        physicalHeld,
                        requiredTicks,
                        delayTicks
                     );
                     DMCNetwork.sendToServer(new CPDoppelgangerTapHold(true));
                  }
               }
            }
         }
      }
   }

   public static void notifyServerNodeFailure(String message) {
      serverFailureMessage = message;
      serverFailureExpireTick = engineTick + 60L;
   }

   @Nullable
   public static String getServerFailureMessage() {
      if (serverFailureMessage != null && engineTick >= serverFailureExpireTick) {
         serverFailureMessage = null;
      }

      return serverFailureMessage;
   }

   private static void checkServerNodeFailure() {
      if (lastDispatchedEngineTick >= 0L) {
         long ticksSince = engineTick - lastDispatchedEngineTick;
         if (ticksSince >= 3L) {
            LocalPlayerPatch lpp = getLocalPlayerPatch();
            if (lpp == null) {
               clearDispatchTrace();
            } else {
               DMCPlayer ip = DMCPlayerCapabilityProvider.get((Player)lpp.getOriginal());
               ComboNode currentNode = ip.getCurrentLogicNode();
               long currentNodeId = currentNode != null ? (long)currentNode.getId() : -1L;
               int resetTicks = ip.getComboResetTicks();
               if (currentNodeId == dispatchedNodeId && currentNode != null && currentNode.getParentNode() == null && resetTicks == -1) {
                  if (serverFailureMessage == null) {
                     notifyServerNodeFailure(String.format("rootResetT=-1 nodeId=%d dispatchedType=%s", currentNodeId, lastDispatchedType));
                  }

                  clearDispatchTrace();
               } else if (currentNodeId == dispatchedNodeId && ticksSince >= 5L) {
                  if (serverFailureMessage == null) {
                     notifyServerNodeFailure(String.format("NO FEEDBACK dispatchedType=%s", lastDispatchedType));
                  }

                  clearDispatchTrace();
               } else {
                  if (currentNodeId != dispatchedNodeId) {
                     clearDispatchTrace();
                  }
               }
            }
         }
      }
   }

   private static void clearDispatchTrace() {
      lastDispatchedEngineTick = -1L;
      lastDispatchedType = null;
      dispatchedNodeId = -1L;
   }

   public static int getJumpBufferTicks() {
      IComboExecutor d = getLocalPlayerDispatcher();
      return d != null ? d.getJumpBufferTicks() : 0;
   }

   public static boolean isJumpCancelExecutable() {
      IComboExecutor d = getLocalPlayerDispatcher();
      return d != null && d.isJumpCancelExecutable();
   }

   public static void clearAllInputState() {
      ComboInputSampler.forceClearAll();
      IComboExecutor d = getLocalPlayerDispatcher();
      if (d != null) {
         d.clearReserve();
         d.consumeJumpBuffer();
      }

      ComboIntentResolver.resetTimestamps();
   }

   private static boolean hasDoppelganger() {
      Minecraft mc = Minecraft.m_91087_();
      return mc.f_91074_ != null && DoppelgangerCapability.getCachedDoppel(mc.f_91074_) != null;
   }

   private static boolean isYamatoSkillActive() {
      LocalPlayerPatch lpp = getLocalPlayerPatch();
      if (lpp == null) {
         return false;
      } else {
         SkillContainer container = lpp.getSkill(SkillSlots.WEAPON_INNATE);
         return !container.isEmpty() && container.getSkill() instanceof VergilSkill;
      }
   }

   private static boolean isDoppelgangerControlAllowed() {
      return VergilSkill.isDoppelgangerAllowed(getLocalPlayerPatch());
   }

   private static void handleWeaponSwitchInput() {
      if (DmcWeaponManager.isWeaponSwitchEnabled()) {
         Minecraft minecraft = Minecraft.m_91087_();
         if (!minecraft.m_91104_()) {
            while (DMCKeyMappings.WEAPON_SWITCH.m_90859_()) {
               if (DmcWeaponManager.isArsenalItem(minecraft.f_91074_.m_21205_())) {
                  DMCNetwork.sendToServer(
                     new CPWeaponSwitch(DmcWeaponManager.getActiveWeapon(minecraft.f_91074_).next(), isMovingDuringWeaponSwitch(minecraft.f_91074_))
                  );
               }
            }
         }
      }
   }

   private static boolean isMovingDuringWeaponSwitch(LocalPlayer player) {
      LocalPlayerPatch playerPatch = getLocalPlayerPatch();
      if (playerPatch != null) {
         LivingMotion motion = playerPatch.getCurrentLivingMotion();
         if (motion.isSame(LivingMotions.WALK) || motion.isSame(LivingMotions.RUN) || motion.isSame(LivingMotions.SNEAK)) {
            return true;
         }
      }

      return Math.abs(player.f_20900_) > 0.01F || Math.abs(player.f_20902_) > 0.01F || player.m_20184_().m_165925_() > 1.0E-4;
   }

   private static boolean isDmcComboSkillActive() {
      LocalPlayerPatch lpp = getLocalPlayerPatch();
      if (lpp == null) {
         return false;
      } else {
         SkillContainer container = lpp.getSkill(SkillSlots.WEAPON_INNATE);
         return !container.isEmpty() && container.getSkill() instanceof ComboBasicAttack;
      }
   }

   private static boolean isPlayerInSDT() {
      LocalPlayerPatch lpp = getLocalPlayerPatch();
      if (lpp == null) {
         return false;
      } else {
         SkillContainer container = lpp.getSkill(SkillSlots.WEAPON_INNATE);
         if (container.isEmpty()) {
            return false;
         } else {
            SkillDataKey<Boolean> key = (SkillDataKey<Boolean>)DMCSkillDataKeys.IS_SDT.get();
            SkillDataManager dm = container.getDataManager();
            return dm.hasData(key) && (Boolean)dm.getDataValue(key);
         }
      }
   }

   @Nullable
   private static LivingEntity tryGetValidEntity(int entityId) {
      Minecraft mc = Minecraft.m_91087_();
      if (mc.f_91073_ == null) {
         return null;
      } else {
         if (mc.f_91073_.m_6815_(entityId) instanceof LivingEntity le && le.m_6084_()) {
            return le;
         }

         return null;
      }
   }
}
