package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class EnchantBedItem extends Item {
   public EnchantBedItem() {
      super(new Properties().m_41487_(1).m_41486_().m_41497_(Rarity.COMMON));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean m_5812_(@NotNull ItemStack itemstack) {
      return true;
   }

   public void m_7373_(@NotNull ItemStack itemstack, Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
      list.add(Component.m_237115_("tooltip.annoyingvillagers.enchanted_bed"));
   }

   @NotNull
   public InteractionResult m_6225_(@NotNull UseOnContext useOnContext) {
      super.m_6225_(useOnContext);
      useOnContext.m_43725_()
         .m_7731_(
            new BlockPos(useOnContext.m_8083_().m_123341_(), useOnContext.m_8083_().m_123342_() + 1, useOnContext.m_8083_().m_123343_()),
            ((Block)AnnoyingVillagersModBlocks.ENCHANT_BED.get()).m_49966_(),
            3
         );
      if (useOnContext.m_43723_() != null) {
         ItemStack itemstack = new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANT_BED_ITEM.get());
         useOnContext.m_43723_()
            .m_150109_()
            .m_36022_(itemstack1 -> itemstack.m_41720_() == itemstack1.m_41720_(), 1, useOnContext.m_43723_().f_36095_.m_39730_());
      }

      return InteractionResult.SUCCESS;
   }

   public void m_6883_(@NotNull ItemStack itemstack, @NotNull Level level, @NotNull Entity entity, int i, boolean flag) {
      super.m_6883_(itemstack, level, entity, i, flag);
   }
}
