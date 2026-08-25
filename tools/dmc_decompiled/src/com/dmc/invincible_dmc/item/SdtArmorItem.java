package com.dmc.invincible_dmc.item;

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
import org.jetbrains.annotations.NotNull;

public class SdtArmorItem extends ArmorItem {
   public static final ArmorMaterial MATERIAL = new ArmorMaterial() {
      public int m_266425_(Type type) {
         return 0;
      }

      public int m_7366_(Type type) {
         return 0;
      }

      public int m_6646_() {
         return 0;
      }

      @NotNull
      public SoundEvent m_7344_() {
         return SoundEvents.f_11679_;
      }

      @NotNull
      public Ingredient m_6230_() {
         return Ingredient.f_43901_;
      }

      @NotNull
      public String m_6082_() {
         return "invincible_dmc:sdt_armor";
      }

      public float m_6651_() {
         return 0.0F;
      }

      public float m_6649_() {
         return 0.0F;
      }
   };
   private static final String EMPTY_TEXTURE = "invincible_dmc:textures/entity/empty";

   public SdtArmorItem() {
      super(MATERIAL, Type.HELMET, new Properties());
   }

   public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
      return "invincible_dmc:textures/entity/empty";
   }
}
