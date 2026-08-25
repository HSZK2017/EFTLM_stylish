package com.pla.annoyingvillagers.item;

import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public abstract class BrokenDiamondArmorItem extends ArmorItem {
   public BrokenDiamondArmorItem(Type type, Properties properties) {
      super(
         new ArmorMaterial() {
            public int m_266425_(@NotNull Type type) {
               return switch (type) {
                  case BOOTS -> 130;
                  case LEGGINGS -> 150;
                  case CHESTPLATE -> 160;
                  case HELMET -> 110;
                  default -> throw new IncompatibleClassChangeError();
               };
            }

            public int m_7366_(@NotNull Type type) {
               return switch (type) {
                  case BOOTS -> 2;
                  case LEGGINGS -> 6;
                  case CHESTPLATE -> 5;
                  case HELMET -> 3;
                  default -> throw new IncompatibleClassChangeError();
               };
            }

            public int m_6646_() {
               return 9;
            }

            @NotNull
            public SoundEvent m_7344_() {
               return Objects.requireNonNull(
                  (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "item.armor.equip_diamond"))
               );
            }

            @NotNull
            public Ingredient m_6230_() {
               return Ingredient.m_151265_();
            }

            @NotNull
            public String m_6082_() {
               return "broken_diamond_armor";
            }

            public float m_6651_() {
               return 1.0F;
            }

            public float m_6649_() {
               return 0.0F;
            }
         },
         type,
         properties
      );
   }

   public static class Boots extends BrokenDiamondArmorItem {
      public Boots() {
         super(Type.BOOTS, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/broken_diamond_armor_layer_1.png";
      }
   }

   public static class Chestplate extends BrokenDiamondArmorItem {
      public Chestplate() {
         super(Type.CHESTPLATE, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/broken_diamond_armor_layer_1.png";
      }
   }

   public static class Helmet extends BrokenDiamondArmorItem {
      public Helmet() {
         super(Type.HELMET, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/broken_diamond_armor_layer_1.png";
      }
   }

   public static class Leggings extends BrokenDiamondArmorItem {
      public Leggings() {
         super(Type.LEGGINGS, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/broken_diamond_armor_layer_2.png";
      }
   }
}
