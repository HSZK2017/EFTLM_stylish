package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.FORGE
)
public final class HerobrineDragonDismountEvent {
   @SubscribeEvent
   public static void onMount(EntityMountEvent event) {
      if (event.isDismounting()) {
         if (event.getEntityBeingMounted() instanceof HerobrineDragonEntity dragon) {
            if (!dragon.m_9236_().f_46443_) {
               if (!dragon.m_20096_() && !dragon.isNearGround() && event.getEntityMounting() instanceof LivingEntity livingEntity) {
                  livingEntity.m_7292_(new MobEffectInstance(MobEffects.f_19591_, 200, 2));
               }
            }
         }
      }
   }
}
