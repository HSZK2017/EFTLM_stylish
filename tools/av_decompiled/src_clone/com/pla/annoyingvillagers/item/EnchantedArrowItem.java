package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.entity.EnchantedArrowEntity;
import com.pla.annoyingvillagers.util.GlintColorHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EnchantedArrowItem extends ArrowItem {
   public EnchantedArrowItem(Properties properties) {
      super(properties);
   }

   public boolean m_5812_(@NotNull ItemStack stack) {
      return true;
   }

   @NotNull
   public AbstractArrow m_6394_(@NotNull Level level, @NotNull ItemStack ammoStack, @NotNull LivingEntity shooter) {
      EnchantedArrowEntity arrow = new EnchantedArrowEntity(level, shooter);
      arrow.setColorGlint(GlintColorHelper.getRandomColor());
      return arrow;
   }
}
