package com.dmc.invincible_dmc.client.input;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.api.events.Side;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.api.skill.IHitExtendNode;
import com.dmc.invincible_dmc.api.skill.ITapHoldNode;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.client.input.crazyCombo.ClientCrazyComboController;
import com.dmc.invincible_dmc.client.input.judegementCut.ClientJudgementCutController;
import com.dmc.invincible_dmc.client.input.judegementCut.JudgementCutAnimationHelper;
import com.dmc.invincible_dmc.client.input.summonedSword.SummonedSwordInputController;
import com.dmc.invincible_dmc.conditions.ComboInterruptWindowCondition;
import com.dmc.invincible_dmc.network.client.CPPlayerInputEvent;
import com.dmc.invincible_dmc.skill.dodge.VergilDodgeSkill;
import com.dmc.invincible_dmc.skill.weapon_combo.Yamato;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch.PlayerMode;

@OnlyIn(Dist.CLIENT)
public final class ComboExecutionDispatcher implements IComboExecutor {
   private static final boolean debugLog = false;
   private final LocalPlayerPatch executorPatch;
   private final Deque<IComboExecutor.ReservedIntent> reservedInputs = new ArrayDeque<>();
   private final Deque<ComboExecutionDispatcher.DodgeReserved> dodgeReserved = new ArrayDeque<>();
   private final DirectionTracker directionTracker = new DirectionTracker();
   private final ClientJudgementCutController jcController;
   private final ClientCrazyComboController ccController;
   private final SummonedSwordInputController ssController;
   private long nextComboRequestSequence;
   private int jumpBufferTicks = 0;
   private boolean prevJumpKeyDown = false;

   public ComboExecutionDispatcher(LocalPlayerPatch executorPatch) {
      this.executorPatch = executorPatch;
      this.jcController = new ClientJudgementCutController(this);
      this.ccController = new ClientCrazyComboController(this);
      this.ssController = new SummonedSwordInputController(this);
   }

   @Override
   public LocalPlayerPatch getExecutorPatch() {
      return this.executorPatch;
   }

   @Override
   public ClientJudgementCutController getJudgementCutController() {
      return this.jcController;
   }

   @Override
   public ClientCrazyComboController getCrazyComboController() {
      return this.ccController;
   }

   @Override
   public SummonedSwordInputController getSummonedSwordController() {
      return this.ssController;
   }

   @Override
   public DirectionTracker getDirectionTracker() {
      return this.directionTracker;
   }

   @Override
   public Deque<IComboExecutor.ReservedIntent> getReservedInputs() {
      return this.reservedInputs;
   }

   @Nullable
   @Override
   public ComboNode getCurrentNode() {
      DMCPlayer ip = DMCPlayerCapabilityProvider.get((Player)this.executorPatch.getOriginal());
      return ip.getCurrentLogicNode();
   }

   @Nullable
   @Override
   public ComboNode getComboRoot() {
      ComboBasicAttack skill = this.getComboBasicSkill();
      return skill != null ? skill.getRoot(this.executorPatch.getSkill(SkillSlots.WEAPON_INNATE)) : null;
   }

   @Override
   public void dispatchIntent(ComboIntentResolver.ComboInputIntent intent) {
      ComboRoutePlanner.ComboRoute route = ComboRoutePlanner.routeIntent(this.getCurrentNode(), intent);
      if (Minecraft.m_91087_().f_91080_ == null) {
         boolean canBA = this.executorPatch.getEntityState().canBasicAttack();
         boolean interruptWindow = !canBA && this.isInComboInterruptWindow();
         String pathLabel = canBA ? "DIRECT" : (interruptWindow ? "INTERRUPT_WINDOW" : "PREINPUT");
         DMCLog.info(
            DMCLog.Category.COMBO_EXECUTE,
            "[3Layer] tick={} type={} route={} canBA={} path={} node={}",
            intent.captureTick(),
            intent.type(),
            route != null,
            canBA,
            pathLabel,
            this.getCurrentNode() != null ? this.getCurrentNode().getId() : "null"
         );
         if (this.isLocalPlayer()) {
            CPPlayerInputEvent.send(intent);
         }

         if (route == null || !this.executeRoute(route, false)) {
            if (route != null) {
               DMCPlayer ip = DMCPlayerCapabilityProvider.get((Player)this.executorPatch.getOriginal());
               ComboNode dataNode = ip.getCurrentDataNode();
               if (dataNode != null && !dataNode.isAllowBuffer()) {
                  return;
               }
            } else if (canBA && this.isLocalPlayer()) {
               DMCLog.info(
                  DMCLog.Category.COMBO_EXECUTE,
                  "[NetSend] SKIP CPSKILL type={} route=null (no route for player, doppel fed by CPPlayerInputEvent)",
                  intent.type()
               );
            }

            this.reserveIntent(intent);
         }
      }
   }

