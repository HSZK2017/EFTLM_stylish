package com.dmc.invincible_dmc.gameassets;

import com.dmc.invincible_dmc.gameassets.animations.stun.CustomStunAnimations;
import com.dmc.invincible_dmc.gameassets.animations.yamato.SummonedSwordAnimations;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoActionConfigs;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.AnimationManager.AnimationRegistryEvent;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.MOD
)
public class DevilMayCryAnimations {
   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void registerAnimations(AnimationRegistryEvent event) {
      event.newBuilder("invincible_dmc", DevilMayCryAnimations::build);
   }

   private static void build(AnimationBuilder animationBuilder) {
      YamatoAnimations.build(animationBuilder);
      CustomStunAnimations.build(animationBuilder);
      SummonedSwordAnimations.build(animationBuilder);
      YamatoActionConfigs.initAll();
   }
}
