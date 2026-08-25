package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
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
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public abstract class CompressedDiamondArmorItem extends ArmorItem {
   public CompressedDiamondArmorItem(Type type, Properties properties) {
      super(
         new ArmorMaterial() {
            public int m_266425_(@NotNull Type type) {
               return switch (type) {
                  case BOOTS -> 923;
                  case LEGGINGS -> 1065;
                  case CHESTPLATE -> 1136;
                  case HELMET -> 781;
                  default -> throw new IncompatibleClassChangeError();
               };
            }

            public int m_7366_(@NotNull Type type) {
               return switch (type) {
                  case BOOTS -> 5;
                  case LEGGINGS -> 8;
                  case CHESTPLATE -> 9;
                  case HELMET -> 7;
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
               return Ingredient.m_43927_(
                  new ItemStack[]{
                     new ItemStack((ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND.get()), new ItemStack(Items.f_42472_), new ItemStack(Items.f_42415_)
                  }
               );
            }

            @NotNull
            public String m_6082_() {
               return "compressed_diamond_armor";
            }

            public float m_6651_() {
               return 1.8F;
            }

            public float m_6649_() {
               return 0.0F;
            }
         },
         type,
         properties
      );
   }

   public static class Boots extends CompressedDiamondArmorItem {
      public Boots() {
         super(Type.BOOTS, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/compressed_diamond_armor_layer_1.png";
      }
   }

   public static class Chestplate extends CompressedDiamondArmorItem {
      public Chestplate() {
         super(Type.CHESTPLATE, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/compressed_diamond_armor_layer_1.png";
      }
   }

   public static class Helmet extends CompressedDiamondArmorItem {
      public Helmet() {
         super(Type.HELMET, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/compressed_diamond_armor_layer_1.png";
      }
   }

   public static class Leggings extends CompressedDiamondArmorItem {
      public Leggings() {
         super(Type.LEGGINGS, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/compressed_diamond_armor_layer_2.png";
      }
   }
}
