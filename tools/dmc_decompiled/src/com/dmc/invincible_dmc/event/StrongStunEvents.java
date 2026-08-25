package com.dmc.invincible_dmc.event;

import com.dmc.invincible_dmc.api.stun.StrongStunController;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "invincible_dmc"
)
public final class StrongStunEvents {
   private StrongStunEvents() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST,
      receiveCanceled = true
   )
   public static void onLivingDeath(LivingDeathEvent event) {
      if (!event.isCanceled()) {
         StrongStunController.finish(event.getEntity(), "death");
         StrongStunController.finishOwnedTargets(event.getEntity(), "source_death");
      }
   }

   @SubscribeEvent
   public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
      if (event.getEntity() instanceof LivingEntity livingEntity) {
         StrongStunController.finish(livingEntity, "leave_level");
         StrongStunController.finishOwnedTargets(livingEntity, "source_leave_level");
      }
   }
}
