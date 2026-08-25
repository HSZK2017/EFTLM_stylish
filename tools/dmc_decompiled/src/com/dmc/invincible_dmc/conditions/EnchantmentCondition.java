package com.dmc.invincible_dmc.conditions;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EnchantmentCondition implements Condition<ServerPlayerPatch> {
   private boolean isMainHand;
   private Supplier<Enchantment> effectSupplier;
   private int min;
   private int max;

   public EnchantmentCondition() {
   }

   public EnchantmentCondition(boolean isMainHand, Supplier<Enchantment> effectSupplier, int min, int max) {
      this.isMainHand = isMainHand;
      this.effectSupplier = effectSupplier;
      this.min = min;
      this.max = max;
   }

   public EnchantmentCondition(boolean isMainHand, Supplier<Enchantment> effectSupplier, int level) {
      this.isMainHand = isMainHand;
      this.effectSupplier = effectSupplier;
      this.min = level;
      this.max = level;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      this.isMainHand = compoundTag.m_128471_("is_main_hand");
      this.effectSupplier = () -> (Enchantment)ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.parse(compoundTag.m_128461_("enchantment")));
      this.min = compoundTag.m_128451_("min");
      this.max = compoundTag.m_128451_("max");
      return this;
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128379_("is_main_hand", this.isMainHand);
      tag.m_128359_("enchantment", Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getKey(this.effectSupplier.get())).toString());
      tag.m_128405_("min", this.min);
      tag.m_128405_("max", this.max);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      int level = EnchantmentHelper.getTagEnchantmentLevel(
         this.effectSupplier.get(),
         this.isMainHand ? ((ServerPlayer)serverPlayerPatch.getOriginal()).m_21205_() : ((ServerPlayer)serverPlayerPatch.getOriginal()).m_21206_()
      );
      return level >= this.min && level <= this.max;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
