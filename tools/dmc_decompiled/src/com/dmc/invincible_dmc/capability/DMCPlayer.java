package com.dmc.invincible_dmc.capability;

import com.dmc.invincible_dmc.api.events.BaseEvent;
import com.dmc.invincible_dmc.api.events.TimePeriodEvent;
import com.dmc.invincible_dmc.api.events.TimeStampedEvent;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboNodeManager;
import com.dmc.invincible_dmc.api.skill.HitExtendRuntime;
import com.dmc.invincible_dmc.api.skill.ICrazyComboNode;
import com.dmc.invincible_dmc.api.skill.IHitExtendNode;
import com.dmc.invincible_dmc.api.skill.ITapHoldNode;
import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionSession;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionStage;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionType;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.world.damagesource.StunType;

public class DMCPlayer {
   public static final DMCPlayer EMPTY = new DMCPlayer();
   private final Map<ItemStack, Integer> cooldownMap = new HashMap<>();
   private ComboNode currentLogicNode = null;
   private ComboNode currentDataNode = ComboNode.EMPTY;
   private int phase;
   private int comboResetTicks = -1;
   private long lastNodeExecutionTick;
   private long lastComboRequestSequence = -1L;
   private int comboKeyIndex;
   @Nullable
   private ITapHoldNode activeTapHoldNode;
   private final HitExtendRuntime hitExtendRuntime = new HitExtendRuntime();
   @Nullable
   private ComboNode activeCrazyComboNode;
   private int windupStartTick;
   private long nextActionSessionId;
   @Nullable
   private WeaponActionSession actionSession;
   private boolean instantJudgementCutEndEnabled = true;
   private final Deque<Long> comboPath = new ArrayDeque<>();
   private boolean aerialAttackPending;

   public void addComboPathNode(long nodeId) {
      this.comboPath.addLast(nodeId);
      if (this.comboPath.size() > 10) {
         this.comboPath.pollFirst();
      }
   }

   public void clearComboPath() {
      this.comboPath.clear();
   }

   public Deque<Long> getComboPath() {
      return this.comboPath;
   }

   public void setItemCooldown(ItemStack item, int cooldown) {
      this.cooldownMap.put(item, cooldown);
   }

   public boolean isItemInCooldown(ItemStack item) {
      return this.cooldownMap.containsKey(item) && this.cooldownMap.get(item) >= 0;
   }

   public int getItemCooldown(ItemStack item) {
      return this.cooldownMap.getOrDefault(item, 0);
   }

   public float getArmorNegation() {
      return this.currentDataNode.getArmorNegation();
   }

   public float getHurtDamageMultiplier() {
      return this.currentDataNode.getHurtDamageMultiplier();
   }

   public ValueModifier getDamageMultiplier() {
      return this.currentDataNode.getDamageMultiplier();
   }

   public float getImpactMultiplier() {
      return this.currentDataNode.getImpactMultiplier();
   }

   public StunType getStunTypeModifier() {
      return this.currentDataNode.getStunTypeModifier();
   }

   public boolean canBeInterrupt() {
      return this.currentDataNode.isCanBeInterrupt();
   }

   public boolean isNotCharge() {
      return this.currentDataNode.isNotCharge();
   }

   @Deprecated(
      forRemoval = true
   )
   public void setNotCharge(boolean notCharge) {
   }

   public float getPlaySpeedMultiplier() {
      return this.currentDataNode.getPlaySpeed();
   }

   @Nullable
   public List<TimeStampedEvent> getTimeEventList() {
      return this.currentDataNode.getTimeEvents();
   }

   @Nullable
   public List<BaseEvent> getDodgeSuccessEvents() {
      return this.currentDataNode.getDodgeSuccessEvents();
   }

   @Nullable
   public List<BaseEvent> getHurtEvents() {
      return this.currentDataNode.getHurtEvents();
   }

   @Nullable
   public List<BaseEvent> getHitSuccessEvents() {
      return this.currentDataNode.getHitEvents();
   }

   @Nullable
   public List<TimePeriodEvent> getTimePeriodEvents() {
      return this.currentDataNode.getTimePeriodEvents();
   }

   public void resetPhase() {
      this.phase = 0;
   }

   public int getPhase() {
      return this.phase;
   }

