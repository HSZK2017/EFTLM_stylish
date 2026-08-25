package com.dmc.invincible_dmc.skill.weapon_innate;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.api.events.Side;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.api.skill.CrazyComboNode;
import com.dmc.invincible_dmc.api.skill.HitExtendNode;
import com.dmc.invincible_dmc.api.skill.ICrazyComboNode;
import com.dmc.invincible_dmc.api.skill.IHitExtendNode;
import com.dmc.invincible_dmc.api.skill.ITapHoldNode;
import com.dmc.invincible_dmc.api.skill.SubComboNode;
import com.dmc.invincible_dmc.api.skill.TapHoldNode;
import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.capability.weapon.DmcWeaponManager;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionSession;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionStage;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionType;
import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.client.input.DirectionTracker;
import com.dmc.invincible_dmc.client.input.IComboExecutor;
import com.dmc.invincible_dmc.client.input.PlayerInputState;
import com.dmc.invincible_dmc.conditions.ComboInterruptWindowCondition;
import com.dmc.invincible_dmc.conditions.DirectionalSequenceCondition;
import com.dmc.invincible_dmc.conditions.LongPressCondition;
import com.dmc.invincible_dmc.conditions.PressIntervalCondition;
import com.dmc.invincible_dmc.conditions.PressedTimeCondition;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.item.DMCItems;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPComboReset;
import com.dmc.invincible_dmc.skill.weapon_combo.Yamato;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.player.Input;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPSkillExecutionFeedback;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.MovementInputEvent;
import yesman.epicfight.world.entity.eventlistener.SkillCastEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class ComboBasicAttack extends AbstractDmcInnateSkill {
   protected final int resetTime;
   protected final int inputBufferDurationTicks;
   protected final int inputBufferCapacity;
   protected final boolean combosHasLongPressCondition;
   protected final boolean allowJumpCancel;
   @OnlyIn(Dist.CLIENT)
   protected boolean isWalking;
   @Nullable
   protected AnimationAccessor<? extends StaticAnimation> walkBegin;
   @Nullable
   protected AnimationAccessor<? extends StaticAnimation> walkEnd;
   protected ComboNode root;
   @OnlyIn(Dist.CLIENT)
   private boolean lastFrameInMoveMotion;

   public ComboBasicAttack(ComboBasicAttack.Builder builder) {
      super(builder);
      this.shouldDrawGui = builder.shouldDrawGui;
      this.skillTexture = builder.skillTextureLocation;
      this.root = builder.root;
      this.walkBegin = builder.walkBegin;
      this.walkEnd = builder.walkEnd;
      this.translationKeys = builder.translationKeys;
      this.resetTime = builder.resetTime;
      this.inputBufferDurationTicks = builder.inputBufferDurationTicks;
      this.inputBufferCapacity = builder.inputBufferCapacity;
      this.combosHasLongPressCondition = scanForLongPressCondition(builder.root, new HashSet<>());
      this.allowJumpCancel = builder.allowJumpCancel;
   }

   private static boolean scanForLongPressCondition(ComboNode node, Set<ComboNode> visited) {
      if (node != null && visited.add(node)) {
         for (ComboNode condNode : node.getConditionNodes()) {
            for (Condition<?> c : condNode.getConditions(Side.SERVER, Side.BOTH)) {
               if (c instanceof LongPressCondition) {
                  return true;
               }
            }

            for (Condition<?> cx : condNode.getConditions(Side.CLIENT)) {
               if (cx instanceof LongPressCondition) {
                  return true;
               }
            }

            if (scanForLongPressCondition(condNode, visited)) {
               return true;
            }
         }

         for (Condition<?> cxx : node.getConditions(Side.SERVER, Side.BOTH)) {
            if (cxx instanceof LongPressCondition) {
               return true;
            }
         }

         for (Condition<?> cxxx : node.getConditions(Side.CLIENT)) {
            if (cxxx instanceof LongPressCondition) {
               return true;
            }
         }

         for (ComboNode child : node.getChildren()) {
            if (scanForLongPressCondition(child, visited)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static ComboBasicAttack.Builder createComboBasicAttack() {
      return new ComboBasicAttack.Builder().setCategory(SkillCategories.WEAPON_INNATE).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.NONE);
   }

   public static void executeNodeOnServer(ServerPlayer serverPlayer, ComboNode node) {
      executeNodeOnServer(serverPlayer, node, 1, 0L);
   }

   public static void executeNodeOnServer(ServerPlayer serverPlayer, ComboNode node, int pressTime, long inputInterval) {
      EpicFightCapabilities.getUnparameterizedEntityPatch(serverPlayer, ServerPlayerPatch.class)
         .ifPresent(serverPlayerPatch -> executeNodeOnServer(serverPlayerPatch, node, pressTime, inputInterval));
   }

   public static void executeNodeOnServer(ServerPlayerPatch serverPlayerPatch, ComboNode node) {
      executeNodeOnServer(serverPlayerPatch, node, 1, 0L);
   }

   public static void executeNodeOnServer(ServerPlayerPatch serverPlayerPatch, ComboNode node, int pressTime, long inputInterval) {
      if (serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getSkill() instanceof ComboBasicAttack comboBasicAttack) {
         comboBasicAttack.executeNodeOnServer(serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE), node, pressTime, inputInterval);
      }
   }

   public static void executeOnServer(ServerPlayer serverPlayer, ComboType type) {
      executeOnServer(serverPlayer, type, 1, 0L);
   }

   public static void executeOnServer(ServerPlayer serverPlayer, ComboType type, int pressTime, long inputInterval) {
      EpicFightCapabilities.getUnparameterizedEntityPatch(serverPlayer, ServerPlayerPatch.class)
         .ifPresent(serverPlayerPatch -> executeOnServer(serverPlayerPatch, type, pressTime, inputInterval));
   }

   public static void executeOnServer(ServerPlayerPatch serverPlayerPatch, ComboType type) {
      executeOnServer(serverPlayerPatch, type, 1, 0L);
   }

   public static void executeOnServer(ServerPlayerPatch serverPlayerPatch, ComboType type, int pressTime, long inputInterval) {
      if (serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getSkill() instanceof ComboBasicAttack comboBasicAttack) {
         comboBasicAttack.executeOnServer(serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE), type, pressTime, inputInterval);
      }
   }

   public static void sendFeedback(ComboNode node, SkillContainer container, DMCPlayer DMCPlayer) {
      sendFeedback(node, container, DMCPlayer, false);
   }

   public static void sendFeedback(ComboNode node, SkillContainer container, DMCPlayer DMCPlayer, boolean consumeDirection) {
      if (node != null) {
         SPSkillExecutionFeedback feedbackPacket = SPSkillExecutionFeedback.executed(container.getSlotId());
         feedbackPacket.getBuffer().writeBoolean(consumeDirection);
         feedbackPacket.getBuffer().m_130079_(DMCPlayer.saveNBTData(new CompoundTag()));
         EpicFightNetworkManager.sendToPlayer(feedbackPacket, (ServerPlayer)container.getServerExecutor().getOriginal(), new Object[0]);
      }
   }

   static boolean checkDirectionalSequence(
      DirectionalSequenceCondition dsc, int matchedSequencesMask, List<DirectionTracker.DirectionEvent> directionEvents, long attackTick
   ) {
      return DirectionalSequenceCondition.check(dsc, matchedSequencesMask, directionEvents, attackTick);
   }

   public static ComboNode getCurrentNode(SkillContainer container) {
      return DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal()).getCurrentLogicNode();
   }

   public static void setCurrentNodeSync(ServerPlayerPatch serverPlayerPatch, ComboNode node) {
      if (serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getSkill() instanceof ComboBasicAttack comboBasicAttack) {
         comboBasicAttack.setCurrentNodeSync(serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE), node);
      }
   }

   public boolean hasAnyLongPressCondition() {
      return this.combosHasLongPressCondition;
   }

   public boolean isAllowJumpCancel() {
      return this.allowJumpCancel;
   }

   public boolean canExecute(SkillContainer container) {
      if (container.getExecutor().isLogicalClient()) {
         return super.canExecute(container);
      } else {
         ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).m_21205_();
         return super.canExecute(container)
            && EpicFightCapabilities.getItemStackCapability(itemstack).getInnateSkill(container.getExecutor(), itemstack) == this
            && ((Player)container.getExecutor().getOriginal()).m_20202_() == null;
      }
   }

   public boolean isDebugMode(SkillContainer container) {
      return ((Player)container.getExecutor().getOriginal()).m_21205_().m_150930_((Item)DMCItems.DEBUG.get())
         || ((Player)container.getExecutor().getOriginal()).m_21205_().m_150930_((Item)DMCItems.CUSTOM_COMBO_DEMO.get());
   }

   public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
      ComboType type = (ComboType)ComboType.ENUM_MANAGER.get(args.readInt());
      if (type != null) {
         int pressedTime = args.readInt();
         long pressInterval = args.readLong();
         boolean isLongPress = args.isReadable() && args.readBoolean();
         int matchedSequencesMask = args.isReadable() ? args.readInt() : 0;
         List<DirectionTracker.DirectionEvent> directionEvents = Collections.emptyList();
         long engineTick = 0L;
         boolean fromPreInput = false;
         long requestSequence = -1L;
         int sourceLogicNodeId = -1;
         int sourceDataNodeId = -1;
         if (args.isReadable()) {
            int eventCount = args.readByte();
            if (eventCount > 0) {
               directionEvents = new ArrayList<>();

               for (int i = 0; i < eventCount; i++) {
                  DirectionalSequenceCondition.Direction dir = DirectionalSequenceCondition.Direction.values()[args.readByte()];
                  long tick = args.readLong();
                  directionEvents.add(new DirectionTracker.DirectionEvent(dir, tick));
               }
            }

            if (args.isReadable()) {
               engineTick = args.readLong();
            }

            if (args.isReadable()) {
               fromPreInput = args.readBoolean();
            }

            if (args.readableBytes() >= 16) {
               requestSequence = args.readLong();
               sourceLogicNodeId = args.readInt();
               sourceDataNodeId = args.readInt();
            }
         }

         this.executeOnServer(
            container,
            type,
            pressedTime,
            pressInterval,
            isLongPress,
            matchedSequencesMask,
            directionEvents,
            engineTick,
            requestSequence,
            sourceLogicNodeId,
            sourceDataNodeId,
            fromPreInput,
            true
         );
      }
   }

   public void executeOnServer(SkillContainer container, ComboType type, int pressedTime, long inputInterval) {
      this.executeOnServer(container, type, pressedTime, inputInterval, false, 0, Collections.emptyList());
   }

   public void executeOnServer(SkillContainer container, ComboType type, int pressedTime, long inputInterval, boolean isLongPress) {
      this.executeOnServer(container, type, pressedTime, inputInterval, isLongPress, 0, Collections.emptyList(), 0L);
   }

   public void executeOnServer(SkillContainer container, ComboType type, int pressedTime, long inputInterval, boolean isLongPress, int matchedSequencesMask) {
      this.executeOnServer(container, type, pressedTime, inputInterval, isLongPress, matchedSequencesMask, Collections.emptyList(), 0L);
   }

   public void executeOnServer(
      SkillContainer container,
      ComboType type,
      int pressedTime,
      long inputInterval,
      boolean isLongPress,
      int matchedSequencesMask,
      List<DirectionTracker.DirectionEvent> directionEvents
   ) {
      this.executeOnServer(container, type, pressedTime, inputInterval, isLongPress, matchedSequencesMask, directionEvents, 0L);
   }

   public void executeOnServer(
      SkillContainer container,
      ComboType type,
      int pressedTime,
      long inputInterval,
      boolean isLongPress,
      int matchedSequencesMask,
      List<DirectionTracker.DirectionEvent> directionEvents,
      long engineTick
   ) {
      this.executeOnServer(
         container, type, pressedTime, inputInterval, isLongPress, matchedSequencesMask, directionEvents, engineTick, -1L, -1, -1, false, false
      );
   }

   private void executeOnServer(
      SkillContainer container,
      ComboType type,
      int pressedTime,
      long inputInterval,
      boolean isLongPress,
      int matchedSequencesMask,
      List<DirectionTracker.DirectionEvent> directionEvents,
      long engineTick,
      long requestSequence,
      int sourceLogicNodeId,
      int sourceDataNodeId,
      boolean fromPreInput,
      boolean validateNetworkRequest
   ) {
      if (this.isDebugMode(container)) {
         DMCLog.debug(
            DMCLog.Category.COMBO_SERVER,
            "{} {} : pressed {} ticks. Interval: {} ms. longPress={} mask={}",
            ((Player)container.getExecutor().getOriginal()).m_21205_().m_41778_(),
            type,
            pressedTime,
            inputInterval,
            isLongPress,
            matchedSequencesMask
         );
      }

      ((Player)container.getExecutor().getOriginal())
         .getCapability(DMCPlayerCapabilityProvider.DMC_PLAYER)
         .ifPresent(
            dmcPlayer -> {
               ComboNode last = dmcPlayer.getCurrentLogicNode();
               String playerName = ((Player)container.getExecutor().getOriginal()).m_7755_().getString();
               if (validateNetworkRequest) {
                  if (requestSequence < 0L) {
                     DMCLog.warn(
                        DMCLog.Category.COMBO_SERVER,
                        "[ComboSvr] REQUEST_REJECT player={} reason=missing_request_metadata clientTick={} type={}",
                        playerName,
                        engineTick,
                        type
                     );
                     return;
                  }

                  if (!dmcPlayer.tryAcceptComboRequestSequence(requestSequence)) {
                     DMCLog.warn(
                        DMCLog.Category.COMBO_SERVER,
                        "[ComboSvr] REQUEST_REJECT player={} reason=stale_sequence seq={} lastSeq={} sourceLogic={} sourceData={} clientTick={} type={}",
                        playerName,
                        requestSequence,
                        dmcPlayer.getLastComboRequestSequence(),
                        sourceLogicNodeId,
                        sourceDataNodeId,
                        engineTick,
                        type
                     );
                     return;
                  }

                  int authoritativeLogicNodeId = nodeIdOrMissing(last);
                  int authoritativeDataNodeId = nodeIdOrMissing(dmcPlayer.getCurrentDataNode());
                  if (sourceLogicNodeId != authoritativeLogicNodeId || sourceDataNodeId != authoritativeDataNodeId) {
                     DMCLog.warn(
                        DMCLog.Category.COMBO_SERVER,
                        "[ComboSvr] REQUEST_REJECT player={} reason=source_node_mismatch seq={} sourceLogic={} serverLogic={} sourceData={} serverData={} clientTick={} type={} fromPreInput={}",
                        playerName,
                        requestSequence,
                        sourceLogicNodeId,
                        authoritativeLogicNodeId,
                        sourceDataNodeId,
                        authoritativeDataNodeId,
                        engineTick,
                        type,
                        fromPreInput
                     );
                     return;
                  }
               }

               if (last == null) {
                  DMCLog.warn(
                     DMCLog.Category.COMBO_SERVER,
                     "[ComboSvr] 节点解析失败! last==null  |  type={} universalOrdinal={}  player={}  clientTick={}",
                     type,
                     type.universalOrdinal(),
                     playerName,
                     engineTick
                  );
               } else {
                  ComboNode current = last.getNext(type);
                  ComboNode activeCrazyComboNode = dmcPlayer.getActiveCrazyComboNode();
                  if (current != null && activeCrazyComboNode != null && this.hasCrazyComboActionSession(dmcPlayer, activeCrazyComboNode)) {
                     AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(container.getExecutor());
                     StaticAnimation realAnimation = animationPlayer != null && !animationPlayer.isEmpty()
                        ? DMCAnimationUtils.getRealAnimation(animationPlayer)
                        : null;
                     boolean isCcAnimPlaying = realAnimation != null && this.isCrazyComboAnimation((ICrazyComboNode)activeCrazyComboNode, realAnimation);
                     WeaponActionSession actionSession = dmcPlayer.getActionSession();
                     boolean canBasicAttack = container.getExecutor().getEntityState().canBasicAttack();
                     boolean allowContinuationDuringFinish = actionSession != null
                        && actionSession.stage() == WeaponActionStage.FINISH
                        && (!((ICrazyComboNode)activeCrazyComboNode).isCcResetCombo() || canBasicAttack);
                     if (isCcAnimPlaying && !allowContinuationDuringFinish) {
                        DMCLog.info(
                           DMCLog.Category.COMBO_SERVER,
                           "[ComboSvr] CC_TRANSITION_BLOCK player={} node={} animation={} stage={} canBasicAttack={}",
                           ((Player)container.getExecutor().getOriginal()).m_7755_().getString(),
                           activeCrazyComboNode.getId(),
                           realAnimation != null ? realAnimation.getRegistryName() : null,
                           actionSession != null ? actionSession.stage() : null,
                           canBasicAttack
                        );
                        current = null;
                     }
                  }

                  if (current == null) {
                     for (ComboType subType : type.getSubTypes()) {
                        if ((current = last.getNext(subType)) != null) {
                           break;
                        }
                     }
                  }

                  if (current == null) {
                     StringBuilder sb = new StringBuilder();
                     sb.append(
                        String.format(
                           "\n  ========== [ComboSvr] 节点解析失败 — 详细诊断 ==========\n  玩家: %s\n  当前逻辑节点 (last): id=%d  anim=%s  class=%s\n  客户端输入类型 (type): %s (universalOrdinal=%d  subTypes=%s)\n  客户端引擎Tick: %d  pressedTime=%d  inputInterval=%d  longPress=%s\n  当前节点的可用子节点列表:",
                           ((Player)container.getExecutor().getOriginal()).m_7755_().getString(),
                           last.getId(),
                           last.getAnimationAccessor() != null ? last.getAnimationAccessor().toString() : "null",
                           last.getClass().getSimpleName(),
                           type,
                           type.universalOrdinal(),
                           type.getSubTypes(),
                           engineTick,
                           pressedTime,
                           inputInterval,
                           isLongPress
                        )
                     );
                     boolean hasAnyChild = false;

                     for (ComboType registeredType : ComboType.ENUM_MANAGER.universalValues()) {
                        ComboNode child = last.getNext(registeredType);
                        if (child != null) {
                           hasAnyChild = true;
                           sb.append(
                              String.format(
                                 "\n    %s → [id=%d anim=%s]",
                                 registeredType,
                                 child.getId(),
                                 child.getAnimationAccessor() != null ? child.getAnimationAccessor().toString() : "null"
                              )
                           );
                        }
                     }

                     if (!hasAnyChild) {
                        sb.append("\n    (无可用子节点 — 当前节点是叶子节点)");
                     }

                     sb.append("\n  ==========================================================");
                     DMCLog.warn(DMCLog.Category.COMBO_SERVER, sb.toString());
                  }
                  int keyIndex = switch (type.universalOrdinal()) {
                     case 0 -> 0;
                     case 1 -> 1;
                     case 2 -> 2;
                     case 3 -> 3;
                     default -> 4;
                  };
                  dmcPlayer.setComboKeyIndex(keyIndex);
                  this.executeNodeOnServer(container, current, pressedTime, inputInterval, isLongPress, matchedSequencesMask, directionEvents, engineTick);
               }
            }
         );
   }

   private static int nodeIdOrMissing(@Nullable ComboNode node) {
      return node != null && node != ComboNode.EMPTY ? node.getId() : -1;
   }

   public void executeNodeOnServer(SkillContainer container, @Nullable ComboNode current, int pressedTime, long inputInterval) {
      this.executeNodeOnServer(container, current, pressedTime, inputInterval, false, 0, Collections.emptyList(), 0L);
   }

   public void executeNodeOnServer(SkillContainer container, @Nullable ComboNode current, int pressedTime, long inputInterval, boolean isLongPress) {
      this.executeNodeOnServer(container, current, pressedTime, inputInterval, isLongPress, 0, Collections.emptyList(), 0L);
   }

   public void executeNodeOnServer(
      SkillContainer container, @Nullable ComboNode current, int pressedTime, long inputInterval, boolean isLongPress, int matchedSequencesMask
   ) {
      this.executeNodeOnServer(container, current, pressedTime, inputInterval, isLongPress, matchedSequencesMask, Collections.emptyList(), 0L);
   }

   private void executeCcInit(SkillContainer container, DMCPlayer ip, ComboNode current, CrazyComboNode ccNode) {
      SubComboNode ccBase = ccNode.getCcBase();
      if (ccBase != null && ccBase.getAnimationAccessor() != null) {
         DMCLog.info(DMCLog.Category.COMBO_SERVER, "[CC] 服务端起手 -> BASE anim={}", ccBase.getAnimationAccessor());
         container.getExecutor().playAnimationSynchronized(ccBase.getAnimationAccessor(), ccBase.getConvertTime());
         this.handleStiff(container, ccBase.getAnimationAccessor());
         current.getOnBeginEvents().forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), ip));
         ccBase.getOnBeginEvents().forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), ip));
         this.initPlayer(container, ip, ccBase);
         ip.setComboResetTicks(this.resolveComboResetTicks(current));
         ip.setLastNodeExecutionTick((long)((Player)container.getExecutor().getOriginal()).f_19797_);
         ip.setCurrentLogicNode(current);
         ip.beginCrazyComboActionSession(DmcWeaponType.YAMATO, current, ip.getComboKeyIndex(), (long)((Player)container.getExecutor().getOriginal()).f_19797_);
         sendFeedback(current, container, ip);
         this.syncWeaponRuntimeState(container);
      }
   }

   public void executeNodeOnServer(
      SkillContainer container,
      @Nullable ComboNode current,
      int pressedTime,
      long inputInterval,
      boolean isLongPress,
      int matchedSequencesMask,
      List<DirectionTracker.DirectionEvent> directionEvents
   ) {
      this.executeNodeOnServer(container, current, pressedTime, inputInterval, isLongPress, matchedSequencesMask, directionEvents, 0L);
   }

   public void executeNodeOnServer(
      SkillContainer container,
      @Nullable ComboNode current,
      int pressedTime,
      long inputInterval,
      boolean isLongPress,
      int matchedSequencesMask,
      List<DirectionTracker.DirectionEvent> directionEvents,
      long engineTick
   ) {
      ComboNode next = current;
      boolean hasPressedTimeCondition = false;
      boolean directionSequencePassed = false;
      boolean debugMode = this.isDebugMode(container);
      DMCPlayer DMCPlayer = DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal());
      String playerName = ((Player)container.getExecutor().getOriginal()).m_7755_().getString();
      boolean sdtRapidSlashLoopWindow = Yamato.isSdtRapidSlashLoopWindow(container.getExecutor());
      if (current != null) {
         if (sdtRapidSlashLoopWindow && current.getConditionNodes().isEmpty() && !Yamato.isRapidSlashNode(current)) {
            return;
         }

         if (current instanceof CrazyComboNode ccNode) {
            if (ccNode.getCcBase() != null && ccNode.getCcBase().getAnimationAccessor() != null) {
               this.executeCcInit(container, DMCPlayer, current, ccNode);
               return;
            }

            DMCLog.warn(
               DMCLog.Category.COMBO_SERVER,
               "[ComboSvr] 节点执行被静默丢弃! player={} nodeId={} reason=CrazyComboNode.ccBase无效 anim={}",
               playerName,
               current.getId(),
               ccNode.getCcBase() != null ? ccNode.getCcBase().getAnimationAccessor() : "null"
            );
            return;
         }

         if (current instanceof TapHoldNode thNode) {
            SubComboNode tapSub = thNode.getTap();
            if (tapSub != null && tapSub.getAnimationAccessor() != null) {
               DMCLog.info(
                  DMCLog.Category.COMBO_SERVER,
                  "[ComboSvr] PLAY TapHold player={} anim={} windup={}",
                  playerName,
                  tapSub.getAnimationAccessor(),
                  thNode.getWindupDurationTicks()
               );
               float convertTime = tapSub.getConvertTime();
               container.getExecutor().playAnimationSynchronized(tapSub.getAnimationAccessor(), convertTime);
               this.handleStiff(container, tapSub.getAnimationAccessor());
               current.getOnBeginEvents().forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), DMCPlayer));
               tapSub.getOnBeginEvents().forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), DMCPlayer));
               this.initPlayer(container, DMCPlayer, tapSub);
               DMCPlayer.setCurrentLogicNode(current.hasNext() ? current : this.getRoot(container));
               DMCPlayer.setComboResetTicks(this.resolveComboResetTicks(current));
               DMCPlayer.setLastNodeExecutionTick((long)((Player)container.getExecutor().getOriginal()).f_19797_);
               DMCPlayer.setActiveTapHoldNode(thNode);
               DMCPlayer.setWindupStartTick(((Player)container.getExecutor().getOriginal()).f_19797_);
               sendFeedback(DMCPlayer.getCurrentLogicNode(), container, DMCPlayer);
               DMCLog.info(
                  DMCLog.Category.COMBO_SERVER,
                  "[ComboSvr] EXEC TapHold player={} node={} anim={} windup={} pressedMs={} interval={}",
                  playerName,
                  current.getId(),
                  tapSub.getAnimationAccessor(),
                  thNode.getWindupDurationTicks(),
                  pressedTime,
                  inputInterval
               );
               return;
            }

            DMCLog.warn(
               DMCLog.Category.COMBO_SERVER,
               "[ComboSvr] 节点执行被静默丢弃! player={} nodeId={} reason=TapHoldNode.tapSub无效 tapSub={}",
               playerName,
               current.getId(),
               tapSub
            );
            return;
         }

         if (current instanceof HitExtendNode heNode) {
            SubComboNode baseSub = heNode.getBase();
            if (baseSub != null && baseSub.getAnimationAccessor() != null) {
               if (Yamato.tryLoopSdtRapidSlash(container.getExecutor(), current)) {
                  DMCPlayer.setCurrentLogicNode(current.hasNext() ? current : this.getRoot(container));
                  DMCPlayer.setComboResetTicks(this.resolveComboResetTicks(current));
                  DMCPlayer.setLastNodeExecutionTick((long)((Player)container.getExecutor().getOriginal()).f_19797_);
                  DMCPlayer.beginHitExtend(heNode, ((Player)container.getExecutor().getOriginal()).f_19797_);
                  sendFeedback(DMCPlayer.getCurrentLogicNode(), container, DMCPlayer);
                  return;
               }

               DMCLog.info(DMCLog.Category.COMBO_SERVER, "[ComboSvr] PLAY HitExtend player={} anim={}", playerName, baseSub.getAnimationAccessor());
               float convertTime = baseSub.getConvertTime();
               container.getExecutor().playAnimationSynchronized(baseSub.getAnimationAccessor(), convertTime);
               this.handleStiff(container, baseSub.getAnimationAccessor());
               current.getOnBeginEvents().forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), DMCPlayer));
               baseSub.getOnBeginEvents().forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), DMCPlayer));
               this.initPlayer(container, DMCPlayer, baseSub);
               DMCPlayer.setCurrentLogicNode(current.hasNext() ? current : this.getRoot(container));
               DMCPlayer.setComboResetTicks(this.resolveComboResetTicks(current));
               DMCPlayer.setLastNodeExecutionTick((long)((Player)container.getExecutor().getOriginal()).f_19797_);
               DMCPlayer.beginHitExtend(heNode, ((Player)container.getExecutor().getOriginal()).f_19797_);
               sendFeedback(DMCPlayer.getCurrentLogicNode(), container, DMCPlayer);
               DMCLog.info(
                  DMCLog.Category.COMBO_SERVER,
                  "[ComboSvr] EXEC HitExtend player={} node={} anim={} pressedMs={} interval={}",
                  playerName,
                  current.getId(),
                  baseSub.getAnimationAccessor(),
                  pressedTime,
                  inputInterval
               );
               return;
            }

            DMCLog.warn(
               DMCLog.Category.COMBO_SERVER,
               "[ComboSvr] 节点执行被静默丢弃! player={} nodeId={} reason=HitExtendNode.base无效 base={}",
               playerName,
               current.getId(),
               baseSub
            );
            return;
         }

         if (current.getAnimationAccessor() != null && current.getConditionNodes().isEmpty()) {
            for (Condition condition : current.getConditions(Side.SERVER, Side.BOTH)) {
               if (condition instanceof PressedTimeCondition pressedTimeCondition) {
                  hasPressedTimeCondition = true;
                  if (!isLongPress || pressedTime < pressedTimeCondition.getMin() || pressedTime > pressedTimeCondition.getMax()) {
                     DMCLog.warn(
                        DMCLog.Category.COMBO_SERVER,
                        "[ComboSvr] 节点执行被静默丢弃! player={} nodeId={} reason=PressedTimeCondition不满足 isLongPress={} pressedTime={} min={} max={}",
                        playerName,
                        current.getId(),
                        isLongPress,
                        pressedTime,
                        pressedTimeCondition.getMin(),
                        pressedTimeCondition.getMax()
                     );
                     return;
                  }

                  DMCLog.info(
                     DMCLog.Category.COMBO_SERVER,
                     "[ComboSvr] COND PASS PressedTimeCondition player={}: pressedTime={} [{}:{}] isLP={}",
                     playerName,
                     pressedTime,
                     pressedTimeCondition.getMin(),
                     pressedTimeCondition.getMax(),
                     isLongPress
                  );
               } else if (condition instanceof LongPressCondition) {
                  hasPressedTimeCondition = true;
                  if (!isLongPress) {
                     DMCLog.warn(
                        DMCLog.Category.COMBO_SERVER,
                        "[ComboSvr] 节点执行被静默丢弃! player={} nodeId={} reason=LongPressCondition不满足 isLongPress={}",
                        playerName,
                        current.getId(),
                        isLongPress
                     );
                     return;
                  }

                  DMCLog.info(DMCLog.Category.COMBO_SERVER, "[ComboSvr] COND PASS LongPressCondition player={}: isLP={}", playerName, isLongPress);
               } else if (condition instanceof PressIntervalCondition pressIntervalCondition) {
                  if (inputInterval < pressIntervalCondition.getMin() || inputInterval > pressIntervalCondition.getMax()) {
                     DMCLog.warn(
                        DMCLog.Category.COMBO_SERVER,
                        "[ComboSvr] 节点执行被静默丢弃! player={} nodeId={} reason=PressIntervalCondition不满足 interval={} min={} max={}",
                        playerName,
                        current.getId(),
                        inputInterval,
                        pressIntervalCondition.getMin(),
                        pressIntervalCondition.getMax()
                     );
                     return;
                  }

                  DMCLog.info(
                     DMCLog.Category.COMBO_SERVER,
                     "[ComboSvr] COND PASS PressIntervalCondition player={}: interval={} [{}:{}]",
                     playerName,
                     inputInterval,
                     pressIntervalCondition.getMin(),
                     pressIntervalCondition.getMax()
                  );
               } else if (condition instanceof DirectionalSequenceCondition dsc) {
                  if (!checkDirectionalSequence(dsc, matchedSequencesMask, directionEvents, engineTick)) {
                     DMCLog.warn(
                        DMCLog.Category.COMBO_SERVER,
                        "[ComboSvr] 节点执行被静默丢弃! player={} nodeId={} reason=DirectionalSequenceCondition不满足 seq={}",
                        playerName,
                        current.getId(),
                        dsc.getSequence().name().toLowerCase()
                     );
                     return;
                  }

                  DMCLog.info(
                     DMCLog.Category.COMBO_SERVER,
                     "[ComboSvr] COND PASS DirectionalSequenceCondition player={}: mask={} eng={}",
                     playerName,
                     matchedSequencesMask,
                     engineTick
                  );
                  directionSequencePassed = true;
               } else {
                  if (!condition.predicate(container.getExecutor())) {
                     DMCLog.warn(
                        DMCLog.Category.COMBO_SERVER,
                        "[ComboSvr] 节点执行被静默丢弃! player={} nodeId={} reason=条件谓词失败 condition={}",
                        playerName,
                        current.getId(),
                        condition.getClass().getSimpleName()
                     );
                     return;
                  }

                  DMCLog.info(DMCLog.Category.COMBO_SERVER, "[ComboSvr] COND PASS {} player={}", condition.getClass().getSimpleName(), playerName);
               }
            }

            if (!hasPressedTimeCondition && pressedTime > 60) {
               DMCLog.warn(
                  DMCLog.Category.COMBO_SERVER,
                  "[ComboSvr] 节点执行被静默丢弃! player={} nodeId={} reason=pressedTime超限(无按压时长条件且>60) pressedTime={}",
                  playerName,
                  current.getId(),
                  pressedTime
               );
               return;
            }
         } else {
            if (current.getConditionNodes().isEmpty()) {
               DMCLog.warn(
                  DMCLog.Category.COMBO_SERVER,
                  "[ComboSvr] 节点执行被静默丢弃! player={} nodeId={} reason=无动画且无条件节点 anim=null conditions=[]",
                  playerName,
                  current.getId()
               );
               return;
            }

            current.getConditionNodes().sort(Comparator.comparingInt(ComboNode::getPriority).reversed());
            if (debugMode) {
               DMCLog.debug(
                  DMCLog.Category.COMBO_SERVER,
                  "[ComboSvr] mask=0x{}  conditionNodes={}",
                  Integer.toHexString(matchedSequencesMask),
                  current.getConditionNodes().size()
               );
            }

            boolean inComboInterruptWindow = this.isInComboInterruptWindow(container);
            boolean canExecute = false;

            for (ComboNode conditionAnimation : current.getConditionNodes()) {
               if ((!sdtRapidSlashLoopWindow || Yamato.isRapidSlashNode(conditionAnimation))
                  && (!inComboInterruptWindow || hasComboInterruptCondition(conditionAnimation))) {
                  canExecute = true;
                  String failReason = null;

                  for (Condition conditionx : conditionAnimation.getConditions(Side.SERVER, Side.BOTH)) {
                     if (conditionx instanceof PressedTimeCondition pressedTimeCondition) {
                        if (!isLongPress || pressedTime < pressedTimeCondition.getMin() || pressedTime > pressedTimeCondition.getMax()) {
                           canExecute = false;
                           failReason = "PressedTimeCondition";
                           DMCLog.info(
                              DMCLog.Category.COMBO_SERVER,
                              "[ComboSvr] COND FAIL PressedTimeCondition player={} condNode={}: pressedTime={} [{}:{}] isLP={}",
                              playerName,
                              conditionAnimation.getId(),
                              pressedTime,
                              pressedTimeCondition.getMin(),
                              pressedTimeCondition.getMax(),
                              isLongPress
                           );
                           break;
                        }

                        DMCLog.info(
                           DMCLog.Category.COMBO_SERVER,
                           "[ComboSvr] COND PASS PressedTimeCondition player={} condNode={}: pressedTime={} [{}:{}] isLP={}",
                           playerName,
                           conditionAnimation.getId(),
                           pressedTime,
                           pressedTimeCondition.getMin(),
                           pressedTimeCondition.getMax(),
                           isLongPress
                        );
                     } else if (conditionx instanceof LongPressCondition) {
                        if (!isLongPress) {
                           canExecute = false;
                           failReason = "LongPressCondition";
                           DMCLog.info(
                              DMCLog.Category.COMBO_SERVER,
                              "[ComboSvr] COND FAIL LongPressCondition player={} condNode={}: isLP={}",
                              playerName,
                              conditionAnimation.getId(),
                              isLongPress
                           );
                           break;
                        }

                        DMCLog.info(
                           DMCLog.Category.COMBO_SERVER,
                           "[ComboSvr] COND PASS LongPressCondition player={} condNode={}: isLP={}",
                           playerName,
                           conditionAnimation.getId(),
                           isLongPress
                        );
                     } else if (conditionx instanceof PressIntervalCondition pressIntervalCondition) {
                        if (inputInterval < pressIntervalCondition.getMin() || inputInterval > pressIntervalCondition.getMax()) {
                           canExecute = false;
                           failReason = "PressIntervalCondition";
                           DMCLog.info(
                              DMCLog.Category.COMBO_SERVER,
                              "[ComboSvr] COND FAIL PressIntervalCondition player={} condNode={}: interval={} [{}:{}]",
                              playerName,
                              conditionAnimation.getId(),
                              inputInterval,
                              pressIntervalCondition.getMin(),
                              pressIntervalCondition.getMax()
                           );
                           break;
                        }

                        DMCLog.info(
                           DMCLog.Category.COMBO_SERVER,
                           "[ComboSvr] COND PASS PressIntervalCondition player={} condNode={}: interval={} [{}:{}]",
                           playerName,
                           conditionAnimation.getId(),
                           inputInterval,
                           pressIntervalCondition.getMin(),
                           pressIntervalCondition.getMax()
                        );
                     } else if (conditionx instanceof DirectionalSequenceCondition dsc) {
                        boolean dirMatch = checkDirectionalSequence(dsc, matchedSequencesMask, directionEvents, engineTick);
                        if (!dirMatch) {
                           canExecute = false;
                           failReason = "DirSeq:" + dsc.getSequence().name().toLowerCase();
                           DMCLog.info(
                              DMCLog.Category.COMBO_SERVER,
                              "[ComboSvr] COND FAIL DirectionalSequenceCondition player={} condNode={}: mask={} eng={}",
                              playerName,
                              conditionAnimation.getId(),
                              matchedSequencesMask,
                              engineTick
                           );
                           break;
                        }

                        DMCLog.info(
                           DMCLog.Category.COMBO_SERVER,
                           "[ComboSvr] COND PASS DirectionalSequenceCondition player={} condNode={}: mask={} eng={}",
                           playerName,
                           conditionAnimation.getId(),
                           matchedSequencesMask,
                           engineTick
                        );
                        directionSequencePassed = true;
                     } else {
                        if (!conditionx.predicate(container.getExecutor())) {
                           canExecute = false;
                           failReason = conditionx.getClass().getSimpleName();
                           DMCLog.info(
                              DMCLog.Category.COMBO_SERVER,
                              "[ComboSvr] COND FAIL {} player={} condNode={}",
                              conditionx.getClass().getSimpleName(),
                              playerName,
                              conditionAnimation.getId()
                           );
                           break;
                        }

                        DMCLog.info(
                           DMCLog.Category.COMBO_SERVER,
                           "[ComboSvr] COND PASS {} player={} condNode={}",
                           conditionx.getClass().getSimpleName(),
                           playerName,
                           conditionAnimation.getId()
                        );
                     }
                  }

                  if (debugMode) {
                     DMCLog.debug(
                        DMCLog.Category.COMBO_SERVER,
                        "[ComboSvr] condNode id={} prio={} canExecute={} fail={}",
                        conditionAnimation.getId(),
                        conditionAnimation.getPriority(),
                        canExecute,
                        failReason
                     );
                  }

                  if (canExecute) {
                     DMCLog.info(
                        DMCLog.Category.COMBO_SERVER,
                        "[ComboSvr] COND MATCH player={} node={} parent={} pressedMs={} interval={} isLP={}",
                        playerName,
                        conditionAnimation.getId(),
                        current.getId(),
                        pressedTime,
                        inputInterval,
                        isLongPress
                     );
                     current = conditionAnimation;
                     if (conditionAnimation.hasNext()) {
                        next = conditionAnimation;
                     }
                     break;
                  }
               }
            }

            if (!canExecute) {
               DMCLog.info(
                  DMCLog.Category.COMBO_SERVER,
                  "[ComboSvr] NODE REJECTED player={} from={} pressedMs={} interval={} isLP={}",
                  playerName,
                  current.getId(),
                  pressedTime,
                  inputInterval,
                  isLongPress
               );
            }
         }

         if (sdtRapidSlashLoopWindow && !Yamato.isRapidSlashNode(current)) {
            return;
         }

         if (current instanceof CrazyComboNode ccNode) {
            if (ccNode.getCcBase() != null && ccNode.getCcBase().getAnimationAccessor() != null) {
               this.executeCcInit(container, DMCPlayer, current, ccNode);
            }

            return;
         }

         if (current instanceof TapHoldNode thNode) {
            SubComboNode tapSub = thNode.getTap();
            if (tapSub != null && tapSub.getAnimationAccessor() != null) {
               float convertTime = tapSub.getConvertTime();
               container.getExecutor().playAnimationSynchronized(tapSub.getAnimationAccessor(), convertTime);
               this.handleStiff(container, tapSub.getAnimationAccessor());
               current.getOnBeginEvents().forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), DMCPlayer));
               tapSub.getOnBeginEvents().forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), DMCPlayer));
               this.initPlayer(container, DMCPlayer, tapSub);
               DMCPlayer.setCurrentLogicNode(current.hasNext() ? current : this.getRoot(container));
               DMCPlayer.setComboResetTicks(this.resolveComboResetTicks(current));
               DMCPlayer.setLastNodeExecutionTick((long)((Player)container.getExecutor().getOriginal()).f_19797_);
               DMCPlayer.setActiveTapHoldNode(thNode);
               DMCPlayer.setWindupStartTick(((Player)container.getExecutor().getOriginal()).f_19797_);
               sendFeedback(DMCPlayer.getCurrentLogicNode(), container, DMCPlayer, directionSequencePassed);
               DMCLog.info(
                  DMCLog.Category.COMBO_SERVER,
                  "[ComboSvr] EXEC TapHold(cond) player={} node={} anim={} windup={} pressedMs={} interval={}",
                  playerName,
                  current.getId(),
                  tapSub.getAnimationAccessor(),
                  thNode.getWindupDurationTicks(),
                  pressedTime,
                  inputInterval
               );
               return;
            }

            DMCLog.warn(
               DMCLog.Category.COMBO_SERVER,
               "[ComboSvr] 节点执行被静默丢弃! player={} nodeId={} reason=TapHoldNode(条件后).tapSub无效 tapSub={}",
               playerName,
               current.getId(),
               tapSub
            );
            return;
         }

         if (current instanceof HitExtendNode heNode) {
            SubComboNode baseSub = heNode.getBase();
            if (baseSub != null && baseSub.getAnimationAccessor() != null) {
               if (Yamato.tryLoopSdtRapidSlash(container.getExecutor(), current)) {
                  DMCPlayer.setCurrentLogicNode(current.hasNext() ? current : this.getRoot(container));
                  DMCPlayer.setComboResetTicks(this.resolveComboResetTicks(current));
                  DMCPlayer.setLastNodeExecutionTick((long)((Player)container.getExecutor().getOriginal()).f_19797_);
                  DMCPlayer.beginHitExtend(heNode, ((Player)container.getExecutor().getOriginal()).f_19797_);
                  sendFeedback(DMCPlayer.getCurrentLogicNode(), container, DMCPlayer, directionSequencePassed);
                  return;
               }

               float convertTime = baseSub.getConvertTime();
               container.getExecutor().playAnimationSynchronized(baseSub.getAnimationAccessor(), convertTime);
               this.handleStiff(container, baseSub.getAnimationAccessor());
               current.getOnBeginEvents().forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), DMCPlayer));
               baseSub.getOnBeginEvents().forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), DMCPlayer));
               this.initPlayer(container, DMCPlayer, baseSub);
               DMCPlayer.setCurrentLogicNode(current.hasNext() ? current : this.getRoot(container));
               DMCPlayer.setComboResetTicks(this.resolveComboResetTicks(current));
               DMCPlayer.setLastNodeExecutionTick((long)((Player)container.getExecutor().getOriginal()).f_19797_);
               DMCPlayer.beginHitExtend(heNode, ((Player)container.getExecutor().getOriginal()).f_19797_);
               sendFeedback(DMCPlayer.getCurrentLogicNode(), container, DMCPlayer, directionSequencePassed);
               DMCLog.info(
                  DMCLog.Category.COMBO_SERVER,
                  "[ComboSvr] EXEC HitExtend(cond) player={} node={} anim={} pressedMs={} interval={}",
                  playerName,
                  current.getId(),
                  baseSub.getAnimationAccessor(),
                  pressedTime,
                  inputInterval
               );
               return;
            }

            DMCLog.warn(
               DMCLog.Category.COMBO_SERVER,
               "[ComboSvr] 节点执行被静默丢弃! player={} nodeId={} reason=HitExtendNode(条件后).base无效 base={}",
               playerName,
               current.getId(),
               baseSub
            );
            return;
         }

         AnimationAccessor animationAccessor = current.getAnimationAccessor();
         if (animationAccessor == null) {
            DMCLog.warn(
               DMCLog.Category.COMBO_SERVER, "[ComboSvr] 节点执行被静默丢弃! player={} nodeId={} reason=animationAccessor为null(无可用动画)", playerName, current.getId()
            );
            return;
         }

         float convertTime = current.getConvertTime();
         if (debugMode) {
            DMCLog.debug(DMCLog.Category.COMBO_SERVER, "animationAccessor: {}", animationAccessor);
         }

         container.getExecutor().playAnimationSynchronized(animationAccessor, convertTime);
         this.handleStiff(container, animationAccessor);
         current.getOnBeginEvents().forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), DMCPlayer));
         this.initPlayer(container, DMCPlayer, current);
         DMCPlayer.setComboResetTicks(this.resolveComboResetTicks(current));
         DMCPlayer.setLastNodeExecutionTick((long)((Player)container.getExecutor().getOriginal()).f_19797_);
         if (current.isRepeatNode()) {
            next = current.getParentNode();
         }

         DMCPlayer.setCurrentLogicNode(next);
         sendFeedback(next, container, DMCPlayer, directionSequencePassed);
         DMCLog.info(
            DMCLog.Category.COMBO_SERVER,
            "[ComboSvr] EXEC player={} node={} anim={} pressedMs={} interval={} isLP={} mask={} eng={}",
            playerName,
            current.getId(),
            animationAccessor,
            pressedTime,
            inputInterval,
            isLongPress,
            matchedSequencesMask,
            engineTick
         );
      } else {
         DMCPlayer.setComboResetTicks(-1);
         ComboNode activeRoot = this.getRoot(container);
         DMCPlayer.setCurrentLogicNode(activeRoot);
         sendFeedback(activeRoot, container, DMCPlayer, directionSequencePassed);
      }
   }

   private boolean hasCrazyComboActionSession(DMCPlayer dmcPlayer, ComboNode node) {
      return dmcPlayer.hasActionSession(DmcWeaponType.YAMATO, WeaponActionType.CRAZY_COMBO);
   }

   @Override
   protected void initPlayer(SkillContainer container, DMCPlayer DMCPlayer, ComboNode dataNode) {
      DMCPlayer.setPhase(dataNode.getNewPhase());
      super.initPlayer(container, DMCPlayer, dataNode);
      DMCPlayer.addComboPathNode((long)dataNode.getId());
      if (DMCPlayer.tryConsumeAerialPending()) {
         SkillDataManager manager = container.getDataManager();
         int count = manager.hasData((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get())
            ? (Integer)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get())
            : 0;
         manager.setDataSync((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get(), count + 1);
      }
   }

   int resolveComboResetTicks(ComboNode node) {
      return node.getComboResetTime() >= 0.0F ? Math.round(node.getComboResetTime() * 20.0F) : node.getComboResetTicks();
   }

   @OnlyIn(Dist.CLIENT)
   public void executeOnClient(SkillContainer container, FriendlyByteBuf args) {
      DMCPlayer DMCPlayer = DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal());
      boolean consumeDirection = args.isReadable() ? args.readBoolean() : false;
      CompoundTag tag = args.m_130260_();
      if (tag != null) {
         DMCPlayer.loadNBTData(tag);
         ComboNode current = DMCPlayer.getCurrentLogicNode();
         if (current != null) {
            DMCPlayer.getCurrentLogicNode()
               .getOnBeginEvents()
               .forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), DMCPlayer));
         }

         this.initPlayer(container, DMCPlayer, DMCPlayer.getCurrentDataNode());
      }

      if (consumeDirection) {
         IComboExecutor dispatcher = DMComboEngine.getLocalPlayerDispatcher();
         if (dispatcher != null) {
            dispatcher.getDirectionTracker().consume(DMComboEngine.engineTick);
         }
      }
   }

   protected void onSkillCastEvent(SkillCastEvent event, SkillContainer container) {
      ItemStack mainHandItem = ((Player)event.getPlayerPatch().getOriginal()).m_21205_();
      Optional<CapabilityItem> optionalCapabilityItem = EpicFightCapabilities.getItemCapability(mainHandItem);
      if (!optionalCapabilityItem.isEmpty() && !optionalCapabilityItem.get().isEmpty()) {
         if (EpicFightCapabilities.getItemStackCapability(mainHandItem).getInnateSkill(event.getPlayerPatch(), mainHandItem) != null) {
            SkillCategory skillCategory = event.getSkillContainer().getSkill().getCategory();
            if (skillCategory.equals(SkillCategories.BASIC_ATTACK) && !((Player)event.getPlayerPatch().getOriginal()).m_20159_()) {
               event.setCanceled(true);
            }
         }
      }
   }

   protected void onClientInput(MovementInputEvent event, SkillContainer container) {
      Input input = event.getMovementInput();
      boolean isUp = input.f_108568_;
      if (isUp && !this.isWalking) {
         if (this.walkBegin != null) {
            container.getExecutor().playAnimationSynchronized(this.walkBegin, 0.15F);
         }

         this.isWalking = true;
      }

      if (!isUp && this.isWalking) {
         if (this.walkEnd != null) {
            container.getExecutor().playAnimationSynchronized(this.walkEnd, 0.15F);
         }

         this.isWalking = false;
      }
   }

   @Override
   public void onInitiate(SkillContainer container) {
      super.onInitiate(container);
      this.resetCombo(container);
      container.getExecutor().getEventListener().addEventListener(EventType.SKILL_CAST_EVENT, EVENT_UUID, event -> this.onSkillCastEvent(event, container));
      container.getExecutor().getEventListener().addEventListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, event -> this.onClientInput(event, container));
   }

   @Override
   public void onRemoved(SkillContainer container) {
      DMCPlayer dmcPlayer = DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal());
      dmcPlayer.clearActiveCrazyComboNode();
      dmcPlayer.clearActionSession(WeaponActionStage.CANCELLED);
      this.syncWeaponRuntimeState(container);
      super.onRemoved(container);
      container.getExecutor().getEventListener().removeListener(EventType.SKILL_CAST_EVENT, EVENT_UUID);
      container.getExecutor().getEventListener().removeListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
   }

   @Override
   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      DMCPlayer DMCPlayer = DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal());
      if (!container.getExecutor().isLogicalClient()) {
         this.tapHoldCheck(container, DMCPlayer);
         this.hitExtendCheck(container, DMCPlayer);
      }

      if (!container.getExecutor().isLogicalClient()) {
         ComboNode activeCrazyComboNode = DMCPlayer.getActiveCrazyComboNode();
         boolean isPlayingCC = activeCrazyComboNode != null && this.hasCrazyComboActionSession(DMCPlayer, activeCrazyComboNode);
         if (isPlayingCC) {
            AnimationPlayer player = DMCAnimationUtils.getMainPlayer(container.getExecutor());
            ICrazyComboNode ccNode = (ICrazyComboNode)activeCrazyComboNode;
            boolean ccAnimActive = false;
            if (player != null && !player.isEmpty()) {
               StaticAnimation realAnimation = DMCAnimationUtils.getRealAnimation(player);
               ccAnimActive = realAnimation != null && this.isCrazyComboAnimation(ccNode, realAnimation);
            }

            if (!ccAnimActive) {
               if (this.hasCrazyComboActionSession(DMCPlayer, activeCrazyComboNode)) {
                  DMCPlayer.clearCrazyComboActionSession(WeaponActionStage.COMPLETED);
                  this.syncWeaponRuntimeState(container);
               } else {
                  DMCPlayer.clearActiveCrazyComboNode();
               }

               isPlayingCC = false;
            }
         }

         if (!isPlayingCC) {
            ComboNode dataNode = DMCPlayer.getCurrentDataNode();
            if (dataNode != null && dataNode.getComboResetAtAnimTime() >= 0.0F) {
               AnimationPlayer playerx = DMCAnimationUtils.getMainPlayer(container.getExecutor());
               if (playerx != null && !playerx.isEmpty() && playerx.getElapsedTime() >= dataNode.getComboResetAtAnimTime()) {
                  this.resetCombo(container);
               }
            }
         }

         int resetThreshold = DMCPlayer.getComboResetTicks() >= 0 ? DMCPlayer.getComboResetTicks() : this.getResetTime(container);
         if (!isPlayingCC) {
            boolean shouldReset;
            if (DMCPlayer.getComboResetTicks() >= 0 && DMCPlayer.getLastNodeExecutionTick() > 0L) {
               long elapsed = (long)((Player)container.getExecutor().getOriginal()).f_19797_ - DMCPlayer.getLastNodeExecutionTick();
               shouldReset = elapsed > (long)resetThreshold;
            } else {
               shouldReset = container.getExecutor().getTickSinceLastAction() > resetThreshold;
            }

            if (shouldReset) {
               this.resetCombo(container);
            }
         }
      }

      if (container.getExecutor().isLogicalClient()) {
         LivingMotion livingMotion = container.getClientExecutor().currentLivingMotion;
         boolean isMoveMotion = livingMotion.isSame(LivingMotions.WALK)
            || livingMotion.isSame(LivingMotions.RUN)
            || livingMotion.isSame(LivingMotions.JUMP)
            || livingMotion.isSame(LivingMotions.KNEEL);
         if (isMoveMotion
            && !this.lastFrameInMoveMotion
            && getCurrentNode(container) != this.getRoot(container)
            && DMCPlayer.getCurrentDataNode().isAutoResetByMove()) {
         }

         this.lastFrameInMoveMotion = isMoveMotion;
      }

      this.tickCooldownsAndTimers(container);
   }

   protected void tapHoldCheck(SkillContainer container, DMCPlayer ip) {
   }

   private boolean isCrazyComboAnimation(ICrazyComboNode node, StaticAnimation animation) {
      return ICrazyComboNode.containsAnimation(node, animation.getRegistryName());
   }

   private void syncWeaponRuntimeState(SkillContainer container) {
      if (!container.getExecutor().isLogicalClient() && container.getExecutor().getOriginal() instanceof ServerPlayer serverPlayer) {
         DmcWeaponManager.syncRuntimeState(serverPlayer);
      }
   }

   public void triggerTapHold(SkillContainer container) {
      DMCPlayer ip = DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal());
      ITapHoldNode activeTH = ip.getActiveTapHoldNode();
      if (activeTH != null) {
         ip.setActiveTapHoldNode(null);
         SubComboNode holdSub = activeTH.getHold();
         if (holdSub != null && holdSub.getAnimationAccessor() != null) {
            DMCLog.info(DMCLog.Category.COMBO_SERVER, "[TapHold] Client confirmed continuous hold -> switching to holdAnim={}", holdSub.getAnimationAccessor());
            container.getExecutor().playAnimationSynchronized(holdSub.getAnimationAccessor(), holdSub.getConvertTime());
            holdSub.getOnBeginEvents().forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), ip));
            this.initPlayer(container, ip, holdSub);
            ServerPlayerPatch sp = (ServerPlayerPatch)container.getExecutor();
            SPSkillExecutionFeedback feedback = SPSkillExecutionFeedback.executed(container.getSlotId());
            feedback.getBuffer().writeBoolean(false);
            feedback.getBuffer().m_130079_(ip.saveNBTData(new CompoundTag()));
            EpicFightNetworkManager.sendToPlayer(feedback, (ServerPlayer)sp.getOriginal(), new Object[0]);
         }
      }
   }

   protected void hitExtendCheck(SkillContainer container, DMCPlayer ip) {
      IHitExtendNode activeHE = ip.getActiveHitExtendNode();
      if (activeHE != null) {
         int currentTick = ((Player)container.getExecutor().getOriginal()).f_19797_;
         if (!this.isComboKeyHeld(container, ip.getComboKeyIndex())) {
            DMCLog.info(
               DMCLog.Category.COMBO_SERVER,
               "[HitExtend] CANCEL player={} node={} reason=key_released latched={}",
               ((Player)container.getExecutor().getOriginal()).m_7755_().getString(),
               ((ComboNode)activeHE).getId(),
               ip.hasLatchedHitExtend()
            );
            ip.setActiveHitExtendNode(null);
         } else if (!this.isHitExtendBaseAnimationActive(container, activeHE)) {
            DMCLog.info(
               DMCLog.Category.COMBO_SERVER,
               "[HitExtend] CANCEL player={} node={} reason=base_animation_ended latched={}",
               ((Player)container.getExecutor().getOriginal()).m_7755_().getString(),
               ((ComboNode)activeHE).getId(),
               ip.hasLatchedHitExtend()
            );
            ip.setActiveHitExtendNode(null);
         } else {
            if (ip.isHitExtendReady(currentTick)) {
               this.triggerHitExtend(container, "latched_tick");
            }
         }
      }
   }

   public void recordHitExtendHit(SkillContainer container, @Nullable Entity target) {
      DMCPlayer ip = DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal());
      IHitExtendNode activeHE = ip.getActiveHitExtendNode();
      if (activeHE != null
         && activeHE.getExtend() != null
         && activeHE.getExtend().getAnimationAccessor() != null
         && this.isHitExtendBaseAnimationActive(container, activeHE)) {
         int currentTick = ((Player)container.getExecutor().getOriginal()).f_19797_;
         int targetId = target != null ? target.m_19879_() : -1;
         if (ip.latchHitExtend(currentTick, targetId)) {
            DMCLog.info(
               DMCLog.Category.COMBO_SERVER,
               "[HitExtend] LATCH player={} node={} target={} hitTick={} heldTicks={}/{}",
               ((Player)container.getExecutor().getOriginal()).m_7755_().getString(),
               ((ComboNode)activeHE).getId(),
               targetId,
               currentTick,
               currentTick - ip.getHitExtendStartTick(),
               activeHE.getMinimumHoldTicks()
            );
         }

         if (!this.isComboKeyHeld(container, ip.getComboKeyIndex())) {
            ip.setActiveHitExtendNode(null);
         } else {
            if (ip.isHitExtendReady(currentTick)) {
               this.triggerHitExtend(container, "damage_event");
            }
         }
      }
   }

   private boolean isHitExtendBaseAnimationActive(SkillContainer container, IHitExtendNode activeHE) {
      return activeHE.matchesBaseAnimation(DMCAnimationUtils.getRealAnimationAccessor(container.getExecutor()));
   }

   private void triggerHitExtend(SkillContainer container, String source) {
      DMCPlayer ip = DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal());
      IHitExtendNode activeHE = ip.getActiveHitExtendNode();
      if (activeHE != null && ip.isHitExtendReady(((Player)container.getExecutor().getOriginal()).f_19797_)) {
         SubComboNode extendSub = activeHE.getExtend();
         if (extendSub != null && extendSub.getAnimationAccessor() != null) {
            int heldTicks = ((Player)container.getExecutor().getOriginal()).f_19797_ - ip.getHitExtendStartTick();
            int hitTick = ip.getHitExtendHitTick();
            int targetId = ip.getHitExtendTargetId();
            ip.setActiveHitExtendNode(null);
            DMCLog.info(
               DMCLog.Category.COMBO_SERVER,
               "[HitExtend] TRIGGER player={} node={} source={} target={} hitTick={} heldTicks={} extendAnim={}",
               ((Player)container.getExecutor().getOriginal()).m_7755_().getString(),
               ((ComboNode)activeHE).getId(),
               source,
               targetId,
               hitTick,
               heldTicks,
               extendSub.getAnimationAccessor()
            );
            container.getExecutor().playAnimationInstantly(extendSub.getAnimationAccessor());
            extendSub.getOnBeginEvents().forEach(event -> event.testAndExecute(container.getExecutor(), container.getExecutor().getTarget(), ip));
            this.initPlayer(container, ip, extendSub);
            ServerPlayerPatch sp = (ServerPlayerPatch)container.getExecutor();
            SPSkillExecutionFeedback feedback = SPSkillExecutionFeedback.executed(container.getSlotId());
            feedback.getBuffer().writeBoolean(false);
            feedback.getBuffer().m_130079_(ip.saveNBTData(new CompoundTag()));
            EpicFightNetworkManager.sendToPlayer(feedback, (ServerPlayer)sp.getOriginal(), new Object[0]);
         }
      }
   }

   private boolean isComboKeyHeld(SkillContainer container, int keyIndex) {
      return keyIndex >= 0 && keyIndex < 5 ? PlayerInputState.isRemoteDown((Player)container.getExecutor().getOriginal(), 9 + keyIndex) : false;
   }

   public void resetCombo(SkillContainer container) {
      this.setCurrentNodeSync(container, this.getRoot(container));
   }

   public void switchComboRootPreservingAction(SkillContainer container) {
      DMCPlayer dmcPlayer = DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal());
      ComboNode nextRoot = this.getRoot(container);
      dmcPlayer.setCurrentLogicNode(nextRoot);
   }

   @Deprecated
   public void resetComboForWeaponSwitch(SkillContainer container) {
      this.switchComboRootPreservingAction(container);
   }

   public void resetComboFromClient() {
      DMCNetwork.sendToServer(new CPComboReset(SkillSlots.WEAPON_INNATE));
   }

   public void setCurrentNodeSync(SkillContainer container, ComboNode comboNode) {
      DMCPlayer DMCPlayer = DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal());
      DMCPlayer.setCurrentLogicNode(comboNode);
      DMCPlayer.clear();
      if (!container.getExecutor().isLogicalClient()) {
         sendFeedback(comboNode, container, DMCPlayer);
      }
   }

   public int getResetTime() {
      return this.resetTime == 0 ? (Integer)DMConfig.RESET_TICK.get() : this.resetTime;
   }

   public int getResetTime(SkillContainer container) {
      return this.getResetTime();
   }

   public int getInputBufferDurationTicks() {
      return this.inputBufferDurationTicks == 0 ? (Integer)DMConfig.INPUT_BUFFER_DURATION_TICKS.get() : this.inputBufferDurationTicks;
   }

   public int getInputBufferCapacity() {
      return this.inputBufferCapacity == 0 ? (Integer)DMConfig.INPUT_BUFFER_CAPACITY.get() : this.inputBufferCapacity;
   }

   public ComboNode getRoot() {
      return this.root;
   }

   public ComboNode getRoot(SkillContainer container) {
      return this.root;
   }

   private boolean isInComboInterruptWindow(SkillContainer container) {
      return container.getExecutor().getEntityState().canBasicAttack() ? false : ComboInterruptWindowCondition.check(container.getExecutor());
   }

   private static boolean hasComboInterruptCondition(ComboNode node) {
      for (Condition c : node.getConditions(Side.SERVER, Side.BOTH)) {
         if (c instanceof ComboInterruptWindowCondition) {
            return true;
         }
      }

      for (Condition cx : node.getConditions(Side.CLIENT)) {
         if (cx instanceof ComboInterruptWindowCondition) {
            return true;
         }
      }

      for (ComboNode child : node.getConditionNodes()) {
         if (hasComboInterruptCondition(child)) {
            return true;
         }
      }

      return false;
   }

   public static class Builder extends SkillBuilder<ComboBasicAttack> {
      protected ComboNode root;
      protected List<String> translationKeys = List.of();
      protected int resetTime;
      protected int inputBufferDurationTicks;
      protected int inputBufferCapacity;
      @Nullable
      protected AnimationAccessor<? extends StaticAnimation> walkBegin;
      @Nullable
      protected AnimationAccessor<? extends StaticAnimation> walkEnd;
      protected boolean shouldDrawGui;
      protected ResourceLocation skillTextureLocation;
      protected boolean allowJumpCancel;

      public ComboBasicAttack.Builder setInputBufferDurationTicks(int inputBufferDurationTicks) {
         this.inputBufferDurationTicks = inputBufferDurationTicks;
         return this;
      }

      @Deprecated
      public ComboBasicAttack.Builder setReserveTime(int inputBufferDurationTicks) {
         return this.setInputBufferDurationTicks(inputBufferDurationTicks);
      }

      public ComboBasicAttack.Builder setInputBufferCapacity(int inputBufferCapacity) {
         this.inputBufferCapacity = inputBufferCapacity;
         return this;
      }

      public ComboBasicAttack.Builder setResetTime(int resetTime) {
         this.resetTime = resetTime;
         return this;
      }

      public ComboBasicAttack.Builder setCategory(SkillCategory category) {
         this.category = category;
         return this;
      }

      public ComboBasicAttack.Builder setActivateType(ActivateType activateType) {
         this.activateType = activateType;
         return this;
      }

      public ComboBasicAttack.Builder setResource(Resource resource) {
         this.resource = resource;
         return this;
      }

      public ComboBasicAttack.Builder setCombo(ComboNode root) {
         this.root = root;
         return this;
      }

      public ComboBasicAttack.Builder setShouldDrawGui(boolean shouldDrawGui) {
         this.shouldDrawGui = shouldDrawGui;
         return this;
      }

      public ComboBasicAttack.Builder setWalkBeginAnim(AnimationAccessor<? extends StaticAnimation> walkBegin) {
         this.walkBegin = walkBegin;
         return this;
      }

      public ComboBasicAttack.Builder setWalkEndAnim(AnimationAccessor<? extends StaticAnimation> walkEnd) {
         this.walkEnd = walkEnd;
         return this;
      }

      public ComboBasicAttack.Builder addToolTipOnItem(List<String> translationKeys) {
         this.translationKeys = translationKeys;
         return this;
      }

      public ComboBasicAttack.Builder setSkillTextureLocation(ResourceLocation skillTextureLocation) {
         this.skillTextureLocation = skillTextureLocation;
         return this;
      }

      public ComboBasicAttack.Builder setAllowJumpCancel(boolean allowJumpCancel) {
         this.allowJumpCancel = allowJumpCancel;
         return this;
      }
   }
}
