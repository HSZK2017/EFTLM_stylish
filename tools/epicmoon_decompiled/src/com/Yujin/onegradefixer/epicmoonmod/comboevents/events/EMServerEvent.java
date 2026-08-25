package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.Yujin.onegradefixer.epicmoonmod.animations.EMAnimations;
import com.Yujin.onegradefixer.epicmoonmod.compat.compat;
import com.Yujin.onegradefixer.epicmoonmod.config.EMConfig;
import com.Yujin.onegradefixer.epicmoonmod.effect.EMeffects;
import com.Yujin.onegradefixer.epicmoonmod.item.EpicmoonItems;
import com.Yujin.onegradefixer.epicmoonmod.skill.weapon_innate.DualInnate;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@EventBusSubscriber(
   modid = "epicmoonmod"
)
public class EMServerEvent {
   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && !event.player.m_9236_().f_46443_) {
         Player player = event.player;
         if (player.m_21023_((MobEffect)EMeffects.POISE.get())) {
            MobEffectInstance currentEffect = player.m_21124_((MobEffect)EMeffects.POISE.get());
            if (currentEffect != null) {
               int currentDuration = currentEffect.m_19557_();
               int amplifier = currentEffect.m_19564_();
               if (amplifier < 4 && currentDuration > 1200) {
                  int newAmplifier = amplifier + 1;
                  player.m_21195_((MobEffect)EMeffects.POISE.get());
                  MobEffectInstance upgradedEffect = new MobEffectInstance(
                     (MobEffect)EMeffects.POISE.get(), 600, newAmplifier, currentEffect.m_19571_(), currentEffect.m_19572_(), currentEffect.m_19575_()
                  );
                  player.m_147207_(upgradedEffect, player);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingHurt(LivingHurtEvent event) {
      LivingEntity target = event.getEntity();
      DamageSource source = event.getSource();
      if (source.m_7639_() instanceof LivingEntity attacker) {
         if (attacker.m_9236_().f_46443_) {
            return;
         }

         if (attacker.m_21023_((MobEffect)EMeffects.POISE.get())) {
            MobEffectInstance currentEffect = attacker.m_21124_((MobEffect)EMeffects.POISE.get());
            double TRIGGER_CHANCE = (double)currentEffect.m_19564_() * 0.2 + 0.2;
            if (attacker.m_217043_().m_188500_() < TRIGGER_CHANCE) {
               float originalDamage = event.getAmount();
               float newDamage = originalDamage * 1.3F;
               event.setAmount(newDamage);
               if (currentEffect != null) {
                  int currentDuration = currentEffect.m_19557_();
                  int durationToSubtract = Math.round(newDamage * 2.0F);
                  int newDuration = currentDuration - durationToSubtract;
                  if (newDuration > 0) {
                     MobEffectInstance updatedEffect = new MobEffectInstance(
                        (MobEffect)EMeffects.POISE.get(),
                        newDuration,
                        currentEffect.m_19564_(),
                        currentEffect.m_19571_(),
                        currentEffect.m_19572_(),
                        currentEffect.m_19575_()
                     );
                     attacker.m_21195_((MobEffect)EMeffects.POISE.get());
                     attacker.m_7292_(updatedEffect);
                  } else {
                     attacker.m_21195_((MobEffect)EMeffects.POISE.get());
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingHurt(LivingAttackEvent event) {
      if (!event.getEntity().m_9236_().f_46443_) {
         LivingEntity entity = event.getEntity();
         LivingEntityPatch<?> patchi = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         if (patchi != null) {
            EntityState state = patchi.getEntityState();
            ResultType result = state.attackResult(event.getSource());
            if (result == ResultType.BLOCKED) {
               return;
            }

            if (patchi instanceof PlayerPatch<?> playerPatch) {
               SkillContainer guardContainer = playerPatch.getSkill(SkillSlots.GUARD);
               if (guardContainer != null && !guardContainer.isEmpty() && playerPatch.isHoldingSkill(guardContainer.getSkill())) {
                  return;
               }
            }
         }

         DamageSource source = event.getSource();
         if (event.getEntity() instanceof Player player) {
            PlayerPatch playerPatchx = (PlayerPatch)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
            if (source.m_7639_() instanceof LivingEntity attacker) {
               if (attacker.m_9236_().f_46443_) {
                  return;
               }

               if (playerPatchx != null) {
                  ItemStack heldItem = player.m_21205_();
                  if (heldItem.m_150930_((Item)EpicmoonItems.VALENCINA_DUAL_SWORDS.get())) {
                     player.getCapability(EpicFightCapabilities.CAPABILITY_SKILL)
                        .ifPresent(
                           capabilitySkill -> {
                              SkillContainer skillContainer = capabilitySkill.getSkillContainerFor(SkillSlots.WEAPON_INNATE);
                              if (skillContainer.getSkill() != null) {
                                 DualInnate dualInnate = (DualInnate)skillContainer.getSkill();
                                 int b = dualInnate.getEye(skillContainer);
                                 if (b >= 3) {
                                    LivingEntity entity1 = event.getEntity();
                                    float afterArmor = CombatRules.m_19272_(
                                       event.getAmount(), (float)entity1.m_21230_(), (float)entity1.m_21133_(Attributes.f_22285_)
                                    );
                                    float finalDamage = afterArmor;
                                    if (afterArmor > 0.0F) {
                                       int epf = EnchantmentHelper.m_44856_(entity1.m_6168_(), event.getSource());
                                       if (epf > 0) {
                                          finalDamage = CombatRules.m_19269_(afterArmor, (float)epf);
                                       }
                                    }

                                    PlayerPatch<?> patch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
                                    if (patch instanceof ServerPlayerPatch serverPatch) {
                                       float maxAutoDodgeDamage = EMConfig.getAutoDodgeMaxDamage();
                                       if (maxAutoDodgeDamage > 0.0F && finalDamage <= maxAutoDodgeDamage) {
                                          event.setCanceled(true);
                                          StaticAnimation staticAnimation = compat.getDualAnimation();
                                          playerPatch.playAnimationSynchronized(EMAnimations.DUAL_DODGE, 0.0F);
                                          dualInnate.setEye(skillContainer, b - 3);
                                       }
                                    }
                                 }
                              }
                           }
                        );
                  }
               }
            }
         }
      }
   }
}
