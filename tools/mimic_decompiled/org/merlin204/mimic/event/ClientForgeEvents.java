package org.merlin204.mimic.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent.BossEventProgress;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.merlin204.mimic.client.gui.BossBar;

@EventBusSubscriber(
   modid = "mimic",
   value = {Dist.CLIENT}
)
public class ClientForgeEvents {
   @SubscribeEvent
   public static void onRenderBossBar(BossEventProgress event) {
      if (BossBar.renderBossBar(event.getGuiGraphics(), event.getBossEvent(), event.getX(), event.getY())) {
         event.setCanceled(true);
      }
   }
}
