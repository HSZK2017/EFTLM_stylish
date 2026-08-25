package com.dmc.invincible_dmc.api.skill;

import com.dmc.invincible_dmc.api.events.BaseEvent;
import com.dmc.invincible_dmc.api.events.Side;
import com.dmc.invincible_dmc.api.events.TimePeriodEvent;
import com.dmc.invincible_dmc.api.events.TimeStampedEvent;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

public class ComboNode {
   public static final ComboNode EMPTY = create();
   protected final Map<ComboType, ComboNode> children = new HashMap<>();
   protected final List<TimeStampedEvent> timeStampedEvents = new ArrayList<>();
   protected final List<TimePeriodEvent> timePeriodEvents = new ArrayList<>();
   protected final List<BaseEvent> dodgeSuccessEvents = new ArrayList<>();
   protected final List<BaseEvent> hitEvents = new ArrayList<>();
   protected final List<BaseEvent> hurtEvents = new ArrayList<>();
   protected final List<BaseEvent> onBeginEvents = new ArrayList<>();
   @NotNull
   protected ComboNode root;
   @Nullable
   protected ComboNode parentNode;
   @Nullable
   protected AnimationAccessor<? extends StaticAnimation> animationAccessor;
   protected String animationName = "";
   protected float playSpeed;
   protected float convertTime;
   protected boolean notCharge;
   protected boolean repeatNode;
   protected boolean allowBuffer = true;
   protected int bufferDurationTicks = -1;
   protected boolean allowLongPress = false;
   protected int longPressThresholdOverride = -1;
   protected int newPhase;
   protected int cooldown;
   protected int comboResetTicks = -1;
   protected float comboResetTime = -1.0F;
   protected float comboResetAtAnimTime = -1.0F;
   protected boolean isAutoResetByMove = true;
   protected List<Pair<Condition, Side>> conditions = new ArrayList<>();
   protected List<ComboNode> conditionNodes = new ArrayList<>();
   private int id;
   private int priority;
   private ValueModifier damageMultiplier = null;
   private float impactMultiplier = 1.0F;
   private float hurtDamageMultiplier;
   private float armorNegation;
   private StunType stunTypeModifier = null;
   private boolean canBeInterrupt = true;
   protected int comboInterruptOnlyNodeId = -1;
   private ActionTag actionTag = ActionTag.NONE;

   protected ComboNode() {
      this.root = this;
      ComboNodeManager.assignId(this);
   }

   public static ComboNode create() {
      ComboNode root = new ComboNode();
      root.root = root;
      return root;
   }

   public static ComboNode createNode(@Nullable AnimationAccessor<? extends StaticAnimation> animation) {
      ComboNode node = new ComboNode();
      node.root = node;
      node.animationAccessor = animation;
      return node;
   }

   public boolean isAssigned() {
      return this.id != 0;
   }

   public int getId() {
      return this.id;
   }

   public void assign(int id) {
      this.id = id;
   }

   public boolean isRepeatNode() {
      return this.repeatNode;
   }

   public ComboNode setRepeatNode(boolean repeatNode) {
      this.repeatNode = repeatNode;
      return this;
   }

   public boolean isAllowBuffer() {
      return this.allowBuffer;
   }

   public ComboNode setAllowBuffer(boolean allowBuffer) {
      this.allowBuffer = allowBuffer;
      return this;
   }

   public int getBufferDurationTicks() {
      return this.bufferDurationTicks;
   }

   public ComboNode setBufferDurationTicks(int bufferDurationTicks) {
      this.bufferDurationTicks = bufferDurationTicks;
      return this;
   }

   public boolean isAllowLongPress() {
      return this.allowLongPress;
   }

   public ComboNode setAllowLongPress(boolean allowLongPress) {
      this.allowLongPress = allowLongPress;
      return this;
   }

   public int getLongPressThresholdOverride() {
      return this.longPressThresholdOverride;
   }

   public ComboNode setLongPressThresholdOverride(int longPressThresholdOverride) {
      this.longPressThresholdOverride = longPressThresholdOverride;
      return this;
   }

