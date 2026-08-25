package com.Yujin.onegradefixer.epicmoonmod.item;

import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringRepresentable.EnumCodec;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public enum EMArmorMaterials implements ArmorMaterial {
   LEIHENG("leiheng", 37, (EnumMap<Type, Integer>)Util.m_137469_(new EnumMap(Type.class), p_266655_ -> {
      p_266655_.put(Type.BOOTS, 3);
      p_266655_.put(Type.LEGGINGS, 6);
      p_266655_.put(Type.CHESTPLATE, 8);
      p_266655_.put(Type.HELMET, 3);
   }), 15, SoundEvents.f_11679_, 3.0F, 0.1F, () -> Ingredient.m_43929_(new ItemLike[]{Items.f_42418_}));

   public static final EnumCodec<ArmorMaterials> CODEC = StringRepresentable.m_216439_(ArmorMaterials::values);
   private static final EnumMap<Type, Integer> HEALTH_FUNCTION_FOR_TYPE = (EnumMap<Type, Integer>)Util.m_137469_(new EnumMap(Type.class), p_266653_ -> {
      p_266653_.put(Type.BOOTS, 13);
      p_266653_.put(Type.LEGGINGS, 15);
      p_266653_.put(Type.CHESTPLATE, 16);
      p_266653_.put(Type.HELMET, 11);
   });
   private final String name;
   private final int durabilityMultiplier;
   private final EnumMap<Type, Integer> protectionFunctionForType;
   private final int enchantmentValue;
   private final SoundEvent sound;
   private final float toughness;
   private final float knockbackResistance;
   private final LazyLoadedValue<Ingredient> repairIngredient;

   private EMArmorMaterials(
      String pName,
      int pDurabilityMultiplier,
      EnumMap<Type, Integer> pProtectionFunctionForType,
      int pEnchantmentValue,
      SoundEvent pSound,
      float pToughness,
      float pKnockbackResistance,
      Supplier<Ingredient> pRepairIngredient
   ) {
      this.name = pName;
      this.durabilityMultiplier = pDurabilityMultiplier;
      this.protectionFunctionForType = pProtectionFunctionForType;
      this.enchantmentValue = pEnchantmentValue;
      this.sound = pSound;
      this.toughness = pToughness;
      this.knockbackResistance = pKnockbackResistance;
      this.repairIngredient = new LazyLoadedValue(pRepairIngredient);
   }

   public int m_266425_(Type pType) {
      return HEALTH_FUNCTION_FOR_TYPE.get(pType) * this.durabilityMultiplier;
   }

   public int m_7366_(Type pType) {
      return this.protectionFunctionForType.get(pType);
   }

   public int m_6646_() {
      return this.enchantmentValue;
   }

   public SoundEvent m_7344_() {
      return this.sound;
   }

   public Ingredient m_6230_() {
      return (Ingredient)this.repairIngredient.m_13971_();
   }

   public String m_6082_() {
      return this.name;
   }

   public float m_6651_() {
      return this.toughness;
   }

   public float m_6649_() {
      return this.knockbackResistance;
   }

   public String getSerializedName() {
      return this.name;
   }
}
