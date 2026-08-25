package com.pla.annoyingvillagers.item;

import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public abstract class EmeraldArmorItem extends ArmorItem {
   public EmeraldArmorItem(Type type, Properties properties) {
      super(
         new ArmorMaterial() {
            public int m_266425_(@NotNull Type type) {
               return switch (type) {
                  case BOOTS -> 624;
                  case LEGGINGS -> 720;
                  case CHESTPLATE -> 768;
                  case HELMET -> 528;
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
               return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Items.f_42616_)});
            }

            @NotNull
            public String m_6082_() {
               return "emerald_armor";
            }

            public float m_6651_() {
               return 2.0F;
            }

            public float m_6649_() {
               return 0.2F;
            }
         },
         type,
         properties
      );
   }

   public static class Boots extends EmeraldArmorItem {
      public Boots() {
         super(Type.BOOTS, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/emerald_armor_layer_1.png";
      }

      public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
         super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
         if (player.m_6844_(EquipmentSlot.FEET) == stack && !player.m_9236_().m_5776_()) {
            player.m_7292_(new MobEffectInstance(MobEffects.f_19603_, 100, 1));
         }
      }
   }

   public static class Chestplate extends EmeraldArmorItem {
      public Chestplate() {
         super(Type.CHESTPLATE, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/emerald_armor_layer_1.png";
      }

      public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
         super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
         if (player.m_6844_(EquipmentSlot.CHEST) == stack && !player.m_9236_().m_5776_()) {
            player.m_7292_(new MobEffectInstance(MobEffects.f_19603_, 100, 1));
         }
      }
   }

   public static class Helmet extends EmeraldArmorItem {
      public Helmet() {
         super(Type.HELMET, new Properties().m_41486_());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/emerald_armor_layer_1.png";
      }

      public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
         super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
         if (player.m_6844_(EquipmentSlot.HEAD) == stack && !player.m_9236_().m_5776_()) {
            player.m_7292_(new MobEffectInstance(MobEffects.f_19603_, 100, 1));
         }
      }
   }

   public static class Leggings extends EmeraldArmorItem {
      public Leggings() {
         super(Type.LEGGINGS, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/emerald_armor_layer_2.png";
      }

      public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
         super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
         if (player.m_6844_(EquipmentSlot.LEGS) == stack && !player.m_9236_().m_5776_()) {
            player.m_7292_(new MobEffectInstance(MobEffects.f_19603_, 100, 1));
         }
      }
   }
}