   public int resolveLongPressThreshold(int globalDefault) {
      return this.longPressThresholdOverride > 0 ? this.longPressThresholdOverride : globalDefault;
   }

   public float getArmorNegation() {
      return this.armorNegation;
   }

   public ComboNode setArmorNegation(float armorNegation) {
      this.armorNegation = armorNegation;
      return this;
   }

   public float getHurtDamageMultiplier() {
      return this.hurtDamageMultiplier;
   }

   public ComboNode setHurtDamageMultiplier(float hurtDamageMultiplier) {
      this.hurtDamageMultiplier = hurtDamageMultiplier;
      return this;
   }

   public ValueModifier getDamageMultiplier() {
      return this.damageMultiplier;
   }

   public ComboNode setDamageMultiplier(ValueModifier damageMultiplier) {
      this.damageMultiplier = damageMultiplier;
      return this;
   }

   public float getImpactMultiplier() {
      return this.impactMultiplier;
   }

   public ComboNode setImpactMultiplier(float impactMultiplier) {
      this.impactMultiplier = impactMultiplier;
      return this;
   }

   public StunType getStunTypeModifier() {
      return this.stunTypeModifier;
   }

   public ComboNode setStunTypeModifier(StunType stunTypeModifier) {
      this.stunTypeModifier = stunTypeModifier;
      return this;
   }

   public boolean isCanBeInterrupt() {
      return this.canBeInterrupt;
   }

   public ComboNode setCanBeInterrupt(boolean canBeInterrupt) {
      this.canBeInterrupt = canBeInterrupt;
      return this;
   }

   public int getComboInterruptOnlyNodeId() {
      return this.comboInterruptOnlyNodeId;
   }

   public ComboNode setComboInterruptOnlyNodeId(int id) {
      this.comboInterruptOnlyNodeId = id;
      return this;
   }

   public ComboNode setActionTag(ActionTag tag) {
      this.actionTag = tag;
      return this;
   }

   public ActionTag getActionTag() {
      return this.actionTag;
   }

   public int getPriority() {
      return this.priority;
   }

   public ComboNode setPriority(int priority) {
      this.priority = priority;
      return this;
   }

   public int getCooldown() {
      return this.cooldown;
   }

   public ComboNode setCooldown(int cooldown) {
      this.cooldown = cooldown;
      return this;
   }

   public int getComboResetTicks() {
      return this.comboResetTicks;
   }

   public ComboNode setComboResetTicks(int comboResetTicks) {
      this.comboResetTicks = comboResetTicks;
      return this;
   }

   public float getComboResetTime() {
      return this.comboResetTime;
   }

   public ComboNode setComboResetAtTime(float seconds) {
      this.comboResetTime = Math.max(0.0F, seconds);
      return this;
   }

   public float getComboResetAtAnimTime() {
      return this.comboResetAtAnimTime;
   }

   public ComboNode setComboResetAtAnimTime(float seconds) {
      this.comboResetAtAnimTime = Math.max(0.0F, seconds);
      return this;
   }

   public ComboNode setIsAutoResetByMove(boolean isAutoResetByMove) {
      this.isAutoResetByMove = isAutoResetByMove;
      return this;
   }

   public boolean isAutoResetByMove() {
      return this.isAutoResetByMove;
   }

   protected ComboNode createCopyInstance() {
      return new ComboNode();
   }