   public void setPhase(int phase) {
      if (phase != 0) {
         this.phase = phase;
      }
   }

   public int getComboResetTicks() {
      return this.comboResetTicks;
   }

   public void setComboResetTicks(int comboResetTicks) {
      this.comboResetTicks = comboResetTicks;
   }

   public long getLastNodeExecutionTick() {
      return this.lastNodeExecutionTick;
   }

   public void setLastNodeExecutionTick(long tick) {
      this.lastNodeExecutionTick = tick;
   }

   public boolean tryAcceptComboRequestSequence(long sequence) {
      if (sequence <= this.lastComboRequestSequence) {
         return false;
      } else {
         this.lastComboRequestSequence = sequence;
         return true;
      }
   }

   public long getLastComboRequestSequence() {
      return this.lastComboRequestSequence;
   }

   @Deprecated
   public ComboNode getCurrentNode() {
      return this.currentLogicNode;
   }

   @Deprecated
   public void setCurrentNode(ComboNode currentLogicNode) {
      this.currentLogicNode = currentLogicNode;
   }

   public ComboNode getCurrentLogicNode() {
      return this.currentLogicNode;
   }

   public void setCurrentLogicNode(ComboNode currentLogicNode) {
      this.currentLogicNode = currentLogicNode;
   }

   public ComboNode getCurrentDataNode() {
      return this.currentDataNode;
   }

   public void setCurrentDataNode(ComboNode currentDataNode) {
      this.currentDataNode = currentDataNode;
   }

   public void clear() {
      this.clearComboStatePreservingAction();
      this.activeCrazyComboNode = null;
      this.actionSession = null;
   }

   public void clearComboStatePreservingAction() {
      this.currentDataNode = ComboNode.EMPTY;
      this.comboResetTicks = -1;
      this.lastNodeExecutionTick = 0L;
      this.phase = 0;
      this.activeTapHoldNode = null;
      this.hitExtendRuntime.clear();
      this.windupStartTick = 0;
   }

   public int getComboKeyIndex() {
      return this.comboKeyIndex;
   }

   public void setComboKeyIndex(int comboKeyIndex) {
      this.comboKeyIndex = comboKeyIndex;
   }

   public boolean isJumpDisabled() {
      return false;
   }

   @Nullable
   public ITapHoldNode getActiveTapHoldNode() {
      return this.activeTapHoldNode;
   }

   public void setActiveTapHoldNode(@Nullable ITapHoldNode activeTapHoldNode) {
      this.activeTapHoldNode = activeTapHoldNode;
   }

   public int getWindupStartTick() {
      return this.windupStartTick;
   }

   public void setWindupStartTick(int windupStartTick) {
      this.windupStartTick = windupStartTick;
   }

   public int getHitExtendStartTick() {
      return this.hitExtendRuntime.getStartTick();
   }

   public void setHitExtendStartTick(int tick) {
      IHitExtendNode activeNode = this.hitExtendRuntime.getActiveNode();
      if (activeNode != null) {
         this.hitExtendRuntime.begin(activeNode, tick);
      }
   }

   @Nullable
   public IHitExtendNode getActiveHitExtendNode() {
      return this.hitExtendRuntime.getActiveNode();
   }

   public void setActiveHitExtendNode(@Nullable IHitExtendNode activeHitExtendNode) {
      if (activeHitExtendNode == null) {
         this.hitExtendRuntime.clear();
      } else {
         this.hitExtendRuntime.begin(activeHitExtendNode, this.hitExtendRuntime.getStartTick());
      }
   }

   public void beginHitExtend(IHitExtendNode node, int startTick) {
      this.hitExtendRuntime.begin(node, startTick);
   }

   public boolean latchHitExtend(int currentTick, int targetId) {
      return this.hitExtendRuntime.latchHit(currentTick, targetId);
   }

   public boolean isHitExtendReady(int currentTick) {
      return this.hitExtendRuntime.isReady(currentTick);
   }

   public boolean hasLatchedHitExtend() {
      return this.hitExtendRuntime.hasLatchedHit();
   }

   public int getHitExtendHitTick() {
      return this.hitExtendRuntime.getHitTick();
   }

   public int getHitExtendTargetId() {
      return this.hitExtendRuntime.getTargetId();
   }

