package com.dmc.invincible_dmc.item;

import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;

public class DoppelgangerSpawnEggItem extends ForgeSpawnEggItem {
   public DoppelgangerSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties properties) {
      super(type, backgroundColor, highlightColor, properties);
   }

   @Nonnull
   public InteractionResult m_6225_(UseOnContext context) {
      Level level = context.m_43725_();
      if (level instanceof ServerLevel serverLevel) {
         ItemStack itemstack = context.m_43722_();
         BlockPos blockpos = context.m_8083_();
         Direction direction = context.m_43719_();
         BlockState blockstate = level.m_8055_(blockpos);
         BlockPos spawnPos;
         if (blockstate.m_60812_(level, blockpos).m_83281_()) {
            spawnPos = blockpos;
         } else {
            spawnPos = blockpos.m_121945_(direction);
         }

         EntityType<?> entityType = this.m_43228_(itemstack.m_41783_());
         Entity spawned = entityType.m_20592_(
            serverLevel, itemstack, context.m_43723_(), spawnPos, MobSpawnType.SPAWN_EGG, false, !blockpos.equals(spawnPos) && direction == Direction.UP
         );
         if (spawned instanceof DoppelgangerEntity doppel) {
            Player player = context.m_43723_();
            if (player != null) {
               doppel.setOwner(player);
            }
         }

         if (spawned != null) {
            itemstack.m_41774_(1);
            level.m_142346_(context.m_43723_(), GameEvent.f_157810_, spawnPos);
         }

         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   @Nonnull
   public InteractionResult m_6880_(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull LivingEntity target, @Nonnull InteractionHand hand) {
      if (target instanceof DoppelgangerEntity && !player.m_9236_().f_46443_) {
         target.m_146870_();
         if (!player.m_150110_().f_35937_) {
            stack.m_41774_(1);
         }

         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.PASS;
      }
   }
}
