package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.entity.ThrownPoisonEggEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class PoisonEggItem extends Item {
   public PoisonEggItem() {
      super(new Properties().m_41487_(16));
   }

   public void m_7373_(ItemStack itemstack, Level level, List<Component> list, TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
      list.add(Component.m_237115_("tooltip.annoyingvillagers.poison_egg"));
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level pLevel, Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.m_21120_(pHand);
      pLevel.m_6263_(
         (Player)null,
         pPlayer.m_20185_(),
         pPlayer.m_20186_(),
         pPlayer.m_20189_(),
         SoundEvents.f_11877_,
         SoundSource.PLAYERS,
         0.5F,
         0.4F / (pLevel.m_213780_().m_188501_() * 0.4F + 0.8F)
      );
      if (!pLevel.f_46443_) {
         ThrownPoisonEggEntity thrownegg = new ThrownPoisonEggEntity(
            (EntityType<? extends ThrownPoisonEggEntity>)AnnoyingVillagersModEntities.THROWN_POISON_EGG.get(), pPlayer, pLevel
         );
         thrownegg.m_37446_(itemstack);
         thrownegg.m_37251_(pPlayer, pPlayer.m_146909_(), pPlayer.m_146908_(), 0.0F, 1.5F, 1.0F);
         pLevel.m_7967_(thrownegg);
      }

      pPlayer.m_36246_(Stats.f_12982_.m_12902_(this));
      if (!pPlayer.m_150110_().f_35937_) {
         itemstack.m_41774_(1);
      }

      return InteractionResultHolder.m_19092_(itemstack, pLevel.m_5776_());
   }
}
