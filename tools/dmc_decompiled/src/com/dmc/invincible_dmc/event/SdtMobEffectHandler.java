package com.dmc.invincible_dmc.event;

import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.MobEffectEvent.Applicable;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE
)
public final class SdtMobEffectHandler {
   private SdtMobEffectHandler() {
   }

   @SubscribeEvent
   public static void onMobEffectApplicable(Applicable event) {
      if (!(event.getEntity() instanceof Player player) || player.m_9236_().m_5776_()) {
         return;
      }

      if (SinDevilTriggerManager.isPlayerInSDT(player)) {
         if (SinDevilTriggerManager.isHarmfulEffect(event.getEffectInstance())) {
            event.setResult(Result.DENY);
         }
      }
   }
}
