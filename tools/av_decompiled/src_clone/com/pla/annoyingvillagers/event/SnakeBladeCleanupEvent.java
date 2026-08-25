package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.item.DemoniacVoltageReaverItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.FORGE
)
public class SnakeBladeCleanupEvent {
   @SubscribeEvent
   public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
      if (!event.getLevel().m_5776_()) {
         if (event.loadedFromDisk() && event.getEntity().m_6095_() == AnnoyingVillagersModEntities.SNAKE_BLADE.get()) {
            event.setCanceled(true);
            event.getEntity().m_146870_();
         }
      }
   }

   @SubscribeEvent
   public static void onLivingTick(LivingTickEvent event) {
      if (!event.getEntity().m_9236_().m_5776_()) {
         DemoniacVoltageReaverItem.clearInterruptedSnakeAnimation(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onLogin(PlayerLoggedInEvent event) {
      Player player = event.getEntity();

      for (ItemStack stack : player.m_150109_().f_35974_) {
         if (stack.m_150930_((Item)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER.get())) {
            DemoniacVoltageReaverItem.clearSnakeAnimation(stack);
         }
      }
   }
}