   @Nullable
   public ComboNode getActiveCrazyComboNode() {
      if (this.actionSession != null && this.actionSession.actionType() == WeaponActionType.CRAZY_COMBO && !this.actionSession.stage().isTerminal()) {
         ComboNode sessionNode = ComboNodeManager.get(this.actionSession.sourceNodeId());
         if (sessionNode instanceof ICrazyComboNode) {
            return sessionNode;
         }
      }

      return this.activeCrazyComboNode;
   }

   public void setActiveCrazyComboNode(@Nullable ComboNode activeCrazyComboNode) {
      this.activeCrazyComboNode = activeCrazyComboNode;
   }

   public void clearActiveCrazyComboNode() {
      this.activeCrazyComboNode = null;
   }

   public WeaponActionSession beginActionSession(
      DmcWeaponType ownerWeapon, WeaponActionType actionType, int sourceNodeId, int inputKeyIndex, long startedTick, WeaponActionStage initialStage
   ) {
      this.actionSession = new WeaponActionSession(++this.nextActionSessionId, ownerWeapon, actionType, sourceNodeId, inputKeyIndex, startedTick, initialStage);
      return this.actionSession;
   }

   public WeaponActionSession beginCrazyComboActionSession(DmcWeaponType ownerWeapon, ComboNode sourceNode, int inputKeyIndex, long startedTick) {
      if (!(sourceNode instanceof ICrazyComboNode)) {
         throw new IllegalArgumentException("Crazy combo session source must implement ICrazyComboNode");
      } else {
         this.activeCrazyComboNode = sourceNode;
         return this.beginActionSession(ownerWeapon, WeaponActionType.CRAZY_COMBO, sourceNode.getId(), inputKeyIndex, startedTick, WeaponActionStage.STARTUP);
      }
   }

   @Nullable
   public WeaponActionSession getActionSession() {
      return this.actionSession;
   }

   public boolean hasActionSession(DmcWeaponType ownerWeapon, WeaponActionType actionType) {
      return this.actionSession != null && this.actionSession.belongsTo(ownerWeapon, actionType);
   }

   public void transitionActionSession(WeaponActionStage stage) {
      if (this.actionSession != null) {
         this.actionSession.transitionTo(stage);
      }
   }

   public void clearActionSession(WeaponActionStage terminalStage) {
      if (this.actionSession != null) {
         this.actionSession.transitionTo(terminalStage);
         this.actionSession = null;
      }
   }

   public void clearCrazyComboActionSession(WeaponActionStage terminalStage) {
      this.clearActiveCrazyComboNode();
      if (this.actionSession != null && this.actionSession.actionType() == WeaponActionType.CRAZY_COMBO) {
         this.clearActionSession(terminalStage);
      }
   }

   public void setActionSessionMirror(@Nullable WeaponActionSession session) {
      if (session != null && this.actionSession != null) {
         if (session.sessionId() < this.actionSession.sessionId()) {
            return;
         }

         if (session.sessionId() == this.actionSession.sessionId()
            && (
               session.stageRevision() < this.actionSession.stageRevision()
                  || session.stageRevision() == this.actionSession.stageRevision() && session.actionStep() < this.actionSession.actionStep()
            )) {
            return;
         }
      }

      this.actionSession = session;
      if (session != null) {
         this.nextActionSessionId = Math.max(this.nextActionSessionId, session.sessionId());
      }
   }

   public void setAerialAttackPending() {
      this.aerialAttackPending = true;
   }

   public boolean tryConsumeAerialPending() {
      if (this.aerialAttackPending) {
         this.aerialAttackPending = false;
         return true;
      } else {
         return false;
      }
   }

   public boolean isInstantJudgementCutEndEnabled() {
      return this.instantJudgementCutEndEnabled;
   }

   public void setInstantJudgementCutEndEnabled(boolean enabled) {
      this.instantJudgementCutEndEnabled = enabled;
   }

