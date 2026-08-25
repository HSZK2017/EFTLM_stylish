package com.dmc.invincible_dmc.entity.doppelganger;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.HitExtendRuntime;
import com.dmc.invincible_dmc.api.skill.IHitExtendNode;
import com.dmc.invincible_dmc.api.skill.ITapHoldNode;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.ArrayDeque;
import javax.annotation.Nullable;

public class DoppelgangerComboState {
   @Nullable
   ComboNode currentLogicNode;
   @Nullable
   ComboNode currentDataNode = ComboNode.EMPTY;
   int comboKeyIndex;
   int comboResetTicks = -1;
   int defaultResetTicks = -1;
   long lastExecutionTick;
   long lastInputEngineTick = -1L;
   @Nullable
   ITapHoldNode activeTapHoldNode;
   final HitExtendRuntime hitExtendRuntime = new HitExtendRuntime();
   int windupStartTick;
   private final ArrayDeque<Long> processedEngineTicks = new ArrayDeque<>(128);
   private static final int MAX_PROCESSED_TICKS = 128;

   public void clear() {
      DMCLog.info(
         DMCLog.Category.DOPPEL_COMBO,
         "[DoppelSvr] comboState.clear() before: logic={} data={} keyIdx={} reset={}/{} lastExec={}",
         this.currentLogicNode != null ? this.currentLogicNode.getId() : "null",
         this.currentDataNode != null ? this.currentDataNode.getId() : "null",
         this.comboKeyIndex,
         this.comboResetTicks,
         this.defaultResetTicks,
         this.lastExecutionTick
      );
      this.currentLogicNode = null;
      this.currentDataNode = ComboNode.EMPTY;
      this.comboKeyIndex = 0;
      this.comboResetTicks = -1;
      this.activeTapHoldNode = null;
      this.hitExtendRuntime.clear();
      this.windupStartTick = 0;
      this.lastExecutionTick = 0L;
      this.lastInputEngineTick = -1L;
      this.processedEngineTicks.clear();
   }

   public void addProcessedEngineTick(long tick) {
      while (this.processedEngineTicks.size() >= 128) {
         this.processedEngineTicks.pollFirst();
      }

      this.processedEngineTicks.addLast(tick);
   }

   public boolean hasProcessedEngineTick(long tick) {
      return this.processedEngineTicks.contains(tick);
   }

   @Nullable
   public ComboNode getCurrentLogicNode() {
      return this.currentLogicNode;
   }

   public void setCurrentLogicNode(@Nullable ComboNode node) {
      this.currentLogicNode = node;
   }

   @Nullable
   public ComboNode getCurrentDataNode() {
      return this.currentDataNode;
   }

   public void setCurrentDataNode(@Nullable ComboNode node) {
      this.currentDataNode = node;
   }

   public int getComboKeyIndex() {
      return this.comboKeyIndex;
   }

   public void setComboKeyIndex(int index) {
      this.comboKeyIndex = index;
   }

   public int getComboResetTicks() {
      return this.comboResetTicks;
   }

   public void setComboResetTicks(int ticks) {
      this.comboResetTicks = ticks;
   }

   public int getDefaultResetTicks() {
      return this.defaultResetTicks;
   }

   public void setDefaultResetTicks(int ticks) {
      this.defaultResetTicks = ticks;
   }

   public long getLastExecutionTick() {
      return this.lastExecutionTick;
   }

   public void setLastExecutionTick(long tick) {
      this.lastExecutionTick = tick;
   }

   public long getLastInputEngineTick() {
      return this.lastInputEngineTick;
   }

   public void setLastInputEngineTick(long tick) {
      this.lastInputEngineTick = tick;
   }

   @Nullable
   public ITapHoldNode getActiveTapHoldNode() {
      return this.activeTapHoldNode;
   }

   public void setActiveTapHoldNode(@Nullable ITapHoldNode node) {
      this.activeTapHoldNode = node;
   }

   @Nullable
   public IHitExtendNode getActiveHitExtendNode() {
      return this.hitExtendRuntime.getActiveNode();
   }

   public void setActiveHitExtendNode(@Nullable IHitExtendNode node) {
      if (node == null) {
         this.hitExtendRuntime.clear();
      } else {
         this.hitExtendRuntime.begin(node, this.hitExtendRuntime.getStartTick());
      }
   }

   public int getWindupStartTick() {
      return this.windupStartTick;
   }

   public void setWindupStartTick(int tick) {
      this.windupStartTick = tick;
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
}
