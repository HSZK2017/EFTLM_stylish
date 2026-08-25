package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.VillagerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class FireworkUseEvent {
   @SubscribeEvent
   public static void onRightClickBlock(RightClickBlock event) {
      if (event.getHand() == event.getEntity().m_7655_()) {
         if (event.getLevel() instanceof ServerLevel serverLevel) {
            if (tryUseVillagerSignalFirework(serverLevel, event.getPos(), event.getEntity(), event.getHand())) {
               event.setCanceled(true);
               event.setCancellationResult(InteractionResult.SUCCESS);
            }
         }
      }
   }

   private static boolean tryUseVillagerSignalFirework(final ServerLevel serverLevel, BlockPos clickedPos, Player player, InteractionHand hand) {
      ItemStack usedStack = player.m_21120_(hand);
      if (!VillagerUtil.isBlackCreeperSignalFirework(usedStack)) {
         return false;
      } else if (player.f_36078_ < 5) {
         return false;
      } else {
         player.m_36335_().m_41524_(usedStack.m_41720_(), 250);
         player.m_6749_(-5);
         if (!player.m_150110_().f_35937_) {
            usedStack.m_41774_(1);
         }

         final double x = (double)clickedPos.m_123341_() + 0.5;
         final double y = (double)clickedPos.m_123342_() + 1.0;
         final double z = (double)clickedPos.m_123343_() + 0.5;
         final float yaw = player.m_146908_();
         serverLevel.m_8767_(ParticleTypes.f_123815_, x, y, z, 40, 0.0, 3.0, 0.0, 1.0);
         serverLevel.m_5594_(null, clickedPos, SoundEvents.f_11932_, SoundSource.NEUTRAL, 1.0F, 2.0F);
         VillagerUtil.launchBlackCreeperSignalFirework(serverLevel, x, y, z);
         new DelayedTask(50) {
            @Override
            public void run() {
               BlockPos signalPos = BlockPos.m_274561_(x, y, z);
               if (serverLevel.m_46749_(signalPos)) {
                  serverLevel.m_5594_(null, signalPos, SoundEvents.f_11871_, SoundSource.NEUTRAL, 1.0F, 2.0F);
                  VillagerUtil.summonRandomVillagerSupportWave(serverLevel, new Vec3(x, y, z), yaw);
               }
            }
         };
         return true;
      }
   }
}
