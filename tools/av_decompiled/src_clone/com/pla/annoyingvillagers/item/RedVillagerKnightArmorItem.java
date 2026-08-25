package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.client.model.ModelVillagerKnightArmor;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public abstract class RedVillagerKnightArmorItem extends ArmorItem {
   public RedVillagerKnightArmorItem(Type type, Properties properties) {
      super(
         new ArmorMaterial() {
            public int m_266425_(@NotNull Type type) {
               return switch (type) {
                  case BOOTS -> 325;
                  case LEGGINGS -> 375;
                  case CHESTPLATE -> 400;
                  case HELMET -> 275;
                  default -> throw new IncompatibleClassChangeError();
               };
            }

            public int m_7366_(@NotNull Type type) {
               return switch (type) {
                  case BOOTS -> 4;
                  case LEGGINGS -> 5;
                  case CHESTPLATE -> 7;
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
                  (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "item.armor.equip_chain"))
               );
            }

            @NotNull
            public Ingredient m_6230_() {
               return Ingredient.m_151265_();
            }

            @NotNull
            public String m_6082_() {
               return "red_villager_knight_armor";
            }

            public float m_6651_() {
               return 2.0F;
            }

            public float m_6649_() {
               return 0.0F;
            }
         },
         type,
         properties
      );
   }

   public static class Armor extends RedVillagerKnightArmorItem {
      public Armor() {
         super(Type.HELMET, new Properties());
      }

      public void initializeClient(Consumer<IClientItemExtensions> consumer) {
         consumer.accept(
            new IClientItemExtensions() {
               @NotNull
               public HumanoidModel<?> getHumanoidArmorModel(
                  LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original
               ) {
                  HumanoidModel humanoidmodel = new HumanoidModel(
                     new ModelPart(
                        Collections.emptyList(),
                        Map.of(
                           "head",
                           (new ModelVillagerKnightArmor(Minecraft.m_91087_().m_167973_().m_171103_(ModelVillagerKnightArmor.LAYER_LOCATION))).Head,
                           "hat",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "body",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "right_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "left_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "right_leg",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "left_leg",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap())
                        )
                     )
                  );
                  humanoidmodel.f_102817_ = livingEntity.m_6144_();
                  humanoidmodel.f_102609_ = original.f_102609_;
                  humanoidmodel.f_102610_ = livingEntity.m_6162_();
                  return humanoidmodel;
               }
            }
         );
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/entities/red.png";
      }
   }

   public static class Boots extends RedVillagerKnightArmorItem {
      public Boots() {
         super(Type.BOOTS, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/red_villager_knight_armor_layer_1.png";
      }
   }

   public static class Chestplate extends RedVillagerKnightArmorItem {
      public Chestplate() {
         super(Type.CHESTPLATE, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/red_villager_knight_armor_layer_1.png";
      }
   }

   public static class Leggings extends RedVillagerKnightArmorItem {
      public Leggings() {
         super(Type.LEGGINGS, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/red_villager_knight_armor_layer_2.png";
      }
   }
}