   public boolean executeRoute(ComboRoutePlanner.ComboRoute route, boolean fromReserved) {
      if (this.executorPatch.getPlayerMode() != PlayerMode.EPICFIGHT) {
         return false;
      } else if (this.isJumpCancelExecutable()) {
         return false;
      } else if (this.isLocalPlayer() && this.jcController.isChargeSuspended() && route.intent().type() == ComboNode.ComboTypes.KEY_1) {
         return false;
      } else {
         SkillContainer container = this.executorPatch.getSkill(SkillSlots.WEAPON_INNATE);
         boolean sdtRapidSlashLoopRequest = route.intent().type() == ComboNode.ComboTypes.KEY_1
            && Yamato.canRequestSdtRapidSlashLoop(this.executorPatch, route.nextNode());
         if (!this.executorPatch.getEntityState().canBasicAttack() && !sdtRapidSlashLoopRequest) {
            if (!this.isInComboInterruptWindow()) {
               return false;
            }

            if (!invincible_dmc$anyNodeHasComboInterruptCondition(route.nextNode())) {
               return false;
            }
         }

         if (this.isLocalPlayer()) {
            DMCPlayer dmcPlayer = DMCPlayerCapabilityProvider.get((Player)this.executorPatch.getOriginal());
            long requestSequence = ++this.nextComboRequestSequence;
            int sourceDataNodeId = nodeIdOrMissing(dmcPlayer.getCurrentDataNode());
            DMCLog.info(
               DMCLog.Category.COMBO_EXECUTE,
               "[NetSend] CPSKILL seq={} sourceLogic={} sourceData={} target={} type={} pressedMs={} interval={} tick={} canBasicAttack={} interruptWindow={} fromReserved={}",
               requestSequence,
               route.currentNode().getId(),
               sourceDataNodeId,
               route.nextNode().getId(),
               route.intent().type(),
               route.intent().pressDuration(),
               route.intent().pressIntervalMs(),
               route.captureTick(),
               this.executorPatch.getEntityState().canBasicAttack(),
               this.isInComboInterruptWindow(),
               fromReserved
            );
            CPSkillRequest packet = getExecutePacket(container.getSlotId(), route, fromReserved, requestSequence, sourceDataNodeId);
            EpicFightNetworkManager.sendToServer(packet);
         } else {
            this.triggerDoppelgangerLocalExecution(route);
         }

         DMCPlayer ip = DMCPlayerCapabilityProvider.get((Player)this.executorPatch.getOriginal());
         if (route.nextNode() instanceof ITapHoldNode thNode) {
            ip.setActiveTapHoldNode(thNode);
         }

         if (route.nextNode() instanceof IHitExtendNode heNode) {
            ip.beginHitExtend(heNode, ((LocalPlayer)this.executorPatch.getOriginal()).f_19797_);
         }

         if (this.isLocalPlayer()) {
            DMComboEngine.recordDispatch(route.intent().type());
         }

         this.clearReserve();
         return true;
      }
   }

   private void triggerDoppelgangerLocalExecution(ComboRoutePlanner.ComboRoute route) {
   }

   private boolean isLocalPlayer() {
      return this.executorPatch.getOriginal() == Minecraft.m_91087_().f_91074_;
   }

   public void clearReservedInputs() {
      this.reservedInputs.clear();
      this.dodgeReserved.clear();
   }

   private void reserveIntent(ComboIntentResolver.ComboInputIntent intent) {
      ComboBasicAttack skill = this.getComboBasicSkill();
      int duration = this.getReserveDuration(skill, intent);
      int capacity = this.getReserveCapacity(skill);

      while (this.reservedInputs.size() >= capacity) {
         IComboExecutor.ReservedIntent var5 = this.reservedInputs.pollFirst();
      }

      this.reservedInputs.removeIf(reserved -> reserved.intent().type() == intent.type());
      this.reservedInputs.addLast(new IComboExecutor.ReservedIntent(intent, duration, duration, false));
      DMCLog.info(
         DMCLog.Category.COMBO_EXECUTE,
         "[Reserve] STORED type={} duration={} ticks queueSize={} canBA={}",
         intent.type(),
         duration,
         this.reservedInputs.size(),
         this.executorPatch.getEntityState().canBasicAttack()
      );
   }

