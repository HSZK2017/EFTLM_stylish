package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.entity.ShockWaveBlockEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.util.ArmorUtil;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class LegendarySwordItem extends SwordItem {
   public LegendarySwordItem() {
      super(new Tier() {
         public int m_6609_() {
            return 1561;
         }

         public float m_6624_() {
            return 4.0F;
         }

         public float m_6631_() {
            return 6.0F;
         }

         public int m_6604_() {
            return 1;
         }

         public int m_6601_() {
            return 2;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack((ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND.get())});
         }
      }, 3, -2.32F, new Properties().m_41486_());
   }

   public boolean m_7579_(@NotNull ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
      if (!pAttacker.m_9236_().m_5776_()) {
         ArmorUtil.damageArmor(pTarget, new Random().nextInt(1, 5));
      }

      return super.m_7579_(pStack, pTarget, pAttacker);
   }

   public void m_7373_(@NotNull ItemStack itemStack, Level level, @NotNull List<Component> componentList, @NotNull TooltipFlag tooltipFlag) {
      super.m_7373_(itemStack, level, componentList, tooltipFlag);
      componentList.add(Component.m_237115_("tooltip.annoyingvillagers.legendary_sword"));
   }

   public static void spawnCircleRing(ServerLevel level, BlockPos centerPos, int radius, LivingEntity owner) {
      double inner = ((double)radius - 0.5) * ((double)radius - 0.5);
      double outer = ((double)radius + 0.5) * ((double)radius + 0.5);

      for (int dx = -radius; dx <= radius; dx++) {
         for (int dz = -radius; dz <= radius; dz++) {
            double dist2 = (double)dx * (double)dx + (double)dz * (double)dz;
            if (dist2 >= inner && dist2 <= outer) {
               spawnShockWaveBlock(level, centerPos.m_7918_(dx, 0, dz), owner);
            }
         }
      }
   }

   private static void spawnShockWaveBlock(ServerLevel level, BlockPos startPos, LivingEntity owner) {
      int BLOCK_SEARCH_DEPTH = 256;
      int ENTITY_GROUND_LIFETIME = 10;
      BlockPos pos = startPos;
      BlockState state = level.m_8055_(startPos);
      int minY = level.m_141937_();

      for (int i = 0; i < 256 && pos.m_123342_() > minY && state.m_60799_() != RenderShape.MODEL; i++) {
         pos = pos.m_7495_();
         state = level.m_8055_(pos);
      }

      if (state.m_60799_() == RenderShape.MODEL) {
         ShockWaveBlockEntity blockEntity = new ShockWaveBlockEntity(
            level, (double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 1.0, (double)pos.m_123343_() + 0.5, state, 10
         );
         blockEntity.setOwnerUuid(owner.m_20148_());
         level.m_7967_(blockEntity);
      }
   }
}
