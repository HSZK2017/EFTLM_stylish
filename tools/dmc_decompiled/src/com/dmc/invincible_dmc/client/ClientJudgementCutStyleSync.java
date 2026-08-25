package com.dmc.invincible_dmc.client;

import com.dmc.invincible_dmc.client.config.YamatoClientConfig;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPJudgementCutStyleSync;
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
public final class ClientJudgementCutStyleSync {
   private static int lastSentStyle = -1;

   private ClientJudgementCutStyleSync() {
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         Minecraft minecraft = Minecraft.m_91087_();
         if (minecraft.f_91074_ != null && minecraft.m_91403_() != null) {
            int style = YamatoClientConfig.JUDGEMENT_CUT_SEQUENCE_STYLE.get() == YamatoClientConfig.JudgementCutSequenceStyle.DMC4 ? 1 : 0;
            if (style != lastSentStyle) {
               DMCNetwork.sendToServer(new CPJudgementCutStyleSync((byte)style));
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
