package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import java.util.Random;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class GreenVillagerKnightHelmetFixItem extends Item {
   public GreenVillagerKnightHelmetFixItem() {
      super(new Properties().m_41487_(1).m_41497_(Rarity.COMMON));
   }

   private ItemStack randomDamage(ItemStack itemStack) {
      int maxDamage = itemStack.m_41776_();
      itemStack.m_41721_(new Random().nextInt(maxDamage / 3, maxDamage * 3 / 4));
      return itemStack;
   }

   public void m_6883_(ItemStack stack, Level level, Entity entity, int slotId, boolean flag) {
      if (!level.f_46443_ && entity instanceof Player player) {
         if (stack.m_41720_() == this) {
            ItemStack replacement = this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.GREEN_VILLAGER_KNIGHT_HELMET.get()));
            player.m_150109_().m_6836_(slotId, replacement);
         }
      }
   }
}
