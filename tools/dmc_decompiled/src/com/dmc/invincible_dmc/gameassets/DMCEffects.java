package com.dmc.invincible_dmc.gameassets;

import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordPatch;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.gameassets.mobeffects.DMCStunImmunityEffect;
import com.dmc.invincible_dmc.gameassets.mobeffects.EnemyStepEffect;
import com.dmc.invincible_dmc.gameassets.mobeffects.SlowEffect;
import com.dmc.invincible_dmc.gameassets.mobeffects.StopEffect;
import com.dmc.invincible_dmc.gameassets.mobeffects.VerticalStop;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.HurtableEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE
)
public class DMCEffects {
   public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "invincible_dmc");
   public static final RegistryObject<MobEffect> ENEMY_STEP = EFFECTS.register("enemy_step", EnemyStepEffect::new);
   public static final RegistryObject<MobEffect> STOP = EFFECTS.register("stop", StopEffect::new);
   public static final RegistryObject<MobEffect> SLOW = EFFECTS.register("slow", SlowEffect::new);
   public static final RegistryObject<MobEffect> VERTICALSTOP = EFFECTS.register("verticalstop", VerticalStop::new);
   public static final RegistryObject<MobEffect> DMC_STUN_IMMUNITY = EFFECTS.register("dmc_stun_immunity", DMCStunImmunityEffect::new);

   @SubscribeEvent
   public static void onLivingHurt(LivingHurtEvent event) {
      if (event.getEntity().m_21023_((MobEffect)ENEMY_STEP.get())) {
         event.setCanceled(true);
      }

      LivingEntity livingEntity = event.getEntity();
      HurtableEntityPatch<?> hurtableEntityPatch = (HurtableEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingEntity, HurtableEntityPatch.class);
      if (hurtableEntityPatch != null && livingEntity.m_21023_((MobEffect)SLOW.get()) && event.getSource() instanceof EpicFightDamageSource efd) {
         boolean exclude = efd.m_269533_(YamatoAnimations.SLOW_PERSISTENT)
            || efd.m_269533_(DMCSummonedSwordPatch.SUMMONED_SWORD_DAMAGE) && !efd.m_269533_(DMCSummonedSwordPatch.STORM_SWORD_DAMAGE);
         if (!exclude) {
            event.getEntity().m_21195_((MobEffect)SLOW.get());
         }
      }
   }
}
