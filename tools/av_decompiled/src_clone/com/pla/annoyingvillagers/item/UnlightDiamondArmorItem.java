package com.pla.annoyingvillagers.item;

import java.util.Objects;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public abstract class UnlightDiamondArmorItem extends ArmorItem {
   public UnlightDiamondArmorItem(Type type, Properties properties) {
      super(
         new ArmorMaterial() {
            public int m_266425_(@NotNull Type type) {
               return switch (type) {
                  case BOOTS -> 598;
                  case LEGGINGS -> 690;
                  case CHESTPLATE -> 736;
                  case HELMET -> 506;
                  default -> throw new IncompatibleClassChangeError();
               };
            }

            public int m_7366_(@NotNull Type type) {
               return switch (type) {
                  case BOOTS -> 4;
                  case LEGGINGS -> 5;
                  case CHESTPLATE -> 8;
                  case HELMET -> 5;
                  default -> throw new IncompatibleClassChangeError();
               };
            }

            public int m_6646_() {
               return 10;
            }

            @NotNull
            public SoundEvent m_7344_() {
               return Objects.requireNonNull(
                  (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "item.armor.equip_diamond"))
               );
            }

            @NotNull
            public Ingredient m_6230_() {
               return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Items.f_42415_)});
            }

            @NotNull
            public String m_6082_() {
               return "unlight_diamond_armor";
            }

            public float m_6651_() {
               return 2.1F;
            }

            public float m_6649_() {
               return 0.0F;
            }
         },
         type,
         properties
      );
   }

   public static class Boots extends UnlightDiamondArmorItem {
      public Boots() {
         super(Type.BOOTS, new Properties().m_41486_());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/unlight_diamond_layer_1.png";
      }
   }

   public static class Chestplate extends UnlightDiamondArmorItem {
      public Chestplate() {
         super(Type.CHESTPLATE, new Properties().m_41486_());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/unlight_diamond_layer_1.png";
      }
   }

   public static class Helmet extends UnlightDiamondArmorItem {
      public Helmet() {
         super(Type.HELMET, new Properties().m_41486_());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/unlight_diamond_layer_1.png";
      }
   }

   public static class Leggings extends UnlightDiamondArmorItem {
      public Leggings() {
         super(Type.LEGGINGS, new Properties().m_41486_());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/unlight_diamond_layer_2.png";
      }
   }
}