   public ComboNode copyForBranching() {
      ComboNode copy = this.createCopyInstance();
      copy.animationAccessor = this.animationAccessor;
      copy.animationName = this.animationName;
      copy.priority = this.priority;
      copy.playSpeed = this.playSpeed;
      copy.convertTime = this.convertTime;
      copy.damageMultiplier = this.damageMultiplier;
      copy.impactMultiplier = this.impactMultiplier;
      copy.hurtDamageMultiplier = this.hurtDamageMultiplier;
      copy.armorNegation = this.armorNegation;
      copy.stunTypeModifier = this.stunTypeModifier;
      copy.canBeInterrupt = this.canBeInterrupt;
      copy.comboInterruptOnlyNodeId = this.comboInterruptOnlyNodeId;
      copy.notCharge = this.notCharge;
      copy.allowBuffer = this.allowBuffer;
      copy.bufferDurationTicks = this.bufferDurationTicks;
      copy.allowLongPress = this.allowLongPress;
      copy.longPressThresholdOverride = this.longPressThresholdOverride;
      copy.newPhase = this.newPhase;
      copy.cooldown = this.cooldown;
      copy.comboResetTicks = this.comboResetTicks;
      copy.comboResetTime = this.comboResetTime;
      copy.comboResetAtAnimTime = this.comboResetAtAnimTime;
      copy.isAutoResetByMove = this.isAutoResetByMove;
      copy.conditions.addAll(this.conditions);
      copy.timeStampedEvents.addAll(this.timeStampedEvents);
      copy.timePeriodEvents.addAll(this.timePeriodEvents);
      copy.dodgeSuccessEvents.addAll(this.dodgeSuccessEvents);
      return copy;
   }

   public int getNewPhase() {
      return this.newPhase;
   }

   public ComboNode setNewPhase(int newPhase) {
      this.newPhase = newPhase;
      return this;
   }

   public boolean isNotCharge() {
      return this.notCharge;
   }

   public ComboNode setNotCharge(boolean notCharge) {
      this.notCharge = notCharge;
      return this;
   }

   public float getPlaySpeed() {
      return this.playSpeed;
   }

   public ComboNode setPlaySpeed(float playSpeed) {
      this.playSpeed = playSpeed;
      return this;
   }

   public float getConvertTime() {
      return this.convertTime;
   }

   public ComboNode setConvertTime(float convertTime) {
      this.convertTime = convertTime;
      return this;
   }

   public ComboNode addTimeEvent(TimeStampedEvent event) {
      this.timeStampedEvents.add(event);
      return this;
   }

   public ComboNode addTimeEvent(BaseEvent event) {
      this.timeStampedEvents.add(new TimeStampedEvent(0.01F, event.consumer));
      return this;
   }

   public ComboNode addDodgeSuccessEvent(BaseEvent event) {
      this.dodgeSuccessEvents.add(event);
      return this;
   }

   public ComboNode addHurtEvent(BaseEvent event) {
      this.hurtEvents.add(event);
      return this;
   }

   public ComboNode addHitEvent(BaseEvent event) {
      this.hitEvents.add(event);
      return this;
   }

   public ComboNode addBeginEvent(BaseEvent event) {
      this.onBeginEvents.add(event);
      return this;
   }

   public ComboNode addTimePeriodEvent(TimePeriodEvent event) {
      this.timePeriodEvents.add(event);
      return this;
   }

   public List<TimePeriodEvent> getTimePeriodEvents() {
      return this.timePeriodEvents;
   }

   public List<TimeStampedEvent> getTimeEvents() {
      return this.timeStampedEvents;
   }

   public List<BaseEvent> getHitEvents() {
      return this.hitEvents;
   }

   public List<BaseEvent> getHurtEvents() {
      return this.hurtEvents;
   }

   public List<BaseEvent> getDodgeSuccessEvents() {
      return this.dodgeSuccessEvents;
   }

   public List<BaseEvent> getOnBeginEvents() {
      return this.onBeginEvents;
   }

   public boolean isRoot() {
      return this.equals(this.root);
   }

   public boolean isEnd() {
      return this.children.isEmpty();
   }

   public Collection<ComboNode> getChildren() {
      return this.children.values();
   }

   public ComboNode getRootNode() {
      return this.root;
   }

