package com.dmc.invincible_dmc.client;

import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.client.render.custom.BloomParticleRenderType;
import com.dmc.invincible_dmc.client.render.custom.SummonedSwordBloomPipeline;
import com.dmc.invincible_dmc.client.renderer.patched.entity.SdtRenderTransitionManager;
import com.dmc.invincible_dmc.network.server.S2CSdtEffectHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT},
   bus = Bus.FORGE
)
public final class ClientLifecycleEvents {
   private ClientLifecycleEvents() {
   }

   @SubscribeEvent
   public static void onClientPlayerLoggingIn(LoggingIn event) {
      DMComboEngine.resetForPlayerStateChange();
      SdtRenderTransitionManager.clearAll();
   }

   @SubscribeEvent
   public static void onClientPlayerLoggingOut(LoggingOut event) {
      DMComboEngine.resetForPlayerStateChange();
      SdtRenderTransitionManager.clearAll();
      S2CSdtEffectHandler.clearAllRemoteParticles();
      BloomParticleRenderType.Pipeline.releaseCachedTargets();
      SummonedSwordBloomPipeline.releaseCachedTargets();
   }

   @SubscribeEvent
   public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
      if (event.getLevel().m_5776_() && event.getEntity() instanceof AbstractClientPlayer player) {
         SdtRenderTransitionManager.clear(player.m_20148_());
      }
   }

   @SubscribeEvent
   public static void onLivingDeath(LivingDeathEvent event) {
      if (event.getEntity().m_9236_().m_5776_() && event.getEntity() == Minecraft.m_91087_().f_91074_) {
         DMComboEngine.resetForPlayerStateChange();
      }
   }
}
