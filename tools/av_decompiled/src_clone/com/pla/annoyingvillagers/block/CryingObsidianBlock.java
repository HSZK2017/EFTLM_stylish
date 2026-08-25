package com.pla.annoyingvillagers.block;

import com.pla.annoyingvillagers.blockentity.CryingObsidianBlockEntity;
import com.pla.annoyingvillagers.clazz.HerobrineObsidianBlock;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.task.DelayedTask;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.ForgeSoundType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

public class CryingObsidianBlock extends HerobrineObsidianBlock implements EntityBlock {
   public CryingObsidianBlock() {
      super(
         Properties.m_284310_()
            .m_60918_(
               new ForgeSoundType(
                  1.0F,
                  1.0F,
                  AnnoyingVillagersModSounds.LOST,
                  () -> SoundEvents.f_12450_,
                  () -> SoundEvents.f_12447_,
                  () -> SoundEvents.f_12446_,
                  AnnoyingVillagersModSounds.SILENT
               )
            )
            .m_60913_(60.0F, 40.0F)
            .m_60953_(blockstate -> 4)
            .m_60955_()
            .m_60982_((blockstate, blockgetter, blockpos) -> true)
            .m_60991_((blockstate, blockgetter, blockpos) -> true)
            .m_60924_((blockstate, blockgetter, blockpos) -> false)
      );
   }

   public void m_5871_(@NotNull ItemStack itemstack, BlockGetter blockgetter, @NotNull List<Component> list, @NotNull TooltipFlag tooltipflag) {
      super.m_5871_(itemstack, blockgetter, list, tooltipflag);
      list.add(Component.m_237113_("Obsidian Fired by Elite Herobrine"));
   }

   @NotNull
   public VoxelShape m_5940_(
      @NotNull BlockState blockstate, @NotNull BlockGetter blockgetter, @NotNull BlockPos blockpos, @NotNull CollisionContext collisioncontext
   ) {
      return m_49796_(0.0, 0.0, 0.0, 16.0, 16.0, 17.0);
   }

   @Override
   public void customTickSound(ServerLevel serverLevel, BlockPos blockPos) {
      super.customTickSound(serverLevel, blockPos);
   }

   @Override
   public void customPlaceSound(ServerLevel serverLevel, BlockPos blockPos) {
      super.customPlaceSound(serverLevel, blockPos);
      serverLevel.m_6263_(
         null,
         (double)blockPos.m_123341_(),
         (double)blockPos.m_123342_(),
         (double)blockPos.m_123343_(),
         SoundEvents.f_12447_,
         SoundSource.BLOCKS,
         new Random().nextFloat(0.0F, 0.7F),
         1.0F
      );
      serverLevel.m_6263_(
         null,
         (double)blockPos.m_123341_(),
         (double)blockPos.m_123342_(),
         (double)blockPos.m_123343_(),
         (SoundEvent)AnnoyingVillagersModSounds.OB_PLACE.get(),
         SoundSource.BLOCKS,
         0.5F,
         1.0F
      );
   }

   @Override
   public boolean conditionEveryTickEntityInside(Entity entity) {
      return entity.f_19797_ % 5 == 0;
   }

   @Override
   public void customHurtLogic(final Entity entity, Entity owner, ServerLevel serverLevel, BlockPos blockPos) {
      super.customHurtLogic(entity, owner, serverLevel, blockPos);
      ((HitParticleType)EpicFightParticles.HIT_BLUNT.get())
         .spawnParticleWithArgument(serverLevel, HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, entity, entity);
      serverLevel.m_6263_(
         null,
         (double)blockPos.m_123341_(),
         (double)blockPos.m_123342_(),
         (double)blockPos.m_123343_(),
         (SoundEvent)AnnoyingVillagersModSounds.OBSIDIAN_HIT.get(),
         SoundSource.BLOCKS,
         0.5F,
         1.0F
      );
      if (owner != null) {
         if (owner instanceof Player player) {
            entity.m_6469_(entity.m_9236_().m_269111_().m_269075_(player), 1.0F);
         } else {
            entity.m_6469_(entity.m_9236_().m_269111_().m_269333_((LivingEntity)owner), 1.0F);
         }
      } else {
         entity.m_6469_(entity.m_9236_().m_269111_().m_269264_(), 1.0F);
      }

      entity.m_20256_(new Vec3(entity.m_20154_().f_82479_ * -2.0, 0.4, entity.m_20154_().f_82481_ * -2.0));
      if (Math.random() <= 0.5) {
         new DelayedTask(1) {
            @Override
            public void run() {
               if (entity.m_9236_() instanceof ServerLevel && entity instanceof Mob mob) {
                  LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                  if (livingEntityPatch != null && !livingEntityPatch.isStunned()) {
                     livingEntityPatch.applyStun(StunType.LONG, 10.0F);
                  }
               }
            }
         };
         if (Math.random() <= 0.3) {
            new DelayedTask(1) {
               @Override
               public void run() {
                  if (entity.m_9236_() instanceof ServerLevel && entity instanceof Mob mob) {
                     LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                     if (livingEntityPatch != null && !livingEntityPatch.isStunned()) {
                        livingEntityPatch.applyStun(StunType.KNOCKDOWN, 10.0F);
                     }
                  }
               }
            };
         }
      }
   }

   @Nullable
   public BlockEntity m_142194_(@NotNull BlockPos pPos, @NotNull BlockState pState) {
      return new CryingObsidianBlockEntity(pPos, pState);
   }
}
