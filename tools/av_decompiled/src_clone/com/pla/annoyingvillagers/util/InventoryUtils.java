package com.pla.annoyingvillagers.util;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class InventoryUtils {
   public static void transferInventory(SimpleContainer from, SimpleContainer to) {
      int size = Math.min(from.m_6643_(), to.m_6643_());

      for (int i = 0; i < size; i++) {
         ItemStack stack = from.m_8020_(i);
         if (!stack.m_41619_()) {
            to.m_6836_(i, stack.m_41777_());
            from.m_6836_(i, ItemStack.f_41583_);
         }
      }
   }
}
