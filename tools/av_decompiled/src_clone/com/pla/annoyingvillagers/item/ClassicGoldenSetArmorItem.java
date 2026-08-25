package com.pla.annoyingvillagers.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;

public abstract class ClassicGoldenSetArmorItem extends ArmorItem {
   public ClassicGoldenSetArmorItem(Type type, Properties properties) {
      super(new ArmorMaterial() {
         public int m_266425_(Type type) {
            return switch (type) {
               case BOOTS -> 351;
               case LEGGINGS -> 405;
               case CHESTPLATE -> 432;
               case HELMET -> 297;
               default -> throw new IncompatibleClassChangeError();
            };
         }

         public int m_7366_(Type type) {
            return switch (type) {
               case BOOTS -> 3;
               case LEGGINGS -> 5;
               case CHESTPLATE -> 6;
               case HELMET -> 3;
               default -> throw new IncompatibleClassChangeError();
            };
         }

         public int m_6646_() {
            return 9;
         }

         public SoundEvent m_7344_() {
            return SoundEvents.f_11675_;
         }

         public Ingredient m_6230_() {
            return Ingredient.m_151265_();
         }

         public String m_6082_() {
            return "classic_golden_armor";
         }

         public float m_6651_() {
            return 0.7F;
         }

         public float m_6649_() {
            return 0.0F;
         }
      }, type, properties);
   }

   public static class Boots extends ClassicGoldenSetArmorItem {
      public Boots() {
         super(Type.BOOTS, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/gold_layer_1.png";
      }
   }

   public static class Chestplate extends ClassicGoldenSetArmorItem {
      public Chestplate() {
         super(Type.CHESTPLATE, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/gold_layer_1.png";
      }
   }

   public static class Helmet extends ClassicGoldenSetArmorItem {
      public Helmet() {
         super(Type.HELMET, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/gold_layer_1.png";
      }
   }

   public static class Leggings extends ClassicGoldenSetArmorItem {
      public Leggings() {
         super(Type.LEGGINGS, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/gold_layer_2.png";
      }
   }
}