   @Nullable
   public ComboNode getParentNode() {
      return this.parentNode;
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getAnimationAccessor() {
      if (this.animationAccessor == null) {
         this.animationAccessor = AnimationManager.byKey(this.animationName);
      }

      return this.animationAccessor;
   }

   public void setAnimationProvider(@Nullable AnimationAccessor<? extends StaticAnimation> animation) {
      this.animationAccessor = animation;
   }

   public void setAnimationName(String animationName) {
      this.animationName = animationName;
   }

   @Nullable
   public ComboNode getNext(ComboType type) {
      return this.children.get(type);
   }

   public boolean hasNext() {
      return !this.children.isEmpty();
   }

   public ComboNode addLeaf(ComboType type, @Nullable AnimationAccessor<? extends StaticAnimation> animation) {
      ComboNode child = new ComboNode();
      child.animationAccessor = animation;
      child.root = this.root;
      child.parentNode = this;
      this.children.put(type, child);
      return child;
   }

   public ComboNode addChild(ComboType type, ComboNode child) {
      child.root = this.root;
      child.parentNode = this;
      this.children.put(type, child);
      return this;
   }

   public void availableVia(ComboType key, ComboNode subtreeRoot) {
      subtreeRoot.injectInto(key, this);
   }

   private void injectInto(ComboType type, ComboNode child) {
      if (this != child && !this.children.containsValue(child)) {
         this.addChild(type, child);
         this.children.forEach((comboType, node) -> node.injectInto(type, child));
         this.conditionNodes.forEach(node -> node.injectInto(type, child));
      }
   }

   public boolean hasConditionAnimations() {
      return this.conditions.isEmpty();
   }

   public ComboNode fanIn(ComboType key, ComboNode... nodes) {
      for (ComboNode node : nodes) {
         if (node instanceof ComboNodeGroup) {
            for (ComboNode leaf : node.conditionNodes) {
               leaf.addChild(key, this);
            }
         } else {
            node.addChild(key, this);
         }
      }

      return this;
   }

   public <T extends LivingEntityPatch<?>> ComboNode addCondition(@Nullable Condition<T> condition) {
      this.conditions.add(Pair.of(condition, Side.SERVER));
      return this;
   }

   public <T extends LivingEntityPatch<?>> ComboNode addClientCondition(@Nullable Condition<T> condition) {
      this.conditions.add(Pair.of(condition, Side.CLIENT));
      return this;
   }

   public <T extends LivingEntityPatch<?>> ComboNode addCondition(@Nullable Condition<T> condition, Side side) {
      this.conditions.add(Pair.of(condition, side));
      return this;
   }

   @NotNull
   public List<Condition> getConditions(Side... sides) {
      return this.conditions
         .stream()
         .filter(pair -> sides == null || sides.length == 0 || Arrays.stream(sides).anyMatch(side -> side == pair.getSecond()))
         .<Condition>map(Pair::getFirst)
         .collect(Collectors.toList());
   }

   public ComboNode addConditionNode(ComboNode conditionAnimation) {
      this.conditionNodes.add(conditionAnimation);
      return this;
   }

   public List<ComboNode> getConditionNodes() {
      return this.conditionNodes;
   }

   public ComboNode provocation(ComboNode child) {
      return this.addChild(ComboNode.ComboTypes.PROVOCATION, child);
   }

   public ComboNode key1(ComboNode child) {
      return this.addChild(ComboNode.ComboTypes.KEY_1, child);
   }

   public ComboNode key2(ComboNode child) {
      return this.addChild(ComboNode.ComboTypes.KEY_2, child);
   }

   public ComboNode key3(ComboNode child) {
      return this.addChild(ComboNode.ComboTypes.KEY_3, child);
   }

   public ComboNode key4(ComboNode child) {
      return this.addChild(ComboNode.ComboTypes.KEY_4, child);
   }

   public ComboNode keyWeaponInnate(ComboNode child) {
      return this.addChild(ComboNode.ComboTypes.WEAPON_INNATE, child);
   }

   public ComboNode keyDodge(ComboNode child) {
      return this.addChild(ComboNode.ComboTypes.DODGE, child);
   }

   public ComboNode key1_2(ComboNode child) {
      return this.addChild(ComboNode.ComboTypes.KEY_1_2, child);
   }

   public ComboNode key1_3(ComboNode child) {
      return this.addChild(ComboNode.ComboTypes.KEY_1_3, child);
   }

   public ComboNode key1_4(ComboNode child) {
      return this.addChild(ComboNode.ComboTypes.KEY_1_4, child);
   }

   public ComboNode key2_3(ComboNode child) {
      return this.addChild(ComboNode.ComboTypes.KEY_2_3, child);
   }

   public ComboNode key2_4(ComboNode child) {
      return this.addChild(ComboNode.ComboTypes.KEY_2_4, child);
   }

   public ComboNode key3_4(ComboNode child) {
      return this.addChild(ComboNode.ComboTypes.KEY_3_4, child);
   }

   public ComboNode key1(AnimationAccessor<? extends StaticAnimation> animation) {
      return this.addLeaf(ComboNode.ComboTypes.KEY_1, animation);
   }

   public ComboNode key2(AnimationAccessor<? extends StaticAnimation> animation) {
      return this.addLeaf(ComboNode.ComboTypes.KEY_2, animation);
   }

   public ComboNode key3(AnimationAccessor<? extends StaticAnimation> animation) {
      return this.addLeaf(ComboNode.ComboTypes.KEY_3, animation);
   }

   public ComboNode key4(AnimationAccessor<? extends StaticAnimation> animation) {
      return this.addLeaf(ComboNode.ComboTypes.KEY_4, animation);
   }

   public ComboNode keyWeaponInnate(AnimationAccessor<? extends StaticAnimation> animation) {
      return this.addLeaf(ComboNode.ComboTypes.WEAPON_INNATE, animation);
   }

   public ComboNode keyProvocation(AnimationAccessor<? extends StaticAnimation> animation) {
      return this.addLeaf(ComboNode.ComboTypes.PROVOCATION, animation);
   }

   public ComboNode keyDodge(AnimationAccessor<? extends StaticAnimation> animation) {
      return this.addLeaf(ComboNode.ComboTypes.DODGE, animation);
   }

   public ComboNode key1_2(AnimationAccessor<? extends StaticAnimation> animation) {
      return this.addLeaf(ComboNode.ComboTypes.KEY_1_2, animation);
   }

   public ComboNode key1_3(AnimationAccessor<? extends StaticAnimation> animation) {
      return this.addLeaf(ComboNode.ComboTypes.KEY_1_3, animation);
   }

   public ComboNode key1_4(AnimationAccessor<? extends StaticAnimation> animation) {
      return this.addLeaf(ComboNode.ComboTypes.KEY_1_4, animation);
   }

   public ComboNode key2_3(AnimationAccessor<? extends StaticAnimation> animation) {
      return this.addLeaf(ComboNode.ComboTypes.KEY_2_3, animation);
   }

   public ComboNode key2_4(AnimationAccessor<? extends StaticAnimation> animation) {
      return this.addLeaf(ComboNode.ComboTypes.KEY_2_4, animation);
   }

   public ComboNode key3_4(AnimationAccessor<? extends StaticAnimation> animation) {
      return this.addLeaf(ComboNode.ComboTypes.KEY_3_4, animation);
   }

   @Override
   public boolean equals(Object obj) {
      if (super.equals(obj)) {
         return true;
      } else {
         if (obj instanceof ComboNode comboNode && comboNode.id == this.id) {
            return true;
         }

         return false;
      }
   }

   public static enum ComboTypes implements ComboType {
      KEY_1,
      KEY_2,
      KEY_3,
      KEY_4,
      KEY_1_2(KEY_1, KEY_2),
      KEY_1_3(KEY_1, KEY_3),
      KEY_1_4(KEY_1, KEY_4),
      KEY_2_3(KEY_2, KEY_3),
      KEY_2_4(KEY_2, KEY_4),
      KEY_3_4(KEY_3, KEY_4),
      DODGE,
      PROVOCATION,
      WEAPON_INNATE;

      final int id;
      final List<ComboType> subTypes;

      private ComboTypes(ComboNode.ComboTypes... subTypes) {
         this.subTypes = List.of(subTypes);
         this.id = ComboType.ENUM_MANAGER.assign(this);
      }

      private ComboTypes() {
         this.subTypes = new ArrayList<>();
         this.id = ComboType.ENUM_MANAGER.assign(this);
      }

      @Override
      public List<ComboType> getSubTypes() {
         return this.subTypes;
      }

      public int universalOrdinal() {
         return this.id;
      }
   }
}
