package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.client.model.ModelHerobrineObsidianDiamondChestplate;
import com.pla.annoyingvillagers.util.ArmorUtil;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public abstract class HerobrineObsidianDiamondArmorChestplateItem extends ArmorItem {
   public HerobrineObsidianDiamondArmorChestplateItem(Type type, Properties properties) {
      super(new ArmorMaterial() {
         public int m_266425_(Type pType) {
            return switch (pType) {
               case BOOTS -> 325;
               case LEGGINGS -> 375;
               case CHESTPLATE -> 600;
               case HELMET -> 275;
               default -> throw new IncompatibleClassChangeError();
            };
         }

         public int m_7366_(Type pType) {
            return switch (pType) {
               case BOOTS -> 0;
               case LEGGINGS -> 0;
               case CHESTPLATE -> 24;
               case HELMET -> 0;
               default -> throw new IncompatibleClassChangeError();
            };
         }

         public int m_6646_() {
            return 0;
         }

         public SoundEvent m_7344_() {
            return SoundEvents.f_11675_;
         }

         public Ingredient m_6230_() {
            return Ingredient.m_151265_();
         }

         public String m_6082_() {
            return "herobrine_obsidian_diamond_armor";
         }

         public float m_6651_() {
            return 2.0F;
         }

         public float m_6649_() {
            return 0.0F;
         }
      }, type, properties);
   }

   public static class Chestplate extends HerobrineObsidianDiamondArmorChestplateItem {
      public Chestplate() {
         super(Type.CHESTPLATE, new Properties());
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
                           "body",
                           (new ModelHerobrineObsidianDiamondChestplate(
                                 Minecraft.m_91087_().m_167973_().m_171103_(ModelHerobrineObsidianDiamondChestplate.LAYER_LOCATION)
                              ))
                              .Body,
                           "left_arm",
                           (new ModelHerobrineObsidianDiamondChestplate(
                                 Minecraft.m_91087_().m_167973_().m_171103_(ModelHerobrineObsidianDiamondChestplate.LAYER_LOCATION)
                              ))
                              .LeftArm,
                           "right_arm",
                           (new ModelHerobrineObsidianDiamondChestplate(
                                 Minecraft.m_91087_().m_167973_().m_171103_(ModelHerobrineObsidianDiamondChestplate.LAYER_LOCATION)
                              ))
                              .RightArm,
                           "head",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "hat",
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
         return "annoyingvillagers:textures/models/armor/herobrine_obsidian_armor_layer_1.png";
      }

      public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
         super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
         if (player.m_6844_(EquipmentSlot.CHEST) == stack) {
            ArmorUtil.dropArmorSlot(player, EquipmentSlot.FEET, "Herobrine Obsidian Diamond Chestplate");
            ArmorUtil.dropArmorSlot(player, EquipmentSlot.LEGS, "Herobrine Obsidian Diamond Chestplate");
         }
      }
   }
}
