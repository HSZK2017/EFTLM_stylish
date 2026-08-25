package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.util.CombatBehaviour;
import java.util.Random;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class MonsterHealingEvent {
   @SubscribeEvent
   public static void onMonsterAttacked(LivingHurtEvent livingHurtEvent) {
      if (livingHurtEvent != null && livingHurtEvent.getEntity() != null) {
         LivingEntity entity = livingHurtEvent.getEntity();
         if (!entity.m_9236_().m_5776_()
            && (Boolean)AnnoyingVillagersConfig.VANILLA_MOB_CAN_DRINK_HEALING_POTION.get()
            && (entity instanceof Zombie || entity instanceof AbstractSkeleton)
            && entity.m_21223_() <= 10.0F
            && entity.getPersistentData().m_128451_("AvHealingCooldown") == 0
            && entity.m_6084_()) {
            CombatBehaviour.drinkingHealingPotion(entity, entity.m_9236_(), true, (double)livingHurtEvent.getAmount());
            entity.getPersistentData().m_128405_("AvHealingCooldown", new Random().nextInt(60, 200));
         }
      }
   }

   @SubscribeEvent
   public static void onMonsterTick(LivingTickEvent livingTickEvent) {
      if (livingTickEvent != null && livingTickEvent.getEntity() != null) {
         LivingEntity entity = livingTickEvent.getEntity();
         if (!entity.m_9236_().m_5776_()
            && (Boolean)AnnoyingVillagersConfig.VANILLA_MOB_CAN_DRINK_HEALING_POTION.get()
            && (entity instanceof Zombie || entity instanceof AbstractSkeleton)) {
            if (!entity.getPersistentData().m_128441_("AvHealingCooldown")) {
               entity.getPersistentData().m_128405_("AvHealingCooldown", 0);
            } else {
               int healingCooldown = entity.getPersistentData().m_128451_("AvHealingCooldown");
               if (healingCooldown > 0) {
                  entity.getPersistentData().m_128405_("AvHealingCooldown", healingCooldown - 1);
               }
            }
         }
      }
   }
}
