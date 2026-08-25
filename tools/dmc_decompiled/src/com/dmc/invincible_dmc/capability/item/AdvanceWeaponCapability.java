package com.dmc.invincible_dmc.capability.item;

import com.dmc.epicarclib.api.exclusive.ExclusiveSkillData;
import com.dmc.epicarclib.api.exclusive.ExclusiveSkillProvider;
import com.dmc.epicarclib.api.exclusive.ExclusiveSkillStateApi;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Builder;
import yesman.epicfight.world.capabilities.item.CapabilityItem.ZoomInType;

public class AdvanceWeaponCapability extends WeaponCapability implements ExclusiveSkillProvider {
   protected final Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData>> exclusiveDodges;
   protected final Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData>> exclusiveGuards;
   protected final Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData>> exclusiveIdentitys;
   protected final Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData>> exclusiveMovers;
   protected final BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData> defaultExclusiveDodge;
   protected final BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData> defaultExclusiveGuard;
   protected final BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData> defaultExclusiveIdentity;
   protected final BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData> defaultExclusiveMover;
   protected final Function<LivingEntityPatch<?>, Map<LivingMotion, AnimationAccessor<? extends StaticAnimation>>> livingMotionProvider;
   protected final boolean preventsCrouching;

   protected AdvanceWeaponCapability(Builder builder) {
      super(builder);
      AdvanceWeaponCapability.AdvanceBuilder advanceBuilder = (AdvanceWeaponCapability.AdvanceBuilder)builder;
      this.exclusiveDodges = advanceBuilder.exclusiveDodges;
      this.exclusiveGuards = advanceBuilder.exclusiveGuards;
      this.exclusiveIdentitys = advanceBuilder.exclusiveIdentitys;
      this.exclusiveMovers = advanceBuilder.exclusiveMovers;
      this.defaultExclusiveDodge = advanceBuilder.defaultExclusiveDodge;
      this.defaultExclusiveGuard = advanceBuilder.defaultExclusiveGuard;
      this.defaultExclusiveIdentity = advanceBuilder.defaultExclusiveIdentity;
      this.defaultExclusiveMover = advanceBuilder.defaultExclusiveMover;
      this.livingMotionProvider = advanceBuilder.livingMotionProvider;
      this.preventsCrouching = advanceBuilder.preventsCrouching;
   }

   public ExclusiveSkillData getExclusiveDodge(PlayerPatch<?> playerpatch, ItemStack itemstack) {
      Style style = this.getStyle(playerpatch);
      if (this.exclusiveDodges.containsKey(style)) {
         return this.exclusiveDodges.get(style).apply(this, playerpatch);
      } else {
         return this.defaultExclusiveDodge != null ? this.defaultExclusiveDodge.apply(this, playerpatch) : null;
      }
   }

   public ExclusiveSkillData getExclusiveGuard(PlayerPatch<?> playerpatch, ItemStack itemstack) {
      Style style = this.getStyle(playerpatch);
      if (this.exclusiveGuards.containsKey(style)) {
         return this.exclusiveGuards.get(style).apply(this, playerpatch);
      } else {
         return this.defaultExclusiveGuard != null ? this.defaultExclusiveGuard.apply(this, playerpatch) : null;
      }
   }

   public ExclusiveSkillData getExclusiveIdentity(PlayerPatch<?> playerpatch, ItemStack itemstack) {
      Style style = this.getStyle(playerpatch);
      if (this.exclusiveIdentitys.containsKey(style)) {
         return this.exclusiveIdentitys.get(style).apply(this, playerpatch);
      } else {
         return this.defaultExclusiveIdentity != null ? this.defaultExclusiveIdentity.apply(this, playerpatch) : null;
      }
   }

   public ExclusiveSkillData getExclusiveMover(PlayerPatch<?> playerpatch, ItemStack itemstack) {
      Style style = this.getStyle(playerpatch);
      if (this.exclusiveMovers.containsKey(style)) {
         return this.exclusiveMovers.get(style).apply(this, playerpatch);
      } else {
         return this.defaultExclusiveMover != null ? this.defaultExclusiveMover.apply(this, playerpatch) : null;
      }
   }

