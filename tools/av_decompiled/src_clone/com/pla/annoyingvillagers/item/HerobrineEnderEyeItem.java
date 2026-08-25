package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.entity.BlockProjectileEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.task.DelayedTask;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HerobrineEnderEyeItem extends Item {
   public HerobrineEnderEyeItem() {
      super(new Properties().m_41487_(1).m_41503_(300));
   }

   public boolean m_5812_(ItemStack pStack) {
      return true;
   }

   public void m_7373_(ItemStack itemstack, Level level, List<Component> list, TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
      list.add(Component.m_237115_("tooltip.annoyingvillagers.herobrine_ender_eye"));
   }

   public static void spawnAndShootDarkObPillars(ServerLevel level, final LivingEntity shooter, int delayTicks) {
      BlockState block = ((Block)Objects.requireNonNull((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_SHORT_PILLAR.get())).m_49966_();
      double lateral = 2.0;
      double sideY = 0.0;
      double upY = 2.0;
      Vec3 eye = shooter.m_20299_(1.0F);
      Vec3 look = shooter.m_20252_(1.0F);
      Vec3 horiz = new Vec3(look.f_82479_, 0.0, look.f_82481_);
      if (horiz.m_82556_() < 1.0E-6) {
         float yaw = shooter.m_146908_() * (float) (Math.PI / 180.0);
         horiz = new Vec3((double)(-Mth.m_14031_(yaw)), 0.0, (double)Mth.m_14089_(yaw));
      }

      Vec3 upAxis = new Vec3(0.0, 1.0, 0.0);
      Vec3 rightAxis = horiz.m_82537_(upAxis).m_82541_();
      Vec3 leftAxis = rightAxis.m_82490_(-1.0);
      Vec3 upPos = eye.m_82520_(0.0, 2.0, 0.0);
      Vec3 leftPos = eye.m_82549_(leftAxis.m_82490_(2.0)).m_82520_(0.0, 0.0, 0.0);
      Vec3 rightPos = eye.m_82549_(rightAxis.m_82490_(2.0)).m_82520_(0.0, 0.0, 0.0);
      final BlockProjectileEntity up = makeFloating(level, shooter, block, upPos.f_82479_, upPos.f_82480_, upPos.f_82481_);
      final BlockProjectileEntity left = makeFloating(level, shooter, block, leftPos.f_82479_, leftPos.f_82480_, leftPos.f_82481_);
      final BlockProjectileEntity right = makeFloating(level, shooter, block, rightPos.f_82479_, rightPos.f_82480_, rightPos.f_82481_);
      new DelayedTask(delayTicks) {
         @Override
         public void run() {
            if (!shooter.m_6084_()) {
               if (up != null && up.m_6084_()) {
                  up.m_146870_();
               }

               if (left != null && left.m_6084_()) {
                  left.m_146870_();
               }

               if (right != null && right.m_6084_()) {
                  right.m_146870_();
               }
            } else {
               Vec3 to = HerobrineEnderEyeItem.lookTarget(shooter, 16.0);
               HerobrineEnderEyeItem.shootOne(up, to, 2.0);
               HerobrineEnderEyeItem.shootOne(left, to, 2.0);
               HerobrineEnderEyeItem.shootOne(right, to, 2.0);
            }
         }
      };
   }

   public static void startShadowObsidianMachineGun(ServerLevel level, Player player) {
      int[] remaining = new int[]{20};
      fireChainTick(level, player, remaining);
   }

   private static void fireChainTick(final ServerLevel level, final Player player, final int[] remaining) {
      if (remaining[0] > 0 && player.m_6084_()) {
         BlockState block = ((Block)Objects.requireNonNull((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())).m_49966_();
         player.m_20256_(Vec3.f_82478_);
         shootChain(player, block, 2.5F, 5);
         remaining[0]--;
         new DelayedTask(1) {
            @Override
            public void run() {
               HerobrineEnderEyeItem.fireChainTick(level, player, remaining);
            }
         };
      }
   }

   private static BlockProjectileEntity makeFloating(Level level, LivingEntity owner, BlockState block, double x, double y, double z) {
      BlockProjectileEntity blockProjectileEntity = new BlockProjectileEntity(level, owner, block);
      blockProjectileEntity.m_20242_(true);
      blockProjectileEntity.setNotReadyForShoot(true);
      blockProjectileEntity.m_7678_(x, y, z, 0.0F, 0.0F);
      if (owner instanceof Player player) {
         blockProjectileEntity.setOwnerUUID(player.m_20148_());
      }

      level.m_7967_(blockProjectileEntity);
      return blockProjectileEntity;
   }

   private static void shootOne(BlockProjectileEntity blockProjectileEntity, Vec3 to, double speed) {
      if (blockProjectileEntity != null && blockProjectileEntity.m_6084_()) {
         Vec3 dir = to.m_82546_(blockProjectileEntity.m_20182_());
         if (dir.m_82556_() < 1.0E-6) {
            dir = blockProjectileEntity.m_20154_();
         }

         blockProjectileEntity.m_20242_(false);
         blockProjectileEntity.m_20256_(dir.m_82541_().m_82490_(speed));
         blockProjectileEntity.setNotReadyForShoot(false);
      }
   }

   private static Vec3 lookTarget(LivingEntity shooter, double distance) {
      return shooter.m_146892_().m_82549_(shooter.m_20154_().m_82541_().m_82490_(distance));
   }

   private static void shootChain(LivingEntity shooter, BlockState block, float velocity, int length) {
      Level level = shooter.m_9236_();
      if (!level.f_46443_) {
         double eyeY = shooter.m_20188_();
         Vec3 look = shooter.m_20154_().m_82541_();
         Random rand = new Random();

         for (int i = 0; i < length; i++) {
            BlockProjectileEntity proj = new BlockProjectileEntity(level, shooter, block);
            Vec3 forward = look.m_82490_((double)i * 1.0);
            double sideX = (rand.nextDouble() - 0.5) * 2.0;
            double sideY = (rand.nextDouble() - 0.5) * 2.0;
            double sideZ = (rand.nextDouble() - 0.5) * 2.0;
            proj.m_6034_(shooter.m_20185_() + forward.f_82479_ + sideX, eyeY + forward.f_82480_ + sideY, shooter.m_20189_() + forward.f_82481_ + sideZ);
            if (shooter instanceof Player player) {
               proj.setOwnerUUID(player.m_20148_());
            }

            proj.m_20256_(look.m_82490_((double)velocity));
            level.m_7967_(proj);
         }
      }
   }
}
