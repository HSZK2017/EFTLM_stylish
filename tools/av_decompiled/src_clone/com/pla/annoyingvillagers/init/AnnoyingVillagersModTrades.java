package com.pla.annoyingvillagers.init;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class AnnoyingVillagersModTrades {
   @SubscribeEvent
   public static void registerWanderingTrades(WandererTradesEvent wanderertradesevent) {
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42417_), new ItemStack(Items.f_42737_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades()
         .add(new BasicItemListing(new ItemStack(Items.f_42678_), new ItemStack(Items.f_42417_, 10), new ItemStack(Items.f_42747_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42690_), new ItemStack(Items.f_42707_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42690_), new ItemStack(Items.f_42708_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42690_), new ItemStack(Items.f_42709_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42690_), new ItemStack(Items.f_42710_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades()
         .add(new BasicItemListing(new ItemStack(Items.f_42415_, 30), new ItemStack(Items.f_42587_, 20), new ItemStack(Items.f_42616_, 64), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades()
         .add(new BasicItemListing(new ItemStack(Items.f_42393_), new ItemStack(Items.f_42415_), new ItemStack(Items.f_42713_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42747_), new ItemStack(Items.f_42612_, 64), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42616_, 2), new ItemStack(Items.f_42455_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42616_), new ItemStack(Items.f_42656_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades()
         .add(new BasicItemListing(new ItemStack(Items.f_42436_), new ItemStack(Items.f_42484_), new ItemStack(Blocks.f_50077_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42616_, 10), new ItemStack(Items.f_42418_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42411_), new ItemStack(Items.f_42450_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades()
         .add(
            new BasicItemListing(
               new ItemStack(Items.f_42616_, 40),
               new ItemStack(Items.f_151049_, 60),
               new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANT_BED_ITEM.get()),
               10,
               5,
               0.05F
            )
         );
      wanderertradesevent.getGenericTrades()
         .add(
            new BasicItemListing(
               new ItemStack(Blocks.f_50201_),
               new ItemStack(Items.f_42686_),
               new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANT_BED_ITEM.get()),
               10,
               5,
               1.0F
            )
         );
      wanderertradesevent.getGenericTrades()
         .add(new BasicItemListing(new ItemStack(Items.f_42690_), new ItemStack(Items.f_42747_), new ItemStack(Blocks.f_50313_), 10, 5, 0.9F));
      wanderertradesevent.getGenericTrades()
         .add(
            new BasicItemListing(
               new ItemStack(Items.f_42729_, 5),
               new ItemStack(Blocks.f_50273_, 50),
               new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANT_BED_ITEM.get()),
               10,
               5,
               1.0F
            )
         );
      wanderertradesevent.getGenericTrades()
         .add(
            new BasicItemListing(
               new ItemStack(Items.f_42710_),
               new ItemStack(Items.f_42716_),
               new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANT_BED_ITEM.get()),
               10,
               5,
               1.0F
            )
         );
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Blocks.f_50314_), new ItemStack(Items.f_42586_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades()
         .add(new BasicItemListing(new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANT_BED_ITEM.get()), new ItemStack(Items.f_42588_, 50), 10, 5, 0.5F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42710_), new ItemStack(Items.f_42586_, 30), 10, 5, 0.9F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42616_, 2), new ItemStack(Items.f_42588_), 10, 5, 1.0F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Blocks.f_50074_), new ItemStack(Items.f_42585_), 10, 5, 1.0F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42584_, 30), new ItemStack(Items.f_42585_, 20), 10, 5, 1.0F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Blocks.f_50144_), new ItemStack(Items.f_42542_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.f_42448_), new ItemStack(Blocks.f_50141_), 10, 5, 0.05F));
      wanderertradesevent.getGenericTrades()
         .add(new BasicItemListing(new ItemStack(Blocks.f_50087_, 55), new ItemStack(Items.f_42545_, 20), new ItemStack(Items.f_42748_, 8), 10, 5, 1.0F));
      wanderertradesevent.getGenericTrades()
         .add(new BasicItemListing(new ItemStack(Items.f_42715_, 50), new ItemStack(Items.f_42710_), new ItemStack(Items.f_42729_), 10, 5, 1.0F));
   }
}
