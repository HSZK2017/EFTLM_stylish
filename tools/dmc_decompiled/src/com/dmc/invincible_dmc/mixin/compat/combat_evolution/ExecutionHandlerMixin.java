package com.dmc.invincible_dmc.mixin.compat.combat_evolution;

import com.dmc.invincible_dmc.gameassets.DMCWeaponCategories;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.shelmarow.combat_evolution.execution.ExecutionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mixin(
   value = {ExecutionHandler.class},
   remap = false
)
public abstract class ExecutionHandlerMixin {
   @Unique
   private static final double DEFAULT_EXECUTION_DISTANCE = 4.0;
   @Unique
   private static final double YAMATO_EXECUTION_DISTANCE = 8.0;

   @Inject(
      method = {"getEntityLookedAt"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void invincible$extendYamatoTargetingDistance(Player player, double distance, CallbackInfoReturnable<LivingEntity> cir) {
      if (Double.compare(distance, 4.0) == 0 && invincible$isHoldingYamato(player)) {
         cir.setReturnValue(ExecutionHandler.getEntityLookedAt(player, 8.0));
      }
   }

   @Inject(
      method = {"targetIsInRange"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void invincible$extendYamatoExecutionRange(
      LivingEntity executor, LivingEntity target, double minDistance, double maxDistance, double maxAngle, CallbackInfoReturnable<Boolean> cir
   ) {
      if (Double.compare(maxDistance, 4.0) == 0 && invincible$isHoldingYamato(executor)) {
         cir.setReturnValue(ExecutionHandler.targetIsInRange(executor, target, minDistance, 8.0, maxAngle));
      }
   }

   @Unique
   private static boolean invincible$isHoldingYamato(LivingEntity entity) {
      return EpicFightCapabilities.getItemStackCapability(entity.m_21205_()).getWeaponCategory() == DMCWeaponCategories.DMC5_YAMATO;
   }
}
