package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import com.pla.annoyingvillagers.item.BlueDemonChestplateItem;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.FORGE
)
public final class BlueDemonChestplateEvent {
   @SubscribeEvent
   public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
      if (event.getSlot() == EquipmentSlot.CHEST) {
         ItemStack oldStack = event.getFrom();
         if (BlueDemonChestplateItem.isBlueDemonChestplate(oldStack)) {
            BlueDemonChestplateItem.stopBuff(oldStack);
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDamage(LivingHurtEvent event) {
      LivingEntity wearer = event.getEntity();
      if (wearer instanceof Player) {
         if (wearer.m_6084_()) {
            ItemStack chest = wearer.m_6844_(EquipmentSlot.CHEST);
            if (BlueDemonChestplateItem.isBlueDemonChestplate(chest)) {
               float finalDamage = event.getAmount();
               if (!(finalDamage <= 0.0F)) {
                  if (BlueDemonChestplateItem.isBuffActive(chest)) {
                     if (event.getSource().m_7639_() instanceof LivingEntity attacker && attacker != wearer) {
                        float chance = new Random().nextFloat();
                        if (chance <= 0.2F) {
                           attacker.m_7292_(new MobEffectInstance((MobEffect)AnnoyingVillagersModMobEffects.ELECTRIFY.get(), 20, 2));
                        } else if (chance <= 0.6F) {
                           attacker.m_7292_(new MobEffectInstance((MobEffect)AnnoyingVillagersModMobEffects.ELECTRIFY.get(), 20, 1));
                        }
                     }
                  } else if (!BlueDemonChestplateItem.isFullyCharged(chest)) {
                     int gainedCharge = Math.max(1, Mth.m_14167_(finalDamage));
                     BlueDemonChestplateItem.addStoredCharge(chest, gainedCharge);
                  }
               }
            }
         }
      }
   }
}
