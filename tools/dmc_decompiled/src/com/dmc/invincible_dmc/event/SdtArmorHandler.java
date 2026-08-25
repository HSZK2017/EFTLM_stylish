package com.dmc.invincible_dmc.event;

import com.dmc.invincible_dmc.item.SdtArmorItem;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@EventBusSubscriber(
   modid = "invincible_dmc"
)
public class SdtArmorHandler {
   public static boolean isWearingSdtArmor(Player player) {
      return player.m_6844_(EquipmentSlot.HEAD).m_41720_() instanceof SdtArmorItem;
   }

   public static boolean isSdtArmorActive(Player player) {
      return isWearingSdtArmor(player) && !hasYamatoSkill(player);
   }

   private static boolean hasYamatoSkill(Player player) {
      PlayerPatch patch = (PlayerPatch)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
      if (patch == null) {
         return false;
      } else {
         SkillContainer container = patch.getSkill(SkillSlots.WEAPON_INNATE);
         return container != null && !container.isEmpty() && container.getSkill() instanceof VergilSkill;
      }
   }

   @SubscribeEvent
   public static void onLivingTick(LivingTickEvent event) {
      if (event.getEntity() instanceof Player player) {
         if (!player.m_9236_().m_5776_()) {
            if (isSdtArmorActive(player)) {
               if (player.f_19797_ % 20 == 0) {
                  player.m_5634_(player.m_21233_() * 0.025F);
               }

               player.m_20095_();
               applyEffect(player, MobEffects.f_19600_, 2);
               applyEffect(player, MobEffects.f_19598_, 0);
               applyEffect(player, MobEffects.f_19596_, 0);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingHurt(LivingHurtEvent event) {
      if (event.getEntity() instanceof Player player) {
         if (isSdtArmorActive(player)) {
            event.setAmount(event.getAmount() * 0.6F);
         }
      }
   }

   @SubscribeEvent
   public static void onLivingAttack(LivingAttackEvent event) {
      if (event.getEntity() instanceof Player player) {
         if (isSdtArmorActive(player)) {
            if (event.getSource().m_269533_(DamageTypeTags.f_268745_)) {
               event.setCanceled(true);
            }
         }
      }
   }

   private static void applyEffect(Player player, MobEffect effect, int amplifier) {
      MobEffectInstance existing = player.m_21124_(effect);
      if (existing == null || existing.m_19564_() < amplifier || existing.m_19557_() < 5) {
         player.m_7292_(new MobEffectInstance(effect, 5, amplifier, false, false, false));
      }
   }
}