   public ExclusiveSkillData getExclusiveSkill(PlayerPatch<?> playerPatch, ItemStack itemStack, SkillSlot slot) {
      if (slot == SkillSlots.DODGE) {
         return this.getExclusiveDodge(playerPatch, itemStack);
      } else if (slot == SkillSlots.GUARD) {
         return this.getExclusiveGuard(playerPatch, itemStack);
      } else if (slot == SkillSlots.IDENTITY) {
         return this.getExclusiveIdentity(playerPatch, itemStack);
      } else {
         return slot == SkillSlots.MOVER ? this.getExclusiveMover(playerPatch, itemStack) : null;
      }
   }

   public boolean preventsCrouching() {
      return this.preventsCrouching;
   }

   public Map<LivingMotion, AnimationAccessor<? extends StaticAnimation>> getLivingMotionModifier(LivingEntityPatch<?> player, InteractionHand hand) {
      return hand == InteractionHand.MAIN_HAND && this.livingMotionProvider != null
         ? this.livingMotionProvider.apply(player)
         : super.getLivingMotionModifier(player, hand);
   }

   public static AdvanceWeaponCapability.AdvanceBuilder builder() {
      return new AdvanceWeaponCapability.AdvanceBuilder();
   }

   public static class AdvanceBuilder extends yesman.epicfight.world.capabilities.item.WeaponCapability.Builder {
      protected final Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData>> exclusiveDodges;
      protected final Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData>> exclusiveGuards;
      protected final Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData>> exclusiveIdentitys;
      protected final Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData>> exclusiveMovers;
      protected BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData> defaultExclusiveDodge = null;
      protected BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData> defaultExclusiveGuard = null;
      protected BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData> defaultExclusiveIdentity = null;
      protected BiFunction<CapabilityItem, PlayerPatch<?>, ExclusiveSkillData> defaultExclusiveMover = null;
      protected Function<LivingEntityPatch<?>, Map<LivingMotion, AnimationAccessor<? extends StaticAnimation>>> livingMotionProvider;
      protected boolean preventsCrouching;

