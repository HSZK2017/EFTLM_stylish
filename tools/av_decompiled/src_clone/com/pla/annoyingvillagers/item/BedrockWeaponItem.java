package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.util.ArmorUtil;
import java.util.List;
import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;

public class BedrockWeaponItem extends SwordItem {
   public BedrockWeaponItem() {
      super(new Tier() {
         public int m_6609_() {
            return 3000;
         }

         public float m_6624_() {
            return 50.0F;
         }

         public float m_6631_() {
            return 0.0F;
         }

         public int m_6604_() {
            return 4;
         }

         public int m_6601_() {
            return 0;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Blocks.f_50752_)});
         }
      }, 3, 0.5F, new Properties().m_41486_());
   }

   public boolean m_7579_(@NotNull ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
      if (!pAttacker.m_9236_().m_5776_()) {
         ArmorUtil.damageArmor(pTarget, new Random().nextInt(1, 3));
      }

      return super.m_7579_(pStack, pTarget, pAttacker);
   }

   public void m_7373_(@NotNull ItemStack itemstack, Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
      list.add(Component.m_237115_("tooltip.annoyingvillagers.bedrock_weapon"));
   }

   public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
      return true;
   }

   public float m_8102_(@NotNull ItemStack stack, @NotNull BlockState state) {
      return 30.0F;
   }

   public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ToolAction action) {
      return action == ToolActions.PICKAXE_DIG
         || action == ToolActions.AXE_DIG
         || action == ToolActions.SHOVEL_DIG
         || action == ToolActions.HOE_DIG
         || action == ToolActions.SHEARS_DIG
         || action == ToolActions.SWORD_DIG;
   }
}
