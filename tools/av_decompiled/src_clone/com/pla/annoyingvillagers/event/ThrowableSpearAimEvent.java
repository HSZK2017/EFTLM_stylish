package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.clazz.ThrowableSpearItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.forgeevent.UpdatePlayerMotionEvent.CompositeLayer;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public final class ThrowableSpearAimEvent {
   private ThrowableSpearAimEvent() {
   }

   @SubscribeEvent
   public static void onCompositeMotion(CompositeLayer event) {
      LivingEntity livingEntity = (LivingEntity)event.getPlayerPatch().getOriginal();
      if (livingEntity.m_6117_() && livingEntity.m_21211_().m_41720_() instanceof ThrowableSpearItem) {
         event.setMotion(LivingMotions.AIM);
      }
   }
}
