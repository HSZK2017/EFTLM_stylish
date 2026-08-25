package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboNodeManager;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.api.skill.ICrazyComboNode;
import com.dmc.invincible_dmc.api.skill.SubComboNode;
import com.dmc.invincible_dmc.api.skill.crazycombo.CrazyComboPhaseHelper;
import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.capability.weapon.DmcWeaponManager;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionSession;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionStage;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionType;
import com.dmc.invincible_dmc.client.input.DirectionTracker;
import com.dmc.invincible_dmc.conditions.DirectionalSequenceCondition;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerInputHandler;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPSkillExecutionFeedback;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPPlayCC {
   private final CPPlayCC.Type type;
   private final int loopCount;
   private final CPPlayCC.CCPlayTarget target;
   private final long actionSessionId;
   private final int requiredFinishPhaseOrder;
   private final int directionMask;
   private final List<DirectionTracker.DirectionEvent> directionEvents;
   private final long engineTick;

   public CPPlayCC(
      CPPlayCC.Type type,
      int loopCount,
      CPPlayCC.CCPlayTarget target,
      long actionSessionId,
      int requiredFinishPhaseOrder,
      int directionMask,
      List<DirectionTracker.DirectionEvent> directionEvents,
      long engineTick
   ) {
      this.type = type;
      this.loopCount = loopCount;
      this.target = target;
      this.actionSessionId = actionSessionId;
      this.requiredFinishPhaseOrder = requiredFinishPhaseOrder;
      this.directionMask = directionMask;
      this.directionEvents = directionEvents == null ? Collections.emptyList() : List.copyOf(directionEvents);
      this.engineTick = engineTick;
   }

   public CPPlayCC(CPPlayCC.Type type, int loopCount, CPPlayCC.CCPlayTarget target, long actionSessionId, int requiredFinishPhaseOrder) {
      this(type, loopCount, target, actionSessionId, requiredFinishPhaseOrder, 0, Collections.emptyList(), 0L);
   }

   public CPPlayCC(CPPlayCC.Type type, int loopCount, CPPlayCC.CCPlayTarget target, long actionSessionId) {
      this(type, loopCount, target, actionSessionId, -1);
   }

   public CPPlayCC(CPPlayCC.Type type, int loopCount, CPPlayCC.CCPlayTarget target) {
      this(type, loopCount, target, 0L);
   }

   public CPPlayCC(CPPlayCC.Type type, int loopCount) {
      this(type, loopCount, CPPlayCC.CCPlayTarget.BOTH);
   }

   public static void toBytes(CPPlayCC msg, FriendlyByteBuf buf) {
      buf.m_130068_(msg.type);
      buf.writeInt(msg.loopCount);
      buf.m_130068_(msg.target);
      buf.m_130103_(msg.actionSessionId);
      buf.writeInt(msg.requiredFinishPhaseOrder);
      buf.writeInt(msg.directionMask);
      int eventCount = Math.min(msg.directionEvents.size(), 16);
      buf.writeByte(eventCount);

      for (int i = 0; i < eventCount; i++) {
         DirectionTracker.DirectionEvent event = msg.directionEvents.get(i);
         buf.writeByte(event.direction().ordinal());
         buf.writeLong(event.tick());
      }

      buf.writeLong(msg.engineTick);
   }

   public static CPPlayCC fromBytes(FriendlyByteBuf buf) {
      CPPlayCC.Type type = (CPPlayCC.Type)buf.m_130066_(CPPlayCC.Type.class);
      int loopCount = buf.readInt();
      CPPlayCC.CCPlayTarget target = (CPPlayCC.CCPlayTarget)buf.m_130066_(CPPlayCC.CCPlayTarget.class);
      long actionSessionId = buf.m_130258_();
      int requiredFinishPhaseOrder = buf.readInt();
      int directionMask = buf.readInt();
      int eventCount = buf.readUnsignedByte();
      List<DirectionTracker.DirectionEvent> directionEvents = new ArrayList<>(eventCount);

      for (int i = 0; i < eventCount; i++) {
         int ordinal = buf.readUnsignedByte();
         long tick = buf.readLong();
         if (ordinal < DirectionalSequenceCondition.Direction.values().length) {
            directionEvents.add(new DirectionTracker.DirectionEvent(DirectionalSequenceCondition.Direction.values()[ordinal], tick));
         }
      }

      long engineTick = buf.readLong();
      return new CPPlayCC(type, loopCount, target, actionSessionId, requiredFinishPhaseOrder, directionMask, directionEvents, engineTick);
   }

   private static boolean validateRequiredFinishPhase(CPPlayCC msg, ICrazyComboNode ccNode, LivingEntityPatch<?> patch) {
      if (msg.type == CPPlayCC.Type.FINISH && msg.requiredFinishPhaseOrder >= 0) {
         int configuredStartupPhase = ccNode.getCcStartupFinishNoChasePhase();
         if (msg.loopCount == 0) {
            if (configuredStartupPhase < 0 || msg.requiredFinishPhaseOrder != configuredStartupPhase) {
               return false;
            }
         } else if (msg.requiredFinishPhaseOrder < 2) {
            return false;
         }

         SubComboNode expectedNode = msg.loopCount == 0 ? ccNode.getCcBase() : ccNode.getCcChase();
         StaticAnimation expectedAnimation = expectedNode != null && expectedNode.getAnimationAccessor() != null
            ? (StaticAnimation)expectedNode.getAnimationAccessor().get()
            : null;
         AttackAnimation expectedAttack = DMCAnimationUtils.asAnimation(expectedAnimation, AttackAnimation.class);
         if (expectedAttack != null && msg.requiredFinishPhaseOrder <= expectedAttack.phases.length) {
            StaticAnimation currentAnimation = DMCAnimationUtils.getRealAnimation(patch);
            if (DMCAnimationUtils.sameAnimation(currentAnimation, expectedAnimation)) {
               int phaseOrder = CrazyComboPhaseHelper.getCurrentPhaseOrder(patch);
               return phaseOrder >= msg.requiredFinishPhaseOrder;
            } else {
               if (patch.getOriginal() instanceof DoppelgangerEntity doppel
                  && doppel.isCcMode()
                  && ccNode instanceof ComboNode comboNode
                  && doppel.getCcNodeId() == comboNode.getId()) {
                  DMCLog.info(
                     DMCLog.Category.COMBO_SERVER,
                     "[CCServer] DOPPEL_FINISH_LATE_ACCEPT node={} loop={} required={} currentAnimation={}",
                     comboNode.getId(),
                     msg.loopCount,
                     msg.requiredFinishPhaseOrder,
                     currentAnimation != null ? currentAnimation.getRegistryName() : null
                  );
                  return true;
               }

               return false;
            }
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   private static DmcWeaponType resolveCrazyComboWeapon(ICrazyComboNode ccNode) {
      return DmcWeaponType.YAMATO;
   }

   private static ComboType resolveInputType(int inputKeyIndex) {
      return switch (inputKeyIndex) {
         case 0 -> ComboNode.ComboTypes.KEY_1;
         case 1 -> ComboNode.ComboTypes.KEY_2;
         case 2 -> ComboNode.ComboTypes.KEY_3;
         case 3 -> ComboNode.ComboTypes.KEY_4;
         default -> null;
      };
   }

   public static void handle(CPPlayCC msg, Supplier<Context> ctxSupplier) {
      Context ctx = ctxSupplier.get();
      ctx.enqueueWork(
         () -> {
            ServerPlayer sender = ctx.getSender();
            if (sender == null) {
               DMCLog.warn(DMCLog.Category.COMBO_SERVER, "[CCServer] REJECT type={} loop={} reason=no_sender", msg.type, msg.loopCount);
            } else {
               DMCLog.info(
                  DMCLog.Category.COMBO_SERVER,
                  "[CCServer] REQUEST player={} type={} loop={} target={} session={} requiredPhase={}",
                  sender.m_36316_().getName(),
                  msg.type,
                  msg.loopCount,
                  msg.target,
                  msg.actionSessionId,
                  msg.requiredFinishPhaseOrder
               );
               EpicFightCapabilities.getUnparameterizedEntityPatch(sender, ServerPlayerPatch.class)
                  .ifPresent(
                     playerPatch -> {
                        SkillContainer sc = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                        if (sc.isEmpty()) {
                           DMCLog.warn(DMCLog.Category.COMBO_SERVER, "[CCServer] REJECT player={} reason=empty_weapon_innate", sender.m_36316_().getName());
                        } else {
                           DMCPlayer ip = DMCPlayerCapabilityProvider.get(sender);
                           boolean affectsPlayer = msg.target == CPPlayCC.CCPlayTarget.PLAYER || msg.target == CPPlayCC.CCPlayTarget.BOTH;
                           WeaponActionSession actionSession = ip.getActionSession();
                           DoppelgangerPatch doppelPatch = null;
                           ICrazyComboNode ccNode;
                           if (msg.target == CPPlayCC.CCPlayTarget.DOPPEL) {
                              doppelPatch = DoppelgangerPatch.getNearestDoppelganger(sender);
                              if (doppelPatch == null) {
                                 DMCLog.warn(DMCLog.Category.COMBO_SERVER, "[CCServer] REJECT player={} reason=no_doppel_target", sender.m_36316_().getName());
                                 return;
                              }

                              int ccNodeId = ((DoppelgangerEntity)doppelPatch.getOriginal()).getCcNodeId();
                              ComboNode root = sc.getSkill() instanceof ComboBasicAttack cba ? cba.getRoot() : null;
                              if (!((root != null ? VergilSkill.findNodeById(root, ccNodeId) : null) instanceof ICrazyComboNode dcc)) {
                                 DMCLog.warn(
                                    DMCLog.Category.COMBO_SERVER,
                                    "[CCServer] REJECT player={} reason=doppel_cc_node_not_found ccNodeId={}",
                                    sender.m_36316_().getName(),
                                    ccNodeId
                                 );
                                 return;
                              }

                              ccNode = dcc;
                           } else {
                              if (actionSession == null
                                 || actionSession.actionType() != WeaponActionType.CRAZY_COMBO
                                 || actionSession.stage().isTerminal()
                                 || actionSession.sessionId() != msg.actionSessionId) {
                                 DMCLog.warn(
                                    DMCLog.Category.COMBO_SERVER,
                                    "[CCServer] REJECT player={} reason=session_mismatch session={} msgSession={} action={}",
                                    sender.m_36316_().getName(),
                                    actionSession,
                                    msg.actionSessionId,
                                    msg.type
                                 );
                                 return;
                              }

                              if (!(ComboNodeManager.get(actionSession.sourceNodeId()) instanceof ICrazyComboNode sessionCcNode)) {
                                 DMCLog.warn(
                                    DMCLog.Category.COMBO_SERVER,
                                    "[CCServer] REJECT player={} reason=session_source_not_cc nodeId={}",
                                    sender.m_36316_().getName(),
                                    actionSession.sourceNodeId()
                                 );
                                 return;
                              }

                              if (actionSession.ownerWeapon() != resolveCrazyComboWeapon(sessionCcNode)) {
                                 DMCLog.warn(
                                    DMCLog.Category.COMBO_SERVER,
                                    "[CCServer] REJECT player={} reason=session_owner_mismatch owner={} node={}",
                                    sender.m_36316_().getName(),
                                    actionSession.ownerWeapon(),
                                    actionSession.sourceNodeId()
                                 );
                                 return;
                              }

                              ccNode = sessionCcNode;
                           }

                           LivingEntityPatch<?> finishPatch = (LivingEntityPatch<?>)(msg.target == CPPlayCC.CCPlayTarget.DOPPEL ? doppelPatch : playerPatch);
                           if (!validateRequiredFinishPhase(msg, ccNode, finishPatch)) {
                              DMCLog.warn(
                                 DMCLog.Category.COMBO_SERVER,
                                 "[CCServer] REJECT player={} reason=finish_phase_gate loop={} required={} current={}",
                                 sender.m_36316_().getName(),
                                 msg.loopCount,
                                 msg.requiredFinishPhaseOrder,
                                 CrazyComboPhaseHelper.getCurrentPhaseOrder(finishPatch)
                              );
                           } else {
                              StaticAnimation targetAnim = null;
                              AnimationAccessor<? extends StaticAnimation> targetAccessor = null;
                              float convertTime = 0.0F;
                              boolean isChase = msg.type == CPPlayCC.Type.CHASE;
                              if (affectsPlayer) {
                                 if (msg.type == CPPlayCC.Type.CANCEL || msg.type == CPPlayCC.Type.RELEASE_NORMAL) {
                                    if (actionSession.stage() != WeaponActionStage.STARTUP) {
                                       DMCLog.warn(
                                          DMCLog.Category.COMBO_SERVER,
                                          "[CCServer] REJECT player={} reason=cancel_stage stage={}",
                                          sender.m_36316_().getName(),
                                          actionSession.stage()
                                       );
                                       return;
                                    }

                                    ComboNode normalFollowup = null;
                                    if (msg.type == CPPlayCC.Type.RELEASE_NORMAL) {
                                       DmcWeaponType activeWeapon = DmcWeaponManager.getActiveWeapon(sender);
                                       if (activeWeapon != actionSession.ownerWeapon()) {
                                          ComboType reroutedInput = resolveInputType(actionSession.inputKeyIndex());
                                          DMCLog.info(
                                             DMCLog.Category.COMBO_SERVER,
                                             "[CCServer] RELEASE_NORMAL_REROUTE player={} sessionWeapon={} activeWeapon={} input={} oldNode={}",
                                             sender.m_36316_().getName(),
                                             actionSession.ownerWeapon(),
                                             activeWeapon,
                                             reroutedInput,
                                             actionSession.sourceNodeId()
                                          );
                                          ip.clearCrazyComboActionSession(WeaponActionStage.CANCELLED);
                                          if (sc.getSkill() instanceof ComboBasicAttack comboBasicAttack) {
                                             comboBasicAttack.switchComboRootPreservingAction(sc);
                                          }

                                          if (reroutedInput != null) {
                                             ComboBasicAttack.executeOnServer(sender, reroutedInput, 1, 0L);
                                          }

                                          DmcWeaponManager.syncRuntimeState(sender);
                                          return;
                                       }

                                       if (!(ccNode instanceof ComboNode sourceNode)) {
                                          DMCLog.warn(
                                             DMCLog.Category.COMBO_SERVER,
                                             "[CCServer] REJECT player={} reason=invalid_release_node node={}",
                                             sender.m_36316_().getName(),
                                             ccNode instanceof ComboNode comboNode ? comboNode.getId() : null
                                          );
                                          return;
                                       }

                                       normalFollowup = sourceNode.getNext(ComboNode.ComboTypes.KEY_1);
                                    }

                                    DMCLog.info(
                                       DMCLog.Category.COMBO_SERVER,
                                       "[CCServer] {} player={} node={} followup={}",
                                       msg.type,
                                       sender.m_36316_().getName(),
                                       ccNode instanceof ComboNode comboNode ? comboNode.getId() : null,
                                       normalFollowup != null ? normalFollowup.getId() : null
                                    );
                                    if (normalFollowup != null) {
                                       long previousSessionId = actionSession.sessionId();
                                       ComboBasicAttack.executeNodeOnServer(sender, normalFollowup);
                                       WeaponActionSession nextSession = ip.getActionSession();
                                       if (nextSession != null && nextSession.sessionId() != previousSessionId) {
                                          return;
                                       }
                                    }

                                    ip.clearCrazyComboActionSession(WeaponActionStage.CANCELLED);
                                    DmcWeaponManager.syncRuntimeState(sender);
                                    return;
                                 }

                                 boolean validStage = actionSession.stage() == WeaponActionStage.STARTUP || actionSession.stage() == WeaponActionStage.LOOP;
                                 int maxChases = ccNode.getCcMaxChases(playerPatch);
                                 boolean validSequence = isChase
                                    ? msg.loopCount > 0 && msg.loopCount <= maxChases && msg.loopCount == actionSession.actionStep() + 1
                                    : true;
                                 if (!validStage || !validSequence) {
                                    DMCLog.warn(
                                       DMCLog.Category.COMBO_SERVER,
                                       "[CCServer] REJECT player={} reason=invalid_sequence type={} loop={} stage={} actionStep={} maxChases={}",
                                       sender.m_36316_().getName(),
                                       msg.type,
                                       msg.loopCount,
                                       actionSession.stage(),
                                       actionSession.actionStep(),
                                       maxChases
                                    );
                                    return;
                                 }

                                 if (!isChase && msg.loopCount != actionSession.actionStep()) {
                                    DMCLog.warn(
                                       DMCLog.Category.COMBO_SERVER,
                                       "[CCServer] FINISH_LOOP_NORMALIZE player={} clientLoop={} serverStep={} session={}",
                                       sender.m_36316_().getName(),
                                       msg.loopCount,
                                       actionSession.actionStep(),
                                       actionSession.sessionId()
                                    );
                                 }
                              }

                              int effectiveLoopCount = affectsPlayer && !isChase ? actionSession.actionStep() : msg.loopCount;
                              boolean runtimeStateChanged = false;
                              if (isChase) {
                                 SubComboNode chase = ccNode.getCcChase();
                                 if (chase != null && chase.getAnimationAccessor() != null) {
                                    targetAccessor = chase.getAnimationAccessor();
                                    targetAnim = (StaticAnimation)targetAccessor.get();
                                    convertTime = chase.getConvertTime();
                                    if (affectsPlayer) {
                                       if (!actionSession.advanceActionStep(msg.loopCount)) {
                                          DMCLog.warn(
                                             DMCLog.Category.COMBO_SERVER,
                                             "[CCServer] REJECT player={} reason=advance_action_step_failed loop={}",
                                             sender.m_36316_().getName(),
                                             msg.loopCount
                                          );
                                          return;
                                       }

                                       ip.transitionActionSession(WeaponActionStage.LOOP);
                                       runtimeStateChanged = true;
                                    }
                                 }
                              } else {
                                 ComboNode finish;
                                 if (effectiveLoopCount == 0) {
                                    finish = ccNode.getCcFinishNoChase();
                                    if (finish == null) {
                                       finish = ccNode.getCcFinish();
                                    }
                                 } else {
                                    finish = ccNode.getCcFinish();
                                    if (finish == null) {
                                       finish = ccNode.getCcFinishNoChase();
                                    }
                                 }

                                 boolean finishUsesNodeTree = finish != null && (!(finish instanceof SubComboNode) || !finish.getConditionNodes().isEmpty());
                                 if (finishUsesNodeTree) {
                                    if (affectsPlayer && sc.getSkill() instanceof ComboBasicAttack comboBasicAttack) {
                                       if (ccNode.isCcResetCombo()) {
                                          ip.clearComboStatePreservingAction();
                                       }

                                       ip.transitionActionSession(WeaponActionStage.FINISH);
                                       comboBasicAttack.executeNodeOnServer(sc, finish, 1, 0L, false, msg.directionMask, msg.directionEvents, msg.engineTick);
                                       ComboNode resolvedFinish = ip.getCurrentDataNode();
                                       if (ccNode instanceof ComboNode comboNode) {
                                          ip.setActiveCrazyComboNode(comboNode);
                                       }

                                       if (ccNode.isCcResetCombo()) {
                                          ComboNode root = comboBasicAttack.getRoot(sc);
                                          if (root != null) {
                                             ip.setCurrentLogicNode(root);
                                          }
                                       }

                                       ComboBasicAttack.sendFeedback(ip.getCurrentLogicNode(), sc, ip);
                                       runtimeStateChanged = true;
                                       DMCLog.info(
                                          DMCLog.Category.COMBO_SERVER,
                                          "[CCServer] FINISH_TREE_STATE player={} resolved={} resetCombo={} logicNode={} dataNode={} stage={}",
                                          sender.m_36316_().getName(),
                                          resolvedFinish != null ? resolvedFinish.getId() : -1,
                                          ccNode.isCcResetCombo(),
                                          ip.getCurrentLogicNode(),
                                          ip.getCurrentDataNode(),
                                          ip.getActionSession() != null ? ip.getActionSession().stage() : null
                                       );
                                    }

                                    if (msg.target == CPPlayCC.CCPlayTarget.DOPPEL && doppelPatch != null) {
                                       doppelPatch.executeComboNode(
                                          finish,
                                          DoppelgangerInputHandler.createImmediateEvent(
                                             sender, ComboNode.ComboTypes.KEY_1, 1, 0L, false, msg.directionMask, msg.directionEvents, msg.engineTick
                                          )
                                       );
                                    }

                                    DMCLog.info(
                                       DMCLog.Category.COMBO_SERVER,
                                       "[CCServer] FINISH_TREE player={} loop={} root={} dataNode={}",
                                       sender.m_36316_().getName(),
                                       effectiveLoopCount,
                                       finish.getId(),
                                       affectsPlayer ? ip.getCurrentDataNode() : null
                                    );
                                 } else if (finish != null && finish.getAnimationAccessor() != null) {
                                    targetAccessor = finish.getAnimationAccessor();
                                    targetAnim = (StaticAnimation)targetAccessor.get();
                                    convertTime = finish.getConvertTime();
                                    DMCLog.info(
                                       DMCLog.Category.COMBO_SERVER,
                                       "[CCServer] NO_CHASE_EXIT player={} session={} logicNode={} stage={}",
                                       sender.m_36316_().getName(),
                                       msg.actionSessionId,
                                       ip.getCurrentLogicNode(),
                                       ip.getActionSession() != null ? ip.getActionSession().stage() : null
                                    );
                                    return;
                                 }

                                 if (!finishUsesNodeTree && affectsPlayer && targetAnim != null) {
                                    boolean resetCombo = ccNode.isCcResetCombo();
                                    if (resetCombo) {
                                       ip.clearComboStatePreservingAction();
                                    }

                                    ip.transitionActionSession(WeaponActionStage.FINISH);
                                    runtimeStateChanged = true;
                                    if (ccNode instanceof ComboNode comboNode) {
                                       ip.setActiveCrazyComboNode(comboNode);
                                    }

                                    if (finish != null) {
                                       ip.setCurrentDataNode(finish);
                                    }

                                    if (resetCombo) {
                                       ComboNode root = sc.getSkill() instanceof ComboBasicAttack cba ? cba.getRoot(sc) : null;
                                       if (root != null) {
                                          ip.setCurrentLogicNode(root);
                                       }
                                    }

                                    SPSkillExecutionFeedback feedback = SPSkillExecutionFeedback.executed(sc.getSlotId());
                                    CompoundTag feedbackTag = ip.saveNBTData(new CompoundTag());
                                    feedback.getBuffer().m_130079_(feedbackTag);
                                    EpicFightNetworkManager.sendToPlayer(feedback, sender, new Object[0]);
                                    DMCLog.info(
                                       DMCLog.Category.COMBO_SERVER,
                                       "[CCServer] FINISH_ACCEPT player={} loop={} animation={} resetCombo={} logicNode={} dataNode={}",
                                       sender.m_36316_().getName(),
                                       effectiveLoopCount,
                                       targetAccessor != null ? targetAccessor.registryName() : null,
                                       resetCombo,
                                       ip.getCurrentLogicNode(),
                                       ip.getCurrentDataNode()
                                    );
                                 }
                              }

                              if (targetAnim != null && affectsPlayer) {
                                 playerPatch.playAnimationSynchronized(DMCAnimationUtils.getRealAnimationAccessor(targetAnim), convertTime);
                              }

                              if (runtimeStateChanged) {
                                 DmcWeaponManager.syncRuntimeState(sender);
                              }

                              if (isChase && targetAnim != null) {
                                 DMCLog.info(
                                    DMCLog.Category.COMBO_SERVER,
                                    "[CCServer] CHASE_ACCEPT player={} loop={} animation={} stage={} actionStep={}",
                                    sender.m_36316_().getName(),
                                    msg.loopCount,
                                    targetAccessor != null ? targetAccessor.registryName() : null,
                                    ip.getActionSession() != null ? ip.getActionSession().stage() : null,
                                    ip.getActionSession() != null ? ip.getActionSession().actionStep() : null
                                 );
                              }

                              if (targetAccessor != null) {
                                 boolean playDoppel = msg.target == CPPlayCC.CCPlayTarget.DOPPEL;
                                 if (playDoppel) {
                                    DoppelgangerPatch nearest = DoppelgangerPatch.getNearestDoppelganger(sender);
                                    if (nearest != null) {
                                       DoppelgangerEntity doppel = (DoppelgangerEntity)nearest.getOriginal();
                                       if (msg.target != CPPlayCC.CCPlayTarget.BOTH || doppel.isCcMode()) {
                                          nearest.playAnimationSynchronized(targetAccessor, convertTime);
                                          if (!isChase) {
                                             nearest.comboState.clear();
                                          }

                                          doppel.setCcMode(isChase);
                                       } else if (!isChase) {
                                          nearest.comboState.clear();
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  );
            }
         }
      );
      ctx.setPacketHandled(true);
   }

   public static enum CCPlayTarget {
      PLAYER,
      DOPPEL,
      BOTH;
   }

   public static enum Type {
      CHASE,
      FINISH,
      CANCEL,
      RELEASE_NORMAL;
   }
}
