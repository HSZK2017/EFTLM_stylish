package com.dmc.invincible_dmc.client;

import com.dmc.invincible_dmc.client.config.YamatoClientConfig;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPPortalStyleSync;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT},
   bus = Bus.FORGE
)
public final class ClientPortalStyleSync {
   private static int lastSentStyle = -1;

   private ClientPortalStyleSync() {
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         Minecraft minecraft = Minecraft.m_91087_();
         if (minecraft.f_91074_ != null && minecraft.m_91403_() != null) {
            int style = YamatoClientConfig.PORTAL_PARTICLE_STYLE.get() == YamatoClientConfig.PortalParticleStyle.ORIGINAL ? 1 : 0;
            if (style != lastSentStyle) {
               DMCNetwork.sendToServer(new CPPortalStyleSync((byte)style));
               lastSentStyle = style;
            }
         }
      }
   }

   @SubscribeEvent
   public static void onClientLogin(LoggingIn event) {
      lastSentStyle = -1;
   }

   @SubscribeEvent
   public static void onClientLogout(LoggingOut event) {
      lastSentStyle = -1;
   }
}
