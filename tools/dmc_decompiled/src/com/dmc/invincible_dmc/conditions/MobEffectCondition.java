package com.dmc.invincible_dmc.conditions;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class MobEffectCondition implements Condition<ServerPlayerPatch> {
   private boolean isTarget;
   private Supplier<MobEffect> effectSupplier;
   private int min;
   private int max;

   public MobEffectCondition() {
   }

   public MobEffectCondition(boolean isTarget, Supplier<MobEffect> effectSupplier, int min, int max) {
      this.isTarget = isTarget;
      this.effectSupplier = effectSupplier;
      this.min = min;
      this.max = max;
   }

   public MobEffectCondition(boolean isTarget, Supplier<MobEffect> effectSupplier, int level) {
      this.isTarget = isTarget;
      this.effectSupplier = effectSupplier;
      this.min = level;
      this.max = level;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      this.isTarget = compoundTag.m_128471_("is_target");
      this.effectSupplier = () -> (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.parse(compoundTag.m_128461_("effect")));
      this.min = compoundTag.m_128451_("min");
      this.max = compoundTag.m_128451_("max");
      return this;
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128379_("is_target", this.isTarget);
      tag.m_128359_("effect", Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.getKey(this.effectSupplier.get())).toString());
      tag.m_128405_("min", this.min);
      tag.m_128405_("max", this.max);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      if (this.isTarget) {
         return serverPlayerPatch.getTarget() == null ? false : this.test(serverPlayerPatch.getTarget());
      } else {
         return this.test((LivingEntity)serverPlayerPatch.getOriginal());
      }
   }

   public boolean test(LivingEntity livingEntity) {
      if (!livingEntity.m_21023_(this.effectSupplier.get())) {
         return false;
      } else {
         int level = Objects.requireNonNull(livingEntity.m_21124_(this.effectSupplier.get())).m_19564_();
         return level >= this.min && level <= this.max;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
