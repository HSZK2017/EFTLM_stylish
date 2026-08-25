package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.clazz.ThrowableSpearItem;
import com.pla.annoyingvillagers.entity.DiamondBoltProjectileEntity;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DiamondBoltItem extends ThrowableSpearItem {
   public DiamondBoltItem() {
      super(new Tier() {
         public int m_6609_() {
            return 1561;
         }

         public float m_6624_() {
            return 6.0F;
         }

         public float m_6631_() {
            return 2.4F;
         }

         public int m_6604_() {
            return 5;
         }

         public int m_6601_() {
            return 21;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Items.f_42415_)});
         }
      }, 3, -2.6F, new Properties());
   }

   @Override
   protected AbstractArrow createThrownProjectile(Level level, Player player, ItemStack stack) {
      return new DiamondBoltProjectileEntity(level, player, stack);
   }

   public void m_7373_(@NotNull ItemStack itemstack, Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
      list.add(Component.m_237115_("tooltip.annoyingvillagers.diamond_bolt"));
   }
}
