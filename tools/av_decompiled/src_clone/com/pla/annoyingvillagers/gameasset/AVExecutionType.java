package com.pla.annoyingvillagers.gameasset;

import net.minecraft.world.phys.Vec3;
import net.shelmarow.combat_evolution.execution.ExecutionTypeManager.Type;

public class AVExecutionType {
   public static final Type STRANGLE = new Type(
      AnimsPugilistSteve.STRANGLE_EXECUTE, AnimsPugilistSteve.STRANGLE_EXECUTE_HIT, new Vec3(0.8F, 0.0, 0.0), -10.0F, 100
   );
   public static final Type WRESTLING = new Type(
      AnimsPugilistSteve.WRESTLING_EXECUTE, AnimsPugilistSteve.WRESTLING_EXECUTE_HIT, new Vec3(1.2F, 0.0, 0.0), -10.0F, 100
   );
   public static final Type WRESTLING_BACK = new Type(
      AnimsPugilistSteve.WRESTLING_BACK_EXECUTE, AnimsPugilistSteve.WRESTLING_BACK_EXECUTE_HIT, new Vec3(1.2F, 0.0, 0.0), -10.0F, 100
   );
   public static final Type STAB = new Type(AnimsPugilistSteve.STAB_EXECUTE, AnimsPugilistSteve.STAB_EXECUTE_HIT, new Vec3(1.2F, 0.0, 0.0), -10.0F, 100);
   public static final Type DUAL_STAB = new Type(
      AnimsPugilistSteve.DUAL_STAB_EXECUTE, AnimsPugilistSteve.STAB_EXECUTE_HIT, new Vec3(1.2F, 0.0, 0.0), -10.0F, 100
   );
   public static final Type SHIELD = new Type(AnimsPugilistSteve.SHIELD_EXECUTE, AnimsPugilistSteve.SHIELD_EXECUTE_HIT, new Vec3(1.2F, 0.0, 0.0), -10.0F, 100);
}
