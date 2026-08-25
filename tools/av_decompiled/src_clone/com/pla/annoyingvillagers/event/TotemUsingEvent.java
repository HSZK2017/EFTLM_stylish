package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.entity.AlexEntity;
import com.pla.annoyingvillagers.entity.ChrisEntity;
import com.pla.annoyingvillagers.entity.SteveEntity;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.task.DelayedTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@EventBusSubscriber
public class TotemUsingEvent {
   @SubscribeEvent
   public static void onLivingUseTotem(LivingUseTotemEvent event) {
      final LivingEntity entity = event.getEntity();
      ItemStack totem = event.getTotem();
      if (totem.m_150930_(Items.f_42747_)) {
         if (entity instanceof SteveEntity steveEntity && entity.m_9236_() instanceof ServerLevel serverLevel) {
            new DelayedTask(1) {
               @Override
               public void run() {
                  steveEntity.m_21153_(steveEntity.m_21233_());
                  ItemStack diamondSword = new ItemStack(Items.f_42388_);
                  diamondSword.m_41663_(Enchantments.f_44977_, 5);
                  diamondSword.m_41663_(Enchantments.f_44978_, 5);
                  steveEntity.m_21008_(InteractionHand.OFF_HAND, diamondSword);
                  steveEntity.setOffWeaponItem(diamondSword);
                  steveEntity.setState(1);
                  LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                  if (!entity.m_9236_().m_5776_() && entity.m_20194_() != null && livingEntityPatch != null) {
                     livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.GUARD_BREAK_ATTACK, 0.0F);
                  }
               }
            };
            new DelayedTask(10) {
               @Override
               public void run() {
                  serverLevel.m_6263_(null, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), SoundEvents.f_11673_, SoundSource.NEUTRAL, 1.0F, 1.0F);
                  ItemStack compressedDiamondHelmet = new ItemStack((ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND_HELMET.get());
                  compressedDiamondHelmet.m_41663_(Enchantments.f_44965_, 5);
                  compressedDiamondHelmet.m_41663_(Enchantments.f_44969_, 5);
                  compressedDiamondHelmet.m_41663_(Enchantments.f_44966_, 5);
                  compressedDiamondHelmet.m_41663_(Enchantments.f_44968_, 5);
                  steveEntity.m_8061_(EquipmentSlot.HEAD, compressedDiamondHelmet);
               }
            };
            new DelayedTask(20) {
               @Override
               public void run() {
                  serverLevel.m_6263_(null, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), SoundEvents.f_11673_, SoundSource.NEUTRAL, 1.0F, 1.0F);
                  ItemStack compressedDiamondChestplate = new ItemStack((ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND_CHESTPLATE.get());
                  compressedDiamondChestplate.m_41663_(Enchantments.f_44965_, 5);
                  compressedDiamondChestplate.m_41663_(Enchantments.f_44969_, 5);
                  compressedDiamondChestplate.m_41663_(Enchantments.f_44966_, 5);
                  compressedDiamondChestplate.m_41663_(Enchantments.f_44968_, 5);
                  steveEntity.m_8061_(EquipmentSlot.CHEST, compressedDiamondChestplate);
               }
            };
         }

         if (entity instanceof AlexEntity alexEntity && entity.m_9236_() instanceof ServerLevel) {
            new DelayedTask(1) {
               @Override
               public void run() {
                  alexEntity.m_21153_(alexEntity.m_21233_());
                  ItemStack diamondSword = new ItemStack((ItemLike)AnnoyingVillagersModItems.THUNDER_DIAMOND_BLADE.get());
                  diamondSword.m_41663_(Enchantments.f_44977_, 5);
                  diamondSword.m_41663_(Enchantments.f_44981_, 2);
                  diamondSword.m_41663_(Enchantments.f_44980_, 2);
                  diamondSword.m_41663_(Enchantments.f_44986_, 5);
                  alexEntity.m_21008_(InteractionHand.OFF_HAND, diamondSword);
                  alexEntity.m_21008_(InteractionHand.MAIN_HAND, diamondSword);
                  alexEntity.setOffWeaponItem(diamondSword);
                  alexEntity.setMainWeaponItem(diamondSword);
                  alexEntity.setState(1);
                  LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                  if (!entity.m_9236_().m_5776_() && entity.m_20194_() != null && livingEntityPatch != null) {
                     livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.GUARD_BREAK_ATTACK, 0.0F);
                  }
               }
            };
         }

         if (entity instanceof ChrisEntity chrisEntity && entity.m_9236_() instanceof ServerLevel) {
            new DelayedTask(1) {
               @Override
               public void run() {
                  chrisEntity.m_21153_(chrisEntity.m_21233_());
                  ItemStack diamondSword = new ItemStack(Items.f_42388_);
                  diamondSword.m_41663_(Enchantments.f_44980_, 5);
                  diamondSword.m_41663_(Enchantments.f_44977_, 5);
                  diamondSword.m_41663_(Enchantments.f_44986_, 5);
                  chrisEntity.m_21008_(InteractionHand.OFF_HAND, diamondSword);
                  chrisEntity.setOffWeaponItem(diamondSword);
                  chrisEntity.setState(1);
                  LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                  if (!entity.m_9236_().m_5776_() && entity.m_20194_() != null && livingEntityPatch != null) {
                     livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.GUARD_BREAK_ATTACK, 0.0F);
                  }
               }
            };
         }
      }
   }
}
