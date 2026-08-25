package com.dmc.invincible_dmc.capability.weapon;

import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;

public final class WeaponActionSession {
   private final long sessionId;
   private final DmcWeaponType ownerWeapon;
   private final WeaponActionType actionType;
   private final int sourceNodeId;
   private final int inputKeyIndex;
   private final long startedTick;
   private WeaponActionStage stage;
   private int stageRevision;
   private int actionStep;

   public WeaponActionSession(
      long sessionId, DmcWeaponType ownerWeapon, WeaponActionType actionType, int sourceNodeId, int inputKeyIndex, long startedTick, WeaponActionStage stage
   ) {
      this(sessionId, ownerWeapon, actionType, sourceNodeId, inputKeyIndex, startedTick, stage, 0);
   }

   public WeaponActionSession(
      long sessionId,
      DmcWeaponType ownerWeapon,
      WeaponActionType actionType,
      int sourceNodeId,
      int inputKeyIndex,
      long startedTick,
      WeaponActionStage stage,
      int stageRevision
   ) {
      this(sessionId, ownerWeapon, actionType, sourceNodeId, inputKeyIndex, startedTick, stage, stageRevision, 0);
   }

   public WeaponActionSession(
      long sessionId,
      DmcWeaponType ownerWeapon,
      WeaponActionType actionType,
      int sourceNodeId,
      int inputKeyIndex,
      long startedTick,
      WeaponActionStage stage,
      int stageRevision,
      int actionStep
   ) {
      this.sessionId = sessionId;
      this.ownerWeapon = Objects.requireNonNull(ownerWeapon);
      this.actionType = Objects.requireNonNull(actionType);
      this.sourceNodeId = sourceNodeId;
      this.inputKeyIndex = inputKeyIndex;
      this.startedTick = startedTick;
      this.stage = Objects.requireNonNull(stage);
      this.stageRevision = Math.max(0, stageRevision);
      this.actionStep = Math.max(0, actionStep);
   }

   public long sessionId() {
      return this.sessionId;
   }

   public DmcWeaponType ownerWeapon() {
      return this.ownerWeapon;
   }

   public WeaponActionType actionType() {
      return this.actionType;
   }

   public int sourceNodeId() {
      return this.sourceNodeId;
   }

   public int inputKeyIndex() {
      return this.inputKeyIndex;
   }

   public long startedTick() {
      return this.startedTick;
   }

   public WeaponActionStage stage() {
      return this.stage;
   }

   public int stageRevision() {
      return this.stageRevision;
   }

   public int actionStep() {
      return this.actionStep;
   }

   public boolean belongsTo(DmcWeaponType weaponType, WeaponActionType type) {
      return this.ownerWeapon == weaponType && this.actionType == type && !this.stage.isTerminal();
   }

   public void transitionTo(WeaponActionStage nextStage) {
      if (!this.stage.isTerminal() && nextStage != this.stage) {
         this.stage = Objects.requireNonNull(nextStage);
         this.stageRevision++;
      }
   }

   public boolean advanceActionStep(int expectedStep) {
      if (!this.stage.isTerminal() && expectedStep == this.actionStep + 1) {
         this.actionStep = expectedStep;
         return true;
      } else {
         return false;
      }
   }

   public CompoundTag save(CompoundTag tag) {
      tag.m_128356_("id", this.sessionId);
      tag.m_128405_("owner", this.ownerWeapon.networkId());
      tag.m_128405_("type", this.actionType.ordinal());
      tag.m_128405_("node", this.sourceNodeId);
      tag.m_128405_("key", this.inputKeyIndex);
      tag.m_128356_("started", this.startedTick);
      tag.m_128405_("stage", this.stage.ordinal());
      tag.m_128405_("revision", this.stageRevision);
      tag.m_128405_("step", this.actionStep);
      return tag;
   }

   public static WeaponActionSession load(CompoundTag tag) {
      WeaponActionType[] types = WeaponActionType.values();
      WeaponActionStage[] stages = WeaponActionStage.values();
      int typeId = tag.m_128451_("type");
      int stageId = tag.m_128451_("stage");
      return new WeaponActionSession(
         tag.m_128454_("id"),
         DmcWeaponType.byNetworkId(tag.m_128451_("owner")),
         typeId >= 0 && typeId < types.length ? types[typeId] : WeaponActionType.BASIC_COMBO,
         tag.m_128451_("node"),
         tag.m_128451_("key"),
         tag.m_128454_("started"),
         stageId >= 0 && stageId < stages.length ? stages[stageId] : WeaponActionStage.CANCELLED,
         tag.m_128451_("revision"),
         tag.m_128451_("step")
      );
   }
}
