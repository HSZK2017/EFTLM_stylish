package com.dmc.invincible_dmc.compat.combat_evolution;

import com.dmc.invincible_dmc.compat.ICompatModule;
import com.dmc.invincible_dmc.gameassets.DMCWeaponCategories;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.utils.DMCLog;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.shelmarow.combat_evolution.api.event.OnExecutionStartEvent;
import net.shelmarow.combat_evolution.api.event.RegisterCustomExecutionEvent;
import net.shelmarow.combat_evolution.execution.ExecutionTypeManager;
import net.shelmarow.combat_evolution.execution.ExecutionTypeManager.Type;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;

public final class CombatEvolutionCompat implements ICompatModule {
   private static final int YAMATO_EXECUTION_DURATION_TICKS = 126;
   private static final Vec3 YAMATO_EXECUTION_OFFSET = new Vec3(2.5, 0.0, 0.0);

   @Override
   public void onLoad(FMLJavaModLoadingContext context) {
      context.getModEventBus().addListener(this::registerExecutionType);
      MinecraftForge.EVENT_BUS.register(this);
      DMCLog.info(DMCLog.Category.COMPAT, "[CombatEvolutionCompat] Combat Evolution integration loaded");
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onExecutionStart(OnExecutionStartEvent event) {
      LivingEntityPatch<?> executorPatch = event.getExecutor();
      LivingEntity livingEntity = (LivingEntity)executorPatch.getOriginal();
      LivingEntityPatch<?> targetPatch = event.getTarget();
      if (executorPatch instanceof ServerPlayerPatch) {
         if (event.getType()
            .equals(ExecutionTypeManager.getExecutionTypeByCategory(DMCWeaponCategories.DMC5_YAMATO, Styles.TWO_HAND, null, executorPatch, targetPatch))) {
         }
      }
   }

   private void registerExecutionType(RegisterCustomExecutionEvent event) {
      Type yamatoExecution = new Type(YamatoAnimations.YAMATO_EXECUTION_DASH, null, YAMATO_EXECUTION_OFFSET, 0.0F, 126);
      event.registerExecutionByCategory(DMCWeaponCategories.DMC5_YAMATO, yamatoExecution);
   }
}
