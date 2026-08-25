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

public abstract class PurpleVillagerKnightArmorItem extends ArmorItem {
   public PurpleVillagerKnightArmorItem(Type type, Properties properties) {
      super(
         new ArmorMaterial() {
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
                  case BOOTS -> 4;
                  case LEGGINGS -> 6;
                  case CHESTPLATE -> 7;
                  case HELMET -> 5;
                  default -> throw new IncompatibleClassChangeError();
               };
            }

            public int m_6646_() {
               return 10;
            }

            public SoundEvent m_7344_() {
               return Objects.requireNonNull(
                  (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "item.armor.equip_chain"))
               );
            }

            public Ingredient m_6230_() {
               return Ingredient.m_151265_();
            }

            public String m_6082_() {
               return "purple_villager_knight_armor";
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

   public static class Chestplate extends PurpleVillagerKnightArmorItem {
      public Chestplate() {
         super(Type.CHESTPLATE, new Properties());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/purple_villager_knight_armor_layer.png";
      }
   }

   public static class Helmet extends PurpleVillagerKnightArmorItem {
      public Helmet() {
         super(Type.HELMET, new Properties());
      }

      public void initializeClient(Consumer<IClientItemExtensions> consumer) {
         consumer.accept(
            new IClientItemExtensions() {
               @NotNull
               public HumanoidModel<?> getHumanoidArmorModel(
                  LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original
               ) {
                  HumanoidModel humanoidmodel1 = new HumanoidModel(
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
                  humanoidmodel1.f_102817_ = livingEntity.m_6144_();
                  humanoidmodel1.f_102609_ = original.f_102609_;
                  humanoidmodel1.f_102610_ = livingEntity.m_6162_();
                  return humanoidmodel1;
               }
            }
         );
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/entities/purple.png";
      }
   }
}
