package com.pla.annoyingvillagers.clazz;

import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import net.minecraft.util.RandomSource;

public enum PlayerNpcTarget {
   MONSTER_HUNTER((Double)AnnoyingVillagersConfig.NPC_TARGET_WEIGHT_MONSTER_HUNTER.get()),
   VILLAGER_HUNTER((Double)AnnoyingVillagersConfig.NPC_TARGET_WEIGHT_VILLAGER_HUNTER.get()),
   PLAYER_HUNTER((Double)AnnoyingVillagersConfig.NPC_TARGET_WEIGHT_PLAYER_HUNTER.get()),
   HOSTILE_HUNTER((Double)AnnoyingVillagersConfig.NPC_TARGET_WEIGHT_HOSTILE_HUNTER.get()),
   PASSIVE_HUNTER((Double)AnnoyingVillagersConfig.NPC_TARGET_WEIGHT_PASSIVE_HUNTER.get()),
   ANIMAL_HUNTER((Double)AnnoyingVillagersConfig.NPC_TARGET_WEIGHT_ANIMAL_HUNTER.get());

   private final double weight;

   private PlayerNpcTarget() {
      this(1.0);
   }

   private PlayerNpcTarget(double weight) {
      if (weight < 0.0) {
         throw new IllegalArgumentException("weight must be >= 0");
      } else {
         this.weight = weight;
      }
   }

   public double getWeight() {
      return this.weight;
   }

   public static PlayerNpcTarget randomByWeight(RandomSource randomSource) {
      double total = 0.0;

      for (PlayerNpcTarget playerNpcTarget : values()) {
         total += playerNpcTarget.weight;
      }

      if (total <= 0.0) {
         PlayerNpcTarget[] value = values();
         return value[randomSource.m_188503_(value.length)];
      } else {
         double random = randomSource.m_188500_() * total;

         for (PlayerNpcTarget playerNpcTarget : values()) {
            random -= playerNpcTarget.weight;
            if (random < 0.0) {
               return playerNpcTarget;
            }
         }

         return values()[values().length - 1];
      }
   }
}
