package com.pla.annoyingvillagers.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArmorUtil {
   public static void dropArmorSlot(LivingEntity living, EquipmentSlot slot, String preventArmor) {
      ItemStack stack = living.m_6844_(slot);
      if (!stack.m_41619_()) {
         if (!living.m_9236_().f_46443_) {
            living.m_19983_(stack.m_41777_());
         }

         living.m_8061_(slot, ItemStack.f_41583_);
         if (living instanceof Player p) {
            p.m_150109_().m_6596_();
            if (!p.m_9236_().m_5776_()) {
               p.m_5661_(Component.m_237113_("§eThe " + preventArmor + " rejects this piece!"), true);
            }
         }
      }
   }

   public static void damageArmor(LivingEntity target, int durabilityDamagePerPiece) {
      RandomSource random = target.m_217043_();
      ServerPlayer serverAttacker = target instanceof ServerPlayer serverPlayer ? serverPlayer : null;

      for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
         ItemStack armor = target.m_6844_(slot);
         if (!armor.m_41619_() && armor.m_41763_() && armor.m_220157_(durabilityDamagePerPiece, random, serverAttacker)) {
            armor.m_41774_(1);
            armor.m_41721_(0);
            target.m_8061_(slot, ItemStack.f_41583_);
         }
      }
   }
}