      protected AdvanceBuilder() {
         this.constructor(AdvanceWeaponCapability::new);
         this.exclusiveDodges = Maps.newHashMap();
         this.exclusiveGuards = Maps.newHashMap();
         this.exclusiveIdentitys = Maps.newHashMap();
         this.exclusiveMovers = Maps.newHashMap();
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveDodge(Style style, Skill skill, boolean isFocusReplace) {
         ExclusiveSkillStateApi.registerExclusiveSkill(skill);
         this.exclusiveDodges.put(style, (cap, patch) -> new ExclusiveSkillData(skill, isFocusReplace));
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveDodge(Style style, Skill skill) {
         return this.exclusiveDodge(style, skill, true);
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveDodge(Skill skill, boolean isFocusReplace) {
         ExclusiveSkillStateApi.registerExclusiveSkill(skill);
         this.defaultExclusiveDodge = (cap, patch) -> new ExclusiveSkillData(skill, isFocusReplace);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveDodge(Skill skill) {
         return this.exclusiveDodge(skill, true);
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveGuard(Style style, Skill skill, boolean isFocusReplace) {
         ExclusiveSkillStateApi.registerExclusiveSkill(skill);
         this.exclusiveGuards.put(style, (cap, patch) -> new ExclusiveSkillData(skill, isFocusReplace));
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveGuard(Style style, Skill skill) {
         return this.exclusiveGuard(style, skill, true);
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveGuard(Skill skill, boolean isFocusReplace) {
         ExclusiveSkillStateApi.registerExclusiveSkill(skill);
         this.defaultExclusiveGuard = (cap, patch) -> new ExclusiveSkillData(skill, isFocusReplace);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveGuard(Skill skill) {
         return this.exclusiveGuard(skill, true);
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveIdentity(Style style, Skill skill, boolean isFocusReplace) {
         ExclusiveSkillStateApi.registerExclusiveSkill(skill);
         this.exclusiveIdentitys.put(style, (cap, patch) -> new ExclusiveSkillData(skill, isFocusReplace));
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveIdentity(Style style, Skill skill) {
         return this.exclusiveIdentity(style, skill, true);
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveIdentity(Skill skill, boolean isFocusReplace) {
         ExclusiveSkillStateApi.registerExclusiveSkill(skill);
         this.defaultExclusiveIdentity = (cap, patch) -> new ExclusiveSkillData(skill, isFocusReplace);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveIdentity(Skill skill) {
         return this.exclusiveIdentity(skill, true);
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveMover(Style style, Skill skill, boolean isFocusReplace) {
         ExclusiveSkillStateApi.registerExclusiveSkill(skill);
         this.exclusiveMovers.put(style, (cap, patch) -> new ExclusiveSkillData(skill, isFocusReplace));
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveMover(Style style, Skill skill) {
         return this.exclusiveMover(style, skill, true);
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveMover(Skill skill, boolean isFocusReplace) {
         ExclusiveSkillStateApi.registerExclusiveSkill(skill);
         this.defaultExclusiveMover = (cap, patch) -> new ExclusiveSkillData(skill, isFocusReplace);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder exclusiveMover(Skill skill) {
         return this.exclusiveMover(skill, true);
      }

      public AdvanceWeaponCapability.AdvanceBuilder preventCrouching() {
         this.preventsCrouching = true;
         return this;
      }

      @SafeVarargs
      public final AdvanceWeaponCapability.AdvanceBuilder newAdvanceStyleCombo(Style style, AnimationAccessor<? extends AttackAnimation>... animation) {
         super.newStyleCombo(style, animation);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder category(WeaponCategory category) {
         super.category(category);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder styleProvider(Function<LivingEntityPatch<?>, Style> styleProvider) {
         super.styleProvider(styleProvider);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder passiveSkill(Skill passiveSkill) {
         super.passiveSkill(passiveSkill);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder swingSound(SoundEvent swingSound) {
         super.swingSound(swingSound);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder hitSound(SoundEvent hitSound) {
         super.hitSound(hitSound);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder hitParticle(HitParticleType hitParticle) {
         super.hitParticle(hitParticle);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder collider(Collider collider) {
         super.collider(collider);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder canBePlacedOffhand(boolean canBePlacedOffhand) {
         super.canBePlacedOffhand(canBePlacedOffhand);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder reach(float reach) {
         super.reach(reach);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder livingMotionProvider(
         Function<LivingEntityPatch<?>, Map<LivingMotion, AnimationAccessor<? extends StaticAnimation>>> provider
      ) {
         this.livingMotionProvider = provider;
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder livingMotionModifier(
         Style wieldStyle, LivingMotion livingMotion, AnimationAccessor<? extends StaticAnimation> animation
      ) {
         super.livingMotionModifier(wieldStyle, livingMotion, animation);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder addStyleAttibutes(Style style, Pair<Attribute, AttributeModifier> attributePair) {
         super.addStyleAttibutes(style, attributePair);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder weaponCombinationPredicator(Function<LivingEntityPatch<?>, Boolean> predicator) {
         super.weaponCombinationPredicator(predicator);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder innateSkill(Style style, Function<ItemStack, Skill> innateSkill) {
         super.innateSkill(style, innateSkill);
         return this;
      }

      @Deprecated
      public AdvanceWeaponCapability.AdvanceBuilder comboCancel(Function<Style, Boolean> comboCancel) {
         super.comboCancel(comboCancel);
         return this;
      }

      public AdvanceWeaponCapability.AdvanceBuilder zoomInType(ZoomInType zoomInType) {
         super.zoomInType(zoomInType);
         return this;
      }
   }
}