   public CompoundTag saveNBTData(CompoundTag tag) {
      if (this.currentLogicNode != null) {
         tag.m_128405_("currentLogicNodeId", this.currentLogicNode.getId());
      }

      if (this.currentDataNode != null && this.currentDataNode != ComboNode.EMPTY) {
         tag.m_128405_("currentDataNodeId", this.currentDataNode.getId());
      }

      tag.m_128405_("comboKeyIndex", this.comboKeyIndex);
      tag.m_128405_("phase", this.phase);
      if (this.activeTapHoldNode != null) {
         tag.m_128405_("activeTapHoldNodeId", ((ComboNode)this.activeTapHoldNode).getId());
         tag.m_128405_("windupStartTick", this.windupStartTick);
      }

      IHitExtendNode activeHitExtendNode = this.getActiveHitExtendNode();
      if (activeHitExtendNode != null) {
         tag.m_128405_("activeHitExtendNodeId", ((ComboNode)activeHitExtendNode).getId());
         tag.m_128405_("hitExtendStartTick", this.getHitExtendStartTick());
      }

      if (this.activeCrazyComboNode != null) {
         tag.m_128405_("activeCrazyComboNodeId", this.activeCrazyComboNode.getId());
      }

      if (this.actionSession != null) {
         tag.m_128365_("weaponActionSession", this.actionSession.save(new CompoundTag()));
      }

      tag.m_128356_("nextActionSessionId", this.nextActionSessionId);
      tag.m_128379_("instantJudgementCutEndEnabled", this.instantJudgementCutEndEnabled);
      return tag;
   }

   public void loadNBTData(CompoundTag tag) {
      this.activeCrazyComboNode = null;
      this.actionSession = null;
      this.hitExtendRuntime.clear();
      if (tag.m_128441_("currentLogicNodeId")) {
         this.currentLogicNode = ComboNodeManager.get(tag.m_128451_("currentLogicNodeId"));
      }

      if (tag.m_128441_("currentDataNodeId")) {
         this.currentDataNode = ComboNodeManager.get(tag.m_128451_("currentDataNodeId"));
      }

      this.comboKeyIndex = tag.m_128441_("comboKeyIndex") ? tag.m_128451_("comboKeyIndex") : 0;
      this.phase = tag.m_128441_("phase") ? tag.m_128451_("phase") : 0;
      this.instantJudgementCutEndEnabled = !tag.m_128441_("instantJudgementCutEndEnabled") || tag.m_128471_("instantJudgementCutEndEnabled");
      if (tag.m_128441_("activeTapHoldNodeId") && ComboNodeManager.get(tag.m_128451_("activeTapHoldNodeId")) instanceof ITapHoldNode thNode) {
         this.activeTapHoldNode = thNode;
         this.windupStartTick = tag.m_128451_("windupStartTick");
      }

      if (tag.m_128441_("activeHitExtendNodeId") && ComboNodeManager.get(tag.m_128451_("activeHitExtendNodeId")) instanceof IHitExtendNode heNode) {
         int startTick = tag.m_128441_("hitExtendStartTick") ? tag.m_128451_("hitExtendStartTick") : 0;
         this.hitExtendRuntime.begin(heNode, startTick);
      }

      if (tag.m_128441_("activeCrazyComboNodeId")) {
         ComboNode node = ComboNodeManager.get(tag.m_128451_("activeCrazyComboNodeId"));
         if (node instanceof ICrazyComboNode) {
            this.activeCrazyComboNode = node;
         }
      }

      if (tag.m_128441_("weaponActionSession")) {
         this.actionSession = WeaponActionSession.load(tag.m_128469_("weaponActionSession"));
      }

      this.nextActionSessionId = Math.max(this.nextActionSessionId, tag.m_128454_("nextActionSessionId"));
      if (this.actionSession != null) {
         this.nextActionSessionId = Math.max(this.nextActionSessionId, this.actionSession.sessionId());
      }
   }

   public void copyFrom(DMCPlayer old) {
      this.currentLogicNode = old.currentLogicNode;
      this.currentDataNode = old.currentDataNode;
      this.lastNodeExecutionTick = old.lastNodeExecutionTick;
      this.phase = old.phase;
      this.activeTapHoldNode = old.activeTapHoldNode;
      this.hitExtendRuntime.copyFrom(old.hitExtendRuntime);
      this.activeCrazyComboNode = null;
      this.actionSession = null;
      this.nextActionSessionId = old.nextActionSessionId;
      this.windupStartTick = old.windupStartTick;
      this.instantJudgementCutEndEnabled = old.instantJudgementCutEndEnabled;
   }
}
