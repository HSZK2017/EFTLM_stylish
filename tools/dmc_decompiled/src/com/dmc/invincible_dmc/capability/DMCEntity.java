package com.dmc.invincible_dmc.capability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;

public class DMCEntity {
   public static final DMCEntity EMPTY = new DMCEntity();
   private final Map<Phase, List<Entity>> phaseAttackTriedEntities = new HashMap<>();
   private final Set<Phase> usedPhases = new HashSet<>();

   public Map<Phase, List<Entity>> getPhaseAttackTriedEntities() {
      return this.phaseAttackTriedEntities;
   }

   public Set<Phase> getUsedPhases() {
      return this.usedPhases;
   }

   public List<Entity> getCurrentlyHurtEntities(Phase phase) {
      List<Entity> toReturn = this.phaseAttackTriedEntities.get(phase);
      if (toReturn == null) {
         List<Entity> newList = new ArrayList<>();
         this.phaseAttackTriedEntities.put(phase, newList);
         return newList;
      } else {
         return toReturn;
      }
   }

   public void resetAttackPhaseCache() {
      this.phaseAttackTriedEntities.clear();
      this.usedPhases.clear();
   }

   public void setPhaseUsed(Phase phase) {
      this.usedPhases.add(phase);
   }

   public boolean isPhaseUsed(Phase phase) {
      return this.usedPhases.contains(phase);
   }

   public void saveNBTData(CompoundTag tag) {
   }

   public void loadNBTData(CompoundTag tag) {
   }

   public void tick() {
   }
}