   @Override
   public void tickReserve() {
      boolean hasAny = !this.reservedInputs.isEmpty() || !this.dodgeReserved.isEmpty();
      if (hasAny) {
         int entries = this.reservedInputs.size();
         boolean busy = this.isContainerBusy();

         for (int i = 0; i < entries; i++) {
            IComboExecutor.ReservedIntent reserved = this.reservedInputs.pollFirst();
            if (reserved != null) {
               if (reserved.remainingTicks() <= 0) {
                  DMCLog.info(
                     DMCLog.Category.COMBO_EXECUTE,
                     "[Reserve] EXPIRED type={} remaining={} canBA={} node={}",
                     reserved.intent().type(),
                     reserved.remainingTicks(),
                     this.executorPatch.getEntityState().canBasicAttack(),
                     this.getCurrentNode() != null ? this.getCurrentNode().getId() : "null"
                  );
               } else {
                  ComboRoutePlanner.ComboRoute route = ComboRoutePlanner.routeIntent(this.getCurrentNode(), reserved.intent());
                  if (route != null && this.executeRoute(route, true)) {
                     DMCLog.info(
                        DMCLog.Category.COMBO_EXECUTE,
                        "[Reserve] FIRED type={} remaining={} canBA={} node={} -> next={}",
                        reserved.intent().type(),
                        reserved.remainingTicks(),
                        this.executorPatch.getEntityState().canBasicAttack(),
                        route.currentNode() != null ? route.currentNode().getId() : "null",
                        route.nextNode() != null ? route.nextNode().getId() : "null"
                     );
                     return;
                  }

                  if (route != null && reserved.remainingTicks() <= 2) {
                     DMCLog.info(
                        DMCLog.Category.COMBO_EXECUTE,
                        "[Reserve] BLOCKED type={} remaining={} canBA={} busy={} node={}",
                        reserved.intent().type(),
                        reserved.remainingTicks(),
                        this.executorPatch.getEntityState().canBasicAttack(),
                        busy,
                        this.getCurrentNode() != null ? this.getCurrentNode().getId() : "null"
                     );
                  }

                  IComboExecutor.ReservedIntent nextState = reserved.tick();
                  if (route != null) {
                     nextState = nextState.markRouted();
                  }

                  this.reservedInputs.addLast(nextState);
               }
            }
         }

         this.tryFireDodgeBuffer();
      }
   }

   @Override
   public void tickJumpCancel(long engineTick) {
      if (this.jumpBufferTicks > 0) {
         this.jumpBufferTicks--;
      }

      if (this.executorPatch.getEntityState().inaction()) {
         boolean jumpDown = this.isLocalPlayer() && Minecraft.m_91087_().f_91066_.f_92089_.m_90857_();
         boolean jumpJustPressed = jumpDown && !this.prevJumpKeyDown;
         this.prevJumpKeyDown = jumpDown;
         if (jumpJustPressed) {
            while (Minecraft.m_91087_().f_91066_.f_92089_.m_90859_()) {
            }

            ComboBasicAttack skill = this.getComboBasicSkill();
            this.jumpBufferTicks = skill != null ? skill.getInputBufferDurationTicks() : (Integer)DMConfig.INPUT_BUFFER_DURATION_TICKS.get();
         }
      } else {
         this.prevJumpKeyDown = false;
      }
   }

   @Override
   public boolean isJumpCancelExecutable() {
      if (this.jumpBufferTicks <= 0) {
         return false;
      } else if (!this.executorPatch.getEntityState().inaction()) {
         return false;
      } else if (!this.executorPatch.getEntityState().canBasicAttack() && !this.isInJumpCancelWindow()) {
         return false;
      } else {
         ComboBasicAttack comboBasicAttack = this.getComboBasicSkill();
         return comboBasicAttack != null && comboBasicAttack.isAllowJumpCancel();
      }
   }

