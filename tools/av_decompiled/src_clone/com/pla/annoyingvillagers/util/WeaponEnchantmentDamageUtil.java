package com.pla.annoyingvillagers.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public final class WeaponEnchantmentDamageUtil {
   private WeaponEnchantmentDamageUtil() {
   }

   public static float addSharpnessBonus(float baseDamage, LivingEntity owner, Class<? extends Item> weaponClass) {
      return baseDamage + getSharpnessDamageBonus(owner, weaponClass);
   }

   private static float getSharpnessDamageBonus(LivingEntity owner, Class<? extends Item> weaponClass) {
      if (owner == null) {
         return 0.0F;
      } else {
         ItemStack weaponStack = findWeaponStack(owner, weaponClass);
         if (weaponStack.m_41619_()) {
            return 0.0F;
         } else {
            int sharpnessLevel = EnchantmentHelper.m_44843_(Enchantments.f_44977_, weaponStack);
            return sharpnessLevel > 0 ? Enchantments.f_44977_.m_7335_(sharpnessLevel, MobType.f_21640_) : 0.0F;
         }
      }
   }

   private static ItemStack findWeaponStack(LivingEntity owner, Class<? extends Item> weaponClass) {
      ItemStack mainHand = owner.m_21205_();
      if (isWeapon(mainHand, weaponClass)) {
         return mainHand;
      } else {
         ItemStack offhand = owner.m_21206_();
         if (isWeapon(offhand, weaponClass)) {
            return offhand;
         } else {
            if (owner instanceof Player player) {
               for (ItemStack stack : player.m_150109_().f_35974_) {
                  if (isWeapon(stack, weaponClass)) {
                     return stack;
                  }
               }
            }

            return ItemStack.f_41583_;
         }
      }
   }

   private static boolean isWeapon(ItemStack stack, Class<? extends Item> weaponClass) {
      return !stack.m_41619_() && weaponClass.isInstance(stack.m_41720_());
   }
}
