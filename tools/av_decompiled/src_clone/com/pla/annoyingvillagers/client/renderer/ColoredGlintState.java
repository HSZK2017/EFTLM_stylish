package com.pla.annoyingvillagers.client.renderer;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.item.BlueDemonChestplateItem;
import com.pla.annoyingvillagers.item.BlueDemonTridentItem;
import com.pla.annoyingvillagers.util.GlintColorHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ColoredGlintState {
   public static final int NONE = 0;
   public static final int ORANGE = 1;
   public static final int CYAN = 2;
   public static final int BLUE = 3;
   public static final int GREEN = 4;
   public static final int LIGHT_BLUE = 5;
   public static final int LIME = 6;
   public static final int MAGENTA = 7;
   public static final int PINK = 8;
   public static final int PURPLE = 9;
   public static final int RED = 10;
   public static final int YELLOW = 11;
   private static final ThreadLocal<Integer> MODE = ThreadLocal.withInitial(() -> 0);

   private ColoredGlintState() {
   }

   public static void setTargetStack(ItemStack stack) {
      int mode = GlintColorHelper.getColor(stack);
      if (mode == 0) {
         if (stack.m_150930_((Item)AnnoyingVillagersModItems.HEROBRINE_ENDER_EYE.get())) {
            mode = 1;
         } else if (!stack.m_150930_((Item)AnnoyingVillagersModItems.RED_AXE.get()) && !stack.m_150930_((Item)AnnoyingVillagersModItems.GIANT_RED_AXE.get())) {
            if (stack.m_150930_((Item)AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get()) && BlueDemonTridentItem.isFullyCharged(stack)
               || stack.m_150930_((Item)AnnoyingVillagersModItems.BLUE_DEMON_CHESTPLATE.get())
                  && (
                     BlueDemonChestplateItem.isFullyCharged(stack)
                        || BlueDemonChestplateItem.isBuffActive(stack)
                        || BlueDemonChestplateItem.hasBlueDemonHealingFoil(stack)
                  )) {
               mode = 2;
            }
         } else {
            mode = 10;
         }
      }

      MODE.set(mode);
   }

   public static int getMode() {
      return MODE.get();
   }

   public static void clear() {
      MODE.remove();
   }
}
