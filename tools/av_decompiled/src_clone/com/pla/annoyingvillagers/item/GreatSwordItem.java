package com.pla.annoyingvillagers.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;

public class GreatSwordItem extends SwordItem {
   public GreatSwordItem() {
      super(new Tier() {
         public int m_6609_() {
            return 1650;
         }

         public float m_6624_() {
            return 4.0F;
         }

         public float m_6631_() {
            return 3.0F;
         }

         public int m_6604_() {
            return 4;
         }

         public int m_6601_() {
            return 5;
         }

         public Ingredient m_6282_() {
            return Ingredient.m_151265_();
         }
      }, 3, -2.5F, new Properties().m_41486_());
   }
}
