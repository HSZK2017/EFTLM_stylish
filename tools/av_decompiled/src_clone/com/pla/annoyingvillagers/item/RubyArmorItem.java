package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class RubyArmorItem extends ArmorItem {
   public RubyArmorItem(Type type, Properties properties) {
      super(new ArmorMaterial() {
         public int m_266425_(Type type) {
            return switch (type) {
               case BOOTS -> 325;
               case LEGGINGS -> 375;
               case CHESTPLATE -> 400;
               case HELMET -> 275;
               default -> throw new IncompatibleClassChangeError();
            };
         }

         public int m_7366_(Type type) {
            return switch (type) {
               case BOOTS -> 5;
               case LEGGINGS -> 6;
               case CHESTPLATE -> 9;
               case HELMET -> 5;
               default -> throw new IncompatibleClassChangeError();
            };
         }

         public int m_6646_() {
            return 9;
         }

         public SoundEvent m_7344_() {
            return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "item.armor.equip_diamond"));
         }

         public Ingredient m_6230_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack((ItemLike)AnnoyingVillagersModItems.DARK_NETHERITE.get()), new ItemStack(Items.f_42417_)});
         }

         public String m_6082_() {
            return "ruby_armor";
         }

         public float m_6651_() {
            return 0.0F;
         }

         public float m_6649_() {
            return 0.0F;
         }
      }, type, properties);
   }

   public static class Boots extends RubyArmorItem {
      public Boots() {
         super(Type.BOOTS, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/ruby_armor_layer_1.png";
      }
   }

   public static class Chestplate extends RubyArmorItem {
      public Chestplate() {
         super(Type.CHESTPLATE, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/ruby_armor_layer_1.png";
      }
   }

   public static class Helmet extends RubyArmorItem {
      public Helmet() {
         super(Type.HELMET, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/ruby_armor_layer_1.png";
      }
   }

   public static class Leggings extends RubyArmorItem {
      public Leggings() {
         super(Type.LEGGINGS, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/ruby_armor_layer_2.png";
      }
   }
}
