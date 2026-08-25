package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class EnchantBedDeathEvent {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent livingdeathevent) {
      if (livingdeathevent != null && livingdeathevent.getEntity() != null) {
         execute(livingdeathevent, livingdeathevent.getEntity().m_9236_(), livingdeathevent.getEntity(), livingdeathevent.getSource().m_7639_());
      }
   }

   public static void execute(LevelAccessor levelaccessor, Entity entity, Entity entity1) {
      execute((Event)null, levelaccessor, entity, entity1);
   }

   private static void execute(@Nullable Event event, LevelAccessor levelaccessor, Entity entity, Entity entity1) {
      if (entity != null && entity1 != null) {
         if (entity instanceof LivingEntity livingentity && livingentity.m_21023_((MobEffect)AnnoyingVillagersModMobEffects.ENCHANT_BED_EFFECT.get())) {
            if (entity instanceof LivingEntity livingentity2) {
               livingentity2.m_21153_(20.0F);
            }

            if (event != null && event.isCancelable()) {
               event.setCanceled(true);
            }

            if (entity instanceof Player player) {
               player.m_6915_();
            }

            double d0;
            label150: {
               if (entity instanceof ServerPlayer serverplayer && !serverplayer.m_9236_().m_5776_()) {
                  d0 = (double)(
                     serverplayer.m_8963_().equals(serverplayer.m_9236_().m_46472_()) && serverplayer.m_8961_() != null
                        ? serverplayer.m_8961_().m_123341_()
                        : serverplayer.m_9236_().m_6106_().m_6789_()
                  );
                  break label150;
               }

               d0 = 0.0;
            }

            int i;
            label140: {
               if (entity instanceof ServerPlayer serverplayer && !serverplayer.m_9236_().m_5776_()) {
                  i = serverplayer.m_8963_().equals(serverplayer.m_9236_().m_46472_()) && serverplayer.m_8961_() != null
                     ? serverplayer.m_8961_().m_123342_()
                     : serverplayer.m_9236_().m_6106_().m_6527_();
                  break label140;
               }

               i = 0;
            }

            double d1;
            double d2;
            label130: {
               d1 = (double)(i + 1);
               if (entity instanceof ServerPlayer serverplayer && !serverplayer.m_9236_().m_5776_()) {
                  d2 = (double)(
                     serverplayer.m_8963_().equals(serverplayer.m_9236_().m_46472_()) && serverplayer.m_8961_() != null
                        ? serverplayer.m_8961_().m_123343_()
                        : serverplayer.m_9236_().m_6106_().m_6526_()
                  );
                  break label130;
               }

               d2 = 0.0;
            }

            entity.m_6021_(d0, d1, d2);
            if (entity instanceof ServerPlayer serverplayer) {
               ServerGamePacketListenerImpl servergamepacketlistenerimpl;
               label118: {
                  servergamepacketlistenerimpl = serverplayer.f_8906_;
                  if (entity instanceof ServerPlayer serverplayer1 && !serverplayer1.m_9236_().m_5776_()) {
                     d0 = (double)(
                        serverplayer1.m_8963_().equals(serverplayer1.m_9236_().m_46472_()) && serverplayer1.m_8961_() != null
                           ? serverplayer1.m_8961_().m_123341_()
                           : serverplayer1.m_9236_().m_6106_().m_6789_()
                     );
                     break label118;
                  }

                  d0 = 0.0;
               }

               label108: {
                  if (entity instanceof ServerPlayer serverplayer2 && !serverplayer2.m_9236_().m_5776_()) {
                     i = serverplayer2.m_8963_().equals(serverplayer2.m_9236_().m_46472_()) && serverplayer2.m_8961_() != null
                        ? serverplayer2.m_8961_().m_123342_()
                        : serverplayer2.m_9236_().m_6106_().m_6527_();
                     break label108;
                  }

                  i = 0;
               }

               label98: {
                  d1 = (double)(i + 1);
                  if (entity instanceof ServerPlayer serverplayer3 && !serverplayer3.m_9236_().m_5776_()) {
                     d2 = (double)(
                        serverplayer3.m_8963_().equals(serverplayer3.m_9236_().m_46472_()) && serverplayer3.m_8961_() != null
                           ? serverplayer3.m_8961_().m_123343_()
                           : serverplayer3.m_9236_().m_6106_().m_6526_()
                     );
                     break label98;
                  }

                  d2 = 0.0;
               }

               servergamepacketlistenerimpl.m_9774_(d0, d1, d2, entity.m_146908_(), entity.m_146909_());
            }

            if (entity instanceof LivingEntity livingentity2) {
               livingentity2.m_21195_((MobEffect)AnnoyingVillagersModMobEffects.ENCHANT_BED_EFFECT.get());
            }

            if (entity instanceof Player player && !player.m_9236_().m_5776_()) {
               player.m_5661_(Component.m_237113_("Your enchanted bed has saved you once. Right-click again to use it again!"), true);
            }
         }

         if (entity instanceof LivingEntity livingentity && livingentity.m_21055_(Items.f_42747_)) {
            entity.m_6074_();
            if (entity instanceof Player player) {
               ItemStack itemstack = new ItemStack(Items.f_42747_);
               player.m_150109_().m_36022_(itemstack1 -> itemstack.m_41720_() == itemstack1.m_41720_(), 1, player.f_36095_.m_39730_());
            }
         }
      }
   }
}
