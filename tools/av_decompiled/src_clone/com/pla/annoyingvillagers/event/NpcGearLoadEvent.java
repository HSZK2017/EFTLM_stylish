package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.util.EquipmentDataLoader;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "annoyingvillagers"
)
public class NpcGearLoadEvent {
   @SubscribeEvent
   public void onAddReloadListeners(AddReloadListenerEvent event) {
      event.addListener(new EquipmentDataLoader());
   }
}