   private boolean isInJumpCancelWindow() {
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(this.executorPatch);
      if (player != null && !player.isEmpty()) {
         DynamicAnimation anim = DMCAnimationUtils.getCurrentAnimation(player);
         if (anim == null) {
            return false;
         } else {
            StaticAnimation real = DMCAnimationUtils.getRealAnimation(anim);
            if (real == null) {
               return false;
            } else {
               Optional<TimePairList> prop = real.getProperty(YamatoAttackAnimation.JUMP_CANCEL_TIME);
               return prop.isPresent() && prop.get().isTimeInPairs(player.getElapsedTime());
            }
         }
      } else {
         return false;
      }
   }

   private static boolean invincible_dmc$anyNodeHasComboInterruptCondition(ComboNode node) {
      if (node == null) {
         return false;
      } else {
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
            if (invincible_dmc$anyNodeHasComboInterruptCondition(child)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean isInComboInterruptWindow() {
      return ComboInterruptWindowCondition.check(this.executorPatch);
   }

   @Nullable
   public ComboBasicAttack getComboBasicSkill() {
      Skill var2 = this.executorPatch.getSkill(SkillSlots.WEAPON_INNATE).getSkill();
      return var2 instanceof ComboBasicAttack ? (ComboBasicAttack)var2 : null;
   }

   private int getReserveDuration(@Nullable ComboBasicAttack skill, ComboIntentResolver.ComboInputIntent intent) {
      int animationOverride = this.getCurrentAnimationBufferDuration();
      if (animationOverride >= 0) {
         return intent.isLongPress() ? Math.max(animationOverride, (Integer)DMConfig.LONG_PRESS_THRESHOLD.get()) : animationOverride;
      } else {
         int nodeOverride = this.getCurrentDataNodeBufferDuration();
         if (nodeOverride >= 0) {
            return intent.isLongPress() ? Math.max(nodeOverride, (Integer)DMConfig.LONG_PRESS_THRESHOLD.get()) : nodeOverride;
         } else {
            int dodgeOverride = this.getDodgeSkillBufferDuration();
            int base = dodgeOverride >= 0
               ? dodgeOverride
               : (skill != null ? skill.getInputBufferDurationTicks() : (Integer)DMConfig.INPUT_BUFFER_DURATION_TICKS.get());
            return intent.isLongPress() ? Math.max(base, (Integer)DMConfig.LONG_PRESS_THRESHOLD.get()) : base;
         }
      }
   }

   private int getCurrentAnimationBufferDuration() {
      StaticAnimation animation = DMCAnimationUtils.getRealAnimation(this.executorPatch);
      return animation != null ? animation.getProperty(YamatoAttackAnimation.INPUT_BUFFER_DURATION_TICKS).orElse(-1) : -1;
   }

   private int getCurrentDataNodeBufferDuration() {
      DMCPlayer ip = DMCPlayerCapabilityProvider.get((Player)this.executorPatch.getOriginal());
      ComboNode dataNode = ip.getCurrentDataNode();
      int dataBuffer = dataNode != null ? dataNode.getBufferDurationTicks() : -1;
      if (dataBuffer >= 0) {
         return dataBuffer;
      } else {
         ComboNode logicNode = ip.getCurrentLogicNode();
         return logicNode != null ? logicNode.getBufferDurationTicks() : -1;
      }
   }

   private int getDodgeSkillBufferDuration() {
      if (this.executorPatch.getSkill(SkillSlots.DODGE).getSkill() instanceof VergilDodgeSkill yds
         && yds.getDodgeBufferDurationTicks() >= 0
         && yds.isDodgeAnimation(JudgementCutAnimationHelper.getCurrentAnimation(this.executorPatch))) {
         return yds.getDodgeBufferDurationTicks();
      }

      return -1;
   }

   private int getReserveCapacity(@Nullable ComboBasicAttack skill) {
      return Math.max(1, skill != null ? skill.getInputBufferCapacity() : (Integer)DMConfig.INPUT_BUFFER_CAPACITY.get());
   }

   private boolean isContainerBusy() {
      SkillContainer container = this.executorPatch.getSkill(SkillSlots.WEAPON_INNATE);
      return !container.isEmpty() && !this.executorPatch.getEntityState().canBasicAttack();
   }

   @Override
   public boolean isReserved(ComboType type) {
      return this.reservedInputs.stream().anyMatch(reserved -> reserved.intent().type() == type);
   }

   @Override
   public void clearReserve() {
      this.reservedInputs.clear();
      this.dodgeReserved.clear();
   }

   @Override
   public void consumeJumpBuffer() {
      this.jumpBufferTicks = 0;
   }

   @Override
   public void resetCrazyCombo() {
      this.ccController.reset();
   }

   @Override
   public int getJumpBufferTicks() {
      return this.jumpBufferTicks;
   }

   static CPSkillRequest getExecutePacket(int slotId, ComboRoutePlanner.ComboRoute route, boolean fromPreInput, long requestSequence, int sourceDataNodeId) {
      CPSkillRequest packet = new CPSkillRequest(SkillSlots.WEAPON_INNATE);
      ComboIntentResolver.ComboInputIntent intent = route.intent();
      packet.getBuffer().writeInt(intent.type().universalOrdinal());
      packet.getBuffer().writeInt(intent.pressDuration());
      packet.getBuffer().writeLong(intent.pressIntervalMs());
      packet.getBuffer().writeBoolean(intent.isLongPress());
      packet.getBuffer().writeInt(route.directionMask());
      writeDirectionEvents(packet, route.directionEvents());
      packet.getBuffer().writeLong(route.captureTick());
      packet.getBuffer().writeBoolean(fromPreInput);
      packet.getBuffer().writeLong(requestSequence);
      packet.getBuffer().writeInt(route.currentNode().getId());
      packet.getBuffer().writeInt(sourceDataNodeId);
      return packet;
   }

   private static int nodeIdOrMissing(@Nullable ComboNode node) {
      return node != null && node != ComboNode.EMPTY ? node.getId() : -1;
   }

   private static void writeDirectionEvents(CPSkillRequest packet, List<DirectionTracker.DirectionEvent> events) {
      int count = Math.min(events.size(), 16);
      packet.getBuffer().writeByte(count);

      for (int i = 0; i < count; i++) {
         DirectionTracker.DirectionEvent event = events.get(i);
         packet.getBuffer().writeByte(event.direction().ordinal());
         packet.getBuffer().writeLong(event.tick());
      }
   }

   @NotNull
   @Override
   public DMCPlayer getInvinciblePlayer() {
      return DMCPlayerCapabilityProvider.get((Player)this.executorPatch.getOriginal());
   }

   @Nullable
   @Override
   public ComboNode getCurrentLogicNode() {
      DMCPlayer ip = this.getInvinciblePlayer();
      return ip.getCurrentLogicNode();
   }

   @Nullable
   @Override
   public ComboNode getCurrentDataNode() {
      DMCPlayer ip = this.getInvinciblePlayer();
      return ip.getCurrentDataNode();
   }

   @Override
   public void setCurrentDataNode(@Nullable ComboNode node) {
      DMCPlayer ip = this.getInvinciblePlayer();
      ip.setCurrentDataNode(node);
   }

   @Override
   public int getComboKeyIndex() {
      DMCPlayer ip = this.getInvinciblePlayer();
      return ip.getComboKeyIndex();
   }

   public void bufferDodge(Object packet, int durationTicks) {
      this.dodgeReserved.removeIf(d -> true);
      this.dodgeReserved.addLast(new ComboExecutionDispatcher.DodgeReserved(packet, durationTicks));
      DMCLog.info(DMCLog.Category.COMBO_EXECUTE, "[DodgeBuffer] BUFFERED duration={}", durationTicks);
   }

   private void tryFireDodgeBuffer() {
      if (!this.dodgeReserved.isEmpty()) {
         ComboExecutionDispatcher.DodgeReserved d = this.dodgeReserved.pollFirst();
         if (d != null) {
            if (d.remainingTicks() <= 0 || !this.isLocalPlayer()) {
               DMCLog.info(DMCLog.Category.COMBO_EXECUTE, "[DodgeBuffer] EXPIRED remaining={}", d.remainingTicks());
            } else if (!this.executorPatch.getEntityState().canBasicAttack()) {
               this.dodgeReserved.addLast(d.tick());
            } else {
               DMCLog.info(DMCLog.Category.COMBO_EXECUTE, "[DodgeBuffer] FIRED remaining={}/{}, canBA=true", d.remainingTicks(), d.totalTicks());
               EpicFightNetworkManager.sendToServer(d.packet());
            }
         }
      }
   }

   static record DodgeReserved(Object packet, int totalTicks, int remainingTicks) {
      DodgeReserved(Object packet, int totalTicks) {
         this(packet, totalTicks, totalTicks);
      }

      ComboExecutionDispatcher.DodgeReserved tick() {
         return new ComboExecutionDispatcher.DodgeReserved(this.packet, this.totalTicks, this.remainingTicks - 1);
      }
   }
}
